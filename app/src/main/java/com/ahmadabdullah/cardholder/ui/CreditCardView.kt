package com.ahmadabdullah.cardholder.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.data.CardNetwork
import com.ahmadabdullah.cardholder.ui.cardart.CardDesign
import com.ahmadabdullah.cardholder.ui.cardart.CardDesigns
import com.ahmadabdullah.cardholder.ui.cardart.drawFinish

/** The ratio of a real bank card's width to its height. */
const val CARD_ASPECT_RATIO = 1.586f

private val CardShape = RoundedCornerShape(18.dp)

/**
 * A card as it looks in the hand. Fills the width it is given. A vertical card stands upright in
 * the middle of that width, at the size of a landscape card turned on its end; with [sideways]
 * it is laid on its side instead, as it would sit in a wallet, so it takes a landscape card's
 * place and its text runs from top to bottom.
 */
@Composable
fun CreditCardView(card: Card, modifier: Modifier = Modifier, sideways: Boolean = false, onClick: (() -> Unit)? = null) {
    val design = CardDesigns.forCard(card.bankId, card.network, card.tier, card.variant)
    when {
        !design.vertical -> CardFace(card, design, modifier.fillMaxWidth().aspectRatio(CARD_ASPECT_RATIO), onClick) {
            LandscapeLayout(card, design)
        }
        sideways -> BoxWithConstraints(modifier.fillMaxWidth().aspectRatio(CARD_ASPECT_RATIO), contentAlignment = Alignment.Center) {
            // Laid out upright at the box's size turned on its end, then turned a quarter clockwise.
            CardFace(card, design, Modifier.requiredSize(width = maxHeight, height = maxWidth).graphicsLayer { rotationZ = 90f }, onClick) {
                UprightLayout(card, design)
            }
        }
        else -> Box(modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            CardFace(card, design, Modifier.fillMaxWidth(1 / CARD_ASPECT_RATIO).aspectRatio(1 / CARD_ASPECT_RATIO), onClick) {
                UprightLayout(card, design)
            }
        }
    }
}

@Composable
private fun CardFace(card: Card, design: CardDesign, modifier: Modifier, onClick: (() -> Unit)?, content: @Composable () -> Unit) {
    val seed = "${card.bankId}/${card.network}/${card.tier}".hashCode()
    Box(
        modifier
            .shadow(10.dp, CardShape)
            .clip(CardShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .background(Brush.linearGradient(design.colors, start = Offset.Zero, end = Offset.Infinite))
    ) {
        Canvas(Modifier.fillMaxSize()) { drawFinish(design.finish, design.accent, seed) }
        content()
    }
}

@Composable
private fun LandscapeLayout(card: Card, design: CardDesign) {
    val ink = design.textColor
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                card.bankName,
                color = ink,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
            Text(
                card.tier.uppercase(),
                color = ink.copy(alpha = 0.8f),
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
            color = ink,
            fontFamily = FontFamily.Monospace,
            fontSize = 19.sp,
            letterSpacing = 1.sp,
            maxLines = 1,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "VALID THRU  ${formatExpiry(card.expiryMonth, card.expiryYear)}",
            color = ink.copy(alpha = 0.8f),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
        )
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Text(
                card.holderName.uppercase(),
                color = ink.copy(alpha = 0.9f),
                fontSize = 13.sp,
                letterSpacing = 1.sp,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
            NetworkMark(card.network, onLight = design.darkText)
        }
    }
}

/**
 * The upright layout. The bank and tier sit at the top, the chip a little below the middle, the
 * number in two lines under it, and the network mark in the bottom right corner.
 */
@Composable
private fun UprightLayout(card: Card, design: CardDesign) {
    val ink = design.textColor
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 18.dp)) {
        Text(
            card.bankName,
            color = ink,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            card.tier.uppercase(),
            color = ink.copy(alpha = 0.8f),
            fontSize = 11.sp,
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
        )
        Spacer(Modifier.weight(1f))
        Chip()
        Spacer(Modifier.height(14.dp))
        maskedNumber(card).split(" ").chunked(2).forEach { groups ->
            Text(
                groups.joinToString(" "),
                color = ink,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                letterSpacing = 1.sp,
                maxLines = 1,
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "VALID THRU  ${formatExpiry(card.expiryMonth, card.expiryYear)}",
            color = ink.copy(alpha = 0.8f),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            card.holderName.uppercase(),
            color = ink.copy(alpha = 0.9f),
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            NetworkMark(card.network, onLight = design.darkText)
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
fun NetworkMark(network: CardNetwork, modifier: Modifier = Modifier, onLight: Boolean = false) {
    // On a light card the marks are drawn dark, as the networks do on their own light cards.
    val ink = if (onLight) Color(0xFF1A1F71) else Color.White
    when (network) {
        CardNetwork.VISA -> Text(
            "VISA",
            color = ink,
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
                drawCircle(ink, style = Stroke(width = 2.dp.toPx()))
                drawCircle(ink, radius = size.minDimension / 5)
            }
            Spacer(Modifier.size(6.dp))
            Text("Diners Club", color = ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
        CardNetwork.UNIONPAY -> UnionPayMark(modifier)
        CardNetwork.JCB -> ColorBlocks(
            listOf(Color(0xFF0E4C96), Color(0xFFE21836), Color(0xFF007940)),
            listOf("J", "C", "B"),
            modifier,
        )
    }
}

/** Three overlapping slanted tiles, red, blue and teal, with the name written across them. */
@Composable
private fun UnionPayMark(modifier: Modifier = Modifier) {
    Box(modifier.size(width = 64.dp, height = 34.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val slant = w * 0.12f
            val tile = (w - slant) / 2.6f
            listOf(Color(0xFFE21836), Color(0xFF00447C), Color(0xFF007B84)).forEachIndexed { i, color ->
                val left = i * tile * 0.8f
                val path = Path().apply {
                    moveTo(left + slant, 0f)
                    lineTo(left + slant + tile, 0f)
                    lineTo(left + tile, h)
                    lineTo(left, h)
                    close()
                }
                drawPath(path, color)
            }
        }
        Text(
            "UnionPay",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            fontSize = 11.sp,
            letterSpacing = 0.sp,
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
