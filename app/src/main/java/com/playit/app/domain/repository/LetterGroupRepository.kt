package com.playit.app.domain.repository

import com.playit.app.domain.model.LetterGroup
import kotlinx.coroutines.flow.Flow

interface LetterGroupRepository {
    fun getAllGroups(): Flow<List<LetterGroup>>
    suspend fun getGroupById(groupId: Int): LetterGroup?
}
