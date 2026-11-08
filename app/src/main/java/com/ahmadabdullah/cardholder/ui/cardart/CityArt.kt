package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/*
 * City Bank's cards, each drawn after the look of the real card in new geometry. The Amex cards
 * share an engraved medallion, as the real ones do, but no portrait or Amex artwork is drawn.
 * Fine lines are gathered into a few paths, so each card draws in a handful of calls.
 */
internal fun DrawScope.drawCity(finish: Finish, accent: Color, random: Random) {
    val w = size.width
    val h = size.height
    val line = density

    when (finish) {
        // Engraved metal: fine wavy lines across the card, and a large oval medallion on the
        // left with a hatched rim and a rosette of rings inside.
        Finish.CITY_AMEX -> {
            val center = Offset(w * 0.27f, h * 0.50f)
            val rx = w * 0.30f
            val ry = h * 0.66f
            fun inside(p: Offset) = ((p.x - center.x) / rx).let { it * it } + ((p.y - center.y) / ry).let { it * it } < 1.08f

            val engraving = Path()
            var y = -h * 0.05f
            while (y < h * 1.05f) {
                var drawing = false
                var x = 0f
                while (x <= w) {
                    val p = Offset(x, y + sin(x / w * 9f + y / h * 4f) * h * 0.015f)
                    if (inside(p)) drawing = false
                    else if (drawing) engraving.lineTo(p.x, p.y)
                    else { engraving.moveTo(p.x, p.y); drawing = true }
                    x += line * 3f
                }
                y += line * 2.6f
            }
            drawPath(engraving, accent.copy(alpha = 0.14f), style = Stroke(line * 0.5f))

            val rosette = Path()
            for (ring in 1..26) {
                val k = ring / 26f
                for (step in 0..120) {
                    val a = step / 120f * 2 * PI.toFloat()
                    val wobble = 1f + 0.035f * sin(a * 14f + ring * 0.6f)
                    val p = Offset(center.x + cos(a) * rx * k * wobble, center.y + sin(a) * ry * k * wobble)
                    if (step == 0) rosette.moveTo(p.x, p.y) else rosette.lineTo(p.x, p.y)
                }
            }
            drawCircle(
                Brush.radialGradient(listOf(Color.White.copy(alpha = 0.22f), Color.Transparent, Color.Black.copy(alpha = 0.12f)), center, ry),
                ry, center,
            )
            drawPath(rosette, accent.copy(alpha = 0.18f), style = Stroke(line * 0.5f))

            val hatch = Path()
            repeat(220) { i ->
                val a = i / 220f * 2 * PI.toFloat()
                hatch.moveTo(center.x + cos(a) * rx, center.y + sin(a) * ry)
                hatch.lineTo(center.x + cos(a) * rx * 1.10f, center.y + sin(a) * ry * 1.10f)
            }
            drawPath(hatch, accent.copy(alpha = 0.38f), style = Stroke(line * 0.6f))
            val rim = Rect(center.x - rx, center.y - ry, center.x + rx, center.y + ry)
            drawOval(Color.Black.copy(alpha = 0.30f), rim.topLeft + Offset(0f, line), rim.size, style = Stroke(line * 2.2f))
            drawOval(Color.White.copy(alpha = 0.55f), rim.topLeft, rim.size, style = Stroke(line * 1.2f))
            val outer = rim.inflate(rx * 0.10f)
            drawOval(Color.White.copy(alpha = 0.40f), outer.topLeft, outer.size, style = Stroke(line))
            // A soft sheen across the metal.
            drawRect(Brush.linearGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.10f), Color.Transparent), Offset(w * 0.3f, 0f), Offset(w * 0.8f, h)))
        }

        // City Islamic: a tall fan of fine lines gathering to a point low on the card.
        Finish.CITY_ISLAMIC_GOLD -> islamicFan(Offset(w * 0.40f, h * 1.04f), accent, 0.30f)
        Finish.CITY_ISLAMIC_BLUE -> islamicFan(Offset(w * 0.30f, h * 1.04f), accent, 0.30f)

        // Black mineral: grainy bands swirling across the card.
        Finish.CITY_MINERAL -> {
            val bands = Path()
            repeat(70) { i ->
                val base = h * (-0.2f + i / 70f * 1.4f)
                val phase = random.nextFloat() * 6f
                var x = -w * 0.05f
                bands.moveTo(x, base)
                while (x <= w * 1.05f) {
                    val u = x / w
                    bands.lineTo(x, base + h * (0.12f * sin(u * 5f + i * 0.08f) + 0.05f * sin(u * 13f + phase) + 0.18f * sin(u * 2.2f + i * 0.04f)))
                    x += line * 4f
                }
            }
            drawPath(bands, Color.White.copy(alpha = 0.07f), style = Stroke(line * 7f))
            drawPath(bands, Color.White.copy(alpha = 0.16f), style = Stroke(line * 0.9f))
            val grain = Path()
            repeat(2400) {
                val x = w * random.nextFloat()
                val y = h * random.nextFloat()
                val r = line * (0.3f + random.nextFloat() * 0.6f)
                grain.addOval(Rect(x - r, y - r, x + r, y + r))
            }
            drawPath(grain, accent.copy(alpha = 0.32f))
        }

        // An upright black card with a twisting band of fine dotted lines across its upper
        // middle, above the chip and the number.
        Finish.CITY_INFINITE_WAVE -> {
            val dots = Path()
            repeat(18) { i ->
                val t = i / 17f
                for (s in 0..54) {
                    // Each dot is nudged a little at random, so they don't fall into columns.
                    val u = (s + random.nextFloat()) / 54f
                    val x = w * (-0.05f + u * 1.10f)
                    val y = h * (0.16f + t * 0.17f + 0.07f * sin(u * 5f + t * 2.2f) * (1.2f - t))
                    val r = line * 0.55f
                    dots.addOval(Rect(x - r, y - r, x + r, y + r))
                }
            }
            drawPath(dots, accent.copy(alpha = 0.50f))
        }

        // Satin black: broad soft folds catching the light.
        Finish.CITY_SATIN -> listOf(
            Triple(0.62f, 0.12f, 0.30f), Triple(0.84f, -0.10f, 0.22f), Triple(0.25f, 0.30f, 0.14f),
        ).forEach { (x, bend, strength) ->
            val fold = Path().apply {
                moveTo(w * (x - 0.10f), -h * 0.05f)
                cubicTo(w * (x + bend), h * 0.30f, w * (x - bend), h * 0.70f, w * (x + 0.12f), h * 1.05f)
            }
            for (k in 6 downTo 1) drawPath(fold, accent.copy(alpha = strength / 6f), style = Stroke(line * k * 9f))
            drawPath(fold, Color.Black.copy(alpha = 0.35f), style = Stroke(line * 4f))
        }

        else -> Unit
    }
}

private fun DrawScope.islamicFan(focus: Offset, accent: Color, alpha: Float) {
    val w = size.width
    val h = size.height
    val fan = Path()
    repeat(36) { i ->
        val t = i / 35f
        val top = Offset(w * (-0.3f + t * 1.6f), -h * 0.05f)
        fan.moveTo(focus.x, focus.y)
        fan.quadraticTo(focus.x + (top.x - focus.x) * 0.15f, h * 0.35f, top.x, top.y)
    }
    drawPath(fan, accent.copy(alpha = alpha), style = Stroke(density * 0.7f))
    drawRect(
        Brush.horizontalGradient(
            0f to Color.Transparent, 0.5f to Color.White.copy(alpha = 0.16f), 1f to Color.Transparent,
            startX = focus.x - w * 0.12f, endX = focus.x + w * 0.12f,
        ),
    )
}
