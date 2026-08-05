package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playit.app.data.local.entity.LessonProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonProgressDao {
    @Query("SELECT * FROM lesson_progress WHERE profileId = :profileId")
    fun getProgressForProfile(profileId: Long): Flow<List<LessonProgressEntity>>

    @Query("SELECT * FROM lesson_progress WHERE profileId = :profileId AND phonemeId = :phonemeId LIMIT 1")
    suspend fun getProgressForPhoneme(profileId: Long, phonemeId: Int): LessonProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: LessonProgressEntity)
}
