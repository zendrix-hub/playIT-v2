package com.playit.app.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.AchievementGold
import com.playit.app.presentation.theme.AchievementGoldShadow
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.CreamWhiteShadow
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.EnergyOrange
import com.playit.app.presentation.theme.EnergyOrangeShadow
import com.playit.app.presentation.theme.FriendlyPurple
import com.playit.app.presentation.theme.FriendlyPurpleShadow
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.GentleCorrectionOrangeShadow
import com.playit.app.presentation.theme.GrowthGreen
import com.playit.app.presentation.theme.GrowthGreenShadow
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.LearningBlueShadow
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.SoftSkyShadow
import com.playit.app.presentation.theme.TextPrimary

/**
 * Playful Mascot Prompt Speech Bubble rendering Lily the Tarsier with 3D Gummy containers and tap response.
 */
@Composable
fun MascotBubble(
    message: String,
    modifier: Modifier = Modifier,
    mascotState: MascotState = MascotState.IDLE,
    backgroundColor: Color = CreamWhite,
    onMascotTap: (() -> Unit)? = null
) {
    var tapTrigger by remember { mutableStateOf(0) }
    val tapBounceScale by animateFloatAsState(
        targetValue = if (tapTrigger % 2 == 1) 1.22f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = {
            if (tapTrigger % 2 == 1) tapTrigger++
        },
        label = "mascotTapBounce"
    )

    GummyStaticContainer(
        modifier = modifier.fillMaxWidth(),
        faceColor = backgroundColor,
        shadowColor = if (backgroundColor == CreamWhite) CreamWhiteShadow else backgroundColor,
        shape = RoundedCornerShape(22.dp),
        depthHeight = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GummyContainer(
                onClick = {
                    tapTrigger++
                    onMascotTap?.invoke()
                },
                modifier = Modifier
                    .size(62.dp)
                    .graphicsLayer {
                        scaleX = tapBounceScale
                        scaleY = tapBounceScale
                    }
                    .idleBounce(enabled = true),
                faceColor = SoftSky,
                shadowColor = SoftSkyShadow,
                shape = CircleShape,
                depthHeight = 3.dp
            ) {
                androidx.compose.foundation.Image(
                    painter = rememberAssetPainter(mascotState.assetPath),
                    contentDescription = "Lily the Tarsier",
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = message,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                lineHeight = 23.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Duolingo ABC-inspired Docked Mascot Prompt (occupies ~25-30% height anchored bottom/side
 * with docked speech bubble, hop-in entrance animation, breathing life pulse, and interactive tap bounce).
 */
@Composable
fun DockedMascotWithBubble(
    message: String,
    modifier: Modifier = Modifier,
    mascotState: MascotState = MascotState.IDLE,
    backgroundColor: Color = CreamWhite,
    onMascotTap: (() -> Unit)? = null
) {
    val isReducedMotion = LocalReducedMotion.current
    var hasAppeared by rememberSaveable { mutableStateOf(false) }
    var tapTrigger by remember { mutableStateOf(0) }

    val tapBounceScale by animateFloatAsState(
        targetValue = if (tapTrigger % 2 == 1) 1.2f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = {
            if (tapTrigger % 2 == 1) tapTrigger++
        },
        label = "mascotDockedTapBounce"
    )

    val animOffsetY by animateFloatAsState(
        targetValue = if (hasAppeared) 0f else 120f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "mascotHopIn"
    )

    val animAlpha by animateFloatAsState(
        targetValue = if (hasAppeared) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "mascotFadeIn"
    )

    LaunchedEffect(Unit) {
        hasAppeared = true
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                if (isReducedMotion) {
                    alpha = animAlpha
                } else {
                    translationY = animOffsetY.dp.toPx()
                    alpha = animAlpha
                }
            },
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        // Prominent mascot character face (~25-30% screen presence) with interactive tap response
        GummyContainer(
            onClick = {
                tapTrigger++
                onMascotTap?.invoke()
            },
            modifier = Modifier
                .size(92.dp)
                .graphicsLayer {
                    scaleX = tapBounceScale
                    scaleY = tapBounceScale
                }
                .breathingPulse(enabled = !isReducedMotion),
            faceColor = SoftSky,
            shadowColor = SoftSkyShadow,
            shape = CircleShape,
            depthHeight = 4.dp
        ) {
            androidx.compose.foundation.Image(
                painter = rememberAssetPainter(mascotState.assetPath),
                contentDescription = "Lily the Tarsier",
                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Speech bubble docked directly to mascot
        GummyStaticContainer(
            modifier = Modifier.weight(1f),
            faceColor = backgroundColor,
            shadowColor = if (backgroundColor == CreamWhite) CreamWhiteShadow else backgroundColor,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomEnd = 24.dp, bottomStart = 4.dp),
            depthHeight = 4.dp
        ) {
            Text(
                text = message,
                fontFamily = LexendFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                lineHeight = 32.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * 5-Heart Status Indicator Bar using reward_heart.png asset
 */
@Composable
fun HeartBar(
    currentHearts: Int,
    maxHearts: Int = 5,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = CreamWhite.copy(alpha = 0.9f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(1.5.dp, com.playit.app.presentation.theme.Sky, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxHearts) {
            val isFilled = i <= currentHearts
            androidx.compose.foundation.Image(
                painter = rememberAssetPainter("images/rewards/reward_heart.png"),
                contentDescription = if (isFilled) "Heart Active" else "Heart Empty",
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer { alpha = if (isFilled) 1f else 0.35f }
            )
        }
    }
}

/**
 * Pediatric Star Display Modal using reward_star.png asset. Each star pops in with a
 * staggered bouncy spring (via Modifier.popIn) rather than just appearing, so a 3-star
 * result reads as an escalating little celebration instead of a static readout.
 */
@Composable
fun StarDisplay(
    earnedStars: Int,
    maxStars: Int = 3,
    starSize: Dp = 48.dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isEarned = i <= earnedStars
            androidx.compose.foundation.Image(
                painter = rememberAssetPainter("images/rewards/reward_star.png"),
                contentDescription = if (isEarned) "Star Earned" else "Star Locked",
                modifier = Modifier
                    .size(starSize)
                    .popIn(delayMillis = (i - 1) * 120)
                    .graphicsLayer { alpha = if (isEarned) 1f else 0.3f }
            )
        }
    }
}
