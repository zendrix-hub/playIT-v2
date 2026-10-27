#!/usr/bin/env python3
"""Make Lily's talking frames: each released pose with its mouth painted out, for TalkingLily.

The app draws Lily's moving mouth in code (presentation/components/TalkingLily.kt) on top of these frames, so the
mouth can follow her voice. Lily herself is not redrawn: each frame is the released picture
(images/mascot/lily_<pose>.png, image release 2026-10-12-mascot) with only the mouth replaced by the face around it.

For each pose it:
  1. takes the pixels inside the pose's mouth box that are not face orange (the mouth line, its inside and the
     tongue), plus their anti-aliased edge;
  2. fills them from the face around them (each pass gives an unknown pixel the mean of its known neighbours), so the
     gradient of the glossy poses carries on;
  3. writes images/mascot/talk/lily_<pose>.png and prints the mouth's box for MOUTHS in TalkingLily.kt.

Usage: python tools/design/make_lily_talk.py [--sheet before_after.png]
Needs Pillow and NumPy.
"""
from __future__ import annotations

import argparse
from pathlib import Path

import numpy as np
from PIL import Image, ImageDraw

REPO = Path(__file__).resolve().parents[2]
MASCOT = REPO / "app" / "src" / "main" / "assets" / "images" / "mascot"
# The mouth of each pose in the 512 x 512 pictures (x0, y0, x1, y1), found by its colour below the nose
MOUTH_BOXES = {
    "celebrating": (220, 232, 288, 275),
    "encouraging": (223, 228, 288, 243),
    "idle": (216, 226, 281, 243),
    "listening": (226, 231, 272, 243),
    "pointing": (216, 231, 279, 244),
    "thinking": (250, 232, 280, 239),
    "waving": (223, 226, 281, 256),
}
MARGIN = 4


def face_orange(rgb):
    # The face is about (249, 120, 36): far more green than blue. The tongue's red has as much blue as green.
    r, g, b = (rgb[..., i].astype(int) for i in range(3))
    return (r > 190) & (g > 70) & (g < 180) & (b < 80) & (g > 1.8 * b)


def grow(mask, steps):
    out = mask.copy()
    for _ in range(steps):
        grown = out.copy()
        grown[1:, :] |= out[:-1, :]
        grown[:-1, :] |= out[1:, :]
        grown[:, 1:] |= out[:, :-1]
        grown[:, :-1] |= out[:, 1:]
        out = grown
    return out


def fill_from_neighbours(rgb, unknown):
    rgb = rgb.astype(float)
    unknown = unknown.copy()
    while unknown.any():
        known = ~unknown
        total = np.zeros_like(rgb)
        count = np.zeros(unknown.shape)
        for dy, dx in ((1, 0), (-1, 0), (0, 1), (0, -1), (1, 1), (-1, -1), (1, -1), (-1, 1)):
            shifted_known = np.roll(np.roll(known, dy, 0), dx, 1)
            shifted_rgb = np.roll(np.roll(rgb, dy, 0), dx, 1)
            total += shifted_rgb * shifted_known[..., None]
            count += shifted_known
        ready = unknown & (count > 0)
        rgb[ready] = total[ready] / count[ready][:, None]
        unknown &= ~ready
    return rgb


def remove_mouth(pose):
    image = Image.open(MASCOT / f"lily_{pose}.png").convert("RGBA")
    a = np.asarray(image).copy()
    x0, y0, x1, y1 = MOUTH_BOXES[pose]
    box = np.zeros(a.shape[:2], bool)
    box[y0 - MARGIN:y1 + MARGIN + 1, x0 - MARGIN:x1 + MARGIN + 1] = True
    mouth = box & ~face_orange(a[..., :3]) & (a[..., 3] > 0)
    mouth = grow(mouth, 2) & grow(box, 2)
    filled = fill_from_neighbours(a[..., :3], mouth)
    a[..., :3] = np.clip(filled + 0.5, 0, 255).astype(np.uint8)
    a[mouth, 3] = 255
    out = Image.fromarray(a, "RGBA")
    (MASCOT / "talk").mkdir(exist_ok=True)
    out.save(MASCOT / "talk" / f"lily_{pose}.png", optimize=True)
    return image, out


def main():
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--sheet", help="also write a before and after sheet, zoomed on the faces")
    args = parser.parse_args()
    pairs = []
    for pose, (x0, y0, x1, y1) in MOUTH_BOXES.items():
        before, after = remove_mouth(pose)
        pairs.append((pose, before, after))
        print(f"{pose}: mouth centre ({(x0 + x1) / 2:.0f}, {(y0 + y1) / 2:.0f}), top {y0}, width {x1 - x0}")
    if args.sheet:
        crop = (130, 120, 382, 320)
        w, h = (crop[2] - crop[0]) * 2, (crop[3] - crop[1]) * 2
        sheet = Image.new("RGB", (w * 2 + 30, (h + 24) * len(pairs)), "#DDE6F0")
        draw = ImageDraw.Draw(sheet)
        for i, (pose, before, after) in enumerate(pairs):
            for j, im in enumerate((before, after)):
                face = im.crop(crop).resize((w, h), Image.NEAREST)
                sheet.paste(face, (j * (w + 30), i * (h + 24)), face)
            draw.text((4, i * (h + 24) + h + 4), pose, fill="black")
        sheet.save(args.sheet)


if __name__ == "__main__":
    main()
