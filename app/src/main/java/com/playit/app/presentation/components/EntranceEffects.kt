package com.playit.app.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.LocalReducedMotion
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.min

// Entrances (user, 2026-10-29: more motion on the opening, the map, lesson starts and rewards). Each plays once, when
// its screen opens, never on a loop; with reduced motion everything is simply there. All draw in a layer, so the
// layout and the touch targets never move.

/** How far an entrance drops from, in dp. */
const val DROP_DP = 56f

/** An entrance at [progress] (a spring, so it passes 1 and comes back): how far above its place it is, 0..1 of the drop, and its opacity. */
fun dropOffsetAt(progress: Float): Float = 1f - progress
fun dropAlphaAt(progress: Float): Float = (progress * 3f).coerceIn(0f, 1f)

/**
 * Drops into place when its screen opens: it falls [distanceDp] from above (a negative distance rises from below, for
 * a line under a title), fades in over the first third and lands with a bounce (a spring that passes its resting place
 * and comes back, about half a second). Once, after [delayMs].
 */
fun Modifier.dropIn(delayMs: Int = 0, distanceDp: Float = DROP_DP): Modifier = composed {
    val reduced = LocalReducedMotion.current
    val progress = remember { Animatable(if (reduced) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (reduced) return@LaunchedEffect
        delay(delayMs.toLong())
        progress.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow))
    }
    graphicsLayer {
        translationY = -dropOffsetAt(progress.value) * distanceDp.dp.toPx()
        alpha = dropAlphaAt(progress.value)
    }
}

/** The map's opening wave: when the first stops pop in, how much later each stop nearer the child's comes, over how many stops. */
const val MAP_POP_BASE_MS = 250
const val MAP_POP_STEP_MS = 70
const val MAP_POP_WAVE = 6

/**
 * When stop [index] pops onto the map: stops [MAP_POP_WAVE] or more away from the child's current stop [activeIndex]
 * first (mostly off screen, as the map scrolls there), then each nearer one a step later, the current stop last.
 */
fun mapPopDelayMs(index: Int, activeIndex: Int): Int =
    MAP_POP_BASE_MS + (MAP_POP_WAVE - min(abs(index - activeIndex), MAP_POP_WAVE)) * MAP_POP_STEP_MS

/** The current stop has landed: the child's animal hops then. */
val MAP_OPEN_HOP_MS: Int = mapPopDelayMs(0, 0) + 250

/** How long Lily waves when a lesson starts, before she takes the pose the screen asks for. */
const val GREET_MS = 1400L

/** A lesson's main button (Hear It's play, Say It's mic) pops in once the letter card has landed. */
const val LESSON_BUTTON_POP_MS = 450
