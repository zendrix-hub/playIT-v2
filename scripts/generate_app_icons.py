"""
PlayIT Official App Icon Suite Generator
Generates the Headspace-styled Lily the Tarsier App Logo using the newest mascot:
1. 512x512 Master Store Icon (ic_launcher_playstore.png)
2. Android Adaptive Icon Foreground & Background (ic_launcher_foreground.png, ic_launcher_background.png)
3. Full Mipmap Density Suite: mdpi (48), hdpi (72), xhdpi (96), xxhdpi (144), xxxhdpi (192)
4. Round and Squircle Icons
"""

import os
from PIL import Image, ImageDraw, ImageFilter

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES_DIR = os.path.join(BASE_DIR, "app", "src", "main", "res")
TOOLS_DIR = os.path.join(BASE_DIR, "tools")
MASCOT_PATH = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images", "mascot", "lily_idle.png")

OUTLINE = (45, 55, 62, 255)         # #2D373E
OUTLINE_WIDTH = 12

# Headspace signature warm sunny cream gradient
BG_TOP = (255, 253, 238, 255)       # #FFFDEE (warm sunny morning light)
BG_BOT = (254, 215, 102, 255)       # #FED766 (warm sunny golden glow)

def get_mascot_image():
    """Loads Lily the Tarsier from the latest master asset."""
    if not os.path.exists(MASCOT_PATH):
        raise FileNotFoundError(f"Mascot not found at {MASCOT_PATH}")
    return Image.open(MASCOT_PATH).convert("RGBA")

def create_sunny_background(size=512):
    """Generates the soft warm sunny Headspace gradient background."""
    bg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(bg)
    for y in range(size):
        ratio = y / float(size - 1)
        r = int(BG_TOP[0] * (1 - ratio) + BG_BOT[0] * ratio)
        g = int(BG_TOP[1] * (1 - ratio) + BG_BOT[1] * ratio)
        b = int(BG_TOP[2] * (1 - ratio) + BG_BOT[2] * ratio)
        d.line([(0, y), (size - 1, y)], fill=(r, g, b, 255))
    return bg

def create_master_squircle_icon(size=512):
    """
    Creates the complete master squircle app icon (512x512).
    Features Headspace warm sunny backdrop with the newest Lily the Tarsier mascot,
    tactile pediatric 3D gummy depth shadow, and continuous #2D373E outline.
    """
    lily = get_mascot_image()
    bg = create_sunny_background(size)

    mask = Image.new("L", (size, size), 0)
    d_mask = ImageDraw.Draw(mask)
    d_mask.rounded_rectangle([16, 16, size - 16, size - 16], radius=115, fill=255)

    squircle = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    squircle.paste(bg, (0, 0), mask)

    # Scale Lily so her head, ears, giant luminous eyes, and cute paws are prominently framed
    scale = 0.94
    w = int(size * scale)
    h = int(size * scale)
    scaled_lily = lily.resize((w, h), Image.Resampling.LANCZOS)

    pos_x = (size - w) // 2
    pos_y = 28 # gives balanced top breathing margin for ears

    char_layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    char_layer.paste(scaled_lily, (pos_x, pos_y), scaled_lily)

    # Mask mascot to squircle
    char_masked = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    char_masked.paste(char_layer, (0, 0), mask)
    squircle = Image.alpha_composite(squircle, char_masked)

    # 3D Gummy depth shadow on bottom edge
    depth_layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d_depth = ImageDraw.Draw(depth_layer)
    d_depth.rounded_rectangle([16, size - 48, size - 16, size - 16], radius=115, fill=(0, 0, 0, 35))
    squircle = Image.alpha_composite(squircle, depth_layer)

    # Final continuous #2D373E pediatric border
    d_final = ImageDraw.Draw(squircle)
    d_final.rounded_rectangle([16, 16, size - 16, size - 16], radius=115, outline=OUTLINE, width=OUTLINE_WIDTH)

    return squircle

def create_round_launcher_icon(size=512):
    """
    Creates the circular launcher icon with Lily gracefully framed
    inside the circular mask and framed with continuous #2D373E border.
    """
    lily = get_mascot_image()
    bg = create_sunny_background(size)

    mask = Image.new("L", (size, size), 0)
    d_mask = ImageDraw.Draw(mask)
    d_mask.ellipse([16, 16, size - 16, size - 16], fill=255)

    round_bg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    round_bg.paste(bg, (0, 0), mask)

    scale = 0.88
    w = int(size * scale)
    h = int(size * scale)
    scaled_lily = lily.resize((w, h), Image.Resampling.LANCZOS)

    pos_x = (size - w) // 2
    pos_y = 44

    char_layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    char_layer.paste(scaled_lily, (pos_x, pos_y), scaled_lily)

    char_masked = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    char_masked.paste(char_layer, (0, 0), mask)

    final_icon = Image.alpha_composite(round_bg, char_masked)

    d_final = ImageDraw.Draw(final_icon)
    d_final.ellipse([16, 16, size - 16, size - 16], outline=OUTLINE, width=OUTLINE_WIDTH)
    return final_icon

def create_adaptive_foreground(size=512):
    """
    Creates the Android 8.0+ adaptive icon foreground.
    Per Android adaptive icon specifications, key content must fit inside
    the central 72dp safe zone circle (diameter = 72/108 * size = ~341px).
    Lily is scaled to 0.71 and centered so no launcher mask ever clips her.
    """
    lily = get_mascot_image()
    fg = Image.new("RGBA", (size, size), (0, 0, 0, 0))

    scale = 0.71
    w = int(size * scale)
    h = int(size * scale)
    scaled_lily = lily.resize((w, h), Image.Resampling.LANCZOS)

    pos_x = (size - w) // 2
    pos_y = (size - h) // 2 + 10
    fg.paste(scaled_lily, (pos_x, pos_y), scaled_lily)
    return fg

def main():
    print("=" * 80)
    print("[*] Generating PlayIT Headspace App Icon Suite (New Mascot Edition)...")
    print("=" * 80)

    master = create_master_squircle_icon(512)
    round_icon = create_round_launcher_icon(512)
    adaptive_fg = create_adaptive_foreground(512)
    adaptive_bg = create_sunny_background(512)

    # 1. Master Play Store Icon (512x512)
    master_path = os.path.join(RES_DIR, "drawable-xxxhdpi", "ic_launcher_playstore.png")
    master.save(master_path, format="PNG")
    print(f"  [+] Saved Play Store Master: {master_path}")

    # 2. Adaptive Foreground & Background in drawable-xxxhdpi
    fg_path = os.path.join(RES_DIR, "drawable-xxxhdpi", "ic_launcher_foreground.png")
    bg_path = os.path.join(RES_DIR, "drawable-xxxhdpi", "ic_launcher_background.png")
    adaptive_fg.save(fg_path, format="PNG")
    adaptive_bg.save(bg_path, format="PNG")
    print("  [+] Saved Adaptive Icon Foreground & Background (drawable-xxxhdpi)")

    # 3. Save preview for tools / inspection
    os.makedirs(TOOLS_DIR, exist_ok=True)
    master.save(os.path.join(TOOLS_DIR, "app_logo_splash_exact.png"), format="PNG")
    print("  [+] Saved preview to tools/app_logo_splash_exact.png")

    # 4. Density Mipmaps
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
    print("[*] Official PlayIT App Icon Suite successfully updated with newest mascot!")

if __name__ == "__main__":
    main()
