package com.playit.app.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onStartClick: () -> Unit = {}
) {
    // Breathing scale animation
    val infiniteTransition = rememberInfiniteTransition(label = "SplashBreatheAnim")
    val breatheScaleY by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreatheScaleY"
    )
    val breatheScaleX by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreatheScaleX"
    )

    // Eye blinking cycle state (0f = open, 1f = closed)
    val blinkProgress = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            delay(3600L)
            // Rapid natural blink
            blinkProgress.animateTo(1f, animationSpec = tween(120, easing = LinearEasing))
            delay(80L)
            blinkProgress.animateTo(0f, animationSpec = tween(120, easing = LinearEasing))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
    ) {
        // Main Screen Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // PlayIT Wordmark
            Text(
                text = "PlayIT",
                fontFamily = LexendFontFamily,
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Ink,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pediatric Value-Proposition Tagline
            Text(
                text = "Mabuhay! Ready to learn to read?",
                fontFamily = LexendFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = InkSoft,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Interactive Tarsier Mascot Dome with Breathing and Blinking
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .graphicsLayer {
                        scaleY = breatheScaleY
                        scaleX = breatheScaleX
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        coroutineScope.launch {
                            // Interactive tap response (wink)
                            blinkProgress.snapTo(1f)
                            delay(450L)
                            blinkProgress.animateTo(0f, animationSpec = tween(180))
                        }
                    },
                contentAlignment = Alignment.BottomCenter
            ) {
                HeadspaceTarsierCanvas(
                    blinkProgress = blinkProgress.value,
                    modifier = Modifier.fillMaxSize()
                )

                // Pinned 64dp Primary Action Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    GummyButton(
                        text = "Simulan Natin • Start",
                        onClick = onStartClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )
                }
            }
        }
    }
}

/**
 * High-performance, GPU-rendered 2D vector Canvas representing Lily the Tarsier
 * in Headspace's iconic minimalist dome aesthetic.
 */
@Composable
private fun HeadspaceTarsierCanvas(
    blinkProgress: Float,
    modifier: Modifier = Modifier
) {
    val mangoColor = Color(0xFFFA7B28)
    val earInnerColor = Color(0xFFFFAF78)
    val creamPatchColor = Color(0xFFFFEBCD)
    val lineDarkColor = Color(0xFF2D373E)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val domeCenterX = width / 2f
        val domeTopY = height * 0.28f
        val domeRadiusX = width * 0.76f
        val domeRadiusY = height * 0.72f

        // 1. Draw Tarsier Ears
        val earW = width * 0.16f
        val earH = width * 0.18f
        val leftEarCenter = Offset(width * 0.22f, domeTopY + 30f)
        val rightEarCenter = Offset(width * 0.78f, domeTopY + 30f)

        // Left Ear
        drawOval(
            color = mangoColor,
            topLeft = Offset(leftEarCenter.x - earW, leftEarCenter.y - earH),
            size = Size(earW * 2, earH * 2)
        )
        drawOval(
            color = earInnerColor,
            topLeft = Offset(leftEarCenter.x - earW * 0.62f, leftEarCenter.y - earH * 0.65f),
            size = Size(earW * 1.24f, earH * 1.3f)
        )

        // Right Ear
        drawOval(
            color = mangoColor,
            topLeft = Offset(rightEarCenter.x - earW, rightEarCenter.y - earH),
            size = Size(earW * 2, earH * 2)
        )
        drawOval(
            color = earInnerColor,
            topLeft = Offset(rightEarCenter.x - earW * 0.62f, rightEarCenter.y - earH * 0.65f),
            size = Size(earW * 1.24f, earH * 1.3f)
        )

        // 2. Draw Main Head Dome
        drawOval(
            color = mangoColor,
            topLeft = Offset(domeCenterX - domeRadiusX, domeTopY),
            size = Size(domeRadiusX * 2, domeRadiusY * 2)
        )

        // 3. Draw Eye Patches (Soft Cream)
        val patchY = domeTopY + (height * 0.25f)
        val patchSpacing = width * 0.13f
        val patchRadius = width * 0.09f

        drawCircle(
            color = creamPatchColor,
            radius = patchRadius,
            center = Offset(domeCenterX - patchSpacing, patchY)
        )
        drawCircle(
            color = creamPatchColor,
            radius = patchRadius,
            center = Offset(domeCenterX + patchSpacing, patchY)
        )

        // 4. Draw Eyes (Interpolating between Open and Closed)
        val strokeWidth = 8.dp.toPx()
        val openEyeOpacity = (1f - blinkProgress).coerceIn(0f, 1f)
        val closedEyeOpacity = blinkProgress.coerceIn(0f, 1f)

        if (openEyeOpacity > 0f) {
            val pupilRadius = width * 0.052f * openEyeOpacity
            val catchlightRadius = pupilRadius * 0.35f

            // Left Pupil
            drawCircle(
                color = lineDarkColor.copy(alpha = openEyeOpacity),
                radius = pupilRadius,
                center = Offset(domeCenterX - patchSpacing, patchY)
            )
            // Left Catchlight
            drawCircle(
                color = Color.White.copy(alpha = openEyeOpacity),
                radius = catchlightRadius,
                center = Offset(domeCenterX - patchSpacing - pupilRadius * 0.3f, patchY - pupilRadius * 0.3f)
            )

            // Right Pupil
            drawCircle(
                color = lineDarkColor.copy(alpha = openEyeOpacity),
                radius = pupilRadius,
                center = Offset(domeCenterX + patchSpacing, patchY)
            )
            // Right Catchlight
            drawCircle(
                color = Color.White.copy(alpha = openEyeOpacity),
                radius = catchlightRadius,
                center = Offset(domeCenterX + patchSpacing - pupilRadius * 0.3f, patchY - pupilRadius * 0.3f)
            )
        }

        if (closedEyeOpacity > 0f) {
            val eyeArcW = width * 0.11f
            val eyeArcH = width * 0.05f

            // Left Closed Arc
            val leftEyePath = Path().apply {
                arcTo(
                    rect = Rect(
                        left = domeCenterX - patchSpacing - eyeArcW / 2,
                        top = patchY - eyeArcH / 2,
                        right = domeCenterX - patchSpacing + eyeArcW / 2,
                        bottom = patchY + eyeArcH / 2
                    ),
                    startAngleDegrees = 10f,
                    sweepAngleDegrees = 160f,
                    forceMoveTo = false
                )
            }
            drawPath(
                path = leftEyePath,
                color = lineDarkColor.copy(alpha = closedEyeOpacity),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Right Closed Arc
            val rightEyePath = Path().apply {
                arcTo(
                    rect = Rect(
                        left = domeCenterX + patchSpacing - eyeArcW / 2,
                        top = patchY - eyeArcH / 2,
                        right = domeCenterX + patchSpacing + eyeArcW / 2,
                        bottom = patchY + eyeArcH / 2
                    ),
                    startAngleDegrees = 10f,
                    sweepAngleDegrees = 160f,
                    forceMoveTo = false
                )
            }
            drawPath(
                path = rightEyePath,
                color = lineDarkColor.copy(alpha = closedEyeOpacity),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // 5. Draw Gentle Smile Arc
        val mouthY = patchY + (height * 0.11f)
        val mouthW = width * 0.16f
        val mouthH = width * 0.06f

        val mouthPath = Path().apply {
            arcTo(
                rect = Rect(
                    left = domeCenterX - mouthW / 2,
                    top = mouthY - mouthH / 2,
                    right = domeCenterX + mouthW / 2,
                    bottom = mouthY + mouthH / 2
                ),
                startAngleDegrees = 20f,
                sweepAngleDegrees = 140f,
                forceMoveTo = false
            )
        }
        drawPath(
            path = mouthPath,
            color = lineDarkColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}
