package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.playit.app.domain.model.BlendItAttempt

@Entity(
    tableName = "blend_it_attempts",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["profileId"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LetterGroupEntity::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BlendItWordEntity::class,
            parentColumns = ["wordId"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profileId", "groupId"]),
        Index(value = ["groupId"]),
        Index(value = ["wordId"])
    ]
)
data class BlendItAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val attemptId: Int = 0,
    val profileId: Long,
    val groupId: Int,
    val wordId: Int,
    val isCorrect: Boolean,
    val attemptedAt: Long = System.currentTimeMillis()
)

fun BlendItAttemptEntity.toDomain(): BlendItAttempt = BlendItAttempt(
    attemptId = attemptId,
    profileId = profileId,
    groupId = groupId,
    wordId = wordId,
    isCorrect = isCorrect,
    attemptedAt = attemptedAt
)

fun BlendItAttempt.toEntity(): BlendItAttemptEntity = BlendItAttemptEntity(
    attemptId = attemptId,
    profileId = profileId,
    groupId = groupId,
    wordId = wordId,
    isCorrect = isCorrect,
    attemptedAt = attemptedAt
)
