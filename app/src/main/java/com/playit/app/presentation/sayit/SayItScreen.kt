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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .background(SoftSky)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text(text = "⬅️", fontSize = 24.sp)
                }
                Text(
                    text = "Say It — Letter $targetLetter",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                // Hearts indicator
                Text(
                    text = "❤️ ".repeat(hearts),
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Microphone Visual Card
            Card(
                modifier = Modifier.size(220.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (state) {
                        is SayItState.Correct -> GrowthGreen.copy(alpha = 0.2f)
                        is SayItState.Incorrect -> GentleCorrectionOrange.copy(alpha = 0.2f)
                        else -> CreamWhite
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = targetLetter,
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Bold,
                            color = LearningBlue
                        )
                        Text(
                            text = when (state) {
                                is SayItState.Listening -> "Listening... 🎙️"
                                is SayItState.Correct -> "Awesome! 🌟"
                                is SayItState.Incorrect -> "Good try! Let me listen again."
                                else -> "Tap Mic & Say /${phoneme?.letter ?: "m"}/"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Big Mic Button (64dp+ target)
            Button(
                onClick = {
                    if (state is SayItState.Listening) {
                        viewModel.stopListening()
                    } else {
                        viewModel.startListening()
                    }
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state is SayItState.Listening) GentleCorrectionOrange else LearningBlue
                )
            ) {
                Text(
                    text = if (state is SayItState.Listening) "⏹️" else "🎙️",
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick simulation button for testing fallback
            Button(
                onClick = { viewModel.simulateCorrectForTesting() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = null
            ) {
                Text(text = "(Tap here if mic unavailable)", fontSize = 14.sp, color = TextPrimary)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue CTA
            Button(
                onClick = { onNext(phoneme?.id?.toString() ?: "1") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                enabled = state is SayItState.Correct,
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GrowthGreen)
            ) {
                Text(
                    text = "Next: Find It 🔍",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CreamWhite
                )
            }
        }
    }
}
