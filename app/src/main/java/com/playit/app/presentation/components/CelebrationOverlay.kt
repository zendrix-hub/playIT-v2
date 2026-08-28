package com.playit.app.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class CelebrationType {
    CONFETTI,
    STAR_BURST,
    SPARKLE
}

@Composable
fun CelebrationOverlay(
    type: CelebrationType,
    isPlaying: Boolean,
    onFinished: () -> Unit = {},
    modifier: Modifier = Modifier,
    colors: List<Color>? = null
) {
    if (!isPlaying) return

    val reducedMotion = LocalReducedMotion.current

    if (reducedMotion) {
        ReducedMotionCelebration(type = type, onFinished = onFinished, modifier = modifier)
    } else {
        FullMotionCelebration(type = type, onFinished = onFinished, modifier = modifier, colors = colors)
    }
}

@Composable
private fun ReducedMotionCelebration(
    type: CelebrationType,
    onFinished: () -> Unit,
    modifier: Modifier
) {
    val animAlpha = remember { Animatable(0f) }

    LaunchedEffect(type) {
        animAlpha.animateTo(1f, animationSpec = tween(300))
        delay(1000)
        animAlpha.animateTo(0f, animationSpec = tween(300))
        onFinished()
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (type == CelebrationType.CONFETTI || type == CelebrationType.STAR_BURST) {
            Image(
                painter = rememberAssetPainter("images/rewards/reward_star.png"),
                contentDescription = "Celebration star",
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer { alpha = animAlpha.value }
            )
        } else {
            // SPARKLE gold glow
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = AchievementGold.copy(alpha = animAlpha.value * 0.3f),
                    radius = size.minDimension / 3
                )
            }
        }
    }
}

@Composable
private fun FullMotionCelebration(
    type: CelebrationType,
    onFinished: () -> Unit,
    modifier: Modifier,
    colors: List<Color>? = null
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(type) {
        val duration = when (type) {
            CelebrationType.CONFETTI -> 2000
            CelebrationType.STAR_BURST -> 1500
            CelebrationType.SPARKLE -> 1000
        }
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = duration, easing = LinearEasing)
        )
        onFinished()
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (type) {
            CelebrationType.CONFETTI -> ConfettiCanvas(progress.value, colors)
            CelebrationType.STAR_BURST -> StarBurstCanvas(progress.value)
            CelebrationType.SPARKLE -> SparkleCanvas(progress.value)
        }
    }
}

@Composable
private fun ConfettiCanvas(progress: Float, customColors: List<Color>? = null) {
    val colors = customColors ?: listOf(
        Mango, Leaf, Ube, Guava, Cloud
    )
    val particles = remember {
        List(40) {
            ConfettiParticle(
                xRange = Random.nextFloat(),
                yOffset = Random.nextFloat() * -0.5f, // Start slightly above
                size = Random.nextFloat() * 6 + 4, // 4-10
                color = colors.random(),
                isCircle = Random.nextBoolean(),
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                fallSpeed = Random.nextFloat() * 1.5f + 0.5f,
                drift = (Random.nextFloat() - 0.5f) * 0.5f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEach { p ->
            val startX = p.xRange * canvasWidth
            val startY = p.yOffset * canvasHeight
            
            // Progress goes 0 to 1
            // Distance fallen
            val currentY = startY + (canvasHeight * 1.5f * p.fallSpeed * progress)
            val currentX = startX + (canvasWidth * p.drift * progress)
            val currentRotation = p.rotationSpeed * progress

            if (currentY < canvasHeight + 100.dp.toPx()) {
                withTransform({
                    translate(left = currentX, top = currentY)
                    rotate(degrees = currentRotation)
                }) {
                    if (p.isCircle) {
                        drawCircle(
                            color = p.color,
                            radius = p.size.dp.toPx() / 2f
                        )
                    } else {
                        drawRect(
                            color = p.color,
                            size = Size(p.size.dp.toPx(), (p.size * 0.6f).dp.toPx())
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StarBurstCanvas(progress: Float) {
    val stars = remember {
        List(6) {
            val angle = (it * (360f / 6)) + Random.nextFloat() * 20f
            val rad = angle * PI / 180f
            StarParticle(
                angleRad = rad.toFloat(),
                targetDist = Random.nextFloat() * 0.3f + 0.3f, // 30-60% of screen
                size = Random.nextFloat() * 10 + 20, // 20-30dp
                rotationSpeed = (Random.nextFloat() - 0.5f) * 360f
            )
        }
    }
    
    // Scale goes 0 to 1 with a spring-like bounce simulated manually or mapped from progress
    // Progress 0..0.2: scale 0->1.2
    // Progress 0.2..0.4: scale 1.2->1.0
    // Progress 0.8..1.0: fade out
    val scale = if (progress < 0.2f) {
        progress / 0.2f * 1.2f
    } else if (progress < 0.4f) {
        1.2f - ((progress - 0.2f) / 0.2f) * 0.2f
    } else {
        1f
    }
    
    val alpha = if (progress > 0.8f) {
        1f - ((progress - 0.8f) / 0.2f)
    } else {
        1f
    }

    // Distance multiplier
    val distMult = if (progress < 0.4f) {
        progress / 0.4f
    } else {
        1f + (progress - 0.4f) * 0.2f // slow drift outward
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxDist = minOf(size.width, size.height)

        stars.forEach { s ->
            val dist = s.targetDist * maxDist * distMult
            val px = cx + cos(s.angleRad) * dist
            val py = cy + sin(s.angleRad) * dist
            val rot = s.rotationSpeed * progress

            withTransform({
                translate(left = px, top = py)
                rotate(degrees = rot)
                scale(scale, scale)
            }) {
                drawStar(
                    color = Mango.copy(alpha = alpha),
                    radius = s.size.dp.toPx()
                )
            }
        }
    }
}

@Composable
private fun SparkleCanvas(progress: Float) {
    val colors = listOf(Mango, CreamWhite)
    val sparkles = remember {
        List(10) {
            SparkleParticle(
                xRange = Random.nextFloat(),
                yRange = Random.nextFloat(),
                size = Random.nextFloat() * 6 + 4,
                color = colors.random(),
                delayProgress = Random.nextFloat() * 0.5f,
                durationProgress = Random.nextFloat() * 0.3f + 0.2f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        sparkles.forEach { s ->
            // Active window: [delay, delay + duration]
            if (progress >= s.delayProgress && progress <= s.delayProgress + s.durationProgress) {
                val localProgress = (progress - s.delayProgress) / s.durationProgress
                // scale in 0..0.3, hold 0.3..0.7, scale out 0.7..1.0
                val scale = if (localProgress < 0.3f) {
                    localProgress / 0.3f
                } else if (localProgress > 0.7f) {
                    1f - (localProgress - 0.7f) / 0.3f
                } else {
                    1f
                }

                val px = s.xRange * size.width
                val py = s.yRange * size.height

                withTransform({
                    translate(left = px, top = py)
                    scale(scale, scale)
                    rotate(degrees = localProgress * 90f)
                }) {
                    drawStar(
                        color = s.color,
                        radius = s.size.dp.toPx(),
                        points = 4,
                        innerRatio = 0.2f
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawStar(color: Color, radius: Float, points: Int = 5, innerRatio: Float = 0.4f) {
    val path = Path()
    val angle = PI / points
    
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) radius else radius * innerRatio
        val a = i * angle - PI / 2f
        val x = (cos(a) * r).toFloat()
        val y = (sin(a) * r).toFloat()
        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()
    drawPath(path = path, color = color)
}

private data class ConfettiParticle(
    val xRange: Float,
    val yOffset: Float,
    val size: Float,
    val color: Color,
    val isCircle: Boolean,
    val rotationSpeed: Float,
    val fallSpeed: Float,
    val drift: Float
)

private data class StarParticle(
    val angleRad: Float,
    val targetDist: Float,
    val size: Float,
    val rotationSpeed: Float
)

private data class SparkleParticle(
    val xRange: Float,
    val yRange: Float,
    val size: Float,
    val color: Color,
    val delayProgress: Float,
    val durationProgress: Float
)
