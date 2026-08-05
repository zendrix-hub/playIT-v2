package com.playit.app.domain.model

data class Profile(
    val id: Long = 0,
    val name: String,
    val avatarResId: Int,
    val totalStars: Int = 0,
    val currentStreak: Int = 0,
    val lastPlayedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
