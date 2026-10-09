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
