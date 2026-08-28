"""
Headspace-Style Tarsier Splash Art Variations Generator
Matches Headspace's iconic minimalist design language with PlayIT's Bohol tarsier mascot.
"""

from PIL import Image, ImageDraw
import os

WIDTH = 1080
HEIGHT = 2400

def draw_thick_arc(draw_ctx, bbox, start_deg, end_deg, color, width):
    draw_ctx.arc(bbox, start=start_deg, end=end_deg, fill=color, width=width)

# Theme Palette (Headspace warm amber + PlayIT mango/tan)
BG_WHITE = (255, 255, 255, 255)
MANGO = (250, 123, 40, 255)          # #FA7B28 Headspace iconic warm orange
MANGO_CREAM = (255, 235, 205, 255)   # #FFEBCD soft belly/face cream
EAR_INNER = (255, 175, 120, 255)     # #FFAF78 soft inner ear peach
LINE_DARK = (45, 55, 62, 255)        # #2D373E dark slate charcoal

# ==========================================
# VARIATION 1: Pure Minimal Headspace Tarsier (Closed Serene Eyes)
# ==========================================
def generate_var_pure_minimal():
    img = Image.new("RGBA", (WIDTH, HEIGHT), BG_WHITE)
    draw = ImageDraw.Draw(img)

    # Dome properties
    dome_cx = WIDTH / 2.0
    dome_top_y = HEIGHT * 0.58       # Dome crest rises to 58% of screen
    dome_radius_x = WIDTH * 0.72
    dome_radius_y = HEIGHT * 0.48
    dome_cy = dome_top_y + dome_radius_y

    # Ears (positioned on top-left and top-right of head dome)
    ear_w = 170
    ear_h = 190
    left_ear_center = (WIDTH * 0.22, dome_top_y + 40)
    right_ear_center = (WIDTH * 0.78, dome_top_y + 40)

    # Draw outer ears
    draw.ellipse([left_ear_center[0] - ear_w, left_ear_center[1] - ear_h,
                  left_ear_center[0] + ear_w, left_ear_center[1] + ear_h], fill=MANGO)
    draw.ellipse([right_ear_center[0] - ear_w, right_ear_center[1] - ear_h,
                  right_ear_center[0] + ear_w, right_ear_center[1] + ear_h], fill=MANGO)

    # Draw inner ears
    inner_w, inner_h = 105, 125
    draw.ellipse([left_ear_center[0] - inner_w, left_ear_center[1] - inner_h,
                  left_ear_center[0] + inner_w, left_ear_center[1] + inner_h], fill=EAR_INNER)
    draw.ellipse([right_ear_center[0] - inner_w, right_ear_center[1] - inner_h,
                  right_ear_center[0] + inner_w, right_ear_center[1] + inner_h], fill=EAR_INNER)

    # Draw main Head Dome (covers lower half of ears seamlessly)
    draw.ellipse([dome_cx - dome_radius_x, dome_cy - dome_radius_y,
                  dome_cx + dome_radius_x, dome_cy + dome_radius_y], fill=MANGO)

    # Facial Features (drawn directly on the orange dome)
    stroke_w = 28
    eye_y = dome_top_y + 105
    eye_spacing = 135
    eye_w = 130
    eye_h = 75

    # Left eye arc (smiling closed eye)
    draw_thick_arc(draw,
        [dome_cx - eye_spacing - eye_w/2, eye_y - eye_h/2,
         dome_cx - eye_spacing + eye_w/2, eye_y + eye_h/2],
        start_deg=10, end_deg=170, color=LINE_DARK, width=stroke_w)

    # Right eye arc
    draw_thick_arc(draw,
        [dome_cx + eye_spacing - eye_w/2, eye_y - eye_h/2,
         dome_cx + eye_spacing + eye_w/2, eye_y + eye_h/2],
        start_deg=10, end_deg=170, color=LINE_DARK, width=stroke_w)

    # Smile Arc
    mouth_y = eye_y + 105
    mouth_w = 200
    mouth_h = 80
    draw_thick_arc(draw,
        [dome_cx - mouth_w/2, mouth_y - mouth_h/2,
         dome_cx + mouth_w/2, mouth_y + mouth_h/2],
        start_deg=15, end_deg=165, color=LINE_DARK, width=stroke_w)

    return img

# ==========================================
# VARIATION 2: Iconic Tarsier Big Gentle Eyes
# ==========================================
def generate_var_big_eyes():
    img = Image.new("RGBA", (WIDTH, HEIGHT), BG_WHITE)
    draw = ImageDraw.Draw(img)

    dome_cx = WIDTH / 2.0
    dome_top_y = HEIGHT * 0.58
    dome_radius_x = WIDTH * 0.72
    dome_radius_y = HEIGHT * 0.48
    dome_cy = dome_top_y + dome_radius_y

    # Ears
    ear_w, ear_h = 170, 190
    left_ear_center = (WIDTH * 0.22, dome_top_y + 40)
    right_ear_center = (WIDTH * 0.78, dome_top_y + 40)

    draw.ellipse([left_ear_center[0] - ear_w, left_ear_center[1] - ear_h,
                  left_ear_center[0] + ear_w, left_ear_center[1] + ear_h], fill=MANGO)
    draw.ellipse([right_ear_center[0] - ear_w, right_ear_center[1] - ear_h,
                  right_ear_center[0] + ear_w, right_ear_center[1] + ear_h], fill=MANGO)

    inner_w, inner_h = 105, 125
    draw.ellipse([left_ear_center[0] - inner_w, left_ear_center[1] - inner_h,
                  left_ear_center[0] + inner_w, left_ear_center[1] + inner_h], fill=EAR_INNER)
    draw.ellipse([right_ear_center[0] - inner_w, right_ear_center[1] - inner_h,
                  right_ear_center[0] + inner_w, right_ear_center[1] + inner_h], fill=EAR_INNER)

    # Main Dome
    draw.ellipse([dome_cx - dome_radius_x, dome_cy - dome_radius_y,
                  dome_cx + dome_radius_x, dome_cy + dome_radius_y], fill=MANGO)

    # Big Tarsier Eye Patches (Soft Cream)
    patch_y = dome_top_y + 115
    patch_spacing = 140
    patch_r = 95
    draw.ellipse([dome_cx - patch_spacing - patch_r, patch_y - patch_r,
                  dome_cx - patch_spacing + patch_r, patch_y + patch_r], fill=MANGO_CREAM)
    draw.ellipse([dome_cx + patch_spacing - patch_r, patch_y - patch_r,
                  dome_cx + patch_spacing + patch_r, patch_y + patch_r], fill=MANGO_CREAM)

    # Big Curious Eyes (Dark Slate with soft catchlight)
    eye_r = 55
    draw.ellipse([dome_cx - patch_spacing - eye_r, patch_y - eye_r,
                  dome_cx - patch_spacing + eye_r, patch_y + eye_r], fill=LINE_DARK)
    draw.ellipse([dome_cx + patch_spacing - eye_r, patch_y - eye_r,
                  dome_cx + patch_spacing + eye_r, patch_y + eye_r], fill=LINE_DARK)

    # Catchlights
    cl_r = 18
    draw.ellipse([dome_cx - patch_spacing - 12 - cl_r, patch_y - 12 - cl_r,
                  dome_cx - patch_spacing - 12 + cl_r, patch_y - 12 + cl_r], fill=BG_WHITE)
    draw.ellipse([dome_cx + patch_spacing - 12 - cl_r, patch_y - 12 - cl_r,
                  dome_cx + patch_spacing - 12 + cl_r, patch_y - 12 + cl_r], fill=BG_WHITE)

    # Cute Smile
    stroke_w = 26
    mouth_y = patch_y + 110
    mouth_w = 170
    mouth_h = 70
    draw_thick_arc(draw,
        [dome_cx - mouth_w/2, mouth_y - mouth_h/2,
         dome_cx + mouth_w/2, mouth_y + mouth_h/2],
        start_deg=20, end_deg=160, color=LINE_DARK, width=stroke_w)

    return img

if __name__ == "__main__":
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    screenshots_dir = os.path.join(base_dir, "screenshots")
    assets_dir = os.path.join(base_dir, "app", "src", "main", "assets", "images", "mascot")
    os.makedirs(screenshots_dir, exist_ok=True)
    os.makedirs(assets_dir, exist_ok=True)

    img1 = generate_var_pure_minimal()
    img1.save(os.path.join(screenshots_dir, "headspace_tarsier_minimal.png"), "PNG", optimize=True)
    img1.save(os.path.join(assets_dir, "splash_tarsier_headspace.png"), "PNG", optimize=True)

    img2 = generate_var_big_eyes()
    img2.save(os.path.join(screenshots_dir, "headspace_tarsier_big_eyes.png"), "PNG", optimize=True)

    print("[+] Generated Headspace-style Tarsier artworks successfully!")
