package com.ahmadabdullah.cardholder

import com.ahmadabdullah.cardholder.data.BankCatalog
import com.ahmadabdullah.cardholder.data.CardNetwork
import com.ahmadabdullah.cardholder.ui.addcard.CardDraft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardDraftTest {
    private val city = BankCatalog.bank("city")!!
    private val brac = BankCatalog.bank("brac")!!
    private val hsbc = BankCatalog.bank("hsbc")!!

    private val complete = CardDraft()
        .withBank(city)
        .withNetwork(CardNetwork.AMEX)
        .withTier("Gold")
        .copy(
            firstName = "Rahim", lastName = "Uddin", firstFour = "3712", lastFour = "0005",
            bdtLimit = "500000", usdLimit = "2000", billingDay = 15,
        )

    @Test
    fun everyBankOffersAtLeastOneTierOnEveryNetworkItLists() {
        BankCatalog.banks.forEach { bank ->
            assertTrue(bank.name, bank.networks.isNotEmpty())
            bank.networks.forEach { assertTrue("${bank.name} $it", bank.tiersFor(it).isNotEmpty()) }
        }
        assertEquals(BankCatalog.banks.size, BankCatalog.banks.map { it.id }.toSet().size)
    }

    @Test
    fun networksAndTiersOnlyComeFromTheChosenBank() {
        val draft = CardDraft().withBank(brac)
        assertFalse(CardNetwork.AMEX in draft.networkOptions)
        assertEquals(brac.tiersFor(CardNetwork.VISA), draft.withNetwork(CardNetwork.VISA).tierOptions)
        assertTrue(CardDraft().networkOptions.isEmpty())
        assertTrue(draft.tierOptions.isEmpty())
    }

    @Test
    fun changingTheBankClearsANetworkItDoesNotIssue() {
        val switched = complete.withBank(brac)
        assertNull(switched.network)
        assertNull(switched.tier)
        assertNull(switched.toCard())
    }

    @Test
    fun aBankWithOneNetworkPicksItAutomatically() {
        assertEquals(CardNetwork.VISA, CardDraft().withBank(hsbc).network)
    }

    @Test
    fun firstFourMustMatchTheNetwork() {
        assertNull(complete.firstFourError)
        assertNotNull(complete.copy(firstFour = "4111").firstFourError)
        val visa = complete.withBank(brac).withNetwork(CardNetwork.VISA).withTier("Gold")
        assertNull(visa.copy(firstFour = "4111").firstFourError)
        val mastercard = visa.withNetwork(CardNetwork.MASTERCARD)
        assertNull(mastercard.copy(firstFour = "5412").firstFourError)
        assertNull(mastercard.copy(firstFour = "2221").firstFourError)
        assertNotNull(mastercard.copy(firstFour = "2800").firstFourError)
    }

    @Test
    fun aCompleteDraftBecomesACard() {
        val card = complete.copy(firstName = " Rahim ").toCard("id")!!
        assertEquals("Rahim", card.firstName)
        assertEquals(500_000L, card.bdtLimit)
        assertEquals("city", card.bankId)
    }

    @Test
    fun missingOrBadFieldsBlockSaving() {
        assertNull(complete.copy(billingDay = null).toCard())
        assertNull(complete.copy(bdtLimit = "0").toCard())
        assertNull(complete.copy(lastFour = "12").toCard())
        assertNull(complete.copy(lastName = "R2D2").toCard())
        assertNotNull(complete.copy(usdLimit = "0").toCard())
    }
}
