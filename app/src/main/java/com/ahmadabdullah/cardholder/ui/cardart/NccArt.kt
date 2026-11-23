package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
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
 * NCC Bank's cards, each following the composition of the real card closely, in new geometry and
 * without the bank's mark.
 */
internal fun DrawScope.drawNcc(finish: Finish, accent: Color, random: Random) {
    val w = size.width
    val h = size.height
    val line = density
    fun at(x: Float, y: Float) = Offset(w * x, h * y)
    fun sweep(path: Path, color: Color, widths: List<Pair<Float, Float>>) {
        widths.forEach { (width, alpha) -> drawPath(path, color.copy(alpha = alpha), style = Stroke(line * width)) }
    }

    when (finish) {
        // Light silver with soft sweeping curves: a bright one rising from the lower left to the
        // top right, and a grey one low across the bottom.
        Finish.NCC_SILVER_SWEEP -> {
            sweep(Path().apply { moveTo(-w * 0.05f, h * 0.62f); cubicTo(w * 0.30f, h * 0.40f, w * 0.60f, h * 0.20f, w * 1.05f, h * 0.10f) }, Color.White, listOf(70f to 0.18f, 36f to 0.25f, 12f to 0.30f))
            sweep(Path().apply { moveTo(-w * 0.05f, h * 0.78f); cubicTo(w * 0.25f, h * 0.70f, w * 0.55f, h * 0.88f, w * 0.85f, h * 1.05f) }, Color(0xFF8A8D92), listOf(40f to 0.10f, 16f to 0.14f))
            sweep(Path().apply { moveTo(-w * 0.05f, h * 0.30f); cubicTo(w * 0.15f, h * 0.20f, w * 0.30f, h * 0.10f, w * 0.40f, -h * 0.05f) }, Color(0xFF9A9DA2), listOf(24f to 0.10f))
        }

        // Bright gold above, bronze below, divided by a glowing line of light, with soft curved
        // highlights in the gold.
        Finish.NCC_DUAL_GOLD -> {
            val split = h * 0.76f
            drawRect(Brush.verticalGradient(listOf(Color(0xFFB8913E), Color(0xFF9A7632)), split, h), Offset(0f, split), Size(w, h - split))
            sweep(Path().apply { moveTo(-w * 0.05f, h * 0.55f); cubicTo(w * 0.25f, h * 0.30f, w * 0.50f, h * 0.15f, w * 0.80f, -h * 0.05f) }, Color.White, listOf(50f to 0.10f, 20f to 0.14f))
            drawRect(
                Brush.horizontalGradient(0f to Color.Transparent, 0.35f to Color(0xFFFFF2B0), 0.75f to Color(0xFFFFE07A), 1f to Color.Transparent),
                Offset(0f, split - line * 1.5f), Size(w, line * 3f),
            )
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFFFFE89A).copy(alpha = 0.45f), Color.Transparent), split - h * 0.06f, split + h * 0.06f), Offset(0f, split - h * 0.06f), Size(w, h * 0.12f))
        }

        // Grey brushed metal with a soft diagonal sheen across the lower left.
        Finish.NCC_BRUSHED -> {
            val brush = Path()
            var y = 0f
            while (y < h) {
                brush.moveTo(0f, y); brush.lineTo(w, y + line * 0.3f)
                y += line * (0.8f + random.nextFloat() * 1.6f)
            }
            drawPath(brush, Color.White.copy(alpha = 0.10f), style = Stroke(line * 0.5f))
            drawRect(Brush.linearGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.22f), Color.Transparent), at(0f, 1f), at(0.5f, 0.2f)))
        }

        // Black on the left, charcoal with a faint pattern on the right, and a huge gold mandala
        // spread over the middle.
        Finish.NCC_MANDALA -> {
            drawRect(Color(0xFF2A2A2E), at(0.55f, 0f), Size(w * 0.45f, h))
            val pattern = Path()
            var y = 0f
            while (y < h) {
                var x = w * 0.55f
                while (x < w) { pattern.addOval(Rect(center = Offset(x, y), radius = line * 3f)); x += line * 9f }
                y += line * 9f
            }
            drawPath(pattern, Color.White.copy(alpha = 0.05f), style = Stroke(line * 0.6f))
            richMandala(at(0.56f, 0.55f), h * 0.52f, accent)
        }

        else -> Unit
    }
}

/** A large, dense mandala: pointed petals in many rings, lattice between them, and dot rings. */
private fun DrawScope.richMandala(center: Offset, radius: Float, color: Color) {
    val path = Path()
    val rings = 13
    for (ring in 1..rings) {
        val r = radius * ring / rings
        val petals = 8 + ring * 3
        for (i in 0 until petals) {
            val a0 = i * 2 * PI.toFloat() / petals
            val a1 = (i + 1) * 2 * PI.toFloat() / petals
            val m = (a0 + a1) / 2
            val inner = r - radius / rings * 0.85f
            val p0 = Offset(center.x + cos(a0) * inner, center.y + sin(a0) * inner)
            val p1 = Offset(center.x + cos(a1) * inner, center.y + sin(a1) * inner)
            val tip = Offset(center.x + cos(m) * r, center.y + sin(m) * r)
            path.moveTo(p0.x, p0.y); path.quadraticTo(center.x + cos(a0) * r * 0.98f, center.y + sin(a0) * r * 0.98f, tip.x, tip.y)
            path.quadraticTo(center.x + cos(a1) * r * 0.98f, center.y + sin(a1) * r * 0.98f, p1.x, p1.y)
            // Every petal has a vein; every other ring also has a bead between petals.
            path.moveTo(center.x + cos(m) * inner, center.y + sin(m) * inner)
            path.lineTo(tip.x, tip.y)
            if (ring % 2 == 0) {
                path.addOval(Rect(center = Offset(center.x + cos(m) * (inner + r) / 2, center.y + sin(m) * (inner + r) / 2), radius = radius / rings * 0.14f))
            }
        }
        if (ring % 3 == 0) path.addOval(Rect(center = center, radius = r * 1.02f))
    }
    drawCircle(Brush.radialGradient(listOf(color.copy(alpha = 0.22f), color.copy(alpha = 0.08f), Color.Transparent), center, radius * 1.05f), radius * 1.05f, center)
    drawPath(path, color, style = Stroke(density * 0.9f))
}
