"""
PlayIT Pediatric Phoneme Pictures Suite Generator
Reworks the 17 remaining phoneme picture cards and word cards to commercial-grade
Duolingo ABC pediatric standards with 2x Lanczos supersampling (1024x1024 -> 512x512),
layered 3-tone shading, expressive storybook charm, and #2D373E continuous outlines:
1.  picture_cat.png    / word_cat.png    (Letter c)
2.  picture_dog.png    / word_dog.png    (Letter d)
3.  picture_lion.png   / word_lion.png   (Letter l)
4.  picture_pig.png    / word_pig.png    (Letter p)
5.  picture_rabbit.png / word_rabbit.png (Letter r)
6.  picture_goat.png   / word_goat.png   (Letter g)
7.  picture_zebra.png  / word_zebra.png  (Letter z)
8.  picture_fish.png   / word_fish.png   (Letter f)
9.  picture_queen.png  / word_queen.png  (Letter q)
10. picture_kite.png   / word_kite.png   (Letter k)
11. picture_nest.png   / word_nest.png   (Letter n)
12. picture_hat.png    / word_hat.png    (Letter h)
13. picture_watch.png  / word_watch.png  (Letter w)
14. picture_yoyo.png   / word_yoyo.png   (Letter y)
15. picture_jug.png    / word_jug.png    (Letter j)
16. picture_van.png    / word_van.png    (Letter v)
17. picture_box.png    / word_box.png    (Letter x)
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

def bezier_curve(p0, p1, p2, p3, steps=30):
    pts = []
    for i in range(steps + 1):
        t = i / float(steps)
        u = 1.0 - t
        x = (u**3)*p0[0] + 3*(u**2)*t*p1[0] + 3*u*(t**2)*p2[0] + (t**3)*p3[0]
        y = (u**3)*p0[1] + 3*(u**2)*t*p1[1] + 3*u*(t**2)*p2[1] + (t**3)*p3[1]
        pts.append((x, y))
    return pts

def apply_pediatric_outline(image: Image.Image, stroke_w: int = 8) -> Image.Image:
    alpha = image.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_w * 2 + 1))
    stroke_layer = Image.new("RGBA", image.size, (0, 0, 0, 0))
    stroke_base = Image.new("RGBA", image.size, OUTLINE)
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, image)

def draw_pediatric_eyes(draw, lx, rx, ly, iris_color=(60, 160, 240, 255), pupil_r=44, eye_spacing=110):
    """Duolingo ABC pediatric eyes with rich colored iris and double catchlights."""
    for cx in [lx, rx]:
        # Eyebrow
        brow_y = ly - pupil_r - 16
        draw_thick_arc(draw, [cx - 30, brow_y - 8, cx + 30, brow_y + 12], 200, 340, OUTLINE, 7)
        # Eye white base
        draw.ellipse([cx - pupil_r, ly - pupil_r, cx + pupil_r, ly + pupil_r], fill=WHITE)
        draw.ellipse([cx - pupil_r, ly - pupil_r, cx + pupil_r, ly + pupil_r], outline=OUTLINE, width=6)
        # Iris
        ir_r = pupil_r * 0.82
        draw.ellipse([cx - ir_r, ly - ir_r, cx + ir_r, ly + ir_r], fill=iris_color)
        # Deep Pupil
        p_r = pupil_r * 0.58
        draw.ellipse([cx - p_r, ly - p_r, cx + p_r, ly + p_r], fill=OUTLINE)
        # Catchlight 1 (top-left)
        s1 = p_r * 0.44
        draw.ellipse([cx - p_r*0.35 - s1, ly - p_r*0.35 - s1, cx - p_r*0.35 + s1, ly - p_r*0.35 + s1], fill=WHITE)
        # Catchlight 2 (bottom-right)
        s2 = p_r * 0.22
        draw.ellipse([cx + p_r*0.35 - s2, ly + p_r*0.35 - s2, cx + p_r*0.35 + s2, ly + p_r*0.35 + s2], fill=WHITE)

def draw_cheeks_and_mouth(draw, cx, cy, mouth_w=28, smile_depth=20, has_tongue=True):
    draw.ellipse([cx - 100 - 24, cy - 8 - 14, cx - 100 + 24, cy - 8 + 14], fill=ROSY_CHEEK)
    draw.ellipse([cx + 100 - 24, cy - 8 - 14, cx + 100 + 24, cy - 8 + 14], fill=ROSY_CHEEK)
    # Smile
    draw.chord([cx - mouth_w, cy - 4, cx + mouth_w, cy + smile_depth * 2], start=0, end=180, fill=OUTLINE)
    if has_tongue:
        draw.chord([cx - mouth_w * 0.7, cy + smile_depth * 0.6, cx + mouth_w * 0.7, cy + smile_depth * 1.9], start=0, end=180, fill=TONGUE)

# ==============================================================================
# 1. CAT (Cute ginger tabby kitten sitting happily)
# ==============================================================================
def render_cat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    ORANGE_BASE = (255, 140, 0, 255)
    ORANGE_DARK = (220, 100, 0, 255)
    CREAM = (255, 240, 220, 255)

    # Tail curling up to right
    tail_pts = bezier_curve((cx + 120, cy + 220), (cx + 340, cy + 260), (cx + 380, cy + 80), (cx + 280, cy + 20), 30)
    for i in range(len(tail_pts) - 1):
        draw.line([tail_pts[i], tail_pts[i+1]], fill=ORANGE_BASE, width=54)
    # White tail tip
    draw.ellipse([cx + 260, cy, cx + 305, cy + 45], fill=WHITE)

    # Chubby Body
    draw.ellipse([cx - 190, cy + 20, cx + 190, cy + 320], fill=ORANGE_BASE)
    # White Chest Patch
    draw.ellipse([cx - 110, cy + 80, cx + 110, cy + 280], fill=WHITE)

    # Front Paws
    for px in [cx - 75, cx + 75]:
        draw.ellipse([px - 45, cy + 260, px + 45, cy + 335], fill=WHITE)
        draw.arc([px - 20, cy + 295, px - 5, cy + 330], 20, 160, fill=OUTLINE, width=5)
        draw.arc([px + 5, cy + 295, px + 20, cy + 330], 20, 160, fill=OUTLINE, width=5)

    # Cat Head with organic cheek tufts
    head_pts = (
        bezier_curve((cx - 160, cy - 230), (cx, cy - 250), (cx, cy - 250), (cx + 160, cy - 230)) +
        bezier_curve((cx + 160, cy - 230), (cx + 230, cy - 140), (cx + 250, cy - 20), (cx + 180, cy + 50)) +
        bezier_curve((cx + 180, cy + 50), (cx + 120, cy + 90), (cx - 120, cy + 90), (cx - 180, cy + 50)) +
        bezier_curve((cx - 180, cy + 50), (cx - 250, cy - 20), (cx - 230, cy - 140), (cx - 160, cy - 230))
    )
    # Ears
    ear_l = [(cx - 180, cy - 140), (cx - 210, cy - 360), (cx - 70, cy - 220)]
    ear_r = [(cx + 180, cy - 140), (cx + 210, cy - 360), (cx + 70, cy - 220)]
    draw.polygon(ear_l, fill=ORANGE_BASE)
    draw.polygon(ear_r, fill=ORANGE_BASE)
    draw.polygon([(cx - 165, cy - 160), (cx - 195, cy - 330), (cx - 85, cy - 220)], fill=(255, 170, 185, 255))
    draw.polygon([(cx + 165, cy - 160), (cx + 195, cy - 330), (cx + 85, cy - 220)], fill=(255, 170, 185, 255))

    draw.polygon(head_pts, fill=ORANGE_BASE)

    # Tabby Head Stripes
    draw.polygon([(cx, cy - 240), (cx - 20, cy - 150), (cx + 20, cy - 150)], fill=ORANGE_DARK)
    draw.polygon([(cx - 45, cy - 230), (cx - 60, cy - 160), (cx - 35, cy - 160)], fill=ORANGE_DARK)
    draw.polygon([(cx + 45, cy - 230), (cx + 60, cy - 160), (cx + 35, cy - 160)], fill=ORANGE_DARK)

    # White Muzzle
    draw.ellipse([cx - 85, cy - 30, cx + 85, cy + 60], fill=WHITE)
    # Pink Nose
    draw.polygon([(cx - 18, cy - 15), (cx + 18, cy - 15), (cx, cy + 5)], fill=(244, 95, 115, 255))

    # Eyes
    draw_pediatric_eyes(draw, cx - 80, cx + 80, cy - 70, iris_color=(76, 175, 80, 255), pupil_r=38)
    draw_cheeks_and_mouth(draw, cx, cy + 20, mouth_w=24, smile_depth=16)

    # Whisker lines
    for dy in [-10, 12]:
        draw.line([(cx - 75, cy + dy), (cx - 180, cy + dy - 6)], fill=OUTLINE, width=6)
        draw.line([(cx + 75, cy + dy), (cx + 180, cy + dy - 6)], fill=OUTLINE, width=6)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 2. DOG (Happy golden puppy with floppy chocolate ears)
# ==============================================================================
def render_dog():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    GOLD_DOG = (245, 185, 65, 255)
    BROWN_EAR = (140, 85, 35, 255)

    # Wagging tail on right
    tail_pts = bezier_curve((cx + 120, cy + 200), (cx + 320, cy + 210), (cx + 340, cy + 100), (cx + 270, cy + 50), 30)
    for i in range(len(tail_pts) - 1):
        draw.line([tail_pts[i], tail_pts[i+1]], fill=GOLD_DOG, width=50)

    # Body
    draw.ellipse([cx - 190, cy + 10, cx + 190, cy + 320], fill=GOLD_DOG)
    draw.ellipse([cx - 100, cy + 70, cx + 100, cy + 270], fill=(255, 245, 220, 255))

    # Chubby Paws
    for px in [cx - 75, cx + 75]:
        draw.ellipse([px - 48, cy + 260, px + 48, cy + 335], fill=GOLD_DOG)
        draw.arc([px - 22, cy + 295, px - 6, cy + 330], 20, 160, fill=OUTLINE, width=5)
        draw.arc([px + 6, cy + 295, px + 22, cy + 330], 20, 160, fill=OUTLINE, width=5)

    # Floppy Ears behind head
    ear_l = bezier_curve((cx - 130, cy - 180), (cx - 280, cy - 140), (cx - 260, cy + 80), (cx - 160, cy + 40), 30)
    ear_r = bezier_curve((cx + 130, cy - 180), (cx + 280, cy - 140), (cx + 260, cy + 80), (cx + 160, cy + 40), 30)
    draw.polygon(ear_l, fill=BROWN_EAR)
    draw.polygon(ear_r, fill=BROWN_EAR)

    # Head
    draw.ellipse([cx - 190, cy - 240, cx + 190, cy + 60], fill=GOLD_DOG)

    # Muzzle
    draw.ellipse([cx - 95, cy - 40, cx + 95, cy + 55], fill=(255, 245, 220, 255))
    # Black Nose
    draw.ellipse([cx - 26, cy - 25, cx + 26, cy + 10], fill=OUTLINE)
    draw.ellipse([cx - 16, cy - 20, cx - 6, cy - 10], fill=WHITE)

    # Eyes
    draw_pediatric_eyes(draw, cx - 80, cx + 80, cy - 75, iris_color=(120, 70, 25, 255), pupil_r=38)
    draw_cheeks_and_mouth(draw, cx, cy + 20, mouth_w=28, smile_depth=20, has_tongue=True)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 3. LION (Cute lion cub with flower-petal mane)
# ==============================================================================
def render_lion():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 15

    LION_GOLD = (255, 195, 45, 255)
    MANE_ORANGE = (235, 120, 20, 255)

    # Body
    draw.ellipse([cx - 170, cy + 40, cx + 170, cy + 320], fill=LION_GOLD)
    draw.ellipse([cx - 90, cy + 90, cx + 90, cy + 280], fill=(255, 242, 190, 255))

    # Paws
    for px in [cx - 70, cx + 70]:
        draw.ellipse([px - 45, cy + 260, px + 45, cy + 335], fill=LION_GOLD)

    # Fluffy Mane (12 overlapping rounded lobes)
    mane_cy = cy - 80
    num_lobes = 12
    for i in range(num_lobes):
        ang = math.radians(i * (360 / num_lobes))
        lx = cx + 220 * math.cos(ang)
        ly = mane_cy + 220 * math.sin(ang)
        draw.ellipse([lx - 75, ly - 75, lx + 75, ly + 75], fill=MANE_ORANGE)

    # Lion Head
    draw.ellipse([cx - 180, mane_cy - 180, cx + 180, mane_cy + 180], fill=LION_GOLD)

    # Ears
    draw.ellipse([cx - 180, mane_cy - 180, cx - 90, mane_cy - 90], fill=LION_GOLD)
    draw.ellipse([cx - 165, mane_cy - 165, cx - 105, mane_cy - 105], fill=(180, 80, 15, 255))
    draw.ellipse([cx + 90, mane_cy - 180, cx + 180, mane_cy - 90], fill=LION_GOLD)
    draw.ellipse([cx + 105, mane_cy - 165, cx + 165, mane_cy - 105], fill=(180, 80, 15, 255))

    # Muzzle
    draw.ellipse([cx - 85, mane_cy + 10, cx + 85, mane_cy + 110], fill=(255, 242, 190, 255))
    # Brown Nose
    draw.polygon([(cx - 22, mane_cy + 30), (cx + 22, mane_cy + 30), (cx, mane_cy + 52)], fill=(130, 60, 20, 255))

    draw_pediatric_eyes(draw, cx - 75, cx + 75, mane_cy - 35, iris_color=(220, 140, 20, 255), pupil_r=36)
    draw_cheeks_and_mouth(draw, cx, mane_cy + 65, mouth_w=24, smile_depth=16)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 4. PIG (Rosy pink piglet with curly tail and snout)
# ==============================================================================
def render_pig():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    PIG_PINK = (255, 175, 190, 255)
    PIG_DARK = (240, 135, 155, 255)

    # Curly Tail on left
    draw.arc([cx - 300, cy + 120, cx - 180, cy + 220], start=30, end=330, fill=PIG_DARK, width=24)

    # Chubby Body
    draw.ellipse([cx - 200, cy + 20, cx + 200, cy + 320], fill=PIG_PINK)
    draw.ellipse([cx - 120, cy + 90, cx + 120, cy + 290], fill=(255, 205, 215, 255))

    # Trotter Feet
    for fx in [cx - 75, cx + 75]:
        draw.ellipse([fx - 42, cy + 265, fx + 42, cy + 335], fill=PIG_DARK)
        draw.polygon([(fx - 6, cy + 300), (fx + 6, cy + 300), (fx, cy + 335)], fill=OUTLINE)

    # Head
    draw.ellipse([cx - 185, cy - 230, cx + 185, cy + 70], fill=PIG_PINK)

    # Floppy Ears
    ear_l = [(cx - 120, cy - 180), (cx - 220, cy - 260), (cx - 190, cy - 100)]
    ear_r = [(cx + 120, cy - 180), (cx + 220, cy - 260), (cx + 190, cy - 100)]
    draw.polygon(ear_l, fill=PIG_DARK)
    draw.polygon(ear_r, fill=PIG_DARK)

    # Big Cute Oval Snout
    draw.ellipse([cx - 75, cy - 25, cx + 75, cy + 45], fill=PIG_DARK)
    # Nostrils
    draw.ellipse([cx - 38, cy - 8, cx - 12, cy + 22], fill=OUTLINE)
    draw.ellipse([cx + 12, cy - 8, cx + 38, cy + 22], fill=OUTLINE)

    draw_pediatric_eyes(draw, cx - 80, cx + 80, cy - 70, iris_color=(60, 140, 220, 255), pupil_r=36)
    draw_cheeks_and_mouth(draw, cx, cy + 50, mouth_w=22, smile_depth=14)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 5. RABBIT (Snow-white bunny with long pink inner ears)
# ==============================================================================
def render_rabbit():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 35

    RABBIT_WHITE = (255, 255, 255, 255)
    INNER_PINK = (255, 175, 195, 255)

    # Long Upright Ears
    for ex in [cx - 85, cx + 85]:
        draw.ellipse([ex - 48, cy - 420, ex + 48, cy - 120], fill=RABBIT_WHITE)
        draw.ellipse([ex - 28, cy - 390, ex + 28, cy - 150], fill=INNER_PINK)

    # Fluffy Cotton Tail
    draw.ellipse([cx + 150, cy + 180, cx + 260, cy + 280], fill=RABBIT_WHITE)

    # Body
    draw.ellipse([cx - 180, cy + 10, cx + 180, cy + 310], fill=RABBIT_WHITE)
    draw.ellipse([cx - 100, cy + 70, cx + 100, cy + 270], fill=(245, 245, 250, 255))

    # Paws
    for px in [cx - 70, cx + 70]:
        draw.ellipse([px - 44, cy + 250, px + 44, cy + 320], fill=RABBIT_WHITE)

    # Head
    draw.ellipse([cx - 175, cy - 200, cx + 175, cy + 80], fill=RABBIT_WHITE)

    # Pink Twitching Nose
    draw.polygon([(cx - 16, cy - 8), (cx + 16, cy - 8), (cx, cy + 8)], fill=INNER_PINK)

    draw_pediatric_eyes(draw, cx - 75, cx + 75, cy - 65, iris_color=(235, 110, 140, 255), pupil_r=36)
    draw_cheeks_and_mouth(draw, cx, cy + 15, mouth_w=20, smile_depth=14)

    # Whiskers
    for dy in [-8, 10]:
        draw.line([(cx - 60, cy + dy), (cx - 160, cy + dy - 4)], fill=OUTLINE, width=5)
        draw.line([(cx + 60, cy + dy), (cx + 160, cy + dy - 4)], fill=OUTLINE, width=5)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 6. GOAT (Charming baby goat with curved horns and beard)
# ==============================================================================
def render_goat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    GOAT_CREAM = (245, 235, 215, 255)
    HORN_BROWN = (175, 130, 85, 255)

    # Curved Horns
    horn_l = bezier_curve((cx - 60, cy - 190), (cx - 120, cy - 320), (cx - 190, cy - 310), (cx - 160, cy - 200), 20)
    horn_r = bezier_curve((cx + 60, cy - 190), (cx + 120, cy - 320), (cx + 190, cy - 310), (cx + 160, cy - 200), 20)
    draw.polygon(horn_l, fill=HORN_BROWN)
    draw.polygon(horn_r, fill=HORN_BROWN)

    # Floppy Horizontal Ears
    draw.ellipse([cx - 240, cy - 140, cx - 110, cy - 70], fill=GOAT_CREAM)
    draw.ellipse([cx + 110, cy - 140, cx + 240, cy - 70], fill=GOAT_CREAM)

    # Body
    draw.ellipse([cx - 170, cy + 20, cx + 170, cy + 320], fill=GOAT_CREAM)
    # Hooves
    for fx in [cx - 65, cx + 65]:
        draw.ellipse([fx - 40, cy + 265, fx + 40, cy + 335], fill=HORN_BROWN)

    # Head
    draw.ellipse([cx - 160, cy - 210, cx + 160, cy + 70], fill=GOAT_CREAM)

    # White Beard
    draw.polygon([(cx - 30, cy + 60), (cx + 30, cy + 60), (cx, cy + 130)], fill=WHITE)

    # Muzzle
    draw.ellipse([cx - 75, cy - 25, cx + 75, cy + 55], fill=(255, 250, 240, 255))
    draw.polygon([(cx - 18, cy - 10), (cx + 18, cy - 10), (cx, cy + 6)], fill=(120, 85, 60, 255))

    draw_pediatric_eyes(draw, cx - 70, cx + 70, cy - 65, iris_color=(190, 130, 40, 255), pupil_r=34)
    draw_cheeks_and_mouth(draw, cx, cy + 18, mouth_w=20, smile_depth=14)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 7. ZEBRA (Cute baby zebra foal with crisp black-and-white stripes)
# ==============================================================================
def render_zebra():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    ZEBRA_WHITE = (255, 255, 255, 255)
    DARK_STRIPE = (45, 55, 62, 255)

    # Spiky Mane on top
    for mx in range(int(cx - 50), int(cx + 60), 20):
        draw.polygon([(mx - 10, cy - 220), (mx + 10, cy - 220), (mx, cy - 310)], fill=DARK_STRIPE)

    # Ears
    for ex in [cx - 110, cx + 110]:
        draw.ellipse([ex - 35, cy - 310, ex + 35, cy - 180], fill=ZEBRA_WHITE)
        draw.ellipse([ex - 20, cy - 280, ex + 20, cy - 200], fill=(255, 175, 195, 255))

    # Body
    draw.ellipse([cx - 180, cy + 10, cx + 180, cy + 315], fill=ZEBRA_WHITE)
    # Body stripes
    for sy in [cy + 70, cy + 140, cy + 210]:
        draw.polygon([(cx - 170, sy), (cx - 70, sy + 15), (cx - 165, sy + 30)], fill=DARK_STRIPE)
        draw.polygon([(cx + 170, sy), (cx + 70, sy + 15), (cx + 165, sy + 30)], fill=DARK_STRIPE)

    # Hooves
    for fx in [cx - 70, cx + 70]:
        draw.ellipse([fx - 42, cy + 260, fx + 42, cy + 335], fill=DARK_STRIPE)

    # Head
    draw.ellipse([cx - 170, cy - 215, cx + 170, cy + 70], fill=ZEBRA_WHITE)

    # Head stripes
    draw.polygon([(cx - 30, cy - 210), (cx, cy - 130), (cx + 30, cy - 210)], fill=DARK_STRIPE)
    draw.polygon([(cx - 160, cy - 120), (cx - 80, cy - 100), (cx - 150, cy - 80)], fill=DARK_STRIPE)
    draw.polygon([(cx + 160, cy - 120), (cx + 80, cy - 100), (cx + 150, cy - 80)], fill=DARK_STRIPE)

    # Gray Muzzle
    draw.ellipse([cx - 85, cy - 20, cx + 85, cy + 60], fill=(120, 130, 140, 255))
    draw.ellipse([cx - 32, cy, cx - 12, cy + 20], fill=DARK_STRIPE)
    draw.ellipse([cx + 12, cy, cx + 32, cy + 20], fill=DARK_STRIPE)

    draw_pediatric_eyes(draw, cx - 75, cx + 75, cy - 65, iris_color=(60, 130, 210, 255), pupil_r=36)
    draw_cheeks_and_mouth(draw, cx, cy + 32, mouth_w=22, smile_depth=14)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 8. FISH (Vibrant cartoon clownfish with flowing fins)
# ==============================================================================
def render_fish():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    ORANGE_BODY = (255, 120, 15, 255)

    # Big Rounded Tail Fin (flowing back to left)
    tail_pts = (
        bezier_curve((cx - 120, cy), (cx - 340, cy - 200), (cx - 360, cy - 120), (cx - 320, cy)) +
        bezier_curve((cx - 320, cy), (cx - 360, cy + 120), (cx - 340, cy + 200), (cx - 120, cy))
    )
    draw.polygon(tail_pts, fill=(255, 150, 40, 255))

    # Dorsal Fin (top)
    dorsal_pts = bezier_curve((cx - 100, cy - 160), (cx - 40, cy - 320), (cx + 80, cy - 300), (cx + 120, cy - 140), 20)
    draw.polygon(dorsal_pts, fill=(255, 150, 40, 255))

    # Pectoral Fin (bottom)
    pect_pts = bezier_curve((cx - 40, cy + 140), (cx, cy + 290), (cx + 100, cy + 270), (cx + 80, cy + 130), 20)
    draw.polygon(pect_pts, fill=(255, 150, 40, 255))

    # Plump Oval Fish Body (facing right)
    draw.ellipse([cx - 210, cy - 170, cx + 250, cy + 170], fill=ORANGE_BODY)

    # White Curved Clownfish Stripe
    stripe_pts = bezier_curve((cx - 10, cy - 165), (cx + 30, cy), (cx + 30, cy), (cx - 10, cy + 165), 20) + \
                 bezier_curve((cx - 50, cy + 165), (cx - 10, cy), (cx - 10, cy), (cx - 50, cy - 165), 20)
    draw.polygon(stripe_pts, fill=WHITE)

    # Big Front Eye
    draw_pediatric_eyes(draw, cx + 120, cx + 120, cy - 35, iris_color=(40, 160, 220, 255), pupil_r=44)

    # Smiling Fish Lips
    draw.ellipse([cx + 215, cy + 15, cx + 275, cy + 65], fill=(255, 90, 80, 255))
    draw.ellipse([cx + 215, cy + 45, cx + 265, cy + 85], fill=(255, 90, 80, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 9. QUEEN (Chibi storybook queen with golden crown)
# ==============================================================================
def render_queen():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 10

    PURPLE_ROYAL = (156, 39, 176, 255)
    GOLD_CROWN = (255, 215, 0, 255)

    # Royal Dress Bell
    dress_pts = [(cx - 40, cy + 60), (cx + 40, cy + 60), (cx + 210, cy + 320), (cx - 210, cy + 320)]
    draw.polygon(dress_pts, fill=PURPLE_ROYAL)
    # White Ermine Fur Collar
    draw.ellipse([cx - 120, cy + 50, cx + 120, cy + 120], fill=WHITE)

    # Chibi Face
    draw.ellipse([cx - 170, cy - 180, cx + 170, cy + 80], fill=(255, 225, 205, 255))

    # Fluffy Brown Hair Bob
    hair_l = bezier_curve((cx - 140, cy - 160), (cx - 240, cy - 60), (cx - 220, cy + 120), (cx - 140, cy + 100), 20)
    hair_r = bezier_curve((cx + 140, cy - 160), (cx + 240, cy - 60), (cx + 220, cy + 120), (cx + 140, cy + 100), 20)
    draw.polygon(hair_l, fill=(110, 60, 25, 255))
    draw.polygon(hair_r, fill=(110, 60, 25, 255))
    # Hair Bangs
    draw.arc([cx - 160, cy - 200, cx + 160, cy - 60], 180, 360, fill=(110, 60, 25, 255), width=45)

    # Sparkling Gold Crown
    crown_pts = [
        (cx - 120, cy - 180), (cx - 140, cy - 320), (cx - 60, cy - 240),
        (cx, cy - 340), (cx + 60, cy - 240), (cx + 140, cy - 320), (cx + 120, cy - 180)
    ]
    draw.polygon(crown_pts, fill=GOLD_CROWN)
    # Crown jewels (rubies & emeralds)
    for jx, jy, col in [(cx - 140, cy - 320, (255, 60, 60, 255)), (cx, cy - 340, (76, 175, 80, 255)), (cx + 140, cy - 320, (33, 150, 243, 255))]:
        draw.ellipse([jx - 15, jy - 15, jx + 15, jy + 15], fill=col)

    draw_pediatric_eyes(draw, cx - 75, cx + 75, cy - 60, iris_color=(120, 60, 180, 255), pupil_r=36)
    draw_cheeks_and_mouth(draw, cx, cy + 15, mouth_w=20, smile_depth=14)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 10. KITE (Colorful diamond kite with fluttering bows)
# ==============================================================================
def render_kite():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 - 50

    top = (cx, cy - 260)
    bottom = (cx, cy + 220)
    left = (cx - 240, cy - 40)
    right = (cx + 240, cy - 40)

    # 4 Colored Quadrants
    draw.polygon([top, left, (cx, cy - 40)], fill=(255, 82, 82, 255))      # Red
    draw.polygon([top, right, (cx, cy - 40)], fill=(255, 215, 0, 255))     # Yellow
    draw.polygon([bottom, left, (cx, cy - 40)], fill=(41, 182, 246, 255))  # Blue
    draw.polygon([bottom, right, (cx, cy - 40)], fill=(76, 175, 80, 255))   # Green

    # Cross Spars
    draw.line([top, bottom], fill=OUTLINE, width=12)
    draw.line([left, right], fill=OUTLINE, width=12)

    # Swirling Kite Tail
    tail_pts = bezier_curve(bottom, (cx - 180, cy + 300), (cx + 120, cy + 380), (cx - 40, H - 70), 30)
    for i in range(len(tail_pts) - 1):
        draw.line([tail_pts[i], tail_pts[i+1]], fill=(120, 140, 160, 255), width=8)

    # Colorful Ribbon Bows along the tail
    BOW_COLORS = [(255, 82, 82, 255), (255, 175, 0, 255), (156, 39, 176, 255)]
    for idx, t_idx in enumerate([8, 18, 28]):
        bx, by = tail_pts[t_idx]
        b_col = BOW_COLORS[idx % len(BOW_COLORS)]
        # Bow knot & loops
        draw.polygon([(bx - 26, by - 16), (bx, by), (bx - 26, by + 16)], fill=b_col)
        draw.polygon([(bx + 26, by - 16), (bx, by), (bx + 26, by + 16)], fill=b_col)
        draw.ellipse([bx - 8, by - 8, bx + 8, by + 8], fill=WHITE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 11. NEST (Woven twig nest with 3 speckled blue eggs)
# ==============================================================================
def render_nest():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    # 3 Pastel Cyan/Blue Bird Eggs sitting inside
    EGGS = [
        (cx - 90, cy - 60, -15),
        (cx + 90, cy - 60,  15),
        (cx,      cy - 90,   0),
    ]
    EGG_BLUE = (128, 222, 234, 255)
    for ex, ey, ang in EGGS:
        draw.ellipse([ex - 60, ey - 85, ex + 60, ey + 85], fill=EGG_BLUE)
        draw.ellipse([ex - 35, ey - 60, ex - 10, ey - 20], fill=WHITE) # shine
        # Speckles
        for sx, sy in [(-20, 20), (15, 30), (-10, -10), (25, -20)]:
            draw.ellipse([ex + sx - 5, ey + sy - 5, ex + sx + 5, ey + sy + 5], fill=(77, 182, 172, 255))

    # Woven Twig Nest Bowl
    nest_pts = (
        bezier_curve((cx - 300, cy - 30), (cx - 240, cy + 240), (cx + 240, cy + 240), (cx + 300, cy - 30)) +
        bezier_curve((cx + 300, cy - 30), (cx + 180, cy + 40), (cx - 180, cy + 40), (cx - 300, cy - 30))
    )
    draw.polygon(nest_pts, fill=(141, 91, 40, 255))

    # Woven Twig Overlays
    for i in range(14):
        y_pos = cy + 20 + i * 14
        spread = 260 - (i * 12)
        draw.arc([cx - spread, y_pos - 40, cx + spread, y_pos + 40], 10, 170, fill=(184, 128, 68, 255), width=8)

    # Green leafy sprout on edge
    draw.ellipse([cx - 280, cy - 60, cx - 220, cy - 20], fill=(88, 204, 2, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 12. HAT (Sunny straw sunhat with ribbon bow)
# ==============================================================================
def render_hat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    STRAW_YELLOW = (255, 215, 80, 255)
    RIBBON_RED = (255, 60, 80, 255)

    # Wide Floppy Rounded Brim
    draw.ellipse([cx - 380, cy + 20, cx + 380, cy + 180], fill=STRAW_YELLOW)
    draw.ellipse([cx - 340, cy + 35, cx + 340, cy + 165], fill=(245, 195, 60, 255))

    # Hat Crown Dome
    draw.ellipse([cx - 180, cy - 220, cx + 180, cy + 70], fill=STRAW_YELLOW)
    draw.arc([cx - 160, cy - 200, cx + 60, cy - 40], 190, 290, fill=WHITE, width=24) # gloss

    # Red Ribbon Band around base of crown
    draw.ellipse([cx - 185, cy + 10, cx + 185, cy + 75], fill=RIBBON_RED)

    # Ribbon Bow on right
    bow_x, bow_y = cx + 150, cy + 45
    draw.polygon([(bow_x - 30, bow_y - 20), (bow_x, bow_y), (bow_x - 30, bow_y + 20)], fill=RIBBON_RED)
    draw.polygon([(bow_x + 30, bow_y - 20), (bow_x, bow_y), (bow_x + 30, bow_y + 20)], fill=RIBBON_RED)
    draw.ellipse([bow_x - 12, bow_y - 12, bow_x + 12, bow_y + 12], fill=WHITE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 13. WATCH (Storybook wristwatch with smiling clock face)
# ==============================================================================
def render_watch():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # Blue Watch Straps (Top and Bottom)
    draw.rounded_rectangle([cx - 85, cy - 400, cx + 85, cy - 140], radius=24, fill=(41, 182, 246, 255))
    draw.rounded_rectangle([cx - 85, cy + 140, cx + 85, cy + 400], radius=24, fill=(41, 182, 246, 255))
    # Strap holes
    for hy in [cy + 220, cy + 280, cy + 340]:
        draw.ellipse([cx - 10, hy - 10, cx + 10, hy + 10], fill=OUTLINE)

    # Gold Circular Bezel Case
    case_r = 210
    draw.ellipse([cx - case_r, cy - case_r, cx + case_r, cy + case_r], fill=(255, 198, 15, 255))
    draw.ellipse([cx - case_r + 15, cy - case_r + 15, cx + case_r - 15, cy + case_r - 15], fill=(255, 225, 60, 255))

    # White Clock Face
    face_r = 160
    draw.ellipse([cx - face_r, cy - face_r, cx + face_r, cy + face_r], fill=WHITE)

    # Clock Face Numbers / Ticks (12, 3, 6, 9)
    for ang, col in [(0, (255, 82, 82, 255)), (90, (76, 175, 80, 255)), (180, (156, 39, 176, 255)), (270, (41, 182, 246, 255))]:
        rad = math.radians(ang - 90)
        tx = cx + 120 * math.cos(rad)
        ty = cy + 120 * math.sin(rad)
        draw.ellipse([tx - 14, ty - 14, tx + 14, ty + 14], fill=col)

    # Friendly Clock Hands at 10:10
    # Hour hand (pointing to 10)
    rad_h = math.radians(300 - 90)
    draw.line([(cx, cy), (cx + 65 * math.cos(rad_h), cy + 65 * math.sin(rad_h))], fill=OUTLINE, width=16)
    # Minute hand (pointing to 2)
    rad_m = math.radians(60 - 90)
    draw.line([(cx, cy), (cx + 100 * math.cos(rad_m), cy + 100 * math.sin(rad_m))], fill=OUTLINE, width=12)
    # Center jewel
    draw.ellipse([cx - 18, cy - 18, cx + 18, cy + 18], fill=(255, 82, 82, 255))

    # Cute Little Clock Smile below hands
    draw.chord([cx - 24, cy + 40, cx + 24, cy + 80], 0, 180, fill=OUTLINE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 14. YOYO (Cherry-red aerodynamic yoyo with yellow star & string)
# ==============================================================================
def render_yoyo():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    # Spiraling White String going up
    str_pts = bezier_curve((cx, cy - 140), (cx - 80, cy - 240), (cx + 60, cy - 340), (cx - 20, cy - 440), 30)
    for i in range(len(str_pts) - 1):
        draw.line([str_pts[i], str_pts[i+1]], fill=WHITE, width=12)

    # Back Rim
    draw.ellipse([cx - 220, cy - 190, cx + 220, cy + 190], fill=(200, 30, 50, 255))

    # Front Main Red Disk
    yoyo_r = 195
    draw.ellipse([cx - yoyo_r, cy - yoyo_r, cx + yoyo_r, cy + yoyo_r], fill=(255, 55, 75, 255))

    # Glossy Specular Arc
    draw.arc([cx - yoyo_r + 25, cy - yoyo_r + 25, cx + yoyo_r - 25, cy + yoyo_r - 25], 190, 280, fill=WHITE, width=28)

    # Yellow Star Decal in Center
    star_r = 75
    inner_r = 35
    s_pts = []
    for i in range(5):
        a1 = math.radians(-90 + i * 72)
        s_pts.append((cx + star_r * math.cos(a1), cy + star_r * math.sin(a1)))
        a2 = math.radians(-90 + i * 72 + 36)
        s_pts.append((cx + inner_r * math.cos(a2), cy + inner_r * math.sin(a2)))
    draw.polygon(s_pts, fill=(255, 215, 0, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 15. JUG (Sunny orange juice pitcher with citrus slice)
# ==============================================================================
def render_jug():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 10

    # Curved Pitcher Handle on right
    handle_pts = bezier_curve((cx + 140, cy - 120), (cx + 340, cy - 80), (cx + 340, cy + 160), (cx + 140, cy + 180), 25)
    for i in range(len(handle_pts) - 1):
        draw.line([handle_pts[i], handle_pts[i+1]], fill=(179, 229, 252, 255), width=44)

    # Glass Pitcher Body
    body_pts = (
        bezier_curve((cx - 180, cy - 200), (cx - 260, cy + 60), (cx - 220, cy + 260), (cx, cy + 260)) +
        bezier_curve((cx, cy + 260), (cx + 220, cy + 260), (cx + 260, cy + 60), (cx + 180, cy - 200))
    )
    draw.polygon(body_pts, fill=(225, 245, 254, 200))

    # Vibrant Orange Juice Level
    juice_pts = (
        bezier_curve((cx - 195, cy - 40), (cx - 230, cy + 80), (cx - 190, cy + 240), (cx, cy + 240)) +
        bezier_curve((cx, cy + 240), (cx + 190, cy + 240), (cx + 230, cy + 80), (cx + 195, cy - 40))
    )
    draw.polygon(juice_pts, fill=(255, 152, 0, 255))
    draw.ellipse([cx - 195, cy - 70, cx + 195, cy - 10], fill=(255, 183, 77, 255))

    # Citrus Orange Slice floating inside
    draw.chord([cx - 90, cy + 20, cx + 70, cy + 160], 30, 210, fill=(255, 215, 0, 255))
    draw.chord([cx - 75, cy + 35, cx + 55, cy + 145], 30, 210, fill=WHITE)

    # Glass Pouring Spout on left
    draw.polygon([(cx - 180, cy - 200), (cx - 250, cy - 240), (cx - 160, cy - 140)], fill=(179, 229, 252, 255))

    # Specular Gloss Stripe down pitcher
    draw.line([(cx - 140, cy - 140), (cx - 180, cy + 180)], fill=WHITE, width=22)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 16. VAN (Retro teal and cream camper van with friendly headlights)
# ==============================================================================
def render_van():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    TEAL_BASE = (0, 150, 136, 255)
    CREAM_TOP = (255, 248, 225, 255)

    # Rounded Van Body
    van_box = [cx - 360, cy - 180, cx + 360, cy + 180]
    draw.rounded_rectangle(van_box, radius=70, fill=TEAL_BASE)

    # Cream Upper Half
    draw.rounded_rectangle([cx - 360, cy - 180, cx + 360, cy], radius=70, fill=CREAM_TOP)
    draw.rectangle([cx - 360, cy - 70, cx + 360, cy], fill=CREAM_TOP)

    # Windows (Windshield & Side Windows)
    for wx in [cx - 240, cx - 60, cx + 120]:
        draw.rounded_rectangle([wx, cy - 150, wx + 140, cy - 25], radius=24, fill=(129, 212, 250, 255))
        draw.line([(wx + 30, cy - 130), (wx + 110, cy - 45)], fill=WHITE, width=12)

    # Round Headlights & Smile Grille
    draw.ellipse([cx - 370, cy + 30, cx - 310, cy + 90], fill=(255, 235, 59, 255))
    draw.ellipse([cx - 360, cy + 40, cx - 320, cy + 80], fill=WHITE)

    # 2 Big Chunky Wheels
    for wx in [cx - 210, cx + 210]:
        # Wheel well cutout
        draw.ellipse([wx - 80, cy + 100, wx + 80, cy + 260], fill=OUTLINE)
        # Tire
        draw.ellipse([wx - 70, cy + 110, wx + 70, cy + 250], fill=(60, 65, 70, 255))
        # Hubcap
        draw.ellipse([wx - 35, cy + 145, wx + 35, cy + 215], fill=(220, 230, 240, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 17. BOX (Festive yellow gift box with giant red ribbon bow)
# ==============================================================================
def render_box():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    BOX_YELLOW = (255, 202, 40, 255)
    RIBBON_RED = (244, 67, 54, 255)

    # Box Body
    draw.rounded_rectangle([cx - 220, cy - 120, cx + 220, cy + 240], radius=32, fill=BOX_YELLOW)

    # Box Lid
    draw.rounded_rectangle([cx - 250, cy - 180, cx + 250, cy - 100], radius=24, fill=(255, 224, 130, 255))

    # Vertical Red Ribbon
    draw.rectangle([cx - 45, cy - 180, cx + 45, cy + 240], fill=RIBBON_RED)

    # Horizontal Red Ribbon
    draw.rectangle([cx - 220, cy + 20, cx + 220, cy + 100], fill=RIBBON_RED)

    # Giant Fluffy Ribbon Bow on Top
    bow_y = cy - 240
    # Left bow loop
    draw.ellipse([cx - 180, bow_y - 80, cx - 20, bow_y + 80], fill=RIBBON_RED)
    draw.ellipse([cx - 150, bow_y - 50, cx - 50, bow_y + 50], fill=(255, 138, 128, 255))
    # Right bow loop
    draw.ellipse([cx + 20, bow_y - 80, cx + 180, bow_y + 80], fill=RIBBON_RED)
    draw.ellipse([cx + 50, bow_y - 50, cx + 150, bow_y + 50], fill=(255, 138, 128, 255))
    # Center knot
    draw.ellipse([cx - 45, bow_y - 45, cx + 45, bow_y + 45], fill=RIBBON_RED)
    draw.ellipse([cx - 30, bow_y - 30, cx - 5, bow_y - 5], fill=WHITE)

    return apply_pediatric_outline(img, stroke_w=7)

def main():
    pics_dir = "/mnt/c/Users/Zendrix/Desktop/thesis/playIT-v2-workspace/app/src/main/assets/images/pictures"
    brain_dir = "/home/zendrix/.gemini/antigravity-cli/brain/af2db79b-86c4-4925-a8b9-dd27044dffec"
    os.makedirs(pics_dir, exist_ok=True)

    items = [
        ("cat", render_cat()),
        ("dog", render_dog()),
        ("lion", render_lion()),
        ("pig", render_pig()),
        ("rabbit", render_rabbit()),
        ("goat", render_goat()),
        ("zebra", render_zebra()),
        ("fish", render_fish()),
        ("queen", render_queen()),
        ("kite", render_kite()),
        ("nest", render_nest()),
        ("hat", render_hat()),
        ("watch", render_watch()),
        ("yoyo", render_yoyo()),
        ("jug", render_jug()),
        ("van", render_van()),
        ("box", render_box()),
    ]

    print("[*] Generating Duolingo ABC Pediatric Phoneme Pictures Suite...")
    for name, raw_img in items:
        final_img = raw_img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)
        
        # Save picture_<name>.png
        pic_path = os.path.join(pics_dir, f"picture_{name}.png")
        final_img.save(pic_path, "PNG", optimize=True)
        
        # Save word_<name>.png
        word_path = os.path.join(pics_dir, f"word_{name}.png")
        final_img.save(word_path, "PNG", optimize=True)

        # Save preview to artifacts
        prev_path = os.path.join(brain_dir, f"preview_picture_{name}.png")
        final_img.save(prev_path, "PNG", optimize=True)
        print(f"  -> Saved picture_{name}.png and word_{name}.png")

    print("\nAll 17 Phoneme Picture & Word Assets successfully generated & deployed!")

if __name__ == "__main__":
    main()
