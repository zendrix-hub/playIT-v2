# 12 — Testing Strategy

## 1. Test Pyramid for This Project

| Layer | Tool | What it covers |
|---|---|---|
| Unit (JVM, fast) | JUnit + kotlin.test / MockK for fakes | `domain/manager/` classes — the deterministic rules in `01_REQUIREMENTS_SUMMARY.md §3`/`§5`/`§6` |
| Instrumented (emulator/device) | AndroidX Test + Room in-memory DB | DAOs, repository impls, migration correctness, Vosk integration smoke tests |
| UI (Compose) | Compose UI Test | Screen-level interaction flows, navigation gating, accessibility assertions (touch target size, contrast where automatable) |
| Manual QA | Scripted checklist | Acoustic/speech-recognition quality (cannot be meaningfully automated with real children's voices), full end-to-end playtests on low-end hardware |

## 2. Unit Test Coverage — Map Directly to Acceptance Criteria

Every row in `01_REQUIREMENTS_SUMMARY.md §6` (FR-01 through FR-14) gets at least one unit test. Priority list, with the specific edge cases from `01 §5` called out explicitly:

- **`HeartManagerTest`**: starts at 5; -1 per failure; restarts at 3 on depletion; +1 per 3 consecutive correct; **recovery never exceeds the session's starting pool** (`01 §7.5` — this is a recommended rule, not literal SRS text, so the test should document that in its name/comment, e.g. `recoveryCapsAtStartingPool_recommendedRule()`).
- **`StarCalculatorTest`**: 100%+0 hearts lost → 3★; ≥80% → 2★; passed-but-lower → 1★.
- **`BlendItStarThresholdsTest`**: exercises the draft rule from `01 §7.4`, clearly commented as pending stakeholder confirmation.
- **`UnlockManagerTest`**: N+1 locked until N's 3 sublevels complete; first letter (`m`) unlocked by default with no predecessor.
- **`GroupUnlockManagerTest`**: group unlocked only when all 4 member letters complete.
- **`GridGeneratorTest`**: exactly 3 targets + 2 distractors; distractors drawn only from mastered letters; **explicit test for Letter 1 (`m`) using the fallback distractor pool** since no letters are mastered yet (`01 §5`).
- **`SpeechValidatorTest`**: pass/fail against a fixture set of `RecognitionResult`s at varying confidence, asserting behavior around the ≥75% baseline vs. the separate ≥80% pedagogical threshold (`01 §2`) — these are two different numbers feeding two different decisions; a test that conflates them is itself a bug.
- **`StreakTrackerTest`**: milestone badges at 5/10/15/20; 24h-inactivity reset; reset does not touch stars/badges/unlocked letters.
- **`BlendItWordSelectorTest`**: 5 words, ≥1 from current group, remainder from mastered groups; **Group 1 edge case** — assert the selector degrades gracefully (documented behavior, not a crash) when fewer than 5 valid words exist in the cumulative pool (`01 §5`).
- **`LetterStatusCalculatorTest`**: Green ≥80%, Yellow 50–79%, Red <50% or 3+ failed attempts.
- **`RetentionCalculatorTest`**: 7-day window boundary conditions (exactly 7 days old, >7 days old, no attempts in window).

## 3. Instrumented Test Coverage

- Room migration test harness (even though `08_DATABASE_SPEC.md §4` recommends shipping the full v1 schema upfront, still write a migration test scaffold now so v2 isn't the first time this pattern is exercised).
- Cascade-delete test: deleting a `Profile` removes all dependent rows across all 9 profile-scoped tables — this is a correctness-critical, easy-to-regress behavior.
- `VoskRecognizer` smoke test: model loads within the ≤5s budget on a min-spec emulator profile (2GB RAM, API 26); grammar-restriction (dynamic vocabulary scoped to current lesson) actually narrows the recognizer's expected set, not just a cosmetic parameter.
- `PdfExporter` test: generated file opens and contains the expected data points for a fixture profile with known progress.

## 4. Compose UI Tests

- Locked map nodes are not clickable (assert no navigation event fires on tap).
- `BlendIt` slot-filling is achievable via tap-only (no drag required) — direct regression test for the WCAG 2.5.7 requirement flagged in `04 §4`/`10 §5`.
- Touch target size assertion: every interactive node on child-facing screens measures ≥64dp (`03 §5.3`, `10 §3`) — Compose UI Test can assert `SemanticsNode` bounds against this.
- Dashboard arithmetic gate: dashboard content is not composed/visible until the gate resolves correctly (`01 §7.6`).

## 5. Manual QA Script (run before any release candidate)

1. **Cold start on min-spec hardware** — API 26 emulator, 2GB RAM profile: confirm ≤5s to interactive Map.
2. **Full vertical slice, one letter, with real speech** — a human tester (ideally a child-voice sample or a team member imitating pitch/pace) exercises Hear It → Say It → Find It, confirming feedback timing budgets (§`01 §2`) hold in practice, not just in unit-test fixtures.
3. **Noisy-room test** — deliberately exceed 40dB during Say It; confirm the noise indicator fires and feedback framing stays encouraging, not punitive (`03 §6` feedback-language rules).
4. **Full Blend It session including a heart-depletion loss** — confirm session ends with 0 stars, no restart-with-3-hearts (the one asymmetric mechanic in the spec, `01 §7.3`).
5. **6-profile cap** — create 6 profiles, confirm the add-profile affordance disables/hides gracefully (`01 §5`).
6. **Reduced-motion system setting on** — confirm particle/bounce effects degrade to fades even with no in-app toggle present (`10 §6`).
7. **Offline confirmation** — airplane mode, full playthrough including PDF export; confirm zero network calls (inspect via a proxy/`adb` network monitor, not just visual absence of errors).
8. **Content QA** — confirm no `PENDING_SME_REVIEW` sentinel content (`ng`/`ñ`) is user-visible if it hasn't been replaced with approved content yet (`08 §5`).
