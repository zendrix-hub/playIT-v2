package com.playit.app.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.audio.SfxEvent
import com.playit.app.data.audio.VoContext
import com.playit.app.domain.manager.GroupUnlockManager
import com.playit.app.domain.manager.StreakTracker
import com.playit.app.domain.manager.UnlockManager
import com.playit.app.domain.model.MapNode
import com.playit.app.domain.repository.AchievementRepository
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.LetterGroupMemberRepository
import com.playit.app.domain.repository.LetterGroupRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.ProfileRepository
import com.playit.app.navigation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserMapStats(
    val profileName: String = "",
    val totalStars: Int = 0,
    val currentStreak: Int = 0,
    val unlockedBadgesCount: Int = 0
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val phonemeRepository: PhonemeRepository,
    private val letterGroupRepository: LetterGroupRepository,
    private val letterGroupMemberRepository: LetterGroupMemberRepository,
    private val lessonProgressRepository: LessonProgressRepository,
    private val profileRepository: ProfileRepository,
    private val achievementRepository: AchievementRepository,
    private val sessionManager: SessionManager,
    private val unlockManager: UnlockManager,
    private val groupUnlockManager: GroupUnlockManager,
    private val streakTracker: StreakTracker,
    private val audioPlayer: AudioPlayer,
    private val audioResolver: AudioResolver
) : ViewModel() {

    val activeProfileId: StateFlow<Long?> = sessionManager.activeProfileId

    init {
        viewModelScope.launch {
            activeProfileId.collect { profileId ->
                if (profileId != null) {
                    streakTracker.resetIfInactive(profileId)
                    streakTracker.recordActivity(profileId)
                    triggerReturnWelcomeIfNeeded()
                }
            }
        }
    }

    private fun triggerReturnWelcomeIfNeeded() {
        if (!sessionManager.hasPlayedReturnWelcome) {
            sessionManager.markReturnWelcomePlayed()
            val welcomeVo = audioResolver.getVoPath(VoContext.RETURN_WELCOME_01)
            audioPlayer.playAssetAudio(welcomeVo)
        }
    }

    fun playHeartRecoverySound() {
        val sfx = audioResolver.getSfxPath(SfxEvent.HEART_RECOVERY_SPARKLE)
        audioPlayer.playAssetAudio(sfx)
    }

    fun playMascotTapReaction() {
        val sfx = audioResolver.getSfxPath(SfxEvent.NODE_UNLOCK_CHIME)
        val vo = audioResolver.getRotatingEncourageVo()
        audioPlayer.playSequence(listOf(sfx, vo))
    }

    fun onLockedNodeTapped() {
        val sfx = audioResolver.getSfxPath(SfxEvent.INCORRECT_POP)
        val vo = audioResolver.getRotatingEncourageVo()
        audioPlayer.playSequence(listOf(sfx, vo))
    }

    fun clearSession() {
        sessionManager.clearActiveProfile()
    }

    val userStats: StateFlow<UserMapStats> = activeProfileId.flatMapLatest { profileId ->
        if (profileId == null) return@flatMapLatest flowOf(UserMapStats())

        combine(
            profileRepository.getAllProfiles().map { list -> list.find { it.id == profileId } },
            achievementRepository.getUnlockedAchievements(profileId)
        ) { profile, unlockedAchievements ->
            UserMapStats(
                profileName = profile?.name ?: "",
                totalStars = profile?.totalStars ?: 0,
                currentStreak = profile?.currentStreak ?: 0,
                unlockedBadgesCount = unlockedAchievements.size
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserMapStats()
    )

    val mapNodes: StateFlow<List<MapNode>> = activeProfileId.flatMapLatest { profileId ->
        if (profileId == null) return@flatMapLatest flowOf(emptyList())

        combine(
            phonemeRepository.getAllPhonemes(),
            letterGroupRepository.getAllGroups(),
            letterGroupMemberRepository.getAllMembers(),
            lessonProgressRepository.getProgressForProfile(profileId)
        ) { phonemes, groups, members, progressList ->
            val nodesList = mutableListOf<MapNode>()
            var globalIndex = 0

            groups.sortedBy { it.groupNumber }.forEach { group ->
                val groupMembers = members.filter { it.groupId == group.groupId }.sortedBy { it.position }
                groupMembers.forEach { member ->
                    val phoneme = phonemes.find { it.id == member.phonemeId }
                    if (phoneme != null) {
                        val progress = progressList.find { it.phonemeId == phoneme.id }
                        val isUnlocked = unlockManager.isPhonemeUnlocked(phoneme.id, progressList)
                        nodesList.add(
                            MapNode.LetterNode(
                                id = phoneme.id.toString(),
                                orderIndex = globalIndex++,
                                isUnlocked = isUnlocked,
                                groupNumber = group.groupNumber,
                                symbol = phoneme.letter.uppercase(),
                                starsEarned = progress?.starsEarned ?: 0
                            )
                        )
                    }
                }

                // Insert BlendIt Challenge Node after each group of letters
                val isGroupUnlocked = groupUnlockManager.isGroupUnlocked(group.groupId, members, progressList)
                nodesList.add(
                    MapNode.BlendItNode(
                        id = "blend_${group.groupId}",
                        orderIndex = globalIndex++,
                        isUnlocked = isGroupUnlocked,
                        groupNumber = group.groupNumber,
                        groupId = group.groupId.toString(),
                        starsEarned = 0
                    )
                )
            }

            nodesList
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
