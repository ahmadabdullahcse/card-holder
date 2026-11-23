package com.ahmadabdullah.cardholder.ui.cardart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/*
 * Mutual Trust Bank's cards, each drawn after the look of the real card in new geometry, without
 * the bank's mark. Text sits in fixed places (see BracArt.kt); large shapes keep clear of it where
 * they can. Repeated small shapes are gathered into a few paths.
 */
internal fun DrawScope.drawMtb(finish: Finish, accent: Color, random: Random) {
    val w = size.width
    val h = size.height
    val line = density
    fun at(x: Float, y: Float) = Offset(w * x, h * y)

    when (finish) {
        // Glass shattered from one point above the middle: large shards, burgundy above and
        // glossy blue below, each lit along one side, with deep dark cracks between them.
        Finish.MTB_DIAMOND -> {
            val c = at(0.52f, 0.33f)
            val rays = 11
            val rings = listOf(0.02f, 0.30f, 0.62f, 1.7f)
            // Every corner is nudged at random, so the shards come out uneven like real broken glass.
            val corners = List(rings.size) { ring ->
                List(rays) { ray ->
                    val a = Math.toRadians((ray * 360.0 / rays) + (random.nextFloat() - 0.5f) * 20.0).toFloat()
                    val r = h * rings[ring] * (0.75f + random.nextFloat() * 0.5f)
                    Offset(c.x + cos(a) * r * 1.45f, c.y + sin(a) * r)
                }
            }
            fun point(ray: Int, ring: Int) = corners[ring][(ray + rays) % rays]
            val core = Path().apply { corners[0].forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
            val cracks = Path().apply { addPath(core) }
            val glints = Path()
            for (ring in 0 until rings.size - 1) for (ray in 0 until rays) {
                val p1 = point(ray, ring); val p2 = point(ray + 1, ring)
                val p3 = point(ray + 1, ring + 1); val p4 = point(ray, ring + 1)
                val shard = Path().apply { moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); lineTo(p4.x, p4.y); close() }
                val mid = (p1 + p3) / 2f
                val upper = mid.y < c.y + h * 0.08f
                val base = if (upper) listOf(Color(0xFF5A1E30), Color(0xFF8E3A55), Color(0xFF3A1424)) else listOf(Color(0xFF123A70), Color(0xFF2F78C8), Color(0xFF0E2A55))
                val color = base[random.nextInt(base.size)]
                drawPath(shard, Brush.linearGradient(listOf(lerp(color, Color.White, 0.30f), color, Color(0xFF07080F)), p2, p4))
                cracks.addPath(shard)
                glints.moveTo(p2.x, p2.y); glints.lineTo(p3.x, p3.y)
            }
            drawPath(cracks, Color(0xFF05060C).copy(alpha = 0.9f), style = Stroke(line * 3.2f))
            drawPath(glints, accent.copy(alpha = 0.6f), style = Stroke(line * 0.9f))
        }

        // Deep green, lighter on the left, covered in overlapping rings that make a geometric lattice.
        Finish.MTB_RINGS -> {
            val glow = at(0.25f, 0.45f)
            drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.10f), Color.Transparent), glow, w * 0.6f), w * 0.6f, glow)
            val rings = Path()
            val step = line * 12f
            var row = 0
            var y = 0f
            while (y < h + step) {
                var x = if (row % 2 == 0) 0f else step / 2
                while (x < w + step) {
                    val r = step * 0.58f
                    rings.addOval(Rect(x - r, y - r, x + r, y + r))
                    x += step
                }
                y += step * 0.866f; row++
            }
            drawPath(rings, Color.Black.copy(alpha = 0.22f), style = Stroke(line * 1.1f))
            translate(top = line * 0.8f) { drawPath(rings, accent.copy(alpha = 0.08f), style = Stroke(line * 0.6f)) }
        }

        // Black with chevrons pointing right: dark grey ones, then one of glittering gold.
        Finish.MTB_CHEVRONS -> {
            fun chevron(x: Float, tip: Float, thick: Float, brush: Brush) {
                val path = Path().apply {
                    moveTo(w * x, -h * 0.02f); lineTo(w * tip, h * 0.5f); lineTo(w * x, h * 1.02f)
                    lineTo(w * (x + thick), h * 1.02f); lineTo(w * (tip + thick), h * 0.5f); lineTo(w * (x + thick), -h * 0.02f)
                    close()
                }
                drawPath(path, brush)
            }
            chevron(0.22f, 0.46f, 0.10f, Brush.linearGradient(listOf(Color(0xFF2E2E30), Color(0xFF1A1A1B))))
            chevron(0.40f, 0.64f, 0.10f, Brush.linearGradient(listOf(Color(0xFF3A3A3C), Color(0xFF202021))))
            chevron(0.60f, 0.84f, 0.09f, Brush.linearGradient(listOf(Color(0xFF8A6420), accent, Color(0xFFF5DE9A), accent), at(0.6f, 0f), at(0.9f, 1f)))
            val glitter = Path()
            repeat(260) {
                val t = random.nextFloat()
                val down = random.nextBoolean()
                val y = h * (if (down) t * 0.5f else 0.5f + t * 0.5f)
                val k = if (down) t else 1 - t
                val x = w * (0.60f + (0.24f) * k + random.nextFloat() * 0.09f)
                val r = line * (0.4f + random.nextFloat() * 0.5f)
                glitter.addOval(Rect(x - r, y - r, x + r, y + r))
            }
            drawPath(glitter, Color(0xFFFFF4CC).copy(alpha = 0.7f))
        }

        // Pale silver with a fine lattice of diamonds and rings.
        Finish.MTB_LATTICE -> {
            val lattice = Path()
            val step = line * 16f
            var y = 0f
            var row = 0
            while (y < h + step) {
                var x = if (row % 2 == 0) 0f else step / 2
                while (x < w + step) {
                    lattice.moveTo(x, y - step / 2); lattice.lineTo(x + step / 2, y); lattice.lineTo(x, y + step / 2); lattice.lineTo(x - step / 2, y); lattice.close()
                    val r = step * 0.18f
                    lattice.addOval(Rect(x - r, y - r, x + r, y + r))
                    x += step
                }
                y += step / 2; row++
            }
            drawPath(lattice, accent.copy(alpha = 0.16f), style = Stroke(line * 0.6f))
            drawRect(Brush.linearGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.35f), Color.Transparent), at(0.2f, 0f), at(0.8f, 1f)))
        }

        // Black, with a large silvery globe on the right drawn in halftone dots, lit from the top
        // right, and ribbons of fine dots flowing along the top and right edges.
        Finish.MTB_GLOBE -> {
            val ribbons = Path()
            repeat(3) { k ->
                for (s in 0..80) {
                    val u = s / 80f
                    listOf(
                        Offset(w * (-0.05f + u * 0.55f), h * (0.02f + 0.08f * k + 0.10f * sin(u * 5f + k))),
                        Offset(w * (0.97f + 0.03f * sin(u * 6f + k)), h * (u * 1.05f)),
                    ).forEach { p ->
                        val r = line * 0.45f
                        ribbons.addOval(Rect(p.x - r, p.y - r, p.x + r, p.y + r))
                    }
                }
            }
            drawPath(ribbons, accent.copy(alpha = 0.40f))
            val center = at(0.81f, 0.50f)
            val radius = h * 0.37f
            drawCircle(Brush.radialGradient(listOf(Color(0xFF1E2A3A), Color(0xFF05070B)), center + Offset(radius * 0.4f, -radius * 0.4f), radius * 1.4f), radius, center)
            val bands = List(3) { Path() }
            val step = line * 2.2f
            var y = center.y - radius
            while (y < center.y + radius) {
                var x = center.x - radius
                while (x < center.x + radius) {
                    val u = (x - center.x) / radius
                    val v = (y - center.y) / radius
                    val r2 = u * u + v * v
                    if (r2 < 1f && isLand(u, v, sqrt(1f - r2))) {
                        val lit = ((u - v) * 0.4f + 0.55f) * sqrt(1f - r2)
                        val band = (lit * 3f).toInt().coerceIn(0, 2)
                        val dot = line * (0.45f + band * 0.3f)
                        bands[band].addOval(Rect(x - dot, y - dot, x + dot, y + dot))
                    }
                    x += step
                }
                y += step
            }
            bands.forEachIndexed { i, path -> drawPath(path, lerp(accent, Color.White, i * 0.5f).copy(alpha = 0.35f + i * 0.3f)) }
            drawCircle(
                Brush.radialGradient(0.9f to Color.Transparent, 1f to accent.copy(alpha = 0.55f), 1.05f to Color.Transparent, center = center, radius = radius * 1.05f),
                radius * 1.05f, center,
            )
        }

        // White, with a grey honeycomb on the right behind a broad, shiny silver arrow pointing
        // right. The arrow's point sits just right of the card number.
        Finish.MTB_HONEYCOMB -> {
            val top = 0.59f
            val tip = 0.79f
            val zone = Path().apply {
                moveTo(w * top, 0f); lineTo(w * tip, h * 0.5f); lineTo(w * top, h); lineTo(w, h); lineTo(w, 0f); close()
            }
            clipPath(zone) {
                drawRect(Color(0xFFDFE0E5))
                val hex = Path()
                val r = line * 6.5f
                var row = 0
                var y = 0f
                while (y < h + r) {
                    var x = w * 0.5f + if (row % 2 == 0) 0f else r * 0.866f
                    while (x < w + r) {
                        for (k in 0..6) {
                            val a = (k * 60f + 30f) * (Math.PI.toFloat() / 180f)
                            val p = Offset(x + cos(a) * r, y + sin(a) * r)
                            if (k == 0) hex.moveTo(p.x, p.y) else hex.lineTo(p.x, p.y)
                        }
                        x += r * 1.732f
                    }
                    y += r * 1.5f; row++
                }
                drawPath(hex, Color(0xFFB3B6BE), style = Stroke(line * 1.1f))
                translate(left = line * 0.8f, top = line * 0.8f) { drawPath(hex, Color.White.copy(alpha = 0.8f), style = Stroke(line * 0.8f)) }
            }
            val band = w * 0.035f
            val arrow = Path().apply {
                moveTo(w * top - band, 0f); lineTo(w * tip - band, h * 0.5f); lineTo(w * top - band, h)
                lineTo(w * top, h); lineTo(w * tip, h * 0.5f); lineTo(w * top, 0f); close()
            }
            drawPath(arrow, Brush.linearGradient(listOf(Color(0xFF8E929A), Color(0xFFFFFFFF), Color(0xFFB9BCC3), Color(0xFFF7F8FA), Color(0xFF80848C)), at(top - 0.04f, 0f), at(top, 0.08f)))
            drawPath(arrow, Color(0xFF7C8088), style = Stroke(line * 0.6f))
        }

        // Black: a fan of fine lines in the top left corner, two long faint arcs, two bright
        // looping strokes on the right, and a ripple of rings curling round the expiry date.
        Finish.MTB_ARCS -> {
            fun path(vararg p: Float) = Path().apply {
                moveTo(w * p[0], h * p[1])
                var i = 2
                while (i < p.size) { cubicTo(w * p[i], h * p[i + 1], w * p[i + 2], h * p[i + 3], w * p[i + 4], h * p[i + 5]); i += 6 }
            }
            val corner = Path()
            for (i in 0..11) {
                val t = i / 11f
                corner.moveTo(w * (0.03f + t * 0.28f), -h * 0.02f)
                corner.quadraticTo(w * (0.01f + t * 0.10f), h * (0.05f + t * 0.15f), -w * 0.01f, h * (0.04f + t * 0.42f))
            }
            drawPath(corner, accent.copy(alpha = 0.40f), style = Stroke(line * 0.7f))
            val faint = Path().apply {
                addPath(path(-0.02f, 0.66f, 0.25f, 0.38f, 0.50f, 0.20f, 0.74f, 0.22f))
                addPath(path(-0.02f, 0.78f, 0.28f, 0.50f, 0.55f, 0.32f, 1.02f, 0.30f))
            }
            drawPath(faint, accent.copy(alpha = 0.30f), style = Stroke(line * 0.8f))
            // A rounded bracket rising to the upper middle, then the two bright loops.
            drawPath(path(0.04f, 0.66f, 0.25f, 0.40f, 0.45f, 0.24f, 0.70f, 0.24f, 0.74f, 0.24f, 0.74f, 0.30f, 0.72f, 0.46f), accent.copy(alpha = 0.75f), style = Stroke(line))
            val bright = Path().apply {
                addPath(path(0.40f, 0.76f, 0.55f, 0.58f, 0.80f, 0.43f, 1.02f, 0.42f))
                addPath(path(1.02f, 0.40f, 0.80f, 0.44f, 0.62f, 0.60f, 0.545f, 0.80f, 0.53f, 0.87f, 0.575f, 0.87f, 0.60f, 0.80f, 0.64f, 0.70f, 0.70f, 0.56f, 0.80f, 0.48f))
            }
            drawPath(bright, Color.White.copy(alpha = 0.18f), style = Stroke(line * 3f))
            drawPath(bright, Color.White.copy(alpha = 0.85f), style = Stroke(line * 1.1f))
            val ripple = Path()
            for (k in 1..7) {
                val r = h * (0.035f + 0.03f * k)
                ripple.addArc(Rect(center = at(0.37f, 0.74f), radius = r), 85f, 125f)
            }
            drawPath(ripple, accent.copy(alpha = 0.60f), style = Stroke(line * 0.8f))
        }

        // Navy, with a web of linked points thickest toward the right.
        Finish.MTB_NETWORK -> {
            val nodes = List(40) { at(0.25f + random.nextFloat() * 0.8f, random.nextFloat()) }
            val links = Path()
            nodes.forEachIndexed { i, p ->
                nodes.drop(i + 1).sortedBy { (it - p).getDistance() }.take(3).forEach { q -> links.moveTo(p.x, p.y); links.lineTo(q.x, q.y) }
            }
            drawPath(links, accent.copy(alpha = 0.28f), style = Stroke(line * 0.7f))
            nodes.forEach { drawCircle(Color.White.copy(alpha = 0.6f), line * 1.3f, it) }
        }

        else -> Unit
    }
}

/**
 * A rough map of the world's land, one character per 10 degrees: rows run from the north pole
 * south, columns from 180°W east. Coarse on purpose; it is smoothed when read.
 */
private val worldMap = listOf(
    "....................................",
    "......######.###...#.....#####......",
    ".#########.#.###...################.",
    ".....########....#####.############.",
    ".....#######.....######.########.#..",
    "......#####......#..########.####...",
    ".......###......#########.#####.....",
    "........##......#######..##.###.....",
    "..........####...######.....###.....",
    "..........#####.....####....#####...",
    "...........####.....###.#......###..",
    "...........###......###......#####..",
    "...........##..................###.#",
    "...........##.......................",
    "...........#........................",
    "....................................",
    "####################################",
    "####################################",
)

/**
 * Whether the point ([u], [v], [z]) on a globe facing the viewer is land. The globe is turned to
 * show the Americas and the Atlantic, tilted a little toward the viewer.
 */
private fun isLand(u: Float, v: Float, z: Float): Boolean {
    val tilt = Math.toRadians(20.0).toFloat()
    // Undo the tilt about the horizontal axis, then read latitude and longitude.
    val y = -v * cos(tilt) + z * sin(tilt)
    val zz = v * sin(tilt) + z * cos(tilt)
    val lat = Math.toDegrees(asin(y.coerceIn(-1f, 1f).toDouble())).toFloat()
    val lon = Math.toDegrees(atan2(u.toDouble(), zz.toDouble())).toFloat() - 55f
    // Smooth the coarse map by blending the four nearest cells.
    val fx = ((lon + 180f + 360f) % 360f) / 10f - 0.5f
    val fy = (90f - lat) / 10f - 0.5f
    fun cell(cx: Int, cy: Int) = if (worldMap[cy.coerceIn(0, 17)][((cx % 36) + 36) % 36] == '#') 1f else 0f
    val x0 = floor(fx).toInt()
    val y0 = floor(fy).toInt()
    val tx = fx - x0
    val ty = fy - y0
    val top = cell(x0, y0) * (1 - tx) + cell(x0 + 1, y0) * tx
    val bottom = cell(x0, y0 + 1) * (1 - tx) + cell(x0 + 1, y0 + 1) * tx
    return top * (1 - ty) + bottom * ty > 0.5f
}
