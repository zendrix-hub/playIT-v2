package com.playit.app.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.MapNode
import com.playit.app.presentation.theme.AchievementGold
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DisabledColor
import com.playit.app.presentation.theme.FriendlyPurple
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary
import com.playit.app.presentation.theme.TextSecondary

import com.playit.app.presentation.components.bounceClick
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.map.components.TopStatsBar

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
            .background(SoftSky)
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Stats Bar
            TopStatsBar(
                totalStars = userStats.totalStars,
                currentStreak = userStats.currentStreak,
                unlockedBadgesCount = userStats.unlockedBadgesCount,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Winding Path Node List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(mapNodes, key = { it.id }) { node ->
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

@Composable
fun LetterMapNodeCard(
    node: MapNode.LetterNode,
    onClick: () -> Unit
) {
    val bgColor = if (node.isUnlocked) LearningBlue else DisabledColor
    val textColor = if (node.isUnlocked) CreamWhite else TextSecondary
    val isCurrentActiveNode = node.isUnlocked && node.starsEarned == 0

    Card(
        modifier = Modifier
            .size(90.dp)
            .breathingPulse(enabled = isCurrentActiveNode)
            .bounceClick(enabled = node.isUnlocked, onClick = onClick),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                if (node.isUnlocked && node.starsEarned > 0) {
                    Text(
                        text = "⭐".repeat(node.starsEarned),
                        fontSize = 12.sp
                    )
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

    Card(
        modifier = Modifier
            .size(110.dp)
            .breathingPulse(enabled = node.isUnlocked)
            .bounceClick(enabled = node.isUnlocked, onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                    text = if (node.isUnlocked) "🧩" else "🔒",
                    fontSize = 32.sp
                )
                Text(
                    text = "Blend ${node.groupId}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}
