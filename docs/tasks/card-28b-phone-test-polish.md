# Card 28b: Phone-test polish: letter-to-mouth pairs, idle wiggle, unit banner, Say It dots (NFR-ACC-01, NFR-ACC-02)

Type: code
Status: done

Written and run by Claude on 2026-10-11 ("run and review", item 3). It follows card 28 (grill decision 8) and the A21s phone test of APK D (`SESSION_HANDOFF.md`, 2026-10-11). The missing Blend It word audio from the same test is card 15b, because it goes through the audio gate.

## Why
1. **Some mouth pictures do not match the sound.** The user saw it on the A21s. Checked against the phonics placement cues used in speech therapy, judged by what a child sees in a mirror for the sound the app teaches (the key word's first sound), five letters were wrong:

   | Letter (key word) | Was | Now | Why |
   |---|---|---|---|
   | o (orange) | round lips | wide open | The short o opens the jaw. The tight round picture is the "oo" of w. |
   | u (umbrella) | round lips | back of mouth | The short u is a relaxed, half-open "uh" with the tongue low and back. That is what `mouth_back.png` shows. |
   | j (jug) | open breath | round lips | /dʒ/ pushes the lips forward and rounds them, with the teeth close. Open breath is h. |
   | r (rabbit) | open breath | tongue up | The tongue tip lifts and curls. Round lips would teach "wabbit", the most common child error with r. |
   | q (queen, /kw/) | back of mouth | round lips | The /k/ is hidden; what shows is the lips rounding for /w/. |

   The other 21 letters were right (m b p lips together; f v teeth on lip; t d n l tongue up; s z x teeth close; k c g back; w round; a wide open; e i y smile; h open breath). A teacher still checks the pairs at Gate 3.
2. **No visual idle cue** (grill decision 8, user decision 2026-10-10). After 10 s idle the app speaks a re-prompt, but a child who cannot read gets no visual pointer to the button.
3. **The unit banner clips its title on every size.** The card was a fixed 72 dp, and the label plus a Lexend Black title need more. "Tarsier Forest Sanctuary" also wraps on 360 dp phones.
4. **Say It's attempt dots hide under the bottom bar on 360x640.** The "Watch my lips" banner makes the pinned bar taller after a miss, and the body has no slack. Card 24b's review recorded this.

## Changes
- **Mouth pairs.** `ArticulationGroup.kt` maps o and u to the open shapes, j and q to round lips, and r to tongue up. Each move is explained in the KDoc. `articulationFor` also trims spaces.
- **Idle wiggle.**
  - `Modifier.idleWiggle(nudge, enabled)` in `PulseModifier.kt` gives one 700 ms shake (±8°, settling to 0) each time `nudge` goes up while `enabled`.
  - A nudge from before the button appeared is not replayed. Reduced motion means no wiggle; the spoken re-prompt is unchanged.
  - The angle is the pure function `wiggleAngleAt(ms)`, so it can be unit-tested.
- **Nudge source.** Every lesson view model with an `IdleTimer` exposes `idleNudge: StateFlow<Int>` and raises it in `onIdle`: Hear It, Find It, Letter Complete and Blend It Complete.
- **Say It idle timer.** Say It had no idle timer. It gets a visual-only one: no audio, because Say It already speaks its prompt ladder.
  - It is busy while listening, while the prompt or model plays, and while Vosk loads.
  - It starts on `ON_START` and stops in `onScreenHidden` and `onCleared`. Touches reset it through `resetsIdle`.
- **What wiggles on each screen:**

  | Screen | Wiggles |
  |---|---|
  | Hear It | the play button until Next lights up, then Next |
  | Say It | the mic while it takes taps (Idle or Try again), then Next once it opens |
  | Find It | Complete Lesson once it lights up. Never a picture, which would give the answer away. |
  | Letter Complete and Blend It Complete | Continue |
  | Blend It | nothing new: its tile bank already bounces |
- **Unit banner.**
  - The face has `heightIn(min = 72.dp)` and grows with its text. The shelf is `matchParentSize()` 6 dp lower, so the 3D edge follows the face.
  - The title gets `lineHeight = 21.sp` and `maxLines = 2`. The content is centred vertically.
- **Say It dots.** On COMPACT, while the feedback banner shows, the word card's height cap goes from 0.8 to 0.62 of `letterCardHeight`, animated. `LetterCard`'s picture has `weight(1f)` and shrinks with it.
- **Tags.** `sayit_attempts`, `sayit_bottom`, `unit_banner_<n>`, `unit_banner_title_<n>`.

## Files
All paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Modify: `domain/model/ArticulationGroup.kt`
- Modify: `presentation/components/PulseModifier.kt` (`idleWiggle`, `wiggleAngleAt`, `WIGGLE_MS`)
- Modify: `presentation/hearit/HearItViewModel.kt`, `presentation/hearit/HearItScreen.kt`
- Modify: `presentation/sayit/SayItViewModel.kt`, `presentation/sayit/SayItScreen.kt`, `presentation/sayit/components/MicButton.kt` (`idleNudge` parameter)
- Modify: `presentation/findit/FindItViewModel.kt`, `presentation/findit/FindItScreen.kt`
- Modify: `presentation/lettercomplete/LetterCompleteViewModel.kt`, `presentation/lettercomplete/LetterCompleteScreen.kt`
- Modify: `presentation/blendit/BlendItCompleteViewModel.kt`, `presentation/blendit/BlendItCompleteScreen.kt`
- Modify: `presentation/map/components/MarungkoGroupBanner.kt`
- Tests: `domain/model/ArticulationGroupTest.kt`, `presentation/components/IdleWiggleTest.kt` (new), `presentation/hearit/HearItViewModelTest.kt`, `presentation/sayit/SayItViewModelTest.kt`, `screenshot/LayoutMatrixTest.kt`

## Tests
| Test file | Test | Assertion |
|---|---|---|
| ArticulationGroupTest | `everyLetterMapsToItsMouthShape` | the full 26-letter table, so any change to a pair is deliberate |
| ArticulationGroupTest | `shortVowelsAreNotRoundLips` | o is wide open, u is back of mouth |
| ArticulationGroupTest | `rDoesNotRoundTheLips` | r is tongue up, not round lips |
| ArticulationGroupTest | `jAndQRoundTheLips` | j and q are round lips |
| ArticulationGroupTest | `caseAndSpacesIgnored` | " M " and "H" map like m and h |
| IdleWiggleTest | `startsAndEndsLevel`, `shakesBothWays`, `staysWithinEightDegrees`, `isShort` | 0° at the start and end, ±8° at the keys, never more than 8°, at most 800 ms |
| HearItViewModelTest | `idle_nudgesTheScreen` | one nudge after 10 s; none after `onScreenHidden` |
| SayItViewModelTest | `idle_tenSecondsWithoutTouch_nudgesOnce` | a touch at 9 s restarts the wait; the nudge comes 10 s after the touch |
| SayItViewModelTest | `idle_noNudgeOnceHidden` | no nudge after `onScreenHidden` |
| SayItViewModelTest | `idle_waitsWhileTheModelPlays` | no nudge while the intro prompt plays |
| LayoutMatrixTest | `sayIt_mouthCueBesideMic` | also `sayit_attempts` above `sayit_bottom` (unclipped) on 4 sizes |
| LayoutMatrixTest | `map_unitBannerTitleFits` | the title's bottom is above the shelf on 4 sizes |

## Phone test
- Say It M: miss twice. The lips-together picture shows left of the mic, and the three dots show in full above the orange banner.
- Hear It O and U: the open-jaw picture shows beside the play button, not round lips. J and Q show round lips; R shows the tongue picture.
- Wait 10 s on Hear It after the first sequence: Next wiggles once and Lily says the re-prompt. Wait on Say It without tapping: the mic wiggles, silently.
- The map's unit banners show the whole title, including "Tarsier Forest Sanctuary" on the A21s.

## Commit
`feat(ux): truer letter-to-mouth pairs, 10 s idle wiggle on the next button, unit banner and Say It dots fit (NFR-ACC-01, NFR-ACC-02)`

Decisions used: idle re-prompt after 10 s (user decision 2026-10-01); grill decision 8, the idle wiggle (user decision 2026-10-10); the A21s phone test of APK D (user, 2026-10-11).
