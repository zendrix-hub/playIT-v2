package com.playit.app.domain.model

enum class RiskStatus {
    GREEN,   // Mastered (Accuracy >= 80%, failed attempts < 3)
    YELLOW,  // Developing (Accuracy 50%-79%, failed attempts < 3)
    RED      // At-Risk (Accuracy < 50% or failed attempts >= 3)
}
