package com.playit.app.presentation.map.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.LocalReducedMotion

enum class TerrainPropType(val assetPath: String) {
    PALM_TREE("images/backgrounds/mapprop_palm_tree.png"),
    NIPA_HUT("images/backgrounds/mapprop_nipa_hut.png"),
    FLOWER("images/backgrounds/mapprop_flower.png"),
    BUSH("images/backgrounds/map_prop_bush.png"),
    ROCK("images/backgrounds/map_prop_rock.png"),
    TREE_SMALL("images/backgrounds/map_prop_tree_small.png"),
    PAPER_AIRPLANE("images/backgrounds/mapprop_paper_airplane.png"),
    BOOK_STACK("images/backgrounds/mapprop_book_stack.png"),
    CRAYON_BRIDGE("images/backgrounds/mapprop_crayon_bridge.png"),
    GLOBE("images/backgrounds/mapprop_globe.png"),
    MAGNIFYING_GLASS("images/backgrounds/mapprop_magnifying_glass.png"),
    PENCIL_TOWER("images/backgrounds/mapprop_pencil_tower.png"),
    BACKPACK("images/backgrounds/mapprop_backpack.png"),
    PAINT_PALETTE("images/backgrounds/mapprop_paint_palette.png")
}

data class PlacedTerrainProp(
    val type: TerrainPropType,
    val offsetDp: Offset,
    val scale: Float = 1.0f
)

/**
 * Calculates biome-aware terrain prop placements along the map based on total node count.
 */
fun generateTerrainProps(
    nodeCount: Int,
    nodeVerticalSpacingDp: Float,
    canvasWidthDp: Float,
    topPaddingDp: Float
): List<PlacedTerrainProp> {
    if (nodeCount <= 0 || canvasWidthDp <= 0f) return emptyList()

    val props = mutableListOf<PlacedTerrainProp>()

    for (i in 0 until nodeCount) {
        val nodeY = topPaddingDp + i * nodeVerticalSpacingDp
        val nodeXOffset = calculateNodeXOffsetDp(i, 50.dp).value
        val centerX = canvasWidthDp / 2f
        val pathX = centerX + nodeXOffset

        val isLeft = (i % 2 == 0)
        val propX = if (isLeft) {
            (pathX - 118f).coerceAtLeast(10f)
        } else {
            (pathX + 58f).coerceAtMost(canvasWidthDp - 82f)
        }
        val propY = nodeY + (if (i % 3 == 0) -24f else 22f)

        // Select prop type based on node index (Filipino Biome theming)
        val propType = when {
            // Group 1: Chocolate Hills & Meadows (Huts, Small Trees, Flowers, Bushes, Paper Airplanes)
            i < 7 -> when (i % 5) {
                0 -> TerrainPropType.NIPA_HUT
                1 -> TerrainPropType.FLOWER
                2 -> TerrainPropType.TREE_SMALL
                3 -> TerrainPropType.BUSH
                else -> TerrainPropType.PAPER_AIRPLANE
            }
            // Group 2: Loboc River Valley (Palm Trees, Rocks, Crayon Bridge, Book Stack, Flowers)
            i < 14 -> when (i % 5) {
                0 -> TerrainPropType.PALM_TREE
                1 -> TerrainPropType.ROCK
                2 -> TerrainPropType.CRAYON_BRIDGE
                3 -> TerrainPropType.BOOK_STACK
                else -> TerrainPropType.FLOWER
            }
            // Group 3: Panglao Shoreline (Palm Trees, Tropical Flowers, Rocks, Globe)
            i < 21 -> when (i % 4) {
                0 -> TerrainPropType.PALM_TREE
                1 -> TerrainPropType.FLOWER
                2 -> TerrainPropType.ROCK
                else -> TerrainPropType.GLOBE
            }
            // Group 4: Tarsier Rainforest Sanctuary (Small Trees, Bushes, Huts, Magnifying Glass)
            i < 28 -> when (i % 5) {
                0 -> TerrainPropType.TREE_SMALL
                1 -> TerrainPropType.BUSH
                2 -> TerrainPropType.NIPA_HUT
                3 -> TerrainPropType.MAGNIFYING_GLASS
                else -> TerrainPropType.FLOWER
            }
            // Group 5: Bohol Mountain Summit (Pencil Tower, Backpack, Paint Palette, Palm Tree)
            else -> when (i % 4) {
                0 -> TerrainPropType.PENCIL_TOWER
                1 -> TerrainPropType.BACKPACK
                2 -> TerrainPropType.PAINT_PALETTE
                else -> TerrainPropType.PALM_TREE
            }
        }

        val baseScale = when (propType) {
            TerrainPropType.NIPA_HUT -> 0.95f
            TerrainPropType.PALM_TREE -> 1.05f
            TerrainPropType.FLOWER -> 0.85f
            TerrainPropType.PENCIL_TOWER -> 0.90f
            TerrainPropType.CRAYON_BRIDGE -> 0.90f
            else -> 0.82f
        }

        props.add(
            PlacedTerrainProp(
                type = propType,
                offsetDp = Offset(propX, propY),
                scale = baseScale
            )
        )
    }

    return props
}

/**
 * Renders background terrain elements and foliage clusters scattered behind the map path using PNG assets
 * with gentle organic swaying/floating animations.
 */
@Composable
fun MapTerrainProps(
    nodeCount: Int,
    nodeVerticalSpacing: Dp,
    topPadding: Dp,
    modifier: Modifier = Modifier
) {
    val isReducedMotion = LocalReducedMotion.current
    val infiniteTransition = rememberInfiniteTransition(label = "terrainSway")

    val swayOffsetY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "swayOffsetY"
    )

    val swayAngle by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "swayAngle"
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val canvasWidthDp = maxWidth.value
        val spacingDp = nodeVerticalSpacing.value
        val topPadDp = topPadding.value

        val props = generateTerrainProps(nodeCount, spacingDp, canvasWidthDp, topPadDp)

        props.forEachIndexed { index, prop ->
            val propSizeDp = (76 * prop.scale).dp
            val phaseFactor = if (index % 2 == 0) 1f else -1f
            val isFloatingProp = prop.type == TerrainPropType.PALM_TREE || prop.type == TerrainPropType.PAPER_AIRPLANE

            Image(
                painter = rememberAssetPainter(prop.type.assetPath),
                contentDescription = "Map Prop ${prop.type.name}",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset(x = prop.offsetDp.x.dp, y = prop.offsetDp.y.dp)
                    .size(propSizeDp)
                    .graphicsLayer {
                        if (!isReducedMotion) {
                            if (isFloatingProp) {
                                translationY = swayOffsetY * phaseFactor * 1.8f
                                rotationZ = swayAngle * phaseFactor * 1.2f
                            } else {
                                rotationZ = swayAngle * phaseFactor * 0.8f
                            }
                        }
                    }
            )
        }
    }
}
