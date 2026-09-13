package com.playit.app.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.GummyButton
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.*
import kotlinx.coroutines.launch

/**
 * PlayIT Intro & Splash Screen.
 * Features:
 * - Aligned directly with Lily the Tarsier's official master mascot artwork (lily_waving.png).
 * - Luminous warm Headspace color palette with rolling playground hills.
 * - Gentle living breathing animation and interactive tap spring bounce.
 * - Purely English welcoming dialogue and value proposition (strictly zero emojis).
 * - Pinned 64dp primary CTA button with pediatric tactile gummy depth.
 */
@Composable
fun SplashScreen(
    onStartClick: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val isReducedMotion = LocalReducedMotion.current

    // Living breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "SplashBreatheAnim")
    val breatheScaleY by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreatheScaleY"
    )
    val breatheScaleX by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.018f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreatheScaleX"
    )

    // Interactive Mascot Tap Reaction (Spring Hop & Squash-Stretch)
    val tapBounce = remember { Animatable(1f) }
    val onMascotTap: () -> Unit = {
        coroutineScope.launch {
            tapBounce.animateTo(
                targetValue = 1.15f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            tapBounce.animateTo(
                targetValue = 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFDEE), // Luminous sunny cream
                        Color(0xFFFEF3C7), // Soft playful warmth
                        Color(0xFFFDE68A)  // Sunny golden horizon
                    )
                )
            )
    ) {
        // Cheerful rolling playground hills along the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            val hillWidths = listOf(110.dp, 150.dp, 130.dp, 170.dp, 120.dp, 140.dp)
            hillWidths.forEachIndexed { index, width ->
                Box(
                    modifier = Modifier
                        .size(width = width, height = width * 0.55f)
                        .offset(x = if (index == 0) 0.dp else ((-22) * index).dp)
                        .clip(RoundedCornerShape(topStartPercent = 50, topEndPercent = 50))
                        .background(
                            if (index % 2 == 0) EmeraldLeaf.copy(alpha = 0.25f) else EmeraldLeafDark.copy(alpha = 0.18f)
                        )
                )
            }
        }

        // Main Screen Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // PlayIT Branding Wordmark
            Text(
                text = "PlayIT",
                fontFamily = LexendFontFamily,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Ink,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Pure English Value-Proposition Tagline
            Text(
                text = "Ready to learn to read?",
                fontFamily = LexendFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = InkSoft,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.weight(0.5f))

            // Welcoming Speech Bubble from Lily
            Box(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp,
                            bottomEnd = 24.dp,
                            bottomStart = 8.dp
                        )
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp,
                            bottomEnd = 24.dp,
                            bottomStart = 8.dp
                        )
                    )
                    .background(SurfaceCard)
                    .border(
                        width = 2.5.dp,
                        color = ModernBorder,
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp,
                            bottomEnd = 24.dp,
                            bottomStart = 8.dp
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onMascotTap
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Hi friend! I'm Lily! Let's learn letter sounds together!",
                    fontFamily = LexendFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMidnight,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Mascot Illustration (Lily the Tarsier) with Sun Halo & Living Animation
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .graphicsLayer {
                        val scale = if (isReducedMotion) tapBounce.value else (tapBounce.value * breatheScaleX)
                        val scaleYVal = if (isReducedMotion) tapBounce.value else (tapBounce.value * breatheScaleY)
                        scaleX = scale
                        scaleY = scaleYVal
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onMascotTap
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Warm Luminous Sun Halo Backdrop
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.85f),
                                    Color(0xFFFEF3C7).copy(alpha = 0.65f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            color = Color.White.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                )

                // Master Lily Waving Artwork
                Image(
                    painter = rememberAssetPainter("images/mascot/lily_waving.png"),
                    contentDescription = "Lily the Tarsier welcoming you",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Pinned 64dp Primary Action Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                GummyButton(
                    text = "Start Playing",
                    onClick = onStartClick,
                    backgroundColor = EmeraldLeaf,
                    shadowColor = EmeraldLeafShadow,
                    contentColor = Color.White,
                    fontSize = 24,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                )
            }
        }
    }
}
