package com.playit.app.domain.manager

import com.playit.app.domain.model.GameplayConstants
import com.playit.app.domain.model.Profile
import com.playit.app.domain.repository.AchievementRepository
import com.playit.app.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages daily learning streaks and awards milestone badges at 5, 10, 15, and 20 days.
 *
 * Implements 01_REQUIREMENTS_SUMMARY.md §1 Module 5 / §6 FR-09 (Learning Streaks & Badges):
 * - Increment streak on consecutive calendar day activity.
 * - Retain streak on same-day repeat activity.
 * - Reset streak to 1 after an inactivity gap of 2+ days without affecting stars, badges, or letter progress.
 * - Unlock milestone achievements at 5, 10, 15, 20 days.
 *
 * Strictly pure Kotlin — zero android.* imports per 02_ARCHITECTURE_SUMMARY.md §3.
 */
@Singleton
class StreakTracker(
    private val profileRepository: ProfileRepository,
    private val achievementRepository: AchievementRepository,
    private val clock: () -> Long
) {
    @Inject
    constructor(
        profileRepository: ProfileRepository,
        achievementRepository: AchievementRepository
    ) : this(profileRepository, achievementRepository, { System.currentTimeMillis() })

    companion object {
        const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
    }

    /**
     * Records learning activity for the profile, computing the updated streak and awarding milestone badges.
     * Implements 01_REQUIREMENTS_SUMMARY.md §1 Module 5 / §6 FR-09.
     *
     * @param profileId Target profile identifier.
     * @return Updated Profile entity, or null if profile not found.
     */
    suspend fun recordActivity(profileId: Long): Profile? {
        val profile = profileRepository.getProfileById(profileId) ?: return null
        val now = clock()
        val lastPlayed = profile.lastPlayedAt

        val newStreak = if (lastPlayed == 0L) {
            1
        } else {
            val daysSince = (now / MILLIS_PER_DAY) - (lastPlayed / MILLIS_PER_DAY)
            when {
                daysSince <= 0L -> profile.currentStreak.coerceAtLeast(1)
                daysSince == 1L -> profile.currentStreak + 1
                else -> 1 // Gap of 2+ days -> reset streak to 1
            }
        }

        val updatedProfile = profile.copy(
            currentStreak = newStreak,
            lastPlayedAt = now
        )
        profileRepository.updateProfile(updatedProfile)

        // Check and award milestone badge if reached
        if (GameplayConstants.STREAK_MILESTONES.contains(newStreak)) {
            val badgeTitle = "$newStreak-Day Streak"
            val existing = achievementRepository.getAchievementByTitle(profileId, badgeTitle)
            if (existing == null || !existing.isUnlocked) {
                achievementRepository.unlockAchievement(profileId, badgeTitle, now)
            }
        }

        return updatedProfile
    }

    /**
     * Checks if a profile has been inactive for 2+ calendar days and resets current streak to 0.
     * Preserves all earned stars, unlocked badges, and letter mastery per 01_REQUIREMENTS_SUMMARY.md §6 FR-09.
     *
     * @param profileId Target profile identifier.
     * @return Updated Profile with streak reset if inactive, or current profile otherwise.
     */
    suspend fun resetIfInactive(profileId: Long): Profile? {
        val profile = profileRepository.getProfileById(profileId) ?: return null
        val now = clock()
        if (profile.lastPlayedAt > 0L) {
            val daysSince = (now / MILLIS_PER_DAY) - (profile.lastPlayedAt / MILLIS_PER_DAY)
            if (daysSince >= 2L && profile.currentStreak > 0) {
                val resetProfile = profile.copy(currentStreak = 0)
                profileRepository.updateProfile(resetProfile)
                return resetProfile
            }
        }
        return profile
    }

    /**
     * Helper to retrieve badge title for milestone streaks.
     * Implements 01_REQUIREMENTS_SUMMARY.md §1 Module 5 / §6 FR-09.
     */
    fun checkMilestone(streak: Int): String? {
        return if (GameplayConstants.STREAK_MILESTONES.contains(streak)) "$streak-Day Streak" else null
    }
}
