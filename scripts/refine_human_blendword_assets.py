"""
PlayIT Blend It Assets Refiner — Ultra-Pediatric Human & Action Illustrations (V2 Organic Polish)
Generates high-definition, 100% transparent vector illustrations for the 9 targeted Blend It words:
1. SAM: Big, lovable cartoon boy with fluffy curved hair, striped tee, friendly smile & waving hand
2. SIS: Big, lovable cartoon girl with round pigtails & pink bows, yellow polka-dot dress & waving hand
3. FACE: Full, lovable cartoon child face with soft round hair, sparkling eyes, button nose & open smile
4. HAND: Chubby 5-finger cartoon toddler hand waving with palm creases & sparkling motion waves
5. DRAW: Chunky red crayon held by child hand drawing a radiant smiling rainbow
6. GAP: Clear river chasm between two green stone banks with a cute frog leaping across
7. MAT: Oval woven floor mat with decorative fringe and cute red slippers
8. NAP: Cozy child tucked snugly in a soft wooden bed with fluffy pillow & floating Zzz
9. BAT: Adorable purple storybook bat with scalloped wings, rosy cheeks & sweet fangs
"""

from PIL import Image, ImageDraw
import math
import os

SIZE = 512
SCALE = 3  # 3x Supersampling for ultra-smooth curves
W = SIZE * SCALE
H = SIZE * SCALE

# Standard Palette
DARK_OUTLINE = (45, 55, 62, 255)       # #2D373E Slate Charcoal
WHITE = (255, 255, 255, 255)
PINK_CHEEK = (255, 140, 140, 225)      # Khan Rosy Blush
MANGO = (250, 123, 40, 255)           # #FA7B28 Warm Orange
CREAM = (255, 238, 215, 255)          # Soft Cream
SKIN_TONE = (255, 220, 190, 255)      # Warm Soft Skin
GOLD_SUN = (255, 204, 0, 255)         # Bright Sunny Yellow
GOLD_DARK = (245, 166, 35, 255)
LEAF_GREEN = (76, 175, 80, 255)       # Fresh Green
SKY_BLUE = (56, 189, 248, 255)        # Clean Sky Blue
OCEAN_BLUE = (2, 132, 199, 255)
UBE_PURPLE = (139, 95, 191, 255)      # Royal Ube
GUAVA_RED = (255, 90, 110, 255)       # Warm Red / Guava
WOOD_BROWN = (175, 105, 55, 255)
WOOD_DARK = (120, 70, 35, 255)

def draw_thick_line(draw, start, end, color=DARK_OUTLINE, width=32):
    draw.line([start, end], fill=color, width=width, joint="curve")

def draw_sparkle_eyes(draw, lx, ly, rx, ry, r=42):
    for (ex, ey) in [(lx, ly), (rx, ry)]:
        draw.ellipse([ex - r, ey - r, ex + r, ey + r], fill=DARK_OUTLINE)
        draw.ellipse([ex - r*0.35 - r*0.35, ey - r*0.35 - r*0.35, ex - r*0.35 + r*0.35, ey - r*0.35 + r*0.35], fill=WHITE)
        draw.ellipse([ex + r*0.3 - r*0.18, ey + r*0.3 - r*0.18, ex + r*0.3 + r*0.18, ey + r*0.3 + r*0.18], fill=WHITE)

# ── 1. SAM (Lovable Cartoon Boy) ─────────────────────────────────────────────
def draw_sam():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 70

    # Red Sneakers
    draw.rounded_rectangle([cx - 220, cy + 340, cx - 60, cy + 450], radius=50, fill=GUAVA_RED, outline=DARK_OUTLINE, width=28)
    draw.rounded_rectangle([cx + 60, cy + 340, cx + 220, cy + 450], radius=50, fill=GUAVA_RED, outline=DARK_OUTLINE, width=28)
    draw.rounded_rectangle([cx - 220, cy + 400, cx - 60, cy + 450], radius=20, fill=WHITE)
    draw.rounded_rectangle([cx + 60, cy + 400, cx + 220, cy + 450], radius=20, fill=WHITE)

    # Chunky Legs
    draw_thick_line(draw, (cx - 140, cy + 190), (cx - 140, cy + 360), DARK_OUTLINE, 82)
    draw_thick_line(draw, (cx - 140, cy + 190), (cx - 140, cy + 360), SKIN_TONE, 64)
    draw_thick_line(draw, (cx + 140, cy + 190), (cx + 140, cy + 360), DARK_OUTLINE, 82)
    draw_thick_line(draw, (cx + 140, cy + 190), (cx + 140, cy + 360), SKIN_TONE, 64)

    # Denim Shorts
    draw.rounded_rectangle([cx - 190, cy + 110, cx + 190, cy + 240], radius=40, fill=OCEAN_BLUE, outline=DARK_OUTLINE, width=28)

    # Striped T-Shirt (Sky Blue & White)
    draw.rounded_rectangle([cx - 220, cy - 110, cx + 220, cy + 140], radius=55, fill=SKY_BLUE, outline=DARK_OUTLINE, width=30)
    draw.rectangle([cx - 200, cy - 40, cx + 200, cy + 10], fill=WHITE)
    draw.rectangle([cx - 200, cy + 50, cx + 200, cy + 100], fill=WHITE)

    # Left Arm (Hand on hip)
    draw_thick_line(draw, (cx - 190, cy - 60), (cx - 330, cy + 40), DARK_OUTLINE, 76)
    draw_thick_line(draw, (cx - 190, cy - 60), (cx - 330, cy + 40), SKIN_TONE, 56)
    draw.ellipse([cx - 380, cy + 10, cx - 290, cy + 90], fill=SKIN_TONE, outline=DARK_OUTLINE, width=22)

    # Right Arm (Waving high)
    draw_thick_line(draw, (cx + 190, cy - 60), (cx + 340, cy - 220), DARK_OUTLINE, 76)
    draw_thick_line(draw, (cx + 190, cy - 60), (cx + 340, cy - 220), SKIN_TONE, 56)
    # Waving Mitten Hand
    hx, hy = cx + 360, cy - 250
    draw.ellipse([hx - 60, hy - 60, hx + 60, hy + 60], fill=SKIN_TONE, outline=DARK_OUTLINE, width=24)
    draw.ellipse([hx + 10, hy - 75, hx + 70, hy - 15], fill=SKIN_TONE, outline=DARK_OUTLINE, width=20)

    # Boy Head
    bx, by = cx, cy - 300
    # Ears
    draw.ellipse([bx - 310, by - 40, bx - 210, by + 60], fill=SKIN_TONE, outline=DARK_OUTLINE, width=24)
    draw.ellipse([bx + 210, by - 40, bx + 310, by + 60], fill=SKIN_TONE, outline=DARK_OUTLINE, width=24)

    # Hair Back
    draw.ellipse([bx - 280, by - 260, bx + 280, by + 100], fill=WOOD_BROWN, outline=DARK_OUTLINE, width=30)
    # Head Face
    draw.ellipse([bx - 260, by - 200, bx + 260, by + 220], fill=SKIN_TONE, outline=DARK_OUTLINE, width=30)
    # Hair Bangs
    draw.chord([bx - 260, by - 210, bx + 260, by - 10], 180, 360, fill=WOOD_BROWN, outline=DARK_OUTLINE, width=26)
    draw.polygon([(bx - 160, by - 70), (bx - 90, by + 20), (bx - 20, by - 50), (bx + 60, by + 30), (bx + 140, by - 40), (bx + 210, by - 80), (bx + 180, by - 160), (bx - 180, by - 160)], fill=WOOD_BROWN)

    # Sparkling Eyes & Cheeks
    draw_sparkle_eyes(draw, bx - 100, by + 15, bx + 100, by + 15, r=44)
    draw.ellipse([bx - 210, by + 55, bx - 130, by + 110], fill=PINK_CHEEK)
    draw.ellipse([bx + 130, by + 55, bx + 210, by + 110], fill=PINK_CHEEK)

    # Wide Joyful Smile
    draw.chord([bx - 85, by + 65, bx + 85, by + 175], 0, 180, fill=(235, 87, 87, 255), outline=DARK_OUTLINE, width=24)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 2. SIS (Lovable Cartoon Girl) ────────────────────────────────────────────
def draw_sis():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 70

    # Red Shoes
    draw.rounded_rectangle([cx - 210, cy + 340, cx - 50, cy + 450], radius=50, fill=GUAVA_RED, outline=DARK_OUTLINE, width=28)
    draw.rounded_rectangle([cx + 50, cy + 340, cx + 210, cy + 450], radius=50, fill=GUAVA_RED, outline=DARK_OUTLINE, width=28)

    # Legs
    draw_thick_line(draw, (cx - 130, cy + 190), (cx - 130, cy + 360), DARK_OUTLINE, 78)
    draw_thick_line(draw, (cx - 130, cy + 190), (cx - 130, cy + 360), SKIN_TONE, 60)
    draw_thick_line(draw, (cx + 130, cy + 190), (cx + 130, cy + 360), DARK_OUTLINE, 78)
    draw_thick_line(draw, (cx + 130, cy + 190), (cx + 130, cy + 360), SKIN_TONE, 60)

    # Yellow Flared Dress with Polka Dots
    dress_pts = [(cx - 100, cy - 110), (cx + 100, cy - 110), (cx + 240, cy + 220), (cx - 240, cy + 220)]
    draw.polygon(dress_pts, fill=GOLD_SUN, outline=DARK_OUTLINE)
    for i in range(4):
        draw_thick_line(draw, dress_pts[i], dress_pts[(i+1)%4], DARK_OUTLINE, 30)
    for (dx, dy) in [(-130, 90), (0, 40), (130, 90), (-60, 150), (80, 150)]:
        draw.ellipse([cx + dx - 24, cy + dy - 24, cx + dx + 24, cy + dy + 24], fill=GUAVA_RED)

    # Left Arm
    draw_thick_line(draw, (cx - 150, cy - 60), (cx - 290, cy + 40), DARK_OUTLINE, 72)
    draw_thick_line(draw, (cx - 150, cy - 60), (cx - 290, cy + 40), SKIN_TONE, 54)
    draw.ellipse([cx - 340, cy + 10, cx - 250, cy + 90], fill=SKIN_TONE, outline=DARK_OUTLINE, width=22)

    # Right Arm (Waving high)
    draw_thick_line(draw, (cx + 150, cy - 60), (cx + 310, cy - 220), DARK_OUTLINE, 72)
    draw_thick_line(draw, (cx + 150, cy - 60), (cx + 310, cy - 220), SKIN_TONE, 54)
    hx, hy = cx + 330, cy - 250
    draw.ellipse([hx - 58, hy - 58, hx + 58, hy + 58], fill=SKIN_TONE, outline=DARK_OUTLINE, width=24)
    draw.ellipse([hx + 10, hy - 72, hx + 65, hy - 15], fill=SKIN_TONE, outline=DARK_OUTLINE, width=20)

    # Pigtail Buns with Pink Ribbons
    bx, by = cx, cy - 300
    draw.ellipse([bx - 390, by - 160, bx - 210, by + 20], fill=WOOD_BROWN, outline=DARK_OUTLINE, width=28)
    draw.ellipse([bx + 210, by - 160, bx + 390, by + 20], fill=WOOD_BROWN, outline=DARK_OUTLINE, width=28)
    draw.ellipse([bx - 260, by - 110, bx - 180, by - 30], fill=PINK_CHEEK, outline=DARK_OUTLINE, width=16)
    draw.ellipse([bx + 180, by - 110, bx + 260, by - 30], fill=PINK_CHEEK, outline=DARK_OUTLINE, width=16)

    # Head & Hair Bangs
    draw.ellipse([bx - 250, by - 200, bx + 250, by + 220], fill=SKIN_TONE, outline=DARK_OUTLINE, width=30)
    draw.arc([bx - 250, by - 210, bx + 250, by - 10], 180, 360, fill=WOOD_BROWN, width=80)

    # Sparkling Eyes & Cheeks
    draw_sparkle_eyes(draw, bx - 95, by + 15, bx + 95, by + 15, r=44)
    draw.ellipse([bx - 200, by + 55, bx - 120, by + 110], fill=PINK_CHEEK)
    draw.ellipse([bx + 120, by + 55, bx + 200, by + 110], fill=PINK_CHEEK)

    # Joyful Open Smile
    draw.chord([bx - 80, by + 65, bx + 80, by + 165], 0, 180, fill=(235, 87, 87, 255), outline=DARK_OUTLINE, width=24)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 3. FACE (Full Detailed Child Face) ───────────────────────────────────────
def draw_face_asset():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2

    # Ears Left & Right
    draw.ellipse([cx - 410, cy - 80, cx - 270, cy + 90], fill=SKIN_TONE, outline=DARK_OUTLINE, width=30)
    draw.ellipse([cx + 270, cy - 80, cx + 410, cy + 90], fill=SKIN_TONE, outline=DARK_OUTLINE, width=30)
    draw.arc([cx - 370, cy - 40, cx - 300, cy + 50], 0, 180, fill=PINK_CHEEK, width=16)
    draw.arc([cx + 300, cy - 40, cx + 370, cy + 50], 0, 180, fill=PINK_CHEEK, width=16)

    # Hair Back
    draw.ellipse([cx - 380, cy - 420, cx + 380, cy + 140], fill=WOOD_BROWN, outline=DARK_OUTLINE, width=34)

    # Face Head Oval
    draw.ellipse([cx - 350, cy - 300, cx + 350, cy + 340], fill=SKIN_TONE, outline=DARK_OUTLINE, width=34)

    # Rounded Hair Bangs Framing Forehead
    draw.chord([cx - 350, cy - 310, cx + 350, cy - 40], 180, 360, fill=WOOD_BROWN, outline=DARK_OUTLINE, width=30)
    draw.polygon([(cx - 240, cy - 120), (cx - 140, cy - 20), (cx - 40, cy - 110), (cx + 60, cy - 10), (cx + 180, cy - 90), (cx + 260, cy - 130), (cx + 220, cy - 260), (cx - 220, cy - 260)], fill=WOOD_BROWN)

    # Eyebrows
    draw_thick_line(draw, (cx - 200, cy - 90), (cx - 80, cy - 110), WOOD_DARK, 22)
    draw_thick_line(draw, (cx + 80, cy - 110), (cx + 200, cy - 90), WOOD_DARK, 22)

    # Sparkling Eyes
    draw_sparkle_eyes(draw, cx - 140, cy - 10, cx + 140, cy - 10, r=54)

    # Glowing Pink Cheeks
    draw.ellipse([cx - 290, cy + 60, cx - 170, cy + 140], fill=PINK_CHEEK)
    draw.ellipse([cx + 170, cy + 60, cx + 290, cy + 140], fill=PINK_CHEEK)

    # Button Nose
    draw.arc([cx - 35, cy + 40, cx + 35, cy + 90], 0, 180, fill=DARK_OUTLINE, width=20)

    # Big Cheerful Open Mouth
    draw.chord([cx - 120, cy + 115, cx + 120, cy + 270], 0, 180, fill=(235, 87, 87, 255), outline=DARK_OUTLINE, width=26)
    draw.ellipse([cx - 60, cy + 195, cx + 60, cy + 265], fill=(255, 170, 180, 255))

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 4. HAND (Chubby Organic Cartoon Waving Hand) ─────────────────────────────
def draw_hand():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 60

    # Wrist Base
    draw.rounded_rectangle([cx - 160, cy + 220, cx + 160, cy + 440], radius=60, fill=SKIN_TONE, outline=DARK_OUTLINE, width=34)

    # Main Palm Round Body
    draw.ellipse([cx - 280, cy - 110, cx + 280, cy + 300], fill=SKIN_TONE, outline=DARK_OUTLINE, width=34)

    # 4 Chunky Organic Fingers
    fingers = [
        (-180, -280, 90, 280),   # Pinky
        (-70, -390, 98, 380),    # Ring
        (55, -420, 102, 410),    # Middle
        (175, -350, 96, 350),    # Index
    ]
    for (fx, fy, fw, fh) in fingers:
        draw.rounded_rectangle([cx + fx - fw/2, cy + fy, cx + fx + fw/2, cy + fy + fh], radius=int(fw/2), fill=SKIN_TONE, outline=DARK_OUTLINE, width=30)

    # Thumb spread naturally to the right
    draw.rounded_rectangle([cx + 190, cy - 20, cx + 410, cy + 110], radius=55, fill=SKIN_TONE, outline=DARK_OUTLINE, width=30)

    # Palm Creases
    draw.arc([cx - 130, cy + 30, cx + 60, cy + 180], 20, 140, fill=PINK_CHEEK, width=18)
    draw.arc([cx - 30, cy + 40, cx + 140, cy + 190], 30, 150, fill=PINK_CHEEK, width=18)

    # Motion Swooshes
    draw.arc([cx - 420, cy - 400, cx - 290, cy - 180], 120, 240, fill=SKY_BLUE, width=24)
    draw.arc([cx + 310, cy - 400, cx + 440, cy - 180], 300, 60, fill=SKY_BLUE, width=24)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 5. DRAW (Hand Gripping Crayon Drawing Radiant Rainbow) ───────────────────
def draw_draw():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2

    # Drawing Radiant Rainbow Arcs
    draw.arc([cx - 460, cy - 380, cx + 100, cy + 240], 180, 360, fill=GUAVA_RED, width=44)
    draw.arc([cx - 410, cy - 330, cx + 50, cy + 190], 180, 360, fill=GOLD_SUN, width=44)
    draw.arc([cx - 360, cy - 280, cx, cy + 140], 180, 360, fill=SKY_BLUE, width=44)

    # Smiling Sun Doodle at rainbow origin
    sx, sy = cx - 310, cy - 270
    draw.ellipse([sx - 75, sy - 75, sx + 75, sy + 75], fill=GOLD_SUN, outline=DARK_OUTLINE, width=22)
    draw.ellipse([sx - 25 - 10, sy - 10 - 10, sx - 25 + 10, sy - 10 + 10], fill=DARK_OUTLINE)
    draw.ellipse([sx + 25 - 10, sy - 10 - 10, sx + 25 + 10, sy - 10 + 10], fill=DARK_OUTLINE)
    draw.arc([sx - 25, sy + 5, sx + 25, sy + 35], 10, 170, fill=DARK_OUTLINE, width=12)

    # Chunky Red Crayon angled
    cr_start = (cx + 320, cy + 330)
    cr_end = (cx - 20, cy + 20)
    draw_thick_line(draw, cr_start, cr_end, DARK_OUTLINE, 105)
    draw_thick_line(draw, cr_start, cr_end, GUAVA_RED, 82)
    # Crayon Label
    draw_thick_line(draw, (cx + 200, cy + 220), (cx + 100, cy + 120), GOLD_SUN, 82)
    # Crayon Point Tip
    draw.polygon([(cx - 20, cy + 20), (cx - 100, cy - 30), (cx - 10, cy - 60)], fill=GUAVA_RED, outline=DARK_OUTLINE)

    # Chubby Hand Gripping Crayon
    hx, hy = cx + 150, cy + 170
    draw.ellipse([hx - 120, hy - 100, hx + 120, hy + 100], fill=SKIN_TONE, outline=DARK_OUTLINE, width=30)
    for fx in [-50, 0, 50]:
        draw.ellipse([hx + fx - 36, hy - 140, hx + fx + 36, hy - 40], fill=SKIN_TONE, outline=DARK_OUTLINE, width=22)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 6. GAP (River Chasm & Leaping Frog) ───────────────────────────────────────
def draw_gap():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2

    # Blue Stream below
    draw.rounded_rectangle([cx - 480, cy + 160, cx + 480, cy + 420], radius=60, fill=SKY_BLUE, outline=DARK_OUTLINE, width=32)
    draw_thick_line(draw, (cx - 220, cy + 280), (cx - 60, cy + 280), WHITE, 20)
    draw_thick_line(draw, (cx + 80, cy + 320), (cx + 240, cy + 320), WHITE, 20)

    # Left Cliff Ledge
    draw.rounded_rectangle([cx - 460, cy - 40, cx - 130, cy + 370], radius=55, fill=LEAF_GREEN, outline=DARK_OUTLINE, width=34)
    draw.rounded_rectangle([cx - 430, cy - 10, cx - 160, cy + 90], radius=30, fill=(120, 205, 90, 255))

    # Right Cliff Ledge (Clear Prominent GAP)
    draw.rounded_rectangle([cx + 130, cy - 40, cx + 460, cy + 370], radius=55, fill=LEAF_GREEN, outline=DARK_OUTLINE, width=34)
    draw.rounded_rectangle([cx + 160, cy - 10, cx + 430, cy + 90], radius=30, fill=(120, 205, 90, 255))

    # Bold Gold Jump Trajectory Arc
    for a in range(195, 345, 18):
        rad = math.radians(a)
        px = cx + 210 * math.cos(rad)
        py = cy - 60 + 160 * math.sin(rad)
        draw.ellipse([px - 18, py - 18, px + 18, py + 18], fill=GOLD_SUN, outline=DARK_OUTLINE, width=8)

    # Cute Little Green Frog Mid-Air
    fx, fy = cx, cy - 210
    draw.ellipse([fx - 130, fy - 95, fx + 130, fy + 95], fill=LEAF_GREEN, outline=DARK_OUTLINE, width=28)
    # Pop-Up Eyes
    draw.ellipse([fx - 105, fy - 160, fx - 25, fy - 70], fill=LEAF_GREEN, outline=DARK_OUTLINE, width=20)
    draw.ellipse([fx + 25, fy - 160, fx + 105, fy - 70], fill=LEAF_GREEN, outline=DARK_OUTLINE, width=20)
    draw.ellipse([fx - 80, fy - 130, fx - 50, fy - 100], fill=DARK_OUTLINE)
    draw.ellipse([fx + 50, fy - 130, fx + 80, fy - 100], fill=DARK_OUTLINE)
    draw.arc([fx - 60, fy - 15, fx + 60, fy + 50], 10, 170, fill=DARK_OUTLINE, width=18)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 7. MAT (Woven Banig Floor Mat with Red Bunny Slippers) ────────────────────
def draw_mat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2

    # Large Woven Banig Mat
    draw.rounded_rectangle([cx - 450, cy - 250, cx + 450, cy + 250], radius=60, fill=GOLD_SUN, outline=DARK_OUTLINE, width=34)
    draw.rounded_rectangle([cx - 390, cy - 190, cx + 390, cy + 190], radius=45, fill=CREAM, outline=DARK_OUTLINE, width=24)
    for x in range(-300, 360, 120):
        draw_thick_line(draw, (cx + x, cy - 180), (cx + x, cy + 180), MANGO, 18)

    # Red Bunny Slippers resting on the Mat
    lx, ly = cx - 140, cy + 20
    draw.rounded_rectangle([lx - 90, ly - 130, lx + 90, ly + 120], radius=55, fill=GUAVA_RED, outline=DARK_OUTLINE, width=26)
    draw.ellipse([lx - 65, ly - 100, lx + 65, ly - 10], fill=WHITE, outline=DARK_OUTLINE, width=16)
    draw.ellipse([lx - 60, ly - 190, lx - 15, ly - 100], fill=GUAVA_RED, outline=DARK_OUTLINE, width=14)
    draw.ellipse([lx + 15, ly - 190, lx + 60, ly - 100], fill=GUAVA_RED, outline=DARK_OUTLINE, width=14)

    rx, ry = cx + 140, cy + 20
    draw.rounded_rectangle([rx - 90, ry - 130, rx + 90, ry + 120], radius=55, fill=GUAVA_RED, outline=DARK_OUTLINE, width=26)
    draw.ellipse([rx - 65, ry - 100, rx + 65, ry - 10], fill=WHITE, outline=DARK_OUTLINE, width=16)
    draw.ellipse([rx - 60, ry - 190, rx - 15, ry - 100], fill=GUAVA_RED, outline=DARK_OUTLINE, width=14)
    draw.ellipse([rx + 15, ry - 190, rx + 60, ry - 100], fill=GUAVA_RED, outline=DARK_OUTLINE, width=14)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 8. NAP (Child Sleeping on Big Fluffy Pillow with Zzz) ────────────────────
def draw_nap():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20

    # Cozy Wooden Bed Headboard
    draw.rounded_rectangle([cx - 440, cy - 200, cx + 440, cy + 340], radius=60, fill=WOOD_BROWN, outline=DARK_OUTLINE, width=34)

    # Big Fluffy White Pillow
    draw.rounded_rectangle([cx - 350, cy - 260, cx + 350, cy + 20], radius=75, fill=WHITE, outline=DARK_OUTLINE, width=30)

    # Child's Peaceful Sleeping Head
    hx, hy = cx - 40, cy - 130
    draw.ellipse([hx - 170, hy - 145, hx + 170, hy + 145], fill=SKIN_TONE, outline=DARK_OUTLINE, width=30)
    # Tousled Hair
    draw.arc([hx - 170, hy - 160, hx + 170, hy + 10], 180, 360, fill=WOOD_BROWN, width=80)

    # Peaceful Curved Closed Eyes
    draw.arc([hx - 105, hy - 10, hx - 25, hy + 55], 10, 170, fill=DARK_OUTLINE, width=20)
    draw.arc([hx + 25, hy - 10, hx + 105, hy + 55], 10, 170, fill=DARK_OUTLINE, width=20)
    # Rosy Cheeks
    draw.ellipse([hx - 120, hy + 45, hx - 60, hy + 90], fill=PINK_CHEEK)
    draw.ellipse([hx + 60, hy + 45, hx + 120, hy + 90], fill=PINK_CHEEK)
    draw.arc([hx - 40, hy + 45, hx + 40, hy + 100], 10, 170, fill=DARK_OUTLINE, width=16)

    # Cozy Star Quilt Blanket
    draw.rounded_rectangle([cx - 440, cy - 30, cx + 440, cy + 340], radius=55, fill=SKY_BLUE, outline=DARK_OUTLINE, width=34)
    draw.rounded_rectangle([cx - 440, cy - 30, cx + 440, cy + 70], radius=30, fill=GOLD_SUN, outline=DARK_OUTLINE, width=22)
    for (qx, qy) in [(-250, 170), (-80, 220), (100, 160), (280, 210)]:
        draw.ellipse([cx + qx - 28, cy + qy - 28, cx + qx + 28, cy + qy + 28], fill=WHITE)

    # Floating "Z z z" Sleep Bubbles
    for (zx, zy, sz, col) in [(cx + 250, cy - 240, 60, UBE_PURPLE), (cx + 350, cy - 350, 85, MANGO)]:
        draw.text((zx, zy), "Z", fill=col)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 9. BAT (Storybook Purple Bat) ─────────────────────────────────────────────
def draw_bat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20

    # Scalloped Wings
    l_wing = [
        (cx - 60, cy + 80),
        (cx - 460, cy - 170),
        (cx - 360, cy + 40),
        (cx - 260, cy + 170),
        (cx - 140, cy + 190),
        (cx - 60, cy + 120)
    ]
    draw.polygon(l_wing, fill=UBE_PURPLE, outline=DARK_OUTLINE)
    for i in range(len(l_wing)-1):
        draw_thick_line(draw, l_wing[i], l_wing[i+1], DARK_OUTLINE, 30)

    r_wing = [
        (cx + 60, cy + 80),
        (cx + 460, cy - 170),
        (cx + 360, cy + 40),
        (cx + 260, cy + 170),
        (cx + 140, cy + 190),
        (cx + 60, cy + 120)
    ]
    draw.polygon(r_wing, fill=UBE_PURPLE, outline=DARK_OUTLINE)
    for i in range(len(r_wing)-1):
        draw_thick_line(draw, r_wing[i], r_wing[i+1], DARK_OUTLINE, 30)

    # Pointy Ears
    draw.polygon([(cx - 170, cy - 70), (cx - 120, cy - 320), (cx - 20, cy - 140)], fill=UBE_PURPLE, outline=DARK_OUTLINE)
    draw.polygon([(cx - 150, cy - 80), (cx - 120, cy - 260), (cx - 50, cy - 130)], fill=PINK_CHEEK)
    draw.polygon([(cx + 170, cy - 70), (cx + 120, cy - 320), (cx + 20, cy - 140)], fill=UBE_PURPLE, outline=DARK_OUTLINE)
    draw.polygon([(cx + 150, cy - 80), (cx + 120, cy - 260), (cx + 50, cy - 130)], fill=PINK_CHEEK)

    # Bat Pear Body & Head
    draw.ellipse([cx - 210, cy - 160, cx + 210, cy + 250], fill=UBE_PURPLE, outline=DARK_OUTLINE, width=34)
    draw.ellipse([cx - 130, cy + 20, cx + 130, cy + 230], fill=CREAM)

    # Sparkling Eyes & Cheeks
    draw_sparkle_eyes(draw, cx - 85, cy - 15, cx + 85, cy - 15, r=40)
    draw.ellipse([cx - 165, cy + 35, cx - 100, cy + 80], fill=PINK_CHEEK)
    draw.ellipse([cx + 100, cy + 35, cx + 165, cy + 80], fill=PINK_CHEEK)

    # Smile & Little White Fangs
    draw.arc([cx - 60, cy + 25, cx + 60, cy + 105], 10, 170, fill=DARK_OUTLINE, width=20)
    draw.polygon([(cx - 28, cy + 70), (cx - 12, cy + 70), (cx - 20, cy + 98)], fill=WHITE, outline=DARK_OUTLINE)
    draw.polygon([(cx + 12, cy + 70), (cx + 28, cy + 70), (cx + 20, cy + 98)], fill=WHITE, outline=DARK_OUTLINE)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── Main Runner ───────────────────────────────────────────────────────────────

REFINED_GENERATORS = {
    "blendword_bat": draw_bat,
    "blendword_draw": draw_draw,
    "blendword_face": draw_face_asset,
    "blendword_gap": draw_gap,
    "blendword_hand": draw_hand,
    "blendword_mat": draw_mat,
    "blendword_nap": draw_nap,
    "blendword_sam": draw_sam,
    "blendword_sis": draw_sis,
}

def main():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    target_dir = os.path.join(base_dir, "app", "src", "main", "assets", "images", "pictures")
    os.makedirs(target_dir, exist_ok=True)

    print("=" * 80)
    print("[*] Refining 9 Target Blend It Word Illustrations (V2 Organic Polish)...")
    print("=" * 80)

    for name, func in REFINED_GENERATORS.items():
        img = func()
        out_path = os.path.join(target_dir, f"{name}.png")
        img.save(out_path, "PNG", optimize=True)
        print(f"  [+] Refined & Saved: {name}.png ({os.path.getsize(out_path)} bytes)")

    print("=" * 80)
    print("[*] All 9 Blend It word illustrations successfully refined!")

if __name__ == "__main__":
    main()
