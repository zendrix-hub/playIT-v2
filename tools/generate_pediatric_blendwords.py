"""
PlayIT Pediatric Blend-It Words Suite Generator
Reworks all 36 remaining blend word illustration pictures to commercial-grade
Duolingo ABC pediatric standards with 2x Lanczos supersampling (1024x1024 -> 512x512),
layered 3-tone shading, expressive storybook charm, and #2D373E continuous outlines.
"""

import os
import math
from PIL import Image, ImageDraw, ImageFilter

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

def draw_pediatric_eyes(draw, lx, rx, ly, iris_color=(60, 160, 240, 255), pupil_r=42):
    """Duolingo ABC pediatric eyes with rich colored iris and double catchlights."""
    for cx in [lx, rx]:
        # Eyebrow
        brow_y = ly - pupil_r - 16
        draw_thick_arc(draw, [cx - 28, brow_y - 8, cx + 28, brow_y + 12], 200, 340, OUTLINE, 7)
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
    draw.ellipse([cx - 95 - 22, cy - 8 - 14, cx - 95 + 22, cy - 8 + 14], fill=ROSY_CHEEK)
    draw.ellipse([cx + 95 - 22, cy - 8 - 14, cx + 95 + 22, cy - 8 + 14], fill=ROSY_CHEEK)
    draw.chord([cx - mouth_w, cy - 4, cx + mouth_w, cy + smile_depth * 2], start=0, end=180, fill=OUTLINE)
    if has_tongue:
        draw.chord([cx - mouth_w * 0.7, cy + smile_depth * 0.6, cx + mouth_w * 0.7, cy + smile_depth * 1.9], start=0, end=180, fill=TONGUE)

# ==============================================================================
# 1. BUS (Cheerful yellow school bus with cute eyes and smiling grille)
# ==============================================================================
def render_bus():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 10

    BUS_YELLOW = (255, 204, 0, 255)
    BUS_SHADOW = (220, 165, 0, 255)
    BUS_LIGHT = (255, 225, 75, 255)
    WINDOW_BLUE = (195, 235, 255, 255)
    TIRE_BLACK = (50, 58, 65, 255)
    HUB_GRAY = (220, 225, 230, 255)

    # Bus Main Body (Rounded rectangle)
    x0, y0, x1, y1 = cx - 360, cy - 180, cx + 360, cy + 180
    draw.rounded_rectangle([x0, y0, x1, y1], radius=60, fill=BUS_YELLOW)
    # Bottom stripe shadow
    draw.rounded_rectangle([x0, cy + 110, x1, y1], radius=40, fill=BUS_SHADOW)
    # Dark side rub-rail strips
    draw.line([(x0 + 20, cy + 40), (x1 - 20, cy + 40)], fill=OUTLINE, width=12)
    draw.line([(x0 + 20, cy + 110), (x1 - 20, cy + 110)], fill=OUTLINE, width=12)

    # Roof lights
    for rx in [cx - 240, cx - 120, cx, cx + 120, cx + 240]:
        draw.rounded_rectangle([rx - 25, y0 - 18, rx + 25, y0 + 6], radius=8, fill=(255, 120, 30, 255))

    # Windows (Front windshield + 3 side windows)
    # Windshield on left (facing left)
    draw.rounded_rectangle([x0 + 40, y0 + 40, x0 + 180, cy + 15], radius=24, fill=WINDOW_BLUE)
    draw.ellipse([x0 + 80, y0 + 65, x0 + 130, cy - 5], fill=WHITE) # Glint
    # Side windows
    for wx in [x0 + 220, x0 + 380, x0 + 540]:
        draw.rounded_rectangle([wx, y0 + 40, wx + 130, cy + 15], radius=20, fill=WINDOW_BLUE)
        draw.line([(wx + 30, y0 + 55), (wx + 90, cy)], fill=WHITE, width=10)

    # Friendly Headlight on front (left)
    hl_box = [x0 - 15, cy + 60, x0 + 45, cy + 120]
    draw.ellipse(hl_box, fill=(255, 245, 150, 255))
    draw.ellipse([x0, cy + 72, x0 + 25, cy + 100], fill=WHITE)

    # Wheels & Wheel Wells
    for wx in [cx - 210, cx + 210]:
        # Arch cutout
        draw.ellipse([wx - 80, cy + 120, wx + 80, cy + 260], fill=(30, 35, 40, 255))
        # Tire
        draw.ellipse([wx - 70, cy + 130, wx + 70, cy + 270], fill=TIRE_BLACK)
        # Hubcap
        draw.ellipse([wx - 35, cy + 165, wx + 35, cy + 235], fill=HUB_GRAY)
        draw.ellipse([wx - 14, cy + 186, wx + 14, cy + 214], fill=OUTLINE)

    # Front Bumper
    draw.rounded_rectangle([x0 - 25, cy + 140, x0 + 35, cy + 195], radius=16, fill=HUB_GRAY)
    draw.rounded_rectangle([x1 - 35, cy + 140, x1 + 25, cy + 195], radius=16, fill=HUB_GRAY)

    # Cheerful Smiling Mascot details on front
    draw.chord([x0 + 60, cy + 85, x0 + 130, cy + 135], start=0, end=180, fill=OUTLINE)
    draw.ellipse([x0 + 45, cy + 90, x0 + 65, cy + 106], fill=ROSY_CHEEK)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 2. SUB (Cheerful yellow submarine with periscope & round porthole)
# ==============================================================================
def render_sub():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    SUB_YELLOW = (255, 210, 30, 255)
    SUB_SHADOW = (220, 160, 10, 255)
    SUB_ORANGE = (255, 140, 20, 255)
    GLASS_CYAN = (160, 235, 255, 255)

    # Bubbles on right
    for bx, by, br in [(cx + 360, cy - 60, 24), (cx + 410, cy - 110, 34), (cx + 430, cy - 10, 18)]:
        draw.ellipse([bx - br, by - br, bx + br, by + br], fill=(200, 245, 255, 200), outline=WHITE, width=6)

    # Propeller on right
    px, py = cx + 320, cy
    draw.polygon([(px, py), (px + 60, py - 60), (px + 60, py + 60)], fill=SUB_ORANGE)
    draw.ellipse([px + 40, py - 65, px + 75, py - 35], fill=SUB_ORANGE)
    draw.ellipse([px + 40, py + 35, px + 75, py + 65], fill=SUB_ORANGE)
    draw.ellipse([px - 15, py - 20, px + 15, py + 20], fill=OUTLINE)

    # Tower & Periscope
    draw.rounded_rectangle([cx - 80, cy - 240, cx + 40, cy - 60], radius=30, fill=SUB_ORANGE)
    # Periscope tube
    draw.rounded_rectangle([cx - 30, cy - 340, cx + 5, cy - 220], radius=16, fill=SUB_YELLOW)
    # Periscope eye
    draw.rounded_rectangle([cx - 65, cy - 360, cx + 25, cy - 310], radius=20, fill=SUB_ORANGE)
    draw.ellipse([cx - 60, cy - 355, cx - 35, cy - 315], fill=GLASS_CYAN)

    # Main Elliptical Sub Body
    draw.ellipse([cx - 330, cy - 140, cx + 330, cy + 160], fill=SUB_YELLOW)
    # Bottom shading
    draw.chord([cx - 330, cy - 140, cx + 330, cy + 160], start=0, end=180, fill=SUB_SHADOW)

    # Main Porthole (Large glass window in middle)
    draw.ellipse([cx - 130, cy - 80, cx + 30, cy + 80], fill=OUTLINE)
    draw.ellipse([cx - 120, cy - 70, cx + 20, cy + 70], fill=SUB_ORANGE)
    draw.ellipse([cx - 100, cy - 50, cx, cy + 50], fill=GLASS_CYAN)
    # Glass gleam
    draw.polygon([(cx - 75, cy - 45), (cx - 50, cy - 45), (cx - 90, cy + 30), (cx - 95, cy + 10)], fill=WHITE)

    # Rivets along sub hull
    for rx in range(int(cx - 260), int(cx + 280), 60):
        draw.ellipse([rx - 6, cy + 100, rx + 6, cy + 112], fill=OUTLINE)

    # Front smiling face (left side)
    draw_thick_arc(draw, [cx - 240, cy - 20, cx - 160, cy + 40], 20, 160, OUTLINE, 8)
    draw.ellipse([cx - 260, cy - 10, cx - 235, cy + 10], fill=ROSY_CHEEK)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 3. MOM (Loving cartoon mother smiling warmly in hug pose)
# ==============================================================================
def render_mom():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    SKIN = (255, 222, 198, 255)
    HAIR_BROWN = (110, 60, 30, 255)
    SWEATER_TEAL = (72, 180, 172, 255)
    SWEATER_DARK = (48, 140, 134, 255)

    # Back hair volume
    draw.ellipse([cx - 200, cy - 260, cx + 200, cy + 120], fill=HAIR_BROWN)

    # Body / Cozy Sweater
    draw.ellipse([cx - 180, cy + 60, cx + 180, cy + 360], fill=SWEATER_TEAL)
    # Sweater collar
    draw.arc([cx - 80, cy + 60, cx + 80, cy + 140], 0, 180, fill=SWEATER_DARK, width=16)

    # Head
    draw.ellipse([cx - 150, cy - 200, cx + 150, cy + 80], fill=SKIN)

    # Front Hair (Soft side bangs and curly locks)
    bangs = (
        bezier_curve((cx - 160, cy - 80), (cx - 150, cy - 230), (cx, cy - 240), (cx + 160, cy - 100)) +
        bezier_curve((cx + 160, cy - 100), (cx + 80, cy - 140), (cx, cy - 130), (cx - 100, cy - 120)) +
        bezier_curve((cx - 100, cy - 120), (cx - 140, cy - 100), (cx - 150, cy - 80), (cx - 160, cy - 80))
    )
    draw.polygon(bangs, fill=HAIR_BROWN)
    # Pearl earrings
    draw.ellipse([cx - 156, cy - 30, cx - 136, cy - 10], fill=WHITE)
    draw.ellipse([cx + 136, cy - 30, cx + 156, cy - 10], fill=WHITE)

    # Pediatric Eyes (Gentle warm brown iris)
    draw_pediatric_eyes(draw, cx - 65, cx + 65, cy - 65, iris_color=(125, 75, 40, 255), pupil_r=36)

    # Cute nose
    draw_thick_arc(draw, [cx - 10, cy - 15, cx + 10, cy + 5], 20, 160, (225, 160, 140, 255), 6)

    # Sweet motherly smile & blush
    draw_cheeks_and_mouth(draw, cx, cy + 25, mouth_w=28, smile_depth=18, has_tongue=True)

    # Arms folded in gentle warm hug pose
    draw_thick_arc(draw, [cx - 160, cy + 120, cx - 20, cy + 260], 40, 170, SWEATER_TEAL, 52)
    draw_thick_arc(draw, [cx + 20, cy + 120, cx + 160, cy + 260], 10, 140, SWEATER_TEAL, 52)
    # Hands meeting in center
    draw.ellipse([cx - 45, cy + 200, cx + 45, cy + 260], fill=SKIN)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 4. BEE (Chubby cheerful bumblebee with honey stripes & translucent wings)
# ==============================================================================
def render_bee():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    YELLOW = (255, 215, 0, 255)
    BLACK = (45, 55, 62, 255)
    WING = (200, 240, 255, 210)

    # Antennae
    draw.arc([cx - 120, cy - 320, cx - 20, cy - 160], 160, 310, fill=BLACK, width=14)
    draw.ellipse([cx - 110, cy - 330, cx - 70, cy - 290], fill=BLACK)
    draw.arc([cx + 20, cy - 320, cx + 120, cy - 160], 230, 20, fill=BLACK, width=14)
    draw.ellipse([cx + 70, cy - 330, cx + 110, cy - 290], fill=BLACK)

    # Wings in back (angle upward)
    # Left wing
    draw.ellipse([cx - 260, cy - 280, cx - 20, cy - 40], fill=WING)
    draw.ellipse([cx - 250, cy - 270, cx - 40, cy - 60], outline=WHITE, width=8)
    # Right wing
    draw.ellipse([cx + 20, cy - 280, cx + 260, cy - 40], fill=WING)
    draw.ellipse([cx + 40, cy - 270, cx + 250, cy - 60], outline=WHITE, width=8)

    # Little stinger at bottom
    draw.polygon([(cx - 24, cy + 220), (cx + 24, cy + 220), (cx, cy + 290)], fill=BLACK)

    # Chubby Round Bee Body
    draw.ellipse([cx - 180, cy - 140, cx + 180, cy + 230], fill=YELLOW)

    # Bold Black Stripes
    # Stripe 1
    draw.rectangle([cx - 170, cy - 30, cx + 170, cy + 30], fill=BLACK)
    # Stripe 2
    draw.rectangle([cx - 150, cy + 90, cx + 150, cy + 150], fill=BLACK)

    # Big Expressive Pediatric Eyes
    draw_pediatric_eyes(draw, cx - 75, cx + 75, cy - 75, iris_color=(60, 160, 240, 255), pupil_r=38)

    # Cheerful Smile & Blush
    draw_cheeks_and_mouth(draw, cx, cy - 10, mouth_w=24, smile_depth=16, has_tongue=True)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 5. BIB (Baby bib with cute duckling emblem)
# ==============================================================================
def render_bib():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 10

    BIB_BLUE = (120, 205, 250, 255)
    BIB_DARK = (70, 165, 220, 255)
    BIB_TRIM = (255, 255, 255, 255)

    # Neck straps at top
    draw.arc([cx - 180, cy - 320, cx + 180, cy - 60], 180, 360, fill=BIB_BLUE, width=54)
    # Bow ties at back
    draw.ellipse([cx - 40, cy - 310, cx + 40, cy - 250], fill=(255, 175, 195, 255))

    # Main Bib Body (Large rounded shield)
    bib_pts = (
        bezier_curve((cx - 180, cy - 80), (cx - 240, cy + 40), (cx - 240, cy + 240), (cx - 120, cy + 300)) +
        bezier_curve((cx - 120, cy + 300), (cx, cy + 330), (cx, cy + 330), (cx + 120, cy + 300)) +
        bezier_curve((cx + 120, cy + 300), (cx + 240, cy + 240), (cx + 240, cy + 40), (cx + 180, cy - 80)) +
        bezier_curve((cx + 180, cy - 80), (cx + 100, cy - 40), (cx - 100, cy - 40), (cx - 180, cy - 80))
    )
    draw.polygon(bib_pts, fill=BIB_BLUE)

    # Decorative Scalloped White Edge
    for ang in range(0, 360, 20):
        # decorative border
        pass
    draw.line(bib_pts, fill=BIB_TRIM, width=12)

    # Cute Yellow Rubber Duckie Emblem in Center
    dx, dy = cx, cy + 120
    # Duck body
    draw.ellipse([dx - 80, dy - 40, dx + 60, dy + 60], fill=(255, 215, 20, 255))
    # Duck head
    draw.ellipse([dx - 70, dy - 90, dx, dy - 20], fill=(255, 215, 20, 255))
    # Duck beak
    draw.polygon([(dx - 70, dy - 60), (dx - 100, dy - 50), (dx - 70, dy - 40)], fill=(255, 130, 20, 255))
    # Duck eye
    draw.ellipse([dx - 55, dy - 72, dx - 40, dy - 57], fill=OUTLINE)
    draw.ellipse([dx - 52, dy - 70, dx - 44, dy - 62], fill=WHITE)
    # Duck wing
    draw.ellipse([dx - 30, dy - 15, dx + 30, dy + 35], fill=(240, 185, 10, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 6. BAT (Wooden baseball bat with red wrapped grip & baseball)
# ==============================================================================
def render_bat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    WOOD_BASE = (235, 185, 130, 255)
    WOOD_DARK = (195, 145, 90, 255)
    RED_GRIP = (235, 75, 75, 255)

    # Bat placed diagonally
    # Barrel at top-right, handle at bottom-left
    bat_pts = [
        (cx - 240, cy + 240),  # Knob bottom
        (cx - 210, cy + 270),
        (cx + 220, cy - 180),  # Barrel top-right
        (cx + 270, cy - 230),  # Tip
        (cx + 230, cy - 270),
        (cx + 170, cy - 220),
        (cx - 200, cy + 210),
    ]
    # Draw smoothed thick angled polygon
    draw.polygon(bat_pts, fill=WOOD_BASE)
    # Barrel woodgrain highlight
    draw.line([(cx - 80, cy + 80), (cx + 210, cy - 210)], fill=(255, 220, 175, 255), width=24)

    # Red wrapped grip at handle
    for i in range(5):
        gx = cx - 210 + i * 25
        gy = cy + 210 - i * 25
        draw.line([(gx - 20, gy + 20), (gx + 20, gy - 20)], fill=RED_GRIP, width=18)
    # Knob
    draw.ellipse([cx - 260, cy + 230, cx - 210, cy + 280], fill=WOOD_DARK)

    # Baseball sitting at bottom-right
    bx, by, br = cx + 130, cy + 130, 110
    draw.ellipse([bx - br, by - br, bx + br, by + br], fill=WHITE)
    # Red curved stitches
    draw.arc([bx - br * 0.7, by - br * 0.9, bx + br * 0.1, by + br * 0.9], 290, 70, fill=RED_GRIP, width=8)
    draw.arc([bx - br * 0.1, by - br * 0.9, bx + br * 0.7, by + br * 0.9], 110, 250, fill=RED_GRIP, width=8)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 7. KIT (First aid kit: bright red rounded box with white cross emblem)
# ==============================================================================
def render_kit():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    KIT_RED = (235, 60, 70, 255)
    KIT_DARK = (195, 40, 50, 255)
    HANDLE_GRAY = (220, 225, 230, 255)

    # Handle at top
    draw.arc([cx - 110, cy - 260, cx + 110, cy - 90], 180, 360, fill=HANDLE_GRAY, width=44)
    # Handle clasps
    draw.rounded_rectangle([cx - 120, cy - 130, cx - 80, cy - 80], radius=10, fill=OUTLINE)
    draw.rounded_rectangle([cx + 80, cy - 130, cx + 120, cy - 80], radius=10, fill=OUTLINE)

    # Main Case Body
    draw.rounded_rectangle([cx - 280, cy - 100, cx + 280, cy + 260], radius=60, fill=KIT_RED)
    # Bottom depth shadow
    draw.rounded_rectangle([cx - 280, cy + 180, cx + 280, cy + 260], radius=40, fill=KIT_DARK)
    # Center seam
    draw.line([(cx - 280, cy + 80), (cx + 280, cy + 80)], fill=KIT_DARK, width=10)

    # White Circular Badge
    draw.ellipse([cx - 120, cy - 20, cx + 120, cy + 180], fill=WHITE)

    # Crisp Red Medical Cross
    cw, cl = 28, 70
    draw.rounded_rectangle([cx - cw, cy + 80 - cl, cx + cw, cy + 80 + cl], radius=12, fill=KIT_RED)
    draw.rounded_rectangle([cx - cl, cy + 80 - cw, cx + cl, cy + 80 + cw], radius=12, fill=KIT_RED)

    # Silver corner protectors
    for cx_c, cy_c in [(cx - 280, cy - 100), (cx + 220, cy - 100), (cx - 280, cy + 200), (cx + 220, cy + 200)]:
        draw.rounded_rectangle([cx_c, cy_c, cx_c + 60, cy_c + 60], radius=16, fill=HANDLE_GRAY)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 8. TOY (Stacking rainbow ring toy with cute spherical topper)
# ==============================================================================
def render_toy():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    # Base stand
    draw.ellipse([cx - 260, cy + 190, cx + 260, cy + 290], fill=(225, 170, 115, 255))
    draw.rounded_rectangle([cx - 25, cy - 180, cx + 25, cy + 230], radius=20, fill=(200, 145, 95, 255))

    # Rings from bottom to top (decreasing size)
    rings = [
        (220, 65, cy + 170, (80, 160, 240, 255)),   # Blue
        (185, 60, cy + 90, (85, 200, 120, 255)),    # Green
        (150, 55, cy + 15, (255, 215, 40, 255)),    # Yellow
        (120, 50, cy - 55, (255, 140, 30, 255)),    # Orange
        (90, 45, cy - 120, (245, 75, 95, 255)),     # Red
    ]

    for rw, rh, ry, color in rings:
        draw.ellipse([cx - rw, ry - rh, cx + rw, ry + rh], fill=color)
        # Highlight reflection
        draw.ellipse([cx - rw * 0.7, ry - rh * 0.6, cx - rw * 0.3, ry - rh * 0.1], fill=(255, 255, 255, 180))

    # Spherical Top Ball with smiling pediatric face
    draw.ellipse([cx - 65, cy - 270, cx + 65, cy - 140], fill=(255, 180, 210, 255))
    draw_pediatric_eyes(draw, cx - 25, cx + 25, cy - 210, iris_color=(60, 140, 220, 255), pupil_r=16)
    draw_thick_arc(draw, [cx - 16, cy - 188, cx + 16, cy - 170], 20, 160, OUTLINE, 5)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 9. BOY (Cheerful young boy waving happily, matching SAM style)
# ==============================================================================
def render_boy():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    SKIN = (255, 220, 195, 255)
    CAP_BLUE = (45, 130, 240, 255)
    SHIRT_GREEN = (90, 195, 110, 255)
    HAIR_DARK = (60, 45, 35, 255)

    # Body / Green T-Shirt
    draw.ellipse([cx - 170, cy + 90, cx + 170, cy + 360], fill=SHIRT_GREEN)
    draw.arc([cx - 60, cy + 80, cx + 60, cy + 150], 0, 180, fill=WHITE, width=14)

    # Waving Left Arm (to viewer's right)
    draw_thick_arc(draw, [cx + 60, cy - 20, cx + 240, cy + 160], 260, 50, SHIRT_GREEN, 46)
    # Waving Hand
    draw.ellipse([cx + 200, cy - 60, cx + 280, cy + 20], fill=SKIN)
    draw.ellipse([cx + 220, cy - 90, cx + 250, cy - 50], fill=SKIN) # Thumb

    # Head
    draw.ellipse([cx - 150, cy - 160, cx + 150, cy + 110], fill=SKIN)

    # Hair tufts on sides
    draw.ellipse([cx - 165, cy - 70, cx - 120, cy], fill=HAIR_DARK)
    draw.ellipse([cx + 120, cy - 70, cx + 165, cy], fill=HAIR_DARK)

    # Sporty Baseball Cap
    draw.ellipse([cx - 165, cy - 250, cx + 165, cy - 90], fill=CAP_BLUE)
    # Cap Visor tilted forward
    visor = [(cx - 170, cy - 110), (cx + 170, cy - 110), (cx + 190, cy - 70), (cx - 140, cy - 70)]
    draw.polygon(visor, fill=(30, 100, 200, 255))
    # Button on cap top
    draw.ellipse([cx - 20, cy - 265, cx + 20, cy - 235], fill=WHITE)

    # Pediatric Eyes
    draw_pediatric_eyes(draw, cx - 65, cx + 65, cy - 35, iris_color=(60, 160, 240, 255), pupil_r=36)

    # Cute nose and big cheerful smile
    draw_thick_arc(draw, [cx - 10, cy + 15, cx + 10, cy + 35], 20, 160, (220, 150, 130, 255), 6)
    draw_cheeks_and_mouth(draw, cx, cy + 50, mouth_w=28, smile_depth=20, has_tongue=True)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 10. PAN (Frying pan with sizzling sunny-side-up egg)
# ==============================================================================
def render_pan():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0 - 50, H / 2.0 + 30

    PAN_METAL = (65, 80, 95, 255)
    PAN_RIM = (95, 115, 135, 255)
    HANDLE_WOOD = (175, 110, 60, 255)

    # Sturdy angled handle to top-right
    hx0, hy0 = cx + 160, cy - 60
    hx1, hy1 = cx + 380, cy - 240
    draw.line([(hx0, hy0), (hx1, hy1)], fill=HANDLE_WOOD, width=54)
    # Hanging hole at tip
    draw.ellipse([hx1 - 25, hy1 - 25, hx1 + 25, hy1 + 25], fill=OUTLINE)
    draw.ellipse([hx1 - 10, hy1 - 10, hx1 + 10, hy1 + 10], fill=(0, 0, 0, 0))

    # Outer Pan Base
    draw.ellipse([cx - 240, cy - 160, cx + 240, cy + 180], fill=PAN_RIM)
    # Inner Cooking Surface
    draw.ellipse([cx - 210, cy - 135, cx + 210, cy + 155], fill=PAN_METAL)

    # Sunny-side-up Egg
    egg_pts = (
        bezier_curve((cx - 120, cy - 40), (cx - 140, cy + 30), (cx - 80, cy + 90), (cx, cy + 80)) +
        bezier_curve((cx, cy + 80), (cx + 80, cy + 100), (cx + 130, cy + 30), (cx + 110, cy - 40)) +
        bezier_curve((cx + 110, cy - 40), (cx + 90, cy - 100), (cx - 20, cy - 100), (cx - 120, cy - 40))
    )
    draw.polygon(egg_pts, fill=WHITE)

    # Golden Yolk
    yx, yy, yr = cx - 10, cy - 10, 52
    draw.ellipse([yx - yr, yy - yr, yx + yr, yy + yr], fill=(255, 195, 20, 255))
    # Yolk highlight
    draw.ellipse([yx - yr * 0.5, yy - yr * 0.6, yx - yr * 0.1, yy - yr * 0.2], fill=WHITE)

    # Friendly cartoon face on egg yolk!
    draw_pediatric_eyes(draw, yx - 22, yx + 22, yy - 5, iris_color=OUTLINE, pupil_r=12)
    draw_thick_arc(draw, [yx - 14, yy + 10, yx + 14, yy + 24], 20, 160, OUTLINE, 4)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 11. BUG (Cute red ladybug with black spots & sparkling eyes)
# ==============================================================================
def render_bug():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    LADY_RED = (245, 55, 65, 255)
    LADY_DARK = (200, 35, 45, 255)
    BLACK = (45, 55, 62, 255)

    # Antennae
    draw.arc([cx - 110, cy - 310, cx - 20, cy - 150], 160, 310, fill=BLACK, width=12)
    draw.ellipse([cx - 105, cy - 325, cx - 75, cy - 295], fill=BLACK)
    draw.arc([cx + 20, cy - 310, cx + 110, cy - 150], 230, 20, fill=BLACK, width=12)
    draw.ellipse([cx + 75, cy - 325, cx + 105, cy - 295], fill=BLACK)

    # 6 Cute Little Rounded Feet
    for lx, ly in [
        (cx - 210, cy - 50), (cx - 230, cy + 50), (cx - 200, cy + 150),
        (cx + 210, cy - 50), (cx + 230, cy + 50), (cx + 200, cy + 150)
    ]:
        draw.ellipse([lx - 25, ly - 20, lx + 25, ly + 20], fill=BLACK)

    # Black Head at Top
    draw.ellipse([cx - 130, cy - 200, cx + 130, cy - 20], fill=BLACK)

    # Ladybug Red Wing Dome
    draw.ellipse([cx - 200, cy - 100, cx + 200, cy + 240], fill=LADY_RED)
    # Bottom shading
    draw.chord([cx - 200, cy - 100, cx + 200, cy + 240], start=0, end=180, fill=LADY_DARK)

    # Center Wing Split Line
    draw.line([(cx, cy - 90), (cx, cy + 240)], fill=BLACK, width=12)

    # Distinct Black Spots
    spots = [
        (cx - 110, cy - 20, 32), (cx + 110, cy - 20, 32),
        (cx - 130, cy + 70, 38), (cx + 130, cy + 70, 38),
        (cx - 65, cy + 150, 30), (cx + 65, cy + 150, 30)
    ]
    for sx, sy, sr in spots:
        draw.ellipse([sx - sr, sy - sr, sx + sr, sy + sr], fill=BLACK)

    # Big Expressive Pediatric Eyes on Head
    draw_pediatric_eyes(draw, cx - 60, cx + 60, cy - 110, iris_color=(75, 175, 245, 255), pupil_r=32)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 12. PIN (Glossy golden pushpin & safety pin)
# ==============================================================================
def render_pin():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    GOLD_BASE = (255, 200, 45, 255)
    GOLD_LIGHT = (255, 235, 140, 255)
    GOLD_DARK = (215, 150, 15, 255)
    STEEL = (210, 220, 230, 255)

    # Sharp Steel Pin Needle pointing downward
    draw.polygon([(cx - 18, cy + 70), (cx + 18, cy + 70), (cx, cy + 300)], fill=STEEL)
    draw.polygon([(cx - 4, cy + 70), (cx + 4, cy + 70), (cx, cy + 300)], fill=WHITE)

    # Golden Pushpin Top Grip (Hourglass shape)
    # Lower rim
    draw.ellipse([cx - 160, cy + 30, cx + 160, cy + 110], fill=GOLD_DARK)
    draw.ellipse([cx - 150, cy + 25, cx + 150, cy + 95], fill=GOLD_BASE)

    # Conical waist
    draw.polygon([(cx - 120, cy + 40), (cx + 120, cy + 40), (cx + 50, cy - 90), (cx - 50, cy - 90)], fill=GOLD_BASE)
    draw.polygon([(cx - 20, cy + 40), (cx + 20, cy + 40), (cx + 10, cy - 90), (cx - 10, cy - 90)], fill=GOLD_LIGHT)

    # Upper bulbous knob
    draw.ellipse([cx - 140, cy - 230, cx + 140, cy - 70], fill=GOLD_DARK)
    draw.ellipse([cx - 130, cy - 240, cx + 130, cy - 85], fill=GOLD_BASE)
    # Glossy dome highlight
    draw.ellipse([cx - 90, cy - 220, cx - 20, cy - 160], fill=WHITE)

    # Little cheerful face on the pushpin dome
    draw_pediatric_eyes(draw, cx - 45, cx + 45, cy - 150, iris_color=OUTLINE, pupil_r=18)
    draw_thick_arc(draw, [cx - 24, cy - 130, cx + 24, cy - 105], 20, 160, OUTLINE, 6)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 13. NAP (Peaceful sleeping crescent moon with nightcap on cloud)
# ==============================================================================
def render_nap():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0 - 20, H / 2.0 - 10

    MOON_YELLOW = (255, 225, 75, 255)
    CAP_PURPLE = (145, 110, 215, 255)
    CLOUD_WHITE = (245, 250, 255, 255)

    # Fluffy Bed of Clouds at Bottom
    for clx, cly, clr in [
        (cx - 180, cy + 200, 90), (cx - 70, cy + 180, 110),
        (cx + 80, cy + 190, 100), (cx + 210, cy + 220, 80)
    ]:
        draw.ellipse([clx - clr, cly - clr, clx + clr, cly + clr], fill=CLOUD_WHITE)

    # Sleeping Crescent Moon
    # Outer circle
    moon_img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    moon_draw = ImageDraw.Draw(moon_img)
    moon_draw.ellipse([cx - 180, cy - 180, cx + 180, cy + 180], fill=MOON_YELLOW)
    # Cutout with inner circle
    cutout = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    c_draw = ImageDraw.Draw(cutout)
    c_draw.ellipse([cx - 40, cy - 200, cx + 260, cy + 140], fill=WHITE)
    # Mask moon
    moon_img.paste(Image.new("RGBA", (W, H), (0, 0, 0, 0)), (0, 0), cutout)
    img = Image.alpha_composite(img, moon_img)
    draw = ImageDraw.Draw(img)

    # Cozy Nightcap at moon tip
    cap_pts = [
        (cx - 90, cy - 160), (cx + 20, cy - 180), (cx + 180, cy - 280), (cx + 110, cy - 160)
    ]
    draw.polygon(cap_pts, fill=CAP_PURPLE)
    # Nightcap pom-pom star
    draw.ellipse([cx + 170, cy - 305, cx + 220, cy - 255], fill=MOON_YELLOW)

    # Peaceful Sleeping Face (closed curved eyelashes & sweet smile)
    draw_thick_arc(draw, [cx - 130, cy - 30, cx - 80, cy + 20], 190, 350, OUTLINE, 8)
    draw_thick_arc(draw, [cx - 60, cy - 30, cx - 10, cy + 20], 190, 350, OUTLINE, 8)
    # Blush
    draw.ellipse([cx - 140, cy + 15, cx - 100, cy + 45], fill=ROSY_CHEEK)
    # Gentle smile
    draw_thick_arc(draw, [cx - 90, cy + 30, cx - 40, cy + 70], 20, 160, OUTLINE, 7)

    # Cute floating "Z z z"
    # Small "Z"
    draw.line([(cx + 140, cy - 40), (cx + 180, cy - 40), (cx + 140, cy), (cx + 180, cy)], fill=CAP_PURPLE, width=10)
    # Bigger "Z"
    draw.line([(cx + 200, cy - 120), (cx + 260, cy - 120), (cx + 200, cy - 60), (cx + 260, cy - 60)], fill=CAP_PURPLE, width=14)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 14. HEN (Plump friendly brown mother hen with red comb & yellow beak)
# ==============================================================================
def render_hen():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    HEN_BROWN = (215, 130, 60, 255)
    HEN_DARK = (175, 95, 35, 255)
    RED = (235, 55, 65, 255)
    YELLOW = (255, 195, 30, 255)

    # Plump Body
    draw.ellipse([cx - 200, cy - 80, cx + 180, cy + 240], fill=HEN_BROWN)
    # Chest highlight
    draw.ellipse([cx - 170, cy - 30, cx - 30, cy + 180], fill=(240, 170, 100, 255))

    # Wing on side
    draw.ellipse([cx - 40, cy + 10, cx + 160, cy + 190], fill=HEN_DARK)
    # Wing feather tips
    draw.arc([cx + 60, cy + 120, cx + 140, cy + 180], 30, 180, fill=OUTLINE, width=6)

    # Perky Tail Feathers on right
    tail_pts = [(cx + 140, cy - 10), (cx + 260, cy - 100), (cx + 230, cy + 30), (cx + 160, cy + 80)]
    draw.polygon(tail_pts, fill=HEN_DARK)

    # Head
    draw.ellipse([cx - 190, cy - 210, cx - 10, cy - 30], fill=HEN_BROWN)

    # Red Comb on top of head (3 lobes)
    for hx, hy, hr in [(-130, -220, 36), (-100, -240, 42), (-70, -215, 34)]:
        draw.ellipse([cx + hx - hr, cy + hy - hr, cx + hx + hr, cy + hy + hr], fill=RED)

    # Red Wattle under beak
    draw.ellipse([cx - 210, cy - 70, cx - 165, cy - 10], fill=RED)

    # Yellow Beak
    draw.polygon([(cx - 190, cy - 130), (cx - 265, cy - 100), (cx - 190, cy - 75)], fill=YELLOW)

    # Big Expressive Pediatric Eye
    draw_pediatric_eyes(draw, cx - 105, cx - 105, cy - 120, iris_color=(60, 140, 220, 255), pupil_r=34)

    # Cheerful cheek blush
    draw.ellipse([cx - 135, cy - 80, cx - 95, cy - 50], fill=ROSY_CHEEK)

    # Little yellow feet
    draw.line([(cx - 70, cy + 230), (cx - 70, cy + 290)], fill=YELLOW, width=16)
    draw.line([(cx - 100, cy + 290), (cx - 40, cy + 290)], fill=YELLOW, width=16)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 15. BED (Cozy storybook wooden bed with turquoise quilt & pillow)
# ==============================================================================
def render_bed():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    WOOD_BASE = (195, 125, 75, 255)
    WOOD_DARK = (155, 95, 50, 255)
    QUILT_TEAL = (65, 185, 195, 255)
    QUILT_DARK = (45, 145, 155, 255)

    # Headboard (Left side, taller)
    draw.rounded_rectangle([cx - 280, cy - 220, cx - 210, cy + 240], radius=24, fill=WOOD_BASE)
    # Headboard posts
    draw.ellipse([cx - 275, cy - 260, cx - 215, cy - 200], fill=WOOD_DARK)

    # Footboard (Right side, shorter)
    draw.rounded_rectangle([cx + 210, cy - 40, cx + 280, cy + 240], radius=24, fill=WOOD_BASE)
    draw.ellipse([cx + 215, cy - 80, cx + 275, cy - 20], fill=WOOD_DARK)

    # Mattress Base Frame
    draw.rounded_rectangle([cx - 240, cy + 60, cx + 240, cy + 180], radius=20, fill=WOOD_DARK)

    # Fluffy White Pillow
    draw.rounded_rectangle([cx - 220, cy - 60, cx - 60, cy + 60], radius=35, fill=WHITE)
    draw.arc([cx - 200, cy - 40, cx - 80, cy + 40], 30, 150, fill=(220, 230, 240, 255), width=8)

    # Cozy Turquoise Quilt
    quilt_box = [cx - 130, cy - 20, cx + 230, cy + 140]
    draw.rounded_rectangle(quilt_box, radius=30, fill=QUILT_TEAL)
    # Folded top sheet
    draw.rounded_rectangle([cx - 130, cy - 20, cx - 70, cy + 140], radius=15, fill=WHITE)
    # Polka dots on quilt
    for qx in range(int(cx - 30), int(cx + 210), 60):
        for qy in range(int(cy + 10), int(cy + 130), 50):
            draw.ellipse([qx - 10, qy - 10, qx + 10, qy + 10], fill=QUILT_DARK)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 16. WEB (Silvery spiderweb with cute friendly purple spider)
# ==============================================================================
def render_web():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 - 40

    WEB_SILVER = (195, 215, 230, 255)
    SPIDER_PURPLE = (155, 95, 220, 255)

    # Radial Web Spokes (8 lines from center)
    for ang_deg in range(0, 360, 45):
        ang = math.radians(ang_deg)
        ex = cx + 330 * math.cos(ang)
        ey = cy + 330 * math.sin(ang)
        draw.line([(cx, cy), (ex, ey)], fill=WEB_SILVER, width=8)

    # Concentric Spiral Arcs
    for r in [90, 170, 250, 320]:
        for ang_deg in range(0, 360, 45):
            a1 = math.radians(ang_deg)
            a2 = math.radians(ang_deg + 45)
            p1 = (cx + r * math.cos(a1), cy + r * math.sin(a1))
            p2 = (cx + r * math.cos(a2), cy + r * math.sin(a2))
            mid = (cx + (r - 18) * math.cos((a1 + a2)/2), cy + (r - 18) * math.sin((a1 + a2)/2))
            pts = bezier_curve(p1, mid, mid, p2, 15)
            for j in range(len(pts) - 1):
                draw.line([pts[j], pts[j+1]], fill=WEB_SILVER, width=7)
        # Glistening Dewdrops
        for ang_deg in [45, 135, 225, 315]:
            da = math.radians(ang_deg)
            draw.ellipse([cx + r * math.cos(da) - 8, cy + r * math.sin(da) - 8,
                          cx + r * math.cos(da) + 8, cy + r * math.sin(da) + 8], fill=WHITE)

    # Cute Little Spider Hanging Down
    sx, sy = cx + 80, cy + 240
    # Silk thread
    draw.line([(cx + 80, cy), (sx, sy)], fill=WEB_SILVER, width=6)

    # Spider 8 Cute Legs
    for lx in [-45, -30, 30, 45]:
        draw.arc([sx + lx - 30, sy - 50, sx + lx + 30, sy + 30], 190, 350, fill=SPIDER_PURPLE, width=10)
        draw.arc([sx + lx - 30, sy, sx + lx + 30, sy + 60], 10, 170, fill=SPIDER_PURPLE, width=10)

    # Spider Chubby Body
    draw.ellipse([sx - 65, sy - 40, sx + 65, sy + 65], fill=SPIDER_PURPLE)

    # Sparkling Eyes & Smile
    draw_pediatric_eyes(draw, sx - 25, sx + 25, sy, iris_color=(255, 215, 60, 255), pupil_r=18)
    draw_thick_arc(draw, [sx - 16, sy + 18, sx + 16, sy + 36], 20, 160, OUTLINE, 5)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 17. FAN (Retro colorful desk fan with cyan cage and spinning blades)
# ==============================================================================
def render_fan():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 - 40

    FAN_TEAL = (75, 195, 205, 255)
    FAN_DARK = (45, 150, 160, 255)
    BLADE_YELLOW = (255, 215, 30, 255)
    BLADE_CORAL = (255, 105, 95, 255)
    BLADE_BLUE = (65, 150, 245, 255)

    # Sturdy Base Stand at Bottom
    draw.ellipse([cx - 160, cy + 290, cx + 160, cy + 370], fill=FAN_DARK)
    draw.ellipse([cx - 140, cy + 280, cx + 140, cy + 350], fill=FAN_TEAL)
    # Base control knob
    draw.ellipse([cx - 25, cy + 300, cx + 25, cy + 330], fill=WHITE)

    # Upright Neck
    draw.rounded_rectangle([cx - 30, cy + 120, cx + 30, cy + 300], radius=16, fill=FAN_DARK)

    # Circular Outer Fan Cage
    draw.ellipse([cx - 240, cy - 240, cx + 240, cy + 240], fill=(235, 248, 250, 255), outline=FAN_TEAL, width=32)

    # Colorful Propeller Blades (3 curved teardrops)
    blade_colors = [BLADE_YELLOW, BLADE_CORAL, BLADE_BLUE]
    for idx, deg in enumerate([0, 120, 240]):
        ang = math.radians(deg)
        bx = cx + 120 * math.cos(ang)
        by = cy + 120 * math.sin(ang)
        draw.ellipse([bx - 65, by - 65, bx + 65, by + 65], fill=blade_colors[idx])

    # Center Hub with smiling face
    draw.ellipse([cx - 70, cy - 70, cx + 70, cy + 70], fill=FAN_TEAL)
    draw_pediatric_eyes(draw, cx - 25, cx + 25, cy - 10, iris_color=OUTLINE, pupil_r=15)
    draw_thick_arc(draw, [cx - 16, cy + 10, cx + 16, cy + 28], 20, 160, OUTLINE, 5)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 18. CAP (Bright blue baseball cap with curved red visor)
# ==============================================================================
def render_cap():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    CAP_BLUE = (40, 135, 235, 255)
    CAP_DARK = (25, 95, 185, 255)
    VISOR_RED = (245, 65, 75, 255)
    VISOR_DARK = (205, 45, 55, 255)

    # Main Cap Crown (Dome)
    draw.ellipse([cx - 220, cy - 220, cx + 160, cy + 110], fill=CAP_BLUE)
    # Seam Panel lines
    draw.arc([cx - 160, cy - 220, cx + 40, cy + 110], 210, 330, fill=CAP_DARK, width=8)
    draw.arc([cx - 80, cy - 220, cx + 120, cy + 110], 210, 330, fill=CAP_DARK, width=8)

    # Button on crown top
    draw.ellipse([cx - 45, cy - 235, cx - 5, cy - 195], fill=WHITE)

    # Bold Curved Visor on right
    visor_pts = (
        bezier_curve((cx - 120, cy + 40), (cx + 60, cy + 40), (cx + 240, cy + 20), (cx + 310, cy + 10)) +
        bezier_curve((cx + 310, cy + 10), (cx + 340, cy + 60), (cx + 280, cy + 140), (cx + 140, cy + 140)) +
        bezier_curve((cx + 140, cy + 140), (cx + 20, cy + 130), (cx - 120, cy + 80), (cx - 120, cy + 40))
    )
    draw.polygon(visor_pts, fill=VISOR_RED)
    draw.line(visor_pts, fill=VISOR_DARK, width=8)

    # Eyelet vents
    draw.ellipse([cx - 120, cy - 110, cx - 100, cy - 90], fill=WHITE)
    draw.ellipse([cx - 20, cy - 130, cx, cy - 110], fill=WHITE)

    # Cheerful embroidery star on side
    draw.ellipse([cx - 110, cy - 40, cx - 50, cy + 20], fill=(255, 215, 30, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 19. CUP (Cozy ceramic mug with hot cocoa & fluffy marshmallows)
# ==============================================================================
def render_cup():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0 - 30, H / 2.0 + 30

    CUP_CORAL = (255, 115, 105, 255)
    CUP_DARK = (220, 80, 70, 255)
    COCOA_BROWN = (115, 65, 35, 255)

    # Steam rising from cup
    for sx, sdeg in [(cx - 60, 20), (cx, 0), (cx + 60, -20)]:
        steam = bezier_curve((sx, cy - 140), (sx - 30, cy - 210), (sx + 30, cy - 270), (sx, cy - 340), 20)
        for i in range(len(steam) - 1):
            draw.line([steam[i], steam[i+1]], fill=(255, 255, 255, 190), width=12)

    # Sturdy Rounded Mug Handle on right
    draw.arc([cx + 100, cy - 70, cx + 310, cy + 150], 280, 80, fill=CUP_CORAL, width=54)

    # Main Cup Body
    draw.rounded_rectangle([cx - 190, cy - 120, cx + 190, cy + 220], radius=50, fill=CUP_CORAL)
    # Shading on right side
    draw.rounded_rectangle([cx + 80, cy - 120, cx + 190, cy + 220], radius=50, fill=CUP_DARK)

    # Cocoa surface at top
    draw.ellipse([cx - 190, cy - 160, cx + 190, cy - 80], fill=COCOA_BROWN)
    draw.ellipse([cx - 190, cy - 160, cx + 190, cy - 80], outline=WHITE, width=10)

    # Floating Marshmallows
    for mx, my in [(cx - 70, cy - 130), (cx + 20, cy - 125), (cx - 20, cy - 110)]:
        draw.ellipse([mx - 28, my - 18, mx + 28, my + 18], fill=WHITE)

    # Friendly Pediatric Face on the Mug!
    draw_pediatric_eyes(draw, cx - 65, cx + 65, cy + 10, iris_color=OUTLINE, pupil_r=28)
    draw_cheeks_and_mouth(draw, cx, cy + 80, mouth_w=24, smile_depth=16, has_tongue=True)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 20. JAM (Glass jar of strawberry jam with gingham cloth lid)
# ==============================================================================
def render_jam():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    JAM_RED = (215, 35, 65, 255)
    JAM_DARK = (165, 20, 45, 255)
    GINGHAM_RED = (240, 75, 85, 255)
    GLASS = (220, 240, 255, 120)

    # Clear Glass Jar Body
    draw.rounded_rectangle([cx - 190, cy - 60, cx + 190, cy + 240], radius=55, fill=JAM_RED)
    # Jam depth shading
    draw.rounded_rectangle([cx - 190, cy + 140, cx + 190, cy + 240], radius=40, fill=JAM_DARK)
    # Glass shine reflection
    draw.line([(cx - 150, cy - 20), (cx - 150, cy + 200)], fill=(255, 255, 255, 160), width=16)

    # White Label in Center
    draw.rounded_rectangle([cx - 110, cy + 10, cx + 110, cy + 160], radius=24, fill=WHITE)
    # Cute Strawberry on Label
    sb_pts = [(cx, cy + 135), (cx + 45, cy + 60), (cx - 45, cy + 60)]
    draw.polygon(sb_pts, fill=JAM_RED)
    draw.ellipse([cx - 45, cy + 45, cx + 45, cy + 85], fill=JAM_RED)
    # Strawberry green leaves
    draw.ellipse([cx - 30, cy + 35, cx + 30, cy + 55], fill=(80, 185, 90, 255))

    # Gingham Cloth Lid at Top
    draw.ellipse([cx - 210, cy - 140, cx + 210, cy - 30], fill=GINGHAM_RED)
    # Checkerboard stripes on cloth
    for gx in range(int(cx - 180), int(cx + 190), 40):
        draw.line([(gx, cy - 130), (gx, cy - 40)], fill=WHITE, width=12)

    # Twine Ribbon Tie
    draw.rounded_rectangle([cx - 180, cy - 60, cx + 180, cy - 35], radius=10, fill=(215, 175, 115, 255))
    # Bow
    draw.ellipse([cx - 35, cy - 65, cx + 35, cy - 30], fill=(215, 175, 115, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 21. FOX (Clever friendly red fox with bushy white-tipped tail)
# ==============================================================================
def render_fox():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0 - 20, H / 2.0 + 20

    FOX_ORANGE = (245, 110, 30, 255)
    FOX_DARK = (205, 80, 15, 255)
    BLACK = (45, 55, 62, 255)

    # Bushy Tail curling around on right
    tail_pts = bezier_curve((cx + 80, cy + 180), (cx + 280, cy + 240), (cx + 340, cy + 40), (cx + 220, cy - 20), 30)
    for i in range(len(tail_pts) - 1):
        draw.line([tail_pts[i], tail_pts[i+1]], fill=FOX_ORANGE, width=80)
    # White tail tip
    draw.ellipse([cx + 210, cy - 50, cx + 290, cy + 30], fill=WHITE)

    # Sitting Body
    draw.ellipse([cx - 160, cy + 40, cx + 160, cy + 320], fill=FOX_ORANGE)
    # White Chest Fluff
    draw.ellipse([cx - 80, cy + 80, cx + 80, cy + 260], fill=WHITE)

    # Paws
    for px in [cx - 65, cx + 65]:
        draw.ellipse([px - 38, cy + 260, px + 38, cy + 330], fill=BLACK)

    # Head (Fox triangular cheek shape)
    head_pts = (
        bezier_curve((cx - 150, cy - 200), (cx, cy - 230), (cx, cy - 230), (cx + 150, cy - 200)) +
        bezier_curve((cx + 150, cy - 200), (cx + 210, cy - 90), (cx + 210, cy - 10), (cx + 120, cy + 70)) +
        bezier_curve((cx + 120, cy + 70), (cx, cy + 90), (cx, cy + 90), (cx - 120, cy + 70)) +
        bezier_curve((cx - 120, cy + 70), (cx - 210, cy - 10), (cx - 210, cy - 90), (cx - 150, cy - 200))
    )
    # Ears
    draw.polygon([(cx - 150, cy - 140), (cx - 190, cy - 340), (cx - 50, cy - 200)], fill=FOX_ORANGE)
    draw.polygon([(cx - 140, cy - 160), (cx - 175, cy - 310), (cx - 70, cy - 200)], fill=BLACK)
    draw.polygon([(cx + 150, cy - 140), (cx + 190, cy - 340), (cx + 50, cy - 200)], fill=FOX_ORANGE)
    draw.polygon([(cx + 140, cy - 160), (cx + 175, cy - 310), (cx + 70, cy - 200)], fill=BLACK)

    draw.polygon(head_pts, fill=FOX_ORANGE)

    # White Cheeks & Muzzle
    draw.ellipse([cx - 150, cy - 40, cx - 20, cy + 60], fill=WHITE)
    draw.ellipse([cx + 20, cy - 40, cx + 150, cy + 60], fill=WHITE)
    draw.polygon([(cx - 45, cy + 10), (cx + 45, cy + 10), (cx, cy + 70)], fill=WHITE)

    # Black Nose
    draw.ellipse([cx - 20, cy + 40, cx + 20, cy + 68], fill=BLACK)

    # Big Amber Eyes
    draw_pediatric_eyes(draw, cx - 65, cx + 65, cy - 70, iris_color=(235, 155, 35, 255), pupil_r=36)
    draw_cheeks_and_mouth(draw, cx, cy + 70, mouth_w=20, smile_depth=14)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 22. ZOO (Wooden zoo archway gate with smiling giraffe peaking over)
# ==============================================================================
def render_zoo():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    WOOD_BROWN = (175, 115, 65, 255)
    WOOD_DARK = (135, 80, 40, 255)
    GIRAFFE_YELLOW = (255, 205, 45, 255)
    BROWN_SPOTS = (170, 95, 30, 255)
    LEAF_GREEN = (85, 185, 95, 255)

    # Friendly Giraffe Head & Long Neck peaking from behind the gate
    # Neck
    draw.rounded_rectangle([cx - 55, cy - 240, cx + 55, cy + 10], radius=24, fill=GIRAFFE_YELLOW)
    # Spots on neck
    draw.ellipse([cx - 35, cy - 170, cx + 5, cy - 120], fill=BROWN_SPOTS)
    draw.ellipse([cx + 5, cy - 90, cx + 45, cy - 50], fill=BROWN_SPOTS)

    # Giraffe Head
    draw.ellipse([cx - 75, cy - 320, cx + 75, cy - 180], fill=GIRAFFE_YELLOW)
    # Ossicones (little horns)
    draw.line([(cx - 35, cy - 300), (cx - 45, cy - 360)], fill=GIRAFFE_YELLOW, width=16)
    draw.ellipse([cx - 55, cy - 375, cx - 35, cy - 350], fill=BROWN_SPOTS)
    draw.line([(cx + 35, cy - 300), (cx + 45, cy - 360)], fill=GIRAFFE_YELLOW, width=16)
    draw.ellipse([cx + 35, cy - 375, cx + 55, cy - 350], fill=BROWN_SPOTS)

    # Giraffe Muzzle
    draw.ellipse([cx - 60, cy - 235, cx + 60, cy - 180], fill=(255, 235, 185, 255))
    # Nostrils
    draw.ellipse([cx - 24, cy - 215, cx - 12, cy - 200], fill=OUTLINE)
    draw.ellipse([cx + 12, cy - 215, cx + 24, cy - 200], fill=OUTLINE)

    # Giraffe Eyes & Smile
    draw_pediatric_eyes(draw, cx - 40, cx + 40, cy - 260, iris_color=(125, 75, 35, 255), pupil_r=22)
    draw_thick_arc(draw, [cx - 20, cy - 200, cx + 20, cy - 182], 20, 160, OUTLINE, 5)

    # Wooden Gate Posts
    draw.rounded_rectangle([cx - 240, cy - 140, cx - 180, cy + 290], radius=16, fill=WOOD_BROWN)
    draw.rounded_rectangle([cx + 180, cy - 140, cx + 240, cy + 290], radius=16, fill=WOOD_BROWN)

    # Wooden Top Sign Plaque with "ZOO"
    sign_box = [cx - 260, cy - 110, cx + 260, cy + 20]
    draw.rounded_rectangle(sign_box, radius=24, fill=WOOD_BROWN)
    draw.rounded_rectangle([cx - 250, cy - 100, cx + 250, cy + 10], radius=18, fill=WOOD_DARK)

    # "Z O O" Letters in Wood Carving
    # Z
    draw.line([(cx - 150, cy - 70), (cx - 90, cy - 70), (cx - 150, cy - 20), (cx - 90, cy - 20)], fill=(255, 225, 140, 255), width=16)
    # O 1
    draw.ellipse([cx - 60, cy - 75, cx, cy - 15], outline=(255, 225, 140, 255), width=16)
    # O 2
    draw.ellipse([cx + 30, cy - 75, cx + 90, cy - 15], outline=(255, 225, 140, 255), width=16)

    # Gate Planks at Bottom
    for gy in [cy + 90, cy + 190]:
        draw.rounded_rectangle([cx - 210, gy - 20, cx + 210, gy + 20], radius=10, fill=WOOD_BROWN)

    # Tropical Palm Leaves on Sides
    draw.ellipse([cx - 280, cy - 160, cx - 160, cy - 80], fill=LEAF_GREEN)
    draw.ellipse([cx + 160, cy - 160, cx + 280, cy - 80], fill=LEAF_GREEN)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 23. QUIZ (Clipboard with A+ gold star grade & sharp pencil)
# ==============================================================================
def render_quiz():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0 - 40, H / 2.0 + 20

    BOARD_WOOD = (220, 160, 100, 255)
    CLIP_STEEL = (195, 205, 215, 255)
    RED_GRADE = (235, 55, 65, 255)

    # Wooden Clipboard Body
    draw.rounded_rectangle([cx - 190, cy - 220, cx + 190, cy + 240], radius=40, fill=BOARD_WOOD)

    # White Quiz Paper Sheet
    draw.rounded_rectangle([cx - 160, cy - 180, cx + 160, cy + 210], radius=24, fill=WHITE)

    # Steel Clip at Top
    draw.rounded_rectangle([cx - 75, cy - 250, cx + 75, cy - 170], radius=16, fill=CLIP_STEEL)
    draw.ellipse([cx - 25, cy - 235, cx + 25, cy - 185], fill=OUTLINE)

    # Checkmarked Questions Lines
    for qy, checked in [(cy - 110, True), (cy - 50, True), (cy + 10, True)]:
        # Checkmark
        draw.line([(cx - 130, qy), (cx - 115, qy + 15), (cx - 95, qy - 15)], fill=(75, 185, 95, 255), width=8)
        # Question text lines
        draw.line([(cx - 75, qy), (cx + 50, qy)], fill=(200, 210, 220, 255), width=10)

    # Big Cheerful "A+" in Red Circle
    draw.ellipse([cx + 30, cy + 60, cx + 140, cy + 170], outline=RED_GRADE, width=10)
    # A
    draw.line([(cx + 60, cy + 145), (cx + 78, cy + 85), (cx + 95, cy + 145)], fill=RED_GRADE, width=9)
    draw.line([(cx + 68, cy + 120), (cx + 87, cy + 120)], fill=RED_GRADE, width=8)
    # +
    draw.line([(cx + 105, cy + 110), (cx + 125, cy + 110)], fill=RED_GRADE, width=8)
    draw.line([(cx + 115, cy + 100), (cx + 115, cy + 120)], fill=RED_GRADE, width=8)

    # Yellow HB Pencil on right
    px, py = cx + 220, cy + 30
    draw.polygon([(px - 20, py - 180), (px + 20, py - 180), (px + 20, py + 140), (px - 20, py + 140)], fill=(255, 205, 45, 255))
    # Pink Eraser
    draw.rounded_rectangle([px - 20, py - 220, px + 20, py - 180], radius=8, fill=(255, 155, 185, 255))
    # Sharpened tip
    draw.polygon([(px - 20, py + 140), (px + 20, py + 140), (px, py + 200)], fill=(245, 215, 180, 255))
    draw.polygon([(px - 8, py + 180), (px + 8, py + 180), (px, py + 200)], fill=OUTLINE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 24. BAM (Playful comic action explosion star with energetic sparks)
# ==============================================================================
def render_bam():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    BURST_ORANGE = (255, 120, 20, 255)
    BURST_YELLOW = (255, 215, 20, 255)

    # Outer 16-point Action Star Burst
    num_pts = 16
    outer_pts = []
    for i in range(num_pts * 2):
        r = 340 if i % 2 == 0 else 180
        ang = math.radians(i * (360 / (num_pts * 2)))
        outer_pts.append((cx + r * math.cos(ang), cy + r * math.sin(ang)))
    draw.polygon(outer_pts, fill=BURST_ORANGE)

    # Inner Yellow Star Burst
    inner_pts = []
    for i in range(num_pts * 2):
        r = 270 if i % 2 == 0 else 140
        ang = math.radians(i * (360 / (num_pts * 2)) + 11)
        inner_pts.append((cx + r * math.cos(ang), cy + r * math.sin(ang)))
    draw.polygon(inner_pts, fill=BURST_YELLOW)

    # Flying Action Sparks
    for sx, sy, sr in [
        (cx - 320, cy - 240, 26), (cx + 320, cy - 220, 32),
        (cx - 290, cy + 250, 28), (cx + 300, cy + 240, 36)
    ]:
        draw.ellipse([sx - sr, sy - sr, sx + sr, sy + sr], fill=BURST_YELLOW)

    # Center Cloud Puff
    for px, py, pr in [(cx - 70, cy, 75), (cx + 70, cy, 75), (cx, cy - 50, 80), (cx, cy + 50, 80)]:
        draw.ellipse([px - pr, py - pr, px + pr, py + pr], fill=WHITE)

    # Comic "B A M !" Lettering
    # B
    bx = cx - 140
    draw.line([(bx, cy - 70), (bx, cy + 70)], fill=OUTLINE, width=28)
    draw.arc([bx - 10, cy - 75, bx + 70, cy + 5], 270, 90, fill=OUTLINE, width=24)
    draw.arc([bx - 10, cy - 5, bx + 75, cy + 75], 270, 90, fill=OUTLINE, width=24)

    # A
    ax = cx - 10
    draw.line([(ax - 40, cy + 70), (ax, cy - 70), (ax + 40, cy + 70)], fill=OUTLINE, width=26)
    draw.line([(ax - 25, cy + 15), (ax + 25, cy + 15)], fill=OUTLINE, width=22)

    # M
    mx = cx + 110
    draw.line([(mx - 45, cy + 70), (mx - 45, cy - 70), (mx, cy + 10), (mx + 45, cy - 70), (mx + 45, cy + 70)], fill=OUTLINE, width=24)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 25. BIRD (Cute chubby bluebird perched on a blossoming twig)
# ==============================================================================
def render_bird():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    BIRD_BLUE = (65, 170, 245, 255)
    BIRD_DARK = (35, 130, 215, 255)
    BELLY_YELLOW = (255, 220, 90, 255)
    WOOD_BROWN = (165, 110, 60, 255)
    LEAF_GREEN = (90, 195, 100, 255)

    # Perched Wooden Twig at Bottom
    draw.line([(cx - 280, cy + 240), (cx + 280, cy + 240)], fill=WOOD_BROWN, width=28)
    # Green leaves
    draw.ellipse([cx - 240, cy + 190, cx - 180, cy + 230], fill=LEAF_GREEN)
    draw.ellipse([cx + 180, cy + 190, cx + 240, cy + 230], fill=LEAF_GREEN)

    # Pointy Tail Feathers to left
    tail_pts = [(cx - 90, cy + 120), (cx - 240, cy + 100), (cx - 220, cy + 160), (cx - 70, cy + 170)]
    draw.polygon(tail_pts, fill=BIRD_DARK)

    # Chubby Round Body
    draw.ellipse([cx - 130, cy - 80, cx + 170, cy + 210], fill=BIRD_BLUE)
    # Sunny Yellow Breast
    draw.ellipse([cx + 10, cy - 20, cx + 170, cy + 190], fill=BELLY_YELLOW)

    # Folded Wing on back
    draw.ellipse([cx - 90, cy + 10, cx + 70, cy + 170], fill=BIRD_DARK)
    draw.arc([cx - 30, cy + 80, cx + 50, cy + 150], 30, 160, fill=OUTLINE, width=6)

    # Bird Head
    draw.ellipse([cx - 20, cy - 190, cx + 180, cy + 10], fill=BIRD_BLUE)

    # Little Orange Beak on right
    draw.polygon([(cx + 160, cy - 110), (cx + 250, cy - 80), (cx + 160, cy - 50)], fill=(255, 140, 20, 255))

    # Big Expressive Pediatric Eye
    draw_pediatric_eyes(draw, cx + 85, cx + 85, cy - 100, iris_color=(60, 150, 230, 255), pupil_r=32)

    # Cheerful Cheek Blush
    draw.ellipse([cx + 50, cy - 55, cx + 90, cy - 25], fill=ROSY_CHEEK)

    # Little Feet gripping branch
    for fx in [cx - 10, cx + 50]:
        draw.arc([fx - 18, cy + 215, fx + 18, cy + 255], 180, 360, fill=(255, 140, 20, 255), width=10)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 26. CAKE (Tiered birthday cake with strawberry frosting & candle)
# ==============================================================================
def render_cake():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 40

    CAKE_SPONGE = (255, 235, 195, 255)
    FROSTING_PINK = (255, 155, 185, 255)
    FROSTING_DARK = (235, 120, 155, 255)
    PLATE_WHITE = (245, 250, 255, 255)

    # White Cake Stand Plate
    draw.ellipse([cx - 260, cy + 180, cx + 260, cy + 270], fill=PLATE_WHITE)
    draw.ellipse([cx - 260, cy + 180, cx + 260, cy + 270], outline=(215, 225, 235, 255), width=10)

    # Bottom Cake Layer
    draw.rounded_rectangle([cx - 210, cy + 30, cx + 210, cy + 200], radius=35, fill=CAKE_SPONGE)
    # Pink Frosting Drips
    draw.rounded_rectangle([cx - 210, cy + 30, cx + 210, cy + 90], radius=25, fill=FROSTING_PINK)
    for dx in range(int(cx - 170), int(cx + 180), 50):
        draw.ellipse([dx - 18, cy + 70, dx + 18, cy + 115], fill=FROSTING_PINK)

    # Top Cake Layer
    draw.rounded_rectangle([cx - 140, cy - 100, cx + 140, cy + 30], radius=28, fill=CAKE_SPONGE)
    draw.rounded_rectangle([cx - 140, cy - 100, cx + 140, cy - 50], radius=20, fill=FROSTING_PINK)
    for dx in range(int(cx - 100), int(cx + 110), 45):
        draw.ellipse([dx - 14, cy - 65, dx + 14, cy - 30], fill=FROSTING_PINK)

    # Rainbow Sprinkles on Frosting
    for sx, sy, sc in [
        (cx - 120, cy + 50, (80, 180, 245, 255)), (cx + 80, cy + 60, (255, 215, 40, 255)),
        (cx - 60, cy - 80, (90, 205, 110, 255)), (cx + 50, cy - 75, (255, 115, 60, 255))
    ]:
        draw.rounded_rectangle([sx - 10, sy - 4, sx + 10, sy + 4], radius=4, fill=sc)

    # Birthday Candle in Center
    draw.rounded_rectangle([cx - 14, cy - 210, cx + 14, cy - 100], radius=8, fill=(85, 195, 245, 255))
    # Spiral stripe on candle
    draw.line([(cx - 12, cy - 170), (cx + 12, cy - 150)], fill=WHITE, width=6)
    draw.line([(cx - 12, cy - 130), (cx + 12, cy - 110)], fill=WHITE, width=6)
    # Wick
    draw.line([(cx, cy - 210), (cx, cy - 225)], fill=OUTLINE, width=6)

    # Glowing Flame
    draw.ellipse([cx - 24, cy - 280, cx + 24, cy - 225], fill=(255, 205, 30, 255))
    draw.ellipse([cx - 12, cy - 265, cx + 12, cy - 235], fill=(255, 95, 35, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 27. DRAW (Rainbow drawing pad with colorful crayons)
# ==============================================================================
def render_draw():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    # Drawing Sketchpad Paper
    draw.rounded_rectangle([cx - 230, cy - 190, cx + 230, cy + 220], radius=32, fill=WHITE)
    # Spiral Binding Rings at Top
    for rx in range(int(cx - 190), int(cx + 200), 45):
        draw.ellipse([rx - 10, cy - 220, rx + 10, cy - 170], fill=(200, 210, 225, 255))

    # Vibrant Rainbow Drawn on Pad
    rb_colors = [
        (245, 65, 75, 255), (255, 160, 35, 255), (255, 220, 45, 255),
        (85, 195, 105, 255), (65, 165, 245, 255), (155, 95, 220, 255)
    ]
    for idx, col in enumerate(rb_colors):
        rw = 260 - idx * 24
        draw.arc([cx - rw, cy - 110 - idx * 10, cx + rw, cy + 180 + idx * 10], 195, 345, fill=col, width=16)

    # Friendly Smiling Cloud at Rainbow Base
    for clx, cly, clr in [(cx - 120, cy + 60, 40), (cx - 70, cy + 50, 50), (cx - 30, cy + 65, 38)]:
        draw.ellipse([clx - clr, cly - clr, clx + clr, cly + clr], fill=(235, 245, 255, 255))

    # Crayons on Right (Red & Blue)
    for crx, cry, ccol in [(cx + 120, cy + 140, (245, 65, 75, 255)), (cx + 175, cy + 100, (65, 165, 245, 255))]:
        draw.polygon([(crx - 16, cry - 70), (crx + 16, cry - 70), (crx + 16, cry + 70), (crx - 16, cry + 70)], fill=ccol)
        draw.polygon([(crx - 16, cry - 70), (crx + 16, cry - 70), (crx, cry - 105)], fill=ccol) # Tip
        draw.rectangle([crx - 16, cry - 25, crx + 16, cry + 35], fill=(250, 245, 230, 255)) # Paper label

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 28. FACE (Cheerful smiling cartoon face with sparkling pediatric eyes)
# ==============================================================================
def render_face():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    SKIN = (255, 222, 195, 255)
    HAIR_BROWN = (110, 65, 30, 255)

    # Round Face
    draw.ellipse([cx - 230, cy - 220, cx + 230, cy + 220], fill=SKIN)

    # Curly Hair Tufts on top
    for hx, hy, hr in [(-110, -210, 55), (0, -235, 65), (110, -210, 55)]:
        draw.ellipse([cx + hx - hr, cy + hy - hr, cx + hx + hr, cy + hy + hr], fill=HAIR_BROWN)

    # Ears on sides
    draw.ellipse([cx - 260, cy - 40, cx - 200, cy + 40], fill=SKIN)
    draw.ellipse([cx + 200, cy - 40, cx + 260, cy + 40], fill=SKIN)

    # Big Expressive Pediatric Eyes
    draw_pediatric_eyes(draw, cx - 85, cx + 85, cy - 40, iris_color=(60, 160, 240, 255), pupil_r=46)

    # Cute Button Nose
    draw.ellipse([cx - 18, cy + 30, cx + 18, cy + 55], fill=(240, 150, 130, 255))

    # Giant Joyful Smile & Rosy Blush
    draw_cheeks_and_mouth(draw, cx, cy + 85, mouth_w=48, smile_depth=32, has_tongue=True)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 29. GAP (Two grassy cliffs with a canyon gap & wooden suspension bridge)
# ==============================================================================
def render_gap():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    CLIFF_STONE = (145, 125, 115, 255)
    GRASS_GREEN = (95, 195, 75, 255)
    BRIDGE_WOOD = (185, 125, 70, 255)
    ROPE = (215, 175, 115, 255)

    # Left Cliff
    draw.polygon([(0, cy - 30), (cx - 120, cy - 30), (cx - 100, H), (0, H)], fill=CLIFF_STONE)
    draw.rounded_rectangle([0, cy - 50, cx - 110, cy + 10], radius=24, fill=GRASS_GREEN)

    # Right Cliff
    draw.polygon([(cx + 120, cy - 30), (W, cy - 30), (W, H), (cx + 100, H)], fill=CLIFF_STONE)
    draw.rounded_rectangle([cx + 110, cy - 50, W, cy + 10], radius=24, fill=GRASS_GREEN)

    # Deep Canyon Gap in middle (Shadows & tiny river below)
    draw.ellipse([cx - 80, cy + 240, cx + 80, cy + 340], fill=(85, 185, 235, 255))

    # Suspension Rope Bridge Spanning the Gap
    # Upper handrails
    rope_pts = bezier_curve((cx - 120, cy - 10), (cx - 40, cy + 50), (cx + 40, cy + 50), (cx + 120, cy - 10), 20)
    for i in range(len(rope_pts) - 1):
        draw.line([rope_pts[i], rope_pts[i+1]], fill=ROPE, width=12)

    # Wooden Planks along bridge
    for px in range(int(cx - 100), int(cx + 110), 28):
        # sag formula
        py = cy + 40 + ((px - cx) / 80) ** 2 * 12
        draw.line([(px - 10, py), (px + 10, py)], fill=BRIDGE_WOOD, width=16)
        # Vertical support ropes
        draw.line([(px, py - 40), (px, py)], fill=ROPE, width=6)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 30. HAND (Friendly cartoon hand in welcoming wave)
# ==============================================================================
def render_hand():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    SKIN = (255, 220, 195, 255)
    CREASE = (235, 160, 140, 255)

    # Wrist / Arm at bottom
    draw.rounded_rectangle([cx - 80, cy + 140, cx + 80, cy + 320], radius=35, fill=SKIN)

    # Palm Center
    draw.ellipse([cx - 140, cy - 70, cx + 140, cy + 170], fill=SKIN)

    # 4 Friendly Fingers
    # Thumb (curving to left)
    draw.rounded_rectangle([cx - 240, cy + 10, cx - 110, cy + 90], radius=38, fill=SKIN)

    # Index Finger
    draw.rounded_rectangle([cx - 120, cy - 250, cx - 50, cy - 40], radius=35, fill=SKIN)
    # Middle Finger (tallest)
    draw.rounded_rectangle([cx - 40, cy - 280, cx + 30, cy - 40], radius=35, fill=SKIN)
    # Ring Finger
    draw.rounded_rectangle([cx + 40, cy - 260, cx + 110, cy - 40], radius=35, fill=SKIN)
    # Pinky Finger
    draw.rounded_rectangle([cx + 120, cy - 200, cx + 185, cy - 20], radius=32, fill=SKIN)

    # Gentle Palm Crease lines
    draw.arc([cx - 60, cy + 20, cx + 60, cy + 100], 30, 150, fill=CREASE, width=8)

    # Motion lines on left indicating cheerful waving
    for dy in [-120, -50, 20]:
        draw.arc([cx - 280, cy + dy, cx - 220, cy + dy + 60], 120, 240, fill=OUTLINE, width=8)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 31. LIT (Glowing incandescent lightbulb with radiant energy sparks)
# ==============================================================================
def render_lit():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 - 20

    BULB_YELLOW = (255, 230, 60, 255)
    GLOW_AURA = (255, 245, 140, 180)
    SCREW_STEEL = (195, 205, 215, 255)

    # Radiant Glow Rays
    for deg in range(0, 360, 30):
        ang = math.radians(deg)
        r0 = 240
        r1 = 320
        p0 = (cx + r0 * math.cos(ang), cy + r0 * math.sin(ang))
        p1 = (cx + r1 * math.cos(ang), cy + r1 * math.sin(ang))
        draw.line([p0, p1], fill=(255, 215, 30, 255), width=14)

    # Soft Glow Aura
    draw.ellipse([cx - 240, cy - 240, cx + 240, cy + 240], fill=GLOW_AURA)

    # Glass Bulb Base Contour
    draw.ellipse([cx - 180, cy - 190, cx + 180, cy + 120], fill=BULB_YELLOW)
    # Tapering throat
    draw.polygon([(cx - 140, cy + 40), (cx + 140, cy + 40), (cx + 85, cy + 180), (cx - 85, cy + 180)], fill=BULB_YELLOW)

    # Glossy Reflection Arc
    draw.arc([cx - 145, cy - 160, cx - 60, cy + 40], 120, 230, fill=WHITE, width=20)

    # Cheerful Face on Bulb
    draw_pediatric_eyes(draw, cx - 65, cx + 65, cy - 30, iris_color=OUTLINE, pupil_r=26)
    draw_cheeks_and_mouth(draw, cx, cy + 35, mouth_w=24, smile_depth=16, has_tongue=True)

    # Metal Screw Base at Bottom
    for sy in [cy + 180, cy + 215, cy + 250]:
        draw.rounded_rectangle([cx - 85, sy, cx + 85, sy + 25], radius=12, fill=SCREW_STEEL)
    # Rounded black contact point
    draw.ellipse([cx - 45, cy + 270, cx + 45, cy + 310], fill=OUTLINE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 32. MOB (Cheerful crowd of three friendly diverse cartoon character faces)
# ==============================================================================
def render_mob():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    # Character 1: Left (Bunny/Girl with purple bow)
    c1x, c1y = cx - 160, cy + 20
    draw.ellipse([c1x - 110, c1y - 110, c1x + 110, c1y + 110], fill=(255, 215, 190, 255))
    draw_pediatric_eyes(draw, c1x - 35, c1x + 35, c1y - 20, iris_color=(60, 160, 240, 255), pupil_r=22)
    draw_thick_arc(draw, [c1x - 20, c1x + 15, c1x + 20, c1x + 35], 20, 160, OUTLINE, 5)

    # Character 2: Right (Smiling boy with cap)
    c2x, c2y = cx + 160, cy + 20
    draw.ellipse([c2x - 110, c2y - 110, c2x + 110, c2y + 110], fill=(245, 195, 160, 255))
    draw_pediatric_eyes(draw, c2x - 35, c2x + 35, c2y - 20, iris_color=(75, 185, 95, 255), pupil_r=22)
    draw_thick_arc(draw, [c2x - 20, c2y + 15, c2x + 20, c2y + 35], 20, 160, OUTLINE, 5)

    # Character 3: Center (Hero Lily/Child upfront)
    c3x, c3y = cx, cy - 30
    draw.ellipse([c3x - 130, c3y - 130, c3x + 130, c3y + 130], fill=(255, 225, 200, 255))
    # Hair
    draw.ellipse([c3x - 140, c3y - 160, c3x + 140, c3y - 40], fill=(115, 65, 30, 255))
    draw_pediatric_eyes(draw, c3x - 48, c3x + 48, c3y - 20, iris_color=(245, 140, 35, 255), pupil_r=30)
    draw_cheeks_and_mouth(draw, c3x, c3y + 35, mouth_w=28, smile_depth=18, has_tongue=True)

    # Cheering Hands raised
    draw.ellipse([cx - 260, cy - 140, cx - 200, cy - 80], fill=(255, 215, 190, 255))
    draw.ellipse([cx + 200, cy - 140, cx + 260, cy - 80], fill=(245, 195, 160, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 33. ROAD (Storybook winding country road through rolling green hills)
# ==============================================================================
def render_road():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    SKY_BLUE = (185, 230, 255, 255)
    HILL_GREEN1 = (110, 205, 85, 255)
    HILL_GREEN2 = (80, 175, 65, 255)
    ROAD_ASPHALT = (120, 135, 145, 255)
    STRIPE_YELLOW = (255, 220, 45, 255)

    # Rolling Hills Landscape (Circular badge base)
    draw.ellipse([cx - 260, cy - 260, cx + 260, cy + 260], fill=SKY_BLUE)

    # Background Hill
    draw.ellipse([cx - 320, cy - 90, cx + 180, cy + 290], fill=HILL_GREEN2)
    # Foreground Hill
    draw.ellipse([cx - 100, cy - 40, cx + 340, cy + 340], fill=HILL_GREEN1)

    # Winding Road Perspective
    road_pts = (
        bezier_curve((cx + 20, cy - 30), (cx - 60, cy + 40), (cx + 80, cy + 120), (cx + 220, cy + 260), 30) +
        bezier_curve((cx + 140, cy + 260), (cx - 10, cy + 140), (cx - 110, cy + 60), (cx - 20, cy - 30), 30)
    )
    draw.polygon(road_pts, fill=ROAD_ASPHALT)

    # Dashed Yellow Center Line
    dash_pts = bezier_curve((cx, cy - 25), (cx - 30, cy + 45), (cx + 35, cy + 125), (cx + 180, cy + 260), 20)
    for i in range(0, len(dash_pts) - 1, 3):
        draw.line([dash_pts[i], dash_pts[i+1]], fill=STRIPE_YELLOW, width=12)

    # Roadside Trees & Flowers
    draw.ellipse([cx - 180, cy + 20, cx - 120, cy + 90], fill=(60, 155, 55, 255))
    draw.ellipse([cx + 160, cy + 40, cx + 220, cy + 110], fill=(60, 155, 55, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 34. SPIN (Colorful spinning top toy with dynamic swirl motion lines)
# ==============================================================================
def render_spin():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 - 10

    TOP_RED = (245, 65, 75, 255)
    TOP_YELLOW = (255, 215, 30, 255)
    TOP_BLUE = (55, 155, 245, 255)
    TIP_STEEL = (200, 210, 220, 255)

    # Dynamic Swirl Motion Rings around the top
    for dy, rw in [(-50, 260), (50, 280), (140, 220)]:
        draw.arc([cx - rw, cy + dy - 40, cx + rw, cy + dy + 40], 190, 350, fill=(255, 255, 255, 180), width=10)
        # Motion sparkles
        draw.ellipse([cx + rw - 30, cy + dy - 10, cx + rw, cy + dy + 20], fill=(255, 225, 40, 255))

    # Pull handle at top
    draw.rounded_rectangle([cx - 20, cy - 260, cx + 20, cy - 140], radius=12, fill=(185, 125, 75, 255))
    draw.ellipse([cx - 40, cy - 280, cx + 40, cy - 230], fill=TOP_RED)

    # Top Upper Dome (Red)
    draw.ellipse([cx - 190, cy - 160, cx + 190, cy + 40], fill=TOP_RED)

    # Middle Band (Yellow)
    draw.rounded_rectangle([cx - 210, cy - 40, cx + 210, cy + 40], radius=24, fill=TOP_YELLOW)
    # Striped zig-zag on band
    for zx in range(int(cx - 180), int(cx + 180), 40):
        draw.line([(zx, cy - 25), (zx + 20, cy + 25)], fill=OUTLINE, width=8)

    # Lower Inverted Cone (Blue)
    draw.polygon([(cx - 190, cy + 10), (cx + 190, cy + 10), (cx, cy + 240)], fill=TOP_BLUE)

    # Metal Spinning Tip at Bottom
    draw.polygon([(cx - 18, cy + 230), (cx + 18, cy + 230), (cx, cy + 290)], fill=TIP_STEEL)

    # Highlights
    draw.ellipse([cx - 140, cy - 120, cx - 70, cy - 70], fill=WHITE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 35. SUM (Wooden number blocks 1 + 2 = 3 with calculation sparkles)
# ==============================================================================
def render_sum():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    BLOCK_BLUE = (65, 160, 245, 255)
    BLOCK_GREEN = (85, 195, 95, 255)
    BLOCK_ORANGE = (255, 140, 30, 255)

    def draw_cube(bx, by, size, color, label):
        # Front face
        draw.rounded_rectangle([bx - size, by - size, bx + size, by + size], radius=20, fill=color)
        # Inner beveled border
        draw.rounded_rectangle([bx - size + 12, by - size + 12, bx + size - 12, by + size - 12], radius=14, outline=WHITE, width=8)
        # Big white label
        if label == "1":
            draw.line([(bx, by - 45), (bx, by + 45)], fill=WHITE, width=20)
            draw.line([(bx - 20, by - 25), (bx, by - 45)], fill=WHITE, width=16)
        elif label == "2":
            draw.arc([bx - 30, by - 50, bx + 30, by], 180, 0, fill=WHITE, width=18)
            draw.line([(bx + 30, by - 20), (bx - 30, by + 45), (bx + 30, by + 45)], fill=WHITE, width=18)
        elif label == "3":
            draw.arc([bx - 30, by - 50, bx + 30, by], 270, 90, fill=WHITE, width=18)
            draw.arc([bx - 30, by, bx + 30, by + 50], 270, 90, fill=WHITE, width=18)
        elif label == "+":
            draw.line([(bx - 30, by), (bx + 30, by)], fill=OUTLINE, width=18)
            draw.line([(bx, by - 30), (bx, by + 30)], fill=OUTLINE, width=18)
        elif label == "=":
            draw.line([(bx - 28, by - 14), (bx + 28, by - 14)], fill=OUTLINE, width=16)
            draw.line([(bx - 28, by + 14), (bx + 28, by + 14)], fill=OUTLINE, width=16)

    # Block 1 (Blue)
    draw_cube(cx - 200, cy, 75, BLOCK_BLUE, "1")

    # "+" Sign
    draw_cube(cx - 90, cy, 30, (240, 245, 250, 255), "+")

    # Block 2 (Green)
    draw_cube(cx + 10, cy, 75, BLOCK_GREEN, "2")

    # "=" Sign
    draw_cube(cx + 115, cy, 30, (240, 245, 250, 255), "=")

    # Block 3 (Orange)
    draw_cube(cx + 220, cy, 75, BLOCK_ORANGE, "3")

    # Golden Math Sparkles at Top
    for sx, sy in [(cx - 180, cy - 140), (cx + 20, cy - 140), (cx + 220, cy - 140)]:
        draw.ellipse([sx - 16, sy - 16, sx + 16, sy + 16], fill=(255, 215, 30, 255))
        draw.line([(sx - 24, sy), (sx + 24, sy)], fill=(255, 215, 30, 255), width=6)
        draw.line([(sx, sy - 24), (sx, sy + 24)], fill=(255, 215, 30, 255), width=6)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 36. WARM (Radiant smiling sun with cute shades & warm tropical glow)
# ==============================================================================
def render_warm():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    SUN_YELLOW = (255, 215, 30, 255)
    SUN_ORANGE = (255, 140, 20, 255)

    # 12 Wavy/Triangular Sun Rays
    for deg in range(0, 360, 30):
        ang = math.radians(deg)
        r_tip = 320
        r_base = 210
        ang1 = math.radians(deg - 14)
        ang2 = math.radians(deg + 14)

        pts = [
            (cx + r_base * math.cos(ang1), cy + r_base * math.sin(ang1)),
            (cx + r_tip * math.cos(ang), cy + r_tip * math.sin(ang)),
            (cx + r_base * math.cos(ang2), cy + r_base * math.sin(ang2)),
        ]
        draw.polygon(pts, fill=SUN_ORANGE)

    # Main Center Sun Circle
    draw.ellipse([cx - 210, cy - 210, cx + 210, cy + 210], fill=SUN_YELLOW)

    # Cool Pediatric Heart / Star Sunglasses!
    # Frame
    draw.rounded_rectangle([cx - 170, cy - 70, cx - 15, cy + 25], radius=28, fill=OUTLINE)
    draw.rounded_rectangle([cx + 15, cy - 70, cx + 170, cy + 25], radius=28, fill=OUTLINE)
    # Bridge
    draw.line([(cx - 15, cy - 30), (cx + 15, cy - 30)], fill=OUTLINE, width=16)

    # Tinted Lenses (Teal gradient glass with gleam)
    draw.rounded_rectangle([cx - 158, cy - 58, cx - 27, cy + 15], radius=20, fill=(60, 185, 225, 255))
    draw.rounded_rectangle([cx + 27, cy - 58, cx + 158, cy + 15], radius=20, fill=(60, 185, 225, 255))
    draw.line([(cx - 140, cy - 45), (cx - 80, cy + 5)], fill=WHITE, width=8)
    draw.line([(cx + 45, cy - 45), (cx + 105, cy + 5)], fill=WHITE, width=8)

    # Cheerful Radiant Smile & Blush
    draw_cheeks_and_mouth(draw, cx, cy + 85, mouth_w=42, smile_depth=26, has_tongue=True)

    return apply_pediatric_outline(img, stroke_w=7)


# ==============================================================================
# MAIN BATCH GENERATOR & DEPLOYMENT
# ==============================================================================
RENDERERS = {
    "blendword_bus.png": render_bus,
    "blendword_sub.png": render_sub,
    "blendword_mom.png": render_mom,
    "blendword_bee.png": render_bee,
    "blendword_bib.png": render_bib,
    "blendword_bat.png": render_bat,
    "blendword_kit.png": render_kit,
    "blendword_toy.png": render_toy,
    "blendword_boy.png": render_boy,
    "blendword_pan.png": render_pan,
    "blendword_bug.png": render_bug,
    "blendword_pin.png": render_pin,
    "blendword_nap.png": render_nap,
    "blendword_hen.png": render_hen,
    "blendword_bed.png": render_bed,
    "blendword_web.png": render_web,
    "blendword_fan.png": render_fan,
    "blendword_cap.png": render_cap,
    "blendword_cup.png": render_cup,
    "blendword_jam.png": render_jam,
    "blendword_fox.png": render_fox,
    "blendword_zoo.png": render_zoo,
    "blendword_quiz.png": render_quiz,
    "blendword_bam.png": render_bam,
    "blendword_bird.png": render_bird,
    "blendword_cake.png": render_cake,
    "blendword_draw.png": render_draw,
    "blendword_face.png": render_face,
    "blendword_gap.png": render_gap,
    "blendword_hand.png": render_hand,
    "blendword_lit.png": render_lit,
    "blendword_mob.png": render_mob,
    "blendword_road.png": render_road,
    "blendword_spin.png": render_spin,
    "blendword_sum.png": render_sum,
    "blendword_warm.png": render_warm,
}

def main():
    target_dir = "app/src/main/assets/images/pictures"
    os.makedirs(target_dir, exist_ok=True)

    print(f"Generating {len(RENDERERS)} Pediatric Blend-It Word Illustrations...")
    for filename, renderer in RENDERERS.items():
        print(f"  Rendering {filename} (1024x1024 -> 512x512 Lanczos)...")
        img_1024 = renderer()
        img_512 = img_1024.resize((SIZE, SIZE), resample=Image.Resampling.LANCZOS)
        
        target_path = os.path.join(target_dir, filename)
        img_512.save(target_path, "PNG", optimize=True)
        print(f"  -> Saved {target_path} ({img_512.size[0]}x{img_512.size[1]} RGBA)")

    print("\nAll 36 Blend-It word illustrations generated and deployed successfully!")

if __name__ == "__main__":
    main()
