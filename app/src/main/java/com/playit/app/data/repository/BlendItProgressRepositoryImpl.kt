package com.playit.app.data.repository

import com.playit.app.data.local.dao.BlendItProgressDao
import com.playit.app.data.local.entity.toDomain
import com.playit.app.data.local.entity.toEntity
import com.playit.app.domain.model.BlendItProgress
import com.playit.app.domain.repository.BlendItProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlendItProgressRepositoryImpl @Inject constructor(
    private val blendItProgressDao: BlendItProgressDao
) : BlendItProgressRepository {

    override suspend fun getProgressForGroup(profileId: Long, groupId: Int): BlendItProgress? {
        return try {
            blendItProgressDao.getProgressForGroup(profileId, groupId)?.toDomain()
        } catch (e: Exception) {
            android.util.Log.e("BlendItProgressRepositoryImpl", "Failed to read data", e)
            null
        }
    }

    override fun getProgressForProfile(profileId: Long): Flow<List<BlendItProgress>> {
        return blendItProgressDao.getProgressForProfile(profileId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveProgress(progress: BlendItProgress) {
        try {
            blendItProgressDao.saveProgress(progress.toEntity())
        } catch (e: Exception) {
            android.util.Log.e("BlendItProgressRepositoryImpl", "Failed to save data", e)
        }
    }
}
