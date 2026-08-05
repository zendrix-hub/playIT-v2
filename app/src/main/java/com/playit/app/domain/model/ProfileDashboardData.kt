package com.playit.app.domain.model

data class ProfileDashboardData(
    val profile: Profile,
    val totalStars: Int,
    val retentionScore: Float,
    val overallAccuracy: Float,
    val completedLettersCount: Int,
    val totalLettersCount: Int,
    val letterPerformances: List<LetterPerformance>,
    val blendItCompletedCount: Int,
    val blendItTotalCount: Int,
    val atRiskLetters: List<LetterPerformance>
)
