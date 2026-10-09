# Card 03b: Spoken Say It corrections (FR-03)

Status: ready after 19

Runs immediately after card 19 is accepted, because both edit `SayItViewModel.kt`.

## Why
In Round 1, teachers flagged that children who made pronunciation mistakes had no spoken guidance showing *how* to fix the sound.
Card 06 added feedback *text* based on the Vosk error type (`LETTER_NAME`, `ADDED_VOWEL`, `SUBSTITUTION`).
Card 03b connects the spoken corrective audio lines:
- When the child produces an added vowel (e.g., "ma" instead of /m/, error `SpeechErrorType.ADDED_VOWEL`), play `fb_no_ah` ("Just the sound, not the extra ah!").
- When the child says the letter name (e.g., "em" instead of /m/, error `SpeechErrorType.LETTER_NAME`), play the targeted reminder.
- Uses existing approved audio line `audio/vo/fb_no_ah.mp3` / `audio/vo/lesson/fb_no_ah.wav` via `AudioResolver`.

Say It never deducts hearts (spec default, user confirmed).

## Files
All code paths under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Edit: `presentation/sayit/SayItViewModel.kt`
- Edit: `data/audio/AudioResolver.kt`
- Edit: `presentation/sayit/SayItViewModelTest.kt`

## Changes
1. **`AudioResolver.kt`**:
   - Ensure `getCorrectiveVoPath(errorType: SpeechErrorType)` returns the appropriate path:
     - `SpeechErrorType.ADDED_VOWEL` -> `"audio/vo/fb_no_ah.mp3"` (or lesson wav if released)
     - `SpeechErrorType.LETTER_NAME` -> `"audio/vo/fb_try_sound.mp3"` (fallback)
     - fallback to default encourage line if none exists.
2. **`SayItViewModel.kt`**:
   - When Vosk returns a speech evaluation with `SpeechErrorType.ADDED_VOWEL` on an incorrect attempt:
     - Play the corrective VO audio clip before returning to idle/next prompt state.
     - Ensure audio debounce prevents overlapping playback.
3. **Tests in `SayItViewModelTest.kt`**:
   - `addedVowelError_triggersFbNoAhSpokenCorrection`: verify `AudioPlayer.playAssetAudio` called with `fb_no_ah`.
   - `letterNameError_triggersTargetedSpokenCorrection`.

## Commit
`feat(sayit): spoken corrections for letter names and added vowels (FR-03)`

Requirement: FR-03, NFR-ASR-01
Tests run: local | CI only
Decisions used: user decision on fb_no_ah
