package com.ahmadabdullah.cardholder.ui

import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.CardNetwork
import java.util.Calendar
import java.util.Locale

/** Groups an amount the Bangladeshi way: thousands first, then every two digits (5,00,000). */
fun groupLakh(amount: Long): String {
    val digits = amount.toString()
    if (digits.length <= 3) return digits
    val head = digits.dropLast(3)
    val groups = head.reversed().chunked(2).map { it.reversed() }.reversed()
    return groups.joinToString(",") + "," + digits.takeLast(3)
}

fun formatBdt(amount: Long): String = "৳" + groupLakh(amount)

fun formatUsd(amount: Long): String = "$" + String.format(Locale.US, "%,d", amount)

/** The card number with the unknown middle digits masked, grouped like the printed card. */
fun maskedNumber(card: Card): String = when (card.network) {
    CardNetwork.AMEX -> "${card.firstFour} •••••• •${card.lastFour}"
    else -> "${card.firstFour} •••• •••• ${card.lastFour}"
}

fun ordinal(day: Int): String {
    val suffix = if (day % 100 in 11..13) "th" else when (day % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
    return "$day$suffix"
}

/**
 * The next date the bill is generated, today included. A billing day past the end of a short
 * month falls on that month's last day.
 */
fun nextBillingDate(billingDay: Int, today: Calendar = Calendar.getInstance()): Calendar {
    fun inMonth(monthOffset: Int): Calendar = (today.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        add(Calendar.MONTH, monthOffset)
        set(Calendar.DAY_OF_MONTH, minOf(billingDay, getActualMaximum(Calendar.DAY_OF_MONTH)))
    }
    val thisMonth = inMonth(0)
    return if (thisMonth.get(Calendar.DAY_OF_MONTH) >= today.get(Calendar.DAY_OF_MONTH)) thisMonth else inMonth(1)
}

fun formatShortDate(date: Calendar): String =
    String.format(Locale.US, "%1\$te %1\$tb %1\$tY", date)
