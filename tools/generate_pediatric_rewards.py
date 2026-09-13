"""
PlayIT Pediatric Rewards Suite Generator
Reworks all 4 reward assets to commercial-grade Duolingo ABC pediatric standards:
1. reward_star.png - 3D faceted golden star with cute expressive eyes, rosy cheeks, and sparkles
2. reward_heart.png - Juicy, plump 3D gel heart with glossy specular reflections
3. reward_streak.png - Lively dancing flame character with warm golden core and cheerful face
4. reward_confetti_burst.png - Dynamic festive burst with swirling ribbons, party dots, and star glimmers
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
ROSY_CHEEK = (255, 130, 145, 220)
TONGUE = (244, 85, 105, 255)

def draw_thick_line(draw, start, end, color, width):
    draw.line([start, end], fill=color, width=int(width), joint="curve")

def draw_thick_arc(draw, bbox, start_deg, end_deg, color, width):
    draw.arc(bbox, start=start_deg, end=end_deg, fill=color, width=int(width))

def apply_pediatric_outline(image: Image.Image, stroke_w: int = 8) -> Image.Image:
    alpha = image.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_w * 2 + 1))
    stroke_layer = Image.new("RGBA", image.size, (0, 0, 0, 0))
    stroke_base = Image.new("RGBA", image.size, OUTLINE)
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, image)

def draw_glossy_eyes(draw, lx, rx, ly, pupil_r=36):
    """Pediatric glossy cartoon eyes with double circular catchlights."""
    for cx in [lx, rx]:
        draw.ellipse([cx - pupil_r, ly - pupil_r, cx + pupil_r, ly + pupil_r], fill=OUTLINE)
        # Catchlight 1 (top-left)
        s1 = pupil_r * 0.40
        draw.ellipse([cx - pupil_r * 0.35 - s1, ly - pupil_r * 0.35 - s1,
                      cx - pupil_r * 0.35 + s1, ly - pupil_r * 0.35 + s1], fill=WHITE)
        # Catchlight 2 (bottom-right)
        s2 = pupil_r * 0.20
        draw.ellipse([cx + pupil_r * 0.35 - s2, ly + pupil_r * 0.35 - s2,
                      cx + pupil_r * 0.35 + s2, ly + pupil_r * 0.35 + s2], fill=WHITE)

def draw_sparkle(draw, cx, cy, r=32, color=(255, 225, 60, 255)):
    """Draws 4-point sparkle star."""
    pts = [
        (cx, cy - r), (cx + r*0.25, cy - r*0.25),
        (cx + r, cy), (cx + r*0.25, cy + r*0.25),
        (cx, cy + r), (cx - r*0.25, cy + r*0.25),
        (cx - r, cy), (cx - r*0.25, cy - r*0.25)
    ]
    draw.polygon(pts, fill=OUTLINE)
    inner_pts = [
        (cx, cy - r + 4), (cx + r*0.22, cy - r*0.22),
        (cx + r - 4, cy), (cx + r*0.22, cy + r*0.22),
        (cx, cy + r - 4), (cx - r*0.22, cy + r*0.22),
        (cx - r + 4, cy), (cx - r*0.22, cy - r*0.22)
    ]
    draw.polygon(inner_pts, fill=color)

# ==============================================================================
# 1. REWARD STAR
# ==============================================================================
def render_reward_star():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    GOLD_BASE = (255, 198, 15, 255)     # #FFC60F Rich vibrant gold
    GOLD_SHADOW = (235, 155, 10, 255)   # #EB9B0A
    GOLD_LIGHT = (255, 228, 90, 255)    # #FFE45A
    
    outer_r = 380
    inner_r = 180
    outer_pts = []
    inner_pts = []
    
    for i in range(5):
        angle_out = math.radians(-90 + i * 72)
        outer_pts.append((cx + outer_r * math.cos(angle_out), cy + outer_r * math.sin(angle_out)))
        angle_in = math.radians(-90 + i * 72 + 36)
        inner_pts.append((cx + inner_r * math.cos(angle_in), cy + inner_r * math.sin(angle_in)))

    star_poly = []
    for i in range(5):
        star_poly.append(outer_pts[i])
        star_poly.append(inner_pts[i])

    draw.polygon(star_poly, fill=OUTLINE)
    
    inset_star = []
    for p in star_poly:
        dx = p[0] - cx
        dy = p[1] - cy
        inset_star.append((cx + dx * 0.965, cy + dy * 0.965))
    draw.polygon(inset_star, fill=GOLD_BASE)

    # 3D Facets from center to vertices
    for i in [0, 4]:
        draw.polygon([(cx, cy), outer_pts[i], inner_pts[i]], fill=GOLD_LIGHT)
    for i in [1, 2, 3]:
        draw.polygon([(cx, cy), outer_pts[i], inner_pts[i]], fill=GOLD_SHADOW)

    draw.ellipse([cx - 130, cy - 120, cx + 130, cy + 140], fill=GOLD_BASE)

    draw_glossy_eyes(draw, cx - 72, cx + 72, cy - 15, pupil_r=34)

    draw.ellipse([cx - 125 - 28, cy + 30 - 18, cx - 125 + 28, cy + 30 + 18], fill=ROSY_CHEEK)
    draw.ellipse([cx + 125 - 28, cy + 30 - 18, cx + 125 + 28, cy + 30 + 18], fill=ROSY_CHEEK)

    draw_thick_arc(draw, [cx - 40, cy + 8, cx + 40, cy + 56], 20, 160, OUTLINE, 9)

    draw_sparkle(draw, cx + 290, cy - 250, r=42)
    draw_sparkle(draw, cx - 290, cy + 220, r=32)

    return apply_pediatric_outline(img, stroke_w=6)

# ==============================================================================
# 2. REWARD HEART
# ==============================================================================
def render_reward_heart():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    CORAL_RUBY = (255, 75, 95, 255)    # #FF4B5F Vibrant Duolingo Heart

    steps = 180
    pts = []
    scale_factor = 21.0
    for i in range(steps):
        t = i * (2 * math.pi / steps)
        x = 16 * (math.sin(t)**3)
        y = -(13 * math.cos(t) - 5 * math.cos(2*t) - 2 * math.cos(3*t) - math.cos(4*t))
        pts.append((cx + x * scale_factor, cy + y * scale_factor))

    draw.polygon(pts, fill=OUTLINE)

    inset_pts = []
    for p in pts:
        dx = p[0] - cx
        dy = p[1] - (cy - 30)
        inset_pts.append((cx + dx * 0.965, (cy - 30) + dy * 0.965))
    draw.polygon(inset_pts, fill=CORAL_RUBY)

    # Glossy Specular Gel Shine
    h_box = [cx - 260, cy - 230, cx - 70, cy - 70]
    draw.arc(h_box, start=170, end=300, fill=WHITE, width=28)
    draw.ellipse([cx - 260, cy - 150, cx - 232, cy - 122], fill=WHITE)
    draw.ellipse([cx - 105, cy - 230, cx - 77, cy - 202], fill=WHITE)
    
    draw.ellipse([cx - 200, cy - 50, cx - 180, cy - 10], fill=WHITE)

    return apply_pediatric_outline(img, stroke_w=6)

# ==============================================================================
# 3. REWARD STREAK FLAME (Natural Teardrop Flame with Dancing Face)
# ==============================================================================
def bezier_curve(p0, p1, p2, p3, steps=25):
    pts = []
    for i in range(steps):
        t = i / float(steps)
        u = 1.0 - t
        x = (u**3)*p0[0] + 3*(u**2)*t*p1[0] + 3*u*(t**2)*p2[0] + (t**3)*p3[0]
        y = (u**3)*p0[1] + 3*(u**2)*t*p1[1] + 3*u*(t**2)*p2[1] + (t**3)*p3[1]
        pts.append((x, y))
    return pts

def render_reward_streak():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 10

    FLAME_ORANGE = (255, 120, 20, 255)   # Vibrant orange
    FLAME_GOLD = (255, 215, 25, 255)     # Golden yellow
    FLAME_CREAM = (255, 252, 205, 255)

    # Teardrop flame polygon with smooth Bezier perimeter
    outer_pts = (
        bezier_curve((cx + 15, cy - 330), (cx - 80, cy - 180), (cx - 230, cy - 20), (cx - 230, cy + 100)) +
        bezier_curve((cx - 230, cy + 100), (cx - 230, cy + 240), (cx - 130, cy + 270), (cx, cy + 270)) +
        bezier_curve((cx, cy + 270), (cx + 130, cy + 270), (cx + 230, cy + 240), (cx + 230, cy + 100)) +
        bezier_curve((cx + 230, cy + 100), (cx + 230, cy - 20), (cx + 90, cy - 180), (cx + 15, cy - 330))
    )

    # Base polygon fill
    draw.polygon(outer_pts, fill=OUTLINE)
    inset_pts = []
    for p in outer_pts:
        dx = p[0] - cx
        dy = p[1] - (cy + 20)
        inset_pts.append((cx + dx * 0.96, (cy + 20) + dy * 0.96))
    draw.polygon(inset_pts, fill=FLAME_ORANGE)

    # Golden inner flame
    inner_pts = []
    for p in inset_pts:
        dx = p[0] - cx
        dy = p[1] - (cy + 70)
        inner_pts.append((cx + dx * 0.65, (cy + 70) + dy * 0.65))
    draw.polygon(inner_pts, fill=FLAME_GOLD)

    # Cream inner core
    cream_pts = []
    for p in inner_pts:
        dx = p[0] - cx
        dy = p[1] - (cy + 95)
        cream_pts.append((cx + dx * 0.48, (cy + 95) + dy * 0.48))
    draw.polygon(cream_pts, fill=FLAME_CREAM)

    # Cheerful Eyes
    face_y = cy + 65
    draw_glossy_eyes(draw, cx - 60, cx + 60, face_y, pupil_r=30)

    # Rosy Cheeks
    draw.ellipse([cx - 105 - 22, face_y + 28 - 14, cx - 105 + 22, face_y + 28 + 14], fill=ROSY_CHEEK)
    draw.ellipse([cx + 105 - 22, face_y + 28 - 14, cx + 105 + 22, face_y + 28 + 14], fill=ROSY_CHEEK)

    # Cheerful Smile
    draw.chord([cx - 30, face_y + 28, cx + 30, face_y + 70], start=0, end=180, fill=OUTLINE)
    draw.chord([cx - 22, face_y + 42, cx + 22, face_y + 68], start=0, end=180, fill=TONGUE)

    return apply_pediatric_outline(img, stroke_w=6)

# ==============================================================================
# 4. REWARD CONFETTI BURST
# ==============================================================================
def render_reward_confetti_burst():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    COLORS = [
        (41, 182, 246, 255),   # Cyan
        (255, 82, 82, 255),    # Coral
        (102, 187, 106, 255),  # Emerald
        (171, 71, 188, 255),   # Purple
        (255, 213, 79, 255),   # Gold
        (255, 138, 101, 255),  # Apricot
    ]

    # Central Golden Star
    star_r = 160
    inner_star_r = 75
    s_pts = []
    for i in range(5):
        a1 = math.radians(-90 + i * 72)
        s_pts.append((cx + star_r * math.cos(a1), cy + star_r * math.sin(a1)))
        a2 = math.radians(-90 + i * 72 + 36)
        s_pts.append((cx + inner_star_r * math.cos(a2), cy + inner_star_r * math.sin(a2)))

    draw.polygon(s_pts, fill=OUTLINE)
    inset_s = []
    for p in s_pts:
        dx = p[0] - cx
        dy = p[1] - cy
        inset_s.append((cx + dx * 0.94, cy + dy * 0.94))
    draw.polygon(inset_s, fill=(255, 215, 20, 255))
    
    # Radial Confetti Streamers
    num_streamers = 8
    for i in range(num_streamers):
        angle = math.radians(i * (360 / num_streamers) + 22)
        col = COLORS[i % len(COLORS)]
        dist = 290
        px = cx + dist * math.cos(angle)
        py = cy + dist * math.sin(angle)
        
        rw, rh = 48, 26
        rad = angle + 0.5
        cos_a = math.cos(rad)
        sin_a = math.sin(rad)
        def r_rot(dx, dy):
            return (px + dx * cos_a - dy * sin_a, py + dx * sin_a + dy * cos_a)
        
        ribbon_pts = [r_rot(-rw/2, -rh/2), r_rot(rw/2, -rh/2), r_rot(rw/2, rh/2), r_rot(-rw/2, rh/2)]
        draw.polygon(ribbon_pts, fill=OUTLINE)
        inset_ribbon = [r_rot(-rw/2+3, -rh/2+3), r_rot(rw/2-3, -rh/2+3), r_rot(rw/2-3, rh/2-3), r_rot(-rw/2+3, rh/2-3)]
        draw.polygon(inset_ribbon, fill=col)

    # Colorful Floating Party Circles
    num_dots = 12
    for i in range(num_dots):
        angle = math.radians(i * (360 / num_dots) + 10)
        col = COLORS[(i + 2) % len(COLORS)]
        dist = 390 + (i % 3) * 35
        dx = cx + dist * math.cos(angle)
        dy = cy + dist * math.sin(angle)
        dr = 22 if (i % 2 == 0) else 16
        draw.ellipse([dx - dr - 4, dy - dr - 4, dx + dr + 4, dy + dr + 4], fill=OUTLINE)
        draw.ellipse([dx - dr, dy - dr, dx + dr, dy + dr], fill=col)

    # Floating Sparkle Stars
    for i in range(4):
        angle = math.radians(45 + i * 90)
        sx = cx + 210 * math.cos(angle)
        sy = cy + 210 * math.sin(angle)
        draw_sparkle(draw, sx, sy, r=26)

    return apply_pediatric_outline(img, stroke_w=6)

def main():
    rewards_dir = "/mnt/c/Users/Zendrix/Desktop/thesis/playIT-v2-workspace/app/src/main/assets/images/rewards"
    brain_dir = "/home/zendrix/.gemini/antigravity-cli/brain/af2db79b-86c4-4925-a8b9-dd27044dffec"
    os.makedirs(rewards_dir, exist_ok=True)

    items = [
        ("reward_star.png", render_reward_star()),
        ("reward_heart.png", render_reward_heart()),
        ("reward_streak.png", render_reward_streak()),
        ("reward_confetti_burst.png", render_reward_confetti_burst()),
    ]

    print("[*] Generating Duolingo ABC Pediatric Rewards Suite...")
    for filename, raw_img in items:
        final_img = raw_img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)
        out_path = os.path.join(rewards_dir, filename)
        final_img.save(out_path, "PNG", optimize=True)
        prev_path = os.path.join(brain_dir, f"preview_{filename}")
        final_img.save(prev_path, "PNG", optimize=True)
        print(f"  -> Saved {out_path}")

    print("\nAll 4 Reward Assets successfully generated & deployed!")

if __name__ == "__main__":
    main()
