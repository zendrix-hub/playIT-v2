package com.playit.app.domain.manager

import com.playit.app.domain.model.GameplayConstants

class HeartManager(
    var initialHearts: Int = GameplayConstants.STARTING_HEARTS,
    val maxCap: Int = initialHearts
) {
    var currentHearts: Int = initialHearts
        private set

    var heartsLost: Int = 0
        private set

    val isGameOver: Boolean
        get() = currentHearts <= 0

    fun deductHeart(): Boolean {
        if (currentHearts > 0) {
            currentHearts--
            heartsLost++
        }
        return isGameOver
    }

    /**
     * Attempts to recover +1 heart if current hearts is less than starting pool cap (01 §7.5).
     */
    fun recoverHeart(): Boolean {
        val effectiveCap = minOf(maxCap, initialHearts)
        if (currentHearts < effectiveCap) {
            currentHearts++
            return true
        }
        return false
    }

    /**
     * Checks if 3 consecutive correct answers trigger a heart recovery.
     * Capped at the session's starting pool size per 01 §7.5.
     */
    fun checkRecovery(consecutiveCorrect: Int): Boolean {
        if (consecutiveCorrect > 0 && consecutiveCorrect % 3 == 0) {
            return recoverHeart()
        }
        return false
    }

    fun getHearts(): Int = currentHearts

    fun resetForRestart(newPool: Int = GameplayConstants.STARTING_HEARTS) {
        initialHearts = newPool
        currentHearts = newPool
        heartsLost = 0
    }

    fun reset() {
        currentHearts = initialHearts
        heartsLost = 0
    }
}
