"""
Test isolating the master IP Tarsier character from its solid sky blue background.
"""

import os
from collections import deque
from PIL import Image, ImageFilter
import numpy as np

BRAIN_DIR = "/home/zendrix/.gemini/antigravity-cli/brain/af2db79b-86c4-4925-a8b9-dd27044dffec"
SRC = os.path.join(BRAIN_DIR, "tarsier_ip_logo_test_1789221745562.jpg")

img = Image.open(SRC)
img_rgb = img.convert("RGB")
w, h = img_rgb.size
print(f"Source size: {w}x{h}")

# Corner pixel for background color
bg_color = np.median([img_rgb.getpixel((2, 2)), img_rgb.getpixel((w-3, 2)), img_rgb.getpixel((2, h-3)), img_rgb.getpixel((w-3, h-3))], axis=0)
print(f"Background color: {bg_color}")

data = np.array(img_rgb, dtype=float)
diff = np.sqrt(np.sum((data - bg_color) ** 2, axis=2))
is_bg_like = diff < 40

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
print(f"Subject bbox: {bbox}")
cropped = res.crop(bbox)

out_path = os.path.join(BRAIN_DIR, "test_ip_tarsier_isolated.png")
cropped.save(out_path)
print(f"Saved isolated test image to {out_path} (size: {cropped.size})")
