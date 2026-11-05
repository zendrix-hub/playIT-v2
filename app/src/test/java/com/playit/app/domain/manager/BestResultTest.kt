package com.playit.app.domain.manager

import com.playit.app.domain.model.BlendItProgress
import com.playit.app.domain.model.LessonProgress
import org.junit.Assert.assertEquals
import org.junit.Test

/** Playing a finished letter again never lowers its stars (the save replaces the row). */
class BestResultTest {
    private fun run(stars: Int, hearts: Int, at: Long) =
        LessonProgress(profileId = 1, phonemeId = 1, starsEarned = stars, heartsLost = hearts, isCompleted = true, completedAt = at)

    @Test fun aWorseReplay_keepsTheBestStars_datedToday() {
        val best = BestResult.lesson(saved = run(3, 0, 100), played = run(1, 4, 200))
        assertEquals(3, best.starsEarned)
        assertEquals(0, best.heartsLost)
        assertEquals(200, best.completedAt)
    }

    @Test fun aBetterOrEqualReplay_isSaved() {
        assertEquals(3, BestResult.lesson(saved = run(2, 1, 100), played = run(3, 0, 200)).starsEarned)
        assertEquals(4, BestResult.lesson(saved = run(2, 1, 100), played = run(2, 4, 200)).heartsLost)
        assertEquals(1, BestResult.lesson(saved = null, played = run(1, 4, 200)).starsEarned)
    }

    @Test fun blendIt_keepsTheBestStarsToo() {
        val saved = BlendItProgress(profileId = 1, groupId = 1, starsEarned = 3, isCompleted = true, completedAt = 1)
        val played = BlendItProgress(profileId = 1, groupId = 1, starsEarned = 1, isCompleted = true, completedAt = 2)
        assertEquals(3, BestResult.blendIt(saved, played).starsEarned)
        assertEquals(2, BestResult.blendIt(saved, played).completedAt)
    }
}
