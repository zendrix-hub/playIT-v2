package com.playit.app.presentation.splash

import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

// The opening (user, 2026-10-29: "is confetti really the only thing ... i want it to be like a good motion design").
// It plays in beats, one thing to look at at a time:
//   1. the clouds drift in from the sides;
//   2. Lily peeks over the panel, blinks, crouches, jumps up and lands, and her paws grab the panel's edge;
//   3. as she jumps, the PlayIT letters fall in one by one, stretching as they fall and squashing as they land;
//   4. the tagline is wiped in from the left;
//   5. the Start button pops in, a wave runs through the letters, sparkles twinkle round the title, a shine
//      crosses the Start button and Lily winks.
// One clock, in ms from the moment the screen draws smoothly (awaitSmoothFrames), drives every part, so the beats keep
// their order even when the phone is slow at first. Each part is a pure function of that clock, and at OPENING_END_MS
// everything is at rest in card 31a's layout (LayoutMatrixTest). With reduced motion the clock starts at the end.

/** Beat 1: each cloud drifts in from its side; the next starts a little later. */
internal const val CLOUD_IN_MS = 900
internal const val CLOUD_STAGGER_MS = 120

/** Beat 2: Lily. How much of her shows when she peeks: her eyes are just over the panel's edge. */
internal const val LILY_PEEK = 0.6f
internal const val LILY_CROUCH = 0.06f
internal const val LILY_TOP = 1.08f
internal const val LILY_PEEK_MS = 100
internal const val LILY_PEEK_END_MS = 420
internal const val LILY_BLINK_MS = 470L
internal const val LILY_CROUCH_MS = 650
internal const val LILY_JUMP_MS = 730
internal const val LILY_TOP_MS = 930
internal const val LILY_LAND_MS = 1080
internal const val LILY_SETTLED_MS = 1230

/** Her paws drop onto the panel's edge as she lands, and squash a little. */
internal const val PAWS_MS = 1060
internal const val PAWS_DROP_MS = 160
internal const val PAWS_SQUASH_MS = 140
internal const val PAWS_DROP_DP = 18f

/** Beat 3: the title's letters. */
internal const val TITLE = "PlayIT"
internal const val TITLE_MS = 950
internal const val TITLE_STAGGER_MS = 70
internal const val LETTER_MS = 420
internal const val LETTER_DROP_DP = 64f

/** Beat 4: the tagline. */
internal const val TAGLINE_MS = 1450
internal const val TAGLINE_LEN_MS = 450
internal const val TAGLINE_RISE_DP = 12f

/** Beat 5: the Start button, the letters' wave, the sparkles, the shine and Lily's wink. */
internal const val START_POP_MS = 1700
internal const val START_POP_LEN_MS = 450
internal const val TITLE_WAVE_MS = 1800
internal const val WAVE_STAGGER_MS = 50
internal const val WAVE_LEN_MS = 260
internal const val SPARKLE_MS = 1900
internal const val SPARKLE_STAGGER_MS = 100
internal const val SPARKLE_LEN_MS = 480
internal const val SHINE_MS = 2200
internal const val SHINE_LEN_MS = 450
internal const val LILY_WINK_MS = 2250L

/** Where the sparkles twinkle, as shares of the title's size from its top-left, and their size as a share of its height. */
internal data class SparkleSpot(val x: Float, val y: Float, val size: Float)

internal val SPARKLES = listOf(
    SparkleSpot(-0.08f, 0.18f, 0.34f),
    SparkleSpot(0.34f, -0.18f, 0.20f),
    SparkleSpot(1.07f, 0.02f, 0.28f),
    SparkleSpot(0.90f, 1.02f, 0.22f)
)

/** The clouds: where each rests (shares of the screen), how wide it is, and the side it drifts in from. */
internal data class CloudSpot(val x: Float, val y: Float, val width: Float, val side: Float)

internal val CLOUDS = listOf(
    CloudSpot(0.08f, 0.07f, 0.34f, -1f),
    CloudSpot(0.93f, 0.15f, 0.30f, 1f),
    CloudSpot(0.62f, 0.035f, 0.17f, 1f)
)

/** Lily's wink, as LilyEyes.winkOnce plays it: shut, hold, open. */
internal val WINK_LEN_MS: Int = 90 + WINK_HOLD_MS.toInt() + 180

/** The opening ends once every part is at rest, Lily's wink included, with a frame or two to spare. */
internal val OPENING_END_MS: Int = maxOf(
    SPARKLE_MS + (SPARKLES.size - 1) * SPARKLE_STAGGER_MS + SPARKLE_LEN_MS,
    SHINE_MS + SHINE_LEN_MS,
    TITLE_WAVE_MS + (TITLE.length - 1) * WAVE_STAGGER_MS + WAVE_LEN_MS,
    LILY_WINK_MS.toInt() + WINK_LEN_MS + 50
)

// ---- Curves -----------------------------------------------------------------------------------------------------

/** How far [ms] is through the stretch from [start] to [end], 0..1. */
private fun seg(ms: Float, start: Int, end: Int): Float = ((ms - start) / (end - start)).coerceIn(0f, 1f)

private fun easeOutCubic(t: Float): Float = 1f - (1f - t) * (1f - t) * (1f - t)
private fun easeOutQuad(t: Float): Float = 1f - (1f - t) * (1f - t)
private fun easeInQuad(t: Float): Float = t * t
private fun easeInOut(t: Float): Float = if (t < 0.5f) 4f * t * t * t else 1f - (-2f * t + 2f).let { it * it * it } / 2f
/** Up and back down: 0 at both ends (exactly, so a part ends at rest), 1 halfway. */
private fun bump(t: Float): Float = if (t <= 0f || t >= 1f) 0f else sin(PI.toFloat() * t)

/** A spring's settle: 0 to 1, passing 1 by about 12 %, dipping 1 % under it, and ending exactly at 1. */
internal fun settle(t: Float): Float {
    if (t <= 0f) return 0f
    if (t >= 1f) return 1f
    return 1f - exp(-6f * t) * cos(2.5f * PI.toFloat() * t)
}

// ---- The parts --------------------------------------------------------------------------------------------------

/** How far a cloud is from its place: 1 = fully off its side of the screen, 0 = in place. */
internal fun cloudAt(index: Int, ms: Float): Float {
    val start = index * CLOUD_STAGGER_MS
    return 1f - easeOutCubic(seg(ms, start, start + CLOUD_IN_MS))
}

/** Lily: how much of her has risen over the panel (1 = her place, more = above it) and her squash and stretch. */
internal data class LilyFrame(val shown: Float, val scaleX: Float, val scaleY: Float)

internal fun lilyAt(ms: Float): LilyFrame = when {
    ms < LILY_PEEK_MS -> LilyFrame(0f, 1f, 1f)
    ms < LILY_PEEK_END_MS -> LilyFrame(LILY_PEEK * easeOutCubic(seg(ms, LILY_PEEK_MS, LILY_PEEK_END_MS)), 1f, 1f)
    ms < LILY_CROUCH_MS -> LilyFrame(LILY_PEEK, 1f, 1f)
    ms < LILY_JUMP_MS -> {
        // Anticipation: she sinks a little and squashes before the jump.
        val q = easeInOut(seg(ms, LILY_CROUCH_MS, LILY_JUMP_MS))
        LilyFrame(LILY_PEEK - LILY_CROUCH * q, 1f + 0.05f * q, 1f - 0.06f * q)
    }
    ms < LILY_TOP_MS -> {
        // The jump: fastest, and so most stretched, as she leaves; slowing to her highest point.
        val q = seg(ms, LILY_JUMP_MS, LILY_TOP_MS)
        val stretch = 1f - q
        LilyFrame(LILY_PEEK - LILY_CROUCH + (LILY_TOP - LILY_PEEK + LILY_CROUCH) * easeOutQuad(q), 1f - 0.05f * stretch, 1f + 0.09f * stretch)
    }
    ms < LILY_LAND_MS -> {
        val q = seg(ms, LILY_TOP_MS, LILY_LAND_MS)
        LilyFrame(LILY_TOP - (LILY_TOP - 1f) * easeInQuad(q), 1f - 0.02f * q, 1f + 0.03f * q)
    }
    ms < LILY_SETTLED_MS -> {
        // The landing squash.
        val a = bump(seg(ms, LILY_LAND_MS, LILY_SETTLED_MS))
        LilyFrame(1f, 1f + 0.05f * a, 1f - 0.07f * a)
    }
    else -> LilyFrame(1f, 1f, 1f)
}

/** A part that drops in: how far above its place it is (a share of its drop), its squash and stretch, and its opacity. */
internal data class DropFrame(val lift: Float, val scaleX: Float, val scaleY: Float, val alpha: Float) {
    companion object {
        val REST = DropFrame(0f, 1f, 1f, 1f)
        val HIDDEN = DropFrame(1f, 1f, 1f, 0f)
    }
}

/** Lily's paws: they drop onto the panel's edge and squash as they grab it. */
internal fun pawsAt(ms: Float): DropFrame {
    val landed = PAWS_MS + PAWS_DROP_MS
    return when {
        ms < PAWS_MS -> DropFrame.HIDDEN
        ms < landed -> {
            val q = seg(ms, PAWS_MS, landed)
            DropFrame(1f - easeInQuad(q), 1f, 1f, (q * 3f).coerceAtMost(1f))
        }
        ms < landed + PAWS_SQUASH_MS -> {
            val a = bump(seg(ms, landed, landed + PAWS_SQUASH_MS))
            DropFrame(0f, 1f + 0.08f * a, 1f - 0.18f * a, 1f)
        }
        else -> DropFrame.REST
    }
}

/**
 * Letter [index] of the title. It falls (speeding up, stretched), lands (a squash from the bottom), rebounds a little
 * and settles. Later the wave lifts it once more.
 */
internal fun letterAt(index: Int, ms: Float): DropFrame {
    val start = TITLE_MS + index * TITLE_STAGGER_MS
    val p = (ms - start) / LETTER_MS
    val drop = when {
        p <= 0f -> DropFrame.HIDDEN
        p < 0.5f -> {
            val q = p / 0.5f
            DropFrame(1f - easeInQuad(q), 1f - 0.10f * q, 1f + 0.16f * q, (q * 2.5f).coerceAtMost(1f))
        }
        p < 0.7f -> {
            // From the stretch of the fall into the squash of the landing, and back to its shape.
            val q = (p - 0.5f) / 0.2f
            val a = bump(q)
            DropFrame(0f, (1f - 0.10f * (1f - q)) * (1f + 0.20f * a), (1f + 0.16f * (1f - q)) * (1f - 0.24f * a), 1f)
        }
        p < 1f -> DropFrame(0.12f * bump((p - 0.7f) / 0.3f), 1f, 1f, 1f)
        else -> DropFrame.REST
    }
    val waveStart = TITLE_WAVE_MS + index * WAVE_STAGGER_MS
    val w = seg(ms, waveStart, waveStart + WAVE_LEN_MS)
    if (w <= 0f || w >= 1f) return drop
    val a = bump(w)
    return drop.copy(lift = drop.lift + 0.16f * a, scaleX = drop.scaleX * (1f + 0.06f * a), scaleY = drop.scaleY * (1f + 0.06f * a))
}

/** The tagline's wipe, 0 = hidden, 1 = all shown. */
internal fun taglineAt(ms: Float): Float = easeInOut(seg(ms, TAGLINE_MS, TAGLINE_MS + TAGLINE_LEN_MS))

/** How far the tagline still is below its place, a share of its rise. */
internal fun taglineRiseAt(ms: Float): Float = 1f - easeOutCubic(seg(ms, TAGLINE_MS, TAGLINE_MS + TAGLINE_LEN_MS))

/** The Start button's pop: its scale and opacity. */
internal data class PopFrame(val scale: Float, val alpha: Float)

internal fun startPopAt(ms: Float): PopFrame {
    val q = seg(ms, START_POP_MS, START_POP_MS + START_POP_LEN_MS)
    return PopFrame(settle(q), (q * 4f).coerceAtMost(1f))
}

/** Sparkle [index]: its size, 0..1..0, and its turn in degrees. */
internal fun sparkleAt(index: Int, ms: Float): Pair<Float, Float> {
    val start = SPARKLE_MS + index * SPARKLE_STAGGER_MS
    val q = seg(ms, start, start + SPARKLE_LEN_MS)
    return bump(q) to 90f * q
}

/** The shine across the Start button, 0..1 from left to right while it runs; null before and after. */
internal fun shineAt(ms: Float): Float? {
    if (ms <= SHINE_MS || ms >= SHINE_MS + SHINE_LEN_MS) return null
    return easeInOut(seg(ms, SHINE_MS, SHINE_MS + SHINE_LEN_MS))
}

// ---- When to start ----------------------------------------------------------------------------------------------

/** Frames this close together are smooth (a 60 Hz frame is 16.7 ms); this many in a row, and the opening can start. */
internal const val SMOOTH_FRAME_MS = 25L
internal const val SMOOTH_FRAMES = 6

/** The opening waits at most this long for smooth frames, so a slow phone still gets it. */
internal const val OPENING_MAX_WAIT_MS = 1000L

/**
 * Waits until the screen is drawing smoothly. Right after the app starts, Android's own launch screen is still fading
 * and the first frames are slow, so an opening that started at once played mostly unseen (user, 2026-10-29: only the
 * confetti showed). It returns after [SMOOTH_FRAMES] smooth frames in a row, or after [OPENING_MAX_WAIT_MS].
 */
internal suspend fun awaitSmoothFrames() {
    val first = withFrameNanos { it }
    var last = first
    var smooth = 0
    while (smooth < SMOOTH_FRAMES && last - first < OPENING_MAX_WAIT_MS * 1_000_000L) {
        val now = withFrameNanos { it }
        smooth = if (now - last <= SMOOTH_FRAME_MS * 1_000_000L) smooth + 1 else 0
        last = now
    }
}

// ---- Drawing ----------------------------------------------------------------------------------------------------

private val CloudWhite = Color.White.copy(alpha = 0.92f)

/** The clouds at [ms], each a flat shape of three puffs on a round base. */
internal fun DrawScope.drawOpeningClouds(ms: Float) {
    CLOUDS.forEachIndexed { i, c ->
        val w = size.width * c.width
        val h = w * 0.42f
        val away = cloudAt(i, ms) * (size.width * 0.5f + w)
        val cx = size.width * c.x + c.side * away
        val cy = size.height * c.y
        drawRoundRect(CloudWhite, Offset(cx - w / 2f, cy - h * 0.05f), Size(w, h * 0.5f), CornerRadius(h * 0.25f))
        drawCircle(CloudWhite, h * 0.34f, Offset(cx - w * 0.22f, cy + h * 0.06f))
        drawCircle(CloudWhite, h * 0.46f, Offset(cx + w * 0.04f, cy - h * 0.10f))
        drawCircle(CloudWhite, h * 0.30f, Offset(cx + w * 0.28f, cy + h * 0.08f))
    }
}

/** The sparkles round the title at [ms]: four-pointed stars in [fill] with a [line] outline. */
internal fun DrawScope.drawTitleSparkles(ms: Float, fill: Color, line: Color, lineWidth: Float) {
    SPARKLES.forEachIndexed { i, s ->
        val (grow, turn) = sparkleAt(i, ms)
        if (grow <= 0f) return@forEachIndexed
        val c = Offset(size.width * s.x, size.height * s.y)
        val r = size.height * s.size * grow
        val star = Path().apply {
            moveTo(c.x, c.y - r)
            quadraticBezierTo(c.x, c.y, c.x + r, c.y)
            quadraticBezierTo(c.x, c.y, c.x, c.y + r)
            quadraticBezierTo(c.x, c.y, c.x - r, c.y)
            quadraticBezierTo(c.x, c.y, c.x, c.y - r)
            close()
        }
        rotate(turn, c) {
            drawPath(star, fill)
            drawPath(star, line, style = Stroke(lineWidth))
        }
    }
}

/** The shine at [progress] across a pill-shaped button face of this size with [corner] radius. */
internal fun DrawScope.drawShine(progress: Float, corner: Float) {
    val band = size.height * 0.9f
    val x = -band + progress * (size.width + 2f * band)
    val face = Path().apply { addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(corner))) }
    clipPath(face) {
        drawRect(
            Brush.linearGradient(
                0f to Color.Transparent,
                0.5f to Color.White.copy(alpha = 0.55f),
                1f to Color.Transparent,
                start = Offset(x - band / 2f, 0f),
                end = Offset(x + band / 2f, size.height * 0.6f)
            )
        )
    }
}
