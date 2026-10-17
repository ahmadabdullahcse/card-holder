package com.ahmadabdullah.cardholder.sync

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.ahmadabdullah.cardholder.BuildConfig
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/** A newer version published on GitHub. */
data class AppUpdate(val version: String, val notes: String, val apkUrl: String)

/**
 * Updates the app from the repository's GitHub Releases, for testers who installed it from an
 * APK rather than Google Play. A release is found by its tag ("v1.1") and must have the APK
 * attached. Android still asks the user to confirm each install, and only accepts an APK
 * signed with the same key as the installed app.
 *
 * Google Play doesn't allow apps to update themselves, so this must be left out of the build
 * published there (see [BuildConfig.SELF_UPDATE]).
 */
object AppUpdater {
    private const val LATEST_RELEASE = "https://api.github.com/repos/ahmadabdullahcse/card-holder/releases/latest"
    private const val APK_FILE = "updates/card-holder.apk"

    /** The latest release, if it's newer than this app; null if not, or if GitHub can't be reached. */
    fun check(): AppUpdate? {
        if (!BuildConfig.SELF_UPDATE) return null
        val release = runCatching { JSONObject(get(LATEST_RELEASE)) }.getOrNull() ?: return null
        val version = release.optString("tag_name").removePrefix("v")
        if (!isNewer(version, BuildConfig.VERSION_NAME)) return null
        val assets = release.optJSONArray("assets") ?: return null
        val apk = (0 until assets.length()).map { assets.getJSONObject(it) }
            .firstOrNull { it.optString("name").endsWith(".apk") } ?: return null
        return AppUpdate(version, release.optString("body"), apk.getString("browser_download_url"))
    }

    /** Whether "1.10" is newer than "1.9", comparing each number in turn. */
    fun isNewer(candidate: String, current: String): Boolean {
        fun parts(version: String) = version.split('.').map { it.toIntOrNull() ?: 0 }
        val a = parts(candidate)
        val b = parts(current)
        for (i in 0 until maxOf(a.size, b.size)) {
            val difference = a.getOrElse(i) { 0 } - b.getOrElse(i) { 0 }
            if (difference != 0) return difference > 0
        }
        return false
    }

    /** Whether Android lets Card holder install APKs. The user grants this once, in settings. */
    fun canInstall(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O || context.packageManager.canRequestPackageInstalls()

    /** The settings page where the user lets Card holder install updates. */
    fun installPermissionSettings(context: Context): Intent =
        Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))

    /** Downloads the update's APK into the app's cache, reporting progress from 0 to 1. */
    fun download(context: Context, update: AppUpdate, onProgress: (Float) -> Unit): File {
        val file = File(context.cacheDir, APK_FILE).apply { parentFile?.mkdirs() }
        val connection = URL(update.apkUrl).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 15_000
            connection.readTimeout = 30_000
            val size = connection.contentLengthLong
            connection.inputStream.use { input ->
                file.outputStream().use { output ->
                    val buffer = ByteArray(64 * 1024)
                    var copied = 0L
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        output.write(buffer, 0, read)
                        copied += read
                        if (size > 0) onProgress(copied.toFloat() / size)
                    }
                }
            }
        } finally {
            connection.disconnect()
        }
        return file
    }

    /** Hands the downloaded APK to Android's installer, which asks the user to confirm. */
    fun install(context: Context, apk: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.updates", apk)
        context.startActivity(
            Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "application/vnd.android.package-archive")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun get(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = 15_000
            connection.readTimeout = 15_000
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }
}
