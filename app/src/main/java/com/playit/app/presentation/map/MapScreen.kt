package com.playit.app.presentation.map

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.MapNode
import com.playit.app.presentation.components.GummyBackButton
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.GummyDialog
import com.playit.app.presentation.components.MascotBubble
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.components.idleBounce
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.map.components.BiomeThemes
import com.playit.app.presentation.map.components.ChocolateHillsBackground
import com.playit.app.presentation.map.components.GroupBannerStatus
import com.playit.app.presentation.map.components.MapCompanionFriends
import com.playit.app.presentation.map.components.MapPathCanvas
import com.playit.app.presentation.map.components.MapTerrainProps
import com.playit.app.presentation.map.components.MarungkoGroupBanner
import com.playit.app.presentation.map.components.NodeActionPopupDialog
import com.playit.app.presentation.map.components.TopStatsBar
import com.playit.app.presentation.map.components.UnitGuidebookDialog
import com.playit.app.presentation.map.components.calculateNodeXOffsetDp
import com.playit.app.presentation.theme.ApricotGlow
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.EmeraldLeaf
import com.playit.app.presentation.theme.EmeraldLeafDark
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.InkFaint
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LeafDark
import com.playit.app.presentation.theme.LeafShadow
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.MangoDark
import com.playit.app.presentation.theme.MangoShadow
import com.playit.app.presentation.theme.ModernBorder
import com.playit.app.presentation.theme.ModernBorderSoft
import com.playit.app.presentation.theme.SunnyGold
import com.playit.app.presentation.theme.SunnyGoldDark
import com.playit.app.presentation.theme.SunnyGoldLight
import com.playit.app.presentation.theme.SurfaceCard
import com.playit.app.presentation.theme.SurfaceCardShadow
import com.playit.app.presentation.theme.Tan
import com.playit.app.presentation.theme.TanDark
import com.playit.app.presentation.theme.TanShadow
import com.playit.app.presentation.theme.TextMidnight
import com.playit.app.presentation.theme.TextMuted
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.graphics.Brush
import com.playit.app.presentation.theme.Sand
import com.playit.app.presentation.theme.SandDeep
import com.playit.app.presentation.theme.Sky
import com.playit.app.presentation.theme.SkyDeep
import com.playit.app.presentation.theme.Ube
import com.playit.app.presentation.theme.UbeShadow
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════════════════════════════
// Map Screen — Phase 10 Filipino-themed Duolingo ABC Polish
// ═══════════════════════════════════════════════════════════════════════════

private val MASCOT_SIZE = 64.dp
private val PATH_AMPLITUDE_X = 50.dp
private val MAP_BOTTOM_EXTENSION = 120.dp
private val TERRAIN_PROPS_VERTICAL_SPACING = 130.dp
private val LETTER_NODE_SIZE = 92.dp
private val BLEND_IT_NODE_SIZE = 136.dp
private val NODE_VERTICAL_SPACING = 38.dp
private val BANNER_HEIGHT_ESTIMATE = 68.dp
private val BANNER_SPACING = 18.dp

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNodeSelected: (String) -> Unit,
    onBack: () -> Unit = {}
) {
    BackHandler(onBack = onBack)

    val mapNodes by viewModel.mapNodes.collectAsStateWithLifecycle()
    val userStats by viewModel.userStats.collectAsStateWithLifecycle()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsStateWithLifecycle()
    val isReducedMotion = LocalReducedMotion.current
    val scrollState = rememberScrollState()

    // Identify current active node for auto-scroll and companion mascot positioning
    val activeNodeIndex = remember(mapNodes) {
        // 1. Earliest unstarted letter node
        val firstUnstartedLetter = mapNodes.indexOfFirst { it.isUnlocked && it is MapNode.LetterNode && it.starsEarned == 0 }
        if (firstUnstartedLetter != -1) return@remember firstUnstartedLetter

        // 2. Earliest unlocked Blend-It milestone
        val unlockedBlendIt = mapNodes.indexOfFirst { it.isUnlocked && it is MapNode.BlendItNode }
        if (unlockedBlendIt != -1) return@remember unlockedBlendIt

        // 3. Fallback to latest unlocked node
        val lastUnlocked = mapNodes.indexOfLast { it.isUnlocked }
        if (lastUnlocked != -1) lastUnlocked else 0
    }

    // Dialog states for Duolingo Node Action Card & Unit Guidebook
    var selectedNodeForAction by remember { mutableStateOf<MapNode?>(null) }
    var selectedGuidebookUnit by remember { mutableStateOf<Int?>(null) }

    // Shake animation state for locked node taps
    var shakenNodeId by remember { mutableStateOf<String?>(null) }
    var lockedBlendItDialogGroup by remember { mutableStateOf<String?>(null) }
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(shakenNodeId) {
        if (shakenNodeId == null) return@LaunchedEffect
        shakeOffset.snapTo(0f)
        shakeOffset.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 350
                -9f at 50 using LinearEasing
                9f at 120 using LinearEasing
                -6f at 190 using LinearEasing
                6f at 260 using LinearEasing
                0f at 350 using LinearEasing
            }
        )
        shakenNodeId = null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SkyDeep,
                        Sky,
                        Sand,
                        SandDeep
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Calculate currently visible Bohol biome section based on scroll offset
            val currentVisibleSectionIndex = remember(scrollState.value, scrollState.maxValue) {
                if (scrollState.maxValue <= 0) 1
                else {
                    val scrollFraction = (scrollState.value.toFloat() / scrollState.maxValue.toFloat()).coerceIn(0f, 1f)
                    val approxGroup = (scrollFraction * 6.0f).toInt() + 1
                    approxGroup.coerceIn(1, 7)
                }
            }
            val activeBiomeTheme = BiomeThemes.forSection(currentVisibleSectionIndex)

            // ── Header: Floating Gummy Top Bar (Dynamically Adapting to Biome) ─
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GummyBackButton(
                    onClick = onBack,
                    size = 46.dp
                )

                TopStatsBar(
                    totalStars = userStats.totalStars,
                    currentStreak = userStats.currentStreak,
                    unlockedBadgesCount = userStats.unlockedBadgesCount,
                    lettersCompleted = mapNodes.count { it is MapNode.LetterNode && it.starsEarned > 0 },
                    profileName = userStats.profileName,
                    biomeTheme = activeBiomeTheme,
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Mascot Prompt Header (Personalized Instant Greeting & Biome Guidance) ─
            val welcomeGreeting = if (userStats.profileName.isNotBlank()) {
                "${userStats.profileName}, ${activeBiomeTheme.mascotDialogue}"
            } else {
                activeBiomeTheme.mascotDialogue
            }

            val animatedBubbleBg by animateColorAsState(
                targetValue = activeBiomeTheme.backgroundTint.copy(alpha = 0.95f),
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
                label = "bubbleBgAnim"
            )

            MascotBubble(
                message = welcomeGreeting,
                mascotState = MascotState.ENCOURAGING,
                avatarId = userStats.avatarId,
                backgroundColor = animatedBubbleBg,
                onMascotTap = { viewModel.playMascotTapReaction() },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // ── Winding Adventure Map ──────────────────────────────────────
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                val density = LocalDensity.current
                val widthPx = with(density) { constraints.maxWidth.toDp().toPx() }
                val spacingDp = NODE_VERTICAL_SPACING
                val spacingPx = with(density) { spacingDp.toPx() }
                val topPaddingDp = 20.dp
                val topPaddingPx = with(density) { topPaddingDp.toPx() }
                val bannerHeightPx = with(density) { BANNER_HEIGHT_ESTIMATE.toPx() }
                val bannerSpacingPx = with(density) { BANNER_SPACING.toPx() }

                val measuredCenters = remember { mutableStateMapOf<Int, Offset>() }

                // Fallback / initial estimated center coordinates
                val fallbackNodeCenters = remember(mapNodes, widthPx) {
                    val centers = mutableListOf<Offset>()
                    var currentY = topPaddingPx

                    mapNodes.forEachIndexed { index, node ->
                        val isGroupStart = index == 0 || mapNodes[index - 1].groupNumber != node.groupNumber
                        if (isGroupStart) {
                            currentY += bannerHeightPx + bannerSpacingPx
                        }

                        val nodeHeightDp = if (node is MapNode.BlendItNode) 54.dp else LETTER_NODE_SIZE
                        val nodeHeightPx = with(density) { nodeHeightDp.toPx() }
                        val centerY = currentY + nodeHeightPx / 2f
                        val xOffsetPx = with(density) { calculateNodeXOffsetDp(index, PATH_AMPLITUDE_X).toPx() }
                        val centerX = widthPx / 2f + xOffsetPx

                        centers.add(Offset(centerX, centerY))

                        val nextNode = mapNodes.getOrNull(index + 1)
                        val nextNodeHeightDp = if (nextNode is MapNode.BlendItNode) 54.dp else LETTER_NODE_SIZE
                        val nextNodeHeightPx = with(density) { nextNodeHeightDp.toPx() }

                        currentY += nodeHeightPx / 2f + spacingPx + nextNodeHeightPx / 2f
                    }
                    centers
                }

                // If all nodes have been measured by Compose layout, use exact pixel centers; else use fallback
                val nodeCenters = if (measuredCenters.size >= mapNodes.size && mapNodes.isNotEmpty()) {
                    (0 until mapNodes.size).mapNotNull { measuredCenters[it] }
                } else {
                    fallbackNodeCenters
                }

                val viewportHeightPx = with(density) { maxHeight.toPx() }

                // Auto-scroll to active node on map launch (reliably leads to current node when progress is far)
                var hasAutoScrolled by remember { mutableStateOf(false) }
                LaunchedEffect(activeNodeIndex, nodeCenters, scrollState.maxValue) {
                    if (!hasAutoScrolled && nodeCenters.isNotEmpty() && activeNodeIndex in nodeCenters.indices) {
                        if (activeNodeIndex == 0) {
                            hasAutoScrolled = true
                            return@LaunchedEffect
                        }
                        if (scrollState.maxValue > 0) {
                            val activeCenterY = nodeCenters[activeNodeIndex].y
                            val targetScrollPx = (activeCenterY - viewportHeightPx / 2.2f).coerceIn(0f, scrollState.maxValue.toFloat())
                            if (isReducedMotion) {
                                scrollState.scrollTo(targetScrollPx.toInt())
                            } else {
                                scrollState.animateScrollTo(
                                    value = targetScrollPx.toInt(),
                                    animationSpec = tween(
                                        durationMillis = 750,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            }
                            hasAutoScrolled = true
                        }
                    }
                }

                // Total calculated map height to ensure path canvas & terrain props span full scroll length
                val totalMapHeightDp = remember(nodeCenters) {
                    if (nodeCenters.isNotEmpty()) {
                        with(density) { (nodeCenters.last().y + MAP_BOTTOM_EXTENSION.toPx()).toDp() }
                    } else {
                        800.dp
                    }
                }

                // Layer 1: Sky-to-sand gradient background with Chocolate Hills
                ChocolateHillsBackground(
                    totalHeight = totalMapHeightDp,
                    modifier = Modifier.fillMaxWidth()
                )

                // Layer 1.5: Swaying Cultural Terrain Props (Palm Trees, Nipa Huts, Tropical Flowers, Adventure Props)
                MapTerrainProps(
                    nodeCount = mapNodes.size,
                    nodeVerticalSpacing = spacingDp,
                    topPadding = topPaddingDp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(totalMapHeightDp)
                )

                // Layer 2: Continuous rope-colored dashed trail path
                MapPathCanvas(
                    nodeCenters = nodeCenters,
                    nodes = mapNodes,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(totalMapHeightDp)
                )

                // Layer 3: Interactive Map Nodes positioned along the winding path with Group Banners
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = topPaddingDp, bottom = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacingDp)
                ) {
                    mapNodes.forEachIndexed { index, node ->
                        val isGroupStart = index == 0 || mapNodes[index - 1].groupNumber != node.groupNumber
                        if (isGroupStart) {
                            val groupNodes = mapNodes.filter { it.groupNumber == node.groupNumber }
                            val groupStatus = when {
                                groupNodes.all { it.isUnlocked && (it !is MapNode.LetterNode || it.starsEarned > 0) } -> GroupBannerStatus.COMPLETED
                                groupNodes.any { it.orderIndex == activeNodeIndex || (it.isUnlocked && (it is MapNode.LetterNode && it.starsEarned == 0)) } -> GroupBannerStatus.IN_PROGRESS
                                groupNodes.any { it.isUnlocked } -> GroupBannerStatus.IN_PROGRESS
                                else -> GroupBannerStatus.LOCKED
                            }
                            MarungkoGroupBanner(
                                groupNumber = node.groupNumber,
                                status = groupStatus,
                                onGuidebookClick = {
                                    selectedGuidebookUnit = node.groupNumber
                                },
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        val xOffsetDp = calculateNodeXOffsetDp(index, PATH_AMPLITUDE_X)
                        val isShaking = shakenNodeId == node.id
                        val shakeX = if (isShaking) shakeOffset.value.dp else 0.dp

                        Box(
                            modifier = Modifier
                                .offset(x = xOffsetDp + shakeX)
                                .onGloballyPositioned { coords ->
                                    val boundsInCol = coords.boundsInParent()
                                    measuredCenters[index] = Offset(
                                        x = boundsInCol.center.x,
                                        y = boundsInCol.center.y + topPaddingPx
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            when (node) {
                                is MapNode.LetterNode -> {
                                    LetterMapNodeCard(
                                        node = node,
                                        onClick = {
                                            if (node.isUnlocked) {
                                                selectedNodeForAction = node
                                            } else {
                                                shakenNodeId = node.id
                                                selectedNodeForAction = node
                                                viewModel.onLockedNodeTapped()
                                            }
                                        }
                                    )
                                }
                                is MapNode.BlendItNode -> {
                                    BlendItChallengeNodeCard(
                                        node = node,
                                        onClick = {
                                            if (node.isUnlocked) {
                                                selectedNodeForAction = node
                                            } else {
                                                shakenNodeId = node.id
                                                selectedNodeForAction = node
                                                viewModel.onLockedNodeTapped()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Layer 4: Animated Lily the Tarsier & Companion Friends with interactive Gummy speech bubbles
                MapCompanionFriends(
                    nodeCount = mapNodes.size,
                    nodeCenters = nodeCenters,
                    nodes = mapNodes,
                    activeNodeIndex = activeNodeIndex,
                    activeAvatarId = userStats.avatarId,
                    profileName = userStats.profileName,
                    onCompanionTap = { _ ->
                        viewModel.playMascotTapReaction()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(totalMapHeightDp)
                )
            }
        }

        // Duolingo Signature Floating 3D Node Action Card
        selectedNodeForAction?.let { targetNode ->
            NodeActionPopupDialog(
                node = targetNode,
                isAudioPlaying = isAudioPlaying,
                onStartChallenge = { nodeId ->
                    selectedNodeForAction = null
                    onNodeSelected(nodeId)
                },
                onDismiss = { selectedNodeForAction = null }
            )
        }

        // Duolingo Unit Phonics Guidebook Dialog
        selectedGuidebookUnit?.let { unitNum ->
            UnitGuidebookDialog(
                unitNumber = unitNum,
                onDismiss = { selectedGuidebookUnit = null }
            )
        }

        // Locked Blend-It Informational Dialog
        if (lockedBlendItDialogGroup != null) {
            val group = lockedBlendItDialogGroup ?: "1"
            GummyDialog(
                title = "Group $group Locked",
                body = "Complete all letters in Group $group to unlock the Blend-It Challenge!",
                confirmText = "Got It",
                onConfirm = { lockedBlendItDialogGroup = null },
                onDismiss = { lockedBlendItDialogGroup = null },
                confirmColor = Mango,
                confirmShadowColor = MangoShadow
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Mini Dialogue Bubble above Companion Mascot
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun MascotMapDialogueBubble(
    message: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GummyContainer(
        onClick = onClick,
        faceColor = SurfaceCard,
        shadowColor = SurfaceCardShadow,
        strokeColor = ModernBorder,
        strokeWidth = 2.dp,
        depthHeight = 3.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                fontFamily = LexendFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextMidnight
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Letter Map Node — Modern Stepping Stone Disc
// ═══════════════════════════════════════════════════════════════════════════

private val NodeGoldFace = SunnyGold
private val NodeGoldShelf = SunnyGoldDark
private val NodeGoldStar = SunnyGoldDark

private val NodeGreenFace = EmeraldLeaf
private val NodeGreenShelf = EmeraldLeafDark

private val NodeLockedFace = Color(0xFFF1F5F9)
private val NodeLockedShelf = Color(0xFFCBD5E1)
private val NodeLockedIcon = Color(0xFF94A3B8)

@Composable
fun LetterMapNodeCard(
    node: MapNode.LetterNode,
    onClick: () -> Unit
) {
    val isCompleted = node.isUnlocked && node.starsEarned > 0
    val isCurrentActiveNode = node.isUnlocked && node.starsEarned == 0
    val isReducedMotion = LocalReducedMotion.current

    // Pulsing focus ring animation on active node
    val infiniteTransition = rememberInfiniteTransition(label = "activeNodeRing")
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringScale"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringAlpha"
    )

    val faceColor = when {
        isCompleted -> NodeGoldFace
        node.isUnlocked -> NodeGreenFace
        else -> NodeLockedFace
    }

    val shelfColor = when {
        isCompleted -> NodeGoldShelf
        node.isUnlocked -> NodeGreenShelf
        else -> NodeLockedShelf
    }

    val accessibilityLabel = when {
        isCompleted -> "Letter ${node.symbol}, completed, ${node.starsEarned} stars"
        node.isUnlocked -> "Letter ${node.symbol}, current lesson"
        else -> "Letter ${node.symbol}, locked"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = accessibilityLabel
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(width = 76.dp, height = 84.dp)
        ) {
            // Active Pulsing Ring Focus Aura
            if (isCurrentActiveNode && !isReducedMotion) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .graphicsLayer {
                            scaleX = ringScale
                            scaleY = ringScale
                            alpha = ringAlpha
                        }
                        .border(
                            width = 3.5.dp,
                            color = NodeGreenShelf,
                            shape = CircleShape
                        )
                )
            }

            // 3D Duolingo Disc (Clickable)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
            ) {
                // 3D Extrusion Bottom Shelf (8dp extrusion)
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .align(Alignment.BottomCenter)
                        .background(shelfColor, CircleShape)
                )

                // Top Disc Face
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .align(Alignment.TopCenter)
                        .background(faceColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Subtle Top-Left Crescent Gleam Highlight
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = Color(0x55FFFFFF),
                            startAngle = 175f,
                            sweepAngle = 90f,
                            useCenter = false,
                            topLeft = Offset(7.dp.toPx(), 5.dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(size.width - 14.dp.toPx(), size.height - 14.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 4.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )
                    }

                    // Inner Letter Symbol (Always visible before, during, and after completion!)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (isCompleted) {
                            // Mastered Gold Disc with Bold White Letter (Always visible!)
                            Text(
                                text = node.symbol,
                                fontFamily = LexendFontFamily,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        } else if (node.isUnlocked) {
                            // Active Green Disc with Bold White Letter
                            Text(
                                text = node.symbol,
                                fontFamily = LexendFontFamily,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        } else {
                            // Locked Slate Disc — Letter symbol STILL VISIBLE in soft slate with subtle lock badge
                            Text(
                                text = node.symbol,
                                fontFamily = LexendFontFamily,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF94A3B8)
                            )
                            // Mini Lock indicator badge at bottom right
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = (-4).dp, y = (-4).dp)
                                    .size(20.dp)
                                    .background(Color(0xFF64748B), CircleShape)
                                    .border(1.5.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = "Locked Letter",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3 Subtle Stars row under Mastered Nodes (matching duoling_map_sample.jpg)
        if (isCompleted) {
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { starIdx ->
                    val isEarned = starIdx < node.starsEarned
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Star ${starIdx + 1}",
                        tint = if (isEarned) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Blend It Challenge Node — Grand Golden Crown & Laurel 3D Challenge Disc
// ═══════════════════════════════════════════════════════════════════════════

private val BlendChallengeGoldFace = SunnyGold
private val BlendChallengeGoldShelf = SunnyGoldDark
private val BlendChallengeFlameAura = ApricotGlow

@Composable
fun BlendItChallengeNodeCard(
    node: MapNode.BlendItNode,
    onClick: () -> Unit
) {
    val isUnlocked = node.isUnlocked
    val isReducedMotion = LocalReducedMotion.current

    // Pulsing Fiery Treasure Aura Transition
    val infiniteTransition = rememberInfiniteTransition(label = "chestAura")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "chestAuraScale"
    )
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.70f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "chestAuraAlpha"
    )

    val chestFace = if (isUnlocked) SunnyGold else Color(0xFFF1F5F9)
    val chestShelf = if (isUnlocked) SunnyGoldDark else Color(0xFFCBD5E1)
    val trimGold = if (isUnlocked) SunnyGoldLight else Color(0xFFE2E8F0)
    val lockBadgeColor = if (isUnlocked) SunnyGold else Color(0xFF94A3B8)

    val accessibilityLabel = if (isUnlocked) {
        "Blend-It Treasure Chest Milestone, Unit ${node.groupId}, unlocked"
    } else {
        "Blend-It Treasure Chest Milestone, Unit ${node.groupId}, locked"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = accessibilityLabel
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(width = 100.dp, height = 98.dp)
        ) {
            // Radiant Treasure Aura Ring when unlocked
            if (isUnlocked && !isReducedMotion) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .graphicsLayer {
                            scaleX = auraScale
                            scaleY = auraScale
                            alpha = auraAlpha
                        }
                        .border(
                            width = 4.dp,
                            color = ApricotGlow,
                            shape = RoundedCornerShape(26.dp)
                        )
                )
            }

            // 3D Treasure Chest Shape (Clickable via GummyContainer)
            GummyContainer(
                onClick = onClick,
                enabled = true,
                faceColor = chestFace,
                shadowColor = chestShelf,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                strokeWidth = 2.5.dp,
                strokeColor = ModernBorder,
                depthHeight = 8.dp,
                modifier = Modifier.size(width = 90.dp, height = 76.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Chest Lid Division & Gold Trim Band
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Lid Trim
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .background(
                                    trimGold,
                                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                                )
                                .border(1.5.dp, ModernBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        // Lower Base Rim
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(
                                    trimGold,
                                    RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                                )
                        )
                    }

                    // Center Clasp / Star Keyhole
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(lockBadgeColor, RoundedCornerShape(10.dp))
                            .border(2.dp, ModernBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "Milestone Star",
                                tint = Color(0xFF78350F),
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = "Locked Chest",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Chest Node Badge Label below
        Box(
            modifier = Modifier
                .offset(y = (-4).dp)
                .background(if (isUnlocked) Color(0xFF78350F) else Color(0xFF64748B), RoundedCornerShape(999.dp))
                .border(1.5.dp, if (isUnlocked) Color(0xFFFFC93C) else Color(0xFF94A3B8), RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Extension,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = "BLEND ${node.groupId}",
                    fontFamily = LexendFontFamily,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // 3 Subtle Stars row under Mastered Blend It Nodes
        if (node.starsEarned > 0) {
            Row(
                modifier = Modifier.padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { starIdx ->
                    val isEarned = starIdx < node.starsEarned
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Star ${starIdx + 1}",
                        tint = if (isEarned) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

