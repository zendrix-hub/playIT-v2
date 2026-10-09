# Card 15: Decodable Blend It word list (FR-13)

Status: ready (Tier 3 in Oct 9-10 sprint)

## Why
In Round 1, an expert curriculum review found that 6 of 33 seeded Blend It words violated phonics rules because they required vowel digraphs or diphthongs not yet taught:
- `AIM` (vowel team *ai*)
- `BEE` (long vowel *ee*)
- `TOY` and `BOY` (diphthong *oy*)
- `ZOO` (vowel digraph *oo*)
- `QUIZ` (*qu* /kw/ digraph)

Card 15 purges the 5 non-decodable words and replaces them with vetted, strictly decodable CVC words using only taught short vowels, keeping QUIZ as a documented exception.
Replacement mapping:
1. `AIM` -> `AM` (Group 1: m, s, a, i)
2. `BEE` -> `SUM` (Group 2: b, t, k, u)
3. `TOY` -> `TUB` (Group 3: l, y, o)
4. `BOY` -> `YAM` (Group 5: h, w, c, e)
5. `ZOO` -> `ZIP` (Group 7: x, z)
6. `QUIZ` remains as a documented exception in Group 6.

## Files
All code paths under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Edit: `app/src/main/java/com/playit/app/di/DatabaseModule.kt` (update SQL seeds for blend_it_words)
- Edit: `app/src/main/java/com/playit/app/domain/manager/BlendItWordSelector.kt` (update group words comment/list)
- Edit: `app/src/main/java/com/playit/app/presentation/blendit/BlendItViewModel.kt` (update fallback words)
- Edit: `app/src/test/java/com/playit/app/data/local/DatabaseSeedTest.kt` (or relevant test suite verifying seeded words)

## Changes
1. **`DatabaseModule.kt`**:
   - Replace SQL INSERT for wordId 3 (`AIM` -> `AM`, pattern `'A-M'`, audio `'audio/words/word_am.mp3'`).
   - Replace SQL INSERT for `BEE` -> `SUM` (`'S-U-M'`, audio `'audio/words/word_sum.mp3'`).
   - Replace SQL INSERT for `TOY` -> `TUB` (`'T-U-B'`, audio `'audio/words/word_tub.mp3'`).
   - Replace SQL INSERT for `BOY` -> `YAM` (`'Y-A-M'`, audio `'audio/words/word_yam.mp3'`).
   - Replace SQL INSERT for `ZOO` -> `ZIP` (`'Z-I-P'`, audio `'audio/words/word_zip.mp3'`).
2. **`BlendItWordSelector.kt` & `BlendItViewModel.kt`**:
   - Update fallback list and constraints to reflect `AM`, `SUM`, `TUB`, `YAM`, `ZIP`.
3. **Tests**:
   - Verify word count per group remains intact.
   - Verify all 33 words use only taught sounds for their unlocked chapters.

## Commit
`feat(curriculum): replace non-decodable Blend It words with AM, SUM, TUB, YAM, ZIP (FR-13)`

Requirement: FR-13
Tests run: local | CI only
Decisions used: user decision 2026-10-09 to proceed with vetted replacement words
