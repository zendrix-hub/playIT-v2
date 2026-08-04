# 03 — Design System Summary

Condensed from `Design_System.pdf` v1.0, the **approved, shipped design system** — treat it as authoritative for every value below *except* where §5 flags a specific, recommended override informed by the research documents. Do not invent colors, type sizes, or spacing values outside this document without adding a note to `13_MASTER_TASKS.md`.

## 1. Design Philosophy

Clarity before decoration · Encouragement before correction · Audio before reading · Progress visibility at all times · Consistency across all learning activities · Child-friendly interactions with large touch targets · Cultural familiarity for Filipino learners.

> "Every interaction should make a child feel successful, capable, and motivated to continue learning."

## 2. Color Palette (as shipped — see §5.1 for a flagged conflict)

| Role | Hex | Use |
|---|---|---|
| Learning Blue (Primary) | `#4A90E2` | Primary actions, navigation, active states |
| Growth Green | `#4CAF50` | Correct answers, success, completion |
| Achievement Gold | `#FFC107` | Stars, completed nodes, rewards, milestones |
| Energy Orange | `#FF9800` | Encouragement, active streaks, mascot excitement |
| Friendly Purple | `#8E7DF2` | Blend It / challenge screens |
| Soft Sky | `#EAF6FF` | Large backgrounds |
| Cream White | `#FFFDF8` | Cards, containers |
| Gentle Correction Orange (error) | `#FFB74D` | Incorrect answers, retry prompts, guidance — **never** a harsh red, flashing screen, or "X" |
| Text Primary | `#2D3748` | |
| Text Secondary | `#718096` | |
| Border | `#E2E8F0` | |
| Disabled | `#CBD5E0` | |

**Hard rule carried over unchanged:** no large red "X," no flashing red, no punishment visuals — incorrect answers are always Gentle Correction Orange, never red.

## 3. Typography (as shipped — see §5.2 for a flagged conflict)

- Primary: **Nunito**, Fallback: **Poppins** (rounded, child-friendly, readable).
- Scale: Display Large 40sp/ExtraBold (letter cards, celebrations) · Heading 28sp/Bold (screen titles) · Subheading 22sp/SemiBold (instructions) · Body 18sp/Medium (mascot messages, parent dashboard) · Caption 16sp/Regular (helper text) — **never below 16sp anywhere.**

## 4. Spacing, Touch Targets, Components

- **Base unit 8dp**, scale: 4/8/16/24/32/48/64dp (tiny → celebration layouts).
- **Touch targets:** Minimum 48dp×48dp · Recommended 56dp×56dp · Important actions 64dp+. Applies to letter tiles, picture cards, buttons, replay controls, map nodes.
- **Buttons:** Primary (56dp height, 28dp radius, Learning Blue, white text, spring-bounce on tap) · Success ("Claim Reward," Achievement Gold, dark gray text) · Secondary ("Replay Audio," Cream White + 2dp Learning Blue border).
- **Cards:** Learning Card (24dp radius, 4dp elevation, 24dp padding, Cream White) · Reward Card (32dp radius, Achievement Gold gradient).
- **Motion durations:** Micro 150–250ms · Standard 300–500ms · Celebration 600–1200ms. Required animations: tap feedback (scale 100→92→100%), breathing-pulse active map node, star reward (drop/bounce/glow + confetti + sound), incorrect answer (gentle shake + encouragement audio, **never** harsh flashing), screen entry (fade + slight upward motion, 200–300ms).

## 5. Flagged Conflicts With the Research Documents

The eight research reports were produced independently of the shipped Design System and, in three places, recommend materially different numbers. None of these should be silently split-the-difference — each gets one recommended resolution:

### 5.1 Color palette — shipped hex values vs. `Color_Palette.md`'s WCAG-engineered palette
`Color_Palette.md` proposes a different Material 3 HCT-based palette (Primary `#00687A` deep teal, Secondary `#6D5A00` earthy gold, Tertiary `#984700` terracotta, Success `#1B6C31`, Warning `#7A5900`, a **restricted-use Error red `#B3261E`**, background `#FAF8F5`), each with documented WCAG contrast ratios. The shipped Design System's hex values have no published contrast ratios at all.

**Resolution:** keep the shipped palette as the source of truth for brand identity (it's the approved deliverable and every SDD mockup and this document's own component specs are built around its named roles) — but before final visual QA, **run every shipped color pairing through a WCAG 2.2 contrast check** (4.5:1 normal text / 3.0:1 large text/UI, per `Color_Palette.md`'s own methodology) and fix any pairing that fails. Separately, adopt two specific practices from the research regardless of which hex values ship: (a) **never rely on color alone** — pair every state (correct/incorrect/locked) with a distinct icon/shape *and* text, since the shipped system doesn't state this as explicitly as the research does; (b) **add one narrowly-scoped destructive-red token** (e.g., `#B3261E`, mirroring the research) reserved *only* for true destructive/system dialogs (delete profile, storage full) — the shipped palette currently has no red at all, which leaves that dialog category undefined.

### 5.2 Typography — Nunito/Poppins vs. `Typography_Guide.md`'s Lexend/Andika recommendation
This is the highest-stakes conflict in the whole package, because it's pedagogical, not cosmetic. `Typography_Guide.md` argues — with specific, well-sourced reasoning — that early readers (ages 5–8) need **single-story 'a' and 'g'** letterforms (matching how children are taught to handwrite), generous x-height/counters, and a body-text floor around 24sp with ≤36-character line length. Nunito and Poppins are both standard double-story sans-serifs; the shipped Design System's body floor is 18sp.

**Recommendation:** switch the app's primary/fallback typefaces to **Lexend (variable) / Andika (static fallback for API<26 edge cases or missing glyph coverage)** for all *reading-critical, child-facing* text — letter cards, phoneme labels, word displays, Blend It tiles, mascot dialogue. Lexend is itself derived from Quicksand (rounded, friendly, geometric), so this does not sacrifice the "child-friendly, rounded" brief the Design System asks for — it *better* satisfies it while also fixing the single-story-letterform gap. Raise the reading-content body floor to the Typography Guide's 24sp/bodyLarge for anything the child is meant to sound out; the shipped 16sp/18sp scale can remain as-is for **adult-only surfaces** (Parent Dashboard, Report Preview) where reading fluency isn't being taught. Full type-scale mapping is in `10_UI_IMPLEMENTATION_GUIDE.md §2`. Flag this switch explicitly to the requirements owner before implementation — it is a visual-identity change, not just a technical substitution.

### 5.3 Touch targets — 48dp shipped minimum vs. multiple research sources converging on 64dp
`Color_Palette.md` cites the WCAG 2.2 legal floor (24×24 CSS px) but recommends 44–48dp; `UI_Research.md`'s age-segmented HCI table recommends **64dp for ages 6–8**; `UX_Research.md` cites Nielsen Norman Group guidance of ~2cm×2cm (materially larger than 48dp); `Typography_Guide.md` independently arrives at **64dp minimum** for all interactive typographic elements. Three of four research sources converge on 64dp; the shipped Design System already has a 64dp tier, but reserves it for "Important Actions" only, with 48dp as the general floor.

**Resolution:** for playIT specifically — a 6–7-year-old's *only* interface — **treat 64dp as the default minimum for every primary child-facing interactive element** (letter tiles, picture cards, mic button, map nodes, all buttons on child-facing screens), not just "important" ones. Reserve the 48dp/56dp tiers for **adult-only surfaces** (Parent Dashboard controls, Report Preview). This doesn't contradict the shipped system — it resolves an ambiguity in "important" by defining the whole child-facing surface as important, per the converging research.

## 6. Mascot, Voice, Feedback, Sound, Accessibility (carried over unchanged)

- **Mascot personality:** friendly teacher/coach/cheerleader — never a judge, supervisor, or scorekeeper. States: Happy (correct), Excited (milestones), Thinking (hints), Encouraging (after mistakes), Celebrating (level complete). Every mascot message pairs on-screen text with audio — never text-only.
- **Feedback language:** never "Wrong!" / "You lost a heart." → always "Good try! Let's listen again." / "Let's practice one more time."
- **Sound design:** distinct cue per action — correct (reward chime), incorrect (soft pop), heart loss (gentle whoosh), heart recovery (bright sparkle), node unlock (magical chime), level complete (celebration fanfare).
- **Accessibility (shipped rules, unchanged):** text + audio for all instructions, always · color never the sole signal (pair with icon/shape/label/animation) · text contrast 4.5:1 min, Parent Dashboard 7:1 preferred · reduced-motion mode must be supported (replace particle effects/bouncing with fades) — **see `13_MASTER_TASKS.md`: there is currently no Settings screen anywhere in the 12-screen inventory to host this toggle; needs a small scope addition** · one primary goal and one primary CTA per screen.
