"""
PlayIT Batch Mascot & Avatar Pipeline

Automates the production processing for Lily the Tarsier (7 poses) and
the 6 Companion Animal Avatars once generated via the Duolingo ABC / Imagen toolchain.
Applies:
1. Pure white backdrop removal to clean RGBA
2. Tight subject bounding box crop
3. Canvas centering with outline padding (512x512)
4. Continuous #2D373E pediatric sticker outline
5. Dual-deployment to Android production directories:
   - app/src/main/assets/images/mascot/
   - app/src/main/assets/images/characters/
"""

import os
import sys
from PIL import Image, ImageFilter

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MASCOT_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images", "mascot")
CHAR_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images", "characters")
OUTLINE_COLOR = (45, 55, 62, 255)  # #2D373E Slate Brown/Black Outline

# Canonical Asset Definitions
MASCOT_POSES = [
    ("lily_idle", "Standing calmly facing forward with gentle resting smile"),
    ("lily_waving", "Cheerful one-paw welcoming wave"),
    ("lily_listening", "Attentive listening pose with head tilted and paw near ear"),
    ("lily_pointing", "Friendly directional gaze pointing to the right"),
    ("lily_encouraging", "Warm empathetic lean forward with supportive gesture"),
    ("lily_thinking", "Curious inquisitive posture with paw touching chin"),
    ("lily_celebrating", "Explosive joyful double-paw skyward hop with stars"),
]

AVATAR_CHARACTERS = [
    ("avatar_01_cat", "Ginger Cat with cream muzzle, warm emerald eyes, soft paws"),
    ("avatar_02_monkey", "Playful Brown Monkey with peach face, big circular ears, curved tail"),
    ("avatar_03_bunny", "White Bunny with pink inner ears and blushing rosy cheeks"),
    ("avatar_04_bear", "Gentle Teddy Bear with caramel snout and friendly button nose"),
    ("avatar_05_frog", "Bright Lime Treefrog with prominent happy eyes and cheerful smile"),
    ("avatar_06_owl", "Plum-purple and cream Owl with large golden eyes and tiny beak"),
]

def remove_white_backdrop(img: Image.Image, threshold: int = 242) -> Image.Image:
    """Isolates subject by converting white/light backdrop pixels to full transparency."""
    img = img.convert("RGBA")
    data = img.getdata()
    new_data = []
    for r, g, b, a in data:
        if r >= threshold and g >= threshold and b >= threshold:
            new_data.append((255, 255, 255, 0))
        else:
            new_data.append((r, g, b, a))
    img.putdata(new_data)
    return img

def apply_pediatric_outline(image: Image.Image, stroke_width: int = 8) -> Image.Image:
    """Applies clean, continuous dark outline using alpha dilation."""
    alpha = image.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_width * 2 + 1))
    stroke_layer = Image.new("RGBA", image.size, (0, 0, 0, 0))
    stroke_base = Image.new("RGBA", image.size, OUTLINE_COLOR)
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, image)

def process_and_deploy_asset(src_path: str, dest_paths: list, size: int = 512, outline: int = 8):
    """Processes source image into production-ready 512x512 PNG and saves to destination paths."""
    img = Image.open(src_path)
    img = remove_white_backdrop(img)

    bbox = img.getbbox()
    if bbox:
        img = img.crop(bbox)

    inner_size = size - (outline * 4)
    img.thumbnail((inner_size, inner_size), Image.Resampling.LANCZOS)

    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    offset = ((size - img.width) // 2, (size - img.height) // 2)
    canvas.paste(img, offset, img)

    final_asset = apply_pediatric_outline(canvas, stroke_width=outline)

    for dest in dest_paths:
        os.makedirs(os.path.dirname(dest), exist_ok=True)
        final_asset.save(dest, format="PNG")
        print(f" [OK] Deployed -> {dest}")

if __name__ == "__main__":
    print("PlayIT Batch Mascot & Avatar Pipeline initialized.")
    print(f"Mascot Poses: {len(MASCOT_POSES)} targets.")
    print(f"Companion Avatars: {len(AVATAR_CHARACTERS)} targets.")
