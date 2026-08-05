package com.playit.app.domain.manager

object BlendItStarThresholds {

    fun calculateStars(_groupId: Int, totalHeartsLost: Int): Int {

        return when {
            totalHeartsLost <= 0 -> 3
            totalHeartsLost in 1..2 -> 2
            else -> 1
        }
    }
}
