package com.playit.app.presentation.components

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** In test mode (AssetImageConfig.decodeSynchronously) a picture is decoded on the first composition (card 17c). */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h740dp-xhdpi")
class AssetImageLoadTest {
    @get:Rule val compose = createComposeRule()

    @Before fun syncImages() { AssetImageConfig.decodeSynchronously = true }

    @Test fun pictureIsDecodedOnFirstComposition() {
        var first: Painter? = null
        compose.setContent {
            val p = rememberAssetPainter("images/pictures/blendword_sam.png", maxSize = 120.dp)
            if (first == null) first = p
        }
        compose.waitForIdle()
        val painter = first
        assertTrue("expected a decoded bitmap, got $painter", painter is BitmapPainter)
        assertTrue(painter!!.intrinsicSize.width >= 120f)
    }

    @Test fun missingAsset_isTransparentNotCrash() {
        var p: Painter? = null
        compose.setContent { p = rememberAssetPainter("images/pictures/does_not_exist.png") }
        compose.waitForIdle()
        assertTrue(p != null && p !is BitmapPainter)
    }
}
