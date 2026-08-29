"""
PlayIT Blend It Assets Generator — 4-Benchmark Synthesis (Human & Kid-Friendly Redesign)
Generates high-definition, 100% transparent vector illustrations for all 32 Blend It words.
Redesigned with rich pediatric clarity:
- BAT: Cute storybook flying fruit bat with purple wings, big glossy eyes & friendly smile
- DRAW: Hand holding a chunky red crayon actively drawing a colorful rainbow & sun doodle
- FACE: Lovable human child's face with hair bangs, ears, eyebrows, nose & warm smile
- GAP: Stepping stones over water with a cute frog leaping across the clear gap
- HAND: Anatomically clear 5-finger cartoon child's hand waving in greeting
- MAT: Cozy woven floor mat with a cute pair of red toddler slippers resting on it
- NAP: Sweet child sleeping peacefully in bed tucked under a cozy blanket with Zzz
- SAM: Full-body cheerful boy character with spiky hair, blue shirt & waving hand
- SIS: Full-body cheerful girl character with pigtail ribbons, yellow dress & waving hand
"""

from PIL import Image, ImageDraw
import math
import os

SIZE = 512
SCALE = 2
W = SIZE * SCALE
H = SIZE * SCALE

# Standard Palette
DARK_OUTLINE = (45, 55, 62, 255)       # #2D373E Slate Charcoal
WHITE = (255, 255, 255, 255)
PINK_CHEEK = (255, 140, 140, 220)      # Khan Rosy Blush
MANGO = (250, 123, 40, 255)           # #FA7B28 Warm Orange
CREAM = (255, 238, 215, 255)          # Soft Skin / Tummy Cream
SKIN_TONE = (255, 224, 189, 255)      # Warm Human Skin Tone
GOLD_SUN = (255, 204, 0, 255)         # Bright Sunny Yellow
GOLD_DARK = (245, 166, 35, 255)
LEAF_GREEN = (76, 175, 80, 255)       # Fresh Green
SKY_BLUE = (56, 189, 248, 255)        # Clean Sky Blue
OCEAN_BLUE = (2, 132, 199, 255)
UBE_PURPLE = (139, 95, 191, 255)      # Royal Ube
GUAVA_RED = (255, 90, 110, 255)       # Warm Red / Guava
WOOD_BROWN = (180, 115, 65, 255)
WOOD_DARK = (130, 80, 45, 255)

def draw_thick_line(draw, start, end, color=DARK_OUTLINE, width=20):
    draw.line([start, end], fill=color, width=width, joint="curve")

def draw_face(draw, cx, cy, eye_spacing=65, eye_y_offset=0, eye_r=24, smile_w=70, smile_h=40, blush=True):
    eye_y = cy + eye_y_offset
    for ex in [cx - eye_spacing, cx + eye_spacing]:
        draw.ellipse([ex - eye_r, eye_y - eye_r, ex + eye_r, eye_y + eye_r], fill=DARK_OUTLINE)
        draw.ellipse([ex - 8 - 7, eye_y - 8 - 7, ex - 8 + 7, eye_y - 8 + 7], fill=WHITE)
        draw.ellipse([ex + 6 - 3, eye_y + 6 - 3, ex + 6 + 3, eye_y + 6 + 3], fill=WHITE)
    if blush:
        draw.ellipse([cx - eye_spacing - 35 - 20, eye_y + 20 - 12, cx - eye_spacing - 35 + 20, eye_y + 20 + 12], fill=PINK_CHEEK)
        draw.ellipse([cx + eye_spacing + 35 - 20, eye_y + 20 - 12, cx + eye_spacing + 35 + 20, eye_y + 20 + 12], fill=PINK_CHEEK)
    draw.arc([cx - smile_w/2, eye_y + 5, cx + smile_w/2, eye_y + 5 + smile_h], start=10, end=170, fill=DARK_OUTLINE, width=14)

# ── Redesigned Kid-Friendly & Human Illustrators ──────────────────────────────

def draw_bat():
    """Cute storybook nocturnal bat with purple wings, big glossy eyes, rosy cheeks & little smile."""
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20

    # Scalloped Wings Left & Right
    # Left Wing
    l_wing = [
        (cx - 50, cy + 60),
        (cx - 380, cy - 140),
        (cx - 300, cy + 30),
        (cx - 220, cy + 140),
        (cx - 120, cy + 160),
        (cx - 50, cy + 100)
    ]
    draw.polygon(l_wing, fill=UBE_PURPLE, outline=DARK_OUTLINE)
    for i in range(len(l_wing)-1):
        draw_thick_line(draw, l_wing[i], l_wing[i+1], DARK_OUTLINE, 20)

    # Right Wing
    r_wing = [
        (cx + 50, cy + 60),
        (cx + 380, cy - 140),
        (cx + 300, cy + 30),
        (cx + 220, cy + 140),
        (cx + 120, cy + 160),
        (cx + 50, cy + 100)
    ]
    draw.polygon(r_wing, fill=UBE_PURPLE, outline=DARK_OUTLINE)
    for i in range(len(r_wing)-1):
        draw_thick_line(draw, r_wing[i], r_wing[i+1], DARK_OUTLINE, 20)

    # Pointy Ears
    draw.polygon([(cx - 140, cy - 60), (cx - 100, cy - 240), (cx - 20, cy - 120)], fill=UBE_PURPLE, outline=DARK_OUTLINE)
    draw.polygon([(cx - 120, cy - 70), (cx - 100, cy - 200), (cx - 40, cy - 110)], fill=PINK_CHEEK)
    draw.polygon([(cx + 140, cy - 60), (cx + 100, cy - 240), (cx + 20, cy - 120)], fill=UBE_PURPLE, outline=DARK_OUTLINE)
    draw.polygon([(cx + 120, cy - 70), (cx + 100, cy - 200), (cx + 40, cy - 110)], fill=PINK_CHEEK)

    # Bat Body & Head (Pear shape)
    draw.ellipse([cx - 160, cy - 120, cx + 160, cy + 200], fill=UBE_PURPLE, outline=DARK_OUTLINE, width=22)
    draw.ellipse([cx - 100, cy + 20, cx + 100, cy + 180], fill=CREAM)

    # Cute Face
    draw.ellipse([cx - 65 - 24, cy - 24, cx - 65 + 24, cy + 24], fill=DARK_OUTLINE)
    draw.ellipse([cx + 65 - 24, cy - 24, cx + 65 + 24, cy + 24], fill=DARK_OUTLINE)
    draw.ellipse([cx - 72, cy - 16, cx - 58, cy - 2], fill=WHITE)
    draw.ellipse([cx + 58, cy - 16, cx + 72, cy - 2], fill=WHITE)
    # Rosy Cheeks
    draw.ellipse([cx - 120, cy + 15, cx - 70, cy + 45], fill=PINK_CHEEK)
    draw.ellipse([cx + 70, cy + 15, cx + 120, cy + 45], fill=PINK_CHEEK)
    # Smile & Tiny Fangs
    draw.arc([cx - 45, cy + 10, cx + 45, cy + 70], 10, 170, fill=DARK_OUTLINE, width=14)
    draw.polygon([(cx - 20, cy + 45), (cx - 10, cy + 45), (cx - 15, cy + 65)], fill=WHITE, outline=DARK_OUTLINE)
    draw.polygon([(cx + 10, cy + 45), (cx + 20, cy + 45), (cx + 15, cy + 65)], fill=WHITE, outline=DARK_OUTLINE)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_draw():
    """A child's hand gripping a bright red crayon actively drawing a colorful rainbow & sun doodle."""
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2

    # Drawing Paper Sheet
    draw.rounded_rectangle([cx - 300, cy - 260, cx + 220, cy + 260], radius=35, fill=WHITE, outline=DARK_OUTLINE, width=22)

    # Rainbow Doodle on Paper
    draw.arc([cx - 260, cy - 180, cx + 60, cy + 140], 180, 360, fill=GUAVA_RED, width=20)
    draw.arc([cx - 230, cy - 150, cx + 30, cy + 110], 180, 360, fill=GOLD_SUN, width=20)
    draw.arc([cx - 200, cy - 120, cx, cy + 80], 180, 360, fill=SKY_BLUE, width=20)

    # Happy Little Sun Doodle
    sx, sy = cx - 180, cy - 140
    draw.ellipse([sx - 40, sy - 40, sx + 40, sy + 40], fill=GOLD_SUN, outline=DARK_OUTLINE, width=10)

    # Chunky Red Crayon angled across the page
    cr_start = (cx + 150, cy + 180)
    cr_end = (cx - 20, cy + 20)
    draw_thick_line(draw, cr_start, cr_end, DARK_OUTLINE, 64)
    draw_thick_line(draw, cr_start, cr_end, GUAVA_RED, 48)
    # Crayon Wrapper Label
    draw_thick_line(draw, (cx + 90, cy + 120), (cx + 40, cy + 70), GOLD_SUN, 48)
    # Crayon Tip Point
    draw.polygon([(cx - 20, cy + 20), (cx - 60, cy - 10), (cx - 5, cy - 25)], fill=GUAVA_RED, outline=DARK_OUTLINE)

    # Child's Hand gripping the crayon
    hx, hy = cx + 80, cy + 100
    draw.ellipse([hx - 70, hy - 60, hx + 70, hy + 60], fill=SKIN_TONE, outline=DARK_OUTLINE, width=18)
    for fx in [-30, 0, 30]:
        draw.ellipse([hx + fx - 22, hy - 80, hx + fx + 22, hy - 20], fill=SKIN_TONE, outline=DARK_OUTLINE, width=14)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_face_asset():
    """Lovable human child's face with cute hair bangs, ears, eyebrows, button nose & warm smile."""
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2

    # Ears Left & Right
    draw.ellipse([cx - 260, cy - 40, cx - 180, cy + 60], fill=SKIN_TONE, outline=DARK_OUTLINE, width=18)
    draw.ellipse([cx + 180, cy - 40, cx + 260, cy + 60], fill=SKIN_TONE, outline=DARK_OUTLINE, width=18)

    # Hair Back
    draw.ellipse([cx - 240, cy - 260, cx + 240, cy + 120], fill=WOOD_BROWN, outline=DARK_OUTLINE, width=22)

    # Child Face Head Oval
    draw.ellipse([cx - 220, cy - 180, cx + 220, cy + 220], fill=SKIN_TONE, outline=DARK_OUTLINE, width=22)

    # Hair Front / Bangs
    draw.polygon([(cx - 220, cy - 120), (cx - 140, cy - 20), (cx - 60, cy - 90), (cx + 20, cy - 20), (cx + 120, cy - 80), (cx + 220, cy - 120), (cx + 180, cy - 240), (cx - 180, cy - 240)], fill=WOOD_BROWN, outline=DARK_OUTLINE)

    # Eyebrows
    draw_thick_line(draw, (cx - 120, cy - 60), (cx - 50, cy - 70), WOOD_DARK, 14)
    draw_thick_line(draw, (cx + 50, cy - 70), (cx + 120, cy - 60), WOOD_DARK, 14)

    # Big Expressive Eyes with Highlights
    draw.ellipse([cx - 85 - 28, cy - 20 - 28, cx - 85 + 28, cy - 20 + 28], fill=DARK_OUTLINE)
    draw.ellipse([cx + 85 - 28, cy - 20 - 28, cx + 85 + 28, cy - 20 + 28], fill=DARK_OUTLINE)
    draw.ellipse([cx - 95, cy - 30, cx - 75, cy - 10], fill=WHITE)
    draw.ellipse([cx + 75, cy - 30, cx + 95, cy - 10], fill=WHITE)

    # Rosy Cheeks
    draw.ellipse([cx - 165, cy + 30, cx - 105, cy + 70], fill=PINK_CHEEK)
    draw.ellipse([cx + 105, cy + 30, cx + 165, cy + 70], fill=PINK_CHEEK)

    # Button Nose
    draw.arc([cx - 20, cy + 10, cx + 20, cy + 40], 0, 180, fill=DARK_OUTLINE, width=12)

    # Cheerful Open Smile
    draw.chord([cx - 65, cy + 60, cx + 65, cy + 150], 0, 180, fill=(235, 87, 87, 255), outline=DARK_OUTLINE, width=16)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_gap():
    """Two green stepping platforms across a sparkling blue stream with a cute frog leaping the GAP."""
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2

    # Sparkling Blue Water at bottom
    draw.rounded_rectangle([cx - 360, cy + 100, cx + 360, cy + 280], radius=40, fill=SKY_BLUE, outline=DARK_OUTLINE, width=20)
    draw_thick_line(draw, (cx - 160, cy + 190), (cx - 40, cy + 190), WHITE, 12)
    draw_thick_line(draw, (cx + 60, cy + 220), (cx + 180, cy + 220), WHITE, 12)

    # Left Stepping Platform
    draw.rounded_rectangle([cx - 340, cy - 40, cx - 90, cy + 240], radius=35, fill=LEAF_GREEN, outline=DARK_OUTLINE, width=22)
    draw.rounded_rectangle([cx - 320, cy - 20, cx - 110, cy + 40], radius=20, fill=(120, 205, 90, 255))

    # Right Stepping Platform (Clear Visible GAP in between)
    draw.rounded_rectangle([cx + 90, cy - 40, cx + 340, cy + 240], radius=35, fill=LEAF_GREEN, outline=DARK_OUTLINE, width=22)
    draw.rounded_rectangle([cx + 110, cy - 20, cx + 320, cy + 40], radius=20, fill=(120, 205, 90, 255))

    # Dashed Leap Arc over the Gap
    for a in range(200, 340, 25):
        rad = math.radians(a)
        px = cx + 120 * math.cos(rad)
        py = cy - 40 + 80 * math.sin(rad)
        draw.ellipse([px - 8, py - 8, px + 8, py + 8], fill=GOLD_SUN)

    # Cute Little Green Frog Mid-Leap in the Air
    fx, fy = cx, cy - 140
    # Frog Body
    draw.ellipse([fx - 80, fy - 60, fx + 80, fy + 60], fill=LEAF_GREEN, outline=DARK_OUTLINE, width=16)
    # Frog Eye Domes
    draw.ellipse([fx - 65, fy - 105, fx - 15, fy - 45], fill=LEAF_GREEN, outline=DARK_OUTLINE, width=12)
    draw.ellipse([fx + 15, fy - 105, fx + 65, fy - 45], fill=LEAF_GREEN, outline=DARK_OUTLINE, width=12)
    draw.ellipse([fx - 50, fy - 85, fx - 30, fy - 65], fill=DARK_OUTLINE)
    draw.ellipse([fx + 30, fy - 85, fx + 50, fy - 65], fill=DARK_OUTLINE)
    draw.arc([fx - 35, fy - 10, fx + 35, fy + 30], 10, 170, fill=DARK_OUTLINE, width=10)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_hand():
    """Anatomically clear 5-finger cartoon child's hand waving in greeting with palm creases."""
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20

    # Wrist / Arm Base
    draw.rounded_rectangle([cx - 95, cy + 120, cx + 95, cy + 280], radius=35, fill=SKIN_TONE, outline=DARK_OUTLINE, width=22)

    # Main Palm
    draw.ellipse([cx - 160, cy - 70, cx + 160, cy + 180], fill=SKIN_TONE, outline=DARK_OUTLINE, width=22)

    # 4 Straight Fingers (Pinky, Ring, Middle, Index)
    fingers = [
        (-105, -170, 48, 170),   # Pinky
        (-40, -240, 54, 220),    # Ring
        (30, -260, 56, 240),     # Middle
        (95, -220, 54, 210),     # Index
    ]
    for (fx, fy, fw, fh) in fingers:
        draw.rounded_rectangle([cx + fx - fw/2, cy + fy, cx + fx + fw/2, cy + fy + fh], radius=int(fw/2), fill=SKIN_TONE, outline=DARK_OUTLINE, width=18)

    # Thumb spread out to the right
    draw.rounded_rectangle([cx + 110, cy - 20, cx + 240, cy + 60], radius=30, fill=SKIN_TONE, outline=DARK_OUTLINE, width=18)

    # Palm Heart / Life Creases
    draw.arc([cx - 80, cy + 10, cx + 40, cy + 110], 20, 140, fill=PINK_CHEEK, width=10)
    draw.arc([cx - 20, cy + 20, cx + 90, cy + 120], 30, 150, fill=PINK_CHEEK, width=10)

    # Cheerful Waving Motion Lines
    draw.arc([cx - 240, cy - 240, cx - 180, cy - 100], 110, 250, fill=SKY_BLUE, width=14)
    draw.arc([cx + 180, cy - 240, cx + 240, cy - 100], 290, 70, fill=SKY_BLUE, width=14)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_mat():
    """Cozy woven floor welcome mat with a cute pair of red toddler slippers resting on it."""
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2

    # Woven Floor Mat Base
    draw.rounded_rectangle([cx - 300, cy - 180, cx + 300, cy + 180], radius=40, fill=GOLD_SUN, outline=DARK_OUTLINE, width=22)
    # Mat Fringe / Border Stripes
    draw.rounded_rectangle([cx - 260, cy - 140, cx + 260, cy + 140], radius=25, fill=CREAM, outline=DARK_OUTLINE, width=14)
    for x in range(-200, 240, 70):
        draw_thick_line(draw, (cx + x, cy - 130), (cx + x, cy + 130), MANGO, 10)

    # Cute Pair of Red Toddler Slippers resting on the Mat
    # Left Slipper
    lx, ly = cx - 90, cy + 10
    draw.rounded_rectangle([lx - 55, ly - 90, lx + 55, ly + 80], radius=35, fill=GUAVA_RED, outline=DARK_OUTLINE, width=16)
    draw.ellipse([lx - 40, ly - 70, lx + 40, ly - 10], fill=WHITE, outline=DARK_OUTLINE, width=10)

    # Right Slipper
    rx, ry = cx + 90, cy + 10
    draw.rounded_rectangle([rx - 55, ry - 90, rx + 55, ry + 80], radius=35, fill=GUAVA_RED, outline=DARK_OUTLINE, width=16)
    draw.ellipse([rx - 40, ry - 70, rx + 40, ry - 10], fill=WHITE, outline=DARK_OUTLINE, width=10)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_nap():
    """Sweet child sleeping peacefully in bed tucked under a cozy blanket with Zzz bubbles."""
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20

    # Wooden Bed Frame
    draw.rounded_rectangle([cx - 320, cy - 100, cx + 320, cy + 240], radius=40, fill=WOOD_BROWN, outline=DARK_OUTLINE, width=22)

    # Fluffy White Pillow
    draw.rounded_rectangle([cx - 240, cy - 160, cx + 240, cy + 20], radius=45, fill=WHITE, outline=DARK_OUTLINE, width=18)

    # Child's Sleeping Head resting on pillow
    hx, hy = cx - 30, cy - 70
    draw.ellipse([hx - 110, hy - 90, hx + 110, hy + 90], fill=SKIN_TONE, outline=DARK_OUTLINE, width=18)
    # Hair
    draw.arc([hx - 110, hy - 100, hx + 110, hy], 180, 360, fill=WOOD_BROWN, width=45)
    # Peaceful Closed Sleeping Eyes
    draw.arc([hx - 65, hy - 10, hx - 15, hy + 30], 10, 170, fill=DARK_OUTLINE, width=12)
    draw.arc([hx + 15, hy - 10, hx + 65, hy + 30], 10, 170, fill=DARK_OUTLINE, width=12)
    draw.ellipse([hx - 70, hy + 25, hx - 30, hy + 50], fill=PINK_CHEEK)
    draw.ellipse([hx + 30, hy + 25, hx + 70, hy + 50], fill=PINK_CHEEK)
    draw.arc([hx - 25, hy + 25, hx + 25, hy + 60], 10, 170, fill=DARK_OUTLINE, width=10)

    # Cozy Polka-Dot Blanket Tucked In
    draw.rounded_rectangle([cx - 300, cy - 20, cx + 300, cy + 220], radius=35, fill=SKY_BLUE, outline=DARK_OUTLINE, width=22)
    draw.rounded_rectangle([cx - 300, cy - 20, cx + 300, cy + 50], radius=20, fill=GOLD_SUN, outline=DARK_OUTLINE, width=14)
    # Blanket Polka-dots
    for px in [-200, -100, 0, 100, 200]:
        draw.ellipse([cx + px - 18, cy + 120, cx + px + 18, cy + 156], fill=WHITE)

    # Peaceful ZZZ Bubbles floating up
    for (zx, zy, sz) in [(cx + 170, cy - 140, "z"), (cx + 230, cy - 210, "Z")]:
        draw.text((zx, zy), sz, fill=UBE_PURPLE)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_sam():
    """Full-body cheerful boy character Sam in blue t-shirt & shorts waving enthusiastically."""
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20

    # Legs & Shoes
    draw_thick_line(draw, (cx - 50, cy + 140), (cx - 50, cy + 220), SKIN_TONE, 32)
    draw_thick_line(draw, (cx + 50, cy + 140), (cx + 50, cy + 220), SKIN_TONE, 32)
    draw.rounded_rectangle([cx - 85, cy + 220, cx - 15, cy + 260], radius=18, fill=GUAVA_RED, outline=DARK_OUTLINE, width=14)
    draw.rounded_rectangle([cx + 15, cy + 220, cx + 85, cy + 260], radius=18, fill=GUAVA_RED, outline=DARK_OUTLINE, width=14)

    # Blue T-Shirt & Shorts
    draw.rounded_rectangle([cx - 70, cy + 90, cx + 70, cy + 160], radius=16, fill=WOOD_BROWN, outline=DARK_OUTLINE, width=16)
    draw.rounded_rectangle([cx - 95, cy - 20, cx + 95, cy + 110], radius=28, fill=SKY_BLUE, outline=DARK_OUTLINE, width=20)

    # Arms: Left arm on hip, Right arm waving high
    draw_thick_line(draw, (cx - 85, cy), (cx - 150, cy + 60), SKIN_TONE, 32)
    draw.ellipse([cx - 170, cy + 45, cx - 130, cy + 85], fill=SKIN_TONE, outline=DARK_OUTLINE, width=12)

    draw_thick_line(draw, (cx + 85, cy), (cx + 160, cy - 80), SKIN_TONE, 32)
    draw.ellipse([cx + 140, cy - 120, cx + 195, cy - 65], fill=SKIN_TONE, outline=DARK_OUTLINE, width=12)

    # Head & Spiky Hair
    hx, hy = cx, cy - 130
    draw.ellipse([hx - 110, hy - 110, hx + 110, hy + 110], fill=SKIN_TONE, outline=DARK_OUTLINE, width=20)
    # Spiky Brown Hair
    draw.polygon([(hx - 110, hy - 40), (hx - 90, hy - 150), (hx - 30, hy - 180), (hx + 30, hy - 180), (hx + 90, hy - 150), (hx + 110, hy - 40), (hx + 70, hy - 70), (hx, hy - 100), (hx - 70, hy - 70)], fill=WOOD_BROWN, outline=DARK_OUTLINE)

    # Cute Face
    draw.ellipse([hx - 45 - 18, hy - 10 - 18, hx - 45 + 18, hy - 10 + 18], fill=DARK_OUTLINE)
    draw.ellipse([hx + 45 - 18, hy - 10 - 18, hx + 45 + 18, hy - 10 + 18], fill=DARK_OUTLINE)
    draw.ellipse([hx - 52, hy - 18, hx - 38, hy - 4], fill=WHITE)
    draw.ellipse([hx + 38, hy - 18, hx + 52, hy - 4], fill=WHITE)
    draw.ellipse([hx - 80, hy + 20, hx - 45, hy + 45], fill=PINK_CHEEK)
    draw.ellipse([hx + 45, hy + 20, hx + 80, hy + 45], fill=PINK_CHEEK)
    draw.arc([hx - 35, hy + 15, hx + 35, hy + 65], 10, 170, fill=DARK_OUTLINE, width=12)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_sis():
    """Full-body cheerful girl character Sis in yellow dress with pigtails & pink ribbons waving."""
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20

    # Legs & Shoes
    draw_thick_line(draw, (cx - 45, cy + 140), (cx - 45, cy + 220), SKIN_TONE, 30)
    draw_thick_line(draw, (cx + 45, cy + 140), (cx + 45, cy + 220), SKIN_TONE, 30)
    draw.rounded_rectangle([cx - 80, cy + 220, cx - 15, cy + 260], radius=18, fill=GUAVA_RED, outline=DARK_OUTLINE, width=14)
    draw.rounded_rectangle([cx + 15, cy + 220, cx + 80, cy + 260], radius=18, fill=GUAVA_RED, outline=DARK_OUTLINE, width=14)

    # Yellow A-Line Dress
    dress_pts = [(cx - 45, cy - 20), (cx + 45, cy - 20), (cx + 110, cy + 150), (cx - 110, cy + 150)]
    draw.polygon(dress_pts, fill=GOLD_SUN, outline=DARK_OUTLINE)
    for i in range(4):
        draw_thick_line(draw, dress_pts[i], dress_pts[(i+1)%4], DARK_OUTLINE, 18)

    # Arms: Left arm on hip, Right arm waving
    draw_thick_line(draw, (cx - 65, cy), (cx - 140, cy + 60), SKIN_TONE, 30)
    draw.ellipse([cx - 160, cy + 45, cx - 120, cy + 85], fill=SKIN_TONE, outline=DARK_OUTLINE, width=12)

    draw_thick_line(draw, (cx + 65, cy), (cx + 150, cy - 80), SKIN_TONE, 30)
    draw.ellipse([cx + 130, cy - 120, cx + 185, cy - 65], fill=SKIN_TONE, outline=DARK_OUTLINE, width=12)

    # Pigtails with Pink Ribbons
    hx, hy = cx, cy - 130
    draw.ellipse([hx - 190, hy - 80, hx - 90, hy + 20], fill=WOOD_BROWN, outline=DARK_OUTLINE, width=16)
    draw.ellipse([hx - 120, hy - 40, hx - 80, hy], fill=PINK_CHEEK, outline=DARK_OUTLINE, width=8)

    draw.ellipse([hx + 90, hy - 80, hx + 190, hy + 20], fill=WOOD_BROWN, outline=DARK_OUTLINE, width=16)
    draw.ellipse([hx + 80, hy - 40, hx + 120, hy], fill=PINK_CHEEK, outline=DARK_OUTLINE, width=8)

    # Girl Head & Bangs
    draw.ellipse([hx - 110, hy - 110, hx + 110, hy + 110], fill=SKIN_TONE, outline=DARK_OUTLINE, width=20)
    draw.arc([hx - 110, hy - 110, hx + 110, hy], 180, 360, fill=WOOD_BROWN, width=40)

    # Cute Face
    draw.ellipse([hx - 45 - 18, hy - 10 - 18, hx - 45 + 18, hy - 10 + 18], fill=DARK_OUTLINE)
    draw.ellipse([hx + 45 - 18, hy - 10 - 18, hx + 45 + 18, hy - 10 + 18], fill=DARK_OUTLINE)
    draw.ellipse([hx - 52, hy - 18, hx - 38, hy - 4], fill=WHITE)
    draw.ellipse([hx + 38, hy - 18, hx + 52, hy - 4], fill=WHITE)
    draw.ellipse([hx - 80, hy + 20, hx - 45, hy + 45], fill=PINK_CHEEK)
    draw.ellipse([hx + 45, hy + 20, hx + 80, hy + 45], fill=PINK_CHEEK)
    draw.arc([hx - 35, hy + 15, hx + 35, hy + 65], 10, 170, fill=DARK_OUTLINE, width=12)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── Existing Illustrations ───────────────────────────────────────────────────

def draw_aim():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20
    draw.ellipse([cx-320, cy-320, cx+320, cy+320], fill=GUAVA_RED, outline=DARK_OUTLINE, width=22)
    draw.ellipse([cx-240, cy-240, cx+240, cy+240], fill=WHITE, outline=DARK_OUTLINE, width=18)
    draw.ellipse([cx-160, cy-160, cx+160, cy+160], fill=SKY_BLUE, outline=DARK_OUTLINE, width=18)
    draw.ellipse([cx-80, cy-80, cx+80, cy+80], fill=GOLD_SUN, outline=DARK_OUTLINE, width=18)
    draw_face(draw, cx, cy, eye_spacing=30, eye_y_offset=-10, eye_r=12, smile_w=30, smile_h=16, blush=False)
    draw_thick_line(draw, (cx+180, cy-180), (cx+15, cy-15), DARK_OUTLINE, 24)
    draw_thick_line(draw, (cx+180, cy-180), (cx+15, cy-15), WOOD_BROWN, 16)
    draw.polygon([(cx+180, cy-180), (cx+230, cy-160), (cx+210, cy-210)], fill=GUAVA_RED, outline=DARK_OUTLINE)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_bam():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    pts = []
    for i in range(16):
        a = math.radians(i * (360/16) - 90)
        r = 380 if i % 2 == 0 else 200
        pts.append((cx + r * math.cos(a), cy + r * math.sin(a)))
    draw.polygon(pts, fill=GOLD_SUN, outline=DARK_OUTLINE)
    for i in range(16):
        draw.line([pts[i], pts[(i+1)%16]], fill=DARK_OUTLINE, width=22, joint="curve")
    pts2 = []
    for i in range(16):
        a = math.radians(i * (360/16) - 90)
        r = 250 if i % 2 == 0 else 130
        pts2.append((cx + r * math.cos(a), cy + r * math.sin(a)))
    draw.polygon(pts2, fill=MANGO)
    draw_face(draw, cx, cy, eye_spacing=50, eye_y_offset=-15, eye_r=20, smile_w=50, smile_h=26)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_bird():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20
    draw.ellipse([cx-190, cy-170, cx+170, cy+190], fill=SKY_BLUE, outline=DARK_OUTLINE, width=22)
    draw.ellipse([cx-130, cy+10, cx+120, cy+180], fill=CREAM)
    draw.ellipse([cx-180, cy-20, cx-40, cy+140], fill=OCEAN_BLUE, outline=DARK_OUTLINE, width=16)
    draw.polygon([(cx+150, cy-30), (cx+240, cy), (cx+150, cy+30)], fill=GOLD_SUN, outline=DARK_OUTLINE)
    draw_face(draw, cx+50, cy-50, eye_spacing=40, eye_y_offset=0, eye_r=18, smile_w=30, smile_h=15)
    draw.polygon([(cx-180, cy+20), (cx-300, cy-40), (cx-240, cy+80)], fill=OCEAN_BLUE, outline=DARK_OUTLINE)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_box():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 30
    bw, bh = 240, 200
    draw.rectangle([cx-bw, cy-bh+50, cx+bw, cy+bh], fill=GOLD_SUN, outline=DARK_OUTLINE, width=22)
    draw.rectangle([cx-bw-20, cy-bh, cx+bw+20, cy-bh+60], fill=MANGO, outline=DARK_OUTLINE, width=22)
    draw.rectangle([cx-35, cy-bh+50, cx+35, cy+bh], fill=GUAVA_RED)
    draw.rectangle([cx-35, cy-bh, cx+35, cy-bh+60], fill=GUAVA_RED)
    draw.ellipse([cx-90, cy-bh-70, cx-10, cy-bh+10], fill=GUAVA_RED, outline=DARK_OUTLINE, width=16)
    draw.ellipse([cx+10, cy-bh-70, cx+90, cy-bh+10], fill=GUAVA_RED, outline=DARK_OUTLINE, width=16)
    draw.ellipse([cx-25, cy-bh-30, cx+25, cy-bh+20], fill=GUAVA_RED, outline=DARK_OUTLINE, width=16)
    draw_face(draw, cx, cy+60, eye_spacing=65, eye_y_offset=0, eye_r=20, smile_w=55, smile_h=30)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_bus():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.rounded_rectangle([cx-300, cy-180, cx+300, cy+140], radius=60, fill=GOLD_SUN, outline=DARK_OUTLINE, width=22)
    for wx in [-190, -40, 110]:
        draw.rounded_rectangle([cx+wx-55, cy-140, cx+wx+55, cy-30], radius=20, fill=SKY_BLUE, outline=DARK_OUTLINE, width=14)
    draw.rectangle([cx-320, cy+80, cx+320, cy+130], fill=CREAM, outline=DARK_OUTLINE, width=16)
    for wx in [-180, 180]:
        draw.ellipse([cx+wx-65, cy+90, cx+wx+65, cy+220], fill=DARK_OUTLINE)
        draw.ellipse([cx+wx-35, cy+120, cx+wx+35, cy+190], fill=CREAM)
    draw_face(draw, cx, cy+40, eye_spacing=75, eye_y_offset=0, eye_r=18, smile_w=60, smile_h=26)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_cake():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 50
    draw.rounded_rectangle([cx-240, cy+20, cx+240, cy+180], radius=35, fill=CREAM, outline=DARK_OUTLINE, width=22)
    draw.rounded_rectangle([cx-170, cy-110, cx+170, cy+20], radius=30, fill=GUAVA_RED, outline=DARK_OUTLINE, width=22)
    for fx in [-190, -110, -30, 50, 130, 200]:
        draw.ellipse([cx+fx-25, cy+10, cx+fx+25, cy+60], fill=GUAVA_RED)
    draw.rectangle([cx-18, cy-220, cx+18, cy-110], fill=SKY_BLUE, outline=DARK_OUTLINE, width=14)
    draw.ellipse([cx-22, cy-280, cx+22, cy-220], fill=GOLD_SUN, outline=DARK_OUTLINE, width=12)
    draw_face(draw, cx, cy+90, eye_spacing=65, eye_y_offset=0, eye_r=20, smile_w=55, smile_h=28)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_cat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.polygon([(cx-220, cy-80), (cx-150, cy-280), (cx-50, cy-170)], fill=MANGO, outline=DARK_OUTLINE)
    draw.polygon([(cx-190, cy-90), (cx-150, cy-240), (cx-80, cy-160)], fill=PINK_CHEEK)
    draw.polygon([(cx+220, cy-80), (cx+150, cy-280), (cx+50, cy-170)], fill=MANGO, outline=DARK_OUTLINE)
    draw.polygon([(cx+190, cy-90), (cx+150, cy-240), (cx+80, cy-160)], fill=PINK_CHEEK)
    draw.ellipse([cx-250, cy-210, cx+250, cy+230], fill=MANGO, outline=DARK_OUTLINE, width=22)
    draw.ellipse([cx-170, cy-80, cx+170, cy+210], fill=CREAM)
    draw_face(draw, cx, cy-30, eye_spacing=85, eye_y_offset=0, eye_r=26, smile_w=70, smile_h=35)
    draw.polygon([(cx-15, cy+30), (cx+15, cy+30), (cx, cy+48)], fill=DARK_OUTLINE)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_fan():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 - 40
    draw.ellipse([cx-240, cy-240, cx+240, cy+240], fill=WHITE, outline=DARK_OUTLINE, width=22)
    for a in [0, 90, 180, 270]:
        rad = math.radians(a)
        bx = cx + 110 * math.cos(rad)
        by = cy + 110 * math.sin(rad)
        draw.ellipse([bx-55, by-55, bx+55, by+55], fill=SKY_BLUE, outline=DARK_OUTLINE, width=12)
    draw.ellipse([cx-75, cy-75, cx+75, cy+75], fill=GOLD_SUN, outline=DARK_OUTLINE, width=16)
    draw_face(draw, cx, cy, eye_spacing=24, eye_y_offset=-6, eye_r=8, smile_w=22, smile_h=12, blush=False)
    draw.rectangle([cx-25, cy+240, cx+25, cy+380], fill=DARK_OUTLINE)
    draw.rounded_rectangle([cx-140, cy+360, cx+140, cy+420], radius=25, fill=SKY_BLUE, outline=DARK_OUTLINE, width=18)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_fish():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2 - 20, H/2
    draw.ellipse([cx-230, cy-170, cx+180, cy+170], fill=MANGO, outline=DARK_OUTLINE, width=22)
    draw.rectangle([cx-60, cy-160, cx, cy+160], fill=WHITE, outline=DARK_OUTLINE, width=14)
    draw.polygon([(cx-190, cy), (cx-320, cy-130), (cx-280, cy), (cx-320, cy+130)], fill=MANGO, outline=DARK_OUTLINE)
    draw.ellipse([cx-30, cy-230, cx+60, cy-130], fill=MANGO, outline=DARK_OUTLINE, width=14)
    draw.ellipse([cx-30, cy+130, cx+60, cy+230], fill=MANGO, outline=DARK_OUTLINE, width=14)
    draw_face(draw, cx+90, cy-20, eye_spacing=35, eye_y_offset=0, eye_r=22, smile_w=40, smile_h=22)
    for (bx, by, r) in [(cx+220, cy-120, 24), (cx+270, cy-180, 18), (cx+240, cy-240, 14)]:
        draw.ellipse([bx-r, by-r, bx+r, by+r], fill=SKY_BLUE, outline=DARK_OUTLINE, width=8)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_fox():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.polygon([(cx-230, cy-70), (cx-170, cy-300), (cx-40, cy-160)], fill=MANGO, outline=DARK_OUTLINE)
    draw.polygon([(cx-190, cy-80), (cx-160, cy-250), (cx-70, cy-150)], fill=DARK_OUTLINE)
    draw.polygon([(cx+230, cy-70), (cx+170, cy-300), (cx+40, cy-160)], fill=MANGO, outline=DARK_OUTLINE)
    draw.polygon([(cx+190, cy-80), (cx+160, cy-250), (cx+70, cy-150)], fill=DARK_OUTLINE)
    draw.ellipse([cx-250, cy-180, cx+250, cy+210], fill=MANGO, outline=DARK_OUTLINE, width=22)
    draw.polygon([(cx, cy+170), (cx-220, cy+10), (cx-100, cy-60), (cx, cy), (cx+100, cy-60), (cx+220, cy+10)], fill=WHITE, outline=DARK_OUTLINE)
    draw_face(draw, cx, cy-30, eye_spacing=85, eye_y_offset=0, eye_r=24, smile_w=65, smile_h=30)
    draw.ellipse([cx-20, cy+130, cx+20, cy+165], fill=DARK_OUTLINE)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_kit():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 30
    draw.rounded_rectangle([cx-260, cy-150, cx+260, cy+180], radius=45, fill=GUAVA_RED, outline=DARK_OUTLINE, width=22)
    draw.arc([cx-100, cy-270, cx+100, cy-110], 180, 360, fill=DARK_OUTLINE, width=26)
    draw.rectangle([cx-30, cy-80, cx+30, cy+80], fill=WHITE)
    draw.rectangle([cx-80, cy-30, cx+80, cy+30], fill=WHITE)
    draw_face(draw, cx, cy+100, eye_spacing=65, eye_y_offset=0, eye_r=16, smile_w=50, smile_h=22, blush=False)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_lit():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 40
    draw.rounded_rectangle([cx-100, cy-80, cx+100, cy+200], radius=30, fill=WHITE, outline=DARK_OUTLINE, width=22)
    draw_thick_line(draw, (cx, cy-80), (cx, cy-130), DARK_OUTLINE, 14)
    draw.ellipse([cx-70, cy-290, cx+70, cy-120], fill=GOLD_SUN, outline=DARK_OUTLINE, width=18)
    draw.ellipse([cx-40, cy-250, cx+40, cy-140], fill=MANGO)
    draw_face(draw, cx, cy+40, eye_spacing=45, eye_y_offset=0, eye_r=16, smile_w=40, smile_h=20)
    for a in [0, 45, 90, 135, 180, 225, 270, 315]:
        rad = math.radians(a)
        draw_thick_line(draw, (cx + 150*math.cos(rad), (cy-200) + 150*math.sin(rad)),
                              (cx + 180*math.cos(rad), (cy-200) + 180*math.sin(rad)), GOLD_SUN, 12)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_mob():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    positions = [(-160, -40, SKY_BLUE), (160, -40, MANGO), (0, 70, GOLD_SUN)]
    for (px, py, col) in positions:
        draw.ellipse([cx+px-115, cy+py-115, cx+px+115, cy+py+115], fill=col, outline=DARK_OUTLINE, width=18)
        draw_face(draw, cx+px, cy+py, eye_spacing=35, eye_y_offset=-10, eye_r=14, smile_w=35, smile_h=18)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_pan():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2 - 40, H/2
    draw.ellipse([cx-210, cy-210, cx+210, cy+210], fill=DARK_OUTLINE)
    draw.ellipse([cx-180, cy-180, cx+180, cy+180], fill=(80, 95, 105, 255))
    draw_thick_line(draw, (cx+180, cy), (cx+340, cy), DARK_OUTLINE, 45)
    draw_thick_line(draw, (cx+180, cy), (cx+340, cy), WOOD_BROWN, 32)
    draw.ellipse([cx-110, cy-100, cx+110, cy+110], fill=WHITE, outline=DARK_OUTLINE, width=14)
    draw.ellipse([cx-55, cy-55, cx+55, cy+55], fill=GOLD_SUN, outline=DARK_OUTLINE, width=12)
    draw_face(draw, cx, cy, eye_spacing=20, eye_y_offset=-5, eye_r=7, smile_w=18, smile_h=10, blush=False)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_pig():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.polygon([(cx-220, cy-110), (cx-160, cy-270), (cx-60, cy-180)], fill=PINK_CHEEK, outline=DARK_OUTLINE)
    draw.polygon([(cx+220, cy-110), (cx+160, cy-270), (cx+60, cy-180)], fill=PINK_CHEEK, outline=DARK_OUTLINE)
    draw.ellipse([cx-250, cy-220, cx+250, cy+230], fill=PINK_CHEEK, outline=DARK_OUTLINE, width=22)
    draw.ellipse([cx-100, cy+10, cx+100, cy+130], fill=(255, 170, 180, 255), outline=DARK_OUTLINE, width=16)
    draw.ellipse([cx-50, cy+50, cx-20, cy+90], fill=DARK_OUTLINE)
    draw.ellipse([cx+20, cy+50, cx+50, cy+90], fill=DARK_OUTLINE)
    draw_face(draw, cx, cy-60, eye_spacing=85, eye_y_offset=0, eye_r=22, smile_w=50, smile_h=20, blush=False)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_quiz():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.rounded_rectangle([cx-220, cy-260, cx+180, cy+260], radius=35, fill=WHITE, outline=DARK_OUTLINE, width=22)
    for qy in [-140, -20, 100]:
        draw_thick_line(draw, (cx-150, cy+qy), (cx-110, cy+qy+30), LEAF_GREEN, 20)
        draw_thick_line(draw, (cx-110, cy+qy+30), (cx-50, cy+qy-30), LEAF_GREEN, 20)
        draw.rectangle([cx-20, cy+qy-10, cx+120, cy+qy+10], fill=SKY_BLUE)
    draw_thick_line(draw, (cx+180, cy-180), (cx+260, cy+180), GOLD_SUN, 40)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_road():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.ellipse([cx-400, cy-100, cx+200, cy+380], fill=LEAF_GREEN, outline=DARK_OUTLINE, width=20)
    draw.ellipse([cx-100, cy-160, cx+450, cy+380], fill=(120, 200, 90, 255), outline=DARK_OUTLINE, width=20)
    draw.polygon([(cx-80, cy-140), (cx+80, cy-140), (cx+260, cy+320), (cx-260, cy+320)], fill=(100, 115, 125, 255), outline=DARK_OUTLINE)
    draw_thick_line(draw, (cx, cy-120), (cx, cy+300), GOLD_SUN, 16)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_spin():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.polygon([(cx, cy-220), (cx+220, cy-40), (cx, cy+240), (cx-220, cy-40)], fill=GUAVA_RED, outline=DARK_OUTLINE)
    draw.polygon([(cx, cy-120), (cx+140, cy-40), (cx, cy+140), (cx-140, cy-40)], fill=GOLD_SUN)
    draw_face(draw, cx, cy-40, eye_spacing=45, eye_y_offset=0, eye_r=16, smile_w=40, smile_h=20)
    draw.arc([cx-260, cy-180, cx+260, cy+180], 30, 150, fill=SKY_BLUE, width=18)
    draw.arc([cx-260, cy-180, cx+260, cy+180], 210, 330, fill=SKY_BLUE, width=18)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_sub():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.ellipse([cx-260, cy-140, cx+220, cy+160], fill=GOLD_SUN, outline=DARK_OUTLINE, width=22)
    draw.rectangle([cx-40, cy-240, cx+20, cy-120], fill=MANGO, outline=DARK_OUTLINE, width=16)
    draw.rectangle([cx-40, cy-260, cx+80, cy-210], fill=MANGO, outline=DARK_OUTLINE, width=16)
    for px in [-120, -10, 100]:
        draw.ellipse([cx+px-40, cy-40, cx+px+40, cy+40], fill=SKY_BLUE, outline=DARK_OUTLINE, width=14)
    draw.polygon([(cx-250, cy), (cx-320, cy-70), (cx-300, cy), (cx-320, cy+70)], fill=MANGO, outline=DARK_OUTLINE)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_sum():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.rounded_rectangle([cx-280, cy-140, cx-120, cy+140], radius=25, fill=SKY_BLUE, outline=DARK_OUTLINE, width=18)
    draw.text((cx-225, cy-70), "1", fill=WHITE)
    draw_thick_line(draw, (cx-80, cy), (cx-20, cy), DARK_OUTLINE, 18)
    draw_thick_line(draw, (cx-50, cy-30), (cx-50, cy+30), DARK_OUTLINE, 18)
    draw.rounded_rectangle([cx+20, cy-140, cx+180, cy+140], radius=25, fill=GOLD_SUN, outline=DARK_OUTLINE, width=18)
    draw.text((cx+75, cy-70), "2", fill=WHITE)
    draw_face(draw, cx+100, cy+60, eye_spacing=26, eye_y_offset=0, eye_r=8, smile_w=24, smile_h=12, blush=False)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_van():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.rounded_rectangle([cx-290, cy-160, cx+280, cy+130], radius=50, fill=SKY_BLUE, outline=DARK_OUTLINE, width=22)
    draw.rounded_rectangle([cx+80, cy-130, cx+240, cy-20], radius=20, fill=WHITE, outline=DARK_OUTLINE, width=14)
    draw.rounded_rectangle([cx-80, cy-130, cx+50, cy-20], radius=20, fill=WHITE, outline=DARK_OUTLINE, width=14)
    for wx in [-170, 160]:
        draw.ellipse([cx+wx-60, cy+80, cx+wx+60, cy+200], fill=DARK_OUTLINE)
        draw.ellipse([cx+wx-30, cy+110, cx+wx+30, cy+170], fill=CREAM)
    draw_face(draw, cx+160, cy+40, eye_spacing=45, eye_y_offset=0, eye_r=14, smile_w=35, smile_h=18)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_warm():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.ellipse([cx-210, cy-210, cx+210, cy+210], fill=GOLD_SUN, outline=DARK_OUTLINE, width=22)
    for i in range(12):
        rad = math.radians(i * (360/12))
        x1 = cx + 240 * math.cos(rad)
        y1 = cy + 240 * math.sin(rad)
        x2 = cx + 320 * math.cos(rad)
        y2 = cy + 320 * math.sin(rad)
        draw_thick_line(draw, (x1, y1), (x2, y2), MANGO, 22)
    draw_face(draw, cx, cy, eye_spacing=75, eye_y_offset=-15, eye_r=26, smile_w=85, smile_h=45)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_zoo():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.rounded_rectangle([cx-260, cy-180, cx+260, cy+220], radius=40, fill=WOOD_BROWN, outline=DARK_OUTLINE, width=22)
    draw.rounded_rectangle([cx-160, cy-60, cx+160, cy+220], radius=40, fill=WHITE, outline=DARK_OUTLINE, width=18)
    draw.rounded_rectangle([cx-220, cy-260, cx+220, cy-120], radius=25, fill=GOLD_SUN, outline=DARK_OUTLINE, width=18)
    draw.ellipse([cx-80, cy+20, cx+80, cy+180], fill=MANGO, outline=DARK_OUTLINE, width=16)
    draw_face(draw, cx, cy+100, eye_spacing=30, eye_y_offset=-8, eye_r=10, smile_w=28, smile_h=14)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── Generator Map ─────────────────────────────────────────────────────────────

GENERATORS = {
    "blendword_aim": draw_aim,
    "blendword_bam": draw_bam,
    "blendword_bat": draw_bat,
    "blendword_bird": draw_bird,
    "blendword_box": draw_box,
    "blendword_bus": draw_bus,
    "blendword_cake": draw_cake,
    "blendword_cat": draw_cat,
    "blendword_draw": draw_draw,
    "blendword_face": draw_face_asset,
    "blendword_fan": draw_fan,
    "blendword_fish": draw_fish,
    "blendword_fox": draw_fox,
    "blendword_gap": draw_gap,
    "blendword_hand": draw_hand,
    "blendword_kit": draw_kit,
    "blendword_lit": draw_lit,
    "blendword_mat": draw_mat,
    "blendword_mob": draw_mob,
    "blendword_nap": draw_nap,
    "blendword_pan": draw_pan,
    "blendword_pig": draw_pig,
    "blendword_quiz": draw_quiz,
    "blendword_road": draw_road,
    "blendword_sam": draw_sam,
    "blendword_sis": draw_sis,
    "blendword_spin": draw_spin,
    "blendword_sub": draw_sub,
    "blendword_sum": draw_sum,
    "blendword_van": draw_van,
    "blendword_warm": draw_warm,
    "blendword_zoo": draw_zoo,
}

def main():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    target_dir = os.path.join(base_dir, "app", "src", "main", "assets", "images", "pictures")
    os.makedirs(target_dir, exist_ok=True)

    print("=" * 80)
    print("[*] Generating all 32 Blend It Word Illustrations (Human & Kid-Friendly)...")
    print("=" * 80)

    for name, func in GENERATORS.items():
        img = func()
        out_path = os.path.join(target_dir, f"{name}.png")
        img.save(out_path, "PNG", optimize=True)
        print(f"  [+] Generated: {name}.png ({os.path.getsize(out_path)} bytes)")

    print("=" * 80)
    print("[*] All 32 Blend It word assets successfully updated!")

if __name__ == "__main__":
    main()
