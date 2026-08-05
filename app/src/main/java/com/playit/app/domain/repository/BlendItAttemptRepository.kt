package com.playit.app.domain.repository

import com.playit.app.domain.model.BlendItAttempt

interface BlendItAttemptRepository {
    suspend fun saveAttempt(attempt: BlendItAttempt)
}
