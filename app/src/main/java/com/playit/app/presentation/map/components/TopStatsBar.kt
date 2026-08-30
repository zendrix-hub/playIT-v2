package com.playit.app.presentation.map.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.SoftSky

@Composable
fun TopStatsBar(
    totalStars: Int,
    currentStreak: Int,
    @Suppress("UNUSED_PARAMETER") unlockedBadgesCount: Int,
    lettersCompleted: Int = 0,
    biomeTheme: BiomeTheme = BiomeThemes.SECTION_1,
    modifier: Modifier = Modifier,
    profileName: String = ""
) {
    // Smooth color animation that adapts to the currently visible section on scroll
    val animatedBg by animateColorAsState(
        targetValue = biomeTheme.backgroundTint.copy(alpha = 0.95f),
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "statsBgAnim"
    )
    val animatedBorder by animateColorAsState(
        targetValue = biomeTheme.borderTint,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "statsBorderAnim"
    )
    val animatedPillBg by animateColorAsState(
        targetValue = biomeTheme.pillBg,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "statsPillBgAnim"
    )
    val animatedProgressColor by animateColorAsState(
        targetValue = biomeTheme.progressColor,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "statsProgressAnim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = animatedBg,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.5.dp,
                color = animatedBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Name + Mini Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(SoftSky)
                        .border(1.5.dp, DarkBrownOutline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAssetPainter(MascotState.IDLE.assetPath),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    )
                }

                Text(
                    text = profileName.ifEmpty { "Learner" },
                    color = Ink,
                    fontSize = 15.sp,
                    fontFamily = LexendFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }

            // Stats Pills (Clean Visual Badges)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak Pill
                StatPill(
                    count = currentStreak,
                    assetPath = "images/rewards/reward_streak.png",
                    backgroundColor = animatedPillBg,
                    textColor = biomeTheme.textSecondary
                )

                // Stars Pill
                StatPill(
                    count = totalStars,
                    assetPath = "images/rewards/reward_star.png",
                    backgroundColor = animatedPillBg,
                    textColor = biomeTheme.textPrimary
                )
            }
        }

        // Letters Learned Progress
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$lettersCompleted of 28 letters",
                fontSize = 12.sp,
                fontFamily = LexendFontFamily,
                fontWeight = FontWeight.Bold,
                color = InkSoft
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(999.dp))
                    .clip(RoundedCornerShape(999.dp))
            ) {
                val progress = (lettersCompleted.toFloat() / 28f).coerceIn(0f, 1f)
                if (progress > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress)
                            .fillMaxHeight()
                            .background(animatedProgressColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    count: Int,
    assetPath: String,
    backgroundColor: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Image(
            painter = rememberAssetPainter(assetPath),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$count",
            fontSize = 13.sp,
            fontFamily = LexendFontFamily,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}
