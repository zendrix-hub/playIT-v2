package com.playit.app.domain.model

data class LetterGroupMember(
    val memberId: Int = 0,
    val groupId: Int,
    val phonemeId: Int,
    val position: Int
)
