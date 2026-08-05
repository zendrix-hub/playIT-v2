package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "find_it_attempts",
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
        Index(value = ["profileId", "phonemeId"]),
        Index(value = ["phonemeId"])
    ]
)
data class FindItAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val attemptId: Int = 0,
    val profileId: Long,
    val phonemeId: Int,
    val selectedPhonemeId: Int,
    val isCorrect: Boolean,
    val attemptedAt: Long = System.currentTimeMillis()
)
