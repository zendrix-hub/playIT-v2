package com.playit.app.navigation

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val _activeProfileId = MutableStateFlow<Long?>(
        if (context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).contains(KEY_ACTIVE_PROFILE_ID)) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getLong(KEY_ACTIVE_PROFILE_ID, -1L).takeIf { it != -1L }
        } else {
            null
        }
    )
    val activeProfileId: StateFlow<Long?> = _activeProfileId.asStateFlow()

    private val visitedScreenIntros = mutableSetOf<String>()
    var hasPlayedReturnWelcome: Boolean = false
        private set
    var hasPlayedFirstOpenWelcome: Boolean = false
        private set

    fun setActiveProfile(profileId: Long) {
        _activeProfileId.value = profileId
        prefs.edit().putLong(KEY_ACTIVE_PROFILE_ID, profileId).apply()
    }

    fun clearActiveProfile() {
        _activeProfileId.value = null
        prefs.edit().remove(KEY_ACTIVE_PROFILE_ID).apply()
        visitedScreenIntros.clear()
        hasPlayedReturnWelcome = false
        hasPlayedFirstOpenWelcome = false
    }

    fun shouldPlayScreenIntro(screenName: String): Boolean {
        return visitedScreenIntros.add(screenName)
    }

    fun markReturnWelcomePlayed() {
        hasPlayedReturnWelcome = true
    }

    fun markFirstOpenWelcomePlayed() {
        hasPlayedFirstOpenWelcome = true
    }

    companion object {
        private const val PREFS_NAME = "playit_session_prefs"
        private const val KEY_ACTIVE_PROFILE_ID = "active_profile_id"
    }
}
