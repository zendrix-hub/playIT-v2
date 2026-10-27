# Card D1: Redraw the app's pictures in the refined flat cartoon style (asset card, design branch)

Type: asset
Status: ready
Branch: `design/visual-refresh` (not `refactor/hear-say-it`)

## Why
The user asked for a new design with new AI-generated art (2026-10-27). Today's pictures were made in batches and do not match: some are bare shapes, some glossy, some have faces and some do not. On the design board the user approved the refined flat cartoon of four sample pictures (umbrella, mouse, fish, sun) as the style for every picture: a bold, even dark-brown outline, flat colour with one shade and one small highlight, and faces only on animals and people.

Only the look changes. Each picture keeps what it shows today, which is why today's picture is attached to every prompt as its identity reference: "bat" stays a baseball bat, the owl avatar stays a purple owl. Lily is not redrawn; the app animates her as she is.

## Generator: free only
**Use Nano Banana Pro inside Antigravity, on its free quota, and nothing else.** The user decided on 2026-10-27 that nothing paid is used for this art: no paid plan, no paid API, no Figma AI. If the free quota runs out, stop (see "Quota rule"). Never switch to another generator to finish a round.

## Batches, in this order
| Batch | Items | Brief |
|---|---|---|
| `2026-10-27-refresh-findit` | 63 Find It and Hear It pictures (`pictures/picture_*.png`) | `docs/assets/briefs/2026-10-27-refresh-findit/items.json` |
| `2026-10-27-refresh-blendit` | 40 Blend It word pictures (`pictures/blendword_*.png`) | `docs/assets/briefs/2026-10-27-refresh-blendit/items.json` |
| `2026-10-27-refresh-world` | 14 map props, 3 rewards, 6 avatar animals | `docs/assets/briefs/2026-10-27-refresh-world/items.json` |

Each brief has the shared `style_prompt`, the 4 `style_refs` (the approved samples, in `docs/assets/briefs/2026-10-27-refresh-anchors/`), and for each item its `prompt`, its identity `refs` (today's picture) and its `app_file`. `tools/design/make_refresh_briefs.py` wrote them; don't edit the JSON by hand.

The anchors are 200 px previews: they show the style, not the size. Every output is still 1024 px or more.

Finish one batch (every item picked) before you start the next.

## Batch folder
`Documents\playIT-image-batches\<batch>\` on your PC (below: `<batch>`). On the first run of a batch, create it and copy that batch's `items.json` into it.

## Each round
Run each round like card 29 ("Each round"):
1. `python tools/images/review_page.py <batch> --status` lists the open items and the user's notes.
2. Make `round-NN`. For each open item, in `items.json` order, make 2 images, `<id>__v1.png` and `<id>__v2.png`. Each image gets:
   - the item's `prompt`, plus the user's notes for it;
   - the 4 `style_refs`;
   - the item's `refs` (today's picture: keep what it shows);
   - the `closest` image, if the user marked one.
3. Log every image in `round-NN\generation_log.jsonl`: round, item, variant, prompt, the references, the model, the time.
4. Check every image with Pillow: square, at least 1024 px, white corners.
5. Run `python tools/images/review_page.py <batch> --round N --app <repo>\app\src\main\assets\images`, so each item shows "now in the app".
6. Tell the user that round N of `<batch>` is ready, then wait for their review CSV.

Consistency across the set matters most. From round 2 on, also attach up to 4 pictures the user already picked in the same batch, and add: ` Match the outline weight, shading, colours and proportions of the attached picked pictures exactly.`

## Quota rule
Generate open items in `items.json` order. If the free quota runs out mid-round:
1. write `<batch>\STATUS.md` with what is done and what is left;
2. build the review page for what exists;
3. tell the user, and stop.

Never re-make an item the user already picked.

## Done (for each batch)
When `--status` shows no open items, it writes `<batch>\picks.json`. Then:
1. Copy `picks.json` and the picked PNGs, renamed to `<id>.png`, into `docs/assets/briefs/<batch>/picks/`.
2. Commit only that folder on `design/visual-refresh`, as `docs(assets): <batch> picks for cutout (card D1)`, with these body lines:
   ```
   Card: D1
   Requirement: none
   Tests run: none (asset card)
   Decisions used: design refresh, refined flat cartoon (user 2026-10-27); free generator only (user 2026-10-27)
   ```
3. Push, and tell the user: "All pictures of `<batch>` are picked and pushed. Tell Claude: "`<batch>` picks ready"."

Claude then:
1. cuts them out with `tools/images/cutout.py`: `--fill 0.70` for pictures and props, `--fill 0.62` for the avatars;
2. audits them with `audit.py --app app/src/main/assets/images --items <batch>/items.json`;
3. gets the user's final OK on a final review page;
4. writes `docs/image-release/<date>/` with `make_release.py --generator "Nano Banana Pro (agy, card D1)"`;
5. copies the released files into `app/src/main/assets/images/` on this branch. On the design branch Claude builds the app changes (user decision 2026-10-27).

## Rules
- **Only write into** `docs/assets/briefs/<batch>/picks/`, and only at the end of a batch. Never touch `app/`.
- **No text, letters, arrows or emoji in the pictures.** Numbers only where the word is a number (`six`, `sum`).
- **Faces only on animals and people.** The sun, the van and the bus have no face.
- **No harsh red:** use the warm tomato or hibiscus red of the style prompt.

## Checks before telling the user a round is ready
- [ ] Every open item has 2 PNGs in `round-NN`, unless the quota stopped the round and `STATUS.md` says so.
- [ ] `generation_log.jsonl` has one line per image, with its references.
- [ ] `round-NN\index.html` shows "now in the app" for every item.
- [ ] Nothing changed in the repo clone (`git status` is clean).

## Commit
Only the hand-off commit in "Done", once per batch. Claude records the result in the evidence log.
