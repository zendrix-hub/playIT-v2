package com.playit.app.domain.manager

import com.playit.app.domain.model.BlendItProgress
import com.playit.app.domain.model.LessonProgress

/**
 * Playing a finished letter or word game again never lowers what the child earned: the record keeps the run with the
 * most stars (and its hearts), dated to the latest play. The save replaces the row, so this choice is made before it.
 */
object BestResult {
    fun lesson(saved: LessonProgress?, played: LessonProgress): LessonProgress =
        if (saved != null && saved.isCompleted && saved.starsEarned > played.starsEarned) {
            saved.copy(completedAt = played.completedAt)
        } else {
            played
        }

    fun blendIt(saved: BlendItProgress?, played: BlendItProgress): BlendItProgress =
        if (saved != null && saved.isCompleted && saved.starsEarned > played.starsEarned) {
            saved.copy(completedAt = played.completedAt)
        } else {
            played
        }
}
