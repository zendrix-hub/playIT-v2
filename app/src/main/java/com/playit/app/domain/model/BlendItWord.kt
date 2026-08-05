package com.playit.app.domain.model

data class BlendItWord(
    val wordId: Int,
    val groupId: Int,
    val word: String,
    val wordPattern: String,
    val audioPath: String,
    val imagePath: String
)
