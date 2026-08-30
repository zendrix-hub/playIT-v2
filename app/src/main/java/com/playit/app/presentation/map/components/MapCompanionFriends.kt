package com.playit.app.presentation.map.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.LocalReducedMotion

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
    val cheerPhrase: String? = null
)

/**
 * Calculates companion animal placements along the map:
 * - Active Profile Avatar positioned as the Explorer Leader right next to the active node.
 * - The other 5 animal friends stationed along the biomes as cheerful companions.
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

    // 1. Place the Active Profile Explorer Avatar right next to the active node
    if (nodeCenters.isNotEmpty() && activeNodeIndex in nodeCenters.indices) {
        val activeCenter = nodeCenters[activeNodeIndex]
        val activeCenterDpX = activeCenter.x / density
        val activeCenterDpY = activeCenter.y / density

        val isLeft = activeCenterDpX > (canvasWidthDp / 2f)
        val explorerX = if (isLeft) {
            (activeCenterDpX - 86f).coerceAtLeast(8f)
        } else {
            (activeCenterDpX + 44f).coerceAtMost(canvasWidthDp - 80f)
        }
        val explorerY = activeCenterDpY - 32f

        companions.add(
            PlacedCompanion(
                animal = activeAnimal,
                offsetDp = Offset(explorerX, explorerY),
                isExplorerLeader = true,
                cheerPhrase = "Let's Go!"
            )
        )
    }

    // 2. Station the other 5 animal friends along the trail as cheerful companions
    val supportingAnimals = CompanionAnimal.values().filter { it.id != activeAnimal.id }
    val milestoneInterval = (nodeCount / (supportingAnimals.size + 1)).coerceAtLeast(4)

    val cheerPhrases = listOf("You can do it!", "Keep going!", "Great job!", "Almost there!", "Hooray!")

    supportingAnimals.forEachIndexed { index, animal ->
        val targetNodeIdx = ((index + 1) * milestoneInterval).coerceAtMost(nodeCount - 1)
        if (targetNodeIdx != activeNodeIndex && targetNodeIdx < nodeCenters.size) {
            val center = nodeCenters[targetNodeIdx]
            val centerDpX = center.x / density
            val centerDpY = center.y / density

            val isLeft = (index % 2 == 0)
            val compX = if (isLeft) {
                (centerDpX - 88f).coerceAtLeast(10f)
            } else {
                (centerDpX + 44f).coerceAtMost(canvasWidthDp - 78f)
            }
            val compY = centerDpY - 26f

            companions.add(
                PlacedCompanion(
                    animal = animal,
                    offsetDp = Offset(compX, compY),
                    isExplorerLeader = false,
                    cheerPhrase = cheerPhrases.getOrNull(index)
                )
            )
        }
    }

    return companions
}

/**
 * Renders the full-body animal avatar companions along the map trail.
 * Replaces arbitrary terrain props with full-body animal characters in the unified 4-Benchmark style.
 */
@Composable
fun MapCompanionFriends(
    nodeCount: Int,
    nodeCenters: List<Offset>,
    activeNodeIndex: Int,
    activeAvatarId: Int,
    modifier: Modifier = Modifier
) {
    if (nodeCount <= 0 || nodeCenters.isEmpty()) return

    val isReducedMotion = LocalReducedMotion.current
    val infiniteTransition = rememberInfiniteTransition(label = "CompanionAnimation")

    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CompanionBounce"
    )

    BoxWithConstraints(modifier = modifier) {
        val density = androidx.compose.ui.platform.LocalDensity.current.density
        val canvasWidthDp = maxWidth.value

        val placedCompanions = generateCompanionPlacements(
            nodeCount = nodeCount,
            nodeCenters = nodeCenters,
            activeNodeIndex = activeNodeIndex,
            activeAvatarId = activeAvatarId,
            canvasWidthDp = canvasWidthDp,
            density = density
        )

        placedCompanions.forEach { companion ->
            val charSize = if (companion.isExplorerLeader) 76.dp else 66.dp
            val animY = if (isReducedMotion) 0f else (if (companion.isExplorerLeader) bounceOffset * 1.5f else bounceOffset)

            Box(
                modifier = Modifier
                    .offset(x = companion.offsetDp.x.dp, y = (companion.offsetDp.y + animY).dp)
                    .size(charSize)
            ) {
                // Character Full-Body Image
                Image(
                    painter = rememberAssetPainter(companion.animal.assetPath),
                    contentDescription = companion.animal.displayName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                // Mini Cheer Speech Bubble for Leader / Milestone Friends
                if (companion.isExplorerLeader && companion.cheerPhrase != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-14).dp)
                            .background(Cloud, RoundedCornerShape(999.dp))
                            .border(1.5.dp, DarkBrownOutline, RoundedCornerShape(999.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = companion.cheerPhrase,
                            fontFamily = LexendFontFamily,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Ink
                        )
                    }
                }
            }
        }
    }
}
