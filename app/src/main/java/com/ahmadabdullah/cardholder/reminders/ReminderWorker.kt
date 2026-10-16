package com.ahmadabdullah.cardholder.reminders

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ahmadabdullah.cardholder.data.CardStore
import com.ahmadabdullah.cardholder.data.bill.BillStore
import com.ahmadabdullah.cardholder.data.bill.Reminders
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/**
 * Checks the saved bills once a day and reminds the user of any due soon. WorkManager keeps the
 * daily check going after the phone restarts.
 */
class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val cardStore = CardStore.get(applicationContext)
        val billStore = BillStore.get(applicationContext)
        cardStore.load()
        val cards = cardStore.cards.value.orEmpty()
        val today = LocalDate.now()

        for (bill in billStore.load()) {
            val card = cards.firstOrNull { it.id == bill.cardId } ?: continue
            val reminders = Reminders.due(bill, today)
            if (reminders.isEmpty()) continue
            BillNotifications.showReminders(applicationContext, card, bill, reminders)
            billStore.recordReminders(bill.id, reminders.map { it.key })
        }
        return Result.success()
    }

    companion object {
        private const val DAILY = "daily_bill_reminders"

        /** Reminders arrive in the morning, when there's still the working day to pay. */
        private val REMINDER_TIME: LocalTime = LocalTime.of(9, 0)

        /** Starts the daily check, if it isn't running already. */
        fun scheduleDaily(context: Context) {
            val now = LocalDateTime.now()
            val next = now.toLocalDate().atTime(REMINDER_TIME).let { if (it.isAfter(now)) it else it.plusDays(1) }
            val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(Duration.between(now, next).toMinutes(), TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(DAILY, ExistingPeriodicWorkPolicy.KEEP, request)
        }

        /** Checks right away, for a bill that arrives on a day it should already be reminded of. */
        fun checkNow(context: Context) {
            WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<ReminderWorker>().build())
        }
    }
}
