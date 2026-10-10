package com.ahmadabdullah.cardholder.ui.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
) {
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
            else -> CardStack(cards, onOpenCard, PaddingValues(bottom = 96.dp), Modifier.padding(padding))
        }
    }
}

@Composable
private fun CardStack(
    cards: List<Card>,
    onOpenCard: (Card) -> Unit,
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
        Box(Modifier.width(cardWidth).height(StackPeek * (cards.size - 1) + cardHeight)) {
            cards.forEachIndexed { index, card ->
                CreditCardView(card, Modifier.offset(y = StackPeek * index), onClick = { onOpenCard(card) })
            }
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
