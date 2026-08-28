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
import com.playit.app.presentation.theme.Rope
import com.playit.app.presentation.theme.RopeShadow
import com.playit.app.presentation.theme.SandDeep
import com.playit.app.presentation.theme.Tan

/**
 * Calculates a deterministic horizontal offset for a given node index.
 * Uses a sine wave formula so offsets alternate smoothly left/right.
 */
fun calculateNodeXOffsetDp(index: Int, amplitudeDp: Dp = 50.dp): Dp {
    val factor = kotlin.math.sin(index * 0.85)
    return (factor * amplitudeDp.value).dp
}

/**
 * Canvas drawing the winding Bezier path behind map nodes with tactile world integration:
 * 1. 3D Node Grounding Pedestals / Island Bases
 * 2. River Wood Bridge Planks (for Chapter 2)
 * 3. Base shadow track and vibrant dashed rope trail
 * 4. Stepping stone pavers
 */
@Composable
fun MapPathCanvas(
    nodeCenters: List<Offset>,
    nodes: List<MapNode>,
    modifier: Modifier = Modifier
) {
    if (nodeCenters.isEmpty()) return

    Canvas(modifier = modifier) {
        val pathDash = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx()), 0f)

        // ── Step 1: Draw Grounding Pedestals under each Node ─────────────────
        nodeCenters.forEachIndexed { index, center ->
            val node = nodes.getOrNull(index)
            val isBlendIt = node is MapNode.BlendItNode
            val pedestalRadiusX = if (isBlendIt) 72.dp.toPx() else 48.dp.toPx()
            val pedestalRadiusY = if (isBlendIt) 32.dp.toPx() else 24.dp.toPx()

            // Pedestal drop shadow on landscape
            drawOval(
                color = Color(0xFF1E293B).copy(alpha = 0.16f),
                topLeft = Offset(center.x - pedestalRadiusX, center.y - pedestalRadiusY + 8.dp.toPx()),
                size = Size(pedestalRadiusX * 2f, pedestalRadiusY * 2f)
            )

            // Pedestal soil rim / ground paver base
            drawOval(
                color = if (isBlendIt) Color(0xFFE9D5FF) else SandDeep.copy(alpha = 0.75f),
                topLeft = Offset(center.x - pedestalRadiusX, center.y - pedestalRadiusY + 3.dp.toPx()),
                size = Size(pedestalRadiusX * 2f, pedestalRadiusY * 2f)
            )
            drawOval(
                color = DarkBrownOutline.copy(alpha = 0.35f),
                topLeft = Offset(center.x - pedestalRadiusX, center.y - pedestalRadiusY + 3.dp.toPx()),
                size = Size(pedestalRadiusX * 2f, pedestalRadiusY * 2f),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // ── Step 2: Draw Connecting Paths & Bridges ───────────────────────────
        for (i in 0 until nodeCenters.size - 1) {
            val start = nodeCenters[i]
            val end = nodeCenters[i + 1]

            val dy = end.y - start.y
            val segmentPath = Path().apply {
                moveTo(start.x, start.y)
                cubicTo(
                    start.x, start.y + dy * 0.5f,
                    end.x, end.y - dy * 0.5f,
                    end.x, end.y
                )
            }

            val isRiverCrossing = i in 6..12

            if (isRiverCrossing) {
                // Wooden Boardwalk Bridge over Loboc River
                drawPath(
                    path = segmentPath,
                    color = DarkBrownOutline.copy(alpha = 0.35f),
                    style = Stroke(
                        width = 16.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                // Bridge deck in Tan
                drawPath(
                    path = segmentPath,
                    color = Tan,
                    style = Stroke(
                        width = 12.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                // Bridge wooden rungs / planks
                val rungsDash = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 7.dp.toPx()), 0f)
                drawPath(
                    path = segmentPath,
                    color = DarkBrownOutline,
                    style = Stroke(
                        width = 14.dp.toPx(),
                        cap = StrokeCap.Butt,
                        join = StrokeJoin.Round,
                        pathEffect = rungsDash
                    )
                )
            } else {
                // Layer 1: Outer shadow track
                drawPath(
                    path = segmentPath,
                    color = RopeShadow.copy(alpha = 0.45f),
                    style = Stroke(
                        width = 9.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Layer 2: Main dashed rope trail
                drawPath(
                    path = segmentPath,
                    color = Rope,
                    style = Stroke(
                        width = 4.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                        pathEffect = pathDash
                    )
                )

                // Layer 3: Tactile Stepping Stones at 1/3 and 2/3 of segment
                val midX1 = start.x * 0.67f + end.x * 0.33f
                val midY1 = start.y + dy * 0.33f
                val midX2 = start.x * 0.33f + end.x * 0.67f
                val midY2 = start.y + dy * 0.67f

                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = 4.dp.toPx(),
                    center = Offset(midX1, midY1)
                )
                drawCircle(
                    color = DarkBrownOutline.copy(alpha = 0.35f),
                    radius = 4.dp.toPx(),
                    center = Offset(midX1, midY1),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = 4.dp.toPx(),
                    center = Offset(midX2, midY2)
                )
                drawCircle(
                    color = DarkBrownOutline.copy(alpha = 0.35f),
                    radius = 4.dp.toPx(),
                    center = Offset(midX2, midY2),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }
    }
}
