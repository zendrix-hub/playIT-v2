"""
Organic Pediatric Vector Illustrator for PlayIT Blend It Words (Natural Human Anatomy & Waving Pose)
"""

from PIL import Image, ImageDraw, ImageFilter
import math
import os

SIZE = 512
SCALE = 3
W = SIZE * SCALE
H = SIZE * SCALE

DARK = (45, 55, 62, 255)            # #2D373E Slate Charcoal
WHITE = (255, 255, 255, 255)
PINK_BLUSH = (255, 145, 155, 230)   # Rosy Cheek
SKIN = (255, 222, 192, 255)         # Warm Toddler Skin
HAIR_BROWN = (165, 100, 50, 255)    # Chestnut Hair
HAIR_DARK = (115, 65, 30, 255)
GOLD = (255, 204, 0, 255)           # Sunny Yellow
ORANGE = (250, 125, 40, 255)        # Mango Orange
RED = (255, 90, 110, 255)           # Guava Red
BLUE_SKY = (56, 189, 248, 255)      # Sky Blue
CREAM = (255, 242, 225, 255)

def stroke_layer(layer_img, stroke_width=14, stroke_color=DARK):
    alpha = layer_img.getchannel("A")
    dilated = alpha.filter(ImageFilter.MaxFilter(stroke_width * 2 + 1))
    stroke_img = Image.new("RGBA", layer_img.size, stroke_color)
    stroke_img.putalpha(dilated)
    stroke_img.alpha_composite(layer_img)
    return stroke_img

def draw_glossy_eyes(draw, lx, ly, rx, ry, r=42):
    for (ex, ey) in [(lx, ly), (rx, ry)]:
        draw.ellipse([ex - r, ey - r, ex + r, ey + r], fill=DARK)
        cr1 = r * 0.38
        draw.ellipse([ex - r*0.35 - cr1, ey - r*0.35 - cr1, ex - r*0.35 + cr1, ey - r*0.35 + cr1], fill=WHITE)
        cr2 = r * 0.18
        draw.ellipse([ex + r*0.35 - cr2, ey + r*0.35 - cr2, ex + r*0.35 + cr2, ey + r*0.35 + cr2], fill=WHITE)

def draw_blush(draw, lx, ly, rx, ry, rw=50, rh=30):
    for (cx, cy) in [(lx, ly), (rx, ry)]:
        draw.ellipse([cx - rw, cy - rh, cx + rw, cy + rh], fill=PINK_BLUSH)

# ── 1. SAM (Natural Waving Cartoon Boy) ──────────────────────────────────────
def craft_organic_sam():
    canvas = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    cx, cy = W/2, H/2 + 70

    # Right Waving Arm (Extending naturally to the right of the body)
    arm_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ad = ImageDraw.Draw(arm_layer)
    # Upper arm & forearm
    ad.line([(cx + 140, cy + 180), (cx + 280, cy + 80), (cx + 340, cy - 40)], fill=SKIN, width=105, joint="curve")
    # Waving Palm & Fingers
    hx, hy = cx + 360, cy - 70
    ad.ellipse([hx - 60, hy - 60, hx + 60, hy + 60], fill=SKIN)
    ad.rounded_rectangle([hx - 35, hy - 130, hx + 35, hy - 20], radius=35, fill=SKIN)   # Middle
    ad.rounded_rectangle([hx - 80, hy - 110, hx - 15, hy - 10], radius=30, fill=SKIN)   # Index
    ad.rounded_rectangle([hx + 15, hy - 110, hx + 80, hy - 10], radius=30, fill=SKIN)   # Ring
    ad.rounded_rectangle([hx - 110, hy - 20, hx - 20, hy + 45], radius=30, fill=SKIN)   # Thumb
    canvas.alpha_composite(stroke_layer(arm_layer, stroke_width=14, stroke_color=DARK))

    # T-Shirt Body Layer
    body_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    bd = ImageDraw.Draw(body_layer)
    bd.rounded_rectangle([cx - 210, cy + 80, cx + 210, cy + 420], radius=70, fill=BLUE_SKY)
    # Sleeves Left & Right
    bd.rounded_rectangle([cx - 260, cy + 100, cx - 140, cy + 240], radius=45, fill=BLUE_SKY)
    bd.rounded_rectangle([cx + 140, cy + 100, cx + 260, cy + 240], radius=45, fill=BLUE_SKY)
    canvas.alpha_composite(stroke_layer(body_layer, stroke_width=14, stroke_color=DARK))

    # White Stripes on Shirt
    draw = ImageDraw.Draw(canvas)
    draw.rounded_rectangle([cx - 190, cy + 180, cx + 190, cy + 240], radius=20, fill=WHITE)
    draw.rounded_rectangle([cx - 190, cy + 290, cx + 190, cy + 350], radius=20, fill=WHITE)

    # Neck
    neck = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    nd = ImageDraw.Draw(neck)
    nd.rounded_rectangle([cx - 65, cy + 20, cx + 65, cy + 110], radius=25, fill=SKIN)
    canvas.alpha_composite(stroke_layer(neck, stroke_width=12, stroke_color=DARK))

    # Head (Ears + Head Oval)
    head_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    hd = ImageDraw.Draw(head_layer)
    bx, by = cx, cy - 140
    hd.ellipse([bx - 280, by - 50, bx - 170, by + 60], fill=SKIN)
    hd.ellipse([bx + 170, by - 50, bx + 280, by + 60], fill=SKIN)
    hd.ellipse([bx - 230, by - 200, bx + 230, by + 200], fill=SKIN)
    canvas.alpha_composite(stroke_layer(head_layer, stroke_width=14, stroke_color=DARK))

    # Boy Hair (Neat rounded top + side bangs)
    hair = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    hrd = ImageDraw.Draw(hair)
    hrd.chord([bx - 240, by - 240, bx + 240, by], 180, 360, fill=HAIR_BROWN)
    for (hx, hy, hr) in [
        (bx - 140, by - 160, 110), (bx - 40, by - 210, 120),
        (bx + 60, by - 210, 120), (bx + 150, by - 160, 110),
        (bx - 90, by - 90, 85), (bx + 20, by - 80, 85), (bx + 110, by - 80, 75)
    ]:
        hrd.ellipse([hx - hr, hy - hr, hx + hr, hy + hr], fill=HAIR_BROWN)
    canvas.alpha_composite(stroke_layer(hair, stroke_width=14, stroke_color=DARK))

    # Facial Features
    draw = ImageDraw.Draw(canvas)
    draw_glossy_eyes(draw, bx - 85, by + 25, bx + 85, by + 25, r=42)
    draw_blush(draw, bx - 150, by + 75, bx + 150, by + 75, rw=48, rh=28)
    draw.chord([bx - 70, by + 85, bx + 70, by + 195], 0, 180, fill=RED, outline=DARK, width=22)
    draw.ellipse([bx - 32, by + 140, bx + 32, by + 190], fill=PINK_BLUSH)

    return canvas.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── 2. SIS (Natural Waving Cartoon Girl) ─────────────────────────────────────
def craft_organic_sis():
    canvas = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    cx, cy = W/2, H/2 + 70

    # Right Waving Arm
    arm_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ad = ImageDraw.Draw(arm_layer)
    ad.line([(cx + 140, cy + 180), (cx + 280, cy + 80), (cx + 340, cy - 40)], fill=SKIN, width=105, joint="curve")
    hx, hy = cx + 360, cy - 70
    ad.ellipse([hx - 60, hy - 60, hx + 60, hy + 60], fill=SKIN)
    ad.rounded_rectangle([hx - 35, hy - 130, hx + 35, hy - 20], radius=35, fill=SKIN)
    ad.rounded_rectangle([hx - 80, hy - 110, hx - 15, hy - 10], radius=30, fill=SKIN)
    ad.rounded_rectangle([hx + 15, hy - 110, hx + 80, hy - 10], radius=30, fill=SKIN)
    ad.rounded_rectangle([hx - 110, hy - 20, hx - 20, hy + 45], radius=30, fill=SKIN)
    canvas.alpha_composite(stroke_layer(arm_layer, stroke_width=14, stroke_color=DARK))

    # Pigtail Buns Left & Right
    pigtails = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    pd = ImageDraw.Draw(pigtails)
    bx, by = cx, cy - 140
    pd.ellipse([bx - 360, by - 120, bx - 180, by + 60], fill=HAIR_BROWN)
    pd.ellipse([bx + 180, by - 120, bx + 360, by + 60], fill=HAIR_BROWN)
    canvas.alpha_composite(stroke_layer(pigtails, stroke_width=14, stroke_color=DARK))

    # Pink Bow Clips
    bows = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    bwd = ImageDraw.Draw(bows)
    bwd.ellipse([bx - 240, by - 60, bx - 160, by + 10], fill=PINK_BLUSH)
    bwd.ellipse([bx + 160, by - 60, bx + 240, by + 10], fill=PINK_BLUSH)
    canvas.alpha_composite(stroke_layer(bows, stroke_width=12, stroke_color=DARK))

    # Yellow Dress Body
    dress_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    dd = ImageDraw.Draw(dress_layer)
    dress_pts = [(cx - 150, cy + 90), (cx + 150, cy + 90), (cx + 250, cy + 420), (cx - 250, cy + 420)]
    dd.polygon(dress_pts, fill=GOLD)
    # Puffy Sleeves
    dd.ellipse([cx - 240, cy + 90, cx - 120, cy + 210], fill=GOLD)
    dd.ellipse([cx + 120, cy + 90, cx + 240, cy + 210], fill=GOLD)
    canvas.alpha_composite(stroke_layer(dress_layer, stroke_width=14, stroke_color=DARK))

    # White Peter Pan Collar
    draw = ImageDraw.Draw(canvas)
    draw.chord([cx - 110, cy + 70, cx + 110, cy + 170], 0, 180, fill=WHITE, outline=DARK, width=18)

    # Neck
    neck = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    nd = ImageDraw.Draw(neck)
    nd.rounded_rectangle([cx - 65, cy + 20, cx + 65, cy + 110], radius=25, fill=SKIN)
    canvas.alpha_composite(stroke_layer(neck, stroke_width=12, stroke_color=DARK))

    # Head Oval
    head_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    hd = ImageDraw.Draw(head_layer)
    hd.ellipse([bx - 230, by - 200, bx + 230, by + 200], fill=SKIN)
    canvas.alpha_composite(stroke_layer(head_layer, stroke_width=14, stroke_color=DARK))

    # Hair Cap & Soft Rounded Bangs
    hair = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    hrd = ImageDraw.Draw(hair)
    hrd.chord([bx - 240, by - 240, bx + 240, by], 180, 360, fill=HAIR_BROWN)
    for (hx, hy, hr) in [
        (bx - 140, by - 160, 110), (bx - 40, by - 210, 120),
        (bx + 60, by - 210, 120), (bx + 150, by - 160, 110),
        (bx - 70, by - 90, 85), (bx + 70, by - 90, 85)
    ]:
        hrd.ellipse([hx - hr, hy - hr, hx + hr, hy + hr], fill=HAIR_BROWN)
    canvas.alpha_composite(stroke_layer(hair, stroke_width=14, stroke_color=DARK))

    # Facial Features
    draw = ImageDraw.Draw(canvas)
    draw_glossy_eyes(draw, bx - 85, by + 25, bx + 85, by + 25, r=42)
    draw_blush(draw, bx - 150, by + 75, bx + 150, by + 75, rw=48, rh=28)
    draw.chord([bx - 70, by + 85, bx + 70, by + 195], 0, 180, fill=RED, outline=DARK, width=22)
    draw.ellipse([bx - 32, by + 140, bx + 32, by + 190], fill=PINK_BLUSH)

    return canvas.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def main():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    target_dir = os.path.join(base_dir, "app", "src", "main", "assets", "images", "pictures")
    
    for name, func in [("blendword_sam", craft_organic_sam), ("blendword_sis", craft_organic_sis)]:
        img = func()
        out_path = os.path.join(target_dir, f"{name}.png")
        img.save(out_path, "PNG", optimize=True)
        print(f"[+] Saved {name}.png ({os.path.getsize(out_path)} bytes)")

if __name__ == "__main__":
    main()
