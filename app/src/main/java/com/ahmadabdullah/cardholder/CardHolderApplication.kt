package com.ahmadabdullah.cardholder

import android.app.Application
import androidx.work.WorkManager
import com.ahmadabdullah.cardholder.reminders.BillNotifications
import java.io.File

class CardHolderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Before the bill reader or a reminder posts anything, even if the app was never opened.
        BillNotifications.createChannels(this)
        // Versions before 1.1 downloaded the holiday list each week. Card holder is offline now,
        // so that job and the list it saved are cleared from phones that had them.
        WorkManager.getInstance(this).cancelUniqueWork("weekly_calendar_sync")
        File(filesDir, "bank_calendar.json").delete()
    }
}
