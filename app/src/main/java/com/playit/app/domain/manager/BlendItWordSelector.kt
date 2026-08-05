package com.playit.app.domain.manager

import com.playit.app.domain.model.BlendItWord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlendItWordSelector @Inject constructor() {

    fun selectWordsForSession(groupId: Int, availableWords: List<BlendItWord>): List<BlendItWord> {
        val groupWords = availableWords.filter { it.groupId == groupId }
        return if (groupId == 1) {
            // Constraint: Group 1 restricted to exactly 3 words (SAM, SIS, AIM)
            groupWords.take(3)
        } else {
            // Standard session: up to 5 words
            groupWords.take(5)
        }
    }
}
