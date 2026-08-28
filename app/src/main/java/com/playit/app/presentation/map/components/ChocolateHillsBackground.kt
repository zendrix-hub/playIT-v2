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
import kotlin.math.sin

/**
 * Living Filipino-Themed Map Background.
 *
 * Implements a multi-biome continuous landscape inspired by Duolingo ABC and Philippine geography:
 * 1. Pangkat 1 (Top): Chocolate Hills & Sunlit Green Meadows
 * 2. Pangkat 2: Loboc River Valley (meandering sparkling river & water ripples)
 * 3. Pangkat 3: Panglao Shoreline (golden sands & turquoise sea waves)
 * 4. Pangkat 4: Tarsier Rainforest Sanctuary (lush canopy layers & glowing fireflies)
 * 5. Pangkat 5 (Bottom): Bohol Mountain Summit (sunset twilight & layered peaks)
 */
@Composable
fun ChocolateHillsBackground(
    totalHeight: Dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val isReducedMotion = LocalReducedMotion.current

    // Infinite animations for living environment
    val infiniteTransition = rememberInfiniteTransition(label = "livingMapEnv")

    // Cloud drift
    val cloudDrift by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloudDrift"
    )

    // Sun pulse
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunPulse"
    )

    // River wave shimmer
    val waveShimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f, // 2 * PI
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveShimmer"
    )

    // Firefly twinkle
    val fireflyAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fireflyAlpha"
    )

    val effectiveDrift = if (isReducedMotion) 0f else cloudDrift
    val effectiveSunPulse = if (isReducedMotion) 1f else sunPulse
    val effectiveWave = if (isReducedMotion) 0f else waveShimmer
    val effectiveFirefly = if (isReducedMotion) 0.7f else fireflyAlpha

    // Biome background gradient
    val backgroundBrush = Brush.verticalGradient(
        colorStops = arrayOf(
            0.00f to Color(0xFFCFE9FF), // SkyDeep - Morning Bohol Sky
            0.15f to Color(0xFFEAF6FF), // Sky - Light Meadow Sky
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
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // ── Zone 1: Sky, Sun & Chocolate Hills (Top 0% - 22%) ────────────────
            val zone1Height = h * 0.22f
            drawSunAndClouds(w, effectiveSunPulse, effectiveDrift)
            drawChocolateHillsZone(w, baseY = zone1Height, drift = effectiveDrift)

            // ── Zone 2: Loboc River Valley (22% - 44%) ─────────────────────────
            val zone2StartY = h * 0.22f
            val zone2Height = h * 0.22f
            drawLobocRiverZone(w, startY = zone2StartY, height = zone2Height, wavePhase = effectiveWave)

            // ── Zone 3: Panglao Shoreline & Coral Beach (44% - 66%) ─────────────
            val zone3StartY = h * 0.44f
            val zone3Height = h * 0.22f
            drawPanglaoBeachZone(w, startY = zone3StartY, height = zone3Height, wavePhase = effectiveWave)

            // ── Zone 4: Tarsier Rainforest Canopy (66% - 88%) ──────────────────
            val zone4StartY = h * 0.66f
            val zone4Height = h * 0.22f
            drawRainforestZone(w, startY = zone4StartY, height = zone4Height, fireflyAlpha = effectiveFirefly)

            // ── Zone 5: Bohol Mountain Summit & Sunset Crest (88% - 100%) ───────
            val zone5StartY = h * 0.88f
            val zone5Height = h * 0.12f
            drawMountainSummitZone(w, startY = zone5StartY, height = zone5Height, totalHeight = h)
        }

        content()
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Canvas Zone Renderers
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Draws the radiant tropical sun with gentle pulsing halo and floating clouds.
 */
private fun DrawScope.drawSunAndClouds(canvasWidth: Float, sunPulse: Float, drift: Float) {
    val sunCenterX = canvasWidth - 48.dp.toPx()
    val sunCenterY = 48.dp.toPx()
    val baseRadius = 24.dp.toPx()

    // Outer warm halo
    drawCircle(
        color = Color(0xFFFFE082).copy(alpha = 0.35f),
        radius = (baseRadius + 16.dp.toPx()) * sunPulse,
        center = Offset(sunCenterX, sunCenterY)
    )

    // Mid halo
    drawCircle(
        color = Color(0xFFFFD54F).copy(alpha = 0.6f),
        radius = (baseRadius + 8.dp.toPx()) * sunPulse,
        center = Offset(sunCenterX, sunCenterY)
    )

    // Sun core
    drawCircle(
        color = Color(0xFFFFB300),
        radius = baseRadius,
        center = Offset(sunCenterX, sunCenterY)
    )

    // Decorative drifting clouds
    val clouds = listOf(
        CloudData(0.08f, 28.dp.toPx(), 60.dp.toPx(), 24.dp.toPx(), 0.85f),
        CloudData(0.42f, 70.dp.toPx(), 74.dp.toPx(), 28.dp.toPx(), 0.75f),
        CloudData(0.72f, 130.dp.toPx(), 52.dp.toPx(), 22.dp.toPx(), 0.80f),
        CloudData(0.18f, 190.dp.toPx(), 68.dp.toPx(), 26.dp.toPx(), 0.70f)
    )

    clouds.forEach { cloud ->
        val x = canvasWidth * cloud.xFraction + (drift * cloud.parallax)
        drawCloud(Offset(x, cloud.y), cloud.width, cloud.height, cloud.alpha)
    }
}

/**
 * Draws iconic Bohol Chocolate Hills mounds and lush rolling meadows for Group 1.
 */
private fun DrawScope.drawChocolateHillsZone(canvasWidth: Float, baseY: Float, drift: Float) {
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
            canvasWidth * 0.75f, baseY - 25.dp.toPx(),
            canvasWidth, baseY + 15.dp.toPx()
        )
        lineTo(canvasWidth, baseY + 80.dp.toPx())
        lineTo(0f, baseY + 80.dp.toPx())
        close()
    }
    drawPath(path = meadowPath, color = Color(0xFF86EFAC).copy(alpha = 0.65f))

    // Decorative grass blade tufts
    drawGrassTuft(Offset(canvasWidth * 0.15f, baseY + 10.dp.toPx()))
    drawGrassTuft(Offset(canvasWidth * 0.82f, baseY + 25.dp.toPx()))
}

/**
 * Draws the winding Loboc River with sparkling turquoise water and ripple curves.
 */
private fun DrawScope.drawLobocRiverZone(
    canvasWidth: Float,
    startY: Float,
    height: Float,
    wavePhase: Float
) {
    val midY = startY + height * 0.45f

    // Riverbank lush meadow background
    val riverbankPath = Path().apply {
        moveTo(0f, startY + 40.dp.toPx())
        cubicTo(
            canvasWidth * 0.35f, startY + 15.dp.toPx(),
            canvasWidth * 0.65f, startY + 65.dp.toPx(),
            canvasWidth, startY + 30.dp.toPx()
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(path = riverbankPath, color = Color(0xFF86EFAC).copy(alpha = 0.45f))

    // Main Loboc River ribbon (curving from left to right)
    val riverPath = Path().apply {
        moveTo(0f, midY - 30.dp.toPx())
        cubicTo(
            canvasWidth * 0.30f, midY + 40.dp.toPx(),
            canvasWidth * 0.70f, midY - 50.dp.toPx(),
            canvasWidth, midY + 20.dp.toPx()
        )
        lineTo(canvasWidth, midY + 65.dp.toPx())
        cubicTo(
            canvasWidth * 0.70f, midY - 5.dp.toPx(),
            canvasWidth * 0.30f, midY + 85.dp.toPx(),
            0f, midY + 15.dp.toPx()
        )
        close()
    }

    // River water gradient
    drawPath(
        path = riverPath,
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF38BDF8),
                Color(0xFF0EA5E9),
                Color(0xFF2DD4BF),
                Color(0xFF38BDF8)
            )
        )
    )

    // Animated water shimmer ripples
    val rippleOffset = sin(wavePhase) * 6.dp.toPx()
    val ripplePath = Path().apply {
        moveTo(canvasWidth * 0.15f, midY + 10.dp.toPx() + rippleOffset)
        quadraticBezierTo(
            canvasWidth * 0.35f, midY + 35.dp.toPx() - rippleOffset,
            canvasWidth * 0.55f, midY - 10.dp.toPx() + rippleOffset
        )
    }
    drawPath(
        path = ripplePath,
        color = Color.White.copy(alpha = 0.7f),
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )

    val ripplePath2 = Path().apply {
        moveTo(canvasWidth * 0.45f, midY + 5.dp.toPx() - rippleOffset)
        quadraticBezierTo(
            canvasWidth * 0.68f, midY - 25.dp.toPx() + rippleOffset,
            canvasWidth * 0.88f, midY + 35.dp.toPx() - rippleOffset
        )
    }
    drawPath(
        path = ripplePath2,
        color = Color.White.copy(alpha = 0.65f),
        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
    )
}

/**
 * Draws the tropical Panglao Beach Shoreline with golden sand curves and turquoise seafoam waves.
 */
private fun DrawScope.drawPanglaoBeachZone(
    canvasWidth: Float,
    startY: Float,
    height: Float,
    wavePhase: Float
) {
    val shoreY = startY + height * 0.5f
    val waveOffset = sin(wavePhase * 1.2f) * 5.dp.toPx()

    // Turquoise Ocean Water
    val oceanPath = Path().apply {
        moveTo(0f, startY)
        lineTo(canvasWidth, startY)
        lineTo(canvasWidth, shoreY - 20.dp.toPx() + waveOffset)
        cubicTo(
            canvasWidth * 0.65f, shoreY + 30.dp.toPx() - waveOffset,
            canvasWidth * 0.35f, shoreY - 40.dp.toPx() + waveOffset,
            0f, shoreY + 10.dp.toPx()
        )
        close()
    }
    drawPath(
        path = oceanPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF2DD4BF),
                Color(0xFF14B8A6),
                Color(0xFF0D9488)
            ),
            startY = startY,
            endY = shoreY + 30.dp.toPx()
        )
    )

    // White Seafoam Wave Crest
    val foamPath = Path().apply {
        moveTo(0f, shoreY + 10.dp.toPx())
        cubicTo(
            canvasWidth * 0.35f, shoreY - 40.dp.toPx() + waveOffset,
            canvasWidth * 0.65f, shoreY + 30.dp.toPx() - waveOffset,
            canvasWidth, shoreY - 20.dp.toPx() + waveOffset
        )
    }
    drawPath(
        path = foamPath,
        color = Color(0xFFF0FDFA).copy(alpha = 0.85f),
        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
    )

    // Warm Golden Sand Coast
    val sandPath = Path().apply {
        moveTo(0f, shoreY + 10.dp.toPx())
        cubicTo(
            canvasWidth * 0.35f, shoreY - 40.dp.toPx() + waveOffset,
            canvasWidth * 0.65f, shoreY + 30.dp.toPx() - waveOffset,
            canvasWidth, shoreY - 20.dp.toPx() + waveOffset
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(
        path = sandPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFEF08A),
                Color(0xFFFDE047),
                Color(0xFFFACC15)
            ),
            startY = shoreY - 20.dp.toPx(),
            endY = startY + height
        )
    )

    // Little sand texture dots
    drawCircle(Color(0xFFCA8A04).copy(alpha = 0.35f), radius = 2.dp.toPx(), center = Offset(canvasWidth * 0.22f, shoreY + 35.dp.toPx()))
    drawCircle(Color(0xFFCA8A04).copy(alpha = 0.35f), radius = 3.dp.toPx(), center = Offset(canvasWidth * 0.78f, shoreY + 45.dp.toPx()))
    drawCircle(Color(0xFFCA8A04).copy(alpha = 0.35f), radius = 2.5.dp.toPx(), center = Offset(canvasWidth * 0.48f, shoreY + 60.dp.toPx()))
}

/**
 * Draws dense tropical rainforest canopy layers and glowing firefly motes for Chapter 4.
 */
private fun DrawScope.drawRainforestZone(
    canvasWidth: Float,
    startY: Float,
    height: Float,
    fireflyAlpha: Float
) {
    val midY = startY + height * 0.5f

    // Deep jungle foliage background layer
    val junglePath = Path().apply {
        moveTo(0f, startY)
        cubicTo(
            canvasWidth * 0.30f, startY + 40.dp.toPx(),
            canvasWidth * 0.70f, startY - 20.dp.toPx(),
            canvasWidth, startY + 30.dp.toPx()
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(
        path = junglePath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF4ADE80).copy(alpha = 0.4f),
                Color(0xFF22C55E).copy(alpha = 0.6f),
                Color(0xFF16A34A).copy(alpha = 0.75f)
            ),
            startY = startY,
            endY = startY + height
        )
    )

    // Overlapping tropical leaf silhouettes at left & right borders
    drawTropicalFrond(Offset(0f, midY - 60.dp.toPx()), radius = 60.dp.toPx(), isLeft = true)
    drawTropicalFrond(Offset(canvasWidth, midY + 40.dp.toPx()), radius = 70.dp.toPx(), isLeft = false)

    // Floating glowing fireflies
    val fireflies = listOf(
        Offset(canvasWidth * 0.18f, startY + 40.dp.toPx()),
        Offset(canvasWidth * 0.82f, startY + 90.dp.toPx()),
        Offset(canvasWidth * 0.28f, midY + 30.dp.toPx()),
        Offset(canvasWidth * 0.72f, midY - 40.dp.toPx()),
        Offset(canvasWidth * 0.50f, startY + height - 30.dp.toPx())
    )

    fireflies.forEachIndexed { i, pos ->
        val phaseAlpha = (fireflyAlpha + (i * 0.15f)) % 1f
        // Outer glow
        drawCircle(
            color = Color(0xFFFDE047).copy(alpha = phaseAlpha * 0.4f),
            radius = 7.dp.toPx(),
            center = pos
        )
        // Bright core
        drawCircle(
            color = Color(0xFFFEF08A).copy(alpha = phaseAlpha),
            radius = 3.dp.toPx(),
            center = pos
        )
    }
}

/**
 * Draws majestic layered mountain crests and sunset twilight sky for Bohol Mountain Summit.
 */
private fun DrawScope.drawMountainSummitZone(
    canvasWidth: Float,
    startY: Float,
    height: Float,
    totalHeight: Float
) {
    // Distant Lavender Mountain Ridge
    val distantMountain = Path().apply {
        moveTo(0f, startY + 30.dp.toPx())
        lineTo(canvasWidth * 0.25f, startY - 20.dp.toPx())
        lineTo(canvasWidth * 0.55f, startY + 25.dp.toPx())
        lineTo(canvasWidth * 0.85f, startY - 35.dp.toPx())
        lineTo(canvasWidth, startY + 10.dp.toPx())
        lineTo(canvasWidth, totalHeight)
        lineTo(0f, totalHeight)
        close()
    }
    drawPath(path = distantMountain, color = Color(0xFFC084FC).copy(alpha = 0.5f))

    // Main Bohol Summit Ridge (Deep Ube / Purple)
    val mainSummit = Path().apply {
        moveTo(0f, startY + 70.dp.toPx())
        lineTo(canvasWidth * 0.35f, startY + 10.dp.toPx())
        lineTo(canvasWidth * 0.65f, startY + 50.dp.toPx())
        lineTo(canvasWidth, startY + 20.dp.toPx())
        lineTo(canvasWidth, totalHeight)
        lineTo(0f, totalHeight)
        close()
    }
    drawPath(
        path = mainSummit,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF9333EA),
                Color(0xFF7E22CE),
                Color(0xFF581C87)
            ),
            startY = startY + 10.dp.toPx(),
            endY = totalHeight
        )
    )

    // Twilight starry sparkle motes
    val stars = listOf(
        Offset(canvasWidth * 0.15f, startY - 15.dp.toPx()),
        Offset(canvasWidth * 0.45f, startY - 30.dp.toPx()),
        Offset(canvasWidth * 0.75f, startY - 10.dp.toPx()),
        Offset(canvasWidth * 0.90f, startY - 25.dp.toPx())
    )
    stars.forEach { star ->
        drawCircle(Color.White.copy(alpha = 0.85f), radius = 2.dp.toPx(), center = star)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Helper Drawing Functions
// ═══════════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawDomeHill(
    centerX: Float,
    baseY: Float,
    width: Float,
    height: Float,
    color: Color
) {
    val halfWidth = width / 2f
    val path = Path().apply {
        moveTo(centerX - halfWidth, baseY)
        cubicTo(
            centerX - halfWidth, baseY - height * 1.25f,
            centerX + halfWidth, baseY - height * 1.25f,
            centerX + halfWidth, baseY
        )
        close()
    }
    drawPath(path = path, color = color)
}

private fun DrawScope.drawCloud(center: Offset, width: Float, height: Float, alpha: Float) {
    val halfW = width / 2f
    val halfH = height / 2f
    val color = Color.White.copy(alpha = alpha)

    // Main center puff
    drawOval(color = color, topLeft = Offset(center.x - halfW, center.y - halfH), size = Size(width, height))
    // Left bubble
    drawCircle(color = color, radius = halfH * 1.15f, center = Offset(center.x - halfW * 0.4f, center.y - halfH * 0.2f))
    // Right bubble
    drawCircle(color = color, radius = halfH * 0.95f, center = Offset(center.x + halfW * 0.4f, center.y - halfH * 0.1f))
}

private fun DrawScope.drawGrassTuft(pos: Offset) {
    val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    val color = Color(0xFF15803D).copy(alpha = 0.6f)

    val leftBlade = Path().apply {
        moveTo(pos.x, pos.y)
        quadraticBezierTo(pos.x - 6.dp.toPx(), pos.y - 12.dp.toPx(), pos.x - 10.dp.toPx(), pos.y - 14.dp.toPx())
    }
    val centerBlade = Path().apply {
        moveTo(pos.x, pos.y)
        quadraticBezierTo(pos.x, pos.y - 14.dp.toPx(), pos.x + 1.dp.toPx(), pos.y - 18.dp.toPx())
    }
    val rightBlade = Path().apply {
        moveTo(pos.x, pos.y)
        quadraticBezierTo(pos.x + 6.dp.toPx(), pos.y - 12.dp.toPx(), pos.x + 10.dp.toPx(), pos.y - 13.dp.toPx())
    }

    drawPath(leftBlade, color, style = stroke)
    drawPath(centerBlade, color, style = stroke)
    drawPath(rightBlade, color, style = stroke)
}

private fun DrawScope.drawTropicalFrond(pos: Offset, radius: Float, isLeft: Boolean) {
    val frondColor = Color(0xFF15803D).copy(alpha = 0.28f)
    val path = Path().apply {
        if (isLeft) {
            moveTo(0f, pos.y)
            cubicTo(
                radius * 0.6f, pos.y - radius * 0.5f,
                radius * 0.9f, pos.y + radius * 0.3f,
                0f, pos.y + radius * 0.8f
            )
        } else {
            moveTo(pos.x, pos.y)
            cubicTo(
                pos.x - radius * 0.6f, pos.y - radius * 0.5f,
                pos.x - radius * 0.9f, pos.y + radius * 0.3f,
                pos.x, pos.y + radius * 0.8f
            )
        }
        close()
    }
    drawPath(path = path, color = frondColor)
}

private data class CloudData(
    val xFraction: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val alpha: Float,
    val parallax: Float = 1.0f
)

private data class HillData(
    val centerXFraction: Float,
    val widthFraction: Float,
    val height: Float,
    val color: Color
)
