package com.playit.app.presentation.components.lipsync

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.talkFramePath
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * The new Lily (user, 2026-11-07: "this is our updated mascot"): what TalkingLily draws lands on her pictures. Each
 * blink's lid sits on the white of each eye, inside its orange ring, and the mouth she draws while she speaks sits
 * where the talking frame painted the picture's mouth out.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class NewLilyPicturesTest {
    private val assets = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.exists() }

    private fun picture(path: String): Bitmap =
        BitmapFactory.decodeFile(File(assets, path).path, BitmapFactory.Options().apply { inScaled = false })

    private fun Bitmap.rgb(x: Float, y: Float): Triple<Int, Int, Int> {
        val c = getPixel(x.toInt(), y.toInt())
        return Triple((c shr 16) and 0xFF, (c shr 8) and 0xFF, c and 0xFF)
    }

    private fun white(c: Triple<Int, Int, Int>) = c.first > 225 && c.second > 225 && c.third > 225
    private fun ringOrange(c: Triple<Int, Int, Int>) = c.first > 200 && c.second in 80..150 && c.third < 90

    @Test fun eachBlink_coversTheWhiteOfEachEye_insideItsRing() {
        MascotState.values().forEach { pose ->
            val eyes = poseEyes(pose) ?: return@forEach
            val img = picture(pose.assetPath)
            for ((x, y, outward) in listOf(Triple(eyes.leftX, eyes.leftY, -1f), Triple(eyes.rightX, eyes.rightY, 1f))) {
                val r = eyes.radius
                // The white shows round the pupil: at least two of the four points just inside the eye's edge.
                val inside = listOf(x - 0.86f * r to y, x + 0.86f * r to y, x to y - 0.86f * r, x to y + 0.86f * r)
                    .count { (px, py) -> white(img.rgb(px, py)) }
                assertTrue("${pose.name} eye at ($x, $y): white inside the lid's circle ($inside of 4)", inside >= 2)
                // Just outside the lid's circle is the orange ring, so the lid never covers the glasses.
                val ring = img.rgb(x + outward * 1.15f * r, y)
                assertTrue("${pose.name} eye at ($x, $y): ring outside it, found $ring", ringOrange(ring))
            }
        }
    }

    @Test fun eachTalkingFrame_changesOnlyTheMouth_whereTheDrawnMouthGoes() {
        MascotState.values().forEach { pose ->
            val still = picture(pose.assetPath)
            val talk = picture(pose.talkFramePath())
            val m = poseMouth(pose)
            var changed = 0
            var x0 = Int.MAX_VALUE; var x1 = -1; var y0 = Int.MAX_VALUE; var y1 = -1
            for (y in 0 until still.height) for (x in 0 until still.width) {
                if (still.getPixel(x, y) != talk.getPixel(x, y)) {
                    changed++
                    x0 = minOf(x0, x); x1 = maxOf(x1, x); y0 = minOf(y0, y); y1 = maxOf(y1, y)
                }
            }
            assertTrue("${pose.name}: the talking frame paints a mouth out", changed > 40)
            val half = maxOf(m.width, 13f) / 2f + 8f
            assertTrue("${pose.name}: changes x $x0..$x1 sit round the drawn mouth at ${m.centerX}",
                x0 >= m.centerX - half && x1 <= m.centerX + half)
            assertTrue("${pose.name}: changes y $y0..$y1 start at the mouth's top ${m.top}", y0 >= m.top - 7f && y1 <= m.top + 60f)
        }
    }
}
