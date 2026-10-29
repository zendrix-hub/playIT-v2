package com.playit.app.presentation.components

import com.playit.app.presentation.theme.LocalReducedMotion
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/** A wrong match shakes quickly (user, 2026-10-29: a rapid 200 ms horizontal shake). */
const val SHAKE_MS = 200

/** Say It keeps its gentler, slower shake: a sound said wrong must never feel like a punishment. */
const val GENTLE_SHAKE_MS = 400

/** (share of the shake, offset as a share of the 6 dp swing): left, right, smaller each time, back to 0. */
private val SHAKE_KEYS = listOf(
    0f to 0f, 0.125f to -1f, 0.25f to 1f, 0.375f to -0.67f, 0.5f to 0.67f, 0.625f to -0.33f, 0.75f to 0.33f, 1f to 0f
)

/** The shake's offset at [t] 0..1, as a share of its 6 dp swing; straight lines between the keys, 0 at both ends. */
fun shakeOffsetAt(t: Float): Float {
    if (t <= 0f || t >= 1f) return 0f
    val i = SHAKE_KEYS.indexOfFirst { it.first >= t }
    val (t0, a0) = SHAKE_KEYS[i - 1]
    val (t1, a1) = SHAKE_KEYS[i]
    return a0 + (a1 - a0) * (t - t0) / (t1 - t0)
}

/**
 * Shakes left and right once each time [trigger] becomes true, over [durationMs], then calls [onShakeComplete].
 * [key] restarts it while [trigger] stays true: pass the screen state when two wrong answers in a row keep the same
 * flag (Blend It). Reduced motion: no shake; the colour and text change still show the answer.
 */
fun Modifier.shake(
    trigger: Boolean,
    durationMs: Int = SHAKE_MS,
    key: Any? = trigger,
    onShakeComplete: (() -> Unit)? = null
): Modifier = composed {
    val progress = remember { Animatable(1f) }
    val reduced = LocalReducedMotion.current

    LaunchedEffect(key) {
        if (trigger && reduced) {
            onShakeComplete?.invoke()
        } else if (trigger) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(durationMs, easing = LinearEasing))
            onShakeComplete?.invoke()
        }
    }

    this.graphicsLayer {
        translationX = shakeOffsetAt(progress.value) * 6.dp.toPx()
    }
}
