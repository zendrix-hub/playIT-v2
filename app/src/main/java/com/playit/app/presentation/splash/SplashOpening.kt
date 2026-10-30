package com.playit.app.presentation.splash

import androidx.compose.runtime.withFrameNanos
import com.playit.app.data.audio.SfxEvent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import com.playit.app.presentation.theme.CoralBerry
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.Mango
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

// The opening (user, 2026-10-29: "is confetti really the only thing ... i want it to be like a good motion design").
// It plays in beats, one thing to look at at a time:
//   1. the clouds drift in from the sides;
//   2. Lily peeks over the panel, blinks, crouches, jumps up and lands, and her paws grab the panel's edge;
//   3. as she jumps, the PlayIT letters pop up one by one, each on a plucked note, and wobble to rest;
//   4. the tagline is wiped in from the left;
//   5. the Start button pops in, a wave runs through the letters as a child says "Play it!", one of four effects
//      plays round the title (a different one each time the app opens), a shine crosses the Start button and Lily
//      winks.
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

/**
 * Beat 3: the title's letters pop up one by one (the user's pick of three motions, 2026-10-30). They come 100 ms apart,
 * near the pace of the plucked tune whose notes they play (user: "the letters should align with it when showing up";
 * version B of the preview). The wave lifts them by a share of LETTER_LIFT_DP.
 */
internal const val TITLE = "PlayIT"
internal const val TITLE_MS = 950
internal const val TITLE_STAGGER_MS = 100
internal const val LETTER_MS = 420
internal const val LETTER_LIFT_DP = 64f

/**
 * What plays round the title after its wave, a different one each time the app opens, in this order (user,
 * 2026-10-30, on the twinkling stars: "make the effect like what the theme of the app is"; then, on the preview of
 * these four: "put all of that everytime you open the app it changes"). TWINKLE: four stars twinkle; FLOWERS: island
 * flowers (hibiscus and sampaguita) burst out; LETTERS: the first letters the app teaches pop out in the island
 * colours; PLANE: a paper airplane, as on the map, loops over the title.
 */
enum class TitleEffect { TWINKLE, FLOWERS, LETTERS, PLANE }

/** The effect after [last] (its saved name), in turn; the first if there was none or it is unknown. */
internal fun titleEffectAfter(last: String?): TitleEffect {
    val all = TitleEffect.values()
    return all[(all.indexOfFirst { it.name == last } + 1) % all.size]
}

/** Beat 4: the tagline. */
internal const val TAGLINE_MS = 1450
internal const val TAGLINE_LEN_MS = 450
internal const val TAGLINE_RISE_DP = 12f

/** Beat 5: the Start button, the letters' wave, the title's effect, the shine and Lily's wink. */
internal const val START_POP_MS = 1700
internal const val START_POP_LEN_MS = 450
internal const val TITLE_WAVE_MS = 1880
internal const val WAVE_STAGGER_MS = 50
internal const val WAVE_LEN_MS = 260
internal const val SPARKLE_MS = 1980
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

/** The burst of flowers or letters: they fly out from round the title, a few ms apart, these ways (degrees, 270 is up). */
internal val BURST_ANGLES = listOf(195f, 230f, 270f, 310f, 345f, 15f, 165f)
internal const val BURST_STAGGER_MS = 15
internal const val BURST_LEN_MS = 650

/** The little letters that pop out: the first the app teaches (UnitGuidebookDialog: m s a i o, then b u). */
internal const val BURST_LETTERS = "msaiobu"

/** The paper airplane's flight over the title, with one loop on the way. */
internal const val PLANE_LEN_MS = 1100

/** The clouds: where each rests (shares of the screen), how wide it is, and the side it drifts in from. */
internal data class CloudSpot(val x: Float, val y: Float, val width: Float, val side: Float)

internal val CLOUDS = listOf(
    CloudSpot(0.08f, 0.07f, 0.34f, -1f),
    CloudSpot(0.93f, 0.15f, 0.30f, 1f),
    CloudSpot(0.62f, 0.035f, 0.17f, 1f)
)

/** Lily's wink, as LilyEyes.winkOnce plays it: shut, hold, open. */
internal val WINK_LEN_MS: Int = 90 + WINK_HOLD_MS.toInt() + 180

/**
 * The opening ends once every part is at rest, whichever effect plays (the plane flies longest), Lily's wink included,
 * with a frame or two to spare.
 */
internal val OPENING_END_MS: Int = maxOf(
    SPARKLE_MS + (SPARKLES.size - 1) * SPARKLE_STAGGER_MS + SPARKLE_LEN_MS,
    SPARKLE_MS + PLANE_LEN_MS,
    SHINE_MS + SHINE_LEN_MS,
    TITLE_WAVE_MS + (TITLE.length - 1) * WAVE_STAGGER_MS + WAVE_LEN_MS,
    LILY_WINK_MS.toInt() + WINK_LEN_MS + 50
)

// ---- Sounds -----------------------------------------------------------------------------------------------------

/**
 * A sound of the opening (user, 2026-10-29: "add a sound on the opening of the app too"): which sound, when, how loud
 * (0..1) and at what pitch (SoundPool's rate: 2 is an octave up). The user's pick (2026-10-30, version B of the
 * preview: "this one is good"): a plucked note as each letter pops up, then a child says "Play it!" as the letters
 * wave; nothing else. Both are in release 2026-10-30-opening.
 */
data class OpeningCue(val atMs: Int, val sfx: SfxEvent, val volume: Float, val rate: Float = 1f)

/** The letters' notes: the plucked G at six pitches, going on up the scale of the tune the user picked: D E F# G A B. */
internal val LETTER_RATES = listOf(0.7492f, 0.8409f, 0.9439f, 1f, 1.1225f, 1.2599f)

/** The plucked note as loud as in the preview the user picked. */
internal const val PLUCK_VOLUME = 0.132f
internal const val VOICE_VOLUME = 0.9f

/** Where the "p" of "Play it!" is in its file: it lands on the wave's start. */
internal const val VOICE_P_MS = 90

internal val OPENING_CUES: List<OpeningCue> = buildList {
    TITLE.indices.forEach { i ->                                             // each letter at its biggest
        add(OpeningCue(TITLE_MS + i * TITLE_STAGGER_MS + LETTER_MS / 2, SfxEvent.OPENING_PLUCK, PLUCK_VOLUME, LETTER_RATES[i]))
    }
    add(OpeningCue(TITLE_WAVE_MS - VOICE_P_MS, SfxEvent.OPENING_PLAYIT, VOICE_VOLUME))   // the wave
}

/** With reduced motion there is no opening: "Play it!" once, as the screen appears. */
internal val STILL_OPENING_CUE = OpeningCue(0, SfxEvent.OPENING_PLAYIT, VOICE_VOLUME)

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

/**
 * A part that drops in: how far above its place it is (a share of its drop), its squash and stretch, its opacity and
 * its turn in degrees (about the middle of its bottom).
 */
internal data class DropFrame(val lift: Float, val scaleX: Float, val scaleY: Float, val alpha: Float, val rotation: Float = 0f) {
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
 * Letter [index] of the title. It pops up out of its place, twisting a little, to its biggest as it arrives halfway
 * through its [LETTER_MS] (as its note plays), then wobbles to rest. Later the wave lifts it once more.
 */
internal fun letterAt(index: Int, ms: Float): DropFrame {
    val start = TITLE_MS + index * TITLE_STAGGER_MS
    val p = (ms - start) / LETTER_MS
    val pop = when {
        p <= 0f -> DropFrame.HIDDEN
        p < 0.5f -> {
            val q = p / 0.5f
            val s = 1.25f * easeOutCubic(q)
            DropFrame(0f, s, s, (q * 3f).coerceAtMost(1f), -14f * (1f - q))
        }
        p < 1f -> {
            // A spring's wobble: smaller each time, and exactly at rest at the end.
            val q = (p - 0.5f) / 0.5f
            val d = exp(-5f * q)
            val s = 1f + 0.25f * d * cos(2.5f * PI.toFloat() * q)
            DropFrame(0f, s, s, 1f, 6f * d * sin(3f * PI.toFloat() * q))
        }
        else -> DropFrame.REST
    }
    val waveStart = TITLE_WAVE_MS + index * WAVE_STAGGER_MS
    val w = seg(ms, waveStart, waveStart + WAVE_LEN_MS)
    if (w <= 0f || w >= 1f) return pop
    val a = bump(w)
    return pop.copy(lift = pop.lift + 0.16f * a, scaleX = pop.scaleX * (1f + 0.06f * a), scaleY = pop.scaleY * (1f + 0.06f * a))
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

/**
 * One piece of the title's effect at one moment: where (shares of the title's size from its top-left), how big (a share
 * of its height), its turn in degrees, which piece it is (for its colour, kind or letter) and how much of it shows.
 */
internal data class EffectFrame(val x: Float, val y: Float, val size: Float, val turn: Float, val index: Int, val alpha: Float = 1f)

/** The twinkling stars at [ms]: each grows and turns at its spot. */
internal fun twinkleAt(ms: Float): List<EffectFrame> = SPARKLES.mapIndexedNotNull { i, s ->
    val (grow, turn) = sparkleAt(i, ms)
    if (grow > 0f) EffectFrame(s.x, s.y, s.size * grow, turn, i) else null
}

/**
 * The burst at [ms] round a title [aspect] times as wide as it is tall: from the edge of the title's oval, each piece
 * flies out its way by up to 0.9 of the title's height, growing to [size] and shrinking away, turning by up to [spin].
 */
internal fun burstAt(ms: Float, aspect: Float, size: Float, spin: Float): List<EffectFrame> =
    BURST_ANGLES.mapIndexedNotNull { i, deg ->
        val start = SPARKLE_MS + i * BURST_STAGGER_MS
        val q = seg(ms, start, start + BURST_LEN_MS)
        if (q <= 0f || q >= 1f) return@mapIndexedNotNull null
        val a = deg * PI.toFloat() / 180f
        val out = 0.9f * easeOutCubic(q)
        val turn = spin * q * (if (i % 2 == 0) 1f else -1f)
        EffectFrame(0.5f + (0.5f + out / aspect) * cos(a), 0.5f + (0.5f + out) * sin(a), size * bump(q), turn, i)
    }

/**
 * Where the paper airplane is, [t] of the way through its flight, round a title [aspect] times as wide as it is tall:
 * from the title's left, up over it with one loop (up, back over the top and down again), to its right.
 */
internal fun planePathAt(t: Float, aspect: Float): Offset {
    val pi = PI.toFloat()
    val base = Offset(0.5f - (0.5f + 0.4f / aspect) * cos(pi * t), 0.5f - 0.95f * sin(pi * t))
    val u = ((t - 0.4f) / 0.3f).coerceIn(0f, 1f)
    return base + Offset(0.5f * sin(2f * pi * u) / aspect, -0.5f * (1f - cos(2f * pi * u)))
}

/** The paper airplane at [ms]: where it is on its flight, 0..1, and how much of it shows; null before and after. */
internal fun planeAt(ms: Float): Pair<Float, Float>? {
    val t = seg(ms, SPARKLE_MS, SPARKLE_MS + PLANE_LEN_MS)
    if (t <= 0f || t >= 1f) return null
    return t to minOf(1f, t / 0.08f, (1f - t) / 0.12f)
}

/**
 * The title's [effect] at [ms], drawn round the title: twinkling stars, island flowers, little letters ([letterStyle],
 * measured by [measurer], in [colors] in turn) or the paper airplane, all in the pictures' [line] outline.
 */
internal fun DrawScope.drawTitleEffect(
    effect: TitleEffect, ms: Float, measurer: TextMeasurer, letterStyle: TextStyle, colors: List<Color>, line: Color,
    lineWidth: Float
) {
    val aspect = size.width / size.height
    fun at(f: EffectFrame) = Offset(size.width * f.x, size.height * f.y)
    when (effect) {
        TitleEffect.TWINKLE -> twinkleAt(ms).forEach { drawStar(at(it), size.height * it.size, it.turn, Mango, line, lineWidth) }
        TitleEffect.FLOWERS -> burstAt(ms, aspect, 0.3f, 120f).forEach {
            // Hibiscus and sampaguita in turn.
            val petals = if (it.index % 2 == 0) CoralBerry else CreamWhite
            drawFlower(at(it), size.height * it.size, it.turn, petals, Mango, line, lineWidth)
        }
        TitleEffect.LETTERS -> burstAt(ms, aspect, 0.85f, 30f).forEach {
            val text = measurer.measure(BURST_LETTERS[it.index % BURST_LETTERS.length].toString(), letterStyle)
            val k = size.height * it.size / text.size.height
            if (k <= 0f) return@forEach
            val box = Offset(text.size.width / 2f, text.size.height / 2f)
            withTransform({
                translate(at(it).x - box.x, at(it).y - box.y)
                rotate(it.turn, box)
                scale(k, k, box)
            }) {
                // A stroke is centred on the letter's edge, and the colour covers its inner half (as LogoLetter).
                drawText(text, color = line, drawStyle = Stroke(2f * lineWidth / k, join = StrokeJoin.Round))
                drawText(text, color = colors[it.index % colors.size], drawStyle = Fill)   // said again: text keeps the last style
            }
        }
        TitleEffect.PLANE -> planeAt(ms)?.let { (t, shown) ->
            fun px(o: Offset) = Offset(size.width * o.x, size.height * o.y)
            // A dotted trail behind it, fading towards its end.
            var d = 0.02f
            while (d <= 0.3f && t - d > 0f) {
                drawCircle(colors[0].copy(alpha = shown * 0.7f * (1f - d / 0.3f)), size.height * 0.045f, px(planePathAt(t - d, aspect)))
                d += 0.03f
            }
            val here = px(planePathAt(t, aspect))
            val ahead = px(planePathAt(minOf(1f, t + 0.01f), aspect)) - px(planePathAt(maxOf(0f, t - 0.01f), aspect))
            val heading = atan2(ahead.y, ahead.x) * 180f / PI.toFloat()
            drawPlane(here, size.height * 0.8f, heading, shown, line, lineWidth)
        }
    }
}

/** A four-pointed star of radius [r] at [c], turned by [turn] degrees. */
private fun DrawScope.drawStar(c: Offset, r: Float, turn: Float, fill: Color, line: Color, lineWidth: Float) {
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

/** A five-petal flower of radius [r] at [c], turned by [turn] degrees: one outline round its petals, a [middle] heart. */
private fun DrawScope.drawFlower(c: Offset, r: Float, turn: Float, petals: Color, middle: Color, line: Color, lineWidth: Float) {
    val spots = (0 until 5).map { k ->
        val a = (turn + k * 72f - 90f) * PI.toFloat() / 180f
        c + Offset(cos(a), sin(a)) * (0.55f * r)
    }
    spots.forEach { drawCircle(line, 0.45f * r + lineWidth, it) }
    spots.forEach { drawCircle(petals, 0.45f * r, it) }
    drawCircle(middle, 0.28f * r, c)
    drawCircle(line, 0.28f * r, c, style = Stroke(lineWidth * 0.6f))
}

/** A paper airplane [len] long, its middle at [c], flying along [heading] degrees, [alpha] of it showing. */
private fun DrawScope.drawPlane(c: Offset, len: Float, heading: Float, alpha: Float, line: Color, lineWidth: Float) {
    val r = len / 2f
    fun p(x: Float, y: Float) = Offset(c.x + x * r, c.y + y * r)
    fun shape(a: Offset, b: Offset, c: Offset) = Path().apply {
        moveTo(a.x, a.y)
        lineTo(b.x, b.y)
        lineTo(c.x, c.y)
        close()
    }
    val top = shape(p(1f, 0f), p(-0.75f, -0.6f), p(-0.4f, 0.05f))
    val under = shape(p(1f, 0f), p(-0.4f, 0.05f), p(-0.75f, 0.35f))
    val edge = Stroke(lineWidth, join = StrokeJoin.Round)
    rotate(heading, c) {
        drawPath(under, PlaneShade, alpha = alpha)
        drawPath(top, CreamWhite, alpha = alpha)
        drawPath(under, line, alpha = alpha, style = edge)
        drawPath(top, line, alpha = alpha, style = edge)
    }
}

/** The underside of the paper airplane's wing: its paper in shade. */
private val PlaneShade = Color(0xFFD7E4EA)

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
