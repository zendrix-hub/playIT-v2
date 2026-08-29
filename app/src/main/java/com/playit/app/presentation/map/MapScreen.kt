package com.playit.app.presentation.map

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import com.playit.app.presentation.map.components.ChocolateHillsBackground
import com.playit.app.presentation.map.components.GroupBannerStatus
import com.playit.app.presentation.map.components.MapPathCanvas
import com.playit.app.presentation.map.components.MapTerrainProps
import com.playit.app.presentation.map.components.MarungkoGroupBanner
import com.playit.app.presentation.map.components.TopStatsBar
import com.playit.app.presentation.map.components.calculateNodeXOffsetDp
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.DarkBrownOutline
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
import com.playit.app.presentation.theme.Tan
import com.playit.app.presentation.theme.TanDark
import com.playit.app.presentation.theme.TanShadow
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

    val mapNodes by viewModel.mapNodes.collectAsState()
    val userStats by viewModel.userStats.collectAsState()
    val isReducedMotion = LocalReducedMotion.current
    val scrollState = rememberScrollState()

    // Identify current active node for auto-scroll and companion mascot positioning
    val activeNodeIndex = remember(mapNodes) {
        val idx = mapNodes.indexOfFirst { it.isUnlocked && (it is MapNode.LetterNode && it.starsEarned == 0) }
        if (idx != -1) idx else mapNodes.indexOfLast { it.isUnlocked }.coerceAtLeast(0)
    }

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
            // ── Header: Floating Gummy Top Bar ──────────────────────────────
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
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Mascot Prompt Header (Personalized Instant Greeting) ───────
            val welcomeGreeting = if (userStats.profileName.isNotBlank()) {
                "Welcome, ${userStats.profileName}! Tap a letter to start!"
            } else {
                "Welcome! Tap a letter to start!"
            }

            MascotBubble(
                message = welcomeGreeting,
                mascotState = MascotState.ENCOURAGING,
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

                // Auto-scroll to active node on map launch
                var hasAutoScrolled by remember { mutableStateOf(false) }
                LaunchedEffect(activeNodeIndex, nodeCenters) {
                    if (!hasAutoScrolled && nodeCenters.isNotEmpty() && activeNodeIndex in nodeCenters.indices) {
                        val activeCenterY = nodeCenters[activeNodeIndex].y
                        val viewportOffsetPx = with(density) { 260.dp.toPx() }
                        val targetScrollPx = if (activeNodeIndex == 0) 0f else (activeCenterY - viewportOffsetPx).coerceAtLeast(0f)
                        delay(250)
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

                // Total calculated map height to ensure path canvas & terrain props span full scroll length
                val totalMapHeightDp = remember(nodeCenters) {
                    if (nodeCenters.isNotEmpty()) {
                        with(density) { (nodeCenters.last().y + MAP_BOTTOM_EXTENSION.toPx()).toDp() }
                    } else {
                        800.dp
                    }
                }

                // Layer 0: Sky-to-sand gradient background with Chocolate Hills
                ChocolateHillsBackground(
                    totalHeight = totalMapHeightDp,
                    modifier = Modifier.fillMaxWidth()
                )

                // Layer 1: Scattered Filipino cultural terrain props (palm, hut, flowers)
                MapTerrainProps(
                    nodeCount = mapNodes.size,
                    nodeVerticalSpacing = TERRAIN_PROPS_VERTICAL_SPACING,
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

                // Layer 4: Interactive Map Nodes positioned along the winding path with Group Banners
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
                                                onNodeSelected(node.id)
                                            } else {
                                                shakenNodeId = node.id
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
                                                onNodeSelected(node.id)
                                            } else {
                                                shakenNodeId = node.id
                                                lockedBlendItDialogGroup = node.groupId
                                                viewModel.onLockedNodeTapped()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Layer 5: Ambient Mascot Companion at the Active Node with dynamic left/right placement & mini dialogue bubble
                if (nodeCenters.isNotEmpty() && activeNodeIndex in nodeCenters.indices) {
                    val activeCenter = nodeCenters[activeNodeIndex]
                    val activeXDp = with(density) { activeCenter.x.toDp() }
                    val activeYDp = with(density) { activeCenter.y.toDp() }
                    val activeNodeXOffset = calculateNodeXOffsetDp(activeNodeIndex, PATH_AMPLITUDE_X)

                    // Dynamic placement: if node is shifted right of center, place mascot to the left; else to the right
                    val mascotIsLeft = activeNodeXOffset >= 0.dp
                    val mascotXOffsetDp = if (mascotIsLeft) (activeXDp - 88.dp) else (activeXDp + 46.dp)
                    val mascotYOffsetDp = activeYDp - 48.dp

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .offset(x = mascotXOffsetDp, y = mascotYOffsetDp)
                            .idleBounce(enabled = true)
                    ) {
                        // Mini Speech Dialogue Bubble above Mascot
                        MascotMapDialogueBubble(
                            message = "Let's Go!",
                            onClick = { viewModel.playMascotTapReaction() },
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // Mascot Avatar Button
                        GummyContainer(
                            onClick = { viewModel.playMascotTapReaction() },
                            faceColor = Color.Transparent,
                            shadowColor = Color.Transparent,
                            shape = CircleShape,
                            strokeWidth = 0.dp,
                            depthHeight = 0.dp,
                            modifier = Modifier.size(MASCOT_SIZE)
                        ) {
                            Image(
                                painter = rememberAssetPainter(MascotState.IDLE.assetPath),
                                contentDescription = "Lily Mascot Active Guide",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
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
        faceColor = Cloud,
        shadowColor = TanShadow.copy(alpha = 0.5f),
        strokeColor = DarkBrownOutline,
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
                fontSize = 10.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Ink
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Letter Map Node — Filipino palette & Duolingo ABC Polish
// ═══════════════════════════════════════════════════════════════════════════

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
        targetValue = 1.22f,
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

    // Filipino palette node colors
    val bgColor = when {
        isCompleted -> Leaf
        node.isUnlocked -> Mango
        else -> Cloud
    }
    val shadowColor = when {
        isCompleted -> LeafShadow
        node.isUnlocked -> MangoShadow
        else -> TanShadow.copy(alpha = 0.55f)
    }
    val textColor = when {
        isCompleted -> Cloud
        node.isUnlocked -> Ink
        else -> InkSoft.copy(alpha = 0.65f)
    }
    val borderColor = when {
        isCurrentActiveNode -> DarkBrownOutline
        isCompleted -> DarkBrownOutline
        else -> DarkBrownOutline.copy(alpha = 0.6f)
    }

    val accessibilityLabel = when {
        isCompleted -> "Letter ${node.symbol}, completed, ${node.starsEarned} stars"
        node.isUnlocked -> "Letter ${node.symbol}, current lesson"
        else -> "Letter ${node.symbol}, locked"
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = accessibilityLabel
        }
    ) {
        // Active Pulsing Ring Focus Aura
        if (isCurrentActiveNode && !isReducedMotion) {
            Box(
                modifier = Modifier
                    .size(LETTER_NODE_SIZE)
                    .graphicsLayer {
                        scaleX = ringScale
                        scaleY = ringScale
                        alpha = ringAlpha
                    }
                    .border(
                        width = 3.dp,
                        color = MangoDark,
                        shape = CircleShape
                    )
            )
        }

        Box(contentAlignment = Alignment.TopCenter) {
            GummyContainer(
                onClick = onClick,
                enabled = true,
                faceColor = bgColor,
                shadowColor = shadowColor,
                shape = CircleShape,
                strokeWidth = if (isCurrentActiveNode) 3.5.dp else 3.dp,
                strokeColor = borderColor,
                depthHeight = if (node.isUnlocked) 5.dp else 3.dp,
                modifier = Modifier
                    .size(LETTER_NODE_SIZE)
                    .idleBounce(enabled = isCurrentActiveNode)
                    .breathingPulse(enabled = isCurrentActiveNode)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (node.isUnlocked) {
                        Text(
                            text = node.symbol,
                            fontFamily = LexendFontFamily,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textColor
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = node.symbol,
                                fontFamily = LexendFontFamily,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = InkSoft.copy(alpha = 0.22f)
                            )
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = "Locked Letter",
                                tint = textColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Checkmark badge for completed nodes
            if (isCompleted) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(20.dp)
                        .background(Cloud, CircleShape)
                        .border(1.dp, LeafDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Completed",
                        tint = LeafDark,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Floating 3-Star Crown Arch
            if (isCompleted && node.starsEarned > 0) {
                Row(
                    modifier = Modifier.offset(y = (-14).dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(node.starsEarned) { starIndex ->
                        val starRotation = when (starIndex) {
                            0 -> -15f
                            2 -> 15f
                            else -> 0f
                        }
                        val starSize = if (starIndex == 1) 22.dp else 18.dp
                        val starYOffset = if (starIndex == 1) (-3).dp else 0.dp

                        Image(
                            painter = rememberAssetPainter("images/rewards/reward_star.png"),
                            contentDescription = "Star ${starIndex + 1}",
                            modifier = Modifier
                                .offset(y = starYOffset)
                                .size(starSize)
                                .rotate(starRotation)
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Blend It Challenge Node — Rattan Weave Pattern
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun BlendItChallengeNodeCard(
    node: MapNode.BlendItNode,
    onClick: () -> Unit
) {
    val isCurrentActiveNode = node.isUnlocked

    val bgColor = if (node.isUnlocked) Ube else Cloud
    val shadowColor = if (node.isUnlocked) UbeShadow else TanShadow.copy(alpha = 0.55f)
    val textColor = if (node.isUnlocked) Cloud else InkSoft.copy(alpha = 0.75f)
    val borderColor = if (node.isUnlocked) DarkBrownOutline else DarkBrownOutline.copy(alpha = 0.65f)

    val accessibilityLabel = if (node.isUnlocked) {
        "Blend-It Challenge Group ${node.groupId}"
    } else {
        "Blend-It Challenge Group ${node.groupId}, locked"
    }

    GummyContainer(
        onClick = onClick,
        enabled = true,
        faceColor = bgColor,
        shadowColor = shadowColor,
        shape = RoundedCornerShape(22.dp),
        strokeWidth = 3.dp,
        strokeColor = borderColor,
        depthHeight = 5.dp,
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                contentDescription = accessibilityLabel
            }
            .size(width = BLEND_IT_NODE_SIZE, height = 54.dp)
            .idleBounce(enabled = isCurrentActiveNode)
            .breathingPulse(enabled = isCurrentActiveNode)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (node.isUnlocked) Icons.Rounded.Extension else Icons.Rounded.Lock,
                contentDescription = if (node.isUnlocked) "Blend Challenge" else "Locked Challenge",
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = if (node.isUnlocked) " Blend ${node.groupId}" else " Locked",
                fontFamily = LexendFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
        }
    }
}

