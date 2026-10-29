package com.playit.app.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import com.playit.app.presentation.theme.*
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

/** Length of the correct-answer squash. */
const val SQUASH_MS = 320

/** Press: shrink to 92 % while held (03 §4 tap feedback) in PRESS_IN_MS; release: back in PRESS_OUT_MS with an overshoot. */
const val PRESS_SCALE = 0.92f
const val PRESS_IN_MS = 100
const val PRESS_OUT_MS = 250

/**
 * Android's OvershootInterpolator curve: 0 at 0 and 1 at 1, passing beyond 1 on the way and settling back,
 * which makes a release feel elastic. Tension 3 overshoots by 25 % of the move: from 92 % a release peaks at 102 %.
 */
fun overshoot(t: Float, tension: Float = 3f): Float {
    val x = t.coerceIn(0f, 1f) - 1f
    return x * x * ((tension + 1f) * x + tension) + 1f
}

private val OvershootEasing = Easing { overshoot(it) }

/**
 * The correct-answer squash at [progress] 0..1 as (scaleX, scaleY): out to 108 % wide and 94 % tall and back,
 * so the box (and any picture in it) has its own shape at both ends.
 */
fun squashAt(progress: Float): Pair<Float, Float> {
    val a = sin(PI.toFloat() * progress.coerceIn(0f, 1f))
    return (1f + 0.08f * a) to (1f - 0.06f * a)
}

/**
 * Reusable Duolingo ABC-inspired Gummy Box with 3D depth-band bottom shadow,
 * 3dp DarkBrownOutline outline, and press-into-depth motion on tap.
 */
@Composable
fun GummyContainer(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    faceColor: Color = LearningBlue,
    shadowColor: Color = faceColor.deriveShadow(),
    shape: Shape = RoundedCornerShape(32.dp),
    strokeWidth: Dp = 3.dp,
    strokeColor: Color = com.playit.app.presentation.theme.DarkBrownOutline,
    depthHeight: Dp = 6.dp,
    isSquashed: Boolean = false,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isReducedMotion = LocalReducedMotion.current

    val effectiveFace = if (enabled) faceColor else DisabledColor
    val effectiveShadow = if (enabled) shadowColor else DisabledColorShadow

    // Press translateY translation (0dp to depthHeight - 1dp)
    val pressOffsetY by animateFloatAsState(
        targetValue = if (isPressed && enabled && !isReducedMotion && onClick != null) {
            (depthHeight.value - 1f).coerceAtLeast(0f)
        } else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "gummyOffsetY"
    )

    // Correct answer: one quick squash when isSquashed turns true, then back to the box's own shape. It used to
    // hold 108 % x 94 % for as long as isSquashed stayed true, which left pictures stretched (user, 2026-10-29).
    val squash = remember { Animatable(1f) }
    LaunchedEffect(isSquashed) {
        if (isSquashed && !isReducedMotion) {
            squash.snapTo(0f)
            squash.animateTo(1f, tween(SQUASH_MS, easing = LinearEasing))
        }
    }

    // Press bounce (user, 2026-10-29: bouncy, elastic buttons): the box shrinks to 92 % while held and springs back
    // past 100 % on release. It draws in a layer, so the touch target never changes; none under reduced motion.
    val pressScale = remember { Animatable(1f) }
    val bouncy = enabled && onClick != null && !isReducedMotion
    LaunchedEffect(isPressed, bouncy) {
        when {
            !bouncy -> pressScale.snapTo(1f)
            isPressed -> pressScale.animateTo(PRESS_SCALE, tween(PRESS_IN_MS, easing = FastOutSlowInEasing))
            pressScale.value != 1f -> pressScale.animateTo(1f, tween(PRESS_OUT_MS, easing = OvershootEasing))
        }
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = {
                onClick()
            }
        )
    } else Modifier

    // Custom layout: the container is as big as its content's natural size, clamped to the caller's
    // limits (size/heightIn/fillMaxWidth). Big content grows it instead of spilling out, and content
    // that uses fillMaxSize still fills only the container (Claude dry run 2026-10-06).
    Layout(
        modifier = modifier
            .graphicsLayer {
                val (sx, sy) = squashAt(squash.value)
                scaleX = sx * pressScale.value
                scaleY = sy * pressScale.value
            }
            .then(clickableModifier),
        content = {
            // Bottom depth band layer (shadow color)
            Box(
                modifier = Modifier
                    .offset(y = depthHeight)
                    .background(effectiveShadow, shape)
                    .border(strokeWidth, strokeColor, shape)
            )
            // Top face layer (face color + content) translated down on press
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, pressOffsetY.dp.roundToPx()) }
                    .background(effectiveFace, shape)
                    .border(strokeWidth, strokeColor, shape),
                contentAlignment = Alignment.Center
            ) {
                // Modern subtle top gloss highlight sheen
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(shape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.22f),
                                    Color.White.copy(alpha = 0.04f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                content()
            }
        }
    ) { measurables, constraints ->
        val band = measurables[0]
        val face = measurables[1]
        val width = if (constraints.hasFixedWidth) constraints.maxWidth
            else face.maxIntrinsicWidth(constraints.maxHeight).coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = if (constraints.hasFixedHeight) constraints.maxHeight
            else face.minIntrinsicHeight(width).coerceIn(constraints.minHeight, constraints.maxHeight)
        val exact = Constraints.fixed(width, height)
        val bandPlaceable = band.measure(exact)
        val facePlaceable = face.measure(exact)
        layout(width, height) {
            bandPlaceable.place(0, 0)
            facePlaceable.place(0, 0)
        }
    }
}

/**
 * High-fidelity Duolingo ABC-inspired Gummy Button (64dp touch target floor,
 * 32dp corner radius near-pill, 3dp DarkBrownOutline outline, press-into-depth animation).
 *
 * strokeColor is intentionally not exposed here — it inherits GummyContainer's
 * DarkBrownOutline default so every button in the app stays on the single global
 * outline standard automatically (previously this hardcoded TextPrimary, which
 * silently diverged from that standard).
 */
@Composable
fun GummyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LearningBlue,
    shadowColor: Color = backgroundColor.deriveShadow(),
    contentColor: Color = CreamWhite,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    fontSize: Int = 20,
    isSquashed: Boolean = false
) {
    GummyContainer(
        onClick = onClick,
        enabled = enabled,
        faceColor = backgroundColor,
        shadowColor = shadowColor,
        shape = RoundedCornerShape(32.dp),
        strokeWidth = 3.dp,
        isSquashed = isSquashed,
        modifier = modifier
            .defaultMinSize(minHeight = 58.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontFamily = com.playit.app.presentation.theme.LexendFontFamily,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (enabled) contentColor else TextPrimary.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Icon-only circular sibling of GummyButton — same tactile contract (GummyContainer,
 * DarkBrownOutline stroke, depth band, press-into-depth spring) but shaped for a single
 * glyph or vector icon instead of a text label. Enforces >=64dp touch-target floor.
 */
@Composable
fun GummyIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    backgroundColor: Color = LearningBlue,
    shadowColor: Color = backgroundColor.deriveShadow(),
    tint: Color = CreamWhite,
    size: Dp = 64.dp,
    iconSize: Dp = 32.dp,
    depthHeight: Dp = 6.dp,
    enabled: Boolean = true,
    isSquashed: Boolean = false
) {
    GummyContainer(
        onClick = onClick,
        enabled = enabled,
        faceColor = backgroundColor,
        shadowColor = shadowColor,
        shape = CircleShape,
        strokeWidth = 3.dp,
        depthHeight = depthHeight,
        isSquashed = isSquashed,
        modifier = modifier
            .defaultMinSize(minWidth = 64.dp, minHeight = 64.dp)
            .size(size)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) tint else TextPrimary.copy(alpha = 0.4f),
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun GummyIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LearningBlue,
    shadowColor: Color = backgroundColor.deriveShadow(),
    size: Dp = 64.dp,
    depthHeight: Dp = 6.dp,
    enabled: Boolean = true,
    isSquashed: Boolean = false,
    content: @Composable () -> Unit
) {
    GummyContainer(
        onClick = onClick,
        enabled = enabled,
        faceColor = backgroundColor,
        shadowColor = shadowColor,
        shape = CircleShape,
        strokeWidth = 3.dp,
        depthHeight = depthHeight,
        isSquashed = isSquashed,
        modifier = modifier
            .defaultMinSize(minWidth = 64.dp, minHeight = 64.dp)
            .size(size),
        content = content
    )
}

@Deprecated("Migrate to ImageVector or Composable content overload per Zero-Emoji Policy")
@Composable
fun GummyIconButton(
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LearningBlue,
    shadowColor: Color = backgroundColor.deriveShadow(),
    size: Dp = 96.dp,
    fontSize: Int = 40,
    depthHeight: Dp = 6.dp,
    enabled: Boolean = true,
    isSquashed: Boolean = false
) {
    GummyContainer(
        onClick = onClick,
        enabled = enabled,
        faceColor = backgroundColor,
        shadowColor = shadowColor,
        shape = CircleShape,
        strokeWidth = 3.dp,
        depthHeight = depthHeight,
        isSquashed = isSquashed,
        modifier = modifier
            .defaultMinSize(minWidth = 64.dp, minHeight = 64.dp)
            .size(size)
    ) {
        Text(
            text = icon,
            fontSize = fontSize.sp
        )
    }
}


/**
 * Static (non-pressable) sibling of GummyContainer — same embossed depth-band look
 * (bottom shadow layer, DarkBrownOutline stroke) but no clickable/interactionSource and
 * no press-into-depth motion, because there's nothing to press. Use this for gummy-styled
 * surfaces that are purely informational — mascot bubbles, celebration cards, badges —
 * rather than bolting a fake onClick onto GummyContainer just to satisfy its required
 * (and rightly so, for a genuinely pressable component) onClick parameter.
 */
@Composable
fun GummyStaticContainer(
    modifier: Modifier = Modifier,
    faceColor: Color = CreamWhite,
    shadowColor: Color = CreamWhiteShadow,
    shape: Shape = RoundedCornerShape(28.dp),
    strokeWidth: Dp = 3.dp,
    strokeColor: Color = DarkBrownOutline,
    depthHeight: Dp = 6.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Bottom depth band layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = depthHeight)
                .background(shadowColor, shape)
                .border(strokeWidth, strokeColor, shape)
        )

        // Top face layer + content
        Box(
            modifier = Modifier
                .background(faceColor, shape)
                .border(strokeWidth, strokeColor, shape),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}