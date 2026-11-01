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

// The child's animal on the map is a little front-facing plush animal (user, 2026-11-01: "make it front facing ... make
// it cute", and before that "Dont let them walk on two legs"). The avatar pictures are heads, so the rest is drawn here in
// each animal's colours and the pictures' outline: a slim body, two front legs, and two hind legs peeking out behind them;
// for the owl, which perches and flies, two feet and its wings. Legs are plain round stubs, with no joints to look broken.
// Where they are comes from GaitFrame, the same frame that moves the animal, so they keep time with it.

/** All lengths here are dp in the explorer's 78 dp square, with x from its middle. */
const val RIG_SIZE = 78f

/** Where the front feet stand. The hind feet stand a little up, so they read as behind. */
const val FOOT_Y = 74.5f
private const val HIND_UP = 1.5f
private const val PAW_H = 6.5f

/**
 * Where the legs and body are, all in dp and 0 when standing. Each foot is some way above where it stands (below it if
 * negative, for a leg still on the ground as the body rises). Left and right are the picture's. Front and hind feet go
 * [frontSpread] and [hindSpread] further out, the body and head sink by [sink] (negative is up), and [tail] turns the
 * tail, in degrees. The owl uses its hind feet.
 */
data class LimbPose(
    val frontLeftUp: Float = 0f,
    val frontRightUp: Float = 0f,
    val hindLeftUp: Float = 0f,
    val hindRightUp: Float = 0f,
    val frontSpread: Float = 0f,
    val hindSpread: Float = 0f,
    val sink: Float = 0f,
    val tail: Float = 0f
) {
    /** This pose, part way ([t] 0..1) to [o]. */
    fun towards(o: LimbPose, t: Float): LimbPose {
        fun at(a: Float, b: Float) = a + (b - a) * t
        return LimbPose(
            at(frontLeftUp, o.frontLeftUp), at(frontRightUp, o.frontRightUp),
            at(hindLeftUp, o.hindLeftUp), at(hindRightUp, o.hindRightUp),
            at(frontSpread, o.frontSpread), at(hindSpread, o.hindSpread),
            at(sink, o.sink), at(tail, o.tail)
        )
    }

    companion object {
        val REST = LimbPose()
    }
}

/** A pair of legs, mirrored: a hip's place, its foot's distance from the middle, and the width of the leg and its paw. */
data class Leg(val hipX: Float, val hipY: Float, val footX: Float, val width: Float, val pawWidth: Float)

/** A tail from ([root]), behind the body: a curve through two controls to its [tip], [width] wide, on the side it trails. */
data class Tail(val root: Offset, val c1: Offset, val c2: Offset, val tip: Offset, val width: Float)

/**
 * How an animal is built. [chin] is the bottom of its head in its picture (a fraction of the square, measured); the
 * picture is drawn at [headScale] with its chin at [chinY]. The body is an oval at [bodyY], [bodyW] by [bodyH]. [front] and
 * [hind] are its legs; with [front] null it stands on two feet.
 */
data class AnimalBuild(
    val chin: Float,
    val headScale: Float,
    val chinY: Float,
    val bodyY: Float,
    val bodyW: Float,
    val bodyH: Float,
    val front: Leg?,
    val hind: Leg,
    val tail: Tail?,
    val shadowWidth: Float,
    val fur: Color,
    val belly: Color,
    val paw: Color
)

private val CatBuild = AnimalBuild(
    chin = 0.787f, headScale = 0.86f, chinY = 54f, bodyY = 60f, bodyW = 25f, bodyH = 22f,
    front = Leg(6f, 64f, 6f, 6.5f, 9f),
    hind = Leg(11f, 62f, 13.5f, 6.5f, 9f),
    tail = Tail(Offset(-9f, 62f), Offset(-22f, 64f), Offset(-29f, 52f), Offset(-24f, 42f), width = 4.5f),
    shadowWidth = 0.6f,
    fur = Color(0xFFFA9A27), belly = Color(0xFFFDEBCD), paw = Color(0xFFFDEBCD)
)

private val MonkeyBuild = AnimalBuild(
    chin = 0.736f, headScale = 0.88f, chinY = 50f, bodyY = 58f, bodyW = 22f, bodyH = 22f,
    // Long arms, set a little wider than its hips.
    front = Leg(9f, 56f, 10f, 5.5f, 8.5f),
    hind = Leg(6f, 64f, 6.5f, 6f, 9f),
    tail = Tail(Offset(-8f, 64f), Offset(-22f, 70f), Offset(-32f, 56f), Offset(-22f, 48f), width = 3.5f),
    shadowWidth = 0.55f,
    fur = Color(0xFF995527), belly = Color(0xFFFDE3B9), paw = Color(0xFFFDE3B9)
)

private val BunnyBuild = AnimalBuild(
    // Dainty: narrow front legs, long hind feet to the sides.
    chin = 0.809f, headScale = 0.84f, chinY = 54f, bodyY = 61f, bodyW = 22f, bodyH = 21f,
    front = Leg(4.5f, 65f, 4.5f, 5.5f, 8f),
    hind = Leg(9.5f, 64f, 12f, 6.5f, 12f),
    tail = null,
    shadowWidth = 0.55f,
    fur = Color(0xFFFCF8F1), belly = Color(0xFFFFFFFF), paw = Color(0xFFFCF8F1)
)

private val BearBuild = AnimalBuild(
    // Sturdy short legs, not a big body.
    chin = 0.768f, headScale = 0.88f, chinY = 55f, bodyY = 61f, bodyW = 27f, bodyH = 21f,
    front = Leg(6.5f, 65f, 6.5f, 8f, 10.5f),
    hind = Leg(12f, 64f, 14f, 8f, 10.5f),
    tail = null,
    shadowWidth = 0.65f,
    fur = Color(0xFFAF6A35), belly = Color(0xFFEDC99C), paw = Color(0xFFEDC99C)
)

private val FrogBuild = AnimalBuild(
    // Its head right on its small body, hind legs set out wide with big flat feet.
    chin = 0.760f, headScale = 0.88f, chinY = 58f, bodyY = 62f, bodyW = 25f, bodyH = 20f,
    front = Leg(5f, 66f, 5.5f, 5.5f, 8.5f),
    hind = Leg(13f, 63f, 17f, 8f, 13f),
    tail = null,
    shadowWidth = 0.7f,
    fur = Color(0xFF8BC034), belly = Color(0xFFEAE794), paw = Color(0xFF8BC034)
)

private val OwlBuild = AnimalBuild(
    // A bird, on its two feet: a smaller head on a round body, its wings at its sides.
    chin = 0.809f, headScale = 0.74f, chinY = 46f, bodyY = 55f, bodyW = 30f, bodyH = 32f,
    front = null,
    hind = Leg(6f, 66f, 6.5f, 4.5f, 9f),
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

/** How wide its head is drawn, in dp: the pictures are cut out at 62 % of their square. */
val AnimalBuild.headWidth: Float get() = 0.62f * RIG_SIZE * headScale

/** How far to move the head picture down, in dp (it is scaled about the square's middle), so its chin is where it goes. */
fun AnimalBuild.headDrop(pose: LimbPose): Float =
    chinY + pose.sink - (RIG_SIZE / 2f + (chin * RIG_SIZE - RIG_SIZE / 2f) * headScale)

/** The body and legs of [animal] in [frame], for drawing behind its head. Its tail trails away from [facing]. */
fun DrawScope.drawExplorerBody(animal: CompanionAnimal, frame: GaitFrame, facing: Float) {
    val b = animal.build
    val pose = frame.limbs
    val u = size.width / RIG_SIZE
    val line = 1.5.dp.toPx()
    val mid = size.width / 2f
    fun at(x: Float, y: Float) = Offset(mid + x * u, y * u)

    // A round stub of a leg from its hip to its paw. A leg behind the body is a little darker.
    fun leg(leg: Leg, side: Float, up: Float, spread: Float, behind: Boolean) {
        val shade = if (behind) 0.86f else 1f
        val pawShade = if (behind) 0.94f else 1f
        val fur = Color(b.fur.red * shade, b.fur.green * shade, b.fur.blue * shade)
        val paw = Color(b.paw.red * pawShade, b.paw.green * pawShade, b.paw.blue * pawShade)
        val hip = at(side * leg.hipX, leg.hipY + pose.sink)
        val footY = FOOT_Y - up - if (behind) HIND_UP else 0f
        val foot = at(side * (leg.footX + spread), footY)
        drawLine(LilyOutline, hip, foot, leg.width * u + 2f * line, StrokeCap.Round)
        drawLine(fur, hip, foot, leg.width * u, StrokeCap.Round)
        val pawSize = Size(leg.pawWidth * u, PAW_H * u)
        val corner = foot + Offset(-pawSize.width / 2f, -pawSize.height / 2f)
        drawOval(paw, corner, pawSize)
        drawOval(LilyOutline, corner, pawSize, style = Stroke(line))
        // Two little toes.
        for (toe in listOf(-1f, 1f)) {
            val x = foot.x + toe * pawSize.width * 0.14f
            drawLine(LilyOutline, Offset(x, foot.y + pawSize.height * 0.08f), Offset(x, foot.y + pawSize.height * 0.42f), line * 0.7f, StrokeCap.Round)
        }
    }

    b.tail?.let { drawTail(it, b, pose, facing) }
    if (b.front == null) drawOwlWings(b, frame.wings, frame.flap, pose.sink)

    leg(b.hind, -1f, pose.hindLeftUp, pose.hindSpread, behind = b.front != null)
    leg(b.hind, 1f, pose.hindRightUp, pose.hindSpread, behind = b.front != null)

    // Its body, and its pale front.
    val bodySize = Size(b.bodyW * u, b.bodyH * u)
    val center = at(0f, b.bodyY + pose.sink)
    val corner = center - Offset(bodySize.width / 2f, bodySize.height / 2f)
    drawOval(b.fur, corner, bodySize)
    val pale = Size(bodySize.width * 0.62f, bodySize.height * 0.66f)
    drawOval(b.belly, Offset(center.x - pale.width / 2f, corner.y + bodySize.height - pale.height - 1.5f * u), pale)
    drawOval(LilyOutline, corner, bodySize, style = Stroke(line))

    b.front?.let {
        leg(it, -1f, pose.frontLeftUp, pose.frontSpread, behind = false)
        leg(it, 1f, pose.frontRightUp, pose.frontSpread, behind = false)
    }
}

private fun DrawScope.drawTail(t: Tail, b: AnimalBuild, pose: LimbPose, facing: Float) {
    val u = size.width / RIG_SIZE
    val mid = size.width / 2f
    val line = 1.5.dp.toPx()
    // Written trailing to the left, as when it faces right.
    fun at(p: Offset) = Offset(mid + facing * p.x * u, (p.y + pose.sink) * u)
    val root = at(t.root)
    val c1 = at(t.c1)
    val c2 = at(t.c2)
    val tip = at(t.tip)
    val curve = Path().apply {
        moveTo(root.x, root.y)
        cubicTo(c1.x, c1.y, c2.x, c2.y, tip.x, tip.y)
    }
    rotate(degrees = facing * pose.tail, pivot = root) {
        drawPath(curve, LilyOutline, style = Stroke(t.width * u + 2f * line, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(curve, b.fur, style = Stroke(t.width * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** The owl's feather line. */
private val OwlPurpleShade = Color(0xFF704080)

/**
 * The owl's two wings at its shoulders. At [open] 0 they are folded down its sides; at 1 they are spread, at [flap] in
 * their beat (-1 raised, 1 pushed down). Each is a flat rounded wing with three feather tips.
 */
private fun DrawScope.drawOwlWings(b: AnimalBuild, open: Float, flap: Float, sink: Float) {
    val w = size.width
    val u = w / RIG_SIZE
    val length = w * 0.40f * (0.44f + 0.56f * open)
    val depth = w * 0.24f * (0.60f + 0.40f * open)
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
    val angle = 78f + (flying - 78f) * open
    for (side in listOf(-1f, 1f)) {
        withTransform({
            translate(left = w / 2f + side * (b.bodyW / 2f - 2.5f) * u, top = (b.chinY + 1f + sink) * u)
            scale(scaleX = side, scaleY = 1f, pivot = Offset.Zero)
            rotate(degrees = angle, pivot = Offset.Zero)
        }) {
            drawPath(wing, b.fur)
            drawPath(feather, OwlPurpleShade, style = Stroke(line, cap = StrokeCap.Round))
            drawPath(wing, LilyOutline, style = Stroke(line, join = StrokeJoin.Round))
        }
    }
}
