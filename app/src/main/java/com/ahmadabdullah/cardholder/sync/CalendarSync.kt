package com.ahmadabdullah.cardholder.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ahmadabdullah.cardholder.data.bill.BankCalendar
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/**
 * Keeps the bank holidays current by downloading data/bank-calendar.json from the GitHub
 * repository once a week. Editing that file on GitHub updates every installed app, which
 * matters because holidays and working Saturdays are often announced only days ahead.
 *
 * The last download is kept on the phone, so the calendar works offline; without one, the
 * built-in lists in [BankCalendar] are used alone.
 */
object CalendarSync {
    const val URL = "https://raw.githubusercontent.com/ahmadabdullahcse/card-holder/main/data/bank-calendar.json"
    private const val FILE_NAME = "bank_calendar.json"
    private const val WEEKLY = "weekly_calendar_sync"

    /** Puts the last downloaded calendar to use. Call when the app starts. */
    fun loadSaved(context: Context) {
        val file = File(context.filesDir, FILE_NAME)
        if (file.exists()) runCatching { apply(file.readText()) }
    }

    /** Downloads the calendar once a week, whenever the phone is online. */
    fun scheduleWeekly(context: Context) {
        val request = PeriodicWorkRequestBuilder<Worker>(7, TimeUnit.DAYS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(WEEKLY, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    /** Downloads the calendar, and saves and uses it if it reads correctly. */
    fun download(context: Context): Boolean {
        val connection = URL(URL).openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = 15_000
            connection.readTimeout = 15_000
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return false
            val text = connection.inputStream.bufferedReader().use { it.readText() }
            apply(text)
            File(context.filesDir, FILE_NAME).writeText(text)
            true
        } finally {
            connection.disconnect()
        }
    }

    /** Reads the calendar and hands it to [BankCalendar]. Throws if the file is malformed. */
    private fun apply(text: String) {
        val json = JSONObject(text)
        BankCalendar.useDownloaded(
            holidays = dates(json.getJSONArray("holidays")),
            workingDays = dates(json.optJSONArray("workingDays") ?: JSONArray()),
        )
    }

    private fun dates(array: JSONArray): Set<LocalDate> =
        List(array.length()) { LocalDate.parse(array.getString(it)) }.toSet()

    class Worker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result =
            if (runCatching { download(applicationContext) }.getOrDefault(false)) Result.success() else Result.retry()
    }
}
