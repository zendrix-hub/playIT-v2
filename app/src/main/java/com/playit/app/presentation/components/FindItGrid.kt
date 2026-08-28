package com.playit.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.model.Phoneme
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.CreamWhiteShadow
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.LexendFontFamily

/**
 * Grid choice card component rendering picture_<lowercase_word>.png assets from assets/images/pictures/.
 */
@Composable
fun FindItCard(
    phoneme: Phoneme,
    borderColor: Color,
    faceColor: Color = CreamWhite,
    index: Int,
    isCorrect: Boolean = false,
    isIncorrect: Boolean = false,
    onClick: () -> Unit
) {
    val word = phoneme.exampleWord.lowercase()
    val assetPath = "images/pictures/picture_$word.png"
    val rotationAngle = remember(index) { ((index * 37) % 5 - 2).toFloat() }

    GummyContainer(
        onClick = onClick,
        faceColor = faceColor,
        shadowColor = CreamWhiteShadow,
        shape = RoundedCornerShape(22.dp),
        strokeWidth = 2.5.dp,
        strokeColor = borderColor,
        depthHeight = 5.dp,
        isSquashed = isCorrect,
        modifier = Modifier
            .fillMaxWidth()
            .height(146.dp)
            .graphicsLayer { rotationZ = rotationAngle }
            .shake(trigger = isIncorrect)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.foundation.layout.Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(72.dp)
                ) {
                    // Soft circular ambient backing
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = com.playit.app.presentation.theme.Sky.copy(alpha = 0.6f),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )

                    GummyMotionAsset(
                        assetPath = assetPath,
                        contentDescription = phoneme.exampleWord,
                        isIdleFloating = true,
                        floatDistance = 3.dp,
                        celebrateTrigger = isCorrect,
                        modifier = Modifier.size(64.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = phoneme.exampleWord,
                    fontFamily = LexendFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Ink
                )
            }
        }
    }
}
