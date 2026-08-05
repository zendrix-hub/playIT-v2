package com.playit.app.domain.manager

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ArithmeticGateManagerTest {

    private lateinit var manager: ArithmeticGateManager

    @Before
    fun setUp() {
        manager = ArithmeticGateManager()
    }

    @Test
    fun `generateProblem produces valid problem and expected answer validates correctly`() {
        val problem = manager.generateProblem()
        assertTrue(manager.validateAnswer(problem, problem.expectedAnswer.toString()))
    }

    @Test
    fun `incorrect answer is rejected`() {
        val problem = manager.generateProblem()
        val wrongAnswer = (problem.expectedAnswer + 99).toString()
        assertFalse(manager.validateAnswer(problem, wrongAnswer))
    }

    @Test
    fun `non numeric string is rejected`() {
        val problem = manager.generateProblem()
        assertFalse(manager.validateAnswer(problem, "abc"))
    }
}
