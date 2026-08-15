package com.playit.app.presentation.lettercomplete

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.DockedMascotWithBubble
import com.playit.app.presentation.components.GummyStaticContainer
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.PediatricButton
import com.playit.app.presentation.components.StarDisplay
import com.playit.app.presentation.components.popIn
import com.playit.app.presentation.components.rememberAssetPainter
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
            // Docked Mascot Celebration Prompt
            DockedMascotWithBubble(
                message = "You mastered Letter $letter! Amazing sound practice!",
                mascotState = MascotState.CELEBRATING
            )

            Spacer(modifier = Modifier.weight(1f))

            // Confetti burst flourish — pops in first, ahead of the card.
            Image(
                painter = rememberAssetPainter("images/rewards/reward_confetti_burst.png"),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .popIn()
            )

            // 3D Gummy Celebration Card (28dp radius, 3dp DarkBrownOutline, 6dp depth band)
            // No streak badge here, unlike BlendItCompleteScreen: this ViewModel fires a
            // NODE_UNLOCK_CHIME, not a streak event — there's no streak signal to honestly
            // represent on this screen. Say the word if letter completions should count
            // toward the same streak and I'll wire it up to match.
            GummyStaticContainer(
                modifier = Modifier.fillMaxWidth()
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

                    // Bouncy staggered star pop-in (Modifier.popIn inside StarDisplay)
                    StarDisplay(earnedStars = starsEarned, maxStars = 3, starSize = 54.dp)

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "You earned $starsEarned ${if (starsEarned == 1) "Star" else "Stars"}! Sound adventure unlocked on the map!",
                        fontSize = 18.sp,
                        color = LearningBlue,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Already on the updated GummyButton contract via PediatricButton's
            // delegation — no direct change needed here beyond that shared fix.
            PediatricButton(
                text = "Back to Map 🗺️",
                onClick = onReturnToMap,
                backgroundColor = GrowthGreen,
                fontSize = 22,
                isSquashed = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
