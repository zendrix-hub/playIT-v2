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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.theme.*

private val AVATAR_CHARACTERS = listOf(
    "cat", "monkey", "bunny", "bear", "frog", "owl"
)

private val AVATAR_BG_COLORS = listOf(
    AquaAdventure,
    SunnyGold,
    PrimaryJoy,
    EmeraldLeaf,
    ApricotGlow,
    CoralBerry
)

/**
 * Pediatric Animal Avatar Circle — renders one of the 6 companion animal avatars
 * (Cat, Monkey, Bunny, Bear, Frog, Owl) with clean selection rings, elevation,
 * and tight bounding boxes suitable for profile frames and dialogue heads.
 */
@Composable
fun AvatarCircle(
    avatarId: Int,
    size: Int = 64,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val clampedId = avatarId.coerceIn(1, 6)
    val charName = AVATAR_CHARACTERS[clampedId - 1]
    val avatarPath = "images/characters/avatar_0${clampedId}_${charName}.png"
    val baseColor = AVATAR_BG_COLORS[(clampedId - 1) % AVATAR_BG_COLORS.size]

    Box(
        modifier = modifier
            .size(size.dp)
            .shadow(elevation = if (isSelected) 6.dp else 2.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(if (isSelected) baseColor.copy(alpha = 0.35f) else Cloud)
            .border(
                width = if (isSelected) 3.5.dp else 2.dp,
                color = if (isSelected) baseColor else DarkBrownOutline.copy(alpha = 0.35f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAssetPainter(avatarPath),
            contentDescription = "Avatar $clampedId ($charName)",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding((size * 0.08f).dp)
        )
    }
}

