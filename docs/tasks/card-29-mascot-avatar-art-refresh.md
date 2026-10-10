# Card 29: Lily poses and avatar animals, redrawn in one consistent style (asset card)

Type: asset
Status: ready (runs in its own agy image session, like card 25)

## Why
On the phone (APK B) the user found Lily and the six avatar animals out of step with the newer pictures: thinner outlines and different shading than the anchor style sheet (`app/src/main/assets/images/_style-reference-sheet/anchor_letter-card.png`, `16_ILLUSTRATION_STYLE_GUIDE.md`). The animals also have uneven sizes on their canvas (51-67% fill, Lily 77%), so they look mismatched side by side in the profile picker.

## Review (Claude, 2026-10-10)
The draft at `cb8e70a` was checked against the app's assets and code. What changed from the draft, and why:
1. **The draft named files the app does not use.** The code draws the animals from `images/characters/avatar_0N_<animal>.png` (map companions, `MapCompanionFriends.kt`) and from `images/mascot/avatar_0N.png` (`AvatarCircle.kt` on the profile screens). The draft's `images/mascot/companion_avatar_0N_*.png` are never read. All three sets are byte-identical copies (same SHA-256). So: draw the six animals once, ship them to `images/characters/`, and let the copy card (29b) point `AvatarCircle` there and delete the two duplicate sets.
2. **`splash_tarsier_headspace.png` is unused** (the splash screen draws no PNG). It is dropped, so no quota goes to it. Lily has 7 poses, one per `MascotState`.
3. **The generator is Nano Banana Pro only** (AGENTS.md, Images). Imagen 3 and Pollinations are not approved for this project.
4. **The cut-out is Claude's step, with `tools/images/cutout.py`, not rembg.** agy delivers white-background squares. `cutout.py` removes the background and the white halo and scales to 512 px. `audit.py` checks size, corners, halo, outline, padding and red.
5. **Lily is redrawn, not redesigned** (AGENTS.md: "Her poses may be redrawn for consistency, but not redesigned"). Every Lily prompt attaches `lily_idle.png` as the identity reference. The user rejects any variant that changes her species, colours, proportions or face.
6. **Each animal keeps its animal and main colour.** A child already picked one as their avatar. The draft's "calico kitten" and "barn owl" would turn today's orange cat and purple owl into different animals. The names in the code stay: Miki (cat), Milo (monkey), Bella (bunny), Barnaby (bear), Finley (frog), Ollie (owl).
7. **The draft had no brief, rounds, hand-off or release.** They are below. The brief is `docs/assets/briefs/2026-10-11-mascot-refresh/items.json`: 13 items, each with its prompt, its identity `refs` and its `app_file`. The image tools take an item's `app_file`, so one batch can serve both app folders.
8. **One known behaviour change.** "Listening" becomes a cupped ear instead of today's headphones (the draft's direction). Tell the user when showing round 1.

## Items (13)
- Lily, 7 poses, `app_file = mascot/<id>.png`: `lily_idle`, `lily_listening`, `lily_celebrating`, `lily_encouraging`, `lily_thinking`, `lily_pointing`, `lily_waving`.
- Animals, 6 heads, `app_file = characters/<id>.png`: `avatar_01_cat`, `avatar_02_monkey`, `avatar_03_bunny`, `avatar_04_bear`, `avatar_05_frog`, `avatar_06_owl`.

## Inputs
- `AGENTS.md`, the "Images" and "Lily" decisions.
- `docs/assets/briefs/2026-10-11-mascot-refresh/items.json`: the shared `style_prompt`, the 4 `style_refs`, and each item's `prompt`, `refs` and `app_file`.
- The tool `tools/images/review_page.py`. Pass `--app app/src/main/assets/images`, so each item shows its `app_file` as "now in the app".

## Batch folder
`Documents\playIT-image-batches\2026-10-11-mascot-refresh\` on your PC (below: `<batch>`). On the first run, create it and copy `items.json` into it.

## Each round
Run each round like card 25 ("Each round", steps 1-7): `--status`, a `round-NN` folder, then 2 images per open item. Each image gets the item's `prompt` plus the user's notes, the 4 `style_refs`, the item's `refs` and the `closest` image if there is one. Then run the Pillow check (square, at least 1024 px, white corners), `review_page.py <batch> --round N --app <repo>/app/src/main/assets/images`, tell the user, and wait.

Consistency across the set matters, as in card 25. From round 2 on, also attach the already-picked pictures of the same group (Lily or animals) and add: ` Match the outline weight, eye style, colours and size of the attached picked pictures exactly; only the pose (or the animal) changes.`

## Quota rule
As in card 25: generate open items in `items.json` order. If the quota runs out mid-round, write `<batch>\STATUS.md`, build the page for what exists, tell the user, and stop. Never re-make an item the user already picked.

## Done
When `--status` shows no open items, it writes `<batch>\picks.json`. Then:
1. Copy `picks.json` and the 13 picked PNGs, renamed to `<id>.png`, into `docs/assets/briefs/2026-10-11-mascot-refresh/picks/`.
2. Commit only that folder: `docs(assets): mascot and avatar picks for cutout (card 29)`, with the body lines `Card: 29 / Requirement: none / Tests run: none (asset card) / Decisions used: images (user decisions 2026-10-01); Lily redrawn, not redesigned`.
3. Push, and tell the user: "All 13 pictures are picked and pushed. Tell Claude: "mascot picks ready"."

Claude then:
1. cuts them out with `cutout.py`, `--fill 0.77` for Lily (today's size) and `--fill 0.62` for the six animals;
2. audits them (`audit.py --app app/src/main/assets/images --items <batch>/items.json`);
3. gets the user's final OK on the final review page;
4. writes `docs/image-release/<date>/` with `make_release.py --generator "Nano Banana Pro (agy, card 29)"` (each image goes to its `app_file`);
5. writes copy card 29b: copy the 13 files into `app/src/main/assets/images/`, point `AvatarCircle.kt` at `images/characters/avatar_0N_<animal>.png`, and delete `images/mascot/avatar_0N.png`, `images/mascot/companion_avatar_0N_*.png` and `images/mascot/splash_tarsier_headspace.png`.

## Rules
- **Only write into the hand-off folder** `docs/assets/briefs/2026-10-11-mascot-refresh/picks/`, and only at the end. Never touch `app/`.
- **No text, letters, arrows or emoji in the pictures.** No harsh red: blush and tongues are soft pink.
- **Lily stays Lily:** an orange tarsier with a cream belly, big brown eyes and a curling tail.

## Checks before telling the user a round is ready
- [ ] Every open item has 2 PNGs in `round-NN`, named `<id>__v1.png` and `<id>__v2.png` (unless the quota stopped the round; then `STATUS.md` says so).
- [ ] `generation_log.jsonl` has one line per image, with the references attached.
- [ ] `round-NN\index.html` exists and shows "now in the app" for all 13 items.
- [ ] Nothing changed in the repo clone (`git status` is clean).

## Commit
Only the hand-off commit in Done. Claude records the result in the evidence log.
