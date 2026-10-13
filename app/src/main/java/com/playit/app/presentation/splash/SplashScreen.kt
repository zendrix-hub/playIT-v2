package com.playit.app.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.*

@Composable
fun SplashScreen(
    onStartClick: () -> Unit = {}
) {
    val isReducedMotion = LocalReducedMotion.current

    // Breathing scale animation (disabled when reduced motion is on)
    val infiniteTransition = rememberInfiniteTransition(label = "SplashBreatheAnim")
    val breatheScaleY by if (!isReducedMotion) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.035f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "BreatheScaleY"
        )
    } else {
        remember { mutableFloatStateOf(1.0f) }
    }
    val breatheScaleX by if (!isReducedMotion) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.015f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "BreatheScaleX"
        )
    } else {
        remember { mutableFloatStateOf(1.0f) }
    }

    // Tap bounce state
    var tapTrigger by remember { mutableIntStateOf(0) }
    val tapBounceScale by animateFloatAsState(
        targetValue = if (!isReducedMotion && tapTrigger % 2 == 1) 1.12f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = {
            if (tapTrigger % 2 == 1) tapTrigger++
        },
        label = "SplashMascotTapBounce"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cloud)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // PlayIT Wordmark
            Text(
                text = "PlayIT",
                style = MaterialTheme.typography.displayLarge,
                maxLines = 1,
                color = Ink,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pediatric Value-Proposition Tagline
            Text(
                text = "Mabuhay! Ready to learn to read?",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 2,
                color = InkSoft,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Lily waving: centred under tagline, at most 280 dp tall
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAssetPainter(MascotState.WAVING.assetPath),
                    contentDescription = "Lily waving",
                    modifier = Modifier
                        .heightIn(max = 280.dp)
                        .aspectRatio(1f, matchHeightConstraintsFirst = true)
                        .graphicsLayer {
                            scaleY = breatheScaleY * tapBounceScale
                            scaleX = breatheScaleX * tapBounceScale
                            transformOrigin = TransformOrigin(0.5f, 1f)
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            tapTrigger++
                        },
                    contentScale = ContentScale.Fit
                )
            }

            // Pinned 64dp Primary Action Button: cleanly placed below Lily
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp)
            ) {
                GummyButton(
                    text = "Start",
                    onClick = onStartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp)
                        .testTag("splash_start")
                )
            }
        }
    }
}
