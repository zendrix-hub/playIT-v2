# Card 24: Captions and mouth-shape cues (NFR-ACC-01, FR-02, FR-03)

Status: ready after 23 is accepted

**This card's steps, code and test code are in the plan:** `docs/superpowers/plans/2026-10-06-ui-fit-effects-overhaul.md`, section "Task 8". Follow Steps 1-5 there exactly, in order (failing test first). Read the plan's "Global Constraints" before you start; they apply to every card. This card holds what the review checks: the Files list, the Tests table and the commit.

## Why
- In Round 1, 3 of 5 caregivers said there was no support for hearing difficulties (ACC-06).
- The SRS asks for sound captions and articulation cues.
- The spec's attempt-2 correction "watch my lips" has nothing to show.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/` unless they start with `app/`.
- Create: `domain/model/ArticulationGroup.kt` (pure Kotlin)
- Create: `domain/manager/CaptionText.kt` (pure Kotlin)
- Modify: `data/audio/AudioPlayer.kt` (`playSequence(..., onItemStart: ((index: Int, path: String) -> Unit)? = null)`, called before each item plays)
- Modify: `presentation/hearit/HearItViewModel.kt` (`caption: StateFlow<String?>` from `onItemStart`; `articulation: ArticulationGroup`)
- Modify: `presentation/sayit/SayItViewModel.kt` (`showMouthCue: StateFlow<Boolean>`, true from attempt 2 and in LeadAndMoveOn)
- Create: `presentation/components/CaptionBubble.kt`, `presentation/components/ArticulationCue.kt`
- Modify: `presentation/hearit/HearItScreen.kt` and `presentation/sayit/SayItScreen.kt` (show the caption under the letter card; mouth cue beside it, 72 dp, larger, 96 dp, at Say It attempt 2)
- Test: `domain/model/ArticulationGroupTest.kt`, `domain/manager/CaptionTextTest.kt`, `presentation/components/ArticulationCueTest.kt`, additions to `HearItViewModelTest` and `SayItViewModelTest`

## Tests
| Test file | Test | Assertion |
|---|---|---|
| ArticulationGroupTest | `everyLetterHasAGroup` | as written in the plan |
| ArticulationGroupTest | `lipsTogether` | as written in the plan |
| ArticulationGroupTest | `teethOnLip` | as written in the plan |
| CaptionTextTest | `phonemeClip` | as written in the plan |
| CaptionTextTest | `keywordClip` | as written in the plan |
| CaptionTextTest | `carrier` | as written in the plan |
| CaptionTextTest | `pauseHasNoCaption` | as written in the plan |
| ArticulationCueTest | `missingAsset_rendersNothing` | Review Focus 4 |
| HearItViewModelTest | `caption_followsSequence` | as written in the plan |
| SayItViewModelTest | `secondMiss_showsMouthCue` | as written in the plan |

All other tests must still pass, including `ZeroEmojiPolicyTest` and the card 10 screenshot tests. Run `./gradlew testDebugUnitTest`, then `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`, and look at the new PNGs for all 4 sizes before committing.

## Commit
`feat(a11y): sound captions and mouth-shape cues in Hear It and Say It (NFR-ACC-01, FR-03)`

Decisions used: about 9 mouth-shape groups covering all 26 letters, plus captions (user decision 2026-10-06).
