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
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.EmeraldLeaf

/**
 * Calculates a deterministic horizontal offset for a given node index.
 * Uses a sine wave formula so offsets alternate smoothly left/right in Duolingo's signature zigzag.
 */
fun calculateNodeXOffsetDp(index: Int, amplitudeDp: Dp = 54.dp): Dp {
    val factor = kotlin.math.sin(index * 0.85)
    return (factor * amplitudeDp.value).dp
}

/** Rope colour of the trail ahead; walked segments are solid green. */
private val RopeTan = Color(0xFFC9A66B)

/**
 * Rope trail between the map nodes: a 10 dp tan rope with a 3 dp dark-brown edge, rounded caps,
 * dashed 18/12 dp. A segment the child has walked (its end node is unlocked) is solid EmeraldLeaf,
 * so progress reads by colour and by the dash disappearing.
 */
@Composable
fun MapPathCanvas(
    nodeCenters: List<Offset>,
    nodes: List<MapNode>,
    modifier: Modifier = Modifier
) {
    if (nodeCenters.size < 2) return

    Canvas(modifier = modifier) {
        val rope = 10.dp.toPx()
        val edge = 3.dp.toPx()
        val dash = PathEffect.dashPathEffect(floatArrayOf(18.dp.toPx(), 12.dp.toPx()), 0f)

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

            val walked = nodes.getOrNull(i + 1)?.isUnlocked == true
            val effect = if (walked) null else dash
            // Dark-brown edge under the rope, then the rope itself
            drawPath(
                path = segmentPath,
                color = DarkBrownOutline,
                style = Stroke(width = rope + edge * 2, cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = effect)
            )
            drawPath(
                path = segmentPath,
                color = if (walked) EmeraldLeaf else RopeTan,
                style = Stroke(width = rope, cap = StrokeCap.Round, join = StrokeJoin.Round, pathEffect = effect)
            )
        }
    }
}
