package com.playit.app.presentation.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    /**
     * The phone's path (background decode): when the same screen switches picture, as Hear It does on
     * "next letter", the old picture never shows again. It used to stay up, mouse on every letter (card 28d).
     */
    @Test fun backgroundLoad_newPictureReplacesTheOldOne() {
        AssetImageConfig.decodeSynchronously = false
        try {
            var path by mutableStateOf("images/pictures/picture_mouse.png")
            val seen = mutableListOf<Painter>()
            compose.setContent { seen += rememberAssetPainter(path, maxSize = 100.dp) }
            awaitDecode { seen.last() is BitmapPainter }
            val mouse = seen.last()
            val before = seen.size

            compose.runOnIdle { path = "images/pictures/picture_sun.png" }
            awaitDecode { seen.last() is BitmapPainter && seen.last() !== mouse }
            assertTrue("the mouse showed after the switch", seen.drop(before).none { it === mouse })
        } finally {
            AssetImageConfig.decodeSynchronously = true
        }
    }

    /**
     * Waits for a background decode. It runs on Dispatchers.IO and posts its result to the main looper,
     * which under Robolectric only runs when the test idles it; waitUntil alone never lets it land.
     */
    private fun awaitDecode(timeoutMs: Long = 15_000, loaded: () -> Boolean) {
        val end = System.currentTimeMillis() + timeoutMs
        while (!(AssetDecodeTracker.isIdle() && loaded())) {
            check(System.currentTimeMillis() < end) { "picture not loaded within $timeoutMs ms" }
            Thread.sleep(50)
            compose.waitForIdle()
        }
    }

    @Test fun missingAsset_isTransparentNotCrash() {
        var p: Painter? = null
        compose.setContent { p = rememberAssetPainter("images/pictures/does_not_exist.png") }
        compose.waitForIdle()
        assertTrue(p != null && p !is BitmapPainter)
    }
}
