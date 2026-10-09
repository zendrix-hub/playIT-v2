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
| Card 19 | *Pending Claude* | *TBD* | *Hear It play & Say It mic visible on all 4 sizes* | *TBD* | *Awaiting implementation* |
| Card 03b | *Pending Claude* | *TBD* | *N/A* | *TBD* | *Awaiting implementation* |
| Card 20 | *Pending Claude* | *TBD* | *Find It grid & Blend It card visible on all 4 sizes* | *TBD* | *Awaiting implementation* |
| Card 22 | *Pending Claude* | *TBD* | *TopStatsBar pills visible with 16-char name* | *TBD* | *Awaiting implementation* |

---

## 3. Final Sprint APK Build & Verification

At the end of the sprint (Saturday night, Oct 10):
1. Assemble final debug APK:
   ```bash
   ./gradlew assembleDebug
   ```
2. Verify output size (~100 MB) and copy to `Documents/playIT-apk/playit-debug-refactor-final.apk`.
3. Confirm 100% zero network calls under airplane mode.
