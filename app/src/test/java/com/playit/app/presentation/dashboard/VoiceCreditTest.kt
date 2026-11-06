package com.playit.app.presentation.dashboard

import com.playit.app.presentation.dashboard.components.VOICE_CREDIT
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceCreditTest {

    @Test fun creditNamesElevenLabsAndItsDomain() {
        assertTrue(VOICE_CREDIT.contains("ElevenLabs"))
        assertTrue(VOICE_CREDIT.contains("elevenlabs.io"))
    }

    @Test fun creditNamesOmniVoiceForTheOpeningsVoice() {
        assertTrue(VOICE_CREDIT.contains("OmniVoice by k2-fsa"))
        assertTrue("release 2026-11-06-sound-check", VOICE_CREDIT.contains("sound check"))
        assertTrue("release 2026-11-06-next-lines", VOICE_CREDIT.contains("Next lines"))
        assertTrue("release 2026-11-06-letter-sounds", VOICE_CREDIT.contains("letter sounds"))
    }
}
