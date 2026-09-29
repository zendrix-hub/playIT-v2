# Card 04: Hear It modeling sequence (FR-02)

Status: ready

Requires card 03 to be committed first (this card uses `AudioResolver.getTutorPath`).

## Why
Hear It plays an intro, then the sound once. The spec's "I do" step (§2.1, Table 3) models the pure sound three times, gives the key word, and repeats the sound, so a child can learn without a teacher.

## Scope notes
- Audio: do not add any. Card 03's pre-step copies the approved carrier clips (`car_listen`, `car_this_letter_says`, `car_say_it_with_me`) into `app/src/main/assets/audio/vo/tutor/`. `AudioPlayer` skips a missing asset, so any clip marked FIX is silent and Hear It still plays the phoneme and key-word clips.
- The ≤15 s sequence limit in spec §2.1 is [proposed] and not covered by AGENTS.md Decisions. Do not implement or test it.
- The per-letter JSON LessonScript (spec §6.2) and the letter-reveal animation (`SHOW_LETTER`) are later work. The template lives in Kotlin for now.

## Files
All paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- New: `domain/manager/HearItSequenceBuilder.kt` (pure Kotlin, no `android.*` imports)
- New: test `domain/manager/HearItSequenceBuilderTest.kt`
- Edit: `data/audio/AudioPlayer.kt` (pause entries in `playNextInSequence` only)
- Edit: `presentation/hearit/HearItViewModel.kt` and test `presentation/hearit/HearItViewModelTest.kt`

## Changes
1. `HearItSequenceBuilder.kt`:
   ```kotlin
   object HearItSequenceBuilder {
       // Spec §2.1 Table 3 / §6.2, without the SHOW_LETTER animation token.
       val TEMPLATE = listOf(
           "car_listen", "car_this_letter_says",
           "PHONEME", "PAUSE_500", "PHONEME", "PAUSE_500", "PHONEME",
           "KEYWORD", "PHONEME", "car_say_it_with_me"
       )

       /** Maps tokens to asset paths. car_* -> tutorPath(id); PHONEME -> phonemePath;
        *  KEYWORD -> keyWordPath (dropped when null); PAUSE_<ms> stays as-is. */
       fun build(
           template: List<String>,
           phonemePath: String,
           keyWordPath: String?,
           tutorPath: (String) -> String
       ): List<String>

       /** Steps 3 to 6 for the ear button: from "car_this_letter_says" through the last PHONEME. */
       fun replayTemplate(template: List<String> = TEMPLATE): List<String>

       /** 500 for "PAUSE_500"; null for anything that is not a PAUSE_<ms> token. */
       fun pauseMillis(entry: String): Long?
   }
   ```
2. `AudioPlayer.playNextInSequence`: before the SFX check, if `HearItSequenceBuilder.pauseMillis(currentPath)` returns a value, post the next step with `mainHandler.postDelayed(..., ms)` (guarded by `isSequencePlaying`, like the SFX branch) instead of playing a file. `stop()` already clears pending callbacks.
3. `HearItViewModel`:
   - Add `playModelingSequence()`: stop audio, set `_isPlaying = true`, increment `_playCount` (the screen unlocks Next when `playCount > 0`), then call `audioPlayer.playSequence(sequence) { _isPlaying.value = false }`, where `sequence` is `HearItSequenceBuilder.build(TEMPLATE, audioResolver.getPhonemePath(letter), keyWordPath, audioResolver::getTutorPath)` and `keyWordPath` is `audioResolver.getWordPath(exampleWord)`, or null when `exampleWord` is blank or `PENDING_SME_REVIEW`.
   - On load and in `playHearItIntroAudio()` (mascot tap), call `playModelingSequence()`. Delete `playIntroThenPhonemeSound()`; `car_listen` replaces the `HEARIT_INTRO_01` intro.
   - `playPhonemeSound()` (the ear button and the letter card tap) now plays `build(replayTemplate(), ...)` with the same `_isPlaying` and `_playCount` handling. Keep its name so `HearItScreen` does not change.

## Tests
`HearItSequenceBuilderTest`:

| Test | Assertion |
|---|---|
| `build_m_mouse_followsTable3` | for m/mouse: starts with the `car_listen` tutor path, ends with the `car_say_it_with_me` tutor path, has 3 phoneme paths before the key-word path and 1 after, and keeps both `PAUSE_500` entries |
| `build_noKeyWord_dropsKeyword` | with `keyWordPath = null`, no key-word entry and still 4 phoneme paths |
| `replayTemplate_isSteps3to6` | starts with `car_this_letter_says`, ends with `PHONEME`, contains neither `car_listen` nor `car_say_it_with_me` |
| `pauseMillis_parsesOnlyPauseTokens` | `pauseMillis("PAUSE_500") == 500L`; null for `"PHONEME"`, `"audio/words/word_mouse.mp3"`, `"PAUSE_x"` |

`HearItViewModelTest`:
- In `setup()`, stub `audioResolver.getWordPath(any())` and `audioResolver.getTutorPath(any())` (strict mock). Make `audioPlayer.playSequence(any(), any())` invoke its callback, like the existing `playAssetAudio` stub.
- Replace `playPhonemeSound_playsCorrectAsset` with `load_playsFullModelingSequence`: verify `playSequence` is called with the builder's full sequence for m/mouse on load.
- Add `playPhonemeSound_playsReplaySegment`: verify `playSequence` is called with the builder's replay sequence.
- Keep `playPhonemeSound_incrementsPlayCount` passing (1 after load, 2 after `playPhonemeSound()`).

## Commit
`feat(hearit): I-do modeling sequence (FR-02)`

Decisions used: none. The ≤15 s limit is [proposed] and not implemented.
