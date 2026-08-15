package com.playit.app.presentation.hearit

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.DockedMascotWithBubble
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.GrowthGreenShadow
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.LearningBlueShadow
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

    // Seeded deterministic static card rotation (-2° to +2°)
    val cardRotation = remember(phoneme?.id) {
        val seed = phoneme?.id ?: 0
        ((seed * 37) % 5 - 2).toFloat()
    }

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

            // Docked Mascot Prompt Guidance
            DockedMascotWithBubble(
                message = "Tap the big speaker button to listen to the letter sound!",
                mascotState = MascotState.LISTENING
            )

            Spacer(modifier = Modifier.weight(1f))

            // 3D Gummy Animated Letter Card overlaying illustration PNG asset.
            // Tapping the card itself now also replays the phoneme (redundant with
            // the speaker button below by design — mirrors the whole-card-is-tappable
            // pattern already used on BlendItCard).
            com.playit.app.presentation.components.LetterCard(
                letter = targetLetter,
                soundText = "Sound: /${phoneme?.letter ?: "m"}/",
                cardRotation = cardRotation,
                wordOverride = phoneme?.exampleWord,
                onTapReplay = { viewModel.playPhonemeSound() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3D Gummy Play Sound Replay Button
            GummyContainer(
                onClick = { viewModel.playPhonemeSound() },
                faceColor = LearningBlue,
                shadowColor = LearningBlueShadow,
                shape = CircleShape,
                strokeWidth = 3.dp,
                strokeColor = DarkBrownOutline,
                modifier = Modifier
                    .size(92.dp)
                    .breathingPulse(enabled = isPlaying)
            ) {
                Text(text = if (isPlaying) "🔊" else "▶️", fontSize = 42.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue to Say It CTA — squishy GummyButton per session design system
            GummyButton(
                text = "Next: Say It 🎤",
                onClick = { onNext(phoneme?.id?.toString() ?: "1") },
                backgroundColor = GrowthGreen,
                shadowColor = GrowthGreenShadow,
                fontSize = 22,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
