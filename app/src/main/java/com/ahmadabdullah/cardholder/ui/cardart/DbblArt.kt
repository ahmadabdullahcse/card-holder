package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/*
 * Dutch-Bangla Bank's cards, each following the composition of the real card closely, in new
 * geometry and without the bank's mark.
 */
internal fun DrawScope.drawDbbl(finish: Finish, accent: Color, random: Random) {
    val w = size.width
    val h = size.height
    val line = density
    fun at(x: Float, y: Float) = Offset(w * x, h * y)

    when (finish) {
        // Black, with a white arabesque panel on the right edged in gold scrollwork, and fine
        // gold strands waving along the bottom toward it. The panel starts right of the number.
        Finish.DBBL_ARABESQUE -> {
            // A twisting ribbon of fine gold strands along the bottom left.
            val strands = Path()
            for (k in 0..12) {
                val t = k / 12f * 2 - 1
                for (i in 0..70) {
                    val u = i / 70f
                    val mid = 0.74f + 0.07f * sin(u * 6f + 0.4f + t * 0.5f) + 0.03f * u
                    val half = 0.015f + 0.06f * kotlin.math.abs(sin(u * 3.2f + 0.9f))
                    val x = w * (-0.02f + u * 0.74f)
                    val y = h * (mid + half * t)
                    if (i == 0) strands.moveTo(x, y) else strands.lineTo(x, y)
                }
            }
            drawPath(strands, accent.copy(alpha = 0.7f), style = Stroke(line * 0.6f))

            // The medallion, laid out in the real card's proportions and drawn in miniature
            // against the right edge, so its point stays clear of the card number.
            val k = 0.68f
            val mid = 0.515f
            fun y(v: Float) = h * (mid + (v - mid) * k)
            fun p(u: Float, v: Float) = Offset(w * (1f - (1f - u) * k), y(v))
            val gold = Stroke(line * 1.9f)
            // The outline's corners on its top half, from the point at the left to the top right
            // corner, with how far each lobe between them bulges outward.
            val nodes = listOf(
                0.58f to mid, 0.62f to 0.41f, 0.665f to 0.32f, 0.79f to 0.19f, 0.88f to 0.08f, 1.03f to 0.02f,
            )
            val bulges = listOf(0.05f, 0.04f, 0.08f, 0.06f, 0.035f)
            fun outline(inset: Float): Path = Path().apply {
                val center = p(0.97f, mid)
                fun node(i: Int, mirrored: Boolean): Offset {
                    val (u, v) = nodes[i]
                    val o = p(u, if (mirrored) 2 * mid - v else v)
                    return o + (center - o) * inset
                }
                fun lobes(mirrored: Boolean, reverse: Boolean) {
                    val order = if (reverse) nodes.indices.reversed() else nodes.indices
                    var prev: Offset? = null
                    for (i in order) {
                        val o = node(i, mirrored)
                        if (prev == null) { if (isEmpty) moveTo(o.x, o.y) else lineTo(o.x, o.y) } else {
                            val d = o - prev
                            val len = d.getDistance()
                            // The outward side is to the left of the top half going up, and the
                            // right of the bottom half; flip with the direction of travel.
                            val sign = (if (mirrored) -1f else 1f) * (if (reverse) -1f else 1f)
                            val n = Offset(d.y, -d.x) / len * sign
                            val bulge = bulges[if (reverse) i else i - 1] * h * k * (1 - inset)
                            cubicTo(prev.x + d.x * 0.1f + n.x * bulge * 1.3f, prev.y + d.y * 0.1f + n.y * bulge * 1.3f,
                                prev.x + d.x * 0.9f + n.x * bulge * 1.3f, prev.y + d.y * 0.9f + n.y * bulge * 1.3f, o.x, o.y)
                        }
                        prev = o
                    }
                }
                lobes(mirrored = true, reverse = true)
                lobes(mirrored = false, reverse = false)
                lineTo(w * 1.05f, y(0.02f)); lineTo(w * 1.05f, y(1.01f)); close()
            }
            val outer = outline(0f)
            val inner = outline(0.16f)
            val top = 0.32f
            val bottom = 0.71f
            clipPath(outer) {
                drawRect(Color.White, Offset(0f, y(top)), androidx.compose.ui.geometry.Size(w, y(bottom) - y(top)))
                // Crossing gold straps on the right of the band.
                clipRect(top = y(top), bottom = y(bottom)) {
                    val straps = Path()
                    for (k in -2..4) {
                        val a = p(0.80f + k * 0.07f, top)
                        val b = p(0.92f + k * 0.07f, bottom)
                        straps.moveTo(a.x, a.y); straps.lineTo(b.x, b.y)
                        val c = p(0.92f + k * 0.07f, top)
                        val d = p(0.80f + k * 0.07f, bottom)
                        straps.moveTo(c.x, c.y); straps.lineTo(d.x, d.y)
                    }
                    clipRect(left = p(0.83f, 0f).x) { drawPath(straps, accent, style = gold) }
                }
                // A lens of two curves from the fleur to the straps.
                val lens = Path()
                for (sgn in listOf(-1f, 1f)) {
                    val a = p(0.70f, mid); val b = p(0.75f, mid + sgn * 0.10f); val c = p(0.80f, mid + sgn * 0.10f); val d = p(0.84f, mid)
                    lens.moveTo(a.x, a.y); lens.cubicTo(b.x, b.y, c.x, c.y, d.x, d.y)
                }
                drawPath(lens, accent, style = gold)
                // A three-petal fleur pointing left.
                val fleur = Path()
                val tip = p(0.635f, mid)
                val base = p(0.70f, mid)
                fleur.moveTo(base.x, base.y)
                fleur.quadraticTo(p(0.665f, mid - 0.05f).x, p(0.665f, mid - 0.05f).y, tip.x, tip.y)
                fleur.quadraticTo(p(0.665f, mid + 0.05f).x, p(0.665f, mid + 0.05f).y, base.x, base.y)
                for (sgn in listOf(-1f, 1f)) {
                    val e = p(0.655f, mid + sgn * 0.11f)
                    fleur.moveTo(base.x, base.y)
                    fleur.quadraticTo(p(0.70f, mid + sgn * 0.13f).x, p(0.70f, mid + sgn * 0.13f).y, e.x, e.y)
                }
                drawPath(fleur, accent, style = gold)
                listOf(0.74f to 0.37f, 0.86f to 0.22f).forEach { (u, v) ->
                    curl(p(u, v), h * 0.06f * k, accent, gold)
                    curl(p(u, 2 * mid - v), h * 0.06f * k, accent, gold)
                }
                drawLine(accent, Offset(0f, y(top)), Offset(w, y(top)), line * 1.6f)
                drawLine(accent, Offset(0f, y(bottom)), Offset(w, y(bottom)), line * 1.6f)
            }
            drawPath(inner, accent, style = Stroke(line * 1.4f))
            drawPath(outer, Color.Black.copy(alpha = 0.5f), style = Stroke(line * 3.6f))
            drawPath(outer, accent, style = gold)
        }

        // Black with a faint dark damask, and a soft grey sheen across the upper middle.
        Finish.DBBL_DARK_DAMASK -> {
            ogee(Color.White.copy(alpha = 0.09f), h * 0.20f)
            drawRect(Brush.verticalGradient(0.12f to Color.Transparent, 0.36f to Color.White.copy(alpha = 0.32f), 0.62f to Color.Transparent))
        }

        // Silver with a light damask, a soft sheen and a faint rainbow across the middle.
        Finish.DBBL_SILVER_DAMASK -> {
            ogee(Color(0xFF8A8D93).copy(alpha = 0.38f), h * 0.20f)
            drawRect(Brush.linearGradient(listOf(Color.Transparent, Color(0xFFF7C6D9).copy(alpha = 0.25f), Color(0xFFC6E6F7).copy(alpha = 0.25f), Color(0xFFF7EEC6).copy(alpha = 0.25f), Color.Transparent), at(0.15f, 0.30f), at(0.80f, 0.60f)))
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.35f), Color.Transparent, Color.White.copy(alpha = 0.25f))))
        }

        // Pale gold, with a great globe on the left showing Europe, Africa and Asia in deeper gold.
        Finish.DBBL_GOLD_GLOBE -> {
            val center = at(0.22f, 0.66f)
            val radius = h * 0.86f
            val globe = Path().apply { addOval(Rect(center = center, radius = radius)) }
            drawPath(globe, Brush.radialGradient(listOf(Color(0xFFF2E6A8), Color(0xFFE2CF78)), center - Offset(radius * 0.3f, radius * 0.3f), radius))
            clipPath(globe) {
                val lon0 = 60.0
                val lat0 = 25.0
                fun project(lon: Float, lat: Float): Offset {
                    val l = Math.toRadians(lon - lon0)
                    val p = Math.toRadians(lat.toDouble())
                    val p0 = Math.toRadians(lat0)
                    val x = cos(p) * sin(l)
                    val y = cos(p0) * sin(p) - sin(p0) * cos(p) * cos(l)
                    return Offset(center.x + (x * radius).toFloat(), center.y - (y * radius).toFloat())
                }
                val land = Path()
                WorldMap.outlines.forEach { shape ->
                    for (i in shape.indices step 2) {
                        val p = project(shape[i], shape[i + 1])
                        if (i == 0) land.moveTo(p.x, p.y) else land.lineTo(p.x, p.y)
                    }
                    land.close()
                }
                drawPath(land, Color(0xFFBFA03A).copy(alpha = 0.75f))
                val grid = Path()
                for (lon in -30..150 step 30) for (lat in -80..80 step 4) {
                    val p = project(lon.toFloat(), lat.toFloat())
                    if (lat == -80) grid.moveTo(p.x, p.y) else grid.lineTo(p.x, p.y)
                }
                for (lat in -60..60 step 30) for (lon in -30..150 step 4) {
                    val p = project(lon.toFloat(), lat.toFloat())
                    if (lon == -30) grid.moveTo(p.x, p.y) else grid.lineTo(p.x, p.y)
                }
                drawPath(grid, accent.copy(alpha = 0.25f), style = Stroke(line * 0.6f))
            }
            drawPath(globe, accent.copy(alpha = 0.5f), style = Stroke(line * 1.2f))
        }

        else -> Unit
    }
}

/** An ogee damask: pairs of mirrored waves meeting in pointed ovals, each holding a small flower. */
private fun DrawScope.ogee(color: Color, cell: Float) {
    val path = Path()
    val w = size.width
    val h = size.height
    var x = -cell
    var col = 0
    while (x < w + cell) {
        for (side in listOf(-1f, 1f)) {
            var y = -cell
            path.moveTo(x, y)
            while (y < h + cell) {
                val y1 = y + cell
                path.cubicTo(x + side * cell * 0.45f, y + cell * 0.25f, x + side * cell * 0.45f, y + cell * 0.75f, x, y1)
                y = y1
            }
        }
        var y = -cell / 2 + (if (col % 2 == 0) 0f else cell / 2)
        while (y < h + cell) {
            val c = Offset(x + cell / 2, y)
            for (k in 0 until 6) {
                val a = k * 60f * (Math.PI.toFloat() / 180f)
                path.addOval(Rect(center = Offset(c.x + cos(a) * cell * 0.12f, c.y + sin(a) * cell * 0.12f), radius = cell * 0.07f))
            }
            y += cell
        }
        x += cell; col++
    }
    drawPath(path, color, style = Stroke(density * 1.1f))
}

/** A gold spiral curl of [radius] round [c]. */
private fun DrawScope.curl(c: Offset, radius: Float, color: Color, stroke: Stroke) {
    val path = Path()
    for (i in 0..40) {
        val t = i / 40f
        val a = t * 3.4f * Math.PI.toFloat()
        val r = radius * (1 - t * 0.8f)
        val p = Offset(c.x + cos(a) * r, c.y + sin(a) * r)
        if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
    }
    drawPath(path, color, style = stroke)
}
