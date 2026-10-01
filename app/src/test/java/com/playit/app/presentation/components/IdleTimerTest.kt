package com.playit.app.presentation.components

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IdleTimerTest {

    @Test
    fun firesAfterTimeout() = runTest {
        var calls = 0
        val timer = IdleTimer(scope = this) { calls++ }

        timer.start()
        advanceTimeBy(9_999)
        assertEquals(0, calls)

        advanceTimeBy(2)
        assertEquals(1, calls)
    }

    @Test
    fun touchResetsTheClock() = runTest {
        var calls = 0
        val timer = IdleTimer(scope = this) { calls++ }

        timer.start()
        advanceTimeBy(8_000)
        timer.touch()
        advanceTimeBy(8_000)
        assertEquals(0, calls)

        advanceTimeBy(2_001)
        assertEquals(1, calls)
    }

    @Test
    fun stopsAfterMaxPrompts() = runTest {
        var calls = 0
        val timer = IdleTimer(scope = this) { calls++ }

        timer.start()
        advanceTimeBy(60_000)
        assertEquals(IDLE_MAX_PROMPTS, calls)
    }

    @Test
    fun busyPostponesWithoutCounting() = runTest {
        var calls = 0
        var busy = true
        val timer = IdleTimer(
            scope = this,
            isBusy = { busy },
            onIdle = { calls++ }
        )

        timer.start()
        advanceTimeBy(25_000)
        busy = false
        assertEquals(0, calls)

        advanceTimeBy(5_001) // At 30s + 1ms
        assertEquals(1, calls)

        advanceTimeBy(30_000) // Past 60s
        assertEquals(3, calls)
    }

    @Test
    fun stopCancels() = runTest {
        var calls = 0
        val timer = IdleTimer(scope = this) { calls++ }

        timer.start()
        timer.stop()
        advanceTimeBy(60_000)
        assertEquals(0, calls)
    }

    @Test
    fun busyForever_givesUp() = runTest {
        var calls = 0
        val timer = IdleTimer(
            scope = this,
            isBusy = { true },
            onIdle = { calls++ }
        )

        timer.start()
        advanceUntilIdle() // Returns cleanly without endless loop
        assertEquals(0, calls)
    }
}
