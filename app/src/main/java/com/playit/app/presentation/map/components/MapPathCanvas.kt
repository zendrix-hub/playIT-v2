package com.playit.app.presentation.map.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playit.app.domain.model.MapNode
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.SandDeep

/**
 * Calculates a deterministic horizontal offset for a given node index.
 * Uses a sine wave formula so offsets alternate smoothly left/right.
 */
fun calculateNodeXOffsetDp(index: Int, amplitudeDp: Dp = 50.dp): Dp {
    val factor = kotlin.math.sin(index * 0.85)
    return (factor * amplitudeDp.value).dp
}

/**
 * High-quality, smooth realistic adventure trail drawn behind map nodes:
 * 1. Soft organic ambient ground shadow / trench carve
 * 2. Natural sandy-soil trail bed
 * 3. Realistic tactile braided rope guide with double-twist fiber highlights
 * 4. Natural river/biome stepping stone pavers with soft drop shadows
 * 5. 3D ground pedestals under each node
 */
@Composable
fun MapPathCanvas(
    nodeCenters: List<Offset>,
    nodes: List<MapNode>,
    modifier: Modifier = Modifier
) {
    if (nodeCenters.isEmpty()) return

    Canvas(modifier = modifier) {
        val ropeBraidDash = PathEffect.dashPathEffect(floatArrayOf(11.dp.toPx(), 5.dp.toPx()), 0f)
        val fiberHighlightDash = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 10.dp.toPx()), 2.dp.toPx())

        // ── Step 1: Draw Grounding Pedestals under each Node ─────────────────
        nodeCenters.forEachIndexed { index, center ->
            val node = nodes.getOrNull(index)
            val isBlendIt = node is MapNode.BlendItNode
            val pedestalRadiusX = if (isBlendIt) 74.dp.toPx() else 48.dp.toPx()
            val pedestalRadiusY = if (isBlendIt) 34.dp.toPx() else 24.dp.toPx()

            // Pedestal drop shadow on landscape
            drawOval(
                color = Color(0x301E293B),
                topLeft = Offset(center.x - pedestalRadiusX, center.y - pedestalRadiusY + 8.dp.toPx()),
                size = Size(pedestalRadiusX * 2f, pedestalRadiusY * 2f)
            )

            // Pedestal soil rim / ground paver base
            drawOval(
                color = if (isBlendIt) Color(0xFFF3E8FF) else SandDeep.copy(alpha = 0.80f),
                topLeft = Offset(center.x - pedestalRadiusX, center.y - pedestalRadiusY + 3.dp.toPx()),
                size = Size(pedestalRadiusX * 2f, pedestalRadiusY * 2f)
            )
            drawOval(
                color = DarkBrownOutline.copy(alpha = 0.35f),
                topLeft = Offset(center.x - pedestalRadiusX, center.y - pedestalRadiusY + 3.dp.toPx()),
                size = Size(pedestalRadiusX * 2f, pedestalRadiusY * 2f),
                style = Stroke(width = 2.5.dp.toPx())
            )
        }

        // ── Step 2: Draw Smooth Realistic Connecting Trail ───────────────────
        for (i in 0 until nodeCenters.size - 1) {
            val start = nodeCenters[i]
            val end = nodeCenters[i + 1]
            val dy = end.y - start.y

            val segmentPath = Path().apply {
                moveTo(start.x, start.y)
                cubicTo(
                    start.x, start.y + dy * 0.52f,
                    end.x, end.y - dy * 0.52f,
                    end.x, end.y
                )
            }

            // Layer 1: Ambient Occlusion / Soil Trench Shadow
            drawPath(
                path = segmentPath,
                color = Color(0x2E1F3A3D),
                style = Stroke(
                    width = 18.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Layer 2: Compacted Sandy Earth Path Bed
            drawPath(
                path = segmentPath,
                color = Color(0xFFEBD2A4),
                style = Stroke(
                    width = 13.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Layer 3: Realistic Tactile Coconut Husk Rope Braid (Base Strand)
            drawPath(
                path = segmentPath,
                color = Color(0xFF9E6334),
                style = Stroke(
                    width = 5.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = ropeBraidDash
                )
            )

            // Layer 4: Woven Fiber Highlight (Shimmer Strand)
            drawPath(
                path = segmentPath,
                color = Color(0xFFFFF9E6),
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = fiberHighlightDash
                )
            )

            // Layer 5: Natural Stepping Stone Pavers (at 28%, 50%, and 72% along segment)
            val tPoints = listOf(0.28f, 0.50f, 0.72f)
            tPoints.forEach { t ->
                val u = 1f - t
                val px = u * u * u * start.x + 3 * u * u * t * start.x + 3 * u * t * t * end.x + t * t * t * end.x
                val py = u * u * u * start.y + 3 * u * u * t * (start.y + dy * 0.52f) + 3 * u * t * t * (end.y - dy * 0.52f) + t * t * t * end.y

                // Stepping Stone Drop Shadow
                drawCircle(
                    color = Color(0x351F3A3D),
                    radius = 5.5.dp.toPx(),
                    center = Offset(px, py + 2.dp.toPx())
                )

                // Stepping Stone Face
                drawCircle(
                    color = Color(0xFFFAF7F2),
                    radius = 5.dp.toPx(),
                    center = Offset(px, py)
                )

                // Stepping Stone Continuous Outline
                drawCircle(
                    color = DarkBrownOutline.copy(alpha = 0.45f),
                    radius = 5.dp.toPx(),
                    center = Offset(px, py),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }
    }
}
