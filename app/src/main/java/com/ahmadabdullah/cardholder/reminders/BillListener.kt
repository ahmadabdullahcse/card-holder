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
import com.ahmadabdullah.cardholder.data.CardStore
import com.ahmadabdullah.cardholder.data.bill.Bill
import com.ahmadabdullah.cardholder.data.bill.BillMatcher
import com.ahmadabdullah.cardholder.data.bill.BillParser
import com.ahmadabdullah.cardholder.data.bill.BillStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Reads notifications as they arrive, looking for credit card bills from the user's banks.
 * Android only runs this once the user turns on notification access for Card holder.
 *
 * Only a bill for one of the user's saved cards is kept, and only the amounts and dates are
 * kept from it. Every other notification is looked at and forgotten.
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

    private suspend fun save(sender: String, texts: List<String>) {
        val (text, message) = texts.firstNotNullOfOrNull { text -> BillParser.parse(text)?.let { text to it } } ?: return
        val cardStore = CardStore.get(this)
        cardStore.load()
        val card = BillMatcher.cardFor(message, cardStore.cards.value.orEmpty(), "$sender $text") ?: return

        val bill = Bill.from(message, card.id)
        if (BillStore.get(this).save(bill)) {
            BillNotifications.showNewBill(this, card, bill)
            ReminderWorker.checkNow(this)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    /**
     * Everything a notification says, longest first. SMS apps put the whole message in the big
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
