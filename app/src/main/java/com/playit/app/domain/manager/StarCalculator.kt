package com.playit.app.domain.manager

object StarCalculator {
    fun calculateStars(heartsLost: Int, accuracyPercent: Float = 100f): Int {
        val normalizedAccuracy = if (accuracyPercent in 0.001f..1.0f) accuracyPercent * 100f else accuracyPercent
        return when {
            heartsLost <= 0 && normalizedAccuracy >= 100f -> 3
            heartsLost <= 2 && normalizedAccuracy >= 80f -> 2
            else -> 1
        }
    }
}
