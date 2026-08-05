package com.playit.app.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.MapNode
import com.playit.app.presentation.components.MascotBubble
import com.playit.app.presentation.components.bounceClick
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.map.components.TopStatsBar
import com.playit.app.presentation.theme.AchievementGold
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DisabledColor
import com.playit.app.presentation.theme.FriendlyPurple
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary
import com.playit.app.presentation.theme.TextSecondary
import kotlin.math.sin

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNodeSelected: (String) -> Unit
) {
    val mapNodes by viewModel.mapNodes.collectAsState()
    val userStats by viewModel.userStats.collectAsState()

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
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Stats Bar
            TopStatsBar(
                totalStars = userStats.totalStars,
                currentStreak = userStats.currentStreak,
                unlockedBadgesCount = userStats.unlockedBadgesCount,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Mascot Prompt Header
            MascotBubble(
                message = "Tap an unlocked letter to start your sound adventure!",
                mascotEmoji = "🦜",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Winding Adventure Map Path
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(mapNodes, key = { _, node -> node.id }) { index, node ->
                    // Sine-wave offset creating a winding S-curve adventure path
                    val xOffsetDp = (sin(index * 0.85) * 75).dp

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(x = xOffsetDp)
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

                        // Connecting path dash
                        if (index < mapNodes.size - 1) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (node.isUnlocked) GrowthGreen else DisabledColor.copy(alpha = 0.5f)
                                    )
                            )
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
    val textColor = if (node.isUnlocked) CreamWhite else TextSecondary
    val isCurrentActiveNode = node.isUnlocked && node.starsEarned == 0

    Surface(
        modifier = Modifier
            .size(92.dp)
            .breathingPulse(enabled = isCurrentActiveNode)
            .bounceClick(enabled = node.isUnlocked, onClick = onClick)
            .shadow(
                elevation = if (node.isUnlocked) 8.dp else 2.dp,
                shape = CircleShape,
                spotColor = LearningBlue
            ),
        shape = CircleShape,
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isCurrentActiveNode) 4.dp else 2.dp,
            color = if (isCurrentActiveNode) AchievementGold else CreamWhite.copy(alpha = 0.6f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (node.isUnlocked) node.symbol else "🔒",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
                if (node.isUnlocked && node.starsEarned > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..node.starsEarned) {
                            Text(text = "⭐", fontSize = 11.sp)
                        }
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
    val textColor = if (node.isUnlocked) CreamWhite else TextSecondary

    Surface(
        modifier = Modifier
            .size(116.dp)
            .breathingPulse(enabled = node.isUnlocked)
            .bounceClick(enabled = node.isUnlocked, onClick = onClick)
            .shadow(
                elevation = if (node.isUnlocked) 10.dp else 2.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = FriendlyPurple
            ),
        shape = RoundedCornerShape(32.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(
            width = 3.dp,
            color = if (node.isUnlocked) AchievementGold else CreamWhite.copy(alpha = 0.6f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = if (node.isUnlocked) "🧩" else "🔒",
                    fontSize = 36.sp
                )
                Text(
                    text = "Blend ${node.groupId}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
            }
        }
    }
}

