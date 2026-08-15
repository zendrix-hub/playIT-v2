package com.playit.app.presentation.map.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playit.app.domain.model.MapNode
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DisabledColor
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.TextPrimary

/**
 * Calculates a deterministic horizontal offset for a given node index.
 * Uses a sine wave formula so offsets alternate smoothly left/right.
 */
fun calculateNodeXOffsetDp(index: Int, amplitudeDp: Dp = 50.dp): Dp {
    val factor = kotlin.math.sin(index * 0.85)
    return (factor * amplitudeDp.value).dp
}

/**
 * Canvas drawing the winding Bezier path behind map nodes.
 *
 * Renders each segment between node `i` and `i+1` with:
 * 1. An outer dark outline stroke (`TextPrimary`, 20dp wide)
 * 2. An inner path stroke (12dp wide, `GrowthGreen` for unlocked, `DisabledColor` for locked)
 * 3. An inner dashed accent line for extra texture
 */
@Composable
fun MapPathCanvas(
    nodeCenters: List<Offset>,
    nodes: List<MapNode>,
    modifier: Modifier = Modifier
) {
    if (nodeCenters.size < 2) return

    Canvas(modifier = modifier) {
        val outerStrokePx = 20.dp.toPx()
        val innerStrokePx = 12.dp.toPx()
        val dashStrokePx = 3.dp.toPx()

        for (i in 0 until nodeCenters.size - 1) {
            val start = nodeCenters[i]
            val end = nodeCenters[i + 1]
            val targetNode = nodes.getOrNull(i + 1)
            val isSegmentUnlocked = targetNode?.isUnlocked ?: false

            val dy = end.y - start.y
            val segmentPath = Path().apply {
                moveTo(start.x, start.y)
                cubicTo(
                    start.x, start.y + dy * 0.5f,
                    end.x, end.y - dy * 0.5f,
                    end.x, end.y
                )
            }

            // Layer 1: Outer dark outline
            drawPath(
                path = segmentPath,
                color = TextPrimary,
                style = Stroke(
                    width = outerStrokePx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Layer 2: Inner color stroke
            val segmentColor = if (isSegmentUnlocked) GrowthGreen else DisabledColor
            drawPath(
                path = segmentPath,
                color = segmentColor,
                style = Stroke(
                    width = innerStrokePx,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Layer 3: Dashed centerline decoration
            if (isSegmentUnlocked) {
                drawPath(
                    path = segmentPath,
                    color = CreamWhite.copy(alpha = 0.6f),
                    style = Stroke(
                        width = dashStrokePx,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12.dp.toPx(), 12.dp.toPx()), 0f)
                    )
                )
            }
        }
    }
}
