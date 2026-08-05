package com.playit.app.domain.repository

import com.playit.app.domain.model.BlendItWord
import kotlinx.coroutines.flow.Flow

interface BlendItWordRepository {
    fun getWordsForGroup(groupId: Int): Flow<List<BlendItWord>>
}
