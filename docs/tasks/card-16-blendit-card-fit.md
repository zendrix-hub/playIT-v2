# Card 16: Blend It word card fits its text (UI fix found by screenshot tests)

Status: superseded by card 20 (plan Task 4 includes this exact fix and test; do not run this card)

Runs after card 14 is accepted. It touches only `BlendItCard.kt` and one new test.

## Why
Card 10's screenshot `blendit_group1.png` (CI run 37380965313, 411x891 dp) shows the Blend It word card cutting off its second line, "Tap to hear word".

Cause (code check 2026-10-06): `GummyContainer` (`presentation/components/GummyButton.kt`) draws both its depth band and its face with `Modifier.matchParentSize()`. Its content therefore can't make the container taller, and the card is exactly its `heightIn(min = 176.dp)`. The content of `BlendItCard` needs about 182 dp:

| Part | Height |
|---|---|
| top padding | 12 dp |
| picture box | 96 dp |
| spacer | 6 dp |
| "Pindutin para marinig" row (20 sp) | about 28 dp |
| "Tap to hear word" (18 sp) | about 26 dp |
| text gap | 2 dp |
| bottom padding | 12 dp |

So the last line spills past the card's bottom edge. Don't change `GummyContainer`: every gummy card in the app uses it, and changing how it measures would move other layouts.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Edit: `presentation/components/BlendItCard.kt`
- New test: `presentation/components/BlendItCardLayoutTest.kt`

## Changes
1. In `BlendItCard`:
   - The picture box `Modifier.size(96.dp)` becomes `Modifier.size(80.dp)`.
   - Its gold circle `size(86.dp)` becomes `size(72.dp)`.
   - The `GummyMotionAsset` `size(92.dp)` becomes `size(76.dp)`.
   - The spacer after it, `height(6.dp)`, becomes `height(4.dp)`.

   The new total is about 164 dp, inside 176 dp with room for the 6 dp depth band.
2. Change nothing else: not the texts, font sizes, colours, the `heightIn(min = 176.dp)`, or `GummyContainer`.

## Tests
| Test file | Test | Assertion |
|---|---|---|
| BlendItCardLayoutTest | `secondLine_staysInsideCard` | Robolectric + `createComposeRule`, with the runner, graphics mode and config of card 10's screenshot tests but no capture. `setContent { PlayItTheme { BlendItCard(word = "SAM", isCorrect = false, onReplayAudio = {}, modifier = Modifier.testTag("blendCard")) } }`. Then the bottom of `onNodeWithText("Tap to hear word").getBoundsInRoot()` is at most the bottom of `onNodeWithTag("blendCard").getBoundsInRoot()` minus 6 dp. Tag the card through its existing `modifier` parameter; don't add a test tag in production code. Before the fix, this test must fail (check it once by running it on the old sizes) |

The existing screenshot test `blendIt_group1_firstWord` must still pass; in its PNG, both lines must be inside the card. Run `./gradlew testDebugUnitTest`, and check `recordRoborazziDebug` locally.

## Commit
`fix(ui): Blend It word card fits its text (FR-13)`

Decisions used: none new. The layout bug was found by card 10's screenshot tests.
