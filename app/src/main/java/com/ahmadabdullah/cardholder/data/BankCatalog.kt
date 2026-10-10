package com.ahmadabdullah.cardholder.data

enum class CardNetwork(val displayName: String, val prefixRule: String) {
    VISA("Visa", "Visa numbers start with 4"),
    MASTERCARD("Mastercard", "Mastercard numbers start with 51–55 or 2221–2720"),
    AMEX("American Express", "Amex numbers start with 34 or 37");

    /** Whether a card number beginning with these four digits can belong to this network. */
    fun acceptsFirstFour(firstFour: String): Boolean {
        if (firstFour.length != 4 || !firstFour.all(Char::isDigit)) return false
        val prefix = firstFour.toInt()
        return when (this) {
            VISA -> firstFour.startsWith("4")
            MASTERCARD -> prefix in 5100..5599 || prefix in 2221..2720
            AMEX -> firstFour.startsWith("34") || firstFour.startsWith("37")
        }
    }
}

/** A bank, and for each network it issues, the credit card tiers it offers on that network. */
data class Bank(
    val id: String,
    val name: String,
    val offerings: Map<CardNetwork, List<String>>,
) {
    val networks: List<CardNetwork> get() = offerings.keys.sortedBy { it.ordinal }

    fun tiersFor(network: CardNetwork): List<String> = offerings[network].orEmpty()
}

/**
 * Credit cards issued in Bangladesh, by bank. Only combinations listed here can be picked,
 * so a bank never shows a network it doesn't issue, nor a network a tier it doesn't have.
 */
object BankCatalog {
    private const val CLASSIC = "Classic"
    private const val GOLD = "Gold"
    private const val PLATINUM = "Platinum"
    private const val SIGNATURE = "Signature"
    private const val INFINITE = "Infinite"
    private const val TITANIUM = "Titanium"
    private const val WORLD = "World"
    private const val GREEN = "Green"

    private fun bank(id: String, name: String, vararg offerings: Pair<CardNetwork, List<String>>) =
        Bank(id, name, mapOf(*offerings))

    val banks: List<Bank> = listOf(
        bank(
            "ab", "AB Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM),
            CardNetwork.MASTERCARD to listOf(GOLD, TITANIUM),
        ),
        bank(
            "bank_asia", "Bank Asia",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
        ),
        bank(
            "brac", "BRAC Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE, INFINITE),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
        ),
        bank(
            "city", "City Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE, INFINITE),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
            CardNetwork.AMEX to listOf(GREEN, GOLD, PLATINUM),
        ),
        bank(
            "dhaka", "Dhaka Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM),
            CardNetwork.MASTERCARD to listOf(GOLD, TITANIUM),
        ),
        bank(
            "dbbl", "Dutch-Bangla Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM),
            CardNetwork.MASTERCARD to listOf(GOLD, TITANIUM),
        ),
        bank(
            "ebl", "Eastern Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE, INFINITE),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
        ),
        bank(
            "hsbc", "HSBC Bangladesh",
            CardNetwork.VISA to listOf(GOLD, PLATINUM, SIGNATURE),
        ),
        bank(
            "ific", "IFIC Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM),
            CardNetwork.MASTERCARD to listOf(TITANIUM),
        ),
        bank(
            "islami", "Islami Bank Bangladesh",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM),
        ),
        bank(
            "jamuna", "Jamuna Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM),
            CardNetwork.MASTERCARD to listOf(TITANIUM),
        ),
        bank(
            "lankabangla", "LankaBangla Finance",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
        ),
        bank(
            "mercantile", "Mercantile Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
        ),
        bank(
            "mtb", "Mutual Trust Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
        ),
        bank(
            "ncc", "NCC Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(TITANIUM),
        ),
        bank(
            "one", "ONE Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
        ),
        bank(
            "prime", "Prime Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(GOLD, TITANIUM, WORLD),
        ),
        bank(
            "pubali", "Pubali Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM),
        ),
        bank(
            "southeast", "Southeast Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
        ),
        bank(
            "scb", "Standard Chartered",
            CardNetwork.VISA to listOf(PLATINUM, SIGNATURE, INFINITE),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
        ),
        bank(
            "trust", "Trust Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM),
        ),
        bank(
            "ucb", "United Commercial Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
        ),
    )

    fun bank(id: String): Bank? = banks.firstOrNull { it.id == id }
}
