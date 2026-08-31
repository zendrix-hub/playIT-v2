package com.playit.app.presentation.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.domain.manager.FindItPictureItem
import com.playit.app.domain.model.Phoneme
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.CreamWhiteShadow
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Sky

@Composable
fun FindItCard(
    item: FindItPictureItem,
    borderColor: Color,
    faceColor: Color = CreamWhite,
    index: Int,
    isCorrect: Boolean = false,
    isIncorrect: Boolean = false,
    onClick: () -> Unit
) {
    val rotationAngle = remember(index) { ((index * 37) % 5 - 2).toFloat() }

    GummyContainer(
        onClick = onClick,
        faceColor = faceColor,
        shadowColor = CreamWhiteShadow,
        shape = RoundedCornerShape(22.dp),
        strokeWidth = 2.5.dp,
        strokeColor = borderColor,
        depthHeight = 4.dp,
        isSquashed = isCorrect,
        modifier = Modifier
            .fillMaxWidth()
            .height(124.dp)
            .graphicsLayer { rotationZ = rotationAngle }
            .shake(trigger = isIncorrect)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(62.dp)
                ) {
                    // Ambient backing circle
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                color = Sky.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                    )

                    GummyMotionAsset(
                        assetPath = item.imagePath,
                        contentDescription = item.word,
                        isIdleFloating = !isCorrect,
                        floatDistance = 3.dp,
                        celebrateTrigger = isCorrect,
                        modifier = Modifier.size(52.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.word,
                    fontFamily = LexendFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
            }

            // Green Checkmark Badge when Found
            if (isCorrect) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(26.dp)
                        .background(Leaf, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Found",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Legacy overload for backward compatibility with existing tests
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
    val item = FindItPictureItem(
        id = phoneme.id.toString(),
        phonemeLetter = phoneme.letter,
        word = phoneme.exampleWord,
        imagePath = assetPath,
        isCorrect = isCorrect
    )
    FindItCard(
        item = item,
        borderColor = borderColor,
        faceColor = faceColor,
        index = index,
        isCorrect = isCorrect,
        isIncorrect = isIncorrect,
        onClick = onClick
    )
}
