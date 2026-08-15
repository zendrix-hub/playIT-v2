package com.playit.app.presentation.map.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.AchievementGold
import com.playit.app.presentation.theme.CreamWhite

@Composable
fun TopStatsBar(
    totalStars: Int,
    currentStreak: Int,
    unlockedBadgesCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CreamWhite,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Total Stars
            StatChip(
                assetPath = "images/rewards/reward_star.png",
                value = "$totalStars Stars",
                color = AchievementGold
            )

            // Daily Streak
            StatChip(
                assetPath = "images/rewards/reward_streak.png",
                value = "$currentStreak Days",
                color = Color(0xFFFF5722)
            )

            // Milestone Badges
            StatChip(
                assetPath = "images/rewards/reward_confetti_burst.png",
                value = "$unlockedBadgesCount Badges",
                color = Color(0xFF9C27B0)
            )
        }
    }
}

@Composable
private fun StatChip(
    assetPath: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Image(
            painter = rememberAssetPainter(assetPath),
            contentDescription = value,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
