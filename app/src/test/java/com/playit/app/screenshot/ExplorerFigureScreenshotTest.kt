package com.playit.app.screenshot

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.presentation.components.AssetImageConfig
import com.playit.app.presentation.map.components.CompanionAnimal
import com.playit.app.presentation.map.components.EXPLORER_SIZE
import com.playit.app.presentation.map.components.ExplorerFigure
import com.playit.app.presentation.map.components.build
import com.playit.app.presentation.map.components.gait
import com.playit.app.presentation.map.components.gaitAt
import com.playit.app.presentation.theme.PlayItTheme
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/** Each animal's move, a frame every 40 ms: the place to see that its limbs keep time with it (user, 2026-11-01). */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ExplorerFigureScreenshotTest {
    @get:Rule val compose = createComposeRule()

    @Before fun syncImages() { AssetImageConfig.decodeSynchronously = true }

    @Composable private fun Cell(animal: CompanionAnimal, ms: Float, facing: Float = 1f) {
        // Room above for a jump, and to the sides for the wings.
        Box(Modifier.size(width = 120.dp, height = 170.dp), contentAlignment = Alignment.BottomCenter) {
            ExplorerFigure(
                animal = animal,
                frame = { gaitAt(animal.gait, ms, facing) },
                facing = facing,
                modifier = Modifier.padding(bottom = 8.dp).size(EXPLORER_SIZE)
            )
        }
    }

    /** The body meets the head where its picture really ends, so a new picture can't leave it floating. */
    @Config(sdk = [34])
    @Test fun aHeadSitsOnItsBody_whereItsPictureEnds() {
        val main = if (File("src/main").exists()) File("src/main") else File("app/src/main")
        CompanionAnimal.values().forEach { animal ->
            val png = BitmapFactory.decodeFile(File(main, "assets/${animal.assetPath}").path)
            val bottom = (png.height - 1 downTo 0).first { y -> (0 until png.width).any { x -> png.getPixel(x, y) ushr 24 > 128 } }
            assertEquals("${animal.name}'s chin", (bottom + 1f) / png.height, animal.build.chin, 0.012f)
        }
    }

    @Config(sdk = [34], qualifiers = "w6200dp-h1100dp-mdpi")
    @Test fun gait_filmstrips() {
        compose.setContent {
            PlayItTheme {
                Column(Modifier.background(Color(0xFFB2F1C6))) {
                    CompanionAnimal.values().forEach { animal ->
                        Row { for (i in 0 until 51) Cell(animal, i * 40f) }
                    }
                }
            }
        }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/explorer_gaits.png")
    }

    @Config(sdk = [34], qualifiers = "w720dp-h340dp-xhdpi")
    @Test fun standing() {
        compose.setContent {
            PlayItTheme {
                Column(Modifier.background(Color(0xFFB2F1C6))) {
                    for (facing in listOf(1f, -1f)) {
                        Row { CompanionAnimal.values().forEach { animal -> Cell(animal, animal.gait.totalMs.toFloat(), facing) } }
                    }
                }
            }
        }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/explorer_standing.png")
    }
}
