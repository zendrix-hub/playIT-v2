"""
PlayIT Unified Companion Animal Avatars Suite Generator — Master Edition v3
Refined to adopt Lily the Philippine Tarsier Mascot's exact aesthetic:
- Lily's signature 2-tone iris rings with deep dark pupils and double circular specular catchlights
- Delicate dark arched eyebrows
- Organic chibi head silhouettes with soft cheek fur tufts and head tufts
- Natural curved limbs with rounded mitten paws, digit pads, and pink paw beans
- Grounded chubby feet with 3 rounded toe lobes
- Consistent 2x Lanczos supersampling (1024x1024 -> 512x512)
- Continuous #2D373E pediatric sticker outline
- Deployed identically across:
  1. app/src/main/assets/images/characters/avatar_0X_<name>.png
  2. app/src/main/assets/images/mascot/avatar_0X.png
  3. app/src/main/assets/images/mascot/companion_avatar_0X_<name>.png
"""

import os
import math
from PIL import Image, ImageDraw, ImageFilter
import numpy as np

SIZE = 512
SCALE = 2
W = SIZE * SCALE
H = SIZE * SCALE

OUTLINE = (45, 55, 62, 255)         # #2D373E Slate Dark Outline
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
    """Lily's signature storybook eyes with colored iris ring and double catchlights."""
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
    for tox in [-w*0.25, 0, w*0.25]:
        draw.ellipse([cx + tox - 12, cy + h*0.25 - 10, cx + tox + 12, cy + h*0.25 + 12], fill=base_color)
    draw_thick_line(draw, (cx - w*0.14, cy - h*0.05), (cx - w*0.14, cy + h*0.48), OUTLINE, 6)
    draw_thick_line(draw, (cx + w*0.14, cy - h*0.05), (cx + w*0.14, cy + h*0.48), OUTLINE, 6)

def draw_mitten_hand(draw, cx, cy, arm_color, paw_color):
    """Draws a cute mitten hand with rounded thumb and soft digits matching Lily."""
    r = 30
    draw.ellipse([cx - r - 5, cy - r - 5, cx + r + 5, cy + r + 5], fill=OUTLINE)
    draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=paw_color)
    for dx in [-14, 0, 14]:
        draw.ellipse([cx + dx - 10, cy + r - 12, cx + dx + 10, cy + r + 6], fill=paw_color, outline=OUTLINE, width=4)

def apply_pediatric_outline(image: Image.Image, stroke_w: int = 8) -> Image.Image:
    alpha = image.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_w * 2 + 1))
    stroke_layer = Image.new("RGBA", image.size, (0, 0, 0, 0))
    stroke_base = Image.new("RGBA", image.size, OUTLINE)
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, image)

# ==============================================================================
# 1. MIKI THE GINGER CAT (Avatar 01)
# ==============================================================================
def render_miki_cat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx = W / 2.0

    CAT_GINGER = (255, 142, 42, 255)
    CAT_CREAM = (255, 244, 225, 255)
    PINK_INNER = (255, 178, 185, 255)
    IRIS_GOLD = (245, 170, 35, 255)
    IRIS_RIM = (205, 122, 18, 255)

    # 1. Curling Tail with Cream Tip
    draw_bezier_tube(draw,
                     (cx + 120, H * 0.74), (cx + 260, H * 0.75),
                     (cx + 315, H * 0.46), (cx + 215, H * 0.36),
                     CAT_GINGER, width=46, outline_w=8)
    t_pts = bezier_points((cx + 120, H * 0.74), (cx + 260, H * 0.75), (cx + 315, H * 0.46), (cx + 215, H * 0.36))
    for p in t_pts[-12:]:
        draw.ellipse([p[0] - 23, p[1] - 23, p[0] + 23, p[1] + 23], fill=CAT_CREAM)
    draw.ellipse([cx + 215 - 24, H * 0.36 - 24, cx + 215 + 24, H * 0.36 + 24], fill=CAT_CREAM)

    # 2. Feet
    draw_cute_foot(draw, cx - 100, H * 0.84, 110, 62, CAT_GINGER)
    draw_cute_foot(draw, cx + 100, H * 0.84, 110, 62, CAT_GINGER)

    # 3. Chubby Pear Torso
    body_cy = H * 0.66
    draw.ellipse([cx - 215, body_cy - 195, cx + 215, body_cy + 195], fill=OUTLINE)
    draw.ellipse([cx - 207, body_cy - 187, cx + 207, body_cy + 187], fill=CAT_GINGER)
    draw.ellipse([cx - 138, body_cy - 120, cx + 138, body_cy + 160], fill=CAT_CREAM)

    # 4. Left Arm (resting)
    draw_bezier_tube(draw, (cx - 145, H * 0.58), (cx - 170, H * 0.66), (cx - 110, H * 0.71), (cx - 55, H * 0.67), CAT_GINGER, width=44, outline_w=7)
    draw_mitten_hand(draw, cx - 55, H * 0.67, CAT_GINGER, CAT_CREAM)

    # 5. Right Arm (waving)
    draw_bezier_tube(draw, (cx + 140, H * 0.58), (cx + 200, H * 0.54), (cx + 235, H * 0.46), (cx + 230, H * 0.38), CAT_GINGER, width=44, outline_w=7)

    # 6. Ears
    hcy = H * 0.36
    draw.polygon([(cx - 215, hcy - 20), (cx - 180, hcy - 245), (cx - 55, hcy - 130)], fill=OUTLINE)
    draw.polygon([(cx - 207, hcy - 22), (cx - 180, hcy - 235), (cx - 63, hcy - 128)], fill=CAT_GINGER)
    draw.polygon([(cx - 175, hcy - 35), (cx - 165, hcy - 200), (cx - 85, hcy - 125)], fill=PINK_INNER)

    draw.polygon([(cx + 215, hcy - 20), (cx + 180, hcy - 245), (cx + 55, hcy - 130)], fill=OUTLINE)
    draw.polygon([(cx + 207, hcy - 22), (cx + 180, hcy - 235), (cx + 63, hcy - 128)], fill=CAT_GINGER)
    draw.polygon([(cx + 175, hcy - 35), (cx + 165, hcy - 200), (cx + 85, hcy - 125)], fill=PINK_INNER)

    # 7. Head with Cheek Tufts & Top Tuft
    head_rx, head_ry = 230, 198
    draw.ellipse([cx - head_rx - 8, hcy - head_ry - 8, cx + head_rx + 8, hcy + head_ry + 8], fill=OUTLINE)
    draw.ellipse([cx - 250, hcy + 10, cx - 180, hcy + 75], fill=OUTLINE)
    draw.ellipse([cx - 245, hcy + 45, cx - 185, hcy + 105], fill=OUTLINE)
    draw.ellipse([cx + 180, hcy + 10, cx + 250, hcy + 75], fill=OUTLINE)
    draw.ellipse([cx + 185, hcy + 45, cx + 245, hcy + 105], fill=OUTLINE)
    draw.ellipse([cx - 28, hcy - head_ry - 22, cx + 28, hcy - head_ry + 22], fill=OUTLINE)

    draw.ellipse([cx - head_rx, hcy - head_ry, cx + head_rx, hcy + head_ry], fill=CAT_GINGER)
    draw.ellipse([cx - 244, hcy + 16, cx - 186, hcy + 69], fill=CAT_GINGER)
    draw.ellipse([cx - 239, hcy + 51, cx - 191, hcy + 99], fill=CAT_GINGER)
    draw.ellipse([cx + 186, hcy + 16, cx + 244, hcy + 69], fill=CAT_GINGER)
    draw.ellipse([cx + 191, hcy + 51, cx + 239, hcy + 99], fill=CAT_GINGER)
    draw.ellipse([cx - 22, hcy - head_ry - 16, cx + 22, hcy - head_ry + 16], fill=CAT_GINGER)

    # 8. Muzzle Area
    muzzle_cy = hcy + 50
    draw.ellipse([cx - 130, muzzle_cy - 72, cx + 130, muzzle_cy + 72], fill=CAT_CREAM)
    draw.ellipse([cx - 85, muzzle_cy - 90, cx + 85, muzzle_cy + 35], fill=CAT_CREAM)

    # 9. Eyes
    draw_lily_style_eyes(draw, cx - 92, cx + 92, hcy - 12, iris_color=IRIS_GOLD, iris_rim=IRIS_RIM, eye_r=55)

    # 10. Cheeks & Freckles
    draw_rosy_cheeks(draw, cx - 150, cx + 150, hcy + 55, rw=38, rh=22)
    for dx, dy in [(-22, -4), (0, 6), (22, -4)]:
        draw.ellipse([cx - 105 + dx - 4, muzzle_cy + dy - 4, cx - 105 + dx + 4, muzzle_cy + dy + 4], fill=OUTLINE)
        draw.ellipse([cx + 105 - dx - 4, muzzle_cy + dy - 4, cx + 105 - dx + 4, muzzle_cy + dy + 4], fill=OUTLINE)

    # 11. Nose & Smile
    nose_y = muzzle_cy - 22
    draw.polygon([(cx - 16, nose_y), (cx + 16, nose_y), (cx, nose_y + 16)], fill=OUTLINE)
    draw.ellipse([cx - 16, nose_y - 4, cx - 8, nose_y + 4], fill=OUTLINE)
    draw.ellipse([cx + 8, nose_y - 4, cx + 16, nose_y + 4], fill=OUTLINE)

    draw_thick_arc(draw, [cx - 48, nose_y + 6, cx, nose_y + 42], 0, 180, OUTLINE, 9)
    draw_thick_arc(draw, [cx, nose_y + 6, cx + 48, nose_y + 42], 0, 180, OUTLINE, 9)
    draw.chord([cx - 16, nose_y + 24, cx + 16, nose_y + 44], start=0, end=180, fill=TONGUE, outline=OUTLINE, width=4)

    # 12. Waving Paw in foreground
    paw_x, paw_y = cx + 230, H * 0.38
    draw.ellipse([paw_x - 36, paw_y - 36, paw_x + 36, paw_y + 36], fill=OUTLINE)
    draw.ellipse([paw_x - 30, paw_y - 30, paw_x + 30, paw_y + 30], fill=CAT_CREAM)
    draw.ellipse([paw_x - 16, paw_y - 12, paw_x + 16, paw_y + 16], fill=PINK_INNER)
    draw.ellipse([paw_x - 24, paw_y - 28, paw_x - 12, paw_y - 16], fill=PINK_INNER)
    draw.ellipse([paw_x - 6, paw_y - 32, paw_x + 6, paw_y - 20], fill=PINK_INNER)
    draw.ellipse([paw_x + 12, paw_y - 28, paw_x + 24, paw_y - 16], fill=PINK_INNER)

    return apply_pediatric_outline(img, stroke_w=6)

# ==============================================================================
# 2. MILO THE MONKEY (Avatar 02)
# ==============================================================================
def render_milo_monkey():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx = W / 2.0

    MONKEY_BROWN = (152, 98, 62, 255)
    PEACH_FACE = (255, 226, 185, 255)
    PEACH_INNER = (255, 208, 172, 255)
    IRIS_GOLD = (245, 170, 35, 255)
    IRIS_RIM = (205, 122, 18, 255)

    # 1. Upward Swirl Monkey Tail
    draw_bezier_tube(draw,
                     (cx - 120, H * 0.74), (cx - 260, H * 0.74),
                     (cx - 320, H * 0.44), (cx - 210, H * 0.34),
                     MONKEY_BROWN, width=44, outline_w=8)
    draw_bezier_tube(draw,
                     (cx - 210, H * 0.34), (cx - 160, H * 0.30),
                     (cx - 140, H * 0.38), (cx - 180, H * 0.42),
                     MONKEY_BROWN, width=40, outline_w=8)

    # 2. Feet
    draw_cute_foot(draw, cx - 100, H * 0.84, 110, 62, MONKEY_BROWN)
    draw_cute_foot(draw, cx + 100, H * 0.84, 110, 62, MONKEY_BROWN)

    # 3. Chubby Torso
    body_cy = H * 0.66
    draw.ellipse([cx - 215, body_cy - 195, cx + 215, body_cy + 195], fill=OUTLINE)
    draw.ellipse([cx - 207, body_cy - 187, cx + 207, body_cy + 187], fill=MONKEY_BROWN)
    draw.ellipse([cx - 138, body_cy - 120, cx + 138, body_cy + 160], fill=PEACH_FACE)

    # 4. Arms (cheerfully resting on tummy)
    draw_bezier_tube(draw, (cx - 145, H * 0.58), (cx - 170, H * 0.66), (cx - 110, H * 0.71), (cx - 55, H * 0.67), MONKEY_BROWN, width=44, outline_w=7)
    draw_mitten_hand(draw, cx - 55, H * 0.67, MONKEY_BROWN, PEACH_FACE)

    draw_bezier_tube(draw, (cx + 145, H * 0.58), (cx + 170, H * 0.66), (cx + 110, H * 0.71), (cx + 55, H * 0.67), MONKEY_BROWN, width=44, outline_w=7)
    draw_mitten_hand(draw, cx + 55, H * 0.67, MONKEY_BROWN, PEACH_FACE)

    # 5. Large Round Ears on sides
    hcy = H * 0.36
    ear_r = 92
    draw.ellipse([cx - 210 - ear_r, hcy - ear_r, cx - 210 + ear_r, hcy + ear_r], fill=OUTLINE)
    draw.ellipse([cx - 210 - ear_r + 8, hcy - ear_r + 8, cx - 210 + ear_r - 8, hcy + ear_r - 8], fill=MONKEY_BROWN)
    draw.ellipse([cx - 210 - 52, hcy - 52, cx - 210 + 52, hcy + 52], fill=PEACH_INNER)

    draw.ellipse([cx + 210 - ear_r, hcy - ear_r, cx + 210 + ear_r, hcy + ear_r], fill=OUTLINE)
    draw.ellipse([cx + 210 - ear_r + 8, hcy - ear_r + 8, cx + 210 + ear_r - 8, hcy + ear_r - 8], fill=MONKEY_BROWN)
    draw.ellipse([cx + 210 - 52, hcy - 52, cx + 210 + 52, hcy + 52], fill=PEACH_INNER)

    # 6. Head with Cheek & Top Tufts
    head_rx, head_ry = 230, 198
    draw.ellipse([cx - head_rx - 8, hcy - head_ry - 8, cx + head_rx + 8, hcy + head_ry + 8], fill=OUTLINE)
    # Side cheek tufts
    draw.ellipse([cx - 245, hcy + 20, cx - 185, hcy + 85], fill=OUTLINE)
    draw.ellipse([cx + 185, hcy + 20, cx + 245, hcy + 85], fill=OUTLINE)
    # Head top hair tuft
    draw.ellipse([cx - 25, hcy - head_ry - 25, cx + 25, hcy - head_ry + 20], fill=OUTLINE)

    draw.ellipse([cx - head_rx, hcy - head_ry, cx + head_rx, hcy + head_ry], fill=MONKEY_BROWN)
    draw.ellipse([cx - 239, hcy + 26, cx - 191, hcy + 79], fill=MONKEY_BROWN)
    draw.ellipse([cx + 191, hcy + 26, cx + 239, hcy + 79], fill=MONKEY_BROWN)
    draw.ellipse([cx - 20, hcy - head_ry - 20, cx + 20, hcy - head_ry + 15], fill=MONKEY_BROWN)

    # 7. Heart-Shaped Peach Face Mask
    mask_r = 92
    mask_y = hcy - 20
    draw.ellipse([cx - 72 - mask_r, mask_y - mask_r, cx - 72 + mask_r, mask_y + mask_r], fill=PEACH_FACE)
    draw.ellipse([cx + 72 - mask_r, mask_y - mask_r, cx + 72 + mask_r, mask_y + mask_r], fill=PEACH_FACE)
    draw.ellipse([cx - 150, hcy - 10, cx + 150, hcy + 130], fill=PEACH_FACE)

    # 8. Eyes
    draw_lily_style_eyes(draw, cx - 78, cx + 78, hcy - 20, iris_color=IRIS_GOLD, iris_rim=IRIS_RIM, eye_r=52)

    # 9. Rosy Cheeks
    draw_rosy_cheeks(draw, cx - 145, cx + 145, hcy + 45, rw=36, rh=22)

    # 10. Cute Nostrils
    draw.ellipse([cx - 15, hcy + 30, cx - 3, hcy + 44], fill=OUTLINE)
    draw.ellipse([cx + 3, hcy + 30, cx + 15, hcy + 44], fill=OUTLINE)

    # 11. Big Open Crescent Smile with Pink Tongue
    smile_y = hcy + 70
    draw.chord([cx - 55, smile_y - 20, cx + 55, smile_y + 55], start=0, end=180, fill=OUTLINE)
    draw.chord([cx - 40, smile_y + 5, cx + 40, smile_y + 53], start=0, end=180, fill=TONGUE)

    return apply_pediatric_outline(img, stroke_w=6)

# ==============================================================================
# 3. BELLA THE BUNNY (Avatar 03)
# ==============================================================================
def render_bella_bunny():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx = W / 2.0

    BUNNY_WHITE = (255, 252, 246, 255)
    PASTEL_PINK = (255, 200, 212, 255)
    NOSE_PINK = (244, 114, 182, 255)
    IRIS_DARK = (125, 82, 42, 255)
    IRIS_RIM = (85, 52, 26, 255)

    # 1. Fluffy Poff Tail (left side)
    draw.ellipse([cx - 245, H * 0.68, cx - 145, H * 0.68 + 100], fill=OUTLINE)
    draw.ellipse([cx - 237, H * 0.68 + 8, cx - 153, H * 0.68 + 92], fill=BUNNY_WHITE)

    # 2. Feet
    draw_cute_foot(draw, cx - 100, H * 0.84, 110, 62, BUNNY_WHITE)
    draw_cute_foot(draw, cx + 100, H * 0.84, 110, 62, BUNNY_WHITE)

    # 3. Chubby Torso
    body_cy = H * 0.66
    draw.ellipse([cx - 215, body_cy - 195, cx + 215, body_cy + 195], fill=OUTLINE)
    draw.ellipse([cx - 207, body_cy - 187, cx + 207, body_cy + 187], fill=BUNNY_WHITE)
    draw.ellipse([cx - 138, body_cy - 120, cx + 138, body_cy + 160], fill=PASTEL_PINK)

    # 4. Arms (cute bunny paws held together in front)
    draw_bezier_tube(draw, (cx - 145, H * 0.58), (cx - 165, H * 0.66), (cx - 100, H * 0.70), (cx - 45, H * 0.66), BUNNY_WHITE, width=44, outline_w=7)
    draw_mitten_hand(draw, cx - 45, H * 0.66, BUNNY_WHITE, BUNNY_WHITE)

    draw_bezier_tube(draw, (cx + 145, H * 0.58), (cx + 165, H * 0.66), (cx + 100, H * 0.70), (cx + 45, H * 0.66), BUNNY_WHITE, width=44, outline_w=7)
    draw_mitten_hand(draw, cx + 45, H * 0.66, BUNNY_WHITE, BUNNY_WHITE)

    # 5. Tall Bunny Ears (behind head)
    hcy = H * 0.38
    # Left Ear
    draw.ellipse([cx - 155, hcy - 370, cx - 35, hcy - 70], fill=OUTLINE)
    draw.ellipse([cx - 147, hcy - 362, cx - 43, hcy - 78], fill=BUNNY_WHITE)
    draw.ellipse([cx - 125, hcy - 330, cx - 65, hcy - 110], fill=PASTEL_PINK)

    # Right Ear
    draw.ellipse([cx + 35, hcy - 370, cx + 155, hcy - 70], fill=OUTLINE)
    draw.ellipse([cx + 43, hcy - 362, cx + 147, hcy - 78], fill=BUNNY_WHITE)
    draw.ellipse([cx + 65, hcy - 330, cx + 125, hcy - 110], fill=PASTEL_PINK)

    # 6. Head with Cheek Fluff & Top Tuft
    head_rx, head_ry = 230, 198
    draw.ellipse([cx - head_rx - 8, hcy - head_ry - 8, cx + head_rx + 8, hcy + head_ry + 8], fill=OUTLINE)
    draw.ellipse([cx - 250, hcy + 15, cx - 180, hcy + 85], fill=OUTLINE)
    draw.ellipse([cx + 180, hcy + 15, cx + 250, hcy + 85], fill=OUTLINE)
    draw.ellipse([cx - 25, hcy - head_ry - 20, cx + 25, hcy - head_ry + 20], fill=OUTLINE)

    draw.ellipse([cx - head_rx, hcy - head_ry, cx + head_rx, hcy + head_ry], fill=BUNNY_WHITE)
    draw.ellipse([cx - 244, hcy + 21, cx - 186, hcy + 79], fill=BUNNY_WHITE)
    draw.ellipse([cx + 186, hcy + 21, cx + 244, hcy + 79], fill=BUNNY_WHITE)
    draw.ellipse([cx - 20, hcy - head_ry - 15, cx + 20, hcy - head_ry + 15], fill=BUNNY_WHITE)

    # 7. Eyes
    draw_lily_style_eyes(draw, cx - 88, cx + 88, hcy - 14, iris_color=IRIS_DARK, iris_rim=IRIS_RIM, eye_r=54)

    # 8. Rosy Cheeks
    draw_rosy_cheeks(draw, cx - 145, cx + 145, hcy + 52, rw=38, rh=22)

    # 9. Nose & Sweet Bunny Smile with Buck Tooth
    nose_y = hcy + 32
    draw.polygon([(cx - 16, nose_y), (cx + 16, nose_y), (cx, nose_y + 16)], fill=NOSE_PINK)
    # Bunny twitch lines
    draw_thick_arc(draw, [cx - 42, nose_y + 4, cx, nose_y + 36], 0, 180, OUTLINE, 8)
    draw_thick_arc(draw, [cx, nose_y + 4, cx + 42, nose_y + 36], 0, 180, OUTLINE, 8)
    # Cute little white buck-tooth
    draw.rectangle([cx - 12, nose_y + 20, cx + 12, nose_y + 36], fill=WHITE, outline=OUTLINE, width=4)
    draw_thick_line(draw, (cx, nose_y + 20), (cx, nose_y + 36), OUTLINE, 3)

    return apply_pediatric_outline(img, stroke_w=6)

# ==============================================================================
# 4. BARNABY THE BEAR (Avatar 04)
# ==============================================================================
def render_barnaby_bear():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx = W / 2.0

    BEAR_BROWN = (185, 125, 75, 255)
    BEAR_CREAM = (255, 240, 212, 255)
    EAR_CREAM = (245, 218, 180, 255)
    IRIS_AMBER = (235, 160, 35, 255)
    IRIS_RIM = (195, 118, 18, 255)

    # 1. Feet
    draw_cute_foot(draw, cx - 105, H * 0.84, 115, 64, BEAR_BROWN)
    draw_cute_foot(draw, cx + 105, H * 0.84, 115, 64, BEAR_BROWN)

    # 2. Chubby Torso
    body_cy = H * 0.66
    draw.ellipse([cx - 220, body_cy - 198, cx + 220, body_cy + 198], fill=OUTLINE)
    draw.ellipse([cx - 212, body_cy - 190, cx + 212, body_cy + 190], fill=BEAR_BROWN)
    draw.ellipse([cx - 142, body_cy - 120, cx + 142, body_cy + 160], fill=BEAR_CREAM)

    # 3. Cuddly Inward Teddy Arms
    draw_bezier_tube(draw, (cx - 150, H * 0.58), (cx - 175, H * 0.66), (cx - 115, H * 0.71), (cx - 55, H * 0.67), BEAR_BROWN, width=46, outline_w=7)
    draw_mitten_hand(draw, cx - 55, H * 0.67, BEAR_BROWN, BEAR_CREAM)

    draw_bezier_tube(draw, (cx + 150, H * 0.58), (cx + 175, H * 0.66), (cx + 115, H * 0.71), (cx + 55, H * 0.67), BEAR_BROWN, width=46, outline_w=7)
    draw_mitten_hand(draw, cx + 55, H * 0.67, BEAR_BROWN, BEAR_CREAM)

    # 4. Round Teddy Ears (behind head)
    hcy = H * 0.36
    ear_r = 82
    draw.ellipse([cx - 180 - ear_r, hcy - 120 - ear_r, cx - 180 + ear_r, hcy - 120 + ear_r], fill=OUTLINE)
    draw.ellipse([cx - 180 - ear_r + 8, hcy - 120 - ear_r + 8, cx - 180 + ear_r - 8, hcy - 120 + ear_r - 8], fill=BEAR_BROWN)
    draw.ellipse([cx - 180 - 45, hcy - 120 - 45, cx - 180 + 45, hcy - 120 + 45], fill=EAR_CREAM)

    draw.ellipse([cx + 180 - ear_r, hcy - 120 - ear_r, cx + 180 + ear_r, hcy - 120 + ear_r], fill=OUTLINE)
    draw.ellipse([cx + 180 - ear_r + 8, hcy - 120 - ear_r + 8, cx + 180 + ear_r - 8, hcy - 120 + ear_r - 8], fill=BEAR_BROWN)
    draw.ellipse([cx + 180 - 45, hcy - 120 - 45, cx + 180 + 45, hcy - 120 + 45], fill=EAR_CREAM)

    # 5. Head with Cheek Fluff & Top Tuft
    head_rx, head_ry = 232, 200
    draw.ellipse([cx - head_rx - 8, hcy - head_ry - 8, cx + head_rx + 8, hcy + head_ry + 8], fill=OUTLINE)
    draw.ellipse([cx - 250, hcy + 20, cx - 180, hcy + 85], fill=OUTLINE)
    draw.ellipse([cx + 180, hcy + 20, cx + 250, hcy + 85], fill=OUTLINE)
    draw.ellipse([cx - 25, hcy - head_ry - 20, cx + 25, hcy - head_ry + 20], fill=OUTLINE)

    draw.ellipse([cx - head_rx, hcy - head_ry, cx + head_rx, hcy + head_ry], fill=BEAR_BROWN)
    draw.ellipse([cx - 244, hcy + 26, cx - 186, hcy + 79], fill=BEAR_BROWN)
    draw.ellipse([cx + 186, hcy + 26, cx + 244, hcy + 79], fill=BEAR_BROWN)
    draw.ellipse([cx - 20, hcy - head_ry - 15, cx + 20, hcy - head_ry + 15], fill=BEAR_BROWN)

    # 6. Large Cream Muzzle
    muzzle_cy = hcy + 52
    draw.ellipse([cx - 130, muzzle_cy - 75, cx + 130, muzzle_cy + 75], fill=BEAR_CREAM, outline=OUTLINE, width=6)

    # 7. Eyes
    draw_lily_style_eyes(draw, cx - 92, cx + 92, hcy - 14, iris_color=IRIS_AMBER, iris_rim=IRIS_RIM, eye_r=55)

    # 8. Rosy Cheeks
    draw_rosy_cheeks(draw, cx - 152, cx + 152, hcy + 50, rw=38, rh=22)

    # 9. Big Dark Button Nose & Cozy Smile
    nose_y = muzzle_cy - 20
    draw.ellipse([cx - 32, nose_y - 20, cx + 32, nose_y + 20], fill=OUTLINE)
    # Nose highlight reflection
    draw.ellipse([cx - 16, nose_y - 12, cx + 4, nose_y - 2], fill=WHITE)
    draw_thick_line(draw, (cx, nose_y + 20), (cx, nose_y + 44), OUTLINE, 9)
    draw_thick_arc(draw, [cx - 56, nose_y + 12, cx + 56, nose_y + 58], 20, 160, OUTLINE, 9)

    return apply_pediatric_outline(img, stroke_w=6)

# ==============================================================================
# 5. FINLEY THE FROG (Avatar 05)
# ==============================================================================
def render_finley_frog():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx = W / 2.0

    FROG_GREEN = (72, 184, 98, 255)
    MINT_BELLY = (220, 252, 222, 255)
    IRIS_GOLD_LIME = (230, 195, 35, 255)
    IRIS_RIM = (165, 140, 20, 255)

    # 1. Webbed Feet
    draw_cute_foot(draw, cx - 105, H * 0.84, 115, 62, FROG_GREEN)
    draw_cute_foot(draw, cx + 105, H * 0.84, 115, 62, FROG_GREEN)

    # 2. Chubby Torso
    body_cy = H * 0.66
    draw.ellipse([cx - 215, body_cy - 195, cx + 215, body_cy + 195], fill=OUTLINE)
    draw.ellipse([cx - 207, body_cy - 187, cx + 207, body_cy + 187], fill=FROG_GREEN)
    draw.ellipse([cx - 138, body_cy - 120, cx + 138, body_cy + 160], fill=MINT_BELLY)

    # 3. Arms (curved webbed paws on tummy)
    draw_bezier_tube(draw, (cx - 145, H * 0.58), (cx - 170, H * 0.66), (cx - 110, H * 0.71), (cx - 55, H * 0.67), FROG_GREEN, width=44, outline_w=7)
    draw_mitten_hand(draw, cx - 55, H * 0.67, FROG_GREEN, MINT_BELLY)

    draw_bezier_tube(draw, (cx + 145, H * 0.58), (cx + 170, H * 0.66), (cx + 110, H * 0.71), (cx + 55, H * 0.67), FROG_GREEN, width=44, outline_w=7)
    draw_mitten_hand(draw, cx + 55, H * 0.67, FROG_GREEN, MINT_BELLY)

    # 4. Fused Head & Eye Domes
    hcy = H * 0.38
    dome_r = 100
    head_rx, head_ry = 238, 188

    # Outline pass
    draw.ellipse([cx - 120 - dome_r - 8, hcy - 100 - dome_r - 8, cx - 120 + dome_r + 8, hcy - 100 + dome_r + 8], fill=OUTLINE)
    draw.ellipse([cx + 120 - dome_r - 8, hcy - 100 - dome_r - 8, cx + 120 + dome_r + 8, hcy - 100 + dome_r + 8], fill=OUTLINE)
    draw.ellipse([cx - head_rx - 8, hcy - head_ry - 8, cx + head_rx + 8, hcy + head_ry + 8], fill=OUTLINE)

    # Fill pass (fuses domes and head seamlessly)
    draw.ellipse([cx - 120 - dome_r, hcy - 100 - dome_r, cx - 120 + dome_r, hcy - 100 + dome_r], fill=FROG_GREEN)
    draw.ellipse([cx + 120 - dome_r, hcy - 100 - dome_r, cx + 120 + dome_r, hcy - 100 + dome_r], fill=FROG_GREEN)
    draw.ellipse([cx - head_rx, hcy - head_ry, cx + head_rx, hcy + head_ry], fill=FROG_GREEN)

    # 5. Big Eyes inside the Domes
    draw_lily_style_eyes(draw, cx - 120, cx + 120, hcy - 100, iris_color=IRIS_GOLD_LIME, iris_rim=IRIS_RIM, eye_r=58)

    # 6. Rosy Cheeks
    draw_rosy_cheeks(draw, cx - 150, cx + 150, hcy + 40, rw=38, rh=22)

    # 7. Nostrils
    draw.ellipse([cx - 16, hcy - 8, cx - 4, hcy + 4], fill=OUTLINE)
    draw.ellipse([cx + 4, hcy - 8, cx + 16, hcy + 4], fill=OUTLINE)

    # 8. Wide Joyful Frog Smile with Pink Tongue
    smile_y = hcy + 45
    draw.chord([cx - 72, smile_y - 20, cx + 72, smile_y + 60], start=0, end=180, fill=OUTLINE)
    draw.chord([cx - 52, smile_y + 8, cx + 52, smile_y + 58], start=0, end=180, fill=TONGUE)

    return apply_pediatric_outline(img, stroke_w=6)

# ==============================================================================
# 6. OLLIE THE OWL (Avatar 06)
# ==============================================================================
def render_ollie_owl():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx = W / 2.0

    OWL_PURPLE = (120, 85, 182, 255)
    OWL_CREAM = (252, 244, 255, 255)
    BEAK_GOLD = (255, 168, 15, 255)
    IRIS_GOLD = (255, 185, 25, 255)
    IRIS_RIM = (210, 135, 15, 255)

    # 1. Golden Talon Feet
    draw_cute_foot(draw, cx - 100, H * 0.84, 110, 60, BEAK_GOLD)
    draw_cute_foot(draw, cx + 100, H * 0.84, 110, 60, BEAK_GOLD)

    # 2. Chubby Torso
    body_cy = H * 0.66
    draw.ellipse([cx - 215, body_cy - 195, cx + 215, body_cy + 195], fill=OUTLINE)
    draw.ellipse([cx - 207, body_cy - 187, cx + 207, body_cy + 187], fill=OWL_PURPLE)
    draw.ellipse([cx - 138, body_cy - 120, cx + 138, body_cy + 160], fill=OWL_CREAM)

    # Feather chevron details on chest (v v v)
    for vy in [body_cy - 15, body_cy + 35, body_cy + 85]:
        for vx in [cx - 48, cx, cx + 48]:
            draw_thick_line(draw, (vx - 14, vy - 8), (vx, vy + 6), OUTLINE, 4)
            draw_thick_line(draw, (vx, vy + 6), (vx + 14, vy - 8), OUTLINE, 4)

    # 3. Folded Scalloped Wings at Sides
    draw.ellipse([cx - 238, body_cy - 110, cx - 145, body_cy + 80], fill=OUTLINE)
    draw.ellipse([cx - 230, body_cy - 102, cx - 153, body_cy + 72], fill=OWL_PURPLE)

    draw.ellipse([cx + 145, body_cy - 110, cx + 238, body_cy + 80], fill=OUTLINE)
    draw.ellipse([cx + 153, body_cy - 102, cx + 230, body_cy + 72], fill=OWL_PURPLE)

    # 4. Ear Horns / Tufts
    hcy = H * 0.36
    draw.polygon([(cx - 200, hcy - 60), (cx - 165, hcy - 245), (cx - 70, hcy - 135)], fill=OUTLINE)
    draw.polygon([(cx - 192, hcy - 62), (cx - 165, hcy - 235), (cx - 78, hcy - 133)], fill=OWL_PURPLE)

    draw.polygon([(cx + 200, hcy - 60), (cx + 165, hcy - 245), (cx + 70, hcy - 135)], fill=OUTLINE)
    draw.polygon([(cx + 192, hcy - 62), (cx + 165, hcy - 235), (cx + 78, hcy - 133)], fill=OWL_PURPLE)

    # 5. Head
    head_rx, head_ry = 230, 198
    draw.ellipse([cx - head_rx - 8, hcy - head_ry - 8, cx + head_rx + 8, hcy + head_ry + 8], fill=OUTLINE)
    draw.ellipse([cx - head_rx, hcy - head_ry, cx + head_rx, hcy + head_ry], fill=OWL_PURPLE)

    # 6. Spectacles / Facial Disc around Eyes
    disc_r = 96
    draw.ellipse([cx - 82 - disc_r, hcy - disc_r, cx - 82 + disc_r, hcy + disc_r], fill=OWL_CREAM, outline=OUTLINE, width=7)
    draw.ellipse([cx + 82 - disc_r, hcy - disc_r, cx + 82 + disc_r, hcy + disc_r], fill=OWL_CREAM, outline=OUTLINE, width=7)

    # 7. Eyes
    draw_lily_style_eyes(draw, cx - 82, cx + 82, hcy - 4, iris_color=IRIS_GOLD, iris_rim=IRIS_RIM, eye_r=55)

    # 8. Rosy Cheeks
    draw_rosy_cheeks(draw, cx - 150, cx + 150, hcy + 58, rw=36, rh=20)

    # 9. Gold Triangular Beak
    draw.polygon([(cx - 24, hcy + 12), (cx + 24, hcy + 12), (cx, hcy + 75)], fill=OUTLINE)
    draw.polygon([(cx - 18, hcy + 15), (cx + 18, hcy + 15), (cx, hcy + 68)], fill=BEAK_GOLD)

    return apply_pediatric_outline(img, stroke_w=6)

# ==============================================================================
# MAIN DEPLOYMENT PIPELINE
# ==============================================================================
def main():
    base_dir = "/mnt/c/Users/Zendrix/Desktop/thesis/playIT-v2-workspace"
    brain_dir = "/home/zendrix/.gemini/antigravity-cli/brain/af2db79b-86c4-4925-a8b9-dd27044dffec"
    char_dir = os.path.join(base_dir, "app", "src", "main", "assets", "images", "characters")
    mascot_dir = os.path.join(base_dir, "app", "src", "main", "assets", "images", "mascot")

    os.makedirs(char_dir, exist_ok=True)
    os.makedirs(mascot_dir, exist_ok=True)

    avatars = [
        ("avatar_01", "cat", render_miki_cat()),
        ("avatar_02", "monkey", render_milo_monkey()),
        ("avatar_03", "bunny", render_bella_bunny()),
        ("avatar_04", "bear", render_barnaby_bear()),
        ("avatar_05", "frog", render_finley_frog()),
        ("avatar_06", "owl", render_ollie_owl()),
    ]

    print("[*] Generating master-edition v3 Companion Animal Avatars matching Lily the Tarsier...")

    for code, name, raw_img in avatars:
        # Downscale with 2x Lanczos supersampling to pristine 512x512
        final_img = raw_img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

        # 1. characters/avatar_0X_name.png
        p1 = os.path.join(char_dir, f"{code}_{name}.png")
        final_img.save(p1, "PNG", optimize=True)

        # 2. mascot/companion_avatar_0X_name.png
        p2 = os.path.join(mascot_dir, f"companion_{code}_{name}.png")
        final_img.save(p2, "PNG", optimize=True)

        # 3. mascot/avatar_0X.png
        p3 = os.path.join(mascot_dir, f"{code}.png")
        final_img.save(p3, "PNG", optimize=True)

        # Preview in brain
        prev = os.path.join(brain_dir, f"preview_new_{code}_{name}.png")
        final_img.save(prev, "PNG", optimize=True)

        print(f"  -> Successfully generated & deployed: {code}_{name}.png")

    print("\nAll 6 Companion Animal Avatars successfully built and deployed matching Lily the Tarsier!")

if __name__ == "__main__":
    main()
