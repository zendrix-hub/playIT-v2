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
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.SoftSkyShadow
import com.playit.app.presentation.theme.TextPrimary

/**
 * High-fidelity 3D Gummy Pediatric Button with 64dp height floor, 32dp corner radius, 
 * 3dp outline, and press-into-depth motion.
 */
@Composable
fun PediatricButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LearningBlue,
    shadowColor: Color = LearningBlueShadow,
    contentColor: Color = CreamWhite,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    fontSize: Int = 20,
    isSquashed: Boolean = false
) {
    val resolvedShadow = when (backgroundColor) {
        LearningBlue -> LearningBlueShadow
        GrowthGreen -> GrowthGreenShadow
        AchievementGold -> AchievementGoldShadow
        EnergyOrange -> EnergyOrangeShadow
        FriendlyPurple -> FriendlyPurpleShadow
        GentleCorrectionOrange -> GentleCorrectionOrangeShadow
        else -> shadowColor
    }

    GummyButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        backgroundColor = backgroundColor,
        shadowColor = resolvedShadow,
        contentColor = contentColor,
        enabled = enabled,
        icon = icon,
        fontSize = fontSize,
        isSquashed = isSquashed
    )
}


/**
 * Playful Mascot Prompt Speech Bubble rendering Lily the Tarsier.
 *
 * NOT refactored to the gummy system in this pass — this screen-refresh session hasn't
 * reached BlendItScreen/MapScreen yet (its only two callers), so its flat Surface chrome
 * is left as-is rather than changed blind. Same fix as DockedMascotWithBubble below is
 * the obvious follow-up once we get to those screens.
 */
@Composable
fun MascotBubble(
    message: String,
    modifier: Modifier = Modifier,
    mascotState: MascotState = MascotState.IDLE,
    mascotEmoji: String? = null,
    backgroundColor: Color = CreamWhite
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(2.dp, SoftSky)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(SoftSky),
                contentAlignment = Alignment.Center
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

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = message,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                lineHeight = 24.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Duolingo ABC-inspired Docked Mascot Prompt (occupies ~25-30% height anchored bottom/side
 * with docked speech bubble and hop-in entrance animation on screen load).
 *
 * Refactored onto GummyStaticContainer (not GummyContainer): the avatar and speech bubble
 * are purely informational, not tappable, so they get the family's non-pressable static
 * variant rather than a fake onClick bolted on to satisfy a pressable-only API.
 */
@Composable
fun DockedMascotWithBubble(
    message: String,
    modifier: Modifier = Modifier,
    mascotState: MascotState = MascotState.IDLE,
    mascotEmoji: String? = null,
    backgroundColor: Color = CreamWhite
) {
    val isReducedMotion = LocalReducedMotion.current
    var hasAppeared by rememberSaveable { mutableStateOf(false) }

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
        // Prominent mascot character face (~25-30% screen presence)
        GummyStaticContainer(
            modifier = Modifier.size(92.dp),
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

        // Speech bubble docked directly to mascot — same asymmetric tail-notch shape as
        // before, just drawn by GummyStaticContainer now instead of a flat Surface.
        GummyStaticContainer(
            modifier = Modifier.weight(1f),
            faceColor = backgroundColor,
            shadowColor = if (backgroundColor == CreamWhite) CreamWhiteShadow else backgroundColor,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomEnd = 24.dp, bottomStart = 4.dp),
            depthHeight = 4.dp
        ) {
            Text(
                text = message,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                lineHeight = 24.sp,
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
            .border(1.5.dp, SoftSky, RoundedCornerShape(16.dp))
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
