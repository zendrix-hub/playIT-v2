package com.playit.app.presentation.lettercomplete

import com.playit.app.presentation.components.LessonScaffold
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.playit.app.presentation.components.breathingPulse
import com.playit.app.presentation.components.resetsIdle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.CelebrationOverlay
import com.playit.app.presentation.components.CelebrationType
import com.playit.app.presentation.components.DockedMascotWithBubble
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.StarDisplay
import com.playit.app.presentation.theme.*
import com.playit.app.presentation.theme.CloudShadow
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Guava
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.Ube
import com.playit.app.presentation.theme.UbeDark

@Composable
fun LetterCompleteScreen(
    viewModel: LetterCompleteViewModel,
    onReturnToMap: () -> Unit
) {
    val phoneme by viewModel.phoneme.collectAsStateWithLifecycle()
    val starsEarned by viewModel.starsEarned.collectAsStateWithLifecycle()
    val nextHighlighted by viewModel.nextHighlighted.collectAsStateWithLifecycle()
    val letter = phoneme?.letter?.uppercase() ?: "M"

    var isPlaying by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        viewModel.onScreenVisible()
        onDispose {
            viewModel.onScreenHidden()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .resetsIdle { viewModel.onUserInteraction() }
            .background(brush = Brush.verticalGradient(colors = listOf(Ube, UbeDark)))
    ) {
        CelebrationOverlay(
            type = CelebrationType.CONFETTI,
            isPlaying = isPlaying,
            onFinished = { isPlaying = false },
            colors = listOf(Mango, Guava, Leaf, Cloud),
            modifier = Modifier.fillMaxSize()
        )

        LessonScaffold(
            topBar = {},
            header = {
                DockedMascotWithBubble(
                    message = "You mastered Letter $letter! Amazing sound practice!",
                    mascotState = MascotState.CELEBRATING,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            },
            bottomBar = {
                GummyContainer(
                    onClick = onReturnToMap,
                    faceColor = com.playit.app.presentation.theme.SunnyGold,
                    shadowColor = com.playit.app.presentation.theme.SunnyGoldShadow,
                    shape = com.playit.app.presentation.theme.ButtonShape,
                    strokeWidth = 2.5.dp,
                    strokeColor = com.playit.app.presentation.theme.ModernBorder,
                    depthHeight = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp)
                        .testTag("complete_continue")
                        .breathingPulse(enabled = nextHighlighted)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Continue to Map",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                            maxLines = 1,
                            color = com.playit.app.presentation.theme.TextMidnight
                        )
                    }
                }
            }
        ) {
            // Title pair stays together; the scaffold spaces the groups evenly.
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LETTER $letter",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                    color = Cloud.copy(alpha = 0.85f),
                    maxLines = 1
                )
                Text(
                    text = "Complete!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Cloud,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }

            StarDisplay(earnedStars = starsEarned, maxStars = 3, starSize = 56.dp)

            Box(
                modifier = Modifier
                    .background(color = SunnyGold.copy(alpha = 0.22f), shape = PillShape)
                    .border(1.5.dp, SunnyGold.copy(alpha = 0.5f), PillShape)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "$starsEarned ${if (starsEarned == 1) "Star" else "Stars"} Earned!",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Cloud,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}
