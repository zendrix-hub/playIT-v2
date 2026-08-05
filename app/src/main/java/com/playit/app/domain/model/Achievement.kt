package com.playit.app.domain.model

data class Achievement(
    val id: Long = 0,
    val profileId: Long,
    val title: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)
