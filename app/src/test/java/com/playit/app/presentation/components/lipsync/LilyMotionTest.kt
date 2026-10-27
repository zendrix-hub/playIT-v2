package com.playit.app.presentation.components.lipsync

import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.stickerAt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LilyMotionTest {

    @Test fun blink_shutsHoldsAndOpens_thenStaysOpen() {
        assertEquals(0f, blinkClosed(0f), 1e-4f)
        assertEquals(0.5f, blinkClosed(35f), 1e-4f)
        assertEquals(1f, blinkClosed(90f), 1e-4f)
        assertEquals(0.5f, blinkClosed(155f), 1e-4f)
        assertEquals(0f, blinkClosed(BLINK_MS.toFloat()), 1e-4f)
        assertEquals(0f, blinkClosed(1000f), 1e-4f)
    }

    @Test fun hop_risesLandsAndSettles() {
        assertEquals(HopFrame(0f, 0f), hopAt(0f))
        assertEquals(0.12f, hopAt(0.4f).lift, 1e-4f)                // the top of the hop
        assertTrue(hopAt(0.6f).lift in 0f..0.12f)
        assertEquals(1f, hopAt(0.9f).squash, 1e-4f)                 // the landing squash
        assertEquals(0f, hopAt(1f).lift, 1e-4f)
        assertEquals(0f, hopAt(1f).squash, 1e-3f)                   // standing again
    }

    @Test fun everyPoseCanBlink_exceptCelebrating_whoseEyesAreShut() {
        MascotState.values().forEach { pose ->
            val eyes = poseEyes(pose)
            if (pose == MascotState.CELEBRATING) assertNull(eyes)
            else {
                assertNotNull(pose.name, eyes)
                assertTrue(pose.name, eyes!!.leftX < eyes.rightX)
                assertTrue(pose.name, eyes.leftX - eyes.radius > 0f && eyes.rightX + eyes.radius < 512f)
            }
        }
    }

    @Test fun sticker_startsLiftedAndEndsPlaced_andOnlyFadesWithReducedMotion() {
        val lifted = stickerAt(0f, reduced = false)
        assertEquals(0f, lifted.alpha, 0f)
        assertEquals(0.88f, lifted.scale, 1e-4f)
        assertEquals(-6f, lifted.rotation, 1e-4f)
        val placed = stickerAt(1f, reduced = false)
        assertEquals(1f, placed.alpha, 0f)
        assertEquals(1f, placed.scale, 1e-4f)
        assertEquals(0f, placed.rotation, 1e-4f)
        assertEquals(1f, stickerAt(1.08f, reduced = false).alpha, 0f)   // the spring overshoots; opacity stays 1
        val reduced = stickerAt(0.5f, reduced = true)
        assertEquals(1f, reduced.scale, 0f)
        assertEquals(0f, reduced.rotation, 0f)
    }
}
