package com.playit.app.presentation.map.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.LocalReducedMotion

/**
 * High-Performance Living Filipino-Themed Map Background.
 *
 * Implements a multi-biome continuous landscape inspired by Duolingo ABC and Philippine geography:
 * 1. Pangkat 1 (Top): Chocolate Hills & Sunlit Green Meadows (Sky & Sand)
 * 2. Pangkat 2: Loboc River Valley (meandering jade river & ripples)
 * 3. Pangkat 3: Panglao Shoreline (golden sands & turquoise sea)
 * 4. Pangkat 4: Tarsier Rainforest Sanctuary (emerald canopy layers)
 * 5. Pangkat 5 (Bottom): Bohol Mountain Summit (sunset lavender peaks)
 */
@Composable
fun ChocolateHillsBackground(
    totalHeight: Dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    // Biome background gradient matching the 4-Benchmark warm storybook palette
    val backgroundBrush = Brush.verticalGradient(
        colorStops = arrayOf(
            0.00f to Color(0xFFCFE9FF), // SkyDeep - Morning Bohol Sky
            0.12f to Color(0xFFEAF6FF), // Sky - Light Meadow Sky
            0.20f to Color(0xFFD8F3DC), // Fresh Meadow Green
            0.35f to Color(0xFFCCFBF1), // River Valley Jade
            0.45f to Color(0xFFBAE6FD), // River Blue
            0.55f to Color(0xFFFEF3C7), // Panglao Coastal Gold
            0.65f to Color(0xFFFDE68A), // Warm Shoreline Sand
            0.75f to Color(0xFFDCFCE7), // Rainforest Mist
            0.85f to Color(0xFFBBF7D0), // Deep Jungle Emerald
            0.92f to Color(0xFFF3E8FF), // Mountain Ascent Lavender
            1.00f to Color(0xFFE9D5FF)  // Bohol Summit Twilight
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(totalHeight)
            .background(backgroundBrush)
    ) {
        // High-Performance Static Vector Biome Landscape
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // ── Zone 1: Sky & Chocolate Hills (Top 0% - 22%) ────────────────
            val zone1Height = h * 0.22f
            drawSunAndStaticClouds(w)
            drawChocolateHillsZone(w, baseY = zone1Height)

            // ── Zone 2: Loboc River Valley (22% - 44%) ─────────────────────────
            val zone2StartY = h * 0.22f
            val zone2Height = h * 0.22f
            drawLobocRiverZone(w, startY = zone2StartY, height = zone2Height)

            // ── Zone 3: Panglao Shoreline & Coral Beach (44% - 66%) ─────────────
            val zone3StartY = h * 0.44f
            val zone3Height = h * 0.22f
            drawPanglaoBeachZone(w, startY = zone3StartY, height = zone3Height)

            // ── Zone 4: Tarsier Rainforest Canopy (66% - 88%) ──────────────────
            val zone4StartY = h * 0.66f
            val zone4Height = h * 0.22f
            drawRainforestZone(w, startY = zone4StartY, height = zone4Height)

            // ── Zone 5: Bohol Mountain Summit & Sunset Crest (88% - 100%) ───────
            val zone5StartY = h * 0.88f
            val zone5Height = h * 0.12f
            drawMountainSummitZone(w, startY = zone5StartY, height = zone5Height, totalHeight = h)
        }

        content()
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Canvas Zone Renderers (Optimized & Unified 4-Benchmark Vector Paths)
// ═══════════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawSunAndStaticClouds(canvasWidth: Float) {
    val sunCenterX = canvasWidth - 48.dp.toPx()
    val sunCenterY = 48.dp.toPx()
    val baseRadius = 24.dp.toPx()

    // Outer warm halo
    drawCircle(
        color = Color(0xFFFFE082).copy(alpha = 0.35f),
        radius = baseRadius + 14.dp.toPx(),
        center = Offset(sunCenterX, sunCenterY)
    )

    // Mid halo
    drawCircle(
        color = Color(0xFFFFD54F).copy(alpha = 0.6f),
        radius = baseRadius + 7.dp.toPx(),
        center = Offset(sunCenterX, sunCenterY)
    )

    // Sun core
    drawCircle(
        color = Color(0xFFFFB300),
        radius = baseRadius,
        center = Offset(sunCenterX, sunCenterY)
    )

    // Floating Clouds
    val clouds = listOf(
        CloudData(0.08f, 28.dp.toPx(), 60.dp.toPx(), 24.dp.toPx(), 0.85f),
        CloudData(0.42f, 70.dp.toPx(), 74.dp.toPx(), 28.dp.toPx(), 0.75f),
        CloudData(0.72f, 130.dp.toPx(), 52.dp.toPx(), 22.dp.toPx(), 0.80f),
        CloudData(0.18f, 190.dp.toPx(), 68.dp.toPx(), 26.dp.toPx(), 0.70f)
    )

    clouds.forEach { cloud ->
        val x = canvasWidth * cloud.xFraction
        drawCloud(Offset(x, cloud.y), cloud.width, cloud.height, cloud.alpha)
    }
}

private fun DrawScope.drawChocolateHillsZone(canvasWidth: Float, baseY: Float) {
    // Distant soft hills (Tan)
    val distantHills = listOf(
        HillData(0.10f, 0.32f, 75.dp.toPx(), Color(0xFFD4A373).copy(alpha = 0.55f)),
        HillData(0.40f, 0.36f, 85.dp.toPx(), Color(0xFFC99A65).copy(alpha = 0.60f)),
        HillData(0.75f, 0.34f, 80.dp.toPx(), Color(0xFFD4A373).copy(alpha = 0.55f)),
        HillData(1.02f, 0.30f, 70.dp.toPx(), Color(0xFFC99A65).copy(alpha = 0.50f))
    )

    distantHills.forEach { hill ->
        drawDomeHill(
            centerX = canvasWidth * hill.centerXFraction,
            baseY = baseY - 20.dp.toPx(),
            width = canvasWidth * hill.widthFraction,
            height = hill.height,
            color = hill.color
        )
    }

    // Midground iconic Bohol Chocolate Hills (Warm Tan / Chocolate)
    val mainHills = listOf(
        HillData(0.02f, 0.38f, 105.dp.toPx(), Color(0xFFC49A6C)),
        HillData(0.32f, 0.42f, 120.dp.toPx(), Color(0xFFA67C52)),
        HillData(0.68f, 0.40f, 115.dp.toPx(), Color(0xFFC49A6C)),
        HillData(0.98f, 0.36f, 98.dp.toPx(), Color(0xFFA67C52))
    )

    mainHills.forEach { hill ->
        drawDomeHill(
            centerX = canvasWidth * hill.centerXFraction,
            baseY = baseY + 10.dp.toPx(),
            width = canvasWidth * hill.widthFraction,
            height = hill.height,
            color = hill.color
        )
    }

    // Foreground lush green meadow contour
    val meadowPath = Path().apply {
        moveTo(0f, baseY)
        cubicTo(
            canvasWidth * 0.25f, baseY + 35.dp.toPx(),
            canvasWidth * 0.70f, baseY - 25.dp.toPx(),
            canvasWidth, baseY + 20.dp.toPx()
        )
        lineTo(canvasWidth, baseY + 140.dp.toPx())
        lineTo(0f, baseY + 140.dp.toPx())
        close()
    }
    drawPath(
        path = meadowPath,
        color = Color(0xFF86EFAC).copy(alpha = 0.50f)
    )
}

private fun DrawScope.drawLobocRiverZone(canvasWidth: Float, startY: Float, height: Float) {
    // Soft valley banks
    val leftBank = Path().apply {
        moveTo(0f, startY)
        cubicTo(
            canvasWidth * 0.30f, startY + height * 0.25f,
            canvasWidth * 0.15f, startY + height * 0.65f,
            0f, startY + height
        )
        lineTo(0f, startY)
        close()
    }
    drawPath(path = leftBank, color = Color(0xFFA7F3D0).copy(alpha = 0.45f))

    val rightBank = Path().apply {
        moveTo(canvasWidth, startY)
        cubicTo(
            canvasWidth * 0.70f, startY + height * 0.35f,
            canvasWidth * 0.85f, startY + height * 0.75f,
            canvasWidth, startY + height
        )
        lineTo(canvasWidth, startY)
        close()
    }
    drawPath(path = rightBank, color = Color(0xFFA7F3D0).copy(alpha = 0.45f))

    // Meandering Jade River Stream
    val riverPath = Path().apply {
        moveTo(canvasWidth * 0.45f, startY)
        cubicTo(
            canvasWidth * 0.20f, startY + height * 0.28f,
            canvasWidth * 0.75f, startY + height * 0.62f,
            canvasWidth * 0.50f, startY + height
        )
    }

    drawPath(
        path = riverPath,
        color = Color(0xFF5EEAD4).copy(alpha = 0.65f),
        style = Stroke(width = 68.dp.toPx(), cap = StrokeCap.Round)
    )
    drawPath(
        path = riverPath,
        color = Color(0xFFCCFBF1).copy(alpha = 0.75f),
        style = Stroke(width = 32.dp.toPx(), cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawPanglaoBeachZone(canvasWidth: Float, startY: Float, height: Float) {
    val shorelinePath = Path().apply {
        moveTo(0f, startY)
        cubicTo(
            canvasWidth * 0.35f, startY + height * 0.30f,
            canvasWidth * 0.65f, startY + height * 0.15f,
            canvasWidth, startY + height * 0.40f
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(path = shorelinePath, color = Color(0xFFFDE68A).copy(alpha = 0.45f))

    // Turquoise Sea Lagoon
    val seaPath = Path().apply {
        moveTo(0f, startY + height * 0.55f)
        cubicTo(
            canvasWidth * 0.40f, startY + height * 0.40f,
            canvasWidth * 0.70f, startY + height * 0.70f,
            canvasWidth, startY + height * 0.50f
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(path = seaPath, color = Color(0xFF38BDF8).copy(alpha = 0.35f))
}

private fun DrawScope.drawRainforestZone(canvasWidth: Float, startY: Float, height: Float) {
    // Canopy Layers (Deep Emerald)
    val layer1 = Path().apply {
        moveTo(0f, startY + 20.dp.toPx())
        cubicTo(
            canvasWidth * 0.30f, startY - 15.dp.toPx(),
            canvasWidth * 0.70f, startY + 45.dp.toPx(),
            canvasWidth, startY + 10.dp.toPx()
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(path = layer1, color = Color(0xFF22C55E).copy(alpha = 0.25f))

    val layer2 = Path().apply {
        moveTo(0f, startY + height * 0.50f)
        cubicTo(
            canvasWidth * 0.40f, startY + height * 0.35f,
            canvasWidth * 0.60f, startY + height * 0.65f,
            canvasWidth, startY + height * 0.45f
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(path = layer2, color = Color(0xFF15803D).copy(alpha = 0.20f))
}

private fun DrawScope.drawMountainSummitZone(canvasWidth: Float, startY: Float, height: Float, totalHeight: Float) {
    // Layered Twilight Mountain Peaks (Ube/Lavender)
    val peak1 = Path().apply {
        moveTo(-20.dp.toPx(), totalHeight)
        lineTo(canvasWidth * 0.28f, startY + 10.dp.toPx())
        lineTo(canvasWidth * 0.62f, totalHeight)
        close()
    }
    drawPath(path = peak1, color = Color(0xFF8B5CF6).copy(alpha = 0.30f))

    val peak2 = Path().apply {
        moveTo(canvasWidth * 0.38f, totalHeight)
        lineTo(canvasWidth * 0.78f, startY - 10.dp.toPx())
        lineTo(canvasWidth + 40.dp.toPx(), totalHeight)
        close()
    }
    drawPath(path = peak2, color = Color(0xFF6D28D9).copy(alpha = 0.35f))
}

// ═══════════════════════════════════════════════════════════════════════════════
// Helper Draw Functions & Data Classes
// ═══════════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawDomeHill(
    centerX: Float,
    baseY: Float,
    width: Float,
    height: Float,
    color: Color
) {
    val halfW = width / 2f
    val path = Path().apply {
        moveTo(centerX - halfW, baseY)
        cubicTo(
            centerX - halfW * 0.75f, baseY - height * 0.95f,
            centerX + halfW * 0.75f, baseY - height * 0.95f,
            centerX + halfW, baseY
        )
        close()
    }
    drawPath(path = path, color = color)
}

private fun DrawScope.drawCloud(center: Offset, width: Float, height: Float, alpha: Float) {
    val cloudColor = Color.White.copy(alpha = alpha)
    val r = height / 2f
    drawRoundRect(
        color = cloudColor,
        topLeft = Offset(center.x - width / 2f, center.y - r),
        size = Size(width, height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r)
    )
    drawCircle(
        color = cloudColor,
        radius = r * 1.15f,
        center = Offset(center.x - width * 0.20f, center.y - r * 0.35f)
    )
    drawCircle(
        color = cloudColor,
        radius = r * 0.95f,
        center = Offset(center.x + width * 0.20f, center.y - r * 0.25f)
    )
}

private data class CloudData(
    val xFraction: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val alpha: Float
)

private data class HillData(
    val centerXFraction: Float,
    val widthFraction: Float,
    val height: Float,
    val color: Color
)
