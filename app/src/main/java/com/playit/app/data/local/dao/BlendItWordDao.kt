package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playit.app.data.local.entity.BlendItWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlendItWordDao {
    @Query("SELECT * FROM blend_it_words WHERE groupId = :groupId ORDER BY wordId ASC")
    fun getWordsForGroup(groupId: Int): Flow<List<BlendItWordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<BlendItWordEntity>)
}
