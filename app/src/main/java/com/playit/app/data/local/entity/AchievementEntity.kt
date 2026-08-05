package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.playit.app.domain.model.Achievement

@Entity(
    tableName = "achievements",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["profileId"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("profileId")]
)
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true)
    val achievementId: Long = 0,
    val profileId: Long,
    val title: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)

fun AchievementEntity.toDomain(): Achievement = Achievement(
    id = achievementId,
    profileId = profileId,
    title = title,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt
)

fun Achievement.toEntity(): AchievementEntity = AchievementEntity(
    achievementId = id,
    profileId = profileId,
    title = title,
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt
)
