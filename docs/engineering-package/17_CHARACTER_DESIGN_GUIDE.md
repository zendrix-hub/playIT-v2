# 17 — Character Design Guide

> Superseded and confirmed by `32_MASCOT_SPECIES_DECISION.md`. This version
> replaces the original "Kuting the cat" proposal — that proposal is no
> longer current; the mascot is **Lily the Tarsier**, already implemented
> in code (`MascotState` enum, `DockedMascotWithBubble`, `MascotBubble`)
> and specced further in `26_MASCOT_COPLAYER_SYSTEM.md`. If anything below
> conflicts with `26`, `26` wins for behavior/reactivity — this doc covers
> identity, appearance, and production guardrails.

## 0. Why a Mascot Is Recommended

Unchanged from the original reasoning: a single recurring mascot is
warranted and already implied by the source material — the SDD's own
screen mockups consistently show a small character in the `MascotBubble`
component across every module, `03_DESIGN_SYSTEM_SUMMARY.md §6`'s
mascot-personality rules (friendly teacher/coach/cheerleader) assume a
consistent character, and `04_RESEARCH_SUMMARY.md`'s Self-Determination
Theory notes need relatedness via a non-judgmental companion to mean
anything.

## 1. Confirmed Character: Lily the Tarsier

A Philippine tarsier — a genuine national symbol with a "little forest
spirit, brings good luck" association in Filipino folklore, and closely
tied to Bohol tourism and Visayan cultural identity. For a phonics app
built for Filipino Grade 1 learners specifically, that cultural
specificity is a real asset: a companion that reads as authentically
theirs, not a generic animal any kids' app in any country could use.
Full reasoning for choosing this over the original cat proposal is in
`32_MASCOT_SPECIES_DECISION.md`.

## 2. Appearance

The real animal's proportions (tiny body, very thin limbs and tail,
nocturnal) do **not** translate directly — stylize hard toward round and
soft, or the character reads as unsettling rather than endearing. Treat
every point below as a deliberate departure from anatomical accuracy in
service of that goal, not an oversight.

- **Build:** small, round-bodied, oversized head-to-body ratio (approx.
  1:1.2), consistent with the simplified, exaggerated-but-safe proportions
  used across children's edtech mascots and matching the "gummy,
  3D-pressable" shape language from `23_DUOLINGO_ABC_UI_REFRESH.md`.
- **Eyes — the hero feature:** large, round, and simple in shape (passes
  the "silhouette test" from `15 §6`) — this is Lily's signature trait and
  should read as the first thing a child notices. Give them a warm iris
  color (not solid black — a flat void reads cold on eyes this large) plus
  one consistent highlight dot. **Do not** render them at anatomically
  accurate scale or placement; oversized-but-friendly, not oversized-and-
  realistic.
- **Ears:** rounded off, soft-edged — real tarsier ears are large and thin;
  round them down so they read as plush rather than bat-like.
- **Limbs:** significantly shortened and thickened from the real animal's
  long, thin fingers and legs — simple rounded paws, no individually
  articulated digits. This is the single biggest departure from the real
  animal and the most important one to get right.
- **Tail:** short, soft, rounded tip — never long, thin, or bare-looking.
  This is the other detail most responsible for tipping a tarsier design
  toward "rodent" instead of "plush companion" if left realistic; keep it
  minimal or tuck it out of frame in most poses.
- **Coat:** warm tan/cream fur base, Cream White for the belly/muzzle
  patch — close enough to the real animal's coloring to still read as
  "tarsier" at a glance, while staying inside the existing palette family.
  [CHECK WITH AGENT: confirm against `03`'s full token list whether an
  existing warm-neutral token covers the tan coat tone, or whether a new
  token is needed.] Energy Orange reserved for small accent details (inner
  ear, paw pads) rather than the whole coat.
- **Outline:** thick, consistent, matching `16_ILLUSTRATION_STYLE_GUIDE.md
  §2`.
- **No accessories** in the base design (no hat, no clothing) — keeps the
  character timeless; a themed variant (e.g. a graduation cap for a
  milestone) is an explicit additional asset later, not a redesign.

## 3. Personality (binding rule, from `03 §6` — repeated here for the asset team)

Friendly teacher / coach / cheerleader. **Never** a judge, supervisor, or
scorekeeper. Lily doesn't "grade" the child — she reacts alongside them.
This is also why she's framed as a co-player and companion rather than a
pet or something to "take care of" — a framing that happens to sidestep
any accidental echo of real tarsiers' well-documented sensitivity to
handling and captivity stress, though that's a side benefit of the
co-player framing already established in `26`, not the reason for it.

## 4. Expression/Pose Set

This package originally proposed 8 poses (below, marked where superseded).
**`26_MASCOT_COPLAYER_SYSTEM.md §3` is the current source of truth** — 7
states total: `IDLE`, `CELEBRATING`, `ENCOURAGING`, `LISTENING`,
`POINTING` (existing), plus `WAVING` and `THINKING` (added in `26`). Full
art-direction detail and asset filenames are in `26 §3`; use that table
when briefing the asset pipeline, not the one below.

| Original 17 proposal | Current status |
|---|---|
| Happy | Superseded — see `IDLE`/`CELEBRATING` in `26` |
| Excited (milestones, streak badges, unlocks) | **Unresolved** — not in `26`'s current state list. Confirm whether milestones reuse `CELEBRATING` or need a distinct pose before locking asset production. See `32 §4`. |
| Thinking | Kept — now `THINKING`, retriggered on `BlendIt` hint (`26 §3`) |
| Encouraging | Kept — `ENCOURAGING`, non-negotiable "never a frown" rule still applies (`26 §3`) |
| Celebrating | Kept — `CELEBRATING` |
| Neutral/Idle | Kept — `IDLE` |
| Listening | Kept — `LISTENING` |
| Pointing | Kept — `POINTING` |
| *(not in original)* | **New:** `WAVING` — first app open / greeting beat (`26 §3`) |

## 5. Usage Guidelines

- `MascotBubble` always pairs on-screen text with audio (`03 §6` — no
  silent text-only mascot lines).
- One mascot instance visible per screen maximum — she's a companion, not
  a recurring background pattern.
- Never used to deliver system/technical errors in a way that implies
  fault ("I couldn't hear you" reads better than "You said it wrong") —
  ties into the corrective-feedback language rules in `03 §6` and the
  error-state guidance in `04 §5`.
- Consistent scale and position per screen type — see the size/position
  table in `26 §2`; don't let her on-screen position or scale drift
  screen to screen within the same screen type.

## 6. What NOT to Do

- Do not license or depict any existing copyrighted/branded character.
- Do not give her a design that varies meaningfully module-to-module — one
  consistent character, one pose set, used everywhere.
- Do not add teeth, claws, or any remotely threatening visual detail,
  regardless of how "playful" the intent — this cuts against the explicit
  non-scary requirement in `16_ILLUSTRATION_STYLE_GUIDE.md §3`.
- Do not render her eyes, limbs, or tail at anatomically realistic
  proportions — §2 above exists specifically because the unstylized
  version of this animal reads as unsettling, not cute.
- Do not build any interaction that frames her as something to be
  physically handled, caught, or "kept" — she's a co-player who's always
  present, not a collectible or a pet (`26`'s existing behavior model
  already implies this; stated explicitly here so it isn't lost on future
  feature additions).
