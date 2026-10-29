# Card 31c: Lily's Say It and Find It lines fit the bubble (NFR-IND-01, NFR-AUD-01)

Status: blocked: the listening review is handed to Zendrix Riva, the team lead (user decision 2026-10-14). The code is ready as `docs/tasks/card-31c-code.patch`.

Part of card 31 (`card-31-post-midterm-polish.md`), team directive 2026-10-13. Not in APK H.

## How to finish (Zendrix, then agy)
The review page of card 31b also holds these takes (steps 1 and 2 there).
1. **Mark.** On that page, mark exactly one take OK for each of the two lines; the takes are speeds 0.95, 0.90 and 1.00. Mark the "reference" rows FIX.
2. **Release.**
   - Replace the `TODO` in `approvedBy` of `tools/audio/releases/2026-10-13-lines.json`.
   - Run `python3 tools/audio/make_release.py tools/audio/releases/2026-10-13-lines.json --batches <your playIT-audio-batches folder> --out docs/audio-release/2026-10-13-lines`, then commit the release and the spec.
3. **agy runs this card.**
   - Check the SHA-256 of `docs/audio-release/2026-10-13-lines/lesson/*.wav`, then copy both over the files in `app/src/main/assets/audio/vo/lesson/`.
   - `git apply docs/tasks/card-31c-code.patch`. It also brings the new texts into `tools/audio/tutor_script.py`.
   - Expected: 438 tests after 31b (436 + 2), 0 failed, 6 skipped.
4. **If a line gets every take FIX:** leave this card blocked. The app keeps today's line and bubble text.

## Why
- **Cut off on compact screens:** on 360x640 the bubble cut off two instructions with an ellipsis.
  - Say It (12 words): "Now it's your turn! Say the whole word clearly into the microphone!"
  - Find It (11 words): "Can you find all three pictures that start with this sound?". The voice said "all the pictures", so text and voice also differed.
- **Too long:** SPMP R3 asks for spoken prompts of 8 words or fewer.

## Step 0 (Claude): the voice takes
1. Batch `2026-10-13-card31-audio` (`tools/audio/card31_audio_batch.py`) has three Kokoro takes of each new line: speeds 0.95, 0.90 and 1.00, in the app's voice mix (af_heart 0.7 + af_bella 0.3).
   - "Your turn! Say the word." (5 words)
   - "Find the pictures that start with this sound." (8 words)
2. After the team marks one take OK per line, `make_release.py tools/audio/releases/2026-10-13-lines.json` writes `docs/audio-release/2026-10-13-lines/`.
3. `tools/audio/tutor_script.py` gets the new texts.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Replace: `app/src/main/assets/audio/vo/lesson/vo_sayit_word_intro_01.wav`, `vo_findit_intro_01.wav` (from the release)
- Modify: `presentation/sayit/SayItScreen.kt`
- Modify: `presentation/findit/FindItScreen.kt`
- Add: `presentation/LessonPromptsTest.kt`
- Modify: `tools/audio/tutor_script.py` (the two line texts)

## Changes
- The bubbles show the new lines, word for word what Lily says. The texts are constants `SAY_IT_WORD_PROMPT` and `FIND_IT_PROMPT`.

## Tests
| Test | Checks |
|---|---|
| `LessonPromptsTest.promptsAreShort` | 8 words or fewer |
| `LessonPromptsTest.promptsMatchTheReleasedVoice` | Each prompt equals the `text` of its clip in `docs/audio-release/2026-10-13-lines/manifest.json` |

## Commit
`feat(ux): Lily's Say It and Find It lines fit the bubble, and the text matches the voice (NFR-IND-01, NFR-AUD-01)`
