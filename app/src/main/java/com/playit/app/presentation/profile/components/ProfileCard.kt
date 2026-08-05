package com.playit.app.presentation.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.Profile
import com.playit.app.presentation.components.bounceClick
import com.playit.app.presentation.theme.AchievementGold
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary
import com.playit.app.presentation.theme.TextSecondary

@Composable
fun ProfileCard(
    profile: Profile,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(104.dp)
            .bounceClick { onSelect(profile.id) }
            .shadow(6.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = CreamWhite,
        border = androidx.compose.foundation.BorderStroke(2.dp, SoftSky)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pediatric Animal Avatar
            AvatarCircle(avatarId = profile.avatarResId, size = 68)

            Spacer(modifier = Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(AchievementGold.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "⭐", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${profile.totalStars} Stars Earned",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
            }

            Text(
                text = "▶",
                fontSize = 24.sp,
                color = LearningBlue,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun AvatarCircle(avatarId: Int, size: Int) {
    val avatarEmojis = listOf("🦁", "🐯", "🐻", "🐸", "🐰", "🦊", "🐼", "🦄")
    val bgColors = listOf(
        LearningBlue,
        Color(0xFFFF7043),
        Color(0xFFAB47BC),
        Color(0xFF26A69A),
        AchievementGold,
        Color(0xFFEC407A),
        Color(0xFF8B5CF6),
        Color(0xFF10B981)
    )
    val index = (avatarId - 1).coerceIn(0, avatarEmojis.size - 1)

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(bgColors[index])
            .border(3.dp, CreamWhite, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = avatarEmojis[index],
            fontSize = (size / 2.2).sp
        )
    }
}

