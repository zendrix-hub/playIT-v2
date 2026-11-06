package com.playit.app.domain.manager

import com.playit.app.domain.model.SoundCheckResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/** The sound check before the word game (user choice, 2026-11-06): each sound once, the first tap counts. */
class SoundCheckRoundTest {
    private val chapter1 = listOf("m", "s", "a", "i")

    @Test fun eachSoundOnce_andEveryLetterIsATileEachTime() {
        val round = SoundCheckRound(chapter1, Random(3))
        assertEquals(chapter1.toSet(), round.order.toSet())
        assertEquals(4, round.order.size)
        repeat(4) {
            assertEquals(chapter1.toSet(), round.tiles.toSet())
            round.tap(round.current!!)
        }
        assertTrue(round.isDone)
    }

    @Test fun theOrderChangesFromCheckToCheck() {
        val orders = (1..20).map { SoundCheckRound(chapter1, Random(it)).order }.toSet()
        assertTrue(orders.size > 1)
    }

    @Test fun theFirstTapCounts_noSecondTry() {
        val round = SoundCheckRound(chapter1, Random(5))
        val first = round.current!!
        val wrong = chapter1.first { it != first }
        assertFalse(round.tap(wrong))
        assertTrue("moved on", round.current != first || round.isDone)
        while (!round.isDone) assertTrue(round.tap(round.current!!))
        val result = round.result(groupId = 1, checkedAt = 42)
        assertEquals(listOf(first), result.notYet)
        assertEquals(chapter1 - first, result.known)
        assertEquals(4, result.total)
    }

    @Test fun theResultKeepsChapterOrder() {
        val round = SoundCheckRound(chapter1, Random(9))
        while (!round.isDone) round.tap(round.current!!)
        assertEquals(chapter1, round.result(1, 0).known)
    }

    @Test fun aTapAfterTheLastSoundIsIgnored() {
        val round = SoundCheckRound(listOf("n", "g", "p"), Random(1))
        repeat(3) { round.tap(round.current!!) }
        assertFalse(round.tap("n"))
        assertEquals(listOf("n", "g", "p"), round.result(4, 0).known)
    }

    @Test fun summary_inPlainWords() {
        assertEquals("not yet", SoundCheckSummary.describe(emptyList()))
        val first = SoundCheckResult(1, 10, listOf("m", "s"), listOf("a", "i"))
        val latest = SoundCheckResult(1, 20, listOf("m", "s", "a", "i"), emptyList())
        assertEquals("knew 2 of 4 sounds. Practice: a, i", SoundCheckSummary.describe(listOf(first)))
        assertEquals("knew all 4 sounds", SoundCheckSummary.describe(listOf(latest)))
        assertEquals("first time 2 of 4, latest 4 of 4", SoundCheckSummary.describe(listOf(latest, first)))
        val worse = SoundCheckResult(1, 30, listOf("m", "s", "i"), listOf("a"))
        assertEquals("first time 2 of 4, latest 3 of 4. Practice: a", SoundCheckSummary.describe(listOf(first, latest, worse)))
        assertEquals(listOf("first time 2 of 4", "Latest 3 of 4", "Practice: a"), SoundCheckSummary.lines(listOf(first, latest, worse)))
        assertEquals(listOf("knew 2 of 4 sounds", "Practice: a, i"), SoundCheckSummary.lines(listOf(first)))
        assertEquals(listOf("not yet"), SoundCheckSummary.lines(emptyList()))
    }
}
