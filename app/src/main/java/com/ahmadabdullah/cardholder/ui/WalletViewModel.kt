package com.ahmadabdullah.cardholder.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.CardStore
import com.ahmadabdullah.cardholder.data.bill.Bill
import com.ahmadabdullah.cardholder.data.bill.BillStore
import com.ahmadabdullah.cardholder.data.bill.UnmatchedBill
import com.ahmadabdullah.cardholder.reminders.ReminderWorker
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WalletViewModel(application: Application) : AndroidViewModel(application) {
    private val cardStore = CardStore.get(application)
    private val billStore = BillStore.get(application)

    /** The saved cards, or null while they are still loading. */
    val cards: StateFlow<List<Card>?> = cardStore.cards

    /** The saved bills, or null while they are still loading. */
    val bills: StateFlow<List<Bill>?> = billStore.bills

    init {
        viewModelScope.launch { cardStore.load() }
        viewModelScope.launch { billStore.load() }
    }

    fun add(card: Card) {
        viewModelScope.launch {
            cardStore.add(card)
            claimBills(card)
        }
    }

    fun update(card: Card) {
        viewModelScope.launch {
            cardStore.replace(card)
            claimBills(card)
        }
    }

    /** Moves bills that were waiting for this card onto it, and reminds of them if they're due soon. */
    private suspend fun claimBills(card: Card) {
        if (billStore.claimUnmatched(card)) ReminderWorker.checkNow(getApplication())
    }

    /** A bill that arrived for a card that isn't saved, to start the add-card form from. */
    suspend fun unmatchedBill(id: String): UnmatchedBill? = billStore.unmatched(id)

    fun delete(id: String) {
        viewModelScope.launch {
            cardStore.delete(id)
            billStore.deleteForCard(id)
        }
    }

    fun setPaid(bill: Bill, paid: Boolean) {
        viewModelScope.launch { billStore.setPaid(bill.id, paid) }
    }
}
