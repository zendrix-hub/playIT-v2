package com.playit.app.domain.calculator

import com.playit.app.domain.model.LetterPerformance
import com.playit.app.domain.model.ProfileDashboardData
import com.playit.app.domain.model.ReportData
import com.playit.app.domain.model.RiskStatus
import com.playit.app.domain.repository.BlendItProgressRepository
import com.playit.app.domain.repository.FindItAttemptRepository
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.ProfileRepository
import com.playit.app.domain.repository.SayItAttemptRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportGenerator @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val phonemeRepository: PhonemeRepository,
    private val lessonProgressRepository: LessonProgressRepository,
    private val sayItAttemptRepository: SayItAttemptRepository,
    private val findItAttemptRepository: FindItAttemptRepository,
    private val blendItProgressRepository: BlendItProgressRepository,
    private val letterStatusCalculator: LetterStatusCalculator,
    private val retentionCalculator: RetentionCalculator
) {

    suspend fun generateDashboardData(profileId: Long): ProfileDashboardData? {
        val profile = profileRepository.getProfileById(profileId) ?: return null
        val phonemes = phonemeRepository.getAllPhonemes().first()
        val lessonProgresses = lessonProgressRepository.getProgressForProfile(profileId).first().associateBy { it.phonemeId }
        val sayItAttempts = sayItAttemptRepository.getAttemptsForProfile(profileId)
        val findItAttempts = findItAttemptRepository.getAttemptsForProfile(profileId)
        val blendItProgresses = blendItProgressRepository.getProgressForProfile(profileId).first()

        val sayItByPhoneme = sayItAttempts.groupBy { it.phonemeId }
        val findItByPhoneme = findItAttempts.groupBy { it.phonemeId }

        val letterPerformances = phonemes.map { phoneme ->
            val progress = lessonProgresses[phoneme.id]
            val sayItList = sayItByPhoneme[phoneme.id] ?: emptyList()
            val findItList = findItByPhoneme[phoneme.id] ?: emptyList()

            val totalAttempts = sayItList.size + findItList.size
            val correctAttempts = sayItList.count { it.isCorrect } + findItList.count { it.isCorrect }
            val failedAttempts = sayItList.count { !it.isCorrect } + findItList.count { !it.isCorrect }

            val accuracy = if (totalAttempts > 0) {
                (correctAttempts.toFloat() / totalAttempts.toFloat()) * 100f
            } else if (progress?.isCompleted == true) {
                100f
            } else {
                0f
            }

            val status = letterStatusCalculator.calculateStatus(accuracy, failedAttempts)

            LetterPerformance(
                phonemeId = phoneme.id,
                symbol = phoneme.letter,
                name = phoneme.letter.uppercase(),
                accuracyPercentage = accuracy,
                totalAttempts = totalAttempts,
                failedAttempts = failedAttempts,
                heartsLost = progress?.heartsLost ?: 0,
                starsEarned = progress?.starsEarned ?: 0,
                isCompleted = progress?.isCompleted ?: false,
                riskStatus = status
            )
        }

        val retention = retentionCalculator.calculate7DayRetention(sayItAttempts, findItAttempts)
        val totalStars = letterPerformances.sumOf { it.starsEarned } + blendItProgresses.sumOf { it.starsEarned }
        val completedLetters = letterPerformances.count { it.isCompleted }

        val totalAttemptsAll = letterPerformances.sumOf { it.totalAttempts }
        val overallAccuracy = if (totalAttemptsAll > 0) {
            val totalCorrect = sayItAttempts.count { it.isCorrect } + findItAttempts.count { it.isCorrect }
            (totalCorrect.toFloat() / totalAttemptsAll.toFloat()) * 100f
        } else if (completedLetters > 0) {
            100f
        } else {
            0f
        }

        val atRisk = letterPerformances.filter { it.riskStatus == RiskStatus.RED }
        val blendItCompleted = blendItProgresses.count { it.isCompleted }

        return ProfileDashboardData(
            profile = profile,
            totalStars = totalStars,
            retentionScore = retention,
            overallAccuracy = overallAccuracy,
            completedLettersCount = completedLetters,
            totalLettersCount = phonemes.size,
            letterPerformances = letterPerformances,
            blendItCompletedCount = blendItCompleted,
            blendItTotalCount = 7,
            atRiskLetters = atRisk
        )
    }

    suspend fun generateReportData(profileId: Long): ReportData? {
        val dashboard = generateDashboardData(profileId) ?: return null
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentDateStr = dateFormat.format(Date())

        return ReportData(
            profileName = dashboard.profile.name,
            generatedDate = currentDateStr,
            totalStars = dashboard.totalStars,
            retentionScorePercentage = dashboard.retentionScore.toInt(),
            overallAccuracyPercentage = dashboard.overallAccuracy.toInt(),
            completedLettersCount = dashboard.completedLettersCount,
            totalLettersCount = dashboard.totalLettersCount,
            atRiskLetters = dashboard.atRiskLetters,
            letterPerformances = dashboard.letterPerformances,
            blendItCompletedCount = dashboard.blendItCompletedCount,
            blendItTotalCount = dashboard.blendItTotalCount
        )
    }
}
