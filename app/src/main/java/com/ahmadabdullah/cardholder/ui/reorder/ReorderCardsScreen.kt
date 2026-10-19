package com.ahmadabdullah.cardholder.ui.reorder

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ahmadabdullah.cardholder.data.Card
import com.ahmadabdullah.cardholder.ui.cardart.CardDesigns
import kotlin.math.roundToInt

private val RowHeight = 76.dp
private val RowGap = 8.dp

/**
 * Lets the user put the cards in the order they want them stacked in the wallet: drag a card by
 * its handle, or move it one place with the arrows. Nothing is saved until Done.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReorderCardsScreen(cards: List<Card>, onBack: () -> Unit, onDone: (List<String>) -> Unit) {
    val byId = remember(cards) { cards.associateBy { it.id } }
    var savedOrder by rememberSaveable { mutableStateOf(cards.map { it.id }) }
    val order = remember(cards) {
        (savedOrder.filter { it in byId } + cards.map { it.id }.filter { it !in savedOrder }).toMutableStateList()
    }
    fun move(from: Int, to: Int) {
        if (to !in order.indices || from == to) return
        order.add(to, order.removeAt(from))
        savedOrder = order.toList()
    }

    // The row being dragged, where it started, and how far it has been pulled, in pixels. The
    // list itself only changes when the row is let go; until then the other rows just slide
    // aside to show where it will land.
    var dragging by remember { mutableStateOf<String?>(null) }
    var dragFrom by remember { mutableIntStateOf(0) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var startY by remember { mutableFloatStateOf(0f) }
    val step = with(LocalDensity.current) { (RowHeight + RowGap).toPx() }
    // Worked out from the current state each time it's needed: the drag handler is created once,
    // so a value computed here would stay as it was when the screen opened.
    fun dropAt() = (dragFrom + (dragOffset / step).roundToInt()).coerceIn(0, (order.size - 1).coerceAtLeast(0))
    fun endDrag(drop: Boolean) {
        if (drop && dragging != null) move(dragFrom, dropAt())
        dragging = null
        dragOffset = 0f
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reorder cards") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back without saving")
                    }
                },
                actions = { TextButton(onClick = { onDone(order.toList()) }) { Text("Done") } },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState(), enabled = dragging == null)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.widthIn(max = 520.dp), verticalArrangement = Arrangement.spacedBy(RowGap)) {
                Text(
                    "Drag a card by its handle, or move it with the arrows. The top card here is the top of the wallet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                order.forEachIndexed { index, id ->
                    val card = byId[id]
                    // Keyed by card, so each row keeps its own drag handling as the list changes.
                    if (card != null) key(id) {
                    var handle by remember { mutableStateOf<LayoutCoordinates?>(null) }
                    val isDragged = dragging == id
                    // Rows between the dragged row's start and where it would land slide one place.
                    val target = when {
                        dragging == null || isDragged -> 0f
                        index in (dragFrom + 1)..dropAt() -> -step
                        index in dropAt() until dragFrom -> step
                        else -> 0f
                    }
                    val shift by animateFloatAsState(target, label = "shift")
                    CardRow(
                        card = card,
                        canMoveUp = index > 0,
                        canMoveDown = index < order.lastIndex,
                        onMoveUp = { move(index, index - 1) },
                        onMoveDown = { move(index, index + 1) },
                        // Measured on the screen, not within the row: the row moves with the finger, so
                        // positions inside it would cancel the drag out.
                        dragHandle = Modifier
                            .onGloballyPositioned { handle = it }
                            .pointerInput(id) {
                                detectDragGestures(
                                    onDragStart = { start ->
                                        dragging = id
                                        dragFrom = order.indexOf(id)
                                        dragOffset = 0f
                                        startY = handle?.localToWindow(start)?.y ?: 0f
                                    },
                                    onDragEnd = { endDrag(drop = true) },
                                    onDragCancel = { endDrag(drop = false) },
                                ) { change, _ ->
                                    change.consume()
                                    handle?.let { dragOffset = it.localToWindow(change.position).y - startY }
                                }
                            },
                        modifier = Modifier
                            .zIndex(if (isDragged) 1f else 0f)
                            .graphicsLayer {
                                translationY = if (isDragged) dragOffset else shift
                                scaleX = if (isDragged) 1.03f else 1f
                                scaleY = if (isDragged) 1.03f else 1f
                            }
                            .then(if (isDragged) Modifier.shadow(8.dp, RoundedCornerShape(12.dp)) else Modifier),
                    )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardRow(
    card: Card,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    dragHandle: Modifier,
    modifier: Modifier = Modifier,
) {
    val design = CardDesigns.forCard(card.bankId, card.network, card.tier)
    OutlinedCard(modifier.fillMaxWidth().height(RowHeight), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.fillMaxSize().padding(start = 4.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(dragHandle.size(40.dp), contentAlignment = Alignment.Center) {
                Icon(DragHandleIcon, contentDescription = "Drag to move ${card.bankName} ending ${card.lastFour}")
            }
            // A small swatch of the card's own design.
            Box(
                Modifier
                    .size(width = 46.dp, height = 29.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Brush.linearGradient(design.colors, start = Offset.Zero, end = Offset.Infinite))
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(card.bankName, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    // The digits first, so they're never the part cut off on a narrow screen.
                    "••${card.lastFour} · ${card.network.displayName} ${card.tier}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(onClick = onMoveUp, enabled = canMoveUp) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Move ${card.bankName} ending ${card.lastFour} up")
            }
            IconButton(onClick = onMoveDown, enabled = canMoveDown) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Move ${card.bankName} ending ${card.lastFour} down")
            }
        }
    }
}
