package com.playit.app.presentation.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.Profile
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.*

@Composable
fun ProfileCard(
    profile: Profile,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val starsWordLower = if (profile.totalStars == 1) "star" else "stars"
    val starsWordTitle = if (profile.totalStars == 1) "Star" else "Stars"
    val accessibilityDescription =
        "Profile for ${profile.name}, ${profile.totalStars} $starsWordLower earned. Tap to start learning."

    GummyContainer(
        onClick = { onSelect(profile.id) },
        faceColor = Cloud,
        shadowColor = CloudShadow,
        shape = RoundedCornerShape(24.dp),
        strokeWidth = 3.dp,
        strokeColor = DarkBrownOutline,
        depthHeight = 5.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(112.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = accessibilityDescription
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pediatric Animal Avatar Frame
            AvatarCircle(avatarId = profile.avatarResId, size = 72)

            Spacer(modifier = Modifier.width(16.dp))

            // Profile Info (Name + Star Pill)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = profile.name,
                    fontFamily = LexendFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = Mango.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Image(
                        painter = rememberAssetPainter("images/rewards/reward_star.png"),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${profile.totalStars} $starsWordTitle",
                        fontFamily = LexendFontFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Chevron Pill
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Ube)
                    .border(2.dp, DarkBrownOutline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Cloud,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
