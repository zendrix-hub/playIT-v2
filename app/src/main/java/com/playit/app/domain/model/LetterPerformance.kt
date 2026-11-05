package com.playit.app.domain.model

/**
 * One letter's row in a learner's record: what the child did in each activity and the level it adds up to.
 * [score] is the rounded share of right answers in Say It and Find It (the percentage score), [grade] that share
 * transmuted as in a DepEd class record; both are null while there is nothing to score.
 */
data class LetterPerformance(
    val phonemeId: Int,
    val symbol: String,
    val name: String,
    val keyWord: String = "",
    val chapter: Int = 0,
    val sayItCorrect: Int = 0,
    val sayItAttempts: Int = 0,
    val findItCorrect: Int = 0,
    val findItAttempts: Int = 0,
    val heartsLost: Int,
    val starsEarned: Int,
    val isCompleted: Boolean,
    val score: Int?,
    val grade: Int?,
    val level: ProgressLevel
) {
    val totalAttempts: Int get() = sayItAttempts + findItAttempts
    val failedAttempts: Int get() = totalAttempts - sayItCorrect - findItCorrect
}
