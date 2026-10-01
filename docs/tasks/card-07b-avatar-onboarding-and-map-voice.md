# Card 07b: Avatar-only onboarding, parent rename, voiced map pop-up (NFR-IND-01)

Status: ready

Runs after card 07 is accepted (it uses `AudioResolver.getUiPath`, which card 07 adds). In the relay, take it only when card 07 is `accepted`.

Revised by Claude on 2026-10-01 after a code check:
1. The map test expectations now match the test's own stubs.
2. The dashboard refreshes the renamed profile in the switcher.
3. The rename limit is 16 characters, the same as the old typed-name limit.
4. The pre-step always copies this card's three clips.

## Why
Onboarding asks a 6-year-old to type a name (`NamePromptScreen.kt`: the button is disabled until `isNameValid`, and `ProfileViewModel.createProfile` rejects a blank name). The map pop-up is written only, and a locked node plays a generic encouragement. Decision (user, 2026-10-01; AGENTS.md): onboarding is avatar-only, and a parent can add or change the name in the Parent Zone. Research principle 6 (docs/proposals/2026-10-01-learning-ux.md): no reading or typing required.

## Pre-step: spoken UI clips (optional)
`docs/audio-release/2026-10-01/manifest.json` exists (18 clips).
1. Verify the SHA-256 of `ui_pick_avatar`, `ui_node_start` and `ui_node_locked` (release files `ui/<id>.wav`). Stop if a hash differs.
2. Copy them unchanged into `app/src/main/assets/audio/vo/ui/<id>.wav`. Card 07 copied only its own three clips, so these are always new.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Edit: `presentation/profile/components/AvatarPicker.kt`
- Edit: `presentation/profile/NamePromptScreen.kt`
- Edit: `presentation/profile/ProfileViewModel.kt` and test `presentation/profile/ProfileViewModelTest.kt`
- Edit: `presentation/dashboard/ParentDashboardViewModel.kt` and test `presentation/dashboard/ParentDashboardViewModelTest.kt`
- Edit: `presentation/dashboard/components/LearnerHeroCard.kt` and its caller `presentation/dashboard/ParentDashboardScreen.kt` (the new `onRename` parameter only)
- Edit: `presentation/map/MapViewModel.kt` and test `presentation/map/MapViewModelTest.kt`
- Edit: `presentation/map/MapScreen.kt`
- Add: `app/src/main/assets/audio/vo/ui/ui_pick_avatar.wav`, `ui_node_start.wav`, `ui_node_locked.wav`

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
   - Add a top-level `const val MAX_NAME_LENGTH = 16` in the same file (the limit the old typed name had in `NamePromptScreen`), and `fun renameProfile(profile: Profile, newName: String)`. Trim the name; if it is blank or longer than `MAX_NAME_LENGTH`, do nothing. Otherwise call `profileRepository.updateProfile(profile.copy(name = trimmed))`.
   - In `loadProfiles()`, re-select the profile by id from the fresh list, so the renamed object replaces the stale one in `selectedProfile` (the switcher's "Learner: <name>" otherwise keeps the old name):
     ```kotlin
     val currentSelected = _uiState.value.selectedProfile
         ?.let { sel -> profiles.firstOrNull { it.id == sel.id } }
         ?: profiles.firstOrNull()
     ```
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
| ParentDashboardViewModelTest | `renameProfile_blankOrTooLong_isIgnored` | `""` and a 17-character name never call `updateProfile` |
| ParentDashboardViewModelTest | `rename_refreshesSelectedProfile` | when the profiles flow emits the renamed profile (same id, new name), `uiState.value.selectedProfile?.name` is the new name |
| MapViewModelTest | `lockedNode_playsLockedCue` | `onLockedNodeTapped()` plays `listOf("sfx.mp3", "ui/ui_node_locked.wav")`. The test's `getSfxPath` stub returns `"sfx.mp3"` (`MapViewModelTest.kt:80`); add `every { audioResolver.getUiPath(any()) } answers { "ui/${firstArg<String>()}.wav" }` to `setup()`, because the resolver is relaxed and would otherwise return `""` |
| MapViewModelTest | `unlockedNode_playsStartCue` | `onUnlockedNodeTapped()` plays `"ui/ui_node_start.wav"` |

Update any existing test that expected the blank-name error or the old locked-node sequence. In `MapViewModelTest.audioActions_invokeAudioPlayerCorrectly`, the last check becomes `verify { audioPlayer.playSequence(listOf("sfx.mp3", "ui/ui_node_locked.wav")) }`. `ProfileViewModelTest` also uses a relaxed resolver, so stub `getUiPath` there the same way. All other tests must still pass. Run `./gradlew testDebugUnitTest`.

## Commit
`feat(ui): avatar-only onboarding, parent rename, voiced map pop-up (NFR-IND-01)`

Decisions used: avatar-only onboarding with parent rename (user decision 2026-10-01, AGENTS.md Decisions).
