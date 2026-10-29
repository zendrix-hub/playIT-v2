package com.playit.app.presentation.map.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.components.MAP_OPEN_HOP_MS
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.LilyOutline
import com.playit.app.presentation.theme.LocalReducedMotion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

enum class CompanionAnimal(val id: Int, val displayName: String, val assetPath: String) {
    CAT(1, "Miki", "images/characters/avatar_01_cat.png"),
    MONKEY(2, "Milo", "images/characters/avatar_02_monkey.png"),
    BUNNY(3, "Bella", "images/characters/avatar_03_bunny.png"),
    BEAR(4, "Barnaby", "images/characters/avatar_04_bear.png"),
    FROG(5, "Finley", "images/characters/avatar_05_frog.png"),
    OWL(6, "Ollie", "images/characters/avatar_06_owl.png");

    companion object {
        fun fromId(id: Int): CompanionAnimal = values().find { it.id == id } ?: CAT
    }
}

data class PlacedCompanion(
    val animal: CompanionAnimal,
    val offsetDp: Offset,
    val isExplorerLeader: Boolean = false,
    val isUnlocked: Boolean = false,
    val cheerPhrase: String? = null
)

/** Where the child's explorer stands beside the node at [center] (px), in dp: on the side with more room. */
fun explorerOffsetDp(center: Offset, canvasWidthDp: Float, density: Float): Offset {
    val centerDpX = center.x / density
    val x = if (centerDpX >= canvasWidthDp / 2f) {
        (centerDpX - 98f).coerceAtLeast(10f)
    } else {
        (centerDpX + 54f).coerceAtMost(canvasWidthDp - 88f)
    }
    return Offset(x, center.y / density - 38f)
}

/** How long the explorer walks to a stop that just opened, after waiting for the map to scroll there. */
const val WALK_MS = 1200
const val WALK_DELAY_MS = 600L
private const val WALK_STEPS = 4
private const val WALK_BOB_DP = 10f

/** The walk at [progress] 0..1: how far along (0 at the old stop, 1 at the new) and how high its step bob is, in dp. */
data class WalkFrame(val along: Float, val liftDp: Float)

fun walkAt(progress: Float): WalkFrame {
    val p = progress.coerceIn(0f, 1f)
    // Sets off and arrives gently, with four little steps on the way.
    return WalkFrame(along = p * p * (3f - 2f * p), liftDp = WALK_BOB_DP * abs(sin(PI.toFloat() * WALK_STEPS * p)))
}

/**
 * Calculates companion animal placements along the map trail:
 * - Active Profile Avatar positioned clearly beside the active node with zero overlap.
 * - Supporting 5 animal friends stationed along alternating sides of the trail.
 */
fun generateCompanionPlacements(
    nodeCount: Int,
    nodeCenters: List<Offset>,
    activeNodeIndex: Int,
    activeAvatarId: Int,
    canvasWidthDp: Float,
    density: Float
): List<PlacedCompanion> {
    if (nodeCount <= 0 || canvasWidthDp <= 0f) return emptyList()

    val companions = mutableListOf<PlacedCompanion>()
    val activeAnimal = CompanionAnimal.fromId(activeAvatarId)

    // 1. Place the Active Profile Explorer Avatar clearly beside the active node
    if (nodeCenters.isNotEmpty() && activeNodeIndex in nodeCenters.indices) {
        companions.add(
            PlacedCompanion(
                animal = activeAnimal,
                offsetDp = explorerOffsetDp(nodeCenters[activeNodeIndex], canvasWidthDp, density),
                isExplorerLeader = true,
                isUnlocked = true,
                cheerPhrase = "Let's Go!"
            )
        )
    }

    // 2. Station the other 5 animal friends along the trail
    val supportingAnimals = CompanionAnimal.values().filter { it.id != activeAnimal.id }
    val milestoneInterval = (nodeCount / (supportingAnimals.size + 1)).coerceAtLeast(4)
    val cheerPhrases = listOf("You can do it!", "Keep going!", "Great job!", "Almost there!", "Hooray!")

    supportingAnimals.forEachIndexed { index, animal ->
        val targetNodeIdx = ((index + 1) * milestoneInterval).coerceAtMost(nodeCount - 1)
        if (targetNodeIdx != activeNodeIndex && targetNodeIdx < nodeCenters.size) {
            val center = nodeCenters[targetNodeIdx]
            val centerDpX = center.x / density
            val centerDpY = center.y / density

            val isRight = (index % 2 == 1)
            val compX = if (isRight) {
                (centerDpX + 52f).coerceAtMost(canvasWidthDp - 84f)
            } else {
                (centerDpX - 96f).coerceAtLeast(10f)
            }
            val compY = centerDpY - 30f

            val isUnlocked = targetNodeIdx <= activeNodeIndex

            companions.add(
                PlacedCompanion(
                    animal = animal,
                    offsetDp = Offset(compX, compY),
                    isExplorerLeader = false,
                    isUnlocked = isUnlocked,
                    cheerPhrase = cheerPhrases.getOrNull(index)
                )
            )
        }
    }

    return companions
}

/**
 * Draws the child's own avatar beside the current node. It stands still while nothing happens (card 22:
 * no endless motion on the map; the current node's pulse ring is the one moving thing), and moves only on
 * a meaningful moment (design refresh), in its own way (MapGaits.kt: the cat trots, the owl flies...):
 * - when a stop has just opened, it travels there from [arrivalFromIndex] (the stop the child just finished);
 * - when the map opens, and when the child taps it, it does the same move on the spot.
 * Under reduced motion it stands at the current node and does not move. The other animal friends are no
 * longer drawn; generateCompanionPlacements still lists them for anyone who needs them.
 */
@Composable
fun MapCompanionFriends(
    nodeCount: Int,
    nodeCenters: List<Offset>,
    activeNodeIndex: Int,
    activeAvatarId: Int,
    onCompanionTap: ((CompanionAnimal) -> Unit)? = null,
    modifier: Modifier = Modifier,
    arrivalFromIndex: Int? = null
) {
    if (nodeCount <= 0 || nodeCenters.isEmpty()) return

    BoxWithConstraints(modifier = modifier) {
        val density = androidx.compose.ui.platform.LocalDensity.current.density
        val leader = generateCompanionPlacements(
            nodeCount = nodeCount,
            nodeCenters = nodeCenters,
            activeNodeIndex = activeNodeIndex,
            activeAvatarId = activeAvatarId,
            canvasWidthDp = maxWidth.value,
            density = density
        ).firstOrNull { it.isExplorerLeader } ?: return@BoxWithConstraints

        val reduced = LocalReducedMotion.current
        val from = arrivalFromIndex
            ?.takeIf { it != activeNodeIndex && it in nodeCenters.indices && !reduced }
            ?.let { explorerOffsetDp(nodeCenters[it], maxWidth.value, density) }
        val to = leader.offsetDp
        val gait = leader.animal.gait
        val direction = if (from != null && from.x > to.x) -1f else 1f
        // The animal's move (MapGaits.kt), in ms; its end is at rest. A walk starts at the old stop in the very first
        // frame, so the explorer never flashes at the new one.
        val move = remember(from != null, arrivalFromIndex) { Animatable(if (from != null) 0f else gait.totalMs.toFloat()) }
        var travelling by remember(from != null, arrivalFromIndex) { mutableStateOf(from != null) }
        val scope = rememberCoroutineScope()
        val playOnTheSpot: () -> Unit = {
            if (!reduced && !travelling && !move.isRunning) scope.launch {
                move.snapTo(0f)
                move.animateTo(gait.totalMs.toFloat(), tween(gait.totalMs, easing = LinearEasing))
            }
        }
        LaunchedEffect(from != null, arrivalFromIndex) {
            if (from == null) return@LaunchedEffect
            delay(WALK_DELAY_MS)
            move.animateTo(gait.totalMs.toFloat(), tween(gait.totalMs, easing = LinearEasing))
            travelling = false
        }
        // Map opening: when the wave of stops reaches the current one, the explorer does its move there.
        LaunchedEffect(Unit) {
            if (from != null) return@LaunchedEffect
            delay(MAP_OPEN_HOP_MS.toLong())
            playOnTheSpot()
        }
        val frame: () -> GaitFrame = { gaitAt(gait, move.value, direction) }

        val charSize = 78.dp
        // The avatar pictures are heads cut out at 62 % of their square, so the chin sits this far above its bottom;
        // the shadow and the dust go there, under the head.
        val chinGap = charSize * (1f - 0.62f) / 2f
        Box(
            modifier = Modifier
                // Along the ground from the old stop to the new one; the lift is drawn in the picture's layer.
                .offset {
                    val along = if (from != null && travelling) frame().along else 1f
                    val start = from ?: to
                    IntOffset(
                        (start.x + (to.x - start.x) * along).dp.roundToPx(),
                        (start.y + (to.y - start.y) * along).dp.roundToPx()
                    )
                }
                .size(charSize)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    playOnTheSpot()
                    onCompanionTap?.invoke(leader.animal)
                },
            contentAlignment = Alignment.Center
        ) {
            // The shadow stays on the ground and shrinks and fades as the animal rises; a landing kicks up dust.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -chinGap + 4.dp)
                    .size(width = charSize * 0.65f, height = 8.dp)
                    .graphicsLayer {
                        val away = (frame().liftDp / 90f).coerceIn(0f, 0.6f)
                        scaleX = 1f - away
                        scaleY = 1f - away
                        alpha = 1f - away
                    }
                    .background(Color(0x2E1F3A3D), CircleShape)
            )
            Spacer(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -chinGap + 6.dp)
                    .size(width = charSize * 1.4f, height = 24.dp)
                    .drawBehind { drawLandingDust(frame().dust) }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val f = frame()
                        // Squash and stretch about its centre (so a somersault turns in place), shifted so it stays
                        // standing on the ground.
                        translationY = -f.liftDp.dp.toPx() + size.height * (1f - f.scaleY) / 2f
                        scaleX = f.scaleX
                        scaleY = f.scaleY
                        rotationZ = f.rotation
                    }
                    // The owl's wings, behind its head, so they come out from behind it and move with it.
                    .drawBehind { frame().let { if (it.wings > 0f) drawOwlWings(it.wings, it.flap) } }
            ) {
                Image(
                    painter = rememberAssetPainter(leader.animal.assetPath),
                    contentDescription = "${leader.animal.displayName}, your explorer",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/** The owl's colours, from its picture, and the pictures' dark-brown line. */
private val OwlPurple = Color(0xFF8A55A0)
private val OwlPurpleShade = Color(0xFF704080)

/**
 * The owl's two wings at the sides of its head, [open] 0..1 out from behind it, at [flap] in their beat (-1 raised,
 * 1 pushed down). Each is a flat rounded wing with three feather tips, in the owl's purple and the pictures' outline,
 * hinged where it meets the head.
 */
private fun DrawScope.drawOwlWings(open: Float, flap: Float) {
    val w = size.width
    val length = w * 0.40f * open
    val depth = w * 0.24f * open
    if (length <= 0f) return
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
    // Raised to 50 degrees, pushed down to 25 degrees below level.
    val angle = -12.5f + 37.5f * flap
    for (side in listOf(-1f, 1f)) {
        withTransform({
            translate(left = w / 2f + side * w * 0.26f, top = size.height * 0.58f)
            scale(scaleX = side, scaleY = 1f, pivot = Offset.Zero)
            rotate(degrees = angle, pivot = Offset.Zero)
        }) {
            drawPath(wing, OwlPurple)
            drawPath(feather, OwlPurpleShade, style = Stroke(line, cap = StrokeCap.Round))
            drawPath(wing, LilyOutline, style = Stroke(line, join = StrokeJoin.Round))
        }
    }
}

/** A landing's dust at [dust] 0..1: a puff either side of the feet, spreading out and up as it fades. */
private fun DrawScope.drawLandingDust(dust: Float) {
    if (dust <= 0f) return
    val alpha = 0.9f * (1f - dust)
    val r = (4f + 6f * dust).dp.toPx()
    val line = 1.5.dp.toPx()
    for (side in listOf(-1f, 1f)) {
        val c = Offset(size.width / 2f + side * (24f + 22f * dust).dp.toPx(), size.height * 0.7f - (5f * dust).dp.toPx())
        for ((at, radius) in listOf(c to r, c + Offset(side * r * 0.95f, r * 0.25f) to r * 0.65f)) {
            drawCircle(Color.White.copy(alpha = alpha), radius, at)
            drawCircle(LilyOutline.copy(alpha = alpha * 0.5f), radius, at, style = Stroke(line))
        }
    }
}
