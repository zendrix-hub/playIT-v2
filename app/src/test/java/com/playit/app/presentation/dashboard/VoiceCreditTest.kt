package com.playit.app.presentation.dashboard

import com.playit.app.presentation.dashboard.components.VOICE_CREDIT
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceCreditTest {

    @Test fun creditNamesElevenLabsAndItsDomain() {
        assertTrue(VOICE_CREDIT.contains("ElevenLabs"))
        assertTrue(VOICE_CREDIT.contains("elevenlabs.io"))
    }
}
