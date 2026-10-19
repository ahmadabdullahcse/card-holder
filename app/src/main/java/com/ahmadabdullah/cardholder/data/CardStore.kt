package com.ahmadabdullah.cardholder.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Keeps the cards in a JSON file in the app's private storage. There is one per app, shared by
 * the screens and the bill reader, so each sees the other's changes.
 */
class CardStore private constructor(context: Context) {
    private val file = JsonListFile(File(context.filesDir, FILE_NAME), Card::fromJson, Card::toJson)
    private val lock = Mutex()
    private val _cards = MutableStateFlow<List<Card>?>(null)

    /** The saved cards, or null until they have been read from disk. */
    val cards: StateFlow<List<Card>?> = _cards.asStateFlow()

    suspend fun load() = lock.withLock {
        if (_cards.value == null) _cards.value = withContext(Dispatchers.IO) { read() }
    }

    suspend fun add(card: Card) = update { it + card }

    /** Replaces the saved card that has the same id. */
    suspend fun replace(card: Card) = update { cards -> cards.map { if (it.id == card.id) card else it } }

    suspend fun delete(id: String) = update { cards -> cards.filterNot { it.id == id } }

    private suspend fun update(change: (List<Card>) -> List<Card>) = lock.withLock {
        val updated = change(_cards.value ?: withContext(Dispatchers.IO) { read() })
        withContext(Dispatchers.IO) { write(updated) }
        _cards.value = updated
    }

    private fun read(): List<Card> = file.read()

    private fun write(cards: List<Card>) = file.write(cards)

    companion object {
        const val FILE_NAME = "cards.json"

        @Volatile private var instance: CardStore? = null

        fun get(context: Context): CardStore =
            instance ?: synchronized(this) { instance ?: CardStore(context.applicationContext).also { instance = it } }
    }
}
