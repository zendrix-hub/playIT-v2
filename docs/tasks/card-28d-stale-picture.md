# Card 28d: A lesson screen that changes letter shows the new letter's picture, not the last one (NFR-ACC-01)

Type: code
Status: done

Written and run by Claude on 2026-10-11, from the user's phone test of APK E on the A21s. It was first numbered 28c, but agy's card 28c (Hear It visual focus, `0573906`) landed first. APK F (`0573906`) still has this bug.

## Why
- **What the user saw:** after "next letter", the new letter's card still showed the mouse picture from M. This happened on most letters in section 1. Going back to the map and opening S again showed the sun.
- **Cause:** `rememberAssetPainter` in `AssetImage.kt` held the picture in `produceState(initialValue, key1 = key)`.
  - When the key (the picture path) changes, `produceState` restarts its loading coroutine but keeps its old value.
  - The coroutine then returned early because the value was not blank (`if (value !== transparent) return@produceState`). So the previous picture stayed up for good.
  - A screen built fresh from the map started with a clean value, which is why S looked right that way.
- **Why the tests missed it:** every picture test runs with `AssetImageConfig.decodeSynchronously = true`. That path uses `remember(key)` and was already correct. Only the phone's background-decode path had the bug.
- **Where it showed:** any screen whose picture changes while the screen stays up, for example the letter card on "next letter". The fix is in the shared loader, so it covers all of them.

## Changes
- `rememberAssetPainter` keeps the picture in `remember(key) { mutableStateOf(...) }` and loads it in `LaunchedEffect(key)`. A new picture path starts from the cache or blank, never from the previous picture.
- The background decode reuses `decodeAsset`, the same function as the synchronous path, instead of a second copy of it.

## Files
All paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Modify: `presentation/components/AssetImage.kt`
- Modify: `presentation/components/AssetImageLoadTest.kt`

## Tests
| Test file | Test | Assertion |
|---|---|---|
| AssetImageLoadTest | `backgroundLoad_newPictureReplacesTheOldOne` | In background-decode mode, the picture switches from mouse to sun on the same screen. The mouse never shows after the switch, and the sun loads. |

- **The test catches the bug.** Run against the old `AssetImage.kt`, it fails at the second wait: the mouse stays up for the full 15 s.
- **Full suite with the fix:** 404 tests, 0 failures, 6 skipped by design.
- **How it waits:** the test waits with `awaitDecode` (sleep, then idle the main looper) rather than `waitUntil`. Under Robolectric the decode's result only lands when the test idles the main looper.

## Phone test
- Finish a letter in section 1 and tap through to the next letter. The card shows the new letter's own picture (sun for S, apple for A, and so on), not the mouse.
- Repeat for two or three letters in a row.

## Commit
`fix(ui): a lesson screen that changes letter shows the new letter's picture, not the last one (NFR-ACC-01)`

Decisions used: none new. This fixes a defect against the current design.
