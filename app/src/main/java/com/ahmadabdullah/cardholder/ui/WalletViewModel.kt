package com.ahmadabdullah.cardholder.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.CardStore
import com.ahmadabdullah.cardholder.data.bill.Bill
import com.ahmadabdullah.cardholder.data.bill.BillStore
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
        viewModelScope.launch { cardStore.add(card) }
    }

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
