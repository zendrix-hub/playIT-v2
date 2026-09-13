"""
Elevated Miki the Cat — Perfect Duolingo ABC Pediatric Storybook Design matching Lily the Tarsier.
"""

import os
import math
from PIL import Image, ImageDraw, ImageFilter
import numpy as np

SIZE = 512
SCALE = 2
W = SIZE * SCALE
H = SIZE * SCALE

OUTLINE = (45, 55, 62, 255)         # #2D373E Slate Outline
WHITE = (255, 255, 255, 255)
ROSY_CHEEK = (255, 145, 155, 215)   # Soft cheerful blush
TONGUE = (244, 95, 115, 255)        # Soft pink tongue

def draw_thick_line(draw, start, end, color, width):
    draw.line([start, end], fill=color, width=int(width), joint="curve")

def draw_thick_arc(draw, bbox, start_deg, end_deg, color, width):
    draw.arc(bbox, start=start_deg, end=end_deg, fill=color, width=int(width))

def bezier_points(p0, p1, p2, p3, steps=40):
    pts = []
    for i in range(steps + 1):
        t = i / float(steps)
        x = (1-t)**3 * p0[0] + 3*(1-t)**2*t * p1[0] + 3*(1-t)*t**2 * p2[0] + t**3 * p3[0]
        y = (1-t)**3 * p0[1] + 3*(1-t)**2*t * p1[1] + 3*(1-t)*t**2 * p2[1] + t**3 * p3[1]
        pts.append((x, y))
    return pts

def draw_bezier_tube(draw, p0, p1, p2, p3, color, width, outline_color=OUTLINE, outline_w=10):
    pts = bezier_points(p0, p1, p2, p3)
    # Outline pass
    for i in range(len(pts) - 1):
        draw.line([pts[i], pts[i+1]], fill=outline_color, width=int(width + outline_w * 2))
    for p in pts:
        r = (width + outline_w * 2) / 2
        draw.ellipse([p[0] - r, p[1] - r, p[0] + r, p[1] + r], fill=outline_color)
    # Fill pass
    for i in range(len(pts) - 1):
        draw.line([pts[i], pts[i+1]], fill=color, width=int(width))
    for p in pts:
        r = width / 2
        draw.ellipse([p[0] - r, p[1] - r, p[0] + r, p[1] + r], fill=color)

def draw_lily_style_eyes(draw, lx, rx, ly, iris_color=(245, 172, 38, 255), iris_rim=(205, 125, 20, 255), eye_r=56):
    """
    Lily's signature eyes:
    - Delicate arched eyebrows
    - Outer colored iris ring with subtle rim shading
    - Deep dark pupil
    - Large crisp primary specular highlight
    - Secondary crisp specular highlight
    """
    for cx in [lx, rx]:
        # Eyebrow
        brow_y = ly - eye_r - 18
        draw_thick_arc(draw, [cx - 36, brow_y - 10, cx + 36, brow_y + 14], 200, 340, OUTLINE, 8)

        # Eye socket/ring outline
        draw.ellipse([cx - eye_r - 5, ly - eye_r - 5, cx + eye_r + 5, ly + eye_r + 5], fill=OUTLINE)

        # Iris outer rim
        draw.ellipse([cx - eye_r, ly - eye_r, cx + eye_r, ly + eye_r], fill=iris_rim)

        # Iris inner vibrant body
        inner_r = eye_r * 0.88
        draw.ellipse([cx - inner_r, ly - inner_r, cx + inner_r, ly + inner_r], fill=iris_color)

        # Deep pupil
        pupil_r = eye_r * 0.65
        draw.ellipse([cx - pupil_r, ly - pupil_r, cx + pupil_r, ly + pupil_r], fill=OUTLINE)

        # Primary catchlight (top-left)
        s1 = pupil_r * 0.44
        s1_x = cx - pupil_r * 0.35
        s1_y = ly - pupil_r * 0.35
        draw.ellipse([s1_x - s1, s1_y - s1, s1_x + s1, s1_y + s1], fill=WHITE)

        # Secondary catchlight (bottom-right)
        s2 = pupil_r * 0.22
        s2_x = cx + pupil_r * 0.38
        s2_y = ly + pupil_r * 0.38
        draw.ellipse([s2_x - s2, s2_y - s2, s2_x + s2, s2_y + s2], fill=WHITE)

def draw_rosy_cheeks(draw, lx, rx, ly, rw=38, rh=24):
    draw.ellipse([lx - rw, ly - rh, lx + rw, ly + rh], fill=ROSY_CHEEK)
    draw.ellipse([rx - rw, ly - rh, rx + rw, ly + rh], fill=ROSY_CHEEK)

def draw_cute_foot(draw, cx, cy, w, h, base_color):
    """Draws foot with 3 rounded toe lobes matching Lily's stance."""
    draw.ellipse([cx - w//2 - 6, cy - h//2 - 6, cx + w//2 + 6, cy + h//2 + 6], fill=OUTLINE)
    draw.ellipse([cx - w//2, cy - h//2, cx + w//2, cy + h//2], fill=base_color)
    # 3 toe lobes at front
    for tox in [-w*0.25, 0, w*0.25]:
        draw.ellipse([cx + tox - 12, cy + h*0.25 - 10, cx + tox + 12, cy + h*0.25 + 12], fill=base_color)
    # Toe crease lines
    draw_thick_line(draw, (cx - w*0.14, cy - h*0.05), (cx - w*0.14, cy + h*0.48), OUTLINE, 6)
    draw_thick_line(draw, (cx + w*0.14, cy - h*0.05), (cx + w*0.14, cy + h*0.48), OUTLINE, 6)

def draw_mitten_hand(draw, cx, cy, arm_color, paw_color, angle=0):
    """Draws a cute 3D-mitten hand with rounded thumb and soft digits matching Lily."""
    r = 30
    draw.ellipse([cx - r - 5, cy - r - 5, cx + r + 5, cy + r + 5], fill=OUTLINE)
    draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=paw_color)
    # 3 rounded digits
    for dx in [-14, 0, 14]:
        draw.ellipse([cx + dx - 10, cy + r - 12, cx + dx + 10, cy + r + 6], fill=paw_color, outline=OUTLINE, width=4)

def apply_pediatric_outline(image: Image.Image, stroke_w: int = 8) -> Image.Image:
    alpha = image.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_w * 2 + 1))
    stroke_layer = Image.new("RGBA", image.size, (0, 0, 0, 0))
    stroke_base = Image.new("RGBA", image.size, OUTLINE)
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, image)

def render_test_miki():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx = W / 2.0

    CAT_GINGER = (255, 142, 42, 255)     # Warm ginger
    CAT_CREAM = (255, 244, 225, 255)     # Cream belly & muzzle
    PINK_INNER = (255, 178, 185, 255)
    IRIS_GOLD = (245, 170, 35, 255)
    IRIS_RIM = (205, 122, 18, 255)

    # 1. Curling Tail (behind body)
    draw_bezier_tube(draw,
                     (cx + 120, H * 0.74),
                     (cx + 260, H * 0.75),
                     (cx + 315, H * 0.46),
                     (cx + 215, H * 0.36),
                     CAT_GINGER, width=46, outline_w=8)
    # Cream tail tip
    t_pts = bezier_points((cx + 120, H * 0.74), (cx + 260, H * 0.75), (cx + 315, H * 0.46), (cx + 215, H * 0.36))
    for p in t_pts[-12:]:
        draw.ellipse([p[0] - 23, p[1] - 23, p[0] + 23, p[1] + 23], fill=CAT_CREAM)
    draw.ellipse([cx + 215 - 24, H * 0.36 - 24, cx + 215 + 24, H * 0.36 + 24], fill=CAT_CREAM)

    # 2. Feet (grounded, chubby)
    draw_cute_foot(draw, cx - 100, H * 0.84, 110, 62, CAT_GINGER)
    draw_cute_foot(draw, cx + 100, H * 0.84, 110, 62, CAT_GINGER)

    # 3. Chubby Torso (natural pear shape)
    body_cy = H * 0.66
    draw.ellipse([cx - 215, body_cy - 195, cx + 215, body_cy + 195], fill=OUTLINE)
    draw.ellipse([cx - 207, body_cy - 187, cx + 207, body_cy + 187], fill=CAT_GINGER)

    # Cream Tummy
    draw.ellipse([cx - 138, body_cy - 120, cx + 138, body_cy + 160], fill=CAT_CREAM)

    # 4. Inward Resting Left Arm (curved tube)
    draw_bezier_tube(draw, (cx - 145, H * 0.58), (cx - 170, H * 0.66), (cx - 110, H * 0.71), (cx - 55, H * 0.67), CAT_GINGER, width=44, outline_w=7)
    draw_mitten_hand(draw, cx - 55, H * 0.67, CAT_GINGER, CAT_CREAM)

    # 5. Welcoming Waving Right Arm (sweeping up beside head)
    draw_bezier_tube(draw, (cx + 140, H * 0.58), (cx + 200, H * 0.54), (cx + 235, H * 0.46), (cx + 230, H * 0.38), CAT_GINGER, width=44, outline_w=7)

    # 6. Ears (behind head)
    hcy = H * 0.36
    # Left Ear
    draw.polygon([(cx - 215, hcy - 20), (cx - 180, hcy - 245), (cx - 55, hcy - 130)], fill=OUTLINE)
    draw.polygon([(cx - 207, hcy - 22), (cx - 180, hcy - 235), (cx - 63, hcy - 128)], fill=CAT_GINGER)
    draw.polygon([(cx - 175, hcy - 35), (cx - 165, hcy - 200), (cx - 85, hcy - 125)], fill=PINK_INNER)

    # Right Ear
    draw.polygon([(cx + 215, hcy - 20), (cx + 180, hcy - 245), (cx + 55, hcy - 130)], fill=OUTLINE)
    draw.polygon([(cx + 207, hcy - 22), (cx + 180, hcy - 235), (cx + 63, hcy - 128)], fill=CAT_GINGER)
    draw.polygon([(cx + 175, hcy - 35), (cx + 165, hcy - 200), (cx + 85, hcy - 125)], fill=PINK_INNER)

    # 7. Head with Rounded Cheek Tufts
    head_rx, head_ry = 230, 198
    # Head outline
    draw.ellipse([cx - head_rx - 8, hcy - head_ry - 8, cx + head_rx + 8, hcy + head_ry + 8], fill=OUTLINE)
    # Left cheek tufts (rounded lobes)
    draw.ellipse([cx - 250, hcy + 10, cx - 180, hcy + 75], fill=OUTLINE)
    draw.ellipse([cx - 245, hcy + 45, cx - 185, hcy + 105], fill=OUTLINE)
    # Right cheek tufts (rounded lobes)
    draw.ellipse([cx + 180, hcy + 10, cx + 250, hcy + 75], fill=OUTLINE)
    draw.ellipse([cx + 185, hcy + 45, cx + 245, hcy + 105], fill=OUTLINE)
    # Top head tuft
    draw.ellipse([cx - 28, hcy - head_ry - 22, cx + 28, hcy - head_ry + 22], fill=OUTLINE)

    # Head fill
    draw.ellipse([cx - head_rx, hcy - head_ry, cx + head_rx, hcy + head_ry], fill=CAT_GINGER)
    draw.ellipse([cx - 244, hcy + 16, cx - 186, hcy + 69], fill=CAT_GINGER)
    draw.ellipse([cx - 239, hcy + 51, cx - 191, hcy + 99], fill=CAT_GINGER)
    draw.ellipse([cx + 186, hcy + 16, cx + 244, hcy + 69], fill=CAT_GINGER)
    draw.ellipse([cx + 191, hcy + 51, cx + 239, hcy + 99], fill=CAT_GINGER)
    draw.ellipse([cx - 22, hcy - head_ry - 16, cx + 22, hcy - head_ry + 16], fill=CAT_GINGER)

    # 8. Storybook Cream Muzzle (Soft heart-cloud shape)
    muzzle_cy = hcy + 50
    draw.ellipse([cx - 130, muzzle_cy - 72, cx + 130, muzzle_cy + 72], fill=CAT_CREAM)
    draw.ellipse([cx - 85, muzzle_cy - 90, cx + 85, muzzle_cy + 35], fill=CAT_CREAM)

    # 9. Lily-Style Eyes with Amber Iris Rings
    draw_lily_style_eyes(draw, cx - 92, cx + 92, hcy - 12, iris_color=IRIS_GOLD, iris_rim=IRIS_RIM, eye_r=55)

    # 10. Rosy Cheeks
    draw_rosy_cheeks(draw, cx - 150, cx + 150, hcy + 55, rw=38, rh=22)

    # 11. Cute Whisker Freckles
    for dx, dy in [(-22, -4), (0, 6), (22, -4)]:
        draw.ellipse([cx - 105 + dx - 4, muzzle_cy + dy - 4, cx - 105 + dx + 4, muzzle_cy + dy + 4], fill=OUTLINE)
        draw.ellipse([cx + 105 - dx - 4, muzzle_cy + dy - 4, cx + 105 - dx + 4, muzzle_cy + dy + 4], fill=OUTLINE)

    # 12. Soft Nose & W-Mouth (:3) with subtle pink tongue
    nose_y = muzzle_cy - 22
    draw.polygon([(cx - 16, nose_y), (cx + 16, nose_y), (cx, nose_y + 16)], fill=OUTLINE)
    draw.ellipse([cx - 16, nose_y - 4, cx - 8, nose_y + 4], fill=OUTLINE)
    draw.ellipse([cx + 8, nose_y - 4, cx + 16, nose_y + 4], fill=OUTLINE)

    # Feline smile curves
    draw_thick_arc(draw, [cx - 48, nose_y + 6, cx, nose_y + 42], 0, 180, OUTLINE, 9)
    draw_thick_arc(draw, [cx, nose_y + 6, cx + 48, nose_y + 42], 0, 180, OUTLINE, 9)
    # Tiny pink tongue peeking out in the center
    draw.chord([cx - 16, nose_y + 24, cx + 16, nose_y + 44], start=0, end=180, fill=TONGUE, outline=OUTLINE, width=4)

    # 13. Waving Right Paw (Drawn in front of cheek for clear silhouette!)
    paw_x, paw_y = cx + 230, H * 0.38
    draw.ellipse([paw_x - 36, paw_y - 36, paw_x + 36, paw_y + 36], fill=OUTLINE)
    draw.ellipse([paw_x - 30, paw_y - 30, paw_x + 30, paw_y + 30], fill=CAT_CREAM)
    # Pink center paw pad
    draw.ellipse([paw_x - 16, paw_y - 12, paw_x + 16, paw_y + 16], fill=PINK_INNER)
    # Pink toe beans
    draw.ellipse([paw_x - 24, paw_y - 28, paw_x - 12, paw_y - 16], fill=PINK_INNER)
    draw.ellipse([paw_x - 6, paw_y - 32, paw_x + 6, paw_y - 20], fill=PINK_INNER)
    draw.ellipse([paw_x + 12, paw_y - 28, paw_x + 24, paw_y - 16], fill=PINK_INNER)

    # 14. Final continuous outline smoothing
    res = apply_pediatric_outline(img, stroke_w=6)
    return res

if __name__ == "__main__":
    img = render_test_miki()
    final_img = img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)
    out_path = "/home/zendrix/.gemini/antigravity-cli/brain/af2db79b-86c4-4925-a8b9-dd27044dffec/test_elevated_cat.png"
    final_img.save(out_path, "PNG", optimize=True)
    print(f"Saved to {out_path}")
