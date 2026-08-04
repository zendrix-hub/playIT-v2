# 17 — Character Design Guide

## 0. Why a Mascot Is Recommended

Yes — a single recurring mascot is warranted and is already implied by the source material: the SDD's own screen mockups (Map, Find It, Blend It) consistently show a small cat-like character in the `MascotBubble` component across every module, and `03_DESIGN_SYSTEM_SUMMARY.md §6`'s mascot-personality rules (friendly teacher/coach/cheerleader) and `04_RESEARCH_SUMMARY.md`'s Self-Determination Theory notes (relatedness via a non-judgmental companion) both assume a consistent character exists. No source document gives this character a name — the name below is a **proposal, not an approved brand asset**; confirm before final production lock.

## 1. Proposed Character: "Kuting"

A friendly, round-bodied cat. Cats read as warm and approachable without the size/danger associations of some animals, match the small character already sketched into the SDD mockups, and "Kuting" (Filipino for "kitten") gives the character a name with genuine cultural resonance for the target learners without requiring the app to depict a specific real-world regional symbol.

*(If stakeholders prefer a different animal or a culturally-specific alternative — the Competitive Analysis research suggested regional options like a tarsier — swap the reference description below; every downstream spec in this package refers to "the mascot" generically enough to survive that swap.)*

## 2. Appearance

- **Build:** round, soft-bodied cat, oversized head-to-body ratio (approx. 1:1.2), consistent with the simplified, exaggerated-but-safe proportions used across children's edtech mascots.
- **Palette:** primary coat color drawn from Energy Orange (`03 §2`) — warm, matches the "cheerleader" personality without competing with Learning Blue's use as the dominant UI chrome color. Cream White belly/muzzle patch. Simple black dot eyes with small white highlight, no pupils/iris detail (keeps the "silhouette test" from `15 §6` passing).
- **Outline:** thick, consistent, matching `16_ILLUSTRATION_STYLE_GUIDE.md §2`.
- **No accessories** in the base design (no hat, no clothing) — keeps the character timeless and avoids any single module "owning" a costumed variant; if a themed variant is wanted later (e.g., a graduation cap for a milestone celebration), treat it as an explicit additional asset, not a redesign.

## 3. Personality (binding rule, from `03 §6` — repeated here for the asset team)

Friendly teacher / coach / cheerleader. **Never** a judge, supervisor, or scorekeeper. The mascot does not "grade" the child — it reacts alongside them.

## 4. Expression/Pose Set (8 poses — full generation prompts in `15_IMAGE_GENERATION_PROMPTS.md §5`)

| State | When it appears |
|---|---|
| Happy | Correct answers (general) |
| Excited | Milestones (streak badges, group/level unlocks) |
| Thinking | Hint moments (Blend It's 2-wrong-attempt hint trigger) |
| Encouraging | After a mistake — paired with the "Good try! Let's listen again" feedback language (`03 §6`) |
| Celebrating | Level/letter/session complete screens |
| Neutral/Idle | Default resting state, Map screen ambient presence |
| Listening | Say It, while the mic is active |
| Pointing | Onboarding, directing attention to the first tappable node |

## 5. Usage Guidelines

- The mascot's `MascotBubble` always pairs on-screen text with audio (`03 §6` — no silent text-only mascot lines).
- One mascot instance visible per screen maximum — it's a companion, not a recurring background pattern.
- Never used to deliver system/technical errors in a way that implies fault ("I'm sorry, I couldn't hear you" reads better than "You said it wrong") — ties into the corrective-feedback language rules in `03 §6` and the error-state guidance in `04 §5`.
- Consistent scale and position for the `MascotBubble` placement across screens (a `03 §6`/WCAG 3.2.6 "Consistent Help" pattern, echoed in the UX research) — don't let the mascot's on-screen position drift screen to screen.

## 6. What NOT to Do

- Do not license or depict any existing copyrighted/branded character.
- Do not give the mascot a design that varies meaningfully module-to-module (e.g., a "Blend It cat" vs. a "Map cat") — one consistent character, 8 poses, used everywhere.
- Do not add teeth, claws, or any remotely threatening visual detail, regardless of how "playful" the intent — this cuts against the explicit non-scary requirement baked into `16_ILLUSTRATION_STYLE_GUIDE.md §3`.
