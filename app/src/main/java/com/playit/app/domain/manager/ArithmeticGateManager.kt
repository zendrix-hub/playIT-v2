package com.playit.app.domain.manager

import com.playit.app.domain.model.ArithmeticProblem
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArithmeticGateManager @Inject constructor() {

    fun generateProblem(): ArithmeticProblem {
        val isAddition = Random.nextBoolean()
        return if (isAddition) {
            val a = Random.nextInt(5, 15)
            val b = Random.nextInt(3, 12)
            ArithmeticProblem(
                operand1 = a,
                operand2 = b,
                operator = "+",
                expectedAnswer = a + b
            )
        } else {
            val a = Random.nextInt(10, 20)
            val b = Random.nextInt(2, 9)
            ArithmeticProblem(
                operand1 = a,
                operand2 = b,
                operator = "-",
                expectedAnswer = a - b
            )
        }
    }

    fun validateAnswer(problem: ArithmeticProblem, userAnswerInput: String): Boolean {
        val parsed = userAnswerInput.trim().toIntOrNull() ?: return false
        return parsed == problem.expectedAnswer
    }
}
