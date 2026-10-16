package com.ahmadabdullah.cardholder

import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.CardNetwork
import com.ahmadabdullah.cardholder.data.bill.Bill
import com.ahmadabdullah.cardholder.data.bill.BillMatcher
import com.ahmadabdullah.cardholder.data.bill.BillParser
import com.ahmadabdullah.cardholder.data.bill.BillPart
import com.ahmadabdullah.cardholder.data.bill.Reminders
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class RemindersTest {
    private fun date(text: String) = LocalDate.parse(text)

    // Due Tuesday 13 October: taka by the 13th, dollars by Sunday the 11th.
    private val ucb = Bill.from(
        BillParser.parse(
            "Bill for UCB Card#8673 for SEP-26. Total Due is BDT 24719.12 Dr & USD 23.00 Dr " +
                "Min Due is BDT 741.57 & USD 23.00 Due date 13-10-26"
        )!!,
        cardId = "ucb-card",
    )

    // Due Saturday 10 October, when banks are shut: pay by Thursday the 8th.
    private val ebl = Bill.from(
        BillParser.parse("Monthly bill 447780****5812 SEP2026; Total Due: BDT 14475.77, Min Due: BDT 3219.00, Last Pmt: 10-OCT-26.")!!,
        cardId = "ebl-card",
    )

    private fun daysLeftOn(bill: Bill, today: String) = Reminders.due(bill, date(today)).map { it.part to it.daysLeft }

    @Test
    fun remindsSevenFiveThreeTwoAndOneDaysBeforeAndOnTheDay() {
        val days = (1..10).map { date("2026-09-30").plusDays(it.toLong()) }
            .filter { Reminders.due(ebl, it).isNotEmpty() }
        // Pay by Thursday 8 October.
        assertEquals(listOf("2026-10-01", "2026-10-03", "2026-10-05", "2026-10-06", "2026-10-07", "2026-10-08").map(::date), days)
        assertEquals(listOf(BillPart.BDT to 0), daysLeftOn(ebl, "2026-10-08"))
        assertTrue(Reminders.due(ebl, date("2026-10-09")).isEmpty())
    }

    @Test
    fun dollarsAreCountedDownSeparatelyWhenDueEarlier() {
        // Sunday 4 October: 7 days to the dollar deadline (11th), 9 to the taka one (13th).
        assertEquals(listOf(BillPart.USD to 7), daysLeftOn(ucb, "2026-10-04"))
        // Thursday 8 October: 3 days to the dollars, 5 to the taka. Both in one notification.
        assertEquals(listOf(BillPart.BDT to 5, BillPart.USD to 3), daysLeftOn(ucb, "2026-10-08"))
        // Sunday 11 October: the last day for the dollars, and 2 days to the taka.
        assertEquals(listOf(BillPart.BDT to 2, BillPart.USD to 0), daysLeftOn(ucb, "2026-10-11"))
        // Monday 12 October: the dollar deadline has passed, 1 day to the taka.
        assertEquals(listOf(BillPart.BDT to 1), daysLeftOn(ucb, "2026-10-12"))
    }

    @Test
    fun aReminderIsShownOnce() {
        val today = date("2026-10-05")
        val sent = ebl.copy(remindersSent = Reminders.due(ebl, today).map { it.key }.toSet())
        assertTrue(Reminders.due(sent, today).isEmpty())
        assertEquals(1, Reminders.due(sent, date("2026-10-06")).size)
    }

    @Test
    fun aPaidBillHasNoReminders() {
        assertTrue(Reminders.due(ebl.copy(paid = true), date("2026-10-07")).isEmpty())
    }

    @Test
    fun aBillGoesToTheCardWithItsDigits() {
        fun card(id: String, bank: String, first: String, last: String) =
            Card(id, bank, CardNetwork.VISA, "Gold", "A", "B", first, last, 1, 0, 1, 1, 2030)
        val cards = listOf(card("ucb-card", "ucb", "4123", "8673"), card("ebl-card", "ebl", "4477", "5812"))
        val message = BillParser.parse("Bill for UCB Card#8673 Total Due is BDT 100.00 Due date 13-10-26")!!
        assertEquals("ucb-card", BillMatcher.cardFor(message, cards, "UCB")?.id)

        // Two cards end in 8673: the sender decides.
        val twins = cards + card("city-card", "city", "3761", "8673")
        assertEquals("ucb-card", BillMatcher.cardFor(message, twins, "UCB Bill for UCB Card#8673")?.id)
        assertNull(BillMatcher.cardFor(message, twins, "Bill for Card#8673"))
    }

    @Test
    fun theBankIsFoundByItsOwnSpelling() {
        assertEquals("ucb", BillMatcher.bankNamedIn("Dear C/H, Bill for UCB Card#8673")?.id)
        assertEquals("city", BillMatcher.bankNamedIn("City Islamic Amex Bill Sep'26")?.id)
        assertEquals("ebl", BillMatcher.bankNamedIn("EBL Monthly bill 447780****5812")?.id)
        // EBL's own message names no bank, and plain words aren't banks.
        assertNull(BillMatcher.bankNamedIn("Monthly bill 447780****5812 SEP2026"))
        assertNull(BillMatcher.bankNamedIn("Pay one bill at the bank"))
    }
}
