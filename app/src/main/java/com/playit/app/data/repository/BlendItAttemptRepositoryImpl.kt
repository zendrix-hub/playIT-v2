package com.playit.app.data.repository

import com.playit.app.data.local.dao.BlendItAttemptDao
import com.playit.app.data.local.entity.toEntity
import com.playit.app.domain.model.BlendItAttempt
import com.playit.app.domain.repository.BlendItAttemptRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlendItAttemptRepositoryImpl @Inject constructor(
    private val blendItAttemptDao: BlendItAttemptDao
) : BlendItAttemptRepository {

    override suspend fun saveAttempt(attempt: BlendItAttempt) {
        blendItAttemptDao.insertAttempt(attempt.toEntity())
    }
}
