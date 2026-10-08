package com.playit.app.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowProfile { COMPACT, REGULAR, WIDE }

fun windowProfileFor(widthDp: Int, heightDp: Int): WindowProfile = when {
    widthDp >= 600 -> WindowProfile.WIDE
    heightDp < 700 -> WindowProfile.COMPACT
    else -> WindowProfile.REGULAR
}

data class PlayItDimens(
    val profile: WindowProfile,
    val screenPadding: Dp,
    val contentMaxWidth: Dp,
    val mascotHeader: Dp,      // Lily in lesson headers
    val bubbleTextSp: Int,     // lesson instruction bubble (spoken aloud too)
    val bubbleMaxLines: Int,
    val letterCardHeight: Dp,
    val primaryCta: Dp,        // Hear It play button
    val micSize: Dp,
    val findItCardMinHeight: Dp,
    val findItCardAspect: Float, // width / height; flatter cards on short screens so 3 rows fit
    val tileSize: Dp,          // Blend It slots and tiles
    val mapNodeSize: Dp,
    val completeMascot: Dp,
)

fun dimensFor(profile: WindowProfile): PlayItDimens = when (profile) {
    WindowProfile.COMPACT -> PlayItDimens(profile, 16.dp, 560.dp, 64.dp, 18, 3, 210.dp, 76.dp, 96.dp, 96.dp, 1.5f, 64.dp, 68.dp, 112.dp)
    WindowProfile.REGULAR -> PlayItDimens(profile, 20.dp, 560.dp, 80.dp, 20, 4, 250.dp, 88.dp, 112.dp, 112.dp, 1.2f, 68.dp, 76.dp, 140.dp)
    WindowProfile.WIDE -> PlayItDimens(profile, 32.dp, 560.dp, 96.dp, 22, 4, 300.dp, 96.dp, 128.dp, 132.dp, 1.2f, 76.dp, 88.dp, 160.dp)
}

val LocalPlayItDimens = staticCompositionLocalOf { dimensFor(WindowProfile.REGULAR) }
