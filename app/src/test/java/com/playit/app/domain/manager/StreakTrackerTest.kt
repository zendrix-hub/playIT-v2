package com.playit.app.domain.manager

import com.playit.app.domain.model.Achievement
import com.playit.app.domain.model.Profile
import com.playit.app.domain.repository.AchievementRepository
import com.playit.app.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class StreakTrackerTest {

    private lateinit var fakeProfileRepository: FakeProfileRepository
    private lateinit var fakeAchievementRepository: FakeAchievementRepository
    private var simulatedTime: Long = 1000000000000L // arbitrary fixed starting time

    @Before
    fun setUp() {
        fakeProfileRepository = FakeProfileRepository()
        fakeAchievementRepository = FakeAchievementRepository()
    }

    private fun createTracker(): StreakTracker {
        return StreakTracker(
            profileRepository = fakeProfileRepository,
            achievementRepository = fakeAchievementRepository,
            clock = { simulatedTime }
        )
    }

    @Test
    fun firstActivity_setsStreakToOne() = runBlocking {
        val profile = Profile(id = 1, name = "Learner", avatarResId = 1, lastPlayedAt = 0L, currentStreak = 0)
        fakeProfileRepository.profiles[1L] = profile

        val tracker = createTracker()
        val updated = tracker.recordActivity(1L)

        assertNotNull(updated)
        assertEquals(1, updated?.currentStreak)
        assertEquals(simulatedTime, updated?.lastPlayedAt)
    }

    @Test
    fun sameDayActivity_retainsCurrentStreak() = runBlocking {
        val profile = Profile(id = 1, name = "Learner", avatarResId = 1, lastPlayedAt = simulatedTime, currentStreak = 3)
        fakeProfileRepository.profiles[1L] = profile

        val tracker = createTracker()
        // Activity 2 hours later on same day
        simulatedTime += 2 * 60 * 60 * 1000L
        val updated = tracker.recordActivity(1L)

        assertEquals(3, updated?.currentStreak)
    }

    @Test
    fun consecutiveDayActivity_incrementsStreak() = runBlocking {
        val profile = Profile(id = 1, name = "Learner", avatarResId = 1, lastPlayedAt = simulatedTime, currentStreak = 3)
        fakeProfileRepository.profiles[1L] = profile

        val tracker = createTracker()
        // Activity 24 hours later (next day)
        simulatedTime += 24 * 60 * 60 * 1000L
        val updated = tracker.recordActivity(1L)

        assertEquals(4, updated?.currentStreak)
    }

    @Test
    fun inactivityGap_resetsStreakToOne_onActivity() = runBlocking {
        val profile = Profile(id = 1, name = "Learner", avatarResId = 1, lastPlayedAt = simulatedTime, currentStreak = 10)
        fakeProfileRepository.profiles[1L] = profile

        val tracker = createTracker()
        // Activity 48+ hours later (missed a full day)
        simulatedTime += 50 * 60 * 60 * 1000L
        val updated = tracker.recordActivity(1L)

        assertEquals(1, updated?.currentStreak)
    }

    @Test
    fun milestoneBadges_unlockedAt5_10_15_20() = runBlocking {
        val tracker = createTracker()

        // Test 5-day badge
        val profile5 = Profile(id = 1, name = "Learner", avatarResId = 1, lastPlayedAt = simulatedTime, currentStreak = 4)
        fakeProfileRepository.profiles[1L] = profile5
        simulatedTime += 24 * 60 * 60 * 1000L
        tracker.recordActivity(1L)
        val badge5 = fakeAchievementRepository.getAchievementByTitle(1L, "5-Day Streak")
        assertNotNull(badge5)
        assertEquals(true, badge5?.isUnlocked)

        // Test 10-day badge
        val profile10 = Profile(id = 2, name = "Learner2", avatarResId = 1, lastPlayedAt = simulatedTime, currentStreak = 9)
        fakeProfileRepository.profiles[2L] = profile10
        simulatedTime += 24 * 60 * 60 * 1000L
        tracker.recordActivity(2L)
        val badge10 = fakeAchievementRepository.getAchievementByTitle(2L, "10-Day Streak")
        assertNotNull(badge10)
        assertEquals(true, badge10?.isUnlocked)

        // Test 15-day badge
        val profile15 = Profile(id = 3, name = "Learner3", avatarResId = 1, lastPlayedAt = simulatedTime, currentStreak = 14)
        fakeProfileRepository.profiles[3L] = profile15
        simulatedTime += 24 * 60 * 60 * 1000L
        tracker.recordActivity(3L)
        val badge15 = fakeAchievementRepository.getAchievementByTitle(3L, "15-Day Streak")
        assertNotNull(badge15)
        assertEquals(true, badge15?.isUnlocked)

        // Test 20-day badge
        val profile20 = Profile(id = 4, name = "Learner4", avatarResId = 1, lastPlayedAt = simulatedTime, currentStreak = 19)
        fakeProfileRepository.profiles[4L] = profile20
        simulatedTime += 24 * 60 * 60 * 1000L
        tracker.recordActivity(4L)
        val badge20 = fakeAchievementRepository.getAchievementByTitle(4L, "20-Day Streak")
        assertNotNull(badge20)
        assertEquals(true, badge20?.isUnlocked)
    }

    @Test
    fun checkMilestone_returnsFormattedTitleForMilestonesOnly() {
        val tracker = createTracker()
        assertEquals("5-Day Streak", tracker.checkMilestone(5))
        assertEquals("10-Day Streak", tracker.checkMilestone(10))
        assertEquals("15-Day Streak", tracker.checkMilestone(15))
        assertEquals("20-Day Streak", tracker.checkMilestone(20))
        assertNull(tracker.checkMilestone(4))
        assertNull(tracker.checkMilestone(6))
        assertNull(tracker.checkMilestone(25))
    }

    @Test
    fun resetIfInactive_resetsStreakToZero_withoutAffectingStars() = runBlocking {
        val profile = Profile(
            id = 1,
            name = "Learner",
            avatarResId = 1,
            totalStars = 15,
            lastPlayedAt = simulatedTime,
            currentStreak = 7
        )
        fakeProfileRepository.profiles[1L] = profile

        val tracker = createTracker()
        // 50 hours pass without activity
        simulatedTime += 50 * 60 * 60 * 1000L

        val reset = tracker.resetIfInactive(1L)
        assertEquals(0, reset?.currentStreak)
        assertEquals(15, reset?.totalStars) // Stars preserved!
    }
}

private class FakeProfileRepository : ProfileRepository {
    val profiles = mutableMapOf<Long, Profile>()

    override fun getAllProfiles(): Flow<List<Profile>> = flowOf(profiles.values.toList())

    override suspend fun getProfileById(id: Long): Profile? = profiles[id]

    override suspend fun createProfile(name: String, avatarResId: Int): Result<Long> {
        val newId = (profiles.keys.maxOrNull() ?: 0L) + 1
        val profile = Profile(id = newId, name = name, avatarResId = avatarResId)
        profiles[newId] = profile
        return Result.success(newId)
    }

    override suspend fun updateProfile(profile: Profile) {
        profiles[profile.id] = profile
    }

    override suspend fun deleteProfile(profile: Profile) {
        profiles.remove(profile.id)
    }
}

private class FakeAchievementRepository : AchievementRepository {
    val achievements = mutableListOf<Achievement>()

    override fun getAchievements(profileId: Long): Flow<List<Achievement>> {
        return flowOf(achievements.filter { it.profileId == profileId })
    }

    override fun getUnlockedAchievements(profileId: Long): Flow<List<Achievement>> {
        return flowOf(achievements.filter { it.profileId == profileId && it.isUnlocked })
    }

    override suspend fun getAchievementByTitle(profileId: Long, title: String): Achievement? {
        return achievements.find { it.profileId == profileId && it.title == title }
    }

    override suspend fun unlockAchievement(
        profileId: Long,
        title: String,
        unlockedAt: Long
    ): Achievement {
        val existing = achievements.find { it.profileId == profileId && it.title == title }
        if (existing != null) {
            val updated = existing.copy(isUnlocked = true, unlockedAt = unlockedAt)
            achievements.remove(existing)
            achievements.add(updated)
            return updated
        } else {
            val newAchievement = Achievement(
                id = achievements.size + 1L,
                profileId = profileId,
                title = title,
                isUnlocked = true,
                unlockedAt = unlockedAt
            )
            achievements.add(newAchievement)
            return newAchievement
        }
    }
}
