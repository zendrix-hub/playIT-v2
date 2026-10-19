# Card 15: Decodable Blend It word list (FR-13)

Status: done

Revised by Claude on 2026-10-09. The earlier draft named the wrong groups for some words and left out two things: existing installs never reseed, and the assets are missing.

## Why
In Round 1, an expert curriculum review found seeded Blend It words that need sounds the child has not been taught:
- `AIM`: vowel team *ai*
- `BEE`: long *ee*
- `TOY`, `BOY`: diphthong *oy*
- `ZOO`: digraph *oo*
- `QUIZ`: *qu*. It stays, as a documented exception.

The replacements below use only letters taught up to their group. They keep each word's id, so progress is safe. User decision 2026-10-09.

| Id | Group (letters added) | Old | New |
|---|---|---|---|
| 3 | 1 (m s a i) | AIM | AM |
| 7 | 2 (o b e u) | BEE | SUM |
| 12 | 3 (t k l y) | TOY | TUB |
| 13 | 3 | BOY | YAM |
| 32 | 7 (q v x z) | ZOO | ZIP |

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/` unless they start with `app/`.
- Modify: `di/DatabaseModule.kt`
  - The 33 words move into `BLEND_IT_WORD_SEEDS`, with the swaps above.
  - The list is written with `INSERT OR REPLACE` on every open. The old seed ran only when the phoneme table was not full, so existing installs would never get the new words.
- Modify: `domain/manager/BlendItWordSelector.kt` (comment)
- Modify: `presentation/blendit/BlendItViewModel.kt` (fallback word 3)
- Test: `di/BlendItWordSeedsTest.kt`

## Tests
| Test file | Test | Assertion |
|---|---|---|
| BlendItWordSeedsTest | `thirtyThreeWords_threeThenFivePerGroup` | 33 words, ids 1-33, 3 in group 1 and 5 in each later group |
| BlendItWordSeedsTest | `everyWordUsesOnlyTaughtLetters` | every word except QUIZ uses only letters of its group and the groups before it |
| BlendItWordSeedsTest | `nonDecodableWordsAreGone` | none of AIM, BEE, TOY, BOY, ZOO; all of AM, SUM, TUB, YAM, ZIP |
| BlendItWordSeedsTest | `replacementsKeepTheirSlots` | each new word has the old word's id |

## Assets still missing (not in this card)
| Word | Word audio | Picture |
|---|---|---|
| AM | none | none |
| SUM | `word_sum.mp3` (old voice) | `blendword_sum.png` |
| TUB | none | none |
| YAM | none | none |
| ZIP | `word_zip.mp3` (old voice) | none |

- **Audio:** the user approved Kokoro takes for AM, TUB and YAM in review batch `2026-10-06-blendwords-card15`. There is no release yet, because the release was waiting on the teacher's word list. Next step: Claude builds a release in WSL, then an agy asset card ships it.
- **Pictures:** the four missing pictures need an image round (agy) and an image release.
- **Until then:** these words show no picture, and tapping the card is silent. Blending with the tiles still works.

## Commit
`feat(curriculum): replace non-decodable Blend It words with AM, SUM, TUB, YAM, ZIP (FR-13)`

Decisions used: user decision 2026-10-09 to proceed with the vetted replacement words.
