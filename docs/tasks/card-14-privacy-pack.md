# Card 14: Privacy pack: no cloud backup, delete a child's data, privacy notice (FR-14)

Status: done

Runs after card 10 is accepted. It touches the Parent Zone, which no open card edits.

## Why
Defense review of 2026-10-05 (`docs/defense/DEFENSE_REVIEWER.md` C2, C3):
1. **Child data can leave the device.** `AndroidManifest.xml:10` has `android:allowBackup="true"`, so Android's auto-backup can copy the Room database, which holds the child's name and progress, to the parent's Google account. The app otherwise has no network permission.
2. **A parent cannot delete a child's data.** `ProfileRepository.deleteProfile` exists (`ProfileRepositoryImpl.kt:51`), and all six child tables cascade on profile delete (`ForeignKey.CASCADE`). But no screen calls it; the only way is to clear app data.
3. **There is no privacy notice.** Parents are never told what is stored.

User decision 2026-10-05: fix these before the defense.

## Files
All code paths are under `app/src/main/java/com/playit/app/` or `app/src/test/java/com/playit/app/`.
- Edit: `app/src/main/AndroidManifest.xml`
- Add: `app/src/main/res/xml/backup_rules.xml`, `app/src/main/res/xml/data_extraction_rules.xml`
- Edit: `presentation/dashboard/ParentDashboardViewModel.kt` and test `presentation/dashboard/ParentDashboardViewModelTest.kt`
- Edit: `presentation/dashboard/ParentDashboardScreen.kt`
- Edit: `presentation/dashboard/components/LearnerHeroCard.kt`
- Add: `presentation/dashboard/components/PrivacyNoticeDialog.kt`
- Edit: `navigation/NavGraph.kt` (the `PARENT_DASHBOARD` destination only)
- New test: `ManifestPrivacyTest.kt` (package `com.playit.app`)

## Changes
1. **Manifest.** On `<application>`:
   - change `android:allowBackup="true"` to `android:allowBackup="false"`;
   - add `android:fullBackupContent="@xml/backup_rules"` and `android:dataExtractionRules="@xml/data_extraction_rules"`.
2. **`backup_rules.xml`** (Android 11 and lower), excluding everything:
   ```xml
   <?xml version="1.0" encoding="utf-8"?>
   <full-backup-content>
       <exclude domain="root" path="." />
       <exclude domain="file" path="." />
       <exclude domain="database" path="." />
       <exclude domain="sharedpref" path="." />
       <exclude domain="external" path="." />
   </full-backup-content>
   ```
   **`data_extraction_rules.xml`** (Android 12+), the same five excludes inside both `<cloud-backup>` and `<device-transfer>`, under `<data-extraction-rules>`.
3. **`ParentDashboardViewModel`:**
   - Add `private val sessionManager: SessionManager` to the constructor (Hilt provides it; update the test setup).
   - Add `fun deleteProfile(profile: Profile)`:
     - calls `profileRepository.deleteProfile(profile)` in `viewModelScope`;
     - if `sessionManager.activeProfileId.value == profile.id`, calls `sessionManager.clearActiveProfile()`;
     - then, if the deleted profile was `selectedProfile`, sets `selectedProfile` to null so `loadProfiles()` picks the first remaining one (it already falls back to `profiles.firstOrNull()`).
   - Add `val noProfilesLeft: StateFlow<Boolean>`: true when the profiles flow emits an empty list after a delete.
4. **`LearnerHeroCard`.** Add an `onDelete: () -> Unit = {}` parameter. Under the name row, add a text button "Delete this child's data", in `TextMuted`, with a 48 dp target. It opens a confirm dialog with the same `DialogShape`/`Surface` style as the rename dialog:
   - title "Delete <name>'s data?";
   - body "This removes this child's profile, stars and progress from this device. It cannot be undone.";
   - buttons Cancel (`TextButton`) and Delete (`GummyButton` with `GentleCorrectionOrange`; never red).

   Delete calls `onDelete()`.
5. **`ParentDashboardScreen`:**
   - At the existing `LearnerHeroCard(...)` call, add `onDelete = { viewModel.deleteProfile(dashboardData.profile) }`.
   - Add an `onAllProfilesDeleted: () -> Unit` parameter, called from a `LaunchedEffect(noProfilesLeft)` when it becomes true.
   - Add a "Privacy" text button (48 dp) at the bottom of the dashboard list. It opens `PrivacyNoticeDialog`.
6. **`PrivacyNoticeDialog.kt`.** A dialog in the same style, titled "Your child's data", with this text exactly:
   - "PlayIT works without the internet. Everything stays on this device."
   - "PlayIT never records or saves your child's voice. It listens only while the microphone button is active, and only the result (right or not yet) is saved."
   - "Saved on this device: your child's name and animal, stars, and which letters they have practised."
   - "Nothing is sent anywhere. A report leaves the device only if you share it."
   - "To remove a child's data, tap Delete this child's data on their card."

   One "OK" button. No emoji.
7. **`NavGraph`.** At the `PARENT_DASHBOARD` destination, pass `onAllProfilesDeleted = { navController.navigate(Routes.PROFILE_SELECT) { popUpTo(0) } }`.
8. **Do not change** the parent gate, the database schema, the profile switcher, or any child-facing screen.

## Tests
| Test file | Test | Assertion |
|---|---|---|
| ParentDashboardViewModelTest | `deleteProfile_callsRepository` | `deleteProfile(p)` calls `profileRepository.deleteProfile(p)` |
| ParentDashboardViewModelTest | `deleteActiveProfile_clearsSession` | with `activeProfileId` = `p.id`: `sessionManager.clearActiveProfile()` is called |
| ParentDashboardViewModelTest | `deleteOtherProfile_keepsSession` | with `activeProfileId` = another id: `clearActiveProfile()` is never called |
| ParentDashboardViewModelTest | `deleteLastProfile_setsNoProfilesLeft` | the profiles flow emits `emptyList()` after the delete: `noProfilesLeft.value == true` |
| ManifestPrivacyTest | `backupIsDisabled` | reads `src/main/AndroidManifest.xml` (module dir, fallback `app/src/main/...` from the repo root, as `ZeroEmojiPolicyTest` does): contains `android:allowBackup="false"`, `@xml/backup_rules` and `@xml/data_extraction_rules`; both XML files exist and each contains `domain="database"` |

`ZeroEmojiPolicyTest` must still pass. All other tests must still pass. Run `./gradlew testDebugUnitTest`.

## Phone test (for the evidence log)
1. Create two profiles, A and B.
2. In the Parent Zone, delete A. A disappears from the switcher, and B's stars are unchanged.
3. Delete B. The app returns to profile selection.
4. Open Privacy and read the notice.

## Commit
`feat(parent): delete a child's data, privacy notice, no cloud backup (FR-14)`

Decisions used: privacy pack before the defense (user decision 2026-10-05). Data minimization follows the Data Privacy Act of 2012 (RA 10173) principles; no legal review is claimed.
