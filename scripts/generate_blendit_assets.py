"""
PlayIT Blend It Assets Generator — 4-Benchmark Synthesis
(Duolingo ABC x Khan Academy Kids x Drops x Headspace)
Generates high-definition, 100% transparent vector illustrations for all 32 Blend It words.
Features:
- Pure transparent background (no artificial white square frame)
- Crisp continuous #2D373E slate-charcoal outlines
- Khan Academy Kids character warmth: Rosy cheeks, glossy expressive eyes, soft pastels
- Drops & Duolingo ABC simplicity: Bold, easily recognizable single-focus objects
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
MANGO_LIGHT = (255, 175, 125, 255)
CREAM = (255, 238, 215, 255)          # Soft Tummy / Muzzle Cream
GOLD_SUN = (255, 204, 0, 255)         # Bright Sunny Yellow
GOLD_DARK = (245, 166, 35, 255)
LEAF_GREEN = (76, 175, 80, 255)       # Fresh Green
SKY_BLUE = (56, 189, 248, 255)        # Clean Sky Blue
OCEAN_BLUE = (2, 132, 199, 255)
UBE_PURPLE = (139, 95, 191, 255)      # Royal Ube
GUAVA_RED = (255, 90, 110, 255)       # Warm Red / Guava
WOOD_BROWN = (180, 115, 65, 255)
WOOD_LIGHT = (210, 150, 95, 255)

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

# ── Asset Illustrators ─────────────────────────────────────────────────────────

def draw_aim():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20
    # Concentric Target Circles
    draw.ellipse([cx-320, cy-320, cx+320, cy+320], fill=GUAVA_RED, outline=DARK_OUTLINE, width=22)
    draw.ellipse([cx-240, cy-240, cx+240, cy+240], fill=WHITE, outline=DARK_OUTLINE, width=18)
    draw.ellipse([cx-160, cy-160, cx+160, cy+160], fill=SKY_BLUE, outline=DARK_OUTLINE, width=18)
    draw.ellipse([cx-80, cy-80, cx+80, cy+80], fill=GOLD_SUN, outline=DARK_OUTLINE, width=18)
    # Target Bullseye Face
    draw_face(draw, cx, cy, eye_spacing=30, eye_y_offset=-10, eye_r=12, smile_w=30, smile_h=16, blush=False)
    # Arrow hitting bullseye
    draw_thick_line(draw, (cx+180, cy-180), (cx+15, cy-15), DARK_OUTLINE, 24)
    draw_thick_line(draw, (cx+180, cy-180), (cx+15, cy-15), WOOD_BROWN, 16)
    # Arrow fletching
    draw.polygon([(cx+180, cy-180), (cx+230, cy-160), (cx+210, cy-210)], fill=GUAVA_RED, outline=DARK_OUTLINE)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_bam():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Comic Action Burst
    pts = []
    for i in range(16):
        a = math.radians(i * (360/16) - 90)
        r = 380 if i % 2 == 0 else 200
        pts.append((cx + r * math.cos(a), cy + r * math.sin(a)))
    draw.polygon(pts, fill=GOLD_SUN, outline=DARK_OUTLINE)
    for i in range(16):
        draw.line([pts[i], pts[(i+1)%16]], fill=DARK_OUTLINE, width=22, joint="curve")
    # Inner Burst
    pts2 = []
    for i in range(16):
        a = math.radians(i * (360/16) - 90)
        r = 250 if i % 2 == 0 else 130
        pts2.append((cx + r * math.cos(a), cy + r * math.sin(a)))
    draw.polygon(pts2, fill=MANGO)
    # Rosy Cheerful Face in center
    draw_face(draw, cx, cy, eye_spacing=50, eye_y_offset=-15, eye_r=20, smile_w=50, smile_h=26)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_bat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Cute Baseball Bat angled diagonally
    draw_thick_line(draw, (cx-180, cy+260), (cx+200, cy-240), DARK_OUTLINE, 58)
    draw_thick_line(draw, (cx-180, cy+260), (cx+200, cy-240), WOOD_BROWN, 44)
    # Grip wrap
    draw_thick_line(draw, (cx-180, cy+260), (cx-90, cy+150), WHITE, 46)
    draw_thick_line(draw, (cx-180, cy+260), (cx-90, cy+150), DARK_OUTLINE, 8)
    # Cheerful Baseball Ball
    bx, by = cx + 110, cy + 120
    draw.ellipse([bx-90, by-90, bx+90, by+90], fill=WHITE, outline=DARK_OUTLINE, width=18)
    draw.arc([bx-70, by-70, bx+70, by+70], 40, 140, fill=GUAVA_RED, width=8)
    draw.arc([bx-70, by-70, bx+70, by+70], 220, 320, fill=GUAVA_RED, width=8)
    draw_face(draw, bx, by, eye_spacing=26, eye_y_offset=-8, eye_r=10, smile_w=24, smile_h=14)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_bird():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20
    # Storybook Bluebird Body
    draw.ellipse([cx-190, cy-170, cx+170, cy+190], fill=SKY_BLUE, outline=DARK_OUTLINE, width=22)
    # Belly Cream
    draw.ellipse([cx-130, cy+10, cx+120, cy+180], fill=CREAM)
    # Wing
    draw.ellipse([cx-180, cy-20, cx-40, cy+140], fill=OCEAN_BLUE, outline=DARK_OUTLINE, width=16)
    # Beak
    draw.polygon([(cx+150, cy-30), (cx+240, cy), (cx+150, cy+30)], fill=GOLD_SUN, outline=DARK_OUTLINE)
    # Cheerful Face
    draw_face(draw, cx+50, cy-50, eye_spacing=40, eye_y_offset=0, eye_r=18, smile_w=30, smile_h=15)
    # Tail Feathers
    draw.polygon([(cx-180, cy+20), (cx-300, cy-40), (cx-240, cy+80)], fill=OCEAN_BLUE, outline=DARK_OUTLINE)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_box():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 30
    # Gift Box Body
    bw, bh = 240, 200
    draw.rectangle([cx-bw, cy-bh+50, cx+bw, cy+bh], fill=GOLD_SUN, outline=DARK_OUTLINE, width=22)
    # Lid
    draw.rectangle([cx-bw-20, cy-bh, cx+bw+20, cy-bh+60], fill=MANGO, outline=DARK_OUTLINE, width=22)
    # Ribbon Vertical & Horizontal
    draw.rectangle([cx-35, cy-bh+50, cx+35, cy+bh], fill=GUAVA_RED)
    draw.rectangle([cx-35, cy-bh, cx+35, cy-bh+60], fill=GUAVA_RED)
    # Ribbon Bow
    draw.ellipse([cx-90, cy-bh-70, cx-10, cy-bh+10], fill=GUAVA_RED, outline=DARK_OUTLINE, width=16)
    draw.ellipse([cx+10, cy-bh-70, cx+90, cy-bh+10], fill=GUAVA_RED, outline=DARK_OUTLINE, width=16)
    draw.ellipse([cx-25, cy-bh-30, cx+25, cy-bh+20], fill=GUAVA_RED, outline=DARK_OUTLINE, width=16)
    # Cheerful Box Face
    draw_face(draw, cx, cy+60, eye_spacing=65, eye_y_offset=0, eye_r=20, smile_w=55, smile_h=30)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_bus():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Cute School Bus Body
    draw.rounded_rectangle([cx-300, cy-180, cx+300, cy+140], radius=60, fill=GOLD_SUN, outline=DARK_OUTLINE, width=22)
    # Windows
    for wx in [-190, -40, 110]:
        draw.rounded_rectangle([cx+wx-55, cy-140, cx+wx+55, cy-30], radius=20, fill=SKY_BLUE, outline=DARK_OUTLINE, width=14)
    # Bus Grill & Bumper
    draw.rectangle([cx-320, cy+80, cx+320, cy+130], fill=CREAM, outline=DARK_OUTLINE, width=16)
    # Wheels
    for wx in [-180, 180]:
        draw.ellipse([cx+wx-65, cy+90, cx+wx+65, cy+220], fill=DARK_OUTLINE)
        draw.ellipse([cx+wx-35, cy+120, cx+wx+35, cy+190], fill=CREAM)
    # Bus Face
    draw_face(draw, cx, cy+40, eye_spacing=75, eye_y_offset=0, eye_r=18, smile_w=60, smile_h=26)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_cake():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 50
    # Layer 1 Base
    draw.rounded_rectangle([cx-240, cy+20, cx+240, cy+180], radius=35, fill=CREAM, outline=DARK_OUTLINE, width=22)
    # Layer 2 Top
    draw.rounded_rectangle([cx-170, cy-110, cx+170, cy+20], radius=30, fill=GUAVA_RED, outline=DARK_OUTLINE, width=22)
    # Frosting Drips
    for fx in [-190, -110, -30, 50, 130, 200]:
        draw.ellipse([cx+fx-25, cy+10, cx+fx+25, cy+60], fill=GUAVA_RED)
    # Candle
    draw.rectangle([cx-18, cy-220, cx+18, cy-110], fill=SKY_BLUE, outline=DARK_OUTLINE, width=14)
    # Flame
    draw.ellipse([cx-22, cy-280, cx+22, cy-220], fill=GOLD_SUN, outline=DARK_OUTLINE, width=12)
    # Face on Cake Base
    draw_face(draw, cx, cy+90, eye_spacing=65, eye_y_offset=0, eye_r=20, smile_w=55, smile_h=28)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_cat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Cat Ears
    draw.polygon([(cx-220, cy-80), (cx-150, cy-280), (cx-50, cy-170)], fill=MANGO, outline=DARK_OUTLINE)
    draw.polygon([(cx-190, cy-90), (cx-150, cy-240), (cx-80, cy-160)], fill=PINK_CHEEK)
    draw.polygon([(cx+220, cy-80), (cx+150, cy-280), (cx+50, cy-170)], fill=MANGO, outline=DARK_OUTLINE)
    draw.polygon([(cx+190, cy-90), (cx+150, cy-240), (cx+80, cy-160)], fill=PINK_CHEEK)
    # Head
    draw.ellipse([cx-250, cy-210, cx+250, cy+230], fill=MANGO, outline=DARK_OUTLINE, width=22)
    # Cream Muzzle
    draw.ellipse([cx-170, cy-80, cx+170, cy+210], fill=CREAM)
    # Cheerful Face
    draw_face(draw, cx, cy-30, eye_spacing=85, eye_y_offset=0, eye_r=26, smile_w=70, smile_h=35)
    # Tiny Nose
    draw.polygon([(cx-15, cy+30), (cx+15, cy+30), (cx, cy+48)], fill=DARK_OUTLINE)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_draw():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Crayon angled
    draw_thick_line(draw, (cx-180, cy+220), (cx+140, cy-180), DARK_OUTLINE, 58)
    draw_thick_line(draw, (cx-180, cy+220), (cx+140, cy-180), GUAVA_RED, 44)
    # Crayon Tip
    draw.polygon([(cx+140, cy-180), (cx+220, cy-240), (cx+180, cy-140)], fill=GUAVA_RED, outline=DARK_OUTLINE)
    # Drawing Sparkle Star
    sx, sy = cx - 140, cy - 140
    draw.ellipse([sx-70, sy-70, sx+70, sy+70], fill=GOLD_SUN, outline=DARK_OUTLINE, width=16)
    draw_face(draw, sx, sy, eye_spacing=22, eye_y_offset=-6, eye_r=8, smile_w=20, smile_h=12)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_face_asset():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Big Radiant Smiling Face
    draw.ellipse([cx-270, cy-270, cx+270, cy+270], fill=GOLD_SUN, outline=DARK_OUTLINE, width=24)
    draw_face(draw, cx, cy, eye_spacing=95, eye_y_offset=-25, eye_r=32, smile_w=120, smile_h=60)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_fan():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 - 40
    # Fan Cage Outer
    draw.ellipse([cx-240, cy-240, cx+240, cy+240], fill=WHITE, outline=DARK_OUTLINE, width=22)
    # 4 Fan Blades
    for a in [0, 90, 180, 270]:
        rad = math.radians(a)
        bx = cx + 110 * math.cos(rad)
        by = cy + 110 * math.sin(rad)
        draw.ellipse([bx-55, by-55, bx+55, by+55], fill=SKY_BLUE, outline=DARK_OUTLINE, width=12)
    # Center Hub Face
    draw.ellipse([cx-75, cy-75, cx+75, cy+75], fill=GOLD_SUN, outline=DARK_OUTLINE, width=16)
    draw_face(draw, cx, cy, eye_spacing=24, eye_y_offset=-6, eye_r=8, smile_w=22, smile_h=12, blush=False)
    # Stand Base
    draw.rectangle([cx-25, cy+240, cx+25, cy+380], fill=DARK_OUTLINE)
    draw.rounded_rectangle([cx-140, cy+360, cx+140, cy+420], radius=25, fill=SKY_BLUE, outline=DARK_OUTLINE, width=18)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_fish():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2 - 20, H/2
    # Fish Body Oval
    draw.ellipse([cx-230, cy-170, cx+180, cy+170], fill=MANGO, outline=DARK_OUTLINE, width=22)
    # White Clownfish Stripes
    draw.rectangle([cx-60, cy-160, cx, cy+160], fill=WHITE, outline=DARK_OUTLINE, width=14)
    # Tail Fin
    draw.polygon([(cx-190, cy), (cx-320, cy-130), (cx-280, cy), (cx-320, cy+130)], fill=MANGO, outline=DARK_OUTLINE)
    # Top & Bottom Fins
    draw.ellipse([cx-30, cy-230, cx+60, cy-130], fill=MANGO, outline=DARK_OUTLINE, width=14)
    draw.ellipse([cx-30, cy+130, cx+60, cy+230], fill=MANGO, outline=DARK_OUTLINE, width=14)
    # Cheerful Face
    draw_face(draw, cx+90, cy-20, eye_spacing=35, eye_y_offset=0, eye_r=22, smile_w=40, smile_h=22)
    # Air Bubbles
    for (bx, by, r) in [(cx+220, cy-120, 24), (cx+270, cy-180, 18), (cx+240, cy-240, 14)]:
        draw.ellipse([bx-r, by-r, bx+r, by+r], fill=SKY_BLUE, outline=DARK_OUTLINE, width=8)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_fox():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Fox Big Ears
    draw.polygon([(cx-230, cy-70), (cx-170, cy-300), (cx-40, cy-160)], fill=MANGO, outline=DARK_OUTLINE)
    draw.polygon([(cx-190, cy-80), (cx-160, cy-250), (cx-70, cy-150)], fill=DARK_OUTLINE)
    draw.polygon([(cx+230, cy-70), (cx+170, cy-300), (cx+40, cy-160)], fill=MANGO, outline=DARK_OUTLINE)
    draw.polygon([(cx+190, cy-80), (cx+160, cy-250), (cx+70, cy-150)], fill=DARK_OUTLINE)
    # Head Heart/Diamond
    draw.ellipse([cx-250, cy-180, cx+250, cy+210], fill=MANGO, outline=DARK_OUTLINE, width=22)
    # White Cheeks & Muzzle
    draw.polygon([(cx, cy+170), (cx-220, cy+10), (cx-100, cy-60), (cx, cy), (cx+100, cy-60), (cx+220, cy+10)], fill=WHITE, outline=DARK_OUTLINE)
    # Cheerful Face
    draw_face(draw, cx, cy-30, eye_spacing=85, eye_y_offset=0, eye_r=24, smile_w=65, smile_h=30)
    # Nose
    draw.ellipse([cx-20, cy+130, cx+20, cy+165], fill=DARK_OUTLINE)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_gap():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Left Platform
    draw.rounded_rectangle([cx-340, cy-60, cx-60, cy+220], radius=30, fill=LEAF_GREEN, outline=DARK_OUTLINE, width=22)
    # Right Platform (Showing clear stepping GAP between them)
    draw.rounded_rectangle([cx+60, cy-60, cx+340, cy+220], radius=30, fill=LEAF_GREEN, outline=DARK_OUTLINE, width=22)
    # Wooden bridge plank arching over gap
    draw.arc([cx-140, cy-140, cx+140, cy+60], 180, 360, fill=WOOD_BROWN, width=32)
    # Happy Flag on right
    draw_thick_line(draw, (cx+200, cy-60), (cx+200, cy-220), DARK_OUTLINE, 14)
    draw.polygon([(cx+200, cy-220), (cx+290, cy-175), (cx+200, cy-130)], fill=GUAVA_RED, outline=DARK_OUTLINE)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_hand():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Friendly Waving Mitten Hand
    draw.ellipse([cx-150, cy-80, cx+150, cy+240], fill=CREAM, outline=DARK_OUTLINE, width=22)
    # 4 Fingers
    for fx in [-100, -35, 35, 100]:
        draw.rounded_rectangle([cx+fx-26, cy-230, cx+fx+26, cy-30], radius=26, fill=CREAM, outline=DARK_OUTLINE, width=18)
    # Thumb
    draw.rounded_rectangle([cx-210, cy-20, cx-90, cy+70], radius=35, fill=CREAM, outline=DARK_OUTLINE, width=18)
    # Happy Face on Palm
    draw_face(draw, cx, cy+70, eye_spacing=50, eye_y_offset=0, eye_r=16, smile_w=45, smile_h=22)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_kit():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 30
    # First Aid / Tool Kit
    draw.rounded_rectangle([cx-260, cy-150, cx+260, cy+180], radius=45, fill=GUAVA_RED, outline=DARK_OUTLINE, width=22)
    # Handle
    draw.arc([cx-100, cy-270, cx+100, cy-110], 180, 360, fill=DARK_OUTLINE, width=26)
    # White Cross
    draw.rectangle([cx-30, cy-80, cx+30, cy+80], fill=WHITE)
    draw.rectangle([cx-80, cy-30, cx+80, cy+30], fill=WHITE)
    # Face
    draw_face(draw, cx, cy+100, eye_spacing=65, eye_y_offset=0, eye_r=16, smile_w=50, smile_h=22, blush=False)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_lit():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 40
    # Lit Candle & Lantern Glow
    draw.rounded_rectangle([cx-100, cy-80, cx+100, cy+200], radius=30, fill=WHITE, outline=DARK_OUTLINE, width=22)
    # Wick
    draw_thick_line(draw, (cx, cy-80), (cx, cy-130), DARK_OUTLINE, 14)
    # Glowing Flame
    draw.ellipse([cx-70, cy-290, cx+70, cy-120], fill=GOLD_SUN, outline=DARK_OUTLINE, width=18)
    draw.ellipse([cx-40, cy-250, cx+40, cy-140], fill=MANGO)
    # Cheerful Candle Face
    draw_face(draw, cx, cy+40, eye_spacing=45, eye_y_offset=0, eye_r=16, smile_w=40, smile_h=20)
    # Sparkle rays
    for a in [0, 45, 90, 135, 180, 225, 270, 315]:
        rad = math.radians(a)
        draw_thick_line(draw, (cx + 150*math.cos(rad), (cy-200) + 150*math.sin(rad)),
                              (cx + 180*math.cos(rad), (cy-200) + 180*math.sin(rad)), GOLD_SUN, 12)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_mat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Woven Banig / Floor Mat
    draw.rounded_rectangle([cx-290, cy-170, cx+290, cy+170], radius=30, fill=GOLD_SUN, outline=DARK_OUTLINE, width=22)
    # Rattan weave stripes
    for x in range(-230, 260, 80):
        draw_thick_line(draw, (cx+x, cy-160), (cx+x, cy+160), MANGO, 14)
    draw.rounded_rectangle([cx-180, cy-80, cx+180, cy+80], radius=20, fill=CREAM, outline=DARK_OUTLINE, width=14)
    draw_face(draw, cx, cy, eye_spacing=55, eye_y_offset=-10, eye_r=16, smile_w=45, smile_h=22)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_mob():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Cheerful crowd of friendly animal heads
    positions = [(-160, -40, SKY_BLUE), (160, -40, MANGO), (0, 70, GOLD_SUN)]
    for (px, py, col) in positions:
        draw.ellipse([cx+px-115, cy+py-115, cx+px+115, cy+py+115], fill=col, outline=DARK_OUTLINE, width=18)
        draw_face(draw, cx+px, cy+py, eye_spacing=35, eye_y_offset=-10, eye_r=14, smile_w=35, smile_h=18)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_nap():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20
    # Soft Fluffy Sleeping Pillow
    draw.rounded_rectangle([cx-280, cy-160, cx+280, cy+160], radius=80, fill=SKY_BLUE, outline=DARK_OUTLINE, width=22)
    # Sleeping peaceful curved eyes
    draw.arc([cx-130, cy-40, cx-50, cy+20], 10, 170, fill=DARK_OUTLINE, width=16)
    draw.arc([cx+50, cy-40, cx+130, cy+20], 10, 170, fill=DARK_OUTLINE, width=16)
    draw.ellipse([cx-130, cy+20, cx-90, cy+45], fill=PINK_CHEEK)
    draw.ellipse([cx+90, cy+20, cx+130, cy+45], fill=PINK_CHEEK)
    # Gentle smile
    draw.arc([cx-35, cy+20, cx+35, cy+60], 10, 170, fill=DARK_OUTLINE, width=12)
    # Sleep ZZZs
    for (zx, zy, sz) in [(cx+180, cy-180, 40), (cx+240, cy-240, 50)]:
        draw.text((zx, zy), "Z", fill=UBE_PURPLE)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_pan():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2 - 40, H/2
    # Skillet Pan Circle
    draw.ellipse([cx-210, cy-210, cx+210, cy+210], fill=DARK_OUTLINE)
    draw.ellipse([cx-180, cy-180, cx+180, cy+180], fill=(80, 95, 105, 255))
    # Handle
    draw_thick_line(draw, (cx+180, cy), (cx+340, cy), DARK_OUTLINE, 45)
    draw_thick_line(draw, (cx+180, cy), (cx+340, cy), WOOD_BROWN, 32)
    # Sunny Side Up Egg
    draw.ellipse([cx-110, cy-100, cx+110, cy+110], fill=WHITE, outline=DARK_OUTLINE, width=14)
    draw.ellipse([cx-55, cy-55, cx+55, cy+55], fill=GOLD_SUN, outline=DARK_OUTLINE, width=12)
    draw_face(draw, cx, cy, eye_spacing=20, eye_y_offset=-5, eye_r=7, smile_w=18, smile_h=10, blush=False)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_pig():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Pig Ears
    draw.polygon([(cx-220, cy-110), (cx-160, cy-270), (cx-60, cy-180)], fill=PINK_CHEEK, outline=DARK_OUTLINE)
    draw.polygon([(cx+220, cy-110), (cx+160, cy-270), (cx+60, cy-180)], fill=PINK_CHEEK, outline=DARK_OUTLINE)
    # Head
    draw.ellipse([cx-250, cy-220, cx+250, cy+230], fill=PINK_CHEEK, outline=DARK_OUTLINE, width=22)
    # Pig Snout
    draw.ellipse([cx-100, cy+10, cx+100, cy+130], fill=(255, 170, 180, 255), outline=DARK_OUTLINE, width=16)
    draw.ellipse([cx-50, cy+50, cx-20, cy+90], fill=DARK_OUTLINE)
    draw.ellipse([cx+20, cy+50, cx+50, cy+90], fill=DARK_OUTLINE)
    # Cheerful Eyes & Blush
    draw_face(draw, cx, cy-60, eye_spacing=85, eye_y_offset=0, eye_r=22, smile_w=50, smile_h=20, blush=False)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_quiz():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Quiz Paper
    draw.rounded_rectangle([cx-220, cy-260, cx+180, cy+260], radius=35, fill=WHITE, outline=DARK_OUTLINE, width=22)
    # Quiz Checkmarks
    for qy in [-140, -20, 100]:
        draw_thick_line(draw, (cx-150, cy+qy), (cx-110, cy+qy+30), LEAF_GREEN, 20)
        draw_thick_line(draw, (cx-110, cy+qy+30), (cx-50, cy+qy-30), LEAF_GREEN, 20)
        draw.rectangle([cx-20, cy+qy-10, cx+120, cy+qy+10], fill=SKY_BLUE)
    # Cheerful Pencil
    draw_thick_line(draw, (cx+180, cy-180), (cx+260, cy+180), GOLD_SUN, 40)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_road():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Rolling Green Hills
    draw.ellipse([cx-400, cy-100, cx+200, cy+380], fill=LEAF_GREEN, outline=DARK_OUTLINE, width=20)
    draw.ellipse([cx-100, cy-160, cx+450, cy+380], fill=(120, 200, 90, 255), outline=DARK_OUTLINE, width=20)
    # Winding Road
    draw.polygon([(cx-80, cy-140), (cx+80, cy-140), (cx+260, cy+320), (cx-260, cy+320)], fill=(100, 115, 125, 255), outline=DARK_OUTLINE)
    # Center Dashed Line
    draw_thick_line(draw, (cx, cy-120), (cx, cy+300), GOLD_SUN, 16)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_sam():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Hair
    draw.ellipse([cx-180, cy-260, cx+180, cy-40], fill=WOOD_BROWN, outline=DARK_OUTLINE, width=22)
    # Boy Head
    draw.ellipse([cx-190, cy-180, cx+190, cy+200], fill=CREAM, outline=DARK_OUTLINE, width=22)
    draw.polygon([(cx-190, cy-140), (cx-100, cy-60), (cx, cy-120), (cx+100, cy-60), (cx+190, cy-140)], fill=WOOD_BROWN)
    draw_face(draw, cx, cy, eye_spacing=75, eye_y_offset=-10, eye_r=22, smile_w=65, smile_h=32)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_sis():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Pigtails
    draw.ellipse([cx-280, cy-160, cx-120, cy], fill=MANGO, outline=DARK_OUTLINE, width=18)
    draw.ellipse([cx+120, cy-160, cx+280, cy], fill=MANGO, outline=DARK_OUTLINE, width=18)
    # Girl Head
    draw.ellipse([cx-190, cy-180, cx+190, cy+200], fill=CREAM, outline=DARK_OUTLINE, width=22)
    draw.arc([cx-190, cy-240, cx+190, cy-40], 0, 180, fill=MANGO, width=50)
    draw_face(draw, cx, cy, eye_spacing=75, eye_y_offset=-10, eye_r=22, smile_w=65, smile_h=32)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_spin():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Colorful Spinning Top Toy
    draw.polygon([(cx, cy-220), (cx+220, cy-40), (cx, cy+240), (cx-220, cy-40)], fill=GUAVA_RED, outline=DARK_OUTLINE)
    draw.polygon([(cx, cy-120), (cx+140, cy-40), (cx, cy+140), (cx-140, cy-40)], fill=GOLD_SUN)
    draw_face(draw, cx, cy-40, eye_spacing=45, eye_y_offset=0, eye_r=16, smile_w=40, smile_h=20)
    # Motion Swirls
    draw.arc([cx-260, cy-180, cx+260, cy+180], 30, 150, fill=SKY_BLUE, width=18)
    draw.arc([cx-260, cy-180, cx+260, cy+180], 210, 330, fill=SKY_BLUE, width=18)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_sub():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Yellow Submarine
    draw.ellipse([cx-260, cy-140, cx+220, cy+160], fill=GOLD_SUN, outline=DARK_OUTLINE, width=22)
    # Periscope Top
    draw.rectangle([cx-40, cy-240, cx+20, cy-120], fill=MANGO, outline=DARK_OUTLINE, width=16)
    draw.rectangle([cx-40, cy-260, cx+80, cy-210], fill=MANGO, outline=DARK_OUTLINE, width=16)
    # Porthole Windows
    for px in [-120, -10, 100]:
        draw.ellipse([cx+px-40, cy-40, cx+px+40, cy+40], fill=SKY_BLUE, outline=DARK_OUTLINE, width=14)
    # Propeller
    draw.polygon([(cx-250, cy), (cx-320, cy-70), (cx-300, cy), (cx-320, cy+70)], fill=MANGO, outline=DARK_OUTLINE)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_sum():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Math Blocks (1 + 2 = 3)
    # Block 1
    draw.rounded_rectangle([cx-280, cy-140, cx-120, cy+140], radius=25, fill=SKY_BLUE, outline=DARK_OUTLINE, width=18)
    draw.text((cx-225, cy-70), "1", fill=WHITE)
    # Plus Sign
    draw_thick_line(draw, (cx-80, cy), (cx-20, cy), DARK_OUTLINE, 18)
    draw_thick_line(draw, (cx-50, cy-30), (cx-50, cy+30), DARK_OUTLINE, 18)
    # Block 2
    draw.rounded_rectangle([cx+20, cy-140, cx+180, cy+140], radius=25, fill=GOLD_SUN, outline=DARK_OUTLINE, width=18)
    draw.text((cx+75, cy-70), "2", fill=WHITE)
    # Happy Face on Block 2
    draw_face(draw, cx+100, cy+60, eye_spacing=26, eye_y_offset=0, eye_r=8, smile_w=24, smile_h=12, blush=False)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_van():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Cute Delivery Van
    draw.rounded_rectangle([cx-290, cy-160, cx+280, cy+130], radius=50, fill=SKY_BLUE, outline=DARK_OUTLINE, width=22)
    # Front Windshield & Side Window
    draw.rounded_rectangle([cx+80, cy-130, cx+240, cy-20], radius=20, fill=WHITE, outline=DARK_OUTLINE, width=14)
    draw.rounded_rectangle([cx-80, cy-130, cx+50, cy-20], radius=20, fill=WHITE, outline=DARK_OUTLINE, width=14)
    # Wheels
    for wx in [-170, 160]:
        draw.ellipse([cx+wx-60, cy+80, cx+wx+60, cy+200], fill=DARK_OUTLINE)
        draw.ellipse([cx+wx-30, cy+110, cx+wx+30, cy+170], fill=CREAM)
    # Face on Front
    draw_face(draw, cx+160, cy+40, eye_spacing=45, eye_y_offset=0, eye_r=14, smile_w=35, smile_h=18)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_warm():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    # Warm Smiling Sun
    draw.ellipse([cx-210, cy-210, cx+210, cy+210], fill=GOLD_SUN, outline=DARK_OUTLINE, width=22)
    # Sun Rays
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
    # Zoo Arch Entrance
    draw.rounded_rectangle([cx-260, cy-180, cx+260, cy+220], radius=40, fill=WOOD_BROWN, outline=DARK_OUTLINE, width=22)
    draw.rounded_rectangle([cx-160, cy-60, cx+160, cy+220], radius=40, fill=WHITE, outline=DARK_OUTLINE, width=18)
    # Zoo Signboard Banner
    draw.rounded_rectangle([cx-220, cy-260, cx+220, cy-120], radius=25, fill=GOLD_SUN, outline=DARK_OUTLINE, width=18)
    # Friendly Lion Head peeking
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
    print("[*] Generating all 32 Blend It Word Illustrations (4-Benchmark Synthesis)...")
    print("=" * 80)

    for name, func in GENERATORS.items():
        img = func()
        out_path = os.path.join(target_dir, f"{name}.png")
        img.save(out_path, "PNG", optimize=True)
        print(f"  [+] Generated: {name}.png ({os.path.getsize(out_path)} bytes)")

    print("=" * 80)
    print("[*] All 32 Blend It word assets successfully updated with 4-Benchmark style!")

if __name__ == "__main__":
    main()
