package com.playit.app.domain.calculator

import com.playit.app.domain.model.BlendItProgress
import com.playit.app.domain.model.FindItAttempt
import com.playit.app.domain.model.LessonProgress
import com.playit.app.domain.model.ProgressLevel
import com.playit.app.domain.model.SayItAttempt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressRecordCalculatorTest {
    private val m = RecordSamples.phonemes.first()

    private fun say(vararg right: Boolean) = right.map { SayItAttempt(profileId = 1, phonemeId = 1, isCorrect = it) }
    private fun find(vararg right: Boolean) =
        right.map { FindItAttempt(profileId = 1, phonemeId = 1, selectedPhonemeId = 1, isCorrect = it) }
    private val done = LessonProgress(profileId = 1, phonemeId = 1, starsEarned = 3, isCompleted = true)

    /** It used to be At-Risk at 0%: a letter the child has never played says so instead. */
    @Test fun aLetterNeverPlayed_isNotStarted_notAtRisk() {
        val l = ProgressRecordCalculator.letter(m, 1, null, emptyList(), emptyList())
        assertEquals(ProgressLevel.NOT_STARTED, l.level)
        assertNull(l.score)
        assertNull(l.grade)
        assertTrue(!l.level.needsPractice)
    }

    @Test fun aLetterStartedButNotFinished_isStillLearning_withoutAGrade() {
        val l = ProgressRecordCalculator.letter(m, 1, null, say(false, false), emptyList())
        assertEquals(ProgressLevel.IN_PROGRESS, l.level)
        assertEquals(0, l.score)
    }

    @Test fun aFinishedLetter_isGradedFromBothActivities() {
        val l = ProgressRecordCalculator.letter(m, 1, done, say(false, true), find(true, true, true))
        assertEquals(1, l.sayItCorrect)
        assertEquals(2, l.sayItAttempts)
        assertEquals(3, l.findItCorrect)
        assertEquals(3, l.findItAttempts)
        assertEquals(80, l.score)                    // 4 of 5
        assertEquals(87, l.grade)                    // transmuted
        assertEquals(ProgressLevel.VERY_SATISFACTORY, l.level)
        assertEquals("Mouse", l.keyWord)
    }

    @Test fun aPerfectLetter_isOutstanding() {
        val l = ProgressRecordCalculator.letter(m, 1, done, say(true), find(true, true, true))
        assertEquals(100, l.grade)
        assertEquals(ProgressLevel.OUTSTANDING, l.level)
    }

    @Test fun aChapter_isGradedOnlyWhenEveryLetterIsFinished() {
        val learner = RecordSamples.learner(1, "Frog", done = 3)
        val chapter1 = learner.chapters.first()
        assertEquals(ProgressLevel.IN_PROGRESS, chapter1.level)
        assertNull(chapter1.grade)
        assertEquals(3, chapter1.lettersDone)
        assertEquals(ProgressLevel.NOT_STARTED, learner.chapters[1].level)

        val finished = RecordSamples.learner(1, "Frog", done = 4)
        assertEquals(100, finished.chapters.first().grade)
        assertEquals(ProgressLevel.OUTSTANDING, finished.chapters.first().level)
        assertTrue(finished.chapters.first().blendItCompleted)
    }

    @Test fun aChapterGrade_isTheMeanOfItsLetters() {
        val letters = RecordSamples.learner(1, "Frog", done = 4, findMisses = { if (it <= 2) 1 else 0 })
            .chapters.first().letters
        val expected = letters.mapNotNull { it.grade }.average().let { Math.round(it).toInt() }
        val chapter = ProgressRecordCalculator.chapter(1, letters, BlendItProgress(profileId = 1, groupId = 1))
        assertEquals(expected, chapter.grade)
    }

    @Test fun overall_isStillLearningUntilALetterIsFinished_thenTheMeanGrade() {
        assertEquals(ProgressLevel.NOT_STARTED, RecordSamples.learner(1, "Frog", done = 0, startedNext = false).overallLevel)
        assertEquals(ProgressLevel.IN_PROGRESS, RecordSamples.learner(1, "Frog", done = 0).overallLevel)
        val learner = RecordSamples.learner(1, "Frog", done = 6, sayMisses = { 1 })
        assertEquals(87, learner.overallGrade)       // every letter 4 of 5
        assertEquals(ProgressLevel.VERY_SATISFACTORY, learner.overallLevel)
    }

    @Test fun practiseAtHome_listsOnlyFinishedLettersAtTheTwoLowestLevels() {
        val learner = RecordSamples.learner(1, "Frog", done = 4, sayMisses = { if (it == 2) 3 else 0 },
            findMisses = { if (it == 2) 2 else 0 })
        assertEquals(listOf("s"), learner.needsPractice.map { it.symbol })
    }
}
