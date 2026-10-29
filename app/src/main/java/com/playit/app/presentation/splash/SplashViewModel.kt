package com.playit.app.presentation.splash

import androidx.lifecycle.ViewModel
import com.playit.app.data.audio.AudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Plays the opening's sounds (SplashOpening.kt, OPENING_CUES) through the app's sound pool. */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    fun play(cue: OpeningCue) = audioPlayer.playSfx(cue.sfx, cue.volume, cue.rate)
}
