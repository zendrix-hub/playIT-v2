# Card 31: Post-midterm polish: a calmer, simpler "minimal Duolingo ABC" (NFR-ACC-01, NFR-ACC-02, NFR-IND-01)

Status: proposed (after the October 14 submission; the team picks which parts run before Round 2 on October 19)

Written by Claude on 2026-10-13 from the card 30 audit of APK G (`89604d7`). Nothing here blocks the submission: APK G is the submitted build, and every change below needs a new APK and a new phone test.

This is a blueprint. Each part (31a to 31f) is small enough for one agy session. The team approves a part before it becomes a `Status: ready` card with the runbook's commit format.

## Why
The design target is "Duolingo ABC, but minimal": chunky tactile gummy controls, squircles, joyful saturated colour, and nothing on screen that does not help the child.

**How the audit was done (2026-10-13):**
- Fresh Roborazzi screenshots of every lesson screen on the 4 test sizes (`:app:recordRoborazziDebug`, screenshot tests).
- The screen code was read, with a scan for text under 16 sp and harsh red.
- The 8 sound-effect files were measured: loudness, length, pitch.

**Overall:** the lesson screens already meet the target's core. Big gummy controls, one main action per screen, the 64 dp floor, no emoji. What is left is extra text, a few mismatched messages, a splash that predates the new Lily, and sounds that are louder than Lily's voice.

| Screen | What works | What to polish (part) |
|---|---|---|
| Splash | One big Start button, short title | Old vector tarsier: no outline, cropped, does not match the redrawn Lily; Start sits on her belly (31a) |
| Map | Trail, nodes, companion animal | On 360x640 the header cards take about half the screen; labels under 16 sp; one harsh-red church dome (31e) |
| Hear It | Letter card and one centred play button (card 28c) | "Sound: /m/" shown twice; the play button turns grey while the sound plays, so it looks switched off (31d) |
| Say It | 4-state mic, mouth cue from miss 2, no hearts | Bubble text cut off on compact; the feedback banner looks like a second button and repeats the bubble (31c, 31d) |
| Find It | 5 large picture cards, hearts | Bubble text cut off on compact (31c); two negative sounds before the kind words (31b) |
| Blend It | Picture, 3 slots, 3 big tiles | Tiles can be dealt already in the answer's order; taking a tile back plays the "wrong" sound (31b, 31f) |
| Letter complete | Lily celebrating, stars, one button | "You mastered Letter M!" also shows with 1 star (31d) |
| New profile | 6 redrawn animal avatars (card 29b) | Three headings say the same thing (31d) |

**Child-design rules:**

| Rule | Result |
|---|---|
| 64 dp touch floor | Pass. The 34 dp speaker badge and the "Tap to listen" chip sit inside the letter card, and the whole card takes the tap. |
| 16 sp text floor | Fails on the map only, listed in 31e and in SDD §2.0. |
| Zero emoji | Pass (`ZeroEmojiPolicyTest`). |
| No harsh red | One background church dome, `0xFFDC2626`, listed in 31e. |

## 31a Splash: Lily waving (P1)
- **Now:** `SplashScreen.kt` draws `HeadspaceTarsierCanvas` (lines 172-353): an orange dome with no dark-brown outline, cropped at the bottom of the screen. The blue Start button is drawn on top of it (lines 151-165).
- **Change:**
  - Show `MascotState.WAVING.assetPath` (`images/mascot/lily_waving.png`, release `2026-10-12-mascot`) with `rememberAssetPainter`, at most 280 dp tall, centred under the tagline.
  - Put the Start button under Lily, not on her. Keep the `splash_start` tag and the 64 dp height.
  - Keep a slow breathing scale, none when reduced motion is on. A tap on Lily may bounce her.
  - Delete the Canvas code and the blink loop (lines 66-78).
- **Tests:**
  - `LayoutMatrixTest` `splash_startVisible` and `splash_startVisible_fontScale13` still pass on the 4 sizes.
  - New `SplashScreenTest`: the picture with content description "Lily waving" is on screen and above Start.
- **Phone:** the first screen shows the same Lily as the lessons, waving, with Start fully below her.

## 31b Feedback sounds: one calm acoustic set (P1)
**Measured on 2026-10-13:**

| Fact | Evidence |
|---|---|
| The Blend It buzzer is never played | `sfx_blendit_buzz.mp3` is only preloaded by `AudioPlayer`. A wrong Blend It answer plays the soft pop, and `BlendItViewModelTest.wrongSubmit_playsSoftPop_notBuzz` already checks that. Children never hear a buzzer today. |
| Loudness is uneven | RMS runs from -15.6 dBFS (fanfare) to -35.3 dBFS (buzz). Lily's voice clips are levelled to -20 dBFS RMS. |
| The error sound is louder than the voice | The heart-loss whoosh is -17.5 dBFS RMS with a -1.5 dBFS peak. |
| Long negative sequence on a wrong tap | Find It (`FindItViewModel.kt:221` and `:224`) and Blend It (`BlendItViewModel.kt:314`) play pop, then whoosh, then the encouragement line. Each file is 1.0 s, so the kind words start about 2 s after the tap, after about 1.1 s of audible error sound. |
| Taking a tile back sounds like an error | Removing a Blend It tile, which is an allowed undo, plays `INCORRECT_POP` (`BlendItViewModel.kt:244`). |
| The sparkle may sound shrill | The recovery sparkle has 99.6% of its energy above 2 kHz (peak near 8.4 kHz), harsh on small phone speakers. |
| No provenance | None of the 8 SFX is in a release manifest; they predate the audio gate. |

**Step 0 (Claude):**
1. Download Kenney's "Interface Sounds" and "UI Audio" packs (CC0 1.0, kenney.nl).
2. Pick 2 to 3 candidates per event:
   - correct (marimba or xylophone)
   - soft "not yet" (wooden or bubble pop)
   - tile tap
   - unlock (gentle chime)
   - celebration
   - streak
3. Convert to 24 kHz mono WAV and trim. Level to about -26 dBFS RMS (6 dB under the voice), with the "not yet" sound the quietest. Peaks at or under -3 dBFS.
4. Lengths: "not yet" pop 250 ms or less, correct 600 ms or less, unlock 1 s or less, fanfare 1.5 s or less.
5. Build an OK/FIX review page in `Documents/playIT-audio-batches/<date>-sfx-kenney/`.
6. After the team's OK, build `docs/audio-release/<date>-sfx/manifest.json` with SHA-256 and license "CC0 1.0 (Kenney, kenney.nl)".

**agy:**
- **Swap the sounds:** copy the released WAVs to `assets/audio/ui/`. In `AudioResolver.getSfxPath`, return `.wav` for released events, like `releasedPhonemes`.
- **Remove the buzzer:** delete `BLENDIT_BUZZ` from `SfxEvent` and delete `sfx_blendit_buzz.mp3`. Update `AudioResolverTest`, `AudioCompletenessCheckTest` and `BlendItViewModelTest`.
- **One soft sound per wrong answer:** a wrong tap or word plays one soft sound, then the encouragement line, with no whoosh. The heart animation already shows the loss. This is a team decision; see "Decisions needed".
- **Neutral undo:** taking a tile back plays the neutral tap, not `INCORRECT_POP`.

**Tests:**
- `FindItViewModelTest`: a wrong tap plays the soft sound, then the line.
- `BlendItViewModelTest`: removing a tile never plays `INCORRECT_POP`.
- `AudioResolverTest`: the released SFX paths, and the files exist.

## 31c Lily's lines fit the bubble (P1)
- **Now:** on 360x640 the bubble cuts two instructions off with an ellipsis (`MascotSpeechHeader.kt:163-164`):
  - **Say It:** "Now it's your turn! Say the whole word clearly into the mic…" is 12 words (`SayItScreen.kt:224`; spoken line `vo_sayit_word_intro_01`).
  - **Find It:** "Can you find all three pictures that start with this soun…" is 11 words (`FindItScreen.kt:100`). The spoken line `vo_findit_intro_01` says "all the pictures", so text and voice also differ.
- **Target:** SPMP R3 asks for spoken prompts of 8 words or fewer.
- **Change:**
  - New lines, each read aloud in full: "Your turn! Say the word." (5 words) and "Find the pictures that start with this sound." (8 words).
  - Claude makes the Kokoro clips (`tools/audio/tutor_script.py`), the team marks them OK, and the clips ship through an audio release. The bubble text then matches the voice word for word.
- **Tests:** `LayoutMatrixTest` on compact shows each instruction without an ellipsis.

## 31d One message at a time (P2)
- **Hear It:**
  - "Sound: /m/" appears both in the bubble while playing (`HearItScreen.kt:175-176`) and on the card (`:214`). The slashes are teacher notation. Keep one, the card's.
  - The play button is disabled while the sound plays, so it turns grey (`HearItScreen.kt:234-236`). Keep its colour and ignore taps; the pulse ring already shows that it is playing.
- **Say It feedback banner** (`SayItScreen.kt:255-290`): a bordered squircle as wide as Next, so it reads as a second button, and it repeats the bubble ("Watch my lips" in both). Show it as plain text under the mic, or drop it when the bubble says the same.
- **New profile:** "New Player Profile" and "Create your player profile" (`NamePromptScreen.kt:115`, `:122`), "Choose Your Avatar" and "Pick your animal companion" (`AvatarPicker.kt:39`, `:46`) and Lily's "Pick your animal friend!" (`NamePromptScreen.kt:135`) all say one thing. Keep Lily's spoken line and the avatars.
- **Letter complete:** "You mastered Letter M!" (`LetterCompleteScreen.kt:93`) also shows with 1 star. "You finished Letter M!" keeps the praise and matches the stars.
- **Tests:**
  - `LayoutMatrixTest` keeps every main action visible.
  - `SayItViewModelTest` keeps the banner text for each tutor action.
  - Screenshot review on the 4 sizes.

## 31e Map: lighter header, 16 sp floor, no harsh red (P2)
- **Header:** on 360x640 the stats card, the greeting and the unit banner fill about half the screen, and only the M and S nodes show (`map_compact` screenshot). Fold the greeting into the stats card, or show it once per session.
- **Text under 16 sp:**

  | File | Size |
  |---|---|
  | `MarungkoGroupBanner.kt:146` | 11 sp |
  | `MarungkoGroupBanner.kt:202` | 12 sp |
  | `TopStatsBar.kt:155` | 12 sp |
  | `TopStatsBar.kt:203` | 13 sp |
  | `MapScreen.kt:929` (the Blend It node badge) | 9.5 sp |
  | `NodeActionPopupDialog.kt` (lines 178, 195, 287, 325) | 11 to 14 sp |
  | `UnitGuidebookDialog.kt` (lines 217, 260, 305, 319, 332, 353) | 11 to 15 sp |

  Raise each to 16 sp, or replace the text with an icon.
- **Red:** the church dome in `ChocolateHillsBackground.kt:454` is `0xFFDC2626` at 70% alpha. Use a muted terracotta from the palette.
- **Tests:**
  - `MapLayoutTest`, `TopStatsBarLayoutTest` and the `LayoutMatrixTest` map tests.
  - A new source scan, like `ZeroEmojiPolicyTest`: fail on `fontSize` under 16 sp outside `presentation/dashboard/`.

## 31f Blend It: tiles never start solved (P2)
- **Now:** `BlendItViewModel.kt:180` deals `word.toCharArray().toList().shuffled()`. The tiles come out in the answer's order about 1 time in 6 for three different letters, and 1 time in 3 for SIS.
- **Change:** reshuffle until the order differs from the word. A word whose letters are all the same is the exception.
- **Tests:** `BlendItViewModelTest`: over 200 deals, the bank never equals the word.

## Not in this card
- New art beyond the released pictures. The pictures for AM, TUB, YAM and ZIP wait for an image round.
- The short /a/, which waits for the team member's takes or a paid ElevenLabs plan.
- Anything that adds decoration. The target is less on screen, not more.

## Decisions needed from the team
1. **Which parts run before Round 2 (October 19).** Claude's suggestion: 31a, 31b and 31c, each phone-tested on the A21s.
2. **Whether a wrong answer drops the whoosh (31b).** The pop is then the only error sound.
3. **The new wording of the two lines (31c), and their Kokoro takes.**
