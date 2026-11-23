package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/*
 * Islami Bank's cards, each following the composition of the real card closely, in new geometry
 * and without the bank's mark. Where the art passes behind text it is drawn softer or deeper.
 */
internal fun DrawScope.drawIslami(finish: Finish, accent: Color, random: Random) {
    val w = size.width
    val h = size.height
    val line = density
    fun at(x: Float, y: Float) = Offset(w * x, h * y)

    when (finish) {
        // Green, with a hand-drawn globe on a curved stand: a sea-blue globe with paler land and
        // pencil meridians, a ringed arm on its right ending in an arrow, and a round foot.
        Finish.ISLAMI_GLOBE_STAND -> {
            val c = at(0.75f, 0.35f)
            val r = h * 0.22f
            val sketch = Color(0xFF16302A)
            val globe = Path().apply { addOval(Rect(center = c, radius = r)) }
            drawPath(globe, Brush.radialGradient(listOf(Color(0xFF6F9AAA), Color(0xFF3A6878), Color(0xFF1E4450)), c - Offset(r * 0.3f, r * 0.3f), r * 1.3f), alpha = 0.95f)
            clipPath(globe) {
                val land = Path()
                WorldMap.outlines.forEach { shape ->
                    for (i in shape.indices step 2) {
                        val lon = Math.toRadians((shape[i] + 20f).toDouble())
                        val lat = Math.toRadians(shape[i + 1].toDouble())
                        val p0 = Math.toRadians(15.0)
                        val x = cos(lat) * sin(lon)
                        val y = cos(p0) * sin(lat) - sin(p0) * cos(lat) * cos(lon)
                        val p = Offset(c.x + (x * r).toFloat(), c.y - (y * r).toFloat())
                        if (i == 0) land.moveTo(p.x, p.y) else land.lineTo(p.x, p.y)
                    }
                    land.close()
                }
                drawPath(land, Color(0xFFB8D0CC).copy(alpha = 0.55f))
                drawPath(land, sketch.copy(alpha = 0.6f), style = Stroke(line * 0.7f))
                for (k in -2..2) drawOval(sketch.copy(alpha = 0.45f), Offset(c.x - r * (0.3f + 0.25f * (k + 2) / 2f), c.y - r), Size(r * 2 * (0.3f + 0.25f * (k + 2) / 2f), r * 2), style = Stroke(line * 0.6f))
                for (k in -2..2) drawLine(sketch.copy(alpha = 0.45f), Offset(c.x - r, c.y + r * k * 0.35f), Offset(c.x + r, c.y + r * k * 0.35f), line * 0.6f)
            }
            drawPath(globe, sketch, style = Stroke(line * 1.4f))
            // The stand: a curved arm round the right of the globe with small ticks.
            val arm = Rect(center = c, radius = r * 1.22f)
            drawArc(Color(0xFF8FB8C0), -80f, 200f, false, arm.topLeft, arm.size, style = Stroke(line * 5f, cap = StrokeCap.Round))
            drawArc(sketch, -80f, 200f, false, arm.topLeft, arm.size, style = Stroke(line * 1.2f))
            for (i in 0..12) {
                val a = Math.toRadians((-80.0 + i * 200.0 / 12)).toFloat()
                drawLine(sketch, Offset(c.x + cos(a) * r * 1.16f, c.y + sin(a) * r * 1.16f), Offset(c.x + cos(a) * r * 1.28f, c.y + sin(a) * r * 1.28f), line * 0.8f)
            }
            val top = Offset(c.x + cos(Math.toRadians(-80.0).toFloat()) * r * 1.22f, c.y + sin(Math.toRadians(-80.0).toFloat()) * r * 1.22f)
            val arrow = Path().apply { moveTo(top.x, top.y - h * 0.10f); lineTo(top.x - h * 0.03f, top.y - h * 0.03f); lineTo(top.x + h * 0.03f, top.y - h * 0.03f); close() }
            drawLine(sketch, top, top - Offset(0f, h * 0.06f), line * 1.4f)
            drawPath(arrow, Color(0xFF8FB8C0)); drawPath(arrow, sketch, style = Stroke(line))
            val foot = at(0.69f, 0.80f)
            drawLine(sketch, Offset(c.x - r * 0.35f, c.y + r * 1.15f), foot, line * 2f)
            drawOval(Color(0xFF2E6A50), foot - Offset(h * 0.10f, h * 0.03f), Size(h * 0.20f, h * 0.06f))
            drawOval(sketch, foot - Offset(h * 0.10f, h * 0.03f), Size(h * 0.20f, h * 0.06f), style = Stroke(line))
        }

        // Navy, with a geometric rosette in the top left corner and a broad gold crescent ring
        // sweeping round from the top, down the middle and along the bottom. Where the ring passes
        // behind the card number it is a deeper gold, so the white digits stay readable.
        Finish.ISLAMI_CRESCENT -> {
            rosette(at(0.04f, 0.10f), h * 0.52f, listOf(Color(0xFF2F5FB0), Color(0xFFD9A860), Color(0xFFE6EEF8)), 0.85f)
            val c = at(0.80f, 0.40f)
            val r = h * 0.56f
            val ring = Rect(center = c, radius = r)
            drawArc(
                Brush.sweepGradient(
                    0f to accent.copy(alpha = 0f), 0.08f to accent, 0.35f to Color(0xFF9A7440), 0.55f to Color(0xFF9A7440), 0.75f to accent, 1f to accent,
                    center = c,
                ),
                20f, 260f, false, ring.topLeft, ring.size, style = Stroke(h * 0.13f),
            )
        }

        // White across the top, then blue: a fan of crossing curved rays from the right that
        // makes a pattern of long diamonds, pale at the left and deep at the right.
        Finish.ISLAMI_FAN -> {
            val split = h * 0.26f
            clipRect(top = split) {
                drawRect(Brush.horizontalGradient(listOf(Color(0xFFC6DCF4), Color(0xFF6E9CE0), Color(0xFF2A5CBC))))
                val focus = at(1.08f, 0.62f)
                val rays = Path()
                for (family in listOf(-1f, 1f)) {
                    for (i in 0..40) {
                        val a = (150f + i * 1.5f) * (PI.toFloat() / 180f)
                        for (s in 0..30) {
                            val t = s / 30f
                            val len = w * 1.3f * t
                            val bend = family * t * t * 0.5f
                            val p = Offset(focus.x + cos(a + bend) * len, focus.y + sin(a + bend) * len * 0.7f)
                            if (s == 0) rays.moveTo(p.x, p.y) else rays.lineTo(p.x, p.y)
                        }
                    }
                }
                drawPath(rays, Color.White.copy(alpha = 0.30f), style = Stroke(line * 0.8f))
            }
            drawRect(Color.White, Offset.Zero, Size(w, split))
        }

        // Graphite, with a khaki leaf rosette in the top left corner, a thin gold line falling
        // across the right, and a gold curve along the bottom.
        Finish.ISLAMI_GRAPHITE -> {
            rosette(at(0.04f, 0.10f), h * 0.58f, listOf(accent, accent.copy(alpha = 0.6f)), 0.75f)
            drawLine(Brush.linearGradient(listOf(Color.Transparent, Color(0xFFE8C070), Color.Transparent), at(0.62f, 0.15f), at(1f, 0.9f)), at(0.62f, 0.15f), at(1f, 0.9f), line * 1.2f)
            val curve = Path().apply { moveTo(-w * 0.02f, h * 0.84f); quadraticTo(w * 0.30f, h * 0.84f, w * 0.52f, h * 1.02f) }
            drawPath(curve, Color(0xFFE8C070), style = Stroke(line * 1.2f))
        }

        // Rich gold with a dark green leaf rosette in the top left corner and soft diagonal sheen.
        Finish.ISLAMI_GOLD -> {
            drawRect(Brush.linearGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.18f), Color.Transparent, Color.White.copy(alpha = 0.12f), Color.Transparent), at(0.2f, 0f), at(1f, 1f)))
            rosette(at(0.04f, 0.10f), h * 0.58f, listOf(accent, accent.copy(alpha = 0.7f)), 0.70f)
        }

        else -> Unit
    }
}

/**
 * A rosette of pointed leaves in two layers round [center], coloured from [colors] in turn,
 * drawn at [alpha] so text over it stays readable.
 */
private fun DrawScope.rosette(center: Offset, radius: Float, colors: List<Color>, alpha: Float) {
    val leaves = List(colors.size) { Path() }
    listOf(1f to 0f, 0.55f to 15f).forEachIndexed { layer, (scale, offset) ->
        for (i in 0 until 12) {
            val a = (i * 30f + offset) * (PI.toFloat() / 180f)
            val r0 = radius * 0.15f * scale
            val r1 = radius * scale
            val half = 0.24f
            val base1 = Offset(center.x + cos(a - half) * r0, center.y + sin(a - half) * r0)
            val base2 = Offset(center.x + cos(a + half) * r0, center.y + sin(a + half) * r0)
            val tip = Offset(center.x + cos(a) * r1, center.y + sin(a) * r1)
            val bulge1 = Offset(center.x + cos(a - half * 1.3f) * r1 * 0.75f, center.y + sin(a - half * 1.3f) * r1 * 0.75f)
            val bulge2 = Offset(center.x + cos(a + half * 1.3f) * r1 * 0.75f, center.y + sin(a + half * 1.3f) * r1 * 0.75f)
            val leaf = leaves[(i + layer) % colors.size]
            leaf.moveTo(base1.x, base1.y)
            leaf.quadraticTo(bulge1.x, bulge1.y, tip.x, tip.y)
            leaf.quadraticTo(bulge2.x, bulge2.y, base2.x, base2.y)
            leaf.close()
        }
    }
    leaves.forEachIndexed { i, path -> drawPath(path, colors[i], alpha = alpha) }
}
