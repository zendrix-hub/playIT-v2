# Card 25: Mouth-shape pictures, in rounds until the user is happy (asset card)

Type: asset
Status: ready (runs now, in parallel with card 17, in its own agy session)

Run this in a separate agy session ("This is the image session"), alongside the code relay. During the rounds it writes only into the batch folder below. The one exception is the hand-off at the end (see Done), because Claude works on a different PC and can only reach the picks through git.

## Why
Card 24 (plan Task 8) shows a mouth-shape cue beside the letter in Hear It and Say It. Round 1 found a gap for learners with hearing difficulties (ACC-06: 3 of 5 Yes), and the SRS asks for articulation cues (NFR-ACC-01).

There are 9 mouth shapes. Together they cover all 26 letters:
- lips together: m, b, p
- teeth on lip: f, v
- tongue up: t, d, n, l
- teeth close: s, z, x
- back of mouth: k, c, g, q
- round lips: o, u, w
- wide open: a
- smile: e, i, y
- open breath: h, j, r

User decision 2026-10-06: start now, with card 17, because Nano Banana has a daily quota.

## Inputs (read these first)
- `AGENTS.md`, the "Images" decision.
- `docs/assets/briefs/2026-10-07-mouth-shapes/items.json`. It has 9 items, each with `id` (the app file name without `.png`), `letter` (the letters it covers), `subject` and a full `prompt`. It also has `style_refs`, the 6 current pictures to attach as style references.
- The tool `tools/images/review_page.py`.

## Batch folder
`Documents\playIT-image-batches\2026-10-07-mouth-shapes\` on your PC (below: `<batch>`). On the first run, create it and copy `items.json` into it.

## Each round
Run each round exactly like card 08, "Each round" steps 1-7:
1. `--status`.
2. `round-NN` folder.
3. 2 images per open item, with the item's `prompt` plus the user's notes, the 6 `style_refs`, and the `closest` image if there is one.
4. Pillow check: square, at least 1024 px, white corners.
5. `review_page.py <batch> --round N`.
6. Tell the user the round is ready.
7. Wait.

These items differ from card 08 in one way: **consistency across the set matters**. From round 2 on, also attach the user's already-picked mouth pictures as references, and add: ` Match the child, skin tone, framing and size of the attached picked mouth pictures exactly; only the mouth shape changes.`

## Quota rule
Nano Banana has a daily quota.
- Generate the open items in the order listed in `items.json`.
- If the quota runs out mid-round:
  1. Write `<batch>\STATUS.md`, with the round, the items done and the items still to make.
  2. Build the review page for what exists.
  3. Tell the user, and stop.
- The next session continues from `STATUS.md`, before starting a new round.
- Never spend quota re-making an item the user already picked.

## Done
When `--status` shows no open items, it writes `<batch>\picks.json`. Then hand the picks to Claude, as card 08 did:
1. Copy `picks.json` and the 9 picked PNGs, renamed to `<id>.png`, into `docs/assets/briefs/2026-10-07-mouth-shapes/picks/`.
2. Commit only that folder: `docs(assets): mouth-shape picks for cutout (card 25, NFR-ACC-01)`, with the body lines `Card: 25 / Requirement: NFR-ACC-01 / Tests run: none (asset card) / Decisions used: images (user decisions 2026-10-01)`.
3. Push.

Then tell the user:
> All 9 mouth pictures are picked and pushed. Tell Claude: "mouth shapes picked".

Claude then:
1. cuts them out (`tools/images/cutout.py`);
2. audits them (`tools/images/audit.py`), including the 4 backgrounds page;
3. gets the user's final OK;
4. writes `docs/image-release/<date>/` with `appPath = images/mouth/<id>.png`.

The pictures reach the app through card 24, or through a small copy card (24b) if card 24 is already done.

## Rules
- **Don't write into the repo** except the hand-off folder `docs/assets/briefs/2026-10-07-mouth-shapes/picks/` at the end. Never touch `app/`.
- **No text, letters or arrows in the pictures.** No emoji anywhere.
- **No harsh red lips:** soft rosy brown, as the prompt says. No scary teeth: simple white shapes only.
- **If a shape is hard to show** (for example "back of mouth"), make your best two versions anyway and say so. The user decides.

## Checks before telling the user a round is ready
- [ ] Every open item has 2 PNGs in `round-NN`, named `<id>__v1.png` and `<id>__v2.png` (unless the quota stopped the round; then `STATUS.md` says so).
- [ ] `generation_log.jsonl` has one line per image.
- [ ] `round-NN\index.html` exists and lists every generated item.
- [ ] Nothing changed in the repo clone (`git status` is clean).

## Commit
Only the hand-off commit in Done. Claude records the result in the evidence log.
