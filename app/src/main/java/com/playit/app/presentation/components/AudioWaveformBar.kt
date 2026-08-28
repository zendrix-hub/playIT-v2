package com.playit.app.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.Guava

/**
 * Animated voice waveform visualizer matching playit-mockup.html.
 * Shows 5 bouncing guava bars when speech recognition is actively recording.
 */
@Composable
fun AudioWaveformBar(
    isRecording: Boolean,
    modifier: Modifier = Modifier,
    activeColor: Color = Guava
) {
    if (!isRecording) {
        Box(modifier = modifier.height(24.dp))
        return
    }

    val transition = rememberInfiniteTransition(label = "WaveformAnimation")
    val h1 by transition.animateFloat(
        initialValue = 6f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse),
        label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 12f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse),
        label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 8f,
        targetValue = 26f,
        animationSpec = infiniteRepeatable(tween(450), RepeatMode.Reverse),
        label = "h3"
    )
    val h4 by transition.animateFloat(
        initialValue = 14f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(320), RepeatMode.Reverse),
        label = "h4"
    )
    val h5 by transition.animateFloat(
        initialValue = 6f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
        label = "h5"
    )

    Row(
        modifier = modifier.height(26.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val heights = listOf(h1, h2, h3, h4, h5)
        for (h in heights) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(h.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(activeColor)
            )
        }
    }
}
