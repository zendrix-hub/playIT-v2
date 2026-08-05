package com.playit.app.presentation.lettercomplete

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.MascotBubble
import com.playit.app.presentation.components.PediatricButton
import com.playit.app.presentation.components.StarDisplay
import com.playit.app.presentation.theme.AchievementGold
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary

@Composable
fun LetterCompleteScreen(
    viewModel: LetterCompleteViewModel,
    onReturnToMap: () -> Unit
) {
    val phoneme by viewModel.phoneme.collectAsState()
    val starsEarned by viewModel.starsEarned.collectAsState()
    val letter = phoneme?.letter?.uppercase() ?: "M"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SoftSky,
                        CreamWhite
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // 3D Celebration Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(36.dp), spotColor = AchievementGold),
                shape = RoundedCornerShape(36.dp),
                color = CreamWhite,
                border = androidx.compose.foundation.BorderStroke(4.dp, AchievementGold.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🥳 🎉", fontSize = 72.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Letter $letter Mastered!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bouncy Star Celebration Display
                    StarDisplay(earnedStars = starsEarned, maxStars = 3, starSize = 54.dp)

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "You earned $starsEarned ${if (starsEarned == 1) "Star" else "Stars"}! Sound adventure unlocked on the map!",
                        fontSize = 18.sp,
                        color = LearningBlue,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PediatricButton(
                text = "Back to Map 🗺️",
                onClick = onReturnToMap,
                backgroundColor = GrowthGreen,
                fontSize = 22,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

