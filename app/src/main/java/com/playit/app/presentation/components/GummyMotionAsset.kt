package com.playit.app.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Applies a smooth continuous idle floating and breathing transform to the component.
 * Spec: translateY ±floatDistance (default 6dp), scale 1.0 -> 1.04, 2000ms duration.
 */
fun Modifier.idleFloating(
    enabled: Boolean = true,
    floatDistance: Dp = 6.dp,
    durationMillis: Int = 2000
): Modifier = composed {
    if (!enabled) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "IdleFloatTransition")

    val translateY by infiniteTransition.animateFloat(
        initialValue = -floatDistance.value,
        targetValue = floatDistance.value,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleTranslateY"
    )

    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleBreatheScale"
    )

    this.graphicsLayer {
        translationY = translateY * density
        scaleX = breatheScale
        scaleY = breatheScale
    }
}

/**
 * Applies a celebratory spring pop (1.15x scale) and playful rotation wiggle (±6°)
 * whenever [trigger] becomes true.
 */
fun Modifier.celebrationWiggle(
    trigger: Boolean,
    maxRotationDegrees: Float = 6f,
    onFinished: () -> Unit = {}
): Modifier = composed {
    val scale = remember { Animatable(1.0f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            launch {
                scale.animateTo(
                    targetValue = 1.15f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                scale.animateTo(
                    targetValue = 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                rotation.animateTo(maxRotationDegrees, tween(75))
                rotation.animateTo(-maxRotationDegrees, tween(75))
                rotation.animateTo(maxRotationDegrees * 0.6f, tween(60))
                rotation.animateTo(-maxRotationDegrees * 0.6f, tween(60))
                rotation.animateTo(0f, tween(50))
                onFinished()
            }
        }
    }

    this.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
        rotationZ = rotation.value
    }
}

/**
 * Interactive tactile touch compression: squishes slightly (-4% scale) and compresses down (+3dp) on press.
 */
fun Modifier.interactiveSquish(
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val squishScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.94f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "squishScale"
    )

    val compressY by animateFloatAsState(
        targetValue = if (isPressed && enabled) 3f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "compressY"
    )

    this
        .graphicsLayer {
            scaleX = squishScale
            scaleY = squishScale
            translationY = compressY * density
        }
        .then(
            if (onClick != null && enabled) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
            } else Modifier
        )
}

/**
 * GummyMotionAsset — High-fidelity animated raster image wrapper following .agents/rules/asset_pipeline.md.
 *
 * Renders transparent PNG assets non-destructively with declarative container-level motion:
 * - Continuous idle floating and breathing
 * - Celebratory spring scale and wiggle rotation
 * - Interactive squish response on touch
 */
@Composable
fun GummyMotionAsset(
    assetPath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    isIdleFloating: Boolean = true,
    floatDistance: Dp = 6.dp,
    celebrateTrigger: Boolean = false,
    contentScale: ContentScale = ContentScale.Fit,
    onClick: (() -> Unit)? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .idleFloating(enabled = isIdleFloating, floatDistance = floatDistance)
            .celebrationWiggle(trigger = celebrateTrigger)
            .interactiveSquish(enabled = onClick != null, onClick = onClick)
    ) {
        Image(
            painter = rememberAssetPainter(assetPath),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.matchParentSize()
        )
    }
}
