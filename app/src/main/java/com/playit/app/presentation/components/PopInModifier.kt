package com.playit.app.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.playit.app.presentation.theme.LocalReducedMotion
import kotlinx.coroutines.delay

/**
 * Bouncy spring "pop into place" entrance: scales 0 -> 1 with a MediumBouncy spring the
 * first time `visible` becomes true, optionally staggered by `delayMillis` so a group of
 * items (stars, badges) cascades in one after another instead of appearing all at once.
 *
 * Companion to Modifier.shake — same "composed extension in the Gummy motion family"
 * shape, this time for reward/celebration moments that should feel like they're arriving
 * with momentum rather than just fading in.
 *
 * Respects LocalReducedMotion: renders at final scale immediately, no delay, no spring.
 */
fun Modifier.popIn(
    visible: Boolean = true,
    delayMillis: Int = 0
): Modifier = composed {
    val isReducedMotion = LocalReducedMotion.current
    var hasAppeared by remember { mutableStateOf(isReducedMotion) }

    LaunchedEffect(visible) {
        if (visible && !isReducedMotion) {
            if (delayMillis > 0) delay(delayMillis.toLong())
            hasAppeared = true
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (hasAppeared) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "popIn"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
