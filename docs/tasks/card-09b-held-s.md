# Card 09b: The approved held /s/ replaces the old Edge-TTS clip (NFR-AUD-01, FR-02)

Status: done

**Not before APK A2 tonight** (the user's decision, 2026-10-08): the meeting build has only the new M. Run it after card 18, or tomorrow.

It touches only `AudioResolver`, its test and one asset, so it doesn't overlap cards 13, 18, 19 or 26b. Card 09 already added `releasedPhonemes`; this card adds one letter to it.

## Why
- **Chapter 1 is m, s, a, i** (SPMP v2.0 §1), and Round 2 tests Chapter 1. The teachers' main Round 1 finding was letter sounds that sound like the letter name or carry a vowel.
- **The new clip:** the user scored the Chatterbox "Sssss." take 5 of 5 on 2026-10-08 and chose to ship it at its own length.
  - The take is `cb_t1_s2` on page `2026-10-01-heldsound-s-a-i`.
  - It is 1.19 s of sound, 0% voiced, so no vowel tail.
- **Release:** `docs/audio-release/2026-10-08-s/manifest.json`, clip `ph_s`, SHA-256 `a3c53911618eb4e8bbfd70ef51e4c0ae080c69b48ec5ed5698f4bca8fa7d8009`.
- **A and I** are not in this card. Their takes were rejected, and a new method is under review.

## Pre-step: copy the released clip
1. Check the SHA-256 of `docs/audio-release/2026-10-08-s/phonemes/ph_s.wav`. Stop if it differs.
2. Copy it unchanged to `app/src/main/assets/audio/phonemes/ph_s.wav`. Keep `phoneme_s.mp3`.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Add: `app/src/main/assets/audio/phonemes/ph_s.wav`
- Modify: `data/audio/AudioResolver.kt` (the `releasedPhonemes` line only)
- Modify: `data/audio/AudioResolverTest.kt` (under `app/src/test/java/com/playit/app/`)

## Changes
1. In `AudioResolver`, change
```kotlin
    private val releasedPhonemes = setOf("m")
```
   to
```kotlin
    private val releasedPhonemes = setOf("m", "s")
```
2. In `AudioResolverTest`:
   - `getPhonemePath_returnsCorrectPathForStandardLetters`: the `"s"` line becomes `assertEquals("audio/phonemes/phoneme_t.mp3", audioResolver.getPhonemePath("t"))`.
   - `otherPhonemes_keepOldPath`: assert `"a"` and `"t"` instead of `"s"` (`audio/phonemes/phoneme_a.mp3`, `audio/phonemes/phoneme_t.mp3`).
   - Add `phonemeS_usesReleasedHeldSound` (below).
   - `releasedPhoneme_fileExists`: also check `audio/phonemes/ph_s.wav` exists and is not empty.
3. Change nothing else.

```kotlin
    @Test
    fun phonemeS_usesReleasedHeldSound() {
        assertEquals("audio/phonemes/ph_s.wav", audioResolver.getPhonemePath("s"))
        assertEquals("audio/phonemes/ph_s.wav", audioResolver.getPhonemePath("S "))
    }
```

## Tests
| Test file | Test | Assertion |
|---|---|---|
| AudioResolverTest | `phonemeS_usesReleasedHeldSound` | `getPhonemePath("s") == "audio/phonemes/ph_s.wav"`, and `getPhonemePath("S ")` gives the same |
| AudioResolverTest | `otherPhonemes_keepOldPath` | `"a"` and `"t"` keep `audio/phonemes/phoneme_<letter>.mp3` |
| AudioResolverTest | `releasedPhoneme_fileExists` | `ph_m.wav` and `ph_s.wav` exist and are not empty |
| AudioResolverTest | `phonemeM_usesReleasedHeldSound` | unchanged; still passes |

`review_card.py` checks the added asset against the release SHA-256. Run `./gradlew testDebugUnitTest`.

## Phone test
- In Hear It for S, the sound is a held hiss, "sss". There is no "es" and no "suh".
- In Blend It, sound out a word with S (for example SAM). Note in the handoff whether the longer /s/ makes the blend feel slow. The teacher audit may ask for a trim.

## Commit
`feat(audio): approved held /s/ replaces the Edge clip (NFR-AUD-01, FR-02)`

Decisions used: held sounds from Chatterbox, picked by ear (AGENTS.md; adviser confirmation pending). The user scored the /s/ take 5 on 2026-10-08 and chose to ship it at 1.19 s.
