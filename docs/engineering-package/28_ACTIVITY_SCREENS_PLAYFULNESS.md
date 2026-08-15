# 28 — Activity Screens: Making Exercises Feel Like Play

> Covers `HearItScreen`, `SayItScreen`, `FindItScreen`, `BlendItScreen`. Per `23`'s own boundary, restated here: **this is a visual/motion layer only** — hearts, gating thresholds, correctness logic, attempt limits, and hint timing (all defined in `01_REQUIREMENTS_SUMMARY.md`) are untouched. Where a "make it feel like play" idea would require changing when the app reveals correctness or how gating works, it's flagged rather than assumed.

## ⚠️ Sync note — FindIt's documented feedback color contradicts the app's own hard rule

`10_UI_IMPLEMENTATION_GUIDE.md §5`'s `FindItScreen` entry currently reads: *"`PictureCard` (tap → green/red flash)."* That directly contradicts the standing hard rule from `03 §2`/`03 §6`: no red, ever, for incorrect answers — `GentleCorrectionOrange` only. This spec corrects it below (§3): correct = `GrowthGreen`, incorrect = `GentleCorrectionOrange`. Antigravity should treat `10 §5`'s "red" as a documentation error to fix at the source, not a screen-specific exception — `SayItScreen`'s `WordFeedbackCard`-equivalent state coloring already gets this right (`GrowthGreen`/`GentleCorrectionOrange`, no red anywhere in the actual `.kt` file), so `FindIt` should simply match the pattern already correctly implemented elsewhere in the codebase.

---

## HearIt Screen

**Layout**: `AnimatedLetterCard` becomes a `GummyStaticContainer` (28dp corners, per `23 §3`'s Learning Card radius) rather than a bare card — it's not tappable itself (only `PlayButton` is), so the static variant is correct, matching the same reasoning `DockedMascotWithBubble` already uses for its own non-tappable elements.

**Animation on audio playback**: when `PlayButton` is tapped, the letter card pulses in rhythm with playback rather than sitting static — scale 100%→106%→100%, two pulses across the audio duration (roughly one pulse per syllable/beat of the phoneme sound, so [CHECK WITH AGENT: exact timing should sync to actual audio-clip duration once available, since phoneme clips vary in length]). `PlayButton` itself becomes a `GummyIconButton` (matching `SayIt`'s mic-button treatment) rather than a plain icon button, with the highlight-sheen refinement from `25 §4` since this is the screen's primary CTA.

**ReplayCounter**: upgrade the existing dot indicators to small filled/empty gummy dots (8–10dp, `LearningBlue` filled / `BorderColor` empty) rather than plain Material dots — each dot that becomes "used" gets a brief scale-pop (1.0→1.3→1.0, ~200ms) at the moment of use, giving the counter a tactile feel instead of a silent tally.

**Mascot**: `POINTING` toward `PlayButton` on first screen arrival (per `26 §4`), easing to `IDLE` after first play or ~1.5s. After ≥1 full playback, mascot shifts to `ENCOURAGING`-adjacent energy with the "Ready for the next step?" line (`26 §6`) as `NextButton` becomes enabled. Give `NextButton`'s enable transition a small unlock-pop (scale 0.95→1.05→1.0, ~250ms `MediumBouncy`) instead of an instant flat enable, so the moment the child "earns" the next screen has a beat of its own.

---

## SayIt Screen

Already the most fully built activity screen in the current codebase (`SayItScreen.kt`). Refinements build directly on what's there rather than replacing it.

**Mic button**: `GummyIconButton` at 112dp is already correct scale and already pulses via `breathingPulse` while listening. Add a **ripple/wave effect** while `isListening`: 2–3 concentric circles expanding outward from the button (radius 56dp→90dp, stroke-only, `LearningBlue` fading alpha 0.4→0), staggered ~400ms apart, looping for the duration of the hold. Pure `Canvas`, layered behind the `GummyIconButton` so it doesn't interfere with the button's own press/depth visuals.

**Big letter card**: the existing 220dp `GummyContainer` with its per-state face/shadow/text-color triple (`SayItScreen.kt`'s `cardFace`/`cardShadow`/`cardTextColor` block) is already exactly the right pattern — state-paired solid tokens rather than a translucent tint, already documented in-file as intentional. No change to that logic. One addition: on the `Correct` state, layer in the squash effect already available via `GummyContainer`'s `isSquashed` parameter (currently wired on the `Next` button but not on the letter card itself) — trigger `isSquashed = true` briefly on the card the instant `state is SayItState.Correct` fires, syncing the card's own "win" reaction with the mascot's `CELEBRATING` cut and the reward chime.

**Heart loss animation**: `HeartBar` currently swaps a heart's alpha instantly between 1.0 (filled) and 0.35 (empty) with no transition. Add an `animateFloatAsState`-driven transition on the specific heart index that's losing its fill: a quick pulse-down (scale 1.0→1.25→0.85→1.0 over ~350ms) combined with the alpha fade to 0.35, so the specific heart being lost is visually legible rather than the whole bar just silently reading differently. This stays purely visual — heart *count* logic is untouched.

**Waveform visualization**: only worth building if real amplitude/RMS data is actually exposed from the recording pipeline — `10 §5` mentions a Vosk-model dependency for speech recognition, but nothing in the uploaded files confirms whether per-frame amplitude is surfaced to the UI layer. [CHECK WITH AGENT: confirm whether `SayItViewModel` (not among the uploaded files) exposes any amplitude/RMS stream during `Listening` state.] If yes: a simple 5–7 bar Compose `Canvas` equalizer, each bar height driven by a smoothed amplitude sample, `LearningBlue` fill, positioned below the mic button. If no: skip real waveform data and rely on the ripple/wave effect above as the purely decorative stand-in — do not fake amplitude data with `Random`, since a waveform that visibly doesn't respond to the child's actual voice would be a confusing, not delightful, signal.

---

## FindIt Screen

*(No existing `.kt` file was provided for this screen — spec is built from `10 §5`'s component list, corrected per the sync note above.)*

**Picture grid redesign**: `PictureCard` becomes a `GummyContainer` per tile — rounded-square shape (not full circle, since these hold rectangular picture content), 20–24dp corners, `DarkBrownOutline` stroke, smaller `depthHeight` (4dp rather than the 6dp default) to keep a 5-card grid from feeling visually heavy at typical tile sizes. Each card still respects the 64dp floor as its minimum footprint even at smaller `depthHeight`.

**Correct/incorrect tile states** — corrected per the sync note:
- **Correct**: tile face crossfades toward `GrowthGreen`/`GrowthGreenShadow`, squash-pop (`scaleX 1.08 / scaleY 0.94`, same pattern as the existing `GummyContainer.isSquashed` mechanic), reward chime.
- **Incorrect**: tile face crossfades toward `GentleCorrectionOrange`/`GentleCorrectionOrangeShadow`, gentle `shake` (reuse the existing `shake` modifier already used on `SayItScreen`'s letter card), soft-pop sound cue. **No red at any point.**

**Progress momentum**: upgrade `ScoreIndicator` ("X of 3") from plain text to a small 3-dot/3-star progress row (matching the gummy-dot treatment from HearIt's `ReplayCounter` above) that fills in with the same squash-pop as each correct pick lands — visual momentum building toward the round's completion rather than a static counter updating in place.

---

## BlendIt Screen

*(No existing `.kt` file was provided — spec built from `10 §5`'s component list.)*

**Word construction area**: `TargetWordImage` and `WordAudioButton` sit in a `GummyStaticContainer` header band (image + auto-playing audio icon), `LetterSlotRow` below it as the build target, `TileBank` below that as the source. Keep this strict top-to-bottom reading order (image/audio → target slots → available tiles) so the "what am I building, and with what" relationship stays visually unambiguous for a 6-year-old.

**Letter tiles**: give `LetterTile` a distinct silhouette from every other tappable element in the app — square-ish rounded corners (12–16dp, noticeably tighter than the 28–32dp used everywhere else) rather than the near-pill/circle shapes used on buttons and map nodes, so tiles read specifically as "letter tiles" (a Scrabble-tile association) rather than generic buttons. Still built on `GummyContainer` (outline, depth band, press spring all inherited), sized to the 64dp floor as roughly-square tiles. `LetterSlot` (the empty target) gets a dashed-outline placeholder in `BorderColor` when empty, solid `DarkBrownOutline` once filled.

**Tap-to-place motion**: when a tile is tapped in the bank, animate it flying from its bank position to the target slot position (`animateOffsetAsState`/`Offset` interpolation over ~250–300ms, slight ease-out) rather than teleporting — lands with a small squash-settle. Tap-to-remove reverses the same motion back to the bank. This is purely the existing tap-to-place/tap-to-remove interaction (`10 §5`'s WCAG 2.5.7 note — never drag-only, already correctly specified) with a flight path added instead of an instant swap.

**Per-tile placement feedback — scoping note.** The brief for this doc asks for "celebration micro-animation on each correct syllable placement." As currently spec'd (`10 §5`), `SubmitButton` only enables once all slots are filled, and a single `WordFeedbackCard` evaluates the whole word at submit time — there's no per-tile correctness check as tiles land. Two ways to resolve this, and the choice matters for what gets built:

- **(a) Per-tile correctness reveal** — check each tile against the answer the instant it's placed, and show correct/incorrect feedback per-slot before submit. This is a **logic change**, not a visual one — it starts revealing the answer piecemeal, which changes the challenge's difficulty and isn't this doc's call to make.
- **(b) Uniform placement delight (recommended)** — every tile placement, correct or not, gets the *same* satisfying animation (the flight + squash-settle above, plus a soft placement "click" sound) purely as tactile feedback that a placement registered. No correctness signal is shown until `SubmitButton`/`WordFeedbackCard`, exactly as currently gated. This delivers "feels like play" on every single placement without touching the existing submit-gated evaluation logic.

This doc assumes **(b)** and specs accordingly. [CHECK WITH AGENT: confirm (b) matches product intent before implementation — if per-tile reveal (a) is actually wanted, it needs a requirements-doc change in `01`, not just a visual spec.]

**Hint indicator**: mascot shifts to `THINKING` (`26 §3`) when `HintIndicator` fires after 2 wrong attempts — purely a mascot-state hookup, no new component needed.
