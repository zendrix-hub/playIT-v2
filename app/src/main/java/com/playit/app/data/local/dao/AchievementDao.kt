package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playit.app.data.local.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements WHERE profileId = :profileId")
    fun getAchievementsForProfile(profileId: Long): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE profileId = :profileId AND isUnlocked = 1")
    fun getUnlockedAchievements(profileId: Long): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE profileId = :profileId AND title = :title LIMIT 1")
    suspend fun getAchievementByTitle(profileId: Long, title: String): AchievementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(achievement: AchievementEntity): Long
}
