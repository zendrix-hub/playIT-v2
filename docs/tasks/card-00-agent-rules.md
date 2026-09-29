# Card 00: Agent rules, precedence, housekeeping

Status: done

## Why
The agent reads AGENTS.md, not CLAUDE.md, so the refactor decisions were invisible to it. AGENTS.md also says the engineering-package bootstrap doc wins every conflict; for this refactor the adviser's directive changes that.

## Files
AGENTS.md, CLAUDE.md, .gitattributes (new), docs/specs/hear-say-refactor.md (§6.5 only), docs/engineering-package/13_MASTER_TASKS.md, tools/elevenlabs_voice_studio.py (move), docs/engineering-package/20_* and 33_* (header note only).

## Changes
1. Add this section to AGENTS.md after "Non-negotiables":

   ## Hear It / Say It refactor (Sep 2026, adviser directive)
   - For Hear It, Say It, SpeechValidator, audio assets, and the tutoring flow, `docs/specs/hear-say-refactor.md` and `docs/tasks/` supersede the engineering-package docs where they conflict. Everything else keeps the bootstrap doc as the source of truth.
   - Work from one `docs/tasks/card-NN-*.md` per session. Change only the files the card lists.
   - [confirm] or [proposed] in the spec: use the spec default and note it in the commit body.
   - Say It never removes hearts. Letter names and added vowels are foils and are never accepted.
   - Every change adds or updates a unit test; run `./gradlew testDebugUnitTest` before committing.
   - The app stays offline. Audio is produced outside the app (Kokoro, Apache-2.0) and only released clips are copied into `app/src/main/assets/audio/`.
   - Commit per card with the requirement ID in the message. Never push.

2. Replace the whole body of CLAUDE.md with one line: `@AGENTS.md`
3. Create `.gitattributes` with the single line `* text=auto eol=lf`, then run `git add --renormalize .` and include the result in this commit.
4. In docs/specs/hear-say-refactor.md §6.5, change the source list to: "AI word extraction (Kokoro), Kokoro phoneme input, or human recording".
5. Move tools/elevenlabs_voice_studio.py to docs/archive/tools/. Add one line at the top of docs/engineering-package/20_* and 33_*: "Superseded for audio by docs/specs/hear-say-refactor.md §2.3 (Kokoro, Sep 2026)."
6. Add cards 01 to 04 to 13_MASTER_TASKS.md as unchecked items under a "Hear It / Say It refactor" heading.

## Tests
No code changes. Run ./gradlew testDebugUnitTest anyway to confirm the renormalize step broke nothing.

## Commit
`chore(agents): refactor precedence rules, lf line endings, archive ElevenLabs tool`
