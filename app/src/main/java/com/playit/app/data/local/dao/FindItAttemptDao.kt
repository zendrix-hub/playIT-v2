package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playit.app.data.local.entity.FindItAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FindItAttemptDao {
    @Query("SELECT * FROM find_it_attempts WHERE profileId = :profileId AND phonemeId = :phonemeId")
    fun getAttemptsForPhoneme(profileId: Long, phonemeId: Int): Flow<List<FindItAttemptEntity>>

    @Query("SELECT * FROM find_it_attempts WHERE profileId = :profileId")
    suspend fun getAttemptsForProfile(profileId: Long): List<FindItAttemptEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: FindItAttemptEntity)
}
