"""
PlayIT Android App Icon Generator
Creates:
1. Full 512x512 Master App Icon (for legacy launcher & Play Store)
2. 512x512 Adaptive Icon Foreground & Background (for Android 8.0+ Adaptive Icons)
3. Mipmap PNGs for all densities: mdpi (48), hdpi (72), xhdpi (96), xxhdpi (144), xxxhdpi (192)
"""

import os
from PIL import Image, ImageDraw, ImageFilter, ImageFont

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES_DIR = os.path.join(BASE_DIR, "app", "src", "main", "res")

OUTLINE = (45, 55, 62, 255)       # #2D373E
OUTLINE_WIDTH = 12

MANGO = (250, 123, 40, 255)       # #FA7B28
MANGO_DARK = (217, 95, 20, 255)
MANGO_SHADOW = (180, 70, 10, 255)
EAR_INNER = (255, 175, 120, 255)  # #FFAF78
CREAM_PATCH = (255, 235, 205, 255)# #FFEBCD
PUPIL = (31, 58, 61, 255)         # #1F3A3D
WHITE = (255, 255, 255, 255)
ROSY_CHEEK = (254, 180, 190, 255)
TONGUE = (244, 63, 94, 255)       # #F43F5E
GOLD_STAR = (255, 215, 0, 255)
GOLD_STAR_DARK = (230, 180, 0, 255)
SKY_TOP = (125, 211, 252, 255)    # #7DD3FC
SKY_BOTTOM = (2, 132, 199, 255)   # #0284C7
UBE = (139, 92, 246, 255)
LEAF = (34, 197, 94, 255)

def apply_outline(fill_img, stroke_w=OUTLINE_WIDTH, stroke_color=OUTLINE):
    alpha = fill_img.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_w * 2 + 1))
    stroke_base = Image.new("RGBA", fill_img.size, stroke_color)
    stroke_layer = Image.new("RGBA", fill_img.size, (0, 0, 0, 0))
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, fill_img)

def draw_star(draw, center, radius_outer, radius_inner, fill):
    import math
    points = []
    for i in range(10):
        r = radius_outer if i % 2 == 0 else radius_inner
        angle = i * math.pi / 5 - math.pi / 2
        x = center[0] + r * math.cos(angle)
        y = center[1] + r * math.sin(angle)
        points.append((x, y))
    draw.polygon(points, fill=fill)

def create_lily_character():
    """Draws Lily the Tarsier with happy expression, paws, and playful star."""
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)

    # 1. Big Tarsier Ears
    # Left Ear
    d.ellipse([40, 110, 190, 260], fill=MANGO)
    d.ellipse([65, 135, 165, 235], fill=EAR_INNER)
    # Right Ear
    d.ellipse([322, 110, 472, 260], fill=MANGO)
    d.ellipse([347, 135, 447, 235], fill=EAR_INNER)

    # 2. Main Head Dome
    d.ellipse([96, 130, 416, 450], fill=MANGO)

    # 3. Big Cream Eye Patches
    d.ellipse([140, 200, 245, 305], fill=CREAM_PATCH)
    d.ellipse([267, 200, 372, 305], fill=CREAM_PATCH)

    # 4. Cheerful Rosy Cheek Blush
    d.ellipse([120, 305, 175, 345], fill=ROSY_CHEEK)
    d.ellipse([337, 305, 392, 345], fill=ROSY_CHEEK)

    # 5. Big Expressive Glossy Eyes (Wink on left, huge shiny eye on right or double glossy eyes)
    # Left Eye (Huge Glossy Eye)
    d.ellipse([160, 220, 225, 285], fill=PUPIL)
    d.ellipse([170, 228, 192, 250], fill=WHITE)   # Main catchlight
    d.ellipse([198, 255, 212, 269], fill=WHITE)   # Secondary catchlight

    # Right Eye (Huge Glossy Eye)
    d.ellipse([287, 220, 352, 285], fill=PUPIL)
    d.ellipse([297, 228, 319, 250], fill=WHITE)   # Main catchlight
    d.ellipse([325, 255, 339, 269], fill=WHITE)   # Secondary catchlight

    # 6. Cute Open Happy Smile with Tongue
    d.pieslice([220, 305, 292, 365], 0, 180, fill=PUPIL)
    d.pieslice([232, 328, 280, 365], 0, 180, fill=TONGUE)

    # 7. Cute Little Tarsier Paws peeking up
    d.ellipse([135, 410, 195, 470], fill=MANGO)
    d.ellipse([317, 410, 377, 470], fill=MANGO)

    # Little paw pads / fingers
    d.ellipse([140, 400, 160, 420], fill=EAR_INNER)
    d.ellipse([162, 395, 182, 415], fill=EAR_INNER)
    d.ellipse([184, 402, 204, 422], fill=EAR_INNER)

    d.ellipse([308, 402, 328, 422], fill=EAR_INNER)
    d.ellipse([330, 395, 350, 415], fill=EAR_INNER)
    d.ellipse([352, 400, 372, 420], fill=EAR_INNER)

    # 8. Playful Phonics Star near Right Ear
    draw_star(d, (395, 105), 42, 20, GOLD_STAR)

    # Apply unified continuous stroke
    outlined = apply_outline(img, stroke_w=12)

    # Add inner details (smile border, star detail)
    d_out = ImageDraw.Draw(outlined)
    draw_star(d_out, (395, 105), 24, 11, WHITE)
    return outlined

def create_background_gradient():
    """Generates a smooth, vibrant Sky-to-Cyan background gradient."""
    bg = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(bg)
    for y in range(512):
        ratio = y / 511.0
        r = int(SKY_TOP[0] * (1 - ratio) + SKY_BOTTOM[0] * ratio)
        g = int(SKY_TOP[1] * (1 - ratio) + SKY_BOTTOM[1] * ratio)
        b = int(SKY_TOP[2] * (1 - ratio) + SKY_BOTTOM[2] * ratio)
        d.line([(0, y), (511, y)], fill=(r, g, b, 255))
    return bg

def create_master_icon():
    """Composites background squircle + Lily for full launcher icon."""
    bg = create_background_gradient()
    
    # Create Rounded Squircle Mask
    mask = Image.new("L", (512, 512), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.rounded_rectangle([16, 16, 496, 496], radius=110, fill=255)
    
    # Rounded background with 3D Gummy depth
    rounded_bg = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    rounded_bg.paste(bg, (0, 0), mask)
    
    # Draw Depth Shadow on squircle bottom
    depth_shadow = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d_depth = ImageDraw.Draw(depth_shadow)
    d_depth.rounded_rectangle([16, 460, 496, 496], radius=110, fill=(0, 0, 0, 60))
    rounded_bg = Image.alpha_composite(rounded_bg, depth_shadow)
    
    # Draw Squircle Outline
    d_bg = ImageDraw.Draw(rounded_bg)
    d_bg.rounded_rectangle([16, 16, 496, 496], radius=110, outline=OUTLINE, width=12)

    # Composite Lily on top
    lily = create_lily_character()
    final_icon = Image.alpha_composite(rounded_bg, lily)
    return final_icon

def create_round_icon():
    """Composites circular launcher icon."""
    bg = create_background_gradient()
    
    mask = Image.new("L", (512, 512), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.ellipse([16, 16, 496, 496], fill=255)
    
    round_bg = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    round_bg.paste(bg, (0, 0), mask)
    
    d_bg = ImageDraw.Draw(round_bg)
    d_bg.ellipse([16, 16, 496, 496], outline=OUTLINE, width=12)
    
    lily = create_lily_character()
    final_icon = Image.alpha_composite(round_bg, lily)
    return final_icon

def create_adaptive_foreground():
    """Draws Lily centered within the 66-72% safe zone for Android adaptive icons."""
    fg = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    lily = create_lily_character()
    # Scale to 74% and center
    w, h = int(512 * 0.76), int(512 * 0.76)
    scaled_lily = lily.resize((w, h), Image.Resampling.LANCZOS)
    offset_x = (512 - w) // 2
    offset_y = (512 - h) // 2 + 10
    fg.paste(scaled_lily, (offset_x, offset_y), scaled_lily)
    return fg

def main():
    print("=" * 80)
    print("[*] Generating PlayIT Custom 4-Benchmark App Icons...")
    print("=" * 80)

    master = create_master_icon()
    round_icon = create_round_icon()
    lily_fg = create_adaptive_foreground()
    bg = create_background_gradient()

    # Save 512x512 Store & Preview Icons
    master_path = os.path.join(RES_DIR, "drawable-xxxhdpi", "ic_launcher_playstore.png")
    master.save(master_path, format="PNG")
    print(f"  [+] Saved Play Store Master: {master_path}")

    # Adaptive Icon Foreground & Background (drawables)
    fg_path = os.path.join(RES_DIR, "drawable-xxxhdpi", "ic_launcher_foreground.png")
    bg_path = os.path.join(RES_DIR, "drawable-xxxhdpi", "ic_launcher_background.png")
    lily_fg.save(fg_path, format="PNG")
    bg.save(bg_path, format="PNG")
    print(f"  [+] Saved Adaptive Foreground & Background")

    # Generate Mipmap densities
    densities = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192
    }

    for folder, size in densities.items():
        folder_path = os.path.join(RES_DIR, folder)
        os.makedirs(folder_path, exist_ok=True)

        scaled_master = master.resize((size, size), Image.Resampling.LANCZOS)
        scaled_round = round_icon.resize((size, size), Image.Resampling.LANCZOS)
        scaled_fg = lily_fg.resize((size, size), Image.Resampling.LANCZOS)
        scaled_bg = bg.resize((size, size), Image.Resampling.LANCZOS)

        scaled_master.save(os.path.join(folder_path, "ic_launcher.png"), format="PNG")
        scaled_round.save(os.path.join(folder_path, "ic_launcher_round.png"), format="PNG")
        scaled_fg.save(os.path.join(folder_path, "ic_launcher_foreground.png"), format="PNG")
        scaled_bg.save(os.path.join(folder_path, "ic_launcher_background.png"), format="PNG")
        print(f"  [+] Generated {folder} ({size}x{size})")

    # Generate Adaptive XML mipmap-anydpi-v26
    anydpi_dir = os.path.join(RES_DIR, "mipmap-anydpi-v26")
    os.makedirs(anydpi_dir, exist_ok=True)

    adaptive_xml = """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@mipmap/ic_launcher_background" />
    <foreground android:drawable="@mipmap/ic_launcher_foreground" />
</adaptive-icon>
"""
    with open(os.path.join(anydpi_dir, "ic_launcher.xml"), "w", encoding="utf-8") as f:
        f.write(adaptive_xml)
    with open(os.path.join(anydpi_dir, "ic_launcher_round.xml"), "w", encoding="utf-8") as f:
        f.write(adaptive_xml)

    print("  [+] Generated mipmap-anydpi-v26/ic_launcher.xml & ic_launcher_round.xml")
    print("=" * 80)
    print("[*] All Android App Icons successfully generated!")

if __name__ == "__main__":
    main()
