package com.playit.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.*
import kotlinx.coroutines.delay

/**
 * Compact Mascot Prompt Speech Bubble header matching playit-mockup.html.
 * Placed at the top of lesson bodies to guide the learner with warm Filipino-pedagogic encouragement.
 */
@Composable
fun MascotSpeechHeader(
    message: String,
    modifier: Modifier = Modifier,
    mascotState: MascotState = MascotState.IDLE,
    isPlayingAudio: Boolean = false,
    amplitude: Float = 0f,
    onMascotTap: (() -> Unit)? = null,
    greet: Boolean = false
) {
    // Each tap on Lily or her bubble: she answers it herself (TalkingLily), a different way each time.
    var taps by remember { mutableStateOf(0) }

    // Lesson start (user, 2026-10-29): Lily waves hello for GREET_MS, then takes the pose the screen asks for. The
    // wave is her waving pose acted out (LilyActing.kt); with reduced motion, the pose only.
    var greeting by remember { mutableStateOf(greet) }
    LaunchedEffect(Unit) {
        if (!greeting) return@LaunchedEffect
        delay(GREET_MS)
        greeting = false
    }
    val pose = if (greeting) MascotState.WAVING else mascotState

    val isReducedMotion = LocalReducedMotion.current
    val d = LocalPlayItDimens.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // Full-Body Mascot Character with Real-Time Mic Amplitude
        Box(
            modifier = Modifier
                .size(d.mascotHeader)
                .graphicsLayer {
                    if (!isReducedMotion) {
                        scaleX = 1.0f - (amplitude.coerceIn(0f, 1f) * 0.12f)
                        scaleY = 1.0f + (amplitude.coerceIn(0f, 1f) * 0.25f)
                    }
                    transformOrigin = TransformOrigin(0.5f, 1f)
                }
                .bounceClick {
                    taps++
                    onMascotTap?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            TalkingLily(
                pose = pose,
                maxSize = d.mascotHeader,
                taps = taps,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 3D Gummy Speech Bubble with Comic Pointed Tail
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(
                        topStart = 22.dp,
                        topEnd = 22.dp,
                        bottomEnd = 22.dp,
                        bottomStart = 6.dp
                    )
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 22.dp,
                        topEnd = 22.dp,
                        bottomEnd = 22.dp,
                        bottomStart = 6.dp
                    )
                )
                .background(SurfaceCard)
                .border(
                    width = 2.dp,
                    color = ModernBorder,
                    shape = RoundedCornerShape(
                        topStart = 22.dp,
                        topEnd = 22.dp,
                        bottomEnd = 22.dp,
                        bottomStart = 6.dp
                    )
                )
                .then(
                    if (onMascotTap != null) {
                        Modifier.bounceClick {
                            taps++
                            onMascotTap()
                        }
                    } else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = message,
                    fontFamily = LexendFontFamily,
                    fontSize = d.bubbleTextSp.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMidnight,
                    lineHeight = (d.bubbleTextSp + 6).sp,
                    maxLines = d.bubbleMaxLines,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (onMascotTap != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isPlayingAudio) SunnyGold else SunnyGoldLight)
                            .border(1.5.dp, ModernBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = "Listen to Lily",
                            tint = TextMidnight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
