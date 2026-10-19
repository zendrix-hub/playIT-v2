# Card 31b: One calm sound set from Kenney's CC0 packs; a wrong answer plays one soft sound (NFR-AUD-01, NFR-IND-01)

Status: blocked: the listening review is handed to Zendrix Riva, the team lead (user decision 2026-10-14). The code is ready as `docs/tasks/card-31b-code.patch`.

Part of card 31 (`card-31-post-midterm-polish.md`), team directive 2026-10-13. Not in APK H.

## How to finish (Zendrix, then agy)
One review page covers this card and card 31c.
1. **Get the batch.** Erik sends `2026-10-13-card31-audio.zip`. Unzip it to `Documents/playIT-audio-batches/2026-10-13-card31-audio/`.
2. **Listen and mark.**
   - Open `index.html`. For each of the 7 sounds mark exactly one take OK and the other two FIX. Mark the "reference" rows (today's sounds) FIX.
   - Export the CSV into the batch folder as `2026-10-13-card31-audio_review.csv`.
3. **Release.**
   - Replace the `TODO` in `approvedBy` of `tools/audio/releases/2026-10-13-sfx.json` with who marked it and when.
   - Run `python3 tools/audio/make_release.py tools/audio/releases/2026-10-13-sfx.json --batches <your playIT-audio-batches folder> --out docs/audio-release/2026-10-13-sfx`.
   - The tool takes the one OK take per sound. If a sound has no OK take or more than one, it refuses: write that to `QUESTIONS.md` and stop.
   - A sound with every take FIX leaves the spec and `releasedSfx`, and keeps its old MP3.
   - Commit the release and the spec: `docs(audio): release 2026-10-13-sfx, Kenney CC0 sound set (NFR-AUD-01)`.
4. **agy runs this card.**
   - Check the SHA-256 of each `docs/audio-release/2026-10-13-sfx/ui/*.wav`, then copy them to `app/src/main/assets/audio/ui/`.
   - `git rm` the 8 old `sfx_*.mp3`, then `git apply docs/tasks/card-31b-code.patch`.
   - Expected: 436 tests (433 + 3), 0 failed, 6 skipped.
   - Two diagrams then need one line each: `seq_m3_find_it` and `seq_m4_blend_it` drop `sfx_heart_loss_whoosh`.

## Why
Measured on 2026-10-13 (card 31 blueprint):

| Problem | Evidence |
|---|---|
| Uneven loudness | The 8 sound effects ranged from -15.6 to -35.3 dBFS RMS, against Lily's voice at -20 dBFS. |
| The error sound was louder than Lily | The heart-loss whoosh was -17.5 dBFS RMS. |
| Long negative sequence | A wrong answer played pop, whoosh, then Lily's kind words: about 2 s before the kind words. |
| Undo sounded like an error | Taking a Blend It tile back played the "wrong" pop. |
| Dead buzzer | The Blend It buzzer was never played, but was preloaded. |
| No provenance | No sound effect was in a release manifest. |

## Step 0 (Claude): the review batch
1. Download Kenney's "Interface Sounds" 1.0, "UI Audio" and "Music Jingles" packs (CC0 1.0, kenney.nl) to `Documents/playIT-audio-batches/_kenney/`.
2. Build batch `2026-10-13-card31-audio` with `tools/audio/card31_audio_batch.py`: 3 candidates per event, the app's current sound as a reference, and an OK/FIX review page.
3. Each candidate is mono, trimmed, capped (pop 0.25 s, tap 0.15 s, correct 0.6 s, sparkle 0.6 s, unlock 1 s, streak 1 s, fanfare 1.5 s) and levelled to -26 dBFS RMS, peaks at or under -3 dBFS.
4. After the team marks one take OK per sound, `make_release.py tools/audio/releases/2026-10-13-sfx.json` writes `docs/audio-release/2026-10-13-sfx/`, picking the one OK take per sound and recording its SHA-256.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Add: `app/src/main/assets/audio/ui/sfx_<event>.wav` (7 files, from the release)
- Delete: the 8 old `app/src/main/assets/audio/ui/sfx_*.mp3`
- Modify: `data/audio/AudioResolver.kt`
- Modify: `presentation/findit/FindItViewModel.kt`
- Modify: `presentation/blendit/BlendItViewModel.kt`
- Modify: `data/audio/AudioResolverTest.kt`
- Modify: `data/audio/AudioCompletenessCheckTest.kt`
- Modify: `presentation/findit/FindItViewModelTest.kt`
- Modify: `presentation/blendit/BlendItViewModelTest.kt`

## Changes
1. **Events:** `SfxEvent` drops `BLENDIT_BUZZ` and `HEART_LOSS_WHOOSH` and adds `TILE_TAP`.
2. **Paths:** `getSfxPath` resolves the released events (`releasedSfx`) to `audio/ui/sfx_<event>.wav`.
3. **Wrong answer:** in Find It and Blend It it plays `INCORRECT_POP`, then the encouragement line; the heart animation shows the loss.
4. **Undo:** taking a Blend It tile back plays `TILE_TAP`.

## Tests
| Test | Checks |
|---|---|
| `AudioResolverTest.getSfxPath_returnsCorrectPaths` | Every event resolves to its WAV |
| `AudioResolverTest.sfx_noBuzzOrWhoosh_andEveryFileExists` | No buzzer or whoosh event, and every file is on disk |
| `AudioCompletenessCheckTest` | The 7 WAVs |
| `FindItViewModelTest.wrongTap_playsOneSoftPop_thenKindWords` | Exactly [pop, encouragement] |
| `BlendItViewModelTest.wrongSubmit_playsOneSoftPop_thenKindWords` | Exactly [pop, encouragement] |
| `BlendItViewModelTest.removeTile_playsNeutralTap` | The tap, never the pop |

## Commit
`feat(audio): one calm Kenney CC0 sound set, levelled under Lily's voice; a wrong answer plays one soft sound (NFR-AUD-01, NFR-IND-01)`
