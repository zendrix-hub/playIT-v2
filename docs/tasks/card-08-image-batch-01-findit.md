# Card 08: Image batch 1: Find It and key-word pictures, in rounds until the user is happy (asset card)

Type: asset
Status: ready

Run this in its own agy session, alongside the code relay. It never touches the repo: no edits, no commit, no push. It writes only into the batch folder below.

## Why
Of the 63 `picture_*.png` files the app shows in Find It and on the letter cards, 24 are crude placeholders: 2-6 colours, no anti-aliasing, and harsh red on the ant and the map. Four key-word pictures, the ones Hear It shows for a letter, are wrong:
- **apple** looks like an orange and is almost the same picture as orange;
- **goat** looks like a green rabbit;
- **tiger** and **zebra** have no stripes.

The word "Up" shows a submarine (`blendword_sub.png`). Children learn the letter-sound link from these pictures, so each one must show its word clearly at Find It size (about 96 px).

User decisions (2026-10-01, AGENTS.md): agy draws with its built-in image model (Nano Banana Pro), and keeps making new versions in rounds until the user picks one for every item. The style matches the best current pictures (`16_ILLUSTRATION_STYLE_GUIDE.md`).

## Inputs (read these first)
- `AGENTS.md`, the "Images" and "Lily" decisions.
- `docs/assets/briefs/2026-10-01-findit-batch-01/items.json`. It holds 29 items, each with `id` (the app file name without `.png`), `word`, `letter`, `why`, `subject` and a full `prompt`, plus `style_refs`, which are 6 current pictures to attach as style references.
- The tool `tools/images/review_page.py` (Python standard library only; `python` on this PC has Pillow too).

## Batch folder
`C:\Users\riva.zn\Documents\playIT-image-batches\2026-10-01-findit-batch-01\` (below: `<batch>`).
On the first run, create it and copy `items.json` into it.

## Each round
1. Run `python tools\images\review_page.py <batch> --status`. It prints JSON with:
   - `open`: the items still without a pick, each with the user's `notes` and the `closest` image they liked;
   - `next_round`: the round number to make now (N below).
2. Create `<batch>\round-NN\` (two digits, e.g. `round-01`).
3. For every item in `open`, generate **2 images**:
   - **Prompt:** the item's `prompt` from `items.json`. If the item has `notes`, add at the end: ` Changes the user asked for: <all notes, oldest first>. Keep everything else the same.`
   - **References:** attach the 6 `style_refs` images (repo paths). If the item has a `closest` image, attach it too (`<batch>\<closest>`) and add: ` Start from the attached closest image and apply the requested changes.`
   - **Shape:** square, at the largest size the tool offers (at least 1024 x 1024).
   - **Save as:** PNG at `<batch>\round-NN\<id>__v1.png` and `__v2.png`. If the tool saves somewhere else, copy the file there. If it gives JPEG or WebP, convert it:
     `python -c "from PIL import Image; Image.open(r'SRC').convert('RGB').save(r'DST')"`
   - **Log:** append one JSON line per image to `<batch>\round-NN\generation_log.jsonl`:
     `{"round": N, "item": "<id>", "variant": 1, "file": "<id>__v1.png", "prompt": "<full prompt>", "references": ["..."], "model": "<model name the tool reports>", "time": "<ISO time>"}`
4. Check every image with Pillow:
   - It is square and at least 1024 px.
   - The four corners are near-white, meaning the mean of a 20 x 20 px patch in each corner is above 235 on every channel.
   - If an image fails, make it once more with `Plain pure white background, nothing touching the edges.` stressed at the start of the prompt.
5. Run:
   `python tools\images\review_page.py <batch> --round N --app app\src\main\assets\images\pictures`
6. Tell the user:
   > Round N is ready. Open `<batch>\round-NN\index.html`. Pick one picture per item, or choose "None of these" and write what to change (tick "closest" on the nearest one). Click Export CSV, save the file in `<batch>` (Downloads also works), then tell me "round N reviewed".

   Then wait.
7. When the user says the round is reviewed, start again at step 1.

## Done
When `--status` shows no open items, it writes `<batch>\picks.json`. Tell the user:
> All 29 pictures are picked. Tell Claude: "image batch 1 picked".

Claude then cuts out the backgrounds, checks the pictures, and builds the final page. After the user's OK, Claude writes the image release, and a later code card copies the images into the app.

## Rules
- **Never write into the repo.** Don't touch `app/`, `docs/` or `items.json` in the clone; don't commit; don't push. Everything goes into `<batch>`.
- **Remake only the open items.** A picked item is final unless the user asks again.
- **No text in the pictures.** No letters, words or labels; the only exception is the numeral 6 for "six". No emojis anywhere.
- **If the image tool cannot take reference images,** tell the user in round 1, continue with the prompts alone, and log `"references": []`.
- **If the image quota runs out,** write `<batch>\STATUS.md` (round, items done, items still open), tell the user, and stop. The next session continues from step 1.
- **If a subject seems unclear or unsuitable** for a 6-year-old (for example "uncle"), make your best two versions anyway and say so in your message; the user decides.

## Checks before telling the user a round is ready
- [ ] Every open item has 2 PNGs in `round-NN`, named `<id>__v1.png` and `<id>__v2.png`.
- [ ] `generation_log.jsonl` has one line per image.
- [ ] `round-NN\index.html` exists and lists every open item.
- [ ] Nothing changed in the repo clone (`git status` is clean).

## Commit
None. Claude records the result (evidence log, card status) from `picks.json`.
