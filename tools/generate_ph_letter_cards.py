"""
Generate styled letter cards for 'NG' and 'Ñ' matching the 1024x1024 Lexend blue bubble aesthetic of letter_a through letter_z.
"""

import os
from PIL import Image, ImageDraw, ImageFilter
import numpy as np

LETTERS_DIR = "/mnt/c/Users/Zendrix/Desktop/thesis/playIT-v2-workspace/app/src/main/assets/images/letters"
OUTLINE_COLOR = (29, 79, 136, 255)   # #1D4F88 Navy Blue Outline
FILL_COLOR = (56, 156, 244, 255)      # #389CF4 Sky Blue Fill
HIGHLIGHT_COLOR = (120, 195, 255, 255)

def create_tilde(width=360, height=90, stroke_w=24):
    """Draws a smooth, rounded cartoon tilde glyph with outline and fill."""
    tilde_img = Image.new("RGBA", (width + 60, height + 60), (0, 0, 0, 0))
    draw = ImageDraw.Draw(tilde_img)
    
    # 2 cubic bezier segments forming ~
    pts = []
    # Left curve: dips down and up
    # Right curve: arcs up and down
    cx = (width + 60) / 2
    cy = (height + 60) / 2
    
    # Generate points along the S-wave
    steps = 50
    for i in range(steps + 1):
        t = i / float(steps)
        # Smooth sine-based wave
        x = 30 + t * width
        y = cy - math.sin(t * 2 * math.pi) * (height * 0.38)
        pts.append((x, y))
        
    # Draw thick outline
    for i in range(len(pts) - 1):
        draw.line([pts[i], pts[i+1]], fill=OUTLINE_COLOR, width=stroke_w + 22, joint="curve")
    for p in pts:
        r = (stroke_w + 22) / 2
        draw.ellipse([p[0]-r, p[1]-r, p[0]+r, p[1]+r], fill=OUTLINE_COLOR)
        
    # Draw fill
    for i in range(len(pts) - 1):
        draw.line([pts[i], pts[i+1]], fill=FILL_COLOR, width=stroke_w, joint="curve")
    for p in pts:
        r = stroke_w / 2
        draw.ellipse([p[0]-r, p[1]-r, p[0]+r, p[1]+r], fill=FILL_COLOR)
        
    return tilde_img

import math

def build_enie():
    """Builds letter_ñ / letter_n_tilde using letter_n.png and matching tilde."""
    src_n = Image.open(os.path.join(LETTERS_DIR, "letter_n.png"))
    
    # Crop to N's bounding box
    bbox_n = src_n.getbbox()
    cropped_n = src_n.crop(bbox_n)
    
    # Scale N down to ~78% so tilde fits comfortably above
    target_h = int(cropped_n.height * 0.80)
    target_w = int(cropped_n.width * 0.80)
    scaled_n = cropped_n.resize((target_w, target_h), Image.Resampling.LANCZOS)
    
    # Create tilde
    tilde = create_tilde(width=int(target_w * 0.72), height=64, stroke_w=28)
    
    # Composite on 1024x1024 canvas
    canvas = Image.new("RGBA", (1024, 1024), (0, 0, 0, 0))
    
    # Center vertically combined
    total_h = scaled_n.height + tilde.height + 15
    start_y = (1024 - total_h) // 2
    
    # Paste tilde
    tilde_x = (1024 - tilde.width) // 2
    canvas.paste(tilde, (tilde_x, start_y), tilde)
    
    # Paste N
    n_x = (1024 - scaled_n.width) // 2
    canvas.paste(scaled_n, (n_x, start_y + tilde.height + 15), scaled_n)
    
    return canvas

def build_ng():
    """Builds letter_ng by compositing N and G from letter_n.png and letter_g.png."""
    src_n = Image.open(os.path.join(LETTERS_DIR, "letter_n.png"))
    src_g = Image.open(os.path.join(LETTERS_DIR, "letter_g.png"))
    
    bbox_n = src_n.getbbox()
    bbox_g = src_g.getbbox()
    
    cropped_n = src_n.crop(bbox_n)
    cropped_g = src_g.crop(bbox_g)
    
    # Scale both down so side-by-side width fits within 880px
    scale = 0.68
    new_wn = int(cropped_n.width * scale)
    new_hn = int(cropped_n.height * scale)
    new_wg = int(cropped_g.width * scale)
    new_hg = int(cropped_g.height * scale)
    
    scaled_n = cropped_n.resize((new_wn, new_hn), Image.Resampling.LANCZOS)
    scaled_g = cropped_g.resize((new_wg, new_hg), Image.Resampling.LANCZOS)
    
    spacing = 18
    total_w = new_wn + spacing + new_wg
    start_x = (1024 - total_w) // 2
    start_yn = (1024 - new_hn) // 2
    start_yg = (1024 - new_hg) // 2
    
    canvas = Image.new("RGBA", (1024, 1024), (0, 0, 0, 0))
    canvas.paste(scaled_n, (start_x, start_yn), scaled_n)
    canvas.paste(scaled_g, (start_x + new_wn + spacing, start_yg), scaled_g)
    
    return canvas

def main():
    enie_img = build_enie()
    # Save both letter_ñ.png and letter_n_tilde.png
    p_enie = os.path.join(LETTERS_DIR, "letter_ñ.png")
    p_ntilde = os.path.join(LETTERS_DIR, "letter_n_tilde.png")
    enie_img.save(p_enie, "PNG", optimize=True)
    enie_img.save(p_ntilde, "PNG", optimize=True)
    print(f"[OK] Saved {p_enie} and {p_ntilde}")
    
    ng_img = build_ng()
    p_ng = os.path.join(LETTERS_DIR, "letter_ng.png")
    ng_img.save(p_ng, "PNG", optimize=True)
    print(f"[OK] Saved {p_ng}")

if __name__ == "__main__":
    main()
