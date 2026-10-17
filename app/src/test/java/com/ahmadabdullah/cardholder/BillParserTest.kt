package com.ahmadabdullah.cardholder

import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.CardNetwork
import com.ahmadabdullah.cardholder.data.bill.BankCalendar
import com.ahmadabdullah.cardholder.data.bill.BillParser
import com.ahmadabdullah.cardholder.data.bill.CardDigits
import com.ahmadabdullah.cardholder.data.bill.Due
import com.ahmadabdullah.cardholder.data.bill.PayBy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class BillParserTest {
    // Real bill messages, as the banks sent them.
    private val ucb = "Dear C/H, Bill for UCB Card#4417 (ID# 1000001) for SEP-26. Total Due is BDT 18450.50 Dr & " +
        "USD 42.00 Dr Min Due is BDT 553.50 & USD 42.00 Due date 13-10-26"
    private val city = """
        City Islamic Amex Bill Sep'26
        Total Due
        Tk 12345.60
        Min Due
        Tk 370.37
        CARD: 371***905
        Client ID: 1000002
        Pay by 08-10-26
        eStatement:t.ly/NbH5
    """.trimIndent()
    private val ebl = "Monthly bill 455120****6630 SEP2026; Total Due: BDT 9876.54, Min Due: BDT 2100.00, " +
        "Last Pmt: 10-OCT-26. Statement link"

    private fun date(text: String) = LocalDate.parse(text)

    @Test
    fun readsUcbBill() {
        val bill = BillParser.parse(ucb)!!
        assertEquals(CardDigits("", "4417"), bill.card)
        assertEquals(YearMonth.of(2026, 9), bill.statementMonth)
        assertEquals(Due(bdt = 18_450_50, usd = 42_00), bill.totalDue)
        assertEquals(Due(bdt = 553_50, usd = 42_00), bill.minDue)
        assertEquals(date("2026-10-13"), bill.dueDate)
    }

    @Test
    fun readsCityBill() {
        val bill = BillParser.parse(city)!!
        assertEquals(CardDigits("371", "905"), bill.card)
        assertEquals(YearMonth.of(2026, 9), bill.statementMonth)
        assertEquals(Due(bdt = 12_345_60, usd = 0), bill.totalDue)
        assertEquals(Due(bdt = 370_37, usd = 0), bill.minDue)
        assertEquals(date("2026-10-08"), bill.dueDate)
    }

    @Test
    fun readsEblBill() {
        val bill = BillParser.parse(ebl)!!
        assertEquals(CardDigits("455120", "6630"), bill.card)
        assertEquals(YearMonth.of(2026, 9), bill.statementMonth)
        assertEquals(Due(bdt = 9_876_54, usd = 0), bill.totalDue)
        assertEquals(Due(bdt = 2_100_00, usd = 0), bill.minDue)
        assertEquals(date("2026-10-10"), bill.dueDate)
    }

    @Test
    fun aCreditBalanceIsNothingOwed() {
        val bill = BillParser.parse(ucb.replace("USD 42.00 Dr Min", "USD 5.00 Cr Min"))!!
        assertEquals(0L, bill.totalDue.usd)
    }

    @Test
    fun messagesThatAreNotBillsAreIgnored() {
        assertNull(BillParser.parse("Your OTP for UCB Card#4417 is 123456"))
        assertNull(BillParser.parse("BDT 5,000.00 spent on Card 371***905 at SHWAPNO on 12-10-26"))
        assertNull(BillParser.parse(ucb.substringBefore(" Due date")))
    }

    @Test
    fun billDigitsMatchSavedCards() {
        fun card(first: String, last: String) = Card(
            "id", "ucb", CardNetwork.VISA, "Gold", "A", "B", first, last, 1, 0, 1, 1, 2030,
        )
        assertTrue(CardDigits("", "4417").matches(card("4123", "4417")))
        assertTrue(CardDigits("371", "905").matches(card("3714", "2905")))
        assertFalse(CardDigits("371", "905").matches(card("3741", "2905")))
        assertTrue(CardDigits("455120", "6630").matches(card("4551", "6630")))
        assertFalse(CardDigits("455120", "6630").matches(card("4551", "6631")))
    }

    @Test
    fun banksAreClosedOnWeekendsAndHolidays() {
        assertTrue(BankCalendar.isOpen(date("2026-10-13"))) // Tuesday
        assertFalse(BankCalendar.isOpen(date("2026-10-16"))) // Friday
        assertFalse(BankCalendar.isOpen(date("2026-10-24"))) // Saturday
        assertFalse(BankCalendar.isOpen(date("2026-10-20"))) // Durga Puja
        assertFalse(BankCalendar.isOpen(date("2026-10-22"))) // Durga Puja, swapped for the 17th
        // A Saturday made a working day, in exchange for 22 October.
        assertTrue(BankCalendar.isOpen(date("2026-10-17")))
    }

    @Test
    fun aDownloadedCalendarAddsHolidaysAndWorkingDays() {
        try {
            // A holiday announced after release, and a Friday made a working day.
            BankCalendar.useDownloaded(setOf(date("2026-11-10")), setOf(date("2026-11-13")))
            assertFalse(BankCalendar.isOpen(date("2026-11-10")))
            assertTrue(BankCalendar.isOpen(date("2026-11-13")))
            // The built-in holidays still apply.
            assertFalse(BankCalendar.isOpen(date("2026-12-16")))
        } finally {
            BankCalendar.useDownloaded(emptySet(), emptySet())
        }
    }

    @Test
    fun payByDatesForTheSampleBills() {
        // UCB is due Tuesday 13 October. The taka is fine that day, but the dollars need two
        // banking days to convert, so pay by Sunday 11 October.
        val ucbPayBy = PayBy.of(BillParser.parse(ucb)!!, usdConversionDays = 2)
        assertEquals(date("2026-10-13"), ucbPayBy.bdt)
        assertEquals(date("2026-10-11"), ucbPayBy.usd)
        assertEquals(date("2026-10-11"), ucbPayBy.earliest)

        // City is due Thursday 8 October, a working day, and has nothing in dollars.
        val cityPayBy = PayBy.of(BillParser.parse(city)!!)
        assertEquals(date("2026-10-08"), cityPayBy.bdt)
        assertNull(cityPayBy.usd)

        // EBL is due Saturday 10 October, when banks are shut, so pay by Thursday 8 October.
        assertEquals(date("2026-10-08"), PayBy.of(BillParser.parse(ebl)!!).bdt)
    }

    @Test
    fun conversionDaysSkipHolidays() {
        // Due Thursday 22 October. Durga Puja closes banks from the 20th to the 22nd, so BDT is
        // due by Monday the 19th. The two banking days before that are Sunday the 18th and
        // Saturday the 17th, a working Saturday, so USD is due by the 17th.
        val bill = BillParser.parse(ucb.replace("13-10-26", "22-10-26"))!!
        val payBy = PayBy.of(bill, usdConversionDays = 2)
        assertEquals(date("2026-10-19"), payBy.bdt)
        assertEquals(date("2026-10-17"), payBy.usd)
    }
}
