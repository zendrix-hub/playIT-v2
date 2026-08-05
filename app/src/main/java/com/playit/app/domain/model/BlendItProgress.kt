package com.playit.app.domain.model

data class BlendItProgress(
    val id: Int = 0,
    val profileId: Long,
    val groupId: Int,
    val starsEarned: Int = 0,
    val heartsLost: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long = 0L
)
