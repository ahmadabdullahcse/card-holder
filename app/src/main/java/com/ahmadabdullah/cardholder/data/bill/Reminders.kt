package com.ahmadabdullah.cardholder.data.bill

import com.ahmadabdullah.cardholder.data.BankCatalog
import com.ahmadabdullah.cardholder.data.Card
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Which part of a bill a reminder is about. */
enum class BillPart { WHOLE, BDT, USD }

/** A reminder to pay [part] of a bill by [deadline], [daysLeft] days from today. */
data class Reminder(val part: BillPart, val deadline: LocalDate, val daysLeft: Int) {
    /** Names this reminder in [Bill.remindersSent]. Includes the deadline in case it moves. */
    val key: String get() = "$part:$deadline:$daysLeft"
}

object Reminders {
    /** How many days before a pay-by date the user is reminded. */
    val DAYS_BEFORE = setOf(7, 5, 3, 2, 1)

    /**
     * The reminders to show today for [bill]. The taka and dollar parts are counted down
     * separately when the dollars have to be paid earlier, and together when they share a day.
     */
    fun due(bill: Bill, today: LocalDate): List<Reminder> {
        if (bill.paid) return emptyList()
        val payBy = bill.payBy
        val deadlines = if (payBy.bdt != null && payBy.bdt == payBy.usd) {
            listOf(BillPart.WHOLE to payBy.bdt)
        } else {
            listOfNotNull(payBy.bdt?.let { BillPart.BDT to it }, payBy.usd?.let { BillPart.USD to it })
        }
        return deadlines
            .map { (part, deadline) -> Reminder(part, deadline, ChronoUnit.DAYS.between(today, deadline).toInt()) }
            .filter { it.daysLeft in DAYS_BEFORE && it.key !in bill.remindersSent }
    }
}

/**
 * Finds which saved card a bill is for. Usually only one card has the digits the message
 * shows; if several do, the bank named in the message or its sender decides.
 */
object BillMatcher {
    fun cardFor(bill: BillMessage, cards: List<Card>, senderAndText: String): Card? {
        val matches = cards.filter { bill.card.matches(it) }
        if (matches.size <= 1) return matches.firstOrNull()
        return matches.singleOrNull { card -> mentionsBank(senderAndText, card.bankId) }
    }

    /** Whether the text names the bank, by its short id (UCB, EBL, City) or the first word of its name. */
    private fun mentionsBank(text: String, bankId: String): Boolean {
        val bank = BankCatalog.bank(bankId) ?: return false
        val names = setOf(bank.id.replace('_', ' '), bank.name.substringBefore(' '))
        return names.any { Regex("""\b${Regex.escape(it)}\b""", RegexOption.IGNORE_CASE).containsMatchIn(text) }
    }
}
