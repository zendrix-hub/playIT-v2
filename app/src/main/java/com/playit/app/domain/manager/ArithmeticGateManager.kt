package com.playit.app.domain.manager

import com.playit.app.domain.model.ArithmeticProblem
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates and validates adult arithmetic gate problems guarding the Parent Dashboard.
 *
 * Implements 01_REQUIREMENTS_SUMMARY.md §1 Module 6 / §7.6:
 * - Simple 2-digit addition/subtraction problem presented upon dashboard entry.
 * - Prevents unintended access by young children (ages 6-7).
 *
 * Strictly pure Kotlin — zero android.* imports per 02_ARCHITECTURE_SUMMARY.md §3.
 */
@Singleton
class ArithmeticGateManager @Inject constructor() {

    /**
     * Generates a random adult arithmetic problem (addition or subtraction with positive results).
     * Implements 01_REQUIREMENTS_SUMMARY.md §1 Module 6 / §7.6.
     */
    fun generateProblem(): ArithmeticProblem {
        val isAddition = Random.nextBoolean()
        return if (isAddition) {
            val a = Random.nextInt(12, 45)
            val b = Random.nextInt(10, 30)
            ArithmeticProblem(
                operand1 = a,
                operand2 = b,
                operator = "+",
                expectedAnswer = a + b
            )
        } else {
            val a = Random.nextInt(25, 50)
            val b = Random.nextInt(5, 15)
            ArithmeticProblem(
                operand1 = a,
                operand2 = b,
                operator = "-",
                expectedAnswer = a - b
            )
        }
    }

    /**
     * Validates whether the user's textual input matches the expected answer for the problem.
     * Implements 01_REQUIREMENTS_SUMMARY.md §1 Module 6 / §7.6.
     *
     * @param problem The active arithmetic problem.
     * @param userAnswerInput User's typed response string.
     * @return true if parsed input equals expectedAnswer, false otherwise.
     */
    fun validateAnswer(problem: ArithmeticProblem, userAnswerInput: String): Boolean {
        val parsed = userAnswerInput.trim().toIntOrNull() ?: return false
        return parsed == problem.expectedAnswer
    }
}
