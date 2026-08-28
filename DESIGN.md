# PlayIT — Design System

A children's phonics app for Filipino Grade 1 learners (ages 6-7). Every
value below is the **resolved** system — where the original shipped spec
conflicted with later research/decisions, this reflects the resolution,
not the superseded original. Full reasoning: `03_DESIGN_SYSTEM_SUMMARY.md`.

## Philosophy
Clarity before decoration. Encouragement before correction. Audio before
reading. Large touch targets. Cultural familiarity for Filipino learners.
Every interaction should make a child feel successful and capable.

## Color
| Role | Hex | Use |
|---|---|---|
| Learning Blue | `#4A90E2` | Primary actions, navigation, active states |
| Growth Green | `#4CAF50` | Correct answers, success, completion — **only** color used for "correct" |
| Achievement Gold | `#FFC107` | Stars, completed nodes, rewards, milestones |
| Energy Orange | `#FF9800` | Encouragement, streaks, mascot excitement (accent only) |
| Friendly Purple | `#8E7DF2` | Blend It / challenge screens |
| Gentle Correction Orange | `#FFB74D` | Incorrect answers, retry prompts — **only** color used for "incorrect" |
| Soft Sky | `#EAF6FF` | Large backgrounds |
| Cream White | `#FFFDF8` | Cards, containers |
| Text Primary | `#2D3748` | |
| Text Secondary | `#718096` | |
| Border | `#E2E8F0` | |
| Destructive Red | `#B3261E` | **Reserved exclusively** for true destructive/system dialogs (delete profile, storage full) — never for incorrect-answer feedback |

**Hard rule:** no red anywhere for incorrect answers, ever — no flashing,
no "X" mark. Gentle Correction Orange only. Never rely on color alone to
signal state — pair every correct/incorrect/locked state with a distinct
icon or shape too, not color alone.

## Typography
**Lexend** (variable) primary, **Andika** static fallback — not
Nunito/Poppins. Single-story 'a' and 'g' letterforms matter here (matches
how children are taught to handwrite). Body-text floor for anything a
child is meant to sound out: **24sp minimum**. Adult-only surfaces
(parent-facing screens) may use a smaller 16-18sp scale. Never below 16sp
anywhere, on any surface.

## Shape & Motion — "gummy," pressable
Every card and button reads as a soft, chunky, pressable 3D object, not a
flat Material card:
- Thick, consistent dark-brown outline on every surface
- A visible drop-shadow "depth" band beneath each element, like a
  physical button — presses squash into that depth on tap
- Rounded-square corners (20-24dp) for grid tiles and picture cards;
  near-pill/circular shapes for buttons and map nodes; tighter
  rounded-square (12-16dp) specifically for letter tiles, to read as
  distinct "Scrabble tile" objects rather than generic buttons
- Base spacing unit: 8dp (scale: 4/8/16/24/32/48/64dp)
- Motion: micro 150-250ms, standard 300-500ms, celebration 600-1200ms.
  Tap feedback scales 100→92→100%. Incorrect answers get a gentle shake,
  never a flash.

## Touch targets
**64dp minimum is the default** for every child-facing interactive
element — letter tiles, picture cards, buttons, mic button, map nodes.
Not just "important" actions — the whole child-facing surface counts.
Smaller 48-56dp targets are reserved for adult-only surfaces only.

## Mascot
Lily, a Philippine tarsier. Round body, oversized head-to-body ratio,
very large round warm-brown eyes as her signature feature, small rounded
ears, short thickened limbs, short soft tail, warm tan/cream fur with a
cream belly patch. No accessories. No realistic proportions — stylize
everything toward soft and round. Personality: friendly teacher/coach/
cheerleader, never a judge. She's a co-player, present throughout an
activity, not a corner tooltip.

## Feedback language
Never "Wrong!" or "You lost a heart." Always "Good try! Let's listen
again." / "Let's practice one more time." Warm, second person, genuine
(not inflated) enthusiasm — an exclamation point marks a real win, not
every line.

## Accessibility
Text + audio for every instruction, always — never text-only. Color
never the sole signal. Text contrast 4.5:1 minimum (7:1 on parent-facing
screens). One primary goal and one primary call-to-action per screen.
