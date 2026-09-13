"""
PlayIT Pediatric Objects & Props Generator — Master Edition
Generates all remaining picture and blendword objects adhering to Duolingo ABC
pediatric standards:
- 2x Lanczos supersampling (1024x1024 -> 512x512 RGBA)
- Continuous #2D373E pediatric sticker outline
- Layered 3-tone shading (base, depth shadow, specular highlights)
- 100% transparent RGBA backgrounds
- Zero emojis
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

# ==============================================================================
# 1. AXE (Storybook woodsman axe with polished steel head & ash handle)
# ==============================================================================
def render_axe():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    WOOD = (205, 145, 85, 255)
    STEEL = (215, 225, 235, 255)
    STEEL_EDGE = (245, 250, 255, 255)

    # Curved Ash Wood Handle
    handle = bezier_points((cx - 180, cy + 280), (cx - 120, cy + 180), (cx + 80, cy - 80), (cx + 160, cy - 220), 30)
    for i in range(len(handle) - 1):
        draw.line([handle[i], handle[i+1]], fill=WOOD, width=54)

    # Steel Axe Head
    ax_pts = [
        (cx + 80, cy - 260), (cx + 250, cy - 230), (cx + 270, cy - 100),
        (cx + 170, cy - 80), (cx + 70, cy - 170)
    ]
    draw.polygon(ax_pts, fill=STEEL)
    draw.polygon([(cx + 220, cy - 230), (cx + 270, cy - 230), (cx + 270, cy - 100), (cx + 230, cy - 90)], fill=STEEL_EDGE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 2. BAÑO (Bathtub with warm water, bubbles, brass faucet & rubber duckie)
# ==============================================================================
def render_bano():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    TUB_WHITE = (248, 250, 252, 255)
    TUB_RIM = (72, 185, 175, 255)
    BRASS = (245, 185, 45, 255)
    WATER = (145, 220, 250, 230)
    DUCK_YELLOW = (255, 215, 20, 255)

    # Tub Feet (Golden Clawfoot)
    for fx in [cx - 190, cx + 190]:
        draw.ellipse([fx - 30, cy + 180, fx + 30, cy + 240], fill=BRASS)

    # Bathtub Porcelain Basin
    draw.rounded_rectangle([cx - 260, cy - 20, cx + 260, cy + 200], radius=60, fill=TUB_WHITE)
    draw.rounded_rectangle([cx - 275, cy - 40, cx + 275, cy + 10], radius=24, fill=TUB_RIM)

    # Warm Water
    draw.rounded_rectangle([cx - 240, cy, cx + 240, cy + 80], radius=20, fill=WATER)

    # Brass Faucet on left
    draw.arc([cx - 250, cy - 180, cx - 170, cy - 40], 180, 360, fill=BRASS, width=28)
    draw.rounded_rectangle([cx - 220, cy - 50, cx - 180, cy - 10], radius=8, fill=BRASS)

    # Soap Bubbles floating
    for bx, by, br in [
        (cx - 140, cy - 40, 35), (cx - 70, cy - 70, 45), (cx + 20, cy - 50, 40),
        (cx + 90, cy - 80, 32), (cx + 170, cy - 35, 38)
    ]:
        draw.ellipse([bx - br, by - br, bx + br, by + br], fill=(215, 245, 255, 210), outline=WHITE, width=6)

    # Rubber Duckie floating on right
    dx, dy = cx + 110, cy - 30
    draw.ellipse([dx - 50, dy - 25, dx + 40, dy + 35], fill=DUCK_YELLOW)
    draw.ellipse([dx - 40, dy - 60, dx + 10, dy - 15], fill=DUCK_YELLOW)
    draw.polygon([(dx - 40, dy - 40), (dx - 65, dy - 32), (dx - 40, dy - 25)], fill=(255, 130, 20, 255))
    draw.ellipse([dx - 28, dy - 50, dx - 18, dy - 40], fill=OUTLINE)
    draw.ellipse([dx - 26, dy - 48, dx - 20, dy - 42], fill=WHITE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 3. DRUM (Marching snare drum with red/yellow chevron pattern & drumsticks)
# ==============================================================================
def render_drum():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    DRUM_RED = (235, 60, 70, 255)
    DRUM_GOLD = (255, 205, 35, 255)
    SKIN_WHITE = (248, 250, 252, 255)

    # Drumsticks crossed behind
    draw.line([(cx - 240, cy - 180), (cx + 140, cy + 200)], fill=(215, 165, 110, 255), width=22)
    draw.ellipse([cx - 260, cy - 200, cx - 220, cy - 160], fill=(235, 185, 130, 255))
    draw.line([(cx + 240, cy - 180), (cx - 140, cy + 200)], fill=(215, 165, 110, 255), width=22)
    draw.ellipse([cx + 220, cy - 200, cx + 260, cy - 160], fill=(235, 185, 130, 255))

    # Drum Cylindrical Body
    draw.rounded_rectangle([cx - 220, cy - 50, cx + 220, cy + 190], radius=30, fill=DRUM_RED)
    # Chevron zig-zags
    for zx in range(int(cx - 200), int(cx + 210), 70):
        draw.line([(zx, cy - 40), (zx + 35, cy + 180), (zx + 70, cy - 40)], fill=DRUM_GOLD, width=12)

    # Bottom Rim
    draw.ellipse([cx - 225, cy + 150, cx + 225, cy + 210], fill=DRUM_GOLD)

    # Top Drumhead Rim & Skin
    draw.ellipse([cx - 230, cy - 90, cx + 230, cy + 20], fill=DRUM_GOLD)
    draw.ellipse([cx - 210, cy - 80, cx + 210, cy + 10], fill=SKIN_WHITE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 4. EGG (Brown speckled egg next to cracked half with sunny yolk)
# ==============================================================================
def render_egg():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    EGG_BROWN = (235, 195, 155, 255)
    EGG_DARK = (205, 165, 125, 255)
    WHITE_SHELL = (255, 250, 245, 255)
    YOLK = (255, 185, 25, 255)

    # Whole Egg on left
    draw.ellipse([cx - 220, cy - 160, cx + 10, cy + 180], fill=EGG_BROWN)
    # Speckles
    for sx, sy in [(cx - 150, cy - 60), (cx - 80, cy - 90), (cx - 120, cy + 50), (cx - 60, cy + 30)]:
        draw.ellipse([sx - 6, sy - 6, sx + 6, sy + 6], fill=EGG_DARK)

    # Cracked Shell Bowl on right
    draw.chord([cx - 20, cy - 40, cx + 240, cy + 190], start=0, end=180, fill=WHITE_SHELL)
    # Jagged crack rim
    crack_pts = [(cx - 20, cy + 75), (cx + 30, cy + 50), (cx + 80, cy + 75), (cx + 140, cy + 45), (cx + 190, cy + 75), (cx + 240, cy + 75)]
    for i in range(len(crack_pts) - 1):
        draw.line([crack_pts[i], crack_pts[i+1]], fill=WHITE_SHELL, width=16)

    # Golden Yolk sitting in egg white
    draw.ellipse([cx + 50, cy + 40, cx + 170, cy + 150], fill=YOLK)
    draw.ellipse([cx + 70, cy + 55, cx + 105, cy + 85], fill=WHITE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 5. GIFT (Teal gift box with magenta satin ribbon & bouncy bow)
# ==============================================================================
def render_gift():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    BOX_TEAL = (55, 185, 195, 255)
    BOX_DARK = (35, 145, 155, 255)
    RIBBON_PINK = (245, 65, 125, 255)

    # Bouncy Bow Loops on Top
    draw.ellipse([cx - 140, cy - 190, cx - 10, cy - 60], fill=RIBBON_PINK)
    draw.ellipse([cx - 110, cy - 160, cx - 40, cy - 90], fill=OUTLINE)
    draw.ellipse([cx + 10, cy - 190, cx + 140, cy - 60], fill=RIBBON_PINK)
    draw.ellipse([cx + 40, cy - 160, cx + 110, cy - 90], fill=OUTLINE)
    draw.ellipse([cx - 30, cy - 130, cx + 30, cy - 70], fill=RIBBON_PINK)

    # Box Body
    draw.rounded_rectangle([cx - 200, cy - 40, cx + 200, cy + 240], radius=32, fill=BOX_TEAL)
    # Box Lid
    draw.rounded_rectangle([cx - 220, cy - 80, cx + 220, cy - 20], radius=20, fill=BOX_TEAL)
    draw.rounded_rectangle([cx - 220, cy - 35, cx + 220, cy - 20], radius=10, fill=BOX_DARK)

    # Vertical Ribbon
    draw.rectangle([cx - 35, cy - 80, cx + 35, cy + 240], fill=RIBBON_PINK)
    # Horizontal Ribbon
    draw.rectangle([cx - 200, cy + 60, cx + 200, cy + 120], fill=RIBBON_PINK)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 6. IGLOO (Cozy rounded dome igloo built with cyan ice blocks)
# ==============================================================================
def render_igloo():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    ICE_CYAN = (215, 240, 255, 255)
    ICE_DARK = (165, 210, 240, 255)
    SNOW = (245, 250, 255, 255)

    # Snow Base
    draw.ellipse([cx - 260, cy + 160, cx + 260, cy + 260], fill=SNOW)

    # Main Dome
    draw.chord([cx - 230, cy - 190, cx + 230, cy + 210], start=180, end=360, fill=ICE_CYAN)

    # Ice Block Seam Arcs
    for r in [100, 160, 210]:
        draw_thick_arc(draw, [cx - r, cy + 20 - r, cx + r, cy + 20 + r], 180, 360, ICE_DARK, 6)

    # Tunnel Entrance on right
    draw.chord([cx + 50, cy + 40, cx + 240, cy + 210], start=180, end=360, fill=ICE_DARK)
    draw.chord([cx + 80, cy + 80, cx + 210, cy + 210], start=180, end=360, fill=OUTLINE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 7. INK (Glass inkwell bottle with cork stopper & feather quill)
# ==============================================================================
def render_ink():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0 - 40, H / 2.0 + 30

    INK_BLUE = (45, 95, 195, 255)
    GLASS = (195, 225, 250, 200)
    CORK = (195, 140, 85, 255)
    FEATHER = (245, 195, 45, 255)

    # Feather Quill Angled in Inkwell
    draw.line([(cx + 30, cy - 20), (cx + 250, cy - 270)], fill=(225, 230, 235, 255), width=16)
    # Feather vane
    feather_pts = (
        bezier_points((cx + 100, cy - 100), (cx + 170, cy - 220), (cx + 270, cy - 290), (cx + 250, cy - 270)) +
        bezier_points((cx + 250, cy - 270), (cx + 210, cy - 180), (cx + 120, cy - 70), (cx + 100, cy - 100))
    )
    draw.polygon(feather_pts, fill=FEATHER)

    # Glass Inkwell Body
    draw.rounded_rectangle([cx - 180, cy - 20, cx + 180, cy + 210], radius=40, fill=GLASS)
    # Rich Blue Ink inside
    draw.rounded_rectangle([cx - 165, cy + 20, cx + 165, cy + 195], radius=30, fill=INK_BLUE)

    # Bottle Neck & Cork
    draw.rounded_rectangle([cx - 80, cy - 70, cx + 80, cy - 15], radius=16, fill=GLASS)
    draw.rounded_rectangle([cx - 65, cy - 110, cx + 65, cy - 65], radius=12, fill=CORK)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 8. JET (Modern passenger jet airliner soaring through white clouds)
# ==============================================================================
def render_jet():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    JET_WHITE = (248, 250, 252, 255)
    WING_BLUE = (45, 125, 235, 255)
    CLOUD = (235, 245, 255, 200)

    # Background Clouds
    for clx, cly, clr in [(cx - 240, cy + 140, 70), (cx + 200, cy + 160, 80), (cx - 120, cy + 170, 90)]:
        draw.ellipse([clx - clr, cly - clr, clx + clr, cly + clr], fill=CLOUD)

    # Jet Wings
    # Left wing (top in angled view)
    draw.polygon([(cx - 70, cy - 40), (cx - 180, cy - 210), (cx - 90, cy - 210), (cx + 50, cy - 40)], fill=WING_BLUE)
    # Right wing
    draw.polygon([(cx - 70, cy + 40), (cx - 160, cy + 220), (cx - 80, cy + 220), (cx + 50, cy + 40)], fill=WING_BLUE)

    # Tail Fin
    draw.polygon([(cx - 240, cy), (cx - 320, cy - 140), (cx - 250, cy - 140), (cx - 180, cy)], fill=WING_BLUE)

    # Main Fuselage
    draw.ellipse([cx - 280, cy - 70, cx + 290, cy + 70], fill=JET_WHITE)
    # Nose cone
    draw.ellipse([cx + 170, cy - 60, cx + 290, cy + 60], fill=JET_WHITE)

    # Blue stripe & passenger windows
    draw.line([(cx - 230, cy), (cx + 210, cy)], fill=WING_BLUE, width=16)
    for wx in range(int(cx - 140), int(cx + 180), 45):
        draw.ellipse([wx - 8, cy - 25, wx + 8, cy - 10], fill=(60, 160, 240, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 9. JUG (Ceramic terracotta pitcher with curved handle & spout)
# ==============================================================================
def render_jug():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0 - 20, H / 2.0 + 20

    TERRACOTTA = (225, 115, 65, 255)
    TERRA_DARK = (185, 85, 45, 255)
    GLAZE_CREAM = (255, 240, 215, 255)

    # Handle on right
    draw.arc([cx + 40, cy - 80, cx + 240, cy + 140], 280, 80, fill=TERRACOTTA, width=44)

    # Bulbous Basin
    draw.ellipse([cx - 180, cy - 40, cx + 160, cy + 240], fill=TERRACOTTA)
    draw.ellipse([cx - 180, cy + 100, cx + 160, cy + 240], fill=TERRA_DARK)

    # Neck and Spout
    draw.polygon([(cx - 100, cy - 30), (cx + 80, cy - 30), (cx + 60, cy - 170), (cx - 160, cy - 170)], fill=TERRACOTTA)
    # Spout lip
    draw.polygon([(cx - 160, cy - 170), (cx - 210, cy - 185), (cx - 130, cy - 150)], fill=TERRACOTTA)

    # Decorative Cream Band
    draw.rounded_rectangle([cx - 160, cy + 40, cx + 140, cy + 100], radius=16, fill=GLAZE_CREAM)
    for zx in range(int(cx - 140), int(cx + 130), 35):
        draw.polygon([(zx, cy + 50), (zx + 15, cy + 85), (zx - 15, cy + 85)], fill=TERRACOTTA)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 10. LEAF (Crisp tropical green leaf with veins & dewdrops)
# ==============================================================================
def render_leaf():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    LEAF_GREEN = (90, 205, 85, 255)
    LEAF_DARK = (60, 165, 60, 255)
    VEIN = (145, 235, 130, 255)

    # Leaf Blade Shape
    leaf_pts = (
        bezier_points((cx - 220, cy + 220), (cx - 250, cy - 50), (cx - 120, cy - 250), (cx + 220, cy - 220)) +
        bezier_points((cx + 220, cy - 220), (cx + 250, cy + 50), (cx + 120, cy + 250), (cx - 220, cy + 220))
    )
    draw.polygon(leaf_pts, fill=LEAF_GREEN)

    # Shading on one half
    draw.polygon(bezier_points((cx - 220, cy + 220), (cx - 250, cy - 50), (cx - 120, cy - 250), (cx + 220, cy - 220)), fill=LEAF_DARK)

    # Central Stem Vein
    draw.line([(cx - 240, cy + 250), (cx + 220, cy - 220)], fill=VEIN, width=16)

    # Side Veins
    for vx, vy, vlen in [(-120, 130, 80), (-40, 50, 100), (40, -30, 90), (120, -110, 70)]:
        draw.line([(cx + vx, cy + vy), (cx + vx - 50, cy + vy - vlen)], fill=VEIN, width=10)
        draw.line([(cx + vx, cy + vy), (cx + vx + vlen, cy + vy + 50)], fill=VEIN, width=10)

    # Dewdrops
    draw.ellipse([cx - 80, cy - 100, cx - 40, cy - 60], fill=(215, 245, 255, 220), outline=WHITE, width=4)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 11. MAP (Unfurled treasure map parchment with dotted trail & red X)
# ==============================================================================
def render_map():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    PARCHMENT = (245, 225, 185, 255)
    PARCH_DARK = (215, 185, 140, 255)
    RED_X = (235, 55, 65, 255)

    # Rolled Parchment Edges
    draw.ellipse([cx - 230, cy - 170, cx - 170, cy + 170], fill=PARCH_DARK)
    draw.ellipse([cx + 170, cy - 170, cx + 230, cy + 170], fill=PARCH_DARK)

    # Parchment Center Sheet
    draw.rounded_rectangle([cx - 200, cy - 160, cx + 200, cy + 160], radius=24, fill=PARCHMENT)

    # Compass Rose at Top-Left
    cmx, cmy = cx - 110, cy - 70
    draw.ellipse([cmx - 35, cmy - 35, cmx + 35, cmy + 35], outline=PARCH_DARK, width=6)
    draw.line([(cmx, cmy - 45), (cmx, cmy + 45)], fill=OUTLINE, width=6)
    draw.line([(cmx - 45, cmy), (cmx + 45, cmy)], fill=OUTLINE, width=6)

    # Dotted Adventure Trail
    trail = bezier_points((cx - 100, cy + 80), (cx - 20, cy + 20), (cx + 30, cy + 100), (cx + 110, cy - 30), 20)
    for i in range(0, len(trail) - 1, 2):
        draw.line([trail[i], trail[i+1]], fill=(140, 85, 45, 255), width=10)

    # Red "X" Marks the Spot
    xx, xy = cx + 110, cy - 30
    draw.line([(xx - 35, xy - 35), (xx + 35, xy + 35)], fill=RED_X, width=16)
    draw.line([(xx - 35, xy + 35), (xx + 35, xy - 35)], fill=RED_X, width=16)

    # Little Mountains and Palm Trees on Map
    draw.polygon([(cx - 40, cy + 50), (cx - 10, cy), (cx + 20, cy + 50)], fill=PARCH_DARK)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 12. NEST (Twig bird's nest on leafy branch with 3 pastel blue eggs)
# ==============================================================================
def render_nest():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    BRANCH = (145, 95, 50, 255)
    NEST_BROWN = (175, 120, 65, 255)
    NEST_DARK = (125, 75, 35, 255)
    EGG_BLUE = (155, 225, 245, 255)
    LEAF_GREEN = (90, 205, 90, 255)

    # Branch
    draw.line([(cx - 260, cy + 160), (cx + 260, cy + 160)], fill=BRANCH, width=36)
    draw.ellipse([cx - 220, cy + 120, cx - 160, cy + 160], fill=LEAF_GREEN)
    draw.ellipse([cx + 170, cy + 120, cx + 230, cy + 160], fill=LEAF_GREEN)

    # 3 Pastel Blue Eggs inside nest
    for ex, ey, er in [(-70, -30, 48), (0, -50, 52), (70, -30, 48)]:
        draw.ellipse([cx + ex - er * 0.7, cy + ey - er, cx + ex + er * 0.7, cy + ey + er], fill=EGG_BLUE)
        draw.ellipse([cx + ex - 10, cy + ey - 20, cx + ex + 10, cy + ey], fill=WHITE)

    # Woven Twig Nest Bowl
    draw.chord([cx - 200, cy - 50, cx + 200, cy + 180], start=0, end=180, fill=NEST_BROWN)
    # Twig Texture Crosshatches
    for tx in range(int(cx - 170), int(cx + 180), 30):
        draw.line([(tx, cy + 20), (tx + 25, cy + 130)], fill=NEST_DARK, width=8)
        draw.line([(tx + 25, cy + 20), (tx, cy + 130)], fill=NEST_DARK, width=8)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 13. NET (Butterfly net with wooden handle & billowing mesh)
# ==============================================================================
def render_net():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    WOOD = (205, 145, 85, 255)
    RIM_STEEL = (175, 195, 215, 255)
    MESH = (235, 245, 255, 180)

    # Long Wooden Pole
    draw.line([(cx - 240, cy + 260), (cx + 80, cy - 60)], fill=WOOD, width=32)

    # Circular Wire Hoop
    draw.ellipse([cx - 20, cy - 240, cx + 240, cy + 20], outline=RIM_STEEL, width=28)

    # Billowing Net Bag
    net_pts = (
        bezier_points((cx + 10, cy - 200), (cx - 160, cy - 140), (cx - 140, cy + 80), (cx + 90, cy + 10)) +
        bezier_points((cx + 90, cy + 10), (cx + 200, cy - 10), (cx + 210, cy - 180), (cx + 10, cy - 200))
    )
    draw.polygon(net_pts, fill=MESH)

    # Mesh Grid Lines
    for my in range(-160, 20, 40):
        draw.arc([cx - 100, cy + my - 40, cx + 180, cy + my + 40], 120, 300, fill=(200, 220, 240, 200), width=6)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 14. NUT (Glossy golden acorn with textured wooden cap & oak leaves)
# ==============================================================================
def render_nut():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    ACORN_BODY = (215, 145, 75, 255)
    ACORN_CAP = (155, 95, 45, 255)
    LEAF_GREEN = (90, 195, 90, 255)

    # Green Oak Leaves behind
    draw.ellipse([cx - 220, cy - 180, cx - 60, cy - 60], fill=LEAF_GREEN)
    draw.ellipse([cx + 60, cy - 180, cx + 220, cy - 60], fill=LEAF_GREEN)

    # Acorn Body (tapering to tip at bottom)
    body_pts = (
        bezier_points((cx - 160, cy - 10), (cx - 170, cy + 150), (cx - 50, cy + 240), (cx, cy + 260)) +
        bezier_points((cx, cy + 260), (cx + 50, cy + 240), (cx + 170, cy + 150), (cx + 160, cy - 10))
    )
    draw.polygon(body_pts, fill=ACORN_BODY)
    # Gloss highlight
    draw.ellipse([cx - 120, cy + 30, cx - 60, cy + 130], fill=WHITE)

    # Textured Cap
    draw.ellipse([cx - 180, cy - 140, cx + 180, cy + 20], fill=ACORN_CAP)
    # Crosshatch on cap
    for cx_c in range(int(cx - 150), int(cx + 160), 30):
        draw.line([(cx_c, cy - 120), (cx_c + 20, cy)], fill=(120, 70, 30, 255), width=6)
        draw.line([(cx_c + 20, cy - 120), (cx_c, cy)], fill=(120, 70, 30, 255), width=6)

    # Stem at top
    draw.rounded_rectangle([cx - 16, cy - 200, cx + 16, cy - 120], radius=10, fill=ACORN_CAP)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 15. PIÑA (Juicy tropical pineapple with crosshatch diamond rind & spiky crown)
# ==============================================================================
def render_pina():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 40

    PINA_GOLD = (255, 195, 30, 255)
    PINA_ORANGE = (235, 135, 20, 255)
    LEAF_GREEN = (75, 185, 75, 255)
    LEAF_DARK = (45, 145, 55, 255)

    # Spiky Green Crown of Leaves at Top
    for lx, ly, lang in [
        (cx, cy - 220, 0), (cx - 70, cy - 200, -25), (cx + 70, cy - 200, 25),
        (cx - 120, cy - 160, -45), (cx + 120, cy - 160, 45)
    ]:
        draw.polygon([(cx - 30, cy - 110), (cx + 30, cy - 110), (lx, ly)], fill=LEAF_GREEN)
        draw.line([(cx, cy - 110), (lx, ly)], fill=LEAF_DARK, width=8)

    # Oval Pineapple Body
    draw.ellipse([cx - 180, cy - 110, cx + 180, cy + 250], fill=PINA_GOLD)

    # Diamond Crosshatch Pattern
    for dx in range(int(cx - 150), int(cx + 160), 45):
        draw.line([(dx - 60, cy - 90), (dx + 60, cy + 230)], fill=PINA_ORANGE, width=8)
        draw.line([(dx + 60, cy - 90), (dx - 60, cy + 230)], fill=PINA_ORANGE, width=8)

    # Cute Lily-style Face on Pineapple!
    for eye_x in [cx - 60, cx + 60]:
        draw.ellipse([eye_x - 22, cy + 40, eye_x + 22, cy + 84], fill=OUTLINE)
        draw.ellipse([eye_x - 18, cy + 45, eye_x - 6, cy + 60], fill=WHITE)
    draw.chord([cx - 24, cy + 90, cx + 24, cy + 125], start=0, end=180, fill=OUTLINE)
    draw.ellipse([cx - 85, cy + 70, cx - 60, cy + 90], fill=ROSY_CHEEK)
    draw.ellipse([cx + 60, cy + 70, cx + 85, cy + 90], fill=ROSY_CHEEK)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 16. QUILT (Patchwork quilt blanket with colorful squares & cross-stitch)
# ==============================================================================
def render_quilt():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    CREAM = (255, 248, 230, 255)
    PINK = (245, 115, 165, 255)
    BLUE = (85, 175, 245, 255)
    GREEN = (85, 205, 120, 255)
    GOLD = (255, 210, 45, 255)

    # Quilt Outer Border
    draw.rounded_rectangle([cx - 240, cy - 240, cx + 240, cy + 240], radius=40, fill=CREAM)

    # 4 Patchwork Quadrants
    draw.rounded_rectangle([cx - 210, cy - 210, cx - 15, cy - 15], radius=20, fill=PINK)
    draw.rounded_rectangle([cx + 15, cy - 210, cx + 210, cy - 15], radius=20, fill=BLUE)
    draw.rounded_rectangle([cx - 210, cy + 15, cx - 15, cy + 210], radius=20, fill=GREEN)
    draw.rounded_rectangle([cx + 15, cy + 15, cx + 210, cy + 210], radius=20, fill=GOLD)

    # Little Center Star on each patch
    for px, py in [(cx - 110, cy - 110), (cx + 110, cy - 110), (cx - 110, cy + 110), (cx + 110, cy + 110)]:
        draw.ellipse([px - 20, py - 20, px + 20, py + 20], fill=WHITE)

    # Cross-stitch lines along seams
    for x in range(int(cx - 200), int(cx + 210), 30):
        draw.line([(x - 8, cy - 8), (x + 8, cy + 8)], fill=OUTLINE, width=6)
        draw.line([(x - 8, cy + 8), (x + 8, cy - 8)], fill=OUTLINE, width=6)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 17. ROCKET (Retro space rocket with red fins, porthole & exhaust fire)
# ==============================================================================
def render_rocket():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 - 20

    ROCKET_WHITE = (248, 250, 252, 255)
    RED = (235, 55, 65, 255)
    FIRE_YELLOW = (255, 215, 30, 255)

    # Billowing Exhaust Fire at Bottom
    draw.polygon([(cx - 70, cy + 160), (cx + 70, cy + 160), (cx, cy + 340)], fill=FIRE_YELLOW)
    draw.polygon([(cx - 40, cy + 160), (cx + 40, cy + 160), (cx, cy + 270)], fill=(255, 120, 20, 255))

    # Red Side Fins
    draw.polygon([(cx - 120, cy + 50), (cx - 220, cy + 180), (cx - 120, cy + 160)], fill=RED)
    draw.polygon([(cx + 120, cy + 50), (cx + 220, cy + 180), (cx + 120, cy + 160)], fill=RED)

    # Bullet Rocket Body
    draw.polygon([(cx - 130, cy + 160), (cx + 130, cy + 160), (cx + 130, cy - 40), (cx - 130, cy - 40)], fill=ROCKET_WHITE)
    # Nose Cone
    draw.polygon([(cx - 130, cy - 40), (cx + 130, cy - 40), (cx, cy - 240)], fill=RED)

    # Blue Porthole Window
    draw.ellipse([cx - 65, cy - 10, cx + 65, cy + 120], fill=RED)
    draw.ellipse([cx - 50, cy + 5, cx + 50, cy + 105], fill=(70, 185, 245, 255))
    draw.ellipse([cx - 30, cy + 20, cx, cy + 50], fill=WHITE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 18. SIX (Glossy 3D number 6 with six colorful count stars)
# ==============================================================================
def render_six():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0 - 20, H / 2.0

    BLUE = (55, 155, 245, 255)
    BLUE_DARK = (30, 110, 200, 255)

    # 3D Depth Shadow
    # Big Number 6 Loop
    draw.ellipse([cx - 140, cy - 40, cx + 180, cy + 240], fill=BLUE_DARK)
    # Top hook
    draw.arc([cx - 110, cy - 240, cx + 170, cy + 40], 100, 270, fill=BLUE_DARK, width=95)

    # Front Face
    draw.ellipse([cx - 150, cy - 50, cx + 170, cy + 230], fill=BLUE)
    draw.ellipse([cx - 60, cy + 35, cx + 80, cy + 155], fill=(0, 0, 0, 0)) # Hollow center
    draw.arc([cx - 120, cy - 250, cx + 160, cy + 30], 100, 270, fill=BLUE, width=90)

    # Specular Gleam
    draw.arc([cx - 140, cy - 230, cx + 140, cy + 20], 120, 200, fill=WHITE, width=20)

    # 6 Colorful Count Stars orbiting
    for idx, (sx, sy, col) in enumerate([
        (cx - 210, cy - 140, (255, 215, 30, 255)), (cx + 200, cy - 160, (245, 65, 85, 255)),
        (cx + 240, cy - 40, (85, 205, 95, 255)), (cx + 220, cy + 100, (255, 140, 30, 255)),
        (cx - 190, cy + 80, (170, 110, 230, 255)), (cx + 170, cy + 220, (65, 215, 245, 255))
    ]):
        draw.ellipse([sx - 24, sy - 24, sx + 24, sy + 24], fill=col)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 19. TOP (Spinning wooden top with lacquer rings & metal tip)
# ==============================================================================
def render_top():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 - 10

    TOP_RED = (245, 65, 75, 255)
    TOP_YELLOW = (255, 215, 30, 255)
    TOP_BLUE = (55, 155, 245, 255)

    # Handle at top
    draw.rounded_rectangle([cx - 20, cy - 260, cx + 20, cy - 140], radius=12, fill=(185, 125, 75, 255))
    draw.ellipse([cx - 40, cy - 280, cx + 40, cy - 230], fill=TOP_RED)

    # Top Dome (Red)
    draw.ellipse([cx - 190, cy - 160, cx + 190, cy + 40], fill=TOP_RED)

    # Middle Band (Yellow)
    draw.rounded_rectangle([cx - 210, cy - 40, cx + 210, cy + 40], radius=24, fill=TOP_YELLOW)

    # Lower Inverted Cone (Blue)
    draw.polygon([(cx - 190, cy + 10), (cx + 190, cy + 10), (cx, cy + 240)], fill=TOP_BLUE)

    # Metal Tip
    draw.polygon([(cx - 18, cy + 230), (cx + 18, cy + 230), (cx, cy + 290)], fill=(200, 210, 220, 255))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 20. TREE (Fluffy storybook oak tree with lush green canopy & textured trunk)
# ==============================================================================
def render_tree():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 10

    TRUNK_BROWN = (155, 95, 50, 255)
    LEAF_GREEN = (85, 195, 80, 255)
    LEAF_DARK = (55, 155, 55, 255)

    # Trunk
    draw.polygon([(cx - 60, cy + 50), (cx + 60, cy + 50), (cx + 100, cy + 300), (cx - 100, cy + 300)], fill=TRUNK_BROWN)

    # Fluffy Cloud Canopy (Overlapping rounded lobes)
    for lx, ly, lr in [
        (cx - 140, cy - 30, 110), (cx + 140, cy - 30, 110),
        (cx - 100, cy - 130, 115), (cx + 100, cy - 130, 115),
        (cx, cy - 180, 130), (cx, cy - 60, 140)
    ]:
        draw.ellipse([lx - lr, ly - lr, lx + lr, ly + lr], fill=LEAF_GREEN)
        draw.arc([lx - lr + 15, ly - lr + 15, lx + lr - 15, ly + lr - 15], 30, 150, fill=LEAF_DARK, width=12)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 21. VASE (Glazed ceramic floral vase holding blooming flowers)
# ==============================================================================
def render_vase():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    VASE_BLUE = (65, 165, 245, 255)
    VASE_DARK = (35, 125, 205, 255)
    FLOWER_PINK = (255, 125, 165, 255)
    FLOWER_YELLOW = (255, 215, 30, 255)

    # Blooming Flowers at Top
    for fx, fy in [(cx - 80, cy - 160), (cx, cy - 220), (cx + 80, cy - 160)]:
        # Stem
        draw.line([(cx, cy - 40), (fx, fy)], fill=(85, 185, 80, 255), width=12)
        # Petals
        for deg in range(0, 360, 60):
            rad = math.radians(deg)
            draw.ellipse([fx + 40*math.cos(rad) - 24, fy + 40*math.sin(rad) - 24, fx + 40*math.cos(rad) + 24, fy + 40*math.sin(rad) + 24], fill=FLOWER_PINK)
        # Flower Center
        draw.ellipse([fx - 24, fy - 24, fx + 24, fy + 24], fill=FLOWER_YELLOW)

    # Ceramic Vase Body
    vase_pts = (
        bezier_points((cx - 60, cy - 50), (cx - 180, cy + 30), (cx - 180, cy + 180), (cx - 90, cy + 240)) +
        bezier_points((cx - 90, cy + 240), (cx + 90, cy + 240), (cx + 180, cy + 180), (cx + 180, cy + 30)) +
        bezier_points((cx + 180, cy + 30), (cx + 60, cy - 50), (cx - 60, cy - 50), (cx - 60, cy - 50))
    )
    draw.polygon(vase_pts, fill=VASE_BLUE)
    # Shading on right
    draw.ellipse([cx - 40, cy + 20, cx + 180, cy + 240], fill=VASE_DARK)

    # Vase Neck Lip
    draw.ellipse([cx - 80, cy - 65, cx + 80, cy - 35], fill=VASE_BLUE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 22. VEST (Cozy outdoor explorer fleece vest with zipper & collar)
# ==============================================================================
def render_vest():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 10

    VEST_GREEN = (65, 175, 120, 255)
    VEST_DARK = (45, 135, 90, 255)
    ZIPPER_GOLD = (255, 205, 35, 255)

    # Vest Body (Sleeveless)
    vest_pts = [
        (cx - 110, cy - 200), (cx - 190, cy - 140), (cx - 150, cy - 20),
        (cx - 170, cy + 220), (cx + 170, cy + 220), (cx + 150, cy - 20),
        (cx + 190, cy - 140), (cx + 110, cy - 200), (cx, cy - 150)
    ]
    draw.polygon(vest_pts, fill=VEST_GREEN)

    # Collar Flaps
    draw.polygon([(cx - 110, cy - 200), (cx - 20, cy - 100), (cx - 20, cy - 170)], fill=VEST_DARK)
    draw.polygon([(cx + 110, cy - 200), (cx + 20, cy - 100), (cx + 20, cy - 170)], fill=VEST_DARK)

    # Center Zipper Line
    draw.line([(cx, cy - 100), (cx, cy + 220)], fill=ZIPPER_GOLD, width=14)
    # Zipper Pull Tab
    draw.ellipse([cx - 15, cy - 70, cx + 15, cy - 30], fill=ZIPPER_GOLD)

    # Front Pockets
    draw.rounded_rectangle([cx - 150, cy + 60, cx - 40, cy + 160], radius=16, fill=VEST_DARK)
    draw.rounded_rectangle([cx + 40, cy + 60, cx + 150, cy + 160], radius=16, fill=VEST_DARK)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 23. WING (Feathered bird / angel wing with soft layered plumage)
# ==============================================================================
def render_wing():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    WING_WHITE = (248, 250, 255, 255)
    FEATHER_BLUE = (210, 230, 245, 255)

    # Base wing contour
    wing_pts = (
        bezier_points((cx - 220, cy + 180), (cx - 180, cy - 120), (cx, cy - 220), (cx + 240, cy - 240)) +
        bezier_points((cx + 240, cy - 240), (cx + 180, cy - 100), (cx + 120, cy + 40), (cx - 10, cy + 180)) +
        bezier_points((cx - 10, cy + 180), (cx - 120, cy + 240), (cx - 180, cy + 240), (cx - 220, cy + 180))
    )
    draw.polygon(wing_pts, fill=WING_WHITE)

    # Layered Individual Primary Feathers
    for i in range(5):
        fy = cy - 180 + i * 70
        fx = cx + 220 - i * 50
        draw.ellipse([fx - 70, fy - 35, fx + 30, fy + 35], fill=FEATHER_BLUE)
        draw.ellipse([fx - 65, fy - 30, fx + 25, fy + 30], fill=WING_WHITE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 24. YARN (Soft ball of pastel wool yarn with smooth knitting needles)
# ==============================================================================
def render_yarn():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    YARN_PINK = (255, 140, 175, 255)
    YARN_DARK = (225, 105, 145, 255)
    NEEDLE_WOOD = (225, 175, 115, 255)

    # Knitting Needles Crossed Behind
    draw.line([(cx - 240, cy - 220), (cx + 160, cy + 180)], fill=NEEDLE_WOOD, width=18)
    draw.ellipse([cx - 260, cy - 240, cx - 225, cy - 205], fill=(185, 125, 65, 255))
    draw.line([(cx + 240, cy - 220), (cx - 160, cy + 180)], fill=NEEDLE_WOOD, width=18)
    draw.ellipse([cx + 225, cy - 240, cx + 260, cy - 205], fill=(185, 125, 65, 255))

    # Main Yarn Sphere
    draw.ellipse([cx - 180, cy - 150, cx + 180, cy + 210], fill=YARN_PINK)

    # Wool Strands Wrapping around sphere
    for r in [60, 110, 150]:
        draw_thick_arc(draw, [cx - r, cy + 30 - r, cx + r, cy + 30 + r], 20, 160, YARN_DARK, 10)
        draw_thick_arc(draw, [cx - r - 20, cy + 10 - r, cx + r + 20, cy + 10 + r], 200, 340, YARN_DARK, 10)

    # Loose Yarn String unwinding on floor
    strand = bezier_points((cx + 140, cy + 140), (cx + 240, cy + 180), (cx + 210, cy + 260), (cx + 280, cy + 280), 20)
    for i in range(len(strand) - 1):
        draw.line([strand[i], strand[i+1]], fill=YARN_PINK, width=14)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 25. YOYO (Glossy two-tone wooden yoyo sleeping on its twisted string)
# ==============================================================================
def render_yoyo():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    YOYO_RED = (245, 60, 75, 255)
    YOYO_YELLOW = (255, 215, 30, 255)
    STRING = (245, 245, 250, 255)

    # Twisted String coming from top finger loop
    draw.ellipse([cx - 30, cy - 280, cx + 30, cy - 220], outline=STRING, width=10)
    draw.line([(cx, cy - 220), (cx, cy - 40)], fill=STRING, width=12)

    # Two Outer Discs of Yoyo
    draw.ellipse([cx - 190, cy - 70, cx + 190, cy + 230], fill=YOYO_RED)
    draw.ellipse([cx - 140, cy - 20, cx + 140, cy + 180], fill=YOYO_YELLOW)

    # Side Profile Rim & Axle
    draw.ellipse([cx - 60, cy + 30, cx + 60, cy + 130], fill=YOYO_RED)
    draw.ellipse([cx - 25, cy + 55, cx + 25, cy + 105], fill=WHITE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 26. ZIP (Chunky golden jacket zipper slider pulling interlocking teeth)
# ==============================================================================
def render_zip():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    FABRIC = (55, 145, 225, 255)
    GOLD = (255, 205, 35, 255)
    GOLD_DARK = (215, 155, 15, 255)

    # Fabric Track Halves
    # Separating at top
    draw.polygon([(0, 0), (cx - 140, 0), (cx - 40, cy - 40), (0, cy - 40)], fill=FABRIC)
    draw.polygon([(W, 0), (cx + 140, 0), (cx + 40, cy - 40), (W, cy - 40)], fill=FABRIC)
    # Closed at bottom
    draw.rectangle([0, cy + 40, cx - 20, H], fill=FABRIC)
    draw.rectangle([cx + 20, cy + 40, W, H], fill=FABRIC)

    # Interlocking Golden Teeth
    for ty in range(int(cy + 40), int(H - 40), 35):
        draw.rounded_rectangle([cx - 35, ty, cx - 5, ty + 20], radius=6, fill=GOLD)
        draw.rounded_rectangle([cx + 5, ty + 12, cx + 35, ty + 32], radius=6, fill=GOLD)

    # Golden Slider Body
    slider_pts = [(cx - 70, cy - 70), (cx + 70, cy - 70), (cx + 45, cy + 50), (cx - 45, cy + 50)]
    draw.polygon(slider_pts, fill=GOLD)
    draw.polygon([(cx - 50, cy - 50), (cx + 50, cy - 50), (cx + 35, cy + 35), (cx - 35, cy + 35)], fill=GOLD_DARK)

    # Pull Tab Hanging Down
    draw.rounded_rectangle([cx - 28, cy + 40, cx + 28, cy + 180], radius=16, fill=GOLD)
    draw.ellipse([cx - 14, cy + 120, cx + 14, cy + 160], fill=OUTLINE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 27. ENVELOPE (Sealed white letter with red heart wax seal)
# ==============================================================================
def render_envelope():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    ENV_WHITE = (248, 250, 252, 255)
    ENV_SHADOW = (220, 230, 240, 255)
    SEAL_RED = (235, 55, 65, 255)

    # Rectangular Envelope Body
    draw.rounded_rectangle([cx - 230, cy - 140, cx + 230, cy + 180], radius=28, fill=ENV_WHITE)

    # Fold lines
    draw.line([(cx - 230, cy + 180), (cx, cy + 30), (cx + 230, cy + 180)], fill=ENV_SHADOW, width=10)

    # Top Triangular Flap
    draw.polygon([(cx - 230, cy - 140), (cx + 230, cy - 140), (cx, cy + 30)], fill=ENV_SHADOW)
    draw.line([(cx - 230, cy - 140), (cx, cy + 30), (cx + 230, cy - 140)], fill=OUTLINE, width=8)

    # Red Heart Wax Seal in Center
    sx, sy = cx, cy + 30
    draw.ellipse([sx - 40, sy - 40, sx + 40, sy + 40], fill=SEAL_RED)
    # Heart
    draw.ellipse([sx - 22, sy - 18, sx, sy + 4], fill=WHITE)
    draw.ellipse([sx, sy - 18, sx + 22, sy + 4], fill=WHITE)
    draw.polygon([(sx - 20, sy - 2), (sx + 20, sy - 2), (sx, sy + 22)], fill=WHITE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 28. KEY (Antique golden key with clover bow & notched teeth)
# ==============================================================================
def render_key():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    KEY_GOLD = (255, 205, 35, 255)
    KEY_DARK = (215, 155, 15, 255)

    # Shaft
    draw.line([(cx - 80, cy), (cx + 230, cy)], fill=KEY_GOLD, width=32)

    # Clover Bow on Left
    draw.ellipse([cx - 240, cy - 90, cx - 110, cy + 40], fill=KEY_GOLD)
    draw.ellipse([cx - 240, cy - 40, cx - 110, cy + 90], fill=KEY_GOLD)
    draw.ellipse([cx - 180, cy - 60, cx - 50, cy + 70], fill=KEY_GOLD)
    # Hollow center
    draw.ellipse([cx - 190, cy - 40, cx - 110, cy + 40], fill=(0, 0, 0, 0))

    # Notched Bit Teeth on Right
    draw.polygon([(cx + 170, cy), (cx + 230, cy), (cx + 230, cy + 90), (cx + 170, cy + 90)], fill=KEY_GOLD)
    draw.rectangle([cx + 190, cy + 40, cx + 210, cy + 90], fill=(0, 0, 0, 0))

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 29. RING (Sparkling golden ring with brilliant blue cut diamond & gleams)
# ==============================================================================
def render_ring():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 40

    GOLD = (255, 210, 35, 255)
    DIAMOND = (95, 205, 255, 255)
    GEM_WHITE = (245, 250, 255, 255)

    # Gold Band
    draw.ellipse([cx - 180, cy - 60, cx + 180, cy + 220], outline=GOLD, width=42)

    # Gemstone Setting Prongs
    draw.polygon([(cx - 70, cy - 60), (cx + 70, cy - 60), (cx + 50, cy - 20), (cx - 50, cy - 20)], fill=GOLD)

    # Brilliant Cut Diamond
    gem_pts = [
        (cx - 100, cy - 140), (cx + 100, cy - 140),
        (cx + 60, cy - 60), (cx, cy - 20), (cx - 60, cy - 60)
    ]
    draw.polygon(gem_pts, fill=DIAMOND)
    # Gemstone facets
    draw.polygon([(cx - 50, cy - 140), (cx + 50, cy - 140), (cx + 30, cy - 90), (cx - 30, cy - 90)], fill=GEM_WHITE)

    # Radiant Star Sparkles
    for sx, sy in [(cx - 130, cy - 180), (cx + 140, cy - 170)]:
        draw.ellipse([sx - 16, sy - 16, sx + 16, sy + 16], fill=WHITE)
        draw.line([(sx - 24, sy), (sx + 24, sy)], fill=WHITE, width=6)
        draw.line([(sx, sy - 24), (sx, sy + 24)], fill=WHITE, width=6)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 30. STAR (Five-pointed golden star with smiling pediatric eyes & rosy blush)
# ==============================================================================
def render_star():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    GOLD_BASE = (255, 205, 30, 255)
    GOLD_LIGHT = (255, 235, 120, 255)

    # 5-pointed star polygon
    star_pts = []
    num_pts = 5
    for i in range(num_pts * 2):
        r = 270 if i % 2 == 0 else 125
        ang = math.radians(i * (360 / (num_pts * 2)) - 90)
        star_pts.append((cx + r * math.cos(ang), cy + r * math.sin(ang)))
    draw.polygon(star_pts, fill=GOLD_BASE)

    # Cute Lily Eyes
    for eye_x in [cx - 50, cx + 50]:
        draw.ellipse([eye_x - 18, cy - 20, eye_x + 18, cy + 18], fill=OUTLINE)
        draw.ellipse([eye_x - 14, cy - 16, eye_x - 4, cy - 4], fill=WHITE)
        draw.ellipse([eye_x + 4, cy + 2, eye_x + 10, cy + 8], fill=WHITE)

    # Sweet smile & rosy blush
    draw.chord([cx - 24, cy + 20, cx + 24, cy + 55], start=0, end=180, fill=OUTLINE)
    draw.ellipse([cx - 75, cy + 5, cx - 45, cy + 25], fill=ROSY_CHEEK)
    draw.ellipse([cx + 45, cy + 5, cx + 75, cy + 25], fill=ROSY_CHEEK)

    return apply_pediatric_outline(img, stroke_w=7)


# BATCH DEPLOYMENT
DEPLOY_TARGETS = {
    "picture_axe.png": render_axe,
    "picture_bano.png": render_bano,
    "picture_drum.png": render_drum,
    "picture_egg.png": render_egg,
    "picture_gift.png": render_gift,
    "picture_igloo.png": render_igloo,
    "picture_ink.png": render_ink,
    "picture_jet.png": render_jet,
    "picture_jug.png": render_jug,
    "word_jug.png": render_jug,
    "picture_leaf.png": render_leaf,
    "picture_map.png": render_map,
    "picture_nest.png": render_nest,
    "word_nest.png": render_nest,
    "picture_net.png": render_net,
    "picture_nut.png": render_nut,
    "picture_pina.png": render_pina,
    "picture_quilt.png": render_quilt,
    "picture_rocket.png": render_rocket,
    "picture_six.png": render_six,
    "picture_top.png": render_top,
    "picture_tree.png": render_tree,
    "picture_vase.png": render_vase,
    "picture_vest.png": render_vest,
    "picture_wing.png": render_wing,
    "picture_yarn.png": render_yarn,
    "picture_yoyo.png": render_yoyo,
    "word_yoyo.png": render_yoyo,
    "picture_zip.png": render_zip,
    "picture_envelope.png": render_envelope,
    "picture_key.png": render_key,
    "picture_ring.png": render_ring,
    "picture_star.png": render_star,
}

def main():
    target_dir = "app/src/main/assets/images/pictures"
    print(f"Generating {len(DEPLOY_TARGETS)} Pediatric Objects & Props...")
    for filename, renderer in DEPLOY_TARGETS.items():
        print(f"  Rendering {filename}...")
        img_1024 = renderer()
        img_512 = img_1024.resize((SIZE, SIZE), resample=Image.Resampling.LANCZOS)
        path = os.path.join(target_dir, filename)
        img_512.save(path, "PNG", optimize=True)
        print(f"  -> Saved {path} (512x512 RGBA)")

if __name__ == "__main__":
    main()
