package com.playit.app.domain.model

data class FindItAttempt(
    val attemptId: Int = 0,
    val profileId: Long,
    val phonemeId: Int,
    val selectedPhonemeId: Int,
    val isCorrect: Boolean,
    val attemptedAt: Long = System.currentTimeMillis()
)
