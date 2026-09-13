"""
PlayIT Pediatric Map Props Generator
Reworks all 14 map props in app/src/main/assets/images/backgrounds/ to commercial-grade
Duolingo ABC pediatric standards with 2x Lanczos supersampling (1024x1024 -> 512x512),
layered 3-tone shading, storybook charm, and #2D373E continuous outlines.
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
WOOD_DARK = (101, 67, 33, 255)
WOOD_MID = (141, 91, 40, 255)
WOOD_LIGHT = (184, 128, 68, 255)
GOLD_BASE = (255, 198, 15, 255)
GOLD_LIGHT = (255, 228, 90, 255)
GREEN_DARK = (46, 125, 50, 255)
GREEN_MID = (88, 204, 2, 255)
GREEN_LIGHT = (140, 230, 60, 255)
SKY_BLUE = (41, 182, 246, 255)
SKY_LIGHT = (129, 212, 250, 255)
CORAL_RED = (255, 82, 82, 255)
CORAL_LIGHT = (255, 138, 128, 255)
PURPLE = (171, 71, 188, 255)

def bezier_curve(p0, p1, p2, p3, steps=30):
    pts = []
    for i in range(steps):
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

def draw_thick_arc(draw, bbox, start_deg, end_deg, color, width):
    draw.arc(bbox, start=start_deg, end=end_deg, fill=color, width=int(width))

# ==============================================================================
# 1. PALM TREE (Tropical Bohol/Palawan palm with curved trunk & lush layered fronds)
# ==============================================================================
def render_palm_tree():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # Curved Trunk
    trunk_pts_l = bezier_curve((cx - 35, H - 90), (cx - 20, cy + 180), (cx + 5, cy + 60), (cx - 15, cy - 40))
    trunk_pts_r = bezier_curve((cx + 35, H - 90), (cx + 50, cy + 180), (cx + 75, cy + 60), (cx + 35, cy - 40))
    trunk_poly = trunk_pts_l + trunk_pts_r[::-1]
    
    draw.polygon(trunk_poly, fill=WOOD_MID)
    # Trunk bark ridges - contained inside trunk
    for i in range(1, 8):
        t_y = H - 100 - i * 55
        t_x = cx + (i - 2) * 6
        draw.arc([t_x - 30, t_y - 12, t_x + 35, t_y + 16], start=10, end=170, fill=WOOD_DARK, width=10)

    # Lush Palm Fronds gracefully arching outward and drooping
    frond_origin = (cx + 10, cy - 50)
    fronds = [
        # (angle_deg, length, arch_h, color)
        (-165, 340, 110, GREEN_DARK),
        (-140, 390, 140, GREEN_MID),
        (-105, 410, 150, GREEN_LIGHT),
        (-75,  410, 150, GREEN_LIGHT),
        (-40,  390, 140, GREEN_MID),
        (-15,  340, 110, GREEN_DARK),
    ]
    
    for angle, flen, arch_h, col in fronds:
        rad = math.radians(angle)
        tip_x = frond_origin[0] + flen * math.cos(rad)
        tip_y = frond_origin[1] + flen * math.sin(rad) + 50 # droop
        
        # Arch peak
        peak_x = frond_origin[0] + (flen * 0.5) * math.cos(rad)
        peak_y = frond_origin[1] + (flen * 0.5) * math.sin(rad) - arch_h
        
        # Upper edge
        upper_pts = bezier_curve(frond_origin, (peak_x, peak_y), (tip_x, tip_y - 20), (tip_x, tip_y), 20)
        # Lower edge (broader at middle)
        lower_pts = bezier_curve((tip_x, tip_y), (peak_x + 20, peak_y + 60), (frond_origin[0] + 15, frond_origin[1] + 20), frond_origin, 20)
        
        draw.polygon(upper_pts + lower_pts, fill=col)

    # Coconuts in center cluster
    coconuts = [(cx - 5, cy - 40), (cx + 25, cy - 35), (cx + 10, cy - 20)]
    for c_pos in coconuts:
        draw.ellipse([c_pos[0] - 26, c_pos[1] - 26, c_pos[0] + 26, c_pos[1] + 26], fill=WOOD_DARK)
        draw.ellipse([c_pos[0] - 22, c_pos[1] - 22, c_pos[0] + 18, c_pos[1] + 18], fill=(120, 80, 40, 255))
        draw.ellipse([c_pos[0] - 12, c_pos[1] - 12, c_pos[0] - 4, c_pos[1] - 4], fill=WOOD_LIGHT)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 2. NIPA HUT (Traditional Filipino Bahay Kubo on stilts with thatched roof)
# ==============================================================================
def render_nipa_hut():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # 4 Bamboo Stilts
    stilts_x = [cx - 240, cx - 100, cx + 100, cx + 240]
    for sx in stilts_x:
        draw.rounded_rectangle([sx - 18, cy + 110, sx + 18, H - 90], radius=10, fill=WOOD_DARK)
        draw.rounded_rectangle([sx - 14, cy + 110, sx + 14, H - 90], radius=8, fill=WOOD_MID)
        # Bamboo nodes
        for ny in [cy + 190, cy + 280, cy + 360]:
            draw.line([(sx - 16, ny), (sx + 16, ny)], fill=WOOD_LIGHT, width=6)

    # Bamboo Floor Platform
    draw.rounded_rectangle([cx - 290, cy + 90, cx + 290, cy + 140], radius=16, fill=WOOD_DARK)
    draw.rounded_rectangle([cx - 286, cy + 94, cx + 286, cy + 136], radius=14, fill=WOOD_LIGHT)

    # Woven Bamboo Sawali Walls
    draw.rounded_rectangle([cx - 230, cy - 80, cx + 230, cy + 100], radius=14, fill=(225, 185, 110, 255))
    # Woven pattern lines
    for i in range(-5, 6):
        draw.line([(cx + i * 40 - 20, cy - 75), (cx + i * 40 - 20, cy + 95)], fill=(200, 160, 85, 255), width=5)

    # Cheerful Little Window with Awning Prop
    draw.rounded_rectangle([cx - 190, cy - 40, cx - 70, cy + 50], radius=12, fill=SKY_BLUE)
    draw.line([(cx - 130, cy - 40), (cx - 130, cy + 50)], fill=WHITE, width=6)
    draw.line([(cx - 190, cy + 5), (cx - 70, cy + 5)], fill=WHITE, width=6)
    # Awning flap
    draw.polygon([(cx - 200, cy - 45), (cx - 60, cy - 45), (cx - 40, cy - 75), (cx - 220, cy - 75)], fill=(190, 140, 70, 255))

    # Cute Little Doorway
    draw.rounded_rectangle([cx + 20, cy - 30, cx + 160, cy + 95], radius=16, fill=WOOD_DARK)
    draw.rounded_rectangle([cx + 25, cy - 25, cx + 155, cy + 95], radius=12, fill=WOOD_MID)
    # Gold doorknob
    draw.ellipse([cx + 40, cy + 30, cx + 56, cy + 46], fill=GOLD_BASE)

    # Stepladder / Bamboo Steps
    ladder_x = cx + 80
    draw.rounded_rectangle([ladder_x - 35, cy + 110, ladder_x - 22, cy + 280], radius=6, fill=WOOD_DARK)
    draw.rounded_rectangle([ladder_x + 22, cy + 110, ladder_x + 35, cy + 280], radius=6, fill=WOOD_DARK)
    for ry in [cy + 145, cy + 185, cy + 225, cy + 265]:
        draw.rounded_rectangle([ladder_x - 30, ry - 6, ladder_x + 30, ry + 6], radius=4, fill=WOOD_LIGHT)

    # Grand Steep Thatched Nipa Palm Roof
    roof_pts = [
        (cx, cy - 420),              # Peak
        (cx + 340, cy - 60),          # Right eave
        (cx + 300, cy - 40),          # Right under-eave
        (cx - 300, cy - 40),          # Left under-eave
        (cx - 340, cy - 60),          # Left eave
    ]
    draw.polygon(roof_pts, fill=(185, 125, 45, 255))
    
    # Roof layered thatch fringe
    for layer_y, width_spread, col in [
        (cy - 330, 130, (215, 155, 60, 255)),
        (cy - 240, 210, (225, 170, 70, 255)),
        (cy - 140, 280, (205, 145, 55, 255)),
        (cy - 50,  335, (195, 135, 45, 255)),
    ]:
        fringe_pts = [(cx - width_spread, layer_y), (cx + width_spread, layer_y)]
        # Zig-zag bottom fringe
        num_teeth = 12
        dx = (width_spread * 2) / num_teeth
        for i in range(num_teeth, -1, -1):
            px = (cx - width_spread) + i * dx
            py = layer_y + 35 if (i % 2 == 1) else layer_y + 10
            fringe_pts.append((px, py))
        draw.polygon(fringe_pts, fill=col)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 3. FLOWER (Tropical Hibiscus / Gumamela with layered coral petals)
# ==============================================================================
def render_flower():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # Two broad green leaves at base
    for lx, ly in [(cx - 240, cy + 180), (cx + 240, cy + 180)]:
        leaf_pts = (
            bezier_curve((cx, cy + 80), (lx, ly - 80), (lx, ly + 80), (cx, cy + 220)) +
            bezier_curve((cx, cy + 220), (cx + (lx-cx)*0.4, cy + 180), (cx, cy + 80), (cx, cy + 80))
        )
        draw.polygon(leaf_pts, fill=GREEN_MID)
        draw.line([(cx, cy + 150), (lx * 0.8 + cx * 0.2, ly)], fill=GREEN_DARK, width=12)

    # 5 Rounded Overlapping Tropical Petals
    num_petals = 5
    petal_r = 280
    for i in range(num_petals):
        angle = math.radians(i * 72 - 90)
        px = cx + petal_r * math.cos(angle)
        py = cy + petal_r * math.sin(angle)
        
        # Draw broad round heart-shaped petal
        p_perp = angle + math.pi / 2
        w_offset = 150
        p_l = (px + w_offset * math.cos(p_perp), py + w_offset * math.sin(p_perp))
        p_r = (px - w_offset * math.cos(p_perp), py - w_offset * math.sin(p_perp))
        
        petal_pts = (
            bezier_curve((cx, cy), (p_l[0]*0.7 + cx*0.3, p_l[1]*0.7 + cy*0.3), (p_l[0], p_l[1]), (px, py)) +
            bezier_curve((px, py), (p_r[0], p_r[1]), (p_r[0]*0.7 + cx*0.3, p_r[1]*0.7 + cy*0.3), (cx, cy))
        )
        draw.polygon(petal_pts, fill=CORAL_RED)
        
        # Inner petal gradient/highlight
        inner_pts = [(cx + (p[0] - cx) * 0.75, cy + (p[1] - cy) * 0.75) for p in petal_pts]
        draw.polygon(inner_pts, fill=CORAL_LIGHT)

    # Deep Crimson Star Throat
    draw.ellipse([cx - 90, cy - 90, cx + 90, cy + 90], fill=(210, 30, 60, 255))

    # Curved Golden Stamen Column
    stamen_pts = bezier_curve((cx, cy), (cx + 30, cy - 100), (cx + 80, cy - 220), (cx + 130, cy - 310), 30)
    for i in range(len(stamen_pts) - 1):
        draw.line([stamen_pts[i], stamen_pts[i+1]], fill=GOLD_BASE, width=22)
    
    # Golden pollen anthers on tip
    tip_x, tip_y = stamen_pts[-1]
    draw.ellipse([tip_x - 30, tip_y - 30, tip_x + 30, tip_y + 30], fill=GOLD_LIGHT)
    for a_dx, a_dy in [(-25, -20), (25, -15), (-15, 25), (20, 20), (0, -35)]:
        draw.ellipse([tip_x + a_dx - 12, tip_y + a_dy - 12, tip_x + a_dx + 12, tip_y + a_dy + 12], fill=GOLD_BASE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 4. PENCIL TOWER (Whimsical giant school pencil observation tower)
# ==============================================================================
def render_pencil_tower():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # Pencil Body: Hexagonal faceted shaft
    body_l, body_r = cx - 120, cx + 120
    body_b = H - 120
    body_t = cy - 140
    
    # Facets (3 visible)
    draw.rectangle([body_l, body_t, cx - 40, body_b], fill=(235, 175, 10, 255))
    draw.rectangle([cx - 40, body_t, cx + 40, body_b], fill=GOLD_BASE)
    draw.rectangle([cx + 40, body_t, body_r, body_b], fill=GOLD_LIGHT)

    # Silver Metal Ferrule near bottom
    ferrule_t = body_b - 120
    draw.rectangle([body_l - 6, ferrule_t, body_r + 6, body_b - 50], fill=(200, 205, 215, 255))
    draw.line([(body_l - 6, ferrule_t + 25), (body_r + 6, ferrule_t + 25)], fill=(160, 165, 175, 255), width=6)
    draw.line([(body_l - 6, ferrule_t + 45), (body_r + 6, ferrule_t + 45)], fill=WHITE, width=4)

    # Pink Eraser Base
    draw.rounded_rectangle([body_l - 4, body_b - 50, body_r + 4, body_b + 20], radius=18, fill=(255, 140, 165, 255))
    draw.rounded_rectangle([body_l, body_b - 45, cx, body_b + 15], radius=14, fill=(240, 115, 145, 255))

    # Sharpened Wood Cone at Top
    wood_tip_y = cy - 360
    draw.polygon([(body_l, body_t), (body_r, body_t), (cx, wood_tip_y)], fill=(240, 210, 160, 255))
    # Scalloped wood edges
    for i in range(3):
        fx_l = body_l + i * 80
        fx_r = fx_l + 80
        draw.chord([fx_l, body_t - 25, fx_r, body_t + 25], start=0, end=180, fill=(215, 180, 130, 255))

    # Graphite Lead Tip
    lead_base_y = wood_tip_y + 80
    lead_pts = [(cx - 32, lead_base_y), (cx + 32, lead_base_y), (cx, wood_tip_y)]
    draw.polygon(lead_pts, fill=OUTLINE)
    draw.polygon([(cx - 15, lead_base_y), (cx + 15, lead_base_y), (cx, wood_tip_y)], fill=(80, 95, 105, 255))

    # Storybook Tower Elements: Arched Observation Windows
    for wy in [cy - 50, cy + 80]:
        draw.rounded_rectangle([cx - 28, wy - 35, cx + 28, wy + 35], radius=24, fill=OUTLINE)
        draw.rounded_rectangle([cx - 24, wy - 31, cx + 24, wy + 31], radius=20, fill=SKY_BLUE)
        draw.line([(cx, wy - 31), (cx, wy + 31)], fill=WHITE, width=5)
        draw.line([(cx - 24, wy), (cx + 24, wy)], fill=WHITE, width=5)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 5. CRAYON BRIDGE (Playful curved bridge built of vibrant colored crayons)
# ==============================================================================
def render_crayon_bridge():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    CRAYON_COLORS = [
        (255, 82, 82, 255),    # Red
        (255, 160, 0, 255),    # Orange
        (255, 220, 0, 255),    # Yellow
        (76, 175, 80, 255),    # Green
        (33, 150, 243, 255),   # Blue
        (156, 39, 176, 255),   # Purple
    ]

    # Arching bridge curve
    num_crayons = 6
    bridge_w = 700
    start_x = cx - bridge_w / 2
    for i in range(num_crayons):
        col = CRAYON_COLORS[i]
        frac = (i + 0.5) / float(num_crayons)
        c_x = start_x + frac * bridge_w
        # Parabolic bridge height
        arc_h = math.sin(frac * math.pi) * 140
        c_y = cy + 120 - arc_h
        
        # Crayon post (vertical cylinder)
        cw, ch = 90, 240
        draw.rounded_rectangle([c_x - cw/2, c_y - ch/2, c_x + cw/2, c_y + ch/2], radius=14, fill=col)
        # Crayon wrapper band
        draw.rectangle([c_x - cw/2, c_y - 20, c_x + cw/2, c_y + 20], fill=OUTLINE)
        draw.rectangle([c_x - cw/2, c_y - 12, c_x + cw/2, c_y + 12], fill=WHITE)
        # Pointed tip at bottom or top
        tip_pts = [(c_x - cw/2, c_y - ch/2), (c_x + cw/2, c_y - ch/2), (c_x, c_y - ch/2 - 50)]
        draw.polygon(tip_pts, fill=col)

    # Curved Handrail spanning the bridge
    handrail_pts_top = []
    for step in range(50):
        t = step / 49.0
        x = cx - 380 + t * 760
        y = cy - 20 - math.sin(t * math.pi) * 145
        handrail_pts_top.append((x, y))
    
    for i in range(len(handrail_pts_top) - 1):
        draw.line([handrail_pts_top[i], handrail_pts_top[i+1]], fill=WOOD_MID, width=28)
        draw.line([handrail_pts_top[i], handrail_pts_top[i+1]], fill=WOOD_LIGHT, width=16)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 6. BOOK STACK (Stack of 3 chunky storybooks with colorful covers & ribbons)
# ==============================================================================
def render_book_stack():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    BOOKS = [
        # (width, height, offset_x, y_pos, cover_color, page_color)
        (620, 110,  0,   cy + 180, (255, 82, 82, 255),   (255, 250, 240, 255)), # Red base
        (540, 105, -20,  cy + 60,  (41, 182, 246, 255),  (255, 250, 240, 255)), # Blue middle
        (460, 100,  15,  cy - 55,  (88, 204, 2, 255),    (255, 250, 240, 255)), # Green top
    ]

    for bw, bh, ox, by, c_col, p_col in BOOKS:
        bx = cx + ox
        # Book pages block (recessed inside cover)
        draw.rounded_rectangle([bx - bw/2 + 25, by - bh/2 + 10, bx + bw/2 - 15, by + bh/2 - 10], radius=10, fill=p_col)
        # Page ridges
        for py in range(int(by - bh/2 + 22), int(by + bh/2 - 15), 18):
            draw.line([(bx - bw/2 + 35, py), (bx + bw/2 - 25, py)], fill=(220, 215, 200, 255), width=3)

        # Book Spine (curved left side)
        spine_w = 70
        draw.rounded_rectangle([bx - bw/2 - 15, by - bh/2, bx - bw/2 + spine_w, by + bh/2], radius=16, fill=c_col)
        # Spine gold bands
        for sy in [by - bh/3, by + bh/3]:
            draw.line([(bx - bw/2 - 10, sy), (bx - bw/2 + spine_w - 5, sy)], fill=GOLD_BASE, width=7)

        # Top & Bottom Cover flaps
        draw.rounded_rectangle([bx - bw/2, by - bh/2, bx + bw/2, by - bh/2 + 18], radius=8, fill=c_col)
        draw.rounded_rectangle([bx - bw/2, by + bh/2 - 18, bx + bw/2, by + bh/2], radius=8, fill=c_col)

    # Cheerful Golden Bookmark Ribbon hanging from top book
    ribbon_pts = [(cx + 60, cy - 55), (cx + 80, cy + 30), (cx + 60, cy + 20), (cx + 40, cy + 30)]
    draw.polygon(ribbon_pts, fill=GOLD_BASE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 7. GLOBE (Pediatric school globe on teal stand with continents & oceans)
# ==============================================================================
def render_globe():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 - 20

    # Curved Stand Base
    base_y = H - 110
    draw.ellipse([cx - 180, base_y - 45, cx + 180, base_y + 45], fill=(38, 166, 154, 255))
    draw.ellipse([cx - 160, base_y - 35, cx + 160, base_y + 30], fill=(77, 208, 198, 255))
    # Vertical support stem
    draw.rounded_rectangle([cx - 24, cy + 180, cx + 24, base_y], radius=12, fill=(38, 166, 154, 255))

    # C-shaped Meridian Ring Arc
    ring_bbox = [cx - 270, cy - 270, cx + 270, cy + 270]
    draw.arc(ring_bbox, start=60, end=300, fill=(38, 166, 154, 255), width=48)
    draw.arc(ring_bbox, start=65, end=295, fill=(77, 208, 198, 255), width=28)

    # Globe Ocean Sphere
    globe_r = 210
    draw.ellipse([cx - globe_r, cy - globe_r, cx + globe_r, cy + globe_r], fill=SKY_BLUE)

    # Storybook Green Continents
    # Continent 1 (North-west)
    c1 = bezier_curve((cx - 150, cy - 110), (cx - 80, cy - 170), (cx, cy - 130), (cx - 40, cy - 50)) + \
         bezier_curve((cx - 40, cy - 50), (cx - 110, cy - 10), (cx - 180, cy - 30), (cx - 150, cy - 110))
    draw.polygon(c1, fill=GREEN_MID)

    # Continent 2 (South-west)
    c2 = bezier_curve((cx - 110, cy + 30), (cx - 30, cy + 20), (cx - 60, cy + 140), (cx - 120, cy + 120)) + \
         bezier_curve((cx - 120, cy + 120), (cx - 140, cy + 80), (cx - 110, cy + 30), (cx - 110, cy + 30))
    draw.polygon(c2, fill=GREEN_MID)

    # Continent 3 (East)
    c3 = bezier_curve((cx + 30, cy - 130), (cx + 140, cy - 110), (cx + 160, cy + 40), (cx + 80, cy + 70)) + \
         bezier_curve((cx + 80, cy + 70), (cx + 20, cy - 20), (cx + 30, cy - 130), (cx + 30, cy - 130))
    draw.polygon(c3, fill=GREEN_MID)

    # Glossy Specular Crescent on sphere
    draw.arc([cx - globe_r + 25, cy - globe_r + 25, cx + globe_r - 25, cy + globe_r - 25], start=180, end=270, fill=WHITE, width=22)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 8. BACKPACK (Cute preschool backpack with pocket, zippers, and straps)
# ==============================================================================
def render_backpack():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # Top Carry Handle
    draw.arc([cx - 90, cy - 340, cx + 90, cy - 180], start=180, end=360, fill=(30, 136, 229, 255), width=32)

    # Main Backpack Body
    draw.rounded_rectangle([cx - 240, cy - 220, cx + 240, cy + 260], radius=80, fill=SKY_BLUE)
    # Side pockets
    for sx in [-255, 215]:
        draw.rounded_rectangle([cx + sx, cy + 50, cx + sx + 40, cy + 220], radius=20, fill=(30, 136, 229, 255))

    # Front Large Pocket
    draw.rounded_rectangle([cx - 180, cy + 10, cx + 180, cy + 240], radius=45, fill=CORAL_RED)
    # Zipper track
    draw.arc([cx - 170, cy - 25, cx + 170, cy + 75], start=180, end=360, fill=OUTLINE, width=12)
    draw.arc([cx - 170, cy - 25, cx + 170, cy + 75], start=180, end=360, fill=GOLD_BASE, width=6)
    # Gold zipper pull
    draw.rounded_rectangle([cx - 12, cy + 20, cx + 12, cy + 65], radius=6, fill=GOLD_BASE)

    # Main Top Zipper
    draw.arc([cx - 210, cy - 200, cx + 210, cy - 100], start=180, end=360, fill=OUTLINE, width=12)
    draw.arc([cx - 210, cy - 200, cx + 210, cy - 100], start=180, end=360, fill=GOLD_BASE, width=6)

    # Cute PlayIT Patch / Badge on upper pocket
    draw.ellipse([cx - 50, cy - 130, cx + 50, cy - 30], fill=WHITE)
    draw.ellipse([cx - 40, cy - 120, cx + 40, cy - 40], fill=GOLD_BASE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 9. PAPER AIRPLANE (Folded white paper airplane banking with cute wind trail)
# ==============================================================================
def render_paper_airplane():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # Whimsical swirling dotted wind trail
    trail_pts = bezier_curve((cx - 380, cy + 280), (cx - 280, cy + 80), (cx - 120, cy + 220), (cx - 50, cy + 40), 30)
    for i, p in enumerate(trail_pts):
        if i % 3 == 0:
            draw.ellipse([p[0] - 8, p[1] - 8, p[0] + 8, p[1] + 8], fill=SKY_LIGHT)

    # Folded Airplane Facets (Facing upper-right)
    nose = (cx + 280, cy - 200)
    tail_l = (cx - 280, cy + 20)
    tail_c = (cx - 160, cy + 130)
    tail_r = (cx - 60, cy + 180)

    # Under-wing shadow facet
    draw.polygon([nose, tail_c, tail_r], fill=(180, 205, 230, 255))
    # Left main wing (light sky blue)
    draw.polygon([nose, tail_l, tail_c], fill=(225, 238, 250, 255))
    # Right top wing (crisp white)
    draw.polygon([nose, tail_c, (cx + 40, cy - 80)], fill=WHITE)
    # Center fold line
    draw.line([nose, tail_c], fill=(150, 180, 210, 255), width=8)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 10. RULER RAMP (Wooden yellow school ruler tilted as playground slide)
# ==============================================================================
def render_ruler_ramp():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # Tilted Ruler Angle (-25 degrees)
    ruler_w, ruler_h = 750, 160
    rad = math.radians(-25)
    cos_a, sin_a = math.cos(rad), math.sin(rad)
    
    def r_rot(dx, dy):
        return (cx + dx * cos_a - dy * sin_a, cy + dx * sin_a + dy * cos_a)

    r_pts = [r_rot(-ruler_w/2, -ruler_h/2), r_rot(ruler_w/2, -ruler_h/2),
             r_rot(ruler_w/2, ruler_h/2), r_rot(-ruler_w/2, ruler_h/2)]
    
    draw.polygon(r_pts, fill=GOLD_BASE)
    
    # Inset top face for 3D bevel
    inset_r = [r_rot(-ruler_w/2 + 10, -ruler_h/2 + 10), r_rot(ruler_w/2 - 10, -ruler_h/2 + 10),
               r_rot(ruler_w/2 - 10, 0), r_rot(-ruler_w/2 + 10, 0)]
    draw.polygon(inset_r, fill=GOLD_LIGHT)

    # Ruler measurement tick marks
    num_ticks = 24
    dx_step = (ruler_w - 60) / num_ticks
    for i in range(num_ticks + 1):
        tx = -ruler_w/2 + 30 + i * dx_step
        tick_len = 50 if (i % 4 == 0) else 30
        p_top = r_rot(tx, -ruler_h/2 + 10)
        p_bot = r_rot(tx, -ruler_h/2 + 10 + tick_len)
        draw.line([p_top, p_bot], fill=OUTLINE, width=6 if (i % 4 == 0) else 4)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 11. ERASER SHRUB & MAP PROP BUSH (Cute topiary shrub / lush green bush)
# ==============================================================================
def render_bush(is_eraser=False):
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    if is_eraser:
        # Beveled Pink School Eraser Base
        draw.polygon([(cx - 180, cy + 90), (cx + 180, cy + 90), (cx + 140, cy + 220), (cx - 220, cy + 220)], fill=(255, 140, 165, 255))
        draw.polygon([(cx - 180, cy + 90), (cx - 220, cy + 220), (cx - 250, cy + 180), (cx - 210, cy + 50)], fill=(235, 115, 145, 255))
        # Blue cardboard wrapper band
        draw.polygon([(cx - 80, cy + 70), (cx + 80, cy + 70), (cx + 60, cy + 220), (cx - 100, cy + 220)], fill=SKY_BLUE)

    # Cloud-like Topiary Shrub Foliage (Multiple overlapping circular lobes)
    lobes = [
        (cx - 150, cy + 40,  130, GREEN_DARK),
        (cx + 150, cy + 40,  130, GREEN_DARK),
        (cx - 90,  cy - 70,  140, GREEN_MID),
        (cx + 90,  cy - 70,  140, GREEN_MID),
        (cx,       cy - 120, 150, GREEN_LIGHT),
        (cx,       cy - 20,  160, GREEN_MID),
    ]
    for lx, ly, lr, col in lobes:
        draw.ellipse([lx - lr, ly - lr, lx + lr, ly + lr], fill=col)

    # Highlights on foliage
    draw.ellipse([cx - 80, cy - 180, cx + 40, cy - 100], fill=GREEN_LIGHT)
    draw.ellipse([cx - 180, cy - 60, cx - 100, cy], fill=GREEN_LIGHT)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 12. MAGNIFYING GLASS (Storybook lens with glossy reflection & wooden handle)
# ==============================================================================
def render_magnifying_glass():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0 - 50, H / 2.0 - 50

    # Thick Round Wooden Handle pointing down-right
    h_start = (cx + 120, cy + 120)
    h_end = (cx + 340, cy + 340)
    draw.line([h_start, h_end], fill=WOOD_DARK, width=64)
    draw.line([h_start, h_end], fill=WOOD_MID, width=44)
    draw.ellipse([h_end[0] - 32, h_end[1] - 32, h_end[0] + 32, h_end[1] + 32], fill=WOOD_LIGHT)

    # Gold Circular Rim Frame
    rim_r = 210
    draw.ellipse([cx - rim_r, cy - rim_r, cx + rim_r, cy + rim_r], fill=GOLD_BASE)
    draw.ellipse([cx - rim_r + 20, cy - rim_r + 20, cx + rim_r - 20, cy + rim_r - 20], fill=GOLD_LIGHT)

    # Translucent Cyan Glass Lens
    lens_r = rim_r - 35
    draw.ellipse([cx - lens_r, cy - lens_r, cx + lens_r, cy + lens_r], fill=(200, 240, 255, 230))
    
    # Curved Specular Gloss Highlight
    draw.arc([cx - lens_r + 25, cy - lens_r + 25, cx + lens_r - 25, cy + lens_r - 25], start=190, end=290, fill=WHITE, width=28)
    draw.ellipse([cx - lens_r + 30, cy - 20, cx - lens_r + 56, cy + 6], fill=WHITE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 13. PAINT PALETTE (Artist wooden palette with vibrant colorful blobs & brush)
# ==============================================================================
def render_paint_palette():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # Kidney Bean Wooden Palette shape
    palette_pts = (
        bezier_curve((cx - 280, cy), (cx - 280, cy - 240), (cx + 140, cy - 280), (cx + 260, cy - 120)) +
        bezier_curve((cx + 260, cy - 120), (cx + 340, cy + 40), (cx + 260, cy + 220), (cx + 100, cy + 250)) +
        bezier_curve((cx + 100, cy + 250), (cx - 40, cy + 260), (cx - 10, cy + 80), (cx - 100, cy + 80)) +
        bezier_curve((cx - 100, cy + 80), (cx - 180, cy + 80), (cx - 280, cy + 180), (cx - 280, cy))
    )
    draw.polygon(palette_pts, fill=(235, 185, 125, 255))
    
    # Inset for wood grain edge
    inset_p = [(cx + (p[0] - cx) * 0.94, cy + (p[1] - cy) * 0.94) for p in palette_pts]
    draw.polygon(inset_p, fill=(245, 205, 145, 255))

    # Thumb Hole
    draw.ellipse([cx + 150, cy + 100, cx + 220, cy + 170], fill=OUTLINE)
    draw.ellipse([cx + 155, cy + 105, cx + 215, cy + 165], fill=(0, 0, 0, 0)) # transparent hole

    # Colorful Paint Blobs
    BLOBS = [
        (cx - 190, cy - 120, 48, CORAL_RED),
        (cx - 90,  cy - 180, 48, (255, 152, 0, 255)),
        (cx + 30,  cy - 190, 48, GOLD_BASE),
        (cx + 140, cy - 150, 48, GREEN_MID),
        (cx + 200, cy - 40,  48, SKY_BLUE),
        (cx - 200, cy + 40,  48, PURPLE),
    ]
    for bx, by, br, col in BLOBS:
        draw.ellipse([bx - br, by - br, bx + br, by + br], fill=col)
        # Specular glint
        draw.ellipse([bx - br + 12, by - br + 12, bx - br + 26, by - br + 26], fill=WHITE)

    return apply_pediatric_outline(img, stroke_w=7)

# ==============================================================================
# 14. ROCK (Smooth rounded storybook pebble with highlights)
# ==============================================================================
def render_rock():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 40

    # Smooth rounded stone
    rock_pts = (
        bezier_curve((cx - 260, cy + 120), (cx - 280, cy - 80), (cx - 140, cy - 180), (cx, cy - 190)) +
        bezier_curve((cx, cy - 190), (cx + 160, cy - 190), (cx + 280, cy - 60), (cx + 260, cy + 120)) +
        bezier_curve((cx + 260, cy + 120), (cx + 180, cy + 190), (cx - 180, cy + 190), (cx - 260, cy + 120))
    )
    draw.polygon(rock_pts, fill=(130, 145, 155, 255))
    
    # Inset light facet
    inset_pts = [(cx + (p[0] - cx) * 0.92, cy - 10 + (p[1] - (cy - 10)) * 0.92) for p in rock_pts]
    draw.polygon(inset_pts, fill=(175, 190, 200, 255))

    # Top highlight
    draw.arc([cx - 180, cy - 160, cx + 120, cy], start=190, end=300, fill=WHITE, width=22)

    return apply_pediatric_outline(img, stroke_w=7)

def main():
    bg_dir = "/mnt/c/Users/Zendrix/Desktop/thesis/playIT-v2-workspace/app/src/main/assets/images/backgrounds"
    brain_dir = "/home/zendrix/.gemini/antigravity-cli/brain/af2db79b-86c4-4925-a8b9-dd27044dffec"
    os.makedirs(bg_dir, exist_ok=True)

    items = [
        ("mapprop_palm_tree.png", render_palm_tree()),
        ("map_prop_tree_small.png", render_palm_tree()),
        ("mapprop_nipa_hut.png", render_nipa_hut()),
        ("mapprop_flower.png", render_flower()),
        ("mapprop_pencil_tower.png", render_pencil_tower()),
        ("mapprop_crayon_bridge.png", render_crayon_bridge()),
        ("mapprop_book_stack.png", render_book_stack()),
        ("mapprop_globe.png", render_globe()),
        ("mapprop_backpack.png", render_backpack()),
        ("mapprop_paper_airplane.png", render_paper_airplane()),
        ("mapprop_ruler_ramp.png", render_ruler_ramp()),
        ("mapprop_eraser_shrub.png", render_bush(is_eraser=True)),
        ("map_prop_bush.png", render_bush(is_eraser=False)),
        ("mapprop_magnifying_glass.png", render_magnifying_glass()),
        ("mapprop_paint_palette.png", render_paint_palette()),
        ("map_prop_rock.png", render_rock()),
    ]

    print("[*] Generating Duolingo ABC Pediatric Map Props Suite...")
    for filename, raw_img in items:
        final_img = raw_img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)
        out_path = os.path.join(bg_dir, filename)
        final_img.save(out_path, "PNG", optimize=True)
        prev_path = os.path.join(brain_dir, f"preview_{filename}")
        final_img.save(prev_path, "PNG", optimize=True)
        print(f"  -> Saved {out_path}")

    print("\nAll Map Prop Assets successfully generated & deployed!")

if __name__ == "__main__":
    main()
