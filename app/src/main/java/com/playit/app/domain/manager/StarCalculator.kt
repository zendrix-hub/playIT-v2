package com.playit.app.domain.manager

object StarCalculator {
    fun calculateStars(heartsLost: Int): Int {
        return when {
            heartsLost <= 0 -> 3
            heartsLost in 1..2 -> 2
            else -> 1
        }
    }
}
