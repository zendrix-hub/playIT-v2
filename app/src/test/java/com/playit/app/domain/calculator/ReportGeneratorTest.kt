package com.playit.app.domain.calculator

import com.playit.app.domain.model.BlendItProgress
import com.playit.app.domain.model.FindItAttempt
import com.playit.app.domain.model.LessonProgress
import com.playit.app.domain.model.LetterGroupMember
import com.playit.app.domain.model.Profile
import com.playit.app.domain.model.ProgressLevel
import com.playit.app.domain.model.SayItAttempt
import com.playit.app.domain.repository.BlendItProgressRepository
import com.playit.app.domain.repository.FindItAttemptRepository
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.LetterGroupMemberRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.ProfileRepository
import com.playit.app.domain.repository.SayItAttemptRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The record is built from the saved rows, with chapters from the seeded letter groups. */
class ReportGeneratorTest {
    private val frog = Profile(id = 1, name = "Frog", avatarResId = 5, createdAt = 1)
    private val bear = Profile(id = 2, name = "Bear", avatarResId = 4, createdAt = 2)

    private val profiles: ProfileRepository = mockk {
        every { getAllProfiles() } returns flowOf(listOf(bear, frog))
        coEvery { getProfileById(1) } returns frog
        coEvery { getProfileById(2) } returns bear
    }
    private val phonemes: PhonemeRepository = mockk { every { getAllPhonemes() } returns flowOf(RecordSamples.phonemes) }
    private val members: LetterGroupMemberRepository = mockk {
        every { getAllMembers() } returns flowOf(RecordSamples.phonemes.map {
            LetterGroupMember(groupId = RecordSamples.chapterOf.getValue(it.id), phonemeId = it.id, position = it.id)
        })
    }
    private val lessons: LessonProgressRepository = mockk {
        every { getProgressForProfile(1) } returns flowOf((1..4).map {
            LessonProgress(profileId = 1, phonemeId = it, starsEarned = 3, isCompleted = true, completedAt = 1000L * it)
        })
        every { getProgressForProfile(2) } returns flowOf(emptyList())
    }
    private val sayIt: SayItAttemptRepository = mockk {
        coEvery { getAttemptsForProfile(1) } returns (1..4).map {
            SayItAttempt(profileId = 1, phonemeId = it, isCorrect = true, attemptedAt = 1000L * it)
        }
        coEvery { getAttemptsForProfile(2) } returns emptyList()
    }
    private val findIt: FindItAttemptRepository = mockk {
        coEvery { getAttemptsForProfile(1) } returns (1..4).flatMap { id ->
            List(3) { FindItAttempt(profileId = 1, phonemeId = id, selectedPhonemeId = id, isCorrect = true, attemptedAt = 1000L * id) }
        }
        coEvery { getAttemptsForProfile(2) } returns emptyList()
    }
    private val blend: BlendItProgressRepository = mockk {
        every { getProgressForProfile(1) } returns flowOf(listOf(
            BlendItProgress(profileId = 1, groupId = 1, starsEarned = 2, isCompleted = true, completedAt = 5000)
        ))
        every { getProgressForProfile(2) } returns flowOf(emptyList())
    }
    private val checks: com.playit.app.domain.repository.SoundCheckRepository = mockk {
        coEvery { resultsFor(1) } returns listOf(
            // Saved before Frog's profile was made (createdAt 1): an earlier child with the same id.
            com.playit.app.domain.model.SoundCheckResult(1, 0, listOf("m"), listOf("s", "a", "i")),
            com.playit.app.domain.model.SoundCheckResult(1, 6000, listOf("m", "s", "i"), listOf("a")),
            com.playit.app.domain.model.SoundCheckResult(1, 9000, listOf("m", "s", "a", "i"), emptyList())
        )
        coEvery { resultsFor(2) } returns emptyList()
    }
    private val generator = ReportGenerator(profiles, phonemes, lessons, sayIt, findIt, blend, members, RetentionCalculator(), checks)

    /** User choice, 2026-11-06: each chapter carries its sound checks, oldest first, only this child's. */
    @Test fun aChapterCarriesItsSoundChecks_withoutAnEarlierChildsWithTheSameId() = runTest {
        val chapter1 = generator.generateDashboardData(1)!!.chapters.first { it.chapter == 1 }
        assertEquals(listOf(6000L, 9000L), chapter1.soundChecks.map { it.checkedAt })
        assertTrue(generator.generateDashboardData(1)!!.chapters.filter { it.chapter != 1 }.all { it.soundChecks.isEmpty() })
        assertTrue(generator.generateDashboardData(2)!!.chapters.all { it.soundChecks.isEmpty() })
    }

    @Test fun aChildsRecord_hasSevenChaptersFromTheSeededGroups() = runTest {
        val data = generator.generateDashboardData(1)!!
        assertEquals(7, data.chapters.size)
        assertEquals(listOf(4, 4, 4, 3, 4, 3, 4), data.chapters.map { it.lettersTotal })
        assertEquals("m, s, a, i", data.chapters.first().letterList)
        assertEquals(ProgressLevel.OUTSTANDING, data.chapters.first().level)
        assertTrue(data.chapters.first().blendItCompleted)
        assertEquals(2, data.chapters.first().blendItStars)
        assertEquals(4, data.completedLettersCount)
        assertEquals(100, data.overallGrade)
        assertEquals(7, data.blendItTotalCount)
        assertEquals(14, data.totalStars)
        assertEquals(1000L, data.firstPlayedAt)
        assertEquals(5000L, data.lastPlayedAt)
    }

    /** A child who has played nothing has no letter at risk: every letter is "Not started yet". */
    @Test fun aNewChild_hasNothingAtRisk() = runTest {
        val data = generator.generateDashboardData(2)!!
        assertTrue(data.letterPerformances.all { it.level == ProgressLevel.NOT_STARTED })
        assertTrue(data.needsPractice.isEmpty())
        assertEquals(ProgressLevel.NOT_STARTED, data.overallLevel)
        assertEquals(null, data.firstPlayedAt)
    }

    @Test fun theClassRecord_listsEveryChild_oldestFirst() = runTest {
        assertEquals(listOf("Frog", "Bear"), generator.generateClassRecord().map { it.profile.name })
        val report = generator.generateReportData()!!
        assertEquals(2, report.learners.size)
    }
}
