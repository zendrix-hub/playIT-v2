package com.playit.app.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.PlayItMotion
import kotlinx.coroutines.delay

/** One frame of a sticker being placed: its opacity, its size (1 = full) and its tilt in degrees. */
data class StickerFrame(val alpha: Float, val scale: Float, val rotation: Float)

/** The sticker at [progress]: 0 is just lifted (faint, 88 % size, tilted 6 degrees left), 1 is placed. */
fun stickerAt(progress: Float, reduced: Boolean): StickerFrame =
    if (reduced) StickerFrame(alpha = progress.coerceIn(0f, 1f), scale = 1f, rotation = 0f)
    else StickerFrame(alpha = progress.coerceIn(0f, 1f), scale = 0.88f + 0.12f * progress, rotation = -6f * (1f - progress))

/** Neighbours land this far apart, so a grid of pictures is placed one by one. */
const val STICKER_STAGGER_MS = 70L

/**
 * A picture card arriving like a sticker being placed: the first time it is shown it fades in from a little smaller
 * and tilted, and settles with a small overshoot, [index] x 70 ms after the one before it. Once, never on a loop.
 * With reduced motion it only fades in. It draws in a layer, so the layout and the touch targets never change.
 */
fun Modifier.stickerIn(index: Int = 0): Modifier = composed {
    val reduced = LocalReducedMotion.current
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(index * STICKER_STAGGER_MS)
        progress.animateTo(
            1f,
            if (reduced) tween(PlayItMotion.MICRO_MS)
            else spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow)
        )
    }
    graphicsLayer {
        val frame = stickerAt(progress.value, reduced)
        alpha = frame.alpha
        scaleX = frame.scale
        scaleY = frame.scale
        rotationZ = frame.rotation
    }
}
