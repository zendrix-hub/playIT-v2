# Card 24b: Mouth pictures into the app; the Hear It cue moves beside the play button (NFR-ACC-01)

Status: ready

Runs after agy validates card 28 (`VALIDATOR_RUNBOOK_OCT10.md` section 6). It edits `HearItScreen.kt`, which card 28 also edited, so find code by name, not by line number.

## Why
- **The pictures are approved.** Card 25: agy made the 9 mouth pictures in rounds and the user picked one per shape. Claude cut them out (`tools/images/cutout.py`) and checked them (`tools/images/audit.py`: 0 FAIL, 0 WARN, and by eye on the 4 app backgrounds). The user approved all 9 in chat on 2026-10-10 ("Ship all 9 now"). The set mixes two face designs (6 + 3); the user accepted the mix, and the 3 may be redrawn later. `tools/images/make_release.py` built the release: `manifest.json` plus `mouth/<id>.png`, with `appPath = images/mouth/<id>.png`, the paths `ArticulationGroup` already reads.
- **Why the release waits outside `docs/image-release/`.** `PictureAssetsTest.releasedPicturesMatchManifest` (card 13) fails for any manifest in `docs/image-release/` whose files are not in the app yet. So Claude committed the release next to the picks, and this card moves it into `docs/image-release/` in the same commit as the copy. That way every commit stays green.
- **A pure copy would break Hear It on the A21s.** Claude tried it on 2026-10-10, with the pictures copied locally and not committed. Today the 72 dp cue has its own row under the letter card, reserved at 48 dp. With the picture present, the row grows to 72 dp. On the A21s profile (360x740 dp) the 5 step dots then end at 670 dp, below the top of Next at 664 dp. They are outside the lesson body, hidden under the bottom bar, so the child would have to scroll to see them.
  - The layout tests did not notice. `assertOnScreen` measures clipped bounds, and clipped bounds stop at the body's edge, so the check can never fail for content inside the lesson body.
  - The fix: the cue sits left of the play button, as Say It already shows it left of the mic. Beside the button it adds no height. A 72 dp empty slot on the right keeps the button centred. Card 24 put the cue "beside" the caption under the letter card; card 28 removed the caption, so the play button is now the thing under the card.
- **One test turns red with the pictures in.** `ArticulationCueTest.missingAsset_rendersNothing` uses `LIPS_TOGETHER`, which now has its picture. The cue gets an internal overload by path, so the test can name a picture that is not in assets.

Claude ran this whole card locally before writing it (Windows, JDK 17):
- Full suite: 386 tests, 0 failed, 6 skipped. The 6 are the font-scale checks on compact, skipped by design.
- `hearIt_stepsAboveNext`, run against the old layout (cue in its own row), fails on a21s only: "hearit_steps bottom 670.0.dp > hearit_next top 664.0.dp". With the fix it passes on all 4 sizes.
- Roborazzi on all 4 sizes: see Screenshots.

## Pre-step: move the release and copy the pictures
1. `git mv docs/assets/briefs/2026-10-07-mouth-shapes/release-2026-10-10-mouth docs/image-release/2026-10-10-mouth`
2. Read `docs/image-release/2026-10-10-mouth/manifest.json`. For each of the 9 entries in `images`, check the SHA-256 of `docs/image-release/2026-10-10-mouth/<file>`. Stop if a hash differs.
3. Copy each file unchanged to `app/src/main/assets/<appPath>` (for example `images/mouth/mouth_lips_together.png`). All 9 are new, and so is the folder `images/mouth/`.
4. Copy nothing else.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Move (`git mv`, from the pre-step):
  - `docs/assets/briefs/2026-10-07-mouth-shapes/release-2026-10-10-mouth/manifest.json` and `docs/assets/briefs/2026-10-07-mouth-shapes/release-2026-10-10-mouth/mouth/<id>.png`
  - to `docs/image-release/2026-10-10-mouth/manifest.json` and `docs/image-release/2026-10-10-mouth/mouth/<id>.png`
- Add: `app/src/main/assets/images/mouth/<id>.png`, the 9 files in the manifest. The review script (tools/dev/review_card.py) checks each one against the release SHA-256.
- Edit: `presentation/hearit/HearItScreen.kt`, `presentation/components/ArticulationCue.kt`
- Edit tests: `presentation/components/ArticulationCueTest.kt`, `data/PictureAssetsTest.kt`, `screenshot/LayoutMatrixTest.kt`

## Changes
1. **`HearItScreen`: the cue moves beside the play button.**
   - Delete the `Row(modifier = Modifier.heightIn(min = 48.dp), ...)` that holds `ArticulationCue`, and its comment.
   - Above `fun HearItScreen`, add:
     ```kotlin
     /** Mouth-shape cue beside the play button, and the empty slot that balances it (card 24b). */
     private val CUE_SIZE = 72.dp
     ```
   - In the `Column` that holds the play button, wrap the play button's `Box(contentAlignment = Alignment.Center) { ... }` (the pulse ring and the `GummyContainer`, unchanged inside) in this `Row`, re-indented by one level:
     ```kotlin
     // Mouth-shape cue (NFR-ACC-01) left of the play button, as Say It shows it left of the mic;
     // beside the button it adds no height (card 24b). Fixed slots on both sides keep the button
     // centred. No caption in the child view: pre-readers cannot read the carrier sentences
     // (user decision 2026-10-10, card 28).
     Row(
         horizontalArrangement = Arrangement.spacedBy(20.dp),
         verticalAlignment = Alignment.CenterVertically
     ) {
         Box(Modifier.size(CUE_SIZE), contentAlignment = Alignment.Center) {
             ArticulationCue(group = viewModel.articulation, size = CUE_SIZE)
         }
         // Pulsating PrimaryJoy Speaker Replay Button; the ring draws outside its bounds
         Box(contentAlignment = Alignment.Center) { /* unchanged */ }
         Spacer(Modifier.size(CUE_SIZE))
     }
     ```
     Why 20 dp: the pulse ring grows to 1.35x, so it reaches about 15 dp past the button on REGULAR (17 dp on WIDE) and stays off the cue. The row is 260 to 280 dp wide, which fits 360 dp phones.
   - Test tags:
     - `.testTag("hearit_next")` on the Next `GummyButton`'s modifier, after `.heightIn(min = 64.dp)`.
     - `modifier = Modifier.testTag("hearit_steps")` on the `Row` of the 5 step dots.
   - Change nothing else. The letter card keeps its 1.2x height (card 28).
2. **`ArticulationCue`: an overload by path.** The public function calls an internal one; the body is today's body with `group.assetPath` replaced by `assetPath`:
   ```kotlin
   @Composable
   fun ArticulationCue(group: ArticulationGroup, size: Dp, modifier: Modifier = Modifier) =
       ArticulationCue(assetPath = group.assetPath, size = size, modifier = modifier)

   /** By path, so a test can name a picture that is not in assets (card 24b). */
   @Composable
   internal fun ArticulationCue(assetPath: String, size: Dp, modifier: Modifier = Modifier) {
       val context = LocalContext.current
       val present = remember(assetPath) {
           val dir = assetPath.substringBeforeLast('/')
           val file = assetPath.substringAfterLast('/')
           runCatching { context.assets.list(dir)?.contains(file) == true }.getOrDefault(false)
       }
       if (!present) return

       Image(
           painter = rememberAssetPainter(assetPath, maxSize = size),
           // contentDescription and modifier exactly as today
       )
   }
   ```
3. **Say It: no change.** Its 96 dp cue already sits left of the mic from the second miss. In Claude's trial the cue and the mic stayed on screen on all 4 sizes.
4. **`LayoutMatrixTest`.**
   - `assertOnScreen` uses `getUnclippedBoundsInRoot()` instead of `getBoundsInRoot()` (add the import). Clipped bounds stop at the lesson body's edge, so the check could never fail for content in the body. In Claude's trial all 77 screenshot-package tests pass with it.
   - Add this helper after `assertOnScreen`:
     ```kotlin
     /**
      * Fails if [upper] reaches below the top of [lower]. Unclipped bounds, so a node pushed out of the
      * lesson body's scroll area still counts (clipped bounds stop at the body's edge and always pass).
      */
     protected fun assertAbove(upper: String, lower: String) {
         val u = compose.onNodeWithTag(upper, useUnmergedTree = true).getUnclippedBoundsInRoot()
         val l = compose.onNodeWithTag(lower, useUnmergedTree = true).getBoundsInRoot()
         assertTrue("$upper bottom ${u.bottom} > $lower top ${l.top} on $deviceName", u.bottom <= l.top)
     }
     ```
   - Add the two tests below, after `sayIt_micVisible_fontScale13`. Add these imports: `onNodeWithContentDescription`, `SpeechErrorType` and `SpeechJudgement`. The Say It test needs `SessionManager.activeProfileId` stubbed with a real `Long`: a miss is recorded for the active profile, and a fully relaxed mock throws `ClassCastException` there.
     ```kotlin
     /** Hear It with the mouth picture beside the play button: the step dots stay above Next (card 24b). */
     @Test fun hearIt_stepsAboveNext() {
         compose.setContent { PlayItTheme { hearIt() } }
         compose.waitForIdle()
         assertAbove("hearit_steps", "hearit_next")
     }

     /** Say It after two misses: the mouth picture shows left of the mic, and the mic stays on screen (card 24b). */
     @Test fun sayIt_mouthCueBesideMic() {
         val validator = mockk<SpeechValidator>(relaxed = true)
         every { validator.judgeWord(any(), any(), any()) } returns
             SpeechJudgement(isCorrect = false, errorType = SpeechErrorType.OTHER_WORD, heard = "cat")
         // The miss is recorded for the active profile, so the relaxed mock needs a real Long here.
         val session = mockk<SessionManager>(relaxed = true) { every { activeProfileId } returns MutableStateFlow<Long?>(1L) }
         val vm = SayItViewModel(
             phonemes, mockk<SayItAttemptRepository>(relaxed = true), validator,
             mockk<VoskRecognizer>(relaxed = true), mockk<AudioPlayer>(relaxed = true),
             mockk<AudioResolver>(relaxed = true), session, handle()
         )
         compose.setContent { PlayItTheme { SayItScreen(vm, onNext = {}, onBack = {}) } }
         compose.waitForIdle()
         compose.runOnIdle { repeat(2) { vm.evaluateSpeech("cat") } }
         compose.waitForIdle()
         val cue = compose.onNodeWithContentDescription("Mouth shape for this sound").getUnclippedBoundsInRoot()
         val mic = compose.onNodeWithTag("sayit_mic", useUnmergedTree = true).getUnclippedBoundsInRoot()
         assertTrue("cue right ${cue.right} > mic left ${mic.left} on $deviceName", cue.right <= mic.left)
         assertOnScreen("sayit_mic"); capture("sayit_cue")
     }
     ```
5. **`PictureAssetsTest`.** Move the manifest parsing out of `releasedPicturesMatchManifest` into `private fun releasedPictures(): Map<String, String>`. The logic is unchanged: appPath to SHA-256, and a later release folder wins. `releasedPicturesMatchManifest` calls it. Then add:
   ```kotlin
   /** Every mouth-shape group shows a released picture (card 24b); none falls back to drawing nothing. */
   @Test
   fun everyMouthCueIsReleased() {
       val released = releasedPictures()
       for (group in ArticulationGroup.values()) {
           assertTrue("${group.assetPath} is in no image release", group.assetPath in released)
           assertTrue("${group.assetPath} is not in assets", File(assetsDir, group.assetPath).exists())
       }
   }
   ```
6. **`ArticulationCueTest`.** Its class comment becomes: "Mouth cue: a released picture shows; a picture not in assets renders nothing, with no empty box (Review Focus 4)."

## Tests
| Test file | Test | Assertion |
|---|---|---|
| PictureAssetsTest | `everyMouthCueIsReleased` | every `ArticulationGroup.assetPath` is an `appPath` in a `docs/image-release` manifest, and the file exists under assets |
| PictureAssetsTest | `releasedPicturesMatchManifest` | unchanged; it now also checks the 9 mouth pictures by SHA-256 |
| ArticulationCueTest | `releasedPicture_renders` | `ArticulationCue(group = LIPS_TOGETHER, 72.dp)` with tag "cue": `assertContentDescriptionEquals("Mouth shape for this sound")` |
| ArticulationCueTest | `missingAsset_rendersNothing` | `ArticulationCue(assetPath = "images/mouth/mouth_not_released.png", 72.dp)` with tag "cue": `assertDoesNotExist()` (today's test used `LIPS_TOGETHER`) |
| LayoutMatrixTest (4 sizes) | `hearIt_stepsAboveNext` | as in Changes 4: the step dots end above Next |
| LayoutMatrixTest (4 sizes) | `sayIt_mouthCueBesideMic` | as in Changes 4: after two misses the cue is left of the mic and the mic is on screen; captures `sayit_cue_<size>.png` |

All other tests must still pass. Run `./gradlew testDebugUnitTest`. Expected: 386 tests, 0 failed, 6 skipped.

## Screenshots (record, then look at all 4 sizes)
`./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`

| Images | Must be true |
|---|---|
| `hearit_*` | The lips-together picture (M) is left of the play button, the play button is centred, and the 5 step dots show above Next. |
| `sayit_cue_*` | "Watch my lips": the picture is left of the orange mic, with "Let's try again" under the mic. Known: on compact (360x640) the attempt dots are half hidden behind the "Watch my lips" banner. That was there before the pictures, with or without the cue, and goes to card 28b. |
| `sayit_*` | Unchanged idle screen with no cue. |

## Phone checks (A21s, APK D)
- **Hear It M:** the lips-together picture is left of the play button, and all 5 dots show without scrolling.
- **Other letters:** S shows teeth close, A wide open and I smile, each matching the sound.
- **Say It M:** miss twice. Lily says "Watch my lips", and the same picture appears left of the mic.

## Commit
`feat(assets): mouth-shape pictures from image release 2026-10-10-mouth; Hear It cue beside the play button (NFR-ACC-01)`

Decisions used: images (user decisions 2026-10-01, AGENTS.md Decisions): agy generates, Claude cuts out and checks, the user approves on a review page, and only a release manifest brings a picture into the app. Card 25 release: the user approved all 9 in chat on 2026-10-10 ("Ship all 9 now"; the 6 + 3 face mix is accepted, and the 3 may be redrawn later).
