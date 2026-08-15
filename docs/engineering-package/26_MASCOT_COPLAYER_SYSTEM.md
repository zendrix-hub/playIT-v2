# 26 — Lily the Tarsier: Mascot as Co-Player

> Builds on the existing `MascotState` enum, `DockedMascotWithBubble`, and `MascotBubble` components. `MascotBubble` (the flat `Surface`-based version) is explicitly not refactored yet per the comment in `PediatricComponents.kt` — this doc assumes that refactor happens as part of implementing this spec, since a co-player mascot can't have one screen where she's a gummy 3D character and another where she's a flat card with a corner icon.

## 1. Co-player vs. tooltip — the principle

A tooltip mascot explains the screen and gets out of the way: small, corner-anchored, appears once, static between messages. A co-player mascot is *in* the activity: she takes up real space, her pose changes with what's happening, and the child's win feels like it's shared with her, not reported to her.

Concretely, this means three behavior changes from the current implementation:

1. **Size.** `MascotBubble`'s current 54dp avatar circle is tooltip-scale. Co-player scale starts at 92dp (already correct in `DockedMascotWithBubble`) and goes up to 25–30% of vertical screen space on the three core activity screens (`03`'s research-flagged number, formalized here — see §2).
2. **Continuity.** Lily doesn't restart her "hop in" entrance every time a bubble's message changes — she's already on screen, and only her *expression* changes per reaction (§4). The hop-in plays once per screen visit, not once per message.
3. **Reactivity.** A tooltip mascot's copy changes; a co-player's *pose* changes. Every state transition in `SayItState` (and the equivalent states on `HearIt`/`FindIt`) should have a paired mascot expression change, not just a paired text change — this is already directionally correct in `SayItScreen.kt`'s `when (state)` block mapping to `MascotState`, and should be the pattern copied to `HearItScreen`/`FindItScreen`.

## 2. Size and position rules per screen type

| Screen type | Mascot footprint | Position | Component |
|---|---|---|---|
| Map | Small/contextual (~68dp, matches current `MapScreen.kt` implementation) | Beside the active node, alternating left/right per node parity (already coded) | Plain `Image` + `idleBounce`, not docked — she's a companion walking the path, not anchored to one spot |
| Activity screens (HearIt, SayIt, FindIt) | Large — 25–30% of vertical screen height | Docked bottom, full-width row, speech bubble emerging from her right side | `DockedMascotWithBubble` |
| BlendIt | Same as other activity screens (25–30%), but see note below | Docked bottom | `DockedMascotWithBubble` |
| Celebration (LetterComplete, BlendItComplete) | Largest — 30–35%, her biggest moment | Centered, above the star/streak stack, not docked to an edge | New static/celebratory variant — spec in `29` |
| Onboarding (Splash, ProfileSelect, NamePrompt) | 25–30%, centered | Centered or docked bottom depending on beat — spec in `30` | `DockedMascotWithBubble` or a dedicated onboarding variant |

**BlendIt note:** the word-construction area (`TargetWordImage`, `LetterSlotRow`, `TileBank`) already competes hard for vertical space on a phone-sized budget tablet screen. If a full 25–30% mascot genuinely doesn't fit alongside the tile bank at typical viewport heights, shrink Lily to ~18–20% on this screen only and keep the bubble docked but narrower, rather than shrinking the tile bank below its 64dp touch-target floor. [CHECK WITH AGENT: verify against actual target device viewport — this is a layout-budget call, not a design preference, and may need per-density testing.]

## 3. Expression system

Current states: `IDLE`, `CELEBRATING`, `ENCOURAGING`, `LISTENING`, `POINTING`. Two additions, zero removals — and one explicit non-addition:

| State | Trigger | Art direction |
|---|---|---|
| `IDLE` *(existing)* | Default resting state, no activity in progress | Calm, neutral-friendly expression, relaxed posture, eyes open and forward. This is her "waiting with you" pose, not a sleepy one. |
| `CELEBRATING` *(existing)* | Correct answer, lesson/word complete | Big open-mouth smile, arms up or a small hop mid-frame, eyes crinkled with joy. This is the highest-energy state in the set. |
| `ENCOURAGING` *(existing)* | After an incorrect attempt, or any non-success moment | Warm, supportive expression — leaning slightly forward, a small reassuring gesture (like an open paw/hand). **Never** a frown, a slumped posture, or downcast eyes — per `03 §6`'s mascot-is-never-a-judge rule, `ENCOURAGING` is the only state that follows a miss, and it must read as "let's try again together," not "that was wrong." |
| `LISTENING` *(existing)* | Mic actively recording on `SayItScreen` | Head slightly tilted, one hand cupped near an ear, focused/attentive eyes. Should read as "I'm listening too," mirroring what the child is being asked to do. |
| `POINTING` *(existing)* | Directing attention to an element (Play button, mic, a tile) | Arm extended toward the target, eyes following her own gesture. Needs a version usable at both her docked activity-screen scale and her smaller map-companion scale. |
| `WAVING` **[NEW]** | First app open on `SplashScreen`, and the greeting beat on `ProfileSelectScreen`/`NamePromptScreen` (`30`) | Open, friendly wave, welcoming posture, slight forward lean — first impression of the whole app, so this needs to be her most inviting single frame. |
| `THINKING` **[NEW]** | `HintIndicator` firing on `BlendItScreen` after 2 wrong attempts (`10 §5`) | Paw/hand near chin, one eyebrow-equivalent raised, looking at the puzzle rather than at the child — signals "let's figure this out," distinct from `ENCOURAGING`'s child-facing warmth. |

**Explicit non-addition:** no "sad," "disappointed," or "concerned" state. Every non-success moment in the app already routes to `ENCOURAGING` by design (`03 §6`: "never Wrong!... always Good try!"). Adding a visibly sad mascot frame would directly contradict that rule the first time a well-meaning implementer reaches for it during a miss — so it's called out here as a state that should not exist, not just one that wasn't requested.

**[ASSET] list** (place at `images/mascot/lily_<state>.png`, matching the existing `rememberAssetPainter` path convention already used for `reward_star.png`/`reward_heart.png`):

- `[ASSET: lily_idle.png]` — calm, neutral-friendly, forward-facing
- `[ASSET: lily_celebrating.png]` — big smile, arms up, mid-hop energy
- `[ASSET: lily_encouraging.png]` — warm, supportive lean-forward, open gesture
- `[ASSET: lily_listening.png]` — head tilt, cupped ear, focused
- `[ASSET: lily_pointing.png]` — extended arm, following gaze
- `[ASSET: lily_waving.png]` — open wave, welcoming lean **(new)**
- `[ASSET: lily_thinking.png]` — paw near chin, puzzling expression **(new)**

Each should be produced at consistent canvas proportions and center-of-mass (same anchor point across all seven) so swapping between them in `DockedMascotWithBubble` doesn't visibly jump the character's position frame-to-frame. Full art-direction prompt guidance is in `31`.

## 4. Reaction system

| Event | Mascot response |
|---|---|
| Correct answer (any activity screen) | Instant cut to `CELEBRATING` (no transition delay — the reaction should feel simultaneous with the reward chime, not lag behind it), holds for ~800–1000ms, then eases back to `IDLE` |
| Incorrect answer | Cut to `ENCOURAGING` in sync with the soft-pop sound cue (`03 §6`), holds through the retry prompt, returns to whatever the screen's default working state is (`LISTENING` on re-record, `IDLE` otherwise) |
| Mic actively recording | `LISTENING` for the duration of the hold |
| First arrival on a screen needing attention-direction | `POINTING`, then eases to `IDLE`/`ENCOURAGING` after ~1.5s or on first interaction, whichever is first |
| Idle timeout (no interaction for ~8–10s on an activity screen) | Small random idle wiggle — a subtle non-verbal check-in, not a new expression. Implement as a tiny periodic scale/rotate flourish (±3°, ~400ms) layered on whatever the current state's pose is, not a state change. This reuses the existing `idleBounce`/`breathingPulse` modifier pattern already used on map nodes. |
| Hint threshold reached (`BlendIt`, 2 wrong attempts) | `THINKING` |
| First app open / new profile greeting | `WAVING` |

## 5. Animation approach — Lottie vs. frame PNG vs. code-driven

**Recommendation: static/limited-frame PNG per state + code-driven Compose motion**, not Lottie, for the mascot character herself.

Reasoning: Lottie animations of a rigged character (breathing, blinking, gesture cycles) require either an After Effects + Bodymovin export pipeline or a vector-rigged tool most solo/thesis-scale art workflows don't have set up — a large production lift for a one-person asset pipeline. The app already gets most of the *feel* of life from motion, not frame animation: `idleBounce`, `breathingPulse`, the hop-in spring in `DockedMascotWithBubble`, and the reaction-state swaps above are all code-driven and already implemented. A 7-state PNG set plus these existing/planned Compose animations covers the co-player brief without a new dependency or a new art pipeline.

**Where Lottie earns its place instead:** effects that are *not* the character — confetti, star bursts, sparkle particles (`29`). Those are exactly the kind of self-contained, non-rigged effects the free LottieFiles library is built for, and don't require animating Lily herself. Bundled `.json` Lottie files are local assets once shipped in the APK, so this doesn't conflict with the "zero network after install" constraint.

## 6. Bubble dialogue spec

Tone guide: short sentences, everyday words, second person ("you"), genuine (not inflated) enthusiasm — an exclamation point marks a real win, not every line. No baby talk, no diminutives that read as condescending ("widdle"), consistent with treating the child as a capable learner per `03 §1`'s philosophy. Every line pairs with audio playback per the existing text+audio-always rule (`03 §6`) — nothing below is text-only.

| Screen / state | Line |
|---|---|
| Splash (first open) | "Hi! I'm Lily. Ready for an adventure?" |
| Map (first visit) | "Tap an unlocked letter to start your sound adventure!" *(existing copy, keep as-is)* |
| Map (returning session) | "Welcome back! Let's keep going." |
| HearIt (before first play) | "Tap the button and listen closely!" |
| HearIt (after ≥1 play) | "Ready for the next step?" |
| SayIt (idle) | "Tap the big mic button and say the sound /{letter}/!" *(existing copy, keep as-is)* |
| SayIt (listening) | "Listening closely to your voice... Speak clearly!" *(existing copy, keep as-is)* |
| SayIt (correct) | "Awesome pronunciation! You got it right!" *(existing copy, keep as-is)* |
| SayIt (incorrect) | "Good try! Let's listen again and try one more time." *(existing copy, keep as-is)* |
| FindIt (prompt) | "Find the pictures that start with this sound!" |
| FindIt (correct pick) | "Yes! You found one!" |
| FindIt (incorrect pick) | "Good try! Let's look again." |
| FindIt (all 3 found) | "You found them all!" |
| BlendIt (prompt) | "Let's build this word together!" |
| BlendIt (hint / THINKING) | "Hmm, let's sound it out together." |
| BlendIt (word correct) | "You blended it! Great job!" |
| LetterComplete | "You did it! Look at those stars!" |
| BlendItComplete | "Amazing work today!" |
| ProfileSelect (new profile) | "Whose adventure is this?" |
| NamePrompt | "What's your name?" |

Existing `SayItScreen` copy is carried over verbatim rather than replaced — it already matches this tone guide.
