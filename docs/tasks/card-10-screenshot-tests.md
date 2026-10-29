# Card 10: Screenshot tests for the changed screens (Roborazzi), uploaded by CI

Status: accepted

Runs after card 12 is accepted (accepted 2026-10-05). It touches only build files, new tests and the CI workflow, so it can run before or after card 13.

## Why
Cards 06-12 changed six child-facing screens. The unit tests check the ViewModels, but nobody sees the screens until a phone test. A screenshot per screen, rendered on the JVM and uploaded by CI, lets the reviewer see each card's visual change in the PR without building an APK.

Claude's spike on 2026-10-05 proved the setup in this repo: Roborazzi 1.26.0, Robolectric 4.13, `@GraphicsMode(NATIVE)`, SDK 34. It rendered Hear It for the letter m, with fonts, Lily, the mouse picture and the 5 dots, in about 14 minutes on a cold cache. Use exactly these versions.

## Files
- Edit: `build.gradle.kts` (root)
- Edit: `app/build.gradle.kts`
- Edit: `.github/workflows/android_ci.yml`
- New tests, package `com.playit.app.screenshot`, all under `app/src/test/java/com/playit/app/screenshot/`:
  - `HearItScreenshotTest.kt`
  - `FindItScreenshotTest.kt`
  - `BlendItScreenshotTest.kt`
  - `NamePromptScreenshotTest.kt`
  - `LetterCompleteScreenshotTest.kt`

## Changes
1. **Root `build.gradle.kts`.** In `buildscript.dependencies`, add `classpath("io.github.takahirom.roborazzi:roborazzi-gradle-plugin:1.26.0")`.
2. **`app/build.gradle.kts`:**
   - Plugins: add `id("io.github.takahirom.roborazzi")`.
   - In `android { }`, add the block below. The environment-variable part is for Claude's WSL machine only. Its proxy re-signs TLS, so Robolectric can't download `android-all` itself. When the variable is unset (CI, agy's PC), the block does nothing.
     ```kotlin
     testOptions {
         unitTests {
             isIncludeAndroidResources = true
             // Local WSL only: the proxy re-signs TLS, so Robolectric reads android-all from a curl-fetched folder.
             System.getenv("ROBOLECTRIC_DEPS_DIR")?.let { dir ->
                 all {
                     it.systemProperty("robolectric.offline", "true")
                     it.systemProperty("robolectric.dependency.dir", dir)
                 }
             }
         }
     }
     ```
   - In `dependencies`, add:
     ```kotlin
     testImplementation("org.robolectric:robolectric:4.13")
     testImplementation("io.github.takahirom.roborazzi:roborazzi:1.26.0")
     testImplementation("io.github.takahirom.roborazzi:roborazzi-compose:1.26.0")
     testImplementation("io.github.takahirom.roborazzi:roborazzi-junit-rule:1.26.0")
     testImplementation(platform(libs.androidx.compose.bom))
     testImplementation(libs.androidx.compose.ui.test.junit4)
     ```
3. **`android_ci.yml`.** After "Run Unit Tests", add two steps:
   ```yaml
         - name: Record Screenshots
           run: ./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'

         - name: Upload Screenshots
           uses: actions/upload-artifact@v4
           with:
             name: playIT-screenshots
             path: app/build/outputs/roborazzi/
             if-no-files-found: error
   ```
   Change nothing else in the workflow.
4. **Every screenshot test uses the spike's pattern.** The spike's test is below. Use it verbatim for `HearItScreenshotTest`, and copy its structure for the others:
   ```kotlin
   @RunWith(RobolectricTestRunner::class)
   @GraphicsMode(GraphicsMode.Mode.NATIVE)
   @Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
   class HearItScreenshotTest {
       @get:Rule val compose = createComposeRule()

       @Test
       fun hearIt_letterM() {
           val repo: PhonemeRepository = mockk()
           coEvery { repo.getPhonemeById(any()) } returns Phoneme(1, "m", "p", "images/pictures/picture_mouse.png", "mouse")
           val player: AudioPlayer = mockk(relaxed = true)
           val resolver: AudioResolver = mockk(relaxed = true)
           val handle: SavedStateHandle = mockk()
           every { handle.get<String>("phonemeId") } returns "1"
           val vm = HearItViewModel(repo, player, resolver, handle)
           compose.setContent { PlayItTheme { HearItScreen(vm, onNext = {}, onBack = {}) } }
           compose.waitForIdle()
           compose.onRoot().captureRoboImage("build/outputs/roborazzi/hearit_letter_m.png")
       }
   }
   ```
   Rules for every test:
   - **Real ViewModels.** Build the real ViewModel with mockk dependencies. The audio player and resolver are relaxed. Stub repositories with fixed data, and reuse the stubs from that ViewModel's existing unit test where it has them. Use a real `GridGenerator` and a real `BlendItWordSelector` if their constructors take no arguments.
   - **File names.** Save to `build/outputs/roborazzi/<name>.png`, with the names in the Tests table.
   - **If `waitForIdle()` never returns** (an endless animation or the 10 s idle timer), set `compose.mainClock.autoAdvance = false` before `setContent`, then call `compose.mainClock.advanceTimeBy(2_000)` before the capture. Note it in the commit body.
   - **Production code is off limits.** If a screen can't be rendered without changing production code, skip that screen: leave its file out and name it in the commit body.
5. **Say It is left out on purpose.** It needs the microphone permission and Vosk. A later card covers it.
6. **Do not change** any production code, theme, layout or test outside the `screenshot` package.

## Tests
| Test file | Test | Assertion |
|---|---|---|
| HearItScreenshotTest | `hearIt_letterM` | writes `hearit_letter_m.png` |
| FindItScreenshotTest | `findIt_letterM_fiveHearts` | Find It for m with a fresh grid: writes `findit_letter_m.png`. The top bar shows 5 hearts (card 11) |
| BlendItScreenshotTest | `blendIt_group1_firstWord` | Blend It group 1, first word: writes `blendit_group1.png` |
| NamePromptScreenshotTest | `namePrompt_avatarOnly` | writes `nameprompt.png`. Shows no text field (card 07b) |
| LetterCompleteScreenshotTest | `letterComplete_threeHeartsLost_oneStar` | `heartsLost` = `"3"` in the `SavedStateHandle`: writes `lettercomplete_1star.png` (card 11) |

A screenshot test passes if it renders without an exception. CI keeps the PNGs as the artifact `playIT-screenshots`. There is no image comparison yet; a later card can add `verifyRoborazziDebug` once the images are stable.

Run `./gradlew testDebugUnitTest`; the screenshot tests run there too, and capturing without the record task does nothing. Then run `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`, and check that the 5 PNGs exist in `app/build/outputs/roborazzi/`. The first run downloads Robolectric's `android-all` (about 150 MB).

## Commit
`test(ui): screenshot tests for the changed screens, uploaded by CI (FR-04, FR-05, NFR-IND-01)`

Decisions used: none new. The Roborazzi and Robolectric versions are the ones Claude's 2026-10-05 spike proved.
