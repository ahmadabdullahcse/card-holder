package com.ahmadabdullah.cardholder.ui.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.ui.CARD_ASPECT_RATIO
import com.ahmadabdullah.cardholder.ui.CreditCardView
import com.ahmadabdullah.cardholder.ui.setup.rememberBillReadingAccess

/** How much of each card shows above the next one in the stack: enough for its bank and tier. */
private val StackPeek = 64.dp

/** Cards stop growing past this width so they stay card-sized on tablets and in landscape. */
private val MaxCardWidth = 440.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    cards: List<Card>?,
    onAddCard: () -> Unit,
    onOpenCard: (Card) -> Unit,
    onSetUpReminders: () -> Unit,
) {
    val access = rememberBillReadingAccess()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Card holder") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddCard,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Add card") },
            )
        },
    ) { padding ->
        when {
            cards == null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            cards.isEmpty() -> EmptyWallet(Modifier.padding(padding))
            else -> CardStack(
                cards,
                onOpenCard,
                header = { if (!access.isComplete) RemindersPrompt(onSetUpReminders) },
                contentPadding = PaddingValues(bottom = 96.dp),
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun CardStack(
    cards: List<Card>,
    onOpenCard: (Card) -> Unit,
    header: @Composable () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(contentPadding),
        contentAlignment = Alignment.TopCenter,
    ) {
        val cardWidth = min(maxWidth, MaxCardWidth)
        val cardHeight = cardWidth / CARD_ASPECT_RATIO
        Column(Modifier.width(cardWidth), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            header()
            Box(Modifier.height(StackPeek * (cards.size - 1) + cardHeight)) {
                cards.forEachIndexed { index, card ->
                    CreditCardView(card, Modifier.offset(y = StackPeek * index), onClick = { onOpenCard(card) })
                }
            }
        }
    }
}

/** Asks the user to set up bill reading, until both switches are on. */
@Composable
private fun RemindersPrompt(onSetUp: () -> Unit) {
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Never miss a bill", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "Let Card holder read your bank's bill messages and remind you before each due date.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = onSetUp) { Text("Set up reminders") }
        }
    }
}

@Composable
private fun EmptyWallet(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("No cards yet", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "Add your credit cards to keep their limits and billing dates in one place.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
