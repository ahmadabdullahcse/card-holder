package com.ahmadabdullah.cardholder.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.ahmadabdullah.cardholder.MainActivity
import com.ahmadabdullah.cardholder.R
import com.ahmadabdullah.cardholder.data.BankCatalog
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.bill.Bill
import com.ahmadabdullah.cardholder.data.bill.BillPart
import com.ahmadabdullah.cardholder.data.bill.PayBy
import com.ahmadabdullah.cardholder.data.bill.Reminder
import com.ahmadabdullah.cardholder.data.bill.Reminders
import com.ahmadabdullah.cardholder.data.bill.UnmatchedBill
import com.ahmadabdullah.cardholder.ui.formatBdtExact
import com.ahmadabdullah.cardholder.ui.formatDay
import com.ahmadabdullah.cardholder.ui.formatDaysLeft
import com.ahmadabdullah.cardholder.ui.formatUsdExact

/** "7, 5, 3, 2 and 1 days before, and on the day" */
val REMINDER_DAYS_TEXT: String = Reminders.DAYS_BEFORE.filter { it > 0 }.sortedDescending().let { days ->
    days.dropLast(1).joinToString(", ") + " and ${days.last()} days before, and on the day"
}

/** The notifications Card holder shows: a new bill arriving, and reminders to pay it. */
object BillNotifications {
    private const val REMINDERS = "bill_reminders"
    private const val DUE_TODAY = "bills_due_today"
    private const val NEW_BILLS = "new_bills"

    /** The urgent color of a bill that must be paid today. */
    private const val URGENT_RED = 0xFFD32F2F.toInt()

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(REMINDERS, "Bill reminders", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Reminders to pay a card's bill before the due date"
            }
        )
        manager.createNotificationChannel(
            NotificationChannel(DUE_TODAY, "Due today", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "The last safe day to pay a bill"
                enableLights(true)
                lightColor = URGENT_RED
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400, 200, 400)
            }
        )
        manager.createNotificationChannel(
            NotificationChannel(NEW_BILLS, "New bills", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "When a bank's bill message is read and saved"
            }
        )
    }

    fun canPost(context: Context): Boolean =
        NotificationManagerCompat.from(context).areNotificationsEnabled() &&
            (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED)

    /** "UCB card ending 4417: bill saved. Pay ৳18,450.50 by Tue 13 Oct and $42.00 by Sun 11 Oct." */
    fun showNewBill(context: Context, card: Card, bill: Bill) {
        val lines = payLines(bill)
        post(
            context, card.id, NEW_BILLS, notificationId(bill.id, "new"),
            title = "${card.bankName} bill saved",
            text = lines.joinToString(" "),
        )
    }

    /**
     * One notification per bill per day, listing every part that is due soon. On the last safe
     * day it is red, urgent and vibrates harder.
     */
    fun showReminders(context: Context, card: Card, bill: Bill, reminders: List<Reminder>) {
        val soonest = reminders.minOf { it.daysLeft }.toLong()
        val lines = reminders.sortedBy { it.deadline }.map { reminderLine(bill, it) }
        val dueToday = soonest == 0L
        post(
            context, card.id, if (dueToday) DUE_TODAY else REMINDERS, notificationId(bill.id, "reminder"),
            title = if (dueToday) {
                red("Pay today: ${card.bankName} ••${card.lastFour}")
            } else {
                "${card.bankName} ••${card.lastFour}: pay ${formatDaysLeft(soonest)}"
            },
            text = lines.joinToString("\n"),
            color = URGENT_RED.takeIf { dueToday },
            largeIcon = if (dueToday) R.drawable.ic_due_today else null,
        )
    }

    /**
     * A bill arrived for a card that isn't saved. Tapping it opens the add-card form with the
     * digits the bank showed already filled in.
     */
    fun showAddCardPrompt(context: Context, bill: UnmatchedBill) {
        val bank = bill.bankId?.let { BankCatalog.bank(it)?.name } ?: "Your bank"
        val digits = bill.message.card.suffix
        val payBy = PayBy.of(bill.message).earliest
        post(
            context, cardId = null, NEW_BILLS, notificationId(bill.id, "unmatched"),
            title = "$bank sent a bill for a card ending $digits",
            text = "Add this card to Card holder to get reminders before it's due" +
                (payBy?.let { " (pay by ${formatDay(it)})" } ?: "") + ".",
            extras = { it.putExtra(MainActivity.EXTRA_UNMATCHED_BILL_ID, bill.id) },
        )
    }

    private fun reminderLine(bill: Bill, reminder: Reminder): String {
        val by = if (reminder.daysLeft == 0) {
            "today, ${formatDay(reminder.deadline)}"
        } else {
            "by ${formatDay(reminder.deadline)} (${formatDaysLeft(reminder.daysLeft.toLong())})"
        }
        return when (reminder.part) {
            BillPart.WHOLE -> "Pay ${formatBdtExact(bill.totalDue.bdt)} and ${formatUsdExact(bill.totalDue.usd)} $by."
            BillPart.BDT -> "Pay ${formatBdtExact(bill.totalDue.bdt)} $by."
            BillPart.USD -> "Pay the ${formatUsdExact(bill.totalDue.usd)} $by, so the bank has time to convert BDT to USD."
        }
    }

    private fun payLines(bill: Bill): List<String> {
        val payBy = bill.payBy
        return listOfNotNull(
            payBy.bdt?.let { "Pay ${formatBdtExact(bill.totalDue.bdt)} by ${formatDay(it)}." },
            payBy.usd?.let { "Pay ${formatUsdExact(bill.totalDue.usd)} by ${formatDay(it)}." },
            "You'll be reminded ${REMINDER_DAYS_TEXT}.",
        )
    }

    private fun post(
        context: Context,
        cardId: String?,
        channel: String,
        id: Int,
        title: CharSequence,
        text: String,
        color: Int? = null,
        largeIcon: Int? = null,
        extras: (Intent) -> Unit = {},
    ) {
        if (!canPost(context)) return
        val intent = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        cardId?.let { intent.putExtra(MainActivity.EXTRA_CARD_ID, it) }
        extras(intent)
        val open = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(open)
            .setAutoCancel(true)
            .setPriority(if (channel == NEW_BILLS) NotificationCompat.PRIORITY_DEFAULT else NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .apply { color?.let { setColor(it) } }
            .apply { largeIcon?.let { setLargeIcon(ContextCompat.getDrawable(context, it)?.toBitmap()) } }
            .build()
        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (_: SecurityException) {
            // Notifications were turned off between the check and posting.
        }
    }

    /** Text drawn in the urgent red. Recent Android ignores an app's accent color, but keeps this. */
    private fun red(text: String): CharSequence = SpannableString(text).apply {
        setSpan(ForegroundColorSpan(URGENT_RED), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(StyleSpan(Typeface.BOLD), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun notificationId(billId: String, kind: String) = "$kind:$billId".hashCode()
}
