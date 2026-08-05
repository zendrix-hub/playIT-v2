package com.playit.app.domain.model

data class ArithmeticProblem(
    val operand1: Int,
    val operand2: Int,
    val operator: String, // "+" or "-"
    val expectedAnswer: Int
) {
    val displayExpression: String
        get() = "$operand1 $operator $operand2 = ?"
}
