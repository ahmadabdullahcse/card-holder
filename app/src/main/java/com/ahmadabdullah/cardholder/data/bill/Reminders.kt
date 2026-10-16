package com.ahmadabdullah.cardholder.data.bill

import com.ahmadabdullah.cardholder.data.Bank
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
    /** How many days before a pay-by date the user is reminded. 0 is the pay-by day itself. */
    val DAYS_BEFORE = setOf(7, 5, 3, 2, 1, 0)

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

    /** The one bank the text names, if it names exactly one. */
    fun bankNamedIn(text: String): Bank? = BankCatalog.banks.singleOrNull { mentionsBank(text, it.id) }

    /**
     * Whether the text names the bank: by its short code in capitals (UCB, EBL, CITY) or the first
     * word of its name as written (City, Eastern, Dutch-Bangla). Letter case counts, so ONE Bank
     * isn't found in every "one". "Bank" alone, as in Bank Asia, would match every bank.
     */
    private fun mentionsBank(text: String, bankId: String): Boolean {
        val bank = BankCatalog.bank(bankId) ?: return false
        val names = setOf(bank.id.replace('_', ' ').uppercase(), bank.name.substringBefore(' ')) - setOf("Bank")
        return names.any { Regex("""\b${Regex.escape(it)}\b""").containsMatchIn(text) }
    }
}
