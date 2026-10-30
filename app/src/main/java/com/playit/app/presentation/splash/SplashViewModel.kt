package com.playit.app.presentation.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import com.playit.app.data.audio.AudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Plays the opening's sounds (SplashOpening.kt, OPENING_CUES) through the app's sound pool. */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer,
    effects: TitleEffectRotation
) : ViewModel() {

    /** The title's effect this time the app opens; it stays while the screen lives, so turning the phone keeps it. */
    val titleEffect: TitleEffect = effects.next()

    fun play(cue: OpeningCue) = audioPlayer.playSfx(cue.sfx, cue.volume, cue.rate)
}

/** Hands out the title's effects in turn, one per opening, remembering the last across launches (TitleEffect). */
@Singleton
class TitleEffectRotation @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun next(): TitleEffect {
        val effect = titleEffectAfter(prefs.getString(KEY_LAST, null))
        prefs.edit().putString(KEY_LAST, effect.name).apply()
        return effect
    }

    private companion object {
        const val PREFS_NAME = "playit_opening_prefs"
        const val KEY_LAST = "last_title_effect"
    }
}
