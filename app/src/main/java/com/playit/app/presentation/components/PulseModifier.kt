package com.playit.app.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.LocalReducedMotion

fun Modifier.breathingPulse(enabled: Boolean = true): Modifier = composed {
    if (!enabled || LocalReducedMotion.current) return@composed this
    val t = rememberInfiniteTransition(label = "pulseTransition")
    val scale by t.animateFloat(1.0f, 1.05f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "pulseScale")
    this.graphicsLayer { scaleX = scale; scaleY = scale }
}

fun Modifier.idleBounce(enabled: Boolean = true): Modifier = composed {
    if (!enabled || LocalReducedMotion.current) return@composed this
    val t = rememberInfiniteTransition(label = "idleBounceTransition")
    val y by t.animateFloat(0f, -6f, infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "idleBounceY")
    this.graphicsLayer { translationY = y.dp.toPx() }
}

/** Length of one idle wiggle (card 28b). */
const val WIGGLE_MS = 700

/** (time in ms, angle in degrees): a left-right shake that settles back to 0. */
private val WIGGLE_KEYS = listOf(0 to 0f, 100 to -8f, 250 to 8f, 400 to -6f, 550 to 4f, WIGGLE_MS to 0f)

/** The wiggle's angle at [ms] from its start, straight lines between the keys; 0 outside the wiggle. */
fun wiggleAngleAt(ms: Float): Float {
    if (ms <= 0f || ms >= WIGGLE_MS) return 0f
    val i = WIGGLE_KEYS.indexOfFirst { it.first >= ms }
    val (t0, a0) = WIGGLE_KEYS[i - 1]
    val (t1, a1) = WIGGLE_KEYS[i]
    return a0 + (a1 - a0) * (ms - t0) / (t1 - t0)
}

/**
 * One short wiggle each time [nudge] goes up while [enabled]: the 10 s idle cue on the button the
 * child should tap next, so a child who cannot read sees where to go (card 28b, grill decision 8).
 * The spoken re-prompt plays as before. A nudge from before the button appeared is not replayed.
 * Reduced motion: no wiggle.
 */
fun Modifier.idleWiggle(nudge: Int, enabled: Boolean = true): Modifier = composed {
    val active by rememberUpdatedState(enabled && !LocalReducedMotion.current)
    val firstNudge = remember { nudge }
    val time = remember { Animatable(0f) }
    LaunchedEffect(nudge) {
        if (nudge > firstNudge && active) {
            time.snapTo(0f)
            time.animateTo(WIGGLE_MS.toFloat(), tween(WIGGLE_MS, easing = LinearEasing))
        }
    }
    this.graphicsLayer { rotationZ = wiggleAngleAt(time.value) }
}
