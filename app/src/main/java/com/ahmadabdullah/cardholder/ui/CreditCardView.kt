package com.ahmadabdullah.cardholder.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.CardNetwork

/** The ratio of a real bank card's width to its height. */
const val CARD_ASPECT_RATIO = 1.586f

private val CardShape = RoundedCornerShape(18.dp)

/** Card colors by tier, darkest edge first. */
private fun tierColors(tier: String): Pair<Color, Color> = when (tier) {
    "Classic" -> Color(0xFF1D3A8A) to Color(0xFF3B6FD8)
    "Gold" -> Color(0xFF7A5A12) to Color(0xFFC9A13B)
    "Platinum" -> Color(0xFF4A5260) to Color(0xFF98A2B3)
    "Titanium" -> Color(0xFF2E3440) to Color(0xFF636B78)
    "World" -> Color(0xFF0E3B43) to Color(0xFF1F7A7A)
    "Signature" -> Color(0xFF2B1747) to Color(0xFF5B3A8E)
    "Infinite", "World Elite", "Platinum Reserve" -> Color(0xFF0A0A0C) to Color(0xFF34343A)
    "Blue" -> Color(0xFF0B3C7A) to Color(0xFF3A86D1)
    "Ultramarine" -> Color(0xFF14146B) to Color(0xFF3B3BC4)
    "Emerald" -> Color(0xFF0F4B33) to Color(0xFF2F8A5F)
    "Diamond" -> Color(0xFF3C5A6E) to Color(0xFFA9C4D4)
    else -> Color(0xFF263238) to Color(0xFF546E7A)
}

@Composable
fun CreditCardView(card: Card, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val (dark, light) = tierColors(card.tier)
    Box(
        modifier
            .fillMaxWidth()
            .aspectRatio(CARD_ASPECT_RATIO)
            .shadow(10.dp, CardShape)
            .clip(CardShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .background(Brush.linearGradient(listOf(light, dark), start = Offset.Zero, end = Offset.Infinite))
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    card.bankName,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    card.tier.uppercase(),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(Modifier.weight(1f))
            Chip()
            Spacer(Modifier.height(14.dp))
            Text(
                maskedNumber(card),
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 19.sp,
                letterSpacing = 1.sp,
                maxLines = 1,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "VALID THRU  ${formatExpiry(card.expiryMonth, card.expiryYear)}",
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Text(
                    card.holderName.uppercase(),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
                NetworkMark(card.network)
            }
        }
    }
}

@Composable
private fun Chip() {
    Box(
        Modifier
            .size(width = 40.dp, height = 30.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFE9D38C), Color(0xFFB8963E))))
    )
}

@Composable
fun NetworkMark(network: CardNetwork, modifier: Modifier = Modifier) {
    when (network) {
        CardNetwork.VISA -> Text(
            "VISA",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            fontStyle = FontStyle.Italic,
            letterSpacing = 1.sp,
            modifier = modifier,
        )
        CardNetwork.MASTERCARD -> Row(modifier, horizontalArrangement = Arrangement.spacedBy((-12).dp)) {
            Canvas(Modifier.size(30.dp)) { drawCircle(Color(0xFFEB001B)) }
            Canvas(Modifier.size(30.dp)) { drawCircle(Color(0xFFF79E1B).copy(alpha = 0.9f)) }
        }
        CardNetwork.AMEX -> Box(
            modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF2E77BC))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Text("AMEX", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 1.sp)
        }
        CardNetwork.DINERS -> Row(modifier, verticalAlignment = Alignment.CenterVertically) {
            Canvas(Modifier.size(26.dp)) {
                drawCircle(Color.White, style = Stroke(width = 2.dp.toPx()))
                drawCircle(Color.White, radius = size.minDimension / 5)
            }
            Spacer(Modifier.size(6.dp))
            Text("Diners Club", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
        CardNetwork.UNIONPAY -> ColorBlocks(
            listOf(Color(0xFFE21836), Color(0xFF00447C), Color(0xFF007B84)),
            listOf("Union", "Pay", ""),
            modifier,
        )
        CardNetwork.JCB -> ColorBlocks(
            listOf(Color(0xFF0E4C96), Color(0xFFE21836), Color(0xFF007940)),
            listOf("J", "C", "B"),
            modifier,
        )
    }
}

/** A row of colored tiles, each carrying a few letters, as the UnionPay and JCB marks are drawn. */
@Composable
private fun ColorBlocks(colors: List<Color>, labels: List<String>, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        colors.zip(labels).forEach { (color, label) ->
            Box(
                Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
                    .padding(horizontal = 5.dp, vertical = 3.dp)
            ) {
                Text(label.ifEmpty { " " }, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
        }
    }
}
