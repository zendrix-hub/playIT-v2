# Card 17c: Pictures decode immediately in tests, so screenshots are reliable

Status: done

## Why
Claude's review of card 17 (2026-10-07) found that the Blend It screenshot sometimes shows an empty circle where the picture should be.
- **The app is fine.** On a phone the picture appears a moment later, which is card 17's intended background loading.
- **The tests are flaky.** `AssetDecodeTracker` (Claude's design in plan Task 1) can report "idle" before Compose has redrawn with the decoded picture.
- **Five debug runs** showed the decode always finished before the capture, but in about 1 run in 5 the redraw had not happened yet. Calling `Snapshot.sendApplyNotifications()` made it rarer but did not remove it.

The robust fix is a **test-only switch**, `AssetImageConfig.decodeSynchronously`:
- When a test turns it on, a picture decodes during the first composition, so it is always in the screenshot.
- Production never sets it, so phones keep the background loading from card 17.

Claude verified this on a throwaway copy of HEAD `9fc7016`:
- the full suite: **247 tests, 0 failed**;
- 3 screenshot recordings: the Blend It image was identical in all 3 runs (the picture is present).

Card 17 itself is accepted; agy implemented what the card said.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Modify: `presentation/components/AssetImage.kt`
- New test: `presentation/components/AssetImageLoadTest.kt`
- Modify (test mode on, tracker wait removed): `app/src/test/java/com/playit/app/screenshot/HearItScreenshotTest.kt`, `FindItScreenshotTest.kt`, `BlendItScreenshotTest.kt`, `NamePromptScreenshotTest.kt`, `LetterCompleteScreenshotTest.kt`

## Changes
1. **`AssetImage.kt`.** Apply exactly this diff, verified in Claude's copy:
```diff
diff --git a/app/src/main/java/com/playit/app/presentation/components/AssetImage.kt b/app/src/main/java/com/playit/app/presentation/components/AssetImage.kt
index 63a2929..6fd43af 100644
--- a/app/src/main/java/com/playit/app/presentation/components/AssetImage.kt
+++ b/app/src/main/java/com/playit/app/presentation/components/AssetImage.kt
@@ -4,6 +4,7 @@ import android.graphics.BitmapFactory
 import androidx.compose.runtime.Composable
 import androidx.compose.runtime.getValue
 import androidx.compose.runtime.produceState
+import androidx.compose.runtime.remember
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.graphics.asImageBitmap
 import androidx.compose.ui.graphics.painter.BitmapPainter
@@ -26,6 +27,15 @@ fun calculateInSampleSize(srcWidth: Int, srcHeight: Int, reqWidth: Int, reqHeigh
 
 private val transparent = ColorPainter(Color.Transparent)
 
+/**
+ * Test switch: when true, pictures decode during composition instead of on a background thread, so
+ * screenshot and layout tests always capture them. Production code never sets it (card 17c; Claude's
+ * review of 2026-10-07 found the background load raced the capture in about 1 run in 5).
+ */
+object AssetImageConfig {
+    @Volatile var decodeSynchronously: Boolean = false
+}
+
 /** Counts decodes in flight, so screenshot tests can wait until every picture on screen has loaded. */
 object AssetDecodeTracker {
     private val pending = java.util.concurrent.atomic.AtomicInteger(0)
@@ -38,11 +48,30 @@ object AssetDecodeTracker {
  * Loads an asset PNG at about [maxSize] (in dp) on a background thread. Shows nothing until
  * it is ready, and nothing if the file is missing (a missing asset never crashes a screen).
  */
+private fun decodeAsset(context: android.content.Context, assetPath: String, reqPx: Int): Painter? =
+    runCatching {
+        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
+        context.assets.open(assetPath).use { BitmapFactory.decodeStream(it, null, bounds) }
+        val opts = BitmapFactory.Options().apply {
+            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, reqPx, reqPx)
+        }
+        context.assets.open(assetPath).use { BitmapFactory.decodeStream(it, null, opts) }
+    }.getOrNull()?.let { bmp ->
+        AssetBitmapCache.put("$assetPath@$reqPx", bmp)
+        BitmapPainter(bmp.asImageBitmap())
+    }
+
 @Composable
 fun rememberAssetPainter(assetPath: String, maxSize: Dp = 160.dp): Painter {
     val context = LocalContext.current
     val reqPx = with(LocalDensity.current) { maxSize.roundToPx() }
     val key = "$assetPath@$reqPx"
+    if (AssetImageConfig.decodeSynchronously) {
+        return remember(key) {
+            AssetBitmapCache.get(key)?.let { BitmapPainter(it.asImageBitmap()) }
+                ?: decodeAsset(context, assetPath, reqPx) ?: transparent
+        }
+    }
     val painter by produceState<Painter>(
         initialValue = AssetBitmapCache.get(key)?.let { BitmapPainter(it.asImageBitmap()) } ?: transparent,
         key1 = key
```
2. **Each of the 5 screenshot tests:**
   - Under `@get:Rule val compose = createComposeRule()`, add:
     `@org.junit.Before fun syncImages() { com.playit.app.presentation.components.AssetImageConfig.decodeSynchronously = true }`
   - Delete the line `compose.waitUntil(5_000) { ...AssetDecodeTracker.isIdle() }`.
   - Keep the `waitForIdle()` calls.
3. Keep `AssetDecodeTracker` as it is. It is harmless, and a later card may use it.

## Tests
| Test file | Test | Assertion |
|---|---|---|
| AssetImageLoadTest | `pictureIsDecodedOnFirstComposition` | in test mode, the first composition already returns a `BitmapPainter` at least 120 px wide |
| AssetImageLoadTest | `missingAsset_isTransparentNotCrash` | a missing file returns a non-bitmap painter, with no exception |

The test file, exactly as it passed:
```kotlin
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
```

Run `./gradlew testDebugUnitTest`, then run `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'` **twice**. In `blendit_group1.png`, the Blend It picture (a boy waving) must show both times.

## Commit
`test(ui): pictures decode immediately in tests so screenshots are reliable (NFR-PERF-01)`

Decisions used: none new. This fixes Claude's plan design, found in the card 17 review.
