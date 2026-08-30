package com.playit.app.presentation.map.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playit.app.domain.model.MapNode

/**
 * Calculates a deterministic horizontal offset for a given node index.
 * Uses a sine wave formula so offsets alternate smoothly left/right in Duolingo's signature zigzag.
 */
fun calculateNodeXOffsetDp(index: Int, amplitudeDp: Dp = 54.dp): Dp {
    val factor = kotlin.math.sin(index * 0.85)
    return (factor * amplitudeDp.value).dp
}

/**
 * Duolingo-style Clean Map Trail:
 * In modern Duolingo (as in duoling_map_sample.jpg), the map background is ultra-clean and light.
 * The connection between nodes is either clean white space or subtle, elegant stepping-stone dots
 * that let the 3D tactile nodes and mascot characters stand out without heavy visual clutter.
 */
@Composable
fun MapPathCanvas(
    nodeCenters: List<Offset>,
    nodes: List<MapNode>,
    modifier: Modifier = Modifier
) {
    if (nodeCenters.size < 2) return

    Canvas(modifier = modifier) {
        val subtleDash = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 10.dp.toPx()), 0f)

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

            // Subtle, light dashed guide line connecting the nodes
            drawPath(
                path = segmentPath,
                color = Color(0x22CBD5E1),
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = subtleDash
                )
            )

            // Minimalist stepping-stone dots between nodes (at 30%, 50%, 70%)
            val tPoints = listOf(0.30f, 0.50f, 0.70f)
            tPoints.forEach { t ->
                val u = 1f - t
                val px = u * u * u * start.x + 3 * u * u * t * start.x + 3 * u * t * t * end.x + t * t * t * end.x
                val py = u * u * u * start.y + 3 * u * u * t * (start.y + dy * 0.52f) + 3 * u * t * t * (end.y - dy * 0.52f) + t * t * t * end.y

                // Clean soft stone dot
                drawCircle(
                    color = Color(0xFFE2E8F0),
                    radius = 3.5.dp.toPx(),
                    center = Offset(px, py)
                )
            }
        }
    }
}
