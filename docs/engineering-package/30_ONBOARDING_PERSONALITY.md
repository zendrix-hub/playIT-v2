# 30 — Onboarding & Profile: World Introduction

> Covers `SplashScreen`, `ProfileSelectScreen`, `NamePromptScreen`. This is the child's — and often the parent setting the tablet up beside them — first 30 seconds with the app, so it carries disproportionate weight for how "toy" vs. "form" the whole app reads from the first frame.

## 1. `SplashScreen` redesign

`10 §5` already specifies the hard requirement correctly: loading must actively mask Vosk-model + Room-init latency (≤5s worst case) with something that reads as alive, never a static spinner. Concrete treatment:

- Background: sky-gradient + spotlight highlight + 2 drifting clouds (`25 §5`).
- Lily enters via `WAVING` (`26 §3`), positioned centered, appearing to peek in from the bottom edge with the existing hop-in spring pattern, then settling into a slow idle-breathing loop (`breathingPulse`, already-established modifier) that continues for however long init actually takes — this is what "masks" the latency, since the loop has no fixed end time and simply continues until data is ready.
- App wordmark/logo fades and settles in above her (200–300ms fade + slight upward motion, matching the existing screen-entry standard from `03 §4`) shortly after her entrance, not simultaneously — staggering the two keeps the frame from feeling cluttered on entry.
- **Ready cue**: once Vosk+Room init completes, Lily briefly shifts from her idle loop to `POINTING`/a small forward nod (~300ms) before the screen navigates away, so the transition reads as "let's go" rather than an abrupt cut the moment loading finishes.

## 2. `ProfileSelectScreen` redesign

- `ProfileCard`s become `GummyContainer`s (they're tappable) rather than plain cards — circular avatar inset at top, name in Lexend `Bold` below, small `AchievementGold` star-count chip in a corner. 28dp corner radius matching the Learning Card standard (`23 §3`).
- `AddProfileButton`: styled as a `GummyIconButton` (a "+" glyph) at the same visual weight as a real `ProfileCard` so it reads as "add a new adventurer," not a secondary utility action tucked in a corner.
- `AvatarPicker` (on `NamePromptScreen`'s creation flow): each curated animal avatar option rendered as its own small `GummyIconButton`-style circle, selected state indicated by a `DarkBrownOutline` ring thickening + slight scale-up (1.0 → 1.08), consistent with how selection states are shown elsewhere in the gummy system rather than introducing a new selection-indicator pattern. No change to the underlying rule: curated avatars only, no free-text/photo upload, no external image picker (`10 §5`).
- Mascot: `WAVING`/`ENCOURAGING` energy, background per `25 §5` (sky gradient + spotlight, lighter than Splash's since the `ProfileCard` grid — not Lily — is this screen's visual anchor).

## 3. `NamePromptScreen` redesign

- Input field: a large, chunky `GummyStaticContainer`-styled text field — rounded-rect, `DarkBrownOutline` stroke, 64dp+ height, Lexend 28sp text — that invokes the **standard system keyboard** on tap. This deliberately does *not* mean building a fully custom on-screen keyboard: a bespoke big-key keyboard is a substantial engineering investment (custom key layout, autocapitalization, input validation, accessibility-service compatibility) for a component the child uses for a few seconds once per profile creation. A large, obviously-tappable, gummy-styled field wrapping the system IME delivers the "big friendly input" feeling from the original brief at a fraction of the implementation risk. [CHECK WITH AGENT: if a fully custom keyboard is genuinely required by a stakeholder for this specific screen, that's a scoping decision for `13_MASTER_TASKS.md`, not something to build by default here.]
- Lily: `ENCOURAGING`/`WAVING` energy with the "What's your name?" line (`26 §6`), then a warm confirmation line once a name is entered (e.g., "Great name, {name}!") before the app proceeds — small personalization moment that costs nothing extra to build since the name is already captured at this point in the flow.

## 4. The first 30 seconds — overall arc

1. **Splash (0–5s, masked by loading)**: world + mascot introduction. The child sees Lily and the forest-trail world identity (`27 §1`) before anything else — this is the single frame most responsible for "toy world" vs. "generic app" as a first impression.
2. **ProfileSelect**: "Whose adventure is this?" — tap an existing profile, or `AddProfileButton` into name creation.
3. **NamePrompt** (new profile only): quick name entry, warm confirmation.
4. **Transition into `MapScreen`**: Lily welcomes the child by name if available, the map auto-scrolls to the first available node (`27 §7`), and a `POINTING`/ambient-bubble cue (`27 §5`) directs the very first tap.

**Continuity principle**: Lily should feel like the same character bridging every one of these screens — not a fresh mascot instance introduced per screen. Concretely, that means her `MascotState` transitions (`WAVING` → `ENCOURAGING` → `POINTING`, etc.) should read as one continuous character's reactions across the whole arc, not four separate "mascot appears" moments each restarting from scratch. This is the practical test for whether the co-player framing (`26 §1`) actually lands in the first-run experience, since first run is where a child forms their read of who Lily is.
