package com.ahmadabdullah.cardholder.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.CardStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WalletViewModel(application: Application) : AndroidViewModel(application) {
    private val store = CardStore(application)

    /** The saved cards, or null while they are still loading. */
    val cards: StateFlow<List<Card>?> = store.cards

    init {
        viewModelScope.launch { store.load() }
    }

    fun add(card: Card) {
        viewModelScope.launch { store.add(card) }
    }

    fun delete(id: String) {
        viewModelScope.launch { store.delete(id) }
    }
}
