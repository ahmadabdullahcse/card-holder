package com.ahmadabdullah.cardholder.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File

/** Keeps the cards in a JSON file in the app's private storage. */
class CardStore(context: Context) {
    private val file = File(context.filesDir, FILE_NAME)
    private val lock = Mutex()
    private val _cards = MutableStateFlow<List<Card>?>(null)

    /** The saved cards, or null until they have been read from disk. */
    val cards: StateFlow<List<Card>?> = _cards.asStateFlow()

    suspend fun load() = lock.withLock {
        if (_cards.value == null) _cards.value = withContext(Dispatchers.IO) { read() }
    }

    suspend fun add(card: Card) = update { it + card }

    suspend fun delete(id: String) = update { cards -> cards.filterNot { it.id == id } }

    private suspend fun update(change: (List<Card>) -> List<Card>) = lock.withLock {
        val updated = change(_cards.value ?: withContext(Dispatchers.IO) { read() })
        withContext(Dispatchers.IO) { write(updated) }
        _cards.value = updated
    }

    private fun read(): List<Card> {
        if (!file.exists()) return emptyList()
        val array = JSONArray(file.readText())
        return List(array.length()) { Card.fromJson(array.getJSONObject(it)) }
    }

    private fun write(cards: List<Card>) {
        val array = JSONArray()
        cards.forEach { array.put(it.toJson()) }
        val temp = File(file.parentFile, "$FILE_NAME.tmp")
        temp.writeText(array.toString())
        temp.renameTo(file)
    }

    companion object {
        const val FILE_NAME = "cards.json"
    }
}
