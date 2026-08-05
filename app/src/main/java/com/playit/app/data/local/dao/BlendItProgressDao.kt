package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playit.app.data.local.entity.BlendItProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlendItProgressDao {
    @Query("SELECT * FROM blend_it_progress WHERE profileId = :profileId AND groupId = :groupId LIMIT 1")
    suspend fun getProgressForGroup(profileId: Long, groupId: Int): BlendItProgressEntity?

    @Query("SELECT * FROM blend_it_progress WHERE profileId = :profileId")
    fun getProgressForProfile(profileId: Long): Flow<List<BlendItProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: BlendItProgressEntity)
}
