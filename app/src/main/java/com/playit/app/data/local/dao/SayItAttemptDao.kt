package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playit.app.data.local.entity.SayItAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SayItAttemptDao {
    @Query("SELECT * FROM say_it_attempts WHERE profileId = :profileId AND phonemeId = :phonemeId")
    fun getAttemptsForPhoneme(profileId: Long, phonemeId: Int): Flow<List<SayItAttemptEntity>>

    @Query("SELECT * FROM say_it_attempts WHERE profileId = :profileId")
    suspend fun getAttemptsForProfile(profileId: Long): List<SayItAttemptEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: SayItAttemptEntity)
}
