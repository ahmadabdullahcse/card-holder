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
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

/**
 * The surface of a card, drawn over its background colors. Each one echoes a look the banks'
 * own cards use, without copying any bank's artwork.
 */
enum class Finish {
    /** Just the colors. */
    PLAIN,

    /** Fine horizontal grain and a diagonal sheen, like brushed metal. */
    BRUSHED,

    /** Fine engraved rings, like the lines on a banknote. */
    GUILLOCHE,

    /** A bundle of fine lines sweeping across the card. */
    RIBBON,

    /** Soft broad folds of light, like draped silk. */
    SILK,

    /** Rotated squares scattered over the right of the card. */
    DIAMONDS,

    /** Triangular facets in shades of one color, like cut crystal or stained glass. */
    FACETS,

    /** A sphere of halftone dots. */
    DOTS,

    /** Fine rays of light bursting from one corner. */
    RAYS,

    /** The glowing rim of a planet rising from the bottom of the card. */
    GLOBE,

    /** Large soft rounded shapes in lighter shades. */
    BLOBS,

    /** A fine woven weave, like carbon fibre. */
    CARBON,

    /** Bold diagonal bands meeting in a point. */
    CHEVRON,

    /** A rosette of petals and rings, like a mandala or an arabesque. */
    ROSETTE,

    /** An eight-pointed star in the top corner. */
    STAR,

    /** A band of color down the left edge. */
    STRIPE,

    /** A patterned panel down the right side. */
    PANEL,

    /** A tall beam of light. */
    BEAM,

    /** Fine wandering veins, like marble. */
    MARBLE,
}

/**
 * How one card looks: its background colors from the top left to the bottom right, the finish
 * over them and that finish's color, and whether its writing is dark, for light cards.
 */
data class CardDesign(
    val colors: List<Color>,
    val finish: Finish = Finish.PLAIN,
    val accent: Color = Color.White,
    val darkText: Boolean = false,
) {
    val textColor: Color get() = if (darkText) Color(0xFF1E2228) else Color.White
}

/** Draws [finish] over the whole card. [seed] keeps the scattered shapes the same on every draw. */
fun DrawScope.drawFinish(finish: Finish, accent: Color, seed: Int) {
    val w = size.width
    val h = size.height
    val random = Random(seed)
    val line = density // 1dp, in pixels
    when (finish) {
        Finish.PLAIN -> Unit

        Finish.BRUSHED -> {
            var y = 0f
            while (y < h) {
                drawLine(Color.White.copy(alpha = random.nextFloat() * 0.06f), Offset(0f, y), Offset(w, y), line * 0.6f)
                y += line * 1.6f
            }
            sheen(0.22f)
        }

        Finish.GUILLOCHE -> {
            listOf(Offset(w * 1.05f, h * 0.55f), Offset(w * 0.85f, -h * 0.25f)).forEach { center ->
                var radius = h * 0.12f
                while (radius < w * 1.4f) {
                    drawCircle(accent.copy(alpha = 0.09f), radius, center, style = Stroke(line * 0.7f))
                    radius += line * 4.5f
                }
            }
            sheen(0.12f)
        }

        Finish.RIBBON -> repeat(18) { i ->
            val t = i / 17f
            // The lines fan out from a narrow neck in the middle, like a ribbon twisting.
            val path = Path().apply {
                moveTo(-w * 0.1f, h * (1.0f - t * 0.45f))
                cubicTo(w * 0.3f, h * (0.15f + t * 0.35f), w * 0.6f, h * (1.15f - t * 0.55f), w * 1.1f, h * (0.05f + t * 0.5f))
            }
            drawPath(path, accent.copy(alpha = 0.18f + 0.5f * (1 - kotlin.math.abs(t - 0.5f) * 2)), style = Stroke(line * 0.9f))
        }

        Finish.SILK -> listOf(0.15f to 0.30f, 0.5f to 0.22f, 0.85f to 0.26f).forEach { (start, width) ->
            val path = Path().apply {
                moveTo(-w * 0.1f, h * start)
                cubicTo(w * 0.3f, h * (start - 0.5f), w * 0.6f, h * (start + 0.6f), w * 1.1f, h * (start - 0.1f))
            }
            drawPath(path, accent.copy(alpha = 0.07f), style = Stroke(h * width))
            drawPath(path, accent.copy(alpha = 0.08f), style = Stroke(h * width * 0.35f))
        }

        Finish.DIAMONDS -> repeat(11) {
            val side = h * (0.12f + random.nextFloat() * 0.28f)
            val center = Offset(w * (0.45f + random.nextFloat() * 0.6f), h * random.nextFloat())
            rotate(45f, center) {
                drawRect(accent.copy(alpha = 0.12f + random.nextFloat() * 0.35f), Offset(center.x - side / 2, center.y - side / 2), Size(side, side))
            }
        }

        Finish.FACETS -> {
            val columns = 6
            val rows = 4
            val points = List(rows + 1) { r ->
                List(columns + 1) { c ->
                    val jitter = if (r in 1 until rows && c in 1 until columns) 0.35f else 0f
                    Offset(
                        w * (c + (random.nextFloat() - 0.5f) * jitter) / columns,
                        h * (r + (random.nextFloat() - 0.5f) * jitter) / rows,
                    )
                }
            }
            for (r in 0 until rows) for (c in 0 until columns) {
                val a = points[r][c]
                val b = points[r][c + 1]
                val d = points[r + 1][c]
                val e = points[r + 1][c + 1]
                listOf(listOf(a, b, e), listOf(a, e, d)).forEach { triangle ->
                    val path = Path().apply {
                        moveTo(triangle[0].x, triangle[0].y)
                        lineTo(triangle[1].x, triangle[1].y)
                        lineTo(triangle[2].x, triangle[2].y)
                        close()
                    }
                    drawPath(path, accent.copy(alpha = random.nextFloat() * 0.22f))
                    drawPath(path, accent.copy(alpha = 0.12f), style = Stroke(line * 0.6f))
                }
            }
        }

        Finish.DOTS -> {
            val center = Offset(w * 0.64f, h * 0.5f)
            val radius = h * 0.62f
            val step = line * 4f
            var y = center.y - radius
            while (y < center.y + radius) {
                var x = center.x - radius
                while (x < center.x + radius) {
                    val distance = hypot(x - center.x, y - center.y) / radius
                    if (distance < 1f) {
                        drawCircle(accent.copy(alpha = 0.25f + 0.5f * distance), line * (0.4f + 1.1f * distance), Offset(x, y))
                    }
                    x += step
                }
                y += step
            }
        }

        Finish.RAYS -> {
            val origin = Offset(w * 0.98f, h * 0.02f)
            drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = 0.45f), Color.Transparent), origin, h * 0.7f), h * 0.7f, origin)
            repeat(34) {
                val angle = Math.toRadians(95.0 + random.nextDouble() * 85.0)
                val length = w * (0.5f + random.nextFloat() * 0.8f)
                drawLine(
                    accent.copy(alpha = 0.12f + random.nextFloat() * 0.35f),
                    origin,
                    Offset(origin.x + (cos(angle) * length).toFloat(), origin.y + (sin(angle) * length).toFloat()),
                    line * (0.5f + random.nextFloat()),
                )
            }
        }

        Finish.GLOBE -> {
            val center = Offset(w * 0.78f, h * 1.32f)
            val radius = h * 0.95f
            drawCircle(Brush.radialGradient(listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent), center, radius), radius, center)
            drawCircle(
                Brush.radialGradient(
                    0.82f to Color.Transparent, 0.97f to accent.copy(alpha = 0.55f), 1.0f to accent, 1.06f to accent.copy(alpha = 0.2f), 1.18f to Color.Transparent,
                    center = center, radius = radius * 1.18f,
                ),
                radius * 1.18f, center,
            )
            for (i in 1..3) {
                drawOval(
                    accent.copy(alpha = 0.12f),
                    Offset(center.x - radius, center.y - radius * i / 4f),
                    Size(radius * 2, radius * i / 2f),
                    style = Stroke(line * 0.6f),
                )
            }
        }

        Finish.BLOBS -> repeat(4) {
            val radius = h * (0.35f + random.nextFloat() * 0.45f)
            drawCircle(accent.copy(alpha = 0.10f + random.nextFloat() * 0.16f), radius, Offset(w * (0.2f + random.nextFloat() * 0.9f), h * random.nextFloat()))
        }

        Finish.CARBON -> {
            val cell = line * 3f
            var y = 0f
            var row = 0
            while (y < h) {
                var x = if (row % 2 == 0) 0f else cell
                while (x < w) {
                    drawRect(accent.copy(alpha = 0.10f), Offset(x, y), Size(cell, cell))
                    x += cell * 2
                }
                y += cell
                row++
            }
            sheen(0.10f)
        }

        Finish.CHEVRON -> {
            val tip = Offset(w * 0.86f, h * 0.5f)
            listOf(0f to 0.30f, 0.16f to 0.14f).forEach { (shift, alpha) ->
                val path = Path().apply {
                    moveTo(w * (0.50f + shift), 0f)
                    lineTo(w * (0.64f + shift), 0f)
                    lineTo(tip.x + w * (shift + 0.14f), tip.y)
                    lineTo(w * (0.64f + shift), h)
                    lineTo(w * (0.50f + shift), h)
                    lineTo(tip.x + w * shift, tip.y)
                    close()
                }
                drawPath(path, Brush.linearGradient(listOf(accent.copy(alpha = alpha), accent.copy(alpha = alpha * 0.3f)), Offset(w * 0.5f, 0f), Offset(w, h)))
            }
            drawLine(accent.copy(alpha = 0.8f), Offset(w * 0.5f, 0f), tip, line)
            drawLine(accent.copy(alpha = 0.8f), tip, Offset(w * 0.5f, h), line)
        }

        Finish.ROSETTE -> {
            val center = Offset(w * 0.86f, h * 0.42f)
            val radius = h * 0.62f
            repeat(16) { i ->
                rotate(i * 22.5f, center) {
                    drawOval(
                        accent.copy(alpha = 0.32f),
                        Offset(center.x - radius * 0.16f, center.y - radius),
                        Size(radius * 0.32f, radius),
                        style = Stroke(line * 0.8f),
                    )
                }
            }
            listOf(0.22f, 0.4f, 1.05f, 1.12f).forEach {
                drawCircle(accent.copy(alpha = 0.3f), radius * it, center, style = Stroke(line * 0.8f))
            }
        }

        Finish.STAR -> {
            val center = Offset(w * 0.06f, h * 0.08f)
            star(center, h * 0.34f, accent.copy(alpha = 0.35f), filled = true)
            star(center, h * 0.52f, accent.copy(alpha = 0.35f), filled = false)
            star(center, h * 0.68f, accent.copy(alpha = 0.2f), filled = false)
            sheen(0.14f)
        }

        Finish.STRIPE -> {
            drawRect(accent, Offset.Zero, Size(w * 0.035f, h))
            sheen(0.2f)
        }

        Finish.PANEL -> {
            val left = w * 0.66f
            drawRect(accent.copy(alpha = 0.22f), Offset(left, 0f), Size(w - left, h))
            var x = left + line * 3
            while (x < w) {
                var y = random.nextFloat() * line * 6
                while (y < h) {
                    drawLine(accent.copy(alpha = 0.35f), Offset(x, y), Offset(x, y + line * 3), line * 0.8f)
                    y += line * (5 + random.nextFloat() * 6)
                }
                x += line * 4
            }
        }

        Finish.BEAM -> {
            val center = Offset(w * 0.66f, h * 0.5f)
            drawRect(
                Brush.horizontalGradient(
                    0f to Color.Transparent, 0.45f to accent.copy(alpha = 0.55f), 0.5f to accent, 0.55f to accent.copy(alpha = 0.55f), 1f to Color.Transparent,
                    startX = center.x - w * 0.12f, endX = center.x + w * 0.12f,
                ),
                Offset(center.x - w * 0.12f, 0f), Size(w * 0.24f, h),
            )
            drawOval(
                Brush.radialGradient(listOf(accent.copy(alpha = 0.5f), Color.Transparent), Offset(center.x, h), w * 0.25f),
                Offset(center.x - w * 0.3f, h * 0.8f), Size(w * 0.6f, h * 0.4f),
            )
        }

        Finish.MARBLE -> repeat(30) {
            val start = Offset(-w * 0.1f, h * random.nextFloat())
            val path = Path().apply {
                moveTo(start.x, start.y)
                cubicTo(
                    w * random.nextFloat(), h * random.nextFloat(),
                    w * random.nextFloat(), h * random.nextFloat(),
                    w * 1.1f, h * random.nextFloat(),
                )
            }
            drawPath(path, accent.copy(alpha = 0.03f + random.nextFloat() * 0.09f), style = Stroke(line * (0.5f + random.nextFloat() * 2.5f)))
        }
    }
}

/** A soft diagonal band of light, as on a polished surface. */
private fun DrawScope.sheen(strength: Float) {
    drawRect(
        Brush.linearGradient(
            0.25f to Color.Transparent, 0.45f to Color.White.copy(alpha = strength), 0.6f to Color.Transparent,
            start = Offset.Zero, end = Offset(size.width, size.height),
        ),
    )
}

/** An eight-pointed star: two squares, one turned by 45°. */
private fun DrawScope.star(center: Offset, radius: Float, color: Color, filled: Boolean) {
    val side = radius * 1.41f
    val rect = Rect(center.x - side / 2, center.y - side / 2, center.x + side / 2, center.y + side / 2)
    listOf(0f, 45f).forEach { angle ->
        rotate(angle, center) {
            if (filled) drawRect(color, rect.topLeft, rect.size) else drawRect(color, rect.topLeft, rect.size, style = Stroke(density))
        }
    }
}
