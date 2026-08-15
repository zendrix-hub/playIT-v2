package com.playit.app.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.FriendlyPurple
import com.playit.app.presentation.theme.TextPrimary
import com.playit.app.presentation.theme.TextSecondary

/**
 * Blend It word scene card loading blendword_<lowercase_word>.png assets from assets/images/pictures/.
 * Features a bouncy success animation on the picture image when the user correctly blends the word,
 * and click-to-replay target word audio.
 */
@Composable
fun BlendItCard(
    word: String,
    modifier: Modifier = Modifier,
    isCorrect: Boolean = false,
    onReplayAudio: () -> Unit = {}
) {
    val cleanWord = word.lowercase()
    val assetPath = "images/pictures/blendword_$cleanWord.png"

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardPressScale"
    )

    val imageBounceScale by animateFloatAsState(
        targetValue = if (isCorrect) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "imageBounceScale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(165.dp)
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            }
            .shadow(8.dp, RoundedCornerShape(28.dp), spotColor = FriendlyPurple)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onReplayAudio
            ),
        shape = RoundedCornerShape(28.dp),
        color = CreamWhite,
        border = BorderStroke(3.dp, TextPrimary)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                Image(
                    painter = rememberAssetPainter(assetPath),
                    contentDescription = "Blend word illustration: $cleanWord",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(92.dp)
                        .graphicsLayer {
                            scaleX = imageBounceScale
                            scaleY = imageBounceScale
                        }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap picture to hear word! 🔊",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
            }
        }
    }
}

