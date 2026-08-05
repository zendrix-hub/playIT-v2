package com.playit.app.domain.repository

import com.playit.app.domain.model.LetterGroupMember
import kotlinx.coroutines.flow.Flow

interface LetterGroupMemberRepository {
    fun getMembersForGroup(groupId: Int): Flow<List<LetterGroupMember>>
    fun getAllMembers(): Flow<List<LetterGroupMember>>
}
