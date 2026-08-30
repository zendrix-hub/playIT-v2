package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.playit.app.domain.model.LessonProgress

@Entity(
    tableName = "lesson_progress",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["profileId"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PhonemeEntity::class,
            parentColumns = ["phonemeId"],
            childColumns = ["phonemeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profileId", "phonemeId"], unique = true),
        Index(value = ["profileId"]),
        Index(value = ["phonemeId"])
    ]
)
data class LessonProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val profileId: Long,
    val phonemeId: Int,
    val starsEarned: Int = 0,
    val heartsLost: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long = 0L,
    val timeSpentMs: Long = 0L
)

fun LessonProgressEntity.toDomain(): LessonProgress = LessonProgress(
    id = id,
    profileId = profileId,
    phonemeId = phonemeId,
    starsEarned = starsEarned,
    heartsLost = heartsLost,
    isCompleted = isCompleted,
    completedAt = completedAt,
    timeSpentMs = timeSpentMs
)

fun LessonProgress.toEntity(): LessonProgressEntity = LessonProgressEntity(
    id = id,
    profileId = profileId,
    phonemeId = phonemeId,
    starsEarned = starsEarned,
    heartsLost = heartsLost,
    isCompleted = isCompleted,
    completedAt = completedAt,
    timeSpentMs = timeSpentMs
)
