package com.ahmadabdullah.cardholder.data

enum class CardNetwork(val displayName: String, val prefixRule: String) {
    VISA("Visa", "Visa numbers start with 4"),
    MASTERCARD("Mastercard", "Mastercard numbers start with 51–55 or 2221–2720"),
    AMEX("American Express", "Amex numbers start with 34 or 37"),
    DINERS("Diners Club", "Diners Club numbers start with 300–305, 36, 38 or 39"),
    UNIONPAY("UnionPay", "UnionPay numbers start with 62"),
    JCB("JCB", "JCB numbers start with 3528–3589");

    /** Whether a card number beginning with these four digits can belong to this network. */
    fun acceptsFirstFour(firstFour: String): Boolean {
        if (firstFour.length != 4 || !firstFour.all(Char::isDigit)) return false
        val prefix = firstFour.toInt()
        return when (this) {
            VISA -> firstFour.startsWith("4")
            MASTERCARD -> prefix in 5100..5599 || prefix in 2221..2720
            AMEX -> firstFour.startsWith("34") || firstFour.startsWith("37")
            DINERS -> prefix in 3000..3059 || prefix == 3095 || prefix / 100 in setOf(36, 38, 39)
            UNIONPAY -> firstFour.startsWith("62")
            JCB -> prefix in 3528..3589
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
 *
 * Taken from each bank's own credit card pages in October 2026. Co-branded and Islamic cards
 * are listed under the tier they are issued on (an EBL Daraz Visa is still a Visa card of
 * its tier), and corporate cards are left out. HSBC is not listed: it closed its retail
 * banking in Bangladesh in 2025–26.
 */
object BankCatalog {
    private const val CLASSIC = "Classic"
    private const val STANDARD = "Standard"
    private const val GOLD = "Gold"
    private const val PLATINUM = "Platinum"
    private const val SIGNATURE = "Signature"
    private const val INFINITE = "Infinite"
    private const val TITANIUM = "Titanium"
    private const val WORLD = "World"
    private const val WORLD_ELITE = "World Elite"
    private const val BLUE = "Blue"
    private const val PLATINUM_RESERVE = "Platinum Reserve"
    private const val ULTRAMARINE = "Ultramarine"
    private const val EMERALD = "Emerald"
    private const val DIAMOND = "Diamond"

    private fun bank(id: String, name: String, vararg offerings: Pair<CardNetwork, List<String>>) =
        Bank(id, name, mapOf(*offerings))

    val banks: List<Bank> = listOf(
        // abbl.com/ebiz/cards/credit-card
        bank(
            "ab", "AB Bank",
            CardNetwork.MASTERCARD to listOf(GOLD, TITANIUM, WORLD),
        ),
        // bankasia-bd.com/retail/credit
        bank(
            "bank_asia", "Bank Asia",
            CardNetwork.VISA to listOf(GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(GOLD, TITANIUM, WORLD_ELITE),
        ),
        // bracbank.com/retail/card
        bank(
            "brac", "BRAC Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE, INFINITE),
            CardNetwork.MASTERCARD to listOf(CLASSIC, GOLD, PLATINUM, TITANIUM, WORLD),
            CardNetwork.DINERS to listOf(EMERALD),
        ),
        // citybankplc.com/cards
        bank(
            "city", "City Bank",
            CardNetwork.VISA to listOf(PLATINUM),
            CardNetwork.MASTERCARD to listOf(WORLD_ELITE),
            CardNetwork.AMEX to listOf(BLUE, GOLD, PLATINUM, PLATINUM_RESERVE, ULTRAMARINE),
        ),
        // dhakabank.com.bd/credit-card
        bank(
            "dhaka", "Dhaka Bank",
            CardNetwork.VISA to listOf(GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
        ),
        // dutchbanglabank.com/electronic-banking/credit-cards.html
        bank(
            "dbbl", "Dutch-Bangla Bank",
            CardNetwork.VISA to listOf(GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(GOLD, TITANIUM, WORLD),
        ),
        // ebl.com.bd/retail/EBL-Cards
        bank(
            "ebl", "Eastern Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE, INFINITE),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD, WORLD_ELITE),
            CardNetwork.DINERS to listOf(STANDARD),
            CardNetwork.UNIONPAY to listOf(PLATINUM),
        ),
        // ificbank.com.bd/personal/product/credit-cards
        bank(
            "ific", "IFIC Bank",
            CardNetwork.VISA to listOf(GOLD, PLATINUM),
        ),
        // islamibankbd.com/cards (Khidmah)
        bank(
            "islami", "Islami Bank Bangladesh",
            CardNetwork.VISA to listOf(GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(GOLD, PLATINUM, WORLD),
            CardNetwork.UNIONPAY to listOf(GOLD, PLATINUM),
        ),
        // jamunabankbd.com credit card schedule of charges, 2025
        bank(
            "jamuna", "Jamuna Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
        ),
        // lankabangla.com/credit-card
        bank(
            "lankabangla", "LankaBangla Finance",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM),
            CardNetwork.MASTERCARD to listOf(CLASSIC, GOLD, TITANIUM),
        ),
        // mblbd.com/retail-cards
        bank(
            "mercantile", "Mercantile Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
        ),
        // mutualtrustbank.com/cards/credit-card
        bank(
            "mtb", "Mutual Trust Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(CLASSIC, GOLD, TITANIUM, WORLD),
            CardNetwork.UNIONPAY to listOf(PLATINUM, DIAMOND),
        ),
        // nccbank.com.bd/NccbCards/creditCard
        bank(
            "ncc", "NCC Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(TITANIUM),
        ),
        // card.onebank.com.bd
        bank(
            "one", "ONE Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
        ),
        // primebank.com.bd/conventional-all-cards; Zero by Prime Bank is a Visa Signature
        bank(
            "prime", "Prime Bank",
            CardNetwork.VISA to listOf(GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(PLATINUM, WORLD),
            CardNetwork.JCB to listOf(GOLD, PLATINUM),
        ),
        // new.pubalibangla.com/CreditCard
        bank(
            "pubali", "Pubali Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM),
            CardNetwork.MASTERCARD to listOf(TITANIUM, WORLD),
        ),
        // cards.southeastbank.com.bd/products
        bank(
            "southeast", "Southeast Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(STANDARD, TITANIUM, WORLD),
        ),
        // sc.com/bd/credit-cards; Gold is the Saadiq Visa Gold
        bank(
            "scb", "Standard Chartered",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(TITANIUM),
        ),
        // tblbd.com/personal/cards/credit-cards
        bank(
            "trust", "Trust Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.UNIONPAY to listOf(PLATINUM),
        ),
        // ucb.com.bd/cards
        bank(
            "ucb", "United Commercial Bank",
            CardNetwork.VISA to listOf(CLASSIC, GOLD, PLATINUM, SIGNATURE),
            CardNetwork.MASTERCARD to listOf(CLASSIC, GOLD, PLATINUM, WORLD),
        ),
    )

    fun bank(id: String): Bank? = banks.firstOrNull { it.id == id }
}
