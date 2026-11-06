package com.playit.app.presentation.soundcheck

import androidx.lifecycle.SavedStateHandle
import com.playit.app.data.audio.AudioPlayer
import com.playit.app.data.audio.AudioResolver
import com.playit.app.domain.model.LetterGroupMember
import com.playit.app.domain.model.Phoneme
import com.playit.app.domain.model.SoundCheckResult
import com.playit.app.domain.repository.LetterGroupMemberRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.SoundCheckRepository
import com.playit.app.navigation.SessionManager
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** The sound check before the word game (user choice, 2026-11-06). */
@OptIn(ExperimentalCoroutinesApi::class)
class SoundCheckViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val phonemes = listOf("m", "s", "a", "i").mapIndexed { i, l -> Phoneme(i + 1, l, "", "", "") }
    private val members: LetterGroupMemberRepository = mockk()
    private val phonemeRepo: PhonemeRepository = mockk { every { getAllPhonemes() } returns flowOf(phonemes) }
    private val checks: SoundCheckRepository = mockk(relaxed = true)
    private val session: SessionManager = mockk { every { activeProfileId } returns MutableStateFlow<Long?>(7L) }
    private val audio: AudioPlayer = mockk(relaxed = true)
    private val played = mutableListOf<List<String>>()

    @Before fun setUp() {
        Dispatchers.setMain(dispatcher)
        // Stored out of order: the check reads them by their place in the chapter.
        every { members.getMembersForGroup(1) } returns flowOf(listOf(
            LetterGroupMember(groupId = 1, phonemeId = 3, position = 2),
            LetterGroupMember(groupId = 1, phonemeId = 1, position = 0),
            LetterGroupMember(groupId = 1, phonemeId = 4, position = 3),
            LetterGroupMember(groupId = 1, phonemeId = 2, position = 1)
        ))
        every { audio.playSequence(capture(played), any<() -> Unit>()) } returns Unit
    }

    @After fun tearDown() = Dispatchers.resetMain()

    private fun vm() = SoundCheckViewModel(members, phonemeRepo, checks, session, audio, AudioResolver(),
        SavedStateHandle(mapOf("groupId" to "1")))

    /** The letter whose sound Lily asked for last: "Which letter says..." then the sound. */
    private fun asked(): String {
        val q = played.last { it.first().endsWith("chk_which_says.wav") }
        return Regex("""ph(?:oneme)?_(\w+)\.""").find(q[1])!!.groupValues[1]
    }

    @Test fun introFirst_thenEachSound_withTheChaptersLettersAsTiles() = runTest(dispatcher) {
        val vm = vm()
        advanceUntilIdle()
        assertEquals(listOf("audio/vo/ui/chk_intro.wav"), played.first())
        assertEquals(4, vm.total.value)
        assertEquals(setOf("m", "s", "a", "i"), vm.tiles.value.toSet())
        assertTrue(vm.acceptingTaps.value)
        assertEquals(SoundCheckSpeech.ASK, vm.speech.value)
        assertTrue(asked() in listOf("m", "s", "a", "i"))
    }

    @Test fun aTapDuringTheIntro_doesNotCount() = runTest(dispatcher) {
        val vm = vm()
        runCurrent()
        assertFalse(vm.acceptingTaps.value)
        vm.tap("m")
        assertNull(vm.lastTap.value)
        assertEquals(0, vm.index.value)
    }

    @Test fun rightAndWrongTaps_noHelp_andTheResultIsSaved() = runTest(dispatcher) {
        val vm = vm()
        advanceUntilIdle()
        val missed = asked()
        vm.tap(listOf("m", "s", "a", "i").first { it != missed })
        runCurrent()
        assertEquals(false, vm.lastTap.value?.right)
        assertEquals(SoundCheckSpeech.GOOD_TRY, vm.speech.value)
        assertEquals(listOf("audio/vo/ui/chk_good_try.wav"), played.last())
        vm.tap(missed) // a second try on the same sound is not taken
        advanceUntilIdle()
        assertEquals(1, vm.index.value)
        assertTrue("the next sound is a different one", asked() != missed)

        repeat(3) {
            vm.tap(asked())
            assertEquals(true, vm.lastTap.value?.right)
            advanceUntilIdle()
        }
        val result = vm.result.value!!
        assertEquals(listOf(missed), result.notYet)
        assertEquals(listOf("m", "s", "a", "i") - missed, result.known)
        assertEquals(SoundCheckSpeech.DONE, vm.speech.value)
        assertEquals(listOf("audio/vo/ui/chk_done.wav"), played.last())
        val saved = slot<SoundCheckResult>()
        coVerify { checks.save(7L, capture(saved)) }
        assertEquals(1, saved.captured.groupId)
    }

    @Test fun theSpeakerButton_playsTheSameSoundAgain() = runTest(dispatcher) {
        val vm = vm()
        advanceUntilIdle()
        val first = asked()
        val calls = played.size
        vm.replaySound()
        advanceUntilIdle()
        assertEquals(calls + 1, played.size)
        assertEquals(first, asked())
        assertEquals(0, vm.index.value)
    }

    @Test fun noLettersForTheChapter_goesStraightToTheWords() = runTest(dispatcher) {
        every { members.getMembersForGroup(1) } returns flowOf(emptyList())
        val vm = vm()
        advanceUntilIdle()
        assertTrue(vm.skip.value)
        coVerify(exactly = 0) { checks.save(any(), any()) }
    }
}
