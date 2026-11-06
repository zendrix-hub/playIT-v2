package com.playit.app.data.repository

import androidx.test.core.app.ApplicationProvider
import com.playit.app.domain.model.SoundCheckResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Sound checks are kept outside Room, so the database schema (and the children's data) stays as it is. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SoundCheckRepositoryImplTest {
    private fun check(group: Int, at: Long, known: List<String> = listOf("m", "s"), notYet: List<String> = listOf("a")) =
        SoundCheckResult(group, at, known, notYet)

    @Test fun savesAndReadsBack_perChild_oldestFirst() = runTest {
        val repo = SoundCheckRepositoryImpl(ApplicationProvider.getApplicationContext())
        repo.save(1, check(1, 200))
        repo.save(1, check(1, 100, known = listOf("m", "s", "a", "i"), notYet = emptyList()))
        repo.save(2, check(3, 150))
        assertEquals(listOf(100L, 200L), repo.resultsFor(1).map { it.checkedAt })
        assertEquals(listOf("m", "s", "a", "i"), repo.resultsFor(1).first().known)
        assertTrue(repo.resultsFor(1).first().notYet.isEmpty())
        assertEquals(1, repo.resultsFor(2).size)
    }

    @Test fun deletingAChild_removesOnlyTheirChecks() = runTest {
        val repo = SoundCheckRepositoryImpl(ApplicationProvider.getApplicationContext())
        repo.save(1, check(1, 1))
        repo.save(2, check(1, 2))
        repo.deleteFor(1)
        assertTrue(repo.resultsFor(1).isEmpty())
        assertEquals(1, repo.resultsFor(2).size)
    }

    @Test fun encodeDecode_roundTrip_andABadLineIsSkipped() {
        val checks = listOf(check(1, 5), check(7, 6, known = emptyList(), notYet = listOf("q", "v", "x", "z")))
        val text = SoundCheckRepositoryImpl.encode(checks)
        assertEquals(checks, SoundCheckRepositoryImpl.decode(text))
        assertEquals(checks, SoundCheckRepositoryImpl.decode(text + "\nnot a check\n9|x|m|"))
        assertTrue(SoundCheckRepositoryImpl.decode(null).isEmpty())
    }

    @Test fun trim_dropsTheOldest_butKeepsEachChaptersFirstCheck() {
        val max = SoundCheckRepositoryImpl.MAX_CHECKS
        val checks = listOf(check(2, 0)) + (1..max).map { check(1, it.toLong()) }
        val kept = SoundCheckRepositoryImpl.trim(checks)
        assertEquals(max, kept.size)
        assertTrue("chapter 2's only check", kept.any { it.groupId == 2 })
        assertTrue("chapter 1's first check", kept.any { it.groupId == 1 && it.checkedAt == 1L })
        assertTrue("the second oldest of chapter 1 went", kept.none { it.groupId == 1 && it.checkedAt == 2L })
    }
}
