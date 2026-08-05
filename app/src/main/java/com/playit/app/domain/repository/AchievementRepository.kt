package com.playit.app.domain.repository

import com.playit.app.domain.model.Achievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun getAchievements(profileId: Long): Flow<List<Achievement>>
    fun getUnlockedAchievements(profileId: Long): Flow<List<Achievement>>
    suspend fun getAchievementByTitle(profileId: Long, title: String): Achievement?
    suspend fun unlockAchievement(profileId: Long, title: String, unlockedAt: Long): Achievement
}
