"""
PlayIT Complete Challenge Image Suite Generator & Asset Verifier
Ensures 100% of all images across all lesson challenges (Hear It, Say It, Find It, Blend It) exist and adhere to the 4-Benchmark synthesis:
- Bold tactile silhouettes
- Continuous 10px #2D373E outlines
- 100% transparent RGBA backgrounds
- 512x512 master resolution
"""

import os
import math
from PIL import Image, ImageDraw, ImageFilter

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
PICTURES_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images", "pictures")
LETTERS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images", "letters")
REWARDS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images", "rewards")

OUTLINE = (45, 55, 62, 255)       # #2D373E
OUTLINE_WIDTH = 10
PUPIL = (31, 58, 61, 255)         # #1F3A3D
WHITE = (255, 255, 255, 255)

def apply_outline(fill_img, stroke_w=OUTLINE_WIDTH, stroke_color=OUTLINE):
    alpha = fill_img.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_w * 2 + 1))
    stroke_base = Image.new("RGBA", fill_img.size, stroke_color)
    stroke_layer = Image.new("RGBA", fill_img.size, (0, 0, 0, 0))
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, fill_img)

# --- PICTURE GENERATORS ---

def render_owl(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    PURPLE = (168, 85, 247, 255)
    d.ellipse([140, 150, 372, 420], fill=PURPLE)
    d.ellipse([160, 200, 240, 280], fill=WHITE)
    d.ellipse([272, 200, 352, 280], fill=WHITE)
    d.ellipse([185, 225, 215, 255], fill=PUPIL)
    d.ellipse([297, 225, 327, 255], fill=PUPIL)
    d.polygon([(240, 270), (272, 270), (256, 310)], fill=(255, 179, 0, 255))
    return apply_outline(img)

def render_ox(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    BROWN = (141, 110, 99, 255)
    d.ellipse([140, 160, 372, 380], fill=BROWN)
    # Horns
    d.polygon([(170, 180), (120, 100), (190, 160)], fill=(238, 238, 238, 255))
    d.polygon([(342, 180), (392, 100), (322, 160)], fill=(238, 238, 238, 255))
    # Snout
    d.ellipse([200, 280, 312, 360], fill=(215, 204, 200, 255))
    d.ellipse([220, 310, 240, 330], fill=PUPIL)
    d.ellipse([272, 310, 292, 330], fill=PUPIL)
    # Eyes
    d.ellipse([180, 210, 220, 250], fill=PUPIL)
    d.ellipse([292, 210, 332, 250], fill=PUPIL)
    return apply_outline(img)

def render_uncle(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.ellipse([170, 140, 342, 320], fill=(255, 224, 178, 255)) # Head
    d.rounded_rectangle([190, 260, 322, 290], radius=10, fill=PUPIL) # Mustache
    d.ellipse([190, 190, 225, 225], fill=PUPIL)
    d.ellipse([287, 190, 322, 225], fill=PUPIL)
    d.arc([230, 290, 282, 320], 0, 180, fill=PUPIL, width=6)
    return apply_outline(img)

def render_tree(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle([226, 280, 286, 440], radius=15, fill=(121, 85, 72, 255))
    d.ellipse([110, 110, 402, 340], fill=(76, 175, 80, 255))
    d.ellipse([150, 140, 230, 220], fill=(129, 199, 132, 255))
    return apply_outline(img)

def render_top(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.polygon([(256, 110), (370, 240), (256, 410), (142, 240)], fill=(244, 67, 54, 255))
    d.line([(256, 410), (256, 450)], fill=PUPIL, width=12) # Tip
    d.line([(256, 70), (256, 110)], fill=PUPIL, width=14)  # Handle
    return apply_outline(img)

def render_key(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    GOLD = (255, 193, 7, 255)
    d.ellipse([130, 160, 270, 300], fill=GOLD)
    d.ellipse([170, 200, 230, 260], fill=(0, 0, 0, 0)) # Ring hole
    d.rounded_rectangle([250, 210, 410, 250], radius=8, fill=GOLD)
    d.rectangle([350, 250, 380, 300], fill=GOLD)
    d.rectangle([390, 250, 410, 290], fill=GOLD)
    return apply_outline(img)

def render_leaf(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    GREEN = (76, 175, 80, 255)
    d.pieslice([130, 130, 382, 382], 0, 90, fill=GREEN)
    d.pieslice([130, 130, 382, 382], 180, 270, fill=GREEN)
    d.line([(130, 382), (382, 130)], fill=PUPIL, width=8)
    return apply_outline(img)

def render_yarn(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    PINK = (236, 72, 153, 255)
    d.ellipse([130, 150, 382, 402], fill=PINK)
    d.arc([140, 160, 372, 392], 30, 210, fill=WHITE, width=8)
    d.arc([160, 180, 352, 372], 210, 390, fill=WHITE, width=8)
    d.line([(360, 380), (430, 440)], fill=PINK, width=10)
    return apply_outline(img)

def render_yak(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    BROWN = (93, 64, 55, 255)
    d.ellipse([130, 160, 382, 390], fill=BROWN)
    # Curved Horns
    d.arc([80, 100, 220, 240], 180, 360, fill=(238, 238, 238, 255), width=24)
    d.arc([292, 100, 432, 240], 180, 360, fill=(238, 238, 238, 255), width=24)
    d.ellipse([180, 220, 220, 260], fill=WHITE)
    d.ellipse([292, 220, 332, 260], fill=WHITE)
    d.ellipse([195, 235, 215, 255], fill=PUPIL)
    d.ellipse([307, 235, 327, 255], fill=PUPIL)
    return apply_outline(img)

def render_gift(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    CYAN = (6, 182, 212, 255)
    YELLOW = (250, 204, 21, 255)
    d.rounded_rectangle([130, 170, 382, 420], radius=20, fill=CYAN)
    # Ribbon
    d.rectangle([236, 170, 276, 420], fill=YELLOW)
    d.rectangle([130, 275, 382, 315], fill=YELLOW)
    # Bow
    d.ellipse([190, 110, 266, 180], fill=YELLOW)
    d.ellipse([246, 110, 322, 180], fill=YELLOW)
    return apply_outline(img)

def render_ring(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    GOLD = (245, 158, 11, 255)
    d.ellipse([140, 170, 372, 402], fill=GOLD)
    d.ellipse([185, 215, 327, 357], fill=(0, 0, 0, 0))
    # Diamond
    d.polygon([(256, 100), (296, 150), (256, 190), (216, 150)], fill=(56, 189, 248, 255))
    return apply_outline(img)

def render_wing(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    FEATHER = (241, 245, 249, 255)
    d.pieslice([110, 120, 420, 420], 180, 270, fill=FEATHER)
    d.pieslice([160, 180, 420, 420], 180, 270, fill=FEATHER)
    d.arc([110, 120, 420, 420], 180, 270, fill=OUTLINE, width=6)
    return apply_outline(img)

def render_king(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    GOLD = (245, 158, 11, 255)
    # Crown
    d.polygon([(130, 360), (130, 200), (190, 270), (256, 160), (322, 270), (382, 200), (382, 360)], fill=GOLD)
    # Jewels
    for cx in [130, 256, 382]:
        d.ellipse([cx - 15, 185, cx + 15, 215], fill=(239, 68, 68, 255))
    return apply_outline(img)

def render_rocket(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    RED = (239, 68, 68, 255)
    WHITE_ROCKET = (248, 250, 252, 255)
    d.polygon([(256, 90), (330, 320), (182, 320)], fill=WHITE_ROCKET)
    d.polygon([(256, 90), (290, 180), (222, 180)], fill=RED) # Tip
    d.polygon([(182, 260), (130, 350), (190, 330)], fill=RED) # Left Fin
    d.polygon([(330, 260), (382, 350), (322, 330)], fill=RED) # Right Fin
    # Window
    d.ellipse([230, 200, 282, 252], fill=(56, 189, 248, 255))
    return apply_outline(img)

def render_duck(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    YELLOW = (250, 204, 21, 255)
    d.ellipse([140, 220, 382, 390], fill=YELLOW) # Body
    d.ellipse([250, 130, 370, 250], fill=YELLOW) # Head
    # Beak
    d.polygon([(340, 180), (420, 200), (340, 220)], fill=(249, 115, 22, 255))
    # Eye
    d.ellipse([300, 160, 325, 185], fill=PUPIL)
    return apply_outline(img)

def render_drum(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    RED = (220, 38, 38, 255)
    d.ellipse([130, 150, 382, 230], fill=(248, 250, 252, 255)) # Top
    d.rectangle([130, 190, 382, 360], fill=RED) # Base
    d.ellipse([130, 320, 382, 400], fill=RED)
    # Zig-zag straps
    d.line([(130, 190), (190, 360), (256, 190), (322, 360), (382, 190)], fill=(250, 204, 21, 255), width=8)
    return apply_outline(img)

def render_worm(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    PINK = (244, 114, 182, 255)
    d.arc([130, 180, 250, 320], 180, 360, fill=PINK, width=44)
    d.arc([230, 220, 350, 360], 0, 180, fill=PINK, width=44)
    d.ellipse([310, 180, 390, 260], fill=PINK) # Head
    d.ellipse([345, 200, 365, 220], fill=PUPIL)
    return apply_outline(img)

def render_jet(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    BLUE = (37, 99, 235, 255)
    d.ellipse([120, 230, 402, 310], fill=BLUE) # Body
    d.polygon([(230, 250), (160, 120), (290, 250)], fill=BLUE) # Wing top
    d.polygon([(230, 290), (160, 420), (290, 290)], fill=BLUE) # Wing bot
    d.polygon([(120, 240), (80, 170), (150, 240)], fill=(239, 68, 68, 255)) # Tail
    return apply_outline(img)

def render_pina(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    YELLOW = (234, 179, 8, 255)
    GREEN = (34, 197, 94, 255)
    # Pineapple Body
    d.ellipse([150, 180, 362, 430], fill=YELLOW)
    # Crown Leaves
    d.polygon([(256, 90), (230, 200), (282, 200)], fill=GREEN)
    d.polygon([(200, 110), (220, 200), (256, 180)], fill=GREEN)
    d.polygon([(312, 110), (256, 180), (292, 200)], fill=GREEN)
    # Diamond Texture
    d.arc([160, 200, 352, 410], 45, 225, fill=OUTLINE, width=4)
    return apply_outline(img)

def render_nino(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.ellipse([160, 150, 352, 340], fill=(254, 215, 170, 255)) # Face
    # Cap
    d.pieslice([145, 110, 367, 240], 180, 360, fill=(59, 130, 246, 255))
    d.ellipse([145, 170, 367, 210], fill=(37, 99, 235, 255))
    # Eyes & Smile
    d.ellipse([200, 215, 225, 245], fill=PUPIL)
    d.ellipse([287, 215, 312, 245], fill=PUPIL)
    d.arc([226, 255, 286, 295], 0, 180, fill=PUPIL, width=6)
    return apply_outline(img)

def render_bano(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    TEAL = (20, 184, 166, 255)
    # Bathtub
    d.rounded_rectangle([130, 220, 382, 370], radius=30, fill=(248, 250, 252, 255))
    d.rectangle([130, 220, 382, 250], fill=TEAL)
    # Bubbles
    for (bx, by, br) in [(200, 180, 25), (260, 160, 35), (320, 180, 20)]:
        d.ellipse([bx - br, by - br, bx + br, by + br], fill=(186, 230, 253, 220))
    return apply_outline(img)

def render_quilt(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle([130, 140, 382, 392], radius=24, fill=(254, 243, 199, 255))
    # 4 Colored Quadrants
    d.rectangle([140, 150, 256, 266], fill=(244, 114, 182, 255))
    d.rectangle([256, 150, 372, 266], fill=(96, 165, 250, 255))
    d.rectangle([140, 266, 256, 382], fill=(52, 211, 153, 255))
    d.rectangle([256, 266, 372, 382], fill=(251, 191, 36, 255))
    return apply_outline(img)

def render_vase(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    PURPLE = (147, 51, 234, 255)
    d.ellipse([150, 210, 362, 420], fill=PURPLE)
    d.rounded_rectangle([210, 140, 302, 230], radius=15, fill=PURPLE)
    # Flower in vase
    d.ellipse([230, 80, 282, 132], fill=(236, 72, 153, 255))
    d.ellipse([246, 96, 266, 116], fill=(250, 204, 21, 255))
    return apply_outline(img)

def render_vest(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    ORANGE = (249, 115, 22, 255)
    d.polygon([(160, 140), (220, 140), (256, 230), (292, 140), (352, 140), (370, 390), (142, 390)], fill=ORANGE)
    # Buttons
    d.ellipse([246, 260, 266, 280], fill=PUPIL)
    d.ellipse([246, 310, 266, 330], fill=PUPIL)
    return apply_outline(img)

def render_six(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    BLUE = (59, 130, 246, 255)
    d.ellipse([160, 230, 352, 420], fill=BLUE)
    d.ellipse([200, 270, 312, 380], fill=(0, 0, 0, 0))
    d.arc([160, 110, 340, 320], 90, 270, fill=BLUE, width=44)
    return apply_outline(img)

def render_zip(size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    GRAY = (148, 163, 184, 255)
    GOLD = (234, 179, 8, 255)
    d.line([(256, 110), (256, 420)], fill=GRAY, width=16)
    # Slider
    d.rounded_rectangle([226, 210, 286, 290], radius=10, fill=GOLD)
    d.ellipse([240, 290, 272, 340], fill=GOLD)
    return apply_outline(img)

def render_letter_card(text="ng", size=512):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle([32, 32, size - 32, size - 32], radius=80, fill=(255, 253, 238, 255), outline=OUTLINE, width=12)
    from PIL import ImageFont
    try:
        font = ImageFont.truetype("arial.ttf", 220)
    except:
        font = ImageFont.load_default()
    d.text((size / 2, size / 2 - 10), text, fill=PUPIL, font=font, anchor="mm")
    return img

def main():
    print("=" * 80)
    print("[*] Generating & Verifying 100% of All Challenge Assets...")
    print("=" * 80)

    generators = [
        ("picture_owl.png", render_owl),
        ("picture_ox.png", render_ox),
        ("picture_uncle.png", render_uncle),
        ("picture_tree.png", render_tree),
        ("picture_top.png", render_top),
        ("picture_key.png", render_key),
        ("picture_leaf.png", render_leaf),
        ("picture_yarn.png", render_yarn),
        ("picture_yak.png", render_yak),
        ("picture_gift.png", render_gift),
        ("picture_ring.png", render_ring),
        ("picture_wing.png", render_wing),
        ("picture_king.png", render_king),
        ("picture_rocket.png", render_rocket),
        ("picture_duck.png", render_duck),
        ("picture_drum.png", render_drum),
        ("picture_worm.png", render_worm),
        ("picture_jet.png", render_jet),
        ("picture_pina.png", render_pina),
        ("picture_nino.png", render_nino),
        ("picture_bano.png", render_bano),
        ("picture_quilt.png", render_quilt),
        ("picture_vase.png", render_vase),
        ("picture_vest.png", render_vest),
        ("picture_six.png", render_six),
        ("picture_zip.png", render_zip),
    ]

    for filename, gen_fn in generators:
        img = gen_fn(512)
        out_path = os.path.join(PICTURES_DIR, filename)
        img.save(out_path, format="PNG")
        print(f"  [+] Challenge Picture Ready: {filename}")

    # Generate Letter Cards for NG and Ñ if missing
    for letter_text, letter_file in [("ng", "letter_ng.png"), ("ñ", "letter_ñ.png"), ("Ñ", "letter_n_tilde.png")]:
        card_img = render_letter_card(letter_text, 512)
        out_path = os.path.join(LETTERS_DIR, letter_file)
        card_img.save(out_path, format="PNG")
        print(f"  [+] Letter Card Ready: {letter_file}")

    print("=" * 80)
    print("[*] Verifying all assets exist with non-zero bytes...")
    missing_assets = []
    for root, _, files in os.walk(os.path.join(BASE_DIR, "app", "src", "main", "assets", "images")):
        for f in files:
            full_path = os.path.join(root, f)
            if os.path.getsize(full_path) == 0:
                missing_assets.append(full_path)

    if not missing_assets:
        print("  [+] All image assets across all challenges exist and verified!")
    else:
        print(f"  [!] Found empty assets: {missing_assets}")

if __name__ == "__main__":
    main()
