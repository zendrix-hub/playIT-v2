"""
Deploy newly generated Duolingo ABC picture card assets for letters:
- Orange (Letter O)
- Ball (Letter B)
- Elephant (Letter E)
- Umbrella (Letter U)
- Tiger (Letter T)
"""

import os
from collections import deque
from PIL import Image, ImageFilter
import numpy as np

BRAIN_DIR = "/home/zendrix/.gemini/antigravity-cli/brain/af2db79b-86c4-4925-a8b9-dd27044dffec"
WORKSPACE_DIR = "/mnt/c/Users/Zendrix/Desktop/thesis/playIT-v2-workspace"
PICTURES_DIR = os.path.join(WORKSPACE_DIR, "app", "src", "main", "assets", "images", "pictures")
OUTLINE_COLOR = (45, 55, 62, 255)  # #2D373E

ITEMS = [
    {
        "word": "orange",
        "src": os.path.join(BRAIN_DIR, "picture_orange_new_1789220963724.jpg"),
        "bg_tol": 35
    },
    {
        "word": "ball",
        "src": os.path.join(BRAIN_DIR, "picture_ball_new_1789220985883.jpg"),
        "bg_tol": 35
    },
    {
        "word": "elephant",
        "src": os.path.join(BRAIN_DIR, "picture_elephant_new_1789221144265.jpg"),
        "bg_tol": 35
    },
    {
        "word": "umbrella",
        "src": os.path.join(BRAIN_DIR, "picture_umbrella_new_1789221166532.jpg"),
        "bg_tol": 35
    },
    {
        "word": "tiger",
        "src": os.path.join(BRAIN_DIR, "picture_tiger_new_1789221181610.jpg"),
        "bg_tol": 35
    }
]

def bfs_isolate_character(img: Image.Image, bg_tolerance: int = 35) -> Image.Image:
    img_rgb = img.convert("RGB")
    w, h = img_rgb.size
    
    corners = [(2, 2), (w - 3, 2), (2, h - 3), (w - 3, h - 3)]
    corner_colors = [img_rgb.getpixel(pt) for pt in corners]
    bg_color = np.median(corner_colors, axis=0)

    data = np.array(img_rgb, dtype=float)
    diff = np.sqrt(np.sum((data - bg_color) ** 2, axis=2))
    is_bg_like = diff < bg_tolerance

    visited = np.zeros((h, w), dtype=bool)
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

def apply_pediatric_outline(image: Image.Image, stroke_width: int = 6) -> Image.Image:
    alpha = image.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_width * 2 + 1))
    stroke_layer = Image.new("RGBA", image.size, (0, 0, 0, 0))
    stroke_base = Image.new("RGBA", image.size, OUTLINE_COLOR)
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, image)

def process_and_save(item, size=512, outline=6):
    src = item["src"]
    word = item["word"]
    print(f"Processing {word} from {src}...")
    img = Image.open(src)
    isolated = bfs_isolate_character(img, bg_tolerance=item["bg_tol"])

    inner_size = size - (outline * 4) - 24
    isolated.thumbnail((inner_size, inner_size), Image.Resampling.LANCZOS)

    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    x = (size - isolated.width) // 2
    y = (size - isolated.height) // 2
    canvas.paste(isolated, (x, y), isolated)

    if outline > 0:
        canvas = apply_pediatric_outline(canvas, stroke_width=outline)

    # Save to picture_<word>.png and word_<word>.png
    p1 = os.path.join(PICTURES_DIR, f"picture_{word}.png")
    p2 = os.path.join(PICTURES_DIR, f"word_{word}.png")
    canvas.save(p1, format="PNG")
    canvas.save(p2, format="PNG")
    print(f" -> Deployed: {p1} and {p2}")

    # Preview in brain
    preview_file = os.path.join(BRAIN_DIR, f"preview_picture_{word}.png")
    canvas.save(preview_file, format="PNG")

def main():
    os.makedirs(PICTURES_DIR, exist_ok=True)
    for item in ITEMS:
        process_and_save(item)
    print("\nAll 5 picture card assets processed and deployed successfully!")

if __name__ == "__main__":
    main()
