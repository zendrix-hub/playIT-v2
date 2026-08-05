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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text(text = "⬅️", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = "Hear It — Letter $targetLetter",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Big Letter Sound Card
            Card(
                modifier = Modifier
                    .size(240.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = CreamWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Play Sound Replay Button
            Button(
                onClick = { viewModel.playPhonemeSound() },
                modifier = Modifier
                    .size(80.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = LearningBlue)
            ) {
                Text(text = if (isPlaying) "🔊" else "▶️", fontSize = 32.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue to Say It CTA
            Button(
                onClick = { onNext(phoneme?.id?.toString() ?: "1") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GrowthGreen)
            ) {
                Text(
                    text = "Next: Say It 🎤",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CreamWhite
                )
            }
        }
    }
}
