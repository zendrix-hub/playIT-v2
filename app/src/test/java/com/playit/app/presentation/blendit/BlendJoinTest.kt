package com.playit.app.presentation.blendit

import org.junit.Assert.assertEquals
import org.junit.Test

/** At the blend moment the slots slide together into one word (design refresh). */
class BlendJoinTest {

    @Test fun threeSlots_outerOnesMoveInOneGapAndTheMiddleStays() {
        assertEquals(listOf(1f, 0f, -1f), (0 until 3).map { blendJoinGaps(it, 3) })
    }

    @Test fun fourSlots_moveSymmetricallyTowardTheMiddle() {
        assertEquals(listOf(1.5f, 0.5f, -0.5f, -1.5f), (0 until 4).map { blendJoinGaps(it, 4) })
    }

    @Test fun everyPairOfNeighboursClosesExactlyOneGap() {
        (2..4).forEach { count ->
            (0 until count - 1).forEach { i -> assertEquals(1f, blendJoinGaps(i, count) - blendJoinGaps(i + 1, count), 0f) }
            assertEquals(0f, (0 until count).sumOf { blendJoinGaps(it, count).toDouble() }.toFloat(), 0f)
        }
    }
}
