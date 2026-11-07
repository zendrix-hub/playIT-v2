package com.playit.app.presentation.soundcheck

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.manager.SoundCheckRound
import com.playit.app.domain.model.SoundCheckResult
import com.playit.app.domain.repository.LetterGroupMemberRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.SoundCheckRepository
import com.playit.app.navigation.SessionManager
import com.playit.app.presentation.components.IdleTimer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.random.Random

/** Lily's lines for the sound check (release 2026-11-06-sound-check). */
object SoundCheckLines {
    const val INTRO = "chk_intro"
    const val WHICH_SAYS = "chk_which_says"
    const val GOOD_TRY = "chk_good_try"
    const val DONE = "chk_done"
    val ALL = listOf(INTRO, WHICH_SAYS, GOOD_TRY, DONE)
}

/** What the bubble says; it matches what Lily says. */
enum class SoundCheckSpeech(val text: String) {
    INTRO("Sound check! Tap the letter you hear."),
    ASK("Which letter says this sound?"),
    RIGHT("Yes! That's it!"),
    GOOD_TRY("Good try! Let's do the next one."),
    DONE("Sound check done! Great job!")
}

/** A tile the child just tapped, shown green when right and orange when not; the right letter is never revealed. */
data class SoundCheckTap(val letter: String, val right: Boolean)

/**
 * The sound check before a chapter's word game (user choice, 2026-11-06: the teacher asked how anyone can tell the
 * child learned). Lily says each of the chapter's sounds once ("Which letter says..." and the sound); the child taps
 * a letter. No help and no second try: a wrong tap gets "Good try! Let's do the next one." The speaker button and
 * the idle re-prompt replay only the sound. The result is saved for the Parent Zone and the PDF.
 */
@HiltViewModel
class SoundCheckViewModel @Inject constructor(
    private val letterGroupMemberRepository: LetterGroupMemberRepository,
    private val phonemeRepository: PhonemeRepository,
    private val soundCheckRepository: SoundCheckRepository,
    private val sessionManager: SessionManager,
    private val audioPlayer: AudioPlayer,
    private val audioResolver: AudioResolver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val groupId: Int = savedStateHandle.get<String>("groupId")?.toIntOrNull() ?: 1

    /** Tests pass a seeded random; the app shuffles freshly each time. */
    internal var random: Random = Random.Default

    private var round: SoundCheckRound? = null

    private val _tiles = MutableStateFlow<List<String>>(emptyList())
    val tiles: StateFlow<List<String>> = _tiles.asStateFlow()

    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index.asStateFlow()

    private val _total = MutableStateFlow(0)
    val total: StateFlow<Int> = _total.asStateFlow()

    private val _speech = MutableStateFlow(SoundCheckSpeech.INTRO)
    val speech: StateFlow<SoundCheckSpeech> = _speech.asStateFlow()

    private val _lastTap = MutableStateFlow<SoundCheckTap?>(null)
    val lastTap: StateFlow<SoundCheckTap?> = _lastTap.asStateFlow()

    /** Taps count only while a sound is being asked, not during the intro or the answer's feedback. */
    private val _acceptingTaps = MutableStateFlow(false)
    val acceptingTaps: StateFlow<Boolean> = _acceptingTaps.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _result = MutableStateFlow<SoundCheckResult?>(null)
    val result: StateFlow<SoundCheckResult?> = _result.asStateFlow()

    /** True when the chapter's letters could not be read: the screen goes straight on to the word game. */
    private val _skip = MutableStateFlow(false)
    val skip: StateFlow<Boolean> = _skip.asStateFlow()

    private var flow: Job? = null

    /**
     * True once the child goes on to the word game. The player is shared, and this screen closes only after Blend It
     * has started "Let's build some words together!" (user, 2026-11-06: Lily stopped saying it), so from then on
     * leaving must not stop the player.
     */
    private var handedOff = false

    private val idleTimer = IdleTimer(
        scope = viewModelScope,
        isBusy = { _isPlaying.value || !_acceptingTaps.value || audioPlayer.isSpeaking() },
        onIdle = { replaySound() }
    )

    init {
        viewModelScope.launch {
            val letters = runCatching {
                val phonemes = phonemeRepository.getAllPhonemes().first().associateBy { it.id }
                letterGroupMemberRepository.getMembersForGroup(groupId).first()
                    .sortedBy { it.position }
                    .mapNotNull { phonemes[it.phonemeId]?.letter?.lowercase() }
            }.getOrDefault(emptyList())
            if (letters.isEmpty()) {
                _skip.value = true
                return@launch
            }
            val r = SoundCheckRound(letters, random)
            round = r
            _total.value = r.total
            flow = launch {
                say(SoundCheckSpeech.INTRO, listOf(audioResolver.getUiPath(SoundCheckLines.INTRO)), maxMs = 6_000L)
                ask()
            }
        }
    }

    fun onScreenVisible() = idleTimer.start()
    fun onScreenHidden() = idleTimer.stop()
    fun onUserInteraction() = idleTimer.touch()

    private fun questionPaths(): List<String> {
        val letter = round?.current ?: return emptyList()
        return listOf(audioResolver.getUiPath(SoundCheckLines.WHICH_SAYS), audioResolver.getPhonemePath(letter))
    }

    private suspend fun ask() {
        val r = round ?: return
        _index.value = r.index
        _tiles.value = r.tiles
        _lastTap.value = null
        _acceptingTaps.value = true
        say(SoundCheckSpeech.ASK, questionPaths(), maxMs = 8_000L)
    }

    /** The speaker button and the idle re-prompt: the question and the sound again, nothing more. */
    fun replaySound() {
        if (!_acceptingTaps.value) return
        flow?.cancel()
        flow = viewModelScope.launch { say(SoundCheckSpeech.ASK, questionPaths(), maxMs = 8_000L) }
    }

    fun tap(letter: String) {
        val r = round ?: return
        if (!_acceptingTaps.value || r.isDone) return
        _acceptingTaps.value = false
        flow?.cancel()
        audioPlayer.stop()
        val right = r.tap(letter)
        _lastTap.value = SoundCheckTap(letter, right)
        flow = viewModelScope.launch {
            if (right) {
                say(SoundCheckSpeech.RIGHT, listOf(audioResolver.getRotatingCorrectVo()), minMs = 900L, maxMs = 4_000L)
            } else {
                say(SoundCheckSpeech.GOOD_TRY, listOf(audioResolver.getUiPath(SoundCheckLines.GOOD_TRY)), minMs = 900L, maxMs = 5_000L)
            }
            if (r.isDone) finish(r) else ask()
        }
    }

    private suspend fun finish(r: SoundCheckRound) {
        val result = r.result(groupId, System.currentTimeMillis())
        _result.value = result
        _tiles.value = emptyList()
        sessionManager.activeProfileId.value?.let { profileId ->
            runCatching { soundCheckRepository.save(profileId, result) }
        }
        say(SoundCheckSpeech.DONE, listOf(audioResolver.getUiPath(SoundCheckLines.DONE)), maxMs = 5_000L)
    }

    /**
     * Shows [speech] and plays [paths]; returns when they end, but no later than [maxMs] (a clip that never reports
     * its end cannot stall the check) and no sooner than [minMs], so the child sees the tap's colour.
     */
    private suspend fun say(speech: SoundCheckSpeech, paths: List<String>, minMs: Long = 0L, maxMs: Long) {
        _speech.value = speech
        _isPlaying.value = true
        try {
            coroutineScope {
                val minimum = launch { delay(minMs) }
                val ended = CompletableDeferred<Unit>()
                audioPlayer.playSequence(paths) { ended.complete(Unit) }
                withTimeoutOrNull(maxMs) { ended.await() }
                minimum.join()
            }
        } finally {
            _isPlaying.value = false
        }
    }

    fun stopAudio() {
        flow?.cancel()
        audioPlayer.stop()
        _isPlaying.value = false
    }

    /** "Build words" (or a chapter with no letters): stop the check's own voice, then leave the player to Blend It. */
    fun goToWords() {
        stopAudio()
        handedOff = true
    }

    /** The screen left: stop the check's voice, unless Blend It already owns the player. */
    fun onLeave() {
        if (handedOff) flow?.cancel() else stopAudio()
    }

    override fun onCleared() {
        super.onCleared()
        flow?.cancel()
        if (!handedOff) audioPlayer.stop()
    }
}
