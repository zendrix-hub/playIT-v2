package com.playit.app.screenshot

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.playit.app.presentation.components.drawLilyMouth
import com.playit.app.presentation.components.lipsync.Mouth
import com.playit.app.presentation.components.lipsync.TALKING_WIDE
import com.playit.app.presentation.components.lipsync.articulationMouth
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
}
