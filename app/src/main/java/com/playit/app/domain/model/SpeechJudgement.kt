package com.playit.app.domain.model

enum class SpeechErrorType {
    NONE,
    LETTER_NAME,
    ADDED_VOWEL,
    SUBSTITUTION,
    OTHER_WORD,
    NO_SPEECH,
    UNCONFIRMED
}

data class SpeechJudgement(
    val isCorrect: Boolean,
    val errorType: SpeechErrorType,
    val heard: String
)
