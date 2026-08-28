"""
PlayIT Headspace Animal Avatars Generator
Generates all 6 profile companion avatars (Cat, Monkey, Bunny, Bear, Frog, Owl)
in the unified Headspace/Duolingo-ABC 2D vector style.
"""

from PIL import Image, ImageDraw
import math
import os

SIZE = 512
SCALE = 2
W = SIZE * SCALE
H = SIZE * SCALE

# Palette
DARK_OUTLINE = (45, 55, 62, 255)       # #2D373E Slate Charcoal Outline
CREAM = (255, 235, 205, 255)          # #FFEBCD Soft Cream
WHITE = (255, 255, 255, 255)
PINK_CHEEK = (255, 175, 175, 255)     # #FFAFAF Soft Pink Cheeks

# Avatar Specific Colors
CAT_ORANGE = (255, 160, 64, 255)
MONKEY_BROWN = (200, 130, 80, 255)
BUNNY_PINK = (255, 210, 220, 255)
BEAR_CARAMEL = (175, 110, 65, 255)
FROG_GREEN = (100, 200, 100, 255)
OWL_PURPLE = (150, 130, 210, 255)

def draw_thick_arc(draw, bbox, start, end, color, width):
    draw.arc(bbox, start=start, end=end, fill=color, width=width)

def draw_thick_line(draw, start, end, color, width):
    draw.line([start, end], fill=color, width=width, joint="curve")

def draw_face_eyes(draw, cx, cy, eye_spacing=105, eye_r=38):
    # Eyes
    lx, rx = cx - eye_spacing, cx + eye_spacing
    draw.ellipse([lx - eye_r, cy - eye_r, lx + eye_r, cy + eye_r], fill=DARK_OUTLINE)
    draw.ellipse([rx - eye_r, cy - eye_r, rx + eye_r, cy + eye_r], fill=DARK_OUTLINE)
    # Specular Highlights
    draw.ellipse([lx - 12 - 14, cy - 12 - 14, lx - 12 + 14, cy - 12 + 14], fill=WHITE)
    draw.ellipse([rx - 12 - 14, cy - 12 - 14, rx - 12 + 14, cy - 12 + 14], fill=WHITE)
    draw.ellipse([lx + 12 - 6, cy + 12 - 6, lx + 12 + 6, cy + 12 + 6], fill=WHITE)
    draw.ellipse([rx + 12 - 6, cy + 12 - 6, rx + 12 + 6, cy + 12 + 6], fill=WHITE)
    # Cheeks
    draw.ellipse([lx - 60 - 24, cy + 30 - 16, lx - 60 + 24, cy + 30 + 16], fill=PINK_CHEEK)
    draw.ellipse([rx + 60 - 24, cy + 30 - 16, rx + 60 + 24, cy + 30 + 16], fill=PINK_CHEEK)

def draw_cat_avatar():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # Ears
    draw.polygon([(cx - 240, cy - 80), (cx - 160, cy - 320), (cx - 60, cy - 200)], fill=CAT_ORANGE, outline=DARK_OUTLINE)
    draw.polygon([(cx - 210, cy - 90), (cx - 160, cy - 270), (cx - 90, cy - 190)], fill=PINK_CHEEK)
    draw.polygon([(cx + 240, cy - 80), (cx + 160, cy - 320), (cx + 60, cy - 200)], fill=CAT_ORANGE, outline=DARK_OUTLINE)
    draw.polygon([(cx + 210, cy - 90), (cx + 160, cy - 270), (cx + 90, cy - 190)], fill=PINK_CHEEK)

    # Head
    draw.ellipse([cx - 270, cy - 240, cx + 270, cy + 260], fill=CAT_ORANGE, outline=DARK_OUTLINE, width=16)
    # Cream Muzzle Patch
    draw.ellipse([cx - 200, cy - 120, cx + 200, cy + 240], fill=CREAM)

    draw_face_eyes(draw, cx, cy - 20, eye_spacing=100)

    # Cat Nose & Mouth
    draw.polygon([(cx - 18, cy + 40), (cx + 18, cy + 40), (cx, cy + 58)], fill=DARK_OUTLINE)
    draw_thick_arc(draw, [cx - 48, cy + 45, cx, cy + 85], 0, 180, DARK_OUTLINE, 12)
    draw_thick_arc(draw, [cx, cy + 45, cx + 48, cy + 85], 0, 180, DARK_OUTLINE, 12)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_monkey_avatar():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # Big Round Ears
    draw.ellipse([cx - 340, cy - 140, cx - 160, cy + 40], fill=MONKEY_BROWN, outline=DARK_OUTLINE, width=14)
    draw.ellipse([cx - 310, cy - 110, cx - 190, cy + 10], fill=CREAM)
    draw.ellipse([cx + 160, cy - 140, cx + 340, cy + 40], fill=MONKEY_BROWN, outline=DARK_OUTLINE, width=14)
    draw.ellipse([cx + 190, cy - 110, cx + 310, cy + 10], fill=CREAM)

    # Head
    draw.ellipse([cx - 260, cy - 250, cx + 260, cy + 250], fill=MONKEY_BROWN, outline=DARK_OUTLINE, width=16)
    # Heart Shaped Face Patch
    draw.ellipse([cx - 180, cy - 180, cx, cy + 20], fill=CREAM)
    draw.ellipse([cx, cy - 180, cx + 180, cy + 20], fill=CREAM)
    draw.ellipse([cx - 210, cy - 80, cx + 210, cy + 210], fill=CREAM)

    draw_face_eyes(draw, cx, cy - 40, eye_spacing=85)

    # Nose & Wide Smile
    draw.ellipse([cx - 18, cy + 30, cx - 4, cy + 46], fill=DARK_OUTLINE)
    draw.ellipse([cx + 4, cy + 30, cx + 18, cy + 46], fill=DARK_OUTLINE)
    draw_thick_arc(draw, [cx - 80, cy + 20, cx + 80, cy + 120], 20, 160, DARK_OUTLINE, 14)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_bunny_avatar():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 30

    # Long Bunny Ears
    draw.ellipse([cx - 160, cy - 440, cx - 50, cy - 120], fill=WHITE, outline=DARK_OUTLINE, width=14)
    draw.ellipse([cx - 140, cy - 410, cx - 70, cy - 160], fill=BUNNY_PINK)
    draw.ellipse([cx + 50, cy - 440, cx + 160, cy - 120], fill=WHITE, outline=DARK_OUTLINE, width=14)
    draw.ellipse([cx + 70, cy - 410, cx + 140, cy - 160], fill=BUNNY_PINK)

    # Head
    draw.ellipse([cx - 250, cy - 210, cx + 250, cy + 240], fill=WHITE, outline=DARK_OUTLINE, width=16)

    draw_face_eyes(draw, cx, cy - 20, eye_spacing=100)

    # Pink Nose & Bunny Smile
    draw.ellipse([cx - 16, cy + 35, cx + 16, cy + 55], fill=BUNNY_PINK, outline=DARK_OUTLINE, width=5)
    draw_thick_arc(draw, [cx - 40, cy + 45, cx, cy + 85], 0, 180, DARK_OUTLINE, 12)
    draw_thick_arc(draw, [cx, cy + 45, cx + 40, cy + 85], 0, 180, DARK_OUTLINE, 12)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_bear_avatar():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # Round Ears
    draw.ellipse([cx - 260, cy - 260, cx - 110, cy - 110], fill=BEAR_CARAMEL, outline=DARK_OUTLINE, width=14)
    draw.ellipse([cx - 230, cy - 230, cx - 140, cy - 140], fill=CREAM)
    draw.ellipse([cx + 110, cy - 260, cx + 260, cy - 110], fill=BEAR_CARAMEL, outline=DARK_OUTLINE, width=14)
    draw.ellipse([cx + 140, cy - 230, cx + 230, cy - 140], fill=CREAM)

    # Head
    draw.ellipse([cx - 260, cy - 210, cx + 260, cy + 250], fill=BEAR_CARAMEL, outline=DARK_OUTLINE, width=16)
    # Snout
    draw.ellipse([cx - 130, cy + 10, cx + 130, cy + 190], fill=CREAM, outline=DARK_OUTLINE, width=10)

    draw_face_eyes(draw, cx, cy - 50, eye_spacing=105)

    # Nose & Mouth
    draw.ellipse([cx - 32, cy + 35, cx + 32, cy + 80], fill=DARK_OUTLINE)
    draw_thick_line(draw, (cx, cy + 80), (cx, cy + 125), DARK_OUTLINE, 12)
    draw_thick_arc(draw, [cx - 60, cy + 85, cx + 60, cy + 155], 20, 160, DARK_OUTLINE, 12)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_frog_avatar():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0 + 20

    # Big Pop-Up Eyes Domes
    draw.ellipse([cx - 230, cy - 260, cx - 50, cy - 80], fill=FROG_GREEN, outline=DARK_OUTLINE, width=14)
    draw.ellipse([cx + 50, cy - 260, cx + 230, cy - 80], fill=FROG_GREEN, outline=DARK_OUTLINE, width=14)

    # Head Base
    draw.ellipse([cx - 270, cy - 170, cx + 270, cy + 220], fill=FROG_GREEN, outline=DARK_OUTLINE, width=16)
    # Cream Chin Patch
    draw.ellipse([cx - 170, cy + 10, cx + 170, cy + 190], fill=CREAM)

    # Eyes Inside Domes
    draw_face_eyes(draw, cx, cy - 170, eye_spacing=140, eye_r=44)

    # Cute Nostrils & Frog Smile
    draw.ellipse([cx - 16, cy - 20, cx - 6, cy - 8], fill=DARK_OUTLINE)
    draw.ellipse([cx + 6, cy - 20, cx + 16, cy - 8], fill=DARK_OUTLINE)
    draw_thick_arc(draw, [cx - 130, cy - 20, cx + 130, cy + 110], 15, 165, DARK_OUTLINE, 14)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_owl_avatar():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W / 2.0, H / 2.0

    # Ear Tufts
    draw.polygon([(cx - 240, cy - 180), (cx - 180, cy - 300), (cx - 100, cy - 200)], fill=OWL_PURPLE, outline=DARK_OUTLINE)
    draw.polygon([(cx + 240, cy - 180), (cx + 180, cy - 300), (cx + 100, cy - 200)], fill=OWL_PURPLE, outline=DARK_OUTLINE)

    # Head
    draw.ellipse([cx - 260, cy - 220, cx + 260, cy + 240], fill=OWL_PURPLE, outline=DARK_OUTLINE, width=16)

    # Giant Eye Mask Rings
    draw.ellipse([cx - 210, cy - 130, cx - 10, cy + 70], fill=CREAM, outline=DARK_OUTLINE, width=10)
    draw.ellipse([cx + 10, cy - 130, cx + 210, cy + 70], fill=CREAM, outline=DARK_OUTLINE, width=10)

    draw_face_eyes(draw, cx, cy - 30, eye_spacing=110, eye_r=42)

    # Golden Beak
    draw.polygon([(cx - 24, cy + 25), (cx + 24, cy + 25), (cx, cy + 85)], fill=(255, 193, 7, 255), outline=DARK_OUTLINE)

    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def main():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    target_dir = os.path.join(base_dir, "app", "src", "main", "assets", "images", "mascot")
    preview_dir = os.path.join(base_dir, "screenshots", "avatars")
    os.makedirs(target_dir, exist_ok=True)
    os.makedirs(preview_dir, exist_ok=True)

    avatars = {
        "avatar_01": draw_cat_avatar(),
        "avatar_02": draw_monkey_avatar(),
        "avatar_03": draw_bunny_avatar(),
        "avatar_04": draw_bear_avatar(),
        "avatar_05": draw_frog_avatar(),
        "avatar_06": draw_owl_avatar(),
    }

    print("[*] Generating all 6 profile animal avatars in Headspace vector style...")
    for name, img in avatars.items():
        img.save(os.path.join(target_dir, f"{name}.png"), "PNG", optimize=True)
        img.save(os.path.join(preview_dir, f"{name}.png"), "PNG", optimize=True)
        print(f"  [+] Generated: {name}.png")

    print("[*] All 6 avatars successfully updated!")

if __name__ == "__main__":
    main()
