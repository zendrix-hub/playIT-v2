# Card 31d: One message at a time on Hear It, Say It, the new profile and the letter's end screen (NFR-IND-01, NFR-ACC-02)

Status: done

Part of card 31 (`card-31-post-midterm-polish.md`), team directive 2026-10-13.

## Why
The audit of APK G found the same message twice on several screens, and a feedback banner that looked like a second button.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Modify: `presentation/hearit/HearItScreen.kt`
- Modify: `presentation/sayit/SayItScreen.kt`
- Modify: `presentation/sayit/components/MicButton.kt`
- Modify: `presentation/profile/NamePromptScreen.kt`
- Modify: `presentation/profile/components/AvatarPicker.kt`
- Modify: `presentation/lettercomplete/LetterCompleteScreen.kt`
- Add: `presentation/hearit/HearItScreenFocusTest.kt`
- Add: `presentation/lettercomplete/LetterCompleteMessageTest.kt`
- Modify: `screenshot/LayoutMatrixTest.kt`

## Changes
1. **Hear It:**
   - While the sound plays, Lily's bubble says "Listen!". "Sound: /m/" stays on the card only.
   - The play button keeps its colour and ignores taps while it plays, instead of turning grey (disabled).
2. **Say It:**
   - The feedback ("Watch my lips", "Great listening!") is now plain text: the one line under the mic, through `MicButton`'s label.
   - The bordered banner in the pinned bar is gone; it looked like a second button and repeated the bubble.
   - The mic label shrinks from 20 sp to fit one line, never below 16 sp.
   - The card no longer shrinks on compact screens when feedback shows.
3. **New profile:** one title ("New Player Profile") and Lily's "Pick your animal friend!". The subtitle and `AvatarPicker`'s two headings said the same thing and are gone.
4. **Letter end screen:** with 1 star, Lily says "You finished Letter M!"; 2 or 3 stars stay "You mastered Letter M!" (`letterCompleteMessage`).

## Tests
| Test | Checks |
|---|---|
| `HearItScreenFocusTest.whilePlaying_soundTextShowsOnce_andLilySaysListen` | One "Sound: /m/" and Lily's "Listen!" while playing |
| `HearItScreenFocusTest.whilePlaying_playButtonKeepsItsColour` | The play button stays enabled while playing |
| `LayoutMatrixTest.sayIt_mouthCueBesideMic` (4 sizes) | "Watch my lips" shows once, under the mic and above the pinned bar |
| `LetterCompleteMessageTest` | 1 star "finished"; 2 and 3 stars "mastered" |

## Commit
`feat(ux): one message at a time on Hear It, Say It, the new profile and the letter's end screen (NFR-IND-01, NFR-ACC-02)`
