# 26 — Lily the Tarsier: Mascot as Co-Player System

> **Architectural Status:** Confirmed & Expanded. Redefined by synthesizing best-in-class UX paradigms from **Duolingo ABC** (gummy physical presence, spring-driven co-player reactivity), **Headspace** (emotional safety, zero-judgment warmth, organic breathing cadence), and **Drops** (radical silhouette simplicity, punchy tactile clarity, zero visual clutter).
> Replaces all prior static tooltip/avatar concepts across child-facing screens.

---

## 1. The Co-Player Philosophy: Tri-Benchmark Synthesis

A traditional edtech mascot acts as an intrusive instructor or a passive decorative sticker. In PlayIT, **Lily the Tarsier** is a **Co-Player and Study Companion**—she explores the island, listens to sounds, and learns alongside the child.

```
       ┌─────────────────────────────────────────────────────────────┐
       │                 PLAYIT CO-PLAYER PARADIGM                   │
       ├──────────────────────────────┬──────────────────────────────┤
       │ ❌ Traditional Tooltip Mascot │ ✅ PlayIT Co-Player (Lily)   │
       ├──────────────────────────────┼──────────────────────────────┤
       │ Small 48–56dp corner avatar  │ Large 25–30% bottom dock     │
       │ Static posture between texts │ Reactive expressive states   │
       │ Evaluates/grades the child   │ Shares wins & retries        │
       │ Disconnected speech popup    │ Integrated gummy speech dock │
       │ Restarts entrance every text │ Persistent alive presence    │
       └──────────────────────────────┴──────────────────────────────┘
```

### Benchmark Inspirations Adapted for PlayIT:
1. **From Duolingo ABC:**
   * **Gummy Bodily Presence:** Lily is anchored at the bottom-left/center with physical springiness, dynamic squash-and-stretch entry, and tactile bounce.
   * **Active Co-Participation:** Lily performs tasks *with* the child (cups her ear when the mic listens, points toward puzzle tiles, jumps into the air on word blends).
2. **From Headspace:**
   * **Emotional Safety & Non-Judgment:** When a child makes a mistake, Lily never frowns, looks sad, or shows frustration. She immediately enters an `ENCOURAGING` warm lean ("we're in this together, let's try again!").
   * **Organic Breathing Cadence:** Even when idle, Lily has a gentle, rhythmic breathing pulse (`idleBreathing` modifier) that makes the app feel soothing and alive rather than sterile.
3. **From Drops:**
   * **Radical Silhouette Simplicity:** Zero intricate fur noise or complex textures. Clean, bold vector geometry (circles, rounded teardrops, pill shapes) readable even at small thumbnail scales.
   * **Tactile Feedback Alignment:** Visual expressions trigger in microsecond sync with audio chimes and haptic pulses.

---

## 2. Visual & Anatomical Identity: Lily the Tarsier

Lily is stylized deliberately away from nocturnal realism toward a **plush, gummy companion**:

```
                       ╭─────────────╮
                      │   ╭───────╮   │  <-- Soft, rounded ears with warm inner glow
                     ╭┴───┤ ◕   ◕ ├───┴╮
                     │    │   ▾   │    │  <-- Giant warm hazel eyes with glossy specular highlight
                     ╰─┬──╰───────╯──┬─╯  <-- Short neckless pear body (1:1.1 head-to-body ratio)
                       │  ╭───────╮  │
                       │  │   ♥   │  │    <-- Cream white belly patch
                       ╰──┤ ╭───╮ ├──╯
                          ╰─╯   ╰─╯       <-- Stubby rounded paws & feet (zero sharp claws)
```

### Anatomical & Style Guardrails:
* **Proportions:** Oversized rounded head (approx. 48% of total height) on a soft pear-shaped body. No sharp angles anywhere.
* **Eyes (Hero Feature):** Giant, friendly, glossy circular eyes with a rich warm hazel/amber iris and a single crisp white highlight dot. Always oriented toward the user or the target UI element.
* **Limbs & Paws:** Short, thick, rounded plush limbs ending in simple rounded paws without individual articulated digits or claws.
* **Tail:** Short, soft tufted tip tucked naturally into her side or behind her body—never long, thin, or rodent-like.
* **Color Palette Tokens:**
  * **Base Coat:** Caramel Tan (`#D97706` / `#F59E0B`)
  * **Muzzle & Belly Patch:** Warm Cream (`#FFFBEB` / `#FEF3C7`)
  * **Inner Ear / Paw Accents:** Energy Orange (`#FB923C`)
  * **Outlines:** Thick, consistent Dark Espresso (`#292524`, 4–6dp equivalent)
  * **Shading:** Clean 2-tier cel shading (single soft warm shadow under chin and paws). Zero photorealistic fur grain.

---

## 3. The 7 Co-Player States & Motion Choreography

Lily operates across 7 distinct states mapped directly to the `MascotState` enum in `AssetUtils.kt`:

```
┌──────────────┬─────────────────────────────┬───────────────────────────────────────────┐
│ State        │ Screen Trigger              │ Visual Choreography & Emotion             │
├──────────────┼─────────────────────────────┼───────────────────────────────────────────┤
│ IDLE         │ Resting state / waiting     │ Gentle upright posture, open warm gaze,   │
│              │ between child actions       │ subtle 2.4s sinusoidal breathing loop     │
├──────────────┼─────────────────────────────┼───────────────────────────────────────────┤
│ WAVING       │ Splash, Profile Select,     │ Friendly one-paw wave, welcoming body     │
│              │ & Name Prompt greeting      │ lean forward, big cheerful smile          │
├──────────────┼─────────────────────────────┼───────────────────────────────────────────┤
│ LISTENING    │ Mic actively recording on   │ Head tilted ~10°, one paw cupped near ear,│
│              │ SayItScreen                 │ attentive focused eyes watching user      │
├──────────────┼─────────────────────────────┼───────────────────────────────────────────┤
│ POINTING     │ Directing child attention   │ Arm/paw extended toward target (mic,      │
│              │ to interactive target       │ tile, play button), eyes locked on target │
├──────────────┼─────────────────────────────┼───────────────────────────────────────────┤
│ ENCOURAGING  │ Incorrect answer, retry,    │ Warm supportive forward lean, open paw    │
│              │ or permission prompt        │ reassurance, gentle caring smile          │
├──────────────┼─────────────────────────────┼───────────────────────────────────────────┤
│ THINKING     │ BlendIt hint threshold or   │ Paw gently touching chin, curious upward  │
│              │ noisy audio environment     │ head tilt, playful inquisitive expression │
├──────────────┼─────────────────────────────┼───────────────────────────────────────────┤
│ CELEBRATING  │ Correct answer, lesson      │ Explosive double-paw skyward hop, eyes    │
│              │ complete, star award        │ crinkled with joy, highest energy pose    │
└──────────────┴─────────────────────────────┴───────────────────────────────────────────┘
```

> [!IMPORTANT]
> **Strict Non-Judgmental Rule:** There is **NO** "sad", "disappointed", or "crying" state. A miss triggers `ENCOURAGING` instantly. Lily is an ally who cheers the effort, never a grader who judges the error.

---

## 4. Screen Layout & Viewport Budget Allocation

Lily is rendered via `DockedMascotWithBubble` or contextual standalone components based on screen viewport requirements:

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│ Activity Screen (HearIt, SayIt, FindIt, BlendIt)                                       │
│                                                                                        │
│   ┌────────────────────────────────────────────────────────────────────────────────┐   │
│   │                                                                                │   │
│   │                           ACTIVITY INTERACTION AREA                            │   │
│   │                (Letter Card / Phoneme Visual / FindIt Grid / Tile Bank)        │   │
│   │                                                                                │   │
│   └────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                        │
│   ┌────────────────────────────────────────────────────────────────────────────────┐   │
│   │ ╭──────────────╮  ╭────────────────────────────────────────────────────────╮   │   │
│   │ │              │  │ 💬 SPEECH BUBBLE (Gummy 3D Surface)                   │   │   │
│   │ │  LILY DOCK   │  │ "Pakinggan natin ang tunog! • Listen closely!"         │   │   │
│   │ │  (24–28% H)  │  ╰──╮                                                     │   │   │
│   │ ╰──────────────╯     ╰─────────────────────────────────────────────────────╯   │   │
│   └────────────────────────────────────────────────────────────────────────────────┘   │
└────────────────────────────────────────────────────────────────────────────────────────┘
```

### Viewport Budget Matrix:
| Screen Type | Mascot Size | Anchor Position | Component | Motion Behavior |
| :--- | :--- | :--- | :--- | :--- |
| **MapScreen** | 68dp | Beside active node (alternating L/R) | `Image` + `idleBounce` | Companion walking the path; hops when unlocked node is selected |
| **HearIt / SayIt / FindIt** | 100–120dp (25–28% height) | Bottom-left docked | `DockedMascotWithBubble` | Spring hop entrance once per screen; instant state cuts on answer |
| **BlendItScreen** | 90–100dp (20–22% height) | Bottom-left docked | `DockedMascotWithBubble` | Compact dock to preserve tile-bank 64dp touch-target floor |
| **Letter / BlendIt Complete** | 140–160dp (32–36% height) | Center hero stage | `CelebrationMascot` | Double-hop celebratory spring with confetti/star bursts |
| **Onboarding (Splash/Profile)** | 120–140dp (28–30% height) | Center / Bottom-docked | `DockedMascotWithBubble` | Friendly entrance with `WAVING` state |

---

## 5. Compose Motion Choreography & Code-Driven Juice

Rather than requiring heavy, bloated video or lottie rigs for Lily herself, PlayIT uses **Compose Spring Physics + 7 Anchor Keyframe States** for 60fps buttery responsiveness:

```kotlin
// Mascot Spring Entry Animation
val mascotScale by animateFloatAsState(
    targetValue = 1f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    ),
    label = "mascotEntryScale"
)

// Organic Breathing Cadence (Headspace-inspired)
val infiniteTransition = rememberInfiniteTransition(label = "lilyBreathing")
val breathScaleY by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.025f,
    animationSpec = infiniteRepeatable(
        animation = tween(1200, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
    ),
    label = "breathScaleY"
)

// Interactive Easter Egg (Child Taps Lily)
var isPoked by remember { mutableStateOf(false) }
val pokeBounce by animateFloatAsState(
    targetValue = if (isPoked) 1.15f else 1.0f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy),
    finishedListener = { isPoked = false },
    label = "lilyPokeBounce"
)
```

### Motion Specs:
1. **Screen Entrance:** Single spring hop-in on initial composition (`scale: 0.7 -> 1.0`, `translationY: +40dp -> 0dp`). Does **not** re-trigger when dialogue changes.
2. **State Swapping:** Instant cut on state enum change (0ms latency with reward sound), followed by a micro-squash (`scaleY: 0.95 -> 1.05 -> 1.0` over 300ms).
3. **Celebration Hold:** Holds `CELEBRATING` for 900ms before smoothly easing back to `IDLE`.
4. **Interactive Tap Response:** When the child taps Lily on any screen, she plays a playful micro-hop animation and emits a cheerful sound cue.

---

## 6. Dialogue Matrix & Audio Pairing Spec

Every line spoken in the `MascotBubble` strictly pairs visual text with synchronized audio playback (`03 §6`).

| Screen / Event | State | Dialogue String (Bilingual Marungko Context) |
| :--- | :--- | :--- |
| **Splash Screen (Open)** | `WAVING` | "Hi! Ako si Lily. Handa ka na ba sa ating adventure? • Ready for an adventure?" |
| **Map (First Visit)** | `WAVING` | "Pindutin ang unlocked na titik para magsimula! • Tap an unlocked letter to start!" |
| **Map (Returning)** | `IDLE` | "Maligayang pagbabalik! Ipagpatuloy natin ang laro. • Welcome back! Let's keep going." |
| **HearIt (Prompt)** | `LISTENING` | "Pakinggan natin nang mabuti ang tunog! • Tap the button and listen closely!" |
| **HearIt (Repeat)** | `POINTING` | "Gusto mo bang pakinggan muli? • Ready for the next step?" |
| **SayIt (Idle)** | `POINTING` | "Pindutin ang mic at bigkasin ang tunog! • Tap the mic and say the sound!" |
| **SayIt (Recording)** | `LISTENING` | "Nakikinig ako nang mabuti... Bigkasin nang malinaw! • Listening closely to your voice..." |
| **SayIt (Correct)** | `CELEBRATING` | "Napakagaling! Tamang-tama ang iyong bigkas! • Awesome pronunciation!" |
| **SayIt (Retry)** | `ENCOURAGING` | "Magandang subok! Pakinggan natin ulit at subukan muli. • Good try! Let's try once more." |
| **FindIt (Prompt)** | `POINTING` | "Hanapin ang mga larawang nagsisimula sa tunog na ito! • Find the matching pictures!" |
| **FindIt (Correct Pick)**| `CELEBRATING` | "Tama! Nahanap mo ang isa! • Yes! You found one!" |
| **FindIt (Wrong Pick)**  | `ENCOURAGING` | "Magandang subok! Hanapin pa natin ang iba. • Good try! Let's look again." |
| **FindIt (Completed)**   | `CELEBRATING` | "Nahanap mo silang lahat! Ang galing! • You found them all!" |
| **BlendIt (Prompt)**     | `POINTING` | "Pagsamahin natin ang mga titik para mabuo ang salita! • Let's build this word!" |
| **BlendIt (Hint/Retry)** | `THINKING` | "Hmm, pakinggan natin ang bawat tunog. • Let's sound it out together." |
| **BlendIt (Complete)**   | `CELEBRATING` | "Nabuo mo ang salita! Napakahusay! • You blended the word! Great job!" |
| **Letter Complete**      | `CELEBRATING` | "Yehey! Natapos mo ang aralin! Tingnan ang iyong mga bituin! • Look at those stars!" |

---

## 7. Production Asset Standard & File Placement

All mascot assets are stored as pre-trimmed, density-independent transparent WebP/PNG files with identical anchor baselines:

```
app/src/main/assets/images/mascot/
├── lily_idle.png         # [Asset] Resting posture, gentle breath
├── lily_waving.png       # [Asset] Welcoming greeting wave
├── lily_listening.png    # [Asset] Attentive ear-cup listening pose
├── lily_pointing.png     # [Asset] Directional focus arm extension
├── lily_encouraging.png  # [Asset] Warm supportive retry pose
├── lily_thinking.png     # [Asset] Chin-touch puzzling hint pose
└── lily_celebrating.png  # [Asset] Skyward leap celebration pose
```

### Canvas & Export Specs:
* **Source Dimensions:** 768 × 768px (rendered at 92–160dp depending on screen context).
* **Anchor Point:** Centered horizontally, bottom-aligned at 92% canvas height so state swaps have zero vertical jitter.
* **Format:** Transparent 8-bit sRGB PNG / Lossless WebP.
