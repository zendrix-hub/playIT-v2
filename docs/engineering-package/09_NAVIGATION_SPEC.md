# 09 — Navigation Specification

Navigation Compose route graph for the 12 screens in `02_ARCHITECTURE_SUMMARY.md §4`.

## 1. Route Table

| Route constant | Path pattern | Nav args | Notes |
|---|---|---|---|
| `SPLASH` | `splash` | — | start destination |
| `PROFILE_SELECT` | `profile_select` | — | |
| `NAME_PROMPT` | `name_prompt` | — | |
| `MAP` | `map` | — | reads `SessionManager.activeProfileId`, not a nav arg |
| `HEAR_IT` | `hear_it/{phonemeId}` | `phonemeId: Int` | |
| `SAY_IT` | `say_it/{phonemeId}` | `phonemeId: Int` | |
| `FIND_IT` | `find_it/{phonemeId}` | `phonemeId: Int` | |
| `LETTER_COMPLETE` | `letter_complete/{phonemeId}` | `phonemeId: Int` | |
| `BLEND_IT` | `blend_it/{groupId}` | `groupId: Int` | |
| `BLEND_IT_COMPLETE` | `blend_it_complete/{groupId}` | `groupId: Int` | |
| `PARENT_DASHBOARD` | `parent_dashboard` | — | gated by the arithmetic check, `01 §7.6` — see §3 |
| `REPORT_PREVIEW` | `report_preview/{profileId}` | `profileId: Int` | dashboard's currently-selected profile, not necessarily `activeProfileId` |

## 2. Flow Diagram

```
SPLASH
  └──(profiles exist)──▶ PROFILE_SELECT ──(select)──▶ MAP
  └──(no profiles)─────▶ PROFILE_SELECT ──(add new)──▶ NAME_PROMPT ──▶ PROFILE_SELECT ──▶ MAP

MAP
  ├──(tap unlocked letter node)──▶ HEAR_IT(phonemeId)
  │                                    └──▶ SAY_IT(phonemeId)
  │                                            └──▶ FIND_IT(phonemeId)
  │                                                    └──(3 sublevels done)──▶ LETTER_COMPLETE(phonemeId)
  │                                                                                  └──(continue)──▶ MAP
  ├──(tap unlocked Blend It node)──▶ BLEND_IT(groupId)
  │                                    └──(session ends, won or lost)──▶ BLEND_IT_COMPLETE(groupId)
  │                                                                          └──(continue)──▶ MAP
  └──(tap Parent Dashboard entry)──▶ [arithmetic gate] ──(solved)──▶ PARENT_DASHBOARD
                                                                          └──(select profile + export)──▶ REPORT_PREVIEW(profileId)
```

## 3. Back-Stack & Gating Rules

- **Locked nodes are not navigable** — `MapScreen` must not even construct a `NavController.navigate()` call for a locked `LetterNode`/`BlendItChallengeNode`; don't rely on the destination screen to reject the request.
- **Hear It → Say It → Find It is a forward-only chain within one letter session.** Back-press from `SAY_IT` returns to `HEAR_IT` (standard back-stack), but `HEAR_IT`'s "Next" button should `popUpTo` itself before pushing `SAY_IT` if the child re-enters an already-completed sublevel, to avoid stacking duplicate screens on replay.
- **`LETTER_COMPLETE` and `BLEND_IT_COMPLETE` clear their sublevel back-stack** on "Continue" (`popUpTo(MAP) { inclusive = false }`) — a child should never be able to back-button from the celebration screen into a finished sublevel.
- **The arithmetic gate is not a route** — implement it as a `Dialog`/`AlertDialog` composed in front of the Map (or wherever the Parent Dashboard entry point lives), not a navigation destination; only call `navigate(PARENT_DASHBOARD)` after it resolves correctly. This keeps it from ever appearing in the back stack (a child mashing "back" should never be able to peek at parent data mid-dialog).
- **`REPORT_PREVIEW` reads `profileId` from its nav arg, not `SessionManager`** — the parent may be viewing a *different* child's report than whichever profile is currently `activeProfileId` in gameplay. Do not conflate the two.

## 4. Deep-Link / Process-Death Notes

- No deep links are in scope (no external entry points — this is a fully offline, closed app).
- On process death mid-sublevel (`HEAR_IT`/`SAY_IT`/`FIND_IT`/`BLEND_IT`), do **not** attempt to restore the exact in-progress nav state — restart at `MAP` on relaunch. This is consistent with `01_REQUIREMENTS_SUMMARY.md §5`'s rule that incomplete sub-level progress is intentionally not persisted.
