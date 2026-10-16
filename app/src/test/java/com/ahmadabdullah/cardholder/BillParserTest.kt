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
    private val ucb = "Dear C/H, Bill for UCB Card#8673 (ID# 2258719) for SEP-26. Total Due is BDT 24719.12 Dr & " +
        "USD 23.00 Dr Min Due is BDT 741.57 & USD 23.00 Due date 13-10-26"
    private val city = """
        City Islamic Amex Bill Sep'26
        Total Due
        Tk 17888.62
        Min Due
        Tk 536.66
        CARD: 376***786
        Client ID: 2853907
        Pay by 08-10-26
        eStatement:t.ly/NbH5
    """.trimIndent()
    private val ebl = "Monthly bill 447780****5812 SEP2026; Total Due: BDT 14475.77, Min Due: BDT 3219.00, " +
        "Last Pmt: 10-OCT-26. Statement link"

    private fun date(text: String) = LocalDate.parse(text)

    @Test
    fun readsUcbBill() {
        val bill = BillParser.parse(ucb)!!
        assertEquals(CardDigits("", "8673"), bill.card)
        assertEquals(YearMonth.of(2026, 9), bill.statementMonth)
        assertEquals(Due(bdt = 24_719_12, usd = 23_00), bill.totalDue)
        assertEquals(Due(bdt = 741_57, usd = 23_00), bill.minDue)
        assertEquals(date("2026-10-13"), bill.dueDate)
    }

    @Test
    fun readsCityBill() {
        val bill = BillParser.parse(city)!!
        assertEquals(CardDigits("376", "786"), bill.card)
        assertEquals(YearMonth.of(2026, 9), bill.statementMonth)
        assertEquals(Due(bdt = 17_888_62, usd = 0), bill.totalDue)
        assertEquals(Due(bdt = 536_66, usd = 0), bill.minDue)
        assertEquals(date("2026-10-08"), bill.dueDate)
    }

    @Test
    fun readsEblBill() {
        val bill = BillParser.parse(ebl)!!
        assertEquals(CardDigits("447780", "5812"), bill.card)
        assertEquals(YearMonth.of(2026, 9), bill.statementMonth)
        assertEquals(Due(bdt = 14_475_77, usd = 0), bill.totalDue)
        assertEquals(Due(bdt = 3_219_00, usd = 0), bill.minDue)
        assertEquals(date("2026-10-10"), bill.dueDate)
    }

    @Test
    fun aCreditBalanceIsNothingOwed() {
        val bill = BillParser.parse(ucb.replace("USD 23.00 Dr Min", "USD 5.00 Cr Min"))!!
        assertEquals(0L, bill.totalDue.usd)
    }

    @Test
    fun messagesThatAreNotBillsAreIgnored() {
        assertNull(BillParser.parse("Your OTP for UCB Card#8673 is 123456"))
        assertNull(BillParser.parse("BDT 5,000.00 spent on Card 376***786 at SHWAPNO on 12-10-26"))
        assertNull(BillParser.parse(ucb.substringBefore(" Due date")))
    }

    @Test
    fun billDigitsMatchSavedCards() {
        fun card(first: String, last: String) = Card(
            "id", "ucb", CardNetwork.VISA, "Gold", "A", "B", first, last, 1, 0, 1, 1, 2030,
        )
        assertTrue(CardDigits("", "8673").matches(card("4123", "8673")))
        assertTrue(CardDigits("376", "786").matches(card("3761", "4786")))
        assertFalse(CardDigits("376", "786").matches(card("3712", "4786")))
        assertTrue(CardDigits("447780", "5812").matches(card("4477", "5812")))
        assertFalse(CardDigits("447780", "5812").matches(card("4477", "5813")))
    }

    @Test
    fun banksAreClosedOnWeekendsAndHolidays() {
        assertTrue(BankCalendar.isOpen(date("2026-10-13"))) // Tuesday
        assertFalse(BankCalendar.isOpen(date("2026-10-16"))) // Friday
        assertFalse(BankCalendar.isOpen(date("2026-10-17"))) // Saturday
        assertFalse(BankCalendar.isOpen(date("2026-10-20"))) // Durga Puja
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
        // Due Thursday 22 October. Durga Puja closes banks on the 20th and 21st, so the two
        // banking days before the due date are Monday the 19th and Sunday the 18th.
        val bill = BillParser.parse(ucb.replace("13-10-26", "22-10-26"))!!
        assertEquals(date("2026-10-18"), PayBy.of(bill, usdConversionDays = 2).usd)
    }
}
