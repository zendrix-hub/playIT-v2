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
- Idle re-prompt after 10 s of no interaction (spec Table 2 [proposed]; user decision 2026-10-01).
- Onboarding is avatar-only: the child never has to type. A parent can add or change the name in the Parent Zone (user decision 2026-10-01).
- Say It is hybrid: the echo step stays in word mode ("mouse"); the recall check uses the pure sound only if the Vosk spike passes (`docs/spikes/vosk-foil-spike.md`).
- Audio source is Kokoro-82M (Apache-2.0) for every spoken line. Claude owns the pipeline and runs it locally in WSL (`tools/audio/`; line texts in `tools/audio/tutor_script.py`); `tools/audio/playit_audio.ipynb` is the Colab backup and lags behind the local tools. Review batches live outside the repo (`Documents/playIT-audio-batches/`). A clip goes into `app/src/main/assets/audio/` only through an agy card, and only if the user marked it OK in a review page and it is listed in a `docs/audio-release/<date>/manifest.json`. A teacher audit of every shipped clip (and spec Gate 3 for phoneme clips) is required before merging to `main`.
- Held (continuous) sounds: the user picks per clip, by ear, between Kokoro phoneme input and Chatterbox-Turbo (MIT), voice-cloned from a Kokoro reference clip so the voice matches (`tools/audio/chatterbox_lab.py`). The first pick is /m/: Chatterbox "Mmm!" (user decision 2026-10-01; this amends spec NFR-AUD-01 and Table 5, adviser confirmation pending). Short sounds still come from a human model.
- Images (user decisions 2026-10-01): agy generates new and fixed art with its built-in Nano Banana Pro, and regenerates in rounds until the user picks an image for every item. Claude cuts out, checks and builds the review pages. An image reaches `app/src/main/assets/images/` only through an agy card, from a `docs/image-release/<date>/manifest.json` (AGY_RUNBOOK.md "Image gate"). Style: match the best current pictures (flat shapes, dark-brown outline `#4A2E18`, no harsh red), per `16_ILLUSTRATION_STYLE_GUIDE.md`.
- Lily stays the current orange tarsier (`images/mascot/lily_idle.png` is the reference). Her poses may be redrawn for consistency, but not redesigned. This overrides the tan coat and short tail in `17_CHARACTER_DESIGN_GUIDE.md`.
- Stars and hearts bugs are fixed now under the current rules (user decision 2026-10-01); the hearts memo to the adviser may change the rules later.
- Relay nights: agy runs one card, Claude reviews it, then the next card starts (AGY_RUNBOOK.md "Relay mode").

Roles (details in `docs/tasks/AGY_RUNBOOK.md`)
- Claude (in WSL) designs, researches, writes and critiques task cards, reviews every agy commit, writes fix cards, and gives technical acceptance. It commits only docs and `tools/`, never app code.
- agy implements one ready card per session, commits, and pushes.
- The user starts agy sessions, approves audio with a teacher, answers `docs/tasks/QUESTIONS.md`, merges the PR, and gives final approval on [proposed] items with the adviser.

Workflow
- One refactor step per session: work from one `docs/tasks/card-NN-*.md`. Change only the files the card lists, plus the bookkeeping files named in `docs/tasks/AGY_RUNBOOK.md` (`13_MASTER_TASKS.md`, `docs/evidence-log.md`, the card's `Status:` line).
- Every card ticks its own item under "Hear It / Say It refactor" in `docs/engineering-package/13_MASTER_TASKS.md`, in the card's commit.
- Every change adds or updates a unit test; run `./gradlew testDebugUnitTest` before committing. If there is no JDK or Android SDK, don't claim the tests pass: write "Unit tests not run locally; verify in CI" in the commit body. CI on the draft PR to `main` is the test gate.
- The app is offline only: no network calls. The offline rule applies to the app, not to the audio pipeline in `tools/audio/`.
- After each step, commit on `refactor/hear-say-it`, one commit per card (the commit body format is in the runbook). Every commit message includes the requirement ID if the card has one. Push your commits to `origin refactor/hear-say-it`; never push to `main`, never force-push.

For how to execute task cards, follow `docs/tasks/AGY_RUNBOOK.md`.
