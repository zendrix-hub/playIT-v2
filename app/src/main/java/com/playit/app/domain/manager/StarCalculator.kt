package com.playit.app.domain.manager

object StarCalculator {
    fun calculateStars(heartsLost: Int, accuracyPercent: Float = 100f): Int {
        return when {
            heartsLost <= 0 && accuracyPercent >= 100f -> 3
            heartsLost <= 2 && accuracyPercent >= 80f -> 2
            else -> 1
        }
    }
}
