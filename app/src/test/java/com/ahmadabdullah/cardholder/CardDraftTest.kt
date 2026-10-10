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
    private val ab = BankCatalog.bank("ab")!!

    /** October 2026. */
    private val today = 2026 * 12 + 9

    private val complete = CardDraft(today = today)
        .withBank(city)
        .withNetwork(CardNetwork.AMEX)
        .withTier("Gold")
        .copy(
            firstName = "Rahim", lastName = "Uddin", firstFour = "3712", lastFour = "0005",
            bdtLimit = "500000", usdLimit = "2000", billingDay = 15,
            expiryMonth = 8, expiryYear = 2029,
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
        assertEquals(CardNetwork.MASTERCARD, CardDraft().withBank(ab).network)
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
    fun otherNetworksCheckTheirOwnPrefixes() {
        assertTrue(CardNetwork.DINERS.acceptsFirstFour("3600"))
        assertTrue(CardNetwork.DINERS.acceptsFirstFour("3050"))
        assertFalse(CardNetwork.DINERS.acceptsFirstFour("3700"))
        assertTrue(CardNetwork.UNIONPAY.acceptsFirstFour("6212"))
        assertFalse(CardNetwork.UNIONPAY.acceptsFirstFour("6011"))
        assertTrue(CardNetwork.JCB.acceptsFirstFour("3530"))
        assertFalse(CardNetwork.JCB.acceptsFirstFour("3590"))
    }

    @Test
    fun expiredMonthsCanNotBeChosen() {
        val thisYear = complete.withExpiryYear(2026)
        assertEquals((10..12).toList(), thisYear.expiryMonthOptions)
        assertEquals((1..12).toList(), complete.withExpiryYear(2027).expiryMonthOptions)
        assertEquals((2026..2036).toList(), complete.expiryYearOptions)
        // August 2026 has passed, so switching the year to 2026 drops the chosen month.
        assertNull(thisYear.expiryMonth)
        assertNull(thisYear.toCard())
        assertEquals(12, complete.withExpiryMonth(12).withExpiryYear(2026).expiryMonth)
    }

    @Test
    fun aCompleteDraftBecomesACard() {
        val card = complete.copy(firstName = " Rahim ").toCard("id")!!
        assertEquals("Rahim", card.firstName)
        assertEquals(500_000L, card.bdtLimit)
        assertEquals("city", card.bankId)
        assertEquals(8, card.expiryMonth)
        assertEquals(2029, card.expiryYear)
    }

    @Test
    fun missingOrBadFieldsBlockSaving() {
        assertNull(complete.copy(billingDay = null).toCard())
        assertNull(complete.copy(expiryMonth = null).toCard())
        assertNull(complete.copy(bdtLimit = "0").toCard())
        assertNull(complete.copy(lastFour = "12").toCard())
        assertNull(complete.copy(lastName = "R2D2").toCard())
        assertNotNull(complete.copy(usdLimit = "0").toCard())
    }
}
