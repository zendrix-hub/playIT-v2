package com.playit.app.domain.calculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** One rule for the Parent Zone's "Practice next" and the PDF's "Needs help with" (user decision 2026-11-07). */
class PracticeNextTest {

    @Test fun lettersFinishedBelowSatisfactory_atMostTwo_worstFirst() {
        // Misses on letters 2, 5 and 6 (s, o, b): b has the most, then o, then s; only two are named.
        val misses = mapOf(2 to 1, 5 to 2, 6 to 4)
        val l = RecordSamples.learner(1, "Frog", done = 8, findMisses = { misses[it] ?: 0 }, sayMisses = { misses[it] ?: 0 })
        val weak = l.chapters.flatMap { it.letters }.filter { it.level.needsPractice }
        assertTrue("the sample has three weak letters: ${weak.map { it.symbol to it.grade }}", weak.size == 3)
        val pick = PracticeNext.of(l.chapters) as PracticeNext.Pick.NeedsHelp
        assertEquals(PracticeNext.MAX, pick.letters.size)
        assertEquals(weak.sortedBy { it.grade }.take(2).map { it.symbol }, pick.letters.map { it.symbol })
        assertTrue("worst first", pick.letters[0].grade!! <= pick.letters[1].grade!!)
    }

    @Test fun nothingWeak_namesTheLetterBeingPlayedNow() {
        val l = RecordSamples.learner(1, "Frog", done = 3)
        val pick = PracticeNext.of(l.chapters) as PracticeNext.Pick.PlayingNow
        assertEquals("i", pick.letter.symbol)
    }

    @Test fun nothingWeakAndNothingHalfDone_isAllGood() {
        assertEquals(PracticeNext.Pick.AllGood, PracticeNext.of(RecordSamples.learner(1, "Owl", done = 4, startedNext = false).chapters))
        assertEquals(PracticeNext.Pick.AllGood, PracticeNext.of(RecordSamples.learner(2, "New", done = 0, startedNext = false).chapters))
    }

    @Test fun names_readAsLetterAndWord() {
        val l = RecordSamples.learner(1, "Frog", done = 2).chapters.first().letters
        assertEquals("m (Mouse), s (Sun)", PracticeNext.names(l.take(2)))
    }
}
