package com.playit.app.data.repository

import com.playit.app.data.local.dao.BlendItWordDao
import com.playit.app.data.local.entity.toDomain
import com.playit.app.domain.model.BlendItWord
import com.playit.app.domain.repository.BlendItWordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlendItWordRepositoryImpl @Inject constructor(
    private val blendItWordDao: BlendItWordDao
) : BlendItWordRepository {

    override fun getWordsForGroup(groupId: Int): Flow<List<BlendItWord>> {
        return blendItWordDao.getWordsForGroup(groupId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
