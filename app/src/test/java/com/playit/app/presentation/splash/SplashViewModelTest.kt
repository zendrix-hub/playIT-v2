package com.playit.app.presentation.splash

import com.playit.app.data.audio.AudioPlayer
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

/** The opening's sounds reach the app's sound pool as the cues say. */
class SplashViewModelTest {

    @Test fun play_passesTheCuesSoundVolumeAndPitch() {
        val player: AudioPlayer = mockk(relaxed = true)
        val vm = SplashViewModel(player)
        OPENING_CUES.forEach { vm.play(it) }
        OPENING_CUES.forEach { cue -> verify { player.playSfx(cue.sfx, cue.volume, cue.rate) } }
    }
}
