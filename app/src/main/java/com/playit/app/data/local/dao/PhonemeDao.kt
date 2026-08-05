package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playit.app.data.local.entity.PhonemeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhonemeDao {
    @Query("SELECT * FROM phonemes ORDER BY phonemeId ASC")
    fun getAllPhonemes(): Flow<List<PhonemeEntity>>

    @Query("SELECT * FROM phonemes WHERE phonemeId = :phonemeId")
    suspend fun getPhonemeById(phonemeId: Int): PhonemeEntity?

    @Query("SELECT * FROM phonemes WHERE letter = :letter LIMIT 1")
    suspend fun getPhonemeByLetter(letter: String): PhonemeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhonemes(phonemes: List<PhonemeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoneme(phoneme: PhonemeEntity)
}
