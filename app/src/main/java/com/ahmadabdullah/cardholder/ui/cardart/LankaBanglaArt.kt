package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp

/*
 * LankaBangla's cards, following the real ones closely. The Mastercard cards are soft wavy layers
 * of one colour, lightest at the top right; the Visa cards are wide curved bands spreading from
 * the top left. [accent] is the card's deepest colour; the lighter layers are tints of it.
 */
internal fun DrawScope.drawLankaBangla(finish: Finish, accent: Color) {
    val w = size.width
    val h = size.height
    fun tint(k: Float) = lerp(accent, Color.White, k)
    fun shape(vararg p: Float) = Path().apply {
        moveTo(w * p[0], h * p[1])
        var i = 2
        while (i < p.size) { cubicTo(w * p[i], h * p[i + 1], w * p[i + 2], h * p[i + 3], w * p[i + 4], h * p[i + 5]); i += 6 }
        close()
    }

    when (finish) {
        Finish.LB_WAVES -> {
            // A broad middle layer from the top, its lower edge waving down to the right.
            drawPath(
                shape(
                    0.30f, -0.02f,
                    0.22f, 0.20f, 0.28f, 0.40f, 0.42f, 0.52f,
                    0.56f, 0.64f, 0.70f, 0.58f, 0.82f, 0.70f,
                    0.90f, 0.78f, 0.96f, 0.80f, 1.02f, 0.74f,
                    1.02f, 0.40f, 1.02f, 0.10f, 1.02f, -0.02f,
                ),
                tint(0.28f),
            )
            // A lighter layer from the top right, with a wavy left edge.
            drawPath(
                shape(
                    0.52f, -0.02f,
                    0.50f, 0.12f, 0.60f, 0.20f, 0.66f, 0.30f,
                    0.72f, 0.42f, 0.86f, 0.44f, 1.02f, 0.50f,
                    1.02f, 0.30f, 1.02f, 0.10f, 1.02f, -0.02f,
                ),
                tint(0.45f),
            )
            // The palest blob, high in the middle.
            drawPath(
                shape(
                    0.42f, -0.02f,
                    0.42f, 0.08f, 0.50f, 0.16f, 0.58f, 0.14f,
                    0.66f, 0.12f, 0.64f, 0.04f, 0.66f, -0.02f,
                ),
                tint(0.62f),
            )
        }

        Finish.LB_ARCS -> {
            // Bands of great circles centred above and left of the card, each a step lighter
            // toward the top left corner.
            val c = Offset(w * 0.05f, -h * 0.55f)
            listOf(2.30f to 0.00f, 1.85f to 0.07f, 1.45f to 0.14f, 1.10f to 0.21f).forEach { (r, k) ->
                drawOval(tint(k), Rect(center = c, radius = h * r).topLeft, Rect(center = c, radius = h * r).size)
            }
        }

        else -> Unit
    }
}
