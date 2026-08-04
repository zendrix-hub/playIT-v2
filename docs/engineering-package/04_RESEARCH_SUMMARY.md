# 04 — Research Summary

Synthesizes all eight research documents (Personas, Journey, UX, UI, Wireframes, Competitive Analysis, Color Palette, Typography) into one implementation-ready guide. Two of these documents have significant usability caveats — read §0 first.

## 0. Source Reliability Notes (read first)

| Document | Usable? | Note |
|---|---|---|
| `Wireframes.md` | **No — discard entirely.** | Zero references to playIT/Marungko/Grade 1/any module. It is a complete wireframe spec for an unrelated generic enterprise SaaS product (login screens, "Data Analyst" persona, billing settings, analytics data grids). Do not extract anything from it. The real screen/interaction structure lives in the SDD's inline mockups and `10_UI_IMPLEMENTATION_GUIDE.md`. |
| `UI_Research.md` | **Partially.** | Sound general research on Material 3 Expressive and pediatric HCI (kept below and in `03`/`10`) — but it repeatedly and incorrectly frames "playIT" as an *existing* video-player/downloader app being "pivoted" (floating players, ad banners, file management). That framing is discarded; this is a greenfield build. |
| Personas, Journey, Competitive Analysis | Yes, with scope caveats | These three hyper-localize to "Baleno, Masbate," citing specific poverty statistics, DepEd regional programs (CRLA, "Catch-Up Fridays," the 3Bs initiative), and recommend a **Teacher Mode** dashboard. The SRS/Proposal define the target market generically as "Philippine public school learners" and explicitly place a teacher dashboard **out of scope**. Treat the Baleno-specific detail as useful context for asset tone (not a hard requirement) and treat Teacher Mode as a documented future opportunity (§7), not something to build now. |
| UX Research, Color Palette, Typography | Yes | Generally well-sourced and already folded into `01`–`03`. |

## 1. Personas

| | Grade 1 Learner (primary) | Parent/Guardian (secondary) | Teacher *(out of scope — research only)* |
|---|---|---|---|
| Age | 6–7 | 25–55 (incl. grandparents) | Adult |
| Tech literacy | Minimal; basic tap/swipe from casual games | Low–moderate | Varies |
| Working memory | Holds ~3–4 discrete items at once | — | — |
| Attention span | 12–20 minutes focused | — | — |
| Core need | Large targets, zero-reading navigation, immediate multisensory feedback, forgiving errors | Zero setup, no login, plain-language signals, exportable proof for school | Standardized, DepEd-aligned diagnostic data (not in scope) |
| Success looks like | Unlocking the next node; a 3-star round; a streak badge | Understanding at a glance whether the child is improving or struggling | — |
| Top pain point | Cognitive overload from cluttered screens; fear of heart depletion | Hidden costs / required connectivity; jargon-heavy dashboards | — |

**Design implications already reflected in `01`–`03`:** binary dual-channel feedback within the stated latency budgets; strict linear navigation with visible locking; scaffolded error recovery (Blend It's 2-attempt hint); an in-app noise indicator tied to the 40dB threshold.

## 2. User Journey — 10 Stages (condensed)

1. **First launch** — masking Vosk-model/DB init latency (≤5s) behind an animated, high-framerate loading state so a cold boot doesn't read as "broken."
2. **Onboarding** — sandbox, not tutorial; reveal gamification elements (hearts/stars/streaks) progressively, not all at once, to respect limited working memory.
3. **First lesson** — the Say It acoustic-friction point: false ASR rejections from noise or child-voice acoustics are the single biggest early-abandonment risk. → drives the 40dB noise indicator and the "be as quiet as a mouse" pre-check idea (§7).
4. **Daily learning / Blend It** — highest cognitive load in the app (holding a word's audio model in working memory while scanning distractor tiles); the 2-wrong-attempt hint lock is the load-bearing mitigation.
5. **Making mistakes** — loss aversion is real at this age; heart loss must never feel like punishment (reinforces `03`'s feedback-language rules).
6. **Completing activities** — auto-save-on-completion is a trust mechanic, not just a technical nicety; a crash that loses a *completed* sub-level would break trust.
7. **Reward collection** — watch for the overjustification effect; rewards must feel earned, not arbitrary.
8. **Returning after an absence** — "streak anxiety" is a named risk. On return after a broken streak, welcome warmly; never lead with the reset counter.
9. **Parent monitoring** — traffic-light color coding is what makes the dashboard usable for low-literacy-in-edtech parents; raw percentages alone are not enough.
10. **Teacher sync** *(out of scope; PDF export already satisfies the underlying need without building anything teacher-specific)*.

## 3. Competitive Landscape

| App | Strength | Weakness for this context | Takeaway for playIT |
|---|---|---|---|
| Khan Academy Kids | Free, polished, adaptive | ~580MB install, Western phonics sequencing | Keep the APK small; Marungko sequencing is the real differentiator, not a weakness to fix |
| Duolingo ABC | Best-in-class gamification loop, offline caching | Strictly linear, no voice validation | Our voice validation (Vosk) is a genuine point of difference — don't under-sell it in UI |
| Reading Eggs | Rigorous synthetic phonics, detailed reporting | Subscription, requires internet, cluttered UI | Validates our "clean, single-task-per-screen" mandate |
| Lingokids | High production value, strong parent dashboard | Heavy bandwidth/storage, no speech evaluation | Parent dashboard transparency bar to match; production value bar to *not* chase (offline-first beats video-heavy) |
| Starfall | Zero-internet offline packs, low cognitive load | Dated aesthetic | Low cognitive load > flashy — don't over-animate |
| ABCmouse | Massive content volume, strong retention economy | Requires broadband, memory-heavy, complex UI for unguided 6-year-olds | Confirms: no token economy in this scope (see §7) |
| Teach Your Monster to Read | Deep engagement via avatar customization, ad-free | UK-phonics-specific, no voice recognition | Confirms our profile avatar system is on the right track without a full customization economy |

**No competitor combines offline-first + local-language-sequenced phonics + on-device voice evaluation.** That combination is the product's actual moat — worth protecting in every scope decision (i.e., don't cut Vosk integration to save time).

## 4. UX Research — Key Principles Already Reflected in `01`–`03`

- **Mayer's Multimedia Learning principles**, mapped directly to build rules: Multimedia (never text-only, always pair with visual/audio — already a `03` rule) · Coherence (prune decorative elements, no background music during active learning) · Signaling (pulse/highlight the next tap target) · Spatial/Temporal Contiguity (feedback must be adjacent to and synchronized with the element it describes — informs `10`'s component layout) · Segmenting (one challenge per screen, child controls pacing via an explicit "Next").
- **WCAG 2.2 SC 2.5.7 (Dragging Movements):** any drag interaction needs a tap-to-select/tap-to-place alternative. Blend It is already spec'd as tap-based in the SDD (not drag-dependent) — confirm this is preserved exactly as designed; do not "improve" it into a drag-only interaction later.
- **Self-Determination Theory (Autonomy/Competence/Relatedness):** autonomy via avatar choice at profile creation; competence via the heart-recovery mechanic already in scope; relatedness via the mascot's non-judgmental companion framing (already a `03` rule).
- **Spring-physics microinteractions, 200–400ms:** consistent with `03`'s Micro (150–250ms)/Standard (300–500ms) bands — use `MediumBouncy` damping for primary taps, critically-damped for screen transitions, no bounce on color/opacity changes.

## 5. UI Research — Usable Findings (framing issue noted in §0)

- **Age-segmented touch targets:** 3–5yo 80dp+ · **6–8yo 64dp** · 9–12yo 48–64dp. This is the source for `03 §5.3`'s 64dp recommendation.
- **Material 3 Expressive motion:** spring-based (`stiffness`/`dampingRatio`/`visibilityThreshold`), not duration-tweens. Spatial tokens (position/size/shape) use bouncy springs; Effect tokens (color/opacity) use heavily-damped, no-overshoot springs.
- **Loading/empty/error states:** replace indeterminate spinners with morphing-shape loaders for waits under 5s; empty states should demonstrate the next action visually, not say "No videos found"; error states use a mascot gesture + guided recovery, never a raw dialog.
- **Contrast table (for `03 §5.1`'s validation pass):** Normal text 4.5:1 · Large text (≥18pt) 3.0:1 · UI components (buttons/cards) 3.0:1 · disabled elements: convey via shape, not opacity alone.
- **Discarded:** anything about "modernizing playIT from a conventional video player," floating pop-ups, file management systems, or ad banners — not applicable (§0).

## 6. Color Psychology & Typography Rationale (why `03`'s recommendations look the way they do)

### 6.1 Color
Cool, low-arousal hues (blues/greens) measurably improve short-term memory and sustained concentration in 6–7-year-olds versus high-arousal warm hues; red specifically triggers avoidance motivation in evaluative contexts due to years of red-ink grading — this is the empirical basis for the Design System's existing "no red for wrong answers" rule, and for `03 §5.1`'s recommendation to add a red token *only* for true destructive actions. High-chroma accent color should be reserved for signaling (the exact next tap target) and rewards, on strict scarcity — overuse collapses back into visual bombardment.

### 6.2 Typography
Children this age decode letter-by-letter, not via whole-word pattern recognition (the adult "bouma" model) — so letterform clarity matters more than adult-typography conventions like tight tracking. The single-story 'a'/'g' argument, the crowding-effect research, and the 24sp/36-character-line guidance in `Typography_Guide.md` are why `03 §5.2` recommends Lexend/Andika over the shipped Nunito/Poppins for reading-critical text. Button labels must **never use all-caps** — capitalization strips the ascenders/descenders a child uses to recognize word shape.

## 7. Recommended Future Enhancements — Explicitly Out of Current Scope

These ideas recur across the research documents and are genuinely good ideas, but **none of them are authorized by the SRS/Proposal**. List them for stakeholder consideration; do not implement without a scope-change sign-off, since several would materially change the deterministic mechanics defined in `01`:

- **Teacher Mode / CRLA-aligned placement test** — a hidden-gesture teacher dashboard and an onboarding diagnostic that auto-classifies a learner's starting difficulty. Explicitly out of scope per the Proposal.
- **Dynamic Difficulty Adjustment / spaced-repetition scheduling (FSRS-style)** — would silently override the SRS's deterministic distractor and pass-rate rules (§01 §3). Do not add adaptive logic.
- **Culturally localized token economy / avatar customization beyond the existing `AvatarPicker`** — no currency system exists in the SDD's data model; do not add one.
- **"Teacher Sync" local push notifications** — would require notification/alarm permissions not present anywhere in the SRS's interface requirements.
- **OpenDyslexic accessibility font toggle** — reasonable low-cost future accessibility win; needs its own Settings screen, which doesn't currently exist (see `03 §6`, `13`).
- **Pre-mic "be as quiet as a mouse" 3-second ambient check** — this one is *cheap and directly satisfies an existing NFR* (the 40dB noise indicator, SRS 3.3.4); recommend fast-tracking this into MVP scope rather than treating it as a stretch goal, since it's an implementation detail of an already-approved requirement rather than a new feature.
