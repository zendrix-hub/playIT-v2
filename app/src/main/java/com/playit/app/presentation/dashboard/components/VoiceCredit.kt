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

/**
 * The ElevenLabs Free plan requires this credit wherever its audio is published (release 2026-10-11-words), and the
 * OmniVoice model's CC BY-NC licence requires one for the opening's voice (release 2026-10-30-opening) and Lily's
 * sound check lines (release 2026-11-06-sound-check), her Next lines (release 2026-11-06-next-lines) and her letter
 * sounds (release 2026-11-06-letter-sounds).
 */
const val VOICE_CREDIT = "Some word voices made with ElevenLabs (elevenlabs.io). Opening voice, sound check, Next lines, letter sounds and the word umbrella made with OmniVoice by k2-fsa."

@Composable
fun VoiceCreditLine(modifier: Modifier = Modifier) {
    Text(
        text = VOICE_CREDIT,
        fontFamily = LexendFontFamily,
        fontSize = 14.sp,  // the smallest text in the Parent Zone (user decision 2026-11-07)
        lineHeight = 18.sp,
        color = TextMuted,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth().padding(bottom = 16.dp).testTag("parent_voice_credit")
    )
}
