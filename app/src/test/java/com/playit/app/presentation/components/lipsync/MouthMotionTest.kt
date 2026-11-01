package com.playit.app.presentation.components.lipsync

import com.playit.app.domain.model.ArticulationGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/** The mouth-shape pictures move, and only their mouths and jaws (user, 2026-11-01: "can you animate that too"). */
class MouthMotionTest {

    private val side = MOUTH_MESH_CELLS + 1
    private val openings = (0..25).map { it / 20f }   // closed to past open

    private fun mesh(g: ArticulationGroup, open: Float) =
        FloatArray(MOUTH_MESH_FLOATS).also { fillMouthMesh(mouthMove(g), open, 1f, 1f, it) }

    private fun FloatArray.x(col: Int, row: Int) = this[(row * side + col) * 2]
    private fun FloatArray.y(col: Int, row: Int) = this[(row * side + col) * 2 + 1]

    /** The mesh point nearest ([x], [y]) in the picture. */
    private fun cell(v: Float) = (v * MOUTH_MESH_CELLS).toInt().coerceIn(0, MOUTH_MESH_CELLS)

    /** How tall the mouth is at its middle, with its box bent. */
    private fun mouthHeight(g: ArticulationGroup, open: Float): Float {
        val m = mesh(g, open)
        val box = mouthMove(g).box
        val col = cell((box.left + box.right) / 2f)
        return m.y(col, cell(box.bottom)) - m.y(col, cell(box.top))
    }

    private fun mouthWidth(g: ArticulationGroup, open: Float): Float {
        val m = mesh(g, open)
        val box = mouthMove(g).box
        val row = cell((box.top + box.bottom) / 2f)
        return m.x(cell(box.right), row) - m.x(cell(box.left), row)
    }

    private fun chinY(g: ArticulationGroup, open: Float) =
        mesh(g, open).y(cell((mouthMove(g).box.left + mouthMove(g).box.right) / 2f), cell(mouthMove(g).box.chin))

    @Test fun fullyOpen_isThePictureItself() {
        ArticulationGroup.values().forEach { g ->
            val m = mesh(g, 1f)
            for (row in 0 until side) for (col in 0 until side) {
                assertEquals("$g x", col / MOUTH_MESH_CELLS.toFloat(), m.x(col, row), 0f)
                assertEquals("$g y", row / MOUTH_MESH_CELLS.toFloat(), m.y(col, row), 0f)
            }
        }
        assertTrue(mouthAtRest(1f))
        assertTrue(!mouthAtRest(.99f) && !mouthAtRest(1.01f))
    }

    @Test fun theNoseAndTheTopOfTheFaceNeverMove() {
        ArticulationGroup.values().forEach { g ->
            openings.forEach { open ->
                val m = mesh(g, open)
                fun stillAt(col: Int, row: Int) {
                    assertEquals("$g $open", col / MOUTH_MESH_CELLS.toFloat(), m.x(col, row), 0.0001f)
                    assertEquals("$g $open", row / MOUTH_MESH_CELLS.toFloat(), m.y(col, row), 0.0001f)
                }
                // The top of the face, right across.
                for (row in 0..cell(.34f)) for (col in 0 until side) stillAt(col, row)
                // The nose, down to where it ends. The cheeks beside it may fill.
                for (row in 0..cell(.41f)) for (col in cell(.38f)..cell(.62f)) stillAt(col, row)
            }
        }
    }

    /** A picture bent so far that it folds over itself would show a torn mouth. */
    @Test fun thePictureNeverFoldsOverItself() {
        ArticulationGroup.values().forEach { g ->
            openings.forEach { open ->
                val m = mesh(g, open)
                for (row in 0 until side) for (col in 0 until side) {
                    if (col > 0) assertTrue("$g $open across at $col,$row", m.x(col, row) > m.x(col - 1, row))
                    if (row > 0) assertTrue("$g $open down at $col,$row", m.y(col, row) > m.y(col, row - 1))
                }
            }
        }
    }

    /** The picture has clear space around its face, so a move this small keeps the face inside the cue's circle. */
    @Test fun nothingMovesFarFromItsSquare() {
        ArticulationGroup.values().forEach { g ->
            openings.forEach { open -> mesh(g, open).forEach { assertTrue("$g $open: $it", it in -0.02f..1.02f) } }
        }
    }

    /** The outline of the chin moves as one piece: no part of it comes up much further than the part beside it. */
    @Test fun theChinNeverBuckles() {
        ArticulationGroup.values().forEach { g ->
            val box = mouthMove(g).box
            val m = mesh(g, 0f)
            val row = cell(box.chin - .02f)
            val cx = cell((box.left + box.right) / 2f)
            val rises = (cx - 8..cx + 8).map { col -> row / MOUTH_MESH_CELLS.toFloat() - m.y(col, row) }
            rises.zipWithNext().forEach { (a, b) -> assertTrue("$g: $a then $b", abs(a - b) < .006f) }
        }
    }

    /** Out where the outline of the jaw runs, the lips' pull is gone: a steep step here shows as a jagged face. */
    @Test fun theOutlineOfTheJaw_isNeverTornByTheLips() {
        ArticulationGroup.values().forEach { g ->
            val box = mouthMove(g).box
            val m = mesh(g, 0f)
            for (row in cell(box.chin - .07f)..cell(box.chin)) {
                val rise = { col: Int -> row / MOUTH_MESH_CELLS.toFloat() - m.y(col, row) }
                (0 until side - 1).forEach { col ->
                    assertTrue("$g row $row col $col: ${rise(col)} then ${rise(col + 1)}", abs(rise(col) - rise(col + 1)) < .016f)
                }
            }
        }
    }

    @Test fun aSmallStepOfTheOpening_isASmallStepOfThePicture() {
        ArticulationGroup.values().forEach { g ->
            openings.zipWithNext().forEach { (a, b) ->
                val p = mesh(g, a)
                val q = mesh(g, b)
                p.indices.forEach { assertTrue("$g $a", abs(p[it] - q[it]) < .03f) }
            }
        }
    }

    @Test fun openMouths_closeToAFractionOfTheirHeight() {
        val openMouths = ArticulationGroup.values().filter { it != ArticulationGroup.LIPS_TOGETHER }
        openMouths.forEach { g ->
            assertTrue("$g", mouthHeight(g, 0f) < mouthHeight(g, 1f) * .75f)
            assertTrue("$g still has a mouth", mouthHeight(g, 0f) > 0f)
        }
    }

    @Test fun eachShape_movesTheWayAChildSeesItInAMirror() {
        fun drop(g: ArticulationGroup) = chinY(g, 1f) - chinY(g, 0f)
        ArticulationGroup.values().filter { it != ArticulationGroup.WIDE_OPEN }.forEach {
            assertTrue("a opens the jaw most, more than $it", drop(ArticulationGroup.WIDE_OPEN) > drop(it))
        }
        assertTrue("m's lips press together", mouthHeight(ArticulationGroup.LIPS_TOGETHER, 0f) > mouthHeight(ArticulationGroup.LIPS_TOGETHER, 1f))
        assertTrue("w's lips push in", mouthWidth(ArticulationGroup.ROUND_LIPS, 0f) > mouthWidth(ArticulationGroup.ROUND_LIPS, 1f) * 1.2f)
        assertTrue("e's lips spread", mouthWidth(ArticulationGroup.SMILE, 0f) < mouthWidth(ArticulationGroup.SMILE, 1f) * .85f)
        assertTrue("s's lips spread a little", mouthWidth(ArticulationGroup.TEETH_CLOSE, 0f) < mouthWidth(ArticulationGroup.TEETH_CLOSE, 1f))
    }

    @Test fun everyShape_hasItsOwnMove() {
        assertEquals(ArticulationGroup.values().size, ArticulationGroup.values().map { mouthMove(it) }.toSet().size)
    }

    @Test fun theCueSaysTheSoundAndTheKeyWord_notTheOtherLines() {
        assertTrue(isLetterModel("audio/phonemes/ph_m.wav"))
        assertTrue(isLetterModel("audio/phonemes/phoneme_b.mp3"))
        assertTrue(isLetterModel("audio/keywords/kw_mouse.wav"))
        assertTrue(!isLetterModel("audio/tutor/car_watch_my_lips.wav"))
        assertTrue(!isLetterModel("audio/ui/sfx_correct.mp3"))
    }
}
