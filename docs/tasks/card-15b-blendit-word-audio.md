# Card 15b: Approved word clips for AM, TUB and YAM; Blend It stops being silent on them (FR-13, NFR-AUD-01)

Status: blocked (needs release 2026-10-11-words: the user's OK marks and the ElevenLabs plan; see step 0)

It touches `AudioResolver`, the Blend It word seeds and their tests, and adds three assets. Card 28b does not touch any of these files, so the two cards can run in either order.

## Why
- **Phone test of APK D (A21s, 2026-10-11):** Blend It is silent for AM, TUB and YAM. Card 15 replaced AIM, TOY and BOY with these decodable words, but their audio never shipped.
  - `word_am.mp3`, `word_tub.mp3` and `word_yam.mp3` are not in `app/src/main/assets/audio/words/`.
- **The clips come from ElevenLabs** (user decision 2026-10-11; adviser confirmation pending). This amends the Kokoro-only source in `AGENTS.md` for these three words.
  - The first Kokoro batch, `2026-10-06-blendwords-card15`, is on the WSL machine, not the Windows PC.
  - A Kokoro remake on the Windows PC (`2026-10-11-blendwords-card15b`) failed review: every TUB take said "tahbi", because Kokoro adds a vowel after the final b.
  - Batch `2026-10-11-blendwords-card15b-eleven` has four ElevenLabs takes per word (e1 to e4): Text to Speech, model `eleven_v4`, library voice "Lily Wolff", because the account has no "PlayIT Lily" clone. The takes are cleaned like every other clip (24 kHz WAV, RMS -20 dBFS).
- **Not in this card:** pictures for AM, TUB, YAM and ZIP. They need an image round and an image release. Until then, these words show no picture.

## Step 0: build the release (Claude, on the Windows PC)
The batch lives outside the repo, in `Documents/playIT-audio-batches/2026-10-11-blendwords-card15b-eleven/` on the Windows PC. Two facts are missing. The spec marks both `TODO`, so `make_release.py` refuses until they are filled in:
- **The user's verdicts:** one take per word marked OK on the batch's review page, exported as `2026-10-11-blendwords-card15b-eleven_review.csv` into the batch folder.
- **The ElevenLabs plan** of the account, for each clip's `license`.
  - Free: non-commercial only, and the app must credit "elevenlabs.io". A follow-up card then adds the credit line; this card does not.
  - Starter or higher: commercial use is allowed and no credit is needed.

Claude fills in `approvedBy` and `license` in `tools/audio/releases/2026-10-11-words.json`, then runs:

```bash
python tools/audio/make_release.py tools/audio/releases/2026-10-11-words.json \
  --batches C:/Users/Erik/Documents/playIT-audio-batches --out docs/audio-release/2026-10-11-words
```

- **The tool picks the takes.** For each word it uses the one take marked OK in the review CSV. It refuses if no take, or more than one, is marked OK, or if the CSV is missing. Nobody picks a take by hand.
- **If it refuses:** stop and write the message to `docs/tasks/QUESTIONS.md`. Do not copy any clip, and do not re-mark the CSV.
- **If it succeeds:** commit only `docs/audio-release/2026-10-11-words/` (the manifest and 3 WAVs) and the filled-in spec as
  `docs/audio(release): 2026-10-11-words, approved ElevenLabs takes for AM, TUB and YAM (FR-13)`,
  then set this card to `Status: ready`.

## Pre-step: copy the released clips
1. Check the SHA-256 of each file in `docs/audio-release/2026-10-11-words/words/` against `manifest.json`. Stop if any differs.
2. Copy all three unchanged to `app/src/main/assets/audio/words/`: `word_am.wav`, `word_tub.wav`, `word_yam.wav`.

## Files
Code paths are under `app/src/main/java/com/playit/app/`; test paths are under `app/src/test/java/com/playit/app/`.
- Add: `app/src/main/assets/audio/words/word_am.wav`, `word_tub.wav`, `word_yam.wav`
- Modify: `data/audio/AudioResolver.kt` (`getWordPath` and a new `releasedWords` set)
- Modify: `di/DatabaseModule.kt` (the three seed lines for AM, TUB, YAM: audio path only)
- Modify: `presentation/blendit/BlendItViewModel.kt` (the AM line of the fallback list: audio path only)
- Modify: `data/audio/AudioResolverTest.kt`, `di/BlendItWordSeedsTest.kt`

## Changes
1. In `AudioResolver`, above `getWordPath`, add the set, then make `getWordPath` use it:
```kotlin
    /** Blend It words released as user-approved ElevenLabs takes (docs/audio-release/2026-10-11-words); the rest stay on the old MP3s. */
    private val releasedWords = setOf("am", "tub", "yam")

    fun getWordPath(word: String): String {
        val clean = word.lowercase().trim()
        if (clean in releasedWords) return "audio/words/word_$clean.wav"
        return "audio/words/word_$clean.mp3"
    }
```
2. In `BLEND_IT_WORD_SEEDS`, change the audio path of AM, TUB and YAM from `.mp3` to `.wav`, so the database matches what plays. The seeds are written on every open, so no migration is needed. Do the same for the AM line in `BlendItViewModel`'s fallback list.
3. Tests:
   - **AudioResolverTest**, add:
```kotlin
    @Test
    fun releasedWords_useWav() {
        assertEquals("audio/words/word_am.wav", audioResolver.getWordPath("AM"))
        assertEquals("audio/words/word_tub.wav", audioResolver.getWordPath("tub "))
        assertEquals("audio/words/word_yam.wav", audioResolver.getWordPath("Yam"))
        assertEquals("audio/words/word_sam.mp3", audioResolver.getWordPath("SAM"))
    }

    @Test
    fun releasedWords_filesExist() {
        val assetsDir = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.exists() }
        listOf("am", "tub", "yam").forEach {
            val f = File(assetsDir, "audio/words/word_$it.wav")
            assertTrue("missing ${f.path}", f.exists() && f.length() > 0L)
        }
    }
```
   - **BlendItWordSeedsTest**, add:
```kotlin
    @Test fun releasedWordsPointAtTheirWav() {
        val byWord = BLEND_IT_WORD_SEEDS.associateBy { it.word }
        listOf("AM", "TUB", "YAM").forEach {
            assertEquals("audio/words/word_${it.lowercase()}.wav", byWord.getValue(it).audioPath)
        }
    }
```
4. Change nothing else.

## Tests
| Test file | Test | Assertion |
|---|---|---|
| AudioResolverTest | `releasedWords_useWav` | AM, TUB and YAM resolve to `.wav`, ignoring case and spaces; SAM keeps `.mp3` |
| AudioResolverTest | `releasedWords_filesExist` | the three WAVs are in assets and not empty |
| BlendItWordSeedsTest | `releasedWordsPointAtTheirWav` | the three seeds use the `.wav` path |
| BlendItWordSeedsTest | `replacementsKeepTheirSlots` | unchanged; ZIP still `.mp3` |

`review_card.py 15b` checks the added assets against the release SHA-256. Run `./gradlew testDebugUnitTest`.

## Phone test
In Blend It group 1 build AM, and in group 3 build TUB and YAM. Each word is spoken once after the sound-out, in a voice close to Lily's, with the right vowel: not "a.m.", "tube", "tab" or "yum".

## Commit
`feat(audio): approved word clips for AM, TUB and YAM from release 2026-10-11-words (FR-13, NFR-AUD-01)`

Decisions used: ElevenLabs for the AM, TUB and YAM word clips (user decision 2026-10-11, amends the Kokoro-only source; adviser confirmation pending); the user's OK on review page 2026-10-11-blendwords-card15b-eleven. Teacher audit pending before merge to main.
