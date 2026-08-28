package com.playit.app.data.repository

import com.playit.app.data.local.dao.FindItAttemptDao
import com.playit.app.data.local.entity.FindItAttemptEntity
import com.playit.app.domain.model.FindItAttempt
import com.playit.app.domain.repository.FindItAttemptRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FindItAttemptRepositoryImpl @Inject constructor(
    private val findItAttemptDao: FindItAttemptDao
) : FindItAttemptRepository {

    override suspend fun saveAttempt(
        profileId: Long,
        phonemeId: Int,
        selectedPhonemeId: Int,
        isCorrect: Boolean
    ) {
        try {
            findItAttemptDao.insertAttempt(
                FindItAttemptEntity(
                    profileId = profileId,
                    phonemeId = phonemeId,
                    selectedPhonemeId = selectedPhonemeId,
                    isCorrect = isCorrect
                )
            )
        } catch (e: Exception) {
            android.util.Log.e("FindItAttemptRepositoryImpl", "Failed to save data", e)
        }
    }

    override suspend fun getAttemptsForProfile(profileId: Long): List<FindItAttempt> {
        return findItAttemptDao.getAttemptsForProfile(profileId).map { entity ->
            FindItAttempt(
                attemptId = entity.attemptId,
                profileId = entity.profileId,
                phonemeId = entity.phonemeId,
                selectedPhonemeId = entity.selectedPhonemeId,
                isCorrect = entity.isCorrect,
                attemptedAt = entity.attemptedAt
            )
        }
    }
}
