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
import androidx.compose.ui.geometry.CornerRadius
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

/**
 * Authentic 6-Biome Bohol Adventure Map Background.
 * Faithfully adapted from reference photographs in tools/:
 *
 * 1. Unit 1 (0% - 17%): Chocolate Hills Phonics (tools/chocolate_hills.jpg) - Visible smiling morning sun, golden dome hills & fresh meadows.
 * 2. Unit 2 (17% - 34%): Loboc River Valley (tools/loboc_river.jpg) - Winding jade-turquoise river through lush rainforest canyon banks.
 * 3. Unit 3 (34% - 50%): Panglao Coral Shore (tools/panglao_coral.jpg) - Warm sand coast, azure ocean waves & vibrant coral reef motifs.
 * 4. Unit 4 (50% - 67%): Tarsier Rainforest Sanctuary (tools/tarsier_forest.webp) - Deep emerald jungle canopy layers, vines & tropical ferns.
 * 5. Unit 5 (67% - 84%): Bohol Mountain Summit (tools/mountain_summit.jpg) - Sunset golden hour mountain ridges & rolling summit horizon.
 * 6. Unit 6 (84% - 100%): Baclayon Heritage (tools/baclayon.jpg) - Historic coral stone church & bell tower, terracotta roofs & bougainvillea garden.
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
            0.10f to Color(0xFFE0F2FE), // Zone 1: Sunlit Sky
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
            1.00f to Color(0xFFFEF9C3)  // Zone 6: Heritage Bougainvillea Courtyard
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

            // ── Zone 1: Chocolate Hills & Clear Morning Sun (0% - 17%) ───────
            drawVisibleSunAndClouds(w)
            drawChocolateHillsZone(w, baseY = zoneHeight * 0.95f)

            // ── Zone 2: Loboc River Valley (17% - 34%) ───────────────────────
            drawLobocRiverZone(w, startY = zoneHeight * 1.0f, height = zoneHeight)

            // ── Zone 3: Panglao Coral Shore (34% - 50%) ───────────────────────
            drawPanglaoCoralZone(w, startY = zoneHeight * 2.0f, height = zoneHeight)

            // ── Zone 4: Tarsier Rainforest Sanctuary (50% - 67%) ─────────────
            drawTarsierRainforestZone(w, startY = zoneHeight * 3.0f, height = zoneHeight)

            // ── Zone 5: Bohol Mountain Summit (67% - 84%) ─────────────────────
            drawMountainSummitZone(w, startY = zoneHeight * 4.0f, height = zoneHeight)

            // ── Zone 6: Baclayon Heritage (84% - 100%) ────────────────────────
            drawBaclayonHeritageZone(w, startY = zoneHeight * 5.0f, height = zoneHeight)
        }

        content()
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Zone 1: Chocolate Hills & Prominent Visible Sun (tools/chocolate_hills.jpg)
// ═══════════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawVisibleSunAndClouds(canvasWidth: Float) {
    // Positioned clearly in the open sky below the header so it is 100% visible
    val sunCenterX = canvasWidth - 54.dp.toPx()
    val sunCenterY = 108.dp.toPx()
    val baseRadius = 26.dp.toPx()

    // Outer warm sunny halo
    drawCircle(
        color = Color(0xFFFFE082).copy(alpha = 0.35f),
        radius = baseRadius + 18.dp.toPx(),
        center = Offset(sunCenterX, sunCenterY)
    )

    // Mid golden halo
    drawCircle(
        color = Color(0xFFFFD54F).copy(alpha = 0.55f),
        radius = baseRadius + 9.dp.toPx(),
        center = Offset(sunCenterX, sunCenterY)
    )

    // Sun core
    drawCircle(
        color = Color(0xFFFFB300),
        radius = baseRadius,
        center = Offset(sunCenterX, sunCenterY)
    )

    // Sunbeams / Rays
    val numRays = 8
    val rayInner = baseRadius + 4.dp.toPx()
    val rayOuter = baseRadius + 14.dp.toPx()
    for (i in 0 until numRays) {
        val angle = (i * (360f / numRays)) * (Math.PI / 180f).toFloat()
        val x1 = sunCenterX + kotlin.math.cos(angle) * rayInner
        val y1 = sunCenterY + kotlin.math.sin(angle) * rayInner
        val x2 = sunCenterX + kotlin.math.cos(angle) * rayOuter
        val y2 = sunCenterY + kotlin.math.sin(angle) * rayOuter
        drawLine(
            color = Color(0xFFFFC107).copy(alpha = 0.75f),
            start = Offset(x1, y1),
            end = Offset(x2, y2),
            strokeWidth = 3.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Soft sky clouds
    drawCloud(Offset(canvasWidth * 0.15f, 75.dp.toPx()), 72.dp.toPx(), 26.dp.toPx(), 0.85f)
    drawCloud(Offset(canvasWidth * 0.55f, 135.dp.toPx()), 64.dp.toPx(), 24.dp.toPx(), 0.75f)
}

private fun DrawScope.drawChocolateHillsZone(canvasWidth: Float, baseY: Float) {
    // Distant tan dome hills
    val distantHills = listOf(
        HillData(0.12f, 0.35f, 70.dp.toPx(), Color(0xFFD4A373).copy(alpha = 0.60f)),
        HillData(0.44f, 0.38f, 82.dp.toPx(), Color(0xFFC99A65).copy(alpha = 0.65f)),
        HillData(0.78f, 0.36f, 76.dp.toPx(), Color(0xFFD4A373).copy(alpha = 0.60f)),
        HillData(1.02f, 0.32f, 68.dp.toPx(), Color(0xFFC99A65).copy(alpha = 0.55f))
    )
    distantHills.forEach { hill ->
        drawDomeHill(
            centerX = canvasWidth * hill.centerXFraction,
            baseY = baseY - 24.dp.toPx(),
            width = canvasWidth * hill.widthFraction,
            height = hill.height,
            color = hill.color
        )
    }

    // Midground iconic Chocolate Hills
    val mainHills = listOf(
        HillData(0.04f, 0.40f, 100.dp.toPx(), Color(0xFFC49A6C)),
        HillData(0.34f, 0.44f, 115.dp.toPx(), Color(0xFFA67C52)),
        HillData(0.70f, 0.42f, 110.dp.toPx(), Color(0xFFC49A6C)),
        HillData(0.96f, 0.38f, 95.dp.toPx(), Color(0xFFA67C52))
    )
    mainHills.forEach { hill ->
        drawDomeHill(
            centerX = canvasWidth * hill.centerXFraction,
            baseY = baseY + 6.dp.toPx(),
            width = canvasWidth * hill.widthFraction,
            height = hill.height,
            color = hill.color
        )
    }

    // Meadow valley contour
    val meadowPath = Path().apply {
        moveTo(0f, baseY)
        cubicTo(
            canvasWidth * 0.25f, baseY + 30.dp.toPx(),
            canvasWidth * 0.70f, baseY - 20.dp.toPx(),
            canvasWidth, baseY + 20.dp.toPx()
        )
        lineTo(canvasWidth, baseY + 120.dp.toPx())
        lineTo(0f, baseY + 120.dp.toPx())
        close()
    }
    drawPath(path = meadowPath, color = Color(0xFF86EFAC).copy(alpha = 0.45f))
}

// ═══════════════════════════════════════════════════════════════════════════════
// Zone 2: Loboc River Valley (tools/loboc_river.jpg)
// ═══════════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawLobocRiverZone(canvasWidth: Float, startY: Float, height: Float) {
    // Lush canyon hillside banks
    val leftBank = Path().apply {
        moveTo(0f, startY)
        cubicTo(
            canvasWidth * 0.32f, startY + height * 0.28f,
            canvasWidth * 0.16f, startY + height * 0.68f,
            0f, startY + height
        )
        lineTo(0f, startY)
        close()
    }
    drawPath(path = leftBank, color = Color(0xFFA7F3D0).copy(alpha = 0.50f))

    val rightBank = Path().apply {
        moveTo(canvasWidth, startY)
        cubicTo(
            canvasWidth * 0.68f, startY + height * 0.32f,
            canvasWidth * 0.84f, startY + height * 0.72f,
            canvasWidth, startY + height
        )
        lineTo(canvasWidth, startY)
        close()
    }
    drawPath(path = rightBank, color = Color(0xFFA7F3D0).copy(alpha = 0.50f))

    // Meandering turquoise jade river
    val riverPath = Path().apply {
        moveTo(canvasWidth * 0.46f, startY)
        cubicTo(
            canvasWidth * 0.22f, startY + height * 0.30f,
            canvasWidth * 0.76f, startY + height * 0.64f,
            canvasWidth * 0.50f, startY + height
        )
    }
    drawPath(
        path = riverPath,
        color = Color(0xFF14B8A6).copy(alpha = 0.60f),
        style = Stroke(width = 64.dp.toPx(), cap = StrokeCap.Round)
    )
    drawPath(
        path = riverPath,
        color = Color(0xFF99F6E4).copy(alpha = 0.75f),
        style = Stroke(width = 30.dp.toPx(), cap = StrokeCap.Round)
    )

    // River ripple highlights
    drawCircle(color = Color.White.copy(alpha = 0.6f), radius = 3.dp.toPx(), center = Offset(canvasWidth * 0.38f, startY + height * 0.40f))
    drawCircle(color = Color.White.copy(alpha = 0.6f), radius = 4.dp.toPx(), center = Offset(canvasWidth * 0.60f, startY + height * 0.55f))
}

// ═══════════════════════════════════════════════════════════════════════════════
// Zone 3: Panglao Coral Shore (tools/panglao_coral.jpg)
// ═══════════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawPanglaoCoralZone(canvasWidth: Float, startY: Float, height: Float) {
    // Golden sand coastline
    val coastPath = Path().apply {
        moveTo(0f, startY)
        cubicTo(
            canvasWidth * 0.35f, startY + height * 0.25f,
            canvasWidth * 0.65f, startY + height * 0.15f,
            canvasWidth, startY + height * 0.35f
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(path = coastPath, color = Color(0xFFFDE68A).copy(alpha = 0.50f))

    // Crystal azure ocean water
    val oceanPath = Path().apply {
        moveTo(0f, startY + height * 0.45f)
        cubicTo(
            canvasWidth * 0.38f, startY + height * 0.35f,
            canvasWidth * 0.68f, startY + height * 0.60f,
            canvasWidth, startY + height * 0.45f
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(path = oceanPath, color = Color(0xFF38BDF8).copy(alpha = 0.40f))

    // Coral Reef & Starfish Motifs (tools/panglao_coral.jpg)
    val coralPink = Color(0xFFFB7185).copy(alpha = 0.55f)
    val coralOrange = Color(0xFFFB923C).copy(alpha = 0.60f)

    // Coral clusters
    drawCircle(color = coralPink, radius = 10.dp.toPx(), center = Offset(canvasWidth * 0.18f, startY + height * 0.70f))
    drawCircle(color = coralPink, radius = 7.dp.toPx(), center = Offset(canvasWidth * 0.22f, startY + height * 0.68f))
    drawCircle(color = coralOrange, radius = 12.dp.toPx(), center = Offset(canvasWidth * 0.82f, startY + height * 0.65f))
    drawCircle(color = coralOrange, radius = 8.dp.toPx(), center = Offset(canvasWidth * 0.77f, startY + height * 0.68f))
}

// ═══════════════════════════════════════════════════════════════════════════════
// Zone 4: Tarsier Rainforest Sanctuary (tools/tarsier_forest.webp)
// ═══════════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawTarsierRainforestZone(canvasWidth: Float, startY: Float, height: Float) {
    // Deep emerald moss canopy layers
    val canopy1 = Path().apply {
        moveTo(0f, startY + 15.dp.toPx())
        cubicTo(
            canvasWidth * 0.30f, startY - 10.dp.toPx(),
            canvasWidth * 0.70f, startY + 40.dp.toPx(),
            canvasWidth, startY + 10.dp.toPx()
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(path = canopy1, color = Color(0xFF22C55E).copy(alpha = 0.30f))

    val canopy2 = Path().apply {
        moveTo(0f, startY + height * 0.45f)
        cubicTo(
            canvasWidth * 0.40f, startY + height * 0.30f,
            canvasWidth * 0.60f, startY + height * 0.60f,
            canvasWidth, startY + height * 0.40f
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(path = canopy2, color = Color(0xFF15803D).copy(alpha = 0.25f))

    // Jungle vine curves
    val vinePath = Path().apply {
        moveTo(canvasWidth * 0.10f, startY)
        cubicTo(
            canvasWidth * 0.15f, startY + height * 0.4f,
            canvasWidth * 0.05f, startY + height * 0.7f,
            canvasWidth * 0.12f, startY + height
        )
    }
    drawPath(path = vinePath, color = Color(0xFF166534).copy(alpha = 0.35f), style = Stroke(width = 3.dp.toPx()))
}

// ═══════════════════════════════════════════════════════════════════════════════
// Zone 5: Bohol Mountain Summit (tools/mountain_summit.jpg)
// ═══════════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawMountainSummitZone(canvasWidth: Float, startY: Float, height: Float) {
    // Rolling golden hour mountain ridges
    val ridge1 = Path().apply {
        moveTo(0f, startY + height * 0.40f)
        cubicTo(
            canvasWidth * 0.30f, startY + height * 0.15f,
            canvasWidth * 0.70f, startY + height * 0.35f,
            canvasWidth, startY + height * 0.20f
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(path = ridge1, color = Color(0xFFF59E0B).copy(alpha = 0.35f))

    val ridge2 = Path().apply {
        moveTo(0f, startY + height * 0.65f)
        cubicTo(
            canvasWidth * 0.45f, startY + height * 0.45f,
            canvasWidth * 0.65f, startY + height * 0.70f,
            canvasWidth, startY + height * 0.55f
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(path = ridge2, color = Color(0xFFD97706).copy(alpha = 0.40f))
}

// ═══════════════════════════════════════════════════════════════════════════════
// Zone 6: Baclayon Heritage (tools/baclayon.jpg) — Spanish Coral Stone & Garden
// ═══════════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawBaclayonHeritageZone(canvasWidth: Float, startY: Float, height: Float) {
    // Cobblestone Heritage Promenade Base
    val promenade = Path().apply {
        moveTo(0f, startY + height * 0.30f)
        cubicTo(
            canvasWidth * 0.35f, startY + height * 0.20f,
            canvasWidth * 0.65f, startY + height * 0.35f,
            canvasWidth, startY + height * 0.25f
        )
        lineTo(canvasWidth, startY + height)
        lineTo(0f, startY + height)
        close()
    }
    drawPath(path = promenade, color = Color(0xFFFEF3C7).copy(alpha = 0.60f))

    // Historic Baclayon Coral Stone Church Silhouette (tools/baclayon.jpg)
    val churchX = canvasWidth * 0.62f
    val churchY = startY + height * 0.35f
    val churchW = 90.dp.toPx()
    val churchH = 65.dp.toPx()

    // Church main facade
    drawRoundRect(
        color = Color(0xFFD4A373).copy(alpha = 0.55f),
        topLeft = Offset(churchX, churchY),
        size = Size(churchW, churchH),
        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
    )

    // Bell Tower (Left section of church)
    val towerW = 32.dp.toPx()
    val towerH = 95.dp.toPx()
    val towerX = churchX - 22.dp.toPx()
    val towerY = churchY - 30.dp.toPx()

    drawRoundRect(
        color = Color(0xFFC49A6C).copy(alpha = 0.65f),
        topLeft = Offset(towerX, towerY),
        size = Size(towerW, towerH),
        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
    )

    // Terracotta Red Roof Dome on Tower (tools/baclayon.jpg)
    val domePath = Path().apply {
        moveTo(towerX, towerY)
        cubicTo(
            towerX + towerW * 0.2f, towerY - 14.dp.toPx(),
            towerX + towerW * 0.8f, towerY - 14.dp.toPx(),
            towerX + towerW, towerY
        )
        close()
    }
    drawPath(path = domePath, color = Color(0xFFDC2626).copy(alpha = 0.70f))

    // Arched Church Portals & Windows
    drawRoundRect(
        color = Color(0xFF78350F).copy(alpha = 0.40f),
        topLeft = Offset(churchX + 24.dp.toPx(), churchY + 28.dp.toPx()),
        size = Size(20.dp.toPx(), 32.dp.toPx()),
        cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
    )
    drawRoundRect(
        color = Color(0xFF78350F).copy(alpha = 0.40f),
        topLeft = Offset(towerX + 8.dp.toPx(), towerY + 18.dp.toPx()),
        size = Size(14.dp.toPx(), 20.dp.toPx()),
        cornerRadius = CornerRadius(7.dp.toPx(), 7.dp.toPx())
    )

    // Flowering Bougainvillea Gardens & Topiary Cones (tools/baclayon.jpg)
    val gardenGreen = Color(0xFF15803D).copy(alpha = 0.65f)
    val bougainvilleaPink = Color(0xFFE11D48).copy(alpha = 0.60f)

    // Manicured topiary cones
    val cone1 = Path().apply {
        moveTo(canvasWidth * 0.15f, startY + height * 0.75f)
        lineTo(canvasWidth * 0.20f, startY + height * 0.58f)
        lineTo(canvasWidth * 0.25f, startY + height * 0.75f)
        close()
    }
    drawPath(path = cone1, color = gardenGreen)

    val cone2 = Path().apply {
        moveTo(canvasWidth * 0.30f, startY + height * 0.80f)
        lineTo(canvasWidth * 0.34f, startY + height * 0.64f)
        lineTo(canvasWidth * 0.38f, startY + height * 0.80f)
        close()
    }
    drawPath(path = cone2, color = gardenGreen)

    // Red/Pink Bougainvillea flower blooms
    drawCircle(color = bougainvilleaPink, radius = 5.dp.toPx(), center = Offset(canvasWidth * 0.20f, startY + height * 0.76f))
    drawCircle(color = bougainvilleaPink, radius = 6.dp.toPx(), center = Offset(canvasWidth * 0.26f, startY + height * 0.78f))
    drawCircle(color = bougainvilleaPink, radius = 5.5.dp.toPx(), center = Offset(canvasWidth * 0.35f, startY + height * 0.82f))
    drawCircle(color = bougainvilleaPink, radius = 6.dp.toPx(), center = Offset(canvasWidth * 0.52f, startY + height * 0.75f))
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
        cornerRadius = CornerRadius(r, r)
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

private data class HillData(
    val centerXFraction: Float,
    val widthFraction: Float,
    val height: Float,
    val color: Color
)
