package com.ahmadabdullah.cardholder

import com.ahmadabdullah.cardholder.data.BankCatalog
import com.ahmadabdullah.cardholder.ui.cardart.CardDesigns
import org.junit.Assert.assertEquals
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
}
