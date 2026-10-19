package com.playit.app.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.playit.app.presentation.theme.PlayItTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h740dp-xhdpi")
class GummyContainerLayoutTest {
    @get:Rule val compose = createComposeRule()

    @Test fun contentTallerThanMin_growsContainer() {
        compose.setContent {
            PlayItTheme {
                GummyContainer(modifier = Modifier.heightIn(min = 100.dp).testTag("box")) {
                    Box(Modifier.size(width = 50.dp, height = 180.dp).testTag("content"))
                }
            }
        }
        val box = compose.onNodeWithTag("box").getBoundsInRoot()
        assertTrue((box.bottom - box.top) >= 180.dp)
    }

    @Test fun wrapContentWidth_isNotZero() {
        compose.setContent {
            PlayItTheme {
                GummyContainer(modifier = Modifier.wrapContentWidth().heightIn(min = 56.dp).testTag("pill")) {
                    Text("Hear: /M/", modifier = Modifier.padding(16.dp))
                }
            }
        }
        val b = compose.onNodeWithTag("pill").getBoundsInRoot()
        assertTrue((b.right - b.left) > 60.dp)
    }

    @Test fun smallContent_keepsMinSize_andIsCentered() {
        compose.setContent {
            PlayItTheme {
                GummyContainer(modifier = Modifier.size(width = 200.dp, height = 64.dp).testTag("btn")) {
                    Box(Modifier.size(20.dp).testTag("dot"))
                }
            }
        }
        val btn = compose.onNodeWithTag("btn").getBoundsInRoot()
        val dot = compose.onNodeWithTag("dot").getBoundsInRoot()
        assertEquals(64.dp.value, (btn.bottom - btn.top).value, 0.5f)
        assertEquals((btn.left + btn.right).value / 2, (dot.left + dot.right).value / 2, 1f)
    }
}
