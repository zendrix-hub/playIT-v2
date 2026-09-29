package com.playit.app.domain.manager

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HearItSequenceBuilderTest {

    @Test
    fun build_forLetterMAndMouse_matchesSpecification() {
        val sequence = HearItSequenceBuilder.build(
            letter = "m",
            keyWord = "mouse"
        )

        // Starts with car_listen
        assertEquals("audio/vo/tutor/car_listen.wav", sequence.first())

        // Ends with car_say_it_with_me
        assertEquals("audio/vo/tutor/car_say_it_with_me.wav", sequence.last())

        // Finds the index of the KEYWORD entry
        val keywordIndex = sequence.indexOf("audio/words/word_mouse.mp3")
        assertTrue("KEYWORD must be present in sequence", keywordIndex != -1)

        val phonemePath = "audio/phonemes/phoneme_m.mp3"

        // Exactly 3 PHONEME entries before KEYWORD
        val phonemesBefore = sequence.subList(0, keywordIndex).count { it == phonemePath }
        assertEquals(3, phonemesBefore)

        // Exactly 1 PHONEME entry after KEYWORD
        val phonemesAfter = sequence.subList(keywordIndex + 1, sequence.size).count { it == phonemePath }
        assertEquals(1, phonemesAfter)

        // Preserves PAUSE_500 tokens
        assertEquals(2, sequence.count { it == "PAUSE_500" })
    }

    @Test
    fun buildReplay_startsFromCarThisLetterSays() {
        val replaySequence = HearItSequenceBuilder.buildReplay(
            letter = "m",
            keyWord = "mouse"
        )

        assertEquals("audio/vo/tutor/car_this_letter_says.wav", replaySequence.first())
        assertEquals("audio/vo/tutor/car_say_it_with_me.wav", replaySequence.last())
        assertTrue("Replay should not contain car_listen", !replaySequence.contains("audio/vo/tutor/car_listen.wav"))
    }
}
