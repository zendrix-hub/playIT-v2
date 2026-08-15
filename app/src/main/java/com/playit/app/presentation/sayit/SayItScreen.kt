package com.playit.app.presentation.sayit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.DockedMascotWithBubble
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.GummyIconButton
import com.playit.app.presentation.components.HeartBar
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.PediatricButton
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.components.shake
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.CreamWhiteShadow
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.GentleCorrectionOrangeShadow
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.GrowthGreenShadow
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.LearningBlueShadow
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary
import com.playit.app.presentation.theme.TextSecondary

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
    val isListening = state is SayItState.Listening

    val toggleListening = {
        if (isListening) viewModel.stopListening() else viewModel.startListening()
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

            // Docked Mascot Prompt Guidance — synced 1:1 with the voice state machine
            DockedMascotWithBubble(
                message = when (state) {
                    is SayItState.Listening -> "Listening closely to your voice... Speak clearly!"
                    is SayItState.Correct -> "Awesome pronunciation! You got it right! 🎉"
                    is SayItState.Incorrect -> "Good try! Let's listen again and try one more time."
                    else -> "Tap the big mic button and say the sound /${phoneme?.letter ?: "m"}/!"
                },
                mascotState = when (state) {
                    is SayItState.Listening -> MascotState.LISTENING
                    is SayItState.Correct -> MascotState.CELEBRATING
                    is SayItState.Incorrect -> MascotState.ENCOURAGING
                    else -> MascotState.POINTING
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // 3D Gummy status card (28dp corners — this is a Learning Card, not a letter
            // tile/map node, so it stays rounded rather than circular per §3). Solid,
            // state-paired face/shadow tokens (rather than a translucent tint over
            // CreamWhite) so the depth-band 3D effect reads correctly in every state.
            // Text color per state follows the existing token contrast pairings already
            // documented in Color.kt: GrowthGreen/LearningBlue -> cream text,
            // GentleCorrectionOrange -> TextPrimary text.
            val (cardFace, cardShadow, cardTextColor) = when (state) {
                is SayItState.Correct -> Triple(GrowthGreen, GrowthGreenShadow, CreamWhite)
                is SayItState.Incorrect -> Triple(GentleCorrectionOrange, GentleCorrectionOrangeShadow, TextPrimary)
                else -> Triple(CreamWhite, CreamWhiteShadow, LearningBlue)
            }
            val cardStatusColor = if (state is SayItState.Correct || state is SayItState.Incorrect) cardTextColor else TextPrimary

            GummyContainer(
                onClick = toggleListening,
                faceColor = cardFace,
                shadowColor = cardShadow,
                shape = RoundedCornerShape(28.dp),
                strokeWidth = 3.dp,
                depthHeight = 6.dp,
                modifier = Modifier
                    .size(220.dp)
                    .graphicsLayer { rotationZ = 1.5f }
                    .shake(trigger = state is SayItState.Incorrect)
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
                            color = cardTextColor
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
                            color = cardStatusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Big 3D Gummy Microphone Button — GummyIconButton (GummyButton's circular
            // sibling; see chat note on why GummyButton itself can't do this shape),
            // 112dp face well above the 64dp touch-target floor, 6dp depth band,
            // DarkBrownOutline stroke, native press-into-depth spring on tap.
            GummyIconButton(
                icon = if (isListening) "⏹️" else "🎙️",
                onClick = toggleListening,
                backgroundColor = if (isListening) GentleCorrectionOrange else LearningBlue,
                shadowColor = if (isListening) GentleCorrectionOrangeShadow else LearningBlueShadow,
                size = 112.dp,
                fontSize = 48,
                modifier = Modifier.breathingPulse(enabled = isListening)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // QA/testing bypass (simulateCorrectForTesting() skips real speech
            // recognition entirely) — debug-only now, per your call: available for local
            // emulator runs, compiled out of the production release build entirely.
            Text(
                text = "Tap here if mic unavailable",
                fontSize = 13.sp,
                color = TextSecondary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable { viewModel.simulateCorrectForTesting() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Continue CTA — already inherits the corrected DarkBrownOutline via the
            // GummyButton fix, no change needed here beyond that shared-component fix.
            PediatricButton(
                text = "Next: Find It 🔍",
                onClick = { onNext(phoneme?.id?.toString() ?: "1") },
                backgroundColor = GrowthGreen,
                enabled = state is SayItState.Correct,
                fontSize = 22,
                isSquashed = state is SayItState.Correct,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}