package com.playit.app.domain.manager

object BlendItStarThresholds {

    @Suppress("UNUSED_PARAMETER")
    fun calculateStars(
        groupId: Int,
        totalHeartsLost: Int,
        wordsCorrect: Int = 5,
        totalWords: Int = 5
    ): Int {
        val accuracy = if (totalWords > 0) wordsCorrect.toFloat() / totalWords else 0f
        return when {
            totalHeartsLost <= 0 && accuracy >= 1.0f -> 3
            totalHeartsLost <= 2 && accuracy >= 0.8f -> 2
            else -> 1
        }
    }
}
