package com.ahmadabdullah.cardholder.ui.addcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ahmadabdullah.cardholder.data.BankCatalog
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.CardNetwork
import com.ahmadabdullah.cardholder.ui.CreditCardView
import com.ahmadabdullah.cardholder.ui.formatBdt
import com.ahmadabdullah.cardholder.ui.formatUsd
import com.ahmadabdullah.cardholder.ui.monthNames
import com.ahmadabdullah.cardholder.ui.ordinal
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    onBack: () -> Unit,
    onSave: (Card) -> Unit,
    title: String = "Add card",
    initial: CardDraft = CardDraft(),
) {
    var draft by rememberSaveable(stateSaver = DraftSaver) { mutableStateOf(initial) }
    // Errors for fields left empty stay hidden until Save is tapped; wrong values show as typed.
    var showAllErrors by rememberSaveable { mutableStateOf(false) }
    fun error(error: String?, filled: Boolean) = if (showAllErrors || filled) error else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.widthIn(max = 520.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                draft.preview()?.let {
                    CreditCardView(it)
                    Spacer(Modifier.height(4.dp))
                }

                Dropdown(
                    label = "Bank",
                    options = CardDraft.banks,
                    selected = draft.bank,
                    optionLabel = { it.name },
                    onSelect = { draft = draft.withBank(it) },
                    error = error(if (draft.bank == null) "Choose a bank" else null, false),
                )
                Dropdown(
                    label = "Card network",
                    options = draft.networkOptions,
                    selected = draft.network,
                    optionLabel = { it.displayName },
                    onSelect = { draft = draft.withNetwork(it) },
                    enabled = draft.bank != null,
                    hint = if (draft.bank == null) "Choose a bank first" else null,
                    error = error(if (draft.bank != null && draft.network == null) "Choose a network" else null, false),
                )
                Dropdown(
                    label = "Card tier",
                    options = draft.tierOptions,
                    selected = draft.tier,
                    optionLabel = { it },
                    onSelect = { draft = draft.withTier(it) },
                    enabled = draft.network != null,
                    hint = if (draft.network == null) "Choose a network first" else null,
                    error = error(if (draft.network != null && draft.tier == null) "Choose a tier" else null, false),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NameField(
                        "First name", draft.firstName, { draft = draft.copy(firstName = it) },
                        error(draft.firstNameError, draft.firstName.isNotBlank()), Modifier.weight(1f),
                    )
                    NameField(
                        "Last name", draft.lastName, { draft = draft.copy(lastName = it) },
                        error(draft.lastNameError, draft.lastName.isNotBlank()), Modifier.weight(1f),
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NumberField(
                        "First 4 digits", draft.firstFour, { draft = draft.copy(firstFour = it) },
                        error(draft.firstFourError, draft.firstFour.length == 4), maxDigits = 4,
                        modifier = Modifier.weight(1f),
                    )
                    NumberField(
                        "Last 4 digits", draft.lastFour, { draft = draft.copy(lastFour = it) },
                        error(draft.lastFourError, false), maxDigits = 4,
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Dropdown(
                        label = "Expiry month",
                        options = draft.expiryMonthOptions,
                        selected = draft.expiryMonth,
                        optionLabel = { String.format(Locale.US, "%02d · %s", it, monthNames[it - 1]) },
                        onSelect = { draft = draft.withExpiryMonth(it) },
                        error = error(draft.expiryMonthError, false),
                        modifier = Modifier.weight(1f),
                    )
                    Dropdown(
                        label = "Expiry year",
                        options = draft.expiryYearOptions,
                        selected = draft.expiryYear,
                        optionLabel = { it.toString() },
                        onSelect = { draft = draft.withExpiryYear(it) },
                        error = error(draft.expiryYearError, false),
                        modifier = Modifier.weight(1f),
                    )
                }
                Text(
                    "Only the first and last 4 digits are kept. Never enter your full card number, CVV or PIN.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                NumberField(
                    "BDT limit", draft.bdtLimit, { draft = draft.copy(bdtLimit = it) },
                    error(draft.bdtLimitError, draft.bdtLimit.isNotEmpty()), maxDigits = 9, prefix = "৳",
                    hint = draft.bdtLimit.toLongOrNull()?.let(::formatBdt),
                )
                NumberField(
                    "USD limit (optional)", draft.usdLimit, { draft = draft.copy(usdLimit = it) },
                    error(draft.usdLimitError, draft.usdLimit.isNotEmpty()), maxDigits = 7, prefix = "$",
                    hint = draft.usdLimit.toLongOrNull()?.let(::formatUsd) ?: "Leave empty if the card has no USD limit",
                )
                Dropdown(
                    label = "Billing date",
                    options = CardDraft.billingDays,
                    selected = draft.billingDay,
                    optionLabel = { "${ordinal(it)} of every month" },
                    onSelect = { draft = draft.copy(billingDay = it) },
                    error = error(draft.billingDayError, false),
                )

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val card = draft.toCard()
                        if (card == null) showAllErrors = true else onSave(card)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                ) {
                    Text("Save card")
                }
            }
        }
    }
}

/** A card to show above the form once enough is chosen to draw it. */
private fun CardDraft.preview(): Card? {
    val bank = bank ?: return null
    val network = network ?: return null
    return Card(
        id = "preview",
        bankId = bank.id,
        network = network,
        tier = tier ?: "",
        firstName = firstName.trim().ifEmpty { "First" },
        lastName = lastName.trim().ifEmpty { "Last" },
        firstFour = firstFour.padEnd(4, '•'),
        lastFour = lastFour.padEnd(4, '•'),
        bdtLimit = 0,
        usdLimit = 0,
        billingDay = 1,
        expiryMonth = expiryMonth ?: 0,
        expiryYear = expiryYear ?: 0,
    )
}

/** A read-only dropdown. It lists only [options], so nothing outside them can be picked. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> Dropdown(
    label: String,
    options: List<T>,
    selected: T?,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
    enabled: Boolean = true,
    hint: String? = null,
    error: String? = null,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selected?.let(optionLabel).orEmpty(),
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && enabled) },
            isError = error != null,
            supportingText = (error ?: hint)?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled),
        )
        ExposedDropdownMenu(expanded = expanded && enabled, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
private fun NameField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.take(30)) },
        label = { Text(label) },
        singleLine = true,
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
        modifier = modifier,
    )
}

@Composable
private fun NumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    maxDigits: Int,
    modifier: Modifier = Modifier.fillMaxWidth(),
    prefix: String? = null,
    hint: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { typed -> onValueChange(typed.filter(Char::isDigit).take(maxDigits)) },
        label = { Text(label) },
        prefix = prefix?.let { { Text(it) } },
        singleLine = true,
        isError = error != null,
        supportingText = (error ?: hint)?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = modifier,
    )
}

private val DraftSaver = Saver<CardDraft, List<Any?>>(
    save = {
        listOf(
            it.bank?.id, it.network?.name, it.tier, it.firstName, it.lastName,
            it.firstFour, it.lastFour, it.bdtLimit, it.usdLimit, it.billingDay,
            it.expiryMonth, it.expiryYear,
        )
    },
    restore = {
        CardDraft(
            bank = (it[0] as String?)?.let(BankCatalog::bank),
            network = (it[1] as String?)?.let(CardNetwork::valueOf),
            tier = it[2] as String?,
            firstName = it[3] as String,
            lastName = it[4] as String,
            firstFour = it[5] as String,
            lastFour = it[6] as String,
            bdtLimit = it[7] as String,
            usdLimit = it[8] as String,
            billingDay = it[9] as Int?,
            expiryMonth = it[10] as Int?,
            expiryYear = it[11] as Int?,
        )
    },
)
