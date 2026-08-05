package com.playit.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.AchievementGold
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DestructiveRed
import com.playit.app.presentation.theme.DisabledColor
import com.playit.app.presentation.theme.FriendlyPurple
import com.playit.app.presentation.theme.LearningBlue
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary

/**
 * High-fidelity 3D Tactile Pediatric Button with 64dp height and bounce feedback.
 */
@Composable
fun PediatricButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LearningBlue,
    shadowColor: Color = LearningBlue.copy(alpha = 0.6f),
    contentColor: Color = CreamWhite,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    fontSize: Int = 20
) {
    val effectiveBg = if (enabled) backgroundColor else DisabledColor
    val effectiveShadow = if (enabled) shadowColor else Color.Transparent

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 64.dp)
            .bounceClick(enabled = enabled, onClick = onClick)
            .shadow(
                elevation = if (enabled) 6.dp else 0.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = effectiveShadow
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        effectiveBg,
                        effectiveBg.copy(alpha = 0.85f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 2.dp,
                color = CreamWhite.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Playful Mascot Prompt Speech Bubble
 */
@Composable
fun MascotBubble(
    message: String,
    modifier: Modifier = Modifier,
    mascotEmoji: String = "🦜",
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
                Text(
                    text = mascotEmoji,
                    fontSize = 32.sp
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
 * 5-Heart Status Indicator Bar
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
            Text(
                text = if (isFilled) "❤️" else "🖤",
                fontSize = 22.sp
            )
        }
    }
}

/**
 * Pediatric Star Display Modal
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
            Text(
                text = if (isEarned) "⭐" else "☆",
                fontSize = starSize.value.sp,
                color = if (isEarned) AchievementGold else DisabledColor
            )
        }
    }
}
