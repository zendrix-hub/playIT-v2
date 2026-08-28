package com.playit.app.presentation.lettercomplete

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.playit.app.presentation.theme.Cloud
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
    val phoneme by viewModel.phoneme.collectAsState()
    val starsEarned by viewModel.starsEarned.collectAsState()
    val letter = phoneme?.letter?.uppercase() ?: "M"

    var isPlaying by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(Ube, UbeDark)))
            .padding(24.dp)
    ) {
        CelebrationOverlay(
            type = CelebrationType.CONFETTI,
            isPlaying = isPlaying,
            onFinished = { isPlaying = false },
            colors = listOf(Mango, Guava, Leaf, Cloud),
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            DockedMascotWithBubble(
                message = "Nakatapos ka sa Titik $letter! Napakahusay na pagsasanay sa tunog! • You mastered Letter $letter! Amazing sound practice!",
                mascotState = MascotState.CELEBRATING
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "TITIK $letter • LETTER $letter",
                fontFamily = LexendFontFamily,
                color = Cloud.copy(alpha = 0.85f),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tapos Na! • Complete!",
                fontFamily = LexendFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Cloud
            )

            Spacer(modifier = Modifier.height(16.dp))

            StarDisplay(earnedStars = starsEarned, maxStars = 3, starSize = 56.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .background(color = Cloud.copy(alpha = 0.16f), shape = RoundedCornerShape(14.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "$starsEarned ${if (starsEarned == 1) "Star" else "Stars"} • Nakuha!",
                    fontFamily = LexendFontFamily,
                    fontSize = 24.sp,
                    color = Cloud,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(bottom = 12.dp)) {
                GummyContainer(
                    onClick = onReturnToMap,
                    faceColor = Cloud,
                    shadowColor = CloudShadow,
                    shape = RoundedCornerShape(18.dp),
                    strokeWidth = 3.dp,
                    strokeColor = DarkBrownOutline,
                    depthHeight = 5.dp,
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Bumalik sa Mapa • Continue to Map",
                            fontFamily = LexendFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = UbeDark
                        )
                    }
                }
            }
        }
    }
}
