package com.ahmadabdullah.cardholder.data.bill

import com.ahmadabdullah.cardholder.data.Card
import java.time.LocalDate
import java.time.YearMonth

/**
 * The digits of a card number that a bank's message shows: some of the first digits and some
 * of the last. "371***905" is prefix 371 and suffix 905; "Card#4417" has no prefix.
 */
data class CardDigits(val prefix: String, val suffix: String) {
    fun matches(card: Card): Boolean {
        val prefixMatches = when {
            prefix.isEmpty() -> true
            prefix.length <= 4 -> card.firstFour.startsWith(prefix)
            else -> card.firstFour == prefix.take(4)
        }
        val suffixMatches = when {
            suffix.length >= 4 -> card.lastFour == suffix.takeLast(4)
            else -> card.lastFour.endsWith(suffix)
        }
        return prefixMatches && suffixMatches
    }
}

/**
 * An amount owed on a bill, in both currencies, in hundredths (poisha and cents) so that
 * 18450.50 is held exactly. A currency the bill doesn't mention is 0.
 */
data class Due(val bdt: Long, val usd: Long)

/** A credit card bill as read from a bank's message. */
data class BillMessage(
    val card: CardDigits,
    /** The month the statement covers, when the message names it. */
    val statementMonth: YearMonth?,
    val totalDue: Due,
    val minDue: Due,
    val dueDate: LocalDate,
)
