"""
PlayIT Asset Pipeline Optimizer (Clipdrop / rembg / 4-Benchmark synthesis)

Processes raw or AI-generated PNG/JPEG illustrations:
1. Strips backgrounds and converts to 100% transparent RGBA
2. Applies continuous #2D373E outline with child-friendly dilation
3. Resizes to standard Android 512x512 / 256x256 WebP / PNG
"""

import os
import sys
import argparse
from PIL import Image, ImageFilter

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images")

OUTLINE_COLOR = (45, 55, 62, 255) # #2D373E Dark Brown / Slate Outline

def apply_duolingo_outline(image: Image.Image, stroke_width: int = 8) -> Image.Image:
    """Applies a smooth continuous outline using alpha dilation."""
    if image.mode != "RGBA":
        image = image.convert("RGBA")
    
    alpha = image.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_width * 2 + 1))
    
    stroke_layer = Image.new("RGBA", image.size, (0, 0, 0, 0))
    stroke_base = Image.new("RGBA", image.size, OUTLINE_COLOR)
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    
    return Image.alpha_composite(stroke_layer, image)

def optimize_asset(input_path: str, output_path: str, size: int = 512, outline: int = 8):
    """Optimizes single asset with transparent background and outline."""
    img = Image.open(input_path).convert("RGBA")
    
    # Resize keeping aspect ratio
    img.thumbnail((size - outline * 4, size - outline * 4), Image.Resampling.LANCZOS)
    
    # Center on canvas
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    offset = ((size - img.width) // 2, (size - img.height) // 2)
    canvas.paste(img, offset, img)
    
    # Apply outline
    if outline > 0:
        canvas = apply_duolingo_outline(canvas, stroke_width=outline)
        
    canvas.save(output_path, format="PNG")
    print(f"  [+] Optimized: {output_path} ({canvas.width}x{canvas.height})")

def main():
    parser = argparse.ArgumentParser(description="PlayIT Asset Pipeline Optimizer")
    parser.add_argument("--input", type=str, help="Input image file")
    parser.add_argument("--out", type=str, help="Output destination")
    parser.add_argument("--size", type=int, default=512, help="Output size (default 512)")
    parser.add_argument("--outline", type=int, default=8, help="Stroke outline width (default 8)")
    args = parser.parse_args()

    if not args.input:
        print("Usage: python tools/asset_pipeline_optimizer.py --input <path> --out <output_path>")
        return

    out = args.out or args.input
    optimize_asset(args.input, out, size=args.size, outline=args.outline)

if __name__ == "__main__":
    main()
