package com.ahmadabdullah.cardholder.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.ahmadabdullah.cardholder.MainActivity
import com.ahmadabdullah.cardholder.R
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.bill.Bill
import com.ahmadabdullah.cardholder.data.bill.BillPart
import com.ahmadabdullah.cardholder.data.bill.Reminder
import com.ahmadabdullah.cardholder.data.bill.Reminders
import com.ahmadabdullah.cardholder.ui.formatBdtExact
import com.ahmadabdullah.cardholder.ui.formatDay
import com.ahmadabdullah.cardholder.ui.formatDaysLeft
import com.ahmadabdullah.cardholder.ui.formatUsdExact

/** The notifications Card holder shows: a new bill arriving, and reminders to pay it. */
object BillNotifications {
    private const val REMINDERS = "bill_reminders"
    private const val NEW_BILLS = "new_bills"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(REMINDERS, "Bill reminders", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Reminders to pay a card's bill before the due date"
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
            context, card, NEW_BILLS, notificationId(bill.id, "new"),
            title = "${card.bankName} bill saved",
            text = lines.joinToString(" "),
        )
    }

    /** One notification per bill per day, listing every part that is due soon. */
    fun showReminders(context: Context, card: Card, bill: Bill, reminders: List<Reminder>) {
        val soonest = reminders.minOf { it.daysLeft }.toLong()
        val lines = reminders.sortedBy { it.deadline }.map { reminderLine(bill, it) }
        post(
            context, card, REMINDERS, notificationId(bill.id, "reminder"),
            title = "${card.bankName} ••${card.lastFour}: pay ${formatDaysLeft(soonest)}",
            text = lines.joinToString("\n"),
        )
    }

    private fun reminderLine(bill: Bill, reminder: Reminder): String {
        val by = "by ${formatDay(reminder.deadline)} (${formatDaysLeft(reminder.daysLeft.toLong())})"
        return when (reminder.part) {
            BillPart.WHOLE -> "Pay ${formatBdtExact(bill.totalDue.bdt)} and ${formatUsdExact(bill.totalDue.usd)} $by."
            BillPart.BDT -> "Pay ${formatBdtExact(bill.totalDue.bdt)} $by."
            BillPart.USD -> "Pay the ${formatUsdExact(bill.totalDue.usd)} $by, so the bank has time to convert the taka."
        }
    }

    private fun payLines(bill: Bill): List<String> {
        val payBy = bill.payBy
        return listOfNotNull(
            payBy.bdt?.let { "Pay ${formatBdtExact(bill.totalDue.bdt)} by ${formatDay(it)}." },
            payBy.usd?.let { "Pay ${formatUsdExact(bill.totalDue.usd)} by ${formatDay(it)}." },
            "Reminders come ${Reminders.DAYS_BEFORE.sortedDescending().joinToString(", ")} days before.",
        )
    }

    private fun post(context: Context, card: Card, channel: String, id: Int, title: String, text: String) {
        if (!canPost(context)) return
        val open = PendingIntent.getActivity(
            context,
            id,
            Intent(context, MainActivity::class.java)
                .putExtra(MainActivity.EXTRA_CARD_ID, card.id)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(open)
            .setAutoCancel(true)
            .setPriority(if (channel == REMINDERS) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(id, notification)
        } catch (_: SecurityException) {
            // Notifications were turned off between the check and posting.
        }
    }

    private fun notificationId(billId: String, kind: String) = "$kind:$billId".hashCode()
}
