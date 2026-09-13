package com.playit.app.presentation.map.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playit.app.domain.model.MapNode

/**
 * Calculates a deterministic horizontal offset for a given node index.
 * Uses a sine wave formula so offsets alternate smoothly left/right in Duolingo's signature zigzag.
 */
fun calculateNodeXOffsetDp(index: Int, amplitudeDp: Dp = 50.dp): Dp {
    val factor = kotlin.math.sin(index * 0.85)
    return (factor * amplitudeDp.value).dp
}

/**
 * Duolingo ABC-inspired Adventure Map Trail:
 * Renders an organic, tactile winding trail connecting the stepping-stone nodes.
 * Features:
 * 1. Warm earthy trail ribbon with depth shadow (visible against Chocolate Hills terrain)
 * 2. Playful dashed center track with dynamic progress coloring (Gold for mastered, Leaf for unlocked, Slate for locked)
 * 3. 3D pebble pavers along the path with drop shadows and top highlights
 */
@Composable
fun MapPathCanvas(
    nodeCenters: List<Offset>,
    nodes: List<MapNode>,
    modifier: Modifier = Modifier
) {
    if (nodeCenters.size < 2) return

    Canvas(modifier = modifier) {
        val ribbonWidth = 20.dp.toPx()
        val ribbonShadowOffset = 4.dp.toPx()
        val centerDashedEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 8.dp.toPx()), 0f)

        for (i in 0 until nodeCenters.size - 1) {
            val start = nodeCenters[i]
            val end = nodeCenters[i + 1]
            val dy = end.y - start.y

            val currentNode = nodes.getOrNull(i)
            val nextNode = nodes.getOrNull(i + 1)

            val isCurrentCompleted = currentNode != null && currentNode.starsEarned > 0
            val isNextCompleted = nextNode != null && nextNode.starsEarned > 0
            val isSegmentCompleted = isCurrentCompleted && isNextCompleted
            val isSegmentUnlocked = (currentNode?.isUnlocked == true) && (nextNode?.isUnlocked == true)

            // Segment Bezier Path
            val segmentPath = Path().apply {
                moveTo(start.x, start.y)
                cubicTo(
                    start.x, start.y + dy * 0.52f,
                    end.x, end.y - dy * 0.52f,
                    end.x, end.y
                )
            }

            // Path 1: Trail Depth Shadow (Cast slightly downward)
            val shadowPath = Path().apply {
                moveTo(start.x, start.y + ribbonShadowOffset)
                cubicTo(
                    start.x, start.y + dy * 0.52f + ribbonShadowOffset,
                    end.x, end.y - dy * 0.52f + ribbonShadowOffset,
                    end.x, end.y + ribbonShadowOffset
                )
            }
            drawPath(
                path = shadowPath,
                color = Color(0x338D6E63),
                style = Stroke(
                    width = ribbonWidth + 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Path 2: Main Warm Earthy Ribbon Trail
            val trailRibbonColor = when {
                isSegmentCompleted -> Color(0xFFFDE68A) // Warm sunny trail for completed sections
                isSegmentUnlocked -> Color(0xFFF5E6CA)  // Soft warm sand for active sections
                else -> Color(0xFFE2E8F0)               // Soft stone slate for locked trail
            }
            drawPath(
                path = segmentPath,
                color = trailRibbonColor,
                style = Stroke(
                    width = ribbonWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Path 3: Trail Border Trim (Subtle outer stroke for crisp cartoon definition)
            val trailBorderColor = when {
                isSegmentCompleted -> Color(0xFFF59E0B).copy(alpha = 0.35f)
                isSegmentUnlocked -> Color(0xFFD4B982).copy(alpha = 0.50f)
                else -> Color(0xFFCBD5E1).copy(alpha = 0.50f)
            }
            drawPath(
                path = segmentPath,
                color = trailBorderColor,
                style = Stroke(
                    width = ribbonWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Path 4: Playful Dashed Center Walking Track
            val trackDashColor = when {
                isSegmentCompleted -> Color(0xFFD97706).copy(alpha = 0.70f)
                isSegmentUnlocked -> Color(0xFFB58A55).copy(alpha = 0.60f)
                else -> Color(0xFF94A3B8).copy(alpha = 0.40f)
            }
            drawPath(
                path = segmentPath,
                color = trackDashColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = centerDashedEffect
                )
            )

            // Path 5: 3D Stepping Stone Pebble Pavers (at 28%, 50%, 72%)
            val tPoints = listOf(0.28f, 0.50f, 0.72f)
            tPoints.forEach { t ->
                val u = 1f - t
                val px = u * u * u * start.x + 3 * u * u * t * start.x + 3 * u * t * t * end.x + t * t * t * end.x
                val py = u * u * u * start.y + 3 * u * u * t * (start.y + dy * 0.52f) + 3 * u * t * t * (end.y - dy * 0.52f) + t * t * t * end.y

                val pebbleRadius = 4.5.dp.toPx()

                // Pebble shadow
                drawCircle(
                    color = Color(0x33000000),
                    radius = pebbleRadius,
                    center = Offset(px, py + 2.dp.toPx())
                )

                // Pebble body
                val pebbleColor = when {
                    isSegmentCompleted -> Color(0xFFFBBF24)
                    isSegmentUnlocked -> Color(0xFFE5D5B8)
                    else -> Color(0xFFCBD5E1)
                }
                drawCircle(
                    color = pebbleColor,
                    radius = pebbleRadius,
                    center = Offset(px, py)
                )

                // Pebble top gleam
                drawCircle(
                    color = Color.White.copy(alpha = 0.65f),
                    radius = pebbleRadius * 0.45f,
                    center = Offset(px - 1.2.dp.toPx(), py - 1.2.dp.toPx())
                )
            }
        }
    }
}

