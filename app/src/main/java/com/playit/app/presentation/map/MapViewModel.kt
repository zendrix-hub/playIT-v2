package com.playit.app.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.data.audio.SfxEvent
import com.playit.app.data.audio.VoContext
import com.playit.app.domain.manager.GroupUnlockManager
import com.playit.app.domain.manager.ReturnGreetingPolicy
import com.playit.app.domain.repository.SayItAttemptRepository
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Lily's welcome back over the map (adviser, 2026-11-05). [letter], [keyWord] and [imagePath] are the next letter's,
 * for [ReturnGreetingPolicy.KeepGoing]; the name shows on screen only, as Lily can't say a typed name.
 */
data class WelcomeBack(
    val offer: ReturnGreetingPolicy.Offer,
    val name: String,
    val letter: String? = null,
    val keyWord: String? = null,
    val imagePath: String? = null
)

data class UserMapStats(
    val profileName: String = "",
    val avatarId: Int = 1,
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
    private val audioResolver: AudioResolver,
    private val sayItAttemptRepository: SayItAttemptRepository
) : ViewModel() {

    private val _welcomeBack = MutableStateFlow<WelcomeBack?>(null)

    /** The welcome-back card, shown once per app launch to a child who has played before; null when closed. */
    val welcomeBack: StateFlow<WelcomeBack?> = _welcomeBack.asStateFlow()

    /** "Let's go!" or "Not now": the card closes and Lily stops. */
    fun dismissWelcomeBack() {
        if (_welcomeBack.value != null) audioPlayer.stop()
        _welcomeBack.value = null
    }

    /** A tap on Lily in the card says it again. */
    fun replayWelcomeBack() {
        _welcomeBack.value?.let { playWelcomeBack(it) }
    }

    private suspend fun hasPlayedBefore(profileId: Long): Boolean =
        lessonProgressRepository.getProgressForProfile(profileId).first().any { it.isCompleted } ||
            sayItAttemptRepository.getAttemptsForProfile(profileId).isNotEmpty()

    private suspend fun showWelcomeBack(profileId: Long) {
        val nodes = mapNodes.first { it.isNotEmpty() }
        val offer = ReturnGreetingPolicy.offerFor(nodes) ?: return
        val name = profileRepository.getProfileById(profileId)?.name.orEmpty()
        val phoneme = (offer as? ReturnGreetingPolicy.KeepGoing)?.nodeId?.toIntOrNull()?.let { phonemeRepository.getPhonemeById(it) }
        val card = WelcomeBack(offer, name, phoneme?.letter, phoneme?.exampleWord, phoneme?.imagePath)
        _welcomeBack.value = card
        playWelcomeBack(card)
    }

    /** Lily's lines, then the next letter's own sound (never its name). A clip not yet released is skipped. */
    private fun playWelcomeBack(card: WelcomeBack) {
        val lines = ReturnGreetingPolicy.lines(card.offer).map { audioResolver.getUiPath(it) }
        val sound = card.letter?.let { listOf(audioResolver.getPhonemePath(it)) }.orEmpty()
        audioPlayer.playSequence(lines + sound)
    }

    val activeProfileId: StateFlow<Long?> = sessionManager.activeProfileId

    fun playHeartRecoverySound() {
        val sfx = audioResolver.getSfxPath(SfxEvent.HEART_RECOVERY_SPARKLE)
        audioPlayer.playAssetAudio(sfx)
    }

    fun playMascotTapReaction() {
        val sfx = audioResolver.getSfxPath(SfxEvent.NODE_UNLOCK_CHIME)
        val vo = audioResolver.getVoPath(VoContext.MAP_TARANA)
        audioPlayer.playSequence(listOf(sfx, vo))
    }

    fun onLockedNodeTapped() {
        val sfx = audioResolver.getSfxPath(SfxEvent.INCORRECT_POP)
        val vo = audioResolver.getUiPath("ui_node_locked")
        audioPlayer.playSequence(listOf(sfx, vo))
    }

    fun onUnlockedNodeTapped() {
        val vo = audioResolver.getUiPath("ui_node_start")
        audioPlayer.playAssetAudio(vo)
    }

    fun clearSession() {
        sessionManager.clearActiveProfile()
    }

    private val _newlyUnlockedNodeId = MutableStateFlow<String?>(null)

    /** A node that went from locked to unlocked between two loads (the child just earned it); null after it is shown. */
    val newlyUnlockedNodeId: StateFlow<String?> = _newlyUnlockedNodeId.asStateFlow()

    /** Unlocked node ids of the previous load; null until the first load, which is never an unlock. */
    private var previousUnlockedIds: Set<String>? = null
    private var trackedProfileId: Long? = null

    private fun trackUnlocks(nodes: List<MapNode>) {
        val unlocked = nodes.filter { it.isUnlocked }.map { it.id }.toSet()
        val before = previousUnlockedIds
        if (before != null) {
            nodes.firstOrNull { it.isUnlocked && it.id !in before }?.let { _newlyUnlockedNodeId.value = it.id }
        }
        previousUnlockedIds = unlocked
    }

    fun onUnlockShown() {
        _newlyUnlockedNodeId.value = null
    }

    fun playUnlockChime() {
        audioPlayer.playAssetAudio(audioResolver.getSfxPath(SfxEvent.NODE_UNLOCK_CHIME))
    }

    val userStats: StateFlow<UserMapStats> = activeProfileId.flatMapLatest { profileId ->
        if (profileId == null) return@flatMapLatest flowOf(UserMapStats())

        combine(
            profileRepository.getAllProfiles().map { list -> list.find { it.id == profileId } },
            achievementRepository.getUnlockedAchievements(profileId)
        ) { profile, unlockedAchievements ->
            UserMapStats(
                profileName = profile?.name ?: "",
                avatarId = profile?.avatarResId ?: 1,
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
        // Another child's map: its first load is not an unlock. The same child coming back from a
        // lesson resubscribes here too, and keeps the comparison.
        if (profileId != trackedProfileId) {
            trackedProfileId = profileId
            previousUnlockedIds = null
        }
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
                val isBlendItUnlocked = groupUnlockManager.isBlendItUnlocked(group.groupId, members, progressList)
                nodesList.add(
                    MapNode.BlendItNode(
                        id = "blend_${group.groupId}",
                        orderIndex = globalIndex++,
                        isUnlocked = isBlendItUnlocked,
                        groupNumber = group.groupNumber,
                        groupId = group.groupId.toString(),
                        starsEarned = 0
                    )
                )
            }

            trackUnlocks(nodesList)
            nodesList
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            activeProfileId.collect { profileId ->
                if (profileId != null) {
                    // Decided before today's activity is recorded: the first map of this launch, for a child who has
                    // played before, gets Lily's welcome back.
                    val firstMap = sessionManager.firstMapOpenThisLaunch(profileId)
                    val greet = firstMap && ReturnGreetingPolicy.shouldGreet(hasPlayedBefore(profileId), firstMap)
                    streakTracker.resetIfInactive(profileId)
                    streakTracker.recordActivity(profileId)
                    if (greet) showWelcomeBack(profileId)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
