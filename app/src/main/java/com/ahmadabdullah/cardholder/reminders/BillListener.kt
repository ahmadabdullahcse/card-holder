package com.ahmadabdullah.cardholder.reminders

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import com.ahmadabdullah.cardholder.data.bill.BillImport
import com.ahmadabdullah.cardholder.data.bill.BillMessage
import com.ahmadabdullah.cardholder.data.bill.BillParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Reads notifications as they arrive, looking for credit card bills from the user's banks.
 * Android only runs this once the user turns on notification access for Card holder.
 *
 * Only credit card bills are kept, and from them only the amounts, dates and the digits the bank
 * shows. A bill for a card that isn't saved yet is kept so the user can add the card. Every other
 * notification is looked at and forgotten.
 */
class BillListener : NotificationListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onNotificationPosted(notification: StatusBarNotification) {
        if (notification.packageName == packageName) return
        val extras = notification.notification.extras
        val sender = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val texts = texts(extras)
        if (texts.isEmpty()) return
        scope.launch { save(sender, texts) }
    }

    /**
     * Saves every bill in the notification. An SMS app shows a conversation as one notification
     * holding its recent messages, so last month's bill from the same sender is often there too.
     */
    private suspend fun save(sender: String, texts: List<String>) {
        texts.mapNotNull { text -> BillParser.parse(text)?.let { it to text } }
            .distinctBy { (message, _) -> message }
            .forEach { (message, text) -> save(sender, text, message) }
    }

    private suspend fun save(sender: String, text: String, message: BillMessage) {
        when (val saved = BillImport.save(this, sender, text, message)) {
            is BillImport.Unmatched -> if (saved.isNew) BillNotifications.showAddCardPrompt(this, saved.bill)
            is BillImport.Saved -> if (saved.isNew) {
                BillNotifications.showNewBill(this, saved.card, saved.bill)
                ReminderWorker.checkNow(this)
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    /**
     * Everything a notification says. SMS apps put the whole message in the big
     * text or in a list of messages, and only the start of it in the plain text.
     */
    private fun texts(extras: Bundle): List<String> {
        val messages = messageTexts(extras)
        return (listOfNotNull(
            extras.getCharSequence(Notification.EXTRA_BIG_TEXT),
            extras.getCharSequence(Notification.EXTRA_TEXT),
        ) + messages + extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES).orEmpty())
            .map { it.toString() }
            .filter { it.isNotBlank() }
            .distinct()
            .sortedByDescending { it.length }
    }

    @Suppress("DEPRECATION")
    private fun messageTexts(extras: Bundle): List<CharSequence> {
        val messages: Array<out Parcelable>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            extras.getParcelableArray(Notification.EXTRA_MESSAGES, Parcelable::class.java)
        } else {
            extras.getParcelableArray(Notification.EXTRA_MESSAGES)
        }
        return messages.orEmpty().mapNotNull { (it as? Bundle)?.getCharSequence("text") }
    }

    companion object {
        /** Whether the user has turned on notification access for Card holder. */
        fun isEnabled(context: Context): Boolean =
            context.packageName in NotificationManagerCompat.getEnabledListenerPackages(context)

        fun component(context: Context) = ComponentName(context, BillListener::class.java)
    }
}
