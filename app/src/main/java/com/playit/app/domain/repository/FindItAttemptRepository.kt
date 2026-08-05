package com.playit.app.domain.repository

import com.playit.app.domain.model.FindItAttempt

interface FindItAttemptRepository {
    suspend fun saveAttempt(profileId: Long, phonemeId: Int, selectedPhonemeId: Int, isCorrect: Boolean)
    suspend fun getAttemptsForProfile(profileId: Long): List<FindItAttempt>
}
