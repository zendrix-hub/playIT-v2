# Card 05: Approved key words and tutor carriers into the app (NFR-AUD-01, FR-02)

Status: done

## Why
The app's key words and phonemes are Edge-TTS clips (`scripts/upgrade_neural_audio_pipeline.py`), which have no redistribution rights. Card 03's tutor clips were removed because they were never approved (6e07d19), so Hear It and Say It currently skip every tutor line. The user approved 26 key words and 7 carrier lines in the chosen Kokoro voice on 2026-09-30. This card ships exactly those clips and points Hear It and Say It at them. Held sounds (phonemes), the correction fragments, and Blend It words are not part of this card.

## Pre-step: check the release folder
1. Open `docs/audio-release/2026-09-30/manifest.json`. It must list 33 clips: 26 with `clipId` `kw_<word>` and 7 tutor clips.
2. For every entry, check that the file exists under `docs/audio-release/2026-09-30/` and that its SHA-256 matches `sha256` (`sha256sum <file>`). If any file is missing or does not match, stop: write "Release folder does not match its manifest" to `docs/tasks/QUESTIONS.md` and follow the runbook's stop-and-ask steps.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Add: `app/src/main/assets/audio/keywords/kw_<word>.wav`, the 26 files copied unchanged from `docs/audio-release/2026-09-30/keywords/`
- Add: `app/src/main/assets/audio/vo/tutor/<id>.wav`, the 7 files copied unchanged from `docs/audio-release/2026-09-30/tutor/`
- Edit: `data/audio/AudioResolver.kt` and test `data/audio/AudioResolverTest.kt`
- Edit: `presentation/hearit/HearItViewModel.kt` and test `presentation/hearit/HearItViewModelTest.kt`
- Edit: `presentation/sayit/SayItViewModel.kt` and test `presentation/sayit/SayItViewModelTest.kt`
- Edit test: `data/audio/AudioCompletenessCheckTest.kt`

Do not copy anything else from `docs/audio-release/` or `docs/audio-review/`. Do not delete or change `audio/words/` or `audio/phonemes/`; Blend It still uses `audio/words/`, and the phonemes are replaced by a later card.

## Changes
1. `AudioResolver`:
   - Add `fun getKeyWordPath(word: String): String`: lowercase, trim, drop `-` (so "Yo-yo" becomes "yoyo"), return `"audio/keywords/kw_$clean.wav"`.
   - In `getDevPlaceholderForAsset`, map `audio/keywords/` to `DevAudioCategory.WORD` and `audio/vo/tutor/` to `DevAudioCategory.VO`, above the existing `contains` checks.
   - Keep `getWordPath` unchanged (Blend It uses it).
2. `HearItViewModel.buildSequence`: use `audioResolver.getKeyWordPath(exampleWord)` instead of `getWordPath`. Nothing else changes.
3. `SayItViewModel`: use `getKeyWordPath` in the two places that play the example word, `playWordAudio()` and the `model` clip in `evaluateSpeech()`. Do not change anything else (the prompt ladder and fragment ids stay as they are; a later card changes them).

## Tests
`AudioResolverTest`:

| Test | Assertion |
|---|---|
| `getKeyWordPath_returnsWavInKeywordsFolder` | `getKeyWordPath("Mouse") == "audio/keywords/kw_mouse.wav"` and `getKeyWordPath("Yo-yo") == "audio/keywords/kw_yoyo.wav"` |
| `devPlaceholder_mapsKeywordsAndTutor` | `getDevPlaceholderForAsset("audio/keywords/kw_mouse.wav") == DevAudioCategory.WORD.assetPath` and `getDevPlaceholderForAsset("audio/vo/tutor/car_listen.wav") == DevAudioCategory.VO.assetPath` |

`AudioCompletenessCheckTest`:

| Test | Assertion |
|---|---|
| `keywordClips_existForAll26SeededWords` | for each of apple, ball, cat, dog, elephant, fish, goat, hat, insect, jug, kite, lion, mouse, nest, orange, pig, queen, rabbit, sun, tiger, umbrella, van, watch, box, yoyo, zebra: `src/main/assets/audio/keywords/kw_<word>.wav` exists and is not empty |
| `approvedTutorCarriers_exist` | for each of car_listen, car_this_letter_says, car_say_it_with_me, car_your_turn, car_watch_my_lips, car_lets_say_together, fb_try_later: `src/main/assets/audio/vo/tutor/<id>.wav` exists and is not empty |

`HearItViewModelTest`: in `setup()`, stub `audioResolver.getKeyWordPath(any())` to return `"word_path"`, so the expected sequences in `load_playsFullModelingSequence` and `playPhonemeSound_playsReplaySegment` stay the same. In `load_pendingExampleWord_dropsKeyword`, verify `getKeyWordPath` is never called (instead of `getWordPath`).

`SayItViewModelTest`: in `setup()`, stub `audioResolver.getKeyWordPath(any())` to return `"word_path"`. In the three tests that stub `getWordPath("mouse")` (`wordMode_promptPlaysIntroVoThenWordAudio`, `playWordAudio_whenListening_stopsListeningAndPlaysAudio`, `playWordAudio_rapidTaps_debounced`), stub and expect `getKeyWordPath("mouse")` returning `"audio/keywords/kw_mouse.wav"` instead.

All other existing tests must still pass. Run `./gradlew testDebugUnitTest`.

## Commit
`feat(audio): ship approved Kokoro key words and tutor carriers (NFR-AUD-01, FR-02)`

Decisions used: the user's listening approval of 2026-09-30 (`docs/audio-release/2026-09-30/manifest.json`). The teacher audit is still pending and is a gate before merging to `main`, not before this branch commit.
