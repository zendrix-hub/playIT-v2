# Card 26: Lesson voice lines in the Kokoro voice (replaces the old Edge TTS)

Status: done

## Why
Lily's 19 lesson lines are still the old Edge-TTS voice: welcome, correct, try again, hints, intros, unlock, complete. They are `audio/ui/vo_*.mp3`, from `AudioResolver.getVoPath` and the 3 rotating functions. Since 2026-09-30 every other spoken line in the app uses the Kokoro voice, so a child hears two different voices.

The user approved the Kokoro remakes on 3 listening pages on 2026-10-07. The release is `docs/audio-release/2026-10-07/manifest.json`: 19 clips in `lesson/`. Two texts changed so they match the app:
- `vo_findit_intro_01` now asks for **all** the pictures, because Find It has 3 targets.
- `vo_sayit_intro_01` says "say it", because Say It scores the word.

Two more changed after the user's review:
- `vo_hint_01` dropped "Hmm", which sounded like "hum".
- Three lines are slower: `vo_welcome_01`, `vo_encourage_02` and `vo_hint_02`.

## Pre-step: copy the released clips
1. For each of the 19 entries in `docs/audio-release/2026-10-07/manifest.json`, check the SHA-256 of `docs/audio-release/2026-10-07/<file>`. Stop if one differs.
2. Copy them unchanged to `app/src/main/assets/audio/vo/lesson/<clipId>.wav`.

The ids: `vo_welcome_01`, `vo_encourage_01`, `vo_encourage_02`, `vo_encourage_03`, `vo_correct_01`, `vo_correct_02`, `vo_hint_01`, `vo_hint_02`, `vo_streak_01`, `vo_complete_01`, `vo_unlock_01`, `vo_blendit_intro_01`, `vo_findit_intro_01`, `vo_sayit_intro_01`, `vo_sayit_word_intro_01`, `vo_quiet_check_01`, `vo_noise_alert_01`, `vo_map_tarana`, `vo_parent_gate`.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Add: `app/src/main/assets/audio/vo/lesson/<clipId>.wav` (the 19 files above)
- Delete: the 19 replaced `app/src/main/assets/audio/ui/<clipId>.mp3` files (the same 19 ids). Keep the 7 unused `vo_*.mp3` and every `sfx_*.mp3`.
- Modify: `data/audio/AudioResolver.kt`
- Modify: `data/audio/AudioResolverTest.kt` and `data/audio/AudioCompletenessCheckTest.kt` (both under `app/src/test/java/com/playit/app/`)

## Changes
1. **`AudioResolver`.** Add, inside the class:
```kotlin
    /** Lesson lines released in the Kokoro voice (docs/audio-release/2026-10-07); the rest stay on the old MP3s. */
    private val kokoroLessonVo = setOf(
        "welcome_01",
        "encourage_01",
        "encourage_02",
        "encourage_03",
        "correct_01",
        "correct_02",
        "hint_01",
        "hint_02",
        "streak_01",
        "complete_01",
        "unlock_01",
        "blendit_intro_01",
        "findit_intro_01",
        "sayit_intro_01",
        "sayit_word_intro_01",
        "quiet_check_01",
        "noise_alert_01",
        "map_tarana",
        "parent_gate"
    )

    private fun lessonVoPath(suffix: String): String =
        if (suffix in kokoroLessonVo) "audio/vo/lesson/vo_$suffix.wav" else "audio/ui/vo_$suffix.mp3"
```
   - `getVoPath(vo)` returns `lessonVoPath(vo.filenameSuffix)`.
   - `getRotatingCorrectVo`, `getRotatingEncourageVo` and `getRotatingHintVo` return `lessonVoPath(suffix)`, with the same rotation.
   - In `getDevPlaceholderForAsset`, treat `audio/vo/lesson/` like `audio/vo/ui/` (VO placeholder).
2. **`AudioResolverTest.getVoPath_returnsCorrectPaths`:**
   - The 4 Kokoro lines expect `audio/vo/lesson/vo_<suffix>.wav`.
   - `RETURN_WELCOME_01` (not released) still expects `audio/ui/vo_return_welcome_01.mp3`.
   - Update the rotating-path tests to the `.wav` lesson paths.
3. **`AudioCompletenessCheckTest`:**
   - `requiredVoLines` becomes the 19 released ids as `vo_<suffix>.wav`, checked in `audio/vo/lesson/`.
   - The two lines no longer in the lesson set (`vo_milestone_01.mp3`, `vo_return_welcome_01.mp3`) move to a `legacyVoLines` list, still checked in `audio/ui/`.
   - `verifyRequiredAssetCounts` expects 19 lesson lines and 2 legacy lines.
4. Change nothing else. These are the only files the card touches.

## Tests
| Test file | Test | Assertion |
|---|---|---|
| AudioResolverTest | `getVoPath_returnsCorrectPaths` | released lines -> `audio/vo/lesson/vo_*.wav`; `RETURN_WELCOME_01` -> old mp3 |
| AudioResolverTest | `lessonVo_allReleasedLinesUseWav` | every id in the 2026-10-07 manifest maps to an existing `app/src/main/assets/audio/vo/lesson/<id>.wav` (read the manifest from `../docs` or `docs`, like `ManifestPrivacyTest` finds files) |
| AudioCompletenessCheckTest | `verifyAllRequiredAudioFilesExistOnDisk` | the 19 lesson wavs and the 2 legacy mp3s exist |

`review_card.py` also checks every added asset against the release SHA-256. Run `./gradlew testDebugUnitTest`.

## Phone test (with card 17's)
- Hear It, Find It and Blend It all speak in one voice, Lily's Kokoro voice.
- Get one Find It answer wrong: you hear "Good try! Let's listen again." in the new voice.
- Open the Parent Zone: the gate line is in the new voice.

## Commit
`feat(audio): lesson voice lines in the Kokoro voice from release 2026-10-07 (NFR-AUD-01)`

Decisions used: Kokoro-82M for every spoken line (AGENTS.md Decisions); the user's OK on review pages 2026-10-06-lesson-vo, 2026-10-07-lesson-vo-redo and 2026-10-07-lesson-vo-final.
