package com.playit.app.domain.model

data class LetterPerformance(
    val phonemeId: Int,
    val symbol: String,
    val name: String,
    val accuracyPercentage: Float,
    val totalAttempts: Int,
    val failedAttempts: Int,
    val heartsLost: Int,
    val starsEarned: Int,
    val isCompleted: Boolean,
    val riskStatus: RiskStatus
)
