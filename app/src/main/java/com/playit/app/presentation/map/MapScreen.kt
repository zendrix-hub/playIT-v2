package com.playit.app.presentation.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.MapNode
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.MascotBubble
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.components.idleBounce
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.map.components.MapPathCanvas
import com.playit.app.presentation.map.components.MapTerrainProps
import com.playit.app.presentation.map.components.TopStatsBar
import com.playit.app.presentation.map.components.calculateNodeXOffsetDp
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.DisabledColor
import com.playit.app.presentation.theme.DisabledColorShadow
import com.playit.app.presentation.theme.FriendlyPurple
import com.playit.app.presentation.theme.FriendlyPurpleShadow
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.LearningBlueShadow
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextSecondary

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNodeSelected: (String) -> Unit
) {
    val mapNodes by viewModel.mapNodes.collectAsState()
    val userStats by viewModel.userStats.collectAsState()

    // Identify current active node for mascot positioning
    val activeNodeIndex = remember(mapNodes) {
        val idx = mapNodes.indexOfFirst { it.isUnlocked && (it is MapNode.LetterNode && it.starsEarned == 0) }
        if (idx != -1) idx else mapNodes.indexOfLast { it.isUnlocked }.coerceAtLeast(0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SoftSky,
                        Color(0xFFE0F2FE),
                        CreamWhite
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Stats Bar
            TopStatsBar(
                totalStars = userStats.totalStars,
                currentStreak = userStats.currentStreak,
                unlockedBadgesCount = userStats.unlockedBadgesCount,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Mascot Prompt Header
            MascotBubble(
                message = "Tap an unlocked letter to start your sound adventure!",
                mascotState = MascotState.ENCOURAGING,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Winding Adventure Map Path Container
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                val density = LocalDensity.current
                val widthPx = with(density) { constraints.maxWidth.toDp().toPx() }
                val spacingDp = 38.dp
                val spacingPx = with(density) { spacingDp.toPx() }
                val topPaddingDp = 24.dp
                val topPaddingPx = with(density) { topPaddingDp.toPx() }

                // Calculate center coordinates for each node to draw the winding Bezier path
                val nodeCenters = remember(mapNodes, widthPx) {
                    val centers = mutableListOf<Offset>()
                    var currentY = topPaddingPx

                    mapNodes.forEachIndexed { index, node ->
                        val nodeHeightDp = if (node is MapNode.BlendItNode) 116.dp else 92.dp
                        val nodeHeightPx = with(density) { nodeHeightDp.toPx() }
                        val centerY = currentY + nodeHeightPx / 2f
                        val xOffsetPx = with(density) { calculateNodeXOffsetDp(index, 50.dp).toPx() }
                        val centerX = widthPx / 2f + xOffsetPx

                        centers.add(Offset(centerX, centerY))

                        val nextNode = mapNodes.getOrNull(index + 1)
                        val nextNodeHeightDp = if (nextNode is MapNode.BlendItNode) 116.dp else 92.dp
                        val nextNodeHeightPx = with(density) { nextNodeHeightDp.toPx() }

                        currentY += nodeHeightPx / 2f + spacingPx + nextNodeHeightPx / 2f
                    }
                    centers
                }

                // Total calculated map height to ensure path canvas & terrain props span full scroll length
                val totalMapHeightDp = remember(nodeCenters) {
                    if (nodeCenters.isNotEmpty()) {
                        with(density) { (nodeCenters.last().y + 120.dp.toPx()).toDp() }
                    } else {
                        800.dp
                    }
                }

                // Layer 1: Scattered Background Terrain Props (seeded by node index)
                MapTerrainProps(
                    nodeCount = mapNodes.size,
                    nodeVerticalSpacing = 130.dp,
                    topPadding = topPaddingDp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(totalMapHeightDp)
                )

                // Layer 2: Continuous Bezier Curved Path Canvas
                MapPathCanvas(
                    nodeCenters = nodeCenters,
                    nodes = mapNodes,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(totalMapHeightDp)
                )

                // Layer 3: Ambient Mascot Companion at the Active Node
                if (nodeCenters.isNotEmpty() && activeNodeIndex in nodeCenters.indices) {
                    val activeCenter = nodeCenters[activeNodeIndex]
                    val activeXDp = with(density) { activeCenter.x.toDp() }
                    val activeYDp = with(density) { activeCenter.y.toDp() }
                    val mascotIsLeft = activeNodeIndex % 2 == 0
                    val mascotXOffsetDp = if (mascotIsLeft) activeXDp - 95.dp else activeXDp + 45.dp
                    val mascotYOffsetDp = activeYDp - 40.dp

                    Image(
                        painter = rememberAssetPainter(MascotState.IDLE.assetPath),
                        contentDescription = "Lily Mascot Active Guide",
                        modifier = Modifier
                            .offset(x = mascotXOffsetDp, y = mascotYOffsetDp)
                            .size(68.dp)
                            .idleBounce(enabled = true)
                    )
                }

                // Layer 4: Interactive Map Nodes positioned along the winding path
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = topPaddingDp, bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacingDp)
                ) {
                    mapNodes.forEachIndexed { index, node ->
                        val xOffsetDp = calculateNodeXOffsetDp(index, 50.dp)

                        Box(
                            modifier = Modifier.offset(x = xOffsetDp),
                            contentAlignment = Alignment.Center
                        ) {
                            when (node) {
                                is MapNode.LetterNode -> {
                                    LetterMapNodeCard(
                                        node = node,
                                        onClick = { if (node.isUnlocked) onNodeSelected(node.id) }
                                    )
                                }
                                is MapNode.BlendItNode -> {
                                    BlendItChallengeNodeCard(
                                        node = node,
                                        onClick = { if (node.isUnlocked) onNodeSelected(node.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LetterMapNodeCard(
    node: MapNode.LetterNode,
    onClick: () -> Unit
) {
    val bgColor = if (node.isUnlocked) LearningBlue else DisabledColor
    val shadowColor = if (node.isUnlocked) LearningBlueShadow else DisabledColorShadow
    val textColor = if (node.isUnlocked) CreamWhite else TextSecondary.copy(alpha = 0.9f)
    val isCurrentActiveNode = node.isUnlocked && node.starsEarned == 0

    GummyContainer(
        onClick = onClick,
        enabled = node.isUnlocked,
        faceColor = bgColor,
        shadowColor = shadowColor,
        shape = CircleShape,
        strokeWidth = if (isCurrentActiveNode) 3.5.dp else 3.dp,
        strokeColor = DarkBrownOutline,
        depthHeight = 6.dp,
        modifier = Modifier
            .size(92.dp)
            .idleBounce(enabled = isCurrentActiveNode)
            .breathingPulse(enabled = isCurrentActiveNode)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (node.isUnlocked) node.symbol else "🔒",
                fontFamily = LexendFontFamily,
                fontSize = if (node.isUnlocked) 36.sp else 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
            if (node.isUnlocked && node.starsEarned > 0) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..node.starsEarned) {
                        Image(
                            painter = rememberAssetPainter("images/rewards/reward_star.png"),
                            contentDescription = "Star",
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BlendItChallengeNodeCard(
    node: MapNode.BlendItNode,
    onClick: () -> Unit
) {
    val bgColor = if (node.isUnlocked) FriendlyPurple else DisabledColor
    val shadowColor = if (node.isUnlocked) FriendlyPurpleShadow else DisabledColorShadow
    val textColor = if (node.isUnlocked) CreamWhite else TextSecondary.copy(alpha = 0.9f)
    val isCurrentActiveNode = node.isUnlocked

    GummyContainer(
        onClick = onClick,
        enabled = node.isUnlocked,
        faceColor = bgColor,
        shadowColor = shadowColor,
        shape = CircleShape,
        strokeWidth = 3.dp,
        strokeColor = DarkBrownOutline,
        depthHeight = 6.dp,
        modifier = Modifier
            .size(116.dp)
            .idleBounce(enabled = isCurrentActiveNode)
            .breathingPulse(enabled = isCurrentActiveNode)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = if (node.isUnlocked) "🧩" else "🔒",
                fontFamily = LexendFontFamily,
                fontSize = 36.sp
            )
            Text(
                text = "Blend ${node.groupId}",
                fontFamily = LexendFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
        }
    }
}

