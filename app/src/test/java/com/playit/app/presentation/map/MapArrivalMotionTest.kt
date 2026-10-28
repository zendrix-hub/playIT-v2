package com.playit.app.presentation.map

import androidx.compose.ui.geometry.Offset
import com.playit.app.presentation.map.components.countUpAt
import com.playit.app.presentation.map.components.countUpScale
import com.playit.app.presentation.map.components.explorerOffsetDp
import com.playit.app.presentation.map.components.generateCompanionPlacements
import com.playit.app.presentation.map.components.walkAt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The unlock moment on the map (design refresh): the explorer's walk and the star count-up are pure curves. */
class MapArrivalMotionTest {

    @Test fun walk_startsAtTheOldStopAndEndsAtTheNewOneOnTheGround() {
        assertEquals(0f, walkAt(0f).along, 0f)
        assertEquals(0f, walkAt(0f).liftDp, 0.001f)
        assertEquals(1f, walkAt(1f).along, 0f)
        assertEquals(0f, walkAt(1f).liftDp, 0.001f)
    }

    @Test fun walk_neverGoesBackAndBobsAsItSteps() {
        val frames = (0..100).map { walkAt(it / 100f) }
        frames.zipWithNext().forEach { (a, b) -> assertTrue(b.along >= a.along) }
        assertTrue(frames.maxOf { it.liftDp } > 9f)
        assertTrue(frames.all { it.liftDp in 0f..10f })
    }

    @Test fun explorerOffset_isWhereTheLeaderStands() {
        val centers = (0 until 10).map { i -> Offset(150f + (i % 3) * 300f, 100f + i * 160f) }
        centers.indices.forEach { i ->
            val leader = generateCompanionPlacements(10, centers, i, 1, 400f, 2f).first { it.isExplorerLeader }
            assertEquals(leader.offsetDp, explorerOffsetDp(centers[i], 400f, 2f))
        }
    }

    @Test fun countUp_goesFromTheOldTotalToTheNewOneStepByStep() {
        assertEquals(7, countUpAt(0f, 7, 10))
        assertEquals(10, countUpAt(1f, 7, 10))
        val shown = (0..20).map { countUpAt(it / 20f, 7, 10) }
        shown.zipWithNext().forEach { (a, b) -> assertTrue(b - a in 0..1) }
        assertEquals(5, countUpAt(0.3f, 5, 5))
    }

    @Test fun countUp_swellsOnceAndSettles() {
        assertEquals(1f, countUpScale(0f), 0.001f)
        assertEquals(1f, countUpScale(1f), 0.001f)
        assertEquals(1.2f, (0..100).maxOf { countUpScale(it / 100f) }, 0.001f)
    }
}
