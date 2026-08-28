package com.playit.app.data.repository

import com.playit.app.data.local.dao.PhonemeDao
import com.playit.app.data.local.entity.toDomain
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.repository.PhonemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhonemeRepositoryImpl @Inject constructor(
    private val phonemeDao: PhonemeDao
) : PhonemeRepository {

    override fun getAllPhonemes(): Flow<List<Phoneme>> {
        return phonemeDao.getAllPhonemes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPhonemeById(id: Int): Phoneme? {
        return try {
            phonemeDao.getPhonemeById(id)?.toDomain()
        } catch (e: Exception) {
            android.util.Log.e("PhonemeRepositoryImpl", "Failed to read data", e)
            null
        }
    }

    override suspend fun getPhonemeByLetter(letter: String): Phoneme? {
        return try {
            phonemeDao.getPhonemeByLetter(letter)?.toDomain()
        } catch (e: Exception) {
            android.util.Log.e("PhonemeRepositoryImpl", "Failed to read data", e)
            null
        }
    }
}
