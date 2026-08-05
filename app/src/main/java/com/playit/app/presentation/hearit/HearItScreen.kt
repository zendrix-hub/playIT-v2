package com.playit.app.presentation.hearit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.MascotBubble
import com.playit.app.presentation.components.PediatricButton
import com.playit.app.presentation.components.bounceClick
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.theme.AchievementGold
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary

@Composable
fun HearItScreen(
    viewModel: HearItViewModel,
    onNext: (String) -> Unit,
    onBack: () -> Unit
) {
    val phoneme by viewModel.phoneme.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val targetLetter = phoneme?.letter?.uppercase() ?: "M"

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
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .background(CreamWhite, CircleShape)
                        .shadow(2.dp, CircleShape)
                ) {
                    Text(text = "⬅️", fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Hear It — Letter $targetLetter",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mascot Bubble Guidance
            MascotBubble(
                message = "Tap the big speaker button to listen to the letter sound!",
                mascotEmoji = "🦜"
            )

            Spacer(modifier = Modifier.weight(1f))

            // 3D Animated Letter Card
            Surface(
                modifier = Modifier
                    .size(240.dp)
                    .shadow(12.dp, RoundedCornerShape(36.dp), spotColor = LearningBlue),
                shape = RoundedCornerShape(36.dp),
                color = CreamWhite,
                border = androidx.compose.foundation.BorderStroke(4.dp, SoftSky)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = targetLetter,
                            fontSize = 120.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LearningBlue
                        )
                        Text(
                            text = "Sound: /${phoneme?.letter ?: "m"}/",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = AchievementGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3D Play Sound Replay Button
            Surface(
                modifier = Modifier
                    .size(92.dp)
                    .breathingPulse(enabled = isPlaying)
                    .bounceClick(onClick = { viewModel.playPhonemeSound() })
                    .shadow(10.dp, CircleShape, spotColor = LearningBlue),
                shape = CircleShape,
                color = LearningBlue,
                border = androidx.compose.foundation.BorderStroke(3.dp, CreamWhite)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = if (isPlaying) "🔊" else "▶️", fontSize = 42.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue to Say It CTA
            PediatricButton(
                text = "Next: Say It 🎤",
                onClick = { onNext(phoneme?.id?.toString() ?: "1") },
                backgroundColor = GrowthGreen,
                fontSize = 22,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

