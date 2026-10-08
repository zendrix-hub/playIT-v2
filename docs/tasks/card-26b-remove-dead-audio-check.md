# Card 26b: Remove the unused AudioCompletenessCheck class (fix card from the card 26 review)

Status: ready

Runs after card 13. It touches one file that no other open card lists.

## Why
- **Claude's review of card 26 (2026-10-08) found it.** `data/audio/AudioCompletenessCheck.kt` still lists the 19 lesson lines as `audio/ui/vo_*.mp3`, which card 26 deleted. The phoneme list also still expects `phoneme_m.mp3` instead of the released `ph_m.wav`.
- **Nothing calls the class.** No screen, ViewModel or Hilt module uses it, so the wrong paths can't break the app. But a reviewer reading the code would see a "build check" that reports 19 missing files.
- **This was a gap in card 26** (Claude's card didn't list the file), not in agy's work.
- **The real check stays:** `AudioCompletenessCheckTest` reads the asset folders on disk and doesn't use this class. Card 26 already moved it to the new paths.

## Files
All code paths are under `app/src/main/java/com/playit/app/`.
- Delete: `data/audio/AudioCompletenessCheck.kt`

Keep `app/src/test/java/com/playit/app/data/audio/AudioCompletenessCheckTest.kt` exactly as it is.

## Changes
1. Delete `data/audio/AudioCompletenessCheck.kt`.
2. Before committing, run this from the repo root:
   `grep -rn "AudioCompletenessCheck\b\|CompletenessReport\|performCheck" app/src/main`
   It must print nothing. If it prints anything, stop and write it in the handoff; don't edit other files.
3. Change nothing else.

## Tests
| Test file | Test | Assertion |
|---|---|---|
| AudioCompletenessCheckTest | `verifyAllRequiredAudioFilesExistOnDisk` | unchanged; still passes (the 19 lesson wavs and the 2 legacy mp3s are on disk) |
| AudioCompletenessCheckTest | `verifyRequiredAssetCounts` | unchanged; still passes |

This card adds no new test: it only deletes code that no test or screen uses. The existing disk check is the test, and it must stay green. Run `./gradlew testDebugUnitTest`.

## Phone test
None. Nothing visible changes.

## Commit
`refactor(audio): remove the unused AudioCompletenessCheck class (NFR-AUD-01)`

Decisions used: none. This is a fix card from Claude's card 26 review (2026-10-08).
