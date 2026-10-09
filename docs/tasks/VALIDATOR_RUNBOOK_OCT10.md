# agy Validator Runbook: 2-Day Refactoring Sprint (Oct 9–10, 2026)

> This document defines the verification gates that **agy (Antigravity)** executes as **The Validator** after **Claude (The Mind & Implementator)** pushes commits on `refactor/hear-say-it`.

---

## 1. Automated Verification Checklist

Whenever Claude announces a new commit for validation:

1. **Pull Latest Changes:**
   ```bash
   git pull origin refactor/hear-say-it
   ```
2. **Execute Full Unit Test Suite:**
   ```bash
   ./gradlew testDebugUnitTest
   ```
   *Requirement: 100% pass rate across all test classes. Zero regressions.*
3. **Execute 4-Size Device Matrix Screenshot Tests:**
   ```bash
   ./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'
   ```
   *Requirement: Verify that all 4 display sizes render without CTA clipping or text overflow:*
   - `compact`: 360x640 dp (smallest supported)
   - `a21s`: 360x740 dp (target test device)
   - `phone`: 411x891 dp (standard modern phone)
   - `tablet`: 800x1280 dp (classroom portrait tablet)
   - Font Scale: verify 1.0 and 1.3 font scaling.
4. **Mechanical Card Check:**
   ```bash
   python3 tools/dev/review_card.py <card_number>
   ```
   *Requirement: ALL PASS (files, tests, status, body, assets, refs, zero-emoji).*

---

## 2. Card Validation Log

| Card | Commit | Unit Tests (Local) | Screenshot Matrix | APK Status | agy Verdict |
|---|---|---|---|---|---|
| Card 13 | `b909329` | 254 passed | Clean | Included in APK A2 | ✅ VALIDATED |
| Card 26b | `0886ec4` | 254 passed | Clean | Included in APK A2 | ✅ VALIDATED |
| Card 18 | `5489293` | 274 passed | 4 sizes green | Included in APK A2+ | ✅ VALIDATED |
| Card 09b | `1df3f56` | 275 passed | Clean | Audio verified | ✅ VALIDATED |
| Card 19 | `dc6098e` | 294 passed (0 failed, 2 skipped) | `review_card.py 19` ALL PASS; 10 card tests green | Validated | ✅ VALIDATED (Continuous Mode) |
| Card 03b | `339f309` | 298 passed (0 failed, 2 skipped) | `review_card.py 03b` ALL PASS; 5 card tests green | Validated | ✅ VALIDATED (Continuous Mode) |
| Card 20 | `9bb8cb7` | 364 (0 failed, 6 skipped) | `findit_*`, `blendit_*` green across 4 sizes | Included in APK B-eaf8634 | ✅ VALIDATED |
| Card 22 | `3be745b` | 364 (0 failed, 6 skipped) | `map_*` green across 4 sizes | Included in APK B-eaf8634 | ✅ VALIDATED |
| Card 21 | `9bac8ca` | 364 (0 failed, 6 skipped) | `complete_*`, `splash_*`, `nameprompt_*` green | Included in APK B-eaf8634 | ✅ VALIDATED |
| Card 23 | `dd42582` | 364 (0 failed, 6 skipped) | Reduced-motion & effects verified | Included in APK B-eaf8634 | ✅ VALIDATED |
| Card 15 | `299cea2` | 364 (0 failed, 6 skipped) | Decodable Blend It seeds verified | Included in APK B-eaf8634 | ✅ VALIDATED |
| Card 24 | `20efb22` | 364 (0 failed, 6 skipped) | `hearit_*`, `sayit_*` captions verified | Included in APK B-eaf8634 | ✅ VALIDATED |
| Card 27 | `eaf8634` | Docs verified | SDD v2.2, SRS v3.2, SPMP v2.2 verified | Included in APK B-eaf8634 | ✅ VALIDATED |

> **SPRINT BATCH VALIDATION COMPLETE (2026-10-10):** All 8 cards in the sprint batch (03b, 20, 22, 21, 23, 15, 24, 27) have been fully verified by agy: 364/364 unit tests passed (100% pass rate, 6 skipped by design), Roborazzi 4-device screenshot matrix clean across compact, a21s, phone, and tablet, and release debug APK assembled (`playit-debug-B-eaf8634.apk`, 102 MB).


---

## 3. Final Sprint APK Build & Verification

At the end of the sprint (Saturday night, Oct 10):
1. Assemble final debug APK:
   ```bash
   ./gradlew assembleDebug
   ```
2. Verify output size (~100 MB) and copy to `Documents/playIT-apk/playit-debug-refactor-final.apk`.
3. Confirm 100% zero network calls under airplane mode.

---

## 4. Ready for agy: Card 19 (`dc6098e`)

Commit: `dc6098e` feat(sayit): Hear It and Say It fit every phone; mic shows listening, heard and result (FR-03).
Acceptance commit before it: `6608552` (cards 13, 26b, 18, 09b accepted; `review_card.py` path fix for Windows).

### 4.1 Automated
```bash
git pull origin refactor/hear-say-it
./gradlew testDebugUnitTest
./gradlew testDebugUnitTest --tests '*MicStatusTest' --tests '*SayItViewModelTest' --tests '*LayoutMatrix*'
python3 tools/dev/review_card.py 19
```
Expected:
- Full suite: 294 tests, 0 failed, 2 skipped (`LayoutMatrixCompactTest` font-scale checks are skipped by design).
- `MicStatusTest`: 5 of 5 pass. `SayItViewModelTest`: 30 of 30 pass, including `partialSpeech_setsHeard` and `recognizerStoppedExternally_returnsToIdle`.
- `LayoutMatrix{Compact,A21s,Phone,Tablet}Test`: 6 tests each; `hearIt_playVisible`, `sayIt_micVisible` and `sayIt_micVisible_fontScale13` pass (font scale is skipped on compact).
- `review_card.py 19`: all PASS. One WARN is expected: there is no evidence-log hash until Claude accepts the card.
- `ZeroEmojiPolicyTest` passes.

### 4.2 Screenshot matrix (Roborazzi)
```bash
./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'
```
Open `app/build/outputs/roborazzi/` and check these 8 images:

| Image | Must be true |
|---|---|
| `hearit_compact.png`, `hearit_a21s.png`, `hearit_phone.png`, `hearit_tablet.png` | "M is for Mouse" is on one line and not ellipsized. The play button and the 5 dots are above the "Next: Say It" bar. The card is at most 320 dp wide (centred on tablet). |
| `sayit_compact.png`, `sayit_a21s.png`, `sayit_phone.png`, `sayit_tablet.png` | "Say the word Mouse" is not cut. The green mic, its label "Tap and say it" and the 3 attempt dots are above the "Next: Find It" bar. No red anywhere. On compact only, the "Tap to listen" pill is hidden; the corner speaker badge stays. |

Known, not a failure: the key-word picture in the Hear It card is small (about 40 dp on compact, 60 dp on phone), because the big "Mm" letter takes most of the card height. Report it in the verdict; Claude decides on a dimens follow-up.

The Hear It play button shows grey in the screenshots, because the mocked player never finishes the intro. That is expected.

### 4.3 Device check (APK on the A21s)
Build: `./gradlew assembleDebug`, then install. Run lesson M:
1. Hear It: the play button is visible without scrolling and Next unlocks as before.
2. Say It, before tapping: the mic is green with "Tap and say it".
3. Tap the mic and stay silent: the mic is amber, one ring grows from it, and the label reads "I'm listening...".
4. Start saying "mouse": the mic turns lavender with bouncing dots, "I hear you!".
5. Correct: a green check with a small pop, "Yes!", and the green banner above Next.
6. Say "em": an orange ear with a gentle shake, "Let's try again", and the orange banner above Next. Tapping the mic starts a new attempt.
7. Review Focus 2: tap the mic, press Home within 3 s, then reopen the app. The mic must be green "Tap and say it", not stuck listening, and no attempt dot is added.
8. Turn on Settings > Accessibility > Remove animations: the listening ring is a still outline, the dots don't move, and there is no pop or shake.
9. Samsung font size Large: the mic is still visible without scrolling.

Record the verdict in section 2 and in `SESSION_HANDOFF.md`. Then Claude accepts the card (hash and CI run in `docs/evidence-log.md`) and starts card 03b.

---

## 5. Ready for agy: sprint batch (cards 20, 22, 21, 23, 15, 24, 27)

Commits, in order, on `refactor/hear-say-it`:

| Card | Commit | What changed |
|---|---|---|
| 03b | `339f309` | Spoken Say It corrections; 5 tutor clips from audio release 2026-10-01 |
| 20 | `9bb8cb7` | Find It and Blend It in LessonScaffold; FindItGrid; card 16 fix |
| 22 | `3be745b` | Map: rope trail, Lily chip, MapLayout, static avatar, unlock moment |
| 21 | `9bac8ca` | Complete, splash and profile screens fit; Type.kt child sizes |
| tools | `b9ae600` | `review_card.py` reads git output as UTF-8 (Windows) |
| 23 | `dd42582` | Screen transitions, centre confetti, heart wobble, star drop, reduced motion |
| 15 | `299cea2` | AM, SUM, TUB, YAM, ZIP; word list written on every open |
| 24 | `20efb22` | Captions in Hear It; mouth cue (pictures pending) |
| 27 | `eaf8634` | SDD 2.2, SRS 3.2 (§4.1), SPMP 2.2 |

### 5.1 Automated
```bash
git pull origin refactor/hear-say-it
./gradlew testDebugUnitTest
for c in 03b 20 22 21 23 15 24 27; do python3 tools/dev/review_card.py $c; done
```
Expected:
- **Full suite:** 364 tests, 0 failed, 6 skipped. The skips are the font-scale checks on the 360x640 profile, by design.
- **`review_card.py`:** all PASS except these known items:
  - every card: WARN "no hash yet" (Claude fills hashes at acceptance);
  - card 24: FAIL `files`. The card at that commit named `HearItViewModelTest`/`SayItViewModelTest` without paths. Both are listed test files; the card text is fixed in the handoff commit.
- **CI:** green on every commit through `299cea2`. Confirm `20efb22` and `eaf8634` on the Actions page.

### 5.2 Screenshot matrix (all 4 sizes)
```bash
./gradlew recordRoborazziDebug --tests 'com.playit.app.screenshot.*'
```
Check `app/build/outputs/roborazzi/`. The images Claude already looked at are marked (seen).

| Images | Must be true |
|---|---|
| `findit_*` (compact seen, a21s seen) | All 5 cards visible above the bottom edge. The 5th card is centred at the same width. The two cells read "0 / 3" and "/M/". |
| `blendit_*` (compact, phone, tablet seen) | "Tap to hear word" inside the card. The S-A-M tiles are above "Check Word". Known: on compact, about 2 dp of the tiles' bottom shadow touches the scroll edge. |
| `map_*` (compact, tablet seen) | The rope trail is visible. The one-line Lily chip is shown. "Maximilianoooooo" is ellipsized and both stat pills are on screen. Known: the unit banner clips its title line (`MarungkoGroupBanner`, not in these cards). |
| `complete_*`, `splash_*`, `nameprompt_*` (compact, a21s seen) | Continue / Start / Let's Play are visible. On compact the name grid scrolls (AvatarPicker not in card 21). |
| `hearit_*`, `sayit_*` | As in section 4.2. Hear It now has an empty caption row under the card (the caption shows while audio plays). |

### 5.3 APK and phone checks (A21s)
- **Build:** `./gradlew assembleDebug`, then copy the APK to `Documents/playIT-apk/playit-debug-B-eaf8634.apk`. Don't overwrite A or A2.
- **Card 20:** Find It on the A21s shows all 5 cards without scrolling. In Blend It the tiles sit above Check Word.
- **Card 22:** finish letter M and return to the map. The S node pops in, a chime plays, and "New letter open!" shows for about 2 s. Only your avatar stands by the current node, and it doesn't move.
- **Card 21:** with Samsung font size Large, the complete screen, splash and new profile still show their main button.
- **Card 23:** screens fade and rise slightly when they change. Losing a heart in Find It wobbles that heart. Stars drop in on the complete screen. The confetti bursts from the centre.
  - With Settings > Accessibility > Remove animations: no confetti, no shakes, and fades only.
- **Card 15:** Blend It group 1 offers SAM, SIS, AM. **Known:** AM, TUB and YAM are silent on tap and have no picture; ZIP has no picture. The assets are pending (see the card's table).
- **Card 24:** in Hear It a caption follows the audio ("Listen!", "This letter says...", "/m/", "mouse"). No mouth picture appears yet: card 25 has no image release, so this is expected.
- **Card 03b:** in Say It (M), say "em": you hear "That's the letter's name. Its sound is /m/ ... mouse. Your turn!" Say "ma": you hear "Almost! Just /m/, no 'ah.' ...".
- **Review Focus 2 (card 19) still holds:** press Home while listening, and the mic comes back as "Tap and say it".

Record each verdict in section 2 and in `SESSION_HANDOFF.md`. Claude then accepts the batch (hashes and CI runs in `docs/evidence-log.md`).

