package com.playit.app.presentation.sayit

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
import com.playit.app.presentation.components.HeartBar
import com.playit.app.presentation.components.MascotBubble
import com.playit.app.presentation.components.PediatricButton
import com.playit.app.presentation.components.bounceClick
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary

@Composable
fun SayItScreen(
    viewModel: SayItViewModel,
    onNext: (String) -> Unit,
    onBack: () -> Unit
) {
    val phoneme by viewModel.phoneme.collectAsState()
    val state by viewModel.state.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
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
                horizontalArrangement = Arrangement.SpaceBetween,
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

                Text(
                    text = "Say It — Letter $targetLetter",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                // 5-Heart Status Indicator Bar
                HeartBar(currentHearts = hearts, maxHearts = 5)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mascot Prompt
            MascotBubble(
                message = when (state) {
                    is SayItState.Listening -> "Listening closely to your voice... Speak clearly!"
                    is SayItState.Correct -> "Awesome pronunciation! You got it right! 🎉"
                    is SayItState.Incorrect -> "Good try! Let's listen again and try one more time."
                    else -> "Tap the big mic button and say the sound /${phoneme?.letter ?: "m"}/!"
                },
                mascotEmoji = when (state) {
                    is SayItState.Listening -> "👂"
                    is SayItState.Correct -> "🌟"
                    is SayItState.Incorrect -> "🦜"
                    else -> "🦜"
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Microphone Prompt Visual Card
            Surface(
                modifier = Modifier
                    .size(220.dp)
                    .shadow(10.dp, RoundedCornerShape(36.dp), spotColor = LearningBlue),
                shape = RoundedCornerShape(36.dp),
                color = when (state) {
                    is SayItState.Correct -> GrowthGreen.copy(alpha = 0.25f)
                    is SayItState.Incorrect -> GentleCorrectionOrange.copy(alpha = 0.25f)
                    else -> CreamWhite
                },
                border = androidx.compose.foundation.BorderStroke(3.dp, SoftSky)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = targetLetter,
                            fontSize = 84.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LearningBlue
                        )
                        Text(
                            text = when (state) {
                                is SayItState.Listening -> "Listening... 🎙️"
                                is SayItState.Correct -> "Correct! 🌟"
                                is SayItState.Incorrect -> "Try Again 💪"
                                else -> "Ready to Record"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Big 3D Microphone Button
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .breathingPulse(enabled = state is SayItState.Listening)
                    .bounceClick(onClick = {
                        if (state is SayItState.Listening) {
                            viewModel.stopListening()
                        } else {
                            viewModel.startListening()
                        }
                    })
                    .shadow(12.dp, CircleShape, spotColor = LearningBlue),
                shape = CircleShape,
                color = if (state is SayItState.Listening) GentleCorrectionOrange else LearningBlue,
                border = androidx.compose.foundation.BorderStroke(3.dp, CreamWhite)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (state is SayItState.Listening) "⏹️" else "🎙️",
                        fontSize = 44.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fallback shortcut button
            Surface(
                modifier = Modifier.bounceClick { viewModel.simulateCorrectForTesting() },
                shape = RoundedCornerShape(12.dp),
                color = CreamWhite.copy(alpha = 0.8f)
            ) {
                Text(
                    text = "Tap here if mic unavailable",
                    fontSize = 13.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue CTA
            PediatricButton(
                text = "Next: Find It 🔍",
                onClick = { onNext(phoneme?.id?.toString() ?: "1") },
                backgroundColor = GrowthGreen,
                enabled = state is SayItState.Correct,
                fontSize = 22,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

