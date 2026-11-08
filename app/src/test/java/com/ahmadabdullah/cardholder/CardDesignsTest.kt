package com.ahmadabdullah.cardholder

import com.ahmadabdullah.cardholder.data.BankCatalog
import com.ahmadabdullah.cardholder.data.CardNetwork
import com.ahmadabdullah.cardholder.ui.cardart.CardDesigns
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CardDesignsTest {
    @Test
    fun everyCardInTheCatalogHasItsOwnDesign() {
        val missing = BankCatalog.banks.flatMap { bank ->
            bank.networks.flatMap { network ->
                bank.tiersFor(network)
                    .filterNot { CardDesigns.hasOwnDesign(bank.id, network, it) }
                    .map { tier -> "${bank.name} $network $tier" }
            }
        }
        assertEquals(emptyList<String>(), missing)
    }

    @Test
    fun everyEditionHasItsOwnDesignAndOlderCardsStillGetOne() {
        val flexi = CardDesigns.forCard("brac", CardNetwork.VISA, "Platinum", "Flexi")
        val tara = CardDesigns.forCard("brac", CardNetwork.VISA, "Platinum", "Tara")
        assertNotEquals(flexi, tara)
        assertEquals(tara, CardDesigns.forCard("brac", CardNetwork.VISA, "Platinum"))
        // An edition the app no longer knows falls back to the tier's design.
        assertEquals(tara, CardDesigns.forCard("brac", CardNetwork.VISA, "Platinum", "Gone"))
        BankCatalog.banks.forEach { bank ->
            bank.networks.forEach { network ->
                bank.tiersFor(network).forEach { tier ->
                    val editions = CardDesigns.variantsFor(bank.id, network, tier)
                    assertEquals(editions.size, editions.map { CardDesigns.forCard(bank.id, network, tier, it) }.distinct().size)
                }
            }
        }
    }
}
