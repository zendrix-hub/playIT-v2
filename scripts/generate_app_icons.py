"""
PlayIT Official App Icon Suite Generator
Generates the Headspace-inspired Tarsier Dome App Logo:
1. 512x512 Master Store Icon (ic_launcher_playstore.png)
2. Adaptive Icon Foreground & Background (ic_launcher_foreground.png, ic_launcher_background.png)
3. Full Mipmap Density Suite: mdpi (48), hdpi (72), xhdpi (96), xxhdpi (144), xxxhdpi (192)
4. Round and Squircle Icons
"""

import os
from PIL import Image, ImageDraw, ImageFilter

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES_DIR = os.path.join(BASE_DIR, "app", "src", "main", "res")

OUTLINE = (45, 55, 62, 255)       # #2D373E
OUTLINE_WIDTH = 12

MANGO = (250, 123, 40, 255)       # #FA7B28
MANGO_TOP = (255, 145, 50, 255)
EAR_INNER = (255, 175, 120, 255)  # #FFAF78
CREAM_PATCH = (255, 238, 215, 255)# #FFEED7
PUPIL = (31, 58, 61, 255)         # #1F3A3D
WHITE = (255, 255, 255, 255)
ROSY_CHEEK = (255, 170, 185, 255)
TONGUE = (244, 63, 94, 255)       # #F43F5E

SUNNY_BG_TOP = (255, 253, 238, 255) # #FFFDEE
SUNNY_BG_BOT = (254, 218, 106, 255) # #FEDA6A

def apply_outline(fill_img, stroke_w=OUTLINE_WIDTH, stroke_color=OUTLINE):
    alpha = fill_img.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_w * 2 + 1))
    stroke_base = Image.new("RGBA", fill_img.size, stroke_color)
    stroke_layer = Image.new("RGBA", fill_img.size, (0, 0, 0, 0))
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, fill_img)

def create_sunny_background(size=512):
    """Generates the soft sunny radial/linear background gradient."""
    bg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(bg)
    for y in range(size):
        ratio = y / float(size - 1)
        r = int(SUNNY_BG_TOP[0] * (1 - ratio) + SUNNY_BG_BOT[0] * ratio)
        g = int(SUNNY_BG_TOP[1] * (1 - ratio) + SUNNY_BG_BOT[1] * ratio)
        b = int(SUNNY_BG_TOP[2] * (1 - ratio) + SUNNY_BG_BOT[2] * ratio)
        d.line([(0, y), (size - 1, y)], fill=(r, g, b, 255))
    return bg

def create_headspace_tarsier_character(size=512, dome_top_offset=0):
    """Renders the Headspace-style geometric Tarsier character."""
    char_img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(char_img)

    # 1. Ears rising from dome
    ear_w = 68
    ear_h = 75
    ear_y = 185 + dome_top_offset
    # Left Ear
    d.ellipse([82 - ear_w, ear_y - ear_h, 82 + ear_w, ear_y + ear_h], fill=MANGO)
    d.ellipse([82 - ear_w * 0.65, ear_y - ear_h * 0.65, 82 + ear_w * 0.65, ear_y + ear_h * 0.65], fill=EAR_INNER)
    # Right Ear
    d.ellipse([size - 82 - ear_w, ear_y - ear_h, size - 82 + ear_w, ear_y + ear_h], fill=MANGO)
    d.ellipse([size - 82 - ear_w * 0.65, ear_y - ear_h * 0.65, size - 82 + ear_w * 0.65, ear_y + ear_h * 0.65], fill=EAR_INNER)

    # 2. Main Headspace Tarsier Dome Head
    dome_top_y = 195 + dome_top_offset
    d.ellipse([size // 2 - 230, dome_top_y, size // 2 + 230, size + 160 + dome_top_offset], fill=MANGO)

    # 3. Soft Cream Eye Patches
    patch_r = 65
    patch_y = 302 + dome_top_offset
    d.ellipse([size // 2 - 85 - patch_r, patch_y - patch_r, size // 2 - 85 + patch_r, patch_y + patch_r], fill=CREAM_PATCH)
    d.ellipse([size // 2 + 85 - patch_r, patch_y - patch_r, size // 2 + 85 + patch_r, patch_y + patch_r], fill=CREAM_PATCH)

    # 4. Cheerful Rosy Cheek Blush
    d.ellipse([size // 2 - 170, patch_y + 40, size // 2 - 118, patch_y + 75], fill=ROSY_CHEEK)
    d.ellipse([size // 2 + 118, patch_y + 40, size // 2 + 170, patch_y + 75], fill=ROSY_CHEEK)

    # 5. Giant Glossy Catchlight Tarsier Eyes
    pupil_r = 38
    # Left Pupil
    d.ellipse([size // 2 - 85 - pupil_r, patch_y - pupil_r, size // 2 - 85 + pupil_r, patch_y + pupil_r], fill=PUPIL)
    d.ellipse([size // 2 - 93 - 12, patch_y - 18, size // 2 - 93 + 12, patch_y + 8], fill=WHITE)
    d.ellipse([size // 2 - 72 - 6, patch_y + 12, size // 2 - 72 + 6, patch_y + 24], fill=WHITE)

    # Right Pupil
    d.ellipse([size // 2 + 85 - pupil_r, patch_y - pupil_r, size // 2 + 85 + pupil_r, patch_y + pupil_r], fill=PUPIL)
    d.ellipse([size // 2 + 77 - 12, patch_y - 18, size // 2 + 77 + 12, patch_y + 8], fill=WHITE)
    d.ellipse([size // 2 + 98 - 6, patch_y + 12, size // 2 + 98 + 6, patch_y + 24], fill=WHITE)

    # 6. Minimalist Nose Dot
    d.ellipse([size // 2 - 5, patch_y + 35, size // 2 + 5, patch_y + 45], fill=PUPIL)

    # 7. Cheerful Open Smile with Tongue
    d.pieslice([size // 2 - 38, patch_y + 52, size // 2 + 38, patch_y + 105], 0, 180, fill=PUPIL)
    d.pieslice([size // 2 - 26, patch_y + 74, size // 2 + 26, patch_y + 105], 0, 180, fill=TONGUE)

    # 8. Apply unified 4-Benchmark continuous outline
    char_outlined = apply_outline(char_img, stroke_w=10)
    return char_outlined

def create_master_squircle_icon(size=512):
    """Creates the complete master squircle app icon."""
    bg = create_sunny_background(size)

    mask = Image.new("L", (size, size), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.rounded_rectangle([16, 16, size - 16, size - 16], radius=115, fill=255)

    squircle = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    squircle.paste(bg, (0, 0), mask)

    # Character masked to squircle
    character = create_headspace_tarsier_character(size, dome_top_offset=0)
    char_masked = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    char_masked.paste(character, (0, 0), mask)

    # Composite
    final_icon = Image.alpha_composite(squircle, char_masked)

    # 3D Gummy Depth Shadow on bottom
    depth_layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d_depth = ImageDraw.Draw(depth_layer)
    d_depth.rounded_rectangle([16, size - 46, size - 16, size - 16], radius=115, fill=(0, 0, 0, 35))
    final_icon = Image.alpha_composite(final_icon, depth_layer)

    # Final Outer Bevel Border
    d_final = ImageDraw.Draw(final_icon)
    d_final.rounded_rectangle([16, 16, size - 16, size - 16], radius=115, outline=OUTLINE, width=12)
    return final_icon

def create_round_launcher_icon(size=512):
    """Creates the circular launcher icon."""
    bg = create_sunny_background(size)

    mask = Image.new("L", (size, size), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.ellipse([16, 16, size - 16, size - 16], fill=255)

    round_bg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    round_bg.paste(bg, (0, 0), mask)

    character = create_headspace_tarsier_character(size, dome_top_offset=10)
    char_masked = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    char_masked.paste(character, (0, 0), mask)

    final_icon = Image.alpha_composite(round_bg, char_masked)

    d_final = ImageDraw.Draw(final_icon)
    d_final.ellipse([16, 16, size - 16, size - 16], outline=OUTLINE, width=12)
    return final_icon

def create_adaptive_foreground(size=512):
    """Creates the adaptive icon foreground centered inside the safe zone (72%)."""
    fg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    char = create_headspace_tarsier_character(size, dome_top_offset=0)
    
    # Scale to 80% to fit within the Android adaptive circle safe-zone
    w, h = int(size * 0.80), int(size * 0.80)
    scaled_char = char.resize((w, h), Image.Resampling.LANCZOS)
    offset_x = (size - w) // 2
    offset_y = (size - h) // 2 + 20
    fg.paste(scaled_char, (offset_x, offset_y), scaled_char)
    return fg

def main():
    print("=" * 80)
    print("[*] Generating Reworked Headspace Tarsier App Icon Suite...")
    print("=" * 80)

    master = create_master_squircle_icon(512)
    round_icon = create_round_launcher_icon(512)
    adaptive_fg = create_adaptive_foreground(512)
    adaptive_bg = create_sunny_background(512)

    # 1. Master Play Store Icon
    master_path = os.path.join(RES_DIR, "drawable-xxxhdpi", "ic_launcher_playstore.png")
    master.save(master_path, format="PNG")
    print(f"  [+] Saved Play Store Master: {master_path}")

    # 2. Adaptive Foreground & Background
    fg_path = os.path.join(RES_DIR, "drawable-xxxhdpi", "ic_launcher_foreground.png")
    bg_path = os.path.join(RES_DIR, "drawable-xxxhdpi", "ic_launcher_background.png")
    adaptive_fg.save(fg_path, format="PNG")
    adaptive_bg.save(bg_path, format="PNG")
    print("  [+] Saved Adaptive Icon Foreground & Background")

    # 3. Density Mipmaps
    densities = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192
    }

    for folder, dim in densities.items():
        folder_path = os.path.join(RES_DIR, folder)
        os.makedirs(folder_path, exist_ok=True)

        scaled_master = master.resize((dim, dim), Image.Resampling.LANCZOS)
        scaled_round = round_icon.resize((dim, dim), Image.Resampling.LANCZOS)
        scaled_fg = adaptive_fg.resize((dim, dim), Image.Resampling.LANCZOS)
        scaled_bg = adaptive_bg.resize((dim, dim), Image.Resampling.LANCZOS)

        scaled_master.save(os.path.join(folder_path, "ic_launcher.png"), format="PNG")
        scaled_round.save(os.path.join(folder_path, "ic_launcher_round.png"), format="PNG")
        scaled_fg.save(os.path.join(folder_path, "ic_launcher_foreground.png"), format="PNG")
        scaled_bg.save(os.path.join(folder_path, "ic_launcher_background.png"), format="PNG")
        print(f"  [+] Generated {folder} ({dim}x{dim})")

    print("=" * 80)
    print("[*] Official PlayIT App Icon Suite successfully updated!")

if __name__ == "__main__":
    main()
