package com.playit.app.data.repository

import com.playit.app.data.local.dao.LetterGroupMemberDao
import com.playit.app.data.local.entity.toDomain
import com.playit.app.domain.model.LetterGroupMember
import com.playit.app.domain.repository.LetterGroupMemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LetterGroupMemberRepositoryImpl @Inject constructor(
    private val letterGroupMemberDao: LetterGroupMemberDao
) : LetterGroupMemberRepository {

    override fun getMembersForGroup(groupId: Int): Flow<List<LetterGroupMember>> {
        return letterGroupMemberDao.getMembersForGroup(groupId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllMembers(): Flow<List<LetterGroupMember>> {
        return letterGroupMemberDao.getAllMembers().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
