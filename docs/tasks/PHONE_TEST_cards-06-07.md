# Phone test: cards 06 and 07 (after agy's run tonight)

**Who:** the user, on the same Android phone as 2026-09-30. **Time:** about 15 minutes. **Build:** the **debug** APK, because the "Heard:" line only shows in debug builds.

## Before you start
1. After agy finishes both cards, ask it: "Build the debug APK with `./gradlew assembleDebug` and tell me the path." Install that APK.
2. Find a quiet room, hold the phone at a normal distance, and turn the volume to about 70%.
3. Copy the results table at the bottom into `docs/tasks/SESSION_HANDOFF.md` (or send it to Claude) when done.

## A. Say It feedback (card 06): letter M, word mode ("mouse")
For each row, tap the mic, say the utterance once, and wait for the result. Write down exactly what the **"Heard:"** line shows, plus the banner text.

| # | Say | Expected banner | Expected "Heard:" judgement |
|---|---|---|---|
| A1 | "mouse" (clearly) | Great listening! | NONE (correct) |
| A2 | "em" (the letter name) | Its sound, not its name | LETTER_NAME |
| A3 | "muh" | Just the sound | ADDED_VOWEL |
| A4 | "ma" | Just the sound | ADDED_VOWEL |
| A5 | stay silent until it stops | Say it out loud | NO_SPEECH |
| A6 | "cat" (a wrong word) | Listen again | OTHER_WORD |
| A7 | miss 3 times in a row (say "cat" 3 times) | Let's say it together (third miss), never "try again" | OTHER_WORD x3 |

Repeat A2 and A3 **three times each**. What we learn from them:
- If "Heard:" shows `em`/`muh` but the banner is wrong, it's a card 06 bug.
- If "Heard:" shows something else (empty, `[unk]`, `mouse`), Vosk is missing the foil, which is acoustic. That's the data we need to tune it.

## B. Idle re-prompt and next-step cues (card 07)

| # | Do | Expected |
|---|---|---|
| B1 | Open Hear It for M and let the sequence finish | While the sequence plays, "Next: Say It" stays disabled. When it ends, you hear "Great listening! Tap the big button." and the Next button unlocks and pulses |
| B2 | On Hear It, don't touch anything for 10 s | The cue repeats; after 3 repeats it stops |
| B3 | On Hear It, touch the screen every 5 s for 30 s | No re-prompt while you keep touching |
| B4 | In Find It, find all 3 pictures | Chime, praise, then "You found them all! Tap the big button."; "Complete Lesson" pulses |
| B5 | In Find It, wait 10 s before tapping anything | The Find It instruction replays |
| B6 | Finish a letter (complete screen), then wait 10 s | Fanfare, lines, then "You did it! Tap the big button."; "Continue to Map" pulses. The 10 s re-prompt never cuts into the fanfare or lines |
| B7 | Turn on reduced motion (if available) and repeat B1 | The pulse is much smaller |

The UI clips ship with card 07 (release `docs/audio-release/2026-10-01/`). If a cue is silent, check that `app/src/main/assets/audio/vo/ui/` has the three clips.

## Results (copy and fill in)
| # | What you heard or saw | "Heard:" line (A only) | Pass / Fail | Note |
|---|---|---|---|---|
| A1 | | | | |
| A2 (x3) | | | | |
| A3 (x3) | | | | |
| A4 | | | | |
| A5 | | | | |
| A6 | | | | |
| A7 | | | | |
| B1 | | — | | |
| B2 | | — | | |
| B3 | | — | | |
| B4 | | — | | |
| B5 | | — | | |
| B6 | | — | | |
| B7 | | — | | |
