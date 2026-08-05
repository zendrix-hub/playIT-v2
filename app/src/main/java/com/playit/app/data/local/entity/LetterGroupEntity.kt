package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.playit.app.domain.model.LetterGroup

@Entity(tableName = "letter_groups")
data class LetterGroupEntity(
    @PrimaryKey
    val groupId: Int,
    val groupNumber: Int
)

fun LetterGroupEntity.toDomain(): LetterGroup = LetterGroup(
    groupId = groupId,
    groupNumber = groupNumber
)

fun LetterGroup.toEntity(): LetterGroupEntity = LetterGroupEntity(
    groupId = groupId,
    groupNumber = groupNumber
)
