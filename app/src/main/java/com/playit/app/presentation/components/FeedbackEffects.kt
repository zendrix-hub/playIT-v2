package com.playit.app.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.components.lipsync.HOP_MS
import com.playit.app.presentation.components.lipsync.hopAt
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.Guava
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.PlayItMotion
import com.playit.app.presentation.theme.Ube
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
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

/** Stars on a reward screen: how far apart they start, how far they fall, and when one has landed after it starts. */
const val STAR_STAGGER_MS = 300
const val STAR_FALL_DP = 72f
const val STAR_LAND_MS = 150

/**
 * A star drops into place (user, 2026-10-29: a bigger bounce for rewards): it falls STAR_FALL_DP, growing from half
 * size, and lands with a springy bounce, one after another (STAR_STAGGER_MS apart by [index]). Under reduced motion it
 * only fades in.
 */
fun Modifier.starDrop(index: Int, visible: Boolean = true): Modifier = composed {
    val reduced = LocalReducedMotion.current
    val progress = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        if (visible) {
            delay(index * STAR_STAGGER_MS.toLong())
            progress.animateTo(
                1f,
                if (reduced) tween(PlayItMotion.STANDARD_MS)
                else spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMediumLow)
            )
        } else {
            progress.snapTo(0f)
        }
    }
    graphicsLayer {
        val p = progress.value
        alpha = p.coerceIn(0f, 1f)
        translationY = if (reduced) 0f else -STAR_FALL_DP.dp.toPx() * (1f - p)
        val s = if (reduced) 1f else 0.5f + 0.5f * p
        scaleX = s
        scaleY = s
    }
}

/** A heart won back: how long its pop takes. */
const val GAIN_POP_MS = 450

/** The won-back heart's size [ms] into its pop: up to 150 % halfway and back to 100 %. */
fun gainPopScale(ms: Float): Float =
    if (ms <= 0f || ms >= GAIN_POP_MS) 1f else 1f + 0.5f * sin(PI.toFloat() * ms / GAIN_POP_MS)

/** A heart won back (user, 2026-10-29): it swells to 150 % and back, once each time [count] goes up. None under reduced motion. */
fun Modifier.gainPop(count: Int): Modifier = composed {
    val reduced = LocalReducedMotion.current
    val first = remember { count }
    val elapsed = remember { Animatable(GAIN_POP_MS.toFloat()) }
    LaunchedEffect(count) {
        if (count == first || reduced) return@LaunchedEffect
        elapsed.snapTo(0f)
        elapsed.animateTo(GAIN_POP_MS.toFloat(), tween(GAIN_POP_MS, easing = LinearEasing))
    }
    graphicsLayer {
        val s = gainPopScale(elapsed.value)
        scaleX = s
        scaleY = s
    }
}

/**
 * One hop on Lily's curve (up 12 % of its height, down, a small landing squash) each time [count] goes up:
 * an animal picked, tapped, or arriving at a new stop. The first value never hops, so opening a screen does
 * not. None under reduced motion. It draws in a layer, so the layout and the touch target never move.
 * [squash] false lands without squashing, for a row of letters that must keep its shape.
 */
fun Modifier.hop(count: Int, squash: Boolean = true): Modifier = composed {
    val reduced = LocalReducedMotion.current
    val first = remember { count }
    val progress = remember { Animatable(1f) }
    LaunchedEffect(count) {
        if (count == first || reduced) return@LaunchedEffect
        progress.snapTo(0f)
        progress.animateTo(1f, tween(HOP_MS, easing = LinearEasing))
    }
    graphicsLayer {
        val frame = hopAt(progress.value)
        val landing = if (squash) frame.squash else 0f
        translationY = -frame.lift * size.height
        scaleX = 1f + 0.06f * landing
        scaleY = 1f - 0.08f * landing
        transformOrigin = TransformOrigin(0.5f, 1f)
    }
}

/** A right answer's confetti pop: how long it lasts and how many pieces it throws (user, 2026-10-29: clearly stronger). */
const val POP_MS = 900
const val POP_PIECES = 20

private val POP_COLORS = listOf(Mango, Leaf, Guava, Ube)

/**
 * One piece of a right-answer pop at [progress] 0..1: where it is, as a share of the element's smaller side from its
 * centre ([dx], [dy]; down is positive), how opaque it is and how far it has turned. The pieces fly out evenly all
 * round, fast at first, then fall a little and fade over the last 40 %.
 */
data class PopPiece(val dx: Float, val dy: Float, val alpha: Float, val rotation: Float)

fun popPieceAt(index: Int, progress: Float): PopPiece {
    val p = progress.coerceIn(0f, 1f)
    val angle = 2f * PI.toFloat() * index / POP_PIECES + 0.25f * ((index * 7) % 3 - 1)
    val reach = 1.1f + 0.3f * ((index * 5) % 3)
    val thrown = 1f - (1f - p) * (1f - p)
    return PopPiece(
        dx = cos(angle) * reach * thrown,
        dy = sin(angle) * reach * thrown + 0.25f * p * p,
        alpha = if (p < 0.6f) 1f else ((1f - p) / 0.4f).coerceIn(0f, 1f),
        rotation = 360f * p * (if (index % 2 == 0) 1f else -1f)
    )
}

/**
 * A right answer (user, 2026-10-29: confetti on correct answers): a small burst of confetti flies out of the element's
 * centre and fades, once each time [count] goes up. The first value never pops, so opening a screen does not. None
 * under reduced motion; the check mark, the colour and the chime still show the answer. Drawn over the element, so the
 * layout and the touch targets never change.
 */
fun Modifier.confettiPop(count: Int): Modifier = composed {
    val reduced = LocalReducedMotion.current
    val first = remember { count }
    val progress = remember { Animatable(1f) }
    LaunchedEffect(count) {
        if (count == first || reduced) return@LaunchedEffect
        progress.snapTo(0f)
        progress.animateTo(1f, tween(POP_MS, easing = LinearEasing))
    }
    drawWithContent {
        drawContent()
        val p = progress.value
        if (p >= 1f) return@drawWithContent
        val side = size.minDimension
        val piece = 10.dp.toPx()
        for (i in 0 until POP_PIECES) {
            val f = popPieceAt(i, p)
            val color = POP_COLORS[i % POP_COLORS.size].copy(alpha = f.alpha)
            translate(center.x + f.dx * side, center.y + f.dy * side) {
                rotate(f.rotation, pivot = Offset.Zero) {
                    if (i % 3 == 0) drawCircle(color, radius = piece / 2f, center = Offset.Zero)
                    else drawRect(color, topLeft = Offset(-piece / 2f, -piece * 0.3f), size = Size(piece, piece * 0.6f))
                }
            }
        }
    }
}

/** A wrong match's glow: how long it lasts, and how strong it gets at its peak. */
const val GLOW_MS = 400
const val GLOW_PEAK = 0.45f

/** The glow's opacity [ms] after a wrong match: up to GLOW_PEAK in the first quarter, then fading to 0 at GLOW_MS. */
fun glowAlphaAt(ms: Float): Float = when {
    ms <= 0f || ms >= GLOW_MS -> 0f
    ms < GLOW_MS / 4f -> GLOW_PEAK * ms / (GLOW_MS / 4f)
    else -> GLOW_PEAK * (GLOW_MS - ms) / (GLOW_MS * 3f / 4f)
}

/**
 * A wrong match (user, 2026-10-29: a gentle flash): the element glows Gentle Correction Orange once, behind its content,
 * each time [trigger] becomes true ([key] restarts it, as for shake). Orange, never red, and never the whole screen (03 §4: no flashing red, no
 * punishment visuals). Under reduced motion it still glows: a colour change, not motion.
 */
fun Modifier.correctionGlow(trigger: Boolean, shape: Shape, key: Any? = trigger): Modifier = composed {
    val elapsed = remember { Animatable(GLOW_MS.toFloat()) }
    LaunchedEffect(key) {
        if (trigger) {
            elapsed.snapTo(0f)
            elapsed.animateTo(GLOW_MS.toFloat(), tween(GLOW_MS, easing = LinearEasing))
        }
    }
    drawBehind {
        val a = glowAlphaAt(elapsed.value)
        if (a > 0f) drawOutline(shape.createOutline(size, layoutDirection, this), GentleCorrectionOrange.copy(alpha = a))
    }
}
