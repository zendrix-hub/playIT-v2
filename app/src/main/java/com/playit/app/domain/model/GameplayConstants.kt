package com.playit.app.domain.model

/**
 * Single source of truth for core gameplay numbers, heart thresholds, and timing constants.
 *
 * Implements 01_REQUIREMENTS_SUMMARY.md §1, §6, §7 / 11_CODING_STANDARDS.md §3.
 * Strictly pure Kotlin — zero android.* imports.
 */
object GameplayConstants {
    const val MAX_PROFILES = 6

    // Heart Pool Constants (01 §1 Modules 2-4 / §6 FR-04 / §7.5)
    const val STARTING_HEARTS = 5
    const val DEPLETED_RESTART_HEARTS = 3
    const val HEART_RECOVERY_STREAK_INTERVAL = 3

    // Gamification & Streak Milestones (01 §1 Module 5 / §6 FR-09)
    val STREAK_MILESTONES = listOf(5, 10, 15, 20)
}
