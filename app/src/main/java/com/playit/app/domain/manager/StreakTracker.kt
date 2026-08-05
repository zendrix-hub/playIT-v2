package com.playit.app.domain.manager

import com.playit.app.domain.model.Profile
import com.playit.app.domain.repository.AchievementRepository
import com.playit.app.domain.repository.ProfileRepository
import javax.inject.Inject

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
        val MILESTONES = listOf(5, 10, 15, 20)
    }

    suspend fun recordActivity(profileId: Long): Profile? {
        val profile = profileRepository.getProfileById(profileId) ?: return null
        val now = clock()
        val lastPlayed = profile.lastPlayedAt

        val newStreak = if (lastPlayed == 0L) {
            1
        } else {
            val daysSince = (now / MILLIS_PER_DAY) - (lastPlayed / MILLIS_PER_DAY)
            when {
                daysSince == 0L -> profile.currentStreak.coerceAtLeast(1)
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
        if (MILESTONES.contains(newStreak)) {
            val badgeTitle = "$newStreak-Day Streak"
            val existing = achievementRepository.getAchievementByTitle(profileId, badgeTitle)
            if (existing == null || !existing.isUnlocked) {
                achievementRepository.unlockAchievement(profileId, badgeTitle, now)
            }
        }

        return updatedProfile
    }

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

    fun checkMilestone(streak: Int): String? {
        return if (MILESTONES.contains(streak)) "$streak-Day Streak" else null
    }
}
