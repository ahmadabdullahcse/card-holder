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
import com.ahmadabdullah.cardholder.ui.cardart.Finish.BRUSHED
import com.ahmadabdullah.cardholder.ui.cardart.Finish.CARBON
import com.ahmadabdullah.cardholder.ui.cardart.Finish.CHEVRON
import com.ahmadabdullah.cardholder.ui.cardart.Finish.DIAMONDS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.DOTS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.FACETS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.GLOBE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.GUILLOCHE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.MARBLE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.PANEL
import com.ahmadabdullah.cardholder.ui.cardart.Finish.PLAIN
import com.ahmadabdullah.cardholder.ui.cardart.Finish.RAYS
import com.ahmadabdullah.cardholder.ui.cardart.Finish.RIBBON
import com.ahmadabdullah.cardholder.ui.cardart.Finish.ROSETTE
import com.ahmadabdullah.cardholder.ui.cardart.Finish.SILK
import com.ahmadabdullah.cardholder.ui.cardart.Finish.STAR
import com.ahmadabdullah.cardholder.ui.cardart.Finish.STRIPE

/**
 * The look of each bank's cards, modelled on the bank's own designs (their colors, finish and
 * motifs as shown on its website in October 2026) but drawn from scratch: no bank's artwork or
 * logo is copied.
 */
object CardDesigns {
    private fun d(light: Long, dark: Long, finish: Finish = PLAIN, accent: Long = 0xFFFFFFFF, darkText: Boolean = false) =
        CardDesign(listOf(Color(light), Color(dark)), finish, Color(accent), darkText)

    private val designs: Map<String, CardDesign> = buildMap {
        fun put(bank: String, network: CardNetwork, tier: String, design: CardDesign) = put("$bank/$network/$tier", design)
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

        // BRAC Bank: rotated squares run through most of its cards.
        putAll("brac", both, "Classic", d(0xFFECEDEF, 0xFFA9ABAE, DIAMONDS, 0xFF7D7F82, darkText = true))
        putAll("brac", both, "Gold", d(0xFFF8D873, 0xFFD9A548, DIAMONDS, 0xFFB8862F, darkText = true))
        putAll("brac", both, "Platinum", d(0xFF2A2A2A, 0xFF000000, DIAMONDS, 0xFF7A7A7A))
        put("brac", VISA, "Signature", d(0xFF2A1035, 0xFF000000, DIAMONDS, 0xFFC9A24B))
        put("brac", VISA, "Infinite", d(0xFF273555, 0xFF0F1628, BEAM, 0xFFDCE6FF))
        put("brac", MASTERCARD, "Titanium", d(0xFF0B0B14, 0xFF000000, RIBBON, 0xFF3B7BD6))
        put("brac", MASTERCARD, "World", d(0xFF2BD0E6, 0xFF0FA3B5, SILK))
        put("brac", DINERS, "Emerald", d(0xFF0B5A36, 0xFF062514, GLOBE, 0xFF9CE0B8))

        // City Bank: the Amex cards carry fine engraved lines.
        put("city", AMEX, "Blue", d(0xFFC9DDF0, 0xFF8DB4D9, GUILLOCHE, darkText = true))
        put("city", AMEX, "Gold", d(0xFFC7A79E, 0xFF8A6A63, GUILLOCHE))
        put("city", AMEX, "Platinum", d(0xFFE6E4E5, 0xFFA9A6A8, GUILLOCHE, darkText = true))
        put("city", AMEX, "Platinum Reserve", d(0xFF9A9A9A, 0xFF4A4A4A, GUILLOCHE))
        put("city", AMEX, "Ultramarine", d(0xFF2A86C0, 0xFF0F5E93, GUILLOCHE))
        put("city", VISA, "Platinum", d(0xFF2A2A2A, 0xFF0E0E0E, MARBLE))
        put("city", VISA, "Infinite", d(0xFF2B2B2B, 0xFF0A0A0A, RIBBON, 0xFF9A9A9A))
        put("city", MASTERCARD, "World Elite", d(0xFF1A1A1A, 0xFF050505, SILK, 0xFF8A8D90))

        // Dhaka Bank: black silk for the metal tiers.
        put("dhaka", VISA, "Gold", d(0xFFD9A84E, 0xFF8E5A1E, SILK, 0xFFFFE2A8))
        put("dhaka", VISA, "Platinum", d(0xFF4A4A4A, 0xFF111111, SILK))
        put("dhaka", VISA, "Signature", d(0xFF1C1C1C, 0xFF000000, RAYS, 0xFFE3B85C))
        put("dhaka", MASTERCARD, "Titanium", d(0xFF444444, 0xFF111111, SILK))
        put("dhaka", MASTERCARD, "World", d(0xFF1C2A7A, 0xFF0A1036, GLOBE, 0xFF5FA8FF))

        // Dutch-Bangla Bank: a gold arabesque on its black cards.
        putAll("dbbl", both, "Gold", d(0xFFEDE2A0, 0xFFCDB45A, BRUSHED, darkText = true))
        put("dbbl", VISA, "Platinum", d(0xFFE4E4E6, 0xFFB7B8BB, ROSETTE, 0xFF8A8A8A, darkText = true))
        put("dbbl", VISA, "Signature", d(0xFF2E292A, 0xFF141112, ROSETTE, 0xFFD4A45A))
        put("dbbl", MASTERCARD, "Titanium", d(0xFF2A2526, 0xFF050404, SILK))
        put("dbbl", MASTERCARD, "World", d(0xFF2E292A, 0xFF141112, ROSETTE, 0xFFD4A45A))

        // Eastern Bank
        put("ebl", VISA, "Classic", d(0xFFE3E4E4, 0xFFA9ADAF, STRIPE, 0xFFE1B100, darkText = true))
        put("ebl", VISA, "Gold", d(0xFFC99A3E, 0xFF9E7130, BRUSHED))
        put("ebl", VISA, "Platinum", d(0xFF3F8AC9, 0xFF1C4F86, FACETS))
        put("ebl", VISA, "Signature", d(0xFF0F4A55, 0xFF020607, DOTS, 0xFF2BA3B8))
        put("ebl", VISA, "Infinite", d(0xFF46464A, 0xFF2E2E30))
        put("ebl", MASTERCARD, "Titanium", d(0xFF2B2B2B, 0xFF050505, CHEVRON, 0xFF9C9C9C))
        put("ebl", MASTERCARD, "World", d(0xFF2A2416, 0xFF050403, GLOBE, 0xFFE3B85C))
        put("ebl", MASTERCARD, "World Elite", d(0xFF333231, 0xFF1A1919, CARBON, 0xFF5A5A5A))
        put("ebl", DINERS, "Standard", d(0xFF2E2A2B, 0xFF121011, BLOBS, 0xFFF7A81B))
        put("ebl", UNIONPAY, "Platinum", d(0xFFF0F0F0, 0xFFB8BABD, RIBBON, 0xFF1AAA61, darkText = true))

        // IFIC Bank
        put("ific", VISA, "Gold", d(0xFFF0C34A, 0xFFD19A22, CHEVRON, darkText = true))
        put("ific", VISA, "Platinum", d(0xFF4A4C50, 0xFF1F2023, RIBBON, 0xFFD0D0D0))

        // Islami Bank: the Khidmah cards carry an eight-pointed star.
        listOf(VISA, MASTERCARD, UNIONPAY).forEach {
            put("islami", it, "Gold", d(0xFFF1C21B, 0xFFC8890E, STAR, 0xFF1F5E2C, darkText = true))
            put("islami", it, "Platinum", d(0xFF4A4B4D, 0xFF1E1F21, STAR, 0xFFB8963E))
        }
        put("islami", VISA, "Signature", d(0xFF2B3466, 0xFF141A3B, GLOBE, 0xFFD5A762))
        put("islami", MASTERCARD, "World", d(0xFF1F8A5E, 0xFF0E4A3A, GLOBE, 0xFF9AD1E6))

        // Jamuna Bank
        put("jamuna", VISA, "Classic", d(0xFFF2F2F2, 0xFFBDBDBD, BRUSHED, darkText = true))
        put("jamuna", VISA, "Gold", d(0xFFF2C14E, 0xFFC98A22, BRUSHED, darkText = true))
        put("jamuna", VISA, "Platinum", d(0xFF2A2A2A, 0xFF080808, ROSETTE, 0xFFA0A0A0))
        put("jamuna", VISA, "Signature", d(0xFF2A3FA0, 0xFF142060, ROSETTE))

        // LankaBangla Finance: bright flat colors with soft rounded shapes.
        put("lankabangla", VISA, "Classic", d(0xFFF67F65, 0xFFE8202F, BLOBS))
        put("lankabangla", VISA, "Gold", d(0xFFFEEAEA, 0xFFF8C9BE, BLOBS, darkText = true))
        put("lankabangla", VISA, "Platinum", d(0xFF3A98A6, 0xFF0E6E78, BLOBS))
        put("lankabangla", MASTERCARD, "Classic", d(0xFFF89A88, 0xFFF0303A, BLOBS))
        put("lankabangla", MASTERCARD, "Gold", d(0xFFFBB782, 0xFFF15A22, BLOBS))
        put("lankabangla", MASTERCARD, "Titanium", d(0xFF6FD3DD, 0xFF00A79D, BLOBS))

        // Mercantile Bank
        put("mercantile", VISA, "Classic", d(0xFF1F9AA8, 0xFF0D5E6A, FACETS))
        put("mercantile", VISA, "Gold", d(0xFFE8B84A, 0xFFB07D22, BRUSHED, darkText = true))
        put("mercantile", VISA, "Platinum", d(0xFF24346E, 0xFF0E173D, FACETS))
        put("mercantile", VISA, "Signature", d(0xFFC2307A, 0xFF6E1446, FACETS))
        put("mercantile", MASTERCARD, "Titanium", d(0xFF2E2F33, 0xFF0C0C0E, FACETS, 0xFF9A9A9A))
        put("mercantile", MASTERCARD, "World", d(0xFF121A33, 0xFF03060E, GLOBE, 0xFF4F9BFF))

        // Mutual Trust Bank
        putAll("mtb", both, "Classic", d(0xFF2E6452, 0xFF0E3B2C, GUILLOCHE))
        putAll("mtb", both, "Gold", d(0xFF2A2724, 0xFF0E0D0B, CHEVRON, 0xFFC9A24B))
        put("mtb", VISA, "Platinum", d(0xFFF4F5F9, 0xFFC9C9D3, CHEVRON, 0xFF9A9AA5, darkText = true))
        put("mtb", VISA, "Signature", d(0xFF1E1E1E, 0xFF000000, RIBBON, 0xFF6E6E6E))
        put("mtb", MASTERCARD, "Titanium", d(0xFFECEEEE, 0xFFC9CDCD, GUILLOCHE, 0xFF9AA0A0, darkText = true))
        put("mtb", MASTERCARD, "World", d(0xFF2B2B2B, 0xFF000000, GLOBE, 0xFF9FB7C6))
        put("mtb", UNIONPAY, "Platinum", d(0xFF0D3A5E, 0xFF001730, FACETS, 0xFF8FB3D9))
        put("mtb", UNIONPAY, "Diamond", d(0xFF714759, 0xFF252037, FACETS, 0xFFC697AA))

        // NCC Bank
        put("ncc", VISA, "Classic", d(0xFFFAFAFA, 0xFFC6C6C6, BRUSHED, darkText = true))
        put("ncc", VISA, "Gold", d(0xFFEDD261, 0xFF9A7C42, BRUSHED, darkText = true))
        put("ncc", VISA, "Platinum", d(0xFF9C9DA0, 0xFF6E7073, BRUSHED))
        put("ncc", VISA, "Signature", d(0xFF2E2F33, 0xFF111113, ROSETTE, 0xFFB89A70))
        put("ncc", MASTERCARD, "Titanium", d(0xFF3A3B3E, 0xFF141416, BRUSHED))

        // ONE Bank: streaks of golden light on black.
        put("one", VISA, "Classic", d(0xFFF2F2F1, 0xFFCFCFCE, ROSETTE, 0xFFFFFFFF, darkText = true))
        put("one", VISA, "Gold", d(0xFF2A2410, 0xFF050505, RIBBON, 0xFFE6B83A))
        put("one", VISA, "Platinum", d(0xFF8E8E8E, 0xFF2E2E2E, SILK))
        put("one", VISA, "Signature", d(0xFF2A2826, 0xFF0A0A0A, RIBBON, 0xFFE0A74E))

        // Prime Bank
        put("prime", VISA, "Gold", d(0xFFFBF4E4, 0xFFE7CF95, SILK, 0xFFD9B25A, darkText = true))
        put("prime", VISA, "Platinum", d(0xFF1C1B1A, 0xFF050505, RIBBON, 0xFFC9A24B))
        put("prime", VISA, "Signature", d(0xFF141414, 0xFF000000, RIBBON, 0xFF8F8F8F))
        put("prime", MASTERCARD, "Platinum", d(0xFF141414, 0xFF000000, CHEVRON, 0xFFC9A24B))
        put("prime", MASTERCARD, "World", d(0xFF0A0A0A, 0xFF000000, GLOBE, 0xFF2EA7E0))
        put("prime", JCB, "Gold", d(0xFFF4CB48, 0xFFD99A1E, BLOBS, darkText = true))
        put("prime", JCB, "Platinum", d(0xFF0B1A2E, 0xFF02060E, SILK, 0xFF6FA0D0))

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
        put("scb", VISA, "Classic", d(0xFFE9ECEF, 0xFFB9C0C7, BRUSHED, darkText = true))
        put("scb", VISA, "Gold", d(0xFF0A5C93, 0xFF003866, PANEL, 0xFF4C91B7))
        put("scb", VISA, "Platinum", d(0xFFF2F5F8, 0xFFDCE6EE, RIBBON, 0xFF9BC7E8, darkText = true))
        put("scb", VISA, "Signature", d(0xFF0A5C93, 0xFF00396B, PANEL, 0xFF5793B8))
        put("scb", MASTERCARD, "Titanium", d(0xFF0E6A7E, 0xFF003F4D, PANEL, 0xFF509AAC))

        // Trust Bank
        put("trust", VISA, "Classic", d(0xFF0E2A1E, 0xFF020805, GUILLOCHE, 0xFF3EAE6E))
        put("trust", VISA, "Gold", d(0xFF3A1510, 0xFF0B0606, BLOBS, 0xFFF0A030))
        put("trust", VISA, "Platinum", d(0xFF2E292A, 0xFF121011, RIBBON, 0xFF2E9BD6))
        put("trust", VISA, "Signature", d(0xFF8E1A10, 0xFF5A0802, FACETS, 0xFFB73A2E))
        put("trust", UNIONPAY, "Platinum", d(0xFF0A0A0A, 0xFF000000, DOTS, 0xFF3F78C9))

        // United Commercial Bank
        putAll("ucb", both, "Classic", d(0xFFF0F0EE, 0xFF9C9C9C, BRUSHED, darkText = true))
        putAll("ucb", both, "Gold", d(0xFFF0C65E, 0xFFB9852F, BRUSHED, darkText = true))
        put("ucb", VISA, "Platinum", d(0xFF2D2829, 0xFF120D0F, ROSETTE, 0xFF6B6B6B))
        put("ucb", MASTERCARD, "Platinum", d(0xFF5E6062, 0xFF3A3B3C, ROSETTE, 0xFF8A8A8A))
        put("ucb", VISA, "Signature", d(0xFF1C5A93, 0xFF0D3D6E, DOTS, 0xFF9CC4E8))
        put("ucb", MASTERCARD, "World", d(0xFF2A2A2A, 0xFF050505, RIBBON, 0xFFC9A24B))
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

    fun hasOwnDesign(bankId: String, network: CardNetwork, tier: String) = "$bankId/$network/$tier" in designs

    fun forCard(bankId: String, network: CardNetwork, tier: String): CardDesign =
        designs["$bankId/$network/$tier"] ?: byTier[tier] ?: fallback
}
