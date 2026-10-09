package com.playit.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.playit.app.domain.model.ArticulationGroup
import com.playit.app.presentation.theme.ModernBorderSoft
import com.playit.app.presentation.theme.SurfaceCard

/**
 * Mouth-shape picture for the current sound (NFR-ACC-01). Pictures arrive through an image
 * release (card 25); until a group's picture is in assets this draws nothing at all, with no
 * empty box (Review Focus 4).
 */
@Composable
fun ArticulationCue(group: ArticulationGroup, size: Dp, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val present = remember(group) {
        val dir = group.assetPath.substringBeforeLast('/')
        val file = group.assetPath.substringAfterLast('/')
        runCatching { context.assets.list(dir)?.contains(file) == true }.getOrDefault(false)
    }
    if (!present) return

    Image(
        painter = rememberAssetPainter(group.assetPath, maxSize = size),
        contentDescription = "Mouth shape for this sound",
        modifier = modifier
            .size(size)
            .background(SurfaceCard, CircleShape)
            .border(1.5.dp, ModernBorderSoft, CircleShape)
            .padding(6.dp)
    )
}
