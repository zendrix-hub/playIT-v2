# 08 — Database Specification

Full Room/SQLite schema, compiled from the SDD's per-module ERDs (Modules 1–6) into one system ERD. 12 tables. All FKs cascade-scope to `Profile` — deleting a profile must delete every row that references it (implement `onDelete = CASCADE` in every `@ForeignKey` targeting `profileId`).

## 1. Entity Reference

### `Profile`
| Column | Type | Notes |
|---|---|---|
| `profileId` | `Int` PK, autogenerate | |
| `name` | `String` | child's display name |
| `avatarResId` | `Int` | index into the curated `AvatarPicker` set (`14_ASSET_MANIFEST.md`) |
| `totalStars` | `Int` | denormalized aggregate, recomputed on write for dashboard speed |
| `currentStreak` | `Int` | |
| `lastPlayedAt` | `Long` | epoch millis, drives streak-reset logic |
| `createdAt` | `Long` | epoch millis |

App-level constraint (not DB-enforced): max 6 rows — check count in `ProfileRepositoryImpl.create()` before insert, per `01_REQUIREMENTS_SUMMARY.md §5`.

### `Phoneme`
| Column | Type | Notes |
|---|---|---|
| `phonemeId` | `Int` PK | seed with a stable ID per letter — recommend Marungko sequence order, 1–28 |
| `letter` | `String` | `"m"`, `"ng"`, `"ñ"`, etc. |
| `audioPath` | `String` | asset path, see `22_FILE_NAMING_CONVENTION.md` |
| `imagePath` | `String` | example-word illustration asset path |
| `exampleWord` | `String` | e.g. `"Mouse"` — **`ng` and `ñ` rows ship with `exampleWord = "PENDING_SME_REVIEW"` until content is approved (`01 §5`)**; treat this sentinel as a build-time content-QA check, not a runtime string ever shown to a child |

### `LetterGroup`
| Column | Type | Notes |
|---|---|---|
| `groupId` | `Int` PK | 1–7 |
| `groupNumber` | `Int` | display order, same as `groupId` in practice |

### `LetterGroupMember`
| Column | Type | Notes |
|---|---|---|
| `memberId` | `Int` PK, autogenerate | |
| `groupId` | `Int` FK → `LetterGroup` | |
| `phonemeId` | `Int` FK → `Phoneme` | |
| `position` | `Int` | 0–3, order within the group |

### `LessonProgress` (per profile, per letter — the Hear It/Say It/Find It rollup)
| Column | Type | Notes |
|---|---|---|
| `id` | `Int` PK, autogenerate | |
| `profileId` | `Int` FK → `Profile` (cascade) | |
| `phonemeId` | `Int` FK → `Phoneme` | |
| `starsEarned` | `Int` | 0–3 |
| `heartsLost` | `Int` | cumulative across Say It + Find It for this letter |
| `isCompleted` | `Int` (boolean) | |
| `completedAt` | `Long` | epoch millis |

Unique index on `(profileId, phonemeId)` — one progress row per letter per profile; upsert with `OnConflictStrategy.REPLACE`.

### `SayItAttempt`
| Column | Type | Notes |
|---|---|---|
| `attemptId` | `Int` PK, autogenerate | |
| `profileId` | `Int` FK → `Profile` (cascade) | |
| `phonemeId` | `Int` FK → `Phoneme` | |
| `isCorrect` | `Int` (boolean) | |
| `attemptedAt` | `Long` | epoch millis |

### `FindItAttempt`
| Column | Type | Notes |
|---|---|---|
| `attemptId` | `Int` PK, autogenerate | |
| `profileId` | `Int` FK → `Profile` (cascade) | |
| `phonemeId` | `Int` FK → `Phoneme` | the target being tested |
| `selectedPhonemeId` | `Int` FK → `Phoneme` | which picture the child actually tapped (target or a distractor's source letter) |
| `isCorrect` | `Int` (boolean) | |
| `attemptedAt` | `Long` | |

### `BlendItWord`
| Column | Type | Notes |
|---|---|---|
| `wordId` | `Int` PK | |
| `groupId` | `Int` FK → `LetterGroup` | the group whose completion unlocks this word into the pool |
| `word` | `String` | e.g. `"SAM"` |
| `wordPattern` | `String` | letter-by-letter breakdown for tile generation, e.g. `"S-A-M"` |
| `audioPath` | `String` | |
| `imagePath` | `String` | |

Seed content: see `19_AUDIO_SCRIPTS.md` for the draft 7-group word bank (flagged as SME-pending for final sign-off).

### `BlendItAttempt`
| Column | Type | Notes |
|---|---|---|
| `attemptId` | `Int` PK, autogenerate | |
| `profileId` | `Int` FK → `Profile` (cascade) | |
| `groupId` | `Int` FK → `LetterGroup` | |
| `wordId` | `Int` FK → `BlendItWord` | |
| `isCorrect` | `Int` (boolean) | |
| `attemptedAt` | `Long` | |

### `BlendItProgress` (per profile, per group)
| Column | Type | Notes |
|---|---|---|
| `id` | `Int` PK, autogenerate | |
| `profileId` | `Int` FK → `Profile` (cascade) | |
| `groupId` | `Int` FK → `LetterGroup` | |
| `starsEarned` | `Int` | via `BlendItStarThresholds`, `01 §7.4` |
| `heartsLost` | `Int` | |
| `isCompleted` | `Int` (boolean) | |
| `completedAt` | `Long` | |

Unique index on `(profileId, groupId)`.

### `Achievement`
| Column | Type | Notes |
|---|---|---|
| `achievementId` | `Int` PK, autogenerate | |
| `profileId` | `Int` FK → `Profile` (cascade) | |
| `title` | `String` | e.g. `"5-Day Streak"` |
| `isUnlocked` | `Int` (boolean) | |
| `unlockedAt` | `Long` | |

### `ReportLog`
| Column | Type | Notes |
|---|---|---|
| `reportId` | `Int` PK, autogenerate | |
| `profileId` | `Int` FK → `Profile` (cascade) | |
| `filePath` | `String` | local storage path of the generated PDF |
| `generatedAt` | `Long` | |

## 2. Relationship Summary

```
Profile 1───* LessonProgress *───1 Phoneme
Profile 1───* SayItAttempt   *───1 Phoneme
Profile 1───* FindItAttempt  *───1 Phoneme (target)  ─── selectedPhonemeId ───1 Phoneme
Profile 1───* BlendItProgress *──1 LetterGroup
Profile 1───* BlendItAttempt  *──1 LetterGroup, *──1 BlendItWord
Profile 1───* Achievement
Profile 1───* ReportLog
LetterGroup 1───* LetterGroupMember *───1 Phoneme
LetterGroup 1───* BlendItWord
```

## 3. Indices (beyond PKs/FKs — for dashboard query performance)

- `LessonProgress(profileId)`, `SayItAttempt(profileId, phonemeId)`, `FindItAttempt(profileId, phonemeId)` — the dashboard's per-letter aggregation queries hit these constantly.
- `BlendItAttempt(profileId, groupId)`, `BlendItProgress(profileId)`.
- `LetterGroupMember(groupId)` — `GroupUnlockManager` reads this on every map load.

## 4. Migration Strategy

- Ship v1 with all 12 tables from day one — the schema above is not expected to grow incrementally per-module the way the code does; build the whole schema in Phase 0/1 of `06_IMPLEMENTATION_ROADMAP.md` even though most tables stay empty until later phases.
- Any schema change after first release needs a real Room `Migration`, not `fallbackToDestructiveMigration()` — this app's entire value proposition is preserving a child's offline progress; destructive migrations would delete it.

## 5. Seed Data Requirements

- `Phoneme`: 28 rows, Marungko order. **26 of 28 are content-ready; `ng` and `ñ` ship with the `PENDING_SME_REVIEW` sentinel** (§1, and `01 §5`) until a reading specialist supplies English-phonics-appropriate example words/audio.
- `LetterGroup` / `LetterGroupMember`: 7 groups × 4 letters, in strict sequence order (Group 1 = `m,s,a,i` … Group 7 = `q,v,x,z`).
- `BlendItWord`: draft 7-group word bank in `19_AUDIO_SCRIPTS.md` — Group 1 is content-constrained (only 3 valid words with `m,s,a,i`); flagged there, not silently padded with invalid words here.
