package com.playit.app.presentation.map.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Lightweight Material 3 Bohol Biome Adventure Backdrop.
 * Provides a soothing, high-performance pedagogical gradient background with subtle
 * topographic contour curves across the 6 geographical units.
 * Eliminates GPU canvas overdraw and visual clutter so children can focus on the learning trail.
 */
@Composable
fun ChocolateHillsBackground(
    totalHeight: Dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    // 6-Biome continuous gradient aligning with each unit's thematic identity
    val backgroundBrush = Brush.verticalGradient(
        colorStops = arrayOf(
            0.00f to Color(0xFFBAE6FD), // Zone 1: Morning Sky Blue (Chocolate Hills)
            0.10f to Color(0xFFE0F2FE), // Zone 1: Sunlit Meadow
            0.17f to Color(0xFFD8F3DC), // Zone 2: Fresh Valley Green (Loboc River)
            0.28f to Color(0xFFCCFBF1), // Zone 2: River Jade Mist
            0.34f to Color(0xFFE0F2FE), // Zone 3: Panglao Shore Sky
            0.44f to Color(0xFFFEF3C7), // Zone 3: Coral Coast Sand
            0.50f to Color(0xFFDCFCE7), // Zone 4: Rainforest Canopy Mist (Tarsier Forest)
            0.62f to Color(0xFFBBF7D0), // Zone 4: Deep Jungle Emerald
            0.67f to Color(0xFFFFEDD5), // Zone 5: Golden Hour Amber (Mountain Summit)
            0.80f to Color(0xFFFED7AA), // Zone 5: Sunset Horizon
            0.84f to Color(0xFFFEF3C7), // Zone 6: Coral Stone Warm Cream (Baclayon Heritage)
            0.94f to Color(0xFFFDE68A), // Zone 6: Sunlit Cobblestone
            1.00f to Color(0xFFFEF9C3)  // Zone 6: Heritage Courtyard
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(totalHeight)
            .background(backgroundBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val zoneHeight = h / 6f

            // Draw subtle, soft topographic contour curves at biome transitions
            val strokeWidth = 2.dp.toPx()
            val contourColor = Color.White.copy(alpha = 0.35f)

            for (zone in 1..5) {
                val transitionY = zone * zoneHeight
                val wavePath = Path().apply {
                    moveTo(0f, transitionY)
                    cubicTo(
                        w * 0.25f, transitionY - 18.dp.toPx(),
                        w * 0.75f, transitionY + 18.dp.toPx(),
                        w, transitionY
                    )
                }
                drawPath(
                    path = wavePath,
                    color = contourColor,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            }

            // Gentle ambient morning sun in the upper atmosphere
            val sunRadius = 40.dp.toPx()
            val sunCenter = Offset(w * 0.82f, 70.dp.toPx())
            drawCircle(
                color = Color(0x33FDE047),
                radius = sunRadius * 1.5f,
                center = sunCenter
            )
            drawCircle(
                color = Color(0x66FEF08A),
                radius = sunRadius,
                center = sunCenter
            )
        }

        content()
    }
}
