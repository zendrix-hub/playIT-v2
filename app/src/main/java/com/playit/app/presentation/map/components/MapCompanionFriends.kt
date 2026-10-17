package com.playit.app.presentation.map.components

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.components.rememberAssetPainter

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
        val activeCenter = nodeCenters[activeNodeIndex]
        val activeCenterDpX = activeCenter.x / density
        val activeCenterDpY = activeCenter.y / density

        val isRightOfCenter = activeCenterDpX >= (canvasWidthDp / 2f)
        val explorerX = if (isRightOfCenter) {
            (activeCenterDpX - 98f).coerceAtLeast(10f)
        } else {
            (activeCenterDpX + 54f).coerceAtMost(canvasWidthDp - 88f)
        }
        val explorerY = activeCenterDpY - 38f

        companions.add(
            PlacedCompanion(
                animal = activeAnimal,
                offsetDp = Offset(explorerX, explorerY),
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
 * Draws the child's own avatar beside the current node, still (card 22: no endless motion on the
 * map; the current node's pulse ring is the one moving thing). The other animal friends are no
 * longer drawn; generateCompanionPlacements still lists them for anyone who needs them.
 */
@Composable
fun MapCompanionFriends(
    nodeCount: Int,
    nodeCenters: List<Offset>,
    activeNodeIndex: Int,
    activeAvatarId: Int,
    onCompanionTap: ((CompanionAnimal) -> Unit)? = null,
    modifier: Modifier = Modifier
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

        val charSize = 78.dp
        Box(
            modifier = Modifier
                .offset(x = leader.offsetDp.x.dp, y = leader.offsetDp.y.dp)
                .size(charSize)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onCompanionTap?.invoke(leader.animal) },
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
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
