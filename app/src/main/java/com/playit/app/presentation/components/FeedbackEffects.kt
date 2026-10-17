package com.playit.app.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.PlayItMotion
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

// Feedback effects for meaningful moments only (user decision 2026-10-06). The curves are pure
// functions of elapsed time, so they are unit-tested; the modifiers animate time and apply them.

/** Damped wobble: +-12 degrees, settles to 0 at STANDARD_MS; 0 under reduced motion. */
fun heartWobbleAngle(elapsedMs: Long, reduced: Boolean): Float {
    if (reduced || elapsedMs >= PlayItMotion.STANDARD_MS) return 0f
    val t = elapsedMs / PlayItMotion.STANDARD_MS.toFloat()
    return 12f * (1f - t) * sin(t * 4f * PI.toFloat())
}

/** 100 -> 108 -> 100 % over MICRO_MS (21_ANIMATION_GUIDE :27); 100 % under reduced motion. */
fun correctPopScale(elapsedMs: Long, reduced: Boolean): Float {
    if (reduced || elapsedMs >= PlayItMotion.MICRO_MS) return 1f
    val t = elapsedMs / PlayItMotion.MICRO_MS.toFloat()
    return 1f + 0.08f * sin(t * PI.toFloat())
}

/** Elapsed milliseconds since [key] last changed (or [start] became true), held at [durationMs] when done. */
private fun Modifier.elapsedOnTrigger(
    key: Any?,
    start: Boolean,
    durationMs: Int,
    apply: Modifier.(elapsedMs: () -> Long) -> Modifier
): Modifier = composed {
    val elapsed = remember { Animatable(durationMs.toFloat()) }
    LaunchedEffect(key) {
        if (start) {
            elapsed.snapTo(0f)
            elapsed.animateTo(durationMs.toFloat(), tween(durationMs, easing = LinearEasing))
        }
    }
    apply { elapsed.value.toLong() }
}

/** Correct answer: one pop to 108 % (no pop under reduced motion). */
fun Modifier.correctPop(trigger: Boolean): Modifier = composed {
    val reduced = LocalReducedMotion.current
    elapsedOnTrigger(trigger, trigger, PlayItMotion.MICRO_MS) { elapsed ->
        graphicsLayer {
            val s = correctPopScale(elapsed(), reduced)
            scaleX = s; scaleY = s
        }
    }
}

/** A heart was lost: a gentle damped wobble when [lossCount] goes up (still under reduced motion). */
fun Modifier.heartLossWobble(lossCount: Int): Modifier = composed {
    val reduced = LocalReducedMotion.current
    elapsedOnTrigger(lossCount, lossCount > 0, PlayItMotion.STANDARD_MS) { elapsed ->
        graphicsLayer { rotationZ = heartWobbleAngle(elapsed(), reduced) }
    }
}

/**
 * A star drops into place: it falls 24 dp and fades in over STANDARD_MS, one after another
 * (MICRO_MS apart by [index]). Under reduced motion it only fades in.
 */
fun Modifier.starDrop(index: Int, visible: Boolean = true): Modifier = composed {
    val reduced = LocalReducedMotion.current
    val progress = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        if (visible) {
            delay(index * PlayItMotion.MICRO_MS.toLong())
            progress.animateTo(1f, if (reduced) tween(PlayItMotion.STANDARD_MS) else PlayItMotion.settle(reduced = false))
        } else {
            progress.snapTo(0f)
        }
    }
    graphicsLayer {
        val p = progress.value
        alpha = p.coerceIn(0f, 1f)
        translationY = if (reduced) 0f else -24.dp.toPx() * (1f - p)
    }
}
