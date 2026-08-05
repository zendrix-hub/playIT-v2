package com.playit.app.domain.repository

import com.playit.app.domain.model.BlendItProgress
import kotlinx.coroutines.flow.Flow

interface BlendItProgressRepository {
    suspend fun getProgressForGroup(profileId: Long, groupId: Int): BlendItProgress?
    fun getProgressForProfile(profileId: Long): Flow<List<BlendItProgress>>
    suspend fun saveProgress(progress: BlendItProgress)
}
