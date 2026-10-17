package com.playit.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.LocalReducedMotion
import com.playit.app.presentation.theme.ModernBorderSoft
import com.playit.app.presentation.theme.PillShape
import com.playit.app.presentation.theme.PlayItMotion
import com.playit.app.presentation.theme.SurfaceCard
import com.playit.app.presentation.theme.TextMidnight

/**
 * On-screen caption of what is being said (NFR-ACC-01): 24 sp, one line, in a pill. It shows the
 * latest caption and fades out when [caption] is null (a snap under reduced motion).
 */
@Composable
fun CaptionBubble(caption: String?, modifier: Modifier = Modifier) {
    val reduced = LocalReducedMotion.current
    var shown by remember { mutableStateOf(caption.orEmpty()) }
    if (caption != null) shown = caption

    AnimatedVisibility(
        visible = caption != null,
        enter = fadeIn(if (reduced) snap() else tween(PlayItMotion.MICRO_MS)),
        exit = fadeOut(if (reduced) snap() else tween(PlayItMotion.MICRO_MS)),
        modifier = modifier
    ) {
        Text(
            text = shown,
            fontFamily = LexendFontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextMidnight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .background(SurfaceCard, PillShape)
                .border(1.5.dp, ModernBorderSoft, PillShape)
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .semantics { liveRegion = LiveRegionMode.Polite }
        )
    }
}
