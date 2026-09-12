package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.presentation.theme.*

@Composable
fun OverallStatsCard(
    data: ProfileDashboardData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Row 1: Accuracy Rate Card
        GummyMetricCard(
            title = "Overall Accuracy",
            value = "${data.overallAccuracy.toInt()}%",
            subtitle = "Pronunciation & phoneme mastery",
            icon = Icons.Default.Check,
            iconTint = EmeraldLeaf,
            iconBg = EmeraldLeafLight,
            progress = (data.overallAccuracy / 100f).coerceIn(0f, 1f),
            progressColor = EmeraldLeaf
        )

        // Row 2: Two Side-by-Side Cards (Letters Mastered & Total Stars)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GummyMiniCard(
                title = "Mastered",
                value = "${data.completedLettersCount} / ${data.totalLettersCount}",
                subtitle = "Letters",
                icon = Icons.Default.CheckCircle,
                iconTint = PrimaryJoy,
                iconBg = PrimaryJoyLight,
                modifier = Modifier.weight(1f)
            )

            GummyMiniCard(
                title = "Total Stars",
                value = "${data.totalStars}",
                subtitle = "Rewards",
                icon = Icons.Default.Star,
                iconTint = SunnyGoldDark,
                iconBg = SunnyGoldLight,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GummyMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    progress: Float,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CardShape,
        color = SurfaceCard,
        border = BorderStroke(2.5.dp, ModernBorder),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(iconBg)
                            .border(1.5.dp, iconTint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text(text = title, fontFamily = LexendFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextMidnight)
                        Text(text = subtitle, fontFamily = LexendFontFamily, fontSize = 12.sp, color = TextMuted)
                    }
                }

                Text(
                    text = value,
                    fontFamily = LexendFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Modern Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(PillShape)
                    .background(ModernBorderFaint)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(PillShape)
                        .background(progressColor)
                )
            }
        }
    }
}

@Composable
private fun GummyMiniCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = Squircle20,
        color = SurfaceCard,
        border = BorderStroke(2.dp, ModernBorder),
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconBg)
                    .border(1.5.dp, iconTint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontFamily = LexendFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = TextMidnight
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$title ($subtitle)",
                fontFamily = LexendFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}
