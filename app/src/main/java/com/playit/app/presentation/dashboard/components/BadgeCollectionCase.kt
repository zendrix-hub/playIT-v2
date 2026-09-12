package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.profile.components.AvatarCircle
import com.playit.app.presentation.theme.*

@Composable
fun BadgeCollectionCase(
    completedLettersCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(2.5.dp, ModernBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SunnyGoldLight)
                        .border(2.dp, SunnyGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "Badges",
                        tint = SunnyGoldDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Column {
                    Text(
                        text = "Companion Badges",
                        fontFamily = LexendFontFamily,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextMidnight
                    )
                    Text(
                        text = "Animal explorer stamps earned",
                        fontFamily = LexendFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6 Avatars / Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..6).forEach { avatarId ->
                    val isUnlocked = avatarId <= 1 || completedLettersCount >= (avatarId - 1) * 4
                    CompanionBadgeSlot(avatarId = avatarId, isUnlocked = isUnlocked)
                }
            }
        }
    }
}

@Composable
private fun CompanionBadgeSlot(
    avatarId: Int,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = if (isUnlocked) {
                    "Companion $avatarId: unlocked"
                } else {
                    "Companion $avatarId: locked"
                }
            },
        contentAlignment = Alignment.TopCenter
    ) {
        if (isUnlocked) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .offset(y = 3.dp)
                    .clip(CircleShape)
                    .background(SunnyGoldShadow.copy(alpha = 0.35f))
            )
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SunnyGoldLight)
                    .border(2.dp, ModernBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AvatarCircle(avatarId = avatarId, size = 38)
            }
        } else {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(CanvasLight)
                    .border(1.5.dp, ModernBorderSoft, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AvatarCircle(avatarId = avatarId, size = 38)
            }
            // Small lock badge for locked companions
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
                    .border(1.5.dp, ModernBorderSoft, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }
}
