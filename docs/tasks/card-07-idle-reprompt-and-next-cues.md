# Card 07: Idle re-prompt and spoken "what to tap next" cues (NFR-IND-01)

Status: done

Revised by Claude on 2026-10-01, before the first run. The code check found three gaps; this text includes their fixes:
1. The Blend It idle test could never pass: its relaxed audio mock never calls back, so `isPlayingPrompt` stays true.
2. The complete screens had no busy flag, so a 10 s cue could cut into their fanfare sequence.
3. Hear It "Next" unlocked when playback *started* (`HearItViewModel.kt:71`, `HearItScreen.kt:121`); `10_UI_IMPLEMENTATION_GUIDE.md` §5 says after one full playback.

The 2026-10-01 audio release now exists, so the pre-step runs.

## Why
A Grade 1 child using PlayIT alone cannot read "Next: Say It", "Complete Lesson" or "Continue to Map". When the child stops interacting, nothing happens. Spec NFR-IND-01 and Table 2 require the mascot to re-prompt after a period of no interaction, and the 10 s value is now decided (user, 2026-10-01). The research (docs/proposals/2026-10-01-learning-ux.md, principle 6) says every instruction should be spoken, and the next action should be shown, not written. This card adds a reusable idle timer and a spoken cue plus a pulse on the button that unlocks the next step. Say It is excluded (card 06 owns `SayItScreen.kt` and `SayItViewModel.kt`). The map pop-up and onboarding are card 07b.

## Pre-step: spoken UI clips (optional)
If `docs/audio-release/2026-10-01/manifest.json` exists:
1. Verify every listed file's SHA-256, as in card 05. The manifest lists 18 clips (UI lines, tutor fragments and `kwslow_mouse`); verify all of them, and stop if any hash differs.
2. Copy only the `ui_hearit_next`, `ui_findit_next` and `ui_complete_next` clips (release files `ui/<id>.wav`), unchanged, into `app/src/main/assets/audio/vo/ui/<id>.wav`. The other clips belong to later cards.

If the manifest does not exist, skip this step. The code below works without the clips, because `AudioPlayer` skips a missing asset. Never copy clips that are not in a release manifest.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- New: `presentation/components/IdleRePrompt.kt` and test `presentation/components/IdleTimerTest.kt`
- Edit: `data/audio/AudioResolver.kt` and test `data/audio/AudioResolverTest.kt`
- Edit: `presentation/hearit/HearItViewModel.kt`, `presentation/hearit/HearItScreen.kt`, test `presentation/hearit/HearItViewModelTest.kt`
- Edit: `presentation/findit/FindItViewModel.kt`, `presentation/findit/FindItScreen.kt`, test `presentation/findit/FindItViewModelTest.kt`
- Edit: `presentation/blendit/BlendItViewModel.kt`, `presentation/blendit/BlendItScreen.kt`, test `presentation/blendit/BlendItViewModelTest.kt`
- Edit: `presentation/lettercomplete/LetterCompleteViewModel.kt`, `presentation/lettercomplete/LetterCompleteScreen.kt`, test `presentation/lettercomplete/LetterCompleteViewModelTest.kt`
- Edit: `presentation/blendit/BlendItCompleteViewModel.kt`, `presentation/blendit/BlendItCompleteScreen.kt`, test `presentation/blendit/BlendItCompleteViewModelTest.kt`
- Add (only if the pre-step ran): `app/src/main/assets/audio/vo/ui/<id>.wav`

## Changes
1. `IdleRePrompt.kt`:
   ```kotlin
   const val IDLE_REPROMPT_MS = 10_000L   // user decision 2026-10-01 (spec Table 2 [proposed] 10 s)
   const val IDLE_MAX_PROMPTS = 3         // then stay quiet until the child touches the screen
   const val IDLE_MAX_POSTPONES = 6       // busy (audio playing) postponements before giving up

   class IdleTimer(
       private val scope: CoroutineScope,
       private val timeoutMs: Long = IDLE_REPROMPT_MS,
       private val maxPrompts: Int = IDLE_MAX_PROMPTS,
       private val maxPostpones: Int = IDLE_MAX_POSTPONES,
       private val isBusy: () -> Boolean = { false },
       private val onIdle: () -> Unit
   ) {
       fun start()  // (re)schedule from now; resets the prompt and postpone counts
       fun touch()  // the child interacted: same as start()
       fun stop()   // cancel; no more prompts
   }

   /** Calls onInteraction for every pointer event in this subtree, without consuming it. */
   fun Modifier.resetsIdle(onInteraction: () -> Unit): Modifier
   ```
   - Behaviour: `timeoutMs` after the last `start`/`touch`:
     - if `isBusy()` is true (audio is playing), reschedule another `timeoutMs` without counting a prompt, but count a postponement; after `maxPostpones` postponements in a row, stop until the next `start`/`touch` (so the timer can never loop forever);
     - otherwise call `onIdle()`, count it, reset the postponement count, and reschedule, until `maxPrompts` prompts have fired.
   - `resetsIdle` uses `pointerInput(Unit) { awaitPointerEventScope { while (true) { awaitPointerEvent(PointerEventPass.Initial); onInteraction() } } }`.
2. `AudioResolver`:
   - Add `fun getUiPath(id: String): String = "audio/vo/ui/$id.wav"`.
   - In `getDevPlaceholderForAsset`, map `audio/vo/ui/` to `DevAudioCategory.VO`, next to the `audio/vo/tutor/` line.
3. **Every ViewModel listed above:**
   - Add a private `IdleTimer` on `viewModelScope`, with `isBusy` = that ViewModel's playing flags (`isPlaying` and/or `isPlayingPrompt`, whichever it has).
   - `LetterCompleteViewModel` and `BlendItCompleteViewModel` have no playing flag yet. Add `private val _isPlaying = MutableStateFlow(false)`. Set it to true just before the completion `playSequence`, and pass a completion callback that sets it back to false: `audioPlayer.playSequence(list) { _isPlaying.value = false }`. Their `isBusy` is `{ _isPlaying.value }`. Without this, the idle cue stops the fanfare and voice sequence mid-way (`AudioPlayer.playAssetAudio` stops the current MediaPlayer, and the sequence callback is lost).
   - Add public `onScreenVisible()` (calls `start()`), `onScreenHidden()` (calls `stop()`) and `onUserInteraction()` (calls `touch()`). Also call `stop()` in `onCleared()`.
   - Do not start the timer in `init`. The screen starts it, so existing ViewModel tests (which never call `onScreenVisible()`) are unaffected by the timer.
   - Add a `nextHighlighted: StateFlow<Boolean>` (false at start) for the pulse.
4. Per screen:

   | ViewModel | On idle (`onIdle`) | When the next step unlocks |
   |---|---|---|
   | HearIt | play `getUiPath("ui_hearit_next")` if `nextHighlighted`, otherwise `playModelingSequence()` | When the first playback finishes, either the modeling sequence (`playModelingSequence`) or the ear-button replay (`playPhonemeSound`), whichever completes first. Use a private `firstPlaybackDone` flag in both `playSequence` completion callbacks: the first time either completes, play `getUiPath("ui_hearit_next")` once and set `nextHighlighted = true`. (Tapping the ear mid-sequence calls `audioPlayer.stop()`, which drops the first sequence's callback, so the replay must be able to unlock too.) |
   | FindIt | `playFindItIntroAudio()` before completion; `getUiPath("ui_findit_next")` after | In the `newFound.size >= 3` branch, play `listOf(sfx, vo, getUiPath("ui_findit_next"))` instead of `listOf(sfx, vo)`, and set `nextHighlighted = true` |
   | BlendIt | `playBlendItIntroAudio()` | — (unchanged) |
   | LetterComplete | `getUiPath("ui_complete_next")` | Append `getUiPath("ui_complete_next")` to the completion `playSequence` list; set `nextHighlighted = true` |
   | BlendItComplete | `getUiPath("ui_complete_next")` | Same as LetterComplete |

5. Screens:
   - Add `DisposableEffect(Unit) { viewModel.onScreenVisible(); onDispose { viewModel.onScreenHidden() } }`.
   - Add `Modifier.resetsIdle { viewModel.onUserInteraction() }` to each screen's root container. On the primary button ("Next: Say It", "Complete Lesson", "Continue to Map"), add `Modifier.breathingPulse(enabled = nextHighlighted)` (from `presentation/components/PulseModifier.kt`, which already respects reduced motion). Do not change button texts, layouts, or any other behaviour.
   - `HearItScreen.kt:121`: change `val isUnlocked = playCount > 0` to `val isUnlocked = nextHighlighted`, so "Next: Say It" stays disabled until one full playback has finished. Leave the other `playCount` uses (mascot state, replay dots) as they are.
6. Do not touch Say It, the map, onboarding, hearts, or the audio sequences beyond the items above.

## Tests
`IdleTimerTest` (runTest with a `StandardTestDispatcher` scope and virtual time):

| Test | Assertion |
|---|---|
| `firesAfterTimeout` | after `start()`, `advanceTimeBy(9_999)` gives 0 calls; `advanceTimeBy(2)` gives 1 |
| `touchResetsTheClock` | `start()`, wait 8 s, `touch()`, wait 8 s: 0 calls; wait 2 s more: 1 |
| `stopsAfterMaxPrompts` | with no touches, after 60 s exactly `IDLE_MAX_PROMPTS` (3) calls |
| `busyPostponesWithoutCounting` | `isBusy` true for the first 25 s, then false: the first call comes at 30 s, and 3 calls still happen in total |
| `stopCancels` | `start()`, `stop()`, wait 60 s: 0 calls |
| `busyForever_givesUp` | `isBusy` always true: `advanceUntilIdle()` returns (no endless loop), and 0 calls |

`AudioResolverTest`: `getUiPath_returnsWavInUiFolder` (`getUiPath("ui_hearit_next") == "audio/vo/ui/ui_hearit_next.wav"`), and `devPlaceholder_mapsUiLines` (it maps to `DevAudioCategory.VO.assetPath`).

ViewModel tests (stub `audioResolver.getUiPath(any())` to answer `"ui/<id>.wav"` in each `setup()`; the resolvers are strict mocks):

| Test file | Test | Assertion |
|---|---|---|
| HearItViewModelTest | `firstSequenceEnd_playsNextCue_andHighlights` | after load (playSequence stub invokes its callback), `playAssetAudio("ui/ui_hearit_next.wav", any())` was called once, and `nextHighlighted` is true |
| HearItViewModelTest | `idle_afterHighlight_playsNextCue` | after load, `onScreenVisible()`, then `advanceTimeBy(10_001)` with no interaction: `playAssetAudio("ui/ui_hearit_next.wav", any())` was called twice in total |
| HearItViewModelTest | `noIdlePrompt_withoutScreenVisible` | after load and `advanceTimeBy(60_000)` without `onScreenVisible()`: the cue was played once (the unlock cue only) |
| HearItViewModelTest | `nextStaysOff_whileFirstSequencePlays` | in this test, before creating the ViewModel, stub `audioPlayer.playSequence(any(), any())` with `just Runs` (no callback); after load, `nextHighlighted` is false and `playAssetAudio("ui/ui_hearit_next.wav", any())` was never called |
| HearItViewModelTest | `replayEnd_alsoUnlocks` | in this test, stub `playSequence` so that only the 2nd call invokes its callback (`var n = 0; every { audioPlayer.playSequence(any(), any()) } answers { n++; if (n >= 2) secondArg<(() -> Unit)?>()?.invoke() }`); after load, call `playPhonemeSound()`: `nextHighlighted` is true, and the cue was played exactly once |
| FindItViewModelTest | `completion_appendsNextCue` | after 3 correct picks, the completion `playSequence` list ends with `"ui/ui_findit_next.wav"`, and `nextHighlighted` is true |
| FindItViewModelTest | `idle_replaysIntro` | as `BlendItViewModelTest.idle_replaysIntro` below, for the Find It intro VO |
| LetterCompleteViewModelTest | `completion_appendsNextCue` | the completion `playSequence` list ends with `"ui/ui_complete_next.wav"` |
| LetterCompleteViewModelTest | `idle_waitsForCompletionSequence` | the relaxed `playSequence` never calls back, so the sequence is still "playing"; after load, `onScreenVisible()`, `advanceTimeBy(30_000)`: `playAssetAudio("ui/ui_complete_next.wav", any())` was never called |
| BlendItCompleteViewModelTest | `completion_appendsNextCue` | the same, for BlendItComplete |
| BlendItCompleteViewModelTest | `idle_waitsForCompletionSequence` | the same, for BlendItComplete |
| BlendItViewModelTest | `idle_replaysIntro` | in this test, before creating the ViewModel, stub `audioPlayer.playAssetAudio(any(), any())` to invoke its callback (`answers { secondArg<(() -> Unit)?>()?.invoke() }`, the pattern in `HearItViewModelTest.kt:40-42`); without it `isPlayingPrompt` never clears and the timer only postpones. After load, `clearMocks(audioPlayer, answers = false)`, then `onScreenVisible()` and `advanceTimeBy(10_001)` with no interaction: `verify(exactly = 1) { audioPlayer.playAssetAudio("vo_path.mp3", any()) }` (the intro VO, `getVoPath` is stubbed to `"vo_path.mp3"`) |

Existing tests that verify exact `playSequence` lists on these screens must be updated to include the appended cue. The complete screens now pass a callback to `playSequence`, and MockK matches an omitted default argument as `null`. So in `LetterCompleteViewModelTest.kt:84` and `BlendItCompleteViewModelTest.kt:65`, change `verify { audioPlayer.playSequence(any()) }` to `verify { audioPlayer.playSequence(any(), any()) }`. All other tests must still pass. Run `./gradlew testDebugUnitTest`.

## Commit
`feat(ui): 10 s idle re-prompt and spoken next-step cues (NFR-IND-01)`

Decisions used: idle re-prompt 10 s (user decision 2026-10-01, AGENTS.md Decisions). IDLE_MAX_PROMPTS = 3 is defined in this card.
