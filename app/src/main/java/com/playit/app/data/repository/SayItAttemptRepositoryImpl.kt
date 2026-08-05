package com.playit.app.data.repository

import com.playit.app.data.local.dao.SayItAttemptDao
import com.playit.app.data.local.entity.SayItAttemptEntity
import com.playit.app.domain.model.SayItAttempt
import com.playit.app.domain.repository.SayItAttemptRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SayItAttemptRepositoryImpl @Inject constructor(
    private val sayItAttemptDao: SayItAttemptDao
) : SayItAttemptRepository {

    override suspend fun saveAttempt(profileId: Long, phonemeId: Int, isCorrect: Boolean) {
        sayItAttemptDao.insertAttempt(
            SayItAttemptEntity(
                profileId = profileId,
                phonemeId = phonemeId,
                isCorrect = isCorrect
            )
        )
    }

    override suspend fun getAttemptsForProfile(profileId: Long): List<SayItAttempt> {
        return sayItAttemptDao.getAttemptsForProfile(profileId).map { entity ->
            SayItAttempt(
                attemptId = entity.attemptId,
                profileId = entity.profileId,
                phonemeId = entity.phonemeId,
                isCorrect = entity.isCorrect,
                attemptedAt = entity.attemptedAt
            )
        }
    }
}
