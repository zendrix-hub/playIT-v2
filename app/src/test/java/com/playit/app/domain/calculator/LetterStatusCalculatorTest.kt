package com.playit.app.domain.calculator

import com.playit.app.domain.model.RiskStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LetterStatusCalculatorTest {

    private lateinit var calculator: LetterStatusCalculator

    @Before
    fun setUp() {
        calculator = LetterStatusCalculator()
    }

    @Test
    fun `accuracy 80 percent or higher with low failed attempts returns GREEN`() {
        val status = calculator.calculateStatus(accuracyPercentage = 85f, failedAttempts = 1)
        assertEquals(RiskStatus.GREEN, status)
    }

    @Test
    fun `accuracy between 50 and 79 percent with low failed attempts returns YELLOW`() {
        val status = calculator.calculateStatus(accuracyPercentage = 65f, failedAttempts = 2)
        assertEquals(RiskStatus.YELLOW, status)
    }

    @Test
    fun `accuracy under 50 percent returns RED`() {
        val status = calculator.calculateStatus(accuracyPercentage = 40f, failedAttempts = 0)
        assertEquals(RiskStatus.RED, status)
    }

    @Test
    fun `3 or more failed attempts returns RED even with high accuracy`() {
        val status = calculator.calculateStatus(accuracyPercentage = 85f, failedAttempts = 3)
        assertEquals(RiskStatus.RED, status)
    }
}
