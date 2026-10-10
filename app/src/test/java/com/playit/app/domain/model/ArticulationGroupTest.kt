package com.playit.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ArticulationGroupTest {
    @Test fun everyLetterHasAGroup() = ('a'..'z').forEach { articulationFor(it.toString()) }

    @Test fun lipsTogether() =
        listOf("m", "b", "p").forEach { assertEquals(ArticulationGroup.LIPS_TOGETHER, articulationFor(it)) }

    @Test fun teethOnLip() =
        listOf("f", "v").forEach { assertEquals(ArticulationGroup.TEETH_ON_LIP, articulationFor(it)) }
}
