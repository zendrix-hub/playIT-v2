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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.components.hop
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.LocalReducedMotion
import kotlinx.coroutines.delay
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
 * a meaningful moment (design refresh):
 * - when a stop has just opened, it walks there from [arrivalFromIndex] (the stop the child just finished)
 *   and hops on arrival;
 * - when the child taps it, it hops.
 * Under reduced motion it stands at the current node and does not hop. The other animal friends are no
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
        // Starts at the old stop in the very first frame, so the explorer never flashes at the new one.
        val walk = remember(from != null, arrivalFromIndex) { Animatable(if (from != null) 0f else 1f) }
        var hops by remember { mutableIntStateOf(0) }
        LaunchedEffect(from != null, arrivalFromIndex) {
            if (from == null) return@LaunchedEffect
            delay(WALK_DELAY_MS)
            walk.animateTo(1f, tween(WALK_MS, easing = LinearEasing))
            hops++
        }
        val step = walkAt(walk.value)
        val x = if (from != null) from.x + (leader.offsetDp.x - from.x) * step.along else leader.offsetDp.x
        val y = if (from != null) from.y + (leader.offsetDp.y - from.y) * step.along - step.liftDp else leader.offsetDp.y

        val charSize = 78.dp
        Box(
            modifier = Modifier
                .offset(x = x.dp, y = y.dp)
                .size(charSize)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    hops++
                    onCompanionTap?.invoke(leader.animal)
                },
            contentAlignment = Alignment.Center
        ) {
            // Ambient Ground Contact Shadow
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 2.dp)
                    .size(width = charSize * 0.65f, height = 8.dp)
                    .background(Color(0x2E1F3A3D), CircleShape)
            )
            Image(
                painter = rememberAssetPainter(leader.animal.assetPath),
                contentDescription = "${leader.animal.displayName}, your explorer",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().hop(hops)
            )
        }
    }
}
