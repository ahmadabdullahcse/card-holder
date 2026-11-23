package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/*
 * Trust Bank's cards, each following the composition of the real card closely, in new geometry
 * and without the bank's mark.
 */
internal fun DrawScope.drawTrust(finish: Finish, accent: Color, random: Random) {
    val w = size.width
    val h = size.height
    val line = density
    fun at(x: Float, y: Float) = Offset(w * x, h * y)

    when (finish) {
        // Red stained glass: uneven cells in shades of red, with dark lead between them.
        Finish.TRUST_GLASS -> {
            val sites = List(72) { at(random.nextFloat() * 1.1f - 0.05f, random.nextFloat() * 1.1f - 0.05f) }
            val shades = listOf(Color(0xFF7A0C0C), Color(0xFF9A1414), Color(0xFF5E0808), Color(0xFFB01A1A), Color(0xFF6A0A0A))
            val lead = Path()
            sites.forEachIndexed { i, site ->
                var cell = listOf(Offset(-10f, -10f), Offset(w + 10f, -10f), Offset(w + 10f, h + 10f), Offset(-10f, h + 10f))
                sites.forEachIndexed { j, other -> if (i != j) cell = clipToNearer(cell, site, other) }
                if (cell.size < 3) return@forEachIndexed
                val path = Path().apply { cell.forEachIndexed { k, p -> if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
                val shade = shades[random.nextInt(shades.size)]
                drawPath(path, Brush.linearGradient(listOf(lerp(shade, Color.White, 0.10f), shade), cell[0], cell[cell.size / 2]))
                lead.addPath(path)
            }
            drawPath(lead, Color(0xFF220303), style = Stroke(line * 2f))
        }

        // Charcoal, with a fan of fine blue lines that pinch into a bright twisting band just
        // left of the middle and spread out toward the top and the lower left.
        Finish.TRUST_STRINGS -> {
            val strings = Path()
            for (i in 0..120) {
                val t = i / 120f
                val start = at(0.34f + 0.24f * t, -0.02f)
                val end = if (t < 0.7f) at(-0.02f, 0.30f + 0.95f * t) else at(0.10f + (t - 0.7f) * 1.2f, 1.02f)
                strings.moveTo(start.x, start.y)
                strings.cubicTo(w * (0.50f + 0.03f * t), h * 0.40f, w * 0.52f, h * (0.80f + 0.05f * t), end.x, end.y)
            }
            drawPath(strings, accent.copy(alpha = 0.55f), style = Stroke(line * 0.5f))
            // The bright pinch of the band.
            drawRect(
                Brush.horizontalGradient(0f to Color.Transparent, 0.5f to Color(0xFF7FD4FF).copy(alpha = 0.25f), 1f to Color.Transparent, startX = w * 0.40f, endX = w * 0.58f),
            )
        }

        // Night in a narrow street: purple and blue haze on the left, a chain of round orange
        // lights down the left side, a lantern hanging on its wire with a glowing faceted base, a
        // red-brown wooden wall on the right, and a tall reflection of light at the bottom. The
        // lantern hangs a little higher than in the photo, so its glow stays off the card number.
        Finish.TRUST_LANTERN -> {
            drawCircle(Brush.radialGradient(listOf(Color(0xFF3A4A8A).copy(alpha = 0.7f), Color.Transparent), at(0.08f, 0.92f), w * 0.5f), w * 0.5f, at(0.08f, 0.92f))
            drawCircle(Brush.radialGradient(listOf(Color(0xFF8A2050).copy(alpha = 0.45f), Color.Transparent), at(0.42f, 0.50f), w * 0.4f), w * 0.4f, at(0.42f, 0.50f))
            val wall = Path().apply { moveTo(w * 0.62f, 0f); lineTo(w, 0f); lineTo(w, h); lineTo(w * 0.60f, h); close() }
            drawPath(wall, Brush.horizontalGradient(listOf(Color(0xFF7A2A1A), Color(0xFF5A1A10), Color(0xFF3A100A)), w * 0.6f, w))
            listOf(0.74f to 0.035f, 0.90f to 0.025f).forEach { (x, width) ->
                drawRect(Color.Black.copy(alpha = 0.55f), at(x, 0f), androidx.compose.ui.geometry.Size(w * width, h))
            }
            drawCircle(Brush.radialGradient(listOf(Color.Black, Color.Transparent), at(1f, 0f), w * 0.3f), w * 0.3f, at(1f, 0f))
            // The chain of lights: discs with soft but definite edges, and the reflection below.
            listOf(
                Triple(0.40f, 0.20f, 0.07f), Triple(0.33f, 0.33f, 0.065f), Triple(0.29f, 0.46f, 0.055f),
                Triple(0.43f, 0.40f, 0.05f), Triple(0.47f, 0.72f, 0.06f), Triple(0.24f, 0.24f, 0.04f),
            ).forEach { (x, y, r) ->
                val c = at(x, y)
                val radius = h * r * 1.3f
                drawCircle(
                    Brush.radialGradient(0f to Color(0xFFFFEAA8), 0.45f to Color(0xFFF5A82A), 0.8f to accent.copy(alpha = 0.6f), 1f to Color.Transparent, center = c, radius = radius),
                    radius, c,
                )
            }
            drawOval(Brush.radialGradient(listOf(Color(0xFFFFC060).copy(alpha = 0.8f), Color.Transparent), at(0.53f, 0.92f), h * 0.18f), at(0.49f, 0.74f), androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.36f))
            // The lantern on its wire.
            drawLine(Color(0xFF0C0605), at(0.69f, -0.02f), at(0.69f, 0.14f), line * 1.5f)
            val body = Path().apply { moveTo(w * 0.64f, h * 0.14f); lineTo(w * 0.74f, h * 0.14f); lineTo(w * 0.84f, h * 0.33f); lineTo(w * 0.56f, h * 0.33f); close() }
            drawPath(body, Color(0xFF140A08))
            val base = Path().apply { moveTo(w * 0.56f, h * 0.33f); lineTo(w * 0.84f, h * 0.33f); lineTo(w * 0.78f, h * 0.44f); lineTo(w * 0.62f, h * 0.44f); close() }
            drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = 0.55f), Color.Transparent), at(0.70f, 0.42f), h * 0.22f), h * 0.22f, at(0.70f, 0.42f))
            drawPath(base, Brush.linearGradient(listOf(Color(0xFFFFE08A), Color(0xFFF5A82A), Color(0xFFB0600F)), at(0.58f, 0.33f), at(0.78f, 0.44f)))
            val facets = Path().apply {
                moveTo(w * 0.66f, h * 0.33f); lineTo(w * 0.68f, h * 0.44f)
                moveTo(w * 0.75f, h * 0.33f); lineTo(w * 0.73f, h * 0.44f)
            }
            drawPath(facets, Color(0xFF8A4A10).copy(alpha = 0.7f), style = Stroke(line))
            drawOval(Brush.radialGradient(listOf(Color(0xFF8FB8FF), Color.Transparent), at(0.82f, 0.22f), h * 0.05f), at(0.81f, 0.14f), androidx.compose.ui.geometry.Size(w * 0.02f, h * 0.16f))
        }

        // Black, with a tunnel of small square outlines in rings round a green glow, turning red
        // toward the lower left.
        Finish.TRUST_TUNNEL -> {
            val center = at(0.56f, 0.52f)
            drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = 0.55f), accent.copy(alpha = 0.10f), Color.Transparent), center, h * 0.45f), h * 0.45f, center)
            val green = Path()
            val red = Path()
            var r = h * 0.12f
            while (r < w) {
                val count = (2 * PI * r / (h * 0.05f)).toInt()
                val side = r * 0.07f
                for (k in 0 until count) {
                    if (random.nextFloat() < 0.15f) continue
                    val a = k * 2 * PI.toFloat() / count + random.nextFloat() * 0.05f
                    val p = Offset(center.x + cos(a) * r * 1.3f, center.y + sin(a) * r)
                    val lowerLeft = cos(a) < 0.1f && sin(a) > -0.6f
                    (if (lowerLeft && random.nextFloat() < 0.8f) red else green).addRect(Rect(center = p, radius = side / 2))
                }
                r *= 1.12f
            }
            drawPath(green, accent.copy(alpha = 0.55f), style = Stroke(line * 0.7f))
            drawPath(red, Color(0xFFE0383A).copy(alpha = 0.65f), style = Stroke(line * 0.7f))
        }

        // Black, with a chequered vortex of blue, pale and teal tiles spiralling out of a small
        // dark hole just right of the middle: tiny near the hole and growing outward over the
        // left and bottom, while the top stays black.
        Finish.TRUST_VORTEX -> {
            val c = at(0.58f, 0.56f)
            val palette = listOf(Color(0xFF3A8BEF), Color(0xFFE6F2FF), Color(0xFF27B5C8), Color(0xFF1C4FA8), Color(0xFF8FC8F0))
            val sectors = 48
            var ring = 0
            var r0 = h * 0.07f
            while (r0 < w * 1.3f) {
                val r1 = r0 * 1.13f
                val twist = ring * 0.16f
                for (s in 0 until sectors) {
                    if ((s + ring) % 2 == 1) continue
                    val a0 = s * 2 * PI.toFloat() / sectors + twist
                    val a1 = a0 + 2 * PI.toFloat() / sectors * 0.85f
                    val mid = (a0 + a1) / 2
                    // Leave the top black, apart from the far left.
                    if (sin(mid) < -0.15f && cos(mid) > -0.7f) continue
                    val skew = 0.12f
                    val quad = Path().apply {
                        moveTo(c.x + cos(a0) * r0 * 1.25f, c.y + sin(a0) * r0)
                        lineTo(c.x + cos(a1) * r0 * 1.25f, c.y + sin(a1) * r0)
                        lineTo(c.x + cos(a1 + skew) * r1 * 1.25f, c.y + sin(a1 + skew) * r1)
                        lineTo(c.x + cos(a0 + skew) * r1 * 1.25f, c.y + sin(a0 + skew) * r1)
                        close()
                    }
                    val fade = (r0 / (w * 0.6f)).coerceAtMost(1f)
                    drawPath(quad, palette[(s / 2 + ring * 3) % palette.size].copy(alpha = 0.9f * fade))
                }
                r0 = r1
                ring++
            }
        }

        else -> Unit
    }
}

/** The part of [cell] nearer to [site] than to [other]: one cut of a Voronoi cell. */
private fun clipToNearer(cell: List<Offset>, site: Offset, other: Offset): List<Offset> {
    val mid = (site + other) / 2f
    val n = other - site
    fun inside(p: Offset) = (p.x - mid.x) * n.x + (p.y - mid.y) * n.y <= 0f
    val out = mutableListOf<Offset>()
    for (i in cell.indices) {
        val a = cell[i]
        val b = cell[(i + 1) % cell.size]
        val ia = inside(a)
        val ib = inside(b)
        if (ia) out += a
        if (ia != ib) {
            val da = (a.x - mid.x) * n.x + (a.y - mid.y) * n.y
            val db = (b.x - mid.x) * n.x + (b.y - mid.y) * n.y
            val t = da / (da - db)
            out += a + (b - a) * t
        }
    }
    return out
}
