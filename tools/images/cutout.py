"""Cut the white background out of picked pictures: transparent 512 px app images plus 1024 px masters.

Claude runs this in WSL with the Kokoro venv (numpy, scipy, Pillow):
  ~/.playit-env/kvenv/bin/python tools/images/cutout.py <picks_dir> <out_dir> [--size 512] [--fill 0.70]

<picks_dir> holds <item>.png (RGB, white background, e.g. docs/assets/briefs/<batch>/picks/).
<out_dir> gets <item>.png (size x size RGBA, what the app ships) and master/<item>.png (1024 px RGBA).

Method (README of the brief, "After the user's picks"):
1. Background = near-white pixels (every channel >= --white) connected to the image border. White that the
   outline encloses (eyes, the egg, highlights) is not connected to the border, so it stays opaque.
2. Edge band = pixels within --band px of the background. Each is treated as a mix of white and the nearest
   solid foreground colour F: P = a*F + (1-a)*255, so a = (255-P)/(255-F) on F's darkest channel, and the
   colour becomes F. This removes the white halo that a hard cut leaves on anti-aliased outlines.
3. Trim to the opaque box, scale so the longer side is --fill of the canvas (the current good pictures use
   about 0.66-0.76 of 512), and centre it.
"""
import argparse
import pathlib

import numpy as np
from PIL import Image
from scipy import ndimage


def cut(rgb, white=235, band=3):
    """rgb: HxWx3 uint8. Returns HxWx4 uint8 with the border-connected white removed."""
    px = rgb.astype(np.float32)
    near_white = px.min(axis=2) >= white
    labels, _ = ndimage.label(near_white)
    border = np.unique(np.concatenate([labels[0], labels[-1], labels[:, 0], labels[:, -1]]))
    bg = np.isin(labels, border[border > 0])

    edge = ndimage.binary_dilation(bg, iterations=band) & ~bg
    solid = ~bg & ~edge
    # colour of the nearest solid pixel, for every pixel
    _, (iy, ix) = ndimage.distance_transform_edt(~solid, return_indices=True)
    fg = px[iy, ix]

    alpha = np.where(bg, 0.0, 1.0)
    darkest = fg.argmin(axis=2)
    f = np.take_along_axis(fg, darkest[..., None], axis=2)[..., 0]
    p = np.take_along_axis(px, darkest[..., None], axis=2)[..., 0]
    a_edge = np.clip((255.0 - p) / np.maximum(255.0 - f, 1.0), 0.0, 1.0)
    alpha[edge] = a_edge[edge]

    out = np.empty(rgb.shape[:2] + (4,), np.uint8)
    colour = np.where(edge[..., None], fg, px)
    colour[bg] = 0
    out[..., :3] = np.clip(colour + 0.5, 0, 255).astype(np.uint8)
    out[..., 3] = np.clip(alpha * 255 + 0.5, 0, 255).astype(np.uint8)
    return out


def place(rgba, size, fill):
    """Trim to the opaque box, scale the longer side to fill*size, centre on a size x size canvas."""
    im = Image.fromarray(rgba, "RGBA")
    box = im.getchannel("A").point(lambda v: 255 if v > 8 else 0).getbbox()
    im = im.crop(box)
    scale = fill * size / max(im.size)
    w, h = max(1, round(im.width * scale)), max(1, round(im.height * scale))
    im = im.resize((w, h), Image.LANCZOS)  # Pillow premultiplies alpha when resizing RGBA
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    canvas.paste(im, ((size - w) // 2, (size - h) // 2))
    return canvas


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("picks_dir")
    ap.add_argument("out_dir")
    ap.add_argument("--size", type=int, default=512)
    ap.add_argument("--fill", type=float, default=0.70)
    ap.add_argument("--white", type=int, default=235)
    ap.add_argument("--band", type=int, default=3)
    a = ap.parse_args(argv)

    src, out = pathlib.Path(a.picks_dir), pathlib.Path(a.out_dir)
    (out / "master").mkdir(parents=True, exist_ok=True)
    files = sorted(src.glob("*.png"))
    for f in files:
        rgba = cut(np.array(Image.open(f).convert("RGB")), a.white, a.band)
        place(rgba, 1024, a.fill).save(out / "master" / f.name, optimize=True)
        place(rgba, a.size, a.fill).save(out / f.name, optimize=True)
        print(f.name)
    print(f"cut {len(files)} pictures into {out}")


if __name__ == "__main__":
    main()
