"""
Master Craftsman Blend It Asset Generator
Crafts ultra-clear, kid-friendly, 4-Benchmark compliant 2D vector illustrations for the 9 targeted words:
1. SAM: Waist-up friendly cartoon boy in striped shirt with tousled hair, rosy cheeks & enthusiastic wave
2. SIS: Waist-up friendly cartoon girl with pigtails, pink bows, yellow dress & enthusiastic wave
3. FACE: Big, friendly cartoon child face with expressive eyes, rosy cheeks, nose & joyful smile
4. HAND: Chubby toddler hand in a friendly open wave / high-five with 5 soft rounded fingers & palm lines
5. DRAW: Big chunky red crayon actively drawing a colorful star and doodle with vibrant spark
6. GAP: Two wooden bridge platforms with a clear visible gap and a bold jumping arrow jumping across
7. MAT: Cozy woven banig welcome mat with fringe and a pair of cute red slippers
8. NAP: Sweet sleeping child tucked snugly under a starry blanket with a fluffy pillow and Zzz
9. BAT: Ultra-cute purple storybook bat with scalloped wings, big glossy eyes, rosy cheeks & tiny fangs
"""

from PIL import Image, ImageDraw
import math
import os

SIZE = 512
SCALE = 3
W = SIZE * SCALE
H = SIZE * SCALE

# Standard 4-Benchmark Palette
DARK = (74, 46, 24, 255)            # #4A2E18 DarkBrownOutline (Style Guide 16 §2.1)
WHITE = (255, 255, 255, 255)
PINK_BLUSH = (255, 145, 155, 230)   # Khan Academy Rosy Cheek
SKIN = (255, 222, 192, 255)         # Warm Toddler Skin
HAIR_BROWN = (165, 100, 50, 255)    # Storybook Chestnut Hair
HAIR_DARK = (115, 65, 30, 255)
GOLD = (255, 204, 0, 255)           # Sunny Yellow
ORANGE = (250, 125, 40, 255)        # Mango Orange
RED = (255, 90, 110, 255)           # Guava Red
BLUE_SKY = (56, 189, 248, 255)      # Duolingo Sky Blue
BLUE_OCEAN = (2, 132, 199, 255)
GREEN = (76, 175, 80, 255)          # Fresh Green
PURPLE = (145, 100, 195, 255)       # Royal Ube
CREAM = (255, 242, 225, 255)
WOOD = (185, 120, 70, 255)

def draw_outline_circle(draw, cx, cy, r, fill, outline=DARK, width=24):
    draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=fill, outline=outline, width=width)

def draw_glossy_eyes(draw, lx, ly, rx, ry, r=40):
    for (ex, ey) in [(lx, ly), (rx, ry)]:
        draw.ellipse([ex - r, ey - r, ex + r, ey + r], fill=DARK)
        # Big upper-left catchlight
        cr1 = r * 0.38
        draw.ellipse([ex - r*0.35 - cr1, ey - r*0.35 - cr1, ex - r*0.35 + cr1, ey - r*0.35 + cr1], fill=WHITE)
        # Small lower-right catchlight
        cr2 = r * 0.18
        draw.ellipse([ex + r*0.35 - cr2, ey + r*0.35 - cr2, ex + r*0.35 + cr2, ey + r*0.35 + cr2], fill=WHITE)

def draw_blush_cheeks(draw, lx, ly, rx, ry, rw=45, rh=28):
    for (cx, cy) in [(lx, ly), (rx, ry)]:
        draw.ellipse([cx - rw, cy - rh, cx + rw, cy + rh], fill=PINK_BLUSH)

# ── 1. SAM (Waist-Up Cartoon Boy Character) ──────────────────────────────────
def craft_sam():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 70

    # Striped T-Shirt Body
    body_pts = [(cx - 220, cy + 100), (cx + 220, cy + 100), (cx + 260, cy + 420), (cx - 260, cy + 420)]
    draw.polygon(body_pts, fill=BLUE_SKY, outline=DARK)
    draw.line([body_pts[0], body_pts[1]], fill=DARK, width=28)
    draw.line([body_pts[1], body_pts[2]], fill=DARK, width=28)
    draw.line([body_pts[2], body_pts[3]], fill=DARK, width=28)
    draw.line([body_pts[3], body_pts[0]], fill=DARK, width=28)
    # White Stripes
    draw.polygon([(cx - 230, cy + 180), (cx + 230, cy + 180), (cx + 245, cy + 240), (cx - 245, cy + 240)], fill=WHITE)
    draw.polygon([(cx - 248, cy + 300), (cx + 248, cy + 300), (cx + 260, cy + 360), (cx - 260, cy + 360)], fill=WHITE)

    # Neck
    draw.rectangle([cx - 70, cy + 30, cx + 70, cy + 120], fill=SKIN, outline=DARK, width=24)

    # Waving Right Arm
    draw.line([(cx + 180, cy + 120), (cx + 340, cy - 80)], fill=DARK, width=86, joint="curve")
    draw.line([(cx + 180, cy + 120), (cx + 340, cy - 80)], fill=SKIN, width=64, joint="curve")
    # Chubby Waving Hand
    hx, hy = cx + 370, cy - 120
    draw_outline_circle(draw, hx, hy, 70, SKIN, DARK, 24)
    # Thumb
    draw.ellipse([hx - 90, hy - 40, hx - 10, hy + 30], fill=SKIN, outline=DARK, width=20)
    # Fingers
    for fx in [-40, 0, 40]:
        draw.ellipse([hx + fx - 25, hy - 90, hx + fx + 25, hy - 20], fill=SKIN, outline=DARK, width=18)

    # Head (Ears first)
    hx, hy = cx, cy - 140
    draw_outline_circle(draw, hx - 240, hy, 55, SKIN, DARK, 24)
    draw_outline_circle(draw, hx + 240, hy, 55, SKIN, DARK, 24)

    # Hair Back
    draw.ellipse([hx - 280, hy - 280, hx + 280, hy + 160], fill=HAIR_BROWN, outline=DARK, width=28)
    # Face
    draw.ellipse([hx - 240, hy - 220, hx + 240, hy + 220], fill=SKIN, outline=DARK, width=28)

    # Fluffy Front Bangs
    draw.chord([hx - 240, hy - 230, hx + 240, hy - 20], 180, 360, fill=HAIR_BROWN, outline=DARK, width=26)
    draw.polygon([(hx - 180, hy - 80), (hx - 90, hy + 30), (hx - 10, hy - 60), (hx + 70, hy + 20), (hx + 170, hy - 60), (hx + 220, hy - 140), (hx + 180, hy - 240), (hx - 180, hy - 240)], fill=HAIR_BROWN)

    # Eyes, Cheeks, Smile
    draw_glossy_eyes(draw, hx - 90, hy + 15, hx + 90, hy + 15, r=42)
    draw_blush_cheeks(draw, hx - 160, hy + 65, hx + 160, hy + 65, rw=50, rh=30)
    # Happy Open Smile
    draw.chord([hx - 80, hy + 75, hx + 80, hy + 185], 0, 180, fill=RED, outline=DARK, width=24)
    draw.ellipse([hx - 40, hy + 130, hx + 40, hy + 180], fill=PINK_BLUSH)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 2. SIS (Waist-Up Cartoon Girl Character) ─────────────────────────────────
def craft_sis():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 70

    # Yellow Dress Body
    dress_pts = [(cx - 180, cy + 100), (cx + 180, cy + 100), (cx + 270, cy + 420), (cx - 270, cy + 420)]
    draw.polygon(dress_pts, fill=GOLD, outline=DARK)
    for i in range(4):
        draw.line([dress_pts[i], dress_pts[(i+1)%4]], fill=DARK, width=28)
    # White Collar
    draw.chord([cx - 120, cy + 80, cx + 120, cy + 180], 0, 180, fill=WHITE, outline=DARK, width=20)

    # Neck
    draw.rectangle([cx - 65, cy + 30, cx + 65, cy + 110], fill=SKIN, outline=DARK, width=22)

    # Waving Right Arm
    draw.line([(cx + 160, cy + 120), (cx + 330, cy - 80)], fill=DARK, width=82, joint="curve")
    draw.line([(cx + 160, cy + 120), (cx + 330, cy - 80)], fill=SKIN, width=60, joint="curve")
    hx, hy = cx + 360, cy - 120
    draw_outline_circle(draw, hx, hy, 68, SKIN, DARK, 24)
    draw.ellipse([hx - 85, hy - 40, hx - 10, hy + 30], fill=SKIN, outline=DARK, width=20)
    for fx in [-35, 5, 45]:
        draw.ellipse([hx + fx - 24, hy - 85, hx + fx + 24, hy - 20], fill=SKIN, outline=DARK, width=18)

    # Pigtails (Two round buns with pink bows)
    bx, by = cx, cy - 140
    draw_outline_circle(draw, bx - 260, by - 40, 80, HAIR_BROWN, DARK, 26)
    draw_outline_circle(draw, bx + 260, by - 40, 80, HAIR_BROWN, DARK, 26)
    # Pink Bows
    draw_outline_circle(draw, bx - 190, by - 20, 36, PINK_BLUSH, DARK, 16)
    draw_outline_circle(draw, bx + 190, by - 20, 36, PINK_BLUSH, DARK, 16)

    # Head & Bangs
    draw.ellipse([bx - 240, hy - 220, bx + 240, hy + 220], fill=SKIN, outline=DARK, width=28)
    draw.chord([bx - 240, hy - 230, bx + 240, hy - 20], 180, 360, fill=HAIR_BROWN, outline=DARK, width=28)

    # Eyes, Cheeks, Smile
    draw_glossy_eyes(draw, bx - 85, by + 15, bx + 85, by + 15, r=42)
    draw_blush_cheeks(draw, bx - 155, by + 65, bx + 155, by + 65, rw=50, rh=30)
    draw.chord([bx - 75, by + 75, bx + 75, by + 180], 0, 180, fill=RED, outline=DARK, width=24)
    draw.ellipse([bx - 35, by + 130, bx + 35, by + 175], fill=PINK_BLUSH)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 3. FACE (Detailed Pediatric Child Face) ───────────────────────────────────
def craft_face():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2

    # Ears Left & Right
    draw_outline_circle(draw, cx - 310, cy, 68, SKIN, DARK, 28)
    draw_outline_circle(draw, cx + 310, cy, 68, SKIN, DARK, 28)
    draw.arc([cx - 330, cy - 30, cx - 270, cy + 40], 0, 180, fill=PINK_BLUSH, width=16)
    draw.arc([cx + 270, cy - 30, cx + 330, cy + 40], 0, 180, fill=PINK_BLUSH, width=16)

    # Hair Back
    draw.ellipse([cx - 330, cy - 350, cx + 330, cy + 160], fill=HAIR_BROWN, outline=DARK, width=32)

    # Head Oval
    draw.ellipse([cx - 290, cy - 270, cx + 290, cy + 270], fill=SKIN, outline=DARK, width=32)

    # Hair Bangs
    draw.chord([cx - 290, cy - 280, cx + 290, cy - 30], 180, 360, fill=HAIR_BROWN, outline=DARK, width=30)
    draw.polygon([(cx - 220, cy - 90), (cx - 130, cy + 10), (cx - 30, cy - 70), (cx + 70, cy + 10), (cx + 170, cy - 60), (cx + 230, cy - 120), (cx + 200, cy - 240), (cx - 200, cy - 240)], fill=HAIR_BROWN)

    # Eyebrows
    draw.line([(cx - 170, cy - 70), (cx - 60, cy - 85)], fill=HAIR_DARK, width=22, joint="curve")
    draw.line([(cx + 60, cy - 85), (cx + 170, cy - 70)], fill=HAIR_DARK, width=22, joint="curve")

    # Big Sparkling Eyes & Cheeks
    draw_glossy_eyes(draw, cx - 110, cy, cx + 110, cy, r=52)
    draw_blush_cheeks(draw, cx - 200, cy + 70, cx + 200, cy + 70, rw=62, rh=36)

    # Button Nose
    draw.arc([cx - 28, cy + 40, cx + 28, cy + 85], 0, 180, fill=DARK, width=18)

    # Cheerful Smile with Tongue
    draw.chord([cx - 105, cy + 100, cx + 105, cy + 235], 0, 180, fill=RED, outline=DARK, width=26)
    draw.ellipse([cx - 50, cy + 170, cx + 50, cy + 230], fill=PINK_BLUSH)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 4. HAND (Chubby Toddler 5-Finger Waving Hand) ────────────────────────────
def craft_hand():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 50

    # Wrist Base
    draw.rounded_rectangle([cx - 140, cy + 190, cx + 140, cy + 410], radius=50, fill=SKIN, outline=DARK, width=32)

    # Palm Circle
    draw_outline_circle(draw, cx, cy + 40, 210, SKIN, DARK, 32)

    # 4 Curved Organic Fingers
    fingers = [
        (-140, -180, 80, 240),   # Pinky
        (-50, -290, 88, 330),    # Ring
        (50, -320, 92, 360),     # Middle
        (145, -250, 88, 300),    # Index
    ]
    for (fx, fy, fw, fh) in fingers:
        draw.rounded_rectangle([cx + fx - fw/2, cy + fy, cx + fx + fw/2, cy + fy + fh], radius=int(fw/2), fill=SKIN, outline=DARK, width=28)

    # Thumb spreading outward to the right
    draw.rounded_rectangle([cx + 150, cy - 20, cx + 340, cy + 90], radius=50, fill=SKIN, outline=DARK, width=28)

    # Soft Palm Creases
    draw.arc([cx - 100, cy + 20, cx + 40, cy + 140], 20, 140, fill=PINK_BLUSH, width=18)
    draw.arc([cx - 20, cy + 30, cx + 110, cy + 150], 30, 150, fill=PINK_BLUSH, width=18)

    # Motion Swooshes
    draw.arc([cx - 360, cy - 320, cx - 250, cy - 140], 120, 240, fill=BLUE_SKY, width=22)
    draw.arc([cx + 270, cy - 320, cx + 380, cy - 140], 300, 60, fill=BLUE_SKY, width=22)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 5. DRAW (Crayon Drawing Sparkling Rainbow) ───────────────────────────────
def craft_draw():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2

    # Rainbow Strokes
    draw.arc([cx - 420, cy - 340, cx + 80, cy + 220], 180, 360, fill=RED, width=38)
    draw.arc([cx - 375, cy - 295, cx + 35, cy + 175], 180, 360, fill=GOLD, width=38)
    draw.arc([cx - 330, cy - 250, cx - 10, cy + 130], 180, 360, fill=BLUE_SKY, width=38)

    # Smiling Yellow Star Doodle at rainbow origin
    sx, sy = cx - 280, cy - 240
    draw_outline_circle(draw, sx, sy, 70, GOLD, DARK, 20)
    draw_glossy_eyes(draw, sx - 22, sy - 8, sx + 22, sy - 8, r=12)
    draw.arc([sx - 20, sy + 5, sx + 20, sy + 30], 10, 170, fill=DARK, width=10)

    # Big Chunky Red Crayon
    cr_start = (cx + 290, cy + 300)
    cr_end = (cx - 20, cy + 10)
    draw.line([cr_start, cr_end], fill=DARK, width=96, joint="curve")
    draw.line([cr_start, cr_end], fill=RED, width=74, joint="curve")
    # Crayon Yellow Stripe Label
    draw.line([(cx + 170, cy + 190), (cx + 90, cy + 110)], fill=GOLD, width=74, joint="curve")
    # Crayon Sharp Tip
    draw.polygon([(cx - 20, cy + 10), (cx - 90, cy - 25), (cx - 10, cy - 55)], fill=RED, outline=DARK)

    # Chubby Toddler Hand Holding Crayon
    hx, hy = cx + 130, cy + 150
    draw_outline_circle(draw, hx, hy, 85, SKIN, DARK, 26)
    for fx in [-40, 0, 40]:
        draw.ellipse([hx + fx - 26, hy - 100, hx + fx + 26, hy - 20], fill=SKIN, outline=DARK, width=18)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 6. GAP (Two Platforms & Jumping Trajectory Arrow) ─────────────────────────
def craft_gap():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2

    # Blue Stream below
    draw.rounded_rectangle([cx - 420, cy + 160, cx + 420, cy + 380], radius=50, fill=BLUE_SKY, outline=DARK, width=28)
    draw.line([(cx - 200, cy + 260), (cx - 60, cy + 260)], fill=WHITE, width=18)
    draw.line([(cx + 60, cy + 300), (cx + 200, cy + 300)], fill=WHITE, width=18)

    # Left Wooden / Stone Ledge
    draw.rounded_rectangle([cx - 420, cy - 40, cx - 120, cy + 320], radius=45, fill=GREEN, outline=DARK, width=28)
    draw.rounded_rectangle([cx - 390, cy - 10, cx - 150, cy + 70], radius=25, fill=(120, 205, 90, 255))

    # Right Wooden / Stone Ledge (Clear Visible GAP between them)
    draw.rounded_rectangle([cx + 120, cy - 40, cx + 420, cy + 320], radius=45, fill=GREEN, outline=DARK, width=28)
    draw.rounded_rectangle([cx + 150, cy - 10, cx + 390, cy + 70], radius=25, fill=(120, 205, 90, 255))

    # Big Golden Jumping Arrow leaping across the GAP
    for a in range(195, 345, 16):
        rad = math.radians(a)
        px = cx + 190 * math.cos(rad)
        py = cy - 50 + 150 * math.sin(rad)
        draw.ellipse([px - 16, py - 16, px + 16, py + 16], fill=GOLD, outline=DARK, width=6)

    # Cheerful Jumping Frog at peak of leap
    fx, fy = cx, cy - 190
    draw.ellipse([fx - 110, fy - 80, fx + 110, fy + 80], fill=GREEN, outline=DARK, width=24)
    # Frog Eyes
    draw_outline_circle(draw, fx - 60, fy - 70, 42, GREEN, DARK, 18)
    draw_outline_circle(draw, fx + 60, fy - 70, 42, GREEN, DARK, 18)
    draw.ellipse([fx - 70, fy - 80, fx - 50, fy - 60], fill=DARK)
    draw.ellipse([fx + 50, fy - 80, fx + 70, fy - 60], fill=DARK)
    draw.arc([fx - 50, fy - 10, fx + 50, fy + 45], 10, 170, fill=DARK, width=16)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 7. MAT (Woven Banig Floor Mat with Red Slippers) ─────────────────────────
def craft_mat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2

    # Oval Woven Mat
    draw.ellipse([cx - 420, cy - 240, cx + 420, cy + 240], fill=GOLD, outline=DARK, width=32)
    draw.ellipse([cx - 360, cy - 180, cx + 360, cy + 180], fill=CREAM, outline=DARK, width=22)
    # Woven Rattan Stripes
    for x in range(-260, 320, 90):
        draw.line([(cx + x, cy - 170), (cx + x, cy + 170)], fill=ORANGE, width=16)

    # Red Slippers on Mat
    # Left Slipper
    lx, ly = cx - 120, cy
    draw.rounded_rectangle([lx - 75, ly - 115, lx + 75, ly + 105], radius=50, fill=RED, outline=DARK, width=24)
    draw.ellipse([lx - 50, ly - 85, lx + 50, ly - 5], fill=WHITE, outline=DARK, width=14)
    # Right Slipper
    rx, ry = cx + 120, cy
    draw.rounded_rectangle([rx - 75, ry - 115, rx + 75, ry + 105], radius=50, fill=RED, outline=DARK, width=24)
    draw.ellipse([rx - 50, ry - 85, rx + 50, ry - 5], fill=WHITE, outline=DARK, width=14)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 8. NAP (Child Tucked Snugly in Bed with Big Pillow & Zzz) ─────────────────
def craft_nap():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 30

    # Wooden Bed Headboard
    draw.rounded_rectangle([cx - 420, cy - 220, cx + 420, cy + 300], radius=55, fill=WOOD, outline=DARK, width=32)

    # Fluffy Cloud Pillow
    draw.rounded_rectangle([cx - 340, cy - 250, cx + 340, cy], radius=65, fill=WHITE, outline=DARK, width=28)

    # Child's Sleeping Head
    hx, hy = cx - 30, cy - 130
    draw.ellipse([hx - 160, hy - 140, hx + 160, hy + 140], fill=SKIN, outline=DARK, width=28)
    draw.arc([hx - 160, hy - 150, hx + 160, hy + 10], 180, 360, fill=HAIR_BROWN, width=70)

    # Sleeping Closed Eyelashes & Rosy Cheeks
    draw.arc([hx - 95, hy - 10, hx - 25, hy + 50], 10, 170, fill=DARK, width=18)
    draw.arc([hx + 25, hy - 10, hx + 95, hy + 50], 10, 170, fill=DARK, width=18)
    draw_blush_cheeks(draw, hx - 85, hy + 60, hx + 85, hy + 60, rw=42, rh=24)
    draw.arc([hx - 35, hy + 45, hx + 35, hy + 95], 10, 170, fill=DARK, width=14)

    # Star Quilt Blanket tucked up to chin
    draw.rounded_rectangle([cx - 420, cy - 40, cx + 420, cy + 300], radius=50, fill=BLUE_SKY, outline=DARK, width=32)
    draw.rounded_rectangle([cx - 420, cy - 40, cx + 420, cy + 60], radius=25, fill=GOLD, outline=DARK, width=20)
    for (qx, qy) in [(-240, 150), (-80, 200), (100, 140), (260, 190)]:
        draw_outline_circle(draw, cx + qx, cy + qy, 25, WHITE, DARK, 10)

    # Floating Sleep "Z z z"
    for (zx, zy, sz, col) in [(cx + 240, cy - 240, 60, PURPLE), (cx + 330, cy - 330, 85, ORANGE)]:
        draw.text((zx, zy), "Z", fill=col)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 9. BAT (Cute Purple Storybook Bat) ────────────────────────────────────────
def craft_bat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20

    # Scalloped Wings
    l_wing = [
        (cx - 60, cy + 80),
        (cx - 450, cy - 160),
        (cx - 350, cy + 40),
        (cx - 250, cy + 160),
        (cx - 140, cy + 180),
        (cx - 60, cy + 120)
    ]
    draw.polygon(l_wing, fill=PURPLE, outline=DARK)
    for i in range(len(l_wing)-1):
        draw.line([l_wing[i], l_wing[i+1]], fill=DARK, width=28, joint="curve")

    r_wing = [
        (cx + 60, cy + 80),
        (cx + 450, cy - 160),
        (cx + 350, cy + 40),
        (cx + 250, cy + 160),
        (cx + 140, cy + 180),
        (cx + 60, cy + 120)
    ]
    draw.polygon(r_wing, fill=PURPLE, outline=DARK)
    for i in range(len(r_wing)-1):
        draw.line([r_wing[i], r_wing[i+1]], fill=DARK, width=28, joint="curve")

    # Pointy Bat Ears
    draw.polygon([(cx - 160, cy - 70), (cx - 110, cy - 310), (cx - 20, cy - 140)], fill=PURPLE, outline=DARK)
    draw.polygon([(cx - 140, cy - 80), (cx - 110, cy - 250), (cx - 50, cy - 130)], fill=PINK_BLUSH)
    draw.polygon([(cx + 160, cy - 70), (cx + 110, cy - 310), (cx + 20, cy - 140)], fill=PURPLE, outline=DARK)
    draw.polygon([(cx + 140, cy - 80), (cx + 110, cy - 250), (cx + 50, cy - 130)], fill=PINK_BLUSH)

    # Bat Body & Belly
    draw_outline_circle(draw, cx, cy + 40, 190, PURPLE, DARK, 30)
    draw.ellipse([cx - 120, cy - 20, cx + 120, cy + 210], fill=CREAM)

    # Big Glossy Eyes & Cheeks
    draw_glossy_eyes(draw, cx - 80, cy - 10, cx + 80, cy - 10, r=38)
    draw_blush_cheeks(draw, cx - 145, cy + 45, cx + 145, cy + 45, rw=48, rh=28)

    # Happy Smile with Tiny White Fangs
    draw.arc([cx - 55, cy + 25, cx + 55, cy + 95], 10, 170, fill=DARK, width=18)
    draw.polygon([(cx - 26, cy + 65), (cx - 12, cy + 65), (cx - 19, cy + 90)], fill=WHITE, outline=DARK)
    draw.polygon([(cx + 12, cy + 65), (cx + 26, cy + 65), (cx + 19, cy + 90)], fill=WHITE, outline=DARK)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── Master Runner ─────────────────────────────────────────────────────────────

CRAFTERS = {
    "blendword_sam": craft_sam,
    "blendword_sis": craft_sis,
    "blendword_face": craft_face,
    "blendword_hand": craft_hand,
    "blendword_draw": craft_draw,
    "blendword_gap": craft_gap,
    "blendword_mat": craft_mat,
    "blendword_nap": craft_nap,
    "blendword_bat": craft_bat,
}

def main():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    target_dir = os.path.join(base_dir, "app", "src", "main", "assets", "images", "pictures")
    os.makedirs(target_dir, exist_ok=True)

    print("=" * 80)
    print("[*] Crafting 9 Master Kid-Friendly Blend It Word Illustrations...")
    print("=" * 80)

    for name, func in CRAFTERS.items():
        img = func()
        out_path = os.path.join(target_dir, f"{name}.png")
        img.save(out_path, "PNG", optimize=True)
        print(f"  [+] Master Crafted: {name}.png ({os.path.getsize(out_path)} bytes)")

    print("=" * 80)
    print("[*] All 9 master illustrations successfully generated!")

if __name__ == "__main__":
    main()
