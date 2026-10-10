package com.playit.app.presentation.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.playit.app.domain.model.ArticulationGroup
import com.playit.app.presentation.theme.PlayItTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Review Focus 4: before its image release, a mouth cue renders nothing (no crash, no empty box). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ArticulationCueTest {
    @get:Rule val compose = createComposeRule()

    @Test fun missingAsset_rendersNothing() {
        compose.setContent {
            PlayItTheme {
                ArticulationCue(group = ArticulationGroup.LIPS_TOGETHER, size = 72.dp, modifier = Modifier.testTag("cue"))
            }
        }
        compose.onNodeWithTag("cue").assertDoesNotExist()   // the picture is not in assets yet
    }
}
