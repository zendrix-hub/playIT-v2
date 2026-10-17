package com.playit.app.domain.manager

import com.playit.app.domain.model.SpeechErrorType
import com.playit.app.domain.model.SpeechJudgement
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TutorPolicyTest {

    private lateinit var tutorPolicy: TutorPolicy

    @Before
    fun setUp() {
        tutorPolicy = TutorPolicy()
    }

    @Test
    fun correct_anyAttempt_praises() {
        val correctJudgement = SpeechJudgement(
            isCorrect = true,
            errorType = SpeechErrorType.NONE,
            heard = "mouse"
        )

        assertEquals(TutorAction.Praise(1), tutorPolicy.next(1, correctJudgement))
        assertEquals(TutorAction.Praise(3), tutorPolicy.next(3, correctJudgement))
    }

    @Test
    fun firstMiss_correctsAtLevel1_withErrorType() {
        val letterNameJudgement = SpeechJudgement(
            isCorrect = false,
            errorType = SpeechErrorType.LETTER_NAME,
            heard = "em"
        )

        val action = tutorPolicy.next(1, letterNameJudgement)
        assertEquals(TutorAction.Correct(SpeechErrorType.LETTER_NAME, 1), action)
    }

    @Test
    fun secondMiss_correctsAtLevel2() {
        val addedVowelJudgement = SpeechJudgement(
            isCorrect = false,
            errorType = SpeechErrorType.ADDED_VOWEL,
            heard = "ma"
        )

        val action = tutorPolicy.next(2, addedVowelJudgement)
        assertEquals(TutorAction.Correct(SpeechErrorType.ADDED_VOWEL, 2), action)
    }

    @Test
    fun thirdMiss_leadsAndMovesOn() {
        val anyMissJudgement = SpeechJudgement(
            isCorrect = false,
            errorType = SpeechErrorType.OTHER_WORD,
            heard = "cat"
        )

        val action = tutorPolicy.next(3, anyMissJudgement)
        assertEquals(TutorAction.LeadAndMoveOn, action)
    }
}
