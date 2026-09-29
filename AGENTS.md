# PlayIT — Agent Entry Point

Before doing anything else, read `docs/engineering-package/99_AGENT_BOOTSTRAP.md`.
That file is the single source of truth for what PlayIT is, its architecture,
folder structure, and current status — this file exists only so Antigravity
finds it automatically at session start. Do not duplicate its content here;
if something here and the bootstrap doc ever disagree, the bootstrap doc wins.
The one exception is the "Hear It / Say It refactor" section below: for the
areas it names, it wins over the bootstrap doc and the engineering-package docs.

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
  permission (bootstrap §17). Tick at commit time. A tick means committed; CI verifies it.
- **Mockup vs Asset Creation Scope**: The prototype mockup (`playit-mockup.html`) is strictly for UI layout, styling, and animation improvements. Asset creation (illustrations, icons, character designs, audio) remains strictly governed by our original engineering package plan (`14_ASSET_MANIFEST.md`, `15_IMAGE_GENERATION_PROMPTS.md`, `16_ILLUSTRATION_STYLE_GUIDE.md`, anchor style sheet `images/_style-reference-sheet/anchor_letter-card.png`) and must NOT change based on the mockup unless explicitly stated by the user.
- **Zero-Emoji Policy**: Emojis are strictly NOT needed and MUST NOT be used in UI text, button labels, speech bubbles, cards, or titles anywhere across child-facing and adult-facing screens. All visual icons must use clean Android Vector Graphics (`Icons.Filled.*`, `Icons.AutoMirrored.*`) or transparent production PNG assets (`images/rewards/`, `images/pictures/`, etc.). Never append or embed emojis (e.g., 🚀, 🎉, 🍎, 🔥, ⭐, 🔒) in text strings or button labels.

## Hear It / Say It refactor (Sep 2026, adviser directive; adviser confirmation pending)

Precedence
- For Hear It, Say It, SpeechValidator, audio assets, and the tutoring flow, `docs/specs/hear-say-refactor.md` and `docs/tasks/` supersede the bootstrap doc and the engineering-package docs where they conflict. Everything else keeps the bootstrap doc as the source of truth.
- `docs/specs/hear-say-refactor.md` wins over `docs/specs/week3-changeset.md`.
- `docs/specs/validation-report.md` is reference only; do not implement from it.

Decisions
- [confirm] and [proposed] items covered by the decisions below use those decisions; note the decision in the commit body. Any other [confirm] or [proposed] item: stop and ask the user (write the question to `docs/tasks/QUESTIONS.md`).
- Say It never removes hearts (spec default). Letter names and added vowels are foils and are never accepted.
- Say It judge targets: at least 80% agreement with teachers; false rejects at most 15% of teacher-correct attempts (spec defaults).
- Say It is hybrid: the echo step stays in word mode ("mouse"); the recall check uses the pure sound only if the Vosk spike passes (`docs/spikes/vosk-foil-spike.md`).
- Audio source is Kokoro-82M (Apache-2.0), run in Google Colab via `tools/audio/playit_audio.ipynb`. Claude writes and audits the pipeline; it does not generate or edit audio files. Only released clips are copied into `app/src/main/assets/audio/`.

Workflow
- One refactor step per session: work from one `docs/tasks/card-NN-*.md`. Change only the files the card lists, plus the bookkeeping files named in `docs/tasks/AGY_RUNBOOK.md` (`13_MASTER_TASKS.md`, `docs/evidence-log.md`, the card's `Status:` line).
- Every card ticks its own item under "Hear It / Say It refactor" in `docs/engineering-package/13_MASTER_TASKS.md`, in the card's commit.
- Every change adds or updates a unit test; run `./gradlew testDebugUnitTest` before committing. If there is no JDK or Android SDK, don't claim the tests pass: write "Unit tests not run locally; verify in CI" in the commit body. CI on the draft PR to `main` is the test gate.
- The app is offline only: no network calls. The offline rule applies to the app, not the Colab notebook.
- After each step, commit on `refactor/hear-say-it`, one commit per card (the commit body format is in the runbook). Every commit message includes the requirement ID if the card has one. Never push; the user pushes.

For how to execute task cards, follow `docs/tasks/AGY_RUNBOOK.md`.
