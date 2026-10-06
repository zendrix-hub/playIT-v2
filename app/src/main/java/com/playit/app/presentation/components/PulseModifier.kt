package com.playit.app.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.LocalReducedMotion

fun Modifier.breathingPulse(enabled: Boolean = true): Modifier = composed {
    if (!enabled || LocalReducedMotion.current) return@composed this
    val t = rememberInfiniteTransition(label = "pulseTransition")
    val scale by t.animateFloat(1.0f, 1.05f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "pulseScale")
    this.graphicsLayer { scaleX = scale; scaleY = scale }
}

fun Modifier.idleBounce(enabled: Boolean = true): Modifier = composed {
    if (!enabled || LocalReducedMotion.current) return@composed this
    val t = rememberInfiniteTransition(label = "idleBounceTransition")
    val y by t.animateFloat(0f, -6f, infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "idleBounceY")
    this.graphicsLayer { translationY = y.dp.toPx() }
}
