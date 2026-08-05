package com.playit.app.domain.repository

import com.playit.app.domain.model.SayItAttempt

interface SayItAttemptRepository {
    suspend fun saveAttempt(profileId: Long, phonemeId: Int, isCorrect: Boolean)
    suspend fun getAttemptsForProfile(profileId: Long): List<SayItAttempt>
}
