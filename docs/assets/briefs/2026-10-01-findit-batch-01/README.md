# Image batch 1 (2026-10-01): Find It and key-word pictures

Executed by agy as card 08 (`docs/tasks/card-08-image-batch-01-findit.md`). `items.json` holds the 29 items, their prompts and the 6 style references.

## How the items were chosen
Claude reviewed a contact sheet of all 63 `picture_*.png` files on 2026-10-01.
- **Placeholders, 24:** ant, axe, egg, envelope, igloo, ink, king, map, net, nut, owl, ox, quilt, six, snake, star, top, uncle, vase, vest, wing, worm, yak, yarn. They have 2-6 colours and no anti-aliasing. Some have content problems too: the king is only a crown, and the top is a red diamond.
- **Key-word fixes, 4:**
  - apple is drawn like an orange and is nearly identical to `picture_orange`;
  - goat looks like a green rabbit;
  - tiger and zebra have no stripes.
- **New, 1:** `picture_up.png`. Today `GridGenerator.kt:26` maps "Up" to `blendword_sub.png`, a submarine. Card 13 changes that mapping.
- **Left out:**
  - `picture_bano`, `picture_nino` and `picture_pina` are ñ words. The letter is pending SME review and can't be reached in the app.
  - The 7 words with two different drawings (box, cat, dog, fish, hat, pig, van) need a choice, not new art. That comes in a later pick page.

## Style
- **References:** the best current pictures, which are mouse, cat, duck, pig, gift and sun.
- **What they share:** a thick dark-brown outline, rounded shapes, flat colour with one shade and a small highlight, and a kawaii face on animals.
- **Background:** plain white, not the magenta in 16 §2. Every shape has a closed dark outline, so a flood fill from the border separates the subject cleanly. Magenta would also clash with the pink subjects (pig, worm, yarn).

## After the user's picks (Claude)
1. `tools/images/cutout.py`: a border flood fill removes the white background and keeps the white inside the outline (eyes, the egg). The result is trimmed, padded and saved as 512 px RGBA, matching today's pictures, plus a 1024 px master.
2. `tools/images/audit.py`: checks alpha, white halo, outline colour, padding, colour count and the 96 px preview.
3. A final page shows each cutout on 4 backgrounds (16 §4.3) next to the current image, for the user's OK.
4. Claude writes `docs/image-release/<date>/` (the files and a manifest with SHA-256).
5. Card 13 copies them into `app/src/main/assets/images/pictures/` and maps "Up" to `picture_up.png`.
