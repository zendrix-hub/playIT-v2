package com.playit.app.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    private val _activeProfileId = MutableStateFlow<Long?>(null)
    val activeProfileId: StateFlow<Long?> = _activeProfileId.asStateFlow()

    private val visitedScreenIntros = mutableSetOf<String>()
    var hasPlayedReturnWelcome: Boolean = false
        private set
    var hasPlayedFirstOpenWelcome: Boolean = false
        private set

    fun setActiveProfile(profileId: Long) {
        _activeProfileId.value = profileId
    }

    fun clearActiveProfile() {
        _activeProfileId.value = null
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
}
