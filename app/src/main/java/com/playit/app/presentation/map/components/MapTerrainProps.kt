package com.playit.app.presentation.map.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.components.rememberAssetPainter

enum class TerrainPropType(val assetPath: String) {
    BACKPACK("images/backgrounds/mapprop_backpack.png"),
    BOOK_STACK("images/backgrounds/mapprop_book_stack.png"),
    CRAYON_BRIDGE("images/backgrounds/mapprop_crayon_bridge.png"),
    ERASER_SHRUB("images/backgrounds/mapprop_eraser_shrub.png"),
    GLOBE("images/backgrounds/mapprop_globe.png"),
    MAGNIFYING_GLASS("images/backgrounds/mapprop_magnifying_glass.png"),
    PAINT_PALETTE("images/backgrounds/mapprop_paint_palette.png"),
    PAPER_AIRPLANE("images/backgrounds/mapprop_paper_airplane.png"),
    PENCIL_TOWER("images/backgrounds/mapprop_pencil_tower.png"),
    RULER_RAMP("images/backgrounds/mapprop_ruler_ramp.png"),
    BUSH("images/backgrounds/map_prop_bush.png"),
    ROCK("images/backgrounds/map_prop_rock.png"),
    TREE_SMALL("images/backgrounds/map_prop_tree_small.png")
}

data class PlacedTerrainProp(
    val type: TerrainPropType,
    val offsetDp: Offset,
    val scale: Float = 1.0f
)

/**
 * Calculates seeded terrain prop positions along the map based on total node count.
 */
fun generateTerrainProps(
    nodeCount: Int,
    nodeVerticalSpacingDp: Float,
    canvasWidthDp: Float,
    topPaddingDp: Float
): List<PlacedTerrainProp> {
    if (nodeCount <= 0 || canvasWidthDp <= 0f) return emptyList()

    val props = mutableListOf<PlacedTerrainProp>()
    val propTypes = TerrainPropType.values()

    for (i in 0 until nodeCount) {
        val nodeY = topPaddingDp + i * nodeVerticalSpacingDp
        val nodeXOffset = calculateNodeXOffsetDp(i, 50.dp).value
        val centerX = canvasWidthDp / 2f
        val pathX = centerX + nodeXOffset

        val isLeft = (i % 2 == 0)
        val propX = if (isLeft) {
            (pathX - 110f).coerceAtLeast(12f)
        } else {
            (pathX + 55f).coerceAtMost(canvasWidthDp - 80f)
        }
        val propY = nodeY + (if (i % 3 == 0) -20f else 25f)
        val propType = propTypes[(i * 3 + 1) % propTypes.size]

        props.add(
            PlacedTerrainProp(
                type = propType,
                offsetDp = Offset(propX, propY),
                scale = 0.85f + (i % 3) * 0.12f
            )
        )
    }

    return props
}

/**
 * Renders background terrain elements and foliage clusters scattered behind the map path using PNG assets.
 */
@Composable
fun MapTerrainProps(
    nodeCount: Int,
    nodeVerticalSpacing: Dp,
    topPadding: Dp,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val canvasWidthDp = maxWidth.value
        val spacingDp = nodeVerticalSpacing.value
        val topPadDp = topPadding.value

        val props = generateTerrainProps(nodeCount, spacingDp, canvasWidthDp, topPadDp)

        props.forEach { prop ->
            val propSizeDp = (72 * prop.scale).dp
            Image(
                painter = rememberAssetPainter(prop.type.assetPath),
                contentDescription = "Map Prop ${prop.type.name}",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset(x = prop.offsetDp.x.dp, y = prop.offsetDp.y.dp)
                    .size(propSizeDp)
            )
        }
    }
}
