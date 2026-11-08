package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/*
 * Prime Bank's cards, each drawn after the look of the real card in new geometry, without the
 * bank's mark. Text sits in fixed places (see BracArt.kt); large shapes keep clear of it where
 * they can. Dots and fine lines are gathered into a few paths.
 */
internal fun DrawScope.drawPrime(finish: Finish, accent: Color, random: Random) {
    val w = size.width
    val h = size.height
    val line = density
    fun at(x: Float, y: Float) = Offset(w * x, h * y)

    when (finish) {
        // Black, with a great blue planet rising from the left: a bright cyan rim and paler
        // blue land on a deep blue sea.
        Finish.PRIME_WORLD -> {
            val oval = Rect(at(-0.46f, 0.18f), at(0.63f, 1.42f))
            val planet = Path().apply { addOval(oval) }
            drawPath(planet, Brush.radialGradient(0f to Color(0xFF061A3A), 0.7f to Color(0xFF0B3466), 0.93f to Color(0xFF1667A8), 1f to accent, center = oval.center, radius = oval.width / 2))
            clipPath(planet) {
                val land = Path()
                val step = line * 3f
                var y = oval.top
                while (y < h) {
                    var x = 0f
                    while (x < oval.right) {
                        val u = x / w
                        val v = y / h
                        if (sin(u * 11f + 1f) * cos(v * 8f - 0.5f) + sin(u * 5f - v * 6f) > 0.55f) land.addRect(Rect(x, y, x + step, y + step))
                        x += step
                    }
                    y += step
                }
                drawPath(land, Color(0xFF8FCBF0).copy(alpha = 0.40f))
            }
            drawOval(accent.copy(alpha = 0.25f), oval.topLeft, oval.size, style = Stroke(line * 6f))
            drawOval(accent.copy(alpha = 0.9f), oval.topLeft, oval.size, style = Stroke(line * 1.5f))
        }

        // Black, folded into dark facets meeting at one point, with a thin gold sliver of light
        // running down to it from the top.
        Finish.PRIME_FACETS -> {
            val c = at(0.68f, 0.58f)
            fun facet(a: Offset, b: Offset, color: Color) = drawPath(Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c.x, c.y); close() }, color)
            facet(at(0f, 0f), at(1f, 0f), Color.White.copy(alpha = 0.05f))
            facet(at(1f, 0f), at(1f, 1f), Color.Black.copy(alpha = 0.35f))
            facet(at(1f, 1f), at(0f, 1f), Color.White.copy(alpha = 0.07f))
            facet(at(0f, 1f), at(0f, 0f), Color.Black.copy(alpha = 0.15f))
            val sliver = Path().apply { moveTo(w * 0.34f, 0f); lineTo(w * 0.40f, 0f); lineTo(c.x, c.y); close() }
            drawPath(sliver, Brush.linearGradient(listOf(Color(0xFFF6E2B0), accent, Color.Transparent), at(0.37f, 0f), c))
        }

        // Black above, black leather below, divided by a wave of metal with stitching under it.
        Finish.PRIME_LEATHER -> {
            fun waveY(u: Float) = h * (0.42f - 0.10f * u + 0.035f * sin(u * 6.5f))
            val below = Path().apply {
                moveTo(0f, waveY(0f))
                for (i in 1..60) { val u = i / 60f; lineTo(w * u, waveY(u)) }
                lineTo(w, h); lineTo(0f, h); close()
            }
            clipPath(below) {
                drawRect(Color(0xFF151515))
                val grain = Path()
                repeat(1600) {
                    val x = w * random.nextFloat()
                    val y = h * random.nextFloat()
                    val r = line * (0.6f + random.nextFloat() * 1.4f)
                    grain.addOval(Rect(x - r, y - r * 0.7f, x + r, y + r * 0.7f))
                }
                drawPath(grain, Color.White.copy(alpha = 0.05f))
            }
            val ribbon = Path().apply {
                moveTo(0f, waveY(0f))
                for (i in 1..60) { val u = i / 60f; lineTo(w * u, waveY(u)) }
            }
            drawPath(ribbon, Brush.horizontalGradient(listOf(Color(0xFF8A8A8A), accent, Color(0xFFF2E3B5), accent)), style = Stroke(h * 0.045f))
            val stitch = Path().apply {
                moveTo(0f, waveY(0f) + h * 0.06f)
                for (i in 1..60) { val u = i / 60f; lineTo(w * u, waveY(u) + h * 0.06f) }
            }
            drawPath(stitch, Color(0xFFBDBDBD).copy(alpha = 0.6f), style = Stroke(line, pathEffect = PathEffect.dashPathEffect(floatArrayOf(line * 4f, line * 3f))))
        }

        // Champagne white, with a mottled gold field filling the left behind a great arc, and a
        // faint pale circle above.
        Finish.PRIME_GOLD_ARC -> {
            drawCircle(Color.White.copy(alpha = 0.5f), h * 0.55f, at(0.55f, 0.10f), style = Stroke(line * 1.2f))
            val field = Path().apply { addOval(Rect(center = at(-0.12f, 0.55f), radius = h * 0.95f)) }
            clipPath(field) {
                drawRect(Brush.linearGradient(listOf(Color(0xFFF2D68A), accent, Color(0xFFF7E8BE)), at(0f, 0f), at(0.5f, 1f)))
                val light = Path()
                val deep = Path()
                repeat(500) {
                    val x = w * random.nextFloat() * 0.6f
                    val y = h * random.nextFloat()
                    val r = line * (0.8f + random.nextFloat() * 3f)
                    (if (it % 2 == 0) light else deep).addOval(Rect(x - r, y - r, x + r, y + r))
                }
                drawPath(light, Color.White.copy(alpha = 0.35f))
                drawPath(deep, Color(0xFFB8862F).copy(alpha = 0.25f))
            }
        }

        // Deep navy with blue smoke curling round the right half.
        Finish.PRIME_SMOKE -> {
            val center = at(0.66f, 0.50f)
            drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = 0.22f), Color.Transparent), center, h * 0.7f), h * 0.7f, center)
            // A few broad curls, each drawn soft and wide, then fine and bright, like lit smoke.
            val smoke = Path()
            repeat(9) { i ->
                val start = i * 0.7f
                val r0 = h * (0.10f + i * 0.03f)
                for (s in 0..50) {
                    val a = start + s * 0.11f
                    val r = r0 * (1f + s * 0.015f)
                    val p = Offset(center.x + cos(a) * r * 1.25f, center.y + sin(a) * r * 0.9f)
                    if (s == 0) smoke.moveTo(p.x, p.y) else smoke.lineTo(p.x, p.y)
                }
            }
            drawPath(smoke, accent.copy(alpha = 0.06f), style = Stroke(line * 10f))
            drawPath(smoke, accent.copy(alpha = 0.14f), style = Stroke(line * 3.5f))
            drawPath(smoke, Color(0xFFBFE0FF).copy(alpha = 0.40f), style = Stroke(line * 0.8f))
            // Bright wisps at the right edge.
            val wisps = Path()
            repeat(14) {
                val y = h * random.nextFloat()
                wisps.moveTo(w * 1.02f, y)
                wisps.cubicTo(w * 0.94f, y - h * 0.08f, w * 0.96f, y + h * 0.10f, w * (0.86f + random.nextFloat() * 0.05f), y + h * 0.02f)
            }
            drawPath(wisps, Color(0xFFDDEBFF).copy(alpha = 0.45f), style = Stroke(line * 0.8f))
        }

        // Yellow gold, with a cluster of soft rounded tiles on the right behind a gentle curve.
        Finish.PRIME_TILES -> {
            val curve = Path().apply {
                moveTo(w * 0.62f, 0f)
                cubicTo(w * 0.50f, h * 0.40f, w * 0.80f, h * 0.70f, w * 0.70f, h)
                lineTo(w, h); lineTo(w, 0f); close()
            }
            drawPath(curve, Color.White.copy(alpha = 0.12f))
            listOf(
                Triple(0.78f, 0.12f, 0.20f), Triple(0.92f, 0.30f, 0.16f), Triple(0.70f, 0.40f, 0.14f),
                Triple(0.86f, 0.55f, 0.22f), Triple(0.98f, 0.80f, 0.18f), Triple(0.74f, 0.80f, 0.16f),
            ).forEachIndexed { i, (x, y, s) ->
                val side = h * s
                rotate(if (i % 2 == 0) 8f else -6f, at(x, y)) {
                    val topLeft = at(x, y) - Offset(side / 2, side / 2)
                    drawRoundRect(accent.copy(alpha = 0.45f + (i % 3) * 0.10f), topLeft, Size(side, side), CornerRadius(side * 0.3f))
                    drawRoundRect(Color(0xFFC77F0A).copy(alpha = 0.35f), topLeft, Size(side, side), CornerRadius(side * 0.3f), style = Stroke(line * 1.2f))
                }
            }
        }

        else -> Unit
    }
}
