package com.playit.app.presentation.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DimensTest {
    @Test fun compactUnder700dpTall() = assertEquals(WindowProfile.COMPACT, windowProfileFor(360, 640))
    @Test fun a21sIsRegular() = assertEquals(WindowProfile.REGULAR, windowProfileFor(360, 740))
    @Test fun tabletIsWide() = assertEquals(WindowProfile.WIDE, windowProfileFor(800, 1280))
    @Test fun childTargetsNeverBelow64dp() {
        WindowProfile.values().map(::dimensFor).forEach {
            assertTrue(it.tileSize.value >= 64f && it.micSize.value >= 64f && it.primaryCta.value >= 64f)
        }
    }
    @Test fun textNeverBelow16sp() {
        WindowProfile.values().map(::dimensFor).forEach { assertTrue(it.bubbleTextSp >= 16) }
    }
}
