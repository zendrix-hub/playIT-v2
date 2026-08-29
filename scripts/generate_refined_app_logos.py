"""
PlayIT App Logo / Icon Rework
Creates professional, iconic app logos inspired by the Headspace x Duolingo splash design:
  Design 1 (Hero Face App Icon - Duolingo ABC style: full-bleed tarsier face mark)
  Design 2 (Iconic Peeking Dome - Headspace style: geometric dome tarsier in warm sunny badge)
"""

import os
import math
from PIL import Image, ImageDraw, ImageFilter

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES_DIR = os.path.join(BASE_DIR, "app", "src", "main", "res")
TOOLS_DIR = os.path.join(BASE_DIR, "tools")

OUTLINE = (45, 55, 62, 255)       # #2D373E
OUTLINE_WIDTH = 12

MANGO = (250, 123, 40, 255)       # #FA7B28
MANGO_TOP = (255, 160, 60, 255)   # #FFA03C
MANGO_DARK = (217, 95, 20, 255)
MANGO_SHADOW = (180, 70, 10, 255)
EAR_INNER = (255, 175, 120, 255)  # #FFAF78
CREAM_PATCH = (255, 238, 215, 255)# #FFEED7
PUPIL = (31, 58, 61, 255)         # #1F3A3D
WHITE = (255, 255, 255, 255)
ROSY_CHEEK = (255, 170, 185, 255)
TONGUE = (244, 63, 94, 255)       # #F43F5E

SUNNY_SKY_TOP = (255, 252, 235, 255)
SUNNY_SKY_BOT = (254, 215, 102, 255) # #FED766

def apply_outline(fill_img, stroke_w=OUTLINE_WIDTH, stroke_color=OUTLINE):
    alpha = fill_img.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_w * 2 + 1))
    stroke_base = Image.new("RGBA", fill_img.size, stroke_color)
    stroke_layer = Image.new("RGBA", fill_img.size, (0, 0, 0, 0))
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, fill_img)

def render_hero_face_icon(size=512):
    """
    Design 1: Full-bleed Hero Face App Icon (Duolingo Style)
    The entire squircle IS Lily's iconic face, bold, expressive, and uncluttered.
    """
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    
    # 1. Warm Mango Gradient Squircle Base
    base_canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d_base = ImageDraw.Draw(base_canvas)
    for y in range(size):
        ratio = y / float(size - 1)
        r = int(MANGO_TOP[0] * (1 - ratio) + MANGO[0] * ratio)
        g = int(MANGO_TOP[1] * (1 - ratio) + MANGO[1] * ratio)
        b = int(MANGO_TOP[2] * (1 - ratio) + MANGO[2] * ratio)
        d_base.line([(0, y), (size - 1, y)], fill=(r, g, b, 255))
        
    mask = Image.new("L", (size, size), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.rounded_rectangle([16, 16, size - 16, size - 16], radius=115, fill=255)
    
    squircle = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    squircle.paste(base_canvas, (0, 0), mask)
    
    # 2. Add 3D Gummy Bottom Depth on Squircle
    depth_layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d_depth = ImageDraw.Draw(depth_layer)
    d_depth.rounded_rectangle([16, size - 50, size - 16, size - 16], radius=115, fill=(0, 0, 0, 45))
    squircle = Image.alpha_composite(squircle, depth_layer)
    
    # 3. Ears integrated at top corners
    ear_layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d_ears = ImageDraw.Draw(ear_layer)
    # Left Ear
    d_ears.ellipse([25, 25, 175, 175], fill=MANGO)
    d_ears.ellipse([50, 50, 150, 150], fill=EAR_INNER)
    # Right Ear
    d_ears.ellipse([size - 175, 25, size - 25, 175], fill=MANGO)
    d_ears.ellipse([size - 150, 50, size - 50, 150], fill=EAR_INNER)
    
    ear_layer = apply_outline(ear_layer, stroke_w=10)
    
    # Composite ears behind/into base
    combined = Image.alpha_composite(squircle, ear_layer)
    
    # 4. Big Expressive Cream Eye Patches
    face_elements = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d_face = ImageDraw.Draw(face_elements)
    
    patch_radius = 82
    patch_y = 240
    d_face.ellipse([145 - patch_radius, patch_y - patch_radius, 145 + patch_radius, patch_y + patch_radius], fill=CREAM_PATCH)
    d_face.ellipse([size - 145 - patch_radius, patch_y - patch_radius, size - 145 + patch_radius, patch_y + patch_radius], fill=CREAM_PATCH)
    
    # Cheerful Rosy Blush
    d_face.ellipse([60, 310, 125, 355], fill=ROSY_CHEEK)
    d_face.ellipse([size - 125, 310, size - 60, 355], fill=ROSY_CHEEK)
    
    # Giant Glossy Tarsier Eyes with double catchlights
    eye_r = 44
    # Left Pupil
    d_face.ellipse([145 - eye_r, patch_y - eye_r, 145 + eye_r, patch_y + eye_r], fill=PUPIL)
    d_face.ellipse([135 - 16, patch_y - 20, 135 + 16, patch_y + 12], fill=WHITE)
    d_face.ellipse([160 - 8, patch_y + 14, 160 + 8, patch_y + 30], fill=WHITE)
    
    # Right Pupil
    d_face.ellipse([size - 145 - eye_r, patch_y - eye_r, size - 145 + eye_r, patch_y + eye_r], fill=PUPIL)
    d_face.ellipse([size - 155 - 16, patch_y - 20, size - 155 + 16, patch_y + 12], fill=WHITE)
    d_face.ellipse([size - 130 - 8, patch_y + 14, size - 130 + 8, patch_y + 30], fill=WHITE)
    
    # Cute Minimalist Nose Dot
    d_face.ellipse([size//2 - 6, patch_y + 40, size//2 + 6, patch_y + 52], fill=PUPIL)
    
    # Joyful Open Smile with Tongue
    d_face.pieslice([size//2 - 44, patch_y + 60, size//2 + 44, patch_y + 130], 0, 180, fill=PUPIL)
    d_face.pieslice([size//2 - 30, patch_y + 90, size//2 + 30, patch_y + 130], 0, 180, fill=TONGUE)
    
    face_outlined = apply_outline(face_elements, stroke_w=9)
    combined = Image.alpha_composite(combined, face_outlined)
    
    # Final Squircle Border
    d_final = ImageDraw.Draw(combined)
    d_final.rounded_rectangle([16, 16, size - 16, size - 16], radius=115, outline=OUTLINE, width=12)
    return combined

def render_headspace_dome_icon(size=512):
    """
    Design 2: Iconic Headspace-style Tarsier Dome in Sunny Badge
    Smooth geometric dome rising from bottom with huge expressive tarsier eyes.
    """
    # 1. Warm Sunny Radial/Vertical Gradient Backdrop (From Splash Screen)
    bg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d_bg = ImageDraw.Draw(bg)
    for y in range(size):
        ratio = y / float(size - 1)
        r = int(SUNNY_SKY_TOP[0] * (1 - ratio) + SUNNY_SKY_BOT[0] * ratio)
        g = int(SUNNY_SKY_TOP[1] * (1 - ratio) + SUNNY_SKY_BOT[1] * ratio)
        b = int(SUNNY_SKY_TOP[2] * (1 - ratio) + SUNNY_SKY_BOT[2] * ratio)
        d_bg.line([(0, y), (size - 1, y)], fill=(r, g, b, 255))
        
    mask = Image.new("L", (size, size), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.rounded_rectangle([16, 16, size - 16, size - 16], radius=115, fill=255)
    
    squircle = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    squircle.paste(bg, (0, 0), mask)
    
    # 2. Tarsier Character Elements (Dome + Ears + Face)
    char_img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d_char = ImageDraw.Draw(char_img)
    
    # Ears rising behind dome
    ear_w = 68
    ear_h = 75
    # Left Ear
    d_char.ellipse([80 - ear_w, 185 - ear_h, 80 + ear_w, 185 + ear_h], fill=MANGO)
    d_char.ellipse([80 - ear_w*0.65, 185 - ear_h*0.65, 80 + ear_w*0.65, 185 + ear_h*0.65], fill=EAR_INNER)
    # Right Ear
    d_char.ellipse([size - 80 - ear_w, 185 - ear_h, size - 80 + ear_w, 185 + ear_h], fill=MANGO)
    d_char.ellipse([size - 80 - ear_w*0.65, 185 - ear_h*0.65, size - 80 + ear_w*0.65, 185 + ear_h*0.65], fill=EAR_INNER)
    
    # Headspace-style Main Tarsier Dome Head
    dome_top_y = 195
    d_char.ellipse([size//2 - 230, dome_top_y, size//2 + 230, size + 160], fill=MANGO)
    
    # Eye Patches
    patch_r = 64
    patch_y = 300
    d_char.ellipse([size//2 - 85 - patch_r, patch_y - patch_r, size//2 - 85 + patch_r, patch_y + patch_r], fill=CREAM_PATCH)
    d_char.ellipse([size//2 + 85 - patch_r, patch_y - patch_r, size//2 + 85 + patch_r, patch_y + patch_r], fill=CREAM_PATCH)
    
    # Rosy Blush
    d_char.ellipse([size//2 - 170, patch_y + 40, size//2 - 120, patch_y + 75], fill=ROSY_CHEEK)
    d_char.ellipse([size//2 + 120, patch_y + 40, size//2 + 170, patch_y + 75], fill=ROSY_CHEEK)
    
    # Glossy Tarsier Pupils
    pupil_r = 38
    # Left Pupil
    d_char.ellipse([size//2 - 85 - pupil_r, patch_y - pupil_r, size//2 - 85 + pupil_r, patch_y + pupil_r], fill=PUPIL)
    d_char.ellipse([size//2 - 93 - 12, patch_y - 18, size//2 - 93 + 12, patch_y + 8], fill=WHITE)
    d_char.ellipse([size//2 - 72 - 6, patch_y + 12, size//2 - 72 + 6, patch_y + 24], fill=WHITE)
    
    # Right Pupil
    d_char.ellipse([size//2 + 85 - pupil_r, patch_y - pupil_r, size//2 + 85 + pupil_r, patch_y + pupil_r], fill=PUPIL)
    d_char.ellipse([size//2 + 77 - 12, patch_y - 18, size//2 + 77 + 12, patch_y + 8], fill=WHITE)
    d_char.ellipse([size//2 + 98 - 6, patch_y + 12, size//2 + 98 + 6, patch_y + 24], fill=WHITE)
    
    # Nose
    d_char.ellipse([size//2 - 5, patch_y + 35, size//2 + 5, patch_y + 45], fill=PUPIL)
    
    # Cheerful Smile
    d_char.pieslice([size//2 - 38, patch_y + 52, size//2 + 38, patch_y + 105], 0, 180, fill=PUPIL)
    d_char.pieslice([size//2 - 26, patch_y + 74, size//2 + 26, patch_y + 105], 0, 180, fill=TONGUE)
    
    # Outline Character
    char_outlined = apply_outline(char_img, stroke_w=10)
    
    # Mask character to squircle
    char_masked = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    char_masked.paste(char_outlined, (0, 0), mask)
    
    # Composite onto sunny background
    final_icon = Image.alpha_composite(squircle, char_masked)
    
    # Final Squircle Border + Gummy Depth
    d_final = ImageDraw.Draw(final_icon)
    d_final.rounded_rectangle([16, 16, size - 16, size - 16], radius=115, outline=OUTLINE, width=12)
    return final_icon

def main():
    print("=" * 80)
    print("[*] Generating Reworked PlayIT App Logos...")
    print("=" * 80)
    
    hero_icon = render_hero_face_icon(512)
    dome_icon = render_headspace_dome_icon(512)
    
    hero_path = os.path.join(TOOLS_DIR, "app_logo_hero_face.png")
    dome_path = os.path.join(TOOLS_DIR, "app_logo_headspace_dome.png")
    
    hero_icon.save(hero_path, format="PNG")
    dome_icon.save(dome_path, format="PNG")
    
    print(f"  [+] Saved Hero Face Logo:     {hero_path}")
    print(f"  [+] Saved Headspace Dome Logo: {dome_path}")
    print("=" * 80)

if __name__ == "__main__":
    main()
