package com.playit.app.domain.repository

import com.playit.app.domain.model.Phoneme
import kotlinx.coroutines.flow.Flow

interface PhonemeRepository {
    fun getAllPhonemes(): Flow<List<Phoneme>>
    suspend fun getPhonemeById(id: Int): Phoneme?
    suspend fun getPhonemeByLetter(letter: String): Phoneme?
}
