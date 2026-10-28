package com.playit.app.presentation.map.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.TalkingLily
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.map.greetingFor
import com.playit.app.presentation.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * The map's one header card (card 31e): Lily waving with the greeting, the letters learned, the streak
 * and star pills, and the progress bar. The greeting used to be a second card under this one; folding it
 * in gives the trail the screen. Every text is at least 16 sp. With [onLilyTap] the whole card (at least
 * 64 dp tall) is Lily's tap target and she says her line, as the old greeting chip did.
 */
@Composable
fun TopStatsBar(
    totalStars: Int,
    starsJustEarned: Int = 0,
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
                TalkingLily(
                    pose = MascotState.WAVING,
                    contentDescription = null,
                    maxSize = 36.dp,
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
                    textColor = biomeTheme.textPrimary,
                    gained = starsJustEarned
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .background(LockedShelf, RoundedCornerShape(999.dp))
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

/** When the stars just earned start counting up on the map, and how long the count takes. */
const val COUNT_UP_DELAY_MS = 600L
const val COUNT_UP_MS = 700

/** The number shown at [progress] 0..1 of a count from [from] up to [to]: one step at a time, [to] at the end. */
fun countUpAt(progress: Float, from: Int, to: Int): Int =
    if (to <= from) to else from + ((to - from) * progress.coerceIn(0f, 1f)).roundToInt()

/** The pill swells to 120 % and back while it counts: 1 at the start and at the end. */
fun countUpScale(progress: Float): Float = 1f + 0.2f * sin(PI.toFloat() * progress.coerceIn(0f, 1f))

/**
 * A count with its icon. When [gained] is above 0 (stars the child just earned, on the unlock moment) it counts
 * up from count - gained to count and swells once, so the child sees the new stars land (design refresh).
 * Under reduced motion it shows the total at once.
 */
@Composable
private fun StatPill(
    count: Int,
    assetPath: String,
    backgroundColor: Color,
    textColor: Color,
    gained: Int = 0
) {
    val reduced = LocalReducedMotion.current
    val counting = remember(gained) { Animatable(if (gained > 0 && !reduced) 0f else 1f) }
    LaunchedEffect(gained) {
        if (gained <= 0 || reduced) return@LaunchedEffect
        delay(COUNT_UP_DELAY_MS)
        counting.animateTo(1f, tween(COUNT_UP_MS, easing = LinearEasing))
    }
    Row(
        modifier = Modifier
            .graphicsLayer {
                val s = countUpScale(counting.value)
                scaleX = s
                scaleY = s
            }
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
            text = "${countUpAt(counting.value, (count - gained).coerceAtLeast(0), count)}",
            fontSize = 16.sp,
            fontFamily = LexendFontFamily,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}
