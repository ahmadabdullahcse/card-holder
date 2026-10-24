package com.ahmadabdullah.cardholder

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ahmadabdullah.cardholder.data.bill.BillImport
import com.ahmadabdullah.cardholder.reminders.BillNotifications
import com.ahmadabdullah.cardholder.reminders.ReminderWorker
import com.ahmadabdullah.cardholder.ui.WalletViewModel
import com.ahmadabdullah.cardholder.ui.formatDay
import com.ahmadabdullah.cardholder.ui.addcard.AddCardScreen
import com.ahmadabdullah.cardholder.ui.addcard.CardDraft
import com.ahmadabdullah.cardholder.ui.detail.CardDetailScreen
import com.ahmadabdullah.cardholder.ui.reorder.ReorderCardsScreen
import com.ahmadabdullah.cardholder.ui.setup.BillReadingSetupScreen
import com.ahmadabdullah.cardholder.ui.theme.CardHolderTheme
import com.ahmadabdullah.cardholder.ui.wallet.WalletScreen
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        BillNotifications.createChannels(this)
        ReminderWorker.scheduleDaily(this)
        // A reminder opens straight onto its card; a bill for an unsaved card opens the form.
        val openScreen = intent.getStringExtra(EXTRA_UNMATCHED_BILL_ID)?.let { ADD_FROM_BILL + it }
            ?: intent.getStringExtra(EXTRA_CARD_ID)
        // A message shared from another app, read once (not again when the screen turns).
        val sharedText = intent.takeIf { savedInstanceState == null && it.action == Intent.ACTION_SEND }
            ?.getStringExtra(Intent.EXTRA_TEXT)
        setContent {
            CardHolderTheme {
                CardHolderApp(openScreen, sharedText)
            }
        }
    }

    companion object {
        const val EXTRA_CARD_ID = "card_id"
        const val EXTRA_UNMATCHED_BILL_ID = "unmatched_bill_id"
    }
}

private const val ADD_CARD = "add"
private const val ADD_FROM_BILL = "add-from:"
private const val EDIT_CARD = "edit:"
private const val SET_UP_REMINDERS = "reminders"
private const val REORDER = "reorder"

@Composable
private fun CardHolderApp(openScreen: String?, sharedText: String?, viewModel: WalletViewModel = viewModel()) {
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val bills by viewModel.bills.collectAsStateWithLifecycle()
    // The wallet when null. Otherwise the add-card form (empty, or filled from a bill), the
    // edit form for a card, the reminders setup, or the id of the card being viewed.
    var screen by rememberSaveable { mutableStateOf(openScreen) }

    BackHandler(enabled = screen != null) { screen = null }

    // A bill shared to Card holder opens its card, or the add-card form if the card isn't saved.
    val context = LocalContext.current
    LaunchedEffect(sharedText) {
        if (sharedText == null) return@LaunchedEffect
        val message = when (val saved = viewModel.importShared(sharedText)) {
            null -> "This isn't a credit card bill Card holder can read"
            is BillImport.Past -> "This bill was due on ${formatDay(saved.message.dueDate)}, so it wasn't added"
            is BillImport.Saved -> {
                screen = saved.card.id
                if (saved.isNew) "Bill added" else "This bill was already added"
            }
            is BillImport.Unmatched -> {
                screen = ADD_FROM_BILL + saved.bill.id
                "Add this card to keep its bill"
            }
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    when (val current = screen) {
        null -> WalletScreen(
            cards = cards,
            onAddCard = { screen = ADD_CARD },
            onOpenCard = { screen = it.id },
            onSetUpReminders = { screen = SET_UP_REMINDERS },
            onReorder = { screen = REORDER },
        )
        SET_UP_REMINDERS -> BillReadingSetupScreen(onBack = { screen = null })
        REORDER -> cards?.let { saved ->
            ReorderCardsScreen(
                cards = saved,
                onBack = { screen = null },
                onDone = { ids ->
                    viewModel.reorder(ids)
                    screen = null
                },
            )
        }
        ADD_CARD -> AddCardScreen(
            onBack = { screen = null },
            onSave = { card ->
                viewModel.add(card)
                screen = null
            },
        )
        else -> when {
            current.startsWith(ADD_FROM_BILL) -> {
                // Null while loading; a bill that's gone (already claimed) opens an empty form.
                val draft by produceState<CardDraft?>(null, current) {
                    value = viewModel.unmatchedBill(current.removePrefix(ADD_FROM_BILL))?.let(CardDraft::from) ?: CardDraft()
                }
                draft?.let { initial ->
                    AddCardScreen(
                        onBack = { screen = null },
                        onSave = { card ->
                            viewModel.add(card)
                            screen = card.id
                        },
                        initial = initial,
                    )
                }
            }
            current.startsWith(EDIT_CARD) -> {
                val card = cards?.firstOrNull { it.id == current.removePrefix(EDIT_CARD) }
                if (card == null) {
                    LaunchedEffect(cards) { if (cards != null) screen = null }
                } else {
                    AddCardScreen(
                        onBack = { screen = card.id },
                        onSave = { edited ->
                            viewModel.update(edited.copy(id = card.id))
                            screen = card.id
                        },
                        title = "Edit card",
                        initial = CardDraft.from(card),
                    )
                }
            }
            else -> {
                val card = cards?.firstOrNull { it.id == current }
                if (card == null) {
                    LaunchedEffect(cards) { if (cards != null) screen = null }
                } else {
                    CardDetailScreen(
                        card = card,
                        // A bill stops showing once its due date has passed, even if the app stays open.
                        bill = bills?.filter { it.cardId == card.id && it.dueDate >= LocalDate.now() }?.maxByOrNull { it.dueDate },
                        onBack = { screen = null },
                        onEdit = { screen = EDIT_CARD + card.id },
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
}
