package com.playit.app.domain.manager

import com.playit.app.domain.model.GameplayConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HeartManagerTest {

    private lateinit var heartManager: HeartManager

    @Before
    fun setUp() {
        heartManager = HeartManager(initialHearts = GameplayConstants.STARTING_HEARTS)
    }

    @Test
    fun startsAtStartingPool() {
        assertEquals(5, heartManager.getHearts())
        assertEquals(0, heartManager.heartsLost)
        assertFalse(heartManager.isGameOver)
    }

    @Test
    fun deductHeart_decrementsCountAndIncrementsHeartsLost() {
        val isGameOver = heartManager.deductHeart()
        assertEquals(4, heartManager.getHearts())
        assertEquals(1, heartManager.heartsLost)
        assertFalse(isGameOver)
    }

    @Test
    fun deductHeart_signalsGameOverWhenZero() {
        heartManager = HeartManager(initialHearts = 1)
        val isGameOver = heartManager.deductHeart()
        assertEquals(0, heartManager.getHearts())
        assertTrue(isGameOver)
        assertTrue(heartManager.isGameOver)

        // Further deduction should remain 0 and return gameOver
        val subsequentGameOver = heartManager.deductHeart()
        assertEquals(0, heartManager.getHearts())
        assertTrue(subsequentGameOver)
    }

    @Test
    fun restartAtReducedPoolOnDepletion() {
        heartManager.deductHeart()
        heartManager.resetForRestart(newPool = GameplayConstants.DEPLETED_RESTART_HEARTS)
        assertEquals(3, heartManager.getHearts())
        assertEquals(3, heartManager.initialHearts)
        assertEquals(0, heartManager.heartsLost)
    }

    @Test
    fun reset_restoresInitialStartingPool() {
        heartManager.deductHeart()
        heartManager.deductHeart()
        assertEquals(3, heartManager.getHearts())
        assertEquals(2, heartManager.heartsLost)

        heartManager.reset()
        assertEquals(5, heartManager.getHearts())
        assertEquals(0, heartManager.heartsLost)
    }

    @Test
    fun checkRecovery_addsHeartEveryThreeConsecutiveCorrect() {
        heartManager = HeartManager(initialHearts = 5)
        heartManager.deductHeart() // hearts = 4

        assertFalse(heartManager.checkRecovery(consecutiveCorrect = 1))
        assertFalse(heartManager.checkRecovery(consecutiveCorrect = 2))
        assertTrue(heartManager.checkRecovery(consecutiveCorrect = 3)) // hearts = 5
        assertEquals(5, heartManager.getHearts())
    }

    @Test
    fun recoveryCapsAtStartingPool_recommendedRule() {
        // Spec 01 §7.5: Recovery never exceeds the session's starting pool
        heartManager = HeartManager(initialHearts = 3)
        assertFalse(heartManager.recoverHeart())
        assertEquals(3, heartManager.getHearts())

        // Even with consecutive correct, recovery should not exceed initial pool of 3
        assertFalse(heartManager.checkRecovery(consecutiveCorrect = 3))
        assertEquals(3, heartManager.getHearts())
    }
}
