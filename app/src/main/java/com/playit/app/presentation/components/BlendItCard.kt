package com.playit.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.Cloud
import com.playit.app.presentation.theme.CloudShadow
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.InkSoft
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.Tan

@Composable
fun BlendItCard(
    word: String,
    modifier: Modifier = Modifier,
    isCorrect: Boolean = false,
    onReplayAudio: () -> Unit = {}
) {
    val cleanWord = word.lowercase()
    val assetPath = "images/pictures/blendword_$cleanWord.png"

    GummyContainer(
        onClick = onReplayAudio,
        faceColor = com.playit.app.presentation.theme.SurfaceCard,
        shadowColor = com.playit.app.presentation.theme.SurfaceCardShadow,
        shape = com.playit.app.presentation.theme.CardShape,
        strokeWidth = 2.5.dp,
        strokeColor = if (isCorrect) com.playit.app.presentation.theme.EmeraldLeaf else com.playit.app.presentation.theme.ModernBorder,
        depthHeight = 6.dp,
        isSquashed = isCorrect,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 176.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(96.dp)) {
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .background(color = com.playit.app.presentation.theme.SunnyGold.copy(alpha = 0.15f), shape = CircleShape)
                    )
                    GummyMotionAsset(
                        assetPath = assetPath,
                        contentDescription = "Blend word illustration: $cleanWord",
                        isIdleFloating = true,
                        floatDistance = 4.dp,
                        celebrateTrigger = isCorrect,
                        modifier = Modifier.size(92.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = "Hear word",
                        tint = com.playit.app.presentation.theme.TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Tap to hear word",
                        fontFamily = LexendFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = com.playit.app.presentation.theme.TextMuted
                    )
                }
            }
        }
    }
}
