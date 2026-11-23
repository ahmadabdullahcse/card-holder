package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/*
 * United Commercial Bank's cards, each following the composition of the real card closely, in
 * new geometry and without the bank's mark. Repeated small shapes are gathered into a few paths.
 */
internal fun DrawScope.drawUcb(finish: Finish, accent: Color, random: Random) {
    val w = size.width
    val h = size.height
    val line = density
    fun at(x: Float, y: Float) = Offset(w * x, h * y)

    when (finish) {
        // Chrome sheet metal: pale at the top with a bright band across the middle, falling to
        // glossy black at the lower left. A darker fold dips down at the top left, and a silver
        // fold cuts across the lower right corner.
        Finish.UCB_CHROME -> {
            drawRect(
                Brush.verticalGradient(
                    0f to Color(0xFFD4D4D6), 0.30f to Color(0xFFEDEDEF), 0.42f to Color.White,
                    0.52f to Color(0xFF9A9A9E), 0.64f to Color(0xFF3A3A3E), 0.80f to Color(0xFF0A0A0C), 1f to Color.Black,
                ),
            )
            // The black falls away toward the right, where the metal turns silver again.
            drawRect(Brush.horizontalGradient(0.45f to Color.Transparent, 1f to Color(0xFFB8B9BD).copy(alpha = 0.55f)))
            val topFold = Path().apply {
                moveTo(w * 0.22f, 0f); lineTo(w * 0.58f, 0f)
                lineTo(w * 0.42f, h * 0.24f)
                quadraticTo(w * 0.38f, h * 0.30f, w * 0.32f, h * 0.29f)
                quadraticTo(w * 0.24f, h * 0.27f, w * 0.23f, h * 0.12f)
                close()
            }
            drawPath(topFold, Brush.linearGradient(listOf(Color(0xFF8C8D91), Color(0xFFC9CACD), Color(0xFF9FA0A4)), at(0.22f, 0f), at(0.5f, 0.3f)))
            val cornerFold = Path().apply {
                moveTo(w * 1.01f, h * 0.40f)
                lineTo(w * 0.72f, h * 0.86f)
                quadraticTo(w * 0.68f, h * 0.92f, w * 0.62f, h * 0.90f)
                lineTo(w * 0.58f, h * 1.01f); lineTo(w * 1.01f, h * 1.01f); close()
            }
            drawPath(cornerFold, Brush.linearGradient(listOf(Color(0xFFF0F0F2), Color(0xFFA7A8AC), Color(0xFF6A6B70)), at(1f, 0.45f), at(0.62f, 1f)))
        }

        // Gold in three bands: brushed gold above and below, and a brighter middle band covered
        // in a damask pattern.
        Finish.UCB_DAMASK -> {
            val top = h * 0.30f
            val bottom = h * 0.79f
            fun brushed(y0: Float, y1: Float) {
                drawRect(Brush.horizontalGradient(listOf(Color(0xFFC89A3E), Color(0xFFD9AE55), Color(0xFF9C8436))), Offset(0f, y0), Size(w, y1 - y0))
                val grain = Path()
                var x = 0f
                while (x < w) {
                    grain.moveTo(x, y0); grain.lineTo(x + line * 0.5f, y1)
                    x += line * (1f + random.nextFloat() * 1.5f)
                }
                drawPath(grain, Color(0xFF5E4410).copy(alpha = 0.18f), style = Stroke(line * 0.6f))
            }
            brushed(0f, top)
            brushed(bottom, h)
            drawRect(Brush.horizontalGradient(listOf(Color(0xFFE9B83A), Color(0xFFF5CF52), Color(0xFFC9962A))), Offset(0f, top), Size(w, bottom - top))
            // Damask: rows of mirrored leaf-and-flower motifs.
            clipRect(top = top, bottom = bottom) {
                val motif = Path()
                val stepX = h * 0.12f
                val stepY = h * 0.115f
                var row = 0
                var cy = top - stepY / 2
                while (cy < bottom + stepY) {
                    var cx = if (row % 2 == 0) 0f else stepX / 2
                    while (cx < w + stepX) {
                        damask(motif, Offset(cx, cy), stepY * 0.5f)
                        cx += stepX
                    }
                    cy += stepY; row++
                }
                drawPath(motif, Color(0xFFA9711A).copy(alpha = 0.20f))
            }
            drawLine(Color(0xFF8A6A1E).copy(alpha = 0.5f), Offset(0f, top), Offset(w, top), line)
            drawLine(Color(0xFF8A6A1E).copy(alpha = 0.5f), Offset(0f, bottom), Offset(w, bottom), line)
        }

        // A huge ornamental half rosette rising from the bottom edge, in dark lines on black.
        Finish.UCB_MANDALA -> rosette(at(0.50f, 1.18f), h * 1.0f, accent)

        // The same rosette on grey carbon weave.
        Finish.UCB_MANDALA_CARBON -> {
            val weave = Path()
            var d = -h
            while (d < w) {
                weave.moveTo(d, 0f); weave.lineTo(d + h, h)
                d += line * 2.4f
            }
            drawPath(weave, Color.Black.copy(alpha = 0.30f), style = Stroke(line * 0.9f))
            rosette(at(0.50f, 1.18f), h * 1.0f, accent)
        }

        // Deep blue with a big round globe made of fine coloured dots, brightest at its rim.
        Finish.UCB_DOT_GLOBE -> {
            val center = at(0.52f, 0.54f)
            val radius = h * 0.46f
            val palettes = listOf(Color(0xFF6FB6F2), Color(0xFF2E7BD0), Color(0xFFBFE4FF), Color(0xFFE8C766))
            val groups = List(palettes.size) { Path() }
            val step = line * 1.5f
            var y = center.y - radius
            while (y < center.y + radius) {
                var x = center.x - radius
                while (x < center.x + radius) {
                    val u = (x - center.x) / radius
                    val v = (y - center.y) / radius
                    val r2 = u * u + v * v
                    // Denser toward the rim, sparser in the middle.
                    if (r2 < 1f && random.nextFloat() < 0.40f + 0.55f * r2) {
                        val pick = random.nextFloat()
                        val g = when { pick < 0.06f -> 3; pick < 0.25f -> 2; pick < 0.6f -> 0; else -> 1 }
                        val d = line * 0.6f
                        groups[g].addRect(Rect(x - d, y - d * 0.6f, x + d, y + d * 0.6f))
                    }
                    x += step
                }
                y += step
            }
            groups.forEachIndexed { i, path -> drawPath(path, palettes[i]) }
            drawCircle(Brush.radialGradient(listOf(Color(0xFF0E3470).copy(alpha = 0.30f), Color.Transparent), center, radius * 0.6f), radius * 0.6f, center)
        }

        // Black with a grey map of the world, and a gold silk ribbon flowing across it.
        Finish.UCB_WORLD_RIBBON -> {
            // The continents' outlines, laid flat across the card.
            val land = Path()
            WorldMap.outlines.forEach { shape ->
                for (i in shape.indices step 2) {
                    val x = w * (0.04f + (shape[i] + 170f) / 360f * 0.94f)
                    val y = h * (0.05f + (82f - shape[i + 1]) / 150f * 0.82f)
                    if (i == 0) land.moveTo(x, y) else land.lineTo(x, y)
                }
                land.close()
            }
            drawPath(land, Brush.linearGradient(listOf(Color(0xFF8E8E92), Color(0xFF4A4A4E), Color(0xFF9A9A9E)), at(0f, 0f), at(1f, 1f)), alpha = 0.75f)
            // The ribbon: many close strands following one flowing line, its width swelling and
            // narrowing so it seems to twist, drawn as a soft glow, then fine bright threads.
            listOf(0f to 1f, 1.9f to 0.6f).forEach { (phase, strength) ->
                fun mid(u: Float) = h * (0.50f + 0.12f * sin(u * 5.0f - 0.8f + phase) - 0.10f * u)
                fun width(u: Float) = h * (0.04f + 0.08f * (0.5f + 0.5f * sin(u * 6.5f + 1.2f + phase)))
                val band = Path().apply {
                    for (i in 0..60) { val u = i / 60f; val x = w * (-0.02f + u * 1.04f); if (i == 0) moveTo(x, mid(u) - width(u)) else lineTo(x, mid(u) - width(u)) }
                    for (i in 60 downTo 0) { val u = i / 60f; lineTo(w * (-0.02f + u * 1.04f), mid(u) + width(u)) }
                    close()
                }
                drawPath(band, Brush.verticalGradient(listOf(accent.copy(alpha = 0.25f * strength), accent.copy(alpha = 0.65f * strength), accent.copy(alpha = 0.25f * strength)), h * 0.3f, h * 0.75f))
                val strands = Path()
                for (k in 0..14) {
                    val t = k / 14f * 2 - 1
                    for (i in 0..80) {
                        val u = i / 80f
                        val p = Offset(w * (-0.02f + u * 1.04f), mid(u) + width(u) * t)
                        if (i == 0) strands.moveTo(p.x, p.y) else strands.lineTo(p.x, p.y)
                    }
                }
                drawPath(strands, Color(0xFFFFE6A0).copy(alpha = 0.55f * strength), style = Stroke(line * 0.6f))
            }
        }

        else -> Unit
    }
}

/** A half rosette: rings, a ring of arches, lotus petals and fine rays, centred below the card. */
private fun DrawScope.rosette(center: Offset, radius: Float, color: Color) {
    val ornament = Path()
    fun ring(r: Float) = ornament.addOval(Rect(center = center, radius = r))
    listOf(1.0f, 0.96f, 0.80f, 0.77f, 0.46f, 0.43f, 0.22f).forEach { ring(radius * it) }
    // Small arches between the two outer rings.
    for (i in 0 until 72) {
        val a = Math.toRadians(180.0 + i * 2.5).toFloat()
        val r = radius * 0.96f
        val p = Offset(center.x + cos(a) * r, center.y + sin(a) * r)
        ornament.addOval(Rect(center = p, radius = radius * 0.028f))
    }
    // Lotus petals between the middle rings.
    for (i in 0 until 18) {
        val a = 180f + i * 10f + 5f
        val base = radius * 0.46f
        val tip = radius * 0.77f
        val half = 4.5f
        fun polar(r: Float, deg: Float): Offset {
            val rad = Math.toRadians(deg.toDouble()).toFloat()
            return Offset(center.x + cos(rad) * r, center.y + sin(rad) * r)
        }
        val left = polar(base, a - half); val right = polar(base, a + half); val top = polar(tip, a)
        val c1 = polar(radius * 0.66f, a - half * 1.3f); val c2 = polar(radius * 0.66f, a + half * 1.3f)
        ornament.moveTo(left.x, left.y)
        ornament.quadraticTo(c1.x, c1.y, top.x, top.y)
        ornament.quadraticTo(c2.x, c2.y, right.x, right.y)
        val inner = polar(radius * 0.62f, a)
        ornament.moveTo(polar(base, a).x, polar(base, a).y); ornament.lineTo(inner.x, inner.y)
    }
    // Fine rays in the centre.
    for (i in 0 until 48) {
        val a = Math.toRadians(180.0 + i * 3.75).toFloat()
        ornament.moveTo(center.x + cos(a) * radius * 0.22f, center.y + sin(a) * radius * 0.22f)
        ornament.lineTo(center.x + cos(a) * radius * 0.43f, center.y + sin(a) * radius * 0.43f)
    }
    drawPath(ornament, Color.Black.copy(alpha = 0.6f), style = Stroke(density * 3.2f))
    drawPath(ornament, color.copy(alpha = 0.85f), style = Stroke(density * 1.3f))
}

/** One damask motif: a mirrored pair of curling leaves round a small flower, inside [size]. */
private fun damask(path: Path, c: Offset, size: Float) {
    for (side in listOf(-1f, 1f)) {
        path.moveTo(c.x, c.y + size * 0.9f)
        path.cubicTo(c.x + side * size * 0.9f, c.y + size * 0.5f, c.x + side * size * 0.7f, c.y - size * 0.6f, c.x + side * size * 0.15f, c.y - size * 0.2f)
        path.cubicTo(c.x + side * size * 0.45f, c.y - size * 0.1f, c.x + side * size * 0.35f, c.y + size * 0.5f, c.x, c.y + size * 0.9f)
    }
    path.addOval(Rect(center = Offset(c.x, c.y - size * 0.55f), radius = size * 0.18f))
    path.moveTo(c.x, c.y - size * 1.0f); path.lineTo(c.x - size * 0.12f, c.y - size * 0.75f); path.lineTo(c.x + size * 0.12f, c.y - size * 0.75f); path.close()
}
