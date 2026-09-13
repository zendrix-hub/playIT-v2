"""
PlayIT Fooocus Asset Bridge

Bridges AI artwork generated via Fooocus (SDXL) into PlayIT's production asset pipeline:
1. Watches / reads Fooocus outputs (e.g. tools/Fooocus/outputs or specified image)
2. Isolates subject / removes background (with auto white/light backdrop threshold or alpha mask)
3. Applies PlayIT 4-Benchmark Duolingo ABC styling (#2D373E continuous border outline)
4. Saves directly to appropriate Android production directory:
   - app/src/main/assets/images/characters/ (Mascot poses)
   - app/src/main/assets/images/pictures/   (Letter & CVC cards)
   - app/src/main/assets/images/rewards/    (Badges & stars)
"""

import os
import sys
import argparse
from PIL import Image, ImageFilter, ImageOps

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images")
OUTLINE_COLOR = (45, 55, 62, 255)  # #2D373E Slate Outline

DEST_DIRS = {
    "character": os.path.join(ASSETS_DIR, "characters"),
    "picture": os.path.join(ASSETS_DIR, "pictures"),
    "reward": os.path.join(ASSETS_DIR, "rewards"),
}

def remove_white_backdrop(img: Image.Image, threshold: int = 240) -> Image.Image:
    """Removes solid white/light background from SDXL generation to produce transparent RGBA."""
    img = img.convert("RGBA")
    data = img.getdata()
    new_data = []
    for item in data:
        r, g, b, a = item
        # If pixel is near pure white, set transparent
        if r >= threshold and g >= threshold and b >= threshold:
            new_data.append((255, 255, 255, 0))
        else:
            new_data.append(item)
    img.putdata(new_data)
    return img

def apply_duolingo_outline(image: Image.Image, stroke_width: int = 8) -> Image.Image:
    """Applies continuous smooth dark outline using alpha dilation."""
    alpha = image.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_width * 2 + 1))
    stroke_layer = Image.new("RGBA", image.size, (0, 0, 0, 0))
    stroke_base = Image.new("RGBA", image.size, OUTLINE_COLOR)
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, image)

def process_image(src_path: str, dest_path: str, size: int = 512, outline: int = 8, clean_white: bool = True):
    """Crops, transparents, resizes, outlines, and saves production asset."""
    img = Image.open(src_path)
    if clean_white:
        img = remove_white_backdrop(img)
    else:
        img = img.convert("RGBA")

    # Crop bounding box of non-transparent content
    bbox = img.getbbox()
    if bbox:
        img = img.crop(bbox)

    # Scale to canvas
    inner_size = size - (outline * 4)
    img.thumbnail((inner_size, inner_size), Image.Resampling.LANCZOS)

    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    offset = ((size - img.width) // 2, (size - img.height) // 2)
    canvas.paste(img, offset, img)

    if outline > 0:
        canvas = apply_duolingo_outline(canvas, stroke_width=outline)

    os.makedirs(os.path.dirname(dest_path), exist_ok=True)
    canvas.save(dest_path, format="PNG")
    print(f" [OK] Processed Fooocus asset -> {dest_path} ({size}x{size})")

def main():
    parser = argparse.ArgumentParser(description="PlayIT Fooocus Asset Bridge")
    parser.add_argument("--input", type=str, required=True, help="Path to Fooocus generated image or directory")
    parser.add_argument("--type", choices=["character", "picture", "reward"], default="picture", help="Target asset category")
    parser.add_argument("--name", type=str, help="Target asset filename (without .png)")
    parser.add_argument("--size", type=int, default=512, help="Output dimensions (512 or 256)")
    parser.add_argument("--outline", type=int, default=8, help="Duolingo outline thickness")
    parser.add_argument("--keep-bg", action="store_true", help="Do not strip white background")

    args = parser.parse_args()

    dest_folder = DEST_DIRS[args.type]
    os.makedirs(dest_folder, exist_ok=True)

    if os.path.isfile(args.input):
        out_name = (args.name or os.path.splitext(os.path.basename(args.input))[0]) + ".png"
        dest_path = os.path.join(dest_folder, out_name)
        process_image(args.input, dest_path, size=args.size, outline=args.outline, clean_white=not args.keep_bg)
    elif os.path.isdir(args.input):
        for fname in sorted(os.listdir(args.input)):
            if fname.lower().endswith((".png", ".jpg", ".jpeg", ".webp")):
                in_path = os.path.join(args.input, fname)
                out_name = os.path.splitext(fname)[0] + ".png"
                dest_path = os.path.join(dest_folder, out_name)
                process_image(in_path, dest_path, size=args.size, outline=args.outline, clean_white=not args.keep_bg)
    else:
        print(f"Error: {args.input} not found.")
        sys.exit(1)

if __name__ == "__main__":
    main()
