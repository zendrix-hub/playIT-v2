package com.playit.app.presentation.components.lipsync

import com.playit.app.presentation.components.MascotState
import kotlin.math.PI
import kotlin.math.sin

/**
 * Where a pose's two eyes are in its 512 x 512 picture (their cream patches, measured from the released pictures),
 * so a drawn lid can close them for a blink.
 */
data class PoseEyes(val leftX: Float, val rightX: Float, val y: Float, val radius: Float)

/** The eyes of [pose]; null for celebrating, whose eyes are already closed in a happy curve. */
fun poseEyes(pose: MascotState): PoseEyes? = when (pose) {
    MascotState.CELEBRATING -> null
    MascotState.ENCOURAGING -> PoseEyes(210f, 300f, 182f, 38f)
    MascotState.IDLE -> PoseEyes(204f, 294f, 182f, 38f)
    MascotState.LISTENING -> PoseEyes(204f, 294f, 182f, 38f)
    MascotState.POINTING -> PoseEyes(202f, 292f, 182f, 38f)
    MascotState.THINKING -> PoseEyes(216f, 306f, 182f, 38f)
    MascotState.WAVING -> PoseEyes(208f, 296f, 182f, 38f)
}

/** One blink: the eyes shut in 70 ms, stay shut 40 ms and open in 90 ms. */
const val BLINK_MS = 200

/** How closed the eyes are [ms] into a blink: 0 open, 1 shut. */
fun blinkClosed(ms: Float): Float = when {
    ms <= 0f -> 0f
    ms < 70f -> ms / 70f
    ms < 110f -> 1f
    ms < BLINK_MS -> 1f - (ms - 110f) / 90f
    else -> 0f
}

/** One hop when Lily celebrates: up, down, and a small squash as she lands. */
const val HOP_MS = 520

/** A hop at [progress] 0..1: how high she is, as a fraction of her height, and how squashed (0..1) she is. */
data class HopFrame(val lift: Float, val squash: Float)

fun hopAt(progress: Float): HopFrame {
    val p = progress.coerceIn(0f, 1f)
    return when {
        p < 0.4f -> HopFrame(lift = HOP_HEIGHT * easeOut(p / 0.4f), squash = 0f)
        p < 0.8f -> HopFrame(lift = HOP_HEIGHT * (1f - easeIn((p - 0.4f) / 0.4f)), squash = 0f)
        else -> HopFrame(lift = 0f, squash = sin(PI.toFloat() * (p - 0.8f) / 0.2f))
    }
}

private const val HOP_HEIGHT = 0.12f
private fun easeOut(t: Float) = 1f - (1f - t) * (1f - t)
private fun easeIn(t: Float) = t * t
