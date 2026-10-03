package com.playit.app.domain.manager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HearItSequenceBuilderTest {

    private val phonemePath = "audio/phonemes/phoneme_m.mp3"
    private val keyWordPath = "audio/words/word_mouse.mp3"
    private val tutorPath: (String) -> String = { "audio/vo/tutor/$it.wav" }

    @Test
    fun build_m_mouse_followsTable3() {
        val sequence = HearItSequenceBuilder.build(
            HearItSequenceBuilder.TEMPLATE, phonemePath, keyWordPath, tutorPath
        )

        assertEquals("audio/vo/tutor/car_listen.wav", sequence.first())
        assertEquals("audio/vo/tutor/car_say_it_with_me.wav", sequence.last())

        val keywordIndex = sequence.indexOf(keyWordPath)
        assertTrue("KEYWORD must be present in sequence", keywordIndex != -1)
        assertEquals(3, sequence.subList(0, keywordIndex).count { it == phonemePath })
        assertEquals(1, sequence.subList(keywordIndex + 1, sequence.size).count { it == phonemePath })
        assertEquals(2, sequence.count { it == "PAUSE_500" })
    }

    @Test
    fun build_noKeyWord_dropsKeyword() {
        val sequence = HearItSequenceBuilder.build(
            HearItSequenceBuilder.TEMPLATE, phonemePath, null, tutorPath
        )

        assertFalse(sequence.contains("KEYWORD"))
        assertFalse(sequence.contains(keyWordPath))
        assertEquals(4, sequence.count { it == phonemePath })
        assertEquals(HearItSequenceBuilder.TEMPLATE.size - 1, sequence.size)
    }

    @Test
    fun replayTemplate_isSteps3to6() {
        val replay = HearItSequenceBuilder.replayTemplate()

        assertEquals("car_this_letter_says", replay.first())
        assertEquals("PHONEME", replay.last())
        assertFalse(replay.contains("car_listen"))
        assertFalse(replay.contains("car_say_it_with_me"))
    }

    @Test
    fun pauseMillis_parsesOnlyPauseTokens() {
        assertEquals(500L, HearItSequenceBuilder.pauseMillis("PAUSE_500"))
        assertNull(HearItSequenceBuilder.pauseMillis("PHONEME"))
        assertNull(HearItSequenceBuilder.pauseMillis("audio/words/word_mouse.mp3"))
        assertNull(HearItSequenceBuilder.pauseMillis("PAUSE_x"))
    }
}
