package com.playit.app.presentation.map.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.LilyOutline
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// The child's animal on the map has a body, arms and legs under its head (user, 2026-11-01: "the animals or the
// characters to have limbs ... it needs to aligns on the animation"). The avatar pictures are heads, so the rest is drawn
// here in each animal's colours and the pictures' outline. Where the limbs are comes from GaitFrame, the same frame that
// moves the animal, so a foot lifts on the step and the knees bend in the crouch.

/** All lengths here are dp in the explorer's 78 dp square. */
const val RIG_SIZE = 78f
private const val CHIN_Y = 50f
private const val BODY_Y = 58f
private const val BODY_H = 24f
private const val SHOULDER_Y = 52f
private const val ARM_LENGTH = 14f
private const val ARM_WIDTH = 6.5f
private const val HAND_R = 3.8f
private const val HIP_X = 6f
private const val HIP_Y = 64f
private const val FOOT_X = 8f
const val FOOT_Y = 74.5f
const val THIGH = 6f
const val SHIN = 6f
private const val LEG_WIDTH = 6.5f
private const val FOOT_H = 6f

/** Arms hang this far out from straight down. */
const val ARM_REST = 14f

/**
 * Where the limbs are: each arm's angle out from straight down (degrees), how far each foot is above where it stands
 * (dp, and below it if negative, for a leg pushing down while the body rises), how much further out both feet are, and
 * how far the hips and everything above them sink. Left and right are the picture's. The animal faces the child.
 */
data class LimbPose(
    val armL: Float = ARM_REST,
    val armR: Float = ARM_REST,
    val footL: Float = 0f,
    val footR: Float = 0f,
    val spread: Float = 0f,
    val sink: Float = 0f
) {
    companion object {
        val REST = LimbPose()
    }
}

/**
 * How an animal is built: the bottom of its head in its picture (a fraction of the square, measured), its body's width
 * and foot's width in dp, and its colours (fur, belly, and hands and feet).
 */
data class AnimalBuild(
    val chin: Float,
    val bodyWidth: Float,
    val footWidth: Float,
    val fur: Color,
    val belly: Color,
    val paw: Color
)

/** Sampled from each avatar picture. */
val CompanionAnimal.build: AnimalBuild
    get() = when (this) {
        CompanionAnimal.CAT -> AnimalBuild(0.787f, 26f, 10f, Color(0xFFFA9A27), Color(0xFFFDEBCD), Color(0xFFFDEBCD))
        CompanionAnimal.MONKEY -> AnimalBuild(0.736f, 25f, 10f, Color(0xFF995527), Color(0xFFFDE3B9), Color(0xFFFDE3B9))
        CompanionAnimal.BUNNY -> AnimalBuild(0.809f, 25f, 11f, Color(0xFFFCF8F1), Color(0xFFFFFFFF), Color(0xFFFCF8F1))
        CompanionAnimal.BEAR -> AnimalBuild(0.768f, 31f, 11f, Color(0xFFAF6A35), Color(0xFFEDC99C), Color(0xFFEDC99C))
        CompanionAnimal.FROG -> AnimalBuild(0.760f, 28f, 14f, Color(0xFF8BC034), Color(0xFFEAE794), Color(0xFF8BC034))
        CompanionAnimal.OWL -> AnimalBuild(0.809f, 27f, 9f, Color(0xFF8E58A1), Color(0xFFF9EDD6), Color(0xFFF2A33A))
    }

/** How far the head picture is drawn above where it was, so its chin meets the body. */
val AnimalBuild.headRise: Float get() = chin * RIG_SIZE - CHIN_Y

/** A hip and its foot on [side] (-1 left, 1 right) in [pose], x from the middle of the animal, y down from the square's top. */
fun hipAt(side: Float, pose: LimbPose) = Offset(side * HIP_X, HIP_Y + pose.sink)

fun footAt(side: Float, pose: LimbPose) =
    Offset(side * (FOOT_X + pose.spread), FOOT_Y - if (side < 0f) pose.footL else pose.footR)

/**
 * The knee of a leg from [hip] to [foot]: the point [THIGH] from the hip and [SHIN] from the foot, bending out on [side],
 * so a crouching animal's knees go out while its feet stay where they are. A foot beyond the leg's reach gets a straight leg.
 */
fun kneeOf(hip: Offset, foot: Offset, side: Float): Offset {
    val dx = foot.x - hip.x
    val dy = foot.y - hip.y
    val d = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
    val along = ((THIGH * THIGH - SHIN * SHIN + d * d) / (2f * d)).coerceIn(-THIGH, THIGH)
    val out = sqrt((THIGH * THIGH - along * along).coerceAtLeast(0f))
    val perpX = -dy / d
    val perpY = dx / d
    // Whichever of the two knee places lies on the outside.
    val sign = if (perpX * side >= 0f) 1f else -1f
    return Offset(hip.x + dx / d * along + sign * perpX * out, hip.y + dy / d * along + sign * perpY * out)
}

/** The body and limbs of [animal] in [frame], for drawing behind its head. */
fun DrawScope.drawExplorerBody(animal: CompanionAnimal, frame: GaitFrame) {
    val build = animal.build
    val pose = frame.limbs
    val u = size.width / RIG_SIZE
    val line = 1.5.dp.toPx()
    val mid = size.width / 2f
    fun at(p: Offset) = Offset(mid + p.x * u, p.y * u)
    val shoulderX = build.bodyWidth / 2f - 3f
    val shoulderY = SHOULDER_Y + pose.sink

    if (animal == CompanionAnimal.OWL) drawOwlWings(frame.wings, frame.flap, shoulderX, shoulderY)

    for (side in listOf(-1f, 1f)) {
        val hip = hipAt(side, pose)
        val foot = footAt(side, pose)
        val knee = kneeOf(hip, foot, side)
        for ((width, color) in listOf((LEG_WIDTH * u + 2f * line) to LilyOutline, (LEG_WIDTH * u) to build.fur)) {
            drawLine(color, at(hip), at(knee), width, StrokeCap.Round)
            drawLine(color, at(knee), at(foot), width, StrokeCap.Round)
        }
        val footSize = Size(build.footWidth * u, FOOT_H * u)
        // A foot's toes point out a little past its leg.
        val footAt = at(foot) + Offset(side * 1.5f * u - footSize.width / 2f, -footSize.height / 2f)
        drawOval(build.paw, footAt, footSize)
        drawOval(LilyOutline, footAt, footSize, style = Stroke(line))
    }

    val bodyAt = Offset(mid - build.bodyWidth / 2f * u, (BODY_Y + pose.sink - BODY_H / 2f) * u)
    val bodySize = Size(build.bodyWidth * u, BODY_H * u)
    drawOval(build.fur, bodyAt, bodySize)
    val bellySize = Size(bodySize.width * 0.62f, bodySize.height * 0.62f)
    drawOval(build.belly, Offset(mid - bellySize.width / 2f, bodyAt.y + bodySize.height - bellySize.height - 1.5f * u), bellySize)
    drawOval(LilyOutline, bodyAt, bodySize, style = Stroke(line))

    if (animal == CompanionAnimal.OWL) return
    for (side in listOf(-1f, 1f)) {
        val shoulder = Offset(side * shoulderX, shoulderY)
        val angle = (if (side < 0f) pose.armL else pose.armR) * PI.toFloat() / 180f
        val hand = shoulder + Offset(side * sin(angle), cos(angle)) * ARM_LENGTH
        drawLine(LilyOutline, at(shoulder), at(hand), ARM_WIDTH * u + 2f * line, StrokeCap.Round)
        drawLine(build.fur, at(shoulder), at(hand), ARM_WIDTH * u, StrokeCap.Round)
        drawCircle(build.paw, HAND_R * u, at(hand))
        drawCircle(LilyOutline, HAND_R * u, at(hand), style = Stroke(line))
    }
}

/** The owl's feather line. */
private val OwlPurpleShade = Color(0xFF704080)

/**
 * The owl's two wings at its shoulders: its arms. At [open] 0 they are folded down its sides; at 1 they are spread, at
 * [flap] in their beat (-1 raised, 1 pushed down). Each is a flat rounded wing with three feather tips.
 */
private fun DrawScope.drawOwlWings(open: Float, flap: Float, shoulderX: Float, shoulderY: Float) {
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
            drawPath(wing, CompanionAnimal.OWL.build.fur)
            drawPath(feather, OwlPurpleShade, style = Stroke(line, cap = StrokeCap.Round))
            drawPath(wing, LilyOutline, style = Stroke(line, join = StrokeJoin.Round))
        }
    }
}
