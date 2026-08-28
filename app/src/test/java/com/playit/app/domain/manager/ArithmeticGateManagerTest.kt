package com.playit.app.domain.manager

import org.junit.Assert.assertEquals
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
    fun generateProblem_producesValidProblem_andExpectedAnswerValidatesCorrectly() {
        // Run 50 iterations to ensure both addition and subtraction branches are thoroughly tested
        for (i in 1..50) {
            val problem = manager.generateProblem()
            if (problem.operator == "+") {
                assertEquals(problem.operand1 + problem.operand2, problem.expectedAnswer)
            } else {
                assertEquals(problem.operand1 - problem.operand2, problem.expectedAnswer)
                // Guaranteed positive result
                assertTrue("Subtraction result must be positive", problem.expectedAnswer > 0)
            }
            assertTrue(manager.validateAnswer(problem, problem.expectedAnswer.toString()))
        }
    }

    @Test
    fun validateAnswer_trimsLeadingAndTrailingWhitespace() {
        val problem = manager.generateProblem()
        val paddedAnswer = "   ${problem.expectedAnswer}   "
        assertTrue(manager.validateAnswer(problem, paddedAnswer))
    }

    @Test
    fun incorrectAnswer_isRejected() {
        val problem = manager.generateProblem()
        val wrongAnswer = (problem.expectedAnswer + 99).toString()
        assertFalse(manager.validateAnswer(problem, wrongAnswer))
    }

    @Test
    fun nonNumericOrEmptyString_isRejected() {
        val problem = manager.generateProblem()
        assertFalse(manager.validateAnswer(problem, ""))
        assertFalse(manager.validateAnswer(problem, "   "))
        assertFalse(manager.validateAnswer(problem, "abc"))
        assertFalse(manager.validateAnswer(problem, "12a"))
    }
}
