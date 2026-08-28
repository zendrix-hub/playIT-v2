package com.playit.app.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Ink
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.SoftSky

/**
 * Compact Mascot Prompt Speech Bubble header matching playit-mockup.html.
 * Placed at the top of lesson bodies to guide the learner with warm Filipino-pedagogic encouragement.
 */
@Composable
fun MascotSpeechHeader(
    message: String,
    modifier: Modifier = Modifier,
    mascotState: MascotState = MascotState.IDLE,
    onMascotTap: (() -> Unit)? = null
) {
    var tapTrigger by remember { mutableStateOf(0) }

    val tapBounceScale by animateFloatAsState(
        targetValue = if (tapTrigger % 2 == 1) 1.18f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = {
            if (tapTrigger % 2 == 1) tapTrigger++
        },
        label = "mascotTapBounce"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // Full-Body Mascot Character (Duolingo ABC Co-Player scale)
        Box(
            modifier = Modifier
                .size(width = 86.dp, height = 98.dp)
                .graphicsLayer {
                    scaleX = tapBounceScale
                    scaleY = tapBounceScale
                }
                .bounceClick {
                    tapTrigger++
                    onMascotTap?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAssetPainter(mascotState.assetPath),
                contentDescription = "Lily the Tarsier",
                contentScale = ContentScale.Fit,
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
                .background(CreamWhite)
                .border(
                    width = 2.5.dp,
                    color = DarkBrownOutline,
                    shape = RoundedCornerShape(
                        topStart = 22.dp,
                        topEnd = 22.dp,
                        bottomEnd = 22.dp,
                        bottomStart = 6.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message,
                fontFamily = LexendFontFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Ink,
                lineHeight = 32.sp
            )
        }
    }
}
