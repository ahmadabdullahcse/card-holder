package com.ahmadabdullah.cardholder.data.bill

import android.content.Context
import com.ahmadabdullah.cardholder.data.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import com.ahmadabdullah.cardholder.data.JsonListFile
import java.io.File
import java.time.LocalDate

/**
 * Keeps the bills in a JSON file in the app's private storage, next to the cards. There is one
 * per app, shared by the screens, the bill reader and the reminders.
 */
class BillStore private constructor(context: Context) {
    private val file = JsonListFile(File(context.filesDir, FILE_NAME), Bill::fromJson, Bill::toJson)
    private val lock = Mutex()
    private val _bills = MutableStateFlow<List<Bill>?>(null)
    private val unmatchedFile =
        JsonListFile(File(context.filesDir, UNMATCHED_FILE_NAME), UnmatchedBill::fromJson, UnmatchedBill::toJson)
    private val unmatchedLock = Mutex()

    /** The saved bills, or null until they have been read from disk. */
    val bills: StateFlow<List<Bill>?> = _bills.asStateFlow()

    suspend fun load(): List<Bill> = lock.withLock {
        _bills.value ?: withContext(Dispatchers.IO) { read() }.also { _bills.value = it }
    }

    /**
     * Saves a bill that has just arrived. If the bank sent the same bill before, its amounts are
     * updated but whether it was paid, and which reminders were shown, are kept.
     *
     * @return true if this bill wasn't saved before.
     */
    suspend fun save(bill: Bill): Boolean {
        var isNew = true
        update { bills ->
            val existing = bills.firstOrNull { it.id == bill.id }
            isNew = existing == null
            val saved = existing?.let { bill.copy(paid = it.paid, remindersSent = it.remindersSent) } ?: bill
            bills.filterNot { it.id == bill.id } + saved
        }
        return isNew
    }

    suspend fun setPaid(id: String, paid: Boolean) = change(id) { it.copy(paid = paid) }

    suspend fun recordReminders(id: String, keys: Collection<String>) =
        change(id) { it.copy(remindersSent = it.remindersSent + keys) }

    /**
     * Keeps a bill for a card that isn't saved yet.
     *
     * @return true if this bill wasn't kept before.
     */
    suspend fun saveUnmatched(bill: UnmatchedBill): Boolean = unmatchedLock.withLock {
        val kept = readUnmatched()
        if (kept.any { it.id == bill.id }) return false
        writeUnmatched(kept + bill)
        true
    }

    suspend fun unmatched(id: String): UnmatchedBill? = unmatchedLock.withLock { readUnmatched().firstOrNull { it.id == id } }

    /**
     * Moves the bills that were waiting for [card] onto it, now that it's saved.
     *
     * @return true if there were any.
     */
    suspend fun claimUnmatched(card: Card): Boolean {
        val claimed = unmatchedLock.withLock {
            val kept = readUnmatched()
            val (mine, others) = kept.partition {
                it.message.card.matches(card) && (it.bankId == null || it.bankId == card.bankId)
            }
            if (mine.isNotEmpty()) writeUnmatched(others)
            mine
        }
        claimed.forEach { save(Bill.from(it.message, card.id)) }
        return claimed.isNotEmpty()
    }

    /**
     * Forgets bills whose due date was before [today], including ones waiting for their card to be
     * added. Their reminders are all done, and the bank's next bill takes their place.
     */
    suspend fun removePast(today: LocalDate = LocalDate.now()) {
        if (load().any { it.dueDate < today }) update { bills -> bills.filterNot { it.dueDate < today } }
        unmatchedLock.withLock {
            val kept = readUnmatched()
            if (kept.any { it.message.dueDate < today }) writeUnmatched(kept.filterNot { it.message.dueDate < today })
        }
    }

    suspend fun deleteForCard(cardId: String) = update { bills -> bills.filterNot { it.cardId == cardId } }

    private suspend fun change(id: String, change: (Bill) -> Bill) =
        update { bills -> bills.map { if (it.id == id) change(it) else it } }

    private suspend fun update(change: (List<Bill>) -> List<Bill>) = lock.withLock {
        val updated = change(_bills.value ?: withContext(Dispatchers.IO) { read() })
        withContext(Dispatchers.IO) { write(updated) }
        _bills.value = updated
    }

    private fun read(): List<Bill> = file.read()

    private fun write(bills: List<Bill>) = file.write(bills)

    private suspend fun readUnmatched(): List<UnmatchedBill> = withContext(Dispatchers.IO) { unmatchedFile.read() }

    private suspend fun writeUnmatched(bills: List<UnmatchedBill>) = withContext(Dispatchers.IO) { unmatchedFile.write(bills) }

    companion object {
        const val FILE_NAME = "bills.json"
        const val UNMATCHED_FILE_NAME = "unmatched_bills.json"

        @Volatile private var instance: BillStore? = null

        fun get(context: Context): BillStore =
            instance ?: synchronized(this) { instance ?: BillStore(context.applicationContext).also { instance = it } }
    }
}
