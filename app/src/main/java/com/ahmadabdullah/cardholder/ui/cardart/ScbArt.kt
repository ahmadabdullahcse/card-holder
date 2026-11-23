package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import kotlin.random.Random

/*
 * Standard Chartered's cards: a body covered in fine geometric linework, a thin silver divider,
 * and a panel on the right with the same shapes drawn stronger. The bank's own mark, which some
 * cards show large behind the pattern, is left out.
 */
internal fun DrawScope.drawScb(finish: Finish, accent: Color, random: Random) {
    val w = size.width
    val h = size.height
    val line = density
    // The panel starts just right of the card number.
    val divider = w * 0.72f

    when (finish) {
        Finish.SCB_PANEL -> {
            clipRect(right = divider) { geometry(accent.copy(alpha = 0.16f), h * 0.085f, random) }
            clipRect(left = divider) {
                drawRect(Color.Black.copy(alpha = 0.28f))
                geometry(accent.copy(alpha = 0.55f), h * 0.07f, random)
            }
            drawLine(Brush.verticalGradient(listOf(Color(0xFFDDE6F0), Color(0xFF8FA0B4), Color(0xFFDDE6F0))), Offset(divider, 0f), Offset(divider, h), line * 1.5f)
        }

        // White, with a soft rainbow sweeping up from the lower left across the body, and a white
        // panel of large pale circles.
        Finish.SCB_SMART -> {
            clipRect(right = divider) {
                val band = Path().apply {
                    moveTo(w * 0.05f, h * 1.05f); lineTo(w * 0.30f, h * 1.05f); lineTo(w * 0.62f, -h * 0.05f); lineTo(w * 0.37f, -h * 0.05f); close()
                }
                drawPath(
                    band,
                    Brush.linearGradient(
                        listOf(Color(0xFFF6E79A), Color(0xFFBFE3B0), Color(0xFF9CC8F0), Color(0xFFC9B6F0), Color(0xFFF2C1D8)),
                        Offset(w * 0.12f, h * 0.9f), Offset(w * 0.45f, h * 0.1f),
                    ),
                    alpha = 0.75f,
                )
                geometry(accent.copy(alpha = 0.22f), h * 0.085f, random)
            }
            clipRect(left = divider) {
                drawRect(Color.White)
                val circles = Path()
                var y = h * 0.1f
                while (y < h * 1.1f) {
                    var x = divider + h * 0.08f
                    while (x < w + h * 0.2f) {
                        circles.addOval(Rect(center = Offset(x, y), radius = h * 0.11f))
                        x += h * 0.24f
                    }
                    y += h * 0.24f
                }
                drawPath(circles, accent.copy(alpha = 0.35f), style = Stroke(line))
            }
            drawLine(Color(0xFFC9CDD4), Offset(divider, 0f), Offset(divider, h), line)
        }

        else -> Unit
    }
}

/**
 * The bank's geometric linework: a grid of small cells, each holding one simple outline, a
 * circle, half circle, triangle, square, quarter arc or a pair of lines, chosen at random.
 */
private fun DrawScope.geometry(color: Color, cell: Float, random: Random) {
    val path = Path()
    var y = 0f
    while (y < size.height) {
        var x = 0f
        while (x < size.width) {
            val c = Offset(x + cell / 2, y + cell / 2)
            val r = cell * 0.38f
            when (random.nextInt(7)) {
                0 -> path.addOval(Rect(center = c, radius = r))
                1 -> path.addArc(Rect(center = Offset(c.x, c.y + r * 0.5f), radius = r), 180f, 180f)
                2 -> { path.moveTo(c.x - r, c.y + r); path.lineTo(c.x, c.y - r); path.lineTo(c.x + r, c.y + r); path.close() }
                3 -> path.addRect(Rect(center = c, radius = r * 0.85f))
                4 -> path.addArc(Rect(center = Offset(x, y + cell), radius = cell * 0.8f), 270f, 90f)
                5 -> { path.moveTo(x + cell * 0.2f, y + cell * 0.2f); path.lineTo(x + cell * 0.2f, y + cell * 0.8f); path.moveTo(x + cell * 0.6f, y + cell * 0.2f); path.lineTo(x + cell * 0.6f, y + cell * 0.8f) }
                else -> Unit
            }
            x += cell
        }
        y += cell
    }
    drawPath(path, color, style = Stroke(density * 0.8f))
}
