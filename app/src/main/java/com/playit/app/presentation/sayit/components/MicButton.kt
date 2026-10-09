package com.playit.app.presentation.sayit.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.components.GummyContainer
import com.playit.app.presentation.sayit.MicStatus
import com.playit.app.presentation.theme.EmeraldLeaf
import com.playit.app.presentation.theme.EmeraldLeafDark
import com.playit.app.presentation.theme.EmeraldLeafShadow
import com.playit.app.presentation.theme.GentleCorrectionOrange
import com.playit.app.presentation.theme.GentleCorrectionOrangeShadow
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.LocalPlayItDimens
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.ModernBorder
import com.playit.app.presentation.theme.PlayItMotion
import com.playit.app.presentation.theme.PrimaryJoyDark
import com.playit.app.presentation.theme.PrimaryJoyLight
import com.playit.app.presentation.theme.SunnyGold
import com.playit.app.presentation.theme.SunnyGoldShadow
import com.playit.app.presentation.theme.TextMidnight
import com.playit.app.presentation.theme.TextMuted

private data class MicLook(val face: Color, val shadow: Color, val tint: Color, val icon: ImageVector, val label: String)

private fun lookFor(status: MicStatus): MicLook = when (status) {
    MicStatus.IDLE -> MicLook(EmeraldLeaf, EmeraldLeafShadow, Color.White, Icons.Rounded.Mic, "Tap and say it")
    MicStatus.LISTENING -> MicLook(SunnyGold, SunnyGoldShadow, TextMidnight, Icons.Rounded.Mic, "I'm listening...")
    MicStatus.HEARD -> MicLook(PrimaryJoyLight, PrimaryJoyDark, PrimaryJoyDark, Icons.Rounded.Mic, "I hear you!")
    MicStatus.RESULT_CORRECT -> MicLook(EmeraldLeafDark, EmeraldLeafShadow, Color.White, Icons.Rounded.Check, "Yes!")
    MicStatus.RESULT_TRY_AGAIN -> MicLook(GentleCorrectionOrange, GentleCorrectionOrangeShadow, TextMidnight, Icons.Rounded.Hearing, "Let's try again")
}

/**
 * The Say It mic. Colour, icon, motion and label change together for each [MicStatus],
 * so no state relies on colour alone, and none of them is red (03 :24, :54).
 * Taps reach [onTap] only in IDLE and RESULT_TRY_AGAIN.
 */
@Composable
fun MicButton(
    status: MicStatus,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    labelOverride: String? = null,
) {
    val d = LocalPlayItDimens.current
    val reduced = LocalReducedMotion.current
    val look = lookFor(status)
    val label = labelOverride ?: look.label
    val tappable = enabled && (status == MicStatus.IDLE || status == MicStatus.RESULT_TRY_AGAIN)

    // Correct pop (100 -> 108 -> 100 %) and gentle shake; both skipped under reduced motion.
    val pop = remember { Animatable(1f) }
    val shakeX = remember { Animatable(0f) }
    LaunchedEffect(status) {
        if (reduced) return@LaunchedEffect
        when (status) {
            MicStatus.RESULT_CORRECT -> {
                pop.animateTo(1.08f, tween(PlayItMotion.MICRO_MS))
                pop.animateTo(1f, tween(PlayItMotion.MICRO_MS))
            }
            MicStatus.RESULT_TRY_AGAIN -> shakeX.animateTo(0f, keyframes {
                durationMillis = 400
                0f at 0; -8f at 80; 8f at 160; -4f at 240; 4f at 320; 0f at 400
            })
            else -> Unit
        }
    }

    Column(
        modifier = modifier.semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (status == MicStatus.LISTENING) ListeningRing(d.micSize, look.face, reduced)
            GummyContainer(
                onClick = if (tappable) onTap else null,
                enabled = enabled,
                faceColor = look.face,
                shadowColor = look.shadow,
                shape = CircleShape,
                strokeWidth = 2.5.dp,
                strokeColor = ModernBorder,
                depthHeight = 6.dp,
                modifier = Modifier
                    .size(d.micSize)
                    .testTag("sayit_mic")
                    .semantics { contentDescription = label }
                    .graphicsLayer {
                        scaleX = pop.value; scaleY = pop.value
                        translationX = shakeX.value
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (status == MicStatus.HEARD) {
                        HeardDots(look.tint, reduced)
                    } else {
                        Icon(
                            imageVector = look.icon,
                            contentDescription = null,
                            tint = if (enabled) look.tint else TextMuted,
                            modifier = Modifier.size(d.micSize * 0.45f)
                        )
                    }
                }
            }
        }
        Text(
            text = label,
            fontFamily = LexendFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextMidnight,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** One ring that grows out of the mic every 1200 ms; a still 4 dp ring under reduced motion. */
@Composable
private fun ListeningRing(size: androidx.compose.ui.unit.Dp, color: Color, reduced: Boolean) {
    if (reduced) {
        Box(Modifier.size(size + 8.dp).border(4.dp, color, CircleShape))
        return
    }
    val t = rememberInfiniteTransition(label = "micRing")
    val progress by t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "micRingProgress"
    )
    Box(
        Modifier
            .size(size)
            .graphicsLayer {
                val s = 1f + 0.4f * progress
                scaleX = s; scaleY = s
                alpha = 0.6f * (1f - progress)
            }
            .background(color, CircleShape)
    )
}

/** Three dots that bounce in turn while speech is heard; still dots under reduced motion. */
@Composable
private fun HeardDots(color: Color, reduced: Boolean) {
    val t = rememberInfiniteTransition(label = "heardDots")
    val phase by t.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "heardDotsPhase"
    )
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(3) { i ->
            val lift = if (reduced) 0f else {
                val local = (phase - i).let { if (it < 0f) it + 3f else it }
                if (local < 1f) kotlin.math.sin(local * Math.PI.toFloat()) else 0f
            }
            Box(
                Modifier
                    .size(12.dp)
                    .graphicsLayer { translationY = -8.dp.toPx() * lift }
                    .background(color, CircleShape)
            )
        }
    }
}
