package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playit.app.data.local.entity.LetterGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LetterGroupDao {
    @Query("SELECT * FROM letter_groups ORDER BY groupNumber ASC")
    fun getAllGroups(): Flow<List<LetterGroupEntity>>

    @Query("SELECT * FROM letter_groups WHERE groupId = :groupId")
    suspend fun getGroupById(groupId: Int): LetterGroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<LetterGroupEntity>)
}
