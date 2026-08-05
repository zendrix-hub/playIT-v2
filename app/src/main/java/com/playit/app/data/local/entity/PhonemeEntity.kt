package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.playit.app.domain.model.Phoneme

@Entity(tableName = "phonemes")
data class PhonemeEntity(
    @PrimaryKey
    val phonemeId: Int,
    val letter: String,
    val audioPath: String,
    val imagePath: String,
    val exampleWord: String
)

fun PhonemeEntity.toDomain(): Phoneme = Phoneme(
    id = phonemeId,
    letter = letter,
    audioPath = audioPath,
    imagePath = imagePath,
    exampleWord = exampleWord
)

fun Phoneme.toEntity(): PhonemeEntity = PhonemeEntity(
    phonemeId = id,
    letter = letter,
    audioPath = audioPath,
    imagePath = imagePath,
    exampleWord = exampleWord
)
