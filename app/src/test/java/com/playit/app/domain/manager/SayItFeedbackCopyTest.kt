package com.playit.app.domain.manager

import com.playit.app.domain.model.SpeechErrorType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SayItFeedbackCopyTest {

    @Test
    fun praise_isEffortPraise() {
        val result = SayItFeedbackCopy.forResult(
            action = TutorAction.Praise(1),
            errorType = SpeechErrorType.NONE,
            wordMode = true,
            word = "mouse"
        )
        assertEquals(
            FeedbackCopy(
                mascot = "Yes! You said it!",
                banner = "Great listening!"
            ),
            result
        )
    }

    @Test
    fun letterName_level1_namesTheError() {
        val result = SayItFeedbackCopy.forResult(
            action = TutorAction.Correct(SpeechErrorType.LETTER_NAME, 1),
            errorType = SpeechErrorType.LETTER_NAME,
            wordMode = true,
            word = "mouse"
        )
        assertEquals("That's the letter's name. Listen for its sound.", result.mascot)
        assertEquals("Its sound, not its name", result.banner)
    }

    @Test
    fun addedVowel_level1_namesTheError() {
        val result = SayItFeedbackCopy.forResult(
            action = TutorAction.Correct(SpeechErrorType.ADDED_VOWEL, 1),
            errorType = SpeechErrorType.ADDED_VOWEL,
            wordMode = true,
            word = "mouse"
        )
        assertEquals("Almost! Just the sound, no 'ah'.", result.mascot)
        assertEquals("Just the sound", result.banner)
    }

    @Test
    fun noSpeech_wordMode_saysTheWord() {
        val result = SayItFeedbackCopy.forResult(
            action = TutorAction.Correct(SpeechErrorType.NO_SPEECH, 1),
            errorType = SpeechErrorType.NO_SPEECH,
            wordMode = true,
            word = "mouse"
        )
        assertEquals("I didn't hear you. Say Mouse!", result.mascot)
        assertEquals("Say it out loud", result.banner)
    }

    @Test
    fun noSpeech_soundMode_saysIt() {
        val result = SayItFeedbackCopy.forResult(
            action = TutorAction.Correct(SpeechErrorType.NO_SPEECH, 1),
            errorType = SpeechErrorType.NO_SPEECH,
            wordMode = false,
            word = "mouse"
        )
        assertEquals("I didn't hear you. Say it!", result.mascot)
        assertEquals("Say it out loud", result.banner)
    }

    @Test
    fun level2_isWatchMyLips_forAnyType() {
        val r1 = SayItFeedbackCopy.forResult(
            action = TutorAction.Correct(SpeechErrorType.OTHER_WORD, 2),
            errorType = SpeechErrorType.OTHER_WORD,
            wordMode = true,
            word = "mouse"
        )
        val r2 = SayItFeedbackCopy.forResult(
            action = TutorAction.Correct(SpeechErrorType.LETTER_NAME, 2),
            errorType = SpeechErrorType.LETTER_NAME,
            wordMode = true,
            word = "mouse"
        )

        assertEquals("Watch my lips, then your turn.", r1.mascot)
        assertEquals("Watch my lips", r1.banner)

        assertEquals("Watch my lips, then your turn.", r2.mascot)
        assertEquals("Watch my lips", r2.banner)
    }

    @Test
    fun leadAndMoveOn_neverSaysTryAgain() {
        val result = SayItFeedbackCopy.forResult(
            action = TutorAction.LeadAndMoveOn,
            errorType = SpeechErrorType.OTHER_WORD,
            wordMode = true,
            word = "mouse"
        )
        assertEquals("Let's say it together. We'll practice later.", result.mascot)
        assertEquals("Let's say it together", result.banner)
        assertFalse(result.mascot.contains("try again", ignoreCase = true))
        assertFalse(result.banner.contains("try again", ignoreCase = true))
    }

    @Test
    fun noRowSaysTryAgain() {
        for (errorType in SpeechErrorType.values()) {
            for (level in 1..2) {
                for (wordMode in listOf(true, false)) {
                    val result = SayItFeedbackCopy.forResult(
                        action = TutorAction.Correct(errorType, level),
                        errorType = errorType,
                        wordMode = wordMode,
                        word = "mouse"
                    )
                    assertFalse(
                        "Found 'try again' in mascot for $errorType level $level (wordMode=$wordMode): ${result.mascot}",
                        result.mascot.contains("try again", ignoreCase = true)
                    )
                    assertFalse(
                        "Found 'try again' in banner for $errorType level $level (wordMode=$wordMode): ${result.banner}",
                        result.banner.contains("try again", ignoreCase = true)
                    )
                }
            }
        }

        val praiseResult = SayItFeedbackCopy.forResult(
            action = TutorAction.Praise(1),
            errorType = SpeechErrorType.NONE,
            wordMode = true,
            word = "mouse"
        )
        assertFalse(praiseResult.mascot.contains("try again", ignoreCase = true))
        assertFalse(praiseResult.banner.contains("try again", ignoreCase = true))

        val leadResult = SayItFeedbackCopy.forResult(
            action = TutorAction.LeadAndMoveOn,
            errorType = SpeechErrorType.OTHER_WORD,
            wordMode = true,
            word = "mouse"
        )
        assertFalse(leadResult.mascot.contains("try again", ignoreCase = true))
        assertFalse(leadResult.banner.contains("try again", ignoreCase = true))
    }
}
