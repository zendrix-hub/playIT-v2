package com.playit.app.presentation.map

import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.audio.SfxEvent
import com.playit.app.data.audio.VoContext
import com.playit.app.domain.manager.GroupUnlockManager
import com.playit.app.domain.manager.StreakTracker
import com.playit.app.domain.manager.UnlockManager
import com.playit.app.domain.model.*
import com.playit.app.domain.repository.*
import com.playit.app.navigation.SessionManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

    private lateinit var viewModel: MapViewModel
    private val phonemeRepository: PhonemeRepository = mockk(relaxed = true)
    private val letterGroupRepository: LetterGroupRepository = mockk(relaxed = true)
    private val letterGroupMemberRepository: LetterGroupMemberRepository = mockk(relaxed = true)
    private val lessonProgressRepository: LessonProgressRepository = mockk(relaxed = true)
    private val profileRepository: ProfileRepository = mockk(relaxed = true)
    private val achievementRepository: AchievementRepository = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk(relaxed = true)
    private val unlockManager: UnlockManager = mockk(relaxed = true)
    private val groupUnlockManager: GroupUnlockManager = mockk(relaxed = true)
    private val streakTracker: StreakTracker = mockk(relaxed = true)
    private val audioPlayer: AudioPlayer = mockk(relaxed = true)
    private val audioResolver: AudioResolver = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private val activeProfileIdFlow = MutableStateFlow<Long?>(1L)

    private val testProfile = Profile(
        id = 1L,
        name = "Leo",
        avatarResId = 2,
        totalStars = 15,
        currentStreak = 4
    )

    private val testPhonemes = listOf(
        Phoneme(id = 1, letter = "m", audioPath = "m.mp3", imagePath = "m.png", exampleWord = "manok"),
        Phoneme(id = 2, letter = "s", audioPath = "s.mp3", imagePath = "s.png", exampleWord = "saging")
    )

    private val testGroups = listOf(
        LetterGroup(groupId = 1, groupNumber = 1)
    )

    private val testMembers = listOf(
        LetterGroupMember(memberId = 1, groupId = 1, phonemeId = 1, position = 1),
        LetterGroupMember(memberId = 2, groupId = 1, phonemeId = 2, position = 2)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { sessionManager.activeProfileId } returns activeProfileIdFlow
        every { profileRepository.getAllProfiles() } returns flowOf(listOf(testProfile))
        every { achievementRepository.getUnlockedAchievements(1L) } returns flowOf(listOf(mockk(), mockk()))
        every { phonemeRepository.getAllPhonemes() } returns flowOf(testPhonemes)
        every { letterGroupRepository.getAllGroups() } returns flowOf(testGroups)
        every { letterGroupMemberRepository.getAllMembers() } returns flowOf(testMembers)
        every { lessonProgressRepository.getProgressForProfile(1L) } returns flowOf(emptyList())

        every { audioResolver.getSfxPath(any()) } returns "sfx.mp3"
        every { audioResolver.getVoPath(any()) } returns "vo.mp3"
        every { audioResolver.getRotatingEncourageVo() } returns "encourage.mp3"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_recordsActivityAndResetsStreakIfInactive() = runTest {
        viewModel = MapViewModel(
            phonemeRepository, letterGroupRepository, letterGroupMemberRepository,
            lessonProgressRepository, profileRepository, achievementRepository,
            sessionManager, unlockManager, groupUnlockManager, streakTracker,
            audioPlayer, audioResolver
        )
        advanceUntilIdle()

        coVerify { streakTracker.resetIfInactive(1L) }
        coVerify { streakTracker.recordActivity(1L) }
    }

    @Test
    fun userStats_aggregatesProfileAndAchievements() = runTest {
        viewModel = MapViewModel(
            phonemeRepository, letterGroupRepository, letterGroupMemberRepository,
            lessonProgressRepository, profileRepository, achievementRepository,
            sessionManager, unlockManager, groupUnlockManager, streakTracker,
            audioPlayer, audioResolver
        )
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.userStats.collect()
        }
        advanceUntilIdle()

        val stats = viewModel.userStats.value
        assertEquals("Leo", stats.profileName)
        assertEquals(2, stats.avatarId)
        assertEquals(15, stats.totalStars)
        assertEquals(4, stats.currentStreak)
        assertEquals(2, stats.unlockedBadgesCount)

        collectJob.cancel()
    }

    @Test
    fun mapNodes_generatesLetterNodesAndBlendItNode() = runTest {
        every { unlockManager.isPhonemeUnlocked(1, any()) } returns true
        every { unlockManager.isPhonemeUnlocked(2, any()) } returns false
        every { groupUnlockManager.isBlendItUnlocked(1, any(), any()) } returns false

        viewModel = MapViewModel(
            phonemeRepository, letterGroupRepository, letterGroupMemberRepository,
            lessonProgressRepository, profileRepository, achievementRepository,
            sessionManager, unlockManager, groupUnlockManager, streakTracker,
            audioPlayer, audioResolver
        )
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.mapNodes.collect()
        }
        advanceUntilIdle()

        val nodes = viewModel.mapNodes.value
        // 2 letter nodes + 1 blend it node = 3 total nodes
        assertEquals(3, nodes.size)

        val firstNode = nodes[0] as MapNode.LetterNode
        assertEquals("1", firstNode.id)
        assertEquals("M", firstNode.symbol)
        assertTrue(firstNode.isUnlocked)

        val secondNode = nodes[1] as MapNode.LetterNode
        assertEquals("2", secondNode.id)
        assertEquals("S", secondNode.symbol)
        assertFalse(secondNode.isUnlocked)

        val thirdNode = nodes[2] as MapNode.BlendItNode
        assertEquals("blend_1", thirdNode.id)
        assertFalse(thirdNode.isUnlocked)

        collectJob.cancel()
    }

    @Test
    fun audioActions_invokeAudioPlayerCorrectly() {
        viewModel = MapViewModel(
            phonemeRepository, letterGroupRepository, letterGroupMemberRepository,
            lessonProgressRepository, profileRepository, achievementRepository,
            sessionManager, unlockManager, groupUnlockManager, streakTracker,
            audioPlayer, audioResolver
        )

        viewModel.playHeartRecoverySound()
        verify { audioPlayer.playAssetAudio("sfx.mp3") }

        viewModel.playMascotTapReaction()
        verify { audioPlayer.playSequence(listOf("sfx.mp3", "vo.mp3")) }

        viewModel.onLockedNodeTapped()
        verify { audioPlayer.playSequence(listOf("sfx.mp3", "encourage.mp3")) }
    }

    @Test
    fun clearSession_clearsActiveProfile() {
        viewModel = MapViewModel(
            phonemeRepository, letterGroupRepository, letterGroupMemberRepository,
            lessonProgressRepository, profileRepository, achievementRepository,
            sessionManager, unlockManager, groupUnlockManager, streakTracker,
            audioPlayer, audioResolver
        )

        viewModel.clearSession()
        verify { sessionManager.clearActiveProfile() }
    }
}
