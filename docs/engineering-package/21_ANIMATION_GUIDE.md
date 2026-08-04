# 21 — Animation Guide

Consolidates the motion rules scattered across `03`, `04`, and `10` into one implementation-facing spec, plus the reward/transition/loading/empty-state categories the SDD's screens actually need.

## 1. Reward Animations

| Trigger | Spec |
|---|---|
| Star earned (Letter Complete / Blend It Complete) | Drop-in + bounce + glow, per-star staggered (star 1, then 2, then 3 with a short delay between), paired with `sfx_level_complete_fanfare` and a confetti burst overlay (`03 §4`, `14 §6`) |
| Heart recovery | Small sparkle burst on the specific heart icon that refills, paired with `sfx_heart_recovery_sparkle` |
| Streak badge unlock | Badge scales in with glow + `sfx_streak_badge_unlock`, distinct from the letter-star celebration so it doesn't feel like a repeat of the same reward |
| Node unlock (map) | New node "grows in" from locked-gray to full color, paired with `sfx_node_unlock_chime` |

## 2. Transition Animations

| Transition | Spec |
|---|---|
| Screen entry (all screens) | Fade + slight upward motion, 200–300ms (`03 §4`) |
| Hear It → Say It → Find It (forward flow) | Slide-transition consistent with standard M3 navigation motion, critically damped (no bounce) — per `10 §4`'s Effect/Spatial token distinction: screen-position changes get gentle motion, not playful bounce, to avoid disorientation |
| Map node tap → sublevel screen | Slight zoom-toward-node before the transition, to spatially connect the tap to the destination |

## 3. Success/Failure Micro-Animations

| Event | Spec |
|---|---|
| Correct tap (Find It/Blend It) | Green highlight + brief scale-up (100%→108%→100%), paired with `sfx_correct_chime` |
| Incorrect tap | **Gentle shake only** — no harsh flashing, no red flooding the screen (`03 §4` explicit rule), paired with `sfx_incorrect_pop`/`sfx_blendit_buzz` |
| Tap feedback (any button/tile) | Scale 100%→92%→100%, `MediumBouncy` damping, every tappable component (`03 §4`, `10 §4`) |
| Active map node | Breathing pulse, infinite repeat, subtle (not distracting during idle map browsing) |

## 4. Loading States

- Replace indeterminate spinners with a morphing-shape loader for any wait under 5 seconds (`04 §5`) — this covers the Splash screen's Vosk-model/Room-init wait and any brief PDF-generation wait on the dashboard.
- Loading states should feature the mascot in its Neutral/Idle or Thinking pose (`17 §4`) rather than a generic system spinner, to keep the wait feeling like part of the app's world rather than a technical interruption.

## 5. Empty States

- Empty `ProfileSelectScreen` (no profiles yet): show the mascot in its Pointing pose (`17 §4`) directing attention to the "add profile" affordance, not a generic "no data" message (`04 §5`).
- Empty Parent Dashboard state (a profile with zero completed letters): plain-language framing ("Ready to start the adventure!") rather than a bare 0% stat, consistent with the "never lead with a deficit" principle already applied to streak-reset messaging (`04 §2` Stage 8, `19 §2`'s note on `vo_return_welcome_01`).

## 6. Error States

- Mascot-guided recovery UI, never a raw system dialog on a child-facing screen (`05 §4`, `10 §6`) — e.g., a missing-asset or Vosk-load failure surfaces as the mascot in an Encouraging pose with a simple "Let's try that again!" rather than a stack trace or default Android error dialog.

## 7. Motion Timing Reference (from `03 §4`, repeated here for convenience)

| Band | Duration |
|---|---|
| Micro | 150–250ms |
| Standard | 300–500ms |
| Celebration | 600–1200ms |

## 8. Easing / Spring Parameters (from `10 §4`)

| Interaction category | Damping |
|---|---|
| Spatial (position/size/shape changes) — taps, tile placement, node growth | Bouncy (`MediumBouncy`/`LowBouncy` depending on emphasis) |
| Effect (color/opacity changes) | Fully damped, no overshoot — a color that overshoots and bounces back reads as a rendering bug, not delight |

## 9. Recommended Implementation

- **Native Jetpack Compose `animate*AsState`/`Animatable` + spring specs** for all micro-interactions (tap feedback, shakes, pulses, screen-entry fades) — no external animation library needed for these; they're simple enough that Compose's built-in APIs are both sufficient and lower-risk than adding a Lottie/Rive dependency for something this small.
- **Lottie** (via the `lottie-compose` library) for the handful of genuinely complex, multi-stage animations: the star reward drop/bounce/glow sequence, the confetti burst, and the streak-badge unlock celebration (`14_ASSET_MANIFEST.md §6`) — these benefit from being authored in a motion tool rather than hand-coded spring chains.
- Respect the system reduced-motion accessibility setting everywhere: swap bounce/particle effects for simple fades when it's enabled (`03 §6`, `10 §6` — and note again the flagged gap that there's no in-app manual override toggle yet, `13_MASTER_TASKS.md`).
