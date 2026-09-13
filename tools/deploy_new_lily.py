"""
Deploy newly generated Duolingo ABC Lily the Tarsier mascot poses.
Applies BFS exterior background isolation, centering on 512x512 canvas,
and consistent #2D373E pediatric sticker stroke.
"""

import os
from collections import deque
from PIL import Image, ImageFilter
import numpy as np

BRAIN_DIR = "/home/zendrix/.gemini/antigravity-cli/brain/af2db79b-86c4-4925-a8b9-dd27044dffec"
WORKSPACE_DIR = "/mnt/c/Users/Zendrix/Desktop/thesis/playIT-v2-workspace"
MASCOT_DIR = os.path.join(WORKSPACE_DIR, "app", "src", "main", "assets", "images", "mascot")
OUTLINE_COLOR = (45, 55, 62, 255)  # #2D373E Slate Black Outline

POSES = [
    {
        "name": "lily_idle",
        "src": os.path.join(BRAIN_DIR, "lily_idle_new_1789219240563.jpg"),
        "bg_tol": 35
    },
    {
        "name": "lily_waving",
        "src": os.path.join(BRAIN_DIR, "lily_waving_new_1789219259118.jpg"),
        "bg_tol": 35
    },
    {
        "name": "lily_listening",
        "src": os.path.join(BRAIN_DIR, "lily_listening_new_1789219278135.jpg"),
        "bg_tol": 35
    },
    {
        "name": "lily_pointing",
        "src": os.path.join(BRAIN_DIR, "lily_pointing_new_1789219294158.jpg"),
        "bg_tol": 35
    },
    {
        "name": "lily_encouraging",
        "src": os.path.join(BRAIN_DIR, "lily_encouraging_new_1789219317171.jpg"),
        "bg_tol": 35
    },
    {
        "name": "lily_thinking",
        "src": os.path.join(BRAIN_DIR, "lily_thinking_new_1789219385721.jpg"),
        "bg_tol": 35
    },
    {
        "name": "lily_celebrating",
        "src": os.path.join(BRAIN_DIR, "lily_celebrating_new_1789219406357.jpg"),
        "bg_tol": 35
    }
]

def bfs_isolate_character(img: Image.Image, bg_tolerance: int = 35) -> Image.Image:
    img_rgb = img.convert("RGB")
    w, h = img_rgb.size
    
    # Corner pixels for background color
    corners = [(2, 2), (w - 3, 2), (2, h - 3), (w - 3, h - 3)]
    corner_colors = [img_rgb.getpixel(pt) for pt in corners]
    bg_color = np.median(corner_colors, axis=0)

    data = np.array(img_rgb, dtype=float)
    diff = np.sqrt(np.sum((data - bg_color) ** 2, axis=2))
    is_bg_like = diff < bg_tolerance

    visited = np.zeros((h, w), dtype=bool)
    queue = deque()
    
    # Seed queue from border pixels
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

def apply_pediatric_outline(image: Image.Image, stroke_width: int = 6) -> Image.Image:
    alpha = image.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_width * 2 + 1))
    stroke_layer = Image.new("RGBA", image.size, (0, 0, 0, 0))
    stroke_base = Image.new("RGBA", image.size, OUTLINE_COLOR)
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, image)

def process_and_save(pose_info, size=512, outline=6):
    src = pose_info["src"]
    name = pose_info["name"]
    print(f"Processing {name} from {src}...")
    img = Image.open(src)
    isolated = bfs_isolate_character(img, bg_tolerance=pose_info["bg_tol"])

    # Fit within inner square
    inner_size = size - (outline * 4) - 20
    isolated.thumbnail((inner_size, inner_size), Image.Resampling.LANCZOS)

    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    x = (size - isolated.width) // 2
    y = (size - isolated.height) // 2
    canvas.paste(isolated, (x, y), isolated)

    if outline > 0:
        canvas = apply_pediatric_outline(canvas, stroke_width=outline)

    # Save to production mascot directory
    dest_file = os.path.join(MASCOT_DIR, f"{name}.png")
    canvas.save(dest_file, format="PNG")
    print(f" -> Deployed: {dest_file}")

    # Also save brain preview
    preview_file = os.path.join(BRAIN_DIR, f"preview_{name}.png")
    canvas.save(preview_file, format="PNG")

def main():
    os.makedirs(MASCOT_DIR, exist_ok=True)
    for p in POSES:
        process_and_save(p)

    # Also update splash_tarsier_headspace
    splash_src = os.path.join(MASCOT_DIR, "lily_idle.png")
    splash_dst = os.path.join(MASCOT_DIR, "splash_tarsier_headspace.png")
    if os.path.exists(splash_src):
        img = Image.open(splash_src)
        img.save(splash_dst, format="PNG")
        print(f" -> Updated {splash_dst}")

    print("\nAll 7 Lily poses processed and deployed successfully!")

if __name__ == "__main__":
    main()
