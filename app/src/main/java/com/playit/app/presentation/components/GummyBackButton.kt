package com.playit.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.CreamWhiteShadow
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.TextPrimary

/**
 * Pediatric Gummy Back Navigation Button — circular 3D container with DarkBrownOutline
 * stroke and a crisp vector arrow glyph (replaces raw emoji text).
 */
@Composable
fun GummyBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    iconColor: Color = TextPrimary,
    backgroundColor: Color = CreamWhite,
    shadowColor: Color = CreamWhiteShadow
) {
    GummyContainer(
        onClick = onClick,
        faceColor = backgroundColor,
        shadowColor = shadowColor,
        shape = CircleShape,
        strokeWidth = 3.dp,
        strokeColor = DarkBrownOutline,
        depthHeight = 4.dp,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )

            // Left horizontal stem
            drawLine(
                color = iconColor,
                start = Offset(w * 0.85f, h * 0.5f),
                end = Offset(w * 0.22f, h * 0.5f),
                strokeWidth = stroke.width,
                cap = stroke.cap
            )

            // Top chevron arm
            drawLine(
                color = iconColor,
                start = Offset(w * 0.52f, h * 0.22f),
                end = Offset(w * 0.22f, h * 0.5f),
                strokeWidth = stroke.width,
                cap = stroke.cap
            )

            // Bottom chevron arm
            drawLine(
                color = iconColor,
                start = Offset(w * 0.52f, h * 0.78f),
                end = Offset(w * 0.22f, h * 0.5f),
                strokeWidth = stroke.width,
                cap = stroke.cap
            )
        }
    }
}
