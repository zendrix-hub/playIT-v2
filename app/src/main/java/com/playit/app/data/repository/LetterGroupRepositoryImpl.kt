package com.playit.app.data.repository

import com.playit.app.data.local.dao.LetterGroupDao
import com.playit.app.data.local.entity.toDomain
import com.playit.app.domain.model.LetterGroup
import com.playit.app.domain.repository.LetterGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LetterGroupRepositoryImpl @Inject constructor(
    private val letterGroupDao: LetterGroupDao
) : LetterGroupRepository {

    override fun getAllGroups(): Flow<List<LetterGroup>> {
        return letterGroupDao.getAllGroups().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getGroupById(groupId: Int): LetterGroup? {
        return letterGroupDao.getGroupById(groupId)?.toDomain()
    }
}
