"""
Build and deploy all 6 IP-as-Logo animal companion avatars to PlayIT production directories.
Uses BFS exterior flood-fill to perfectly preserve light facial/belly regions.
"""

import os
from collections import deque
from PIL import Image, ImageDraw, ImageFilter
import numpy as np

BRAIN_DIR = "/home/zendrix/.gemini/antigravity-cli/brain/af2db79b-86c4-4925-a8b9-dd27044dffec"
WORKSPACE_DIR = "/mnt/c/Users/Zendrix/Desktop/thesis/playIT-v2-workspace"

AVATARS = [
    {
        "id": 1,
        "name": "cat",
        "label": "Cat",
        "src": os.path.join(BRAIN_DIR, "cat_avatar_logo_1789204871247.jpg"),
        "crop_bottom": 0,
        "bg_tol": 45,
    },
    {
        "id": 2,
        "name": "monkey",
        "label": "Monkey",
        "src": os.path.join(BRAIN_DIR, "monkey_avatar_logo_1789204888941.jpg"),
        "crop_bottom": 0,
        "bg_tol": 45,
    },
    {
        "id": 3,
        "name": "bunny",
        "label": "Bunny",
        "src": os.path.join(BRAIN_DIR, "bunny_avatar_logo_1789204922476.jpg"),
        "crop_bottom": 0,
        "bg_tol": 45,
    },
    {
        "id": 4,
        "name": "bear",
        "label": "Bear",
        "src": os.path.join(BRAIN_DIR, "bear_avatar_logo_1789204947241.jpg"),
        "crop_bottom": 0,
        "bg_tol": 45,
    },
    {
        "id": 5,
        "name": "frog",
        "label": "Frog",
        "src": os.path.join(BRAIN_DIR, "frog_avatar_logo_1789204964789.jpg"),
        "crop_bottom": 0,
        "bg_tol": 45,
    },
    {
        "id": 6,
        "name": "owl",
        "label": "Owl",
        "src": os.path.join(BRAIN_DIR, "owl_avatar_logo.png"),
        "crop_bottom": 60,
        "bg_tol": 40,
    }
]

def bfs_isolate_character(img: Image.Image, bg_tolerance: int = 45) -> Image.Image:
    """Exterior BFS flood-fill to only remove connected background pixels."""
    img_rgb = img.convert("RGB")
    w, h = img_rgb.size
    
    # Sample corner pixels to find background color
    corners = [(2, 2), (w - 3, 2), (2, h - 3), (w - 3, h - 3)]
    corner_colors = [img_rgb.getpixel(pt) for pt in corners]
    bg_color = np.median(corner_colors, axis=0)

    data = np.array(img_rgb, dtype=float)
    diff = np.sqrt(np.sum((data - bg_color) ** 2, axis=2))
    is_bg_like = diff < bg_tolerance

    visited = np.zeros((h, w), dtype=bool)
    # Seed queue from all 4 borders to catch any open background
    queue = deque()
    for x in range(w):
        if is_bg_like[0, x] and not visited[0, x]:
            visited[0, x] = True
            queue.append((x, 0))
        if is_bg_like[h - 1, x] and not visited[h - 1, x]:
            visited[h - 1, x] = True
            queue.append((x, h - 1))
    for y in range(h):
        if is_bg_like[y, 0] and not visited[y, 0]:
            visited[y, 0] = True
            queue.append((0, y))
        if is_bg_like[y, w - 1] and not visited[y, w - 1]:
            visited[y, w - 1] = True
            queue.append((w - 1, y))

    while queue:
        cx, cy = queue.popleft()
        for dx, dy in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
            nx, ny = cx + dx, cy + dy
            if 0 <= nx < w and 0 <= ny < h and not visited[ny, nx]:
                if is_bg_like[ny, nx]:
                    visited[ny, nx] = True
                    queue.append((nx, ny))

    alpha = np.where(visited, 0, 255).astype(np.uint8)
    res = img.convert("RGBA")
    res.putalpha(Image.fromarray(alpha))

    bbox = res.getbbox()
    if bbox:
        res = res.crop(bbox)
        
    return res

def apply_stroke(img: Image.Image, stroke_width: int = 8, stroke_color = (45, 55, 62, 255)) -> Image.Image:
    alpha = img.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_width * 2 + 1))
    stroke_layer = Image.new("RGBA", img.size, (0, 0, 0, 0))
    stroke_base = Image.new("RGBA", img.size, stroke_color)
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, img)

def process_avatar(cfg: dict, size: int = 512, outline: int = 8) -> Image.Image:
    img = Image.open(cfg["src"])
    if cfg["crop_bottom"] > 0:
        img = img.crop((0, 0, img.width, img.height - cfg["crop_bottom"]))
        
    iso = bfs_isolate_character(img, bg_tolerance=cfg["bg_tol"])
    
    # Apply soft rounded pebble contour at the base to eliminate sharp crop edges
    w, h = iso.size
    round_mask = Image.new('L', (w, h), 0)
    draw = ImageDraw.Draw(round_mask)
    draw.rounded_rectangle([0, 0, w, h], radius=int(min(w, h) * 0.35), fill=255)

    alpha = iso.split()[3]
    combined_alpha = Image.fromarray(np.minimum(np.array(alpha), np.array(round_mask)))
    iso.putalpha(combined_alpha)

    # Scale with margin
    inner_size = size - (outline * 4) - 24
    iso.thumbnail((inner_size, inner_size), Image.Resampling.LANCZOS)
    
    canvas = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    x = (size - iso.width) // 2
    y = (size - iso.height) // 2
    canvas.paste(iso, (x, y), iso)
    
    if outline > 0:
        canvas = apply_stroke(canvas, stroke_width=outline)
        
    return canvas

def main():
    dest_dirs = [
        os.path.join(WORKSPACE_DIR, "app", "src", "main", "assets", "images", "mascot"),
        os.path.join(WORKSPACE_DIR, "app", "src", "main", "assets", "images", "characters"),
    ]
    for d in dest_dirs:
        os.makedirs(d, exist_ok=True)

    for cfg in AVATARS:
        cid = cfg["id"]
        cname = cfg["name"]
        print(f"Processing Avatar {cid} ({cfg['label']})...")
        out_img = process_avatar(cfg)
        
        # Save preview in brain
        preview_path = os.path.join(BRAIN_DIR, f"ip_avatar_{cid}_{cname}.png")
        out_img.save(preview_path, format="PNG")
        
        # Save production files
        p1 = os.path.join(WORKSPACE_DIR, "app", "src", "main", "assets", "images", "mascot", f"avatar_0{cid}.png")
        p2 = os.path.join(WORKSPACE_DIR, "app", "src", "main", "assets", "images", "characters", f"avatar_0{cid}_{cname}.png")
        p3 = os.path.join(WORKSPACE_DIR, "app", "src", "main", "assets", "images", "mascot", f"companion_avatar_0{cid}_{cname}.png")
        
        out_img.save(p1, format="PNG")
        out_img.save(p2, format="PNG")
        out_img.save(p3, format="PNG")
        print(f" -> Saved {p1}")

    print("\nAll 6 IP-as-Logo avatars successfully deployed!")

if __name__ == "__main__":
    main()
