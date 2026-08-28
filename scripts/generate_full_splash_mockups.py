"""
Generate Full Splash Screen Mockups for Both Variations
Generates complete mobile splash screens with PlayIT typography, tagline, and 3D Gummy Button.
"""

from PIL import Image, ImageDraw, ImageFont
import os

WIDTH = 1080
HEIGHT = 2400

def draw_thick_arc(draw_ctx, bbox, start_deg, end_deg, color, width):
    draw_ctx.arc(bbox, start=start_deg, end=end_deg, fill=color, width=width)

BG_WHITE = (255, 255, 255, 255)
MANGO = (250, 123, 40, 255)          # #FA7B28 Headspace iconic warm orange
MANGO_DARK = (217, 95, 20, 255)      # Depth
MANGO_CREAM = (255, 235, 205, 255)   # #FFEBCD soft belly/face cream
EAR_INNER = (255, 175, 120, 255)     # #FFAF78 soft inner ear peach
LINE_DARK = (45, 55, 62, 255)        # #2D373E dark slate charcoal
INK_PRIMARY = (31, 58, 61, 255)      # #1F3A3D
INK_SOFT = (92, 118, 121, 255)       # #5C7679
UBE_BTN = (139, 95, 191, 255)        # #8B5FBF
UBE_SHADOW = (92, 50, 142, 255)      # #5C328E

def try_load_font(font_path, size):
    try:
        return ImageFont.truetype(font_path, size)
    except Exception:
        try:
            return ImageFont.truetype("arial.ttf", size)
        except Exception:
            return ImageFont.load_default()

def draw_splash_header(draw, title="PlayIT", subtitle="Breathe in, learn phonics."):
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    font_bold = try_load_font(os.path.join(base_dir, "app", "src", "main", "assets", "fonts", "Lexend-ExtraBold.ttf"), 110)
    font_semi = try_load_font(os.path.join(base_dir, "app", "src", "main", "assets", "fonts", "Lexend-SemiBold.ttf"), 52)

    # Wordmark
    draw.text((WIDTH / 2, HEIGHT * 0.16), title, font=font_bold, fill=INK_PRIMARY, anchor="mm")
    # Subtitle
    draw.text((WIDTH / 2, HEIGHT * 0.22), subtitle, font=font_semi, fill=INK_SOFT, anchor="mm")

def draw_gummy_button(draw, text="Simulan Natin • Start"):
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    font_btn = try_load_font(os.path.join(base_dir, "app", "src", "main", "assets", "fonts", "Lexend-Bold.ttf"), 54)
    
    btn_w = 880
    btn_h = 160
    btn_x = (WIDTH - btn_w) / 2
    btn_y = HEIGHT * 0.88
    radius = 45

    # 3D Gummy Shadow Layer
    draw.rounded_rectangle([btn_x, btn_y + 16, btn_x + btn_w, btn_y + btn_h + 16], radius=radius, fill=(180, 70, 10, 255))
    # Top Layer (Bright White with Dark Ink text)
    draw.rounded_rectangle([btn_x, btn_y, btn_x + btn_w, btn_y + btn_h], radius=radius, fill=BG_WHITE)
    # Outline
    draw.rounded_rectangle([btn_x, btn_y, btn_x + btn_w, btn_y + btn_h], radius=radius, outline=LINE_DARK, width=6)
    # Button Text
    draw.text((WIDTH / 2, btn_y + btn_h / 2), text, font=font_btn, fill=LINE_DARK, anchor="mm")

# ==========================================
# VARIATION 1: Pure Minimal Full Screen
# ==========================================
def render_full_minimal():
    img = Image.new("RGBA", (WIDTH, HEIGHT), BG_WHITE)
    draw = ImageDraw.Draw(img)

    draw_splash_header(draw, title="PlayIT", subtitle="Mabuhay! Ready to learn to read?")

    # Dome properties
    dome_cx = WIDTH / 2.0
    dome_top_y = HEIGHT * 0.48
    dome_radius_x = WIDTH * 0.72
    dome_radius_y = HEIGHT * 0.48
    dome_cy = dome_top_y + dome_radius_y

    # Ears
    ear_w, ear_h = 175, 195
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

    # Dome Body
    draw.ellipse([dome_cx - dome_radius_x, dome_cy - dome_radius_y,
                  dome_cx + dome_radius_x, dome_cy + dome_radius_y], fill=MANGO)

    # Line features
    stroke_w = 28
    eye_y = dome_top_y + 115
    eye_spacing = 135
    eye_w, eye_h = 130, 75

    draw_thick_arc(draw,
        [dome_cx - eye_spacing - eye_w/2, eye_y - eye_h/2,
         dome_cx - eye_spacing + eye_w/2, eye_y + eye_h/2],
        start_deg=10, end_deg=170, color=LINE_DARK, width=stroke_w)

    draw_thick_arc(draw,
        [dome_cx + eye_spacing - eye_w/2, eye_y - eye_h/2,
         dome_cx + eye_spacing + eye_w/2, eye_y + eye_h/2],
        start_deg=10, end_deg=170, color=LINE_DARK, width=stroke_w)

    # Smile
    mouth_y = eye_y + 115
    mouth_w, mouth_h = 200, 85
    draw_thick_arc(draw,
        [dome_cx - mouth_w/2, mouth_y - mouth_h/2,
         dome_cx + mouth_w/2, mouth_y + mouth_h/2],
        start_deg=15, end_deg=165, color=LINE_DARK, width=stroke_w)

    draw_gummy_button(draw)
    return img

# ==========================================
# VARIATION 2: Big Gentle Eyes Full Screen
# ==========================================
def render_full_big_eyes():
    img = Image.new("RGBA", (WIDTH, HEIGHT), BG_WHITE)
    draw = ImageDraw.Draw(img)

    draw_splash_header(draw, title="PlayIT", subtitle="Mabuhay! Ready to learn to read?")

    dome_cx = WIDTH / 2.0
    dome_top_y = HEIGHT * 0.48
    dome_radius_x = WIDTH * 0.72
    dome_radius_y = HEIGHT * 0.48
    dome_cy = dome_top_y + dome_radius_y

    ear_w, ear_h = 175, 195
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

    draw.ellipse([dome_cx - dome_radius_x, dome_cy - dome_radius_y,
                  dome_cx + dome_radius_x, dome_cy + dome_radius_y], fill=MANGO)

    # Big Tarsier Eye Patches
    patch_y = dome_top_y + 125
    patch_spacing = 140
    patch_r = 95
    draw.ellipse([dome_cx - patch_spacing - patch_r, patch_y - patch_r,
                  dome_cx - patch_spacing + patch_r, patch_y + patch_r], fill=MANGO_CREAM)
    draw.ellipse([dome_cx + patch_spacing - patch_r, patch_y - patch_r,
                  dome_cx + patch_spacing + patch_r, patch_y + patch_r], fill=MANGO_CREAM)

    eye_r = 55
    draw.ellipse([dome_cx - patch_spacing - eye_r, patch_y - eye_r,
                  dome_cx - patch_spacing + eye_r, patch_y + eye_r], fill=LINE_DARK)
    draw.ellipse([dome_cx + patch_spacing - eye_r, patch_y - eye_r,
                  dome_cx + patch_spacing + eye_r, patch_y + eye_r], fill=LINE_DARK)

    cl_r = 18
    draw.ellipse([dome_cx - patch_spacing - 12 - cl_r, patch_y - 12 - cl_r,
                  dome_cx - patch_spacing - 12 + cl_r, patch_y - 12 + cl_r], fill=BG_WHITE)
    draw.ellipse([dome_cx + patch_spacing - 12 - cl_r, patch_y - 12 - cl_r,
                  dome_cx + patch_spacing - 12 + cl_r, patch_y - 12 + cl_r], fill=BG_WHITE)

    stroke_w = 26
    mouth_y = patch_y + 115
    mouth_w, mouth_h = 175, 75
    draw_thick_arc(draw,
        [dome_cx - mouth_w/2, mouth_y - mouth_h/2,
         dome_cx + mouth_w/2, mouth_y + mouth_h/2],
        start_deg=20, end_deg=160, color=LINE_DARK, width=stroke_w)

    draw_gummy_button(draw)
    return img

if __name__ == "__main__":
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    screenshots_dir = os.path.join(base_dir, "screenshots")

    img_min = render_full_minimal()
    img_min.save(os.path.join(screenshots_dir, "splash_mockup_variation_a_minimal.png"), "PNG", optimize=True)

    img_big = render_full_big_eyes()
    img_big.save(os.path.join(screenshots_dir, "splash_mockup_variation_b_big_eyes.png"), "PNG", optimize=True)

    print("[+] Successfully generated full splash screen mockups for both variations!")
