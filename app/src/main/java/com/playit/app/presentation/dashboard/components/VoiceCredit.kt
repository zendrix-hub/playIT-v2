package com.playit.app.presentation.dashboard.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playit.app.presentation.theme.LexendFontFamily
import com.playit.app.presentation.theme.TextMuted

/** The ElevenLabs Free plan requires this credit wherever its audio is published (release 2026-10-11-words). */
const val VOICE_CREDIT = "Some word voices made with ElevenLabs (elevenlabs.io)"

@Composable
fun VoiceCreditLine(modifier: Modifier = Modifier) {
    Text(
        text = VOICE_CREDIT,
        fontFamily = LexendFontFamily,
        fontSize = 12.sp,
        color = TextMuted,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth().padding(bottom = 16.dp).testTag("parent_voice_credit")
    )
}
