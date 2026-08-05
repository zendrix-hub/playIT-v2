package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.playit.app.domain.model.Profile

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val profileId: Long = 0,
    val name: String,
    val avatarResId: Int,
    val totalStars: Int = 0,
    val currentStreak: Int = 0,
    val lastPlayedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

fun ProfileEntity.toDomain(): Profile = Profile(
    id = profileId,
    name = name,
    avatarResId = avatarResId,
    totalStars = totalStars,
    currentStreak = currentStreak,
    lastPlayedAt = lastPlayedAt,
    createdAt = createdAt
)

fun Profile.toEntity(): ProfileEntity = ProfileEntity(
    profileId = id,
    name = name,
    avatarResId = avatarResId,
    totalStars = totalStars,
    currentStreak = currentStreak,
    lastPlayedAt = lastPlayedAt,
    createdAt = createdAt
)
