package com.playit.app.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.components.VoiceMouth
import com.playit.app.presentation.theme.LilyBlush
import com.playit.app.presentation.theme.LilyCream
import com.playit.app.presentation.theme.LilyEarInner
import com.playit.app.presentation.theme.LilyMouthInside
import com.playit.app.presentation.theme.LilyOrange
import com.playit.app.presentation.theme.LilyOrangeShade
import com.playit.app.presentation.theme.LilyEyeRing
import com.playit.app.presentation.theme.LilyPeach
import com.playit.app.presentation.theme.LilyPupil
import com.playit.app.presentation.theme.LilyTongue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

// Lily on the opening, as a puppet (user, 2026-10-29: "for our mascot how we make it better the design how it moves
// how can it be natural"). She is drawn in parts (body, ears, glasses-like eye rings, eyes, muzzle, belly, arms,
// tail). Her figure moves as one piece, because her head is the top of her body: she leans, breathes and hops whole.
// On it, her eyes look at what happens, her ears twitch and perk up, her tail sways and swings behind a jump, she
// blinks at odd moments and answers a tap a different way each time.
// She is the user's new Lily (2026-11-07: "this is our updated mascot"; images/mascot/lily_idle.png, image release
// 2026-11-07-new-lily), drawn as her pictures draw her: flat shapes with no outline, one rounded body with her head
// at its top, a darker orange along her right side, big round ears, peach rings round her eyes like glasses with a tab
// at each side, a peach muzzle with her nose and open smile, and a tufted tail.

/** Her parts, in shares of her figure's height F (ears' top to feet), across from her middle and down from her top. */
internal data class LilyShape(
    /** Her body: half its width, where its top is, and how tall the rounded top (her head) is. */
    val bodyHalfW: Float, val bodyTop: Float, val domeRy: Float,
    val earX: Float, val earY: Float, val earR: Float, val earInnerR: Float, val earInnerDrop: Float,
    /** Each eye: its white, the pupil in it, the orange ring round it and the peach ring round that. */
    val eyeX: Float, val eyeY: Float, val eyeR: Float, val pupilR: Float, val ringR: Float, val glassesR: Float,
    /** The tab at each side of her glasses: how far out it reaches and how tall it is. */
    val templeX: Float, val templeH: Float,
    val muzzleY: Float, val muzzleRx: Float, val muzzleRy: Float,
    val cheekX: Float, val cheekY: Float, val cheekW: Float, val cheekH: Float,
    val noseY: Float, val noseW: Float, val noseH: Float,
    val mouthY: Float, val mouthW: Float, val mouthH: Float,
    val bellyY: Float, val bellyRx: Float, val bellyRy: Float,
    val shoulderX: Float, val shoulderY: Float, val armW: Float,
    val tail: List<Offset>, val tailW: Float
)

/** As her idle picture draws her (measured from images/mascot/lily_idle.png: her figure is 759 px tall in the master). */
internal val LILY_SHAPE = LilyShape(
    bodyHalfW = 0.285f, bodyTop = 0.066f, domeRy = 0.22f,
    earX = 0.25f, earY = 0.13f, earR = 0.13f, earInnerR = 0.092f, earInnerDrop = 0.045f,
    eyeX = 0.1235f, eyeY = 0.307f, eyeR = 0.087f, pupilR = 0.058f, ringR = 0.108f, glassesR = 0.129f,
    templeX = 0.294f, templeH = 0.054f,
    muzzleY = 0.42f, muzzleRx = 0.085f, muzzleRy = 0.075f,
    cheekX = 0.22f, cheekY = 0.43f, cheekW = 0.083f, cheekH = 0.049f,
    noseY = 0.385f, noseW = 0.04f, noseH = 0.023f,
    mouthY = 0.43f, mouthW = 0.088f, mouthH = 0.034f,
    bellyY = 0.74f, bellyRx = 0.173f, bellyRy = 0.174f,
    shoulderX = 0.245f, shoulderY = 0.56f, armW = 0.06f,
    tail = listOf(Offset(0.25f, 0.86f), Offset(0.44f, 0.82f), Offset(0.29f, 0.56f), Offset(0.36f, 0.41f)), tailW = 0.04f
)

/** Her hands' place either side of her middle, as a share of her drawing's width: LilyHands spans 0.62 of it, hands at 0.18 and 0.82. */
internal const val HAND_SPAN = 0.62f
internal const val HAND_DX = HAND_SPAN * (0.82f - 0.5f)

/** Her figure is this many times her drawing's height (her lower body is behind the panel), from this far down. */
internal const val FIGURE_SCALE = 1.18f
internal const val FIGURE_TOP = 0.03f

/**
 * Where the bottom of her face is (her muzzle, below her smile), as a share of her drawing's height: it must stay
 * above LILY_FACE_BOTTOM.
 */
internal fun faceBottom(shape: LilyShape): Float =
    FIGURE_TOP + maxOf(shape.mouthY + shape.mouthH * 2.2f, shape.muzzleY + shape.muzzleRy) * FIGURE_SCALE

/**
 * Her shade, from lily_idle.png: a band of darker orange along her right side, as wide as this share of the part
 * (a copy of the part this far to the left covers the rest).
 */
internal const val SHADE_SHIFT = 0.05f

// ---- What she looks at, and how her ears and tail answer the opening ---------------------------------------------

/** Where her eyes look during the opening, -1..1 across and down, as keys in ms: held between, eased from one to the next. */
private val GAZE_KEYS = listOf(
    0f to Offset(0f, 0f),
    LILY_PEEK_END_MS + 10f to Offset(0f, 0f),
    LILY_PEEK_END_MS + 80f to Offset(-0.75f, 0.1f),      // peeking over the wall: left...
    LILY_PEEK_END_MS + 140f to Offset(-0.75f, 0.1f),
    LILY_PEEK_END_MS + 210f to Offset(0.75f, 0.1f),      // ...and right
    LILY_CROUCH_MS + 40f to Offset(0.75f, 0.1f),
    LILY_CROUCH_MS + 110f to Offset(0f, -0.6f),          // up, as she jumps
    TITLE_MS.toFloat() to Offset(-0.6f, -0.9f),          // the letters as they fall, left to right
    (TITLE_MS + (TITLE.length - 1) * TITLE_STAGGER_MS + LETTER_MS / 2).toFloat() to Offset(0.6f, -0.9f),
    START_POP_MS + 80f to Offset(0f, 0.9f),              // down at Start as it pops in
    LILY_WINK_MS - 120f to Offset(0f, 0.9f),
    LILY_WINK_MS - 30f to Offset(0f, 0f)                 // then at the child, and the wink
)

private fun smooth(t: Float): Float = t * t * (3f - 2f * t)
private fun bump(t: Float): Float = if (t <= 0f || t >= 1f) 0f else sin(PI.toFloat() * t)
private fun seg(t: Float, a: Float, b: Float): Float = ((t - a) / (b - a)).coerceIn(0f, 1f)

internal fun openingGazeAt(ms: Float): Offset {
    val next = GAZE_KEYS.indexOfFirst { it.first > ms }
    if (next == -1) return GAZE_KEYS.last().second
    if (next == 0) return GAZE_KEYS.first().second
    val (t0, a) = GAZE_KEYS[next - 1]
    val (t1, b) = GAZE_KEYS[next]
    val k = smooth(seg(ms, t0, t1))
    return Offset(a.x + (b.x - a.x) * k, a.y + (b.y - a.y) * k)
}

/** How far her ears turn up and out, in degrees: as she peeks over the wall, and as Start pops in. */
internal fun earPerkAt(ms: Float): Float =
    8f * bump(seg(ms, LILY_PEEK_END_MS.toFloat(), LILY_CROUCH_MS.toFloat())) +
        12f * bump(seg(ms, START_POP_MS.toFloat(), START_POP_MS + 500f))

/** Her tail swings behind her as she moves, in degrees (follow-through): it answers her rise a moment late. */
internal fun tailLagAt(ms: Float): Float {
    val v = (lilyAt(ms - 60f).shown - lilyAt(ms - 110f).shown) / 50f
    return (v * 3500f).coerceIn(-22f, 22f)
}

// ---- Her own little life, after the opening --------------------------------------------------------------------

/** How long until her next blink, glance, ear twitch and head tilt: never on a beat, so she never looks wound up. */
internal fun nextBlinkMs(r: Random): Long = r.nextLong(2200L, 5600L)
internal fun nextGlanceMs(r: Random): Long = r.nextLong(1800L, 4200L)
internal fun nextEarTwitchMs(r: Random): Long = r.nextLong(3500L, 8000L)
internal fun nextTiltMs(r: Random): Long = r.nextLong(6000L, 11000L)

/** The reactions a tap gets, one after the other. */
internal enum class TapReaction { GIGGLE, EAR_WIGGLE, WINK, HOP }

internal fun tapReaction(tap: Int): TapReaction = TapReaction.values()[tap % TapReaction.values().size]

/** Her moving parts besides the opening's: where she looks, her ears, her head's tilt, a giggle, a hop. */
internal class LilyLife {
    val gazeX = Animatable(0f)
    val gazeY = Animatable(0f)
    val earL = Animatable(0f)
    val earR = Animatable(0f)
    val tilt = Animatable(0f)
    val happy = Animatable(0f)
    val hop = Animatable(0f)
    private var taps = 0

    suspend fun glance(r: Random) {
        // Mostly little looks about, now and then back at the child.
        val back = r.nextFloat() < 0.35f
        val x = if (back) 0f else r.nextFloat() * 1.2f - 0.6f
        val y = if (back) 0f else r.nextFloat() * 0.7f - 0.4f
        coroutineScope {
            launch { gazeX.animateTo(x, tween(130)) }
            gazeY.animateTo(y, tween(130))
        }
    }

    suspend fun twitchEar(r: Random) {
        val ear = if (r.nextBoolean()) earL else earR
        ear.animateTo(14f, tween(70))
        ear.animateTo(0f, spring(dampingRatio = 0.3f, stiffness = Spring.StiffnessMedium))
    }

    suspend fun tiltHead(r: Random) {
        val side = if (r.nextBoolean()) 1f else -1f
        tilt.animateTo(7f * side, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow))
        delay(r.nextLong(1200L, 2000L))
        tilt.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow))
    }

    /** A tap: the next reaction in turn. */
    suspend fun react(eyes: LilyEyes) {
        when (tapReaction(taps++)) {
            TapReaction.GIGGLE -> {
                happy.animateTo(1f, tween(120))
                for (a in listOf(-6f, 6f, -4f, 3f, 0f)) tilt.animateTo(a, tween(90))
                delay(250L)
                happy.animateTo(0f, tween(160))
            }
            TapReaction.EAR_WIGGLE -> repeat(3) {
                earL.animateTo(16f, tween(80)); earL.animateTo(0f, tween(80))
                earR.animateTo(16f, tween(80)); earR.animateTo(0f, tween(80))
            }
            TapReaction.WINK -> eyes.winkOnce()
            TapReaction.HOP -> {
                hop.snapTo(0f)
                hop.animateTo(1f, tween(560))
                hop.snapTo(0f)
            }
        }
    }
}

/** Her hop after a tap at [p] 0..1: how high (a share of F) and how much she squashes as she lands. */
internal fun tapHopAt(p: Float): Pair<Float, Float> = when {
    p <= 0f || p >= 1f -> 0f to 0f
    p < 0.75f -> 0.06f * bump(p / 0.75f) to 0f
    else -> 0f to bump((p - 0.75f) / 0.25f)
}

/** Everything that moves her, read in the draw phase so a frame only redraws. */
internal class LilyPose(
    val gaze: Offset, val earOut: Float, val earL: Float, val earR: Float, val tilt: Float,
    val headBob: Float, val bellyBreath: Float, val tailBend: Float,
    val happy: Float, val hopLift: Float, val hopSquash: Float
)

/**
 * Lily, drawn in parts. [pose] gives every moving part; [ledge] is how far above her drawing's bottom the panel's top
 * edge is, where her arms reach down to her hands.
 */
@Composable
internal fun LilyPuppet(
    eyes: LilyEyes,
    voice: VoiceMouth,
    ledge: Dp,
    pose: () -> LilyPose,
    modifier: Modifier = Modifier
) {
    val s = LILY_SHAPE
    Spacer(
        modifier = modifier.drawWithCache {
            val h = size.height
            val f = h * FIGURE_SCALE
            val top = h * FIGURE_TOP
            val cx = size.width / 2f
            val line = maxOf(2.dp.toPx(), f * 0.007f)
            val ledgeY = h - ledge.toPx()
            // Her hands sit this far either side of her middle on the panel's edge (LilyHands, as the old paws did).
            val handDx = size.width * HAND_DX
            fun p(x: Float, y: Float) = Offset(cx + x * f, top + y * f)
            onDrawBehind {
                val q = pose()
                // Her head is the top of her one round body, so she moves as one piece (user, 2026-11-07: "body
                // feels ... like its not attached together"): body, ears, face, tail and arms share one lean, breath
                // and hop. Only her eyes, lids, mouth, ears' twitch and tail's sway move on their own, as they
                // would on a real body. Her hands stay on the panel; her arms reach them however she moves.
                val m = figureMove(q)
                val feet = p(0f, 1f)
                val hinge = Offset(cx, ledgeY)
                val hands = listOf(-1f, 1f).map { side -> Offset(cx + side * handDx, ledgeY).intoFigure(m, f, feet, hinge) }
                withTransform({
                    translate(top = m.lift * f)
                    scale(m.scaleX, m.scaleY, pivot = feet)
                    rotate(m.lean, pivot = hinge)
                }) {
                    drawTail(s, q, f, ::p)
                    drawEars(s, q, f, ::p)
                    drawBody(s, f, ::p)
                    drawArms(s, f, ::p, hands)
                    drawFace(s, q, eyes, voice, f, line, ::p)
                }
            }
        }
    )
}

/**
 * How her whole figure moves at one moment, every part together: up by [lift] (a share of her figure's height),
 * swelling or squashing from her feet, and leaning [lean] degrees from where her body meets the panel.
 */
internal data class FigureMove(val lift: Float, val scaleX: Float, val scaleY: Float, val lean: Float)

/** A tilt of her head becomes this much lean of her whole body: her head is its top. */
internal const val LEAN_SHARE = 0.6f

internal fun figureMove(q: LilyPose) = FigureMove(
    lift = q.headBob - q.hopLift,
    // A hop lands with a squash from her feet; she breathes with her whole body, a little more up than across.
    scaleX = (1f + 0.05f * q.hopSquash) * (1f + 0.008f * q.bellyBreath),
    scaleY = (1f - 0.06f * q.hopSquash) * (1f + 0.015f * q.bellyBreath),
    lean = q.tilt * LEAN_SHARE
)

/** Where this point on screen falls in her moving figure's own drawing: the figure's move undone. */
internal fun Offset.intoFigure(m: FigureMove, f: Float, feet: Offset, hinge: Offset): Offset {
    val t = Offset(x, y - m.lift * f)
    val s = Offset(feet.x + (t.x - feet.x) / m.scaleX, feet.y + (t.y - feet.y) / m.scaleY)
    return s.rotatedAbout(hinge, -m.lean)
}

/** Where a point of her figure's drawing shows on screen as the figure moves (as withTransform draws it). */
internal fun Offset.outOfFigure(m: FigureMove, f: Float, feet: Offset, hinge: Offset): Offset {
    val r = rotatedAbout(hinge, m.lean)
    val s = Offset(feet.x + (r.x - feet.x) * m.scaleX, feet.y + (r.y - feet.y) * m.scaleY)
    return Offset(s.x, s.y + m.lift * f)
}

/** A part in her orange with the pictures' shade along its right: the shade colour, then the lit colour a little to the left over it. */
private fun DrawScope.shaded(path: Path, width: Float, lit: Color = LilyOrange, shade: Color = LilyOrangeShade) {
    drawPath(path, shade)
    clipPath(path) { translate(left = -width * SHADE_SHIFT) { drawPath(path, lit) } }
}

internal fun Offset.rotatedAbout(pivot: Offset, degrees: Float): Offset {
    val a = degrees * PI.toFloat() / 180f
    val d = this - pivot
    return pivot + Offset(d.x * cos(a) - d.y * sin(a), d.x * sin(a) + d.y * cos(a))
}

private fun DrawScope.drawTail(s: LilyShape, q: LilyPose, f: Float, p: (Float, Float) -> Offset) {
    val pts = s.tail.map { p(it.x, it.y) }
    val root = pts[0]
    // The bend grows towards the tip, so the tail curls rather than turning stiffly.
    val c1 = pts[1].rotatedAbout(root, q.tailBend * 0.3f)
    val c2 = pts[2].rotatedAbout(root, q.tailBend * 0.7f)
    val tip = pts[3].rotatedAbout(root, q.tailBend)
    val path = Path().apply { moveTo(root.x, root.y); cubicTo(c1.x, c1.y, c2.x, c2.y, tip.x, tip.y) }
    val w = s.tailW * f
    drawPath(path, LilyOrangeShade, style = Stroke(w, cap = StrokeCap.Round, join = StrokeJoin.Round))
    // Her pictures end the tail in a tuft: three pointed locks fanned out along the way the tail ends.
    val dir = atan2(tip.y - c2.y, tip.x - c2.x) * 180f / PI.toFloat() + 90f
    for ((angle, length) in listOf(-38f to 2.1f, 0f to 2.6f, 38f to 2.1f)) {
        rotate(dir + angle, pivot = tip) {
            val lock = Path().apply {
                moveTo(tip.x - w * 0.62f, tip.y + w * 0.2f)
                quadraticBezierTo(tip.x - w * 0.55f, tip.y - w * length * 0.7f, tip.x, tip.y - w * length)
                quadraticBezierTo(tip.x + w * 0.55f, tip.y - w * length * 0.7f, tip.x + w * 0.62f, tip.y + w * 0.2f)
                close()
            }
            drawPath(lock, LilyOrangeShade)
        }
    }
}

/** Her ears, behind her head: an orange disc with the pictures' peach inside, turning out and up from its base. */
private fun DrawScope.drawEars(s: LilyShape, q: LilyPose, f: Float, p: (Float, Float) -> Offset) {
    for (side in listOf(-1f, 1f)) {
        val c = p(side * s.earX, s.earY)
        val base = p(side * s.earX * 0.55f, s.earY + s.earR * 0.9f)
        val turn = side * (q.earOut + if (side < 0f) q.earL else q.earR)
        rotate(turn, pivot = base) {
            drawCircle(LilyOrangeShade, s.earR * f, c)
            drawCircle(LilyEarInner, s.earInnerR * f, c + Offset(side * 0.005f * f, s.earInnerDrop * f * 0.6f))
        }
    }
}

/** Her body, head at its rounded top, with her belly (she breathes with her whole figure: figureMove). */
private fun DrawScope.drawBody(s: LilyShape, f: Float, p: (Float, Float) -> Offset) {
    val tl = p(-s.bodyHalfW, s.bodyTop)
    val br = p(s.bodyHalfW, 1.3f)
    val r = CornerRadius(s.bodyHalfW * f, s.domeRy * f)
    val body = Path().apply {
        addRoundRect(RoundRect(Rect(tl, br), topLeft = r, topRight = r, bottomLeft = CornerRadius.Zero, bottomRight = CornerRadius.Zero))
    }
    shaded(body, 2f * s.bodyHalfW * f)
    drawOval(LilyCream, p(-s.bellyRx, s.bellyY - s.bellyRy), Size(s.bellyRx * 2f * f, s.bellyRy * 2f * f))
}

/** Her arms reach down to her hands on the panel's edge: her orange, with the pictures' darker edge on the outside. */
private fun DrawScope.drawArms(s: LilyShape, f: Float, p: (Float, Float) -> Offset, hands: List<Offset>) {
    for ((i, side) in listOf(-1f, 1f).withIndex()) {
        val shoulder = p(side * s.shoulderX, s.shoulderY)
        val hand = hands[i]
        val path = Path().apply {
            moveTo(shoulder.x, shoulder.y)
            quadraticBezierTo(shoulder.x + side * 0.07f * f, (shoulder.y + hand.y) / 2f, hand.x, hand.y)
        }
        val w = s.armW * f
        drawPath(path, LilyOrangeShade, style = Stroke(w, cap = StrokeCap.Round))
        translate(left = -side * w * 0.16f) { drawPath(path, LilyOrange, style = Stroke(w * 0.62f, cap = StrokeCap.Round)) }
    }
}

private fun DrawScope.drawFace(
    s: LilyShape, q: LilyPose, eyes: LilyEyes, voice: VoiceMouth, f: Float, line: Float, p: (Float, Float) -> Offset
) {
    // The peach of her glasses-like eye rings, with a tab at each side, and her muzzle below, all one colour.
    for (side in listOf(-1f, 1f)) {
        drawCircle(LilyPeach, s.glassesR * f, p(side * s.eyeX, s.eyeY))
        val inner = p(side * s.eyeX, s.eyeY - s.templeH / 2f)
        val outer = p(side * s.templeX, s.eyeY + s.templeH / 2f)
        drawRoundRect(
            LilyPeach, Offset(minOf(inner.x, outer.x), inner.y), Size(kotlin.math.abs(outer.x - inner.x), outer.y - inner.y),
            CornerRadius(s.templeH * f * 0.3f)
        )
    }
    drawOval(LilyPeach, p(-s.muzzleRx, s.muzzleY - s.muzzleRy), Size(s.muzzleRx * 2f * f, s.muzzleRy * 2f * f))
    // where her rings meet above the muzzle the peach is one piece, as in her pictures
    drawRect(LilyPeach, p(-0.045f, s.eyeY + 0.02f), Size(0.09f * f, (s.muzzleY - s.eyeY - 0.02f) * f))
    for (side in listOf(-1f, 1f)) {
        drawOval(LilyBlush, p(side * s.cheekX - s.cheekW / 2f, s.cheekY - s.cheekH / 2f), Size(s.cheekW * f, s.cheekH * f))
    }

    // Eyes: where she looks, her lids coming down as she blinks, happy arcs when she giggles.
    for (side in listOf(-1f, 1f)) {
        val c = p(side * s.eyeX, s.eyeY)
        val r = s.eyeR * f
        val ball = s.pupilR * f
        drawCircle(LilyEyeRing, s.ringR * f, c)
        drawCircle(Color.White, r, c)
        val closed = max(eyes.closed(side), q.happy)
        if (q.happy <= 0.5f) {
            val look = c + Offset(q.gaze.x, q.gaze.y) * ((r - ball) * 0.9f)
            drawCircle(LilyPupil, ball, look)
            // Her pictures' catchlights: a big one up on the left, a small one down on the right.
            drawCircle(Color.White, ball * 0.34f, look + Offset(-ball * 0.32f, -ball * 0.36f))
            drawCircle(Color.White, ball * 0.13f, look + Offset(ball * 0.4f, ball * 0.34f))
        }
        if (closed > 0f) {
            // The lid: the peach of her rings over the white, as her shut eyes are in the celebrating picture.
            val lid = c.y - r + 2f * r * closed
            val bow = lid + r * 0.45f * (1f - closed)
            val edge = Path().apply { moveTo(c.x - r - 1f, lid); quadraticBezierTo(c.x, bow, c.x + r + 1f, lid) }
            val cover = Path().apply {
                moveTo(c.x - r - 1f, c.y - r - 1f)
                lineTo(c.x + r + 1f, c.y - r - 1f)
                lineTo(c.x + r + 1f, lid)
                quadraticBezierTo(c.x, bow, c.x - r - 1f, lid)
                close()
            }
            // A pixel wider than the white, so no rim of it shows round a closed eye.
            clipPath(Path().apply { addOval(Rect(c, r + 1.5f)) }) {
                drawPath(cover, LilyPeach)
                if (closed <= 0.92f && q.happy <= 0.5f) drawPath(edge, LilyPupil, style = Stroke(line, cap = StrokeCap.Round))
            }
            if (q.happy > 0.5f) {
                // Happy eyes: arcs up, like ^ ^, as in the celebrating picture.
                drawArc(LilyPupil, 200f, 140f, false, Offset(c.x - r * 0.62f, c.y - r * 0.2f), Size(r * 1.24f, r * 0.9f),
                    style = Stroke(line * 1.5f, cap = StrokeCap.Round))
            } else if (closed > 0.92f) {
                drawArc(LilyPupil, 20f, 140f, false, Offset(c.x - r * 0.62f, c.y - r * 0.45f), Size(r * 1.24f, r * 0.6f),
                    style = Stroke(line * 1.5f, cap = StrokeCap.Round))
            }
        }
    }

    // Her nose, a soft triangle, and her open smile with its tongue, wider as she speaks or laughs.
    val nose = p(0f, s.noseY)
    val nw = s.noseW * f
    val nh = s.noseH * f
    drawPath(Path().apply {
        moveTo(nose.x - nw / 2f, nose.y)
        quadraticBezierTo(nose.x, nose.y - nh * 0.25f, nose.x + nw / 2f, nose.y)
        quadraticBezierTo(nose.x + nw * 0.2f, nose.y + nh, nose.x, nose.y + nh)
        quadraticBezierTo(nose.x - nw * 0.2f, nose.y + nh, nose.x - nw / 2f, nose.y)
        close()
    }, LilyPupil)
    val open = 0.35f + 0.65f * max(if (voice.speaking) voice.level else 0f, q.happy * 0.8f)
    val mc = p(0f, s.mouthY)
    val w = s.mouthW * f * (1f - 0.15f * open)
    val d = s.mouthH * f * (0.7f + 1.6f * open)
    val y0 = mc.y
    val mouth = Path().apply {
        moveTo(mc.x - w / 2f, y0)
        quadraticBezierTo(mc.x, y0 + s.mouthH * f * 0.25f, mc.x + w / 2f, y0)
        cubicTo(mc.x + w / 2f, y0 + d * 0.9f, mc.x + w * 0.28f, y0 + d, mc.x, y0 + d)
        cubicTo(mc.x - w * 0.28f, y0 + d, mc.x - w / 2f, y0 + d * 0.9f, mc.x - w / 2f, y0)
        close()
    }
    drawPath(mouth, LilyMouthInside)
    clipPath(mouth) { drawOval(LilyTongue, Offset(mc.x - w * 0.32f, y0 + d * 0.5f), Size(w * 0.64f, d * 0.7f)) }
}

/** Her hands on the panel's edge, in front of it, fingers over it: her paws as her pictures draw them, a palm with four round fingertips. */
@Composable
internal fun LilyHands(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier.drawWithCache {
            val w = size.width
            val h = size.height
            val handW = minOf(w * 0.26f, 46.dp.toPx())
            val centres = listOf(w * 0.18f, w * 0.82f)
            onDrawBehind {
                for (x in centres) {
                    val palmTop = h * 0.2f
                    val palmH = h * 0.66f
                    drawRoundRect(LilyOrangeShade, Offset(x - handW * 0.42f, palmTop), Size(handW * 0.84f, palmH), CornerRadius(palmH / 2f))
                    val tip = handW * 0.15f
                    for (k in 0..3) {
                        val fx = x - handW * 0.36f + handW * 0.24f * k
                        drawCircle(LilyOrangeShade, tip, Offset(fx, palmTop + tip * 0.6f))
                    }
                }
            }
        }
    )
}
