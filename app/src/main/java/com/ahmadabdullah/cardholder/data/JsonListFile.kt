package com.ahmadabdullah.cardholder.data

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * A list saved as JSON in the app's private storage, kept safe against a damaged file.
 *
 * Each save writes a new file and swaps it in, keeping the previous one as a backup next to it.
 * If the file can't be read (the phone died mid-save, the storage failed), the backup is used
 * instead, so the app opens with the last good list rather than crashing on every launch. A
 * file that can't be read at all is set aside as `.damaged` instead of being overwritten, in
 * case it can be recovered.
 *
 * The backup is just a second copy in the same private storage, with the same protection as the
 * file itself: no other app can read it, and it's left out of cloud backups.
 */
class JsonListFile<T>(
    private val file: File,
    private val fromJson: (JSONObject) -> T,
    private val toJson: (T) -> JSONObject,
) {
    private val backup = File(file.parentFile, "${file.name}.bak")
    private val temp = File(file.parentFile, "${file.name}.tmp")

    fun read(): List<T> {
        if (!file.exists() && !backup.exists()) return emptyList()
        parse(file)?.let { return it }
        if (file.exists()) file.renameTo(File(file.parentFile, "${file.name}.damaged"))
        return parse(backup) ?: emptyList()
    }

    fun write(items: List<T>) {
        val array = JSONArray()
        items.forEach { array.put(toJson(it)) }
        temp.writeText(array.toString())
        // Keep the current file as the backup only if it's sound; never back up a damaged file.
        if (file.exists() && parse(file) != null) file.copyTo(backup, overwrite = true)
        if (!temp.renameTo(file)) {
            temp.copyTo(file, overwrite = true)
            temp.delete()
        }
    }

    private fun parse(source: File): List<T>? = runCatching {
        if (!source.exists()) return null
        val array = JSONArray(source.readText())
        List(array.length()) { fromJson(array.getJSONObject(it)) }
    }.getOrNull()
}
