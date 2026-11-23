package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.graphics.Color
import com.ahmadabdullah.cardholder.data.CardNetwork
import com.ahmadabdullah.cardholder.data.CardNetwork.AMEX
import com.ahmadabdullah.cardholder.data.CardNetwork.DINERS
import com.ahmadabdullah.cardholder.data.CardNetwork.JCB
import com.ahmadabdullah.cardholder.data.CardNetwork.MASTERCARD
import com.ahmadabdullah.cardholder.data.CardNetwork.UNIONPAY
import com.ahmadabdullah.cardholder.data.CardNetwork.VISA
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BEAM
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BLOBS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BRAC_BRIDGE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BRAC_CLASSIC
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BRAC_FLEXI
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BRAC_GLOBE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BRAC_GOLD
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BRAC_NIGHT_TILES
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BRAC_PORTAL
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BRAC_SAILS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BRAC_SIGNATURE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BRAC_TARA
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BRAC_TARA_WORLD
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BRUSHED
import com.ahmadabdullah.cardholder.ui.cardart.Finish.CARBON
import com.ahmadabdullah.cardholder.ui.cardart.Finish.DBBL_ARABESQUE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.DBBL_DARK_DAMASK
import com.ahmadabdullah.cardholder.ui.cardart.Finish.DBBL_SILVER_DAMASK
import com.ahmadabdullah.cardholder.ui.cardart.Finish.DBBL_GOLD_GLOBE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.CITY_AMEX
import com.ahmadabdullah.cardholder.ui.cardart.Finish.EBL_CLASSIC
import com.ahmadabdullah.cardholder.ui.cardart.Finish.PRIME_WORLD
import com.ahmadabdullah.cardholder.ui.cardart.Finish.PRIME_FACETS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.PRIME_LEATHER
import com.ahmadabdullah.cardholder.ui.cardart.Finish.PRIME_GOLD_ARC
import com.ahmadabdullah.cardholder.ui.cardart.Finish.PRIME_SMOKE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.PRIME_TILES
import com.ahmadabdullah.cardholder.ui.cardart.Finish.EBL_GOLD
import com.ahmadabdullah.cardholder.ui.cardart.Finish.EBL_PLATINUM
import com.ahmadabdullah.cardholder.ui.cardart.Finish.EBL_SIGNATURE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.EBL_TITANIUM
import com.ahmadabdullah.cardholder.ui.cardart.Finish.EBL_WORLD
import com.ahmadabdullah.cardholder.ui.cardart.Finish.EBL_WORLD_ELITE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.EBL_DINERS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.EBL_UNIONPAY
import com.ahmadabdullah.cardholder.ui.cardart.Finish.CITY_INFINITE_WAVE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.CITY_ISLAMIC_BLUE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.CITY_ISLAMIC_GOLD
import com.ahmadabdullah.cardholder.ui.cardart.Finish.CITY_MINERAL
import com.ahmadabdullah.cardholder.ui.cardart.Finish.CITY_SATIN
import com.ahmadabdullah.cardholder.ui.cardart.Finish.CHEVRON
import com.ahmadabdullah.cardholder.ui.cardart.Finish.DIAMONDS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.DOTS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.FACETS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.GLOBE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.GUILLOCHE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.MARBLE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.ONE_TRAILS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.ONE_RIBS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.ONE_GOLD_STREAKS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.ONE_PEONIES
import com.ahmadabdullah.cardholder.ui.cardart.Finish.NCC_SILVER_SWEEP
import com.ahmadabdullah.cardholder.ui.cardart.Finish.NCC_DUAL_GOLD
import com.ahmadabdullah.cardholder.ui.cardart.Finish.NCC_BRUSHED
import com.ahmadabdullah.cardholder.ui.cardart.Finish.NCC_MANDALA
import com.ahmadabdullah.cardholder.ui.cardart.Finish.PANEL
import com.ahmadabdullah.cardholder.ui.cardart.Finish.PLAIN
import com.ahmadabdullah.cardholder.ui.cardart.Finish.MTB_DIAMOND
import com.ahmadabdullah.cardholder.ui.cardart.Finish.MTB_RINGS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.MTB_CHEVRONS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.MTB_LATTICE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.MTB_GLOBE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.MTB_HONEYCOMB
import com.ahmadabdullah.cardholder.ui.cardart.Finish.ISLAMI_GLOBE_STAND
import com.ahmadabdullah.cardholder.ui.cardart.Finish.ISLAMI_CRESCENT
import com.ahmadabdullah.cardholder.ui.cardart.Finish.ISLAMI_FAN
import com.ahmadabdullah.cardholder.ui.cardart.Finish.ISLAMI_GRAPHITE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.ISLAMI_GOLD
import com.ahmadabdullah.cardholder.ui.cardart.Finish.LB_WAVES
import com.ahmadabdullah.cardholder.ui.cardart.Finish.LB_ARCS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.MTB_ARCS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.UCB_CHROME
import com.ahmadabdullah.cardholder.ui.cardart.Finish.TRUST_GLASS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.TRUST_STRINGS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.TRUST_LANTERN
import com.ahmadabdullah.cardholder.ui.cardart.Finish.TRUST_TUNNEL
import com.ahmadabdullah.cardholder.ui.cardart.Finish.TRUST_VORTEX
import com.ahmadabdullah.cardholder.ui.cardart.Finish.UCB_DAMASK
import com.ahmadabdullah.cardholder.ui.cardart.Finish.UCB_MANDALA
import com.ahmadabdullah.cardholder.ui.cardart.Finish.UCB_MANDALA_CARBON
import com.ahmadabdullah.cardholder.ui.cardart.Finish.UCB_DOT_GLOBE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.UCB_WORLD_RIBBON
import com.ahmadabdullah.cardholder.ui.cardart.Finish.MTB_NETWORK
import com.ahmadabdullah.cardholder.ui.cardart.Finish.RAYS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.RIBBON
import com.ahmadabdullah.cardholder.ui.cardart.Finish.ROSETTE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.SCB_PANEL
import com.ahmadabdullah.cardholder.ui.cardart.Finish.SCB_SMART
import com.ahmadabdullah.cardholder.ui.cardart.Finish.SILK
import com.ahmadabdullah.cardholder.ui.cardart.Finish.STAR
import com.ahmadabdullah.cardholder.ui.cardart.Finish.STRIPE

/**
 * The look of each bank's cards, modelled on the bank's own designs (their colors, finish and
 * motifs as shown on its website in October 2026) but drawn from scratch: no bank's artwork or
 * logo is copied.
 */
object CardDesigns {
    private fun d(
        light: Long, dark: Long, finish: Finish = PLAIN, accent: Long = 0xFFFFFFFF, darkText: Boolean = false, vertical: Boolean = false,
        topDarkText: Boolean = darkText, markOnLight: Boolean = darkText,
    ) = CardDesign(listOf(Color(light), Color(dark)), finish, Color(accent), darkText, vertical, topDarkText, markOnLight)

    private val designs: Map<String, CardDesign> = buildMap {
        fun put(bank: String, network: CardNetwork, tier: String, design: CardDesign) = put(key(bank, network, tier), design)
        fun putVariant(bank: String, network: CardNetwork, tier: String, variant: String, design: CardDesign) =
            put(key(bank, network, tier, variant), design)
        fun putAll(bank: String, networks: List<CardNetwork>, tier: String, design: CardDesign) = networks.forEach { put(bank, it, tier, design) }
        val both = listOf(VISA, MASTERCARD)

        // AB Bank: red is the bank's color.
        put("ab", MASTERCARD, "Gold", d(0xFFE9C25E, 0xFFB88A2E, BRUSHED, darkText = true))
        put("ab", MASTERCARD, "Titanium", d(0xFFF2F2F2, 0xFFC4C4C4, STRIPE, 0xFFD7262E, darkText = true))
        put("ab", MASTERCARD, "World", d(0xFF8E1B1F, 0xFF3A0A0C, SILK))

        // Bank Asia
        put("bank_asia", VISA, "Gold", d(0xFFE9B04A, 0xFFB36E1E, BRUSHED))
        put("bank_asia", VISA, "Platinum", d(0xFF2B2B2B, 0xFF080808, RIBBON, 0xFFBDBDBD))
        put("bank_asia", VISA, "Signature", d(0xFF1D2556, 0xFF0E1233, ROSETTE))
        put("bank_asia", MASTERCARD, "Gold", d(0xFFF0962E, 0xFFA9541A, BRUSHED))
        put("bank_asia", MASTERCARD, "Titanium", d(0xFF1E9E6A, 0xFF0B5E3D, SILK))
        put("bank_asia", MASTERCARD, "World Elite", d(0xFF1A1A1A, 0xFF000000, SILK, 0xFF8A8A8A))

        // BRAC Bank: each card drawn after its real one (see BracArt.kt).
        putAll("brac", both, "Classic", d(0xFFF4F5F7, 0xFFC9CCD2, BRAC_CLASSIC, 0xFF6B707A, darkText = true))
        putAll("brac", both, "Gold", d(0xFFF8CF4A, 0xFFDB9C22, BRAC_GOLD, 0xFF9A6F23, darkText = true))
        put("brac", MASTERCARD, "Platinum", d(0xFF1C1F24, 0xFF030405, BRAC_NIGHT_TILES, 0xFFA3AEBD))
        // Older cards saved before editions existed get the Tara look.
        put("brac", VISA, "Platinum", d(0xFFA8232A, 0xFF4A0709, BRAC_TARA, 0xFFF6CDB4))
        putVariant("brac", VISA, "Platinum", "Tara", d(0xFFA8232A, 0xFF4A0709, BRAC_TARA, 0xFFF6CDB4))
        putVariant("brac", VISA, "Platinum", "Flexi", d(0xFF1A92D4, 0xFF04305A, BRAC_FLEXI, 0xFFC4F0FF))
        put("brac", VISA, "Signature", d(0xFF1A1020, 0xFF040306, BRAC_SIGNATURE, 0xFFE2B44C))
        put("brac", VISA, "Infinite", d(0xFF2B4067, 0xFF070C18, BRAC_PORTAL, 0xFFCFE2FF))
        put("brac", MASTERCARD, "Titanium", d(0xFF0D111B, 0xFF010204, BRAC_BRIDGE, 0xFF4FB4F0))
        put("brac", MASTERCARD, "World", d(0xFF1FCBE0, 0xFF0590B4, BRAC_SAILS, 0xFFE6FFFB))
        putVariant("brac", MASTERCARD, "World", "Standard", d(0xFF1FCBE0, 0xFF0590B4, BRAC_SAILS, 0xFFE6FFFB))
        putVariant("brac", MASTERCARD, "World", "Tara", d(0xFF3FB9CC, 0xFF0F8399, BRAC_TARA_WORLD, 0xFFFFFFFF))
        put("brac", DINERS, "Emerald", d(0xFF1F7D44, 0xFF052611, BRAC_GLOBE, 0xFF9EEFC0))

        // City Bank: the Amex cards carry fine engraved lines.
        put("city", AMEX, "Blue", d(0xFFC4DCF2, 0xFF86AEDA, CITY_AMEX, 0xFF2A5D93, darkText = true))
        putVariant("city", AMEX, "Blue", "Standard", d(0xFFC4DCF2, 0xFF86AEDA, CITY_AMEX, 0xFF2A5D93, darkText = true))
        putVariant("city", AMEX, "Blue", "Islamic", d(0xFF168C70, 0xFF0F4C8C, CITY_ISLAMIC_BLUE, 0xFFB8F0E0))
        put("city", AMEX, "Gold", d(0xFFDDB9AE, 0xFFA87E73, CITY_AMEX, 0xFF5E3B33, darkText = true))
        putVariant("city", AMEX, "Gold", "Standard", d(0xFFDDB9AE, 0xFFA87E73, CITY_AMEX, 0xFF5E3B33, darkText = true))
        putVariant("city", AMEX, "Gold", "Islamic", d(0xFFEAD47E, 0xFFC4A042, CITY_ISLAMIC_GOLD, 0xFFFFF6D0, darkText = true))
        put("city", AMEX, "Platinum", d(0xFFF0F0F1, 0xFFB4B4B7, CITY_AMEX, 0xFF4A4C50, darkText = true))
        put("city", AMEX, "Platinum Reserve", d(0xFF6C6D70, 0xFF2C2D30, CITY_AMEX, 0xFFE0E0E0))
        put("city", AMEX, "Ultramarine", d(0xFF2E86CC, 0xFF0D4A8E, CITY_AMEX, 0xFFCFE6FF))
        put("city", VISA, "Platinum", d(0xFF2A2A2C, 0xFF0A0A0B, CITY_MINERAL, 0xFFB8B8B8))
        put("city", VISA, "Infinite", d(0xFF232325, 0xFF060607, CITY_INFINITE_WAVE, 0xFFA0A0A4, vertical = true))
        put("city", MASTERCARD, "World Elite", d(0xFF0A0A0B, 0xFF000000, CITY_SATIN, 0xFF7E8084))

        // Dhaka Bank: black silk for the metal tiers.
        put("dhaka", VISA, "Gold", d(0xFFD9A84E, 0xFF8E5A1E, SILK, 0xFFFFE2A8))
        put("dhaka", VISA, "Platinum", d(0xFF4A4A4A, 0xFF111111, SILK))
        put("dhaka", VISA, "Signature", d(0xFF1C1C1C, 0xFF000000, RAYS, 0xFFE3B85C))
        put("dhaka", MASTERCARD, "Titanium", d(0xFF444444, 0xFF111111, SILK))
        put("dhaka", MASTERCARD, "World", d(0xFF1C2A7A, 0xFF0A1036, GLOBE, 0xFF5FA8FF))

        // Dutch-Bangla Bank: a gold arabesque on its black cards.
        putAll("dbbl", both, "Gold", d(0xFFE3CC62, 0xFFC9AD3E, DBBL_GOLD_GLOBE, 0xFFC4A43C, darkText = true))
        put("dbbl", VISA, "Platinum", d(0xFFF0F0F2, 0xFFC4C6CA, DBBL_SILVER_DAMASK, darkText = true))
        put("dbbl", VISA, "Signature", d(0xFF1C1B1C, 0xFF0C0B0C, DBBL_ARABESQUE, 0xFFC9A05A))
        put("dbbl", MASTERCARD, "Titanium", d(0xFF2A2A2C, 0xFF000000, DBBL_DARK_DAMASK))
        put("dbbl", MASTERCARD, "World", d(0xFF1C1B1C, 0xFF0C0B0C, DBBL_ARABESQUE, 0xFFC9A05A))

        // Eastern Bank
        put("ebl", VISA, "Classic", d(0xFFE8E9EA, 0xFFB0B3B6, EBL_CLASSIC, 0xFFF2C400, darkText = true))
        put("ebl", VISA, "Gold", d(0xFFE4B759, 0xFFB07C2A, EBL_GOLD, darkText = true))
        put("ebl", VISA, "Platinum", d(0xFF2F86D6, 0xFF0B2F6E, EBL_PLATINUM, 0xFF9FD8FF))
        put("ebl", VISA, "Signature", d(0xFF0B1A1F, 0xFF020405, EBL_SIGNATURE, 0xFF2BB3C8))
        put("ebl", VISA, "Infinite", d(0xFF4A4A4E, 0xFF2A2A2D, BRUSHED))
        put("ebl", MASTERCARD, "Titanium", d(0xFF1A1A1B, 0xFF050505, EBL_TITANIUM))
        put("ebl", MASTERCARD, "World", d(0xFF121218, 0xFF020203, EBL_WORLD, 0xFFF2B744))
        put("ebl", MASTERCARD, "World Elite", d(0xFF2A2A2B, 0xFF0E0E0F, EBL_WORLD_ELITE))
        put("ebl", DINERS, "Standard", d(0xFF1E1C1D, 0xFF0A0909, EBL_DINERS, 0xFFFFC21A))
        put("ebl", UNIONPAY, "Platinum", d(0xFFF2F2F3, 0xFFC4C6C9, EBL_UNIONPAY, darkText = true))

        // IFIC Bank
        put("ific", VISA, "Gold", d(0xFFF0C34A, 0xFFD19A22, CHEVRON, darkText = true))
        put("ific", VISA, "Platinum", d(0xFF4A4C50, 0xFF1F2023, RIBBON, 0xFFD0D0D0))

        // Islami Bank. Platinum comes as the standard graphite card or the blue Priority card;
        // older cards saved before editions existed get the standard one.
        listOf(VISA, MASTERCARD, UNIONPAY).forEach {
            put("islami", it, "Gold", d(0xFFF0B820, 0xFFC8890E, ISLAMI_GOLD, 0xFF1F5E2C, darkText = true))
            put("islami", it, "Platinum", d(0xFF4A4B4E, 0xFF161618, ISLAMI_GRAPHITE, 0xFFA89870))
            putVariant("islami", it, "Platinum", "Standard", d(0xFF4A4B4E, 0xFF161618, ISLAMI_GRAPHITE, 0xFFA89870))
            putVariant("islami", it, "Platinum", "Priority", d(0xFF6E9CE0, 0xFF2A5CBC, ISLAMI_FAN, darkText = true, markOnLight = false))
        }
        put("islami", VISA, "Signature", d(0xFF232C6E, 0xFF161C50, ISLAMI_CRESCENT, 0xFFD9A860))
        put("islami", MASTERCARD, "World", d(0xFF1E6E6A, 0xFF1A8A50, ISLAMI_GLOBE_STAND))

        // Jamuna Bank
        put("jamuna", VISA, "Classic", d(0xFFF2F2F2, 0xFFBDBDBD, BRUSHED, darkText = true))
        put("jamuna", VISA, "Gold", d(0xFFF2C14E, 0xFFC98A22, BRUSHED, darkText = true))
        put("jamuna", VISA, "Platinum", d(0xFF2A2A2A, 0xFF080808, ROSETTE, 0xFFA0A0A0))
        put("jamuna", VISA, "Signature", d(0xFF2A3FA0, 0xFF142060, ROSETTE))

        // LankaBangla Finance: bright flat colors with soft rounded shapes.
        put("lankabangla", VISA, "Classic", d(0xFFE8202F, 0xFFE01E2C, LB_ARCS, 0xFFE8202F))
        put("lankabangla", VISA, "Gold", d(0xFFF7C4B8, 0xFFF4BBAE, LB_ARCS, 0xFFF6C0B3, darkText = true))
        put("lankabangla", VISA, "Platinum", d(0xFF0E6B75, 0xFF0B5F68, LB_ARCS, 0xFF0E6B75))
        put("lankabangla", MASTERCARD, "Classic", d(0xFFE8202F, 0xFFE01E2C, LB_WAVES, 0xFFE8202F))
        put("lankabangla", MASTERCARD, "Gold", d(0xFFEF5A22, 0xFFE9521C, LB_WAVES, 0xFFEF5A22))
        put("lankabangla", MASTERCARD, "Titanium", d(0xFF00A79D, 0xFF009C92, LB_WAVES, 0xFF00A79D))

        // Mercantile Bank
        put("mercantile", VISA, "Classic", d(0xFF1F9AA8, 0xFF0D5E6A, FACETS))
        put("mercantile", VISA, "Gold", d(0xFFE8B84A, 0xFFB07D22, BRUSHED, darkText = true))
        put("mercantile", VISA, "Platinum", d(0xFF24346E, 0xFF0E173D, FACETS))
        put("mercantile", VISA, "Signature", d(0xFFC2307A, 0xFF6E1446, FACETS))
        put("mercantile", MASTERCARD, "Titanium", d(0xFF2E2F33, 0xFF0C0C0E, FACETS, 0xFF9A9A9A))
        put("mercantile", MASTERCARD, "World", d(0xFF121A33, 0xFF03060E, GLOBE, 0xFF4F9BFF))

        // Mutual Trust Bank
        putAll("mtb", both, "Classic", d(0xFF24684F, 0xFF0D3B2B, MTB_RINGS, 0xFFBFEBD6))
        putAll("mtb", both, "Gold", d(0xFF1C1C1D, 0xFF050505, MTB_CHEVRONS, 0xFFC9A24B))
        put("mtb", VISA, "Platinum", d(0xFFFBFBFC, 0xFFE4E5E9, MTB_HONEYCOMB, darkText = true))
        put("mtb", VISA, "Signature", d(0xFF151515, 0xFF000000, MTB_ARCS, 0xFF9A9A9E))
        put("mtb", MASTERCARD, "Titanium", d(0xFFF1F2F2, 0xFFC9CDCD, MTB_LATTICE, 0xFF7E8A8A, darkText = true))
        put("mtb", MASTERCARD, "World", d(0xFF0E0E10, 0xFF000000, MTB_GLOBE, 0xFF8FC1E8))
        put("mtb", UNIONPAY, "Platinum", d(0xFF143C6C, 0xFF061833, MTB_NETWORK, 0xFF7FB8E8))
        put("mtb", UNIONPAY, "Diamond", d(0xFF2A2F4E, 0xFF0D1020, MTB_DIAMOND, 0xFFE8C8E0))

        // NCC Bank
        put("ncc", VISA, "Classic", d(0xFFE2E3E5, 0xFFB4B6BA, NCC_SILVER_SWEEP, darkText = true))
        put("ncc", VISA, "Gold", d(0xFFF2D35A, 0xFFD6A82E, NCC_DUAL_GOLD, darkText = true))
        put("ncc", VISA, "Platinum", d(0xFF8A8C90, 0xFF6A6C70, NCC_BRUSHED))
        put("ncc", VISA, "Signature", d(0xFF121214, 0xFF060607, NCC_MANDALA, 0xFFC9A45A))
        // No reference picture for Titanium yet, so it keeps its earlier look.
        put("ncc", MASTERCARD, "Titanium", d(0xFF3A3B3E, 0xFF141416, BRUSHED))

        // ONE Bank: streaks of golden light on black.
        put("one", VISA, "Classic", d(0xFFE6E7E9, 0xFFCDCFD2, ONE_PEONIES, darkText = true))
        put("one", VISA, "Gold", d(0xFF2A2612, 0xFF0A0C10, ONE_GOLD_STREAKS, 0xFFF2C420))
        put("one", VISA, "Platinum", d(0xFF8A8A8E, 0xFF2A2A2C, ONE_RIBS))
        put("one", VISA, "Signature", d(0xFF1C1B1A, 0xFF060606, ONE_TRAILS, 0xFFE0A050))

        // Prime Bank
        put("prime", VISA, "Gold", d(0xFFFDFBF6, 0xFFEFE8D8, PRIME_GOLD_ARC, 0xFFE2B85A, darkText = true))
        put("prime", VISA, "Platinum", d(0xFF1A1A1A, 0xFF050505, PRIME_LEATHER, 0xFFC9A24B))
        // No reference picture for Signature yet, so it keeps its earlier look.
        put("prime", VISA, "Signature", d(0xFF141414, 0xFF000000, RIBBON, 0xFF8F8F8F))
        put("prime", MASTERCARD, "Platinum", d(0xFF161616, 0xFF050505, PRIME_FACETS, 0xFFC9A24B))
        put("prime", MASTERCARD, "World", d(0xFF0A0A0B, 0xFF000000, PRIME_WORLD, 0xFF47C0F0))
        put("prime", JCB, "Gold", d(0xFFF7C93A, 0xFFE8A417, PRIME_TILES, 0xFFFFE27A, darkText = true))
        put("prime", JCB, "Platinum", d(0xFF0B1A2E, 0xFF02060E, PRIME_SMOKE, 0xFF6FB0F0))

        // Pubali Bank
        put("pubali", VISA, "Classic", d(0xFF167A4A, 0xFF0B3F27, FACETS, 0xFF4BD08C))
        put("pubali", VISA, "Gold", d(0xFFE8C25A, 0xFFB5832A, BRUSHED, darkText = true))
        put("pubali", VISA, "Platinum", d(0xFF1E1E1E, 0xFF050505, RIBBON, 0xFFC9A24B))
        put("pubali", MASTERCARD, "Titanium", d(0xFF141414, 0xFF000000, RIBBON, 0xFF34B3A6))
        put("pubali", MASTERCARD, "World", d(0xFF141414, 0xFF000000, RIBBON, 0xFF8E6BD8))

        // Southeast Bank
        put("southeast", VISA, "Classic", d(0xFF3B4FB5, 0xFF7A2D8F, SILK, 0xFFFF7BC0))
        put("southeast", VISA, "Gold", d(0xFF2A1A08, 0xFF050302, RAYS, 0xFFE8A93A))
        put("southeast", VISA, "Platinum", d(0xFF2B2B2D, 0xFF0B0B0C, RIBBON, 0xFFC0C0C0))
        put("southeast", VISA, "Signature", d(0xFF0F2F33, 0xFF050C0E, FACETS, 0xFF2E8A8F))
        put("southeast", MASTERCARD, "Standard", d(0xFFE2CE9A, 0xFFB89B5E, BRUSHED, darkText = true))
        put("southeast", MASTERCARD, "Titanium", d(0xFFE3E3E3, 0xFFA8A8A8, BRUSHED, darkText = true))
        put("southeast", MASTERCARD, "World", d(0xFF14213D, 0xFF070C1A, RAYS, 0xFFD4A94E))

        // Standard Chartered: a patterned panel down the right.
        // No reference picture for Classic yet, so it keeps its earlier look.
        put("scb", VISA, "Classic", d(0xFFE9ECEF, 0xFFB9C0C7, BRUSHED, darkText = true))
        put("scb", VISA, "Gold", d(0xFF0E56AE, 0xFF0A4590, SCB_PANEL, 0xFF7FB4F0))
        // Older cards saved before editions existed get the Smart look.
        put("scb", VISA, "Platinum", d(0xFFF8F9FB, 0xFFE6E9EE, SCB_SMART, 0xFF9AA0AA, darkText = true))
        putVariant("scb", VISA, "Platinum", "Smart", d(0xFFF8F9FB, 0xFFE6E9EE, SCB_SMART, 0xFF9AA0AA, darkText = true))
        putVariant("scb", VISA, "Platinum", "Saadiq", d(0xFF4C4C50, 0xFF2A2A2E, SCB_PANEL, 0xFFB4B4B8))
        put("scb", VISA, "Signature", d(0xFF0B4C9C, 0xFF083A80, SCB_PANEL, 0xFF6FA6E8))
        put("scb", MASTERCARD, "Titanium", d(0xFF0D5C66, 0xFF0A4A55, SCB_PANEL, 0xFF4FC0C8))

        // Trust Bank
        put("trust", VISA, "Classic", d(0xFF0C1410, 0xFF000000, TRUST_TUNNEL, 0xFF3ED67A))
        put("trust", VISA, "Gold", d(0xFF2A1410, 0xFF0A0505, TRUST_LANTERN, 0xFFF09A20))
        put("trust", VISA, "Platinum", d(0xFF2C2C2E, 0xFF1A1A1C, TRUST_STRINGS, 0xFF2E8FD6))
        put("trust", VISA, "Signature", d(0xFF9E1414, 0xFF6A0808, TRUST_GLASS))
        put("trust", UNIONPAY, "Platinum", d(0xFF080A10, 0xFF000000, TRUST_VORTEX))

        // United Commercial Bank
        // The chrome Classic is pale at the top and black below, so its top line is printed dark.
        putAll("ucb", both, "Classic", d(0xFFE0E0E2, 0xFF2A2A2E, UCB_CHROME, topDarkText = true, markOnLight = true))
        putAll("ucb", both, "Gold", d(0xFFF0C65E, 0xFFB9852F, UCB_DAMASK, darkText = true))
        put("ucb", VISA, "Platinum", d(0xFF151416, 0xFF050505, UCB_MANDALA, 0xFF6E6E72))
        put("ucb", MASTERCARD, "Platinum", d(0xFF4E4F52, 0xFF2C2D30, UCB_MANDALA_CARBON, 0xFF9A9A9E))
        put("ucb", VISA, "Signature", d(0xFF164A92, 0xFF0B2B62, UCB_DOT_GLOBE))
        put("ucb", MASTERCARD, "World", d(0xFF0E0E10, 0xFF000000, UCB_WORLD_RIBBON, 0xFFE2B85A))
    }

    /** A sensible look for a tier, for any card that has no design of its own. */
    private val byTier: Map<String, CardDesign> = mapOf(
        "Classic" to d(0xFFEDEEEF, 0xFFA7AAAD, BRUSHED, darkText = true),
        "Standard" to d(0xFFEDEEEF, 0xFFA7AAAD, BRUSHED, darkText = true),
        "Gold" to d(0xFFF2CB5E, 0xFFC08A2E, BRUSHED, darkText = true),
        "Platinum" to d(0xFF5A5C60, 0xFF1E1F22, SILK),
        "Titanium" to d(0xFF3A3B3E, 0xFF111214, BRUSHED),
        "Signature" to d(0xFF1E1E22, 0xFF050506, RIBBON, 0xFFC9A24B),
        "Infinite" to d(0xFF2B2B2E, 0xFF0A0A0B, RIBBON, 0xFF9A9A9A),
        "World" to d(0xFF1A1A1C, 0xFF050506, GLOBE, 0xFF9FB7C6),
        "World Elite" to d(0xFF2A2A2A, 0xFF101010, CARBON, 0xFF5A5A5A),
        "Blue" to d(0xFFC9DDF0, 0xFF8DB4D9, GUILLOCHE, darkText = true),
        "Ultramarine" to d(0xFF2A86C0, 0xFF0F5E93, GUILLOCHE),
        "Platinum Reserve" to d(0xFF9A9A9A, 0xFF4A4A4A, GUILLOCHE),
        "Emerald" to d(0xFF0B5A36, 0xFF062514, GLOBE, 0xFF9CE0B8),
        "Diamond" to d(0xFF714759, 0xFF252037, FACETS, 0xFFC697AA),
    )

    private val fallback = d(0xFF546E7A, 0xFF263238, SILK)

    private fun key(bankId: String, network: CardNetwork, tier: String, variant: String? = null) =
        listOfNotNull(bankId, network.name, tier, variant).joinToString("/")

    private val variants = mapOf(
        key("brac", VISA, "Platinum") to listOf("Tara", "Flexi"),
        key("brac", MASTERCARD, "World") to listOf("Standard", "Tara"),
        key("city", AMEX, "Gold") to listOf("Standard", "Islamic"),
        key("city", AMEX, "Blue") to listOf("Standard", "Islamic"),
        key("scb", VISA, "Platinum") to listOf("Smart", "Saadiq"),
        key("islami", VISA, "Platinum") to listOf("Standard", "Priority"),
        key("islami", MASTERCARD, "Platinum") to listOf("Standard", "Priority"),
        key("islami", UNIONPAY, "Platinum") to listOf("Standard", "Priority"),
    )

    fun variantsFor(bankId: String, network: CardNetwork, tier: String): List<String> = variants[key(bankId, network, tier)].orEmpty()

    fun hasOwnDesign(bankId: String, network: CardNetwork, tier: String) = key(bankId, network, tier) in designs

    fun forCard(bankId: String, network: CardNetwork, tier: String, variant: String? = null): CardDesign =
        designs[key(bankId, network, tier, variant)] ?: designs[key(bankId, network, tier)] ?: byTier[tier] ?: fallback
}
