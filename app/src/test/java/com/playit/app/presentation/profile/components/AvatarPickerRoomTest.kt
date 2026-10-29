package com.playit.app.presentation.profile.components

import com.playit.app.presentation.components.lipsync.hopAt
import org.junit.Assert.assertTrue
import org.junit.Test

/** A picked animal's ring is never cut by the scrolling area around the grid (user, 2026-10-29). */
class AvatarPickerRoomTest {

    @Test fun pickedAnimal_growAndHopStayInsideTheRoom() {
        // The tallest item: the circle, the 4 dp gap and a 24 sp label at the largest font scale tested (1.3), with a
        // generous 1.5 line height.
        val item = AVATAR_CIRCLE_DP + 4 + 24f * 1.3f * 1.5f
        // A picked animal grows from its centre, so half the growth goes up; its circle then hops by a share of its height.
        val grow = (AVATAR_SELECTED_SCALE - 1f) / 2f * item
        val hop = (0..100).maxOf { hopAt(it / 100f).lift } * AVATAR_CIRCLE_DP * AVATAR_SELECTED_SCALE
        assertTrue("a pick reaches ${grow + hop} dp past the grid", grow + hop <= AVATAR_PICK_ROOM_DP)
    }
}
