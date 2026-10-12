# Card 09c: The approved short i replaces the old placeholder clip (NFR-AUD-01, FR-02)

Status: ready (run after card 15b)

**Run after card 15b.** This clip is ElevenLabs Free-plan audio, so it may only ship with the "elevenlabs.io" credit line that card 15b adds to the Parent Zone.

It touches only `AudioResolver`, its test and one asset, like card 09b. It doesn't overlap card 28d.

## Why
- **Phone test of APK 28d (A21s, 2026-10-12):** the user heard the I sound in section 1 as wrong.
  - Only M and S have released sounds (`releasedPhonemes`).
  - I still plays `phoneme_i.mp3`, the old pre-refactor text-to-speech placeholder.
- **Chapter 1 is m, s, a, i** (SPMP v2.0 §1), and Round 2 (Oct 19) tests it. The teachers' main Round 1 finding was letter sounds that sound like the letter name.
- **The clip comes from ElevenLabs Text to Speech** (user choice 2026-10-12; adviser confirmation pending). The team member's Voice Changer takes (`AUDIO_TASK_short-vowels.md`) had not arrived.
  - **Text and voice:** `/ɪ/, /ɪ/, insect.`, with the IPA between slashes so the voice doesn't read the letter name "eye". Model `eleven_v4`, library voice "Lily Wolff", the same voice as AM, TUB and YAM.
  - **The cut:** the first sound was cut from the phrase at its first silence, then cleaned like every other clip (24 kHz WAV, RMS -20 dBFS). It is 0.28 s.
  - **Review:** on page `2026-10-12-vowel-i-eleven` the user marked the three phrase takes and their three cuts OK, then picked `i_phrase_e1_cut` in chat.
- **Release:** `docs/audio-release/2026-10-12-i/manifest.json`, clip `ph_i`, SHA-256 `f88b5b0bf14e0a28c32bb3f99e61e945d6d7989cce6ced24aa35dc6de3030c14`.
- **A is not in this card.** ElevenLabs disabled the account's Free Tier during this batch ("unusual activity"), so no A takes could be made.

## Pre-step: copy the released clip
1. Check the SHA-256 of `docs/audio-release/2026-10-12-i/phonemes/ph_i.wav`. Stop if it differs.
2. Copy it unchanged to `app/src/main/assets/audio/phonemes/ph_i.wav`. Keep `phoneme_i.mp3`.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Add: `app/src/main/assets/audio/phonemes/ph_i.wav`
- Modify: `data/audio/AudioResolver.kt` (the `releasedPhonemes` line only)
- Modify: `data/audio/AudioResolverTest.kt` (under `app/src/test/java/com/playit/app/`)

## Changes
1. In `AudioResolver`, change
```kotlin
    private val releasedPhonemes = setOf("m", "s")
```
   to
```kotlin
    private val releasedPhonemes = setOf("m", "s", "i")
```
2. In `AudioResolverTest`:
   - Add `phonemeI_usesReleasedClip` (below), after `phonemeS_usesReleasedHeldSound`.
   - `releasedPhoneme_fileExists`: also check `audio/phonemes/ph_i.wav` exists and is not empty.
3. Change nothing else.

```kotlin
    @Test
    fun phonemeI_usesReleasedClip() {
        assertEquals("audio/phonemes/ph_i.wav", audioResolver.getPhonemePath("i"))
        assertEquals("audio/phonemes/ph_i.wav", audioResolver.getPhonemePath("I "))
    }
```

## Tests
| Test file | Test | Assertion |
|---|---|---|
| AudioResolverTest | `phonemeI_usesReleasedClip` | `getPhonemePath("i") == "audio/phonemes/ph_i.wav"`, and `getPhonemePath("I ")` gives the same |
| AudioResolverTest | `releasedPhoneme_fileExists` | `ph_m.wav`, `ph_s.wav` and `ph_i.wav` exist and are not empty |
| AudioResolverTest | `otherPhonemes_keepOldPath` | unchanged (`"a"` and `"t"` keep `phoneme_<letter>.mp3`); still passes |

`review_card.py` checks the added asset against the release SHA-256. Run `./gradlew testDebugUnitTest`. Expected after card 15b: 409 tests, 0 failed, 6 skipped.

## Phone test
- In Hear It for I, each sound is the short i of "insect". There is no "eye" and no "ee".
- In Blend It, sound out a word with I (for example ZIP). Note in the handoff whether the short clip sounds clipped next to the held M and S.

## Commit
`feat(audio): approved short i replaces the old placeholder clip (NFR-AUD-01, FR-02)`

Decisions used: short i from ElevenLabs Text to Speech (user choice 2026-10-12; AGENTS.md; adviser confirmation pending). The user picked `i_phrase_e1_cut` on 2026-10-12.
