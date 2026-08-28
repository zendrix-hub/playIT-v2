package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.presentation.profile.components.AvatarCircle
import com.playit.app.presentation.theme.*

@Composable
fun LearnerHeroCard(
    data: ProfileDashboardData,
    modifier: Modifier = Modifier
) {
    val masteryFraction = if (data.totalLettersCount > 0) {
        (data.completedLettersCount.toFloat() / data.totalLettersCount.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Cloud),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(3.dp, DarkBrownOutline)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Row 1: Avatar, Name, Streak & Level
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarCircle(
                    avatarId = data.profile.avatarResId,
                    size = 60
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.profile.name,
                        fontFamily = LexendFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Ink
                    )
                    Text(
                        text = "Marungko Phonics Explorer",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = InkSoft
                    )
                }

                // Streak Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Mango.copy(alpha = 0.2f))
                        .border(2.dp, Mango, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Streak: ${data.profile.currentStreak}d",
                            fontFamily = LexendFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Ink
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Row 2: Mastery Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Curriculum Mastery",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )
                    Text(
                        text = "${data.completedLettersCount} of ${data.totalLettersCount} Sounds (${(masteryFraction * 100).toInt()}%)",
                        fontFamily = LexendFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ube
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { masteryFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    color = Leaf,
                    trackColor = Sand.copy(alpha = 0.5f)
                )
            }
        }
    }
}
