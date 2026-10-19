# Card 29b: Mascot poses and companion avatars in the app from release 2026-10-12-mascot (NFR-ACC-02)

Status: done

## Why
Approved mascot poses and companion animal avatars from release `2026-10-12-mascot` are moved into the app.
AvatarCircle points to `images/characters/avatar_0N_<animal>.png` via `CompanionAnimal.fromId(clampedId).assetPath`, and redundant copies in `images/mascot/` are deleted.

## Files
- `docs/image-release/2026-10-12-mascot/manifest.json`
- `docs/image-release/2026-10-12-mascot/characters/avatar_01_cat.png`
- `docs/image-release/2026-10-12-mascot/characters/avatar_02_monkey.png`
- `docs/image-release/2026-10-12-mascot/characters/avatar_03_bunny.png`
- `docs/image-release/2026-10-12-mascot/characters/avatar_04_bear.png`
- `docs/image-release/2026-10-12-mascot/characters/avatar_05_frog.png`
- `docs/image-release/2026-10-12-mascot/characters/avatar_06_owl.png`
- `docs/image-release/2026-10-12-mascot/mascot/lily_celebrating.png`
- `docs/image-release/2026-10-12-mascot/mascot/lily_encouraging.png`
- `docs/image-release/2026-10-12-mascot/mascot/lily_idle.png`
- `docs/image-release/2026-10-12-mascot/mascot/lily_listening.png`
- `docs/image-release/2026-10-12-mascot/mascot/lily_pointing.png`
- `docs/image-release/2026-10-12-mascot/mascot/lily_thinking.png`
- `docs/image-release/2026-10-12-mascot/mascot/lily_waving.png`
- `app/src/main/assets/images/characters/avatar_01_cat.png`
- `app/src/main/assets/images/characters/avatar_02_monkey.png`
- `app/src/main/assets/images/characters/avatar_03_bunny.png`
- `app/src/main/assets/images/characters/avatar_04_bear.png`
- `app/src/main/assets/images/characters/avatar_05_frog.png`
- `app/src/main/assets/images/characters/avatar_06_owl.png`
- `app/src/main/assets/images/mascot/lily_celebrating.png`
- `app/src/main/assets/images/mascot/lily_encouraging.png`
- `app/src/main/assets/images/mascot/lily_idle.png`
- `app/src/main/assets/images/mascot/lily_listening.png`
- `app/src/main/assets/images/mascot/lily_pointing.png`
- `app/src/main/assets/images/mascot/lily_thinking.png`
- `app/src/main/assets/images/mascot/lily_waving.png`
- `app/src/main/assets/images/mascot/avatar_01.png`
- `app/src/main/assets/images/mascot/avatar_02.png`
- `app/src/main/assets/images/mascot/avatar_03.png`
- `app/src/main/assets/images/mascot/avatar_04.png`
- `app/src/main/assets/images/mascot/avatar_05.png`
- `app/src/main/assets/images/mascot/avatar_06.png`
- `app/src/main/assets/images/mascot/companion_avatar_01_cat.png`
- `app/src/main/assets/images/mascot/companion_avatar_02_monkey.png`
- `app/src/main/assets/images/mascot/companion_avatar_03_bunny.png`
- `app/src/main/assets/images/mascot/companion_avatar_04_bear.png`
- `app/src/main/assets/images/mascot/companion_avatar_05_frog.png`
- `app/src/main/assets/images/mascot/companion_avatar_06_owl.png`
- `app/src/main/assets/images/mascot/splash_tarsier_headspace.png`
- `app/src/main/java/com/playit/app/presentation/profile/components/AvatarCircle.kt`
- `docs/engineering-package/13_MASTER_TASKS.md`
- `docs/evidence-log.md`
- `docs/tasks/card-29-mascot-avatar-art-refresh.md`
- `docs/tasks/card-29b-mascot-avatars.md`

## Changes
1. Move release `2026-10-12-mascot` into `docs/image-release/`.
2. Copy 6 companion avatars to `app/src/main/assets/images/characters/`.
3. Copy 7 Lily poses to `app/src/main/assets/images/mascot/`.
4. Delete duplicate avatar copies from `app/src/main/assets/images/mascot/`.
5. Point `AvatarCircle.kt` at `CompanionAnimal.fromId(clampedId).assetPath`.
6. Run unit tests (`PictureAssetsTest`).
