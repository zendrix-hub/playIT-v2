# Card 07b: Avatar-only onboarding, parent rename, voiced map pop-up (NFR-IND-01)

Status: critiqued

Runs after card 07 is accepted (it uses `AudioResolver.getUiPath`, which card 07 adds). Claude sets it to `ready` at that point; until then agy must not run it.

## Why
Onboarding asks a 6-year-old to type a name (`NamePromptScreen.kt`: the button is disabled until `isNameValid`, and `ProfileViewModel.createProfile` rejects a blank name). The map pop-up is written only, and a locked node plays a generic encouragement. Decision (user, 2026-10-01; AGENTS.md): onboarding is avatar-only, and a parent can add or change the name in the Parent Zone. Research principle 6 (docs/proposals/2026-10-01-learning-ux.md): no reading or typing required.

## Pre-step: spoken UI clips (optional)
If `docs/audio-release/2026-10-01/manifest.json` exists:
1. Verify the SHA-256 of `ui_pick_avatar`, `ui_node_start` and `ui_node_locked`.
2. Copy them unchanged into `app/src/main/assets/audio/vo/ui/`, unless card 07 already did.

Skip this step if the manifest does not exist. Missing clips are skipped by `AudioPlayer`.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Edit: `presentation/profile/components/AvatarPicker.kt`
- Edit: `presentation/profile/NamePromptScreen.kt`
- Edit: `presentation/profile/ProfileViewModel.kt` and test `presentation/profile/ProfileViewModelTest.kt`
- Edit: `presentation/dashboard/ParentDashboardViewModel.kt` and test `presentation/dashboard/ParentDashboardViewModelTest.kt`
- Edit: `presentation/dashboard/components/LearnerHeroCard.kt` and its caller `presentation/dashboard/ParentDashboardScreen.kt` (the new `onRename` parameter only)
- Edit: `presentation/map/MapViewModel.kt` and test `presentation/map/MapViewModelTest.kt`
- Edit: `presentation/map/MapScreen.kt`
- Add (only if the pre-step ran): `app/src/main/assets/audio/vo/ui/<id>.wav`

## Changes
1. `AvatarPicker.kt`: move the local `avatarNames` list to a public top-level `val AVATAR_NAMES = listOf("Cat", "Monkey", "Bunny", "Bear", "Frog", "Owl")`, and use it where the list was used.
2. `ProfileViewModel`:
   - `createProfile(name, avatarResId)`: a blank name is no longer an error. Use `name.trim().ifBlank { AVATAR_NAMES.getOrElse(avatarResId - 1) { "Friend" } }`. Everything else stays the same.
   - `playNamePromptIntro()` now plays `audioResolver.getUiPath("ui_pick_avatar")`.
3. `NamePromptScreen` (track "an avatar was tapped this visit" with a screen-local `remember { mutableStateOf(false) }`):
   - Remove the `GummyTextField` and the `isNameValid` gate. The create button is enabled whenever `uiState !is ProfileUiState.Loading`, and calls `viewModel.createProfile("", selectedAvatarId)`.
   - The mascot message becomes "Pick your animal friend!".
   - `MascotState` is `CELEBRATING` once an avatar has been tapped this visit, otherwise `POINTING`; errors keep priority.
   - Keep the rest of the layout.
4. `ParentDashboardViewModel`:
   - Add `fun renameProfile(profile: Profile, newName: String)`. Trim the name; if it is blank or longer than 20 characters, do nothing. Otherwise call `profileRepository.updateProfile(profile.copy(name = trimmed))`.
   - The existing profile flow refreshes the UI.
5. `LearnerHeroCard` (parent-facing, behind the existing parent gate):
   - Add an edit icon button (`Icons.Rounded.Edit`, 48dp target, content description "Edit name") next to the name.
   - It opens a dialog with a text field (the existing `GummyTextField`) and Save/Cancel. Save calls a new `onRename: (String) -> Unit` parameter. In `ParentDashboardScreen.kt`, wire it at the existing call (`item { LearnerHeroCard(data = dashboardData) }`, about line 184) as `onRename = { viewModel.renameProfile(dashboardData.profile, it) }`; change nothing else there.
6. `MapViewModel` and `MapScreen`:
   - `onLockedNodeTapped()` plays `listOf(getSfxPath(SfxEvent.INCORRECT_POP), getUiPath("ui_node_locked"))` instead of the rotating encourage line.
   - Add `onUnlockedNodeTapped()`, which plays `getUiPath("ui_node_start")`. Call it in `MapScreen` where an unlocked node sets `selectedNodeForAction` (about lines 427 and 441).
7. Do not change the parent gate, the profile database, the hearts, or any lesson screen.

## Tests
| Test file | Test | Assertion |
|---|---|---|
| ProfileViewModelTest | `createProfile_blankName_usesAvatarName` | `createProfile("", 3)` calls `profileRepository.createProfile("Bunny", 3)` and reaches `Created` |
| ProfileViewModelTest | `createProfile_typedName_isKept` | `createProfile("  Ana ", 1)` calls `createProfile("Ana", 1)` |
| ProfileViewModelTest | `namePromptIntro_playsPickAvatar` | `playNamePromptIntro()` plays `getUiPath("ui_pick_avatar")` |
| ParentDashboardViewModelTest | `renameProfile_updatesTrimmedName` | `renameProfile(p, " Maya ")` calls `updateProfile(p.copy(name = "Maya"))` |
| ParentDashboardViewModelTest | `renameProfile_blankOrTooLong_isIgnored` | `""` and a 21-character name never call `updateProfile` |
| MapViewModelTest | `lockedNode_playsLockedCue` | `onLockedNodeTapped()` plays `listOf("sfx_path", "ui/ui_node_locked.wav")` (stub `getUiPath` as in card 07) |
| MapViewModelTest | `unlockedNode_playsStartCue` | `onUnlockedNodeTapped()` plays `"ui/ui_node_start.wav"` |

Update any existing test that expected the blank-name error or the old locked-node sequence. All other tests must still pass. Run `./gradlew testDebugUnitTest`.

## Commit
`feat(ui): avatar-only onboarding, parent rename, voiced map pop-up (NFR-IND-01)`

Decisions used: avatar-only onboarding with parent rename (user decision 2026-10-01, AGENTS.md Decisions).
