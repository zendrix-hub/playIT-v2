package com.playit.app.domain.model

data class Phoneme(
    val id: Int,
    val letter: String,
    val audioPath: String,
    val imagePath: String,
    val exampleWord: String
)
