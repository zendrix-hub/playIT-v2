package com.playit.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** The record uses the DepEd report card's words and its transmutation table (adviser, 2026-11-05). */
class ProgressLevelTest {

    @Test fun theFiveDepEdBands_haveTheirEdges() {
        assertEquals(ProgressLevel.OUTSTANDING, ProgressLevel.forGrade(100))
        assertEquals(ProgressLevel.OUTSTANDING, ProgressLevel.forGrade(90))
        assertEquals(ProgressLevel.VERY_SATISFACTORY, ProgressLevel.forGrade(89))
        assertEquals(ProgressLevel.VERY_SATISFACTORY, ProgressLevel.forGrade(85))
        assertEquals(ProgressLevel.SATISFACTORY, ProgressLevel.forGrade(84))
        assertEquals(ProgressLevel.SATISFACTORY, ProgressLevel.forGrade(80))
        assertEquals(ProgressLevel.FAIRLY_SATISFACTORY, ProgressLevel.forGrade(79))
        assertEquals(ProgressLevel.FAIRLY_SATISFACTORY, ProgressLevel.forGrade(75))
        assertEquals(ProgressLevel.DID_NOT_MEET, ProgressLevel.forGrade(74))
        assertEquals(ProgressLevel.DID_NOT_MEET, ProgressLevel.forGrade(60))
    }

    /** DepEd Order 8, s. 2015, Appendix B: the table's own rows, at their edges. */
    @Test fun transmutation_followsTheDepEdTable() {
        assertEquals(100, ProgressLevel.transmute(10, 10))           // 100
        assertEquals(99, ProgressLevel.transmute(984, 1000))         // 98.40
        assertEquals(98, ProgressLevel.transmute(9839, 10000))       // 98.39
        assertEquals(76, ProgressLevel.transmute(616, 1000))         // 61.60
        assertEquals(75, ProgressLevel.transmute(6159, 10000))       // 61.59
        assertEquals(75, ProgressLevel.transmute(60, 100))           // 60.00
        assertEquals(74, ProgressLevel.transmute(5999, 10000))       // 59.99
        assertEquals(74, ProgressLevel.transmute(56, 100))           // 56.00
        assertEquals(73, ProgressLevel.transmute(5599, 10000))       // 55.99
        assertEquals(61, ProgressLevel.transmute(4, 100))            // 4.00
        assertEquals(60, ProgressLevel.transmute(0, 5))              // 0
    }

    /** One miss in Say It and one in Find It (4 of 6) is still a passing grade, as it would be on a class record. */
    @Test fun aChildWithTwoMisses_isNotMarkedAsFailing() {
        val grade = ProgressLevel.transmute(4, 6)
        assertEquals(79, grade)
        assertEquals(ProgressLevel.FAIRLY_SATISFACTORY, ProgressLevel.forGrade(grade))
    }

    @Test fun onlyTheGradedLevelsHaveBands_andTheTwoLowestNeedPractice() {
        assertEquals(5, ProgressLevel.graded.size)
        assertFalse(ProgressLevel.NOT_STARTED.isGraded)
        assertFalse(ProgressLevel.IN_PROGRESS.isGraded)
        assertTrue(ProgressLevel.DID_NOT_MEET.needsPractice)
        assertTrue(ProgressLevel.FAIRLY_SATISFACTORY.needsPractice)
        assertFalse(ProgressLevel.SATISFACTORY.needsPractice)
        assertFalse(ProgressLevel.NOT_STARTED.needsPractice)
    }

    @Test fun everyLevel_hasPlainWordsAndARemark() {
        ProgressLevel.values().forEach {
            assertTrue(it.label.isNotBlank())
            assertTrue(it.remark.isNotBlank())
            assertFalse("$it uses a percentage", it.label.contains("%") || it.remark.contains("%"))
        }
    }

    /** Parents read plain words (user, 2026-11-05); the DepEd words stay for teachers, in the legend and the PDF. */
    @Test fun parentWords_avoidTheTeachersTerms() {
        val teacherWords = listOf("outstanding", "satisfactory", "expectations", "grade", "%")
        ProgressLevel.values().forEach { level ->
            teacherWords.forEach { w -> assertFalse("$level: ${level.parentLabel}", level.parentLabel.lowercase().contains(w)) }
        }
        assertEquals(ProgressLevel.values().size, ProgressLevel.values().map { it.parentLabel }.toSet().size)
    }
}
