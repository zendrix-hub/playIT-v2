# PlayIT — Agent Entry Point

Before doing anything else, read `docs/engineering-package/99_AGENT_BOOTSTRAP.md`.
That file is the single source of truth for what PlayIT is, its architecture,
folder structure, and current status — this file exists only so Antigravity
finds it automatically at session start. Do not duplicate its content here;
if something here and the bootstrap doc ever disagree, the bootstrap doc wins.

Re-read the bootstrap doc after any context reset, per its own §16.

## Quick pointers
- Tech stack, architecture, current phase → `docs/engineering-package/99_AGENT_BOOTSTRAP.md`
- Live task checklist (edit this as work progresses) → `docs/engineering-package/13_MASTER_TASKS.md`
- Full doc index → `docs/engineering-package/00_PROJECT_SUMMARY.md` §8

## Non-negotiables (repeated here because they're easy to violate by accident)
- `domain/` is pure Kotlin — zero `android.*` imports. If a class needs one,
  it belongs in `data/` or `presentation/` instead.
- No new architecture or gameplay-number decisions without checking
  `01_REQUIREMENTS_SUMMARY.md §7` and `03_DESIGN_SYSTEM_SUMMARY.md §5` first —
  most "obvious" numbers (letter counts, star thresholds, colors) have
  already been through conflict resolution there. Don't re-derive them from
  the raw source documents.
- Update `13_MASTER_TASKS.md` in place as tasks complete — no need to ask
  permission to check off a verified, completed task (bootstrap §17).
- **Mockup vs Asset Creation Scope**: The prototype mockup (`playit-mockup.html`) is strictly for UI layout, styling, and animation improvements. Asset creation (illustrations, icons, character designs, audio) remains strictly governed by our original engineering package plan (`14_ASSET_MANIFEST.md`, `15_IMAGE_GENERATION_PROMPTS.md`, `16_ILLUSTRATION_STYLE_GUIDE.md`, anchor style sheet `images/_style-reference-sheet/anchor_letter-card.png`) and must NOT change based on the mockup unless explicitly stated by the user.
- **Zero-Emoji Policy**: Emojis are strictly NOT needed and MUST NOT be used in UI text, button labels, speech bubbles, cards, or titles anywhere across child-facing and adult-facing screens. All visual icons must use clean Android Vector Graphics (`Icons.Filled.*`, `Icons.AutoMirrored.*`) or transparent production PNG assets (`images/rewards/`, `images/pictures/`, etc.). Never append or embed emojis (e.g., 🚀, 🎉, 🍎, 🔥, ⭐, 🔒) in text strings or button labels.
