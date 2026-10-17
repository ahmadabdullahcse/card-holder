package com.ahmadabdullah.cardholder

import android.app.Application
import com.ahmadabdullah.cardholder.sync.CalendarSync

class CardHolderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Before anything works out a pay-by date: the screens, the bill reader or a reminder.
        CalendarSync.loadSaved(this)
        // The first download runs as soon as the phone is online, then once a week.
        CalendarSync.scheduleWeekly(this)
    }
}
