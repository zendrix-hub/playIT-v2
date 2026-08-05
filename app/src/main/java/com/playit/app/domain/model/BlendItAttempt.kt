package com.playit.app.domain.model

data class BlendItAttempt(
    val attemptId: Int = 0,
    val profileId: Long,
    val groupId: Int,
    val wordId: Int,
    val isCorrect: Boolean,
    val attemptedAt: Long = System.currentTimeMillis()
)
