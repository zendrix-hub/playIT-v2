"""
PlayIT Official App Icon Suite — Exact Splash Screen Headspace Tarsier Design
Generates:
1. 512x512 Master Play Store Icon (ic_launcher_playstore.png)
2. Android Adaptive Icon Foreground & Background (ic_launcher_foreground.png, ic_launcher_background.png)
3. Full Density Suite: mipmap-mdpi (48), hdpi (72), xhdpi (96), xxhdpi (144), xxxhdpi (192)
4. Round & Squircle Launcher Icons
Features:
- Exact geometric dome, ears, cream patches, and peaceful curved line features from SplashScreen.kt
- Soft warm sunny cream gradient background (#FFF9E6 -> #FFF3D9)
- Perfectly balanced, uncluttered, world-class app mark
"""

import os
from PIL import Image, ImageDraw

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES_DIR = os.path.join(BASE_DIR, "app", "src", "main", "res")
TOOLS_DIR = os.path.join(BASE_DIR, "tools")

# Exact color tokens from SplashScreen.kt
COLOR_MANGO = (250, 123, 40, 255)       # #FA7B28
COLOR_EAR_INNER = (255, 175, 120, 255)  # #FFAF78
COLOR_CREAM_PATCH = (255, 235, 205, 255)# #FFEBCD
COLOR_DARK_LINE = (45, 55, 62, 255)     # #2D373E

# Soft warm sunny cream backdrop
BG_TOP = (255, 250, 235, 255)           # #FFFAEB
BG_BOT = (255, 240, 212, 255)           # #FFF0D4

def create_background_layer(size=512):
    """Creates soft warm sunny cream gradient canvas."""
    bg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(bg)
    for y in range(size):
        ratio = y / float(size - 1)
        r = int(BG_TOP[0] * (1 - ratio) + BG_BOT[0] * ratio)
        g = int(BG_TOP[1] * (1 - ratio) + BG_BOT[1] * ratio)
        b = int(BG_TOP[2] * (1 - ratio) + BG_BOT[2] * ratio)
        d.line([(0, y), (size - 1, y)], fill=(r, g, b, 255))
    return bg

def draw_tarsier_character(size=512, scale=1.0, offset_y=0):
    """
    Renders the exact Headspace Tarsier character from SplashScreen.kt:
    - Mango ears with peach inner lobes
    - Giant smooth mango dome head
    - Soft cream eye patches
    - Peaceful curved line eyes (∪ ∪)
    - Gentle smiling line arc (⌣)
    """
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)

    w = size
    h = size

    cx = w / 2.0
    dome_top_y = (145 + offset_y) * scale
    dome_rx = (w * 0.72) * scale
    dome_ry = (h * 0.76) * scale

    # 1. Ears
    ear_w = (w * 0.16) * scale
    ear_h = (w * 0.18) * scale
    left_ear_cx = (w * 0.22) * scale + (cx - cx * scale)
    right_ear_cx = (w * 0.78) * scale + (cx - cx * scale)
    ear_cy = (185 + offset_y) * scale

    # Left Ear (Outer + Inner)
    d.ellipse([left_ear_cx - ear_w, ear_cy - ear_h, left_ear_cx + ear_w, ear_cy + ear_h], fill=COLOR_MANGO)
    d.ellipse([left_ear_cx - ear_w * 0.62, ear_cy - ear_h * 0.65, left_ear_cx + ear_w * 0.62, ear_cy + ear_h * 0.65], fill=COLOR_EAR_INNER)

    # Right Ear (Outer + Inner)
    d.ellipse([right_ear_cx - ear_w, ear_cy - ear_h, right_ear_cx + ear_w, ear_cy + ear_h], fill=COLOR_MANGO)
    d.ellipse([right_ear_cx - ear_w * 0.62, ear_cy - ear_h * 0.65, right_ear_cx + ear_w * 0.62, ear_cy + ear_h * 0.65], fill=COLOR_EAR_INNER)

    # 2. Main Head Dome
    d.ellipse([cx - dome_rx, dome_top_y, cx + dome_rx, dome_top_y + dome_ry * 2], fill=COLOR_MANGO)

    # 3. Soft Cream Eye Patches
    patch_y = (260 + offset_y) * scale
    patch_spacing = (w * 0.13) * scale
    patch_r = (w * 0.092) * scale

    d.ellipse([cx - patch_spacing - patch_r, patch_y - patch_r, cx - patch_spacing + patch_r, patch_y + patch_r], fill=COLOR_CREAM_PATCH)
    d.ellipse([cx + patch_spacing - patch_r, patch_y - patch_r, cx + patch_spacing + patch_r, patch_y + patch_r], fill=COLOR_CREAM_PATCH)

    # 4. Peaceful Curved Line Eyes (∪ ∪)
    line_w = int(12 * scale)
    eye_arc_w = (w * 0.11) * scale
    eye_arc_h = (w * 0.052) * scale

    # Left Eye Arc
    d.arc([
        cx - patch_spacing - eye_arc_w / 2.0,
        patch_y - eye_arc_h / 2.0,
        cx - patch_spacing + eye_arc_w / 2.0,
        patch_y + eye_arc_h / 2.0
    ], start=10, end=170, fill=COLOR_DARK_LINE, width=line_w)

    # Right Eye Arc
    d.arc([
        cx + patch_spacing - eye_arc_w / 2.0,
        patch_y - eye_arc_h / 2.0,
        cx + patch_spacing + eye_arc_w / 2.0,
        patch_y + eye_arc_h / 2.0
    ], start=10, end=170, fill=COLOR_DARK_LINE, width=line_w)

    # 5. Gentle Smile Arc (⌣)
    mouth_y = patch_y + (48 * scale)
    mouth_w = (w * 0.16) * scale
    mouth_h = (w * 0.065) * scale

    d.arc([
        cx - mouth_w / 2.0,
        mouth_y - mouth_h / 2.0,
        cx + mouth_w / 2.0,
        mouth_y + mouth_h / 2.0
    ], start=20, end=160, fill=COLOR_DARK_LINE, width=line_w)

    return img

def create_master_icon(size=512):
    """Creates the master squircle store icon."""
    bg = create_background_layer(size)
    char = draw_tarsier_character(size, scale=1.0, offset_y=0)

    # Composite character onto background
    composite = Image.alpha_composite(bg, char)

    # Mask to smooth squircle with subtle continuous outline
    mask = Image.new("L", (size, size), 0)
    d_mask = ImageDraw.Draw(mask)
    d_mask.rounded_rectangle([16, 16, size - 16, size - 16], radius=115, fill=255)

    final_icon = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    final_icon.paste(composite, (0, 0), mask)

    # Outline
    d_final = ImageDraw.Draw(final_icon)
    d_final.rounded_rectangle([16, 16, size - 16, size - 16], radius=115, outline=COLOR_DARK_LINE, width=10)

    return final_icon

def create_round_icon(size=512):
    """Creates the round launcher icon."""
    bg = create_background_layer(size)
    char = draw_tarsier_character(size, scale=0.92, offset_y=20)

    composite = Image.alpha_composite(bg, char)

    mask = Image.new("L", (size, size), 0)
    d_mask = ImageDraw.Draw(mask)
    d_mask.ellipse([16, 16, size - 16, size - 16], fill=255)

    final_icon = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    final_icon.paste(composite, (0, 0), mask)

    d_final = ImageDraw.Draw(final_icon)
    d_final.ellipse([16, 16, size - 16, size - 16], outline=COLOR_DARK_LINE, width=10)

    return final_icon

def create_adaptive_foreground(size=512):
    """Creates the Android 8.0+ adaptive foreground centered in 72% safe-zone."""
    char = draw_tarsier_character(size, scale=0.76, offset_y=75)
    return char

def main():
    print("=" * 80)
    print("[*] Generating Exact Splash-Screen Headspace Tarsier App Icons...")
    print("=" * 80)

    master = create_master_icon(512)
    round_icon = create_round_icon(512)
    adaptive_fg = create_adaptive_foreground(512)
    adaptive_bg = create_background_layer(512)

    # Save to tools/ for inspection
    os.makedirs(TOOLS_DIR, exist_ok=True)
    master.save(os.path.join(TOOLS_DIR, "app_logo_splash_exact.png"), format="PNG")
    print("  [+] Saved preview to tools/app_logo_splash_exact.png")

    # 1. Save Play Store Master
    master_path = os.path.join(RES_DIR, "drawable-xxxhdpi", "ic_launcher_playstore.png")
    master.save(master_path, format="PNG")
    print(f"  [+] Saved Play Store Master: {master_path}")

    # 2. Save Adaptive Icon Foreground & Background
    fg_path = os.path.join(RES_DIR, "drawable-xxxhdpi", "ic_launcher_foreground.png")
    bg_path = os.path.join(RES_DIR, "drawable-xxxhdpi", "ic_launcher_background.png")
    adaptive_fg.save(fg_path, format="PNG")
    adaptive_bg.save(bg_path, format="PNG")

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
    print("[*] Official PlayIT App Icon Suite updated to exact Splash Screen design!")

if __name__ == "__main__":
    main()
