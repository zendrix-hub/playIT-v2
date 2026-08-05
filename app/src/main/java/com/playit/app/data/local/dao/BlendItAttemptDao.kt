package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.playit.app.data.local.entity.BlendItAttemptEntity

@Dao
interface BlendItAttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: BlendItAttemptEntity)
}
