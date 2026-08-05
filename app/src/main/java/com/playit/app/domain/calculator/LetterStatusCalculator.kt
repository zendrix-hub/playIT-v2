package com.playit.app.domain.calculator

import com.playit.app.domain.model.RiskStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LetterStatusCalculator @Inject constructor() {
    fun calculateStatus(accuracyPercentage: Float, failedAttempts: Int): RiskStatus {
        return when {
            accuracyPercentage < 50f || failedAttempts >= 3 -> RiskStatus.RED
            accuracyPercentage >= 80f -> RiskStatus.GREEN
            else -> RiskStatus.YELLOW
        }
    }
}
