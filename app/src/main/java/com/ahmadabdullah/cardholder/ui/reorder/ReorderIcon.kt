package com.ahmadabdullah.cardholder.ui.reorder

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** An arrow up beside an arrow down: "change the order". The core icon set has no such icon. */
val ReorderIcon: ImageVector = ImageVector.Builder("Reorder", 24.dp, 24.dp, 24f, 24f).apply {
    path(fill = SolidColor(Color.Black)) {
        // Up arrow, on the left
        moveTo(9f, 3f); lineTo(5f, 7f); lineTo(8f, 7f); lineTo(8f, 14f); lineTo(10f, 14f); lineTo(10f, 7f); lineTo(13f, 7f); close()
        // Down arrow, on the right
        moveTo(15f, 21f); lineTo(19f, 17f); lineTo(16f, 17f); lineTo(16f, 10f); lineTo(14f, 10f); lineTo(14f, 17f); lineTo(11f, 17f); close()
    }
}.build()

/** Six dots, the usual handle for dragging a row. */
val DragHandleIcon: ImageVector = ImageVector.Builder("DragHandle", 24.dp, 24.dp, 24f, 24f).apply {
    path(fill = SolidColor(Color.Black)) {
        listOf(9f, 15f).forEach { x ->
            listOf(6f, 12f, 18f).forEach { y ->
                moveTo(x + 1.6f, y)
                arcTo(1.6f, 1.6f, 0f, true, true, x - 1.6f, y)
                arcTo(1.6f, 1.6f, 0f, true, true, x + 1.6f, y)
                close()
            }
        }
    }
}.build()
