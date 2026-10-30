package com.playit.app.presentation.map

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sin

/** Pure layout math for the map trail (no Compose state). */
object MapLayout {
    /** Sine trail that uses about 60 % of the free half-width, so it stays clear of the edges on any phone. */
    fun nodeXOffset(index: Int, availableWidth: Dp, nodeSize: Dp): Dp {
        val freeHalf = (availableWidth / 2) - (nodeSize / 2) - 16.dp
        return freeHalf * 0.6f * sin(index * 0.85f)
    }
}

/** The Bohol section (1..7) on screen when the map is scrolled [scroll] of [maxScroll] px; the header takes its colours. */
fun mapSectionAt(scroll: Int, maxScroll: Int): Int =
    if (maxScroll <= 0) 1
    else ((scroll.toFloat() / maxScroll).coerceIn(0f, 1f) * 6f).toInt().plus(1).coerceIn(1, 7)
