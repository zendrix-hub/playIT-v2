package com.playit.app.presentation.components

import android.os.SystemClock
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.playit.app.data.audio.VoiceClip
import com.playit.app.domain.model.ArticulationGroup
import com.playit.app.presentation.theme.PlayItTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Mouth cue: a released picture shows; a picture not in assets renders nothing, with no empty box (Review Focus 4). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ArticulationCueTest {
    @get:Rule val compose = createComposeRule()

    @Test fun releasedPicture_renders() {
        compose.setContent {
            PlayItTheme {
                ArticulationCue(group = ArticulationGroup.LIPS_TOGETHER, size = 72.dp, modifier = Modifier.testTag("cue"))
            }
        }
        compose.onNodeWithTag("cue").assertContentDescriptionEquals("Mouth shape for this sound")
    }

    /** It moves with the model's voice, then stops: a cue that kept running would never let the test go idle. */
    @Test fun whenTheModelSoundEnds_theCueSettlesAndStopsMoving() {
        val clip = MutableStateFlow<VoiceClip?>(null)
        compose.setContent {
            CompositionLocalProvider(LocalVoicePlayback provides clip) {
                PlayItTheme {
                    ArticulationCue(group = ArticulationGroup.WIDE_OPEN, size = 96.dp, modifier = Modifier.testTag("cue"))
                }
            }
        }
        compose.mainClock.autoAdvance = false
        clip.value = VoiceClip("audio/phonemes/ph_a.wav", SystemClock.uptimeMillis())
        compose.mainClock.advanceTimeBy(300)
        compose.onNodeWithTag("cue").assertExists()
        clip.value = null
        compose.mainClock.autoAdvance = true
        compose.waitForIdle()
        compose.onNodeWithTag("cue").assertContentDescriptionEquals("Mouth shape for this sound")
    }

    @Test fun missingAsset_rendersNothing() {
        compose.setContent {
            PlayItTheme {
                ArticulationCue(assetPath = "images/mouth/mouth_not_released.png", size = 72.dp, modifier = Modifier.testTag("cue"))
            }
        }
        compose.onNodeWithTag("cue").assertDoesNotExist()
    }
}
