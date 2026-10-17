package com.ahmadabdullah.cardholder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.ahmadabdullah.cardholder.data.BankCatalog
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.CardNetwork
import com.ahmadabdullah.cardholder.ui.CARD_ASPECT_RATIO
import com.ahmadabdullah.cardholder.ui.CreditCardView
import com.ahmadabdullah.cardholder.ui.theme.CardHolderTheme

/**
 * Every card in the catalog, drawn with its design, for checking them side by side. Only in
 * debug builds: adb shell am start -n com.ahmadabdullah.cardholder/.DesignGalleryActivity
 * Pass --es bank <id> to show one bank.
 */
private val FULL_WIDTH = 360.dp

/** First digits the network's real cards start with. */
private fun sampleStart(network: CardNetwork) = when (network) {
    CardNetwork.VISA -> "4123"
    CardNetwork.MASTERCARD -> "5412"
    CardNetwork.AMEX -> "3712"
    CardNetwork.DINERS -> "3612"
    CardNetwork.UNIONPAY -> "6212"
    CardNetwork.JCB -> "3530"
}

class DesignGalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val only = intent.getStringExtra("bank")
        val banks = BankCatalog.banks.filter { only == null || it.id == only }
        setContent {
            CardHolderTheme {
                Surface(Modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        GridCells.Fixed(2),
                        Modifier.safeDrawingPadding(),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        banks.forEach { bank ->
                            item(span = { GridItemSpan(2) }) { Text(bank.name, style = MaterialTheme.typography.titleMedium) }
                            val cards = bank.networks.flatMap { network -> bank.tiersFor(network).map { network to it } }
                            items(cards) { (network, tier) ->
                                // Drawn at a phone's full card width, then scaled down, so it looks as it does in the app.
                                BoxWithConstraints(Modifier.fillMaxWidth().aspectRatio(CARD_ASPECT_RATIO)) {
                                    val scale = maxWidth / FULL_WIDTH
                                    CreditCardView(
                                        Card(
                                            id = "${bank.id}/$network/$tier", bankId = bank.id, network = network, tier = tier,
                                            firstName = "Rahim", lastName = "Uddin", firstFour = sampleStart(network), lastFour = "8673",
                                            bdtLimit = 0, usdLimit = 0, billingDay = 1, expiryMonth = 8, expiryYear = 2029,
                                        ),
                                        Modifier
                                            .wrapContentSize(Alignment.TopStart, unbounded = true)
                                            .requiredWidth(FULL_WIDTH)
                                            .graphicsLayer {
                                                scaleX = scale
                                                scaleY = scale
                                                transformOrigin = TransformOrigin(0f, 0f)
                                            },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
