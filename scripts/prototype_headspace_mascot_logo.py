"""
Prototype Headspace-Style Mascot Logos for PlayIT
Explores candidate designs synthesizing Headspace's iconic dome geometry
with Lily the Tarsier's authentic character design.
"""

import os
import math
from PIL import Image, ImageDraw, ImageFilter

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SCRATCH_DIR = "/home/zendrix/.gemini/antigravity-cli/brain/af2db79b-86c4-4925-a8b9-dd27044dffec"
TOOLS_DIR = os.path.join(BASE_DIR, "tools")
MASCOT_PATH = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images", "mascot", "lily_idle.png")

SIZE = 2048  # 4x supersampling for pristine antialiasing

# Color Palette strictly matching Lily the Tarsier & Headspace
OUTLINE = (45, 55, 62, 255)            # #2D373E
OUTLINE_WIDTH = 48                    # ~12px at 512

FUR_BASE = (217, 143, 70, 255)         # #D98F46 Lily main caramel fur
FUR_LIGHT = (238, 172, 102, 255)       # Soft highlight
FUR_SHADOW = (185, 110, 45, 255)       # Depth shadow
CREAM_PATCH = (253, 238, 207, 255)     # #FDEECF Lily muzzle & eye patches
EAR_INNER = (251, 152, 121, 255)       # #FB9879 Lily inner ear peach
EAR_INNER_LIGHT = (255, 185, 160, 255)

EYE_AMBER_OUTER = (217, 143, 70, 255)  # Outer amber
EYE_AMBER = (245, 166, 35, 255)        # Glowing amber ring
EYE_GOLD = (255, 215, 110, 255)        # Bright iris gold
EYE_PUPIL = (45, 55, 62, 255)          # Deep #2D373E
WHITE = (255, 255, 255, 255)
NOSE_PEACH = (209, 124, 76, 255)       # Lily nose
ROSY_BLUSH = (255, 160, 165, 200)      # Soft cheek blush
TONGUE = (244, 63, 94, 255)

BG_TOP = (255, 253, 238, 255)          # #FFFDEE (Headspace morning cream)
BG_BOT = (254, 215, 102, 255)          # #FED766 (Headspace golden glow)

def create_badge_canvas(size=SIZE):
    bg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(bg)
    for y in range(size):
        r = y / float(size - 1)
        c = (
            int(BG_TOP[0] * (1 - r) + BG_BOT[0] * r),
            int(BG_TOP[1] * (1 - r) + BG_BOT[1] * r),
            int(BG_TOP[2] * (1 - r) + BG_BOT[2] * r),
            255
        )
        d.line([(0, y), (size - 1, y)], fill=c)
    mask = Image.new("L", (size, size), 0)
    d_mask = ImageDraw.Draw(mask)
    d_mask.rounded_rectangle([64, 64, size - 64, size - 64], radius=460, fill=255)
    
    badge = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    badge.paste(bg, (0, 0), mask)
    return badge, mask

def apply_outline(fill_img, stroke_w=OUTLINE_WIDTH, stroke_color=OUTLINE):
    alpha = fill_img.split()[3]
    # Fast hierarchical dilation: 4x downscale -> small MaxFilter -> upscale
    small_size = (fill_img.width // 4, fill_img.height // 4)
    small_alpha = alpha.resize(small_size, Image.Resampling.BILINEAR)
    small_filter_r = int(math.ceil(stroke_w / 4.0))
    small_expanded = small_alpha.filter(ImageFilter.MaxFilter(small_filter_r * 2 + 1))
    expanded = small_expanded.resize(fill_img.size, Image.Resampling.BILINEAR)
    stroke_base = Image.new("RGBA", fill_img.size, stroke_color)
    stroke_layer = Image.new("RGBA", fill_img.size, (0, 0, 0, 0))
    stroke_layer.paste(stroke_base, (0, 0), expanded)
    return Image.alpha_composite(stroke_layer, fill_img)

def finish_badge(badge, mask, char_layer):
    char_masked = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    char_masked.paste(char_layer, (0, 0), mask)
    combined = Image.alpha_composite(badge, char_masked)
    
    # Gummy depth shadow at bottom
    depth = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    d_depth = ImageDraw.Draw(depth)
    d_depth.rounded_rectangle([64, SIZE - 192, SIZE - 64, SIZE - 64], radius=460, fill=(0, 0, 0, 35))
    combined = Image.alpha_composite(combined, depth)
    
    # Outer Border
    d_final = ImageDraw.Draw(combined)
    d_final.rounded_rectangle([64, 64, SIZE - 64, SIZE - 64], radius=460, outline=OUTLINE, width=OUTLINE_WIDTH)
    return combined

def draw_tarsier_eyes(d, cx, cy, eye_spacing=380, eye_r=190, pupil_r=130):
    """Draws Lily the Tarsier's signature luminous golden eyes with double catchlights."""
    # Left eye
    lx = cx - eye_spacing
    rx = cx + eye_spacing
    
    for ex in (lx, rx):
        # 1. Outer eye orbit outline & amber ring
        d.ellipse([ex - eye_r, cy - eye_r, ex + eye_r, cy + eye_r], fill=EYE_AMBER)
        # 2. Glowing gold inner iris ring
        d.ellipse([ex - eye_r*0.82, cy - eye_r*0.82, ex + eye_r*0.82, cy + eye_r*0.82], fill=EYE_GOLD)
        # 3. Dark espresso pupil
        d.ellipse([ex - pupil_r, cy - pupil_r, ex + pupil_r, cy + pupil_r], fill=EYE_PUPIL)
        # 4. Big primary catchlight (top-left)
        cl1_r = pupil_r * 0.40
        d.ellipse([ex - pupil_r*0.35 - cl1_r, cy - pupil_r*0.35 - cl1_r,
                   ex - pupil_r*0.35 + cl1_r, cy - pupil_r*0.35 + cl1_r], fill=WHITE)
        # 5. Secondary catchlight (bottom-right)
        cl2_r = pupil_r * 0.20
        d.ellipse([ex + pupil_r*0.45 - cl2_r, cy + pupil_r*0.40 - cl2_r,
                   ex + pupil_r*0.45 + cl2_r, cy + pupil_r*0.40 + cl2_r], fill=WHITE)

def render_candidate_a():
    """
    Candidate A: Pure Headspace Geometric Dome with Lily's Mascot Design.
    Smooth rising dome, rounded tarsier ears with peach inner lobes,
    cream eye mask, Lily's radiant amber eyes, peach button nose, and gentle smile.
    """
    badge, mask = create_badge_canvas(SIZE)
    char_img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    d = ImageDraw.Draw(char_img)
    
    cx = SIZE // 2
    dome_top_y = int(SIZE * 0.40)      # Crest of dome at y=820
    dome_rx = int(SIZE * 0.48)         # Dome width radius
    dome_ry = int(SIZE * 0.44)         # Dome height radius
    dome_cy = dome_top_y + dome_ry
    
    # 1. Ears rising behind dome
    ear_w = 270
    ear_h = 300
    ear_lx = cx - 480
    ear_rx = cx + 480
    ear_y = dome_top_y + 40
    
    # Outer ears (Caramel fur)
    d.ellipse([ear_lx - ear_w, ear_y - ear_h, ear_lx + ear_w, ear_y + ear_h], fill=FUR_BASE)
    d.ellipse([ear_rx - ear_w, ear_y - ear_h, ear_rx + ear_w, ear_y + ear_h], fill=FUR_BASE)
    
    # Inner ears (Soft Peach)
    in_w = int(ear_w * 0.68)
    in_h = int(ear_h * 0.68)
    d.ellipse([ear_lx - in_w, ear_y - in_h, ear_lx + in_w, ear_y + in_h], fill=EAR_INNER)
    d.ellipse([ear_rx - in_w, ear_y - in_h, ear_rx + in_w, ear_y + in_h], fill=EAR_INNER)
    
    # Inner ear soft light highlight
    in_light_w = int(ear_w * 0.42)
    in_light_h = int(ear_h * 0.42)
    d.ellipse([ear_lx - in_light_w, ear_y - in_light_h*1.1, ear_lx + in_light_w, ear_y + in_light_h*0.9], fill=EAR_INNER_LIGHT)
    d.ellipse([ear_rx - in_light_w, ear_y - in_light_h*1.1, ear_rx + in_light_w, ear_y + in_light_h*0.9], fill=EAR_INNER_LIGHT)
    
    # 2. Main Headspace Dome Body (Caramel Fur)
    d.ellipse([cx - dome_rx, dome_cy - dome_ry, cx + dome_rx, dome_cy + dome_ry + 200], fill=FUR_BASE)
    
    # 3. Soft Cream Eye Mask / Muzzle Patch (Lily's distinctive mask)
    patch_y = dome_top_y + 460
    patch_spacing = 380
    patch_rx = 290
    patch_ry = 260
    d.ellipse([cx - patch_spacing - patch_rx, patch_y - patch_ry, cx - patch_spacing + patch_rx, patch_y + patch_ry], fill=CREAM_PATCH)
    d.ellipse([cx + patch_spacing - patch_rx, patch_y - patch_ry, cx + patch_spacing + patch_rx, patch_y + patch_ry], fill=CREAM_PATCH)
    # Bridge connecting eye patches
    d.ellipse([cx - 190, patch_y - 120, cx + 190, patch_y + 220], fill=CREAM_PATCH)
    
    # 4. Lily's Signature Luminous Golden Eyes
    draw_tarsier_eyes(d, cx, patch_y, eye_spacing=patch_spacing, eye_r=185, pupil_r=128)
    
    # 5. Soft Rosy Blush Cheeks
    blush_w = 120
    blush_h = 75
    d.ellipse([cx - 620 - blush_w, patch_y + 190 - blush_h, cx - 620 + blush_w, patch_y + 190 + blush_h], fill=ROSY_BLUSH)
    d.ellipse([cx + 620 - blush_w, patch_y + 190 - blush_h, cx + 620 + blush_w, patch_y + 190 + blush_h], fill=ROSY_BLUSH)
    
    # 6. Cute Peach Button Nose
    nose_y = patch_y + 150
    d.ellipse([cx - 38, nose_y - 28, cx + 38, nose_y + 28], fill=NOSE_PEACH)
    
    # 7. Sweet Reassuring Smile
    mouth_y = patch_y + 245
    mouth_w = 170
    mouth_h = 90
    # Smile arc
    d.pieslice([cx - mouth_w, mouth_y - 40, cx + mouth_w, mouth_y + mouth_h*2], 0, 180, fill=OUTLINE)
    d.pieslice([cx - mouth_w*0.75, mouth_y + 20, cx + mouth_w*0.75, mouth_y + mouth_h*2], 0, 180, fill=TONGUE)
    
    # Apply crisp continuous #2D373E pediatric outline
    char_outlined = apply_outline(char_img, stroke_w=OUTLINE_WIDTH)
    return finish_badge(badge, mask, char_outlined)

def render_candidate_b():
    """
    Candidate B: Headspace Mascot Dome with Lily's Forehead Fur Crest & Cheek Tufts.
    Adds Lily's unmistakable top tuft peak and cheek tufts to the Headspace dome.
    """
    badge, mask = create_badge_canvas(SIZE)
    char_img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    d = ImageDraw.Draw(char_img)
    
    cx = SIZE // 2
    dome_top_y = int(SIZE * 0.40)
    dome_rx = int(SIZE * 0.47)
    dome_ry = int(SIZE * 0.44)
    dome_cy = dome_top_y + dome_ry
    
    # 1. Ears with Lily's natural contours
    ear_w = 280
    ear_h = 310
    ear_lx = cx - 490
    ear_rx = cx + 490
    ear_y = dome_top_y + 35
    
    d.ellipse([ear_lx - ear_w, ear_y - ear_h, ear_lx + ear_w, ear_y + ear_h], fill=FUR_BASE)
    d.ellipse([ear_rx - ear_w, ear_y - ear_h, ear_rx + ear_w, ear_y + ear_h], fill=FUR_BASE)
    
    in_w = int(ear_w * 0.68)
    in_h = int(ear_h * 0.68)
    d.ellipse([ear_lx - in_w, ear_y - in_h, ear_lx + in_w, ear_y + in_h], fill=EAR_INNER)
    d.ellipse([ear_rx - in_w, ear_y - in_h, ear_rx + in_w, ear_y + in_h], fill=EAR_INNER)
    
    in_light_w = int(ear_w * 0.42)
    in_light_h = int(ear_h * 0.42)
    d.ellipse([ear_lx - in_light_w, ear_y - in_light_h*1.1, ear_lx + in_light_w, ear_y + in_light_h*0.9], fill=EAR_INNER_LIGHT)
    d.ellipse([ear_rx - in_light_w, ear_y - in_light_h*1.1, ear_rx + in_light_w, ear_y + in_light_h*0.9], fill=EAR_INNER_LIGHT)
    
    # 2. Main Dome Body
    d.ellipse([cx - dome_rx, dome_cy - dome_ry, cx + dome_rx, dome_cy + dome_ry + 200], fill=FUR_BASE)
    
    # 3. Lily's Signature Forehead Fur Crest (Crown Tuft)
    crest_pts = [
        (cx - 160, dome_top_y + 80),
        (cx, dome_top_y - 95),      # Peaks delightfully above the dome curve
        (cx + 160, dome_top_y + 80),
    ]
    d.polygon(crest_pts, fill=FUR_BASE)
    
    # 4. Lily's Subtle Cheek Tufts (Soft pediatric triangular flares)
    patch_y = dome_top_y + 460
    # Left cheek tuft
    d.polygon([(cx - dome_rx + 40, patch_y - 60), (cx - dome_rx - 70, patch_y + 40), (cx - dome_rx + 50, patch_y + 140)], fill=FUR_BASE)
    # Right cheek tuft
    d.polygon([(cx + dome_rx - 40, patch_y - 60), (cx + dome_rx + 70, patch_y + 40), (cx + dome_rx - 50, patch_y + 140)], fill=FUR_BASE)
    
    # 5. Soft Cream Eye Mask / Muzzle Patch
    patch_spacing = 380
    patch_rx = 290
    patch_ry = 260
    d.ellipse([cx - patch_spacing - patch_rx, patch_y - patch_ry, cx - patch_spacing + patch_rx, patch_y + patch_ry], fill=CREAM_PATCH)
    d.ellipse([cx + patch_spacing - patch_rx, patch_y - patch_ry, cx + patch_spacing + patch_rx, patch_y + patch_ry], fill=CREAM_PATCH)
    d.ellipse([cx - 190, patch_y - 120, cx + 190, patch_y + 220], fill=CREAM_PATCH)
    
    # 6. Lily's Radiant Amber Eyes
    draw_tarsier_eyes(d, cx, patch_y, eye_spacing=patch_spacing, eye_r=185, pupil_r=128)
    
    # 7. Rosy Blush
    blush_w = 120
    blush_h = 75
    d.ellipse([cx - 620 - blush_w, patch_y + 190 - blush_h, cx - 620 + blush_w, patch_y + 190 + blush_h], fill=ROSY_BLUSH)
    d.ellipse([cx + 620 - blush_w, patch_y + 190 - blush_h, cx + 620 + blush_w, patch_y + 190 + blush_h], fill=ROSY_BLUSH)
    
    # 8. Button Nose & Smile
    nose_y = patch_y + 150
    d.ellipse([cx - 38, nose_y - 28, cx + 38, nose_y + 28], fill=NOSE_PEACH)
    
    mouth_y = patch_y + 245
    mouth_w = 170
    mouth_h = 90
    d.pieslice([cx - mouth_w, mouth_y - 40, cx + mouth_w, mouth_y + mouth_h*2], 0, 180, fill=OUTLINE)
    d.pieslice([cx - mouth_w*0.75, mouth_y + 20, cx + mouth_w*0.75, mouth_y + mouth_h*2], 0, 180, fill=TONGUE)
    
    char_outlined = apply_outline(char_img, stroke_w=OUTLINE_WIDTH)
    return finish_badge(badge, mask, char_outlined)

def render_candidate_c():
    """
    Candidate C: Headspace Peeking Lily with Cute Paws on Badge Rim.
    Lily's head dome peeks up with her two rounded paws resting comfortably
    on the bottom edge, adding tangible physical presence.
    """
    badge, mask = create_badge_canvas(SIZE)
    char_img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    d = ImageDraw.Draw(char_img)
    
    cx = SIZE // 2
    dome_top_y = int(SIZE * 0.38)
    dome_rx = int(SIZE * 0.46)
    dome_ry = int(SIZE * 0.44)
    dome_cy = dome_top_y + dome_ry
    
    # 1. Ears
    ear_w = 280
    ear_h = 310
    ear_lx = cx - 490
    ear_rx = cx + 490
    ear_y = dome_top_y + 35
    
    d.ellipse([ear_lx - ear_w, ear_y - ear_h, ear_lx + ear_w, ear_y + ear_h], fill=FUR_BASE)
    d.ellipse([ear_rx - ear_w, ear_y - ear_h, ear_rx + ear_w, ear_y + ear_h], fill=FUR_BASE)
    
    in_w = int(ear_w * 0.68)
    in_h = int(ear_h * 0.68)
    d.ellipse([ear_lx - in_w, ear_y - in_h, ear_lx + in_w, ear_y + in_h], fill=EAR_INNER)
    d.ellipse([ear_rx - in_w, ear_y - in_h, ear_rx + in_w, ear_y + in_h], fill=EAR_INNER)
    
    in_light_w = int(ear_w * 0.42)
    in_light_h = int(ear_h * 0.42)
    d.ellipse([ear_lx - in_light_w, ear_y - in_light_h*1.1, ear_lx + in_light_w, ear_y + in_light_h*0.9], fill=EAR_INNER_LIGHT)
    d.ellipse([ear_rx - in_light_w, ear_y - in_light_h*1.1, ear_rx + in_light_w, ear_y + in_light_h*0.9], fill=EAR_INNER_LIGHT)
    
    # 2. Main Dome Body + Forehead Tuft
    d.ellipse([cx - dome_rx, dome_cy - dome_ry, cx + dome_rx, dome_cy + dome_ry + 200], fill=FUR_BASE)
    d.polygon([(cx - 150, dome_top_y + 80), (cx, dome_top_y - 85), (cx + 150, dome_top_y + 80)], fill=FUR_BASE)
    
    # 3. Soft Cream Eye Mask / Muzzle Patch
    patch_y = dome_top_y + 440
    patch_spacing = 370
    patch_rx = 280
    patch_ry = 250
    d.ellipse([cx - patch_spacing - patch_rx, patch_y - patch_ry, cx - patch_spacing + patch_rx, patch_y + patch_ry], fill=CREAM_PATCH)
    d.ellipse([cx + patch_spacing - patch_rx, patch_y - patch_ry, cx + patch_spacing + patch_rx, patch_y + patch_ry], fill=CREAM_PATCH)
    d.ellipse([cx - 180, patch_y - 110, cx + 180, patch_y + 210], fill=CREAM_PATCH)
    
    # 4. Glowing Amber Eyes
    draw_tarsier_eyes(d, cx, patch_y, eye_spacing=patch_spacing, eye_r=180, pupil_r=124)
    
    # 5. Rosy Blush
    blush_w = 110
    blush_h = 70
    d.ellipse([cx - 610 - blush_w, patch_y + 180 - blush_h, cx - 610 + blush_w, patch_y + 180 + blush_h], fill=ROSY_BLUSH)
    d.ellipse([cx + 610 - blush_w, patch_y + 180 - blush_h, cx + 610 + blush_w, patch_y + 180 + blush_h], fill=ROSY_BLUSH)
    
    # 6. Button Nose & Smile
    nose_y = patch_y + 140
    d.ellipse([cx - 36, nose_y - 26, cx + 36, nose_y + 26], fill=NOSE_PEACH)
    
    mouth_y = patch_y + 235
    mouth_w = 160
    mouth_h = 85
    d.pieslice([cx - mouth_w, mouth_y - 35, cx + mouth_w, mouth_y + mouth_h*2], 0, 180, fill=OUTLINE)
    d.pieslice([cx - mouth_w*0.75, mouth_y + 18, cx + mouth_w*0.75, mouth_y + mouth_h*2], 0, 180, fill=TONGUE)
    
    # 7. Cute Paws resting at bottom rim
    paw_y = SIZE - 190
    paw_spacing = 380
    paw_r = 130
    # Left Paw
    d.ellipse([cx - paw_spacing - paw_r, paw_y - paw_r*0.7, cx - paw_spacing + paw_r, paw_y + paw_r*0.7], fill=FUR_BASE)
    d.ellipse([cx - paw_spacing - paw_r*0.65, paw_y - paw_r*0.4, cx - paw_spacing + paw_r*0.65, paw_y + paw_r*0.4], fill=CREAM_PATCH)
    # Right Paw
    d.ellipse([cx + paw_spacing - paw_r, paw_y - paw_r*0.7, cx + paw_spacing + paw_r, paw_y + paw_r*0.7], fill=FUR_BASE)
    d.ellipse([cx + paw_spacing - paw_r*0.65, paw_y - paw_r*0.4, cx + paw_spacing + paw_r*0.65, paw_y + paw_r*0.4], fill=CREAM_PATCH)
    
    char_outlined = apply_outline(char_img, stroke_w=OUTLINE_WIDTH)
    return finish_badge(badge, mask, char_outlined)

def main():
    print("=" * 80)
    print("[*] Rendering Headspace x Lily Mascot Candidates (2048x2048 -> 512x512)...")
    print("=" * 80)
    
    ca = render_candidate_a().resize((512, 512), Image.Resampling.LANCZOS)
    cb = render_candidate_b().resize((512, 512), Image.Resampling.LANCZOS)
    cc = render_candidate_c().resize((512, 512), Image.Resampling.LANCZOS)
    
    path_a = os.path.join(SCRATCH_DIR, "candidate_a_pure_headspace.png")
    path_b = os.path.join(SCRATCH_DIR, "candidate_b_tuft_headspace.png")
    path_c = os.path.join(SCRATCH_DIR, "candidate_c_paws_headspace.png")
    
    ca.save(path_a, format="PNG")
    cb.save(path_b, format="PNG")
    cc.save(path_c, format="PNG")
    print(f"  [+] Saved Candidate A: {path_a}")
    print(f"  [+] Saved Candidate B: {path_b}")
    print(f"  [+] Saved Candidate C: {path_c}")
    
    # Build side-by-side comparison board
    board = Image.new("RGBA", (1680, 640), (245, 247, 250, 255))
    d_board = ImageDraw.Draw(board)
    
    board.paste(ca, (60, 60), ca)
    board.paste(cb, (580, 60), cb)
    board.paste(cc, (1100, 60), cc)
    
    board_path = os.path.join(SCRATCH_DIR, "candidate_comparison_board.png")
    board.save(board_path, format="PNG")
    print(f"  [+] Saved Comparison Board: {board_path}")
    print("=" * 80)

if __name__ == "__main__":
    main()
