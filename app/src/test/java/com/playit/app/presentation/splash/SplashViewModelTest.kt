package com.playit.app.presentation.splash

import com.playit.app.data.audio.AudioPlayer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

/** The opening's sounds reach the app's sound pool as the cues say, and each opening takes the next title effect. */
class SplashViewModelTest {

    private val effects: TitleEffectRotation = mockk { every { next() } returns TitleEffect.PLANE }

    @Test fun play_passesTheCuesSoundVolumeAndPitch() {
        val player: AudioPlayer = mockk(relaxed = true)
        val vm = SplashViewModel(player, effects)
        (OPENING_CUES + STILL_OPENING_CUE).forEach { vm.play(it) }
        (OPENING_CUES + STILL_OPENING_CUE).forEach { cue -> verify { player.playSfx(cue.sfx, cue.volume, cue.rate) } }
    }

    @Test fun titleEffect_isTakenOnceWhenTheScreenOpens() {
        val vm = SplashViewModel(mockk(relaxed = true), effects)
        assertEquals(TitleEffect.PLANE, vm.titleEffect)
        assertEquals(TitleEffect.PLANE, vm.titleEffect)
        verify(exactly = 1) { effects.next() }
    }
}
