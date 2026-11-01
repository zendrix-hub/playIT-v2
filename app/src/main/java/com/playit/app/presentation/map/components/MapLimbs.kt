package com.playit.app.presentation.map.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.LilyOutline
import kotlin.math.sqrt

// The child's animal on the map has a body under its head, and moves as that animal does (user, 2026-11-01: "Dont let
// them walk on two legs, let the cat be cat and bear be bear on how they walk"). The avatar pictures are heads looking at
// the child, so the rest is drawn here, side on behind the head, in each animal's colours and the pictures' outline: four
// legs and a tail, or for the owl, which perches and flies, two feet and its wings. Where the limbs are comes from
// GaitFrame, the same frame that moves the animal, so they keep time with it.

/** All lengths here are dp in the explorer's 78 dp square, with x forward, the way the animal faces, from the middle. */
const val RIG_SIZE = 78f

/** Where the feet stand. The far pair stand a little up and back, so they read as further away. */
const val FOOT_Y = 74.5f
private const val FAR_BACK = 2.5f
private const val FAR_UP = 1.5f
private const val FOOT_H = 5.5f

/**
 * Where the limbs and body are, all in dp and 0 when standing. Each foot is [x] forward of where it stands and [up] above
 * it (below it if negative, for a leg still on the ground as the body rises); front and hind, near and far. The chest and
 * the rump sink ([frontSink], [rearSink]; negative is up, so the body can tip) and stretch apart ([frontOut], [rearOut]);
 * the head goes with the chest. [tail] turns the tail, in degrees. The owl stands facing the child: its near and far hind
 * feet are its two feet, and its chest and rump sink together.
 */
data class LimbPose(
    val frontNearX: Float = 0f,
    val frontNearUp: Float = 0f,
    val frontFarX: Float = 0f,
    val frontFarUp: Float = 0f,
    val hindNearX: Float = 0f,
    val hindNearUp: Float = 0f,
    val hindFarX: Float = 0f,
    val hindFarUp: Float = 0f,
    val frontSink: Float = 0f,
    val rearSink: Float = 0f,
    val frontOut: Float = 0f,
    val rearOut: Float = 0f,
    val tail: Float = 0f
) {
    /** This pose, part way ([t] 0..1) to [o]. */
    fun towards(o: LimbPose, t: Float): LimbPose {
        fun at(a: Float, b: Float) = a + (b - a) * t
        return LimbPose(
            at(frontNearX, o.frontNearX), at(frontNearUp, o.frontNearUp),
            at(frontFarX, o.frontFarX), at(frontFarUp, o.frontFarUp),
            at(hindNearX, o.hindNearX), at(hindNearUp, o.hindNearUp),
            at(hindFarX, o.hindFarX), at(hindFarUp, o.hindFarUp),
            at(frontSink, o.frontSink), at(rearSink, o.rearSink),
            at(frontOut, o.frontOut), at(rearOut, o.rearOut),
            at(tail, o.tail)
        )
    }

    companion object {
        val REST = LimbPose()
    }
}

/** An oval, by its centre and its width and height. */
data class Oval(val x: Float, val y: Float, val w: Float, val h: Float)

/**
 * A pair of legs: the hip's place, the foot's place on the ground, the two bones' lengths, the width of the leg and of the
 * foot, and which way the joint bends (forward like a front leg or a frog's knee, back like a cat's hock).
 */
data class Leg(
    val hipX: Float,
    val hipY: Float,
    val footX: Float,
    val upper: Float,
    val lower: Float,
    val width: Float,
    val footWidth: Float,
    val bendsForward: Boolean
)

/** A tail from ([x], [y]): a curve through two controls to its tip, [width] wide, or a puff of radius [puff]. */
data class Tail(
    val x: Float,
    val y: Float,
    val c1: Offset = Offset.Zero,
    val c2: Offset = Offset.Zero,
    val tip: Offset = Offset.Zero,
    val width: Float = 0f,
    val puff: Float = 0f
)

/**
 * How an animal is built. [chin] is the bottom of its head in its picture (a fraction of the square, measured); the
 * picture is drawn at [headScale] with its middle [headX] forward and its chin at [chinY]. The body is two ovals, [chest]
 * and [rump]. [front] and [hind] are its legs; an animal with [front] null stands on two.
 */
data class AnimalBuild(
    val chin: Float,
    val headScale: Float,
    val headX: Float,
    val chinY: Float,
    val chest: Oval,
    val rump: Oval,
    val front: Leg?,
    val hind: Leg,
    val tail: Tail?,
    val shadowWidth: Float,
    val fur: Color,
    val belly: Color,
    val paw: Color
)

private val CatBuild = AnimalBuild(
    chin = 0.787f, headScale = 0.80f, headX = 12f, chinY = 52f,
    chest = Oval(8f, 54f, 26f, 20f), rump = Oval(-15f, 54f, 28f, 21f),
    front = Leg(9f, 58f, 9f, 9f, 9f, 5.5f, 8f, bendsForward = true),
    hind = Leg(-18f, 58f, -17f, 9f, 9f, 6f, 8.5f, bendsForward = false),
    // A cat's tail, up and curling over at the tip.
    tail = Tail(-27f, 50f, Offset(-38f, 42f), Offset(-38f, 26f), Offset(-30f, 22f), width = 5f),
    shadowWidth = 0.85f,
    fur = Color(0xFFFA9A27), belly = Color(0xFFFDEBCD), paw = Color(0xFFFDEBCD)
)

private val MonkeyBuild = AnimalBuild(
    chin = 0.736f, headScale = 0.82f, headX = 13f, chinY = 46f,
    // Shoulders high and hips low, on its long arms.
    chest = Oval(6f, 50f, 22f, 20f), rump = Oval(-14f, 58f, 24f, 20f),
    front = Leg(9f, 52f, 13f, 11.5f, 11.5f, 5f, 8f, bendsForward = true),
    hind = Leg(-17f, 62f, -15f, 8f, 8f, 5.5f, 9f, bendsForward = true),
    // A long tail with a curl.
    tail = Tail(-25f, 58f, Offset(-40f, 58f), Offset(-40f, 34f), Offset(-28f, 34f), width = 4f),
    shadowWidth = 0.8f,
    fur = Color(0xFF995527), belly = Color(0xFFFDE3B9), paw = Color(0xFFFDE3B9)
)

private val BunnyBuild = AnimalBuild(
    chin = 0.809f, headScale = 0.78f, headX = 12f, chinY = 50f,
    // Hunched: a big round rump, a small chest.
    chest = Oval(8f, 58f, 20f, 22f), rump = Oval(-10f, 57f, 34f, 32f),
    front = Leg(11f, 64f, 13f, 6f, 6f, 4.5f, 7f, bendsForward = true),
    hind = Leg(-13f, 62f, -11f, 9f, 9f, 8f, 19f, bendsForward = true),
    tail = Tail(-27f, 60f, puff = 6f),
    shadowWidth = 0.8f,
    fur = Color(0xFFFCF8F1), belly = Color(0xFFFFFFFF), paw = Color(0xFFFCF8F1)
)

private val BearBuild = AnimalBuild(
    // Big and low: a heavy body and its head carried down in front, on short thick legs.
    chin = 0.768f, headScale = 0.82f, headX = 14f, chinY = 60f,
    chest = Oval(8f, 50f, 34f, 32f), rump = Oval(-12f, 48f, 38f, 36f),
    front = Leg(9f, 62f, 9f, 7f, 7f, 9f, 12f, bendsForward = true),
    hind = Leg(-20f, 62f, -20f, 7f, 7f, 9.5f, 12f, bendsForward = false),
    tail = Tail(-31f, 46f, puff = 3.5f),
    shadowWidth = 0.9f,
    fur = Color(0xFFAF6A35), belly = Color(0xFFEDC99C), paw = Color(0xFFEDC99C)
)

private val FrogBuild = AnimalBuild(
    // Squat, its head on its body, and long hind legs folded at its side.
    chin = 0.760f, headScale = 0.86f, headX = 7f, chinY = 58f,
    chest = Oval(4f, 58f, 26f, 24f), rump = Oval(-10f, 59f, 28f, 24f),
    front = Leg(10f, 64f, 15f, 6f, 6f, 5f, 9f, bendsForward = true),
    hind = Leg(-16f, 60f, -7f, 15f, 15f, 7f, 18f, bendsForward = true),
    tail = null,
    shadowWidth = 0.9f,
    fur = Color(0xFF8BC034), belly = Color(0xFFEAE794), paw = Color(0xFF8BC034)
)

private val OwlBuild = AnimalBuild(
    // A bird: upright, as it perches, with its wings for arms.
    chin = 0.809f, headScale = 1f, headX = 0f, chinY = 50f,
    chest = Oval(0f, 58f, 27f, 24f), rump = Oval(0f, 58f, 27f, 24f),
    front = null,
    hind = Leg(6f, 64f, 8f, 6f, 6f, 5f, 9f, bendsForward = true),
    tail = null,
    shadowWidth = 0.55f,
    fur = Color(0xFF8E58A1), belly = Color(0xFFF9EDD6), paw = Color(0xFFF2A33A)
)

/** Colours sampled from each avatar picture. */
val CompanionAnimal.build: AnimalBuild
    get() = when (this) {
        CompanionAnimal.CAT -> CatBuild
        CompanionAnimal.MONKEY -> MonkeyBuild
        CompanionAnimal.BUNNY -> BunnyBuild
        CompanionAnimal.BEAR -> BearBuild
        CompanionAnimal.FROG -> FrogBuild
        CompanionAnimal.OWL -> OwlBuild
    }

/** How far back from its place it stands, in dp: its head is at the front of its body, and must stay clear of the stop it faces. */
val AnimalBuild.standBack: Float get() = if (front == null) 0f else 12f

/** How far its head reaches forward of the middle of its place, in dp, as it stands there. */
val AnimalBuild.headReach: Float get() = headX + 0.31f * RIG_SIZE * headScale - standBack

/** How far to move the head picture down, in dp (it is scaled about the square's middle), so its chin is where it goes. */
fun AnimalBuild.headDrop(pose: LimbPose): Float =
    chinY + pose.frontSink - (RIG_SIZE / 2f + (chin * RIG_SIZE - RIG_SIZE / 2f) * headScale)

/**
 * The joint of a leg from [hip] to [foot]: the point [upper] from the hip and [lower] from the foot, on the side given by
 * [side] (+x or -x), so the feet can stay where they are while the body sinks. A foot beyond the leg's reach gets a
 * straight leg.
 */
fun kneeOf(hip: Offset, foot: Offset, upper: Float, lower: Float, side: Float): Offset {
    val dx = foot.x - hip.x
    val dy = foot.y - hip.y
    val d = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
    val along = ((upper * upper - lower * lower + d * d) / (2f * d)).coerceIn(-upper, upper)
    val out = sqrt((upper * upper - along * along).coerceAtLeast(0f))
    val perpX = -dy / d
    val perpY = dx / d
    // Whichever of the two places lies on that side.
    val sign = if (perpX * side >= 0f) 1f else -1f
    return Offset(hip.x + dx / d * along + sign * perpX * out, hip.y + dy / d * along + sign * perpY * out)
}

/** The hip of [leg] in [pose], front or hind. */
fun hipOf(leg: Leg, isFront: Boolean, pose: LimbPose): Offset =
    if (isFront) Offset(leg.hipX + pose.frontOut, leg.hipY + pose.frontSink)
    else Offset(leg.hipX - pose.rearOut, leg.hipY + pose.rearSink)

/** The body and limbs of [animal] in [frame], for drawing behind its head, facing [facing] (+1 right, -1 left). */
fun DrawScope.drawExplorerBody(animal: CompanionAnimal, frame: GaitFrame, facing: Float) {
    if (animal.build.front == null) drawPerchedBird(animal.build, frame) else drawFourLegs(animal.build, frame.limbs, facing)
}

private fun Color.inShade() = Color(red * 0.8f, green * 0.8f, blue * 0.8f, alpha)

private fun DrawScope.drawFourLegs(b: AnimalBuild, pose: LimbPose, facing: Float) {
    val front = b.front ?: return
    val u = size.width / RIG_SIZE
    val line = 1.5.dp.toPx()
    val mid = size.width / 2f
    fun at(p: Offset) = Offset(mid + facing * p.x * u, p.y * u)

    fun leg(leg: Leg, isFront: Boolean, x: Float, up: Float, far: Boolean) {
        val shift = if (far) Offset(-FAR_BACK, -FAR_UP) else Offset.Zero
        val hip = hipOf(leg, isFront, pose) + shift
        val foot = Offset(leg.footX + x, FOOT_Y - up) + shift
        val knee = kneeOf(hip, foot, leg.upper, leg.lower, if (leg.bendsForward) 1f else -1f)
        val fur = if (far) b.fur.inShade() else b.fur
        val paw = if (far) b.paw.inShade() else b.paw
        val w = leg.width * u
        for ((width, color) in listOf((w + 2f * line) to LilyOutline, w to fur)) {
            drawLine(color, at(hip), at(knee), width, StrokeCap.Round)
            drawLine(color, at(knee), at(foot), width, StrokeCap.Round)
        }
        val footSize = Size(leg.footWidth * u, FOOT_H * u)
        // A foot's toes reach forward of its leg.
        val center = at(foot + Offset(leg.footWidth * 0.22f, 0f))
        val corner = center - Offset(footSize.width / 2f, footSize.height / 2f)
        drawOval(paw, corner, footSize)
        drawOval(LilyOutline, corner, footSize, style = Stroke(line))
    }

    b.tail?.let { drawTail(it, b, pose, facing) }

    leg(b.hind, false, pose.hindFarX, pose.hindFarUp, far = true)
    leg(front, true, pose.frontFarX, pose.frontFarUp, far = true)

    // The body: both ovals and a band between them in the outline, then again in fur, so it is outlined as one shape
    // however it tips and stretches.
    val chest = Offset(b.chest.x + pose.frontOut, b.chest.y + pose.frontSink)
    val rump = Offset(b.rump.x - pose.rearOut, b.rump.y + pose.rearSink)
    val band = minOf(b.chest.h, b.rump.h) * 0.8f * u
    fun body(color: Color, grow: Float) {
        drawLine(color, at(chest), at(rump), band + 2f * grow, StrokeCap.Round)
        for ((c, o) in listOf(chest to b.chest, rump to b.rump)) {
            val s = Size(o.w * u + 2f * grow, o.h * u + 2f * grow)
            drawOval(color, at(c) - Offset(s.width / 2f, s.height / 2f), s)
        }
    }
    body(LilyOutline, line)
    body(b.fur, 0f)
    // Its pale front, under its chest.
    val pale = Size(b.chest.w * 0.62f * u, b.chest.h * 0.5f * u)
    val paleCenter = at(chest + Offset(b.chest.w * 0.1f, b.chest.h * 0.22f))
    drawOval(b.belly, paleCenter - Offset(pale.width / 2f, pale.height / 2f), pale)

    leg(b.hind, false, pose.hindNearX, pose.hindNearUp, far = false)
    leg(front, true, pose.frontNearX, pose.frontNearUp, far = false)
}

private fun DrawScope.drawTail(t: Tail, b: AnimalBuild, pose: LimbPose, facing: Float) {
    val u = size.width / RIG_SIZE
    val mid = size.width / 2f
    val line = 1.5.dp.toPx()
    val root = Offset(mid + facing * (t.x - pose.rearOut) * u, (t.y + pose.rearSink) * u)
    if (t.puff > 0f) {
        drawCircle(b.fur, t.puff * u, root)
        drawCircle(LilyOutline, t.puff * u, root, style = Stroke(line))
        return
    }
    fun p(o: Offset) = Offset(root.x + facing * o.x * u, root.y + o.y * u)
    val c1 = p(t.c1 - Offset(t.x, t.y))
    val c2 = p(t.c2 - Offset(t.x, t.y))
    val tip = p(t.tip - Offset(t.x, t.y))
    val curve = Path().apply {
        moveTo(root.x, root.y)
        cubicTo(c1.x, c1.y, c2.x, c2.y, tip.x, tip.y)
    }
    rotate(degrees = facing * pose.tail, pivot = root) {
        drawPath(curve, LilyOutline, style = Stroke(t.width * u + 2f * line, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(curve, b.fur, style = Stroke(t.width * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** The owl faces the child, on its two feet, with its wings at its shoulders. */
private fun DrawScope.drawPerchedBird(b: AnimalBuild, frame: GaitFrame) {
    val pose = frame.limbs
    val u = size.width / RIG_SIZE
    val line = 1.5.dp.toPx()
    val mid = size.width / 2f
    fun at(p: Offset) = Offset(mid + p.x * u, p.y * u)
    val shoulderX = b.chest.w / 2f - 3f
    val shoulderY = 52f + pose.frontSink

    drawOwlWings(b, frame.wings, frame.flap, shoulderX, shoulderY)

    val leg = b.hind
    for (side in listOf(-1f, 1f)) {
        val hip = Offset(side * leg.hipX, leg.hipY + pose.frontSink)
        val foot = Offset(side * leg.footX, FOOT_Y - if (side < 0f) pose.hindNearUp else pose.hindFarUp)
        val knee = kneeOf(hip, foot, leg.upper, leg.lower, side)
        for ((width, color) in listOf((leg.width * u + 2f * line) to LilyOutline, (leg.width * u) to b.fur)) {
            drawLine(color, at(hip), at(knee), width, StrokeCap.Round)
            drawLine(color, at(knee), at(foot), width, StrokeCap.Round)
        }
        val footSize = Size(leg.footWidth * u, FOOT_H * u)
        val corner = at(foot) + Offset(side * 1.5f * u - footSize.width / 2f, -footSize.height / 2f)
        drawOval(b.paw, corner, footSize)
        drawOval(LilyOutline, corner, footSize, style = Stroke(line))
    }

    val bodyAt = Offset(mid - b.chest.w / 2f * u, (b.chest.y + pose.frontSink - b.chest.h / 2f) * u)
    val bodySize = Size(b.chest.w * u, b.chest.h * u)
    drawOval(b.fur, bodyAt, bodySize)
    val bellySize = Size(bodySize.width * 0.62f, bodySize.height * 0.62f)
    drawOval(b.belly, Offset(mid - bellySize.width / 2f, bodyAt.y + bodySize.height - bellySize.height - 1.5f * u), bellySize)
    drawOval(LilyOutline, bodyAt, bodySize, style = Stroke(line))
}

/** The owl's feather line. */
private val OwlPurpleShade = Color(0xFF704080)

/**
 * The owl's two wings at its shoulders. At [open] 0 they are folded down its sides; at 1 they are spread, at [flap] in
 * their beat (-1 raised, 1 pushed down). Each is a flat rounded wing with three feather tips.
 */
private fun DrawScope.drawOwlWings(b: AnimalBuild, open: Float, flap: Float, shoulderX: Float, shoulderY: Float) {
    val w = size.width
    val u = w / RIG_SIZE
    val length = w * 0.40f * (0.42f + 0.58f * open)
    val depth = w * 0.24f * (0.55f + 0.45f * open)
    val line = 2.dp.toPx()
    // Pointing out from the hinge (0, 0) along +x: a rounded leading edge to the tip, three feather tips back.
    val wing = Path().apply {
        moveTo(0f, -depth * 0.45f)
        cubicTo(length * 0.40f, -depth * 1.05f, length * 0.92f, -depth * 0.70f, length, -depth * 0.10f)
        quadraticBezierTo(length * 0.93f, depth * 0.38f, length * 0.74f, depth * 0.22f)
        quadraticBezierTo(length * 0.66f, depth * 0.62f, length * 0.47f, depth * 0.36f)
        quadraticBezierTo(length * 0.36f, depth * 0.72f, length * 0.18f, depth * 0.42f)
        quadraticBezierTo(length * 0.06f, depth * 0.50f, 0f, depth * 0.30f)
        close()
    }
    val feather = Path().apply {
        moveTo(length * 0.16f, -depth * 0.10f)
        quadraticBezierTo(length * 0.50f, -depth * 0.30f, length * 0.82f, -depth * 0.12f)
    }
    // In the air: raised to 50 degrees, pushed down to 25 degrees below level. Folded: nearly straight down.
    val flying = -12.5f + 37.5f * flap
    val angle = 74f + (flying - 74f) * open
    for (side in listOf(-1f, 1f)) {
        withTransform({
            translate(left = w / 2f + side * shoulderX * u, top = shoulderY * u)
            scale(scaleX = side, scaleY = 1f, pivot = Offset.Zero)
            rotate(degrees = angle, pivot = Offset.Zero)
        }) {
            drawPath(wing, b.fur)
            drawPath(feather, OwlPurpleShade, style = Stroke(line, cap = StrokeCap.Round))
            drawPath(wing, LilyOutline, style = Stroke(line, join = StrokeJoin.Round))
        }
    }
}
