package com.playit.app.presentation.map

import com.playit.app.presentation.theme.*
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
import androidx.compose.foundation.ScrollState
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
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
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
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.components.idleBounce
import com.playit.app.presentation.components.mapPopDelayMs
import com.playit.app.presentation.components.popIn
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.map.components.BiomeThemes
import com.playit.app.presentation.map.components.ChocolateHillsBackground
import com.playit.app.presentation.map.components.GroupBannerStatus
import com.playit.app.presentation.map.components.MapCompanionFriends
import com.playit.app.presentation.map.components.MapPathCanvas
import com.playit.app.presentation.map.components.MarungkoGroupBanner
import com.playit.app.presentation.map.components.NodeActionPopupDialog
import com.playit.app.presentation.map.components.TopStatsBar
import com.playit.app.presentation.map.components.UnitGuidebookDialog
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.playit.app.presentation.theme.LocalPlayItDimens
import com.playit.app.presentation.theme.PlayItMotion
import kotlinx.coroutines.delay

// ═══════════════════════════════════════════════════════════════════════════
// Map Screen — Phase 10 Filipino-themed Duolingo ABC Polish
// ═══════════════════════════════════════════════════════════════════════════

private val MASCOT_SIZE = 64.dp
private val MAP_BOTTOM_EXTENSION = 120.dp
private val TERRAIN_PROPS_VERTICAL_SPACING = 130.dp
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
    val newlyUnlockedNodeId by viewModel.newlyUnlockedNodeId.collectAsStateWithLifecycle()
    val isReducedMotion = LocalReducedMotion.current
    val d = LocalPlayItDimens.current
    val scrollState = rememberScrollState()

    // Unlock moment (card 22): the node that just opened pops in, with a chime and a chip.
    val unlockPop = remember { Animatable(1f) }
    var poppingNodeId by remember { mutableStateOf<String?>(null) }
    var showUnlockChip by remember { mutableStateOf(false) }

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

    // On the unlock moment the explorer walks to the new stop from the one before it, the stop the child just
    // finished, and the star counter counts up the stars earned there (design refresh).
    val arrivalFromIndex = remember(newlyUnlockedNodeId, mapNodes) {
        newlyUnlockedNodeId?.let { id -> mapNodes.indexOfFirst { it.id == id } - 1 }?.takeIf { it >= 0 }
    }
    val starsJustEarned = arrivalFromIndex?.let { (mapNodes[it] as? MapNode.LetterNode)?.starsEarned } ?: 0

    // Dialog states for Duolingo Node Action Card & Unit Guidebook
    var selectedNodeForAction by remember { mutableStateOf<MapNode?>(null) }
    var selectedGuidebookUnit by remember { mutableStateOf<Int?>(null) }

    // Shake animation state for locked node taps
    var shakenNodeId by remember { mutableStateOf<String?>(null) }

    // The active node starts on one tap; finished nodes open the pop-up for replay (card 28).
    val onNodeTap: (MapNode, Int) -> Unit = { node, index ->
        when (nodeTapAction(node.isUnlocked, index, activeNodeIndex)) {
            NodeTapAction.LAUNCH -> onNodeSelected(node.id)
            NodeTapAction.POPUP -> {
                selectedNodeForAction = node
                viewModel.onUnlockedNodeTapped()
            }
            NodeTapAction.LOCKED -> {
                shakenNodeId = node.id
                selectedNodeForAction = node
                viewModel.onLockedNodeTapped()
            }
        }
    }
    var lockedBlendItDialogGroup by remember { mutableStateOf<String?>(null) }
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(shakenNodeId) {
        if (shakenNodeId == null) return@LaunchedEffect
        if (isReducedMotion) {
            // Reduced motion: no shake; the locked cue and the node dialog still respond.
            shakenNodeId = null
            return@LaunchedEffect
        }
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
            // ── Header: Floating Gummy Top Bar (Dynamically Adapting to Biome) ─
            MapHeader(
                scrollState = scrollState,
                userStats = userStats,
                starsJustEarned = starsJustEarned,
                lettersCompleted = mapNodes.count { it is MapNode.LetterNode && it.starsEarned > 0 },
                onBack = onBack,
                onLilyTap = { viewModel.playMascotTapReaction() }
            )

            // ── Winding Adventure Map ──────────────────────────────────────
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                val density = LocalDensity.current
                val mapWidth = maxWidth
                val widthPx = with(density) { constraints.maxWidth.toDp().toPx() }
                val spacingDp = NODE_VERTICAL_SPACING
                val spacingPx = with(density) { spacingDp.toPx() }
                val topPaddingDp = 20.dp
                val topPaddingPx = with(density) { topPaddingDp.toPx() }
                val bannerHeightPx = with(density) { BANNER_HEIGHT_ESTIMATE.toPx() }
                val bannerSpacingPx = with(density) { BANNER_SPACING.toPx() }

                val measuredCenters = remember(mapNodes) { mutableStateMapOf<Int, Offset>() }
                // Disc plus its 8 dp shelf and the stars row under a finished letter
                val letterNodeHeight = d.mapNodeSize + 16.dp

                // Fallback / initial estimated center coordinates
                val fallbackNodeCenters = remember(mapNodes, widthPx, d.mapNodeSize) {
                    val centers = mutableListOf<Offset>()
                    var currentY = topPaddingPx

                    mapNodes.forEachIndexed { index, node ->
                        val isGroupStart = index == 0 || mapNodes[index - 1].groupNumber != node.groupNumber
                        if (isGroupStart) {
                            currentY += bannerHeightPx + bannerSpacingPx
                        }

                        val nodeHeightDp = if (node is MapNode.BlendItNode) 54.dp else letterNodeHeight
                        val nodeHeightPx = with(density) { nodeHeightDp.toPx() }
                        val centerY = currentY + nodeHeightPx / 2f
                        val xOffsetPx = with(density) { MapLayout.nodeXOffset(index, mapWidth, d.mapNodeSize).toPx() }
                        val centerX = widthPx / 2f + xOffsetPx

                        centers.add(Offset(centerX, centerY))

                        val nextNode = mapNodes.getOrNull(index + 1)
                        val nextNodeHeightDp = if (nextNode is MapNode.BlendItNode) 54.dp else letterNodeHeight
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

                // Unlock moment: scroll to the new node, pop it, chime, show the chip, then clear.
                LaunchedEffect(newlyUnlockedNodeId, nodeCenters) {
                    val id = newlyUnlockedNodeId ?: return@LaunchedEffect
                    val index = mapNodes.indexOfFirst { it.id == id }
                    if (index !in nodeCenters.indices) return@LaunchedEffect
                    val targetScrollPx = (nodeCenters[index].y - viewportHeightPx / 2.2f)
                        .coerceIn(0f, scrollState.maxValue.toFloat()).toInt()
                    if (isReducedMotion) scrollState.scrollTo(targetScrollPx) else scrollState.animateScrollTo(targetScrollPx)
                    hasAutoScrolled = true
                    viewModel.playUnlockChime()
                    showUnlockChip = true
                    if (!isReducedMotion) {
                        poppingNodeId = id
                        unlockPop.snapTo(0.6f)
                        unlockPop.animateTo(1f, tween(PlayItMotion.CELEBRATION_MS))
                        poppingNodeId = null
                    }
                    delay(2000L)
                    showUnlockChip = false
                    viewModel.onUnlockShown()
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

                        val xOffsetDp = MapLayout.nodeXOffset(index, mapWidth, d.mapNodeSize)
                        Box(
                            modifier = Modifier
                                // The shake and the unlock pop are read as the node lays out and draws, so they
                                // move this node without rebuilding the map on every frame (MapScrollTest).
                                .offset {
                                    val shakeX = if (shakenNodeId == node.id) shakeOffset.value.dp else 0.dp
                                    IntOffset((xOffsetDp + shakeX).roundToPx(), 0)
                                }
                                .graphicsLayer {
                                    val popScale = if (poppingNodeId == node.id) unlockPop.value else 1f
                                    scaleX = popScale; scaleY = popScale
                                }
                                .then(if (index == activeNodeIndex) Modifier.testTag("map_current_node") else Modifier)
                                // Measured when placed in the trail, not on every frame of a scroll, and stored only
                                // when it moved, so the map isn't rebuilt while it scrolls.
                                .onPlaced { coords ->
                                    val boundsInCol = coords.boundsInParent()
                                    val center = Offset(
                                        x = boundsInCol.center.x,
                                        y = boundsInCol.center.y + topPaddingPx
                                    )
                                    if (measuredCenters[index] != center) measuredCenters[index] = center
                                }
                                // Map opening (user, 2026-10-29): the stops pop onto the trail in a wave that
                                // ends at the child's current stop. Inside the measuring, so centres never move.
                                .popIn(delayMillis = mapPopDelayMs(index, activeNodeIndex)),
                            contentAlignment = Alignment.Center
                        ) {
                            when (node) {
                                is MapNode.LetterNode -> {
                                    LetterMapNodeCard(
                                        node = node,
                                        discSize = d.mapNodeSize,
                                        onClick = { onNodeTap(node, index) }
                                    )
                                }
                                is MapNode.BlendItNode -> {
                                    BlendItChallengeNodeCard(
                                        node = node,
                                        onClick = { onNodeTap(node, index) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Layer 4: Full-body Animal Avatar Companions with Breathing Animation (Leader Avatar beside active node + Cheerful Friends)
                MapCompanionFriends(
                    nodeCount = mapNodes.size,
                    nodeCenters = nodeCenters,
                    activeNodeIndex = activeNodeIndex,
                    activeAvatarId = userStats.avatarId,
                    onCompanionTap = { animal ->
                        viewModel.playMascotTapReaction()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(totalMapHeightDp),
                    arrivalFromIndex = arrivalFromIndex
                )
            }
        }

        if (showUnlockChip) {
            UnlockChip(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp)
            )
        }

        // Duolingo Signature Floating 3D Node Action Card
        selectedNodeForAction?.let { targetNode ->
            NodeActionPopupDialog(
                node = targetNode,
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

/**
 * The back button and the header card, coloured for the Bohol section on screen. It reads the scroll itself, through
 * derivedStateOf, so only the header changes, and only when the section does: reading the scroll in MapScreen rebuilt
 * the whole map on every frame of a scroll (user, 2026-10-30: "when i scroll down the fps is like low").
 */
@Composable
private fun MapHeader(
    scrollState: ScrollState,
    userStats: UserMapStats,
    starsJustEarned: Int,
    lettersCompleted: Int,
    onBack: () -> Unit,
    onLilyTap: () -> Unit
) {
    val section by remember(scrollState) { derivedStateOf { mapSectionAt(scrollState.value, scrollState.maxValue) } }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 64 dp like every child-facing control (it was 46 dp).
        GummyBackButton(
            onClick = onBack,
            modifier = Modifier.testTag("map_back")
        )

        // One header card: the greeting is folded in, so the trail gets the screen (card 31e).
        // Tapping it, Lily says "Let's go! Tap a letter to begin our adventure!" (card 28).
        TopStatsBar(
            totalStars = userStats.totalStars,
            starsJustEarned = starsJustEarned,
            currentStreak = userStats.currentStreak,
            unlockedBadgesCount = userStats.unlockedBadgesCount,
            lettersCompleted = lettersCompleted,
            profileName = userStats.profileName,
            biomeTheme = BiomeThemes.forSection(section),
            onLilyTap = onLilyTap,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * A ring that grows and fades, over and over: the current letter's focus ring and an open chest's aura. Its
 * animation lives here, so it runs only where a ring shows, not in every stop on the map.
 */
@Composable
private fun PulseRing(
    size: Dp,
    width: Dp,
    color: Color,
    shape: Shape,
    durationMs: Int,
    maxScale: Float,
    startAlpha: Float,
    label: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = label)
    val spec = infiniteRepeatable<Float>(
        animation = tween(durationMs, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Restart
    )
    val ringScale by infiniteTransition.animateFloat(1.0f, maxScale, spec, label = "${label}Scale")
    val ringAlpha by infiniteTransition.animateFloat(startAlpha, 0.0f, spec, label = "${label}Alpha")
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                scaleX = ringScale
                scaleY = ringScale
                alpha = ringAlpha
            }
            .border(width = width, color = color, shape = shape)
    )
}

// ═══════════════════════════════════════════════════════════════════════════
// Letter Map Node — Modern Stepping Stone Disc
// ═══════════════════════════════════════════════════════════════════════════

private val NodeGoldFace = SunnyGold
private val NodeGoldShelf = SunnyGoldDark
private val NodeGoldStar = SunnyGoldDark

private val NodeGreenFace = EmeraldLeaf
private val NodeGreenShelf = EmeraldLeafDark

private val NodeLockedFace = LockedFace
private val NodeLockedShelf = LockedShelf
private val NodeLockedIcon = LockedIcon

@Composable
fun LetterMapNodeCard(
    node: MapNode.LetterNode,
    discSize: Dp = 76.dp,
    onClick: () -> Unit
) {
    val isCompleted = node.isUnlocked && node.starsEarned > 0
    val isCurrentActiveNode = node.isUnlocked && node.starsEarned == 0
    val isReducedMotion = LocalReducedMotion.current

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
            modifier = Modifier.size(width = discSize, height = discSize + 8.dp)
        ) {
            // Active Pulsing Ring Focus Aura
            if (isCurrentActiveNode && !isReducedMotion) {
                PulseRing(
                    size = discSize,
                    width = 3.5.dp,
                    color = NodeGreenShelf,
                    shape = CircleShape,
                    durationMs = 1400,
                    maxScale = 1.25f,
                    startAlpha = 0.65f,
                    label = "activeNodeRing"
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
                        .size(discSize)
                        .align(Alignment.BottomCenter)
                        .background(shelfColor, CircleShape)
                )

                // Top Disc Face
                Box(
                    modifier = Modifier
                        .size(discSize)
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
                                color = LockedIcon
                            )
                            // Mini Lock indicator badge at bottom right
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = (-4).dp, y = (-4).dp)
                                    .size(20.dp)
                                    .background(LockedInk, CircleShape)
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
                        tint = if (isEarned) Color(0xFFF59E0B) else LockedShelf,
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

    val chestFace = if (isUnlocked) SunnyGold else LockedFace
    val chestShelf = if (isUnlocked) SunnyGoldDark else LockedShelf
    val trimGold = if (isUnlocked) SunnyGoldLight else LockedShelf
    val lockBadgeColor = if (isUnlocked) SunnyGold else LockedIcon

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
                PulseRing(
                    size = 96.dp,
                    width = 4.dp,
                    color = ApricotGlow,
                    shape = RoundedCornerShape(26.dp),
                    durationMs = 1300,
                    maxScale = 1.30f,
                    startAlpha = 0.70f,
                    label = "chestAura"
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
                .background(if (isUnlocked) Color(0xFF78350F) else LockedInk, RoundedCornerShape(999.dp))
                .border(1.5.dp, if (isUnlocked) Color(0xFFFFC93C) else LockedIcon, RoundedCornerShape(999.dp))
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
                    modifier = Modifier.size(16.dp)
                )
                // 16 sp like all map text (it was 9.5 sp, card 31e).
                Text(
                    text = "BLEND ${node.groupId}",
                    fontFamily = LexendFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                    maxLines = 1
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// The unlock chip (Lily's greeting now lives in TopStatsBar, card 31e)
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun UnlockChip(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(EmeraldLeaf, RoundedCornerShape(999.dp))
            .border(2.dp, DarkBrownOutline, RoundedCornerShape(999.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.LockOpen,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "New letter open!",
            fontFamily = LexendFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            maxLines = 1
        )
    }
}
