package com.playit.app.domain.manager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CaptionTextTest {
    @Test fun phonemeClip() = assertEquals("/m/", CaptionText.forClip("audio/phonemes/m.mp3", "m", "mouse"))
    @Test fun keywordClip() = assertEquals("mouse", CaptionText.forClip("audio/keywords/kw_mouse.wav", "m", "mouse"))
    @Test fun carrier() = assertEquals("Listen!", CaptionText.forClip("audio/vo/tutor/car_listen.wav", "m", "mouse"))
    @Test fun pauseHasNoCaption() {
        assertNull(CaptionText.forClip("pause:500", "m", "mouse"))
        assertNull(CaptionText.forClip("PAUSE_500", "m", "mouse"))   // the token HearItSequenceBuilder uses
    }
}
