package com.ahmadabdullah.cardholder.ui.addcard

import com.ahmadabdullah.cardholder.data.Bank
import com.ahmadabdullah.cardholder.data.BankCatalog
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.CardNetwork
import java.util.UUID

/**
 * What has been typed into the add-card form so far. Choosing a bank narrows the networks,
 * and choosing a network narrows the tiers, so the draft never holds a combination the
 * bank doesn't issue.
 */
data class CardDraft(
    val bank: Bank? = null,
    val network: CardNetwork? = null,
    val tier: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val firstFour: String = "",
    val lastFour: String = "",
    val bdtLimit: String = "",
    val usdLimit: String = "",
    val billingDay: Int? = null,
) {
    val networkOptions: List<CardNetwork> get() = bank?.networks.orEmpty()
    val tierOptions: List<String> get() = network?.let { bank?.tiersFor(it) }.orEmpty()

    fun withBank(bank: Bank): CardDraft {
        if (bank == this.bank) return this
        val network = bank.networks.singleOrNull()
        return copy(bank = bank, network = network, tier = network?.let { bank.tiersFor(it).singleOrNull() })
    }

    fun withNetwork(network: CardNetwork): CardDraft {
        if (network == this.network) return this
        return copy(network = network, tier = bank?.tiersFor(network)?.singleOrNull())
    }

    fun withTier(tier: String): CardDraft = copy(tier = tier)

    val firstNameError: String? get() = nameError(firstName, "First name")
    val lastNameError: String? get() = nameError(lastName, "Last name")

    val firstFourError: String?
        get() = when {
            firstFour.length < 4 -> "Enter the first 4 digits"
            network != null && !network.acceptsFirstFour(firstFour) -> network.prefixRule
            else -> null
        }

    val lastFourError: String? get() = if (lastFour.length < 4) "Enter the last 4 digits" else null

    val bdtLimitError: String?
        get() = when {
            bdtLimit.isEmpty() -> "Enter the BDT limit"
            bdtLimit.toLongOrNull()?.takeIf { it > 0 } == null -> "Enter an amount above 0"
            else -> null
        }

    val usdLimitError: String?
        get() = when {
            usdLimit.isEmpty() -> "Enter the USD limit"
            usdLimit.toLongOrNull() == null -> "Enter a whole dollar amount, or 0"
            else -> null
        }

    val billingDayError: String? get() = if (billingDay == null) "Choose the billing date" else null

    val isComplete: Boolean
        get() = bank != null && network != null && tier != null &&
            listOf(
                firstNameError, lastNameError, firstFourError, lastFourError,
                bdtLimitError, usdLimitError, billingDayError,
            ).all { it == null }

    /** The card this draft describes, or null while anything is missing or invalid. */
    fun toCard(id: String = UUID.randomUUID().toString()): Card? {
        if (!isComplete) return null
        return Card(
            id = id,
            bankId = bank!!.id,
            network = network!!,
            tier = tier!!,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            firstFour = firstFour,
            lastFour = lastFour,
            bdtLimit = bdtLimit.toLong(),
            usdLimit = usdLimit.toLong(),
            billingDay = billingDay!!,
        )
    }

    companion object {
        val banks: List<Bank> get() = BankCatalog.banks
        val billingDays: List<Int> = (1..31).toList()

        private fun nameError(name: String, label: String): String? = when {
            name.isBlank() -> "Enter the ${label.lowercase()}"
            !name.trim().all { it.isLetter() || it == ' ' || it == '.' || it == '-' || it == '\'' } ->
                "$label can only have letters"
            else -> null
        }
    }
}
