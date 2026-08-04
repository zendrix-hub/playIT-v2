# 22 — File Naming Convention

Single reference for every naming rule scattered across `07`, `08`, `14`–`21`. All lowercase, `snake_case`, ASCII where possible (see the `ñ` note below).

## 1. Audio

| Type | Pattern | Example |
|---|---|---|
| Phoneme model | `phoneme_<letter>.mp3` | `phoneme_m.mp3`, `phoneme_ng.mp3` |
| Blend It word | `word_<lowercase_word>.mp3` | `word_sam.mp3` |
| UI/SFX | `sfx_<event>.mp3` | `sfx_correct_chime.mp3` |
| Mascot voice-over | `vo_<context>_<2-digit-index>.mp3` | `vo_encourage_01.mp3` |

**`ñ` filename note:** use `phoneme_nti.mp3` or an agreed ASCII transliteration (not a literal `ñ` character) for the filename, to avoid filesystem/encoding portability issues across build machines — reserve the literal `ñ` glyph for the `Phoneme.letter` database column and on-screen display text only, never for a file path.

## 2. Images

| Type | Pattern | Example |
|---|---|---|
| Letter card | `letter_<letter>.png` | `letter_m.png` |
| Example-word illustration | `word_<lowercase_word>.png` | `word_mouse.png` (shared naming root with audio's `word_` prefix is intentional — same concept, different extension, easy to grep as a pair) |
| Find It picture | `picture_<lowercase_word>.png` | `picture_apple.png` |
| Blend It illustration | `blendword_<lowercase_word>.png` | `blendword_sam.png` (distinct prefix from the letter-level `word_` to avoid collisions where a Blend It word and a Hear It example word coincide) |
| Mascot pose | `mascot_<pose>.png` | `mascot_happy.png`, `mascot_listening.png` |
| Avatar option | `avatar_<index>.png` | `avatar_01.png` |
| Map prop | `mapprop_<name>.png` | `mapprop_pencil_tower.png` |
| Icon | `icon_<name>_<state>.xml` (vector drawable) | `icon_mic_idle.xml`, `icon_mic_listening.xml` |
| Reward/celebration | `reward_<name>.png` | `reward_confetti_burst.png` |

## 3. Animations

| Type | Pattern | Example |
|---|---|---|
| Lottie JSON | `anim_<name>.json` | `anim_star_reward.json`, `anim_confetti_burst.json` |

## 4. Database

- Table names: `PascalCase`, singular noun (`Profile`, `LessonProgress`, `BlendItAttempt`) — matches `08_DATABASE_SPEC.md` exactly; do not pluralize table names.
- Column names: `camelCase` (`profileId`, `heartsLost`, `completedAt`).
- Foreign key columns: `<referencedEntity>Id` (`profileId`, `phonemeId`, `groupId`, `wordId`).

## 5. Packages (Kotlin)

- All-lowercase, no underscores: `com.playit.app.domain.manager`, `com.playit.app.data.local.entity` — matches `07_FOLDER_STRUCTURE.md`.

## 6. Kotlin Files

- One class/interface per file, filename matches the type name exactly: `HeartManager.kt`, `ProfileRepository.kt`, `ProfileRepositoryImpl.kt`.
- Composable-heavy files may group a screen with its immediate private helper Composables in one file (`MapScreen.kt`) but every *named, reusable* component listed in `07_FOLDER_STRUCTURE.md`'s component lists gets its own file under that screen's `components/` folder.

## 7. Resources (Android `res/`)

- This project stores gameplay assets in `assets/`, not `res/raw/drawable/` (`07_FOLDER_STRUCTURE.md` note) — reserve `res/` for standard Android framework needs (app icon, standard string resources not tied to gameplay content, themes.xml if any XML theme scaffolding is needed alongside the Compose theme).
- Standard Android `res/` naming conventions apply where `res/` is used (`ic_<name>` for drawables if any framework-level icons are needed outside the vector-icon set in `20_ICON_GUIDE.md`, `strings.xml` keys in `snake_case`).

## 8. Assets Folder Root (recap of `07_FOLDER_STRUCTURE.md §Notes`)

```
assets/audio/phonemes/, assets/audio/words/, assets/audio/ui/, assets/audio/vosk-model-small-en-us/
assets/images/letters/, assets/images/pictures/, assets/images/mascot/, assets/images/backgrounds/, assets/images/rewards/
assets/fonts/
```

## 9. Version/Build Tagging (for asset QA, not user-facing)

- Any placeholder asset used during early implementation phases (`06_IMPLEMENTATION_ROADMAP.md` Phases 0–6) should be prefixed `placeholder_` (e.g., `placeholder_letter_m.png`) so a build-time content-completeness check can grep for the prefix and fail the Phase 7 "asset integration complete" exit criteria if any remain (`06 §Phase 7`).
