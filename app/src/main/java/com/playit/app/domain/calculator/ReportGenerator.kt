package com.playit.app.domain.calculator

import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.domain.model.ReportData
import com.playit.app.domain.repository.BlendItProgressRepository
import com.playit.app.domain.repository.FindItAttemptRepository
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.LetterGroupMemberRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.ProfileRepository
import com.playit.app.domain.repository.SayItAttemptRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/** Builds each learner's record (letters, chapters, overall) for the Parent Zone and the PDF report. */
@Singleton
class ReportGenerator @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val phonemeRepository: PhonemeRepository,
    private val lessonProgressRepository: LessonProgressRepository,
    private val sayItAttemptRepository: SayItAttemptRepository,
    private val findItAttemptRepository: FindItAttemptRepository,
    private val blendItProgressRepository: BlendItProgressRepository,
    private val letterGroupMemberRepository: LetterGroupMemberRepository,
    private val retentionCalculator: RetentionCalculator
) {

    suspend fun generateDashboardData(profileId: Long): ProfileDashboardData? {
        val profile = profileRepository.getProfileById(profileId) ?: return null
        val phonemes = phonemeRepository.getAllPhonemes().first()
        val members = letterGroupMemberRepository.getAllMembers().first()
        val chapterOf = members.associate { it.phonemeId to it.groupId }
        val orderInChapter = members.associate { it.phonemeId to it.position }
        val lessonProgresses = lessonProgressRepository.getProgressForProfile(profileId).first().associateBy { it.phonemeId }
        val sayItAttempts = sayItAttemptRepository.getAttemptsForProfile(profileId)
        val findItAttempts = findItAttemptRepository.getAttemptsForProfile(profileId)
        val blendItProgresses = blendItProgressRepository.getProgressForProfile(profileId).first()

        val sayItByPhoneme = sayItAttempts.groupBy { it.phonemeId }
        val findItByPhoneme = findItAttempts.groupBy { it.phonemeId }

        val letters = phonemes.map { phoneme ->
            ProgressRecordCalculator.letter(
                phoneme = phoneme,
                chapter = chapterOf[phoneme.id] ?: 0,
                progress = lessonProgresses[phoneme.id],
                sayIt = sayItByPhoneme[phoneme.id].orEmpty(),
                findIt = findItByPhoneme[phoneme.id].orEmpty()
            )
        }
        val blendByChapter = blendItProgresses.associateBy { it.groupId }
        val chapters = letters.filter { it.chapter > 0 }
            .groupBy { it.chapter }
            .toSortedMap()
            .map { (chapter, inChapter) ->
                ProgressRecordCalculator.chapter(
                    chapter,
                    inChapter.sortedBy { orderInChapter[it.phonemeId] ?: 0 },
                    blendByChapter[chapter]
                )
            }
        val (overallGrade, overallLevel) = ProgressRecordCalculator.overall(letters)

        val recent = retentionCalculator.recentAttempts(sayItAttempts, findItAttempts)
        val times = sayItAttempts.map { it.attemptedAt } + findItAttempts.map { it.attemptedAt } +
            lessonProgresses.values.filter { it.isCompleted }.map { it.completedAt } +
            blendItProgresses.filter { it.isCompleted }.map { it.completedAt }
        val playedTimes = times.filter { it > 0L }

        return ProfileDashboardData(
            profile = profile,
            totalStars = letters.sumOf { it.starsEarned } + blendItProgresses.sumOf { it.starsEarned },
            completedLettersCount = letters.count { it.isCompleted },
            totalLettersCount = phonemes.size,
            letterPerformances = letters,
            chapters = chapters,
            overallGrade = overallGrade,
            overallLevel = overallLevel,
            recentScore = ProgressRecordCalculator.score(recent.first, recent.second),
            blendItCompletedCount = blendItProgresses.count { it.isCompleted },
            blendItTotalCount = chapters.size,
            firstPlayedAt = playedTimes.minOrNull(),
            lastPlayedAt = playedTimes.maxOrNull()
        )
    }

    /** Every learner on this phone, oldest profile first, for the class record. */
    suspend fun generateClassRecord(): List<ProfileDashboardData> =
        profileRepository.getAllProfiles().first()
            .sortedBy { it.createdAt }
            .mapNotNull { generateDashboardData(it.id) }

    /** The report for every learner on this phone. */
    suspend fun generateReportData(): ReportData? {
        val learners = generateClassRecord()
        if (learners.isEmpty()) return null
        return ReportData(
            generatedDate = SimpleDateFormat("MMMM d, yyyy", Locale.US).format(Date()),
            learners = learners
        )
    }
}
