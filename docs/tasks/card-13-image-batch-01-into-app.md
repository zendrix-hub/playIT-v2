# Card 13: Batch-1 pictures into the app; "Up" gets its own picture (FR-05)

Status: done

Runs after card 12 is accepted. It edits `GridGenerator.kt`, which card 12 also edited, so find code by name, not by line number.

## Why
Card 08 regenerated 29 pictures. The user picked one per item in round 1 (`docs/assets/briefs/2026-10-01-findit-batch-01/picks.json`). Claude cut out the white backgrounds (`tools/images/cutout.py`), checked them (`tools/images/audit.py`: size, transparent corners, halo, outline colour, padding, colour count, harsh red) and showed them to the user on the 4 app backgrounds (16 §4.3). The pictures the user marked OK are in `docs/image-release/2026-10-07/manifest.json`, built by `tools/images/make_release.py`.

Today the word "Up" shows a submarine: `GridGenerator.kt` maps `"Up"` to `images/pictures/blendword_sub.png`. The release adds `picture_up.png` (a balloon going up).

## Pre-step: copy the released pictures
1. Read `docs/image-release/2026-10-07/manifest.json`. For every entry in `images`, check the SHA-256 of `docs/image-release/2026-10-07/<file>`. Stop if a hash differs.
2. Copy each file unchanged to `app/src/main/assets/<appPath>` (for example `images/pictures/picture_apple.png`). Most replace an existing file with the same name; `picture_up.png` is new.
3. Copy nothing else. Items the user marked FIX are not in the manifest; they keep today's picture until a later release.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Edit: `domain/manager/GridGenerator.kt` and test `domain/manager/GridGeneratorTest.kt`
- New test: `data/PictureAssetsTest.kt` (package `com.playit.app.data`)
- Add or replace: the 29 files listed in the manifest, `app/src/main/assets/images/pictures/*.png` (`review_card.py` checks each against the release SHA-256)

## Changes
1. **`GridGenerator`.** In `pictureBank`, the `"u"` list: `"Up" to "images/pictures/blendword_sub.png"` becomes `"Up" to "images/pictures/picture_up.png"`. Only if `picture_up` is in the manifest; if it isn't, leave the line and say so in the commit body. Change nothing else.
2. Do not change `blendword_sub.png` or the Blend It word SUB (`DatabaseModule.kt`), which correctly shows a submarine.
3. Do not change any layout, size or colour. The new pictures have the same 512 x 512 RGBA format and padding as today's.

## Tests
| Test file | Test | Assertion |
|---|---|---|
| GridGeneratorTest | `up_usesItsOwnPicture` | among 200 grids for target `u`, every item whose `word` is "Up" has `imagePath == "images/pictures/picture_up.png"` (skip this test with the commit-body note if change 1 was skipped) |
| PictureAssetsTest | `everyGridPictureExists` | every `imagePath` that `GridGenerator` can produce (200 grids per bank letter, as in card 12's loop) exists under `src/main/assets/` (module dir, with the `app/src/main/assets/` fallback from the repo root, like `AudioCompletenessCheckTest`) |
| PictureAssetsTest | `releasedPicturesMatchManifest` | for every `docs/image-release/*/manifest.json` (find `docs/` at `../docs` from the module or `docs` from the repo root), each entry's `appPath` exists under the assets folder and its SHA-256 equals the entry's `sha256`. If two releases list the same `appPath`, only the release with the later folder name counts |

All other tests must still pass. Run `./gradlew testDebugUnitTest`.

## Commit
`feat(assets): batch-1 pictures from image release 2026-10-07; "Up" gets its own picture (FR-05)`

Decisions used: images (user decisions 2026-10-01, AGENTS.md Decisions): agy generates, Claude cuts out and checks, the user approves on a review page, and only a release manifest brings a picture into the app.
