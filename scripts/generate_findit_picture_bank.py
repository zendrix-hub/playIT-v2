"""
PlayIT 4-Benchmark Find-It Picture Bank Generator
Generates clean, pediatric illustrated picture cards for all Marungko phonemes (3 target pictures per letter).
Features:
- Continuous #2D373E outlines (10px)
- Bold Duolingo ABC silhouettes
- 100% transparent RGBA backgrounds
- 512x512 master resolution
"""

import os
from PIL import Image, ImageDraw, ImageFilter

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
PICTURES_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images", "pictures")

OUTLINE = (45, 55, 62, 255)       # #2D373E
OUTLINE_WIDTH = 10
PUPIL = (31, 58, 61, 255)         # #1F3A3D
WHITE = (255, 255, 255, 255)
ROSY_CHEEK = (255, 170, 185, 255)

def apply_outline(fill_img, stroke_w=OUTLINE_WIDTH, stroke_color=OUTLINE):
    alpha = fill_img.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_w * 2 + 1))
    stroke_base = Image.new("RGBA", fill_img.size, stroke_color)
    stroke_layer = Image.new("RGBA", fill_img.size, (0, 0, 0, 0))
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, fill_img)

def draw_star(draw, cx, cy, r_outer, r_inner, fill):
    import math
    points = []
    for i in range(10):
        r = r_outer if i % 2 == 0 else r_inner
        angle = i * math.pi / 5 - math.pi / 2
        x = cx + r * math.cos(angle)
        y = cy + r * math.sin(angle)
        points.append((x, y))
    draw.polygon(points, fill=fill)

def render_star(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    draw_star(d, 256, 256, 190, 85, (255, 214, 0, 255))
    # Eyes & Smile
    d.ellipse([215, 235, 235, 265], fill=PUPIL)
    d.ellipse([277, 235, 297, 265], fill=PUPIL)
    d.arc([235, 265, 277, 295], 0, 180, fill=PUPIL, width=8)
    return apply_outline(img)

def render_snake(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    GREEN = (76, 175, 80, 255)
    # S-curved body
    d.arc([140, 160, 360, 360], 0, 180, fill=GREEN, width=54)
    d.arc([160, 260, 380, 440], 180, 360, fill=GREEN, width=54)
    # Head
    d.ellipse([120, 130, 220, 220], fill=GREEN)
    d.ellipse([145, 150, 165, 175], fill=WHITE)
    d.ellipse([150, 155, 165, 170], fill=PUPIL)
    # Tongue
    d.line([(120, 175), (85, 175)], fill=(244, 67, 54, 255), width=8)
    return apply_outline(img)

def render_ant(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    RED_ANT = (211, 47, 47, 255)
    # 3 Body segments
    d.ellipse([120, 220, 230, 320], fill=RED_ANT) # Abdomen
    d.ellipse([215, 235, 305, 305], fill=RED_ANT) # Thorax
    d.ellipse([290, 210, 390, 310], fill=RED_ANT) # Head
    # Legs
    for x in [220, 260, 300]:
        d.line([(x, 270), (x - 20, 370), (x - 40, 390)], fill=PUPIL, width=10)
    # Antennae
    d.line([(360, 220), (390, 160), (410, 155)], fill=PUPIL, width=8)
    # Eye
    d.ellipse([340, 230, 365, 260], fill=WHITE)
    d.ellipse([350, 240, 362, 255], fill=PUPIL)
    return apply_outline(img)

def render_axe(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    WOOD = (161, 110, 75, 255)
    STEEL = (176, 190, 197, 255)
    # Handle
    d.polygon([(160, 420), (190, 440), (340, 160), (310, 140)], fill=WOOD)
    # Blade
    d.polygon([(260, 120), (390, 80), (410, 200), (280, 220)], fill=STEEL)
    return apply_outline(img)

def render_igloo(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    ICE = (224, 242, 254, 255)
    DARK_ICE = (186, 230, 253, 255)
    # Main Dome
    d.ellipse([110, 150, 402, 420], fill=ICE)
    # Entrance
    d.ellipse([215, 270, 315, 420], fill=DARK_ICE)
    d.pieslice([225, 290, 305, 420], 180, 360, fill=PUPIL)
    # Ice Block lines
    d.arc([120, 160, 392, 410], 180, 360, fill=OUTLINE, width=6)
    d.arc([150, 220, 362, 410], 180, 360, fill=OUTLINE, width=6)
    return apply_outline(img)

def render_ink(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    GLASS = (203, 213, 225, 255)
    INK_BLUE = (30, 58, 138, 255)
    # Bottle
    d.rounded_rectangle([150, 200, 362, 420], radius=30, fill=GLASS)
    d.rounded_rectangle([165, 240, 347, 405], radius=20, fill=INK_BLUE)
    # Neck & Cap
    d.rounded_rectangle([200, 140, 312, 200], radius=15, fill=(71, 85, 105, 255))
    # Quill Feather
    d.polygon([(290, 160), (390, 60), (370, 140)], fill=(255, 255, 255, 255))
    return apply_outline(img)

def render_egg(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.ellipse([140, 100, 372, 430], fill=(254, 243, 199, 255))
    # Smile
    d.ellipse([220, 240, 240, 265], fill=PUPIL)
    d.ellipse([272, 240, 292, 265], fill=PUPIL)
    d.arc([236, 265, 276, 295], 0, 180, fill=PUPIL, width=6)
    return apply_outline(img)

def render_envelope(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle([110, 160, 402, 360], radius=20, fill=(255, 255, 255, 255))
    d.polygon([(110, 160), (256, 270), (402, 160)], fill=(241, 245, 249, 255))
    d.polygon([(110, 160), (256, 270), (402, 160)], outline=OUTLINE, width=6)
    return apply_outline(img)

def render_nut(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    NUT_BROWN = (180, 83, 9, 255)
    d.ellipse([160, 150, 352, 420], fill=NUT_BROWN)
    d.arc([160, 150, 352, 300], 180, 360, fill=(120, 53, 15, 255), width=36)
    return apply_outline(img)

def render_net(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # Ring
    d.ellipse([130, 130, 382, 220], outline=OUTLINE, width=20)
    # Mesh
    d.polygon([(150, 180), (256, 420), (362, 180)], fill=(224, 242, 254, 200))
    for x in range(170, 360, 30):
        d.line([(x, 180), (256, 420)], fill=OUTLINE, width=4)
    return apply_outline(img)

def render_map(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle([120, 140, 392, 380], radius=24, fill=(254, 243, 199, 255))
    # Red X trail
    d.line([(160, 200), (230, 290), (320, 220), (340, 320)], fill=(239, 68, 68, 255), width=8)
    d.line([(330, 310), (350, 330)], fill=(220, 38, 38, 255), width=10)
    d.line([(350, 310), (330, 330)], fill=(220, 38, 38, 255), width=10)
    return apply_outline(img)

def main():
    print("=" * 80)
    print("[*] Generating 4-Benchmark Find-It Picture Bank Assets...")
    print("=" * 80)

    generators = [
        ("picture_star.png", render_star),
        ("picture_snake.png", render_snake),
        ("picture_ant.png", render_ant),
        ("picture_axe.png", render_axe),
        ("picture_igloo.png", render_igloo),
        ("picture_ink.png", render_ink),
        ("picture_egg.png", render_egg),
        ("picture_envelope.png", render_envelope),
        ("picture_nut.png", render_nut),
        ("picture_net.png", render_net),
        ("picture_map.png", render_map),
    ]

    for filename, gen_fn in generators:
        img = gen_fn(512)
        out_path = os.path.join(PICTURES_DIR, filename)
        img.save(out_path, format="PNG")
        print(f"  [+] Generated picture: {filename} -> {out_path}")

    print("=" * 80)
    print("[*] Find-It Picture Bank generation complete!")

if __name__ == "__main__":
    main()
