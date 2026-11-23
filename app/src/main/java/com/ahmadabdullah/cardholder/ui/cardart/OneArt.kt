package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/*
 * ONE Bank's cards, each following the composition of the real card closely, in new geometry and
 * without the bank's mark.
 */
internal fun DrawScope.drawOne(finish: Finish, accent: Color, random: Random) {
    val w = size.width
    val h = size.height
    val line = density
    fun at(x: Float, y: Float) = Offset(w * x, h * y)

    when (finish) {
        // Black, with faint dark mandalas across the whole card, an intricate gold mandala in the
        // top left corner, and a long looping trail of golden light: thin where it starts near the
        // expiry date, widening as it curves round on the right and back along the top.
        Finish.ONE_TRAILS -> {
            listOf(Triple(0.55f, 0.20f, 0.45f), Triple(0.95f, 0.85f, 0.40f), Triple(0.25f, 0.95f, 0.35f)).forEach { (x, y, r) ->
                mandala(at(x, y), h * r, Color.White.copy(alpha = 0.05f), line * 0.6f)
            }
            mandala(at(0.0f, 0.0f), h * 0.62f, accent.copy(alpha = 0.75f), line * 0.7f)
            val c = at(0.72f, 0.47f)
            val rx = w * 0.30f
            val ry = h * 0.11f
            val tilt = Math.toRadians(-35.0).toFloat()
            val glow = Path()
            val core = Path()
            for (k in 0..24) {
                val t = k / 24f - 0.5f
                for (s in 0..80) {
                    val u = s / 80f
                    // From the lower left, round under the right side, and back along the top.
                    val a = Math.toRadians(180.0 - 290.0 * u).toFloat()
                    // The strands start together and fan apart toward the far end of the loop.
                    val spread = t * 0.9f * u
                    val x0 = cos(a) * rx * (1f + spread * 0.15f)
                    val y0 = sin(a) * ry * (1f + spread * 2.2f) + spread * ry
                    val p = Offset(c.x + x0 * cos(tilt) - y0 * sin(tilt), c.y + x0 * sin(tilt) + y0 * cos(tilt))
                    if (s == 0) glow.moveTo(p.x, p.y) else glow.lineTo(p.x, p.y)
                    if (u in 0.12f..0.50f && kotlin.math.abs(t) < 0.25f) { if (s == 10) core.moveTo(p.x, p.y) else if (s > 10) core.lineTo(p.x, p.y) }
                }
            }
            val fade = Brush.linearGradient(listOf(accent.copy(alpha = 0.10f), accent, Color(0xFFFFE9B8), accent.copy(alpha = 0.5f)), at(0.47f, 0.75f), at(0.96f, 0.21f))
            drawPath(glow, accent.copy(alpha = 0.04f), style = Stroke(line * 8f))
            drawPath(glow, fade, style = Stroke(line * 0.5f))
            drawPath(core, Color(0xFFFFF4D8).copy(alpha = 0.55f), style = Stroke(line * 1.2f))
        }

        // Silver ribbed metal: rounded ribs that run almost flat across the lower left, then bend
        // up and stand tall on the right, with a bright sheen across them above the card number
        // and a softer one down the right.
        Finish.ONE_RIBS -> {
            fun sheen(path: Path, alpha: Float) {
                for ((width, a) in listOf(60f to 0.14f, 34f to 0.22f, 14f to 0.34f)) drawPath(path, Color.White.copy(alpha = a * alpha), style = Stroke(line * width))
            }
            sheen(Path().apply { moveTo(w * 0.62f, -h * 0.05f); cubicTo(w * 0.48f, h * 0.18f, w * 0.30f, h * 0.30f, -w * 0.02f, h * 0.40f) }, 1f)
            sheen(Path().apply { moveTo(w * 0.92f, -h * 0.05f); cubicTo(w * 0.85f, h * 0.35f, w * 0.80f, h * 0.70f, w * 0.78f, h * 1.05f) }, 0.6f)
            val grooves = Path()
            for (i in 0..30) {
                val y0 = 0.04f + i * 0.055f
                val x1 = 0.28f + i * 0.034f
                grooves.moveTo(-w * 0.02f, h * y0)
                grooves.cubicTo(w * 0.30f, h * y0, w * (x1 - 0.04f), h * (y0 * 0.55f), w * x1, -h * 0.02f)
            }
            // Each rib is rounded: shadow just after its groove, light just before the next.
            translate(left = line * 2.5f, top = line * 2.5f) { drawPath(grooves, Color.Black.copy(alpha = 0.40f), style = Stroke(line * 7f)) }
            translate(left = -line * 2.5f, top = -line * 2.5f) { drawPath(grooves, Color.White.copy(alpha = 0.22f), style = Stroke(line * 4f)) }
            drawPath(grooves, Color.Black.copy(alpha = 0.85f), style = Stroke(line * 1.2f))
        }

        // Dark, warming to olive in the middle and cooling to blue at the corners, with broad soft
        // ribbons of golden light flowing up to the right, each with a bright edge, and sparkles.
        Finish.ONE_GOLD_STREAKS -> {
            drawCircle(Brush.radialGradient(listOf(Color(0xFF2A4A6A).copy(alpha = 0.6f), Color.Transparent), at(0.95f, 1.0f), w * 0.4f), w * 0.4f, at(0.95f, 1.0f))
            drawCircle(Brush.radialGradient(listOf(Color(0xFF1E3A5A).copy(alpha = 0.5f), Color.Transparent), at(0.0f, 0.95f), w * 0.3f), w * 0.3f, at(0.0f, 0.95f))
            drawCircle(Brush.radialGradient(listOf(Color(0xFF6A5A10).copy(alpha = 0.5f), Color.Transparent), at(0.45f, 0.35f), w * 0.5f), w * 0.5f, at(0.45f, 0.35f))
            fun ribbon(x0: Float, y0: Float, x1: Float, y1: Float, lift: Float, width: Float, strength: Float) {
                val top = Path().apply { moveTo(w * x0, h * y0); cubicTo(w * (x0 + (x1 - x0) * 0.35f), h * (y0 - lift), w * (x0 + (x1 - x0) * 0.65f), h * (y1 + lift * 0.4f), w * x1, h * y1) }
                val band = Path().apply {
                    addPath(top)
                    lineTo(w * x1, h * (y1 + width))
                    cubicTo(w * (x0 + (x1 - x0) * 0.65f), h * (y1 + lift * 0.4f + width * 1.6f), w * (x0 + (x1 - x0) * 0.35f), h * (y0 - lift + width), w * x0, h * (y0 + width * 0.2f))
                    close()
                }
                drawPath(band, Brush.verticalGradient(listOf(accent.copy(alpha = 0.30f * strength), accent.copy(alpha = 0.08f * strength), Color.Transparent), h * (y1 - 0.05f), h * (y0 + width)))
                for ((stroke, alpha) in listOf(30f to 0.05f, 16f to 0.09f, 7f to 0.16f, 3f to 0.30f)) drawPath(top, accent.copy(alpha = alpha * strength), style = Stroke(line * stroke))
                drawPath(top, Color(0xFFFFF6C8).copy(alpha = 0.9f * strength), style = Stroke(line * 0.9f))
            }
            drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = 0.55f), Color.Transparent), at(0.42f, 0.22f), w * 0.30f), w * 0.30f, at(0.42f, 0.22f))
            ribbon(0.12f, 0.46f, 0.72f, -0.02f, 0.10f, 0.12f, 1f)
            ribbon(0.30f, 0.40f, 1.02f, 0.12f, 0.06f, 0.10f, 0.8f)
            ribbon(0.05f, 0.30f, 0.55f, 0.02f, 0.05f, 0.06f, 0.6f)
            ribbon(0.40f, 0.95f, 1.02f, 0.62f, 0.08f, 0.08f, 0.7f)
            ribbon(0.20f, 1.02f, 0.85f, 0.78f, 0.05f, 0.05f, 0.5f)
            val sparkles = Path()
            repeat(60) {
                val p = at(random.nextFloat(), random.nextFloat())
                val r = line * (0.5f + random.nextFloat() * 1.2f)
                sparkles.addOval(Rect(center = p, radius = r))
            }
            drawPath(sparkles, Color(0xFFFFE6A0).copy(alpha = 0.75f))
        }

        // Pale silver engraved with large flowers: rings of rounded petals round a seeded centre,
        // drawn as white lines with a soft grey shadow.
        Finish.ONE_PEONIES -> {
            val flowers = Path()
            listOf(
                Triple(0.62f, 0.30f, 0.34f), Triple(0.92f, 0.78f, 0.30f), Triple(0.30f, 0.86f, 0.26f),
                Triple(0.16f, 0.22f, 0.24f), Triple(1.02f, 0.10f, 0.22f), Triple(0.58f, 0.92f, 0.18f),
            ).forEach { (x, y, r) ->
                val c = at(x, y)
                for (layer in 1..4) {
                    val radius = h * r * layer / 4f
                    val petals = 5 + layer * 2
                    for (i in 0 until petals) {
                        val a0 = i * 2 * PI.toFloat() / petals + layer * 0.3f
                        val a1 = (i + 1) * 2 * PI.toFloat() / petals + layer * 0.3f
                        val m = (a0 + a1) / 2
                        val p0 = Offset(c.x + cos(a0) * radius * 0.8f, c.y + sin(a0) * radius * 0.8f)
                        val p1 = Offset(c.x + cos(a1) * radius * 0.8f, c.y + sin(a1) * radius * 0.8f)
                        val b0 = Offset(c.x + cos(m - 0.3f) * radius * 1.15f, c.y + sin(m - 0.3f) * radius * 1.15f)
                        val b1 = Offset(c.x + cos(m + 0.3f) * radius * 1.15f, c.y + sin(m + 0.3f) * radius * 1.15f)
                        val dip = Offset(c.x + cos(m) * radius * 0.98f, c.y + sin(m) * radius * 0.98f)
                        // A small notch in the middle of each petal's edge makes it look ruffled.
                        flowers.moveTo(p0.x, p0.y); flowers.quadraticTo(b0.x, b0.y, dip.x, dip.y); flowers.quadraticTo(b1.x, b1.y, p1.x, p1.y)
                        // A vein down the middle of each outer petal.
                        if (layer == 4) {
                            flowers.moveTo(c.x + cos(m) * radius * 0.45f, c.y + sin(m) * radius * 0.45f)
                            flowers.lineTo(c.x + cos(m) * radius * 0.95f, c.y + sin(m) * radius * 0.95f)
                        }
                    }
                }
                flowers.addOval(Rect(center = c, radius = h * r * 0.12f))
            }
            translate(left = line, top = line) { drawPath(flowers, Color(0xFF9A9CA0).copy(alpha = 0.55f), style = Stroke(line * 1.1f)) }
            drawPath(flowers, Color.White, style = Stroke(line * 0.9f))
        }

        else -> Unit
    }
}

/** An intricate mandala: rings of double-outlined petals, each with a centre vein, and dot rings. */
private fun DrawScope.mandala(center: Offset, radius: Float, color: Color, width: Float) {
    val path = Path()
    for (ring in 1..6) {
        val r = radius * ring / 6f
        val petals = 6 + ring * 4
        for (i in 0 until petals) {
            val a0 = i * 2 * PI.toFloat() / petals
            val a1 = (i + 1) * 2 * PI.toFloat() / petals
            val m = (a0 + a1) / 2
            for (k in listOf(1f, 0.82f)) {
                val p0 = Offset(center.x + cos(a0) * r * 0.86f, center.y + sin(a0) * r * 0.86f)
                val p1 = Offset(center.x + cos(a1) * r * 0.86f, center.y + sin(a1) * r * 0.86f)
                val tip = Offset(center.x + cos(m) * r * (0.86f + 0.30f * k), center.y + sin(m) * r * (0.86f + 0.30f * k))
                path.moveTo(p0.x, p0.y); path.quadraticTo(tip.x, tip.y, p1.x, p1.y)
            }
            path.moveTo(center.x + cos(m) * r * 0.86f, center.y + sin(m) * r * 0.86f)
            path.lineTo(center.x + cos(m) * r * 1.05f, center.y + sin(m) * r * 1.05f)
            if (ring % 2 == 0) path.addOval(Rect(center = Offset(center.x + cos(a0) * r * 0.80f, center.y + sin(a0) * r * 0.80f), radius = width * 1.2f))
        }
    }
    drawPath(path, color, style = Stroke(width))
}
