package com.playit.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.CreamWhite
import com.playit.app.presentation.theme.SoftSky
import com.playit.app.presentation.theme.TextPrimary

/**
 * Playful Mascot Prompt Speech Bubble component rendering Lily the Tarsier.
 */
@Composable
fun MascotBubbleComponent(
    message: String,
    modifier: Modifier = Modifier,
    mascotState: MascotState = MascotState.IDLE,
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
                GummyMotionAsset(
                    assetPath = mascotState.assetPath,
                    contentDescription = "Lily the Tarsier (${mascotState.name})",
                    isIdleFloating = true,
                    floatDistance = 2.dp,
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
