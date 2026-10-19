package com.playit.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ArticulationGroupTest {
    @Test fun everyLetterHasAGroup() = ('a'..'z').forEach { articulationFor(it.toString()) }

    @Test fun lipsTogether() =
        listOf("m", "b", "p").forEach { assertEquals(ArticulationGroup.LIPS_TOGETHER, articulationFor(it)) }

    @Test fun teethOnLip() =
        listOf("f", "v").forEach { assertEquals(ArticulationGroup.TEETH_ON_LIP, articulationFor(it)) }

    /** The whole table for the 26 letters, so a change to any pair is a deliberate one (card 28b). */
    @Test fun everyLetterMapsToItsMouthShape() {
        val expected = mapOf(
            ArticulationGroup.LIPS_TOGETHER to "mbp",
            ArticulationGroup.TEETH_ON_LIP to "fv",
            ArticulationGroup.TONGUE_UP to "tdnlr",
            ArticulationGroup.TEETH_CLOSE to "szx",
            ArticulationGroup.BACK_OF_MOUTH to "kcgu",
            ArticulationGroup.ROUND_LIPS to "wqj",
            ArticulationGroup.WIDE_OPEN to "ao",
            ArticulationGroup.SMILE to "eiy",
            ArticulationGroup.OPEN_BREATH to "h",
        )
        assertEquals(26, expected.values.sumOf { it.length })
        expected.forEach { (group, letters) ->
            letters.forEach { assertEquals("letter $it", group, articulationFor(it.toString())) }
        }
    }

    /** The five pairs the A21s phone test of APK D found wrong (card 28b). */
    @Test fun shortVowelsAreNotRoundLips() {
        assertEquals(ArticulationGroup.WIDE_OPEN, articulationFor("o"))
        assertEquals(ArticulationGroup.BACK_OF_MOUTH, articulationFor("u"))
    }

    @Test fun rDoesNotRoundTheLips() {
        assertEquals(ArticulationGroup.TONGUE_UP, articulationFor("r"))
        assertNotEquals(ArticulationGroup.ROUND_LIPS, articulationFor("r"))
    }

    @Test fun jAndQRoundTheLips() {
        assertEquals(ArticulationGroup.ROUND_LIPS, articulationFor("j"))
        assertEquals(ArticulationGroup.ROUND_LIPS, articulationFor("q"))
    }

    @Test fun caseAndSpacesIgnored() {
        assertEquals(ArticulationGroup.LIPS_TOGETHER, articulationFor(" M "))
        assertEquals(ArticulationGroup.OPEN_BREATH, articulationFor("H"))
    }
}
