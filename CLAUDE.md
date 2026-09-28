@AGENTS.md

# Refactor rules (Hear It / Say It)
- Spec: docs/specs/hear-say-refactor.md wins over docs/specs/week3-changeset.md.
- docs/specs/validation-report.md is reference only; do not implement from it.
- One refactor step per session. Cite the requirement ID in commit messages.
- [confirm] or [proposed] in the spec means stop and ask me.
- Say It never removes hearts. Letter names and added vowels are foils, never accepted.
- Every change updates or adds a unit test. Run ./gradlew testDebugUnitTest before finishing.
- Offline only: no network calls.

# Decisions (Sep 2026, adviser confirmation pending)
- Use spec defaults for [confirm] items: Say It never removes hearts; ASR target >=80% agreement, false rejects <=15%.
- Say It is hybrid: echo step stays in word mode ("mouse"); recall check uses the pure sound only if the Vosk spike passes.
- After each step: tests pass, then commit on refactor/hear-say-it with the requirement ID. Never push; I push.
- Audio source is Kokoro-82M (Apache-2.0), run in Google Colab via tools/audio/playit_audio.ipynb. Claude writes and audits the pipeline; it does not generate or edit audio files. The offline rule applies to the app, not the Colab notebook.
