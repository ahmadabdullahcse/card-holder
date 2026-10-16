package com.ahmadabdullah.cardholder.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.bill.Bill
import com.ahmadabdullah.cardholder.data.bill.Due
import com.ahmadabdullah.cardholder.reminders.REMINDER_DAYS_TEXT
import com.ahmadabdullah.cardholder.ui.CreditCardView
import com.ahmadabdullah.cardholder.ui.formatBdt
import com.ahmadabdullah.cardholder.ui.formatBdtExact
import com.ahmadabdullah.cardholder.ui.formatDay
import com.ahmadabdullah.cardholder.ui.formatDaysLeft
import com.ahmadabdullah.cardholder.ui.formatShortDate
import com.ahmadabdullah.cardholder.ui.formatUsd
import com.ahmadabdullah.cardholder.ui.formatUsdExact
import com.ahmadabdullah.cardholder.ui.monthNames
import com.ahmadabdullah.cardholder.ui.nextBillingDate
import com.ahmadabdullah.cardholder.ui.ordinal
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    card: Card,
    bill: Bill?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetPaid: (Bill, Boolean) -> Unit,
) {
    var confirmingDelete by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(card.bankName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit card")
                    }
                    IconButton(onClick = { confirmingDelete = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remove card")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.widthIn(max = 440.dp)) {
                CreditCardView(card)
                Spacer(Modifier.height(24.dp))
                if (bill != null) {
                    BillSection(bill, onSetPaid = { onSetPaid(bill, it) })
                    Spacer(Modifier.height(24.dp))
                }
                OutlinedCard(Modifier.fillMaxWidth()) {
                    DetailRow("Card", "${card.network.displayName} ${card.tier}")
                    HorizontalDivider()
                    DetailRow("Card holder", card.holderName)
                    HorizontalDivider()
                    DetailRow("Expires", "${monthNames[card.expiryMonth - 1]} ${card.expiryYear}")
                    HorizontalDivider()
                    DetailRow("BDT limit", formatBdt(card.bdtLimit))
                    HorizontalDivider()
                    DetailRow("USD limit", if (card.usdLimit > 0) formatUsd(card.usdLimit) else "None")
                    HorizontalDivider()
                    DetailRow(
                        "Billing date",
                        "${ordinal(card.billingDay)} of every month",
                        "Next on ${formatShortDate(nextBillingDate(card.billingDay))}",
                    )
                }
            }
        }
    }

    if (confirmingDelete) {
        AlertDialog(
            onDismissRequest = { confirmingDelete = false },
            title = { Text("Remove this card?") },
            text = { Text("${card.bankName} ${card.network.displayName} ending in ${card.lastFour} will be removed from Card holder.") },
            confirmButton = {
                TextButton(onClick = { confirmingDelete = false; onDelete() }) { Text("Remove") }
            },
            dismissButton = {
                TextButton(onClick = { confirmingDelete = false }) { Text("Cancel") }
            },
        )
    }
}

/** The latest bill the bank sent for this card: what's owed, and the last safe day to pay it. */
@Composable
private fun BillSection(bill: Bill, onSetPaid: (Boolean) -> Unit) {
    val today = LocalDate.now()
    val payBy = bill.payBy
    val month = bill.statementMonth?.let { "${monthNames[it.monthValue - 1]} bill" } ?: "Latest bill"
    Text(month, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
    OutlinedCard(Modifier.fillMaxWidth()) {
        DetailRow("Total due", dueText(bill.totalDue))
        HorizontalDivider()
        DetailRow("Minimum due", dueText(bill.minDue))
        HorizontalDivider()
        DetailRow("Due date", formatDay(bill.dueDate))
        if (!bill.paid) {
            payBy.bdt?.let {
                HorizontalDivider()
                DetailRow("Pay taka by", formatDay(it), formatDaysLeft(ChronoUnit.DAYS.between(today, it)))
            }
            payBy.usd?.let {
                HorizontalDivider()
                DetailRow(
                    "Pay dollars by",
                    formatDay(it),
                    "${formatDaysLeft(ChronoUnit.DAYS.between(today, it))} · the bank needs time to convert",
                )
            }
        }
        HorizontalDivider()
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                if (bill.paid) "Paid. No more reminders for this bill." else "Reminders come $REMINDER_DAYS_TEXT.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            if (bill.paid) {
                TextButton(onClick = { onSetPaid(false) }) { Text("Not paid") }
            } else {
                Button(onClick = { onSetPaid(true) }) { Text("Mark as paid") }
            }
        }
    }
}

private fun dueText(due: Due): String = listOfNotNull(
    formatBdtExact(due.bdt).takeIf { due.bdt > 0 },
    formatUsdExact(due.usd).takeIf { due.usd > 0 },
).joinToString(" + ").ifEmpty { "Nothing" }

@Composable
private fun DetailRow(label: String, value: String, note: String? = null) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(value, style = MaterialTheme.typography.bodyLarge)
            if (note != null) {
                Text(
                    note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
