package com.ahmadabdullah.cardholder.ui.cardart

import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/**
 * A rough map of the world's land, for globes and maps on the cards. One character per 10
 * degrees: rows run from the north pole south, columns from 180°W east. Coarse on purpose; it is
 * smoothed when read.
 */
internal object WorldMap {
    private val rows = listOf(
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

    /** How much of the area round [lat], [lon] (degrees) is land, from 0 to 1, smoothly. */
    fun land(lat: Float, lon: Float): Float {
        val fx = ((lon + 180f) % 360f + 360f) % 360f / 10f - 0.5f
        val fy = (90f - lat) / 10f - 0.5f
        fun cell(cx: Int, cy: Int) = if (rows[cy.coerceIn(0, 17)][((cx % 36) + 36) % 36] == '#') 1f else 0f
        val x0 = floor(fx).toInt()
        val y0 = floor(fy).toInt()
        val tx = fx - x0
        val ty = fy - y0
        val top = cell(x0, y0) * (1 - tx) + cell(x0 + 1, y0) * tx
        val bottom = cell(x0, y0 + 1) * (1 - tx) + cell(x0 + 1, y0 + 1) * tx
        return top * (1 - ty) + bottom * ty
    }

    /**
     * Whether the point ([u], [v], [z]) on a globe facing the viewer is land. The globe is turned
     * to show the Americas and the Atlantic, tilted a little toward the viewer.
     */
    fun onGlobe(u: Float, v: Float, z: Float): Boolean {
        val tilt = Math.toRadians(20.0).toFloat()
        val y = -v * cos(tilt) + z * sin(tilt)
        val zz = v * sin(tilt) + z * cos(tilt)
        val lat = Math.toDegrees(asin(y.coerceIn(-1f, 1f).toDouble())).toFloat()
        val lon = Math.toDegrees(atan2(u.toDouble(), zz.toDouble())).toFloat() - 55f
        return land(lat, lon) > 0.5f
    }

    /** Simple outlines of the continents and large islands, as longitude, latitude pairs. */
    val outlines: List<FloatArray> = listOf(
        // North America
        floatArrayOf(-168f, 66f, -162f, 70f, -140f, 70f, -125f, 72f, -95f, 72f, -80f, 73f, -62f, 66f, -56f, 52f, -66f, 45f, -70f, 42f, -76f, 35f, -81f, 30f, -80f, 25f, -82f, 27f, -84f, 30f, -90f, 29f, -97f, 26f, -97f, 21f, -92f, 18f, -88f, 21f, -87f, 15f, -83f, 9f, -79f, 9f, -86f, 13f, -92f, 15f, -105f, 20f, -110f, 24f, -112f, 30f, -117f, 33f, -124f, 40f, -124f, 48f, -131f, 55f, -140f, 60f, -152f, 58f, -158f, 56f, -165f, 60f),
        // Greenland
        floatArrayOf(-55f, 82f, -30f, 83f, -20f, 75f, -22f, 70f, -40f, 65f, -44f, 60f, -50f, 62f, -55f, 70f, -62f, 76f, -70f, 78f),
        // South America
        floatArrayOf(-80f, 9f, -75f, 11f, -62f, 10f, -52f, 5f, -35f, -5f, -35f, -9f, -39f, -15f, -41f, -22f, -48f, -26f, -53f, -33f, -58f, -38f, -63f, -41f, -65f, -46f, -68f, -52f, -72f, -53f, -75f, -48f, -73f, -40f, -71f, -30f, -70f, -18f, -76f, -14f, -81f, -5f, -80f, 1f, -77f, 8f),
        // Africa
        floatArrayOf(-17f, 21f, -13f, 28f, -6f, 35f, 10f, 37f, 11f, 33f, 20f, 32f, 32f, 31f, 34f, 28f, 43f, 12f, 51f, 12f, 46f, 2f, 40f, -5f, 40f, -15f, 35f, -24f, 32f, -29f, 26f, -34f, 19f, -34f, 15f, -27f, 12f, -17f, 13f, -6f, 9f, -1f, 9f, 4f, -4f, 5f, -8f, 4f, -13f, 8f, -17f, 14f),
        // Europe and Asia
        floatArrayOf(-10f, 36f, -9f, 43f, -2f, 44f, -5f, 48f, 2f, 51f, 8f, 54f, 5f, 58f, 10f, 63f, 16f, 69f, 28f, 71f, 40f, 67f, 44f, 68f, 60f, 70f, 70f, 73f, 80f, 73f, 100f, 77f, 115f, 74f, 140f, 72f, 160f, 70f, 170f, 70f, 180f, 66f, 178f, 62f, 163f, 60f, 156f, 51f, 143f, 59f, 136f, 55f, 140f, 48f, 131f, 43f, 128f, 35f, 122f, 40f, 122f, 31f, 120f, 23f, 110f, 20f, 106f, 10f, 103f, 1f, 100f, 13f, 98f, 8f, 92f, 22f, 86f, 20f, 80f, 15f, 77f, 8f, 72f, 21f, 66f, 25f, 57f, 25f, 56f, 27f, 48f, 30f, 50f, 25f, 55f, 22f, 59f, 22f, 52f, 16f, 45f, 13f, 43f, 13f, 39f, 21f, 35f, 28f, 35f, 33f, 27f, 37f, 26f, 41f, 23f, 36f, 20f, 40f, 16f, 38f, 12f, 44f, 8f, 44f, 3f, 42f, -4f, 37f),
        // Australia
        floatArrayOf(114f, -22f, 122f, -18f, 130f, -12f, 137f, -12f, 142f, -11f, 146f, -19f, 153f, -27f, 150f, -37f, 141f, -38f, 132f, -31f, 118f, -35f, 115f, -33f),
        // Britain
        floatArrayOf(-6f, 50f, 2f, 51f, -2f, 57f, -5f, 58f, -6f, 55f),
        // Borneo
        floatArrayOf(109f, 1f, 119f, 5f, 117f, -3f, 110f, -3f),
        // Sumatra
        floatArrayOf(95f, 5f, 106f, -6f, 102f, -4f),
        // Madagascar
        floatArrayOf(44f, -25f, 50f, -15f, 47f, -12f, 43f, -17f),
        // Japan
        floatArrayOf(130f, 31f, 136f, 34f, 141f, 41f, 142f, 45f, 140f, 40f, 135f, 35f),
        // New Zealand
        floatArrayOf(172f, -34f, 178f, -38f, 174f, -41f, 167f, -46f, 170f, -44f),
    )
}
