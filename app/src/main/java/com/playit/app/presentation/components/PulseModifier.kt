package com.playit.app.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.LocalReducedMotion

fun Modifier.breathingPulse(
    enabled: Boolean = true
): Modifier = composed {
    val isReducedMotion = LocalReducedMotion.current
    if (!enabled) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isReducedMotion) 1.02f else 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

fun Modifier.idleBounce(
    enabled: Boolean = true
): Modifier = composed {
    val isReducedMotion = LocalReducedMotion.current
    if (!enabled || isReducedMotion) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "idleBounceTransition")
    val translateY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleBounceY"
    )

    this.graphicsLayer {
        translationY = translateY.dp.toPx()
    }
}

