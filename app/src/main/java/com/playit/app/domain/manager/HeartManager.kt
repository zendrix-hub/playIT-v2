package com.playit.app.domain.manager

import com.playit.app.domain.model.GameplayConstants

/**
 * Manages heart pool depletion, restart pool reduction, and 3-consecutive-correct heart recovery.
 *
 * Implements 01_REQUIREMENTS_SUMMARY.md §1 Modules 2, 3 / §6 FR-04 (Depletion & Restart)
 * and §1 Module 5 / §6 FR-07 / §7.5 (Recovery cap at starting pool size).
 *
 * Strictly pure Kotlin — zero android.* imports per 02_ARCHITECTURE_SUMMARY.md §3.
 */
class HeartManager(
    var initialHearts: Int = GameplayConstants.STARTING_HEARTS,
    val maxCap: Int = initialHearts
) {
    var currentHearts: Int = initialHearts
        private set

    var heartsLost: Int = 0
        private set

    /**
     * Whether the player has lost all hearts and triggered a game over condition.
     * Implements 01_REQUIREMENTS_SUMMARY.md §6 FR-04.
     */
    val isGameOver: Boolean
        get() = currentHearts <= 0

    /**
     * Deducts 1 heart on incorrect attempt.
     * Implements 01_REQUIREMENTS_SUMMARY.md §6 FR-04.
     *
     * @return true if the deduction caused a game over (currentHearts <= 0).
     */
    fun deductHeart(): Boolean {
        if (currentHearts > 0) {
            currentHearts--
            heartsLost++
        }
        return isGameOver
    }

    /**
     * Attempts to recover +1 heart if current hearts is less than starting pool cap.
     * Implements 01_REQUIREMENTS_SUMMARY.md §1 Module 5 / §6 FR-07 / §7.5.
     *
     * @return true if a heart was successfully recovered.
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
     * Checks if consecutive correct answers hit the recovery interval threshold.
     * Capped at the session's starting pool size per 01_REQUIREMENTS_SUMMARY.md §7.5.
     *
     * @param consecutiveCorrect Count of consecutive correct answers.
     * @return true if a heart was recovered.
     */
    fun checkRecovery(consecutiveCorrect: Int): Boolean {
        if (consecutiveCorrect > 0 && consecutiveCorrect % GameplayConstants.HEART_RECOVERY_STREAK_INTERVAL == 0) {
            return recoverHeart()
        }
        return false
    }

    /**
     * Returns the current remaining hearts in the pool.
     */
    fun getHearts(): Int = currentHearts

    /**
     * Resets heart pool for a restart after game over, reducing pool from 5 to 3 hearts.
     * Implements 01_REQUIREMENTS_SUMMARY.md §1 Modules 2, 3 / §6 FR-04.
     */
    fun resetForRestart(newPool: Int = GameplayConstants.DEPLETED_RESTART_HEARTS) {
        initialHearts = newPool
        currentHearts = newPool
        heartsLost = 0
    }

    /**
     * Resets heart pool back to its initial configured capacity.
     */
    fun reset() {
        currentHearts = initialHearts
        heartsLost = 0
    }
}
