package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/*
 * IFIC Bank's cards, following the real ones: broad rounded chevrons pointing right, on gold for
 * Gold and over charcoal satin for Platinum.
 */
internal fun DrawScope.drawIfic(finish: Finish, accent: Color) {
    val w = size.width
    val h = size.height
    val line = density
    // A broad chevron with rounded corners: from above the top edge down to its point, then back
    // out below the bottom edge.
    fun chevron(top: Float, tip: Float, tipY: Float, bottom: Float, width: Float, color: Color) {
        val path = Path().apply { moveTo(w * top, -h * 0.15f); lineTo(w * tip, h * tipY); lineTo(w * bottom, h * 1.15f) }
        drawPath(path, color, style = Stroke(h * width, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }

    when (finish) {
        Finish.IFIC_GOLD_CHEVRONS -> {
            chevron(0.02f, 0.30f, 0.40f, 0.00f, 0.22f, Color(0xFFFFE08A).copy(alpha = 0.30f))
            chevron(0.36f, 0.80f, 0.42f, 0.30f, 0.30f, accent.copy(alpha = 0.35f))
            chevron(0.20f, 0.50f, 0.72f, 0.24f, 0.16f, accent.copy(alpha = 0.22f))
        }

        Finish.IFIC_SATIN_CHEVRON -> {
            // Satin folds catching the light across the right.
            listOf(Triple(0.62f, 0.14f, 0.35f), Triple(0.85f, -0.08f, 0.28f), Triple(0.45f, 0.20f, 0.18f)).forEach { (x, bend, strength) ->
                val fold = Path().apply {
                    moveTo(w * (x - 0.15f), -h * 0.05f)
                    cubicTo(w * (x + bend), h * 0.30f, w * (x - bend), h * 0.70f, w * (x + 0.15f), h * 1.05f)
                }
                for (k in 6 downTo 1) drawPath(fold, Color(0xFFB8B8BC).copy(alpha = strength / 6f), style = Stroke(line * k * 9f))
                drawPath(fold, Color.Black.copy(alpha = 0.30f), style = Stroke(line * 4f))
            }
            chevron(0.05f, 0.58f, 0.40f, 0.00f, 0.30f, accent.copy(alpha = 0.22f))
            drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent, Color.Black.copy(alpha = 0.25f))))
        }

        else -> Unit
    }
}
