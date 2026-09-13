"""
Process IP-as-Logo Avatar Characters into transparent, outlined PlayIT production assets.
"""

import os
from PIL import Image, ImageFilter, ImageDraw
import numpy as np

def isolate_character(img: Image.Image, bg_tolerance: int = 40) -> Image.Image:
    """Removes the solid background color sampled from outer corner pixels."""
    img = img.convert("RGBA")
    w, h = img.size
    
    # Sample corner pixels for the background color
    corners = [(2, 2), (w - 3, 2), (2, h - 3), (w - 3, h - 3)]
    corner_colors = [img.getpixel(pt)[:3] for pt in corners]
    bg_color = np.median(corner_colors, axis=0)

    data = np.array(img)
    rgb = data[:, :, :3].astype(float)
    
    # Calculate color distance to background
    diff = np.sqrt(np.sum((rgb - bg_color) ** 2, axis=2))
    
    # Mask out background
    mask = diff > bg_tolerance
    
    # Create clean alpha channel
    alpha = np.zeros((h, w), dtype=np.uint8)
    alpha[mask] = 255
    
    result = Image.fromarray(data)
    result.putalpha(Image.fromarray(alpha))
    
    # Crop to subject bounding box
    bbox = result.getbbox()
    if bbox:
        # Avoid cropping out whole image if mask failed
        result = result.crop(bbox)
        
    return result

def apply_stroke(img: Image.Image, stroke_width: int = 8, stroke_color = (45, 55, 62, 255)) -> Image.Image:
    """Applies smooth continuous border outline using dilation filter."""
    alpha = img.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_width * 2 + 1))
    stroke_layer = Image.new("RGBA", img.size, (0, 0, 0, 0))
    stroke_base = Image.new("RGBA", img.size, stroke_color)
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, img)

def make_production_avatar(src_path: str, size: int = 512, outline: int = 8) -> Image.Image:
    img = Image.open(src_path)
    isolated = isolate_character(img)
    
    # Scale to canvas with margin
    inner_size = size - (outline * 4) - 32
    isolated.thumbnail((inner_size, inner_size), Image.Resampling.LANCZOS)
    
    # Center on canvas
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    x = (size - isolated.width) // 2
    y = (size - isolated.height) // 2
    canvas.paste(isolated, (x, y), isolated)
    
    if outline > 0:
        canvas = apply_stroke(canvas, stroke_width=outline)
        
    return canvas

if __name__ == "__main__":
    import sys
    print("Avatar processor loaded.")
