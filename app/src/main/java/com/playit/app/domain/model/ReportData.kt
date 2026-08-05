package com.playit.app.domain.model

data class ReportData(
    val profileName: String,
    val generatedDate: String,
    val totalStars: Int,
    val retentionScorePercentage: Int,
    val overallAccuracyPercentage: Int,
    val completedLettersCount: Int,
    val totalLettersCount: Int,
    val atRiskLetters: List<LetterPerformance>,
    val letterPerformances: List<LetterPerformance>,
    val blendItCompletedCount: Int,
    val blendItTotalCount: Int
)
