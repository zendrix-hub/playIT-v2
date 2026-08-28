# 34 — Google Stitch Master Mockup Specification

> **Purpose:** This document is a complete, self-contained prompt and design specification ready to be pasted or uploaded directly into **Google Stitch** (or any AI UI prototyping tool). It contains the entire design system, color tokens, typography, component rules, character design, and screen-by-screen layouts required to generate the complete visual mockups for **PlayIT**.

---

# PlayIT — Google Stitch Master Prompt & Design System

## 1. Project Overview & Context
- **App Name:** PlayIT
- **Target Audience:** Grade 1 Filipino learners (ages 6–7) and their parents/educators.
- **Pedagogical Framework:** The **Marungko Approach** — a sequential phonics method starting with simple sounds (`M`, `S`, `A`, `I`, `O`, `B`...).
- **App Nature:** Offline-first Android tablet/mobile application with large touch targets, child-friendly gamification, and rich tactile audio-visual feedback.
- **Core Mascot:** **Lily the Tarsier** — a cute, big-eyed, plush Philippine tarsier companion who acts as a co-player and cheerleader.

---

## 2. Design System & Visual Aesthetic: "Gummy Toy / Duolingo ABC"

### 2.1 The "Gummy" Depth Signature
- **Tactile 3D Buttons:** Every interactive surface (buttons, letter tiles, level nodes, picture cards) features a solid face fill with a **4–6dp darker depth band (-20% luminance)** along the bottom edge, simulating a physical plastic toy button.
- **Stroke / Outlines:** Child-facing elements are encased in a solid **3dp dark charcoal outline (`#2D3748`)**.
- **Playful Irregularity:** Picture cards and letter cards have a subtle, organic **-2° to +2° rotation** to feel hand-placed rather than rigid.
- **Generous Corner Radii:** Buttons have a **32dp pill radius**, learning cards have a **28dp corner radius**, and level nodes / letter tiles are **full perfect circles**.

### 2.2 Color Palette & Semantic Depth Tokens
| Role | Face Hex | Depth Shadow Hex (-20% L) | Purpose |
| :--- | :--- | :--- | :--- |
| **Learning Blue** (Primary) | `#4A90E2` | `#2F6FBF` | Main CTAs, navigation, active letter cards |
| **Growth Green** (Success) | `#4CAF50` | `#357A38` | Correct answers, continue buttons, completed badges |
| **Achievement Gold** (Rewards) | `#FFC107` | `#C99000` | Stars, trophy cards, unlocked reward chests |
| **Energy Orange** (Streak/Active) | `#FF9800` | `#C97200` | Streak flame counter, energetic accents |
| **Friendly Purple** (Blend It) | `#8E7DF2` | `#6656C9` | "Blend It" word challenge screens & boss nodes |
| **Gentle Correction Orange** (Error) | `#FFB74D` | `#D9922E` | Incorrect answers, retry cues (**NEVER RED**) |
| **Soft Sky** (Background) | `#EAF6FF` | — | Canvas background for daytime learning screens |
| **Cream White** (Surface) | `#FFFDF8` | — | Card containers, dialog backgrounds |
| **Text Primary / Outlines** | `#2D3748` | — | High-contrast typography and 3dp borders |
| **Text Secondary** | `#718096` | — | Subtitles and parent dashboard labels |

> ⚠️ **CRITICAL NON-PUNITIVE RULE:** Never use a harsh red "X", red flashing background, or buzzer sound for mistakes. Errors must always display in **Gentle Correction Orange** with encouraging prompts (e.g., *"Let's try again together!"*).

### 2.3 Typography Specs
- **Font Family:** Rounded, high-legibility sans-serif with **single-story 'a' and 'g'** (e.g., **Lexend** or **Nunito**).
- **Type Scale:**
  - **Display Large (40sp / ExtraBold):** Hero letter titles, milestone celebrations.
  - **Heading (28sp / Bold):** Screen titles, primary instructions.
  - **Subheading (22sp / SemiBold):** Activity prompts, word targets.
  - **Child Body Floor (24sp / Medium):** Reading-critical phoneme text, speech bubbles.
  - **Adult UI (16sp–18sp):** Parent Dashboard metrics and reports only.

### 2.4 Touch Target Minimums
- **Child Screens:** Minimum **64dp × 64dp** for all clickable tiles, buttons, mic controls, and map nodes.
- **Adult Screens (Parent Dashboard):** Minimum **48dp × 48dp**.

---

## 3. Character Design: Lily the Tarsier
- **Species:** Philippine Tarsier (Bohol native symbol).
- **Style:** Stylized, plush, and ultra-cute (not realistic). Oversized head-to-body ratio ($1:1.2$), huge warm round eyes with bright highlight dots, soft rounded ears, thick short plush paws, and a short rounded tail.
- **Role on Screen:** Anchored at the bottom left/center, occupying **25%–30% of screen height** on activity screens. Dialogue emerges via a rounded white speech bubble with a 3dp charcoal stroke.
- **Emotional States:**
  - *Happy / Idle:* Friendly smile, gentle breathing animation.
  - *Listening (Say It):* Leaning forward with hand near ear.
  - *Thinking:* Tilting head with thoughtful eye glance.
  - *Encouraging (Retry):* Warm reassuring smile with open paws.
  - *Celebrating:* Hopping up with arms raised and star confetti.

---

## 4. Screen-by-Screen UI Mockup Requirements

### Screen 1: Map Screen (`MapScreen`) — Adventure World Hub
- **Header Status Bar (Floating Gummy Container):**
  - Left: Child profile avatar circle + Name pill.
  - Center: Streak flame icon + counter (e.g., 🔥 `3`).
  - Right: 3 Gummy Hearts (`❤️❤️❤️`) + Total Star Counter (`⭐ 14`).
  - Far Right: "Parent Zone" lock button.
- **Main World View:**
  - Vertical scrolling winding island path through playful biomes (Tropical Beach $\rightarrow$ Lush Jungle $\rightarrow$ Mountain Mist).
  - **Letter Level Nodes (64dp Circular Gummy Buttons):**
    - Unlocked / Completed: Growth Green or Learning Blue with 1–3 golden stars sitting on top.
    - Active Current Node: Bouncing/pulsating Learning Blue node with a "Play" indicator.
    - Locked Node: Soft Gray (`#CBD5E0`) with a clean padlock icon.
  - **Milestone Boss Node (Every 4 Letters):** A larger Friendly Purple circular node labeled **"Blend It!"** with a treasure chest or golden ribbon.
  - **Mascot Presence:** Lily the Tarsier perched joyfully next to the current active node.

---

### Screen 2: Hear It Screen (`HearItScreen`) — Audio Modeling
- **Top Bar:** Back arrow, Heart status, current letter indicator (e.g., Letter `M`).
- **Center Hero Area:**
  - A large Cream White learning card with 3dp dark outline.
  - Giant letter display (**M m**) in 48sp ExtraBold.
  - High-quality, colorful illustration card representing a culturally relevant word (e.g., **Manok** / Chicken or **Mangga** / Mango).
  - Floating Gummy Speaker Button (64dp Learning Blue) that pulsates with audio rings when playing the phoneme sound `/m/`.
- **Bottom Section:**
  - Lily the Tarsier at bottom left with speech bubble: *"Listen to the sound: /m/ as in Manok!"*
  - Big Gummy "Next" Button (Growth Green, 64dp height, pill shape) at bottom right.

---

### Screen 3: Say It Screen (`SayItScreen`) — Voice Practice
- **Top Bar:** Step 2 indicator: "Say It", Hearts display.
- **Center Display:**
  - Clean target letter card showing uppercase & lowercase `M m`.
  - Mascot or visual cue demonstrating mouth shape / pronunciation hint.
- **Central Action:**
  - Giant **Gummy Microphone Button (84dp, Learning Blue / Growth Green)**.
  - Floating reactive soundwave animation rings around the mic when the child speaks.
  - Real-time friendly state pill: *"Listening..."* $\rightarrow$ *"Awesome pronunciation!"*
- **Footer:** Lily the Tarsier cheering on the child with encouraging gesture.

---

### Screen 4: Find It Screen (`FindItScreen`) — Discrimination Game
- **Top Prompt:** Large audio trigger banner: *"Find the picture that starts with /m/!"* with replay speaker button.
- **Center Game Area:**
  - **2 × 2 Grid of Chunky Picture Cards (80dp–100dp each):**
    - Card A: *Manok* (Correct)
    - Card B: *Aso* (Dog)
    - Card C: *Saging* (Banana)
    - Card D: *Mesa* (Table)
  - Cards have 3dp dark borders, slight organic rotations, and gummy depth.
- **Feedback State:**
  - Tap correct: Card bounces with Growth Green glow and sparkles.
  - Tap incorrect: Card does a gentle horizontal wobble in Gentle Correction Orange; Lily says *"Good try! Let's listen again."*

---

### Screen 5: Blend It Challenge Screen (`BlendItScreen`) — Word Synthesis
- **Theme:** Rich **Friendly Purple (`#8E7DF2`)** adventure ambiance with starry or magical borders.
- **Header:** Level title "Blend It Checkpoint!" + Heart bar.
- **Word Construction Slot Area:**
  - Empty magnetic drop slots (e.g., `[ _ ] [ _ ] [ _ ]` for `M - A - T`).
  - Target object silhouette or clue image at center.
- **Tile Bank (Bottom Row):**
  - Chunky circular / square letter tiles (`M`, `A`, `S`, `T`) with thick gummy depth bands.
  - Child taps or drags tiles into the word slot.
- **Footer:** Lily dressed in a playful explorer or wizard cape providing hints.

---

### Screen 6: Celebration & Win Screen (`LetterComplete` / `BlendItComplete`)
- **Center Stage:**
  - Big 3D golden star trophy or letter badge sparkling with light rays.
  - 3 Star drop-in slots where 1, 2, or 3 Achievement Gold stars slam down with a juicy bounce.
- **Mascot Action:**
  - Lily the Tarsier jumping in celebration with colorful confetti streamers.
- **Stats Card:**
  - Accuracy badge: *"100% Correct!"*
  - Streak bonus: *"+1 Day Streak!"*
  - Hearts preserved: *"3/3 Hearts Kept!"*
- **Primary CTA:** Full-width chunky Gummy "Continue" button in Growth Green (`#4CAF50`).

---

### Screen 7: Profile Selection & Creation (`ProfileSelect` & `NamePrompt`)
- **Profile Select:**
  - Colorful grid of child avatar cards with bright circular gummy frames.
  - Big dashed-border "+" button for "Add New Learner".
- **Name & Avatar Creation Modal:**
  - Fun animal avatar picker (Tarsier, Carabao, Eagle, Pawikan).
  - Large friendly keyboard or letter tile picker for typing child's first name.
  - Giant "Let's Play!" start button.

---

### Screen 8: Parent Dashboard & Progress Report (`ParentDashboard` & `ReportPreview`)
- **Arithmetic Gate Dialog:**
  - Parental lock modal asking: *"Solve to enter Parent Zone: 8 + 6 = ?"*
- **Dashboard Layout (Clean, organized Material 3 aesthetic):**
  - Child switcher tabs at top.
  - **Mastery Summary Cards:**
    - Letters Mastered: `12 / 28`
    - Pronunciation Accuracy: `88%`
    - Average Daily Session: `15 mins`
  - **Phoneme Breakdown Heatmap:** Color-coded chip grid showing mastered letters (Green), in-progress (Yellow), and needs review (Orange).
  - **Primary Action:** Gummy "Download PDF Progress Report" button.

---

## 5. Summary of Negative Prompts / Prohibited Patterns
- ❌ **NO Red Error Banners or "X" icons:** Mistakes must use Gentle Correction Orange (`#FFB74D`).
- ❌ **NO Flat / Lifeless Corporate Buttons:** All buttons must have a $-20\%$ luminance gummy depth band.
- ❌ **NO Tiny Clickables:** Child touch targets must never be below $64\text{dp}$.
- ❌ **NO Double-story 'a' and 'g':** Letters must look like handwritten early-reader letterforms.
- ❌ **NO Busy Text-Heavy Layouts on Kids Screens:** Instructions must always be accompanied by audio / speaker cues.

---

## 6. How to Export from Google Stitch & Merge into PlayIT

Once the designs are generated and approved:
1. In Google Stitch, click **Export** $\rightarrow$ **Download ZIP** (or export Figma / SVG / HTML/CSS / React assets).
2. Drop the exported `.zip` file into the root of this workspace.
3. Notify the assistant: *"I've uploaded the Stitch mockup zip. Please inspect and integrate it."*
4. The assistant will:
   - Extract and optimize vector icons and background drawables into `app/src/main/res/drawable/`.
   - Map color and typography tokens into `Color.kt` and `Type.kt`.
   - Implement the gummy button, map node, and screen layouts directly in Jetpack Compose.
