package com.playit.app.domain.model

/**
 * How a learner is doing, in plain words for parents ([parentLabel]: user, 2026-11-05, "the parent screen needs it to
 * be easier for the parents") and in the DepEd report card's words for teachers ([label]: adviser, 2026-11-05, "label
 * the progress, not just put the n%"). The five graded levels use the bands of DepEd Order 8, s. 2015. A letter is graded only once the child has finished it, so a letter they haven't played is "Not started
 * yet" rather than a low grade.
 *
 * As in a DepEd class record, the share of right answers (the percentage score) is transmuted into a grade from 60 to
 * 100 with the Order's transmutation table, and the grade sets the level: a raw 60% is a grade of 75.
 */
enum class ProgressLevel(val label: String, val parentLabel: String, val band: String?, val remark: String) {
    OUTSTANDING("Outstanding", "Knows it very well", "90-100", "Great! Keep playing so it stays strong."),
    VERY_SATISFACTORY("Very Satisfactory", "Knows it well", "85-89", "Good! A little more play keeps it strong."),
    SATISFACTORY("Satisfactory", "Knows it", "80-84", "Doing fine. Play this letter again some time."),
    FAIRLY_SATISFACTORY("Fairly Satisfactory", "Almost there", "75-79", "Practice this sound together at home."),
    DID_NOT_MEET("Did Not Meet Expectations", "Needs more practice", "below 75", "Play this letter again together, slowly."),
    IN_PROGRESS("Still learning", "Still learning", null, "Started. Let your child finish this letter."),
    NOT_STARTED("Not started yet", "Not started yet", null, "Not played yet.");

    /** One of the five DepEd levels, with a grade behind it. */
    val isGraded: Boolean get() = band != null

    /** The two lowest levels: worth practicing together at home. */
    val needsPractice: Boolean get() = this == FAIRLY_SATISFACTORY || this == DID_NOT_MEET

    companion object {
        /** The DepEd level for a grade from 60 to 100. */
        fun forGrade(grade: Int): ProgressLevel = when {
            grade >= 90 -> OUTSTANDING
            grade >= 85 -> VERY_SATISFACTORY
            grade >= 80 -> SATISFACTORY
            grade >= 75 -> FAIRLY_SATISFACTORY
            else -> DID_NOT_MEET
        }

        /**
         * DepEd Order 8, s. 2015 transmutation table for [correct] right answers out of [attempts]: a percentage
         * score from 60 up gains a grade point every 1.6 points above 75 (60.00 to 61.59 is 75, 100 is 100), and below
         * 60 one every 4 points from 60 (0 is 60). Worked in whole numbers, so the table's edges are exact.
         */
        fun transmute(correct: Int, attempts: Int): Int {
            require(attempts > 0 && correct in 0..attempts)
            return if (100 * correct >= 60 * attempts) {
                75 + (1000 * correct - 600 * attempts) / (16 * attempts)
            } else {
                60 + (25 * correct) / attempts
            }
        }

        /** The five graded levels, best first, for a legend. */
        val graded: List<ProgressLevel> = values().filter { it.isGraded }
    }
}
