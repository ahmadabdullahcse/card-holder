package com.ahmadabdullah.cardholder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ahmadabdullah.cardholder.ui.WalletViewModel
import com.ahmadabdullah.cardholder.ui.addcard.AddCardScreen
import com.ahmadabdullah.cardholder.ui.detail.CardDetailScreen
import com.ahmadabdullah.cardholder.ui.theme.CardHolderTheme
import com.ahmadabdullah.cardholder.ui.wallet.WalletScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CardHolderTheme {
                CardHolderApp()
            }
        }
    }
}

private const val ADD_CARD = "add"

@Composable
private fun CardHolderApp(viewModel: WalletViewModel = viewModel()) {
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    // The wallet when null; otherwise the add-card form or the id of the card being viewed.
    var screen by rememberSaveable { mutableStateOf<String?>(null) }

    BackHandler(enabled = screen != null) { screen = null }

    when (val current = screen) {
        null -> WalletScreen(
            cards = cards,
            onAddCard = { screen = ADD_CARD },
            onOpenCard = { screen = it.id },
        )
        ADD_CARD -> AddCardScreen(
            onBack = { screen = null },
            onSave = { card ->
                viewModel.add(card)
                screen = null
            },
        )
        else -> {
            val card = cards?.firstOrNull { it.id == current }
            if (card == null) {
                LaunchedEffect(cards) { if (cards != null) screen = null }
            } else {
                CardDetailScreen(
                    card = card,
                    onBack = { screen = null },
                    onDelete = {
                        viewModel.delete(card.id)
                        screen = null
                    },
                )
            }
        }
    }
}
