"""
PlayIT Full-Body Animal Avatar Character Generator
Generates the 6 Full-Body Animal Companions in the unified 4-Benchmark Mascot Style:
  1. Cat (Miki) - Cheerful Waving Cat
  2. Monkey (Milo) - Energetic Clapping Monkey
  3. Bunny (Bella) - Bouncing Cheering Bunny
  4. Bear (Barnaby) - Warm Pointing Bear
  5. Frog (Finley) - Joyful Jumping Frog
  6. Owl (Ollie) - Wise Cheering Owl
All assets feature:
- Continuous #2D373E outlines (10px)
- Glossy double-catchlight pupils
- Cheerful rosy blush
- Transparent RGBA background
- 512x512 master resolution
"""

import os
from PIL import Image, ImageDraw, ImageFilter

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images", "characters")
MASCOT_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images", "mascot")

OUTLINE = (45, 55, 62, 255)       # #2D373E
OUTLINE_WIDTH = 10
PUPIL = (31, 58, 61, 255)         # #1F3A3D
WHITE = (255, 255, 255, 255)
ROSY_CHEEK = (255, 170, 185, 255)
TONGUE = (244, 63, 94, 255)

def apply_outline(fill_img, stroke_w=OUTLINE_WIDTH, stroke_color=OUTLINE):
    alpha = fill_img.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_w * 2 + 1))
    stroke_base = Image.new("RGBA", fill_img.size, stroke_color)
    stroke_layer = Image.new("RGBA", fill_img.size, (0, 0, 0, 0))
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, fill_img)

def draw_glossy_eyes(draw, lx, rx, y, pupil_r=22, patch_color=None, patch_r=36):
    if patch_color:
        draw.ellipse([lx - patch_r, y - patch_r, lx + patch_r, y + patch_r], fill=patch_color)
        draw.ellipse([rx - patch_r, y - patch_r, rx + patch_r, y + patch_r], fill=patch_color)
    # Left Pupil
    draw.ellipse([lx - pupil_r, y - pupil_r, lx + pupil_r, y + pupil_r], fill=PUPIL)
    draw.ellipse([lx - 7 - 6, y - 8 - 6, lx - 7 + 6, y - 8 + 6], fill=WHITE)
    draw.ellipse([lx + 5 - 3, y + 6 - 3, lx + 5 + 3, y + 6 + 3], fill=WHITE)
    # Right Pupil
    draw.ellipse([rx - pupil_r, y - pupil_r, rx + pupil_r, y + pupil_r], fill=PUPIL)
    draw.ellipse([rx - 7 - 6, y - 8 - 6, rx - 7 + 6, y - 8 + 6], fill=WHITE)
    draw.ellipse([rx + 5 - 3, y + 6 - 3, rx + 5 + 3, y + 6 + 3], fill=WHITE)

def draw_smile(draw, cx, cy, w=24, h=16):
    draw.pieslice([cx - w, cy - h//2, cx + w, cy + h*2], 0, 180, fill=PUPIL)
    draw.pieslice([cx - w*0.7, cy + h*0.4, cx + w*0.7, cy + h*2], 0, 180, fill=TONGUE)

def render_cat_fullbody(size=512):
    """Avatar 1: Cheerful Orange Cat (Waving)"""
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    CAT_ORANGE = (255, 152, 0, 255)
    CAT_LIGHT = (255, 204, 128, 255)
    
    # Tail
    d.arc([340, 260, 440, 440], 270, 90, fill=CAT_ORANGE, width=32)
    # Feet
    d.ellipse([170, 430, 230, 480], fill=CAT_ORANGE)
    d.ellipse([282, 430, 342, 480], fill=CAT_ORANGE)
    # Body
    d.ellipse([175, 240, 337, 450], fill=CAT_ORANGE)
    d.ellipse([210, 280, 302, 430], fill=CAT_LIGHT) # Belly
    # Left Arm (Waving up)
    d.ellipse([110, 210, 180, 280], fill=CAT_ORANGE)
    d.ellipse([100, 190, 150, 240], fill=CAT_LIGHT) # Paw
    # Right Arm
    d.ellipse([320, 280, 380, 350], fill=CAT_ORANGE)
    # Ears
    d.polygon([(140, 180), (180, 80), (220, 160)], fill=CAT_ORANGE)
    d.polygon([(155, 170), (180, 105), (205, 155)], fill=ROSY_CHEEK)
    d.polygon([(292, 160), (332, 80), (372, 180)], fill=CAT_ORANGE)
    d.polygon([(307, 155), (332, 105), (357, 170)], fill=ROSY_CHEEK)
    # Head
    d.ellipse([140, 120, 372, 300], fill=CAT_ORANGE)
    # Cheeks & Whiskers
    d.ellipse([160, 220, 200, 250], fill=ROSY_CHEEK)
    d.ellipse([312, 220, 352, 250], fill=ROSY_CHEEK)
    draw_glossy_eyes(d, 205, 307, 195, pupil_r=22, patch_color=CAT_LIGHT, patch_r=36)
    # Nose & Mouth
    d.polygon([(250, 220), (262, 220), (256, 230)], fill=PUPIL)
    draw_smile(d, 256, 235, w=22, h=16)
    return apply_outline(img)

def render_monkey_fullbody(size=512):
    """Avatar 2: Energetic Brown Monkey (Clapping/Cheering)"""
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    BROWN = (141, 110, 99, 255)
    PEACH = (255, 224, 178, 255)
    
    # Swirl Tail
    d.arc([80, 270, 200, 440], 90, 270, fill=BROWN, width=28)
    # Feet
    d.ellipse([175, 435, 235, 485], fill=BROWN)
    d.ellipse([277, 435, 337, 485], fill=BROWN)
    # Body
    d.ellipse([180, 250, 332, 455], fill=BROWN)
    d.ellipse([210, 290, 302, 435], fill=PEACH)
    # Cheering Arms (Raised high)
    d.ellipse([110, 210, 180, 290], fill=BROWN)
    d.ellipse([332, 210, 402, 290], fill=BROWN)
    d.ellipse([110, 185, 160, 235], fill=PEACH)
    d.ellipse([352, 185, 402, 235], fill=PEACH)
    # Big Monkey Ears
    d.ellipse([110, 150, 185, 225], fill=BROWN)
    d.ellipse([125, 165, 170, 210], fill=PEACH)
    d.ellipse([327, 150, 402, 225], fill=BROWN)
    d.ellipse([342, 165, 387, 210], fill=PEACH)
    # Head
    d.ellipse([145, 115, 367, 295], fill=BROWN)
    # Face Mask
    d.ellipse([170, 140, 342, 280], fill=PEACH)
    d.ellipse([160, 220, 200, 250], fill=ROSY_CHEEK)
    d.ellipse([312, 220, 352, 250], fill=ROSY_CHEEK)
    draw_glossy_eyes(d, 212, 300, 185, pupil_r=22)
    # Nose & Mouth
    d.ellipse([250, 218, 262, 228], fill=PUPIL)
    draw_smile(d, 256, 235, w=24, h=16)
    return apply_outline(img)

def render_bunny_fullbody(size=512):
    """Avatar 3: Cute Cream/Pink Bunny (Bouncing)"""
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    BUNNY_CREAM = (255, 248, 225, 255)
    BUNNY_PINK = (255, 205, 210, 255)
    
    # Long Upright Ears
    d.ellipse([165, 40, 225, 200], fill=BUNNY_CREAM)
    d.ellipse([180, 65, 210, 175], fill=BUNNY_PINK)
    d.ellipse([287, 40, 347, 200], fill=BUNNY_CREAM)
    d.ellipse([302, 65, 332, 175], fill=BUNNY_PINK)
    # Feet
    d.ellipse([165, 430, 235, 485], fill=BUNNY_CREAM)
    d.ellipse([277, 430, 347, 485], fill=BUNNY_CREAM)
    # Body
    d.ellipse([175, 250, 337, 455], fill=BUNNY_CREAM)
    d.ellipse([210, 295, 302, 435], fill=BUNNY_PINK)
    # Cute Paws held in excitement
    d.ellipse([170, 275, 225, 335], fill=BUNNY_CREAM)
    d.ellipse([287, 275, 342, 335], fill=BUNNY_CREAM)
    # Head
    d.ellipse([145, 130, 367, 305], fill=BUNNY_CREAM)
    # Rosy Blushing Cheeks
    d.ellipse([160, 230, 205, 265], fill=ROSY_CHEEK)
    d.ellipse([307, 230, 352, 265], fill=ROSY_CHEEK)
    draw_glossy_eyes(d, 208, 304, 200, pupil_r=22)
    # Nose & Mouth
    d.polygon([(250, 228), (262, 228), (256, 238)], fill=ROSY_CHEEK)
    draw_smile(d, 256, 244, w=20, h=14)
    return apply_outline(img)

def render_bear_fullbody(size=512):
    """Avatar 4: Warm Honey Bear (Pointing/Cheering)"""
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    BEAR_HONEY = (255, 183, 77, 255)
    BEAR_MUZZLE = (255, 236, 179, 255)
    
    # Round Ears
    d.ellipse([135, 100, 205, 170], fill=BEAR_HONEY)
    d.ellipse([150, 115, 190, 155], fill=BEAR_MUZZLE)
    d.ellipse([307, 100, 377, 170], fill=BEAR_HONEY)
    d.ellipse([322, 115, 362, 155], fill=BEAR_MUZZLE)
    # Feet
    d.ellipse([170, 430, 235, 485], fill=BEAR_HONEY)
    d.ellipse([277, 430, 342, 485], fill=BEAR_HONEY)
    # Sturdy Round Body
    d.ellipse([165, 245, 347, 455], fill=BEAR_HONEY)
    d.ellipse([200, 290, 312, 435], fill=BEAR_MUZZLE)
    # Right Arm Pointing forward
    d.ellipse([320, 240, 410, 310], fill=BEAR_HONEY)
    # Left Arm
    d.ellipse([115, 275, 185, 350], fill=BEAR_HONEY)
    # Head
    d.ellipse([140, 120, 372, 305], fill=BEAR_HONEY)
    # Muzzle
    d.ellipse([195, 190, 317, 280], fill=BEAR_MUZZLE)
    d.ellipse([155, 220, 195, 255], fill=ROSY_CHEEK)
    d.ellipse([317, 220, 357, 255], fill=ROSY_CHEEK)
    draw_glossy_eyes(d, 205, 307, 180, pupil_r=20)
    # Bear Nose & Smile
    d.ellipse([245, 208, 267, 225], fill=PUPIL)
    draw_smile(d, 256, 235, w=22, h=16)
    return apply_outline(img)

def render_frog_fullbody(size=512):
    """Avatar 5: Bouncy Green Frog (Jumping/Cheering)"""
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    FROG_GREEN = (102, 187, 106, 255)
    FROG_LIGHT = (200, 230, 201, 255)
    
    # Big Upper Eye Pods
    d.ellipse([145, 90, 235, 180], fill=FROG_GREEN)
    d.ellipse([277, 90, 367, 180], fill=FROG_GREEN)
    # Webbed Feet
    d.ellipse([150, 435, 230, 485], fill=FROG_GREEN)
    d.ellipse([282, 435, 362, 485], fill=FROG_GREEN)
    # Body
    d.ellipse([170, 240, 342, 455], fill=FROG_GREEN)
    d.ellipse([205, 285, 307, 435], fill=FROG_LIGHT)
    # Jumping Arms
    d.ellipse([100, 200, 175, 275], fill=FROG_GREEN)
    d.ellipse([337, 200, 412, 275], fill=FROG_GREEN)
    # Wide Friendly Head
    d.ellipse([130, 130, 382, 295], fill=FROG_GREEN)
    # Rosy Cheeks
    d.ellipse([150, 215, 195, 250], fill=ROSY_CHEEK)
    d.ellipse([317, 215, 362, 250], fill=ROSY_CHEEK)
    draw_glossy_eyes(d, 190, 322, 135, pupil_r=26, patch_color=WHITE, patch_r=40)
    # Huge Joyful Frog Smile
    draw_smile(d, 256, 220, w=38, h=24)
    return apply_outline(img)

def render_owl_fullbody(size=512):
    """Avatar 6: Wise Purple Owl (Waving Wings)"""
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    OWL_PURPLE = (171, 71, 188, 255)
    OWL_LIGHT = (225, 190, 231, 255)
    BEAK_GOLD = (255, 179, 0, 255)
    
    # Ear Tufts
    d.polygon([(140, 170), (160, 90), (210, 150)], fill=OWL_PURPLE)
    d.polygon([(302, 150), (352, 90), (372, 170)], fill=OWL_PURPLE)
    # Talons
    d.ellipse([180, 440, 230, 485], fill=BEAK_GOLD)
    d.ellipse([282, 440, 332, 485], fill=BEAK_GOLD)
    # Wings
    d.ellipse([90, 220, 190, 370], fill=OWL_PURPLE) # Left Wing
    d.ellipse([322, 220, 422, 370], fill=OWL_PURPLE) # Right Wing
    # Round Body
    d.ellipse([155, 140, 357, 455], fill=OWL_PURPLE)
    # Feather Chest
    d.ellipse([185, 250, 327, 430], fill=OWL_LIGHT)
    # Cheeks
    d.ellipse([145, 235, 185, 265], fill=ROSY_CHEEK)
    d.ellipse([327, 235, 367, 265], fill=ROSY_CHEEK)
    # Giant Owl Eye Rings
    draw_glossy_eyes(d, 205, 307, 195, pupil_r=26, patch_color=WHITE, patch_r=44)
    # Golden Beak
    d.polygon([(244, 220), (268, 220), (256, 248)], fill=BEAK_GOLD)
    return apply_outline(img)

def main():
    print("=" * 80)
    print("[*] Generating 6 Full-Body Animal Companions in 4-Benchmark Style...")
    print("=" * 80)
    
    os.makedirs(ASSETS_DIR, exist_ok=True)
    os.makedirs(MASCOT_DIR, exist_ok=True)
    
    generators = [
        ("avatar_01_cat.png", render_cat_fullbody),
        ("avatar_02_monkey.png", render_monkey_fullbody),
        ("avatar_03_bunny.png", render_bunny_fullbody),
        ("avatar_04_bear.png", render_bear_fullbody),
        ("avatar_05_frog.png", render_frog_fullbody),
        ("avatar_06_owl.png", render_owl_fullbody)
    ]
    
    for filename, gen_fn in generators:
        img = gen_fn(512)
        # Save to characters/
        path_chars = os.path.join(ASSETS_DIR, filename)
        img.save(path_chars, format="PNG")
        # Save companion copy to mascot/ for avatar display compatibility
        path_mascot = os.path.join(MASCOT_DIR, f"companion_{filename}")
        img.save(path_mascot, format="PNG")
        print(f"  [+] Saved {filename} -> {path_chars}")
        
    print("=" * 80)
    print("[*] Full-Body Animal Companions generated successfully!")

if __name__ == "__main__":
    main()
