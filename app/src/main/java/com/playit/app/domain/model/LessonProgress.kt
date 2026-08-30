package com.playit.app.domain.model

data class LessonProgress(
    val id: Int = 0,
    val profileId: Long,
    val phonemeId: Int,
    val starsEarned: Int = 0,
    val heartsLost: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long = 0L,
    val timeSpentMs: Long = 0L
)
