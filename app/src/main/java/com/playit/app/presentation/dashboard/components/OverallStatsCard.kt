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
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.Sky
import com.playit.app.presentation.theme.Ube

@Composable
fun OverallStatsCard(
    data: ProfileDashboardData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Row 1: Accuracy Rate Card (slight -1 deg rotation)
        GummyMetricCard(
            title = "Overall Accuracy",
            value = "${data.overallAccuracy.toInt()}%",
            subtitle = "Pronunciation & phoneme mastery",
            icon = Icons.Default.Check,
            iconTint = Leaf,
            rotation = -1f,
            progress = (data.overallAccuracy / 100f).coerceIn(0f, 1f),
            progressColor = Leaf
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
                iconTint = Ube,
                rotation = 1f,
                modifier = Modifier.weight(1f)
            )

            GummyMiniCard(
                title = "Total Stars",
                value = "${data.totalStars}",
                subtitle = "Rewards",
                icon = Icons.Default.Star,
                iconTint = Mango,
                rotation = -1.5f,
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
    rotation: Float,
    progress: Float,
    progressColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Cloud,
        border = BorderStroke(3.dp, DarkBrownOutline),
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .rotate(rotation)
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
                            .background(Sky)
                            .border(2.dp, DarkBrownOutline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                    }
                    Column {
                        Text(text = title, fontFamily = LexendFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink)
                        Text(text = subtitle, fontFamily = LexendFontFamily, fontSize = 12.sp, color = InkSoft)
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

            // Gummy Progress Bar Well
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Sky)
                    .border(2.dp, DarkBrownOutline, RoundedCornerShape(10.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
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
    rotation: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Cloud,
        border = BorderStroke(3.dp, DarkBrownOutline),
        shadowElevation = 4.dp,
        modifier = modifier.rotate(rotation)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Sky)
                    .border(2.dp, DarkBrownOutline, CircleShape),
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
                color = Ink
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$title ($subtitle)",
                fontFamily = LexendFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = InkSoft
            )
        }
    }
}
