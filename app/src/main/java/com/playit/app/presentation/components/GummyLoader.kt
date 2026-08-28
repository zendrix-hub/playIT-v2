package com.playit.app.presentation.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.TextPrimary
import kotlin.math.roundToInt

@Composable
fun GummyLoader(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    val isReducedMotion = LocalReducedMotion.current
    val colors = listOf(LearningBlue, GrowthGreen, GentleCorrectionOrange)
    
    val infiniteTransition = rememberInfiniteTransition(label = "gummy_loader")
    
    val animations = colors.mapIndexed { index, _ ->
        if (isReducedMotion) {
            infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 500),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(
                        offsetMillis = index * 200,
                        offsetType = StartOffsetType.Delay
                    )
                ),
                label = "alpha_$index"
            )
        } else {
            infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -24f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 300, easing = FastOutLinearInEasing),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(
                        offsetMillis = index * 150,
                        offsetType = StartOffsetType.Delay
                    )
                ),
                label = "translation_$index"
            )
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                colors.forEachIndexed { index, color ->
                    val alpha = if (isReducedMotion) animations[index].value else 1f
                    val yOffset = if (isReducedMotion) 0f else animations[index].value
                    
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(0, yOffset.roundToInt()) }
                            .size(20.dp)
                            .alpha(alpha)
                            .background(color, CircleShape)
                            .border(2.dp, DarkBrownOutline, CircleShape)
                    )
                }
            }
            
            if (message != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}
