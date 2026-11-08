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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/*
 * BRAC Bank's cards, each drawn after the look of the real card: its colour, its large shape
 * and its motif, in new geometry. No logo, wordmark or artwork is copied.
 *
 * The text on a card sits in fixed places, so the large shapes keep out of them: the number row
 * ends about 70% across, VALID THRU and the name stay in the left half, and the network mark
 * fills the lower right corner. A gap from about 70% to 77% across stays clear top to bottom.
 */
internal fun DrawScope.drawBrac(finish: Finish, accent: Color, random: Random) {
    val w = size.width
    val h = size.height
    val line = density
    fun at(x: Float, y: Float) = Offset(w * x, h * y)

    when (finish) {
        // Silver, with clusters of grey and white squares at the right.
        Finish.BRAC_CLASSIC -> tiles(
            Tile(0.80f, 0.16f, 0.36f, Color(0xFF6E747D), 0.26f),
            Tile(0.66f, 0.32f, 0.22f, Color.White, 0.55f),
            Tile(0.95f, 0.44f, 0.30f, Color(0xFF7E848D), 0.16f),
            Tile(0.86f, 0.66f, 0.18f, Color.White, 0.50f),
            Tile(0.72f, 0.88f, 0.26f, Color(0xFF7E848D), 0.14f),
            Tile(0.97f, 0.92f, 0.20f, Color(0xFF5F656E), 0.26f),
            Tile(0.62f, 0.12f, 0.12f, Color(0xFF5F656E), 0.22f),
            Tile(0.90f, 0.30f, 0.12f, Color(0xFF5F656E), 0.30f),
            Tile(0.80f, 0.50f, 0.10f, Color.White, 0.6f),
        )

        // Bright gold, with paler and deeper gold squares in the same places.
        Finish.BRAC_GOLD -> tiles(
            Tile(0.80f, 0.16f, 0.36f, Color(0xFFB27A1C), 0.26f),
            Tile(0.66f, 0.32f, 0.22f, Color(0xFFFFF0B5), 0.55f),
            Tile(0.95f, 0.44f, 0.30f, Color(0xFFB27A1C), 0.24f),
            Tile(0.86f, 0.66f, 0.18f, Color(0xFFFFF0B5), 0.50f),
            Tile(0.72f, 0.88f, 0.26f, Color(0xFFB27A1C), 0.22f),
            Tile(0.97f, 0.92f, 0.20f, Color(0xFFFFF0B5), 0.40f),
            Tile(0.62f, 0.12f, 0.12f, Color(0xFFB27A1C), 0.30f),
            Tile(0.90f, 0.30f, 0.12f, Color(0xFFFFF0B5), 0.55f),
            Tile(0.80f, 0.50f, 0.10f, Color(0xFFB27A1C), 0.35f),
        )

        // Black, with soft graphite squares.
        Finish.BRAC_NIGHT_TILES -> tiles(
            Tile(0.80f, 0.16f, 0.36f, accent, 0.16f),
            Tile(0.66f, 0.32f, 0.22f, accent, 0.26f),
            Tile(0.95f, 0.44f, 0.30f, accent, 0.20f),
            Tile(0.86f, 0.66f, 0.18f, accent, 0.30f),
            Tile(0.72f, 0.88f, 0.26f, accent, 0.12f),
            Tile(0.97f, 0.92f, 0.20f, accent, 0.18f),
        )

        // Black, with a bold cluster of violet and gold squares. The gold ones stay clear of
        // the tier label and the network mark, where white text would sit on them.
        Finish.BRAC_SIGNATURE -> tiles(
            Tile(0.62f, 0.16f, 0.30f, Color(0xFF6B2C92), 0.85f),
            Tile(0.88f, 0.12f, 0.30f, Color(0xFF4E1E70), 0.90f),
            Tile(0.77f, 0.37f, 0.24f, accent, 0.95f),
            Tile(0.96f, 0.42f, 0.22f, Color(0xFF8A3DB8), 0.80f),
            Tile(0.60f, 0.42f, 0.13f, accent, 0.70f),
            Tile(0.87f, 0.67f, 0.16f, accent, 0.75f),
            Tile(1.00f, 0.80f, 0.20f, Color(0xFF6B2C92), 0.70f),
        )

        // A starry night with a tall lit doorway in the clear gap, and its light on the floor.
        Finish.BRAC_PORTAL -> {
            repeat(70) {
                drawCircle(
                    Color.White.copy(alpha = 0.15f + random.nextFloat() * 0.45f),
                    line * (0.4f + random.nextFloat() * 0.8f),
                    at(random.nextFloat(), random.nextFloat() * 0.72f),
                )
            }
            val floor = h * 0.74f
            val door = w * 0.735f
            drawRect(
                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f)), floor, h),
                Offset(0f, floor), Size(w, h - floor),
            )
            // The light spilling forward from the doorway.
            val spill = Path().apply {
                moveTo(door - w * 0.02f, floor)
                lineTo(door + w * 0.02f, floor)
                lineTo(w * 1.05f, h)
                lineTo(w * 0.40f, h)
                close()
            }
            drawPath(spill, Brush.verticalGradient(listOf(accent.copy(alpha = 0.22f), Color.Transparent), floor, h))
            // A wide soft glow, then the bright opening itself.
            val glow = w * 0.11f
            drawRect(
                Brush.horizontalGradient(
                    0f to Color.Transparent, 0.5f to accent.copy(alpha = 0.40f), 1f to Color.Transparent,
                    startX = door - glow, endX = door + glow,
                ),
                Offset(door - glow, 0f), Size(glow * 2, floor),
            )
            val core = w * 0.028f
            drawRect(
                Brush.horizontalGradient(
                    0f to accent.copy(alpha = 0.6f), 0.3f to Color.White, 0.7f to Color.White, 1f to accent.copy(alpha = 0.6f),
                    startX = door - core, endX = door + core,
                ),
                Offset(door - core, 0f), Size(core * 2, floor),
            )
            drawOval(
                Brush.radialGradient(listOf(accent.copy(alpha = 0.45f), Color.Transparent), Offset(door, floor), w * 0.16f),
                Offset(door - w * 0.16f, floor - h * 0.05f), Size(w * 0.32f, h * 0.10f),
            )
        }

        // A cable-stayed bridge lit in blue, above the number row, with its glow on the water.
        Finish.BRAC_BRIDGE -> {
            val start = at(0.20f, 0.50f)
            val control = at(0.60f, 0.50f)
            val end = at(1.05f, 0.30f)
            fun deck(t: Float): Offset {
                val u = 1 - t
                return start * (u * u) + control * (2 * t * u) + end * (t * t)
            }
            val deckPath = Path().apply {
                moveTo(start.x, start.y)
                quadraticTo(control.x, control.y, end.x, end.y)
            }
            // Reflections on the water, right of the number row.
            repeat(14) {
                val y = h * (0.56f + random.nextFloat() * 0.20f)
                val x = w * (0.74f + random.nextFloat() * 0.2f)
                drawLine(accent.copy(alpha = 0.08f + random.nextFloat() * 0.14f), Offset(x, y), Offset(x + w * (0.03f + random.nextFloat() * 0.08f), y), line)
            }
            // Two towers, each with a fan of cables down to the deck.
            listOf(0.52f to 0.22f, 0.84f to 0.20f).forEach { (t, top) ->
                val foot = deck(t)
                val peak = Offset(foot.x, h * top)
                drawLine(accent.copy(alpha = 0.75f), peak, foot, line * 1.6f)
                listOf(-0.20f, -0.14f, -0.08f, 0.08f, 0.14f, 0.20f).forEach { dt ->
                    val anchor = deck((t + dt).coerceIn(0f, 1f))
                    drawLine(accent.copy(alpha = 0.35f), peak, anchor, line * 0.6f)
                }
                drawCircle(Color.White.copy(alpha = 0.9f), line * 1.6f, peak)
            }
            drawPath(deckPath, accent.copy(alpha = 0.18f), style = Stroke(line * 12f))
            drawPath(deckPath, accent.copy(alpha = 0.35f), style = Stroke(line * 5f))
            drawPath(deckPath, accent.copy(alpha = 0.95f), style = Stroke(line * 2f))
            // Rows of small lamps along the railings.
            for (i in 0..60) {
                val p = deck(i / 60f)
                drawLine(Color.White.copy(alpha = 0.35f + (i % 4) * 0.12f), p, p - Offset(0f, h * (0.025f + (i % 3) * 0.012f)), line * 0.8f)
            }
            for (i in 0..30) drawCircle(Color.White.copy(alpha = 0.5f + (i % 3) * 0.2f), line * 0.9f, deck(i / 30f))
        }

        // Turquoise water with a ring of slim boats, like petals, at the upper right.
        Finish.BRAC_SAILS -> {
            // Deeper and shallower patches of water.
            repeat(12) {
                val c = at(random.nextFloat(), random.nextFloat())
                val r = h * (0.15f + random.nextFloat() * 0.30f)
                val tint = if (it % 2 == 0) Color(0xFF7FF3F7).copy(alpha = 0.30f) else Color(0xFF0479A0).copy(alpha = 0.30f)
                drawCircle(Brush.radialGradient(listOf(tint, Color.Transparent), c, r), r, c)
            }
            // Wavelets seen from above: thousands of tiny curved crests, each with a sunlit edge
            // and a shadowed trough just below it. They are gathered into three shapes (glints,
            // soft crests and troughs) so they draw in three calls.
            val glints = Path()
            val crests = Path()
            val troughs = Path()
            repeat(2600) {
                val x = w * random.nextFloat()
                val y = h * random.nextFloat()
                val len = line * (1.5f + random.nextFloat() * 3f)
                val lift = len * (0.15f + random.nextFloat() * 0.25f)
                val tilt = (random.nextFloat() - 0.5f) * len * 0.5f
                val crest = if (it % 5 == 0) glints else crests
                crest.moveTo(x, y)
                crest.quadraticTo(x + len / 2, y - lift, x + len, y + tilt)
                troughs.moveTo(x, y + line * 1.1f)
                troughs.quadraticTo(x + len / 2, y + line * 1.1f - lift, x + len, y + tilt + line * 1.1f)
            }
            drawPath(troughs, Color(0xFF02708C).copy(alpha = 0.20f), style = Stroke(line * 0.7f))
            drawPath(crests, Color(0xFFBDFBFF).copy(alpha = 0.18f), style = Stroke(line * 0.6f))
            drawPath(glints, Color.White.copy(alpha = 0.38f), style = Stroke(line * 0.6f))
            boats(at(1.02f, 0.66f), h * 0.15f, 8)
            boats(at(0.82f, 0.40f), h * 0.22f, 10)
            drawCircle(accent.copy(alpha = 0.35f), h * 0.035f, at(0.82f, 0.40f))
        }

        // Paler turquoise covered in faint embossed flowers and curls.
        Finish.BRAC_TARA_WORLD -> {
            listOf(
                Triple(0.86f, 0.30f, 0.20f), Triple(0.62f, 0.10f, 0.12f), Triple(0.08f, 0.95f, 0.16f),
                Triple(0.48f, 0.80f, 0.12f), Triple(0.98f, 0.72f, 0.15f), Triple(0.30f, 0.30f, 0.10f),
            ).forEach { (x, y, r) -> rosette(at(x, y), h * r, accent) }
            repeat(18) {
                val c = at(random.nextFloat(), random.nextFloat())
                val r = h * (0.03f + random.nextFloat() * 0.05f)
                drawArc(accent.copy(alpha = 0.10f), random.nextFloat() * 360f, 220f, false, c - Offset(r, r), Size(r * 2, r * 2), style = Stroke(line * 0.8f))
            }
        }

        // Deep red, with a sheaf of fine flowing lines sweeping up the lower right.
        Finish.BRAC_TARA -> {
            drawCircle(
                Brush.radialGradient(listOf(Color(0xFFD23A3A).copy(alpha = 0.35f), Color.Transparent), at(0.25f, 0.2f), w * 0.6f),
                w * 0.6f, at(0.25f, 0.2f),
            )
            // A band of evenly spaced threads: flat along the bottom left, rising through the
            // middle, then curling steeply up the right edge to the top corner.
            repeat(14) { i ->
                val f = i / 13f
                val y0 = h * (1.06f - f * 0.44f)
                val y1 = h * (0.90f - f * 0.40f)
                val y2 = h * (0.34f - f * 0.32f)
                val path = Path().apply {
                    moveTo(-w * 0.05f, y0)
                    cubicTo(w * 0.25f, y0, w * 0.40f, y1 + h * 0.10f, w * 0.62f, y1)
                    cubicTo(w * 0.82f, y1 - h * 0.08f, w * 0.95f, y2 + h * 0.25f, w * 1.05f, y2)
                }
                drawPath(path, accent.copy(alpha = 0.34f), style = Stroke(line * 1.3f))
            }
        }

        // Blue, lighter toward the top right, with wide rings spreading from the left edge.
        Finish.BRAC_FLEXI -> {
            drawCircle(
                Brush.radialGradient(listOf(accent.copy(alpha = 0.30f), Color.Transparent), at(0.95f, 0f), w * 0.7f),
                w * 0.7f, at(0.95f, 0f),
            )
            // Three quiet sets of rings: at the top edge, the left edge and the lower right.
            listOf(Triple(0.44f, -0.02f, 2), Triple(-0.01f, 0.64f, 3), Triple(0.97f, 0.84f, 2)).forEach { (x, y, rings) ->
                for (k in 1..rings) drawCircle(accent.copy(alpha = 0.20f - k * 0.04f), h * 0.12f * k, at(x, y), style = Stroke(line * 1.1f))
            }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.22f)), h * 0.6f, h), Offset(0f, h * 0.6f), Size(w, h * 0.4f))
        }

        // Emerald, with a dotted globe rising from the lower right and light on its rim.
        Finish.BRAC_GLOBE -> {
            val center = at(0.94f, 1.10f)
            val radius = h * 0.62f
            drawCircle(Brush.radialGradient(listOf(Color(0xFF04170B), Color(0xFF0A3A1E)), center, radius), radius, center)
            // Land as dots: a smooth made-up pattern, fading toward the rim like a sphere. The dots
            // are gathered into three shapes by brightness, so they draw in three calls, not hundreds.
            val step = line * 3.6f
            val dot = line * 1.1f
            val bands = List(3) { Path() }
            var y = center.y - radius
            while (y < h) {
                var x = center.x - radius
                while (x < w) {
                    val u = (x - center.x) / radius
                    val v = (y - center.y) / radius
                    val r2 = u * u + v * v
                    if (r2 < 1f && sin(u * 7f + 1.3f) * cos(v * 5f - 0.7f) + sin(u * 3.1f + v * 4.3f) > 0.45f) {
                        val band = (sqrt(1f - r2) * 3f).toInt().coerceAtMost(2)
                        bands[band].addOval(Rect(x - dot, y - dot, x + dot, y + dot))
                    }
                    x += step
                }
                y += step
            }
            bands.forEachIndexed { i, path -> drawPath(path, accent.copy(alpha = 0.30f + i * 0.27f)) }
            listOf(0.30f, 0.62f, 0.90f).forEach { k ->
                drawOval(accent.copy(alpha = 0.20f), Offset(center.x - radius * k, center.y - radius), Size(radius * 2 * k, radius * 2), style = Stroke(line * 0.7f))
            }
            listOf(-0.75f, -0.45f, -0.15f).forEach { k ->
                val half = radius * sqrt(1f - k * k)
                drawLine(accent.copy(alpha = 0.18f), Offset(center.x - half, center.y + radius * k), Offset(center.x + half, center.y + radius * k), line * 0.7f)
            }
            drawCircle(
                Brush.radialGradient(
                    0.90f to Color.Transparent, 0.985f to accent.copy(alpha = 0.75f), 1f to Color.White.copy(alpha = 0.85f),
                    1.03f to accent.copy(alpha = 0.25f), 1.12f to Color.Transparent,
                    center = center, radius = radius * 1.12f,
                ),
                radius * 1.12f, center,
            )
        }

        else -> Unit
    }
}

private class Tile(val x: Float, val y: Float, val side: Float, val color: Color, val alpha: Float)

/** Squares turned on their corner, each shaded from its top corner and edged with light. */
private fun DrawScope.tiles(vararg tiles: Tile) = tiles.forEach { tile ->
    val center = Offset(size.width * tile.x, size.height * tile.y)
    val side = size.height * tile.side
    val topLeft = center - Offset(side / 2, side / 2)
    rotate(45f, center) {
        drawRect(
            Brush.linearGradient(listOf(tile.color.copy(alpha = tile.alpha), tile.color.copy(alpha = tile.alpha * 0.45f)), topLeft, topLeft + Offset(side, side)),
            topLeft, Size(side, side),
        )
        drawRect(Color.White.copy(alpha = tile.alpha * 0.35f), topLeft, Size(side, side), style = Stroke(density * 0.6f))
    }
}

/** A ring of slim green boats pointing out from [center], like petals. */
private fun DrawScope.boats(center: Offset, length: Float, count: Int) {
    val beam = length * 0.14f
    repeat(count) { i ->
        rotate(i * 360f / count + 8f, center) {
            val boat = Path().apply {
                moveTo(center.x + length * 0.16f, center.y)
                quadraticTo(center.x + length * 0.55f, center.y - beam, center.x + length, center.y)
                quadraticTo(center.x + length * 0.55f, center.y + beam, center.x + length * 0.16f, center.y)
                close()
            }
            drawPath(boat, if (i % 2 == 0) Color(0xFF3C8F3E) else Color(0xFF9BC84A))
            drawPath(boat, Color(0xFF6B4A2B), style = Stroke(density * 1.2f))
        }
    }
}

/** A faint embossed flower: a ring of petals around two circles. */
private fun DrawScope.rosette(center: Offset, radius: Float, color: Color) {
    repeat(8) { i ->
        rotate(i * 45f, center) {
            drawOval(color.copy(alpha = 0.10f), Offset(center.x - radius * 0.16f, center.y - radius), Size(radius * 0.32f, radius * 0.62f))
        }
    }
    drawCircle(color.copy(alpha = 0.14f), radius * 0.30f, center, style = Stroke(density))
    drawCircle(color.copy(alpha = 0.10f), radius, center, style = Stroke(density * 0.8f))
}
