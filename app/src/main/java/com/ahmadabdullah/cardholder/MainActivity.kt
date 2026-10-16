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
import com.ahmadabdullah.cardholder.reminders.BillNotifications
import com.ahmadabdullah.cardholder.reminders.ReminderWorker
import com.ahmadabdullah.cardholder.ui.WalletViewModel
import com.ahmadabdullah.cardholder.ui.addcard.AddCardScreen
import com.ahmadabdullah.cardholder.ui.detail.CardDetailScreen
import com.ahmadabdullah.cardholder.ui.setup.BillReadingSetupScreen
import com.ahmadabdullah.cardholder.ui.theme.CardHolderTheme
import com.ahmadabdullah.cardholder.ui.wallet.WalletScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        BillNotifications.createChannels(this)
        ReminderWorker.scheduleDaily(this)
        // A reminder opens straight onto its card.
        val openCardId = intent.getStringExtra(EXTRA_CARD_ID)
        setContent {
            CardHolderTheme {
                CardHolderApp(openCardId)
            }
        }
    }

    companion object {
        const val EXTRA_CARD_ID = "card_id"
    }
}

private const val ADD_CARD = "add"
private const val SET_UP_REMINDERS = "reminders"

@Composable
private fun CardHolderApp(openCardId: String?, viewModel: WalletViewModel = viewModel()) {
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val bills by viewModel.bills.collectAsStateWithLifecycle()
    // The wallet when null; otherwise the add-card form, the reminders setup, or the id of the
    // card being viewed.
    var screen by rememberSaveable { mutableStateOf(openCardId) }

    BackHandler(enabled = screen != null) { screen = null }

    when (val current = screen) {
        null -> WalletScreen(
            cards = cards,
            onAddCard = { screen = ADD_CARD },
            onOpenCard = { screen = it.id },
            onSetUpReminders = { screen = SET_UP_REMINDERS },
        )
        SET_UP_REMINDERS -> BillReadingSetupScreen(onBack = { screen = null })
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
                    bill = bills?.filter { it.cardId == card.id }?.maxByOrNull { it.dueDate },
                    onBack = { screen = null },
                    onDelete = {
                        viewModel.delete(card.id)
                        screen = null
                    },
                    onSetPaid = viewModel::setPaid,
                )
            }
        }
    }
}
