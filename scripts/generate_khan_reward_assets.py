"""
PlayIT Reward Assets Generator — Khan Academy Kids x Duolingo ABC Blend
Generates high-definition, perfectly joined vector reward badges:
1. reward_star.png (smiling golden star with rosy cheeks & specular highlights)
2. reward_heart.png (glossy warm heart badge with smooth continuous outline)
3. reward_streak.png (curved cheerful golden flame badge with happy face)
4. reward_confetti_burst.png (colorful celebration burst with stars & ribbons)
"""

from PIL import Image, ImageDraw
import math
import os

SIZE = 512
SCALE = 2
W = SIZE * SCALE
H = SIZE * SCALE

# Palette
DARK_OUTLINE = (74, 46, 24, 255)       # #4A2E18 DarkBrownOutline (Style Guide 16 §2.1)
GOLD_MAIN = (255, 204, 0, 255)         # Bright Sunny Gold
GOLD_DARK = (245, 166, 35, 255)        # Shaded Gold
GOLD_LIGHT = (255, 235, 115, 255)      # Top Highlight Gold
GUAVA_PINK = (255, 90, 110, 255)       # Soft Guava Heart
MANGO_FIRE = (250, 123, 40, 255)       # Warm Flame Orange
FIRE_YELLOW = (255, 215, 0, 255)       # Inner Flame Yellow
PINK_CHEEK = (255, 140, 140, 220)      # Rosy Blush
WHITE = (255, 255, 255, 255)

def draw_star():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 15
    outer_r = 380
    inner_r = 180

    points = []
    for i in range(10):
        angle = math.radians(i * 36 - 90)
        r = outer_r if i % 2 == 0 else inner_r
        points.append((cx + r * math.cos(angle), cy + r * math.sin(angle)))

    draw.polygon(points, fill=GOLD_MAIN, outline=DARK_OUTLINE)
    for i in range(10):
        p1 = points[i]
        p2 = points[(i + 1) % 10]
        draw.line([p1, p2], fill=DARK_OUTLINE, width=24, joint="curve")

    draw.polygon([points[0], (cx, cy), points[1]], fill=GOLD_LIGHT)
    draw.polygon([points[0], (cx, cy), points[9]], fill=GOLD_DARK)

    draw.line([points[9], points[0]], fill=DARK_OUTLINE, width=24, joint="curve")
    draw.line([points[0], points[1]], fill=DARK_OUTLINE, width=24, joint="curve")

    eye_spacing = 90
    eye_y = cy - 20
    eye_r = 34
    for ex in [cx - eye_spacing, cx + eye_spacing]:
        draw.ellipse([ex - eye_r, eye_y - eye_r, ex + eye_r, eye_y + eye_r], fill=DARK_OUTLINE)
        draw.ellipse([ex - 12 - 11, eye_y - 12 - 11, ex - 12 + 11, eye_y - 12 + 11], fill=WHITE)
        draw.ellipse([ex + 10 - 5, eye_y + 10 - 5, ex + 10 + 5, eye_y + 10 + 5], fill=WHITE)

    draw.ellipse([cx - 150 - 30, eye_y + 30 - 18, cx - 150 + 30, eye_y + 30 + 18], fill=PINK_CHEEK)
    draw.ellipse([cx + 150 - 30, eye_y + 30 - 18, cx + 150 + 30, eye_y + 30 + 18], fill=PINK_CHEEK)
    draw.arc([cx - 55, eye_y + 5, cx + 55, eye_y + 75], start=10, end=170, fill=DARK_OUTLINE, width=16)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_heart():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 - 40

    steps = 200
    points = []
    scale_factor = 22.0
    for i in range(steps):
        t = math.radians(i * (360.0 / steps))
        hx = 16 * (math.sin(t) ** 3)
        hy = -(13 * math.cos(t) - 5 * math.cos(2*t) - 2 * math.cos(3*t) - math.cos(4*t))
        px = cx + hx * scale_factor
        py = cy + hy * scale_factor
        points.append((px, py))

    draw.polygon(points, fill=GUAVA_PINK, outline=DARK_OUTLINE)
    for i in range(len(points)):
        p1 = points[i]
        p2 = points[(i + 1) % len(points)]
        draw.line([p1, p2], fill=DARK_OUTLINE, width=26, joint="curve")

    hl_pts = []
    for i in range(int(steps * 0.55), int(steps * 0.80)):
        t = math.radians(i * (360.0 / steps))
        hx = 16 * (math.sin(t) ** 3)
        hy = -(13 * math.cos(t) - 5 * math.cos(2*t) - 2 * math.cos(3*t) - math.cos(4*t))
        px = cx + (hx * 0.78) * scale_factor - 15
        py = cy + (hy * 0.78) * scale_factor - 15
        hl_pts.append((px, py))

    if len(hl_pts) > 1:
        for i in range(len(hl_pts) - 1):
            draw.line([hl_pts[i], hl_pts[i+1]], fill=WHITE, width=28, joint="curve")

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_streak_flame():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # Smooth teardrop/flame curve
    steps = 180
    flame_pts = []
    for i in range(steps):
        t = math.radians(i * (360.0 / steps))
        # Parametric organic teardrop flame
        fx = 220 * math.sin(t) * (math.sin(t/2.0) ** 1.8)
        fy = -360 * math.cos(t) + 60
        flame_pts.append((cx + fx, cy + fy))

    draw.polygon(flame_pts, fill=MANGO_FIRE, outline=DARK_OUTLINE)
    for i in range(len(flame_pts)):
        draw.line([flame_pts[i], flame_pts[(i+1)%len(flame_pts)]], fill=DARK_OUTLINE, width=26, joint="curve")

    # Inner Flame
    inner_pts = []
    for i in range(steps):
        t = math.radians(i * (360.0 / steps))
        fx = 120 * math.sin(t) * (math.sin(t/2.0) ** 1.8)
        fy = -200 * math.cos(t) + 70
        inner_pts.append((cx + fx, cy + fy))
    draw.polygon(inner_pts, fill=FIRE_YELLOW)

    # Cheerful Face
    eye_spacing = 65
    eye_y = cy + 40
    eye_r = 24
    for ex in [cx - eye_spacing, cx + eye_spacing]:
        draw.ellipse([ex - eye_r, eye_y - eye_r, ex + eye_r, eye_y + eye_r], fill=DARK_OUTLINE)
        draw.ellipse([ex - 8 - 7, eye_y - 8 - 7, ex - 8 + 7, eye_y - 8 + 7], fill=WHITE)

    draw.ellipse([cx - 110 - 22, eye_y + 25 - 14, cx - 110 + 22, eye_y + 25 + 14], fill=PINK_CHEEK)
    draw.ellipse([cx + 110 - 22, eye_y + 25 - 14, cx + 110 + 22, eye_y + 25 + 14], fill=PINK_CHEEK)
    draw.arc([cx - 40, eye_y + 10, cx + 40, eye_y + 65], start=10, end=170, fill=DARK_OUTLINE, width=12)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_confetti_burst():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    colors = [
        (255, 204, 0, 255),    # Gold
        (76, 175, 80, 255),     # Leaf Green
        (139, 95, 191, 255),   # Ube Purple
        (255, 90, 110, 255),   # Guava Pink
        (56, 189, 248, 255),   # Sky Blue
    ]

    star_r = 130
    star_inner = 65
    pts = []
    for i in range(10):
        angle = math.radians(i * 36 - 90)
        r = star_r if i % 2 == 0 else star_inner
        pts.append((cx + r * math.cos(angle), cy + r * math.sin(angle)))
    draw.polygon(pts, fill=GOLD_MAIN, outline=DARK_OUTLINE)
    for i in range(10):
        draw.line([pts[i], pts[(i+1)%10]], fill=DARK_OUTLINE, width=16, joint="curve")

    num_particles = 16
    for i in range(num_particles):
        angle = math.radians(i * (360 / num_particles))
        dist = 280 + (i % 3) * 60
        px = cx + dist * math.cos(angle)
        py = cy + dist * math.sin(angle)
        c = colors[i % len(colors)]

        if i % 2 == 0:
            dr = 36
            d_pts = [(px, py - dr), (px + dr, py), (px, py + dr), (px - dr, py)]
            draw.polygon(d_pts, fill=c, outline=DARK_OUTLINE)
            for j in range(4):
                draw.line([d_pts[j], d_pts[(j+1)%4]], fill=DARK_OUTLINE, width=8, joint="curve")
        else:
            draw.ellipse([px - 26, py - 26, px + 26, py + 26], fill=c, outline=DARK_OUTLINE, width=8)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def main():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    target_dir = os.path.join(base_dir, "app", "src", "main", "assets", "images", "rewards")
    os.makedirs(target_dir, exist_ok=True)

    assets = {
        "reward_star": draw_star(),
        "reward_heart": draw_heart(),
        "reward_streak": draw_streak_flame(),
        "reward_confetti_burst": draw_confetti_burst(),
    }

    print("[*] Generating polished Khan Academy Kids reward badges...")
    for name, img in assets.items():
        output_path = os.path.join(target_dir, f"{name}.png")
        img.save(output_path, "PNG", optimize=True)
        print(f"  [+] Generated: {name}.png ({os.path.getsize(output_path)} bytes)")

    print("[*] All reward badge assets successfully updated!")

if __name__ == "__main__":
    main()
