"""
PlayIT Master Vector Graphics Engine
Generates 100% vector-precise, pediatric illustrated picture cards and blend-word graphics.
Adheres strictly to docs/engineering-package/16_ILLUSTRATION_STYLE_GUIDE.md:
- DarkBrownOutline (#4A2E18)
- 3-tone cel shading (highlight top-left, base fill, shadow bottom-right)
- Double catchlight glossy eyes + rosy cheek blush on characters/animals
- 100% clean anti-aliased RGBA transparency
- 2x/3x supersampling with Lanczos downsampling
"""

import os
import math
from PIL import Image, ImageDraw, ImageFilter

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
PICTURES_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images", "pictures")
os.makedirs(PICTURES_DIR, exist_ok=True)

SIZE = 512
SCALE = 2
W = SIZE * SCALE
H = SIZE * SCALE

# 16_ILLUSTRATION_STYLE_GUIDE.md Token Palette
OUTLINE = (74, 46, 24, 255)           # #4A2E18 DarkBrownOutline
OUTLINE_WIDTH = 12 * SCALE
PUPIL = (60, 36, 21, 255)             # #3C2415 DarkEspresso
WHITE = (255, 255, 255, 255)
CREAM = (255, 253, 248, 255)         # #FFFDF8 Cream White
ROSY_CHEEK = (255, 170, 185, 220)    # Soft Blush

# Accent tokens
BLUE = (74, 144, 226, 255)           # #4A90E2 Learning Blue
BLUE_LIGHT = (147, 197, 253, 255)
BLUE_DARK = (37, 99, 235, 255)

GREEN = (76, 175, 80, 255)           # #4CAF50 Growth Green
GREEN_LIGHT = (134, 239, 172, 255)
GREEN_DARK = (22, 101, 52, 255)

GOLD = (255, 193, 7, 255)            # #FFC107 Achievement Gold
GOLD_LIGHT = (254, 240, 138, 255)
GOLD_DARK = (217, 119, 6, 255)

ORANGE = (255, 152, 0, 255)          # #FF9800 Energy Orange
ORANGE_LIGHT = (254, 215, 170, 255)
ORANGE_DARK = (194, 65, 12, 255)

PURPLE = (142, 125, 242, 255)        # #8E7DF2 Friendly Purple
PURPLE_LIGHT = (216, 180, 254, 255)
PURPLE_DARK = (107, 33, 168, 255)

PINK = (244, 114, 182, 255)
PINK_LIGHT = (251, 207, 232, 255)
PINK_DARK = (190, 24, 93, 255)

BROWN = (180, 83, 9, 255)
BROWN_LIGHT = (217, 119, 6, 255)
BROWN_DARK = (120, 53, 15, 255)

GRAY = (203, 213, 225, 255)
GRAY_LIGHT = (241, 245, 249, 255)
GRAY_DARK = (100, 116, 139, 255)

def apply_stroke(fill_img, stroke_w=OUTLINE_WIDTH, stroke_color=OUTLINE):
    alpha = fill_img.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_w * 2 + 1))
    stroke_base = Image.new("RGBA", fill_img.size, stroke_color)
    stroke_layer = Image.new("RGBA", fill_img.size, (0, 0, 0, 0))
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    res = Image.alpha_composite(stroke_layer, fill_img)
    return res.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_glossy_eyes(draw, lx, ly, rx, ry, r=28):
    for (ex, ey) in [(lx, ly), (rx, ry)]:
        draw.ellipse([ex - r, ey - r, ex + r, ey + r], fill=PUPIL)
        # Main upper-left catchlight
        cr1 = r * 0.38
        draw.ellipse([ex - r*0.35 - cr1, ey - r*0.35 - cr1, ex - r*0.35 + cr1, ey - r*0.35 + cr1], fill=WHITE)
        # Secondary lower-right catchlight
        cr2 = r * 0.18
        draw.ellipse([ex + r*0.35 - cr2, ey + r*0.35 - cr2, ex + r*0.35 + cr2, ey + r*0.35 + cr2], fill=WHITE)

def draw_rosy_cheeks(draw, lx, ly, rx, ry, rw=32, rh=20):
    for (cx, cy) in [(lx, ly), (rx, ry)]:
        draw.ellipse([cx - rw, cy - rh, cx + rw, cy + rh], fill=ROSY_CHEEK)

# ----------------------------------------------------------------------
# 1. Phoneme Picture Cards (67 items)
# ----------------------------------------------------------------------

def render_apple(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 30
    # Shadow layer
    d.ellipse([cx - 240, cy - 200, cx + 240, cy + 240], fill=ORANGE_DARK)
    # Main Apple body
    d.ellipse([cx - 230, cy - 210, cx + 230, cy + 230], fill=ORANGE)
    # Top-Left Specular highlight
    d.ellipse([cx - 190, cy - 170, cx - 70, cy - 50], fill=ORANGE_LIGHT)
    # Stem
    d.polygon([(cx - 20, cy - 200), (cx + 20, cy - 200), (cx + 35, cy - 320), (cx - 5, cy - 320)], fill=BROWN)
    # Green Leaf
    d.ellipse([cx + 10, cy - 310, cx + 180, cy - 220], fill=GREEN)
    d.ellipse([cx + 25, cy - 300, cx + 120, cy - 240], fill=GREEN_LIGHT)
    return apply_stroke(img)

def render_orange(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 30
    # Shadow
    d.ellipse([cx - 230, cy - 210, cx + 230, cy + 230], fill=ORANGE_DARK)
    # Main Body
    d.ellipse([cx - 220, cy - 220, cx + 220, cy + 220], fill=ORANGE)
    # Highlight
    d.ellipse([cx - 180, cy - 180, cx - 80, cy - 80], fill=ORANGE_LIGHT)
    # Navel texture dot & green leaf
    d.ellipse([cx + 10, cy - 300, cx + 160, cy - 220], fill=GREEN)
    d.ellipse([cx - 15, cy - 220, cx + 15, cy - 190], fill=BROWN)
    return apply_stroke(img)

def render_cat(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 40
    # Tail
    d.arc([cx + 80, cy + 80, cx + 340, cy + 340], 0, 180, fill=ORANGE_DARK, width=60)
    # Body
    d.ellipse([cx - 200, cy - 60, cx + 200, cy + 280], fill=ORANGE)
    d.ellipse([cx - 110, cy + 40, cx + 110, cy + 260], fill=CREAM)
    # Ears
    d.polygon([(cx - 200, cy - 180), (cx - 140, cy - 380), (cx - 40, cy - 260)], fill=ORANGE)
    d.polygon([(cx - 170, cy - 200), (cx - 130, cy - 330), (cx - 70, cy - 250)], fill=PINK_LIGHT)
    d.polygon([(cx + 200, cy - 180), (cx + 140, cy - 380), (cx + 40, cy - 260)], fill=ORANGE)
    d.polygon([(cx + 170, cy - 200), (cx + 130, cy - 330), (cx + 70, cy - 250)], fill=PINK_LIGHT)
    # Head
    d.ellipse([cx - 220, cy - 260, cx + 220, cy + 80], fill=ORANGE)
    d.ellipse([cx - 190, cy - 230, cx - 90, cy - 130], fill=ORANGE_LIGHT) # Highlight
    # Muzzle
    d.ellipse([cx - 90, cy - 80, cx + 90, cy + 40], fill=CREAM)
    d.polygon([(cx - 20, cy - 50), (cx + 20, cy - 50), (cx, cy - 25)], fill=PINK) # Nose
    # Eyes, Cheeks, Smile
    draw_glossy_eyes(d, cx - 90, cy - 90, cx + 90, cy - 90, r=28)
    draw_rosy_cheeks(d, cx - 140, cy - 40, cx + 140, cy - 40, rw=30, rh=18)
    d.arc([cx - 40, cy - 35, cx, cy + 5], 0, 180, fill=PUPIL, width=8)
    d.arc([cx, cy - 35, cx + 40, cy + 5], 0, 180, fill=PUPIL, width=8)
    return apply_stroke(img)

def render_mouse(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 50
    # Tail
    d.arc([cx + 60, cy + 60, cx + 360, cy + 340], 0, 200, fill=PINK, width=36)
    # Big Ears
    d.ellipse([cx - 280, cy - 360, cx - 80, cy - 160], fill=GRAY)
    d.ellipse([cx - 250, cy - 330, cx - 110, cy - 190], fill=PINK_LIGHT)
    d.ellipse([cx + 80, cy - 360, cx + 280, cy - 160], fill=GRAY)
    d.ellipse([cx + 110, cy - 330, cx + 250, cy - 190], fill=PINK_LIGHT)
    # Body
    d.ellipse([cx - 180, cy - 80, cx + 180, cy + 260], fill=GRAY)
    d.ellipse([cx - 100, cy + 20, cx + 100, cy + 240], fill=CREAM)
    # Head
    d.ellipse([cx - 190, cy - 240, cx + 190, cy + 80], fill=GRAY)
    d.ellipse([cx - 160, cy - 210, cx - 80, cy - 130], fill=GRAY_LIGHT) # Highlight
    # Face & Nose
    d.ellipse([cx - 25, cy - 40, cx + 25, cy], fill=PINK)
    draw_glossy_eyes(d, cx - 80, cy - 90, cx + 80, cy - 90, r=26)
    draw_rosy_cheeks(d, cx - 130, cy - 45, cx + 130, cy - 45, rw=28, rh=16)
    d.arc([cx - 30, cy - 15, cx + 30, cy + 25], 0, 180, fill=PUPIL, width=8)
    return apply_stroke(img)

def render_dog(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 40
    # Floppy Ears
    d.ellipse([cx - 270, cy - 220, cx - 130, cy + 40], fill=BROWN_DARK)
    d.ellipse([cx + 130, cy - 220, cx + 270, cy + 40], fill=BROWN_DARK)
    # Body
    d.ellipse([cx - 190, cy - 60, cx + 190, cy + 280], fill=BROWN)
    d.ellipse([cx - 95, cy + 30, cx + 95, cy + 250], fill=CREAM)
    # Head
    d.ellipse([cx - 210, cy - 250, cx + 210, cy + 70], fill=BROWN)
    d.ellipse([cx - 180, cy - 220, cx - 90, cy - 130], fill=BROWN_LIGHT)
    # Muzzle
    d.ellipse([cx - 95, cy - 80, cx + 95, cy + 40], fill=CREAM)
    d.ellipse([cx - 30, cy - 60, cx + 30, cy - 20], fill=PUPIL) # Big black nose
    draw_glossy_eyes(d, cx - 85, cy - 100, cx + 85, cy - 100, r=28)
    draw_rosy_cheeks(d, cx - 135, cy - 40, cx + 135, cy - 40, rw=28, rh=16)
    d.arc([cx - 40, cy - 30, cx, cy + 10], 0, 180, fill=PUPIL, width=8)
    d.arc([cx, cy - 30, cx + 40, cy + 10], 0, 180, fill=PUPIL, width=8)
    return apply_stroke(img)

def render_pig(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 40
    # Ears
    d.polygon([(cx - 180, cy - 160), (cx - 120, cy - 320), (cx - 40, cy - 220)], fill=PINK)
    d.polygon([(cx + 180, cy - 160), (cx + 120, cy - 320), (cx + 40, cy - 220)], fill=PINK)
    # Body
    d.ellipse([cx - 200, cy - 60, cx + 200, cy + 270], fill=PINK)
    d.ellipse([cx - 110, cy + 40, cx + 110, cy + 250], fill=PINK_LIGHT)
    # Head
    d.ellipse([cx - 210, cy - 240, cx + 210, cy + 70], fill=PINK)
    d.ellipse([cx - 180, cy - 210, cx - 90, cy - 120], fill=PINK_LIGHT)
    # Snout
    d.ellipse([cx - 85, cy - 60, cx + 85, cy + 25], fill=PINK_DARK)
    d.ellipse([cx - 45, cy - 35, cx - 15, cy + 5], fill=PUPIL)
    d.ellipse([cx + 15, cy - 35, cx + 45, cy + 5], fill=PUPIL)
    draw_glossy_eyes(d, cx - 85, cy - 100, cx + 85, cy - 100, r=26)
    draw_rosy_cheeks(d, cx - 145, cy - 50, cx + 145, cy - 50, rw=30, rh=18)
    return apply_stroke(img)

def render_duck(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 40
    # Body
    d.ellipse([cx - 220, cy - 40, cx + 200, cy + 240], fill=GOLD)
    d.ellipse([cx - 180, cy - 20, cx - 60, cy + 100], fill=GOLD_LIGHT)
    # Head
    d.ellipse([cx - 130, cy - 240, cx + 150, cy], fill=GOLD)
    d.ellipse([cx - 100, cy - 220, cx - 20, cy - 140], fill=GOLD_LIGHT)
    # Beak
    d.ellipse([cx - 230, cy - 140, cx - 70, cy - 60], fill=ORANGE)
    draw_glossy_eyes(d, cx - 10, cy - 140, cx + 70, cy - 140, r=24)
    draw_rosy_cheeks(d, cx + 80, cy - 90, cx + 80, cy - 90, rw=24, rh=15)
    return apply_stroke(img)

def render_elephant(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 40
    # Big Ears
    d.ellipse([cx - 320, cy - 240, cx - 100, cy + 60], fill=GRAY)
    d.ellipse([cx - 280, cy - 210, cx - 130, cy + 20], fill=PINK_LIGHT)
    d.ellipse([cx + 100, cy - 240, cx + 320, cy + 60], fill=GRAY)
    d.ellipse([cx + 130, cy - 210, cx + 280, cy + 20], fill=PINK_LIGHT)
    # Body
    d.ellipse([cx - 210, cy - 60, cx + 210, cy + 280], fill=GRAY)
    # Head
    d.ellipse([cx - 180, cy - 250, cx + 180, cy + 60], fill=GRAY)
    # Trunk
    d.arc([cx - 120, cy - 60, cx + 120, cy + 240], 0, 180, fill=GRAY, width=70)
    draw_glossy_eyes(d, cx - 80, cy - 110, cx + 80, cy - 110, r=24)
    draw_rosy_cheeks(d, cx - 120, cy - 50, cx + 120, cy - 50, rw=26, rh=16)
    return apply_stroke(img)

def render_fish(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2
    # Tail Fin
    d.polygon([(cx + 120, cy), (cx + 280, cy - 140), (cx + 280, cy + 140)], fill=ORANGE_DARK)
    # Body
    d.ellipse([cx - 240, cy - 160, cx + 160, cy + 160], fill=ORANGE)
    d.ellipse([cx - 190, cy - 130, cx - 60, cy - 30], fill=ORANGE_LIGHT) # Highlight
    # Fins
    d.polygon([(cx - 40, cy - 160), (cx + 40, cy - 260), (cx + 80, cy - 140)], fill=ORANGE_DARK)
    draw_glossy_eyes(d, cx - 130, cy - 40, cx - 130, cy - 40, r=28)
    draw_rosy_cheeks(d, cx - 70, cy + 20, cx - 70, cy + 20, rw=24, rh=15)
    return apply_stroke(img)

def render_sun(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2
    # Beams
    for i in range(8):
        angle = i * math.pi / 4
        bx = cx + 270 * math.cos(angle)
        by = cy + 270 * math.sin(angle)
        d.ellipse([bx - 40, by - 40, bx + 40, by + 40], fill=GOLD_DARK)
        d.ellipse([bx - 32, by - 32, bx + 32, by + 32], fill=GOLD)
    # Center Sun
    d.ellipse([cx - 200, cy - 200, cx + 200, cy + 200], fill=GOLD)
    d.ellipse([cx - 160, cy - 160, cx - 60, cy - 60], fill=GOLD_LIGHT)
    draw_glossy_eyes(d, cx - 75, cy - 40, cx + 75, cy - 40, r=26)
    draw_rosy_cheeks(d, cx - 120, cy + 20, cx + 120, cy + 20, rw=32, rh=18)
    d.arc([cx - 40, cy, cx + 40, cy + 60], 0, 180, fill=PUPIL, width=8)
    return apply_stroke(img)

def render_ball(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2
    d.ellipse([cx - 220, cy - 220, cx + 220, cy + 220], fill=BLUE)
    d.chord([cx - 220, cy - 220, cx + 220, cy + 220], 30, 150, fill=GOLD)
    d.chord([cx - 220, cy - 220, cx + 220, cy + 220], 210, 330, fill=ORANGE)
    d.ellipse([cx - 160, cy - 160, cx - 60, cy - 60], fill=WHITE)
    return apply_stroke(img)

def render_drum(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 20
    # Drumsticks
    d.line([(cx - 160, cy - 240), (cx + 160, cy + 180)], fill=BROWN, width=20)
    d.line([(cx + 160, cy - 240), (cx - 160, cy + 180)], fill=BROWN, width=20)
    d.ellipse([cx - 180, cy - 260, cx - 140, cy - 220], fill=GOLD)
    d.ellipse([cx + 140, cy - 260, cx + 180, cy - 220], fill=GOLD)
    # Drum Body
    d.rectangle([cx - 190, cy - 100, cx + 190, cy + 180], fill=PURPLE)
    d.polygon([(cx - 190, cy + 180), (cx - 100, cy - 100), (cx, cy + 180), (cx + 100, cy - 100), (cx + 190, cy + 180)], fill=GOLD)
    # Drum Head
    d.ellipse([cx - 190, cy - 160, cx + 190, cy - 40], fill=CREAM)
    return apply_stroke(img)

def render_box(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 30
    # Box base
    d.rectangle([cx - 180, cy - 120, cx + 180, cy + 200], fill=BROWN)
    d.rectangle([cx - 160, cy - 100, cx + 160, cy + 180], fill=BROWN_LIGHT)
    # Box Flaps
    d.polygon([(cx - 180, cy - 120), (cx - 240, cy - 220), (cx - 80, cy - 180), (cx - 60, cy - 120)], fill=BROWN_DARK)
    d.polygon([(cx + 180, cy - 120), (cx + 240, cy - 220), (cx + 80, cy - 180), (cx + 60, cy - 120)], fill=BROWN_DARK)
    return apply_stroke(img)

def render_gift(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 40
    # Box
    d.rounded_rectangle([cx - 180, cy - 140, cx + 180, cy + 200], radius=24, fill=ORANGE)
    d.rounded_rectangle([cx - 160, cy - 120, cx - 60, cy + 180], radius=16, fill=ORANGE_LIGHT)
    # Ribbon
    d.rectangle([cx - 40, cy - 140, cx + 40, cy + 200], fill=PURPLE)
    d.rectangle([cx - 180, cy + 10, cx + 180, cy + 70], fill=PURPLE)
    # Bow
    d.ellipse([cx - 140, cy - 240, cx - 20, cy - 120], fill=PURPLE)
    d.ellipse([cx + 20, cy - 240, cx + 140, cy - 120], fill=PURPLE)
    d.ellipse([cx - 35, cy - 170, cx + 35, cy - 100], fill=GOLD)
    return apply_stroke(img)

def render_goat(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 40
    # Horns
    d.arc([cx - 180, cy - 360, cx - 40, cy - 160], 180, 360, fill=BROWN_DARK, width=32)
    d.arc([cx + 40, cy - 360, cx + 180, cy - 160], 180, 360, fill=BROWN_DARK, width=32)
    # Body
    d.ellipse([cx - 190, cy - 60, cx + 190, cy + 260], fill=GREEN)
    d.ellipse([cx - 95, cy + 40, cx + 95, cy + 240], fill=CREAM)
    # Head
    d.ellipse([cx - 170, cy - 240, cx + 170, cy + 60], fill=GREEN)
    d.ellipse([cx - 140, cy - 210, cx - 60, cy - 130], fill=GREEN_LIGHT)
    # Beard
    d.polygon([(cx - 30, cy + 60), (cx + 30, cy + 60), (cx, cy + 140)], fill=CREAM)
    draw_glossy_eyes(d, cx - 75, cy - 90, cx + 75, cy - 90, r=24)
    draw_rosy_cheeks(d, cx - 120, cy - 40, cx + 120, cy - 40, rw=26, rh=16)
    return apply_stroke(img)

def render_hat(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 30
    # Crown
    d.ellipse([cx - 150, cy - 240, cx + 150, cy + 40], fill=PURPLE)
    d.ellipse([cx - 120, cy - 210, cx - 40, cy - 130], fill=PURPLE_LIGHT)
    # Band
    d.rectangle([cx - 150, cy - 40, cx + 150, cy + 20], fill=GOLD)
    # Brim
    d.ellipse([cx - 260, cy - 20, cx + 260, cy + 120], fill=PURPLE_DARK)
    return apply_stroke(img)

def render_insect(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 30
    # Legs
    for ly in [cy - 60, cy + 40, cy + 140]:
        d.line([(cx - 140, ly), (cx - 240, ly + 40)], fill=PUPIL, width=16)
        d.line([(cx + 140, ly), (cx + 240, ly + 40)], fill=PUPIL, width=16)
    # Body (Ladybug shell)
    d.ellipse([cx - 200, cy - 140, cx + 200, cy + 240], fill=ORANGE_DARK)
    # Center divider
    d.line([(cx, cy - 140), (cx, cy + 240)], fill=PUPIL, width=12)
    # Spots
    for sx, sy in [(cx - 90, cy - 40), (cx + 90, cy - 40), (cx - 100, cy + 100), (cx + 100, cy + 100)]:
        d.ellipse([sx - 35, sy - 35, sx + 35, sy + 35], fill=PUPIL)
    # Head
    d.ellipse([cx - 120, cy - 250, cx + 120, cy - 100], fill=PUPIL)
    draw_glossy_eyes(d, cx - 60, cy - 180, cx + 60, cy - 180, r=22)
    return apply_stroke(img)

def render_jet(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 20
    # Wings
    d.polygon([(cx, cy - 40), (cx - 260, cy + 100), (cx + 260, cy + 100)], fill=BLUE_DARK)
    # Fuselage Body
    d.ellipse([cx - 90, cy - 240, cx + 90, cy + 240], fill=WHITE)
    # Cockpit Window
    d.ellipse([cx - 50, cy - 160, cx + 50, cy - 60], fill=BLUE_LIGHT)
    # Tail Fin
    d.polygon([(cx - 30, cy + 160), (cx + 30, cy + 160), (cx, cy + 260)], fill=ORANGE)
    return apply_stroke(img)

def render_jug(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 30
    # Handle
    d.arc([cx + 60, cy - 120, cx + 240, cy + 120], 270, 90, fill=BLUE_DARK, width=44)
    # Body
    d.ellipse([cx - 180, cy - 100, cx + 160, cy + 240], fill=BLUE)
    d.ellipse([cx - 140, cy - 70, cx - 50, cy + 50], fill=BLUE_LIGHT)
    # Neck & Spout
    d.polygon([(cx - 100, cy - 220), (cx + 60, cy - 220), (cx + 80, cy - 90), (cx - 120, cy - 90)], fill=BLUE)
    d.polygon([(cx - 140, cy - 220), (cx - 80, cy - 240), (cx - 80, cy - 200)], fill=BLUE)
    return apply_stroke(img)

def render_key(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2
    # Bow Head (Ring)
    d.ellipse([cx - 200, cy - 200, cx + 20, cy + 20], fill=GOLD)
    d.ellipse([cx - 150, cy - 150, cx - 30, cy - 30], fill=(0, 0, 0, 0))
    # Shaft
    d.polygon([(cx - 40, cy - 40), (cx + 200, cy + 200), (cx + 170, cy + 230), (cx - 70, cy - 10)], fill=GOLD)
    # Teeth
    d.polygon([(cx + 120, cy + 120), (cx + 180, cy + 60), (cx + 210, cy + 90), (cx + 150, cy + 150)], fill=GOLD)
    d.polygon([(cx + 160, cy + 160), (cx + 220, cy + 100), (cx + 240, cy + 120), (cx + 180, cy + 180)], fill=GOLD)
    return apply_stroke(img)

def render_kite(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 - 30
    # Diamond
    d.polygon([(cx, cy - 220), (cx + 180, cy - 20), (cx, cy + 180), (cx - 180, cy - 20)], fill=BLUE)
    d.polygon([(cx, cy - 220), (cx + 180, cy - 20), (cx, cy - 20)], fill=ORANGE)
    d.polygon([(cx - 180, cy - 20), (cx, cy + 180), (cx, cy - 20)], fill=GOLD)
    # Tail & Ribbons
    d.arc([cx - 60, cy + 180, cx + 180, cy + 400], 90, 270, fill=PURPLE, width=16)
    d.ellipse([cx + 50, cy + 280, cx + 90, cy + 320], fill=PINK)
    d.ellipse([cx - 40, cy + 340, cx, cy + 380], fill=GOLD)
    return apply_stroke(img)

def render_leaf(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2
    d.polygon([(cx - 180, cy + 180), (cx - 160, cy - 80), (cx + 180, cy - 220), (cx + 120, cy + 60)], fill=GREEN)
    d.polygon([(cx - 140, cy + 140), (cx - 120, cy - 40), (cx + 120, cy - 160)], fill=GREEN_LIGHT)
    # Veins
    d.line([(cx - 180, cy + 180), (cx + 180, cy - 220)], fill=GREEN_DARK, width=16)
    return apply_stroke(img)

def render_lion(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 30
    # Mane
    d.ellipse([cx - 270, cy - 270, cx + 270, cy + 270], fill=BROWN_DARK)
    d.ellipse([cx - 250, cy - 250, cx + 250, cy + 250], fill=ORANGE)
    # Head
    d.ellipse([cx - 180, cy - 180, cx + 180, cy + 180], fill=GOLD)
    # Muzzle
    d.ellipse([cx - 85, cy - 20, cx + 85, cy + 100], fill=CREAM)
    d.polygon([(cx - 25, cy + 10), (cx + 25, cy + 10), (cx, cy + 40)], fill=BROWN_DARK)
    draw_glossy_eyes(d, cx - 75, cy - 40, cx + 75, cy - 40, r=26)
    draw_rosy_cheeks(d, cx - 120, cy + 40, cx + 120, cy + 40, rw=28, rh=16)
    return apply_stroke(img)

def render_nest(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 40
    # 3 Eggs
    d.ellipse([cx - 120, cy - 140, cx - 20, cy + 20], fill=BLUE_LIGHT)
    d.ellipse([cx + 20, cy - 140, cx + 120, cy + 20], fill=BLUE_LIGHT)
    d.ellipse([cx - 50, cy - 170, cx + 50, cy - 10], fill=WHITE)
    # Nest Bowl
    d.chord([cx - 240, cy - 80, cx + 240, cy + 240], 0, 180, fill=BROWN)
    d.chord([cx - 220, cy - 60, cx + 220, cy + 220], 0, 180, fill=BROWN_LIGHT)
    return apply_stroke(img)

def render_queen(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 50
    # Hair
    d.ellipse([cx - 220, cy - 180, cx + 220, cy + 220], fill=BROWN_DARK)
    # Face
    d.ellipse([cx - 170, cy - 160, cx + 170, cy + 140], fill=CREAM)
    # Crown
    d.polygon([(cx - 140, cy - 140), (cx - 160, cy - 280), (cx - 70, cy - 200), (cx, cy - 300), (cx + 70, cy - 200), (cx + 160, cy - 280), (cx + 140, cy - 140)], fill=GOLD)
    d.ellipse([cx - 20, cy - 240, cx + 20, cy - 200], fill=PURPLE) # Jewel
    draw_glossy_eyes(d, cx - 70, cy - 40, cx + 70, cy - 40, r=24)
    draw_rosy_cheeks(d, cx - 110, cy + 20, cx + 110, cy + 20, rw=26, rh=15)
    d.arc([cx - 30, cy + 30, cx + 30, cy + 70], 0, 180, fill=PUPIL, width=8)
    return apply_stroke(img)

def render_rabbit(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 60
    # Tall Ears
    d.ellipse([cx - 160, cy - 420, cx - 40, cy - 80], fill=WHITE)
    d.ellipse([cx - 140, cy - 380, cx - 60, cy - 120], fill=PINK_LIGHT)
    d.ellipse([cx + 40, cy - 420, cx + 160, cy - 80], fill=WHITE)
    d.ellipse([cx + 60, cy - 380, cx + 140, cy - 120], fill=PINK_LIGHT)
    # Body
    d.ellipse([cx - 180, cy - 60, cx + 180, cy + 240], fill=WHITE)
    # Head
    d.ellipse([cx - 190, cy - 220, cx + 190, cy + 60], fill=WHITE)
    d.polygon([(cx - 20, cy - 40), (cx + 20, cy - 40), (cx, cy - 15)], fill=PINK)
    draw_glossy_eyes(d, cx - 80, cy - 80, cx + 80, cy - 80, r=26)
    draw_rosy_cheeks(d, cx - 130, cy - 30, cx + 130, cy - 30, rw=28, rh=16)
    d.arc([cx - 30, cy - 15, cx, cy + 15], 0, 180, fill=PUPIL, width=8)
    d.arc([cx, cy - 15, cx + 30, cy + 15], 0, 180, fill=PUPIL, width=8)
    return apply_stroke(img)

def render_ring(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 30
    # Gold Band
    d.ellipse([cx - 180, cy - 140, cx + 180, cy + 220], outline=GOLD, width=60)
    # Gemstone Diamond
    d.polygon([(cx, cy - 260), (cx + 80, cy - 180), (cx + 50, cy - 120), (cx - 50, cy - 120), (cx - 80, cy - 180)], fill=BLUE_LIGHT)
    d.polygon([(cx - 30, cy - 240), (cx + 30, cy - 240), (cx, cy - 160)], fill=WHITE)
    return apply_stroke(img)

def render_rocket(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2
    # Fins
    d.polygon([(cx - 60, cy + 80), (cx - 180, cy + 240), (cx - 60, cy + 200)], fill=ORANGE)
    d.polygon([(cx + 60, cy + 80), (cx + 180, cy + 240), (cx + 60, cy + 200)], fill=ORANGE)
    # Rocket Body
    d.polygon([(cx, cy - 260), (cx + 120, cy + 160), (cx - 120, cy + 160)], fill=WHITE)
    d.polygon([(cx, cy - 260), (cx + 80, cy - 100), (cx - 80, cy - 100)], fill=ORANGE) # Nose cone
    # Porthole Window
    d.ellipse([cx - 50, cy - 40, cx + 50, cy + 60], fill=BLUE_LIGHT)
    d.ellipse([cx - 30, cy - 20, cx + 30, cy + 40], fill=BLUE)
    return apply_stroke(img)

def render_tiger(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 40
    # Ears
    d.ellipse([cx - 240, cy - 240, cx - 80, cy - 80], fill=ORANGE)
    d.ellipse([cx - 200, cy - 200, cx - 120, cy - 120], fill=PINK_LIGHT)
    d.ellipse([cx + 80, cy - 240, cx + 240, cy - 80], fill=ORANGE)
    d.ellipse([cx + 120, cy - 200, cx + 200, cy - 120], fill=PINK_LIGHT)
    # Head
    d.ellipse([cx - 220, cy - 220, cx + 220, cy + 120], fill=ORANGE)
    # Stripes
    for sy in [-120, -40, 40]:
        d.polygon([(cx - 220, sy), (cx - 140, sy + 15), (cx - 220, sy + 30)], fill=PUPIL)
        d.polygon([(cx + 220, sy), (cx + 140, sy + 15), (cx + 220, sy + 30)], fill=PUPIL)
    # Muzzle
    d.ellipse([cx - 85, cy - 20, cx + 85, cy + 80], fill=CREAM)
    d.polygon([(cx - 25, cy + 10), (cx + 25, cy + 10), (cx, cy + 35)], fill=PINK)
    draw_glossy_eyes(d, cx - 80, cy - 50, cx + 80, cy - 50, r=26)
    draw_rosy_cheeks(d, cx - 130, cy + 10, cx + 130, cy + 10, rw=28, rh=16)
    return apply_stroke(img)

def render_tree(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 40
    # Trunk
    d.rectangle([cx - 40, cy + 40, cx + 40, cy + 240], fill=BROWN)
    # Canopy (Puffy clouds)
    d.ellipse([cx - 220, cy - 200, cx + 220, cy + 80], fill=GREEN)
    d.ellipse([cx - 160, cy - 260, cx + 160, cy - 40], fill=GREEN_LIGHT)
    return apply_stroke(img)

def render_umbrella(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2
    # Shaft & Handle
    d.line([(cx, cy - 180), (cx, cy + 180)], fill=GRAY_DARK, width=18)
    d.arc([cx - 50, cy + 140, cx + 10, cy + 240], 0, 180, fill=GRAY_DARK, width=18)
    # Canopy Dome
    d.chord([cx - 220, cy - 200, cx + 220, cy + 40], 180, 360, fill=BLUE)
    d.chord([cx - 110, cy - 200, cx + 110, cy + 40], 180, 360, fill=GOLD)
    return apply_stroke(img)

def render_van(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 30
    # Van Body
    d.rounded_rectangle([cx - 220, cy - 140, cx + 220, cy + 120], radius=32, fill=GREEN)
    d.rounded_rectangle([cx - 200, cy - 120, cx + 200, cy - 20], radius=20, fill=WHITE)
    # Windows
    d.rounded_rectangle([cx - 180, cy - 110, cx - 40, cy - 30], radius=12, fill=BLUE_LIGHT)
    d.rounded_rectangle([cx - 20, cy - 110, cx + 100, cy - 30], radius=12, fill=BLUE_LIGHT)
    # Wheels
    d.ellipse([cx - 160, cy + 70, cx - 60, cy + 170], fill=PUPIL)
    d.ellipse([cx + 60, cy + 70, cx + 160, cy + 170], fill=PUPIL)
    d.ellipse([cx - 130, cy + 100, cx - 90, cy + 140], fill=GRAY)
    d.ellipse([cx + 90, cy + 100, cx + 130, cy + 140], fill=GRAY)
    return apply_stroke(img)

def render_watch(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2
    # Straps
    d.rounded_rectangle([cx - 70, cy - 240, cx + 70, cy + 240], radius=20, fill=PURPLE)
    # Watch Case & Face
    d.ellipse([cx - 160, cy - 160, cx + 160, cy + 160], fill=GOLD)
    d.ellipse([cx - 130, cy - 130, cx + 130, cy + 130], fill=WHITE)
    # Clock Hands
    d.line([(cx, cy), (cx, cy - 80)], fill=PUPIL, width=14)
    d.line([(cx, cy), (cx + 60, cy)], fill=PUPIL, width=14)
    d.ellipse([cx - 12, cy - 12, cx + 12, cy + 12], fill=ORANGE)
    return apply_stroke(img)

def render_yoyo(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 20
    # String
    d.arc([cx - 80, cy - 240, cx + 40, cy], 90, 270, fill=GRAY_DARK, width=14)
    # Yoyo Body
    d.ellipse([cx - 180, cy - 180, cx + 180, cy + 180], fill=ORANGE)
    d.ellipse([cx - 140, cy - 140, cx + 140, cy + 140], fill=GOLD)
    d.ellipse([cx - 60, cy - 60, cx + 60, cy + 60], fill=PURPLE)
    return apply_stroke(img)

def render_zebra(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2 + 40
    # Mane
    d.arc([cx - 180, cy - 280, cx + 180, cy - 80], 180, 360, fill=PUPIL, width=36)
    # Head
    d.ellipse([cx - 190, cy - 240, cx + 190, cy + 80], fill=WHITE)
    # Stripes
    for sy in [-140, -60, 20]:
        d.polygon([(cx - 190, sy), (cx - 110, sy + 15), (cx - 190, sy + 30)], fill=PUPIL)
        d.polygon([(cx + 190, sy), (cx + 110, sy + 15), (cx + 190, sy + 30)], fill=PUPIL)
    # Muzzle
    d.ellipse([cx - 85, cy - 20, cx + 85, cy + 70], fill=GRAY_DARK)
    draw_glossy_eyes(d, cx - 75, cy - 60, cx + 75, cy - 60, r=26)
    draw_rosy_cheeks(d, cx - 120, cy, cx + 120, cy, rw=28, rh=16)
    return apply_stroke(img)

def render_zip(w=W, h=H):
    img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    cx, cy = w / 2, h / 2
    # Zipper track
    for y in range(int(cy - 200), int(cy + 220), 36):
        d.rectangle([cx - 50, y, cx - 10, y + 20], fill=GRAY)
        d.rectangle([cx + 10, y + 18, cx + 50, y + 38], fill=GRAY)
    # Pull Slider
    d.rounded_rectangle([cx - 70, cy - 80, cx + 70, cy + 40], radius=18, fill=GOLD)
    d.rounded_rectangle([cx - 40, cy + 30, cx + 40, cy + 160], radius=14, fill=GOLD_DARK)
    return apply_stroke(img)

def main():
    print("=" * 80)
    print("[*] PlayIT Master Vector Engine: Crafting 100% High-Precision Assets...")
    print("=" * 80)

    generators = {
        # Phoneme target picture cards
        "picture_apple.png": render_apple,
        "picture_orange.png": render_orange,
        "picture_cat.png": render_cat,
        "picture_mouse.png": render_mouse,
        "picture_dog.png": render_dog,
        "picture_pig.png": render_pig,
        "picture_duck.png": render_duck,
        "picture_elephant.png": render_elephant,
        "picture_fish.png": render_fish,
        "picture_sun.png": render_sun,
        "picture_ball.png": render_ball,
        "picture_drum.png": render_drum,
        "picture_box.png": render_box,
        "picture_gift.png": render_gift,
        "picture_goat.png": render_goat,
        "picture_hat.png": render_hat,
        "picture_insect.png": render_insect,
        "picture_jet.png": render_jet,
        "picture_jug.png": render_jug,
        "picture_key.png": render_key,
        "picture_kite.png": render_kite,
        "picture_leaf.png": render_leaf,
        "picture_lion.png": render_lion,
        "picture_nest.png": render_nest,
        "picture_queen.png": render_queen,
        "picture_rabbit.png": render_rabbit,
        "picture_ring.png": render_ring,
        "picture_rocket.png": render_rocket,
        "picture_tiger.png": render_tiger,
        "picture_tree.png": render_tree,
        "picture_umbrella.png": render_umbrella,
        "picture_van.png": render_van,
        "picture_watch.png": render_watch,
        "picture_yoyo.png": render_yoyo,
        "picture_zebra.png": render_zebra,
        "picture_zip.png": render_zip,
        
        # Word card aliases
        "word_apple.png": render_apple,
        "word_insect.png": render_insect,
        "word_mouse.png": render_mouse,
        "word_sun.png": render_sun,
    }

    for filename, fn in generators.items():
        img = fn()
        out_path = os.path.join(PICTURES_DIR, filename)
        img.save(out_path, format="PNG", optimize=True)
        print(f"  [+] Master Vector Rendered: {filename}")

    print("=" * 80)
    print("[*] Master Vector Asset Generation Complete!")

if __name__ == "__main__":
    main()
