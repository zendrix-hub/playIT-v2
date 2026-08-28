# playIT — Duolingo ABC UI Refresh: Implementation Prompt (Round 3)

You are implementing UI changes on top of an already-working first pass. Two prior
rounds have been completed and verified:

- **Round 1**: initial gummy/pressable visual layer built from `23_DUOLINGO_ABC_UI_REFRESH.md`.
- **Round 2**: fixed two confirmed gaps (letter card rotation per §3, BlendItScreen mascot
  resize reverted since §5 never authorized it) and confirmed the squash animation is
  wired to correct-answer moments.

**This round has one job: prove the test suite actually passes, then close out any
remaining polish.** Nothing here changes visual spec — `23_DUOLINGO_ABC_UI_REFRESH.md`
is still the single source of truth for what "correct" looks like.

## Hard constraints (unchanged, non-negotiable)

- Do not touch existing colors from `03_DESIGN_SYSTEM_SUMMARY.md §2` — only the new
  -20% luminance "shadow" tokens in `23 §2` may be added.
- Do not touch type sizes or the Lexend/Andika font choice (`10 §2`).
- 64dp child touch-target floor is a floor, not a target including shadow band — verify
  no tap target shrank when the depth band was added.
- No red, no flashing, for any error/incorrect state — `03 §2` / `03 §6` unchanged.
- Respect reduced-motion: mascot hop-in becomes a plain fade; verify this is still true
  after this round's changes (regression risk if animation code was touched).
- Mascot resize (`23 §5`) applies ONLY to `HearItScreen`, `SayItScreen`, `FindItScreen`,
  and both Complete screens. `MapScreen` and `BlendItScreen` mascots stay as shipped —
  do not resize them.

## Task 1 — Produce real test evidence

The last report claimed "full test suite passed" but only showed a compile check and
one filtered package run. That is not evidence the full suite passes. Do the following
and paste the actual terminal output (not a summary) back in your report:

1. Run the **full** test suite (unit + instrumented/UI tests if any exist), not a
   filtered subset. State the exact command used.
2. Report the full pass/fail/skip counts as printed by the test runner — do not
   paraphrase them.
3. If any test was skipped, disabled, or excluded, name it explicitly and say why.
4. If the full suite cannot run in this environment (e.g. instrumented tests need a
   device/emulator not available here), say so plainly and run the largest subset that
   *can* run, again with real output — don't imply full coverage from a partial run.

## Task 2 — Spec-conformance self-check

Before reporting done, walk `23_DUOLINGO_ABC_UI_REFRESH.md` section by section and
confirm each item against the actual code (cite file + line, not just "done"):

| Spec section | Item | Confirm in code |
|---|---|---|
| §1 | Gummy button: face + depth band, press-into-depth on tap | |
| §2 | Shadow tokens added, no new hues, no red introduced | |
| §3 | Corner radius raised (cards 28dp, buttons ~32dp, tiles/nodes circular) | |
| §3 | 3dp Text Primary outline on every child-facing tappable shape | |
| §3 | Card rotation −2°/+2°, seeded by index, stable across recomposition | |
| §4 | Map node idle bounce, current-node-only, 900ms | |
| §4 | Correct-answer squash (scaleX 1.08 / scaleY 0.94) wired to actual correct-answer event, not just present in a preview | |
| §4 | Mascot hop-in once per screen visit, not per recomposition | |
| §5 | Mascot ~25–30% vertical real estate on the 5 authorized screens only | |
| §6 | Touch targets still ≥64dp including shadow band | |
| §6 | Error states unchanged (no red, no flash) | |

Report this table filled in, with a code citation per row, not a bare checkmark.

## Task 3 — Fix anything Task 2 turns up

If the self-check finds a gap, fix it before reporting done. Do not report done with
known gaps "for a future round."

## What "done" looks like in your report

1. Full, unedited test runner output (Task 1).
2. Filled-in conformance table with code citations (Task 2).
3. List of any fixes made in this round (Task 3), or "none needed" if the self-check
   was clean.
4. Explicit confirmation that no file outside the UI layer (gating logic, pedagogy,
   database, colors, type) was touched this round.
