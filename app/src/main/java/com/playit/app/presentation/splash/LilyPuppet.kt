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
import com.playit.app.presentation.theme.LilyOutline
import com.playit.app.presentation.theme.LilyPupil
import com.playit.app.presentation.theme.LilyTongue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

// Lily on the opening, as a puppet (user, 2026-10-29: "for our mascot how we make it better the design how it moves
// how can it be natural"). She is drawn in parts (head, ears, eyes, body, belly, arms, tail), so each part moves on its
// own: her eyes look at what happens, her ears twitch and perk up, her tail sways and swings behind a jump, she breathes
// with her belly, tilts her head, blinks at odd moments and answers a tap a different way each time.
// Her finish is her pictures' (user, 2026-10-30: "enhance the character the design"; images/mascot/lily_idle.png):
// a darker shade along her lower right, a shine on her head, big pupils with the pictures' catchlights, a light
// stripe down her tail and its knot of a tip.

/** Her parts, in shares of her figure's height F (ears' top to feet), across from her middle and down from her top. */
internal data class LilyShape(
    val headY: Float, val headRx: Float, val headRy: Float,
    val earX: Float, val earY: Float, val earRx: Float, val earRy: Float,
    val eyeX: Float, val eyeY: Float, val eyeR: Float, val pupilR: Float,
    val cheekX: Float, val cheekY: Float, val cheekW: Float, val cheekH: Float,
    val noseY: Float, val mouthY: Float, val mouthW: Float, val mouthH: Float,
    val bodyY: Float, val bodyR: Float, val bellyY: Float, val bellyRx: Float, val bellyRy: Float,
    val shoulderX: Float, val shoulderY: Float, val armW: Float,
    val tail: List<Offset>, val tailW: Float
)

/** As her pictures draw her (images/mascot/lily_idle.png, lily_encouraging.png). */
internal val LILY_SHAPE = LilyShape(
    headY = 0.33f, headRx = 0.287f, headRy = 0.268f,
    earX = 0.25f, earY = 0.13f, earRx = 0.125f, earRy = 0.125f,
    eyeX = 0.112f, eyeY = 0.30f, eyeR = 0.098f, pupilR = 0.069f,
    cheekX = 0.205f, cheekY = 0.405f, cheekW = 0.075f, cheekH = 0.042f,
    noseY = 0.385f, mouthY = 0.425f, mouthW = 0.10f, mouthH = 0.05f,
    bodyY = 0.73f, bodyR = 0.27f, bellyY = 0.77f, bellyRx = 0.17f, bellyRy = 0.16f,
    shoulderX = 0.19f, shoulderY = 0.57f, armW = 0.055f,
    tail = listOf(Offset(0.22f, 0.86f), Offset(0.38f, 0.84f), Offset(0.42f, 0.60f), Offset(0.33f, 0.46f)), tailW = 0.03f
)

/** Her hands' place either side of her middle, as a share of her drawing's width: LilyHands spans 0.62 of it, hands at 0.18 and 0.82. */
internal const val HAND_SPAN = 0.62f
internal const val HAND_DX = HAND_SPAN * (0.82f - 0.5f)

/** Her figure is this many times her drawing's height (her lower body is behind the panel), from this far down. */
internal const val FIGURE_SCALE = 1.18f
internal const val FIGURE_TOP = 0.03f

/** Where the bottom of her smile is, as a share of her drawing's height: it must stay above LILY_FACE_BOTTOM. */
internal fun faceBottom(shape: LilyShape): Float = FIGURE_TOP + (shape.mouthY + shape.mouthH) * FIGURE_SCALE

/**
 * Her finish, from lily_idle.png: the shade is a crescent along her lower right, as wide as this share of the part
 * (a copy of the part this far up and left covers the rest); the shine on her head and the stripe down her tail are
 * white over her orange, which gives the pictures' pale peach (#FAB987).
 */
internal const val SHADE_SHIFT = 0.045f
private val Shine = Color.White.copy(alpha = 0.5f)
/** The front lobe of her tail's tuft: only a little lighter than her orange, as in the pictures. */
private val TuftLight = Color.White.copy(alpha = 0.25f)

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
            val line = maxOf(2.5.dp.toPx(), f * 0.009f)
            val ledgeY = h - ledge.toPx()
            // Her hands sit this far either side of her middle on the panel's edge (LilyHands, as the old paws did).
            val handDx = size.width * HAND_DX
            fun p(x: Float, y: Float) = Offset(cx + x * f, top + y * f)
            onDrawBehind {
                val q = pose()
                translate(top = -q.hopLift * f) {
                    withTransform({
                        // A hop lands with a squash from her feet; behind the panel, so only her top shows it.
                        scale(1f + 0.05f * q.hopSquash, 1f - 0.06f * q.hopSquash, pivot = p(0f, 1f))
                    }) {
                        drawTail(s, q, f, line, ::p)
                        drawBody(s, q, f, line, ::p, ledgeY, cx, handDx)
                        withTransform({
                            translate(top = q.headBob * f)
                            rotate(q.tilt, pivot = p(0f, s.headY + s.headRy * 0.85f))
                        }) {
                            drawHead(s, q, eyes, voice, f, line, ::p)
                        }
                    }
                }
            }
        }
    )
}

private fun DrawScope.tube(path: Path, width: Float, color: Color, line: Float) {
    drawPath(path, LilyOutline, style = Stroke(width + 2f * line, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawPath(path, color, style = Stroke(width, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/** An oval part in her orange with the pictures' shade along its lower right. */
private fun DrawScope.shadedOval(r: Rect) {
    drawOval(LilyOrangeShade, r.topLeft, r.size)
    clipPath(Path().apply { addOval(r) }) {
        drawOval(LilyOrange, r.topLeft - Offset(r.width * SHADE_SHIFT, r.height * SHADE_SHIFT), r.size)
    }
}

private fun Offset.rotatedAbout(pivot: Offset, degrees: Float): Offset {
    val a = degrees * PI.toFloat() / 180f
    val d = this - pivot
    return pivot + Offset(d.x * cos(a) - d.y * sin(a), d.x * sin(a) + d.y * cos(a))
}

private fun DrawScope.drawTail(s: LilyShape, q: LilyPose, f: Float, line: Float, p: (Float, Float) -> Offset) {
    val pts = s.tail.map { p(it.x, it.y) }
    val root = pts[0]
    // The bend grows towards the tip, so the tail curls rather than turning stiffly.
    val c1 = pts[1].rotatedAbout(root, q.tailBend * 0.3f)
    val c2 = pts[2].rotatedAbout(root, q.tailBend * 0.7f)
    val tip = pts[3].rotatedAbout(root, q.tailBend)
    val path = Path().apply { moveTo(root.x, root.y); cubicTo(c1.x, c1.y, c2.x, c2.y, tip.x, tip.y) }
    val w = s.tailW * f
    tube(path, w, LilyOrange, line)
    // The light stripe down its length, a little to the left of its middle.
    translate(left = -w * 0.14f) { drawPath(path, Shine, style = Stroke(w * 0.26f, cap = StrokeCap.Round)) }
    // Her pictures end the tail in a tuft of three round bumps, one behind on top and two in front: one outline round
    // them all, her shade on the one behind, and only fine lines inside where the front ones overlap, the left one a
    // little lighter (user, 2026-10-30: each bump outlined on its own looked "like just a bunch of circles"). It leans
    // away from her head, so it stays clear of it as the tail sways.
    val back = tip + Offset(0.45f, -0.75f) * w
    val right = tip + Offset(0.95f, 0.05f) * w
    val left = tip + Offset(-0.15f, 0.05f) * w
    val rb = 0.95f * w
    val rf = 0.7f * w
    listOf(back to rb, right to rf, left to rf).forEach { (o, r) -> drawCircle(LilyOutline, r + line, o) }
    shadedOval(Rect(back, rb))
    // Each fine line runs between the points where its bump's edge meets the outline (169 to 307 degrees on the right
    // one, 242 to 398 on the left one), and the left bump covers the right one's.
    val fine = Stroke(line * 0.6f, cap = StrokeCap.Round)
    drawCircle(LilyOrange, rf, right)
    drawArc(LilyOutline, 169f, 138f, false, right - Offset(rf, rf), Size(2f * rf, 2f * rf), style = fine)
    drawCircle(LilyOrange, rf, left)
    drawCircle(TuftLight, rf, left)
    drawArc(LilyOutline, 242f, 156f, false, left - Offset(rf, rf), Size(2f * rf, 2f * rf), style = fine)
}

private fun DrawScope.drawBody(
    s: LilyShape, q: LilyPose, f: Float, line: Float, p: (Float, Float) -> Offset, ledgeY: Float, cx: Float, handDx: Float
) {
    val body = p(0f, s.bodyY)
    val r = s.bodyR * f
    // She breathes with her belly: the body swells a little from below.
    withTransform({ scale(1f + 0.01f * q.bellyBreath, 1f + 0.02f * q.bellyBreath, pivot = Offset(body.x, body.y + r)) }) {
        shadedOval(Rect(body, r))
        drawCircle(LilyOutline, r, body, style = Stroke(line))
        drawOval(LilyCream, p(-s.bellyRx, s.bellyY - s.bellyRy), Size(s.bellyRx * 2f * f, s.bellyRy * 2f * f))
    }
    // Her arms reach down to her hands on the panel's edge.
    for (side in listOf(-1f, 1f)) {
        val shoulder = p(side * s.shoulderX, s.shoulderY)
        val hand = Offset(cx + side * handDx, ledgeY)
        val path = Path().apply {
            moveTo(shoulder.x, shoulder.y)
            quadraticBezierTo(shoulder.x + side * 0.07f * f, (shoulder.y + hand.y) / 2f, hand.x, hand.y)
        }
        tube(path, s.armW * f, LilyOrange, line)
    }
}

private fun DrawScope.drawHead(
    s: LilyShape, q: LilyPose, eyes: LilyEyes, voice: VoiceMouth, f: Float, line: Float, p: (Float, Float) -> Offset
) {
    // Ears first, so the head covers their bases. They turn out and up from where they meet the head.
    for (side in listOf(-1f, 1f)) {
        val c = p(side * s.earX, s.earY)
        val base = p(side * s.earX * 0.55f, s.earY + s.earRy * 0.9f)
        val turn = side * (q.earOut + if (side < 0f) q.earL else q.earR)
        withTransform({ rotate(turn, pivot = base) }) {
            val outer = Rect(c.x - s.earRx * f, c.y - s.earRy * f, c.x + s.earRx * f, c.y + s.earRy * f)
            shadedOval(outer)
            val inner = Rect(c.x - s.earRx * f * 0.62f, c.y - s.earRy * f * 0.62f, c.x + s.earRx * f * 0.62f, c.y + s.earRy * f * 0.62f)
                .translate(Offset(-side * s.earRx * f * 0.1f, s.earRy * f * 0.1f))
            drawOval(LilyEarInner, inner.topLeft, inner.size)
            drawOval(LilyOutline, outer.topLeft, outer.size, style = Stroke(line))
        }
    }
    val head = Rect(p(-s.headRx, s.headY - s.headRy), p(s.headRx, s.headY + s.headRy))
    shadedOval(head)
    // The shine on her head, as in her pictures: a short bar up on her left, and a dot below it.
    val rx = head.width / 2f
    val ry = head.height / 2f
    val bar = head.center + Offset(-0.54f * rx, -0.66f * ry)
    rotate(-32f, pivot = bar) {
        drawRoundRect(Shine, bar - Offset(0.13f * rx, 0.045f * rx), Size(0.26f * rx, 0.09f * rx), CornerRadius(0.045f * rx))
    }
    drawCircle(Shine, 0.04f * rx, head.center + Offset(-0.71f * rx, -0.47f * ry))
    drawOval(LilyOutline, head.topLeft, head.size, style = Stroke(line))

    // Eyes: where she looks, her lids coming down as she blinks, happy arcs when she giggles.
    for (side in listOf(-1f, 1f)) {
        val c = p(side * s.eyeX, s.eyeY)
        val r = s.eyeR * f
        val ball = s.pupilR * f
        val look = c + Offset(q.gaze.x, q.gaze.y) * ((r - ball) * 0.9f)
        drawCircle(LilyCream, r, c)
        drawCircle(LilyPupil, ball, look)
        // Her pictures' catchlights: a big one up on the left, a small one down on the right.
        drawCircle(Color.White, ball * 0.32f, look + Offset(-ball * 0.3f, -ball * 0.34f))
        drawCircle(Color.White, ball * 0.15f, look + Offset(ball * 0.38f, ball * 0.36f))
        val closed = max(eyes.closed(side), q.happy)
        if (closed > 0f) {
            // The lid: her fur over the cream eye patch.
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
            // A pixel wider than the eye patch, so no rim of cream shows round a closed eye.
            clipPath(Path().apply { addOval(Rect(c, r + 1.5f)) }) {
                drawPath(cover, LilyOrange)
                if (closed <= 0.92f && q.happy <= 0.5f) drawPath(edge, LilyOutline, style = Stroke(line, cap = StrokeCap.Round))
            }
            if (q.happy > 0.5f) {
                // Happy eyes: arcs up, like ^ ^.
                drawArc(LilyPupil, 200f, 140f, false, Offset(c.x - r * 0.6f, c.y - r * 0.15f), Size(r * 1.2f, r * 0.9f),
                    style = Stroke(line * 1.3f, cap = StrokeCap.Round))
            } else if (closed > 0.92f) {
                drawArc(LilyPupil, 20f, 140f, false, Offset(c.x - r * 0.62f, c.y - r * 0.45f), Size(r * 1.24f, r * 0.6f),
                    style = Stroke(line * 1.3f, cap = StrokeCap.Round))
            }
        }
    }

    // Cheeks, nose and mouth.
    for (side in listOf(-1f, 1f)) {
        drawOval(LilyBlush.copy(alpha = 0.85f), p(side * s.cheekX - s.cheekW / 2f, s.cheekY - s.cheekH / 2f), Size(s.cheekW * f, s.cheekH * f))
    }
    drawOval(LilyPupil, p(-0.013f, s.noseY - 0.008f), Size(0.026f * f, 0.018f * f))
    val open = max(if (voice.speaking) voice.level else 0f, q.happy * 0.7f)
    val mw = s.mouthW * f
    val mh = s.mouthH * f
    val mc = p(0f, s.mouthY)
    if (open < 0.08f) {
        drawArc(LilyOutline, 20f, 140f, false, Offset(mc.x - mw / 2f, mc.y - mh / 2f), Size(mw, mh),
            style = Stroke(line * 1.2f, cap = StrokeCap.Round))
    } else {
        val w = mw * (1f - 0.2f * open)
        val d = mh * (0.6f + 1.8f * open)
        val y0 = mc.y
        val mouth = Path().apply {
            moveTo(mc.x - w / 2f, y0)
            quadraticBezierTo(mc.x, y0 + mh * 0.3f, mc.x + w / 2f, y0)
            cubicTo(mc.x + w / 2f, y0 + d * 0.9f, mc.x + w * 0.28f, y0 + d, mc.x, y0 + d)
            cubicTo(mc.x - w * 0.28f, y0 + d, mc.x - w / 2f, y0 + d * 0.9f, mc.x - w / 2f, y0)
            close()
        }
        drawPath(mouth, LilyMouthInside)
        clipPath(mouth) { drawOval(LilyTongue, Offset(mc.x - w * 0.31f, y0 + d * 0.55f), Size(w * 0.62f, d * 0.6f)) }
        drawPath(mouth, LilyOutline, style = Stroke(line * 1.2f, join = StrokeJoin.Round))
    }
}

/** Her hands on the panel's edge, in front of it, fingers over it: cream mittens, as in her pictures. */
@Composable
internal fun LilyHands(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier.drawWithCache {
            val w = size.width
            val h = size.height
            val line = 2.5.dp.toPx()
            val handW = minOf(w * 0.26f, 46.dp.toPx())
            val centres = listOf(w * 0.18f, w * 0.82f)
            onDrawBehind {
                for (x in centres) {
                    val top = h * 0.07f
                    val hh = h * 0.86f
                    val r = CornerRadius(hh / 2f)
                    drawRoundRect(LilyCream, Offset(x - handW / 2f, top), Size(handW, hh), r)
                    drawRoundRect(LilyOutline, Offset(x - handW / 2f, top), Size(handW, hh), r, style = Stroke(line))
                    for (k in 1..2) {
                        val fx = x - handW / 2f + handW * k / 3f
                        drawLine(LilyOutline, Offset(fx, top + hh * 0.55f), Offset(fx, top + hh * 0.92f), strokeWidth = line * 0.8f, cap = StrokeCap.Round)
                    }
                }
            }
        }
    )
}
