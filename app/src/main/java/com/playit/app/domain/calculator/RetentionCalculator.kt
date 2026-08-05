package com.playit.app.domain.calculator

import com.playit.app.domain.model.FindItAttempt
import com.playit.app.domain.model.SayItAttempt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetentionCalculator @Inject constructor() {

    fun calculate7DayRetention(
        sayItAttempts: List<SayItAttempt>,
        findItAttempts: List<FindItAttempt>,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): Float {
        val sevenDaysAgo = currentTimeMillis - (7L * 24 * 60 * 60 * 1000)

        val recentSayIt = sayItAttempts.filter { it.attemptedAt >= sevenDaysAgo }
        val recentFindIt = findItAttempts.filter { it.attemptedAt >= sevenDaysAgo }

        val totalRecent = recentSayIt.size + recentFindIt.size
        if (totalRecent == 0) return 0f

        val correctRecent = recentSayIt.count { it.isCorrect } + recentFindIt.count { it.isCorrect }
        return (correctRecent.toFloat() / totalRecent.toFloat()) * 100f
    }
}
