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
        faceColor = Cloud,
        shadowColor = CloudShadow,
        shape = RoundedCornerShape(28.dp),
        strokeWidth = 3.dp,
        strokeColor = if (isCorrect) Leaf else DarkBrownOutline,
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
                            .background(color = Tan.copy(alpha = 0.20f), shape = CircleShape)
                    )
                    GummyMotionAsset(
                        assetPath = assetPath,
                        contentDescription = "Larawan ng salitang $cleanWord • Blend word illustration: $cleanWord",
                        isIdleFloating = true,
                        floatDistance = 4.dp,
                        celebrateTrigger = isCorrect,
                        modifier = Modifier.size(92.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                            contentDescription = null,
                            tint = InkSoft,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Pindutin para marinig",
                            fontFamily = LexendFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = InkSoft
                        )
                    }
                    Text(
                        text = "Tap to hear word",
                        fontFamily = LexendFontFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = InkSoft,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
