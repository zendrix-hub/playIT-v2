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
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.MangoShadow

@Composable
fun BadgeCollectionCase(
    completedLettersCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Cloud),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(3.dp, DarkBrownOutline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Mango.copy(alpha = 0.2f))
                        .border(2.dp, Mango, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "Badges",
                        tint = DarkBrownOutline,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Column {
                    Text(
                        text = "Companion Badges",
                        fontFamily = LexendFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Ink
                    )
                    Text(
                        text = "Animal explorer stamps earned",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = InkSoft
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

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
            .size(46.dp)
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
            // Soft translucent shadow to match the ring's translucent face
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .offset(y = 3.dp)
                    .clip(CircleShape)
                    .background(MangoShadow.copy(alpha = 0.25f))
            )
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Mango.copy(alpha = 0.15f))
                    .border(2.dp, DarkBrownOutline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AvatarCircle(avatarId = avatarId, size = 36)
            }
        } else {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Cloud)
                    .border(2.dp, DarkBrownOutline.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AvatarCircle(avatarId = avatarId, size = 40)
            }
            // Small lock badge for locked companions
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Cloud)
                    .border(1.5.dp, DarkBrownOutline.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = InkSoft,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }
}
