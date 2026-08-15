package com.playit.app.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.CreamWhiteShadow
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.DisabledColor
import com.playit.app.presentation.theme.DisabledColorShadow
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.LearningBlueShadow
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.TextPrimary
import kotlin.math.roundToInt

/**
 * Reusable Duolingo ABC-inspired Gummy Box with 3D depth-band bottom shadow,
 * 3dp DarkBrownOutline outline, and press-into-depth motion on tap.
 */
@Composable
fun GummyContainer(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    faceColor: Color = LearningBlue,
    shadowColor: Color = LearningBlueShadow,
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
        targetValue = if (isPressed && enabled && !isReducedMotion) 4f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "gummyOffsetY"
    )

    // Squash effect for correct answer
    val squashScaleX by animateFloatAsState(
        targetValue = if (isSquashed && !isReducedMotion) 1.08f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "gummySquashX"
    )
    val squashScaleY by animateFloatAsState(
        targetValue = if (isSquashed && !isReducedMotion) 0.94f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "gummySquashY"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = squashScaleX
                scaleY = squashScaleY
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // Bottom depth band layer (shadow color)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = depthHeight)
                .background(effectiveShadow, shape)
                .border(strokeWidth, strokeColor, shape)
        )

        // Top face layer (face color + content) translated down on press
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset { IntOffset(0, pressOffsetY.dp.roundToPx()) }
                .background(effectiveFace, shape)
                .border(strokeWidth, strokeColor, shape),
            contentAlignment = Alignment.Center
        ) {
            content()
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
    shadowColor: Color = LearningBlueShadow,
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
            .defaultMinSize(minHeight = 64.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
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
 * glyph instead of a text label. GummyButton itself can't serve this role: it hardcodes
 * a 32dp-rounded-rect shape and forces fillMaxWidth, which is wrong for a standalone
 * circular control like a record/mic button. Enforces the same >=64dp touch-target floor.
 */
@Composable
fun GummyIconButton(
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LearningBlue,
    shadowColor: Color = LearningBlueShadow,
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
    Box(modifier = modifier) {
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
                .matchParentSize()
                .background(faceColor, shape)
                .border(strokeWidth, strokeColor, shape),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}