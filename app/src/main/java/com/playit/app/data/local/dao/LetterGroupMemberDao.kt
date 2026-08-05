package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playit.app.data.local.entity.LetterGroupMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LetterGroupMemberDao {
    @Query("SELECT * FROM letter_group_members WHERE groupId = :groupId ORDER BY position ASC")
    fun getMembersForGroup(groupId: Int): Flow<List<LetterGroupMemberEntity>>

    @Query("SELECT * FROM letter_group_members ORDER BY groupId ASC, position ASC")
    fun getAllMembers(): Flow<List<LetterGroupMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<LetterGroupMemberEntity>)
}
