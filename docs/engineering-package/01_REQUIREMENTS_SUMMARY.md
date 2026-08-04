# 01 — Requirements Summary

Condensed from SRS v2.0. Where the SRS is ambiguous, silent, or internally inconsistent, this document says so explicitly rather than papering over it — see §7.

## 1. Functional Requirements by Module

### Module 1 — "Hear It" (Letter-Sound Acquisition)
- Plays a pre-recorded, native-speaker phoneme model (e.g., "/mmm/ for M like Mouse") + letter animation + example picture.
- **Target: 100% accurate playback**, load time ≤5.0s.
- Replays are unlimited; each replay increments a `replayCount` telemetry metric.
- "Next" advances only after at least one full playback.
- **Post-condition:** unlocks Say It for the same letter.

### Module 2 — "Say It" (Speech Production)
- Pre-condition: Hear It complete for this letter; starts with **5 hearts**.
- Child speaks into the mic; Vosk transcribes locally; compared against the target phoneme's accepted-word list.
- **Exactly 1 correct production passes** — this is not "best of N," it's "first success wins."
- Incorrect attempt: red highlight, corrective audio (e.g., "/m/"), **-1 heart**, retry (max 5 attempts before pool exhausts).
- **Heart depletion (0 hearts):** sub-level restarts, pool reinitializes at **3 hearts** (not 5).
- Feedback must render ≤0.5s after mic deactivation.
- Post-condition: unlocks Find It; writes attempt row(s) to `SayItAttempt`.

### Module 3 — "Find It" (Phoneme Discrimination)
- Pre-condition: Say It passed; heart pool reset to **5**.
- 5-image grid: **exactly 3 target images, 2 distractors sourced from previously mastered letters only**.
- Correct tap: green highlight, success sound, score++. Incorrect tap: red highlight, failure sound, image disables, **-1 heart**.
- Pass condition: all 3 targets found (100% discrimination) before hearts hit 0.
- **Heart depletion:** round restarts, grid reshuffles, pool resets to **3**.
- Visual feedback must render ≤0.3s after tap.
- Post-condition: letter marked complete; triggers star allocation (§3).

### Module 4 — "Blend It" (Word Construction) — *added at SDD v0.2, not in original Proposal*
- Pre-condition: all 4 letters in the current group fully complete (all 3 sublevels each); fresh **5-heart** pool.
- Session = 5 words: at least 1 from the current group, remainder from previously mastered groups.
- Per word: auto-play word audio, render empty letter slots, shuffled tile bank (correct letters + 1–3 distractor letters), tap-to-place / tap-to-remove.
- Submit: correct → success chime, green, advance. Incorrect → buzz, red, **-1 heart**, word resets for retry.
- **Hint rule:** after 2 wrong attempts on the *same word*, one correct tile auto-locks into place.
- **Heart depletion (mid-session):** session ends immediately, **0 stars awarded** — this is a *harder* failure mode than Say It/Find It; there is no 3-heart restart for Blend It. Confirm this asymmetry is intentional before implementation (flagged in §7.3).
- Tap the picture at any time to replay word audio.
- Post-condition: star rating (1–3) computed from words-correct + hearts-lost (see §7.4 — thresholds are not numerically specified in the SRS and need a proposed rule).

### Module 5 — Mastery Progression & Gamification
- **Star rule (per letter, after all 3 sublevels):** 3★ = 100% accuracy + 0 hearts lost · 2★ = ≥80% accuracy · 1★ = passed with lower accuracy/multiple hearts lost.
- **Heart recovery:** +1 heart per 3 consecutive correct answers, across Say It/Find It/Blend It. No maximum cap is specified — recommend capping recovery at the session's starting pool size (5, or 3 post-restart) to avoid unbounded accumulation (flagged in §7.5).
- **Unlock rule:** Letter N+1 locked until all 3 sublevels of Letter N pass. Sequence (28 letters): `m, s, a, i, o, b, e, u, t, k, l, y, n, g, ng, p, r, d, h, w, c, f, j, ñ, q, v, x, z` — see §7.1 for the letter-count conflict this resolves.
- **Group unlock (Blend It):** 7 groups of 4 letters each, in sequence order (Group 1 = m,s,a,i … Group 7 = q,v,x,z). Blend It node for a group unlocks only once all 4 letters in it are fully complete.
- **Streak rule:** any activity in a rolling 24h window increments the streak; badges unlock at 5/10/15/20 consecutive days; 24h of zero activity resets the counter to 0 (progress/stars/badges are **not** affected by a streak reset).

### Module 6 — Parent/Guardian Dashboard
- Accessible from main menu; **no login required** (SRS 6.1) — but see §7.6 for a recommended arithmetic speed-bump at entry.
- Displays, per letter, per profile: accuracy %, attempts, hearts lost, time to complete, stars earned; plus 7-day retention score.
- **Risk color-coding:** Green ≥80% (mastered) · Yellow 50–79% (developing) · Red <50% or 3+ failed attempts (at-risk).
- **Export:** one-tap PDF generation via `PdfDocument`, saved to local device storage.

### Implicit Module — Profile Management *(design-only; not in SRS — see §7.2)*
- Up to 6 child profiles per device (SDD, not SRS). Profile = name + avatar (from a curated `AvatarPicker` set) + independent progress across everything above.
- First launch → `NamePromptScreen` (create profile) or `ProfileSelectScreen` (choose existing) → `MapScreen` scoped to `activeProfileId`.

## 2. Non-Functional Requirements

| Category | Requirement | Source |
|---|---|---|
| Performance | Hear It audio load ≤5.0s | SRS 3.3.1 |
| Performance | Say It feedback ≤0.5s after mic deactivation | SRS 3.3.1 |
| Performance | Find It tap feedback ≤0.3s | SRS 3.3.1 |
| Performance | Reference tone pitch deviation ≤±10 cents | SRS 3.1.1 |
| Security/Privacy | 100% of telemetry/profiles/voice processed & stored locally; nothing transmitted externally | SRS 3.3.2 |
| Security/Privacy | No accounts/logins; destructive actions (reset/delete) gated by a simple arithmetic problem | SRS 3.3.2 |
| Reliability | Vosk baseline detection accuracy ≥75% (engineering target, distinct from the ≥80% pedagogical 2★ threshold) | SRS 3.3.3 |
| Reliability | Auto-save to Room after every sub-level completion | SRS 3.3.3 |
| Environmental | In-app noise indicator; alert if ambient noise >40dB | SRS 3.3.4 |
| **Recommended addition** | APK size budget — target base install <150MB including the bundled Vosk small model, to fit low-storage DepEd-spec tablets | Competitive Analysis research; not in SRS, flagged for sign-off |
| **Recommended addition** | First-launch microphone/voice-data disclosure notice for parents, given RA 10173 (Philippine Data Privacy Act) treats voice as sensitive personal data even when processed transiently in-memory | Competitive Analysis research; **not legal advice — route final copy through counsel** |

## 3. Business Rules (deterministic — implement exactly as stated, no adaptive variants)

1. Letter unlock is strictly linear; no skipping, no teacher/parent override in scope.
2. Distractors in Find It/Blend It are drawn **only** from already-mastered material — never from not-yet-taught letters.
3. Say It requires exactly 1 correct production; it is not graded on a best-of-N or majority basis.
4. Find It requires 100% discrimination (3/3) in a single round to pass; there is no partial pass.
5. Heart pools are **per sub-level session**, not shared across Hear It/Say It/Find It/Blend It — each module that uses hearts starts its own pool (5, or 3 after a restart).
6. Blend It's hint auto-lock is per-word and resets in the next word of the same session.

## 4. Validation Rules

- Profile name: non-empty, reasonable max length (recommend 20 chars) — no format specified in source docs; treat as a UI-layer constraint, not a security boundary.
- Arithmetic gate for destructive actions: single-digit addition/subtraction appropriate for a non-reader parent to solve in seconds — exact problem format not specified; implement as simple, randomized, and never blocking on correctness feedback tone (adult UI).
- Audio/image asset paths referenced in `Phoneme`, `BlendItWord` must resolve at build time — treat a missing asset as a build-breaking error, not a runtime fallback (there is no offline asset-download path).

## 5. Edge Cases to Handle Explicitly

| Edge case | Source status | Recommended handling |
|---|---|---|
| Find It distractors for **Letter 1 (m)** — no letters are "previously mastered" yet | **Gap.** SRS's distractor rule has no defined fallback for the very first letter. | Define a small curated fallback distractor pool (2–4 neutral, unrelated pictures) used only for Letter M's Find It round; retire it permanently once Letter S is mastered and real distractors become available. |
| Blend It Group 1 word bank — only `m, s, a, i` available | **Gap.** Extremely few valid English words exist with 4 letters. | Draft bank: SAM, SIS, AIM (+ reuse across sessions if fewer than 5 unique words are needed — see `19_AUDIO_SCRIPTS.md`). Flag to a reading-curriculum SME before final content lock. |
| Letters **`ng`** and **`ñ`** — Filipino-specific Marungko sounds with no natural English word-initial equivalent | **Gap.** No source document supplies English example words for these two letters. | Do not invent pedagogical content silently. Mark both letters `PENDING SME REVIEW` throughout asset docs (`14`, `15`, `19`) until a reading specialist supplies approved English-phonics example words (candidates: end-sound examples like "sing"/"king" for `ng`; loanword or end-sound treatment for `ñ`). |
| Heart pool reaches 0 mid-Blend-It-session | Handled: session ends, 0 stars, no restart-with-3-hearts (unlike Say It/Find It) | Implement as specified; confirm asymmetry with stakeholders is intentional (§7.3). |
| App killed/crashes mid-sublevel | Handled by auto-save-per-completion rule | Any progress *within* an incomplete sub-level (e.g., mid-Say-It attempt) is lost by design — only completed sub-levels persist. Do not add mid-session autosave beyond what's specified without a scope note. |
| 6 profiles already exist, user taps "add profile" | **Gap.** No stated behavior. | Disable/hide the add-profile affordance at 6 profiles; show a friendly "profiles full" state rather than a hard error. |
| Streak resets at 24h of inactivity | Handled | Reset only the streak counter — never touch stars, unlocked letters, or badges already earned. |

## 6. Acceptance Criteria (SRS §4 Requirements Traceability Matrix, verbatim source of truth)

Use these IDs in commit messages / PR descriptions / test file names so work stays traceable back to the SRS. Each is a single, testable claim — write one automated test class per row where feasible (see `12_TESTING_STRATEGY.md`).

| ID | Requirement | Acceptance target |
|---|---|---|
| FR-01 | Playback Phoneme Audio | Audio load ≤5s; pitch deviation ≤±10 cents |
| FR-02 | Process Speech Input | Vosk latency ≤0.5s; baseline detection accuracy ≥75% |
| FR-03 | Enforce Pass Condition | Exactly 1 correct vocal production passes Say It |
| FR-04 | Heart Depletion & Reset | 5 starting hearts; -1 per error; restart at 3 hearts on depletion |
| FR-05 | Grid Evaluation & Distractors | Exactly 3 targets + 2 distractors, distractors from mastered letters only |
| FR-06 | Star Calculation Rules | 1/2/3-star distribution matches the accuracy/hearts-lost thresholds in §1 Module 5 |
| FR-07 | Heart Recovery System | +1 heart per 3 consecutive correct answers |
| FR-08 | Marungko Lock Progression | Letter N+1 stays locked until Letter N passes all 3 sublevels |
| FR-09 | Learning Streaks & Badges | Badge unlock at 5/10/15/20 days; 24h inactivity resets the counter |
| FR-10 | Dashboard Metrics Display | All stated metrics (incl. retention score) render correctly, fully offline |
| FR-11 | At-Risk Letter Flags | Green/Yellow/Red thresholds apply exactly as specified |
| FR-12 | PDF Report Export | System writes a formatted PDF to local storage on request |

Two IDs worth adding that the SRS matrix doesn't cover (both already justified above): **FR-13** Blend It word construction + hint/heart mechanics (§1 Module 4), and **FR-14** multi-profile create/select/scope (§1, "Implicit Module" — flagged in §7.2 as an SRS gap to formalize).

## 7. Conflicts Identified & Recommended Resolutions

### 7.1 Marungko letter count: 26 vs 28
SRS §1.2 (Scope) lists only 26 letters, omitting `ng` and `ñ`. SRS §3.2 Module 5 (4.2), the SDD's ERD/screen inventory, and the Proposal all use **28 letters**, and the SDD explicitly structures 7 Blend-It checkpoints (7 × 4 = 28). **Resolution: 28 letters is authoritative.** Treat the §1.2 list as a documentation error.

### 7.2 Multi-profile support: SDD-only feature, absent from the SRS
The SDD (and its ERD) define full multi-profile support (up to 6 profiles) as a core feature, added per the team's adviser feedback (see SDD Change History v0.2/v1.0 and Preface). The SRS v2.0 traceability matrix has no corresponding requirement ID, acceptance criteria, or test case. **Resolution: build it — the SDD is the more recent, more detailed authority — but flag to the requirements owner that the SRS should be amended with a formal FR entry (e.g., FR-13) for traceability.**

### 7.3 Blend It heart depletion has no restart-with-3-hearts, unlike every other module
This may be an intentional design decision (Blend It is the "boss level" checkpoint, so failure should cost the whole session) or an oversight (every other module gets a second chance at reduced hearts). **Resolution: implement exactly as SRS Module 4 states (session ends, 0 stars) since it is explicit, unambiguous text — but list this as an open question in `13_MASTER_TASKS.md` for a one-line stakeholder confirmation before final ship**, since it's the one place the mechanic diverges from the pattern established everywhere else.

### 7.4 Blend It star thresholds are not numerically defined
Unlike the letter-level 100%/≥80%/pass thresholds, SRS Module 4's post-condition only says "based on words correct and hearts lost" with no numbers. **Recommended (draft, not yet approved) rule, mirroring the letter-level logic:** 3★ = 5/5 words correct AND 0 hearts lost · 2★ = 5/5 words correct with hearts lost, OR 4/5 correct · 1★ = session completed (didn't hit 0 hearts) with ≤3/5 correct. Implement behind a single named constant (`BlendItStarThresholds`) so it can be tuned without a code change once a stakeholder confirms it.

### 7.5 Heart recovery has no stated maximum
"+1 heart per 3 consecutive correct answers" could in theory push a player's heart count above their session's starting pool. **Resolution: cap recovered hearts at the session's starting value** (5 in a normal session, 3 in a post-restart session) — this is the only reading consistent with the mechanic's purpose (undoing recent damage, not banking a permanent surplus).

### 7.6 Parent Dashboard entry: "no login" (SRS 6.1) vs. an arithmetic gate (research + Proposal step 5)
SRS §6.1 states the dashboard displays immediately with no login. SRS §3.3.2 separately reserves the arithmetic gate for "destructive actions" only. But the Proposal's system workflow and the User Journey research both describe an arithmetic gate specifically at *dashboard entry*, as a child-deterrent. **Resolution: add a lightweight arithmetic gate at the Parent Dashboard entry point.** This does not violate "no login required" — solving `7 + 5 = ?` is a UX speed bump, not authentication, and is consistent with the same mechanism already mandated for destructive actions elsewhere in the same NFR. Recommend the SRS be updated to reflect this at the next revision.

### 7.7 Speech recognition engine: Vosk vs. PocketSphinx
The Proposal (all sections, including the RRL and methodology) specifies **PocketSphinx**. The SRS v2.0, the SDD, and this package's own project brief specify **Vosk**. **Resolution: Vosk is authoritative** — it is the most recent and most specific source (SRS is versioned 2.0 with an explicit revision history citing alignment with "approved proposal details"; the SDD independently confirms Vosk with a pinned version). Treat every PocketSphinx reference in the Proposal as superseded.

### 7.8 Vosk version pin
SRS §3.1.2 says "minimum version 0.3.45"; SDD's tech stack table says "Vosk 0.3.47." **Resolution: target 0.3.47, floor 0.3.45.**
