package com.playit.app.domain.repository

import com.playit.app.domain.model.SoundCheckResult

/** Every sound check a child has done, kept apart from the database so its schema does not change. */
interface SoundCheckRepository {
    suspend fun save(profileId: Long, result: SoundCheckResult)

    /** Every check of every chapter, oldest first. */
    suspend fun resultsFor(profileId: Long): List<SoundCheckResult>

    suspend fun deleteFor(profileId: Long)
}
