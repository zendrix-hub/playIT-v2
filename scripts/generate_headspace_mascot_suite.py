"""
PlayIT Mascot Suite Generator — Khan Academy Kids x Headspace Blend
Generates all 7 production companion mascot poses (Lily the Tarsier)
infused with Khan Academy Kids' warm, storybook character charm:
- Rosy pink cheek glow
- Oversized glossy expressive eyes with double catchlights
- Friendly cuddly pear-shaped body & soft cream tummy
- Authentic slender tarsier tail with fluffy brush tip
- Clean organic limbs and defined mitten paws
"""

from PIL import Image, ImageDraw
import math
import os

SIZE = 512
SCALE = 2
W = SIZE * SCALE
H = SIZE * SCALE

# Khan Academy Kids Blended Color Palette
MANGO = (250, 123, 40, 255)          # #FA7B28 Warm Vibrant Orange
MANGO_DARK = (215, 95, 20, 255)      # Arm / Tail Shade
CREAM = (255, 238, 215, 255)         # #FFEED7 Soft Storybook Cream
PEACH_EAR = (255, 175, 125, 255)     # #FFAF7D Inner Ear Peach
ROSY_CHEEK = (255, 140, 140, 220)    # Khan Academy Kids Cheerful Blush
SLATE_DARK = (60, 36, 21, 255)       # #3C2415 DarkEspressoOutline (Style Guide 16 §2.1)
WHITE = (255, 255, 255, 255)
GOLD_STAR = (255, 193, 7, 255)       # Gold Celebration Sparkle

# Headphones Palette
HEADPHONE_BAND = (92, 107, 192, 255)    # #5C6BC0 Royal Indigo
HEADPHONE_CUSHION = (63, 81, 181, 255)  # #3F51B5 Deep Indigo
HEADPHONE_RING = (197, 202, 233, 255)   # #C5CAE9 Soft Accent Glow
HEADPHONE_ACCENT = (255, 193, 7, 255)   # Gold Pivot Dot

def draw_thick_arc(draw_ctx, bbox, start_deg, end_deg, color, width):
    draw_ctx.arc(bbox, start=start_deg, end=end_deg, fill=color, width=width)

def draw_thick_line(draw_ctx, start, end, color, width):
    draw_ctx.line([start, end], fill=color, width=width, joint="curve")

def draw_pointing_hand(draw, wrist_x, wrist_y):
    """
    Classic cartoon pointing hand with clear extended index finger and folded thumb.
    """
    fist_rx, fist_ry = 26, 26
    draw.ellipse([wrist_x - fist_rx - 4, wrist_y - fist_ry - 4, wrist_x + fist_rx + 4, wrist_y + fist_ry + 4], fill=SLATE_DARK)
    draw.ellipse([wrist_x - fist_rx, wrist_y - fist_ry, wrist_x + fist_rx, wrist_y + fist_ry], fill=CREAM)

    finger_start = (wrist_x + 10, wrist_y - 10)
    finger_end = (wrist_x + 72, wrist_y - 10)
    draw_thick_line(draw, finger_start, finger_end, SLATE_DARK, 26)
    draw_thick_line(draw, finger_start, finger_end, CREAM, 18)
    draw.ellipse([finger_end[0] - 9, finger_end[1] - 9, finger_end[0] + 9, finger_end[1] + 9], fill=CREAM)

    thumb_x = wrist_x + 4
    thumb_y = wrist_y - 18
    draw.ellipse([thumb_x - 12, thumb_y - 10, thumb_x + 12, thumb_y + 10], fill=CREAM, outline=SLATE_DARK, width=5)

    draw_thick_line(draw, (wrist_x + 4, wrist_y + 4), (wrist_x + 22, wrist_y + 4), SLATE_DARK, 4)
    draw_thick_line(draw, (wrist_x + 2, wrist_y + 16), (wrist_x + 18, wrist_y + 16), SLATE_DARK, 4)

def draw_organic_arm_and_paw(draw, shoulder, wrist, paw_angle=0, pointing=False, open_hand=False):
    draw_thick_line(draw, shoulder, wrist, SLATE_DARK, 58)
    draw_thick_line(draw, shoulder, wrist, MANGO, 46)

    if pointing:
        draw_pointing_hand(draw, wrist[0], wrist[1])
        return

    rad = math.radians(paw_angle)
    cos_a = math.cos(rad)
    sin_a = math.sin(rad)

    def rot(dx, dy):
        return (wrist[0] + dx * cos_a - dy * sin_a, wrist[1] + dx * sin_a + dy * cos_a)

    paw_r = 30
    draw.ellipse([wrist[0] - paw_r - 4, wrist[1] - paw_r - 4, wrist[0] + paw_r + 4, wrist[1] + paw_r + 4], fill=SLATE_DARK)
    draw.ellipse([wrist[0] - paw_r, wrist[1] - paw_r, wrist[0] + paw_r, wrist[1] + paw_r], fill=CREAM)

    if open_hand:
        for offset in [-16, 0, 16]:
            f_tip = rot(offset, paw_r + 12)
            draw.ellipse([f_tip[0] - 11, f_tip[1] - 11, f_tip[0] + 11, f_tip[1] + 11], fill=CREAM, outline=SLATE_DARK, width=4)
    else:
        thumb_pos = rot(-paw_r * 0.7, -paw_r * 0.5)
        draw.ellipse([thumb_pos[0] - 12, thumb_pos[1] - 12, thumb_pos[0] + 12, thumb_pos[1] + 12], fill=CREAM, outline=SLATE_DARK, width=4)
        draw_thick_line(draw, rot(-7, paw_r - 6), rot(-7, paw_r + 4), SLATE_DARK, 4)
        draw_thick_line(draw, rot(7, paw_r - 6), rot(7, paw_r + 4), SLATE_DARK, 4)

def draw_realistic_tarsier_tail(draw, cx):
    start_x, start_y = cx - 140, H * 0.76
    ctrl1_x, ctrl1_y = cx - 270, H * 0.78
    ctrl2_x, ctrl2_y = cx - 340, H * 0.58
    end_x, end_y = cx - 280, H * 0.44

    steps = 40
    tail_points = []
    for i in range(steps + 1):
        t = i / float(steps)
        x = (1-t)**3 * start_x + 3*(1-t)**2*t * ctrl1_x + 3*(1-t)*t**2 * ctrl2_x + t**3 * end_x
        y = (1-t)**3 * start_y + 3*(1-t)**2*t * ctrl1_y + 3*(1-t)*t**2 * ctrl2_y + t**3 * end_y
        tail_points.append((x, y))

    for i in range(len(tail_points) - 1):
        draw_thick_line(draw, tail_points[i], tail_points[i+1], SLATE_DARK, 28)
    for i in range(len(tail_points) - 1):
        draw_thick_line(draw, tail_points[i], tail_points[i+1], MANGO, 20)

    tip_x, tip_y = end_x, end_y
    draw.ellipse([tip_x - 24, tip_y - 36, tip_x + 24, tip_y + 36], fill=SLATE_DARK)
    draw.ellipse([tip_x - 20, tip_y - 32, tip_x + 20, tip_y + 32], fill=MANGO)
    draw.ellipse([tip_x - 12, tip_y - 22, tip_x + 12, tip_y + 22], fill=CREAM)
    draw.ellipse([tip_x - 30, tip_y - 15, tip_x + 6, tip_y + 25], fill=MANGO, outline=SLATE_DARK, width=4)
    draw.ellipse([tip_x - 6, tip_y - 20, tip_x + 30, tip_y + 20], fill=MANGO, outline=SLATE_DARK, width=4)

def draw_base_tarsier(draw, head_tilt=0, eyes_state="open", eye_offset=(0, 0), smile_type="normal", hands_pose="idle"):
    cx = W / 2.0
    
    # 1. Realistic Tail
    draw_realistic_tarsier_tail(draw, cx)

    # 2. Feet with soft pads
    foot_w, foot_h = 82, 48
    draw.ellipse([cx - 135, H * 0.83, cx - 135 + foot_w, H * 0.83 + foot_h], fill=MANGO, outline=SLATE_DARK, width=6)
    draw.ellipse([cx + 55, H * 0.83, cx + 55 + foot_w, H * 0.83 + foot_h], fill=MANGO, outline=SLATE_DARK, width=6)

    # 3. Soft Pear Body
    body_w, body_h = 220, 210
    body_cy = H * 0.66
    draw.ellipse([cx - body_w, body_cy - body_h, cx + body_w, body_cy + body_h], fill=MANGO, outline=SLATE_DARK, width=8)
    # Belly Cream Patch
    belly_w, belly_h = 135, 125
    draw.ellipse([cx - belly_w, body_cy - belly_h + 30, cx + belly_w, body_cy + belly_h + 30], fill=CREAM)

    # 4. Head
    hcx = cx + head_tilt * 15
    hcy = H * 0.36 + abs(head_tilt) * 5
    head_rx, head_ry = 230, 205

    # Ears
    ear_w, ear_h = 115, 135
    left_ear_cx = hcx - 175
    left_ear_cy = hcy - 120
    right_ear_cx = hcx + 175
    right_ear_cy = hcy - 120

    draw.ellipse([left_ear_cx - ear_w, left_ear_cy - ear_h, left_ear_cx + ear_w, left_ear_cy + ear_h], fill=MANGO, outline=SLATE_DARK, width=7)
    draw.ellipse([right_ear_cx - ear_w, right_ear_cy - ear_h, right_ear_cx + ear_w, right_ear_cy + ear_h], fill=MANGO, outline=SLATE_DARK, width=7)
    draw.ellipse([left_ear_cx - ear_w * 0.62, left_ear_cy - ear_h * 0.65, left_ear_cx + ear_w * 0.62, left_ear_cy + ear_h * 0.65], fill=PEACH_EAR)
    draw.ellipse([right_ear_cx - ear_w * 0.62, right_ear_cy - ear_h * 0.65, right_ear_cx + ear_w * 0.62, right_ear_cy + ear_h * 0.65], fill=PEACH_EAR)

    # Head Circle
    draw.ellipse([hcx - head_rx, hcy - head_ry, hcx + head_rx, hcy + head_ry], fill=MANGO, outline=SLATE_DARK, width=8)

    # 5. Cream Eye Patches
    patch_spacing = 90
    patch_r = 75
    patch_y = hcy - 10
    draw.ellipse([hcx - patch_spacing - patch_r, patch_y - patch_r, hcx - patch_spacing + patch_r, patch_y + patch_r], fill=CREAM)
    draw.ellipse([hcx + patch_spacing - patch_r, patch_y - patch_r, hcx + patch_spacing + patch_r, patch_y + patch_r], fill=CREAM)

    # 6. Khan Academy Kids Rosy Cheek Glow
    blush_y = patch_y + 70
    draw.ellipse([hcx - 170 - 32, blush_y - 20, hcx - 170 + 32, blush_y + 20], fill=ROSY_CHEEK)
    draw.ellipse([hcx + 170 - 32, blush_y - 20, hcx + 170 + 32, blush_y + 20], fill=ROSY_CHEEK)

    # 7. Eyes
    stroke_w = 20
    if eyes_state == "open":
        pupil_r = 46
        lx = hcx - patch_spacing + eye_offset[0] * 12
        ly = patch_y + eye_offset[1] * 12
        rx = hcx + patch_spacing + eye_offset[0] * 12
        ry = patch_y + eye_offset[1] * 12

        draw.ellipse([lx - pupil_r, ly - pupil_r, lx + pupil_r, ly + pupil_r], fill=SLATE_DARK)
        draw.ellipse([rx - pupil_r, ry - pupil_r, rx + pupil_r, ry + pupil_r], fill=SLATE_DARK)
        # Specular Highlights
        draw.ellipse([lx - 16 - 15, ly - 16 - 15, lx - 16 + 15, ly - 16 + 15], fill=WHITE)
        draw.ellipse([rx - 16 - 15, ry - 16 - 15, rx - 16 + 15, ry - 16 + 15], fill=WHITE)
        draw.ellipse([lx + 14 - 7, ly + 14 - 7, lx + 14 + 7, ly + 14 + 7], fill=WHITE)
        draw.ellipse([rx + 14 - 7, ry + 14 - 7, rx + 14 + 7, ry + 14 + 7], fill=WHITE)
    elif eyes_state == "closed_happy":
        draw_thick_arc(draw, [hcx - patch_spacing - 50, patch_y - 25, hcx - patch_spacing + 50, patch_y + 25],
                       start_deg=10, end_deg=170, color=SLATE_DARK, width=stroke_w)
        draw_thick_arc(draw, [hcx + patch_spacing - 50, patch_y - 25, hcx + patch_spacing + 50, patch_y + 25],
                       start_deg=10, end_deg=170, color=SLATE_DARK, width=stroke_w)
    elif eyes_state == "wink":
        draw_thick_arc(draw, [hcx - patch_spacing - 50, patch_y - 25, hcx - patch_spacing + 50, patch_y + 25],
                       start_deg=10, end_deg=170, color=SLATE_DARK, width=stroke_w)
        pupil_r = 46
        rx = hcx + patch_spacing
        ry = patch_y
        draw.ellipse([rx - pupil_r, ry - pupil_r, rx + pupil_r, ry + pupil_r], fill=SLATE_DARK)
        draw.ellipse([rx - 16 - 15, ry - 16 - 15, rx - 16 + 15, ry - 16 + 15], fill=WHITE)
        draw.ellipse([rx + 14 - 7, ry + 14 - 7, rx + 14 + 7, ry + 14 + 7], fill=WHITE)

    # 8. Nose & Friendly Smile
    nose_y = patch_y + 60
    draw.ellipse([hcx - 10, nose_y - 8, hcx + 10, nose_y + 8], fill=SLATE_DARK)

    mouth_y = nose_y + 35
    if smile_type == "big":
        draw_thick_arc(draw, [hcx - 85, mouth_y - 35, hcx + 85, mouth_y + 40],
                       start_deg=15, end_deg=165, color=SLATE_DARK, width=stroke_w)
    elif smile_type == "open":
        draw.chord([hcx - 70, mouth_y - 15, hcx + 70, mouth_y + 65], start=0, end=180, fill=(230, 80, 80, 255), outline=SLATE_DARK, width=12)
    else:
        draw_thick_arc(draw, [hcx - 65, mouth_y - 25, hcx + 65, mouth_y + 28],
                       start_deg=15, end_deg=165, color=SLATE_DARK, width=stroke_w)

    # 9. Organic Limbs & Mitten Paws
    l_shoulder = (cx - 150, H * 0.58)
    r_shoulder = (cx + 150, H * 0.58)

    if hands_pose == "idle":
        draw_organic_arm_and_paw(draw, l_shoulder, (cx - 65, H * 0.65), paw_angle=40)
        draw_organic_arm_and_paw(draw, r_shoulder, (cx + 65, H * 0.65), paw_angle=-40)

    elif hands_pose == "waving":
        draw_organic_arm_and_paw(draw, l_shoulder, (cx - 65, H * 0.65), paw_angle=40)
        draw_organic_arm_and_paw(draw, r_shoulder, (cx + 250, H * 0.38), paw_angle=-55, open_hand=True)

    elif hands_pose == "pointing":
        draw_organic_arm_and_paw(draw, l_shoulder, (cx - 65, H * 0.65), paw_angle=40)
        draw_organic_arm_and_paw(draw, r_shoulder, (cx + 240, H * 0.56), pointing=True)

    elif hands_pose == "celebrating":
        l_paw = (cx - 230, H * 0.24)
        r_paw = (cx + 230, H * 0.24)
        draw_organic_arm_and_paw(draw, l_shoulder, l_paw, paw_angle=-135, open_hand=True)
        draw_organic_arm_and_paw(draw, r_shoulder, r_paw, paw_angle=135, open_hand=True)
        draw.ellipse([l_paw[0] - 25 - 16, l_paw[1] - 30 - 16, l_paw[0] - 25 + 16, l_paw[1] - 30 + 16], fill=GOLD_STAR)
        draw.ellipse([r_paw[0] + 25 - 16, r_paw[1] - 30 - 16, r_paw[0] + 25 + 16, r_paw[1] - 30 + 16], fill=GOLD_STAR)

    elif hands_pose == "encouraging":
        draw_organic_arm_and_paw(draw, l_shoulder, (cx - 160, H * 0.66), paw_angle=60, open_hand=True)
        draw_organic_arm_and_paw(draw, r_shoulder, (cx + 40, H * 0.62), paw_angle=-45)

    elif hands_pose == "thinking":
        draw_organic_arm_and_paw(draw, l_shoulder, (cx - 160, H * 0.68), paw_angle=70)
        draw_organic_arm_and_paw(draw, r_shoulder, (hcx + 60, hcy + 115), paw_angle=-80)

    elif hands_pose == "listening":
        draw.arc([hcx - 220, hcy - 220, hcx + 220, hcy + 15], start=180, end=360, fill=HEADPHONE_BAND, width=34)
        draw.arc([hcx - 220, hcy - 220, hcx + 220, hcy + 15], start=180, end=360, fill=HEADPHONE_RING, width=12)

        l_cup_x, l_cup_y = hcx - 195, hcy - 15
        draw.ellipse([l_cup_x - 36, l_cup_y - 54, l_cup_x + 36, l_cup_y + 54], fill=HEADPHONE_CUSHION, outline=SLATE_DARK, width=6)
        draw.ellipse([l_cup_x - 24, l_cup_y - 40, l_cup_x + 24, l_cup_y + 40], fill=HEADPHONE_BAND)
        draw.ellipse([l_cup_x - 12, l_cup_y - 20, l_cup_x + 12, l_cup_y + 20], fill=HEADPHONE_RING)
        draw.ellipse([l_cup_x - 7, l_cup_y - 7, l_cup_x + 7, l_cup_y + 7], fill=HEADPHONE_ACCENT)

        r_cup_x, r_cup_y = hcx + 195, hcy - 15
        draw.ellipse([r_cup_x - 36, r_cup_y - 54, r_cup_x + 36, r_cup_y + 54], fill=HEADPHONE_CUSHION, outline=SLATE_DARK, width=6)
        draw.ellipse([r_cup_x - 24, r_cup_y - 40, r_cup_x + 24, r_cup_y + 40], fill=HEADPHONE_BAND)
        draw.ellipse([r_cup_x - 12, r_cup_y - 20, r_cup_x + 12, r_cup_y + 20], fill=HEADPHONE_RING)
        draw.ellipse([r_cup_x - 7, r_cup_y - 7, r_cup_x + 7, r_cup_y + 7], fill=HEADPHONE_ACCENT)

        draw_organic_arm_and_paw(draw, l_shoulder, (cx - 65, H * 0.65), paw_angle=40)
        draw_organic_arm_and_paw(draw, r_shoulder, (cx + 65, H * 0.65), paw_angle=-40)

def generate_pose(name, head_tilt=0, eyes_state="open", eye_offset=(0,0), smile_type="normal", hands_pose="idle"):
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    draw_base_tarsier(draw, head_tilt=head_tilt, eyes_state=eyes_state, eye_offset=eye_offset, smile_type=smile_type, hands_pose=hands_pose)
    final_img = img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)
    return final_img

def main():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    target_assets_dir = os.path.join(base_dir, "app", "src", "main", "assets", "images", "mascot")
    preview_dir = os.path.join(base_dir, "screenshots", "mascot_suite")
    os.makedirs(target_assets_dir, exist_ok=True)
    os.makedirs(preview_dir, exist_ok=True)

    poses = {
        "lily_idle": {"head_tilt": 0, "eyes_state": "open", "eye_offset": (0, 0), "smile_type": "normal", "hands_pose": "idle"},
        "lily_waving": {"head_tilt": -0.2, "eyes_state": "open", "eye_offset": (0, 0), "smile_type": "big", "hands_pose": "waving"},
        "lily_pointing": {"head_tilt": 0.2, "eyes_state": "open", "eye_offset": (0.8, 0), "smile_type": "normal", "hands_pose": "pointing"},
        "lily_celebrating": {"head_tilt": 0, "eyes_state": "closed_happy", "eye_offset": (0, 0), "smile_type": "open", "hands_pose": "celebrating"},
        "lily_encouraging": {"head_tilt": 0.3, "eyes_state": "wink", "eye_offset": (0, 0), "smile_type": "big", "hands_pose": "encouraging"},
        "lily_thinking": {"head_tilt": -0.4, "eyes_state": "open", "eye_offset": (0, -0.8), "smile_type": "normal", "hands_pose": "thinking"},
        "lily_listening": {"head_tilt": 0, "eyes_state": "closed_happy", "eye_offset": (0, 0), "smile_type": "normal", "hands_pose": "listening"},
    }

    print("[*] Generating all 7 mascot poses with Khan Academy Kids character warmth...")
    for filename, config in poses.items():
        img = generate_pose(filename, **config)
        img.save(os.path.join(target_assets_dir, f"{filename}.png"), "PNG", optimize=True)
        img.save(os.path.join(preview_dir, f"{filename}.png"), "PNG", optimize=True)
        print(f"  [+] Generated: {filename}.png")

    print("[*] Mascot suite assets successfully generated and synced!")

if __name__ == "__main__":
    main()
