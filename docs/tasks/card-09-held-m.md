# Card 09: The approved held /m/ replaces the old Edge-TTS clip (NFR-AUD-01, FR-02)

Status: ready

Runs any time: it touches only `AudioResolver`, its tests and one asset. It doesn't overlap cards 13, 17c, 18 or 26 in code. Card 26 also edits `AudioResolver.kt` and `AudioResolverTest.kt`, so if both run the same night, run 26 first and then make this card's change on top of it.

## Why
- **The teachers' main Round 1 finding:** 2 of 4 flagged letter sounds that sound like the letter name ("ma, ma, ma" instead of a held /m/; PED-08).
- **The new clip:** the user picked the held /m/ on 2026-10-07. It is the Chatterbox "Mmm" (AGENTS.md held-sound decision), stretched to 700 ms.
- **Release:** `docs/audio-release/2026-10-07-m/manifest.json`, clip `ph_m`, SHA-256 `953f906da0d7e76de50094ce3855045e5018f1fec31e3f277483cb5fb9e627d3`.
- **One place to change:** every screen gets letter sounds from `AudioResolver.getPhonemePath` (Hear It, Say It, Find It, Blend It), so this one change reaches all of them.

## Pre-step: copy the released clip
1. Check the SHA-256 of `docs/audio-release/2026-10-07-m/phonemes/ph_m.wav`. Stop if it differs.
2. Copy it unchanged to `app/src/main/assets/audio/phonemes/ph_m.wav`.
3. Keep the old `phoneme_m.mp3`. Other letters still use the MP3 pattern, and a later card removes the old files.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Add: `app/src/main/assets/audio/phonemes/ph_m.wav`
- Modify: `data/audio/AudioResolver.kt` (`getPhonemePath` only)
- Modify: `data/audio/AudioResolverTest.kt` (under `app/src/test/java/com/playit/app/`)

## Changes
1. In `AudioResolver`, add:
```kotlin
    /** Letter sounds released as user-approved held sounds (docs/audio-release); the rest are still the old MP3s. */
    private val releasedPhonemes = setOf("m")
```
   and in `getPhonemePath`, before the current return:
```kotlin
        if (key in releasedPhonemes) return "audio/phonemes/ph_$key.wav"
```
2. Change nothing else.

## Tests
| Test file | Test | Assertion |
|---|---|---|
| AudioResolverTest | `phonemeM_usesReleasedHeldSound` | `getPhonemePath("m") == "audio/phonemes/ph_m.wav"`, and `getPhonemePath("M ")` gives the same |
| AudioResolverTest | `otherPhonemes_keepOldPath` | `getPhonemePath("s") == "audio/phonemes/phoneme_s.mp3"` |
| AudioResolverTest | `releasedPhoneme_fileExists` | `app/src/main/assets/audio/phonemes/ph_m.wav` exists (module dir or repo-root fallback) |

Run `./gradlew testDebugUnitTest`.

## Phone test
In Hear It for M, the sound is a held "mmm". There is no "ma" and no "muh".

## Commit
`feat(audio): approved held /m/ replaces the Edge clip (NFR-AUD-01, FR-02)`

Decisions used: held /m/ is Chatterbox "Mmm" (AGENTS.md; adviser confirmation pending); the user picked the 700 ms take on 2026-10-07.
