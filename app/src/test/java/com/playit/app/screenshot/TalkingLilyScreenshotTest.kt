package com.playit.app.screenshot

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.domain.model.ArticulationGroup
import com.playit.app.presentation.components.AssetImageConfig
import com.playit.app.presentation.components.MascotState
import com.playit.app.presentation.components.TalkingLily
import com.playit.app.presentation.components.drawLilyBlink
import com.playit.app.presentation.components.drawLilyMouth
import com.playit.app.presentation.components.lilyMove
import com.playit.app.presentation.components.lipsync.LilyMove
import com.playit.app.presentation.components.lipsync.LilyTap
import com.playit.app.presentation.components.lipsync.STANDING
import com.playit.app.presentation.components.lipsync.SWAP_MS
import com.playit.app.presentation.components.lipsync.poseChangeAt
import com.playit.app.presentation.components.lipsync.tapMoveAt
import com.playit.app.presentation.components.lipsync.Mouth
import com.playit.app.presentation.components.lipsync.TALKING_WIDE
import com.playit.app.presentation.components.lipsync.articulationMouth
import com.playit.app.presentation.components.lipsync.poseEyes
import com.playit.app.presentation.components.lipsync.poseMouth
import com.playit.app.presentation.components.rememberAssetPainter
import com.playit.app.presentation.components.talkFramePath
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Galleries of Lily's drawn mouth, for review: every pose resting and talking, and every letter-sound mouth. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w800dp-h1280dp-mdpi")
class TalkingLilyScreenshotTest {
    @get:Rule val compose = createComposeRule()

    @Before fun syncImages() { AssetImageConfig.decodeSynchronously = true }

    @Composable
    private fun Frame(pose: MascotState, mouth: Mouth?, label: String) {
        Column(Modifier.padding(4.dp)) {
            Box(Modifier.size(180.dp).background(Color(0xFFDDE6F0))) {
                if (mouth == null) {
                    TalkingLily(pose, Modifier.fillMaxSize())     // no clip playing: the released picture
                } else {
                    Image(rememberAssetPainter(pose.talkFramePath()), null, Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                    Canvas(Modifier.fillMaxSize()) { drawLilyMouth(poseMouth(pose), mouth) }
                }
            }
            Text(label, fontSize = 11.sp)
        }
    }

    @Test fun poses_restingAndTalking() {
        compose.setContent {
            Column(Modifier.background(Color.White)) {
                MascotState.values().toList().chunked(4).forEach { row ->
                    Row {
                        row.forEach { pose ->
                            val pm = poseMouth(pose)
                            Column {
                                Frame(pose, null, "${pose.name} still")
                                Frame(pose, pm.rest, "between words")
                                Frame(pose, pm.rest.lerp(TALKING_WIDE, 1f), "talking, loud")
                            }
                        }
                    }
                }
            }
        }
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/talking_lily_poses.png")
    }

    /** Every pose that can blink, with the lids half down and shut, as they look at the start of a line. */
    @Test fun blinks() {
        compose.setContent {
            Column(Modifier.background(Color.White)) {
                MascotState.values().filter { poseEyes(it) != null }.chunked(2).forEach { row ->
                    Row {
                        row.forEach { pose ->
                            listOf(0.5f, 1f).forEach { closed ->
                                Column(Modifier.padding(4.dp)) {
                                    Box(Modifier.size(150.dp).background(Color(0xFFDDE6F0))) {
                                        Image(rememberAssetPainter(pose.assetPath), null, Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                                        Canvas(Modifier.fillMaxSize()) { drawLilyBlink(poseEyes(pose)!!, closed) }
                                    }
                                    Text("${pose.name} ${(closed * 100).toInt()}%", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/talking_lily_blinks.png")
    }

    @Test fun letterSoundMouths() {
        compose.setContent {
            Column(Modifier.background(Color.White)) {
                ArticulationGroup.values().toList().chunked(4).forEach { row ->
                    Row { row.forEach { Frame(MascotState.IDLE, articulationMouth(it), it.name) } }
                }
            }
        }
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/talking_lily_letter_sounds.png")
    }

    /** One row of a filmstrip: a frame every FILM_STEP_MS, [frame] giving the picture shown and her move at that time. */
    @Composable
    private fun FilmRow(label: String, frame: (Float) -> Pair<MascotState, LilyMove>) {
        Row(Modifier.height(90.dp)) {
            Text(label, fontSize = 11.sp, modifier = Modifier.width(110.dp))
            repeat(FILM_FRAMES) { i ->
                val (pose, move) = frame(i * FILM_STEP_MS)
                Box(Modifier.size(72.dp, 90.dp).background(Color(0xFFDDE6F0)), contentAlignment = Alignment.BottomCenter) {
                    Image(rememberAssetPainter(pose.assetPath), null, Modifier.size(72.dp).lilyMove { move }, contentScale = ContentScale.Fit)
                }
            }
        }
    }

    /**
     * Lily acting out each pose as she changes to it from idle, then her answers to taps (LilyActing.kt): a frame every
     * 40 ms, for review. Each 72 x 90 frame can be cut out in turn to play the row as a moving picture.
     */
    @Config(sdk = [34], qualifiers = "w2600dp-h1000dp-mdpi")
    @Test fun acting_filmstrips() {
        compose.setContent {
            Column(Modifier.background(Color.White)) {
                MascotState.values().forEach { pose ->
                    FilmRow(pose.name) { t -> (if (t < SWAP_MS) MascotState.IDLE else pose) to poseChangeAt(pose, t, STANDING) }
                }
                LilyTap.values().forEach { tap ->
                    FilmRow("tap ${tap.name}") { t -> MascotState.IDLE to tapMoveAt(tap, t) }
                }
            }
        }
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/talking_lily_acting.png")
    }

    private companion object {
        const val FILM_FRAMES = 34
        const val FILM_STEP_MS = 40f
    }
}
