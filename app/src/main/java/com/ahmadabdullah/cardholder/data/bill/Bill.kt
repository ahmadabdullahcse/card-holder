package com.ahmadabdullah.cardholder.data.bill

import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.YearMonth

/** A bill saved against one of the user's cards. */
data class Bill(
    val cardId: String,
    val statementMonth: YearMonth?,
    val totalDue: Due,
    val minDue: Due,
    val dueDate: LocalDate,
    /** Set by the user once they've paid, which stops the reminders. */
    val paid: Boolean = false,
    /** The reminders already shown, so none is shown twice. See [Reminder.key]. */
    val remindersSent: Set<String> = emptySet(),
) {
    /** A bank sends one bill per card per due date; a repeat of the message updates it. */
    val id: String get() = "$cardId:$dueDate"

    val payBy: PayBy get() = PayBy.of(dueDate, totalDue)

    fun toJson(): JSONObject = JSONObject()
        .put("cardId", cardId)
        .put("statementMonth", statementMonth?.toString() ?: JSONObject.NULL)
        .put("totalDueBdt", totalDue.bdt)
        .put("totalDueUsd", totalDue.usd)
        .put("minDueBdt", minDue.bdt)
        .put("minDueUsd", minDue.usd)
        .put("dueDate", dueDate.toString())
        .put("paid", paid)
        .put("remindersSent", JSONArray(remindersSent.sorted()))

    companion object {
        fun from(message: BillMessage, cardId: String) = Bill(
            cardId = cardId,
            statementMonth = message.statementMonth,
            totalDue = message.totalDue,
            minDue = message.minDue,
            dueDate = message.dueDate,
        )

        fun fromJson(json: JSONObject): Bill {
            val sent = json.getJSONArray("remindersSent")
            return Bill(
                cardId = json.getString("cardId"),
                statementMonth = if (json.isNull("statementMonth")) null else YearMonth.parse(json.getString("statementMonth")),
                totalDue = Due(json.getLong("totalDueBdt"), json.getLong("totalDueUsd")),
                minDue = Due(json.getLong("minDueBdt"), json.getLong("minDueUsd")),
                dueDate = LocalDate.parse(json.getString("dueDate")),
                paid = json.getBoolean("paid"),
                remindersSent = List(sent.length()) { sent.getString(it) }.toSet(),
            )
        }
    }
}

/**
 * A bill for a card the user hasn't saved yet. It's kept so that the user can be offered to
 * add the card, and is moved onto the card once they do.
 */
data class UnmatchedBill(
    val message: BillMessage,
    /** The bank, when the message or its sender names one. */
    val bankId: String?,
) {
    val id: String get() = "${message.card.prefix}*${message.card.suffix}:${message.dueDate}"

    fun toJson(): JSONObject = JSONObject()
        .put("prefix", message.card.prefix)
        .put("suffix", message.card.suffix)
        .put("statementMonth", message.statementMonth?.toString() ?: JSONObject.NULL)
        .put("totalDueBdt", message.totalDue.bdt)
        .put("totalDueUsd", message.totalDue.usd)
        .put("minDueBdt", message.minDue.bdt)
        .put("minDueUsd", message.minDue.usd)
        .put("dueDate", message.dueDate.toString())
        .put("bankId", bankId ?: JSONObject.NULL)

    companion object {
        fun fromJson(json: JSONObject) = UnmatchedBill(
            message = BillMessage(
                card = CardDigits(json.getString("prefix"), json.getString("suffix")),
                statementMonth = if (json.isNull("statementMonth")) null else YearMonth.parse(json.getString("statementMonth")),
                totalDue = Due(json.getLong("totalDueBdt"), json.getLong("totalDueUsd")),
                minDue = Due(json.getLong("minDueBdt"), json.getLong("minDueUsd")),
                dueDate = LocalDate.parse(json.getString("dueDate")),
            ),
            bankId = if (json.isNull("bankId")) null else json.getString("bankId"),
        )
    }
}
