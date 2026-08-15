package com.playit.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import com.playit.app.presentation.theme.LearningBlueShadow
import com.playit.app.presentation.theme.TextPrimary

/**
 * Grid choice card component rendering picture_<lowercase_word>.png assets from assets/images/pictures/.
 */
@Composable
fun FindItCard(
    phoneme: Phoneme,
    borderColor: Color,
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
        faceColor = CreamWhite,
        shadowColor = LearningBlueShadow.copy(alpha = 0.3f),
        shape = RoundedCornerShape(28.dp),
        strokeWidth = 3.dp,
        strokeColor = borderColor,
        isSquashed = isCorrect,
        modifier = Modifier
            .fillMaxWidth()
            .height(146.dp)
            .graphicsLayer { rotationZ = rotationAngle }
            .shake(trigger = isIncorrect)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = rememberAssetPainter(assetPath),
                contentDescription = phoneme.exampleWord,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(68.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = phoneme.exampleWord,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
        }
    }
}
