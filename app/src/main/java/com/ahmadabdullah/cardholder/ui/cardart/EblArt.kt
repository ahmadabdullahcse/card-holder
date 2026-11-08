package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/*
 * Eastern Bank's cards, each drawn after the look of the real card in new geometry, without the
 * bank's mark. Text sits in fixed places (see BracArt.kt), so the large shapes keep clear of the
 * number row where they can. Fine lines and dots are gathered into a few paths.
 */
internal fun DrawScope.drawEbl(finish: Finish, accent: Color, random: Random) {
    val w = size.width
    val h = size.height
    val line = density
    fun at(x: Float, y: Float) = Offset(w * x, h * y)

    when (finish) {
        // Silver with a fine woven texture, and a yellow strip down the left edge.
        Finish.EBL_CLASSIC -> {
            val weave = Path()
            var d = -h
            while (d < w) {
                weave.moveTo(d, 0f); weave.lineTo(d + h * 0.7f, h)
                weave.moveTo(d + h * 0.7f, 0f); weave.lineTo(d, h)
                d += line * 3.4f
            }
            drawPath(weave, Color(0xFF5E6368).copy(alpha = 0.30f), style = Stroke(line * 0.8f))
            translate(top = line) { drawPath(weave, Color.White.copy(alpha = 0.45f), style = Stroke(line * 0.6f)) }
            val strip = w * 0.05f
            drawRect(Brush.verticalGradient(listOf(Color(0xFFF7CF1A), accent, Color(0xFFE5B800))), Offset.Zero, Size(strip, h))
            drawLine(Color.White.copy(alpha = 0.6f), Offset(strip, 0f), Offset(strip, h), line)
        }

        // Worn gold: wavy streaks of lighter and darker metal, and a few darker patches.
        Finish.EBL_GOLD -> {
            repeat(6) {
                val c = at(random.nextFloat(), random.nextFloat())
                val r = h * (0.2f + random.nextFloat() * 0.3f)
                drawCircle(Brush.radialGradient(listOf(Color(0xFF5A3A0E).copy(alpha = 0.18f), Color.Transparent), c, r), r, c)
            }
            val light = Path()
            val dark = Path()
            repeat(160) {
                val y = h * random.nextFloat()
                val x0 = w * (random.nextFloat() * 1.2f - 0.2f)
                val len = w * (0.15f + random.nextFloat() * 0.5f)
                val path = if (it % 2 == 0) light else dark
                path.moveTo(x0, y)
                var x = x0
                while (x < x0 + len) {
                    x += line * 6f
                    path.lineTo(x, y + sin(x / w * 18f + it) * h * 0.008f)
                }
            }
            drawPath(light, Color(0xFFFFF1C2).copy(alpha = 0.30f), style = Stroke(line * 1.1f))
            drawPath(dark, Color(0xFF6B4510).copy(alpha = 0.30f), style = Stroke(line * 1.1f))
        }

        // Deep blue with a star of crossing lines, and a scatter of linked points like a network.
        Finish.EBL_PLATINUM -> {
            val center = at(0.42f, 0.42f)
            drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = 0.25f), Color.Transparent), center, h * 0.6f), h * 0.6f, center)
            val star = Path()
            listOf(0.10f to 9, 0.18f to 12, 0.28f to 16, 0.40f to 20, 0.55f to 24).forEach { (r, points) ->
                val radius = h * r
                for (i in 0..points) {
                    // Each point joins one a few steps around, which draws a many-pointed star.
                    val a = i * 5 * 2 * PI.toFloat() / points
                    val p = Offset(center.x + cos(a) * radius, center.y + sin(a) * radius)
                    if (i == 0) star.moveTo(p.x, p.y) else star.lineTo(p.x, p.y)
                }
            }
            drawPath(star, accent.copy(alpha = 0.32f), style = Stroke(line * 0.8f))
            drawCircle(Color(0xFF0A2F6B), h * 0.035f, center)
            val nodes = List(28) { at(random.nextFloat(), random.nextFloat()) }
            val links = Path()
            nodes.forEachIndexed { i, p ->
                nodes.drop(i + 1).sortedBy { (it - p).getDistance() }.take(2).forEach { q -> links.moveTo(p.x, p.y); links.lineTo(q.x, q.y) }
            }
            drawPath(links, Color.White.copy(alpha = 0.10f), style = Stroke(line * 0.6f))
            nodes.forEach { drawCircle(Color.White.copy(alpha = 0.45f), line * 1.2f, it) }
        }

        // Black with rows of turquoise dots curving down from the top left, large in front and
        // shrinking into the distance.
        Finish.EBL_SIGNATURE -> {
            val near = Path()
            val far = Path()
            for (row in 0..8) {
                for (s in 0..26) {
                    val u = s / 26f
                    val x = w * (-0.02f + u * 0.80f)
                    val y = h * (-0.04f + row * 0.075f + 0.30f * u * u + 0.04f * row * u)
                    val r = h * (0.026f * (1 - u) + 0.004f)
                    (if (u < 0.45f) near else far).addOval(Rect(x - r * 1.5f, y - r, x + r * 1.5f, y + r))
                }
            }
            drawPath(near, accent.copy(alpha = 0.40f))
            drawPath(far, accent.copy(alpha = 0.26f))
        }

        // Black with broad bands of brushed metal crossing one another.
        Finish.EBL_TITANIUM -> {
            listOf(Triple(0.30f, 0.80f, -40f), Triple(0.62f, 0.10f, -40f), Triple(0.55f, 0.75f, 45f)).forEach { (x, y, angle) ->
                val c = at(x, y)
                val long = w * 1.1f
                val wide = h * 0.24f
                rotate(angle, c) {
                    val topLeft = c - Offset(long / 2, wide / 2)
                    drawRect(
                        Brush.verticalGradient(listOf(Color(0xFF2B2B2D), Color(0xFF6E6E72), Color(0xFF3A3A3C)), topLeft.y, topLeft.y + wide),
                        topLeft, Size(long, wide), alpha = 0.85f,
                    )
                    var yy = topLeft.y
                    while (yy < topLeft.y + wide) {
                        drawLine(Color.White.copy(alpha = random.nextFloat() * 0.06f), Offset(topLeft.x, yy), Offset(topLeft.x + long, yy), line * 0.5f)
                        yy += line * 1.5f
                    }
                }
            }
        }

        // Space, with the golden edge of the Earth rising at the lower right, its night side
        // dotted with city lights.
        Finish.EBL_WORLD -> {
            val stars = Path()
            repeat(80) {
                val p = at(random.nextFloat(), random.nextFloat())
                val r = line * (0.3f + random.nextFloat() * 0.6f)
                stars.addOval(Rect(p.x - r, p.y - r, p.x + r, p.y + r))
            }
            drawPath(stars, Color.White.copy(alpha = 0.6f))
            val center = at(0.97f, 1.20f)
            val radius = h * 0.66f
            drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = 0.35f), Color.Transparent), center, radius * 1.6f), radius * 1.6f, center)
            drawCircle(Color(0xFF07070A), radius, center)
            val lights = Path()
            val step = line * 2.6f
            var y = center.y - radius
            while (y < h) {
                var x = center.x - radius
                while (x < w) {
                    val u = (x - center.x) / radius
                    val v = (y - center.y) / radius
                    if (u * u + v * v < 0.92f && sin(u * 9f + 2f) * cos(v * 7f) + sin(u * 4f - v * 5f) > 0.3f && random.nextFloat() < 0.8f) {
                        val r = line * (0.4f + random.nextFloat() * 0.5f)
                        lights.addOval(Rect(x - r, y - r, x + r, y + r))
                    }
                    x += step
                }
                y += step
            }
            drawPath(lights, accent.copy(alpha = 0.85f))
            drawCircle(
                Brush.radialGradient(
                    0.88f to Color.Transparent, 0.97f to accent, 0.99f to Color.White, 1.03f to accent.copy(alpha = 0.3f), 1.1f to Color.Transparent,
                    center = center, radius = radius * 1.1f,
                ),
                radius * 1.1f, center,
            )
            // The sun just behind the rim.
            val sun = Offset(center.x - radius * 0.55f, center.y - radius * 0.84f)
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.9f), accent.copy(alpha = 0.3f), Color.Transparent), sun, h * 0.16f), h * 0.16f, sun)
        }

        // Carbon fibre: a fine basket weave of short dark and light strands.
        Finish.EBL_WORLD_ELITE -> {
            val lit = Path()
            val shade = Path()
            val cell = line * 5f
            var row = 0
            var y = -cell
            while (y < h + cell) {
                var col = 0
                var x = -cell
                while (x < w + cell) {
                    rotateCell(if ((row + col) % 2 == 0) lit else shade, Offset(x, y), cell)
                    x += cell; col++
                }
                y += cell; row++
            }
            drawPath(lit, Color.White.copy(alpha = 0.10f))
            drawPath(shade, Color.Black.copy(alpha = 0.35f))
            drawRect(Brush.linearGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.08f), Color.Transparent), Offset(0f, 0f), Offset(w, h)))
        }

        // Black, with one broad painterly stroke of orange and yellow across the upper middle.
        Finish.EBL_DINERS -> {
            // Edges wander smoothly, with a little roughness, like a dry brush.
            fun edge(u: Float, side: Float, k: Float) =
                0.35f + 0.04f * sin(u * 4f + 0.5f) + side * (0.16f * k) * (0.85f + 0.15f * sin(u * 9f + side * 2f)) + 0.012f * sin(u * 61f + side * 7f)
            fun band(k: Float, color: Color) {
                val path = Path()
                val steps = 90
                for (i in 0..steps) {
                    val u = i / steps.toFloat()
                    val p = Offset(w * (-0.04f + u * 1.08f), h * edge(u, -1f, k))
                    if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
                }
                for (i in steps downTo 0) {
                    val u = i / steps.toFloat()
                    path.lineTo(w * (-0.04f + u * 1.08f), h * edge(u, 1f, k))
                }
                path.close()
                drawPath(path, color)
            }
            band(1.0f, Color(0xFFF29A0A))
            band(0.85f, accent.copy(alpha = 0.55f))
            // Mottled paint: soft patches of deeper orange and pale yellow inside the stroke.
            val deep = Path()
            val pale = Path()
            repeat(160) {
                val u = random.nextFloat()
                val y = h * edge(u, (random.nextFloat() * 2 - 1) * 0.8f, 1f)
                val r = h * (0.01f + random.nextFloat() * 0.035f)
                (if (it % 2 == 0) deep else pale).addOval(Rect(w * u - r * 1.6f, y - r, w * u + r * 1.6f, y + r))
            }
            drawPath(deep, Color(0xFFE07800).copy(alpha = 0.35f))
            drawPath(pale, Color(0xFFFFF3B0).copy(alpha = 0.35f))
            // Bristle streaks along the stroke.
            val streaks = Path()
            repeat(70) {
                val u = random.nextFloat()
                val y = h * edge(u, (random.nextFloat() * 2 - 1) * 0.9f, 1f)
                val x = w * u
                streaks.moveTo(x, y); streaks.lineTo(x + w * (0.05f + random.nextFloat() * 0.12f), y + h * 0.01f)
            }
            drawPath(streaks, Color(0xFFFFF6C8).copy(alpha = 0.45f), style = Stroke(line * 0.8f))
        }

        // Brushed silver with sweeps of yellow, green and blue rising across the bottom.
        Finish.EBL_UNIONPAY -> {
            var y = 0f
            while (y < h) {
                drawLine(Color.White.copy(alpha = random.nextFloat() * 0.25f), Offset(0f, y), Offset(w, y), line * 0.6f)
                y += line * 1.4f
            }
            fun sweep(top: Float, color: Color) {
                val path = Path().apply {
                    // Each sweep falls from the left edge and runs out along the bottom right.
                    moveTo(-w * 0.02f, h * top)
                    cubicTo(w * 0.35f, h * (top + 0.02f), w * 0.70f, h * (top + 0.16f), w * 1.05f, h * (top + 0.20f))
                    lineTo(w * 1.05f, h * 1.05f)
                    lineTo(-w * 0.02f, h * 1.05f)
                    close()
                }
                drawPath(path, color)
            }
            sweep(0.72f, Color(0xFFF2C21B))
            sweep(0.80f, Color(0xFFF7F7F7))
            sweep(0.83f, Color(0xFF1E9A4A))
            sweep(0.91f, Color(0xFF1F5FAE))
        }

        else -> Unit
    }
}

/** One weave cell of carbon fibre: a short strand across the cell, slightly inset. */
private fun rotateCell(path: Path, corner: Offset, cell: Float) {
    val inset = cell * 0.12f
    path.addRect(Rect(corner.x + inset, corner.y + inset, corner.x + cell - inset, corner.y + cell - inset))
}
