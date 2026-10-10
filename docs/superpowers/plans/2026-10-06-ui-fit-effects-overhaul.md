# PlayIT UI Fit, Performance and Purposeful Effects: Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. One task = one card = one agy session (AGENTS.md "Workflow"). Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Every child-facing screen fits any phone. The main action is visible without scrolling, nothing is clipped, and the layout scales from 360x640 to tablets. The app runs smoothly on a low-end phone (Samsung Galaxy A21s). Effects are used only where they teach or reward. Say It shows clear mic states, and sounds get captions and mouth-shape cues. All of this lands before Round 2 (Oct 19).

**Architecture:**
1. A small adaptive-layout layer comes first:
   - a window profile and a dimension set, provided by `PlayItTheme`;
   - `GummyContainer` grows with its content;
   - a shared `LessonScaffold` that fills the screen when content fits and scrolls only when it can't.
2. A performance and motion foundation:
   - images decoded at display size, off the main thread;
   - no haptics;
   - no endless idle animations;
   - every effect honours reduced motion.
3. The screens are rebuilt on top of these layers, one card at a time. Each card is checked by Robolectric layout tests and Roborazzi screenshots at four device sizes.

**Tech Stack:** Kotlin 1.9.23, Jetpack Compose (BOM 2024.05.00), Navigation Compose 2.7.7, Hilt, Room, Vosk 0.3.47; tests use JUnit4, MockK, Robolectric 4.13 and Roborazzi 1.26.0 (card 10).

**Spec:** The user's request of 2026-10-06 and these documents:
- the decisions below;
- `docs/engineering-package/03_DESIGN_SYSTEM_SUMMARY.md`;
- `docs/engineering-package/21_ANIMATION_GUIDE.md`;
- `docs/engineering-package/26_MASCOT_COPLAYER_SYSTEM.md`;
- `docs/SRS_v3.0_Refactored.md` (FR-03, NFR-ACC-01, NFR-ACC-02);
- `docs/defense/DEFENSE_REVIEWER.md`.

AGENTS.md wins where it conflicts.

**User decisions (2026-10-06), binding for this plan:**
1. The new UI lands **before Round 2**: the code cards are done by **Fri Oct 16**.
2. Keep the current style (gummy buttons, Lily, palette). Fix fit and layout; no new visual identity.
3. **Remove haptics** (the user's phone lagged).
4. **Effects on meaningful moments only:** tap, correct, wrong, unlock, celebration, screen change. Remove always-on decorative animation. The current map node may keep its gentle pulse, since it marks "where to go".
5. **Mouth-shape cues** come as about 9 shape groups covering all 26 letters, plus sound captions.
6. **Portrait only.**
7. **Target phone: Samsung Galaxy A21s** (6.5" HD+, about 360 x 740 dp usable, Exynos 850, low-end GPU). The smallest supported size is 360 x 640 dp.
8. **Say It mic states:** Idle, Listening, Heard (speech detected), Result. The "voice-driven" ripple (live loudness) needs replacing Vosk's `SpeechService` with an `AudioRecord` loop. That is too risky before Round 2, so Listening uses an animated ripple, and "Heard" switches on when Vosk returns the first partial words. The live-level ripple is post-Round-2 work.

---

## Global Constraints

Every task's requirements include these. Each comes from the source file named.

- **Domain layer:** `domain/` stays pure Kotlin, with zero `android.*` imports (AGENTS.md).
- **Zero emoji** in any string, label or comment. `ZeroEmojiPolicyTest` must pass (AGENTS.md).
- **Touch targets:** at least **64 dp** for every child-facing interactive element, with at least 8 dp spacing; adult screens at least 48 dp (03 §5.3 :64; SRS NFR-ACC-02).
- **Text sizes:** never below **16 sp**. Text the child is meant to sound out (letters, words) is at least **24 sp** (03 :37, :59).
- **Colours:** wrong answers use `GentleCorrectionOrange`, never red. Never use colour alone: pair it with shape, icon or text (03 :24, :30, :54).
- **Motion durations:** micro 150-250 ms, standard 300-500 ms, celebration 600-1200 ms. Screen change is a fade plus a slight upward move of 200-300 ms, critically damped, no bounce. A tap is 100 -> 92 -> 100 %. Correct is 100 -> 108 -> 100 % plus a chime. Wrong is a gentle shake only (21_ANIMATION_GUIDE :18-28, :47-51).
- **Reduced motion:** when `LocalReducedMotion` is true, no particles, no bounce and no shake; fades only (03 :71; 21 :64).
- **No haptics** anywhere (user decision 3).
- **Portrait only** (user decision 6).
- **No network calls.** Assets enter `app/src/main/assets/` only from a `docs/audio-release/` or `docs/image-release/` manifest (AGENTS.md).
- **One card per agy session:**
  - Change only the card's files plus the bookkeeping files.
  - Tick the card in `13_MASTER_TASKS.md`, set `Status: done`, and add an evidence-log row.
  - Commit on `refactor/hear-say-it` with the body lines `Card / Requirement / Tests run / Decisions used`.
  - Run `./gradlew testDebugUnitTest` before committing.
- **Screenshot sizes** (Robolectric qualifiers) used by every layout test in this plan:

  | Name | Qualifier | Why |
  |---|---|---|
  | `compact` | `w360dp-h640dp-xhdpi` | smallest supported |
  | `a21s` | `w360dp-h740dp-xhdpi` | the user's phone |
  | `phone` | `w411dp-h891dp-xxhdpi` | common large phone |
  | `tablet` | `w800dp-h1280dp-mdpi` | classroom tablet, portrait |

## Review Focus

Five failure modes that the spec implies and no feature test covers. Each one has a test in the owning task.

1. **Samsung "Large" font (font scale 1.3) on the A21s:** the main action must stay visible, and no text may be clipped. Test owner: Task 2, `LayoutMatrixTest.fontScale13_mainActionVisible`. Tasks 3-6 each add their screen to it.
2. **App sent to the background while the mic is listening** (`MainActivity.onStop` stops Vosk): when the child returns, the mic must not be stuck in "Listening". Test owner: Task 3, `SayItViewModelTest.recognizerStoppedExternally_returnsToIdle`.
3. **A 16-character child name on the map top bar:** the stat pills must stay on screen, and the name is ellipsized. Test owner: Task 6, `TopStatsBarLayoutTest.longName_pillsStayVisible`.
4. **A mouth-cue picture missing from assets** (before its image release): the cue area must render nothing, with no crash and no empty box. Test owner: Task 8, `ArticulationCueTest.missingAsset_rendersNothing`.
5. **Tablet width:** the content column stays at most 560 dp wide and centred, so cards don't stretch edge to edge. Test owner: Task 2, `LayoutMatrixTest.tablet_contentWidthCapped`.

---

## File Structure

New files, each with one responsibility:

| File | Responsibility | Task |
|---|---|---|
| `presentation/theme/Dimens.kt` | `WindowProfile`, `PlayItDimens`, `dimensFor()`, `LocalPlayItDimens` | 2 |
| `presentation/theme/PlayItMotion.kt` | Every duration and animation spec, with reduced-motion variants (replaces `Motion.kt`) | 1 |
| `presentation/components/AssetImage.kt` | `calculateInSampleSize()`, async downscaled `rememberAssetPainter(path, maxSize)` | 1 |
| `presentation/components/LessonScaffold.kt` | Shared lesson layout: top bar, header, fit-or-scroll body, pinned bottom bar | 2 |
| `presentation/sayit/MicStatus.kt` | `MicStatus` enum and `micStatusFor()` (pure mapping) | 3 |
| `presentation/sayit/components/MicButton.kt` | The 4-state mic button | 3 |
| `presentation/map/MapLayout.kt` | Pure node-position math, scaled to width | 6 |
| `presentation/components/FeedbackEffects.kt` | Correct pop, heart-loss wobble, star drop, center confetti trigger | 7 |
| `domain/model/ArticulationGroup.kt` | Letter -> mouth-shape group (pure Kotlin) | 8 |
| `domain/manager/CaptionText.kt` | Clip path -> caption text (pure Kotlin) | 8 |
| `presentation/components/ArticulationCue.kt`, `CaptionBubble.kt` | Mouth picture and caption UI | 8 |
| `app/src/test/.../screenshot/Devices.kt`, `LayoutMatrixTest.kt` | The 4 device qualifiers, a font-scale helper, and the shared layout checks (one subclass per size) | 2 |

---

## Schedule and owners

| Day | agy (main executor) | Claude (second hand) |
|---|---|---|
| Tue Oct 6 | Card 14 (privacy, already ready) | Write cards 17-25 from this plan; mouth-shape brief; reviewer corrections |
| Wed Oct 7 | **Card 17** (Task 1: performance and calm motion), then **card 17b** (ViewModel init order; found by the dry run) | Review card 14. Audio: Kokoro remakes of the lesson VO lines (review page) |
| Thu Oct 8 | **Card 18** (Task 2: adaptive foundation) | Review card 17 on the A21s checklist |
| Fri Oct 9 | **Card 19** (Task 3: Hear It + Say It + mic states) | Review card 18; image session brief for mouth shapes (card 25 ready) |
| Mon Oct 12 | **Card 20** (Task 4: Find It + Blend It) | Review card 19; image release for batch 1, if the user OK'd it |
| Tue Oct 13 | **Card 21** (Task 5: complete screens, splash, profile) | Review card 20; mouth-shape cutouts and release |
| Wed Oct 14 | **Card 22** (Task 6: map) | Review card 21 |
| Thu Oct 15 | **Card 23** (Task 7: purposeful effects) | Review card 22 |
| Fri Oct 16 | **Card 24** (Task 8: captions and mouth cues) | Review cards 23-24; build the Round 2 APK; A21s phone-test checklist |
| any evening | **Card 25** (asset session: mouth-shape pictures in rounds) | Cut out, audit and release the user's picks |

**Cut line, if days slip:** cards 17, 18, 19, 20 and 22 must land before Round 2. Cards 21, 23 and 24 may move to Oct 26-30 (before the Week 8 freeze). The existing cards 13, 09, 03b and 15 slot in when their assets are approved. Two cards may run in one night only if their files don't overlap (runbook "Two cards a night").

**Weekday review loop (Claude, when the user says "run and review"):**
1. `git pull`.
2. `python3 tools/dev/review_card.py NN`.
3. Run `ROBOLECTRIC_DEPS_DIR=$HOME/.playit-env/robolectric-deps tools/dev/gradlew_wsl.sh testDebugUnitTest`, then `tools/dev/ci_status.sh`.
4. Download the `playIT-screenshots` artifact and look at all 4 sizes.
5. Read the diff against the card.
6. Accept, or write a fix card (NNb).
7. Update `SESSION_HANDOFF.md`.

---

### Task 1 (card 17): Performance and calm motion foundation

**Why:**
- The user's A21s lags.
- `GummyContainer` fires a haptic on every tap (`components/GummyButton.kt:111`).
- `breathingPulse()` and `idleBounce()` start an endless animation *before* checking `enabled` or reduced motion (`components/PulseModifier.kt:17-61`), so disabled pulses still animate every frame.
- `LetterCard` breathes forever (`LetterCard.kt:88`).
- `GummyMotionAsset` floats every picture forever.
- `MascotSpeechHeader` breathes Lily forever (`:67-84`).
- Every image decodes at full 512 px (1 MB) on the main thread (`components/AssetUtils.kt:41-62`), though it is drawn at 70-120 dp.
- The app is not locked to portrait.

**Files:**
- Modify: `app/src/main/AndroidManifest.xml` (activity `screenOrientation`)
- Modify: `presentation/components/GummyButton.kt` (remove haptic only)
- Modify: `presentation/components/PulseModifier.kt`
- Modify: `presentation/components/GummyMotionAsset.kt`
- Modify: `presentation/components/LetterCard.kt` (remove `.breathingPulse()` and its `isIdleFloating = true` argument only)
- Modify (remove the explicit `isIdleFloating = true` / `isIdleFloating = !isCorrect` argument only): `presentation/components/BlendItCard.kt`, `presentation/components/FindItGrid.kt`, `presentation/components/MascotBubble.kt` (and `LetterCard.kt`, above)
- Modify: `presentation/components/MascotSpeechHeader.kt` (remove breathing only)
- Modify: `presentation/components/AssetUtils.kt` (delegate to `AssetImage.kt`)
- Create: `presentation/components/AssetImage.kt`
- Create: `presentation/theme/PlayItMotion.kt`; Delete: `presentation/theme/Motion.kt` (no code uses it; checked 2026-10-06)
- Delete: `app/src/main/assets/audio/vo/vo_*.mp3` (26 byte-identical duplicates of `audio/ui/vo_*`; no code reference) and `app/src/main/assets/audio/tts_*.mp3` (one stray file, `tts_[exci_20260816_104503.mp3`)
- Test: `app/src/test/java/com/playit/app/presentation/components/AssetImageTest.kt`
- Test: `app/src/test/java/com/playit/app/PerformancePolicyTest.kt`
- Modify (screenshot tests only): `app/src/test/java/com/playit/app/screenshot/HearItScreenshotTest.kt`, `FindItScreenshotTest.kt`, `BlendItScreenshotTest.kt`, `NamePromptScreenshotTest.kt`, `LetterCompleteScreenshotTest.kt`. After `compose.waitForIdle()`, add `compose.waitUntil(5_000) { AssetDecodeTracker.isIdle() }` and then `compose.waitForIdle()` again, before the capture. Claude's dry run of 2026-10-06 showed that without this, the Blend It picture is still loading when the screenshot is taken.

**Interfaces:**
- Produces:
  - `fun calculateInSampleSize(srcWidth: Int, srcHeight: Int, reqWidth: Int, reqHeight: Int): Int`
  - `@Composable fun rememberAssetPainter(assetPath: String, maxSize: Dp = 160.dp): Painter`
  - `object PlayItMotion { const val MICRO_MS = 200; const val STANDARD_MS = 350; const val CELEBRATION_MS = 900; const val SCREEN_MS = 250; fun <T> tap(reduced: Boolean): AnimationSpec<T>; fun <T> settle(reduced: Boolean): AnimationSpec<T> }`

- [ ] **Step 1: Write the failing tests**

```kotlin
// app/src/test/java/com/playit/app/presentation/components/AssetImageTest.kt
package com.playit.app.presentation.components

import org.junit.Assert.assertEquals
import org.junit.Test

class AssetImageTest {
    @Test fun inSampleSize_512to120_is4() = assertEquals(4, calculateInSampleSize(512, 512, 120, 120))
    @Test fun inSampleSize_512to200_is2() = assertEquals(2, calculateInSampleSize(512, 512, 200, 200))
    @Test fun inSampleSize_512to512_is1() = assertEquals(1, calculateInSampleSize(512, 512, 512, 512))
    @Test fun inSampleSize_neverZero() = assertEquals(1, calculateInSampleSize(100, 100, 0, 0))
}
```

```kotlin
// app/src/test/java/com/playit/app/PerformancePolicyTest.kt
package com.playit.app

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PerformancePolicyTest {
    private val main = if (File("src/main").exists()) File("src/main") else File("app/src/main")
    private fun kotlinSources() = File(main, "java").walkTopDown().filter { it.extension == "kt" }

    @Test fun noHapticFeedbackCalls() {
        val hits = kotlinSources().filter { it.readText().contains("performHapticFeedback") }.map { it.name }.toList()
        assertTrue("Haptics are removed (user decision 2026-10-06): $hits", hits.isEmpty())
    }

    @Test fun activityIsPortraitOnly() {
        val manifest = File(main, "AndroidManifest.xml").readText()
        assertTrue(manifest.contains("android:screenOrientation=\"portrait\""))
    }

    @Test fun noIdleFloatingRequestedByCallers() {
        // Dry run 2026-10-06: four callers passed isIdleFloating = true explicitly, so changing the default alone kept them floating.
        val hits = kotlinSources().filter { Regex("isIdleFloating\\s*=\\s*(true|!)").containsMatchIn(it.readText()) }.map { it.name }.toList()
        assertTrue("Idle floating is removed (effects on meaningful moments only): $hits", hits.isEmpty())
    }

    @Test fun noEndlessIdleAnimationOnLetterCardOrMascotHeader() {
        val card = File(main, "java/com/playit/app/presentation/components/LetterCard.kt").readText()
        val header = File(main, "java/com/playit/app/presentation/components/MascotSpeechHeader.kt").readText()
        assertFalse(card.contains("breathingPulse("))
        assertFalse(header.contains("rememberInfiniteTransition("))   // the call, not a leftover import
    }
}
```

- [ ] **Step 2: Run the tests and see them fail**

Run: `./gradlew testDebugUnitTest --tests '*AssetImageTest' --tests '*PerformancePolicyTest'`
Expected: FAIL. The first fails to compile (`calculateInSampleSize` unresolved); the policy tests fail on the haptic call and the missing portrait lock.

- [ ] **Step 3: Implement**

`AssetImage.kt`. It decodes off the main thread at roughly display size and caches by path plus size:
```kotlin
package com.playit.app.presentation.components

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Largest power of two that keeps the decoded image at least as big as the requested size. */
fun calculateInSampleSize(srcWidth: Int, srcHeight: Int, reqWidth: Int, reqHeight: Int): Int {
    if (reqWidth <= 0 || reqHeight <= 0) return 1
    var sample = 1
    while (srcWidth / (sample * 2) >= reqWidth && srcHeight / (sample * 2) >= reqHeight) sample *= 2
    return sample
}

private val transparent = ColorPainter(Color.Transparent)

/** Counts decodes in flight, so screenshot tests can wait until every picture on screen has loaded. */
object AssetDecodeTracker {
    private val pending = java.util.concurrent.atomic.AtomicInteger(0)
    fun isIdle(): Boolean = pending.get() == 0
    internal fun start() { pending.incrementAndGet() }
    internal fun done() { pending.decrementAndGet() }
}

/**
 * Loads an asset PNG at about [maxSize] (in dp) on a background thread. Shows nothing until
 * it is ready, and nothing if the file is missing (a missing asset never crashes a screen).
 */
@Composable
fun rememberAssetPainter(assetPath: String, maxSize: Dp = 160.dp): Painter {
    val context = LocalContext.current
    val reqPx = with(LocalDensity.current) { maxSize.roundToPx() }
    val key = "$assetPath@$reqPx"
    val painter by produceState<Painter>(
        initialValue = AssetBitmapCache.get(key)?.let { BitmapPainter(it.asImageBitmap()) } ?: transparent,
        key1 = key
    ) {
        if (value !== transparent) return@produceState
        AssetDecodeTracker.start()
        try {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.assets.open(assetPath).use { BitmapFactory.decodeStream(it, null, bounds) }
                val opts = BitmapFactory.Options().apply {
                    inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, reqPx, reqPx)
                }
                context.assets.open(assetPath).use { BitmapFactory.decodeStream(it, null, opts) }
            }.getOrNull()?.let { bmp ->
                AssetBitmapCache.put(key, bmp)
                BitmapPainter(bmp.asImageBitmap())
            } ?: transparent
        }
        } finally {
            AssetDecodeTracker.done()   // after the value is set, so a waiting test sees the picture
        }
    }
    return painter
}
```
In `AssetUtils.kt`:
- Delete the old `rememberAssetPainter(assetPath: String)`.
- Keep `AssetBitmapCache` and `MascotState`.
- Existing callers compile unchanged, because `maxSize` has a default.
- Pass `maxSize` where the image is larger than 160 dp: the letter card picture (`200.dp`) and the splash (`320.dp`).

`PlayItMotion.kt` (it replaces the unused `Motion.kt`):
```kotlin
package com.playit.app.presentation.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring

object PlayItMotion {
    const val MICRO_MS = 200
    const val STANDARD_MS = 350
    const val CELEBRATION_MS = 900
    const val SCREEN_MS = 250

    /** Tap press and release (21_ANIMATION_GUIDE: 100 -> 92 -> 100 %, MediumBouncy). */
    fun <T> tap(reduced: Boolean): AnimationSpec<T> =
        if (reduced) snap() else spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)

    /** Critically damped settle, no overshoot (colours, opacity, screen moves). */
    fun <T> settle(reduced: Boolean): AnimationSpec<T> =
        if (reduced) snap() else spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMediumLow)
}
```

`PulseModifier.kt`: create the infinite transition **only** when it will be used:
```kotlin
fun Modifier.breathingPulse(enabled: Boolean = true): Modifier = composed {
    if (!enabled || LocalReducedMotion.current) return@composed this
    val t = rememberInfiniteTransition(label = "pulseTransition")
    val scale by t.animateFloat(1.0f, 1.05f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "pulseScale")
    this.graphicsLayer { scaleX = scale; scaleY = scale }
}

fun Modifier.idleBounce(enabled: Boolean = true): Modifier = composed {
    if (!enabled || LocalReducedMotion.current) return@composed this
    val t = rememberInfiniteTransition(label = "idleBounceTransition")
    val y by t.animateFloat(0f, -6f, infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "idleBounceY")
    this.graphicsLayer { translationY = y.dp.toPx() }
}
```

`GummyMotionAsset.kt`:
- Change the `isIdleFloating` default to `false`, and delete the explicit `isIdleFloating = true` / `isIdleFloating = !isCorrect` argument in `LetterCard`, `BlendItCard`, `FindItGrid` and `MascotBubble`.
- In `idleFloating()`, return `this` when `!enabled || LocalReducedMotion.current`, *before* `rememberInfiniteTransition`.
- `celebrationWiggle` and `interactiveSquish` stay. Make `celebrationWiggle` skip when reduced motion is on.

Other edits:
- `LetterCard.kt`: delete `.breathingPulse()` from the modifier chain.
- `MascotSpeechHeader.kt`:
  - Delete `infiniteTransition`, `breatheScaleY` and `breatheScaleX`.
  - The `graphicsLayer` keeps only `tapBounceScale` and the amplitude squash (and only the tap bounce under reduced motion).
  - Delete the imports that become unused (`rememberInfiniteTransition`, `infiniteRepeatable`, `RepeatMode`, `animateFloat`, `tween`, `FastOutSlowInEasing` if nothing else uses them).
- `GummyButton.kt`:
  - Delete `val haptic = LocalHapticFeedback.current` and the `haptic.performHapticFeedback(...)` line.
  - Delete the two unused imports.
- `AndroidManifest.xml`: add `android:screenOrientation="portrait"` to the `MainActivity` `<activity>`.
- Delete the 26 duplicate MP3s under `audio/vo/` (root level only, not `audio/vo/tutor/` or `audio/vo/ui/`) and the stray `tts_[...]` file. Before deleting, confirm with `git grep -n "audio/vo/vo_"` that nothing references them; if anything does, stop and write to `QUESTIONS.md`.

- [ ] **Step 4: Run the tests and see them pass**

Run: `./gradlew testDebugUnitTest`
Expected: PASS, including `ZeroEmojiPolicyTest`, `AudioCompletenessCheckTest` and the 5 screenshot tests.

- [ ] **Step 5: Phone check (A21s), recorded in the evidence log**
  - Open the map, then Hear It, Find It, Blend It.
  - Taps never vibrate.
  - The Find It pictures appear without a visible stall.
  - Rotating the phone does not rotate the app.

- [ ] **Step 6: Commit**

```bash
git add -A app/src/main app/src/test docs/engineering-package/13_MASTER_TASKS.md docs/evidence-log.md docs/tasks/card-17-performance-calm-motion.md
git commit -m "perf(ui): downscaled async images, no haptics, no endless idle animation, portrait (NFR-PERF-01)"
```

---

### Task 2 (card 18): Adaptive layout foundation and the device matrix

**Why:**
- `GummyContainer` draws its face with `matchParentSize()`, so content can never size it. Every button and card is stuck at its minimum, and content spills out. Examples: the Blend It card (176 dp); the Find It "Hear" pill, whose `wrapContentWidth()` collapses to 0 width.
- There are no dimension tokens: 164 hard-coded `fontSize` values and fixed card sizes such as `LetterCard` 280x290.
- `MascotSpeechHeader` takes about 260 dp on a 360-wide screen: an 86x98 dp mascot plus 24 sp text in a 132 dp column.

**Files:**
- Modify: `presentation/components/GummyButton.kt` (`GummyContainer` measuring only; `GummyButton` stays the same)
- Create: `presentation/theme/Dimens.kt`
- Modify: `presentation/theme/Theme.kt` (provide `LocalPlayItDimens`)
- Create: `presentation/components/LessonScaffold.kt`
- Modify: `presentation/components/MascotSpeechHeader.kt` (sizes from dimens, `maxLines`, ellipsis)
- Modify (only `.height(N.dp)` -> `.heightIn(min = N.dp)` on a `GummyButton` or `GummyContainer`): `presentation/hearit/HearItScreen.kt`, `presentation/blendit/BlendItScreen.kt`, `presentation/blendit/BlendItCompleteScreen.kt`, `presentation/lettercomplete/LetterCompleteScreen.kt`, `presentation/findit/FindItScreen.kt`, `presentation/sayit/SayItScreen.kt`, `presentation/splash/SplashScreen.kt`, `presentation/components/FindItGrid.kt`, `presentation/components/LetterCard.kt`, `presentation/profile/ProfileSelectScreen.kt`, `presentation/profile/components/AddProfileButton.kt`, `presentation/profile/components/ProfileCard.kt`, `presentation/map/components/NodeActionPopupDialog.kt`, `presentation/map/components/UnitGuidebookDialog.kt`, `presentation/dashboard/ParentDashboardScreen.kt`, `presentation/dashboard/components/ArithmeticGuardDialog.kt`, `presentation/dashboard/components/ProfileSwitcherDropdown.kt`. Leave a fixed height that is not on a gummy component alone. If another file needs the change, name it in the commit body.
- Create: `app/src/test/java/com/playit/app/screenshot/Devices.kt` (qualifier constants + `WithFontScale`)
- Test: `app/src/test/java/com/playit/app/presentation/theme/DimensTest.kt`
- Test: `app/src/test/java/com/playit/app/presentation/components/GummyContainerLayoutTest.kt`
- Test: `app/src/test/java/com/playit/app/screenshot/LayoutMatrixTest.kt`

**Interfaces:**
- Produces:
  - `enum class WindowProfile { COMPACT, REGULAR, WIDE }`
  - `fun windowProfileFor(widthDp: Int, heightDp: Int): WindowProfile`
  - `data class PlayItDimens(...)` (fields below)
  - `fun dimensFor(profile: WindowProfile): PlayItDimens`
  - `val LocalPlayItDimens: ProvidableCompositionLocal<PlayItDimens>`
  - `@Composable fun LessonScaffold(topBar: @Composable () -> Unit, header: @Composable () -> Unit, bottomBar: @Composable () -> Unit, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit)`
  - `object Devices { const val COMPACT; A21S; PHONE; TABLET }` (qualifier strings); `@Composable fun WithFontScale(fontScale: Float, content: @Composable () -> Unit)`
  - `abstract class LayoutMatrixTest(deviceName: String)` with `assertOnScreen(tag)`, `capture(name)`, `assumeFontScaleChecks()`; subclasses `LayoutMatrixCompactTest`, `LayoutMatrixA21sTest`, `LayoutMatrixPhoneTest`, `LayoutMatrixTabletTest`

- [ ] **Step 1: Write the failing tests**

```kotlin
// DimensTest.kt
package com.playit.app.presentation.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DimensTest {
    @Test fun compactUnder700dpTall() = assertEquals(WindowProfile.COMPACT, windowProfileFor(360, 640))
    @Test fun a21sIsRegular() = assertEquals(WindowProfile.REGULAR, windowProfileFor(360, 740))
    @Test fun tabletIsWide() = assertEquals(WindowProfile.WIDE, windowProfileFor(800, 1280))
    @Test fun childTargetsNeverBelow64dp() {
        WindowProfile.values().map(::dimensFor).forEach {
            assertTrue(it.tileSize.value >= 64f && it.micSize.value >= 64f && it.primaryCta.value >= 64f)
        }
    }
    @Test fun textNeverBelow16sp() {
        WindowProfile.values().map(::dimensFor).forEach { assertTrue(it.bubbleTextSp >= 16) }
    }
}
```

```kotlin
// GummyContainerLayoutTest.kt: Robolectric, same runner annotations as card 10's screenshot tests (complete file, compiled in the dry run)
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
```

```kotlin
// screenshot/Devices.kt
package com.playit.app.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

/** Qualifiers for the 4 sizes; each LayoutMatrix subclass puts one of these in its @Config. */
object Devices {
    const val COMPACT = "w360dp-h640dp-xhdpi"
    const val A21S = "w360dp-h740dp-xhdpi"
    const val PHONE = "w411dp-h891dp-xxhdpi"
    const val TABLET = "w800dp-h1280dp-mdpi"
}

/** Renders [content] as if the system font size were [fontScale] (Samsung "Large" is about 1.3). */
@Composable
fun WithFontScale(fontScale: Float, content: @Composable () -> Unit) {
    val d = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(d.density, fontScale)) { content() }
}
```

```kotlin
// LayoutMatrixTest.kt: shared checks; each subclass fixes the screen size with @Config (complete file, compiled in the dry run)
package com.playit.app.screenshot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.playit.app.presentation.components.AssetImageConfig
import com.playit.app.presentation.components.LessonScaffold
import com.playit.app.presentation.components.MascotSpeechHeader
import com.playit.app.presentation.theme.PlayItTheme
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

// (the size must be set before the test activity starts, so it can't be a runtime parameter).
@GraphicsMode(GraphicsMode.Mode.NATIVE)
abstract class LayoutMatrixTest(private val deviceName: String) {
    @get:Rule val compose = createComposeRule()

    /** Pictures decode on the first frame in tests, so captures always include them (card 17c). */
    @Before fun syncImages() { AssetImageConfig.decodeSynchronously = true }

    /** Fails if the node's bottom is below the window (the child would have to scroll to reach it). */
    protected fun assertOnScreen(tag: String) {
        val node = compose.onNodeWithTag(tag, useUnmergedTree = true).getBoundsInRoot()
        val root = compose.onRoot().getBoundsInRoot()
        assertTrue("$tag bottom ${node.bottom} > window ${root.bottom} on $deviceName", node.bottom <= root.bottom)
    }

    protected fun capture(name: String) {
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/outputs/roborazzi/${name}_$deviceName.png")
    }

    /** Font-scale checks run on every size except compact (360x640 at 1.3 is below our support floor). */
    protected fun assumeFontScaleChecks() = assumeTrue(deviceName != "compact")

    @Composable
    private fun scaffoldUnderTest() = LessonScaffold(
        topBar = { Box(Modifier.height(64.dp)) },
        header = { MascotSpeechHeader(message = "Listen closely to the sound of the letter, then tap play.") },
        bottomBar = { Box(Modifier.fillMaxWidth().height(64.dp).testTag("bottom")) },
    ) { Box(Modifier.size(120.dp).testTag("main")) }

    @Test fun lessonScaffold_bottomBarAndBodyVisible() {
        compose.setContent { PlayItTheme { scaffoldUnderTest() } }
        assertOnScreen("main"); assertOnScreen("bottom"); capture("scaffold")
    }

    @Test fun fontScale13_mainActionVisible() {
        assumeFontScaleChecks()
        compose.setContent { PlayItTheme { WithFontScale(1.3f) { scaffoldUnderTest() } } }
        assertOnScreen("main"); assertOnScreen("bottom")
    }

    @Test fun tablet_contentWidthCapped() {
        compose.setContent {
            PlayItTheme {
                LessonScaffold(topBar = {}, header = {}, bottomBar = {}) {
                    Box(Modifier.fillMaxWidth().height(10.dp).testTag("wide"))
                }
            }
        }
        val w = compose.onNodeWithTag("wide").getBoundsInRoot().let { it.right - it.left }
        assertTrue(w <= 560.dp)
    }
}

@RunWith(RobolectricTestRunner::class) @Config(sdk = [34], qualifiers = Devices.COMPACT)
class LayoutMatrixCompactTest : LayoutMatrixTest("compact")
@RunWith(RobolectricTestRunner::class) @Config(sdk = [34], qualifiers = Devices.A21S)
class LayoutMatrixA21sTest : LayoutMatrixTest("a21s")
@RunWith(RobolectricTestRunner::class) @Config(sdk = [34], qualifiers = Devices.PHONE)
class LayoutMatrixPhoneTest : LayoutMatrixTest("phone")
@RunWith(RobolectricTestRunner::class) @Config(sdk = [34], qualifiers = Devices.TABLET)
class LayoutMatrixTabletTest : LayoutMatrixTest("tablet")
```
Tasks 3-6 add their screen checks as new `@Test` methods in the abstract `LayoutMatrixTest`, so every size runs them.

- [ ] **Step 2: Run the tests and see them fail**

Run: `./gradlew testDebugUnitTest --tests '*DimensTest' --tests '*GummyContainerLayoutTest' --tests '*LayoutMatrix*'`
Expected: compile errors (`windowProfileFor`, `LessonScaffold` and `Devices` don't exist). After they exist, `contentTallerThanMin_growsContainer` and `wrapContentWidth_isNotZero` still fail against today's `GummyContainer`.

- [ ] **Step 3: Implement**

`Dimens.kt`:
```kotlin
package com.playit.app.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowProfile { COMPACT, REGULAR, WIDE }

fun windowProfileFor(widthDp: Int, heightDp: Int): WindowProfile = when {
    widthDp >= 600 -> WindowProfile.WIDE
    heightDp < 700 -> WindowProfile.COMPACT
    else -> WindowProfile.REGULAR
}

data class PlayItDimens(
    val profile: WindowProfile,
    val screenPadding: Dp,
    val contentMaxWidth: Dp,
    val mascotHeader: Dp,      // Lily in lesson headers
    val bubbleTextSp: Int,     // lesson instruction bubble (spoken aloud too)
    val bubbleMaxLines: Int,
    val letterCardHeight: Dp,
    val primaryCta: Dp,        // Hear It play button
    val micSize: Dp,
    val findItCardMinHeight: Dp,
    val findItCardAspect: Float, // width / height; flatter cards on short screens so 3 rows fit
    val tileSize: Dp,          // Blend It slots and tiles
    val mapNodeSize: Dp,
    val completeMascot: Dp,
)

fun dimensFor(profile: WindowProfile): PlayItDimens = when (profile) {
    WindowProfile.COMPACT -> PlayItDimens(profile, 16.dp, 560.dp, 64.dp, 18, 3, 210.dp, 76.dp, 96.dp, 96.dp, 1.5f, 64.dp, 68.dp, 112.dp)
    WindowProfile.REGULAR -> PlayItDimens(profile, 20.dp, 560.dp, 80.dp, 20, 4, 250.dp, 88.dp, 112.dp, 112.dp, 1.2f, 68.dp, 76.dp, 140.dp)
    WindowProfile.WIDE -> PlayItDimens(profile, 32.dp, 560.dp, 96.dp, 22, 4, 300.dp, 96.dp, 128.dp, 132.dp, 1.2f, 76.dp, 88.dp, 160.dp)
}

val LocalPlayItDimens = staticCompositionLocalOf { dimensFor(WindowProfile.REGULAR) }
```

`Theme.kt`, inside `PlayItTheme`:
- Read `val cfg = LocalConfiguration.current`.
- Provide `LocalPlayItDimens provides dimensFor(windowProfileFor(cfg.screenWidthDp, cfg.screenHeightDp))` next to `LocalReducedMotion`.

`GummyContainer` keeps the same signature and visuals but changes how it measures. Use a custom `Layout`:
1. Ask the face for its content's natural (intrinsic) size.
2. Clamp it to the caller's limits.
3. Measure the depth band and the face at exactly that size.

Big content then grows the container instead of spilling out, and content that uses `fillMaxSize` still fills only the container.

**Don't use `propagateMinConstraints` for this.** Claude's dry run (2026-10-06) found that it made fill-content buttons, such as "Let's Play", grow to the whole free height. `Modifier.height(IntrinsicSize.Min)` doesn't work either: a Box whose children all use `matchParentSize` reports a natural size of 0.

Replace the outer `Box(modifier = modifier.graphicsLayer { ... }.then(clickableModifier)) { ... }` of `GummyContainer` with this. Add `import androidx.compose.ui.layout.Layout` and `import androidx.compose.ui.unit.Constraints`:
```kotlin
    // Custom layout: the container is as big as its content's natural size, clamped to the caller's
    // limits (size/heightIn/fillMaxWidth). Big content grows it instead of spilling out, and content
    // that uses fillMaxSize still fills only the container (Claude dry run 2026-10-06).
    Layout(
        modifier = modifier
            .graphicsLayer {
                scaleX = squashScaleX
                scaleY = squashScaleY
            }
            .then(clickableModifier),
        content = {
            // Bottom depth band layer (shadow color)
            Box(
                modifier = Modifier
                    .offset(y = depthHeight)
                    .background(effectiveShadow, shape)
                    .border(strokeWidth, strokeColor, shape)
            )
            // Top face layer (face color + content) translated down on press
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, pressOffsetY.dp.roundToPx()) }
                    .background(effectiveFace, shape)
                    .border(strokeWidth, strokeColor, shape),
                contentAlignment = Alignment.Center
            ) {
                // Modern subtle top gloss highlight sheen
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(shape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.22f),
                                    Color.White.copy(alpha = 0.04f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                content()
            }
        }
    ) { measurables, constraints ->
        val band = measurables[0]
        val face = measurables[1]
        val width = if (constraints.hasFixedWidth) constraints.maxWidth
            else face.maxIntrinsicWidth(constraints.maxHeight).coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = if (constraints.hasFixedHeight) constraints.maxHeight
            else face.minIntrinsicHeight(width).coerceIn(constraints.minHeight, constraints.maxHeight)
        val exact = Constraints.fixed(width, height)
        val bandPlaceable = band.measure(exact)
        val facePlaceable = face.measure(exact)
        layout(width, height) {
            bandPlaceable.place(0, 0)
            facePlaceable.place(0, 0)
        }
    }
```
This code passed all 260 tests and the 5 screenshot screens in the dry run.

Change every caller's fixed `.height(N.dp)` on a gummy component to `.heightIn(min = N.dp)` (the Files list says how to find them).

`LessonScaffold.kt` fills the screen when the content fits, scrolls when it doesn't, caps the width and pins the bottom bar:
```kotlin
package com.playit.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.playit.app.presentation.theme.LocalPlayItDimens

@Composable
fun LessonScaffold(
    topBar: @Composable () -> Unit,
    header: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val d = LocalPlayItDimens.current
    Column(modifier.fillMaxSize().statusBarsPadding()) {
        topBar()
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Box(Modifier.widthIn(max = d.contentMaxWidth)) { header() }
        }
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .widthIn(max = d.contentMaxWidth)
                    .fillMaxWidth()
                    .heightIn(min = maxHeight)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = d.screenPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                content = content
            )
        }
        Box(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = d.screenPadding, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) { Box(Modifier.widthIn(max = d.contentMaxWidth)) { bottomBar() } }
    }
}
```
(Add `import androidx.compose.ui.unit.dp`.)

`MascotSpeechHeader`, the only changes:
- The mascot `Box` size becomes `Modifier.size(d.mascotHeader)`, where `val d = LocalPlayItDimens.current`.
- The text gets `fontSize = d.bubbleTextSp.sp`, `lineHeight = (d.bubbleTextSp + 6).sp`, `maxLines = d.bubbleMaxLines` and `overflow = TextOverflow.Ellipsis`.
- Pass `maxSize = d.mascotHeader` to `rememberAssetPainter`.
- The full message is always spoken by the mascot audio, so ellipsis never hides an instruction from a non-reader.

- [ ] **Step 4: Run the tests and see them pass**

Run: `./gradlew testDebugUnitTest`
Expected: PASS. Look at the `scaffold_<device>.png` images in `app/build/outputs/roborazzi/` after `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`.

- [ ] **Step 5: Commit**

```bash
git commit -m "feat(ui): adaptive dimensions, LessonScaffold, GummyContainer grows with content (NFR-ACC-02)"
```

---

### Task 3 (card 19): Hear It and Say It fit, plus the Say It mic states (FR-03)

**Why:**
- On 360x640 the Hear It play button is below the fold (content about 830 dp).
- The Say It mic is below the fold (content about 1000 dp; mic ring 180 dp).
- The Say It feedback banner sits below the fold.
- The legacy letter card at 116 dp overflows.
- The listening mic is red (`CoralBerry`, `SayItScreen.kt:322,331`), against 03 :24.
- There is no Processing or Heard state, and children hesitated at the mic in Round 1 (F-02).

**Files:**
- Modify: `presentation/hearit/HearItScreen.kt` (use `LessonScaffold`; letter card height from dimens; play button `d.primaryCta`; `testTag("hearit_play")`)
- Modify: `presentation/components/LetterCard.kt` (`fillMaxWidth()` + `heightIn(max = d.letterCardHeight)` + `aspectRatio(0.97f, matchHeightConstraintsFirst = true)` instead of 280x290; picture `maxSize = 200.dp`; letter text `maxLines = 1`)
- Modify: `presentation/sayit/SayItScreen.kt` (use `LessonScaffold`; mic from `MicButton`; feedback banner in the scaffold's bottom bar above the Next button; `testTag("sayit_mic")`)
- Modify: `presentation/sayit/SayItViewModel.kt` (`micStatus` flow; reset on screen hidden)
- Create: `presentation/sayit/MicStatus.kt`, `presentation/sayit/components/MicButton.kt`
- Test: `presentation/sayit/MicStatusTest.kt`, additions to `presentation/sayit/SayItViewModelTest.kt`, additions to `screenshot/LayoutMatrixTest.kt`

**Interfaces:**
- Consumes: `LessonScaffold`, `LocalPlayItDimens`, `PlayItMotion` (Tasks 1-2).
- Produces:
  - `enum class MicStatus { IDLE, LISTENING, HEARD, RESULT_CORRECT, RESULT_TRY_AGAIN }`
  - `fun micStatusFor(state: SayItState, heardSpeech: Boolean): MicStatus`
  - `SayItViewModel.micStatus: StateFlow<MicStatus>`
  - `fun SayItViewModel.onScreenHidden()`

- [ ] **Step 1: Write the failing tests**

```kotlin
// MicStatusTest.kt
class MicStatusTest {
    @Test fun idle() = assertEquals(MicStatus.IDLE, micStatusFor(SayItState.Idle, heardSpeech = false))
    @Test fun listeningSilent() = assertEquals(MicStatus.LISTENING, micStatusFor(SayItState.Listening, false))
    @Test fun listeningHeard() = assertEquals(MicStatus.HEARD, micStatusFor(SayItState.Listening, true))
    @Test fun correct() = assertEquals(MicStatus.RESULT_CORRECT, micStatusFor(SayItState.Correct("mouse"), false))
    @Test fun incorrect() = assertEquals(MicStatus.RESULT_TRY_AGAIN, micStatusFor(SayItState.Incorrect("em"), true))
}
```

Additions to `SayItViewModelTest`, using the existing setup and its `voskRecognizer` mock. Capture the `onResult` lambda with a `slot<(String, Boolean) -> Unit>()` as the existing listening tests do:
```kotlin
@Test fun partialSpeech_setsHeard() = runTest {
    val cb = slot<(String, Boolean) -> Unit>()
    every { voskRecognizer.startListening(capture(cb)) } just Runs
    viewModel.startListening()
    cb.captured("ma", false)          // wrong partial: keeps listening, but speech was heard
    assertEquals(MicStatus.HEARD, viewModel.micStatus.value)
}

@Test fun recognizerStoppedExternally_returnsToIdle() = runTest {
    every { voskRecognizer.startListening(any()) } just Runs
    viewModel.startListening()
    viewModel.onScreenHidden()        // MainActivity.onStop stopped Vosk while listening
    assertEquals(MicStatus.IDLE, viewModel.micStatus.value)
}
```

Additions to `LayoutMatrixTest`: build `HearItScreen` and `SayItScreen` exactly as the card 10 screenshot tests do, then:
```kotlin
@Composable private fun hearIt() { /* build HearItViewModel exactly as HearItScreenshotTest; */ HearItScreen(vm, onNext = {}, onBack = {}) }
@Composable private fun sayIt() { /* build SayItViewModel with mocks as SayItViewModelTest; */ SayItScreen(vm, onNext = {}, onBack = {}) }

@Test fun hearIt_playVisible() {
    compose.setContent { PlayItTheme { hearIt() } }
    assertOnScreen("hearit_play"); capture("hearit")
}
@Test fun sayIt_micVisible() {
    compose.setContent { PlayItTheme { sayIt() } }
    assertOnScreen("sayit_mic"); capture("sayit")
}
@Test fun sayIt_micVisible_fontScale13() {
    assumeFontScaleChecks()
    compose.setContent { PlayItTheme { WithFontScale(1.3f) { sayIt() } } }
    assertOnScreen("sayit_mic")
}
```

- [ ] **Step 2: Run the tests and see them fail**

Run: `./gradlew testDebugUnitTest --tests '*MicStatusTest' --tests '*SayItViewModelTest' --tests '*LayoutMatrixTest'`
Expected: compile errors, then FAIL on `hearIt_playVisible` and `sayIt_micVisible` for `compact` (content below the fold).

- [ ] **Step 3: Implement**

```kotlin
// presentation/sayit/MicStatus.kt
package com.playit.app.presentation.sayit

enum class MicStatus { IDLE, LISTENING, HEARD, RESULT_CORRECT, RESULT_TRY_AGAIN }

fun micStatusFor(state: SayItState, heardSpeech: Boolean): MicStatus = when (state) {
    SayItState.Idle -> MicStatus.IDLE
    SayItState.Listening -> if (heardSpeech) MicStatus.HEARD else MicStatus.LISTENING
    is SayItState.Correct -> MicStatus.RESULT_CORRECT
    is SayItState.Incorrect -> MicStatus.RESULT_TRY_AGAIN
}
```

`SayItViewModel`:
- Add `private val _heardSpeech = MutableStateFlow(false)`, and expose `val micStatus = combine(_state, _heardSpeech) { s, h -> micStatusFor(s, h) }.stateIn(viewModelScope, SharingStarted.Eagerly, MicStatus.IDLE)`.
- In `startListening()`, set `_heardSpeech.value = false` before listening.
- In the `onResult` lambda, set `_heardSpeech.value = true` on any non-blank transcript, *before* judging.
- Add:
```kotlin
fun onScreenHidden() {
    autoStopJob?.cancel()
    if (_state.value is SayItState.Listening) {
        voskRecognizer.stopListening()
        _state.value = SayItState.Idle
    }
    _heardSpeech.value = false
}
```
- Call `viewModel.onScreenHidden()` from the existing `DisposableEffect`/`ON_STOP` handling in `SayItScreen` (card 07 added the screen-visibility hooks; reuse them).

`MicButton.kt` (no red; colour plus shape plus label, per 03 :54):

| Status | Look | Label (20 sp, 1 line) |
|---|---|---|
| IDLE | teal `EmeraldLeaf` face, mic icon, static | "Tap and say it" |
| LISTENING | amber `SunnyGold` face, one expanding ring (1200 ms loop; under reduced motion a static 4 dp ring instead) | "I'm listening..." |
| HEARD | lavender `PrimaryJoyLight` face, 3 bouncing dots (static dots under reduced motion) | "I hear you!" |
| RESULT_CORRECT | green face, check icon, correct pop (100 -> 108 -> 100 %) | "Yes!" |
| RESULT_TRY_AGAIN | `GentleCorrectionOrange` face, ear icon, gentle shake (none under reduced motion) | "Let's try again" |

Size `d.micSize` (at least 64 dp), `testTag("sayit_mic")`, `contentDescription` = the label. Clicks call `onTap` only in IDLE and RESULT_TRY_AGAIN.

`HearItScreen` and `SayItScreen` move into `LessonScaffold`:
- `topBar = { LessonTopBar(...) }`, `header = { MascotSpeechHeader(...) }`.
- The body holds the letter card and the play button or mic.
- `bottomBar` holds the feedback banner (Say It) and the Next button.
- Remove the screens' own `verticalScroll` Column and fixed spacers. The scaffold's `SpaceEvenly` spaces the body.

- [ ] **Step 4: Run the tests and see them pass**

Run: `./gradlew testDebugUnitTest` (all must pass), then `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`, and look at `hearit_*` and `sayit_*` for all 4 devices.

- [ ] **Step 5: Commit**

```bash
git commit -m "feat(sayit): Hear It and Say It fit every phone; mic shows listening, heard and result (FR-03)"
```

---

### Task 4 (card 20): Find It and Blend It fit (supersedes card 16)

**Why:**
- On 360x640 the Find It header takes about 320 dp, so only one row of the grid shows.
- The Find It cards are a fixed 124 dp.
- The found/hear row overflows at font scale 1.3.
- The Blend It card clips its second line (card 16's finding).
- The Blend It tiles are a fixed 68 dp in a non-wrapping Row.

**Files:**
- Modify: `presentation/findit/FindItScreen.kt` (`LessonScaffold`; found/hear row `FlowRow` or two weighted cells; grid in the body; `testTag("findit_grid")`)
- Modify: `presentation/components/FindItGrid.kt` (card height `heightIn(min = d.findItCardMinHeight)` + `aspectRatio(d.findItCardAspect)` within a 2-column grid; word text `maxLines = 1`; picture `maxSize = 120.dp`)
- Modify: `presentation/blendit/BlendItScreen.kt` (`LessonScaffold`; slots and tiles `d.tileSize` in a `FlowRow` with 8 dp spacing; `testTag("blendit_tiles")`)
- Modify: `presentation/components/BlendItCard.kt` (picture box `80.dp`, gold circle `72.dp`, image `76.dp`, spacer `4.dp`; card 16's exact fix)
- Test: `presentation/components/BlendItCardLayoutTest.kt` (card 16's test, unchanged)
- Test: additions to `screenshot/LayoutMatrixTest.kt`
- Bookkeeping: set `docs/tasks/card-16-blendit-card-fit.md` to `Status: superseded by card 20`

**Interfaces:**
- Consumes: `LessonScaffold`, `LocalPlayItDimens` (Task 2).

- [ ] **Step 1: Write the failing tests**

`BlendItCardLayoutTest.secondLine_staysInsideCard`, exactly as in `docs/tasks/card-16-blendit-card-fit.md`. Then, in `LayoutMatrixTest`:
```kotlin
@Composable private fun findIt() { /* build FindItViewModel exactly as FindItScreenshotTest */ FindItScreen(vm, onNext = { _, _ -> }, onBack = {}) }
@Composable private fun blendIt() { /* build BlendItViewModel exactly as BlendItScreenshotTest */ BlendItScreen(vm, onSessionComplete = {}, onBack = {}) }

@Test fun findIt_wholeGridVisible() {
    compose.setContent { PlayItTheme { findIt() } }
    assertOnScreen("findit_grid"); capture("findit")
}
@Test fun findIt_fontScale13() {
    assumeFontScaleChecks()
    compose.setContent { PlayItTheme { WithFontScale(1.3f) { findIt() } } }
    assertOnScreen("findit_grid")
}
@Test fun blendIt_tilesVisible() {
    compose.setContent { PlayItTheme { blendIt() } }
    assertOnScreen("blendit_tiles"); capture("blendit")
}
```

- [ ] **Step 2: Run them and see them fail** (`compact`: the grid is below the fold; the card's second line is clipped)

- [ ] **Step 3: Implement**

Use the `LessonScaffold` pattern from Task 3. The grid's 5 cards in 2 columns:
```kotlin
@Composable
fun FindItGrid(items: List<FindItPictureItem>, /* existing params */ modifier: Modifier = Modifier) {
    val d = LocalPlayItDimens.current
    Column(modifier.testTag("findit_grid"), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { item ->
                    FindItCard(item, /* existing params */ modifier = Modifier.weight(1f).heightIn(min = d.findItCardMinHeight).aspectRatio(d.findItCardAspect))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
```
Keep the existing correct, wrong and celebration visuals of `FindItCard`. Only its size modifiers change.

- [ ] **Step 4: Run all the tests and look at the screenshots**

- [ ] **Step 5: Commit** `fix(ui): Find It and Blend It fit every phone; Blend It card fits its text (FR-05, FR-13)`

---

### Task 5 (card 21): Complete screens, splash, profile screens, typography pass

**Why:**
- LetterComplete and BlendItComplete don't scroll, and their fixed content can push the button off.
- Splash uses a `.height(420.dp)` dome.
- The profile and name screens use fixed 90 dp headers.
- Child text is hard-coded at 164 sites. This task routes the child screens' text through `MaterialTheme.typography` with `maxLines`.

**Files:**
- Modify: `presentation/lettercomplete/LetterCompleteScreen.kt`, `presentation/blendit/BlendItCompleteScreen.kt` (body in a fit-or-scroll Column as in `LessonScaffold`; mascot `d.completeMascot`; button pinned; `testTag("complete_continue")`)
- Modify: `presentation/splash/SplashScreen.kt` (dome `fillMaxWidth().aspectRatio(0.9f).heightIn(max = 420.dp)`; Start button `testTag("splash_start")`)
- Modify: `presentation/profile/ProfileSelectScreen.kt`, `presentation/profile/NamePromptScreen.kt` (headers `heightIn(min = 72.dp)`; avatar grid adaptive)
- Modify: `presentation/theme/Type.kt` (child styles per 10_UI_IMPLEMENTATION_GUIDE :20-24: displayLarge 40, headlineLarge 28, titleMedium 22, bodyLarge 24, labelLarge 18; Lexend)
- Test: additions to `screenshot/LayoutMatrixTest.kt` (`complete_continueVisible`, `splash_startVisible`, `nameprompt_letsPlayVisible`, each with a font-scale 1.3 variant)

- [ ] **Step 1: Write the failing layout tests** in the abstract `LayoutMatrixTest`, following the Task 3 pattern:
  - `complete_continueVisible` (`assertOnScreen("complete_continue"); capture("complete")`) and `complete_continueVisible_fontScale13` (`assumeFontScaleChecks()` + `WithFontScale(1.3f)`);
  - `splash_startVisible` and `splash_startVisible_fontScale13`;
  - `nameprompt_letsPlayVisible` and `nameprompt_letsPlayVisible_fontScale13` (tag the Let's Play button `nameprompt_play`).
- [ ] **Step 2: Run them and see them fail** (`compact`: the Continue button is below the fold; `a21s` at font scale 1.3: clipped titles)
- [ ] **Step 3: Implement.**
  - Replace fixed heights with `heightIn(min = ...)` and weights.
  - Replace `fontSize = N.sp` on these screens with `style = MaterialTheme.typography.<role>` and `maxLines`.
  - Letters and words stay at least 24 sp.
- [ ] **Step 4: Run all the tests and look at the screenshots**
- [ ] **Step 5: Commit** `feat(ui): complete, splash and profile screens fit every phone; child text styles (NFR-ACC-02)`

---

### Task 6 (card 22): Map overhaul

**Why:**
- The header (stats bar plus a 3-5 line greeting) takes about 230 dp of a 640 dp screen.
- The trail is a faint 4 dp grey dash (`MapPathCanvas.kt:43-83`).
- Node x positions use a fixed 50 dp amplitude, so the path is narrow on tablets.
- 6 companion animals float forever.
- `measuredCenters` is remembered without a key (`MapScreen.kt:287`).
- A long name pushes the stat pills off-screen (`TopStatsBar.kt:109`).
- Coming back from a lesson shows no unlock moment.

**Files:**
- Create: `presentation/map/MapLayout.kt` (pure math)
- Modify: `presentation/map/MapScreen.kt`:
  - the greeting becomes a one-line Lily chip that speaks the full greeting on tap;
  - nodes use `d.mapNodeSize`;
  - x offsets come from `MapLayout`;
  - `measuredCenters` is keyed on `mapNodes`;
  - companions: only the player's avatar beside the current node, static;
  - the current node keeps its pulse ring (decision 4);
  - unlock moment;
  - `testTag("map_current_node")`.
- Modify: `presentation/map/components/MapPathCanvas.kt` (rope trail: 10 dp `Color(0xFFC9A66B)` with a 3 dp `DarkBrownOutline` edge, rounded caps, dash 18/12 dp; completed segments solid `EmeraldLeaf`)
- Modify: `presentation/map/components/ChocolateHillsBackground.kt` (build all paths in `drawWithCache`, so scrolling doesn't rebuild them)
- Modify: `presentation/map/components/TopStatsBar.kt` (name `weight(1f)`, `maxLines = 1`, `TextOverflow.Ellipsis`; `testTag("stats_pills")` on the pill row)
- Modify: `presentation/map/components/MapCompanionFriends.kt` (static avatar beside the current node; no infinite transitions)
- Modify: `presentation/map/MapViewModel.kt` (`newlyUnlockedNodeId: StateFlow<String?>`, set when a node's state goes locked -> unlocked between two loads; `fun onUnlockShown()`)
- Test: `presentation/map/MapLayoutTest.kt`, `presentation/map/components/TopStatsBarLayoutTest.kt`, additions to `presentation/map/MapViewModelTest.kt` and `screenshot/LayoutMatrixTest.kt`

**Interfaces:**
- Produces:
  - `object MapLayout { fun nodeXOffset(index: Int, availableWidth: Dp, nodeSize: Dp): Dp }`
  - `MapViewModel.newlyUnlockedNodeId`
  - `MapViewModel.onUnlockShown()`

- [ ] **Step 1: Write the failing tests**

```kotlin
class MapLayoutTest {
    @Test fun offsetsStayInsideTheWidth() {
        listOf(360.dp, 411.dp, 800.dp).forEach { w ->
            (0 until 40).forEach { i ->
                val x = MapLayout.nodeXOffset(i, w, 76.dp)
                assertTrue(x.value.absoluteValue + 38f <= w.value / 2f - 16f)
            }
        }
    }
    @Test fun widerScreen_widerPath() =
        assertTrue(MapLayout.nodeXOffset(2, 800.dp, 88.dp) > MapLayout.nodeXOffset(2, 360.dp, 76.dp))
}
```
```kotlin
// TopStatsBarLayoutTest (Robolectric, a21s qualifiers)
@Test fun longName_pillsStayVisible() {
    compose.setContent { PlayItTheme { TopStatsBar(/* existing params */ profileName = "Maximilianoooooo") } }
    val pills = compose.onNodeWithTag("stats_pills").getBoundsInRoot()
    assertTrue(pills.right <= compose.onRoot().getBoundsInRoot().right)
}
```
```kotlin
// MapViewModelTest: use the existing node-flow stubs; emit a state where node 2 is locked, then unlocked
@Test fun unlockBetweenLoads_setsNewlyUnlocked() { /* ... */ assertEquals(node2.id.toString(), viewModel.newlyUnlockedNodeId.value) }
@Test fun onUnlockShown_clears() { /* ... */ viewModel.onUnlockShown(); assertNull(viewModel.newlyUnlockedNodeId.value) }
```
Add to the abstract `LayoutMatrixTest`: `map_currentNodeVisible` (build `MapViewModel` with the mocks from `MapViewModelTest`, then `assertOnScreen("map_current_node"); capture("map")`).

- [ ] **Step 2: Run them and see them fail**
- [ ] **Step 3: Implement**

```kotlin
object MapLayout {
    /** Sine trail that uses about 60 % of the free half-width, so it stays clear of the edges on any phone. */
    fun nodeXOffset(index: Int, availableWidth: Dp, nodeSize: Dp): Dp {
        val freeHalf = (availableWidth / 2) - (nodeSize / 2) - 16.dp
        return freeHalf * 0.6f * sin(index * 0.85f)
    }
}
```

The unlock moment runs when `newlyUnlockedNodeId != null`:
1. Scroll to the node.
2. Pop it (scale 0.6 -> 1.0, `PlayItMotion.CELEBRATION_MS`).
3. Play `SfxEvent.NODE_UNLOCK_CHIME`.
4. Show a one-line chip: "New letter open!"
5. Then call `onUnlockShown()`.

Under reduced motion: no pop, just the chip and the chime.

- [ ] **Step 4: Run all the tests and look at `map_*` at 4 sizes**
- [ ] **Step 5: Commit** `feat(map): clear rope trail, compact header, scales to every phone, unlock moment (FR-01)`

---

### Task 7 (card 23): Purposeful effects

**Why:**
- There are no screen transitions (`navigation/NavGraph.kt`).
- Confetti falls from the top instead of bursting at the reward.
- Hearts drop with no feedback.
- Stars appear without the drop-in that makes them feel earned (mockup and 21 :26-28).
- Several effects ignore reduced motion: `shake`, the pulse rings, the BlendIt wobble, the locked-node shake.

**Files:**
- Modify: `navigation/NavGraph.kt` (`NavHost` enter: `fadeIn(tween(SCREEN_MS)) + slideInVertically(tween(SCREEN_MS)) { it / 40 }`; exit: `fadeOut(tween(SCREEN_MS))`; `EnterTransition.None` and `ExitTransition.None` under reduced motion)
- Create: `presentation/components/FeedbackEffects.kt` (`Modifier.correctPop(trigger)`, `Modifier.heartLossWobble(trigger)`, `Modifier.starDrop(index, visible)`)
- Modify: `presentation/components/CelebrationOverlay.kt` (CONFETTI bursts from the centre, at most 24 particles; nothing under reduced motion)
- Modify: `presentation/components/ShakeModifier.kt` (no-op under reduced motion)
- Modify: `presentation/components/LessonTopBar.kt` (hearts use `heartLossWobble` when the count drops)
- Modify: `presentation/components/PediatricComponents.kt` (`StarDisplay` uses `starDrop`)
- Modify: `presentation/map/MapScreen.kt` (locked-node shake honours reduced motion)
- Test: `presentation/components/FeedbackEffectsTest.kt` (pure curves), `presentation/theme/PlayItMotionTest.kt`

- [ ] **Step 1: Write the failing tests**

```kotlin
class PlayItMotionTest {
    @Test fun durationsInsideAnimationGuide() {
        assertTrue(PlayItMotion.MICRO_MS in 150..250)
        assertTrue(PlayItMotion.STANDARD_MS in 300..500)
        assertTrue(PlayItMotion.CELEBRATION_MS in 600..1200)
        assertTrue(PlayItMotion.SCREEN_MS in 200..300)
    }
    @Test fun reducedMotionSnaps() = assertTrue(PlayItMotion.tap<Float>(reduced = true) is SnapSpec)
}
```
```kotlin
// FeedbackEffectsTest: the effect curves are pure functions, and the modifiers only apply them
class FeedbackEffectsTest {
    @Test fun heartWobble_reducedMotionIsStill() =
        (0L..600L step 50).forEach { assertEquals(0f, heartWobbleAngle(it, reduced = true), 0f) }
    @Test fun heartWobble_movesThenSettles() {
        assertTrue(heartWobbleAngle(100, reduced = false).absoluteValue > 1f)
        assertEquals(0f, heartWobbleAngle(PlayItMotion.STANDARD_MS.toLong(), reduced = false), 0.01f)
    }
    @Test fun correctPop_peaksAt108Percent() {
        val peak = (0L..PlayItMotion.MICRO_MS.toLong() step 10).maxOf { correctPopScale(it, reduced = false) }
        assertEquals(1.08f, peak, 0.01f)
        assertEquals(1f, correctPopScale(80, reduced = true), 0f)
    }
}
```
`FeedbackEffects.kt` defines the curves as pure functions, and each modifier animates time and applies the curve:
```kotlin
/** Damped wobble: +-12 degrees, settles to 0 at STANDARD_MS; 0 under reduced motion. */
fun heartWobbleAngle(elapsedMs: Long, reduced: Boolean): Float {
    if (reduced || elapsedMs >= PlayItMotion.STANDARD_MS) return 0f
    val t = elapsedMs / PlayItMotion.STANDARD_MS.toFloat()
    return 12f * (1f - t) * kotlin.math.sin(t * 4f * Math.PI.toFloat())
}

/** 100 -> 108 -> 100 % over MICRO_MS (21_ANIMATION_GUIDE :27); 100 % under reduced motion. */
fun correctPopScale(elapsedMs: Long, reduced: Boolean): Float {
    if (reduced || elapsedMs >= PlayItMotion.MICRO_MS) return 1f
    val t = elapsedMs / PlayItMotion.MICRO_MS.toFloat()
    return 1f + 0.08f * kotlin.math.sin(t * Math.PI.toFloat())
}
```

- [ ] **Step 2: Run them and see them fail**
- [ ] **Step 3: Implement** the effects, using `PlayItMotion` specs only (no new magic numbers).
- [ ] **Step 4: Run all the tests; record the screenshots**
- [ ] **Step 5: Commit** `feat(ui): purposeful effects: screen transitions, correct pop, heart wobble, star drop, centre confetti`

---

### Task 8 (card 24): Captions and mouth-shape cues (NFR-ACC-01, FR-02, FR-03)

**Why:**
- In Round 1, 3 of 5 caregivers said there was no support for hearing difficulties (ACC-06).
- The SRS asks for sound captions and articulation cues.
- The spec's attempt-2 correction "watch my lips" has nothing to show.

**Files:**
- Create: `domain/model/ArticulationGroup.kt` (pure Kotlin)
- Create: `domain/manager/CaptionText.kt` (pure Kotlin)
- Modify: `data/audio/AudioPlayer.kt` (`playSequence(..., onItemStart: ((index: Int, path: String) -> Unit)? = null)`, called before each item plays)
- Modify: `presentation/hearit/HearItViewModel.kt` (`caption: StateFlow<String?>` from `onItemStart`; `articulation: ArticulationGroup`)
- Modify: `presentation/sayit/SayItViewModel.kt` (`showMouthCue: StateFlow<Boolean>`, true from attempt 2 and in LeadAndMoveOn)
- Create: `presentation/components/CaptionBubble.kt`, `presentation/components/ArticulationCue.kt`
- Modify: `presentation/hearit/HearItScreen.kt` and `presentation/sayit/SayItScreen.kt` (show the caption under the letter card; mouth cue beside it, 72 dp, larger, 96 dp, at Say It attempt 2)
- Test: `domain/model/ArticulationGroupTest.kt`, `domain/manager/CaptionTextTest.kt`, `presentation/components/ArticulationCueTest.kt`, additions to `HearItViewModelTest` and `SayItViewModelTest`

**Interfaces:**
- Produces:
  - `enum class ArticulationGroup(val assetPath: String)`
  - `fun articulationFor(letter: String): ArticulationGroup`
  - `object CaptionText { fun forClip(path: String, letter: String, word: String): String? }`

- [ ] **Step 1: Write the failing tests**

```kotlin
class ArticulationGroupTest {
    @Test fun everyLetterHasAGroup() = ('a'..'z').forEach { articulationFor(it.toString()) }
    @Test fun lipsTogether() = listOf("m", "b", "p").forEach { assertEquals(ArticulationGroup.LIPS_TOGETHER, articulationFor(it)) }
    @Test fun teethOnLip() = listOf("f", "v").forEach { assertEquals(ArticulationGroup.TEETH_ON_LIP, articulationFor(it)) }
}

class CaptionTextTest {
    @Test fun phonemeClip() = assertEquals("/m/", CaptionText.forClip("audio/phonemes/m.mp3", "m", "mouse"))
    @Test fun keywordClip() = assertEquals("mouse", CaptionText.forClip("audio/keywords/kw_mouse.wav", "m", "mouse"))
    @Test fun carrier() = assertEquals("Listen!", CaptionText.forClip("audio/vo/tutor/car_listen.wav", "m", "mouse"))
    @Test fun pauseHasNoCaption() = assertNull(CaptionText.forClip("pause:500", "m", "mouse"))
}
```
```kotlin
// ArticulationCueTest (Robolectric)
@Test fun missingAsset_rendersNothing() {
    compose.setContent { PlayItTheme { ArticulationCue(group = ArticulationGroup.LIPS_TOGETHER, size = 72.dp, modifier = Modifier.testTag("cue")) } }
    compose.onNodeWithTag("cue").assertDoesNotExist()   // the picture is not in assets yet
}
```
Additions to `HearItViewModelTest`:
- `caption_followsSequence`: capture the `onItemStart` lambda.
- Calling it with a phoneme path sets `caption` to `"/m/"`.

Additions to `SayItViewModelTest`:
- `secondMiss_showsMouthCue`: after 2 wrong attempts, `showMouthCue.value` is true.

- [ ] **Step 2: Run them and see them fail**
- [ ] **Step 3: Implement**

```kotlin
package com.playit.app.domain.model

/** Mouth-shape groups for articulation cues; pictures come from docs/image-release (card 25). */
enum class ArticulationGroup(val assetPath: String) {
    LIPS_TOGETHER("images/mouth/mouth_lips_together.png"),   // m b p
    TEETH_ON_LIP("images/mouth/mouth_teeth_on_lip.png"),     // f v
    TONGUE_UP("images/mouth/mouth_tongue_up.png"),           // t d n l
    TEETH_CLOSE("images/mouth/mouth_teeth_close.png"),       // s z x
    BACK_OF_MOUTH("images/mouth/mouth_back.png"),            // k c g q
    ROUND_LIPS("images/mouth/mouth_round.png"),              // o u w
    WIDE_OPEN("images/mouth/mouth_wide_open.png"),           // a
    SMILE("images/mouth/mouth_smile.png"),                   // e i y
    OPEN_BREATH("images/mouth/mouth_open_breath.png"),       // h j r
}

fun articulationFor(letter: String): ArticulationGroup = when (letter.lowercase()) {
    "m", "b", "p" -> ArticulationGroup.LIPS_TOGETHER
    "f", "v" -> ArticulationGroup.TEETH_ON_LIP
    "t", "d", "n", "l" -> ArticulationGroup.TONGUE_UP
    "s", "z", "x" -> ArticulationGroup.TEETH_CLOSE
    "k", "c", "g", "q" -> ArticulationGroup.BACK_OF_MOUTH
    "o", "u", "w" -> ArticulationGroup.ROUND_LIPS
    "a" -> ArticulationGroup.WIDE_OPEN
    "e", "i", "y" -> ArticulationGroup.SMILE
    else -> ArticulationGroup.OPEN_BREATH // h j r
}
```
The groups are approximations for a child's cue, not phonetics. A teacher checks them during Gate 3, and the user approves the pictures (card 25).

```kotlin
package com.playit.app.domain.manager

object CaptionText {
    private val carriers = mapOf(
        "car_listen" to "Listen!", "car_this_letter_says" to "This letter says...",
        "car_say_it_with_me" to "Say it with me!", "car_your_turn" to "Your turn!",
        "car_watch_my_lips" to "Watch my lips.", "car_lets_say_together" to "Let's say it together.",
        "fb_try_later" to "Good trying! We'll practice this one again soon.",
    )
    fun forClip(path: String, letter: String, word: String): String? {
        val file = path.substringAfterLast('/').substringBeforeLast('.')
        return when {
            path.startsWith("pause:") -> null
            path.contains("/phonemes/") -> "/$letter/"
            path.contains("/keywords/") -> word
            else -> carriers[file]
        }
    }
}
```

`ArticulationCue` checks with `LocalContext.current.assets.list("images/mouth")` whether the file exists, inside a `remember`. If it is missing, it emits nothing. `CaptionBubble` is 24 sp, one line, in a pill, and shows the latest caption. When the caption is null it fades out (a snap under reduced motion).

- [ ] **Step 4: Run all the tests; record the screenshots**
- [ ] **Step 5: Commit** `feat(a11y): sound captions and mouth-shape cues in Hear It and Say It (NFR-ACC-01, FR-03)`

---

### Task 9 (card 25, agy image session, asset card): Mouth-shape pictures

This runs exactly like card 08: rounds until the user picks one per item. It writes only to `C:\Users\riva.zn\Documents\playIT-image-batches\2026-10-07-mouth-shapes\`.
- **Brief:** Claude writes `docs/assets/briefs/2026-10-07-mouth-shapes/items.json` (9 items).
  - **Subject:** a friendly child's mouth close-up in the PlayIT style (flat shapes, `#4A2E18` outline, no teeth detail beyond simple white shapes, no harsh red lips).
  - **Style references:** the 6 style references of card 08.
- **After the user's picks:** Claude runs `tools/images/cutout.py`, `audit.py` and `make_release.py` into `docs/image-release/<date>/` with `appPath = images/mouth/<id>.png`. Then a small copy step (in card 24 if it is not yet done, or card 24b) brings them into assets. That is when `ArticulationCue` starts showing them.

---

## Claude's track (second hand)

1. **Today:**
   - Generate cards 17-25 from this plan (one file per task, "Status: ready" for 17; the rest "ready after NN is accepted").
   - Write the mouth-shape brief.
   - Fix the defense reviewer: "marks the letter for review" isn't built (A2, F6); mic states are time-based, not voice-driven.
2. **Audio (by Oct 14, user review needed):**
   - Kokoro remakes of the lesson voice lines still on Edge TTS (`audio/ui/vo_*.mp3`, 26 lines) and of the Chapter 1 words. Review pages go to the user, then into an audio release.
   - With the user's /m/ pick: card 09. With the composition OKs: card 03b.
3. **Images:** releases for batch 1 (card 13) and the mouth shapes (card 25), once the user OKs them.
4. **Every weekday on "run and review":** the review loop above; one accept or fix card per agy commit.
5. **Fri Oct 16:**
   - Build the Round 2 APK.
   - Write `docs/tasks/PHONE_TEST_round2-ui.md`: an A21s checklist covering fit at Samsung font sizes, no lag on the map and in Find It, the mic states, captions and portrait.
   - Update `docs/defense/NUMBERS.md` (test counts, APK size).

## Self-review (done while writing)

- **Spec coverage:**
  - fit/responsive: Tasks 2-6;
  - performance on the A21s and no haptics: Task 1;
  - purposeful effects: Tasks 1 and 7;
  - map: Task 6;
  - core screens: Tasks 3-5;
  - assets: Task 9, Claude track 2-3, and cards 13, 09, 03b and 15;
  - reviewer items built: mic states (Task 3), captions and mouth cues (Task 8), privacy (card 14), decodable words (card 15);
  - limitations untouched: telemetry, ASR on children, learning effect, noise handling, voice-driven ripple.
- **Placeholders:** the screen-wiring steps in Tasks 3-6 refer to existing composables by name and give the new structure. The new components, pure functions and tests are written out in full.
- **Type consistency:**
  - `LocalPlayItDimens` / `PlayItDimens` fields (`tileSize`, `micSize`, `primaryCta`, `findItCardMinHeight`, `mapNodeSize`, `completeMascot`, `letterCardHeight`, `mascotHeader`, `bubbleTextSp`, `bubbleMaxLines`, `contentMaxWidth`, `screenPadding`, `findItCardAspect`) are defined in Task 2 and used under the same names in Tasks 3-6.
  - `MicStatus` and `micStatusFor` are in Task 3; `ArticulationGroup` and `articulationFor` in Task 8.
  - `rememberAssetPainter(path, maxSize)` is in Task 1.
- **Review Focus:** all 5 failure modes have a named test in the task that owns the code.
