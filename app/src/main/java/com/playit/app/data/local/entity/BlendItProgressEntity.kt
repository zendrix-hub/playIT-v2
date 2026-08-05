package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.playit.app.domain.model.BlendItProgress

@Entity(
    tableName = "blend_it_progress",
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
        )
    ],
    indices = [
        Index(value = ["profileId", "groupId"], unique = true),
        Index(value = ["profileId"]),
        Index(value = ["groupId"])
    ]
)
data class BlendItProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val profileId: Long,
    val groupId: Int,
    val starsEarned: Int = 0,
    val heartsLost: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long = 0L
)

fun BlendItProgressEntity.toDomain(): BlendItProgress = BlendItProgress(
    id = id,
    profileId = profileId,
    groupId = groupId,
    starsEarned = starsEarned,
    heartsLost = heartsLost,
    isCompleted = isCompleted,
    completedAt = completedAt
)

fun BlendItProgress.toEntity(): BlendItProgressEntity = BlendItProgressEntity(
    id = id,
    profileId = profileId,
    groupId = groupId,
    starsEarned = starsEarned,
    heartsLost = heartsLost,
    isCompleted = isCompleted,
    completedAt = completedAt
)
