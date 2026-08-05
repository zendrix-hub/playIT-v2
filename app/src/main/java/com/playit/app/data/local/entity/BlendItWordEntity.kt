package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.playit.app.domain.model.BlendItWord

@Entity(
    tableName = "blend_it_words",
    foreignKeys = [
        ForeignKey(
            entity = LetterGroupEntity::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["groupId"])
    ]
)
data class BlendItWordEntity(
    @PrimaryKey
    val wordId: Int,
    val groupId: Int,
    val word: String,
    val wordPattern: String,
    val audioPath: String,
    val imagePath: String
)

fun BlendItWordEntity.toDomain(): BlendItWord = BlendItWord(
    wordId = wordId,
    groupId = groupId,
    word = word,
    wordPattern = wordPattern,
    audioPath = audioPath,
    imagePath = imagePath
)

fun BlendItWord.toEntity(): BlendItWordEntity = BlendItWordEntity(
    wordId = wordId,
    groupId = groupId,
    word = word,
    wordPattern = wordPattern,
    audioPath = audioPath,
    imagePath = imagePath
)
