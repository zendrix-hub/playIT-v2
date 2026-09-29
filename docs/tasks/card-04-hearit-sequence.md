# Card 04: Hear It modeling sequence (FR-02)

Status: draft

Run after cards 01 to 03 are reviewed.

## Why
Hear It plays an intro, then the sound once. The spec's "I do" step (Table 3) models the pure sound three times, gives the key word, and repeats the sound, so a child can learn without a teacher.

## Files
- New: `app/src/main/assets/lessons/hearit_sequence.json`
- New: `domain/manager/HearItSequenceBuilder.kt` (pure Kotlin) + test
- Edit: `presentation/hearit/HearItViewModel.kt` + its test

## Changes
1. `hearit_sequence.json`, one shared template for every letter:
   `["car_listen", "car_this_letter_says", "PHONEME", "PAUSE_500", "PHONEME", "PAUSE_500", "PHONEME", "KEYWORD", "PHONEME", "car_say_it_with_me"]`
2. `HearItSequenceBuilder.build(template, letter, keyWord): List<String>` maps tokens to asset paths: `car_*` → `audio/vo/tutor/<id>.wav`, PHONEME → the letter's phoneme path, KEYWORD → the word path, and `PAUSE_500` → the literal token `PAUSE_500`.
3. `AudioPlayer.playSequence`: if an entry is `PAUSE_<ms>`, wait that many milliseconds instead of playing a file. Add a unit test for this if `AudioPlayer` is testable; otherwise handle the pause in `HearItViewModel` before calling `playSequence` for each segment.
4. `HearItViewModel.playIntroThenPhonemeSound()` plays the built sequence. The replay button replays from `car_this_letter_says` onward.

## Tests
- `HearItSequenceBuilderTest`: the sequence for m/mouse has 3 PHONEME entries before KEYWORD and 1 after, starts with `car_listen`, and ends with `car_say_it_with_me`.
- `HearItViewModelTest`: verify playSequence is called with the builder's output on load.

## Commit
`feat(hearit): I-do modeling sequence from a data template (FR-02)`
