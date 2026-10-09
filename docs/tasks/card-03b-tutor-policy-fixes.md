# Card 03b: Spoken Say It corrections (FR-03)

Status: done

Runs right after card 19, because both edit `SayItViewModel.kt`. Rewritten by Claude on 2026-10-09; the earlier draft named mp3 paths that don't exist.

## Why
- After a first miss, Say It plays `INCORRECT_POP`, then `tutor/fb_letter_name.wav` or `tutor/fb_added_vowel.wav` or `tutor/fb_listen_again.wav`, then the key word, then "Your turn!".
- **None of those three clips is in `app/src/main/assets/audio/vo/tutor/`.** `AudioPlayer` skips missing files, so the child hears only a pop and the word. They never hear *what* was wrong (Round 1 finding; the agy root-cause note in SESSION_HANDOFF, foil cases 4 and 5).
- The fix:
  - **The clips:** the user approved the correction fragments in release `docs/audio-release/2026-10-01` (verdict OK; teacher audit pending, which is required before merging to `main`). `fb_added_vowel` and `fb_listen_again` were never clip ids.
  - **The sequences:** spec §3.2 fixes them, and `tools/audio/tutor_script.py` `COMPOSE` lists them.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/` unless they start with `app/`.
- Modify: `presentation/sayit/SayItViewModel.kt` (level-1 correction sequences)
- Test: `presentation/sayit/SayItViewModelTest.kt`, `data/audio/AudioResolverTest.kt`
- Asset (from `docs/audio-release/2026-10-01/tutor/`, SHA-256 must match the manifest):
  - `app/src/main/assets/audio/vo/tutor/fb_listen.wav`
  - `app/src/main/assets/audio/vo/tutor/fb_letter_name.wav`
  - `app/src/main/assets/audio/vo/tutor/fb_its_sound_is.wav`
  - `app/src/main/assets/audio/vo/tutor/fb_almost_just.wav`
  - `app/src/main/assets/audio/vo/tutor/fb_no_ah.wav`

## Changes
Level-1 corrections (first miss, `TutorAction.Correct` with `supportLevel == 1`) play these, after `getSfxPath(INCORRECT_POP)`. `ph` is `getPhonemePath(letter)`; `kw` is `getKeyWordPath(word)` and is left out in letter-sound mode (ng, ñ):

| Error type | Sequence after the pop | Spec |
|---|---|---|
| `LETTER_NAME` | `fb_letter_name`, `fb_its_sound_is`, ph, kw, `car_your_turn` | §3.2 "That's the letter's name. Its sound is /m/. mouse. Your turn!" |
| `ADDED_VOWEL` | `fb_almost_just`, ph, `fb_no_ah`, kw, `car_your_turn` | §3.2 "Almost! Just /m/, no 'ah.' mouse. Your turn!" |
| anything else (`OTHER_WORD`, `SUBSTITUTION`, `NO_SPEECH`) | `fb_listen`, ph, kw, `car_your_turn` | Table 2 no speech / §3.2 remodel |

Levels 2 and 3 do not change. Hearts do not change: Say It never removes hearts.

## Tests
| Test file | Test | Assertion |
|---|---|---|
| SayItViewModelTest | `firstLetterNameMiss_playsLetterNameCorrection` (updated) | sequence is `sfx, tutor/fb_letter_name, tutor/fb_its_sound_is, ph, word, tutor/car_your_turn` |
| SayItViewModelTest | `addedVowelError_triggersFbNoAhSpokenCorrection` | "ma" judged ADDED_VOWEL plays `sfx, tutor/fb_almost_just, ph, tutor/fb_no_ah, word, tutor/car_your_turn` |
| SayItViewModelTest | `otherWordMiss_playsListenRemodel` | "cat" plays `sfx, tutor/fb_listen, ph, word, tutor/car_your_turn` |
| SayItViewModelTest | `letterSoundMode_correctionDropsKeyWord` | ng with a miss: no key-word path in the sequence |
| AudioResolverTest | `correctionFragments_fileExists` | the 5 released fragments and `car_your_turn.wav` exist under `src/main/assets/audio/vo/tutor/` |

## Commit
`feat(sayit): spoken corrections for letter names and added vowels (FR-03)`

Requirement: FR-03, NFR-ASR-01
Decisions used: Say It never removes hearts (spec default); the user keeps the current `fb_no_ah` (SESSION_HANDOFF 2026-10-08); clips from release 2026-10-01 (user OK; teacher audit pending before main).
