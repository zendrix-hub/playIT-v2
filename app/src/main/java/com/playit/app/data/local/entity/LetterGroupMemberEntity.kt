package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.playit.app.domain.model.LetterGroupMember

@Entity(
    tableName = "letter_group_members",
    foreignKeys = [
        ForeignKey(
            entity = LetterGroupEntity::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
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
        Index(value = ["groupId"]),
        Index(value = ["phonemeId"])
    ]
)
data class LetterGroupMemberEntity(
    @PrimaryKey(autoGenerate = true)
    val memberId: Int = 0,
    val groupId: Int,
    val phonemeId: Int,
    val position: Int
)

fun LetterGroupMemberEntity.toDomain(): LetterGroupMember = LetterGroupMember(
    memberId = memberId,
    groupId = groupId,
    phonemeId = phonemeId,
    position = position
)

fun LetterGroupMember.toEntity(): LetterGroupMemberEntity = LetterGroupMemberEntity(
    memberId = memberId,
    groupId = groupId,
    phonemeId = phonemeId,
    position = position
)
