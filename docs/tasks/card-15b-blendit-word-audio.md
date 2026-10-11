# Card 15b: Approved word clips for AM, TUB and YAM; Blend It stops being silent on them (FR-13, NFR-AUD-01)

Status: blocked (needs release 2026-10-11-words; see step 0)

It touches `AudioResolver`, the Blend It word seeds and their tests, and adds three assets. Card 28b does not touch any of these files, so the two cards can run in either order.

## Why
- **Phone test of APK D (A21s, 2026-10-11):** Blend It is silent for AM, TUB and YAM. Card 15 replaced AIM, TOY and BOY with these decodable words, but their audio never shipped.
  - `word_am.mp3`, `word_tub.mp3` and `word_yam.mp3` are not in `app/src/main/assets/audio/words/`.
- **The clips exist.** The user marked one Kokoro take per word OK on the review page `2026-10-06-blendwords-card15`.
  - Each word has three takes there: t1 is text at speed 0.95, t2 is text at 0.85, t3 is IPA at 0.95.
  - The takes were made by `tools/audio/audio_lab.py blendwords`, commit 9315da8.
- **Not in this card:** pictures for AM, TUB, YAM and ZIP. They need an image round and an image release. Until then, these words show no picture.

## Step 0: build the release (machine with the batch; it is not on the Windows PC)
The batch lives outside the repo, in `~/Documents/playIT-audio-batches/2026-10-06-blendwords-card15/`, on the WSL machine that made it. The user's verdicts are in that batch's `_review.csv` (exported from the review page). Claude prepared the spec, so building the release is one command:

```bash
python3 tools/audio/make_release.py tools/audio/releases/2026-10-11-words.json \
  --batches ~/Documents/playIT-audio-batches --out docs/audio-release/2026-10-11-words
```

- **The tool picks the takes.** For each word it uses the one take marked OK in the review CSV. It refuses if no take, or more than one, is marked OK, or if the CSV is missing. Nobody picks a take by hand.
- **If it refuses:** stop and write the message to `docs/tasks/QUESTIONS.md`. Do not copy any clip, and do not re-mark the CSV.
- **If it succeeds:** commit only `docs/audio-release/2026-10-11-words/` (the manifest and 3 WAVs) as
  `docs/audio(release): 2026-10-11-words, approved Kokoro takes for AM, TUB and YAM (FR-13)`,
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
    /** Blend It words released as user-approved Kokoro takes (docs/audio-release/2026-10-11-words); the rest stay on the old MP3s. */
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
In Blend It group 1 build AM, and in group 3 build TUB and YAM. Each word is spoken once after the sound-out, in Lily's voice, with the right vowel: not "a.m.", "tube", "tab" or "yum".

## Commit
`feat(audio): approved word clips for AM, TUB and YAM from release 2026-10-11-words (FR-13, NFR-AUD-01)`

Decisions used: audio source Kokoro-82M (AGENTS.md); the user's OK on review page 2026-10-06-blendwords-card15. Teacher audit pending before merge to main.
