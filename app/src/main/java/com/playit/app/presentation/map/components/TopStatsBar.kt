package com.playit.app.presentation.map.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.map.greetingFor
import com.playit.app.presentation.theme.*

/**
 * The map's one header card (card 31e): Lily waving with the greeting, the letters learned, the streak
 * and star pills, and the progress bar. The greeting used to be a second card under this one; folding it
 * in gives the trail the screen. Every text is at least 16 sp. With [onLilyTap] the whole card (at least
 * 64 dp tall) is Lily's tap target and she says her line, as the old greeting chip did.
 */
@Composable
fun TopStatsBar(
    totalStars: Int,
    currentStreak: Int,
    @Suppress("UNUSED_PARAMETER") unlockedBadgesCount: Int,
    lettersCompleted: Int = 0,
    biomeTheme: BiomeTheme = BiomeThemes.SECTION_1,
    modifier: Modifier = Modifier,
    profileName: String = "",
    onLilyTap: (() -> Unit)? = null
) {
    val greeting = greetingFor(profileName)

    // Smooth color animation that adapts to the currently visible section on scroll
    val animatedBg by animateColorAsState(
        targetValue = biomeTheme.backgroundTint.copy(alpha = 0.95f),
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "statsBgAnim"
    )
    val animatedBorder by animateColorAsState(
        targetValue = biomeTheme.borderTint,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "statsBorderAnim"
    )
    val animatedPillBg by animateColorAsState(
        targetValue = biomeTheme.pillBg,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "statsPillBgAnim"
    )
    val animatedProgressColor by animateColorAsState(
        targetValue = biomeTheme.progressColor,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "statsProgressAnim"
    )

    val tapModifier = if (onLilyTap != null) {
        Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onLilyTap
            )
            .semantics(mergeDescendants = true) { contentDescription = "Lily says: $greeting Tap to hear her." }
    } else Modifier

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .background(color = animatedBg, shape = RoundedCornerShape(20.dp))
            .border(width = 1.5.dp, color = animatedBorder, shape = RoundedCornerShape(20.dp))
            .then(tapModifier)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("stats_card"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Lily waving and the greeting; a 16-character name wraps to a second line rather than being cut.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SoftSky)
                    .border(1.5.dp, ModernBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAssetPainter(MascotState.WAVING.assetPath),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                )
            }
            Text(
                text = greeting,
                color = TextMidnight,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontFamily = LexendFontFamily,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .testTag("stats_greeting")
            )
        }

        // Letters learned ("5/26": at 16 sp "5 of 26 letters" did not fit beside the pills on 360 dp),
        // then the streak and star pills. TalkBack still reads the full phrase.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$lettersCompleted/26",
                fontSize = 16.sp,
                fontFamily = LexendFontFamily,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .semantics { contentDescription = "$lettersCompleted of 26 letters learned" }
                    .testTag("stats_letters")
            )
            Row(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .testTag("stats_pills"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatPill(
                    count = currentStreak,
                    assetPath = "images/rewards/reward_streak.png",
                    backgroundColor = animatedPillBg,
                    textColor = biomeTheme.textSecondary
                )
                StatPill(
                    count = totalStars,
                    assetPath = "images/rewards/reward_star.png",
                    backgroundColor = animatedPillBg,
                    textColor = biomeTheme.textPrimary
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .background(Color(0xFFE2E8F0), RoundedCornerShape(999.dp))
                .clip(RoundedCornerShape(999.dp))
        ) {
            val progress = (lettersCompleted.toFloat() / 26f).coerceIn(0f, 1f)
            if (progress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress)
                        .fillMaxHeight()
                        .background(animatedProgressColor)
                )
            }
        }
    }
}

@Composable
private fun StatPill(
    count: Int,
    assetPath: String,
    backgroundColor: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Image(
            painter = rememberAssetPainter(assetPath),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "$count",
            fontSize = 16.sp,
            fontFamily = LexendFontFamily,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}
