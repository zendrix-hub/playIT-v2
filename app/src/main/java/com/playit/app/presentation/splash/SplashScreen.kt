package com.playit.app.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.AchievementGold
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.SoftSky

@Composable
fun SplashScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val mascotScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascotPulse"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Edge-to-edge production background asset
        androidx.compose.foundation.Image(
            painter = com.playit.app.presentation.components.rememberAssetPainter("images/backgrounds/bg_splash_screen.png"),
            contentDescription = "Splash Background",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Mascot Graphic Container
            Box(
                modifier = Modifier
                    .scale(mascotScale)
                    .size(140.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(CreamWhite),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = com.playit.app.presentation.components.rememberAssetPainter(com.playit.app.presentation.components.MascotState.EXCITED.assetPath),
                    contentDescription = "Lily Tarsier Mascot",
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Title
            Text(
                text = "playIT",
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CreamWhite,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Marungko Reading Adventures",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = AchievementGold
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Animated Loading Badge
            Box(
                modifier = Modifier
                    .background(
                        color = CreamWhite.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Loading fun sound games...",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = CreamWhite
                )
            }
        }
    }
}

