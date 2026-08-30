package com.playit.app.presentation.map.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.Leaf
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
 * Renders the full-body animal avatar companions along the map trail with
 * Splash Screen-like breathing and gentle floating animations.
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

    val isReducedMotion = LocalReducedMotion.current
    val infiniteTransition = rememberInfiniteTransition(label = "CompanionAnimation")

    // Headspace-style Breathing scale animation (matching SplashScreen.kt)
    val breatheScaleY by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CompanionBreatheY"
    )
    val breatheScaleX by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CompanionBreatheX"
    )

    // Gentle vertical bobbing
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3.5f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CompanionFloat"
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
            val charWidth = if (companion.isExplorerLeader) 78.dp else 68.dp
            val charHeight = if (companion.isExplorerLeader) 78.dp else 68.dp
            val animY = if (isReducedMotion) 0f else floatOffset

            Box(
                modifier = Modifier
                    .offset(x = companion.offsetDp.x.dp, y = (companion.offsetDp.y + animY).dp)
                    .size(width = charWidth, height = charHeight + 18.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Speech cheer bubble (for Leader and cheering Friends)
                    if (companion.cheerPhrase != null) {
                        Box(
                            modifier = Modifier
                                .background(Cloud, RoundedCornerShape(999.dp))
                                .border(1.5.dp, DarkBrownOutline, RoundedCornerShape(999.dp))
                                .padding(horizontal = 7.dp, vertical = 2.5.dp)
                        ) {
                            Text(
                                text = companion.cheerPhrase,
                                fontFamily = LexendFontFamily,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (companion.isExplorerLeader) Leaf else Ink
                            )
                        }
                    }

                    // Character with Breathing & Floating Motion
                    Box(
                        modifier = Modifier
                            .size(charWidth, charHeight)
                            .graphicsLayer {
                                if (!isReducedMotion) {
                                    scaleY = breatheScaleY
                                    scaleX = breatheScaleX
                                    transformOrigin = TransformOrigin(0.5f, 1f)
                                }
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onCompanionTap?.invoke(companion.animal)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Ambient Ground Contact Shadow
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 2.dp)
                                .size(width = charWidth * 0.65f, height = 8.dp)
                                .background(Color(0x2E1F3A3D), CircleShape)
                        )

                        // Character Graphic
                        Image(
                            painter = rememberAssetPainter(companion.animal.assetPath),
                            contentDescription = "${companion.animal.displayName} Companion",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
