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
| Card 03b | *In Progress (Claude)* | *TBD* | *N/A* | *TBD* | *Continuous Execution* |
| Card 20 | *Pending Claude* | *TBD* | *Find It grid & Blend It card visible on all 4 sizes* | *TBD* | *Continuous Execution* |
| Card 22 | *Pending Claude* | *TBD* | *TopStatsBar pills visible with 16-char name* | *TBD* | *Continuous Execution* |

> **CONTINUOUS MODE ACTIVE (User Directive 2026-10-09):** Claude does NOT wait for agy between cards. Claude proceeds continuously: Card 19 $\to$ Card 03b $\to$ Card 20 $\to$ Card 22. agy will conduct the full multi-card review, test run, and APK validation once Claude finishes the sprint run.


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
