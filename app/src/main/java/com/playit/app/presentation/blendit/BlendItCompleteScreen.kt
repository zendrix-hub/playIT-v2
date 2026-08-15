package com.playit.app.presentation.blendit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.playit.app.presentation.theme.EnergyOrange
import com.playit.app.presentation.theme.FriendlyPurple
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary

@Composable
fun BlendItCompleteScreen(
    viewModel: BlendItCompleteViewModel,
    onReturnToMap: () -> Unit
) {
    val starsEarned by viewModel.starsEarned.collectAsState()

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
            // Docked Mascot Prompt
            DockedMascotWithBubble(
                message = "Group ${viewModel.groupId} Word Challenge Mastered!",
                mascotState = MascotState.CELEBRATING
            )

            Spacer(modifier = Modifier.weight(1f))

            // Confetti burst flourish — pops in first, ahead of the card, to open the
            // celebration beat before anything else settles in.
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
            GummyStaticContainer(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🧩 🥳 🎉", fontSize = 72.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Group ${viewModel.groupId} Mastered!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bouncy staggered star pop-in (Modifier.popIn inside StarDisplay)
                    StarDisplay(earnedStars = starsEarned, maxStars = 3, starSize = 54.dp)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Streak badge — reward_streak.png, popping in after the stars finish
                    // (viewModel plays a STREAK_BADGE_UNLOCK sfx for this exact moment, so
                    // the visual badge now matches the audio cue that was already firing
                    // with nothing on screen to go with it). No streak *count* shown: the
                    // ViewModel only exposes that a streak bonus fired, not a numeric
                    // value, so showing a number here would be fabricating data.
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.popIn(delayMillis = 450)
                    ) {
                        Image(
                            painter = rememberAssetPainter("images/rewards/reward_streak.png"),
                            contentDescription = "Streak bonus",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Streak Bonus Unlocked!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EnergyOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Awesome word blending! Next Marungko letter group unlocked on your adventure map!",
                        fontSize = 18.sp,
                        color = FriendlyPurple,
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
