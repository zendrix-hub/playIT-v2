"""The new Lily (user, 2026-11-07: "this is our updated mascot i want you to change the moscots on the app to that").

Sources, in <batch>/source/:
- user_front.jpg: the user's picture of her standing (idle);
- user_sheet.jpg: the user's sheet of four poses (waving, celebrating, thinking, encouraging);
- canva_page15.png: page 15 of the Canva design "PlayIT Canva pictures" (DAHZTibHMH8), two takes each of the two
  poses the sheet has no picture for, listening and pointing, made by Canva's image AI from the user's two pictures
  (user: "you can make that on canva the new poses but be sure to not to change the design of the mascot").

For every pose it:
1. cuts the white background out (tools/images/cutout.py's cut: border-connected white, plus the white holes listed
   in POSES, such as the gaps between an arm on a hip and the body), and keeps the figure in the pose's part of the
   source;
2. places it on a 512 x 512 canvas (and a 1024 master) at one scale for every pose, set by the white of her eyes, so
   she keeps her size as she changes pose: standing poses with their feet midpoint at the bottom centre the app
   pivots on (LILY_FEET_Y = 453/512, x 256), the jump with its belly over that point and a little higher;
3. measures the white of each eye (PoseEyes in LilyEyes.kt) and the mouth's box (PoseMouth in LilyMouth.kt);
4. paints the mouth out with the muzzle around it, for the talking frame TalkingLily draws its mouth on;
5. writes a review page: each pose as the app shows it, its talking frame, and one row per take.

Usage:
  python -I tools/images/make_new_lily.py <batch>
  python -I tools/images/make_new_lily.py <batch> --release <docs/image-release/<date>> --csv <review csv>
      copies the takes the user marked OK into the release folder with its manifest.json;
      add --install app to copy them into the app (mascot pictures into assets, the icon into res)
Needs NumPy, SciPy and Pillow.
"""
import argparse
import csv
import hashlib
import html
import json
import pathlib
import shutil
import sys

import numpy as np
from PIL import Image, ImageDraw
from scipy import ndimage

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
from cutout import cut  # noqa: E402

SIZE = 512
FEET_Y = 453          # LILY_FEET_Y * 512, where TalkingLily pivots her
CENTRE_X = 256
SCLERA_D = 66         # the white of an eye across, in the 512 picture: her height then about matches the old Lily
JUMP_RISE = 16        # the jump's ears sit this much higher than the standing poses'
MARGIN = 6
FUR = np.array([250, 132, 44])  # #FA842C, her orange, sampled from user_front.jpg

# pose: (source, region x0, y0, x1, y1 in source pixels, holes as source pixel points)
POSES = {
    "idle": ("user_front.jpg", (0, 0, 2048, 2048), []),
    "waving": ("user_sheet.jpg", (0, 0, 1024, 1024), []),
    "celebrating": ("user_sheet.jpg", (1024, 0, 2048, 1024), []),
    "thinking": ("user_sheet.jpg", (0, 1024, 1024, 2048), []),
    # the gaps inside her elbows and between her tail and her hip are background
    "encouraging": ("user_sheet.jpg", (1024, 1024, 2048, 2048), [(1229, 1626), (1695, 1626), (1727, 1703), (1686, 1748)]),
    # the gaps between her fingers, and between her paw and her face
    "listening_a": ("canva_page15.png", (0, 0, 1536, 1536), [(301, 594), (412, 760)]),
    "listening_b": ("canva_page15.png", (1536, 0, 3072, 1536), [(2606, 738)]),
    "pointing_a": ("canva_page15.png", (0, 1536, 1536, 3072), []),
    "pointing_b": ("canva_page15.png", (1536, 1536, 3072, 3072), []),
}
SOURCE_NOTE = {
    "user_front.jpg": "the user's picture (2026-11-07)",
    "user_sheet.jpg": "the user's four-pose sheet (2026-11-07)",
    "canva_page15.png": "Canva image AI from the user's two pictures, page 15 of design DAHZTibHMH8",
}
# The app's pose for each take; a pose with two takes waits for the user's pick.
APP_POSE = {"idle": "idle", "waving": "waving", "celebrating": "celebrating", "thinking": "thinking",
            "encouraging": "encouraging", "listening_a": "listening", "listening_b": "listening",
            "pointing_a": "pointing", "pointing_b": "pointing"}
JUMPS = {"celebrating"}


def figure(rgba, region):
    """The opaque components whose centre lies in [region], everything else cleared."""
    solid = rgba[..., 3] > 8
    lab, n = ndimage.label(solid)
    keep = np.zeros(n + 1, bool)
    x0, y0, x1, y1 = region
    areas = ndimage.sum(solid, lab, range(1, n + 1))
    for i, (cy, cx) in enumerate(ndimage.center_of_mass(solid, lab, range(1, n + 1)), 1):
        if x0 <= cx < x1 and y0 <= cy < y1 and areas[i - 1] > 200:
            keep[i] = True
    out = rgba.copy()
    out[~keep[lab]] = 0
    # Slivers of white left in a hole's sharp corners, cut off from it by the soft edge: white that touches the
    # background goes too (her eye whites and teeth are inside her and never touch it).
    gone = out[..., 3] < 8
    whiteish = (out[..., :3].min(axis=2) >= 200) & ~gone
    wl, wn = ndimage.label(whiteish)
    near = ndimage.binary_dilation(gone, iterations=3)
    if wn:
        sizes = ndimage.sum(whiteish, wl, range(1, wn + 1))
        touching = np.unique(wl[near & (wl > 0)])
        small = [i for i in touching if sizes[i - 1] < 0.002 * (~gone).sum()]
        drop = ndimage.binary_dilation(np.isin(wl, small), iterations=1) & (out[..., :3].min(axis=2) >= 170)
        out[drop] = 0
    return out


def eye_whites(rgba):
    """The two eye whites as (cx, cy, r), left one first: white regions enclosed by the figure, the two largest."""
    rgb, a = rgba[..., :3].astype(int), rgba[..., 3]
    white = (rgb.min(axis=2) >= 235) & (a > 200)
    lab, n = ndimage.label(white)
    if n == 0:
        return []
    areas = ndimage.sum(white, lab, range(1, n + 1))
    eyes = []
    for i in np.argsort(areas)[::-1][:2]:
        if areas[i] < 0.002 * a.astype(bool).sum():
            break
        ys, xs = np.where(lab == i + 1)
        w, h = xs.max() - xs.min() + 1, ys.max() - ys.min() + 1
        if not 0.8 < w / h < 1.25:
            continue
        eyes.append(((xs.min() + xs.max()) / 2, (ys.min() + ys.max()) / 2, (w + h) / 4))
    return sorted(eyes)


def feet_midpoint(rgba):
    """The midpoint between her two feet: the two widest runs of opaque columns along her bottom."""
    a = rgba[..., 3] > 128
    ys = np.where(a.any(axis=1))[0]
    bottom = ys.max()
    band = a[max(0, bottom - int(0.03 * (bottom - ys.min()))):bottom + 1].any(axis=0)
    lab, n = ndimage.label(band)
    runs = sorted((((lab == i).sum()), i) for i in range(1, n + 1))[-2:]
    xs = np.concatenate([np.where(lab == i)[0] for _, i in runs])
    return (xs.min() + xs.max()) / 2, bottom


def belly_centre(rgba):
    rgb, a = rgba[..., :3].astype(int), rgba[..., 3]
    cream = (rgb[..., 0] > 245) & (rgb[..., 1] > 215) & (rgb[..., 1] < 245) & (rgb[..., 2] > 180) & (rgb[..., 2] < 225) & (a > 200)
    ys, xs = np.where(cream)
    return xs.mean(), ys.mean()


def place(rgba, scale, anchor, at, size):
    """[rgba] scaled by [scale], moved so source point [anchor] lands on [at]; shifted in if it pokes out."""
    im = Image.fromarray(rgba, "RGBA")
    box = im.getchannel("A").point(lambda v: 255 if v > 8 else 0).getbbox()
    im = im.crop(box)
    w, h = round(im.width * scale), round(im.height * scale)
    im = im.resize((w, h), Image.LANCZOS)
    left = at[0] - (anchor[0] - box[0]) * scale
    top = at[1] - (anchor[1] - box[1]) * scale
    m = MARGIN * size / SIZE
    shift = 0.0
    if left < m:
        shift = m - left
    elif left + w > size - m:
        shift = size - m - (left + w)
    if w > size - 2 * m or h > size - 2 * m:
        raise SystemExit(f"a pose is {w} x {h}, too big for {size}: lower SCLERA_D")
    left += shift
    top = min(max(top, m), size - m - h)
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    canvas.paste(im, (round(left), round(top)))
    return canvas, (box, scale, left - box[0] * scale, top - box[1] * scale, shift)


def grow(mask, steps):
    return ndimage.binary_dilation(mask, iterations=steps) if steps else mask


def mouth_of(rgba, eyes):
    """The mouth's box (x0, y0, x1, y1): the biggest dark shape below her nose, with the tongue and teeth in it."""
    rgb, a = rgba[..., :3].astype(int), rgba[..., 3]
    r, g, b = rgb[..., 0], rgb[..., 1], rgb[..., 2]
    dark = (r < 150) & (g < 120) & (b < 120) & (a > 200)
    (lx, ly, lr), (rx, ry, rr) = eyes
    mid, span = (lx + rx) / 2, rx - lx
    y_eyes = max(ly, ry)
    zone = np.zeros_like(dark)
    zone[int(y_eyes + 0.5 * lr):int(y_eyes + 1.9 * span), int(mid - 0.6 * span):int(mid + 0.6 * span)] = True
    lab, n = ndimage.label(dark & zone)
    if n < 2:
        raise SystemExit("no nose and mouth found")
    parts = []
    for i in range(1, n + 1):
        ys, xs = np.where(lab == i)
        if len(ys) < 12:
            continue
        parts.append((ys.min(), xs.min(), ys.max(), xs.max(), len(ys)))
    parts.sort()
    nose = parts[0]
    below = [p for p in parts[1:] if p[0] > nose[2] - 2]
    m = max(below, key=lambda p: p[4])
    return with_inside(rgba, dark, m[1], m[0], m[3], m[2])


def with_inside(rgba, dark, x0, y0, x1, y1):
    """The box of a mouth's dark shape grown to take in the tongue and teeth that touch it."""
    rgb = rgba[..., :3].astype(int)
    r, g, b = rgb[..., 0], rgb[..., 1], rgb[..., 2]
    inside = ((r > 220) & (g < 175) & (b > 90) & (b < 175)) | (rgb.min(axis=2) > 225)
    box = np.zeros_like(dark)
    box[y0 - 2:y1 + 3, x0 - 2:x1 + 3] = True
    lab2, _ = ndimage.label(inside)  # whole shapes: a tongue can hang below the dark part's box
    touch = np.unique(lab2[grow(dark & box, 2) & (lab2 > 0)])
    if len(touch):
        ys, xs = np.where(np.isin(lab2, touch))
        y0, x0, y1, x1 = min(y0, ys.min()), min(x0, xs.min()), max(y1, ys.max()), max(x1, xs.max())
    return [int(x0), int(y0), int(x1), int(y1)]


def paint_out_mouth(img, box):
    """The mouth replaced by the muzzle around it: every pixel in the box that is not muzzle-coloured, plus its soft
    edge, gets the mean of its known neighbours, pass after pass."""
    rgba = np.asarray(img).astype(float)
    rgb = rgba[..., :3]
    x0, y0, x1, y1 = box
    pad = 4
    region = np.zeros(rgba.shape[:2], bool)
    region[y0 - pad:y1 + pad + 1, x0 - pad:x1 + pad + 1] = True
    # Only the mouth's own colours (dark, the tongue's pink, the teeth's white) and their soft edge: the fur's orange
    # and the muzzle's edge against it stay as drawn.
    r, g, b = rgb[..., 0], rgb[..., 1], rgb[..., 2]
    mouthlike = ((r < 170) & (g < 140)) | ((r > 220) & (g < 175) & (b > 95)) | (rgb.min(axis=2) > 225)
    fur = np.abs(rgb - FUR).max(axis=2) < 36
    # Only the shapes that reach into the mouth's own box: the nose just above it stays.
    lab, _ = ndimage.label(mouthlike & region)
    inner = np.zeros_like(region)
    inner[y0:y1 + 1, x0:x1 + 1] = True
    mouth = np.isin(lab, np.unique(lab[inner & (lab > 0)]))
    mouth &= ~(np.arange(rgba.shape[0])[:, None] < y0 - 1)
    unknown = grow(mouth & region, 2) & region & ~fur & ~grow(mouthlike & ~mouth, 1)
    filled = unknown.copy()
    known = ~unknown
    out = rgb.copy()
    while unknown.any():
        acc = np.zeros_like(out)
        cnt = np.zeros(out.shape[:2])
        for dy, dx in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            k = np.roll(known, (dy, dx), axis=(0, 1))
            v = np.roll(out, (dy, dx), axis=(0, 1))
            acc += v * k[..., None]
            cnt += k
        fill = unknown & (cnt > 0)
        out[fill] = acc[fill] / cnt[fill][:, None]
        known = known | fill
        unknown = unknown & ~fill
    for _ in range(3):  # smooth the streaks the passes leave, inside the filled pixels only
        soft = ndimage.uniform_filter(out, size=(3, 3, 1))
        out[filled] = soft[filled]
    res = rgba.copy()
    res[..., :3] = out
    return Image.fromarray(np.clip(res + 0.5, 0, 255).astype(np.uint8), "RGBA"), int(unknown.sum())


def sha256(p):
    return hashlib.sha256(pathlib.Path(p).read_bytes()).hexdigest()


def build(batch):
    src = {name: np.asarray(Image.open(batch / "source" / name).convert("RGB")) for name in
           {v[0] for v in POSES.values()}}
    cuts = {}
    for name, rgb in src.items():
        h, w = rgb.shape[:2]
        holes = [(x / w, y / h) for p in POSES.values() if p[0] == name for (x, y) in p[2]]
        cuts[name] = cut(rgb, holes=holes)
    out = batch / "out"
    for d in ("mascot", "mascot/talk", "master", "master/talk"):
        (out / d).mkdir(parents=True, exist_ok=True)
    figs, eyes = {}, {}
    for pose, (name, region, _) in POSES.items():
        figs[pose] = figure(cuts[name], region)
        eyes[pose] = eye_whites(figs[pose])
    scales = {p: SCLERA_D / (2 * np.mean([e[2] for e in eyes[p]])) for p in POSES if len(eyes[p]) == 2}
    for p in POSES:
        if p not in scales:  # eyes shut (the jump): the scale of the other poses from its source
            same = [scales[q] for q in scales if POSES[q][0] == POSES[p][0]]
            scales[p] = float(np.mean(same))
    standing_top = None
    info = {}
    for pose in POSES:
        rgba, s = figs[pose], scales[pose]
        if pose in JUMPS:
            continue
        foot, bottom = feet_midpoint(rgba)
        top = np.where((rgba[..., 3] > 8).any(axis=1))[0].min()
        standing_top = FEET_Y - (bottom - top) * s if standing_top is None else standing_top
        info[pose] = ((foot, bottom), (CENTRE_X, FEET_Y))
    for pose in JUMPS:
        rgba = figs[pose]
        bx, _ = belly_centre(rgba)
        top = np.where((rgba[..., 3] > 8).any(axis=1))[0].min()
        info[pose] = ((bx, top), (CENTRE_X, standing_top - JUMP_RISE))
    meta = {}
    for pose in POSES:
        anchor, at = info[pose]
        for size, folder in ((SIZE, "mascot"), (1024, "master")):
            k = size / SIZE
            img, (box, s, ox, oy, shift) = place(figs[pose], scales[pose] * k, anchor, (at[0] * k, at[1] * k), size)
            img.save(out / folder / f"lily_{pose}.png", optimize=True)
            if size == SIZE:
                arr = np.asarray(img)
                e = eye_whites(arr)
                mouth = mouth_of(arr, e) if len(e) == 2 else None  # eyes shut: found below, by jump_mouth
                meta[pose] = {"scale": float(round(scales[pose], 4)), "shiftX": float(round(shift, 1)),
                              "eyes": [[float(round(v, 1)) for v in x] for x in e], "mouth": mouth}
        talk(out, pose, meta[pose]["mouth"])
    for pose in JUMPS:  # its mouth: found in the 512 picture below the cheeks
        arr = np.asarray(Image.open(out / "mascot" / f"lily_{pose}.png"))
        meta[pose]["mouth"] = jump_mouth(arr)
        talk(out, pose, meta[pose]["mouth"])
    (out / "measurements.json").write_text(json.dumps(meta, indent=1))
    make_icon(out, meta["idle"])
    write_review(batch, meta)
    for p, m in meta.items():
        print(p, m)


# The app icon keeps the old one's layout (scripts/generate_splash_style_app_icon.py: a cream tile, her head rising
# from its bottom edge, a dark rim on the square and round icons); only Lily is the new one, from her idle picture.
ICON_BG_TOP, ICON_BG_BOTTOM = (255, 250, 235), (255, 240, 212)
ICON_RIM = (45, 55, 62, 255)


def icon_head(out, idle):
    """Her head from the idle master (1024): ears, eye rings and muzzle, the tail left out, the body carried straight
    down below the cut so it can run off the tile's bottom edge. Returns (RGBA, eye line y in it)."""
    im = np.asarray(Image.open(out / "master" / "lily_idle.png")).copy()
    (lx, ly, _), (rx, _, _) = [[2 * v for v in e] for e in idle["eyes"]]
    cx, eye_y = round((lx + rx) / 2), round(ly)
    half = round(1.55 * (rx - lx))
    x0, x1 = cx - half, cx + half
    top = np.where((im[:, x0:x1, 3] > 8).any(axis=1))[0].min()
    # The cut ends above her belly and arms, so the rows carried down are plain fur and its shade.
    rgb = im[..., :3].astype(int)
    cream = (rgb[..., 0] > 245) & (rgb[..., 1] > 215) & (rgb[..., 1] < 245) & (rgb[..., 2] > 180) & (rgb[..., 2] < 225)
    below = eye_y + round(0.8 * (rx - lx))
    belly_top = below + np.where(cream[below:, cx - 20:cx + 20].any(axis=1))[0].min()
    # the first row below her face that is fur all across (no muzzle, cheek or belly colours in it)
    fur = np.abs(rgb - FUR).max(axis=2) < 40
    shade = np.abs(rgb - np.array([233, 109, 37])).max(axis=2) < 30
    solid = im[..., 3] > 200
    plain = [y for y in range(eye_y, belly_top) if (solid[y, x0:x1] <= (fur | shade)[y, x0:x1]).all()]
    if not plain:
        raise SystemExit("no plain row of fur between her face and her belly")
    y1 = plain[0] + 3
    head = im[top:y1, x0:x1].copy()
    # The tail's tuft reaches in at her right: keep only the shape joined to her head.
    lab, _ = ndimage.label(head[..., 3] > 8)
    mid = lab[eye_y - top, half]
    head[(lab != mid) & (lab > 0)] = 0
    # the mean of its last rows, so the picture's grain doesn't run down as streaks
    row = np.round(head[-8:].astype(float).mean(axis=0, keepdims=True)).astype(np.uint8)
    body = np.repeat(row, 700, axis=0)
    return np.concatenate([head, body]), eye_y - top


def icon_tile(head, eye_y, size, width, eye_at, shape):
    """[head] scaled to [width] with its eye line at [eye_at], on the cream tile; [shape] 'square', 'round' or None
    (the adaptive layers: no mask, no rim)."""
    bg = Image.new("RGBA", (size, size))
    d = np.zeros((size, size, 4), np.uint8)
    for y in range(size):
        t = y / (size - 1)
        d[y, :, :3] = [round(a * (1 - t) + b * t) for a, b in zip(ICON_BG_TOP, ICON_BG_BOTTOM)]
    d[..., 3] = 255
    bg = Image.fromarray(d, "RGBA")
    k = width / head.shape[1]
    h = Image.fromarray(head, "RGBA").resize((round(head.shape[1] * k), round(head.shape[0] * k)), Image.LANCZOS)
    fg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    fg.alpha_composite(h.crop((0, 0, h.width, min(h.height, size + 50))), (round((size - h.width) / 2), max(0, round(eye_at - eye_y * k))))
    if shape is None:
        return fg, bg
    tile = bg.copy()
    tile.alpha_composite(fg)
    m = round(size * 16 / 512)
    mask = Image.new("L", (size * 4, size * 4), 0)
    dm = ImageDraw.Draw(mask)
    box = [m * 4, m * 4, (size - m) * 4, (size - m) * 4]
    if shape == "square":
        dm.rounded_rectangle(box, radius=round(size * 115 / 512) * 4, fill=255)
    else:
        dm.ellipse(box, fill=255)
    mask = mask.resize((size, size), Image.LANCZOS)
    outp = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    outp.paste(tile, (0, 0), mask)
    rim = Image.new("L", (size * 4, size * 4), 0)
    dr = ImageDraw.Draw(rim)
    w = round(size * 10 / 512) * 4
    if shape == "square":
        dr.rounded_rectangle(box, radius=round(size * 115 / 512) * 4, outline=255, width=w)
    else:
        dr.ellipse(box, outline=255, width=w)
    rim = rim.resize((size, size), Image.LANCZOS)
    outp.alpha_composite(Image.composite(Image.new("RGBA", (size, size), ICON_RIM), Image.new("RGBA", (size, size), (0, 0, 0, 0)), rim))
    return outp, None


def make_icon(out, idle):
    head, eye_y = icon_head(out, idle)
    (out / "icon").mkdir(exist_ok=True)
    square, _ = icon_tile(head, eye_y, 512, 400, 268, "square")
    rnd, _ = icon_tile(head, eye_y, 512, 372, 276, "round")
    fg, bg = icon_tile(head, eye_y, 512, 318, 262, None)
    square.save(out / "icon" / "ic_launcher.png")
    rnd.save(out / "icon" / "ic_launcher_round.png")
    fg.save(out / "icon" / "ic_launcher_foreground.png")
    bg.save(out / "icon" / "ic_launcher_background.png")
    # What a phone shows of the adaptive icon: the two layers in a circle and in a rounded square, at 72 of 108 dp.
    full = bg.copy()
    full.alpha_composite(fg)
    for name, shape in (("adaptive_circle", "ellipse"), ("adaptive_squircle", "rounded")):
        mask = Image.new("L", (512, 512), 0)
        dm = ImageDraw.Draw(mask)
        inset = round(512 * 18 / 108)
        box = [inset, inset, 512 - inset, 512 - inset]
        dm.ellipse(box, fill=255) if shape == "ellipse" else dm.rounded_rectangle(box, radius=90, fill=255)
        prev = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
        prev.paste(full, (0, 0), mask)
        prev.save(out / "icon" / f"{name}.png")


def jump_mouth(arr):
    """The jump's eyes are shut, so its mouth is found as the biggest dark shape in the face's middle third."""
    rgb, a = arr[..., :3].astype(int), arr[..., 3]
    dark = (rgb[..., 0] < 150) & (rgb[..., 1] < 120) & (rgb[..., 2] < 120) & (a > 200)
    lab, n = ndimage.label(dark)
    parts = []
    for i in range(1, n + 1):
        ys, xs = np.where(lab == i)
        if len(ys) >= 12 and abs((xs.min() + xs.max()) / 2 - CENTRE_X) < 40:
            parts.append((len(ys), xs.min(), ys.min(), xs.max(), ys.max()))
    _, x0, y0, x1, y1 = max(parts)
    return with_inside(arr, dark, x0, y0, x1, y1)


def talk(out, pose, mouth):
    if mouth is None:
        return
    for size, folder in ((SIZE, "mascot"), (1024, "master")):
        k = size / SIZE
        box = [round(v * k) for v in mouth]
        img = Image.open(out / folder / f"lily_{pose}.png")
        frame, left = paint_out_mouth(img, box)
        assert left == 0, pose
        frame.save(out / folder / "talk" / f"lily_{pose}.png", optimize=True)


def write_review(batch, meta):
    rows = []
    for pose in POSES:
        app = APP_POSE[pose]
        rows.append({"file": f"lily_{pose}.png", "group": f"Lily {app}", "source": SOURCE_NOTE[POSES[pose][0]],
                     "pics": [f"out/mascot/lily_{pose}.png"] * 3 + [f"out/mascot/talk/lily_{pose}.png"],
                     "what": "talking frame: 4th picture"})
    rows.append({"file": "app_icon", "group": "App icon (home screen)", "source": "her head from the idle picture, on the old icon's cream tile",
                 "pics": ["out/icon/ic_launcher.png", "out/icon/ic_launcher_round.png", "out/icon/adaptive_circle.png",
                          "out/icon/adaptive_squircle.png"],
                 "what": "square and round icons (older phones), then the adaptive icon as a circle and a rounded square"})
    if (batch / "out" / "splash").exists():  # the opening's Lily, drawn in code (LilyPuppet.kt), as tests render it
        rows.append({"file": "opening_lily", "group": "Opening screen (Lily drawn in the app's code)",
                     "source": "LilyPuppet.kt redrawn in the new design; she moves, blinks, looks about and answers taps",
                     "pics": [f"out/splash/splash_{n}.png" for n in ("compact", "a21s", "phone", "tablet")],
                     "what": "the first screen on four phone sizes (small, Galaxy A21s, phone, tablet)"})
    data = json.dumps(rows).replace("</", "<\\/")
    page = f"""<!doctype html>
<meta charset="utf-8">
<title>playIT: the new Lily</title>
<style>
body {{ font-family: system-ui, sans-serif; margin: 24px; color: #1d2b36; }}
.row {{ display: flex; gap: 16px; align-items: center; border-bottom: 1px solid #dde3e8; padding: 10px 0; flex-wrap: wrap; }}
.tile {{ width: 200px; height: 200px; border-radius: 12px; display: flex; align-items: center; justify-content: center; }}
.tile img {{ width: 192px; height: 192px; object-fit: contain; }}
.light {{ background: #fff8ef; }} .dark {{ background: #24343f; }} .green {{ background: #cfe8c0; }}
h2 {{ background: #eef3f7; padding: 6px 8px; margin: 18px 0 0; font-size: 1.05em; }}
.bar {{ position: sticky; top: 0; background: #fff; padding: 8px 0; border-bottom: 2px solid #1d2b36; z-index: 2; }}
button {{ font-size: 1em; padding: 6px 14px; margin-right: 8px; }}
.meta {{ font-size: .85em; color: #555; max-width: 260px; }}
textarea {{ width: 220px; height: 34px; }}
@keyframes blink {{ 0%, 88%, 100% {{ transform: scaleY(0); }} 92%, 96% {{ transform: scaleY(1); }} }}
</style>
<h1>playIT: the new Lily</h1>
<p>Your new Lily, cut out of your two pictures, plus two takes each of the poses your pictures have no picture for
(listening and pointing), made on Canva from your pictures. Each row shows the picture as the app shows it on a light,
a dark and a map-green background (look for white edges), and its talking frame (the mouth painted out: the app draws
her moving mouth there while she speaks). For each pose mark the take OK, or FIX with a note. For listening and
pointing mark exactly ONE take OK and the other FIX. Export CSV when done.</p>
<div class="bar"><button id="export">Export CSV</button><button id="clear">Clear my answers</button><span id="status"></span></div>
<div id="rows"></div>
<script>
const ROWS = {data};
const KEY = "review-{batch.name}";
let answers = {{}};
try {{ answers = JSON.parse(localStorage.getItem(KEY) || "{{}}"); }} catch (e) {{}}
const save = () => {{ try {{ localStorage.setItem(KEY, JSON.stringify(answers)); }} catch (e) {{}}
  document.getElementById("status").textContent = Object.keys(answers).filter(k => answers[k].v).length + " of " + ROWS.length + " marked"; }};
const box = document.getElementById("rows");
let group = null;
for (const r of ROWS) {{
  if (r.group !== group) {{ group = r.group; const h = document.createElement("h2"); h.textContent = group; box.appendChild(h); }}
  const a = answers[r.file] || (answers[r.file] = {{ v: "", note: "" }});
  const row = document.createElement("div"); row.className = "row";
  const bgs = ["light", "dark", "green", "light"];
  row.innerHTML = r.pics.map((p, i) => `<div class="tile ${{bgs[i]}}"><img src="${{p}}"></div>`).join("") + `
    <div class="meta"><b>${{r.file}}</b><br>${{r.source}}<br>${{r.what}}</div>
    <div><label><input type="radio" name="${{r.file}}" value="OK"> OK</label>
    <label><input type="radio" name="${{r.file}}" value="FIX"> FIX</label><br><textarea placeholder="note"></textarea></div>`;
  for (const i of row.querySelectorAll("input")) {{ i.checked = a.v === i.value; i.onchange = () => {{ a.v = i.value; save(); }}; }}
  const t = row.querySelector("textarea"); t.value = a.note; t.oninput = () => {{ a.note = t.value; save(); }};
  box.appendChild(row);
}}
save();
const q = s => '"' + String(s).replace(/"/g, '""') + '"';
document.getElementById("export").onclick = () => {{
  const lines = ["file,group,source,OK_or_FIX,note"].concat(ROWS.map(r => [r.file, r.group, r.source, answers[r.file].v, answers[r.file].note].map(q).join(",")));
  const blob = new Blob(["\\ufeff" + lines.join("\\r\\n")], {{ type: "text/csv" }});
  const link = document.createElement("a"); link.href = URL.createObjectURL(blob); link.download = "{batch.name}_review.csv"; link.click();
}};
document.getElementById("clear").onclick = () => {{ if (confirm("Clear every answer?")) {{ answers = {{}}; save(); location.reload(); }} }};
</script>
"""
    (batch / "index.html").write_text(page, encoding="utf-8")


def release(batch, out_dir, csv_path, choose=None):
    """[choose] {app pose: take} settles a pose whose takes the user all marked OK (the chosen one must be OK)."""
    rows = {r["file"]: r for r in csv.DictReader(open(csv_path, encoding="utf-8-sig"))}
    meta = json.loads((batch / "out" / "measurements.json").read_text())
    choose = choose or {}
    picks, chosen = {}, {}
    for pose in POSES:
        r = rows.get(f"lily_{pose}.png")
        if r and r["OK_or_FIX"].strip().upper() == "OK":
            app = APP_POSE[pose]
            if app in choose:
                if choose[app] == pose:
                    picks[app] = pose
                    chosen[app] = [q for q in POSES if APP_POSE[q] == app]
                continue
            if app in picks:
                raise SystemExit(f"two OK takes for {app}: pass --choose {app}=<take>")
            picks[app] = pose
    missing = sorted(set(APP_POSE.values()) - set(picks))
    if missing:
        raise SystemExit(f"no OK take for {missing}")
    out_dir = pathlib.Path(out_dir)
    (out_dir / "mascot" / "talk").mkdir(parents=True, exist_ok=True)
    (out_dir / "source").mkdir(parents=True, exist_ok=True)
    for name in SOURCE_NOTE:
        shutil.copyfile(batch / "source" / name, out_dir / "source" / name)
    images = []
    for app, pose in sorted(picks.items()):
        for sub, what in (("", "pose"), ("talk/", "talking frame (mouth painted out; TalkingLily draws the mouth)")):
            dest = out_dir / "mascot" / sub / f"lily_{app}.png"
            shutil.copyfile(batch / "out" / "mascot" / sub / f"lily_{pose}.png", dest)
            images.append({
                "itemId": f"lily_{'talk_' if sub else ''}{app}", "file": f"mascot/{sub}lily_{app}.png",
                "appPath": f"images/mascot/{sub}lily_{app}.png", "word": f"Lily {app}, {what}",
                "sourceBatch": batch.name, "pick": f"out/mascot/{sub}lily_{pose}.png",
                "source": f"source/{POSES[pose][0]} ({SOURCE_NOTE[POSES[pose][0]]})",
                "tool": "tools/images/make_new_lily.py", "eyes512": meta[pose]["eyes"], "mouthBox512": meta[pose]["mouth"],
                "reviewCsv": pathlib.Path(csv_path).name, "userVerdict": "OK",
                "userNote": rows[f"lily_{pose}.png"]["note"].strip(), "sha256": sha256(dest),
                **({"choice": f"the user marked every take OK ({', '.join(chosen[app])}); Claude chose {pose}: "
                              f"{CHOICE_WHY.get(pose, '')}"} if app in chosen else {})})
    icon = rows.get("app_icon")
    if icon and icon["OK_or_FIX"].strip().upper() == "OK":
        (out_dir / "icon").mkdir(exist_ok=True)
        for name in ICON_FILES:
            dest = out_dir / "icon" / f"{name}.png"
            shutil.copyfile(batch / "out" / "icon" / f"{name}.png", dest)
            images.append({
                "itemId": name, "file": f"icon/{name}.png", "resPath": ICON_RES[name],  # not appPath: not an asset
                "word": "the app icon: her head from the idle picture on the old icon's cream tile",
                "sourceBatch": batch.name, "pick": f"out/icon/{name}.png", "source": "release mascot/lily_idle.png",
                "tool": "tools/images/make_new_lily.py (make_icon)", "reviewCsv": pathlib.Path(csv_path).name,
                "userVerdict": "OK", "userNote": icon["note"].strip(), "sha256": sha256(dest)})
    manifest = {"release": out_dir.name, "approvedBy": f"user: OK marks on review page {batch.name} ({pathlib.Path(csv_path).name})",
                "decision": "user, 2026-11-07: \"this is our updated mascot i want you to change the moscots on the app to that\"; "
                            "listening and pointing from Canva (user: \"you can make that on canva the new poses but be sure to not to change the design of the mascot\")",
                "teacherAudit": "pending, required before merge to main",
                "images": images}
    (out_dir / "manifest.json").write_text(json.dumps(manifest, indent=1, ensure_ascii=False) + "\n", encoding="utf-8")
    shutil.copyfile(csv_path, out_dir / pathlib.Path(csv_path).name)
    print(f"released {len(images)} images to {out_dir}: {picks}")


CHOICE_WHY = {
    "listening_b": "her paw cupped at her ear reads as listening; take a, a paw beside her face, reads like a wave",
    "pointing_a": "the two takes are near the same; this one's measurements were already in the app code",
}
ICON_FILES = ("ic_launcher", "ic_launcher_round", "ic_launcher_foreground", "ic_launcher_background")
ICON_RES = {n: f"res/mipmap-*dpi/{n}.png (48 to 192 px), res/drawable-xxxhdpi" for n in ICON_FILES}
# The sizes the old icon set used (scripts/generate_splash_style_app_icon.py), so only the pictures change.
DENSITIES = {"mipmap-mdpi": 48, "mipmap-hdpi": 72, "mipmap-xhdpi": 96, "mipmap-xxhdpi": 144, "mipmap-xxxhdpi": 192}


def install(release_dir, app_dir):
    """Copies a release into the app: the mascot pictures into assets/images/mascot, and the icon into res, at the
    old icon set's sizes (the Play Store picture and the adaptive layers at 512 in drawable-xxxhdpi)."""
    release_dir, app_dir = pathlib.Path(release_dir), pathlib.Path(app_dir)
    m = json.loads((release_dir / "manifest.json").read_text(encoding="utf-8"))
    for e in m["images"]:
        src = release_dir / e["file"]
        assert sha256(src) == e["sha256"], e["file"]
        if e.get("appPath", "").startswith("images/"):
            shutil.copyfile(src, app_dir / "src" / "main" / "assets" / e["appPath"])
    res = app_dir / "src" / "main" / "res"
    if (release_dir / "icon").exists():
        pics = {n: Image.open(release_dir / "icon" / f"{n}.png") for n in ICON_FILES}
        for folder, px in DENSITIES.items():
            for n, im in pics.items():
                im.resize((px, px), Image.LANCZOS).save(res / folder / f"{n}.png")
        pics["ic_launcher"].save(res / "drawable-xxxhdpi" / "ic_launcher_playstore.png")
        pics["ic_launcher_foreground"].save(res / "drawable-xxxhdpi" / "ic_launcher_foreground.png")
        pics["ic_launcher_background"].save(res / "drawable-xxxhdpi" / "ic_launcher_background.png")
    print(f"installed {release_dir.name} into {app_dir}")


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("batch")
    ap.add_argument("--release")
    ap.add_argument("--csv")
    ap.add_argument("--choose", help="pose=take,... for a pose whose takes the user all marked OK")
    ap.add_argument("--install", help="the app folder (app/): copy the release's pictures and icon into it")
    a = ap.parse_args(argv)
    batch = pathlib.Path(a.batch)
    if a.release:
        release(batch, a.release, a.csv, dict(kv.split("=") for kv in a.choose.split(",")) if a.choose else None)
        if a.install:
            install(a.release, a.install)
    else:
        build(batch)


if __name__ == "__main__":
    main()
