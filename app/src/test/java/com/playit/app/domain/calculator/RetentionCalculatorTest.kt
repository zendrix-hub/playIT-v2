package com.playit.app.domain.calculator

import com.playit.app.domain.model.FindItAttempt
import com.playit.app.domain.model.SayItAttempt
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RetentionCalculatorTest {

    private lateinit var retentionCalculator: RetentionCalculator

    @Before
    fun setUp() {
        retentionCalculator = RetentionCalculator()
    }

    @Test
    fun `no attempts in 7-day window returns 0 retention`() {
        val now = 1_000_000_000L
        val oldAttempt = SayItAttempt(profileId = 1L, phonemeId = 1, isCorrect = true, attemptedAt = now - (10L * 24 * 60 * 60 * 1000))
        val retention = retentionCalculator.calculate7DayRetention(listOf(oldAttempt), emptyList(), now)
        assertEquals(0f, retention, 0.01f)
    }

    @Test
    fun `calculates percentage correctly for attempts within 7 days`() {
        val now = 1_000_000_000L
        val recentSayIt1 = SayItAttempt(profileId = 1L, phonemeId = 1, isCorrect = true, attemptedAt = now - (1L * 24 * 60 * 60 * 1000))
        val recentSayIt2 = SayItAttempt(profileId = 1L, phonemeId = 1, isCorrect = false, attemptedAt = now - (2L * 24 * 60 * 60 * 1000))
        val recentFindIt = FindItAttempt(profileId = 1L, phonemeId = 1, selectedPhonemeId = 1, isCorrect = true, attemptedAt = now - (3L * 24 * 60 * 60 * 1000))

        val retention = retentionCalculator.calculate7DayRetention(
            sayItAttempts = listOf(recentSayIt1, recentSayIt2),
            findItAttempts = listOf(recentFindIt),
            currentTimeMillis = now
        )

        // 2 correct out of 3 total = 66.66%
        assertEquals(66.66f, retention, 0.1f)
    }
}
