package com.playit.app.presentation.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.DarkBrownOutline
import com.playit.app.presentation.theme.Guava
import com.playit.app.presentation.theme.Leaf
import com.playit.app.presentation.theme.Mango
import com.playit.app.presentation.theme.Sand
import com.playit.app.presentation.theme.Sky
import com.playit.app.presentation.theme.Ube

/**
 * Pediatric Animal Avatar Circle — renders one of the 6 companion animal avatars
 * (Cat, Monkey, Bunny, Bear, Frog, Owl) with a themed background and DarkBrownOutline.
 * Zero-emoji compliant, replacing legacy unicode placeholder with production assets.
 */
@Composable
fun AvatarCircle(
    avatarId: Int,
    size: Int,
    modifier: Modifier = Modifier
) {
    val clampedId = avatarId.coerceIn(1, 6)
    val avatarPath = "images/mascot/avatar_0$clampedId.png"
    val bgColors = listOf(
        Sky,
        Mango,
        Ube,
        Leaf,
        Sand,
        Guava
    )
    val bgColor = bgColors[(clampedId - 1) % bgColors.size]

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(2.5.dp, DarkBrownOutline, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAssetPainter(avatarPath),
            contentDescription = "Avatar $clampedId",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding((size * 0.1f).dp)
        )
    }
}
