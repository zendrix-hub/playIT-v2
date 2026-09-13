package com.playit.app.presentation.map.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.MapNode
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.EmeraldLeaf
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.ModernBorder
import com.playit.app.presentation.theme.PrimaryJoy
import com.playit.app.presentation.theme.SunnyGold
import com.playit.app.presentation.theme.SurfaceCard
import com.playit.app.presentation.theme.SurfaceCardShadow
import com.playit.app.presentation.theme.TextMidnight
import kotlinx.coroutines.launch

enum class CompanionAnimal(val id: Int, val displayName: String, val assetPath: String) {
    CAT(1, "Miki", "images/characters/avatar_01_cat.webp"),
    MONKEY(2, "Milo", "images/characters/avatar_02_monkey.webp"),
    BUNNY(3, "Bella", "images/characters/avatar_03_bunny.webp"),
    BEAR(4, "Barnaby", "images/characters/avatar_04_bear.webp"),
    FROG(5, "Finley", "images/characters/avatar_05_frog.webp"),
    OWL(6, "Ollie", "images/characters/avatar_06_owl.webp");

    companion object {
        fun fromId(id: Int): CompanionAnimal = values().find { it.id == id } ?: CAT
    }
}

sealed class MapCharacter {
    data class Lily(val poseAssetPath: String, val roleDescription: String) : MapCharacter()
    data class Animal(val animal: CompanionAnimal, val isLeader: Boolean) : MapCharacter()
}

data class PlacedMapCharacter(
    val id: String,
    val character: MapCharacter,
    val offsetDp: Offset,
    val isLily: Boolean = false,
    val isLeader: Boolean = false,
    val isUnlocked: Boolean = false,
    val defaultPhrase: String,
    val tappedPhrase: String
)

/**
 * Calculates responsive mascot & companion placements along the winding adventure map trail:
 * 1. Chief Learning Guide: Lily the Tarsier stationed right beside the Active Lesson Node pointing/waving.
 * 2. Explorer Buddy: The child's chosen profile companion animal advancing along the trail.
 * 3. Chapter Guardians: Lily stationed at milestone checkpoints (Blend-It treasure chest nodes).
 * 4. Supporting Animal Friends: Stationed at scenic chapter biomes along alternating sides.
 */
fun generateMapCharacterPlacements(
    nodeCount: Int,
    nodeCenters: List<Offset>,
    nodes: List<MapNode>,
    activeNodeIndex: Int,
    activeAvatarId: Int,
    profileName: String,
    canvasWidthDp: Float,
    density: Float
): List<PlacedMapCharacter> {
    if (nodeCount <= 0 || canvasWidthDp <= 0f || nodeCenters.isEmpty()) return emptyList()

    val characters = mutableListOf<PlacedMapCharacter>()
    val activeAnimal = CompanionAnimal.fromId(activeAvatarId)
    val activeNode = nodes.getOrNull(activeNodeIndex)
    val activeLetterSymbol = (activeNode as? MapNode.LetterNode)?.symbol ?: "M"

    // ── 1. Lily the Tarsier — Chief Learning Guide at Active Node ─────────────
    if (activeNodeIndex in nodeCenters.indices) {
        val activeCenter = nodeCenters[activeNodeIndex]
        val activeCenterDpX = activeCenter.x / density
        val activeCenterDpY = activeCenter.y / density

        // Place Lily on the spacious side of the node (opposite of path curve)
        val isRightOfCenter = activeCenterDpX >= (canvasWidthDp / 2f)
        val lilyX = if (isRightOfCenter) {
            (activeCenterDpX - 96f).coerceAtLeast(10f)
        } else {
            (activeCenterDpX + 54f).coerceAtMost(canvasWidthDp - 86f)
        }
        val lilyY = activeCenterDpY - 36f

        val activeGuidePhrase = if (activeNode is MapNode.BlendItNode) {
            "Word Challenge! Let's go!"
        } else {
            "Let's go! Letter $activeLetterSymbol"
        }

        characters.add(
            PlacedMapCharacter(
                id = "lily_active_guide",
                character = MapCharacter.Lily(
                    poseAssetPath = "images/mascot/lily_pointing.webp",
                    roleDescription = "Lily the Tarsier Guide"
                ),
                offsetDp = Offset(lilyX, lilyY),
                isLily = true,
                isLeader = false,
                isUnlocked = true,
                defaultPhrase = activeGuidePhrase,
                tappedPhrase = "You can do it!"
            )
        )
    }

    // ── 2. Active Profile Explorer Animal Buddy ───────────────────────────────
    // Station the child's chosen animal companion nearby advancing alongside them
    val explorerNodeIndex = if (activeNodeIndex == 0) {
        if (nodeCenters.size > 1) 1 else 0
    } else {
        activeNodeIndex - 1
    }
    if (explorerNodeIndex in nodeCenters.indices) {
        val nodeCenter = nodeCenters[explorerNodeIndex]
        val centerDpX = nodeCenter.x / density
        val centerDpY = nodeCenter.y / density

        val isRight = if (explorerNodeIndex == activeNodeIndex) {
            centerDpX < (canvasWidthDp / 2f)
        } else {
            (explorerNodeIndex % 2 == 1)
        }
        val buddyX = if (isRight) {
            (centerDpX + 50f).coerceAtMost(canvasWidthDp - 84f)
        } else {
            (centerDpX - 92f).coerceAtLeast(10f)
        }
        val buddyY = centerDpY - 30f

        val greetingName = if (profileName.isNotBlank()) profileName else "Explorer"
        characters.add(
            PlacedMapCharacter(
                id = "explorer_buddy",
                character = MapCharacter.Animal(activeAnimal, isLeader = true),
                offsetDp = Offset(buddyX, buddyY),
                isLily = false,
                isLeader = true,
                isUnlocked = true,
                defaultPhrase = "Let's go, $greetingName!",
                tappedPhrase = "Let's try it!"
            )
        )
    }

    return characters
}

/**
 * Renders animated Lily the Tarsier and companion animal friends along the winding map trail.
 * Features:
 * - Interactive spring hop and squash-stretch tap physics.
 * - Dynamic tactile 3D Gummy speech bubbles with directional pointer tails.
 * - Pedagogical encouragement in clear, friendly English with zero emojis.
 */
@Composable
fun MapCompanionFriends(
    nodeCount: Int,
    nodeCenters: List<Offset>,
    nodes: List<MapNode>,
    activeNodeIndex: Int,
    activeAvatarId: Int,
    profileName: String = "",
    onCompanionTap: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (nodeCount <= 0 || nodeCenters.isEmpty()) return

    val isReducedMotion = LocalReducedMotion.current
    val coroutineScope = rememberCoroutineScope()

    // Interactive tapped character tracking for temporary reaction display
    var tappedCharacterId by remember { mutableStateOf<String?>(null) }

    // Synchronized ambient breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "CompanionBreathe")

    val breatheScaleY by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.052f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CompanionBreatheY"
    )
    val breatheScaleX by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.968f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CompanionBreatheX"
    )

    // Gentle vertical bobbing
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3.0f,
        targetValue = 3.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CompanionFloat"
    )

    BoxWithConstraints(modifier = modifier) {
        val density = androidx.compose.ui.platform.LocalDensity.current.density
        val canvasWidthDp = maxWidth.value

        val placedCharacters = remember(nodeCount, nodeCenters, activeNodeIndex, activeAvatarId, profileName, canvasWidthDp) {
            generateMapCharacterPlacements(
                nodeCount = nodeCount,
                nodeCenters = nodeCenters,
                nodes = nodes,
                activeNodeIndex = activeNodeIndex,
                activeAvatarId = activeAvatarId,
                profileName = profileName,
                canvasWidthDp = canvasWidthDp,
                density = density
            )
        }

        placedCharacters.forEach { item ->
            // Local tap bounce animation state
            val tapJumpY = remember { Animatable(0f) }
            val tapScaleX = remember { Animatable(1f) }
            val tapScaleY = remember { Animatable(1f) }

            val isTapped = tappedCharacterId == item.id
            val charWidth = if (item.isLily) 82.dp else if (item.isLeader) 76.dp else 66.dp
            val charHeight = if (item.isLily) 82.dp else if (item.isLeader) 76.dp else 66.dp

            val animFloatY = if (isReducedMotion) 0f else floatOffset
            val currentJumpY = tapJumpY.value

            val showBubble = item.isUnlocked || item.isLily || item.isLeader || isTapped
            val bubbleMessage = if (isTapped) item.tappedPhrase else item.defaultPhrase

            val assetPath = when (val c = item.character) {
                is MapCharacter.Lily -> c.poseAssetPath
                is MapCharacter.Animal -> c.animal.assetPath
            }

            val characterDescription = when (val c = item.character) {
                is MapCharacter.Lily -> c.roleDescription
                is MapCharacter.Animal -> "${c.animal.displayName} Companion"
            }

            Box(
                modifier = Modifier
                    .offset(
                        x = item.offsetDp.x.dp,
                        y = (item.offsetDp.y + animFloatY + currentJumpY).dp
                    )
                    .size(width = charWidth + 32.dp, height = charHeight + 36.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // ── 3D Gummy Speech Bubble with Downward Pointer Tail ──────
                    if (showBubble) {
                        GummyMapSpeechBubble(
                            message = bubbleMessage,
                            isLily = item.isLily,
                            isLeader = item.isLeader,
                            onClick = {
                                coroutineScope.launch {
                                    tappedCharacterId = item.id
                                    tapJumpY.snapTo(0f)
                                    tapJumpY.animateTo(-14f, spring(dampingRatio = 0.5f, stiffness = 500f))
                                    tapJumpY.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = 400f))
                                }
                                onCompanionTap?.invoke(item.id)
                            }
                        )
                    }

                    // ── Character Sprite with Breathing, Jump & Squash Physics ─
                    Box(
                        modifier = Modifier
                            .size(charWidth, charHeight)
                            .graphicsLayer {
                                if (!isReducedMotion) {
                                    scaleY = breatheScaleY * tapScaleY.value
                                    scaleX = breatheScaleX * tapScaleX.value
                                    transformOrigin = TransformOrigin(0.5f, 1f)
                                }
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                coroutineScope.launch {
                                    tappedCharacterId = item.id

                                    // Squash on press
                                    tapScaleX.snapTo(1.12f)
                                    tapScaleY.snapTo(0.88f)

                                    // Spring leap upward
                                    launch {
                                        tapJumpY.animateTo(-16f, tween(160, easing = FastOutSlowInEasing))
                                        tapJumpY.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = 450f))
                                    }
                                    launch {
                                        tapScaleX.animateTo(0.92f, tween(160))
                                        tapScaleX.animateTo(1.0f, spring(dampingRatio = 0.6f, stiffness = 400f))
                                    }
                                    launch {
                                        tapScaleY.animateTo(1.12f, tween(160))
                                        tapScaleY.animateTo(1.0f, spring(dampingRatio = 0.6f, stiffness = 400f))
                                    }
                                }
                                onCompanionTap?.invoke(item.id)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Ambient Ground Contact Shadow
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 3.dp)
                                .size(width = charWidth * 0.68f, height = 9.dp)
                                .background(Color(0x281F3A3D), CircleShape)
                        )

                        // Character Graphic
                        Image(
                            painter = rememberAssetPainter(assetPath),
                            contentDescription = characterDescription,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tactile 3D Gummy Speech Bubble designed for map trail immersion.
 * Features clean 12sp Lexend Black typography, 3dp depth shadow, and downward directional tail.
 */
@Composable
private fun GummyMapSpeechBubble(
    message: String,
    isLily: Boolean,
    isLeader: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val faceTint = when {
        isLily -> SurfaceCard
        isLeader -> Color(0xFFF0FDF4) // Light emerald tint for explorer leader
        else -> SurfaceCard
    }

    val textColor = when {
        isLily -> PrimaryJoy
        isLeader -> EmeraldLeaf
        else -> TextMidnight
    }

    val shadowColor = SurfaceCardShadow

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        // Bubble body
        Box(
            modifier = Modifier
                .background(shadowColor, RoundedCornerShape(12.dp))
                .padding(bottom = 2.5.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(faceTint, RoundedCornerShape(12.dp))
                    .border(1.75.dp, ModernBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) {
                Text(
                    text = message,
                    fontFamily = LexendFontFamily,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black,
                    color = textColor,
                    lineHeight = 14.sp
                )
            }
        }

        // Downward speech pointer triangle
        Canvas(
            modifier = Modifier
                .size(width = 10.dp, height = 5.dp)
                .offset(y = (-1).dp)
        ) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width / 2f, size.height)
                lineTo(size.width, 0f)
                close()
            }
            drawPath(path, ModernBorder)
        }
    }
}
