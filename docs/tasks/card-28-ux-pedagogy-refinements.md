# Card 28: UX and pedagogy refinements from the APK B phone test (FR-01, FR-02, FR-03, FR-13, NFR-ACC-01, NFR-ACC-02)

Type: code
Status: done

## Why
The user tested APK B on a phone and settled 7 changes in a grill session (user decisions 2026-10-10, Option A on every branch):
1. **Hear It ends with "Say it with me!"**, then "Great listening! Tap the big button." The child hears two instructions on a listen-only screen.
2. **Captions distract pre-readers.** Grade 1 children cannot read the carrier sentences, and the text pulls their eyes from the letter and the picture.
3. **Debug text on the child screen.** Debug builds show `Heard: "..." -> errorType (attempt n)` under the Say It banner.
4. **Say It traps the child after the third miss.** The mic still looks tappable and orange, the banner still looks like a retry, and nothing points at Next.
5. **Blend It mixes Filipino and English** ("Pindutin para marinig"), and its sound-out runs the letter sounds together.
6. **Stars stay at 0** on the map and the profile cards.
7. **The map needs two taps** (node, then pop-up) to start the next letter, and Lily's greeting is cut off with "..." on phones.

## Review (Claude, 2026-10-10)
The draft at `cb8e70a` was checked against the code at `80d019e`. What changed from the draft, and why:
1. **Hear It closure is half done already.** `HearItViewModel` plays `ui_hearit_next` and lights the pulsing Next button after the first sequence (`firstSequenceEnd_playsNextCue_andHighlights`). Only the template token changes. Dropping "Say it with me!" amends spec §2.1 Table 3 and SRS FR-02 item 4 (user decision 2026-10-10; the SRS note goes in this card).
2. **Captions and NFR-ACC-01.** Removing them from the child view drops SRS NFR-ACC-01 item 2, which the IT411 package lists as implemented (MVP form Priority #5, SDD §2.0). The card follows the user's decision. The adviser question is in `QUESTIONS.md`. The mouth-shape cue stays as the visual support and gets its pictures from card 25. `CaptionText` and the view model's caption state stay, so an accessibility option can bring captions back without new logic.
3. **The draft's +20% letter card would break the smallest phone.** Removing the caption frees no height, because the row keeps the 72 dp mouth cue. On 360x640 the play button would move below the window (`LayoutMatrixTest.hearIt_playVisible`). The card grows 1.2x on REGULAR and WIDE only.
4. **The third-miss trap is visual, not logic.** `canContinue` is already true after `LeadAndMoveOn`. The mic stays `RESULT_TRY_AGAIN` (orange, tappable) while `startListening()` ignores the taps, the banner stays orange, and Next does not pulse. Fix: a resting mic state `DONE`, a warm banner, and a pulsing Next.
5. **No success chime after three misses.** The draft's "celebratory chime" would reuse the correct-answer signal. Use `sfx_node_unlock_chime` (the map's "something opened" sound), so the correct chime always means "right".
6. **`NEEDS_PRACTICE` in the database is out of scope.** `PlayItDatabase` is version 3 with `fallbackToDestructiveMigration()` and `exportSchema = false`. Any new table or column bumps the version and **wipes every child's progress** on update. The three misses are already saved as `SayItAttempt` rows, so FR-NEW-REC can derive `NEEDS_PRACTICE` when Room v4 lands with real migrations. The TODO stays.
7. **Blend It text.** The card shows two lines, "Pindutin para marinig" above "Tap to hear word". Replacing the first with the second, as the draft says, would print the English line twice. Delete the Filipino line and keep one English line with the speaker icon.
8. **Blend It cadence.** `AudioPlayer` stops the playing clip whenever a new one starts (`playWithMediaPlayer` calls `stopInternal`). So a fixed 400 ms (or the draft's 750 ms) gap cuts the held sounds short (`ph_s.wav` is 1.19 s), and the 600 ms after the whole word lets the praise voice cut the word. Each clip is awaited (at least 750 ms per letter, at most 2 s), then 500 ms with all tiles lit, then the word, awaited, then the chime.
9. **Stars.** One SQL aggregate in `ProfileDao`, the same sums `ReportGenerator` uses, with no schema change and so no migration. `MapViewModel.userStats` and the profile cards read `getAllProfiles()`, a Room `Flow` over `profiles`, `lesson_progress` and `blend_it_progress`, so the counts update live. No DAO test existed; a Robolectric in-memory Room test now covers the SQL.
10. **The 11 sp "companion bubble" is dead code.** `MascotMapDialogueBubble` has no caller, and it had none in APK B either (the call was removed in `c851f4d`). So the 11 sp text the user saw is something else: the group banner's "SECTION 1, UNIT n" label and the pop-up labels are 11 sp. This card deletes the dead function; which text to raise is in `QUESTIONS.md`.
11. **Direct launch must not say "Tap the big button to start."** The pop-up path plays `ui_node_start`. A direct launch plays nothing and lets the lesson's own audio start.
12. **Greeting.** The chip shows "{name}, " plus the biome line, for example "Welcome to Chocolate Hills! Let's master the first phonemes!", which cannot fit on one line. Tapping Lily plays "Let's go! Tap a letter to begin our adventure!" (`map_tarana`), not the biome line. The chip now shows "Hi, {name}!" (21 characters with a 16-character name).
13. **Format.** The draft had no Tests table and no Commit section, which `tools/dev/review_card.py` needs.
14. **Not in this card.** Decision 8 of the grill session (a 10-second idle wiggle on the main button) is not covered by the draft's file list. It is a follow-up card (28b), so this card stays reviewable.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/` unless they start with `app/` or `docs/`.
- Modify: `domain/manager/HearItSequenceBuilder.kt` (`TEMPLATE` ends with `"KEYWORD", "PHONEME"`; the `car_say_it_with_me` asset stays)
- Modify: `presentation/hearit/HearItScreen.kt` (no `CaptionBubble`; letter card 1.2x tall on REGULAR and WIDE)
- Modify: `presentation/components/LetterCard.kt` (optional `height: Dp` parameter, default `d.letterCardHeight`)
- Modify: `presentation/sayit/MicStatus.kt` (`DONE`; `micStatusFor(state, heardSpeech, movedOn = false)`)
- Modify: `presentation/sayit/components/MicButton.kt` (`DONE` look: muted face, mic icon, label "Nice try!", takes no taps)
- Modify: `presentation/sayit/SayItViewModel.kt` (`micStatus` is `DONE` after `LeadAndMoveOn`; the third-miss sequence ends with `sfx_node_unlock_chime`)
- Modify: `presentation/sayit/SayItScreen.kt` (no on-screen "Heard:" line, Logcat only; warm banner after `LeadAndMoveOn`; Next pulses and pops when it unlocks)
- Modify: `domain/manager/SayItFeedbackCopy.kt` (`LeadAndMoveOn` banner "Nice try! Let's keep going!")
- Modify: `presentation/components/BlendItCard.kt` (one line, speaker icon + "Tap to hear word")
- Modify: `presentation/blendit/BlendItViewModel.kt` (sound-out awaits each clip; `ALL_SLOTS` lights every tile for 500 ms before the word)
- Modify: `presentation/blendit/BlendItScreen.kt` (a slot is lit when `highlightedSlotIndex` is its index or `ALL_SLOTS`)
- Modify: `data/local/dao/ProfileDao.kt` (`getAllProfiles()` and `getProfileById()` compute `totalStars`)
- Modify: `presentation/map/MapScreen.kt` (the active node launches on one tap; the chip says `greetingFor(name)`; `MascotMapDialogueBubble` deleted)
- Create: `presentation/map/MapTapRules.kt` (pure Kotlin: `nodeTapAction(isUnlocked, index, activeNodeIndex)` and `greetingFor(name)`)
- Docs: `docs/SRS_v3.0_Refactored.md` (FR-02 item 4 and NFR-ACC-01 item 2 notes), `docs/SDD_v2.0_Refactored.md` (§2.0 rows, §4.1 step 7), `docs/tasks/QUESTIONS.md`
- Test: `domain/manager/HearItSequenceBuilderTest.kt`, `presentation/hearit/HearItViewModelTest.kt`, `presentation/sayit/MicStatusTest.kt`, `presentation/sayit/SayItViewModelTest.kt`, `domain/manager/SayItFeedbackCopyTest.kt`, `presentation/blendit/BlendItViewModelTest.kt`, `presentation/components/BlendItCardLayoutTest.kt`, `presentation/map/MapTapRulesTest.kt` (new), `data/local/dao/ProfileDaoStarsTest.kt` (new, Robolectric with an in-memory Room database)

## Tests
| Test file | Test | Assertion |
|---|---|---|
| HearItSequenceBuilderTest | `build_m_mouse_followsTable3` | the sequence ends with the key word and then the sound; no `car_say_it_with_me` |
| HearItViewModelTest | `load_playsFullModelingSequence` | the played sequence has no `car_say_it_with_me` |
| HearItViewModelTest | `firstSequenceEnd_playsNextCue_andHighlights` | unchanged: `ui_hearit_next` plays and Next lights after the first sequence |
| MicStatusTest | `movedOn_isDone` | `micStatusFor(Incorrect, heard, movedOn = true)` is `DONE` |
| SayItViewModelTest | `thirdMiss_micRestsAndNextUnlocks` | after the third miss: `micStatus` is `DONE`, `canContinue` is true, the sequence ends with the unlock chime and has no correct chime |
| SayItFeedbackCopyTest | `leadAndMoveOn_neverSaysTryAgain` | banner "Nice try! Let's keep going!"; no "try again" |
| BlendItViewModelTest | `soundOut_waitsForEachSound` | the next letter's sound starts only after the previous one completes, and at least 750 ms later |
| BlendItViewModelTest | `soundOut_lightsAllTilesBeforeWord` | `highlightedSlotIndex` is `ALL_SLOTS` for 500 ms, then the word plays |
| BlendItCardLayoutTest | `hintLine_staysInsideCard` | "Tap to hear word" is inside the card; no "Pindutin para marinig" node |
| ProfileDaoStarsTest | `totalStars_sumsLessonAndBlendStars` | 3 + 2 lesson stars and 3 blend stars give 8, the same as `ReportGenerator` |
| ProfileDaoStarsTest | `totalStars_isZeroWithoutProgress` | a new profile has 0 |
| ProfileDaoStarsTest | `totalStars_isPerProfile` | profile B's stars never count for profile A |
| ProfileDaoStarsTest | `getAllProfiles_reEmitsWhenProgressChanges` | the `Flow` emits the new total after a lesson is saved |
| MapTapRulesTest | `activeNode_launchesDirectly` | the unlocked active node is `LAUNCH` |
| MapTapRulesTest | `otherUnlockedNode_opensPopup` | a completed unlocked node is `POPUP` (replay) |
| MapTapRulesTest | `lockedNode_staysLocked` | a locked node is `LOCKED` |
| MapTapRulesTest | `greeting_fitsOneLine` | "Hi, {name}!" with a 16-character name is at most 21 characters; blank name gives "Let's play!" |

All other tests must still pass, including `ZeroEmojiPolicyTest`. Run `./gradlew testDebugUnitTest`, then `./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'`, and look at the `hearit`, `sayit`, `blendit` and `map` PNGs on all 4 sizes before committing.

## Commit
`feat(ux): Hear It ends on the sound, Say It never traps, full Blend It sound-out, live stars, one-tap map (FR-01, FR-02, FR-03, FR-13)`

Decisions used: Hear It ends on the key word and the sound; no captions in the child view; Say It third miss rests the mic and unlocks Next; Blend It in English only, with a slower sound-out; live star totals; one-tap map (user decisions 2026-10-10, grill session). Amends spec §2.1 Table 3 and SRS FR-02 item 4 and NFR-ACC-01 item 2 (adviser confirmation pending).
