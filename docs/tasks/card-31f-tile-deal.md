# Card 31f: Blend It never deals a word's tiles already solved (FR-13)

Status: done

Part of card 31 (`card-31-post-midterm-polish.md`), team directive 2026-10-13.

## Why
`setupWordAtIndex` dealt `word.toList().shuffled()`. That gives the answer's own order 1 time in 6 for three different letters, and 1 time in 3 for SIS. The child then only taps the tiles left to right.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Modify: `presentation/blendit/BlendItViewModel.kt`
- Add: `presentation/blendit/BlendItTileDealTest.kt`

## Changes
- New `dealTiles(word, random)` reshuffles until the deal differs from the word.
- A word whose letters are all the same has only one order, so it keeps it.
- `setupWordAtIndex` uses it.

## Tests
| Test | Checks |
|---|---|
| `BlendItTileDealTest.deal_isNeverTheAnswer_andAlwaysTheSameLetters` | 200 deals each of SAM, SIS, AM, TUB, YAM, ZIP, QUIZ and MOM: never the word, always its letters |
| `BlendItTileDealTest.deal_ofOneRepeatedLetter_keepsItsOnlyOrder` | MM and A keep their only order |

## Commit
`fix(blendit): tiles are never dealt in the answer's order (FR-13)`
