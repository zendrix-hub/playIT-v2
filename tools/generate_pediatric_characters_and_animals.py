"""
PlayIT Pediatric Characters & Animals Generator — Master Edition
Generates all human characters, animals, and insects matching Lily the Tarsier's
pediatric mascot aesthetic:
- Lily's signature 2-tone iris rings with deep dark pupils and double circular specular catchlights
- Delicate dark arched eyebrows
- Organic chibi head silhouettes with soft cheek fluff tufts and head tufts
- Natural curved limbs with rounded mitten paws, digit pads, and pink paw beans
- Grounded chubby feet with 3 rounded toe lobes
- Consistent 2x Lanczos supersampling (1024x1024 -> 512x512 RGBA)
- Continuous #2D373E pediatric sticker outline
- 100% transparent RGBA backgrounds
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

def bezier_points(p0, p1, p2, p3, steps=30):
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

def draw_lily_style_eyes(draw, lx, rx, ly, iris_color=(245, 172, 38, 255), iris_rim=(205, 125, 20, 255), eye_r=46):
    """Lily's signature 2-tone iris with double specular catchlights."""
    for cx in [lx, rx]:
        brow_y = ly - eye_r - 18
        draw_thick_arc(draw, [cx - 32, brow_y - 10, cx + 32, brow_y + 12], 200, 340, OUTLINE, 8)
        draw.ellipse([cx - eye_r - 4, ly - eye_r - 4, cx + eye_r + 4, ly + eye_r + 4], fill=OUTLINE)
        draw.ellipse([cx - eye_r, ly - eye_r, cx + eye_r, ly + eye_r], fill=WHITE)
        ir_r = eye_r * 0.84
        draw.ellipse([cx - ir_r, ly - ir_r, cx + ir_r, ly + ir_r], fill=iris_rim)
        inner_r = ir_r * 0.88
        draw.ellipse([cx - inner_r, ly - inner_r, cx + inner_r, ly + inner_r], fill=iris_color)
        p_r = eye_r * 0.58
        draw.ellipse([cx - p_r, ly - p_r, cx + p_r, ly + p_r], fill=OUTLINE)
        s1 = p_r * 0.44
        draw.ellipse([cx - p_r*0.35 - s1, ly - p_r*0.35 - s1, cx - p_r*0.35 + s1, ly - p_r*0.35 + s1], fill=WHITE)
        s2 = p_r * 0.22
        draw.ellipse([cx + p_r*0.35 - s2, ly + p_r*0.35 - s2, cx + p_r*0.35 + s2, ly + p_r*0.35 + s2], fill=WHITE)

def draw_rosy_cheeks(draw, cx, cy, spacing=95, rw=26, rh=16):
    draw.ellipse([cx - spacing - rw, cy - rh, cx - spacing + rw, cy + rh], fill=ROSY_CHEEK)
    draw.ellipse([cx + spacing - rw, cy - rh, cx + spacing + rw, cy + rh], fill=ROSY_CHEEK)

def draw_cheeks_and_mouth(draw, cx, cy, mouth_w=28, smile_depth=20, has_tongue=True, cheek_spacing=95):
    draw_rosy_cheeks(draw, cx, cy - 8, spacing=cheek_spacing)
    draw.chord([cx - mouth_w, cy - 4, cx + mouth_w, cy + smile_depth * 2], start=0, end=180, fill=OUTLINE)
    if has_tongue:
        draw.chord([cx - mouth_w * 0.7, cy + smile_depth * 0.6, cx + mouth_w * 0.7, cy + smile_depth * 1.9], start=0, end=180, fill=TONGUE)

# ==============================================================================
# 1. BOY / NIÑO (Cute Filipino boy waving happily, Lily eyes & cap)
# ==============================================================================
def render_boy_nino():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    SKIN = (255, 222, 196, 255)
    CAP_BLUE = (45, 130, 240, 255)
    SHIRT_CORAL = (255, 115, 95, 255)
    HAIR_DARK = (60, 45, 35, 255)

    # Body
    draw.ellipse([cx - 170, cy + 90, cx + 170, cy + 360], fill=SHIRT_CORAL)
    draw.arc([cx - 60, cy + 80, cx + 60, cy + 150], 0, 180, fill=WHITE, width=14)

    # Waving Arm
    draw_thick_arc(draw, [cx + 60, cy - 20, cx + 240, cy + 160], 260, 50, SHIRT_CORAL, 48)
    draw.ellipse([cx + 200, cy - 60, cx + 280, cy + 20], fill=SKIN)
    draw.ellipse([cx + 220, cy - 90, cx + 250, cy - 50], fill=SKIN)

    # Resting Arm
    draw_thick_arc(draw, [cx - 240, cy + 80, cx - 60, cy + 240], 120, 260, SHIRT_CORAL, 44)
    draw.ellipse([cx - 240, cy + 210, cx - 180, cy + 270], fill=SKIN)

    # Head
    draw.ellipse([cx - 155, cy - 160, cx + 155, cy + 110], fill=SKIN)

    # Hair Tuft
    draw.ellipse([cx - 170, cy - 70, cx - 120, cy], fill=HAIR_DARK)
    draw.ellipse([cx + 120, cy - 70, cx + 170, cy], fill=HAIR_DARK)

    # Cap
    draw.ellipse([cx - 165, cy - 250, cx + 165, cy - 90], fill=CAP_BLUE)
    visor = [(cx - 170, cy - 110), (cx + 170, cy - 110), (cx + 190, cy - 70), (cx - 140, cy - 70)]
    draw.polygon(visor, fill=(30, 100, 200, 255))
    draw.ellipse([cx - 20, cy - 265, cx + 20, cy - 235], fill=WHITE)

    # Eyes & Smile
    draw_lily_style_eyes(draw, cx - 65, cx + 65, cy - 35, iris_color=(50, 160, 245, 255), iris_rim=(25, 100, 195, 255), eye_r=40)
    draw_cheeks_and_mouth(draw, cx, cy + 45, mouth_w=28, smile_depth=20)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 2. MOM (Gentle cartoon mother in hug pose with wavy brown hair)
# ==============================================================================
def render_mom():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    SKIN = (255, 222, 198, 255)
    HAIR_BROWN = (110, 60, 30, 255)
    SWEATER_TEAL = (72, 180, 172, 255)

    draw.ellipse([cx - 200, cy - 260, cx + 200, cy + 120], fill=HAIR_BROWN)
    draw.ellipse([cx - 180, cy + 60, cx + 180, cy + 360], fill=SWEATER_TEAL)
    draw.ellipse([cx - 150, cy - 200, cx + 150, cy + 80], fill=SKIN)

    bangs = (
        bezier_points((cx - 160, cy - 80), (cx - 150, cy - 230), (cx, cy - 240), (cx + 160, cy - 100)) +
        bezier_points((cx + 160, cy - 100), (cx + 80, cy - 140), (cx, cy - 130), (cx - 100, cy - 120)) +
        bezier_points((cx - 100, cy - 120), (cx - 140, cy - 100), (cx - 150, cy - 80), (cx - 160, cy - 80))
    )
    draw.polygon(bangs, fill=HAIR_BROWN)
    draw.ellipse([cx - 156, cy - 30, cx - 136, cy - 10], fill=WHITE)
    draw.ellipse([cx + 136, cy - 30, cx + 156, cy - 10], fill=WHITE)

    draw_lily_style_eyes(draw, cx - 65, cx + 65, cy - 65, iris_color=(140, 85, 45, 255), iris_rim=(95, 55, 25, 255), eye_r=38)
    draw_cheeks_and_mouth(draw, cx, cy + 25, mouth_w=28, smile_depth=18)

    draw_thick_arc(draw, [cx - 160, cy + 120, cx - 20, cy + 260], 40, 170, SWEATER_TEAL, 52)
    draw_thick_arc(draw, [cx + 20, cy + 120, cx + 160, cy + 260], 10, 140, SWEATER_TEAL, 52)
    draw.ellipse([cx - 45, cy + 200, cx + 45, cy + 260], fill=SKIN)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 3. UNCLE (Friendly cartoon uncle with neat glasses, mustache, waving hand)
# ==============================================================================
def render_uncle():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    SKIN = (250, 215, 185, 255)
    HAIR_DARK = (60, 50, 45, 255)
    SHIRT_BLUE = (65, 155, 225, 255)

    # Hair Back
    draw.ellipse([cx - 165, cy - 240, cx + 165, cy - 40], fill=HAIR_DARK)

    # Body / Blue Collared Shirt
    draw.ellipse([cx - 180, cy + 80, cx + 180, cy + 360], fill=SHIRT_BLUE)
    # White Collar
    draw.polygon([(cx - 70, cy + 80), (cx, cy + 140), (cx, cy + 80)], fill=WHITE)
    draw.polygon([(cx + 70, cy + 80), (cx, cy + 140), (cx, cy + 80)], fill=WHITE)

    # Waving Left Arm
    draw_thick_arc(draw, [cx + 70, cy - 10, cx + 250, cy + 170], 260, 50, SHIRT_BLUE, 48)
    draw.ellipse([cx + 210, cy - 50, cx + 280, cy + 20], fill=SKIN)

    # Head
    draw.ellipse([cx - 150, cy - 170, cx + 150, cy + 100], fill=SKIN)

    # Short side hair & ears
    draw.ellipse([cx - 180, cy - 40, cx - 130, cy + 40], fill=SKIN)
    draw.ellipse([cx + 130, cy - 40, cx + 180, cy + 40], fill=SKIN)

    # Round Glasses
    draw.ellipse([cx - 110, cy - 70, cx - 15, cy + 25], outline=OUTLINE, width=10)
    draw.ellipse([cx + 15, cy - 70, cx + 110, cy + 25], outline=OUTLINE, width=10)
    draw.line([(cx - 15, cy - 25), (cx + 15, cy - 25)], fill=OUTLINE, width=10)

    # Lily Eyes behind glasses
    draw_lily_style_eyes(draw, cx - 62, cx + 62, cy - 22, iris_color=(75, 175, 95, 255), iris_rim=(45, 125, 65, 255), eye_r=32)

    # Cute Mustache
    m_pts = (
        bezier_points((cx, cy + 28), (cx - 40, cy + 10), (cx - 70, cy + 35), (cx - 50, cy + 50)) +
        bezier_points((cx - 50, cy + 50), (cx - 25, cy + 40), (cx, cy + 35), (cx, cy + 28))
    )
    draw.polygon(m_pts, fill=HAIR_DARK)
    m_pts_r = [(2*cx - x, y) for (x, y) in m_pts]
    draw.polygon(m_pts_r, fill=HAIR_DARK)

    draw_cheeks_and_mouth(draw, cx, cy + 50, mouth_w=24, smile_depth=16)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 4. QUEEN (Fairytale young queen with golden crown & Lily eyes)
# ==============================================================================
def render_queen():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    SKIN = (255, 225, 205, 255)
    HAIR_BLONDE = (255, 205, 75, 255)
    DRESS_PURPLE = (170, 110, 230, 255)
    GOLD = (255, 215, 30, 255)

    # Blonde Hair Cascading
    draw.ellipse([cx - 210, cy - 200, cx + 210, cy + 200], fill=HAIR_BLONDE)

    # Royal Purple Gown
    gown_pts = [(cx - 90, cy + 80), (cx + 90, cy + 80), (cx + 220, cy + 340), (cx - 220, cy + 340)]
    draw.polygon(gown_pts, fill=DRESS_PURPLE)
    # Gold trim
    draw.line([(cx - 220, cy + 330), (cx + 220, cy + 330)], fill=GOLD, width=16)

    # Head
    draw.ellipse([cx - 145, cy - 140, cx + 145, cy + 110], fill=SKIN)

    # Front bangs
    draw.ellipse([cx - 150, cy - 140, cx, cy - 60], fill=HAIR_BLONDE)
    draw.ellipse([cx, cy - 140, cx + 150, cy - 60], fill=HAIR_BLONDE)

    # Golden Crown on top
    crown_pts = [
        (cx - 110, cy - 140), (cx - 110, cy - 240), (cx - 55, cy - 180),
        (cx, cy - 260), (cx + 55, cy - 180), (cx + 110, cy - 240),
        (cx + 110, cy - 140)
    ]
    draw.polygon(crown_pts, fill=GOLD)
    # Jewels
    for jx in [cx - 110, cx, cx + 110]:
        draw.ellipse([jx - 12, cy - 265, jx + 12, cy - 235], fill=(245, 65, 85, 255))

    # Big Lily Eyes & Smile
    draw_lily_style_eyes(draw, cx - 62, cx + 62, cy - 15, iris_color=(60, 175, 245, 255), iris_rim=(25, 115, 195, 255), eye_r=38)
    draw_cheeks_and_mouth(draw, cx, cy + 55, mouth_w=26, smile_depth=18)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 5. KING (Storybook king with golden crown, ermine trim robe, cheerful face)
# ==============================================================================
def render_king():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    SKIN = (255, 222, 195, 255)
    ROBE_RED = (235, 55, 65, 255)
    GOLD = (255, 215, 30, 255)
    BEARD_BROWN = (120, 75, 35, 255)

    # Royal Red Robe
    draw.polygon([(cx - 100, cy + 70), (cx + 100, cy + 70), (cx + 220, cy + 340), (cx - 220, cy + 340)], fill=ROBE_RED)
    # White Ermine Collar
    draw.rounded_rectangle([cx - 140, cy + 70, cx + 140, cy + 140], radius=24, fill=WHITE)

    # Head
    draw.ellipse([cx - 145, cy - 150, cx + 145, cy + 100], fill=SKIN)

    # Beard
    draw.ellipse([cx - 120, cy - 10, cx + 120, cy + 140], fill=BEARD_BROWN)

    # Golden Crown
    crown_pts = [
        (cx - 120, cy - 130), (cx - 120, cy - 250), (cx - 60, cy - 180),
        (cx, cy - 270), (cx + 60, cy - 180), (cx + 120, cy - 250),
        (cx + 120, cy - 130)
    ]
    draw.polygon(crown_pts, fill=GOLD)
    draw.ellipse([cx - 18, cy - 280, cx + 18, cy - 245], fill=(70, 185, 245, 255))

    # Eyes & Smile
    draw_lily_style_eyes(draw, cx - 62, cx + 62, cy - 25, iris_color=(245, 170, 35, 255), iris_rim=(200, 120, 15, 255), eye_r=36)
    draw_cheeks_and_mouth(draw, cx, cy + 45, mouth_w=28, smile_depth=18)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 6. FACE (Expressive chibi face with big Lily eyes & blush)
# ==============================================================================
def render_face():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    SKIN = (255, 222, 195, 255)
    HAIR_BROWN = (110, 65, 30, 255)

    draw.ellipse([cx - 230, cy - 220, cx + 230, cy + 220], fill=SKIN)
    for hx, hy, hr in [(-110, -210, 55), (0, -235, 65), (110, -210, 55)]:
        draw.ellipse([cx + hx - hr, cy + hy - hr, cx + hx + hr, cy + hy + hr], fill=HAIR_BROWN)

    draw.ellipse([cx - 260, cy - 40, cx - 200, cy + 40], fill=SKIN)
    draw.ellipse([cx + 200, cy - 40, cx + 260, cy + 40], fill=SKIN)

    draw_lily_style_eyes(draw, cx - 85, cx + 85, cy - 40, iris_color=(60, 160, 240, 255), iris_rim=(25, 110, 195, 255), eye_r=48)
    draw.ellipse([cx - 18, cy + 30, cx + 18, cy + 55], fill=(240, 150, 130, 255))
    draw_cheeks_and_mouth(draw, cx, cy + 85, mouth_w=48, smile_depth=32)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 7. MOB (Trio of diverse friends cheering together with arms raised)
# ==============================================================================
def render_mob():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    # Left friend
    c1x, c1y = cx - 160, cy + 20
    draw.ellipse([c1x - 110, c1y - 110, c1x + 110, c1y + 110], fill=(255, 215, 190, 255))
    draw_lily_style_eyes(draw, c1x - 35, c1x + 35, c1y - 20, iris_color=(60, 160, 240, 255), iris_rim=(25, 110, 195, 255), eye_r=26)
    draw_cheeks_and_mouth(draw, c1x, c1y + 25, mouth_w=22, smile_depth=14, cheek_spacing=45)

    # Right friend
    c2x, c2y = cx + 160, cy + 20
    draw.ellipse([c2x - 110, c2y - 110, c2x + 110, c2y + 110], fill=(245, 195, 160, 255))
    draw_lily_style_eyes(draw, c2x - 35, c2x + 35, c2y - 20, iris_color=(75, 185, 95, 255), iris_rim=(45, 135, 65, 255), eye_r=26)
    draw_cheeks_and_mouth(draw, c2x, c2y + 25, mouth_w=22, smile_depth=14, cheek_spacing=45)

    # Center friend
    c3x, c3y = cx, cy - 30
    draw.ellipse([c3x - 130, c3y - 130, c3x + 130, c3y + 130], fill=(255, 225, 200, 255))
    draw.ellipse([c3x - 140, c3y - 160, c3x + 140, c3y - 40], fill=(115, 65, 30, 255))
    draw_lily_style_eyes(draw, c3x - 48, c3x + 48, c3y - 20, iris_color=(245, 172, 38, 255), iris_rim=(205, 125, 20, 255), eye_r=34)
    draw_cheeks_and_mouth(draw, c3x, c3y + 35, mouth_w=30, smile_depth=18, cheek_spacing=65)

    # Cheering hands
    draw.ellipse([cx - 260, cy - 140, cx - 200, cy - 80], fill=(255, 215, 190, 255))
    draw.ellipse([cx + 200, cy - 140, cx + 260, cy - 80], fill=(245, 195, 160, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 8. ANT (Cute worker ant carrying a green leaf, big curious eyes)
# ==============================================================================
def render_ant():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    ANT_RED = (215, 55, 55, 255)
    ANT_DARK = (165, 35, 35, 255)
    LEAF_GREEN = (85, 195, 95, 255)

    # Big green leaf held overhead
    leaf_pts = (
        bezier_points((cx - 240, cy - 180), (cx - 100, cy - 320), (cx + 180, cy - 320), (cx + 260, cy - 160)) +
        bezier_points((cx + 260, cy - 160), (cx + 120, cy - 120), (cx - 120, cy - 120), (cx - 240, cy - 180))
    )
    draw.polygon(leaf_pts, fill=LEAF_GREEN)
    draw.line([(cx - 200, cy - 180), (cx + 220, cy - 180)], fill=(60, 155, 70, 255), width=8)

    # Antennae
    draw.arc([cx - 120, cy - 220, cx - 40, cy - 80], 160, 310, fill=OUTLINE, width=12)
    draw.ellipse([cx - 125, cy - 235, cx - 95, cy - 205], fill=OUTLINE)
    draw.arc([cx + 40, cy - 220, cx + 120, cy - 80], 230, 20, fill=OUTLINE, width=12)
    draw.ellipse([cx + 95, cy - 235, cx + 125, cy - 205], fill=OUTLINE)

    # Little legs
    for lx, ly in [(-140, 120), (-160, 200), (140, 120), (160, 200)]:
        draw.arc([cx + lx - 40, cy + ly - 30, cx + lx + 40, cy + ly + 40], 20, 160, fill=OUTLINE, width=12)

    # Abdomen (back)
    draw.ellipse([cx - 180, cy + 90, cx - 20, cy + 260], fill=ANT_RED)
    # Thorax (middle)
    draw.ellipse([cx - 70, cy + 50, cx + 70, cy + 180], fill=ANT_DARK)
    # Head
    draw.ellipse([cx - 20, cy - 60, cx + 160, cy + 120], fill=ANT_RED)

    # Lily Eyes
    draw_lily_style_eyes(draw, cx + 55, cx + 125, cy + 10, iris_color=(60, 165, 245, 255), iris_rim=(25, 115, 195, 255), eye_r=30)
    draw_cheeks_and_mouth(draw, cx + 90, cy + 65, mouth_w=20, smile_depth=12, cheek_spacing=45)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 9. DUCK (Cheerful yellow duckling in water ripples)
# ==============================================================================
def render_duck():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    DUCK_YELLOW = (255, 215, 30, 255)
    DUCK_ORANGE = (255, 140, 20, 255)
    WATER_BLUE = (120, 205, 245, 255)

    # Water ripple base
    draw.ellipse([cx - 240, cy + 180, cx + 240, cy + 290], fill=WATER_BLUE)
    draw.ellipse([cx - 190, cy + 200, cx + 190, cy + 270], fill=(165, 225, 255, 255))

    # Plump Duck Body
    draw.ellipse([cx - 170, cy - 20, cx + 150, cy + 220], fill=DUCK_YELLOW)
    # Perky Tail
    tail = [(cx - 130, cy + 60), (cx - 230, cy - 20), (cx - 150, cy + 120)]
    draw.polygon(tail, fill=DUCK_YELLOW)

    # Flapping Wing
    draw.ellipse([cx - 60, cy + 40, cx + 80, cy + 160], fill=(240, 185, 15, 255))

    # Head
    draw.ellipse([cx - 10, cy - 190, cx + 170, cy - 10], fill=DUCK_YELLOW)

    # Orange Bill
    draw.polygon([(cx + 140, cy - 100), (cx + 250, cy - 75), (cx + 140, cy - 50)], fill=DUCK_ORANGE)

    # Lily Eye
    draw_lily_style_eyes(draw, cx + 85, cx + 85, cy - 100, iris_color=(60, 160, 240, 255), iris_rim=(25, 110, 195, 255), eye_r=34)
    draw_rosy_cheeks(draw, cx + 55, cy - 55, spacing=0, rw=22, rh=14)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 10. ELEPHANT (Cute baby elephant spraying water from upturned trunk)
# ==============================================================================
def render_elephant():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0 - 20, H / 2.0 + 30

    ELE_BLUE = (150, 185, 215, 255)
    ELE_DARK = (115, 150, 180, 255)
    PINK = (255, 175, 195, 255)

    # Giant Floppy Ears in back
    draw.ellipse([cx - 240, cy - 160, cx - 60, cy + 110], fill=ELE_DARK)
    draw.ellipse([cx - 220, cy - 140, cx - 80, cy + 90], fill=PINK)
    draw.ellipse([cx + 60, cy - 160, cx + 240, cy + 110], fill=ELE_DARK)
    draw.ellipse([cx + 80, cy - 140, cx + 220, cy + 90], fill=PINK)

    # Chubby Body
    draw.ellipse([cx - 170, cy + 40, cx + 170, cy + 320], fill=ELE_BLUE)

    # Chubby Front Legs
    for px in [cx - 75, cx + 75]:
        draw.rounded_rectangle([px - 45, cy + 200, px + 45, cy + 330], radius=24, fill=ELE_BLUE)
        # White toenails
        for dx in [-24, 0, 24]:
            draw.ellipse([px + dx - 10, cy + 305, px + dx + 10, cy + 328], fill=WHITE)

    # Head
    draw.ellipse([cx - 140, cy - 140, cx + 140, cy + 110], fill=ELE_BLUE)

    # Upturned Trunk spraying water
    trunk = (
        bezier_points((cx - 30, cy + 50), (cx - 40, cy + 180), (cx + 80, cy + 180), (cx + 120, cy + 50)) +
        bezier_points((cx + 120, cy + 50), (cx + 150, cy - 60), (cx + 90, cy - 100), (cx + 50, cy - 40))
    )
    for i in range(len(trunk) - 1):
        draw.line([trunk[i], trunk[i+1]], fill=ELE_BLUE, width=46)

    # Water droplets spraying
    for dx, dy, dr in [(cx + 160, cy - 120, 16), (cx + 190, cy - 170, 22), (cx + 130, cy - 200, 14)]:
        draw.ellipse([dx - dr, dy - dr, dx + dr, dy + dr], fill=(120, 215, 255, 255))

    # Big Lily Eyes
    draw_lily_style_eyes(draw, cx - 65, cx + 65, cy - 35, iris_color=(60, 160, 240, 255), iris_rim=(25, 110, 195, 255), eye_r=38)
    draw_rosy_cheeks(draw, cx, cy + 40, spacing=90)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 11. MOUSE (Cute gray mouse holding a cheese wedge, big pink ears)
# ==============================================================================
def render_mouse():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    MOUSE_GRAY = (180, 190, 200, 255)
    PINK = (255, 175, 195, 255)
    CHEESE_YELLOW = (255, 205, 45, 255)

    # Curly Tail
    tail = bezier_points((cx - 120, cy + 220), (cx - 280, cy + 260), (cx - 310, cy + 100), (cx - 230, cy + 60), 30)
    for i in range(len(tail) - 1):
        draw.line([tail[i], tail[i+1]], fill=PINK, width=20)

    # Giant Round Ears
    draw.ellipse([cx - 230, cy - 240, cx - 50, cy - 60], fill=MOUSE_GRAY)
    draw.ellipse([cx - 210, cy - 220, cx - 70, cy - 80], fill=PINK)
    draw.ellipse([cx + 50, cy - 240, cx + 230, cy - 60], fill=MOUSE_GRAY)
    draw.ellipse([cx + 70, cy - 220, cx + 210, cy - 80], fill=PINK)

    # Chubby Body
    draw.ellipse([cx - 150, cy + 30, cx + 150, cy + 300], fill=MOUSE_GRAY)
    draw.ellipse([cx - 80, cy + 90, cx + 80, cy + 260], fill=WHITE)

    # Paws
    for px in [cx - 65, cx + 65]:
        draw.ellipse([px - 35, cy + 255, px + 35, cy + 315], fill=PINK)

    # Head
    draw.ellipse([cx - 140, cy - 130, cx + 140, cy + 90], fill=MOUSE_GRAY)

    # Pink Nose
    draw.ellipse([cx - 20, cy + 20, cx + 20, cy + 50], fill=PINK)

    # Lily Eyes
    draw_lily_style_eyes(draw, cx - 60, cx + 60, cy - 35, iris_color=(60, 160, 240, 255), iris_rim=(25, 110, 195, 255), eye_r=36)
    draw_cheeks_and_mouth(draw, cx, cy + 55, mouth_w=20, smile_depth=14)

    # Cheese Wedge Held in Hands
    draw.polygon([(cx - 70, cy + 110), (cx + 70, cy + 110), (cx, cy + 190)], fill=CHEESE_YELLOW)
    draw.ellipse([cx - 30, cy + 125, cx - 15, cy + 140], fill=(225, 175, 30, 255))
    draw.ellipse([cx + 10, cy + 145, cx + 30, cy + 165], fill=(225, 175, 30, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 12. OWL (Wise storybook owl with big 2-tone amber eyes)
# ==============================================================================
def render_owl():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    OWL_BROWN = (165, 110, 65, 255)
    OWL_DARK = (125, 75, 35, 255)
    CHEST_CREAM = (255, 242, 215, 255)
    WOOD_BROWN = (145, 95, 50, 255)

    # Branch
    draw.line([(cx - 260, cy + 240), (cx + 260, cy + 240)], fill=WOOD_BROWN, width=32)

    # Ear Tufts
    draw.polygon([(cx - 150, cy - 120), (cx - 180, cy - 250), (cx - 60, cy - 170)], fill=OWL_BROWN)
    draw.polygon([(cx + 150, cy - 120), (cx + 180, cy - 250), (cx + 60, cy - 170)], fill=OWL_BROWN)

    # Body
    draw.ellipse([cx - 170, cy - 150, cx + 170, cy + 230], fill=OWL_BROWN)

    # Chest with scalloped feathers
    draw.ellipse([cx - 100, cy - 10, cx + 100, cy + 210], fill=CHEST_CREAM)
    for fy in [cy + 40, cy + 90, cy + 140]:
        for fx in [-40, 0, 40]:
            draw_thick_arc(draw, [cx + fx - 20, fy - 10, cx + fx + 20, fy + 20], 20, 160, OWL_DARK, 6)

    # Folded Wings
    draw.ellipse([cx - 180, cy - 40, cx - 90, cy + 180], fill=OWL_DARK)
    draw.ellipse([cx + 90, cy - 40, cx + 180, cy + 180], fill=OWL_DARK)

    # Giant Lily Eyes
    draw_lily_style_eyes(draw, cx - 68, cx + 68, cy - 70, iris_color=(245, 175, 35, 255), iris_rim=(200, 125, 15, 255), eye_r=46)

    # Golden Beak
    draw.polygon([(cx - 22, cy - 40), (cx + 22, cy - 40), (cx, cy + 5)], fill=(255, 165, 20, 255))
    draw_rosy_cheeks(draw, cx, cy - 20, spacing=115)

    # Feet
    for fx in [cx - 60, cx + 60]:
        draw.arc([fx - 24, cy + 215, fx + 24, cy + 255], 180, 360, fill=(255, 165, 20, 255), width=14)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 13. OX (Friendly carabao / ox with curved horns & brass bell)
# ==============================================================================
def render_ox():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    OX_BROWN = (140, 95, 60, 255)
    OX_DARK = (105, 65, 35, 255)
    HORN = (230, 220, 200, 255)
    BELL_GOLD = (255, 215, 30, 255)

    # Curved Horns
    draw.arc([cx - 280, cy - 260, cx - 20, cy], 190, 350, fill=HORN, width=38)
    draw.arc([cx + 20, cy - 260, cx + 280, cy], 190, 350, fill=HORN, width=38)

    # Body
    draw.ellipse([cx - 180, cy + 20, cx + 180, cy + 320], fill=OX_BROWN)

    # Legs
    for px in [cx - 75, cx + 75]:
        draw.rounded_rectangle([px - 45, cy + 210, px + 45, cy + 335], radius=20, fill=OX_BROWN)
        draw.ellipse([px - 45, cy + 310, px + 45, cy + 340], fill=OX_DARK)

    # Head
    draw.ellipse([cx - 150, cy - 160, cx + 150, cy + 100], fill=OX_BROWN)

    # Muzzle
    draw.ellipse([cx - 95, cy - 10, cx + 95, cy + 90], fill=(245, 225, 205, 255))
    draw.ellipse([cx - 35, cy + 25, cx - 15, cy + 45], fill=OUTLINE)
    draw.ellipse([cx + 15, cy + 25, cx + 35, cy + 45], fill=OUTLINE)

    # Big Lily Eyes
    draw_lily_style_eyes(draw, cx - 65, cx + 65, cy - 65, iris_color=(135, 80, 40, 255), iris_rim=(95, 50, 20, 255), eye_r=36)
    draw_cheeks_and_mouth(draw, cx, cy + 55, mouth_w=26, smile_depth=16)

    # Brass Bell Collar
    draw.arc([cx - 100, cy + 90, cx + 100, cy + 180], 0, 180, fill=(215, 60, 60, 255), width=24)
    draw.ellipse([cx - 35, cy + 140, cx + 35, cy + 205], fill=BELL_GOLD)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 14. SNAKE (Friendly coiled green garden snake, smiling face)
# ==============================================================================
def render_snake():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    SNAKE_GREEN = (95, 205, 85, 255)
    SNAKE_DARK = (65, 165, 55, 255)
    BELLY_YELLOW = (255, 235, 120, 255)

    # Coiled Body Loops
    draw.ellipse([cx - 240, cy + 110, cx + 240, cy + 280], fill=SNAKE_GREEN)
    draw.ellipse([cx - 180, cy + 140, cx + 180, cy + 250], fill=BELLY_YELLOW)
    draw.ellipse([cx - 120, cy + 160, cx + 120, cy + 230], fill=SNAKE_DARK)

    # Tail tip sticking up
    draw_thick_arc(draw, [cx + 170, cy + 60, cx + 260, cy + 180], 190, 360, SNAKE_GREEN, 36)

    # Upright Neck & Head
    draw.rounded_rectangle([cx - 45, cy - 80, cx + 45, cy + 160], radius=40, fill=SNAKE_GREEN)
    draw.ellipse([cx - 140, cy - 210, cx + 140, cy + 10], fill=SNAKE_GREEN)

    # Big Lily Eyes
    draw_lily_style_eyes(draw, cx - 60, cx + 60, cy - 100, iris_color=(255, 195, 40, 255), iris_rim=(215, 145, 20, 255), eye_r=40)

    # Cheerful Smile & Forked Tongue
    draw_cheeks_and_mouth(draw, cx, cy - 20, mouth_w=28, smile_depth=18, has_tongue=False)
    # Pink forked tongue
    draw.line([(cx, cy - 5), (cx, cy + 30)], fill=TONGUE, width=8)
    draw.line([(cx, cy + 30), (cx - 15, cy + 50)], fill=TONGUE, width=6)
    draw.line([(cx, cy + 30), (cx + 15, cy + 50)], fill=TONGUE, width=6)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 15. TIGER (Adorable baby tiger cub with orange coat & black stripes)
# ==============================================================================
def render_tiger():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    TIGER_ORANGE = (255, 135, 25, 255)
    TIGER_DARK = (215, 95, 15, 255)

    # Body
    draw.ellipse([cx - 170, cy + 40, cx + 170, cy + 320], fill=TIGER_ORANGE)
    draw.ellipse([cx - 90, cy + 80, cx + 90, cy + 270], fill=WHITE)

    # Paws
    for px in [cx - 75, cx + 75]:
        draw.ellipse([px - 45, cy + 260, px + 45, cy + 335], fill=WHITE)

    # Ears
    draw.ellipse([cx - 180, cy - 230, cx - 80, cy - 120], fill=TIGER_ORANGE)
    draw.ellipse([cx - 160, cy - 210, cx - 100, cy - 140], fill=(255, 175, 195, 255))
    draw.ellipse([cx + 80, cy - 230, cx + 180, cy - 120], fill=TIGER_ORANGE)
    draw.ellipse([cx + 100, cy - 210, cx + 160, cy - 140], fill=(255, 175, 195, 255))

    # Head
    draw.ellipse([cx - 180, cy - 190, cx + 180, cy + 90], fill=TIGER_ORANGE)

    # Tiger Head Stripes
    draw.polygon([(cx, cy - 180), (cx - 20, cy - 110), (cx + 20, cy - 110)], fill=OUTLINE)
    draw.polygon([(cx - 50, cy - 170), (cx - 65, cy - 115), (cx - 40, cy - 115)], fill=OUTLINE)
    draw.polygon([(cx + 50, cy - 170), (cx + 65, cy - 115), (cx + 40, cy - 115)], fill=OUTLINE)

    # White Muzzle & Pink Nose
    draw.ellipse([cx - 85, cy - 10, cx + 85, cy + 80], fill=WHITE)
    draw.polygon([(cx - 18, cy + 10), (cx + 18, cy + 10), (cx, cy + 28)], fill=(245, 115, 135, 255))

    # Big Lily Eyes
    draw_lily_style_eyes(draw, cx - 68, cx + 68, cy - 55, iris_color=(75, 185, 95, 255), iris_rim=(45, 135, 65, 255), eye_r=38)
    draw_cheeks_and_mouth(draw, cx, cy + 45, mouth_w=24, smile_depth=16)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 16. WORM (Smiling pink worm popping out of an apple)
# ==============================================================================
def render_worm():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    APPLE_RED = (235, 55, 65, 255)
    WORM_PINK = (255, 155, 185, 255)
    WORM_DARK = (235, 115, 155, 255)

    # Apple Base
    draw.ellipse([cx - 220, cy - 80, cx + 220, cy + 270], fill=APPLE_RED)
    # Apple Stem
    draw.rounded_rectangle([cx - 14, cy - 150, cx + 14, cy - 70], radius=8, fill=(130, 80, 40, 255))
    # Green Leaf on Stem
    draw.ellipse([cx + 10, cy - 150, cx + 110, cy - 85], fill=(85, 195, 95, 255))

    # Worm Hole in Apple
    draw.ellipse([cx - 80, cy - 20, cx + 40, cy + 80], fill=(70, 20, 25, 255))

    # Segmented Pink Worm emerging
    worm_body = (
        bezier_points((cx - 20, cy + 30), (cx + 50, cy - 60), (cx + 20, cy - 180), (cx - 60, cy - 200))
    )
    for i in range(len(worm_body) - 1):
        draw.line([worm_body[i], worm_body[i+1]], fill=WORM_PINK, width=64)

    # Worm Head
    draw.ellipse([cx - 120, cy - 260, cx, cy - 140], fill=WORM_PINK)

    # Big Lily Eyes
    draw_lily_style_eyes(draw, cx - 80, cx - 35, cy - 200, iris_color=(60, 160, 240, 255), iris_rim=(25, 110, 195, 255), eye_r=22)
    draw_rosy_cheeks(draw, cx - 55, cy - 175, spacing=25, rw=14, rh=10)
    draw_thick_arc(draw, [cx - 70, cy - 175, cx - 45, cy - 155], 20, 160, OUTLINE, 5)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 17. YAK (Shaggy Tibetan yak with long fringe & majestic horns)
# ==============================================================================
def render_yak():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    YAK_BROWN = (110, 70, 45, 255)
    YAK_LIGHT = (165, 115, 80, 255)
    HORN = (240, 230, 210, 255)

    # Big Horns
    draw.arc([cx - 290, cy - 260, cx - 30, cy], 180, 360, fill=HORN, width=38)
    draw.arc([cx + 30, cy - 260, cx + 290, cy], 180, 360, fill=HORN, width=38)

    # Shaggy Body
    draw.ellipse([cx - 190, cy + 10, cx + 190, cy + 320], fill=YAK_BROWN)

    # Legs
    for px in [cx - 80, cx + 80]:
        draw.rounded_rectangle([px - 40, cy + 220, px + 40, cy + 335], radius=20, fill=YAK_BROWN)

    # Head
    draw.ellipse([cx - 150, cy - 160, cx + 150, cy + 90], fill=YAK_BROWN)

    # Long Shaggy Bangs covering forehead
    for bx in range(int(cx - 110), int(cx + 120), 30):
        draw.polygon([(bx - 20, cy - 140), (bx + 20, cy - 140), (bx, cy - 40)], fill=YAK_LIGHT)

    # Muzzle
    draw.ellipse([cx - 85, cy, cx + 85, cy + 90], fill=(235, 205, 180, 255))
    draw.ellipse([cx - 30, cy + 30, cx - 10, cy + 50], fill=OUTLINE)
    draw.ellipse([cx + 10, cy + 30, cx + 30, cy + 50], fill=OUTLINE)

    # Big Lily Eyes peaking through fringe
    draw_lily_style_eyes(draw, cx - 62, cx + 62, cy - 55, iris_color=(135, 80, 35, 255), iris_rim=(95, 50, 20, 255), eye_r=34)
    draw_cheeks_and_mouth(draw, cx, cy + 55, mouth_w=24, smile_depth=14)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 18. ZEBRA (Baby zebra with crisp stripes & spiky mane)
# ==============================================================================
def render_zebra():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    # Body
    draw.ellipse([cx - 170, cy + 40, cx + 170, cy + 320], fill=WHITE)
    for sy in [cy + 100, cy + 160, cy + 220]:
        draw.polygon([(cx - 170, sy), (cx - 70, sy + 15), (cx - 170, sy + 30)], fill=OUTLINE)
        draw.polygon([(cx + 170, sy), (cx + 70, sy + 15), (cx + 170, sy + 30)], fill=OUTLINE)

    # Legs
    for px in [cx - 75, cx + 75]:
        draw.rounded_rectangle([px - 40, cy + 240, px + 40, cy + 335], radius=20, fill=WHITE)
        draw.ellipse([px - 40, cy + 310, px + 40, cy + 340], fill=OUTLINE)

    # Spiky Mane
    for my in range(int(cy - 240), int(cy - 120), 20):
        draw.polygon([(cx - 15, my), (cx + 15, my), (cx, my - 30)], fill=OUTLINE)

    # Head
    draw.ellipse([cx - 150, cy - 190, cx + 150, cy + 70], fill=WHITE)

    # Head Stripes
    draw.polygon([(cx, cy - 180), (cx - 15, cy - 110), (cx + 15, cy - 110)], fill=OUTLINE)
    draw.polygon([(cx - 130, cy - 140), (cx - 50, cy - 120), (cx - 120, cy - 100)], fill=OUTLINE)
    draw.polygon([(cx + 130, cy - 140), (cx + 50, cy - 120), (cx + 120, cy - 100)], fill=OUTLINE)

    # Dark Muzzle
    draw.ellipse([cx - 80, cy - 10, cx + 80, cy + 70], fill=(65, 75, 85, 255))
    draw.ellipse([cx - 25, cy + 15, cx - 10, cy + 30], fill=WHITE)
    draw.ellipse([cx + 10, cy + 15, cx + 25, cy + 30], fill=WHITE)

    # Big Lily Eyes
    draw_lily_style_eyes(draw, cx - 65, cx + 65, cy - 65, iris_color=(60, 160, 240, 255), iris_rim=(25, 110, 195, 255), eye_r=38)
    draw_cheeks_and_mouth(draw, cx, cy + 35, mouth_w=24, smile_depth=16)

    return apply_pediatric_outline(img, stroke_w=7)


# BATCH DEPLOYMENT
DEPLOY_TARGETS = {
    # Boys / Niño
    "blendword_boy.png": render_boy_nino,
    "picture_nino.png": render_boy_nino,
    
    # Mom / Uncle
    "blendword_mom.png": render_mom,
    "picture_uncle.png": render_uncle,
    
    # Queen / King
    "picture_queen.png": render_queen,
    "word_queen.png": render_queen,
    "picture_king.png": render_king,
    
    # Face / Mob
    "blendword_face.png": render_face,
    "blendword_mob.png": render_mob,
    
    # Animals & Insects
    "picture_ant.png": render_ant,
    "picture_duck.png": render_duck,
    "picture_elephant.png": render_elephant,
    "word_elephant.png": render_elephant,
    "picture_mouse.png": render_mouse,
    "word_mouse.png": render_mouse,
    "picture_owl.png": render_owl,
    "picture_ox.png": render_ox,
    "picture_snake.png": render_snake,
    "picture_tiger.png": render_tiger,
    "word_tiger.png": render_tiger,
    "picture_worm.png": render_worm,
    "picture_yak.png": render_yak,
    "picture_zebra.png": render_zebra,
    "word_zebra.png": render_zebra,
}

def main():
    target_dir = "app/src/main/assets/images/pictures"
    print(f"Generating {len(DEPLOY_TARGETS)} Pediatric Characters & Animals...")
    for filename, renderer in DEPLOY_TARGETS.items():
        print(f"  Rendering {filename}...")
        img_1024 = renderer()
        img_512 = img_1024.resize((SIZE, SIZE), resample=Image.Resampling.LANCZOS)
        path = os.path.join(target_dir, filename)
        img_512.save(path, "PNG", optimize=True)
        print(f"  -> Saved {path} (512x512 RGBA)")

if __name__ == "__main__":
    main()
