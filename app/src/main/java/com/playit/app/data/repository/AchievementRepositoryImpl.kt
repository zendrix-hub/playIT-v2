package com.playit.app.data.repository

import com.playit.app.data.local.dao.AchievementDao
import com.playit.app.data.local.entity.toDomain
import com.playit.app.data.local.entity.toEntity
import com.playit.app.domain.model.Achievement
import com.playit.app.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: AchievementDao
) : AchievementRepository {

    override fun getAchievements(profileId: Long): Flow<List<Achievement>> {
        return achievementDao.getAchievementsForProfile(profileId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getUnlockedAchievements(profileId: Long): Flow<List<Achievement>> {
        return achievementDao.getUnlockedAchievements(profileId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getAchievementByTitle(profileId: Long, title: String): Achievement? {
        return try {
            achievementDao.getAchievementByTitle(profileId, title)?.toDomain()
        } catch (e: Exception) {
            android.util.Log.e("AchievementRepositoryImpl", "Failed to read data", e)
            null
        }
    }

    override suspend fun unlockAchievement(
        profileId: Long,
        title: String,
        unlockedAt: Long
    ): Achievement {
        return try {
            val existing = achievementDao.getAchievementByTitle(profileId, title)
            val entityToSave = existing?.copy(isUnlocked = true, unlockedAt = unlockedAt)
                ?: com.playit.app.data.local.entity.AchievementEntity(
                    profileId = profileId,
                    title = title,
                    isUnlocked = true,
                    unlockedAt = unlockedAt
                )
            val id = achievementDao.insertOrUpdate(entityToSave)
            entityToSave.copy(achievementId = if (existing != null) existing.achievementId else id).toDomain()
        } catch (e: Exception) {
            android.util.Log.e("AchievementRepositoryImpl", "Failed to save data", e)
            Achievement(
                id = 0L,
                profileId = profileId,
                title = title,
                isUnlocked = true,
                unlockedAt = unlockedAt
            )
        }
    }
}
