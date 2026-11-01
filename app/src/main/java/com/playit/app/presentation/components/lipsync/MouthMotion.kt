package com.playit.app.presentation.components.lipsync

import com.playit.app.domain.model.ArticulationGroup
import kotlin.math.abs
import kotlin.math.sign

/**
 * The mouth-shape pictures move (user, 2026-11-01: "on the mouth shapes can you animate that too").
 *
 * The released picture is the finished shape and is never changed: it is bent on a mesh, so its mouth comes from a
 * relaxed mouth and opens into the shape, the lower lip and the chin going with the jaw while the nose stays where it
 * is. Fading from one picture to another won't do: three of the pictures have a different nose and cheeks.
 */

/** Where a picture's mouth is, in fractions of its square; [chin] is the bottom of the face. */
data class MouthBox(val left: Float, val top: Float, val right: Float, val bottom: Float, val chin: Float)

/**
 * The relaxed mouth a shape opens from, as changes to its picture. [width] and [height] scale the mouth; [jaw] is how
 * far the chin comes up; [cheeks] how far the cheeks come in; [corners] how far the corners of the mouth come down
 * (or up, so that a nearly closed mouth doesn't frown); [reach] is how far from the middle the [width] change carries, when something beside the mouth goes with it.
 */
data class MouthMove(
    val box: MouthBox,
    val width: Float = 1f,
    val height: Float = 1f,
    val jaw: Float = 0f,
    val cheeks: Float = 0f,
    val corners: Float = 0f,
    val reach: Float = 0f,
)

private val FACE_A_CHIN = 0.793f   // back, open breath, round, teeth close, teeth on lip, tongue up
private val FACE_B_CHIN = 0.768f   // lips together, smile, wide open

/** Measured on each picture. What a child should see in a mirror: the jaw drops for a, the lips push out for w. */
fun mouthMove(group: ArticulationGroup): MouthMove = when (group) {
    // The lips part a little, then press together and the cheeks fill, for the hum of m.
    ArticulationGroup.LIPS_TOGETHER -> MouthMove(MouthBox(.377f, .478f, .63f, .586f, FACE_B_CHIN), width = .92f, height = 1.35f, jaw = -.012f, cheeks = .012f)
    // The lip comes up under the top teeth.
    ArticulationGroup.TEETH_ON_LIP -> MouthMove(MouthBox(.361f, .47f, .645f, .607f, FACE_A_CHIN), height = .45f, jaw = .012f)
    // A small opening, showing the tongue.
    ArticulationGroup.TONGUE_UP -> MouthMove(MouthBox(.344f, .465f, .656f, .64f, FACE_A_CHIN), width = .92f, height = .35f, jaw = .035f, corners = -.01f)
    // The mouth stretches a little and the teeth stay close.
    ArticulationGroup.TEETH_CLOSE -> MouthMove(MouthBox(.353f, .469f, .65f, .611f, FACE_A_CHIN), width = .84f, height = .5f, jaw = .01f, corners = .014f)
    // A relaxed drop of the jaw.
    ArticulationGroup.BACK_OF_MOUTH -> MouthMove(MouthBox(.365f, .45f, .637f, .675f, FACE_A_CHIN), width = .92f, height = .32f, jaw = .05f, corners = -.012f)
    // A flat wide mouth pushes in and out into a circle.
    ArticulationGroup.ROUND_LIPS -> MouthMove(MouthBox(.406f, .459f, .592f, .63f, FACE_A_CHIN), width = 1.4f, height = .6f, jaw = .01f)
    // The biggest drop of the jaw.
    ArticulationGroup.WIDE_OPEN -> MouthMove(MouthBox(.373f, .451f, .627f, .66f, FACE_B_CHIN), width = .92f, height = .28f, jaw = .065f, corners = -.014f)
    // The corners go out and up.
    ArticulationGroup.SMILE -> MouthMove(MouthBox(.328f, .465f, .674f, .617f, FACE_B_CHIN), width = .76f, height = .45f, jaw = .008f, corners = .02f)
    // The sideways move reaches the curls of breath, so they blow out of the mouth as it opens.
    ArticulationGroup.OPEN_BREATH -> MouthMove(MouthBox(.387f, .459f, .613f, .658f, FACE_A_CHIN), width = .78f, height = .35f, jaw = .04f, corners = -.01f, reach = .19f)
}

/** How finely a picture is bent. The bends are gentle curves, and a cell is about 2 dp on a 96 dp cue. */
const val MOUTH_MESH_CELLS = 32
const val MOUTH_MESH_FLOATS = (MOUTH_MESH_CELLS + 1) * (MOUTH_MESH_CELLS + 1) * 2

/** A little past open is its overshoot; closed is as far as it goes the other way. */
private const val MOST_OPEN = 1.25f

/** Fully open is the picture itself, so a settled cue draws it plainly. */
fun mouthAtRest(open: Float): Boolean = open == 1f

private fun smooth(from: Float, to: Float, v: Float): Float {
    val t = ((v - from) / (to - from)).coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}

private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t

/** How far the picture's point ([x], [y]) moves when its mouth is relaxed, across then down, in fractions of the square. */
private inline fun bend(m: MouthMove, x: Float, y: Float, result: (dx: Float, dy: Float) -> Unit) {
    val b = m.box
    val cx = (b.left + b.right) / 2f
    val halfWidth = (b.right - b.left) / 2f
    val fromMiddle = abs(x - cx)

    // The upper lip hardly moves and the lower lip takes the rest, as in speech: it's the jaw that opens a mouth.
    val hinge = b.top + (b.bottom - b.top) * .12f
    val lowerLip = (m.height - 1f) * (b.bottom - hinge)
    val upperLip = -lowerLip * .1f
    val mouth = when {
        y <= hinge -> upperLip * smooth(b.top - .035f, hinge, y)
        y <= b.bottom -> lerp(upperLip, lowerLip, (y - hinge) / (b.bottom - hinge))
        // The skin under the lip takes up the difference, well above the chin: the outline of the face only ever
        // moves with the jaw, as one piece, or it buckles.
        else -> lerp(lowerLip, -m.jaw, smooth(b.bottom, b.bottom + (b.chin - b.bottom) * .25f, y))
    }
    val jaw = smooth(b.top, b.chin, y) * m.jaw   // how much of the jaw has come up by this height
    // A bell across the mouth, like the curve of the lip, gone before the outline of the jaw (which rises towards the
    // sides as high as the lower lip).
    val nearMouth = 1f - smooth(halfWidth * .5f, halfWidth + .05f, fromMiddle)
    val nearChin = 1f - smooth(.24f, .46f, fromMiddle)
    val downMouth = nearMouth * (mouth + jaw) - jaw * nearChin

    // Across, the mouth narrows or widens, and only at its own height.
    val reach = maxOf(halfWidth, m.reach)
    val sideways = (1f - smooth(reach, reach + .1f, fromMiddle)) *
        smooth(b.top - .03f, b.top + .02f, y) * (1f - smooth(b.bottom, b.bottom + .07f, y))
    val across = (m.width - 1f) * (x - cx) * sideways
    val corners = m.corners * ((x - cx) / halfWidth).let { (it * it).coerceAtMost(1f) } * sideways
    val cheeks = -m.cheeks * sign(x - cx) * smooth(.12f, .34f, fromMiddle) * (1f - smooth(0f, .17f, abs(y - .52f)))

    result(across + cheeks, downMouth + corners)
}

/**
 * Fills [out] with where each mesh point of a [widthPx] by [heightPx] picture goes for this [open]: 0 is the relaxed
 * mouth and 1 the picture itself. Nothing is allocated, because this runs on every frame of the move.
 */
fun fillMouthMesh(m: MouthMove, open: Float, widthPx: Float, heightPx: Float, out: FloatArray) {
    val away = 1f - open.coerceIn(0f, MOST_OPEN)
    var i = 0
    for (row in 0..MOUTH_MESH_CELLS) {
        val y = row / MOUTH_MESH_CELLS.toFloat()
        for (col in 0..MOUTH_MESH_CELLS) {
            val x = col / MOUTH_MESH_CELLS.toFloat()
            bend(m, x, y) { dx, dy ->
                out[i++] = (x + dx * away) * widthPx
                out[i++] = (y + dy * away) * heightPx
            }
        }
    }
}
