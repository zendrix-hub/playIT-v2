"""
Master PlayIT Blend It Assets Generator — Complete 33-Word Concrete Bank (Final Polish)
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
GREEN = (76, 175, 80, 255)          # Fresh Green
PURPLE = (145, 100, 195, 255)       # Royal Ube
CREAM = (255, 242, 225, 255)
WOOD = (185, 120, 70, 255)

def stroke_layer(layer_img, stroke_width=14, stroke_color=DARK):
    alpha = layer_img.getchannel("A")
    dilated = alpha.filter(ImageFilter.MaxFilter(stroke_width * 2 + 1))
    stroke_img = Image.new("RGBA", layer_img.size, stroke_color)
    stroke_img.putalpha(dilated)
    stroke_img.alpha_composite(layer_img)
    return stroke_img

def draw_glossy_eyes(draw, lx, ly, rx, ry, r=40):
    for (ex, ey) in [(lx, ly), (rx, ry)]:
        draw.ellipse([ex - r, ey - r, ex + r, ey + r], fill=DARK)
        cr1 = r * 0.38
        draw.ellipse([ex - r*0.35 - cr1, ey - r*0.35 - cr1, ex - r*0.35 + cr1, ey - r*0.35 + cr1], fill=WHITE)
        cr2 = r * 0.18
        draw.ellipse([ex + r*0.35 - cr2, ey + r*0.35 - cr2, ex + r*0.35 + cr2, ey + r*0.35 + cr2], fill=WHITE)

def draw_blush(draw, lx, ly, rx, ry, rw=48, rh=28):
    for (cx, cy) in [(lx, ly), (rx, ry)]:
        draw.ellipse([cx - rw, cy - rh, cx + rw, cy + rh], fill=PINK_BLUSH)

# ── GROUP 1 (M, S, A, I) ─────────────────────────────────────────────────────

def draw_sam():
    canvas = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    cx, cy = W/2 - 30, H/2 + 70
    arm_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ad = ImageDraw.Draw(arm_layer)
    ad.line([(cx + 120, cy + 120), (cx + 310, cy - 80)], fill=SKIN, width=120, joint="curve")
    hx, hy = cx + 330, cy - 100
    ad.ellipse([hx - 70, hy - 70, hx + 70, hy + 70], fill=SKIN)
    ad.rounded_rectangle([hx - 40, hy - 140, hx + 40, hy - 30], radius=35, fill=SKIN)
    ad.rounded_rectangle([hx - 90, hy - 120, hx - 20, hy - 20], radius=30, fill=SKIN)
    ad.rounded_rectangle([hx + 10, hy - 120, hx + 80, hy - 20], radius=30, fill=SKIN)
    ad.rounded_rectangle([hx - 120, hy - 30, hx - 30, hy + 40], radius=30, fill=SKIN)
    canvas.alpha_composite(stroke_layer(arm_layer, stroke_width=14, stroke_color=DARK))

    body_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    bd = ImageDraw.Draw(body_layer)
    bd.rounded_rectangle([cx - 210, cy + 80, cx + 210, cy + 420], radius=70, fill=BLUE_SKY)
    canvas.alpha_composite(stroke_layer(body_layer, stroke_width=14, stroke_color=DARK))

    draw = ImageDraw.Draw(canvas)
    draw.rounded_rectangle([cx - 190, cy + 170, cx + 190, cy + 230], radius=20, fill=WHITE)
    draw.rounded_rectangle([cx - 190, cy + 280, cx + 190, cy + 340], radius=20, fill=WHITE)

    neck = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    nd = ImageDraw.Draw(neck)
    nd.rounded_rectangle([cx - 65, cy + 20, cx + 65, cy + 110], radius=25, fill=SKIN)
    canvas.alpha_composite(stroke_layer(neck, stroke_width=12, stroke_color=DARK))

    head_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    hd = ImageDraw.Draw(head_layer)
    bx, by = cx, cy - 150
    hd.ellipse([bx - 280, by - 50, bx - 170, by + 60], fill=SKIN)
    hd.ellipse([bx + 170, by - 50, bx + 280, by + 60], fill=SKIN)
    hd.ellipse([bx - 230, by - 200, bx + 230, by + 200], fill=SKIN)
    canvas.alpha_composite(stroke_layer(head_layer, stroke_width=14, stroke_color=DARK))

    hair = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    hrd = ImageDraw.Draw(hair)
    for (hx, hy, hr) in [
        (bx - 170, by - 150, 120), (bx - 90, by - 220, 130),
        (bx, by - 240, 140), (bx + 90, by - 220, 130),
        (bx + 170, by - 150, 120), (bx - 90, by - 110, 110),
        (bx + 60, by - 100, 110), (bx + 150, by - 90, 95)
    ]:
        hrd.ellipse([hx - hr, hy - hr, hx + hr, hy + hr], fill=HAIR_BROWN)
    canvas.alpha_composite(stroke_layer(hair, stroke_width=14, stroke_color=DARK))

    draw = ImageDraw.Draw(canvas)
    draw_glossy_eyes(draw, bx - 85, by + 25, bx + 85, by + 25, r=42)
    draw_blush(draw, bx - 150, by + 75, bx + 150, by + 75, rw=48, rh=28)
    draw.chord([bx - 70, by + 85, bx + 70, by + 195], 0, 180, fill=RED, outline=DARK, width=22)
    draw.ellipse([bx - 32, by + 140, bx + 32, by + 190], fill=PINK_BLUSH)
    return canvas.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_sis():
    canvas = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    cx, cy = W/2 - 30, H/2 + 70
    arm_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ad = ImageDraw.Draw(arm_layer)
    ad.line([(cx + 120, cy + 120), (cx + 310, cy - 80)], fill=SKIN, width=120, joint="curve")
    hx, hy = cx + 330, cy - 100
    ad.ellipse([hx - 70, hy - 70, hx + 70, hy + 70], fill=SKIN)
    ad.rounded_rectangle([hx - 40, hy - 140, hx + 40, hy - 30], radius=35, fill=SKIN)
    ad.rounded_rectangle([hx - 90, hy - 120, hx - 20, hy - 20], radius=30, fill=SKIN)
    ad.rounded_rectangle([hx + 10, hy - 120, hx + 80, hy - 20], radius=30, fill=SKIN)
    ad.rounded_rectangle([hx - 120, hy - 30, hx - 30, hy + 40], radius=30, fill=SKIN)
    canvas.alpha_composite(stroke_layer(arm_layer, stroke_width=14, stroke_color=DARK))

    pigtails = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    pd = ImageDraw.Draw(pigtails)
    bx, by = cx, cy - 140
    pd.ellipse([bx - 360, by - 120, bx - 180, by + 60], fill=HAIR_BROWN)
    pd.ellipse([bx + 180, by - 120, bx + 360, by + 60], fill=HAIR_BROWN)
    canvas.alpha_composite(stroke_layer(pigtails, stroke_width=14, stroke_color=DARK))

    bows = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    bwd = ImageDraw.Draw(bows)
    bwd.ellipse([bx - 240, by - 60, bx - 160, by + 10], fill=PINK_BLUSH)
    bwd.ellipse([bx + 160, by - 60, bx + 240, by + 10], fill=PINK_BLUSH)
    canvas.alpha_composite(stroke_layer(bows, stroke_width=12, stroke_color=DARK))

    dress_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    dd = ImageDraw.Draw(dress_layer)
    dress_pts = [(cx - 150, cy + 90), (cx + 150, cy + 90), (cx + 250, cy + 420), (cx - 250, cy + 420)]
    dd.polygon(dress_pts, fill=GOLD)
    canvas.alpha_composite(stroke_layer(dress_layer, stroke_width=14, stroke_color=DARK))

    draw = ImageDraw.Draw(canvas)
    draw.chord([cx - 110, cy + 70, cx + 110, cy + 170], 0, 180, fill=WHITE, outline=DARK, width=18)

    neck = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    nd = ImageDraw.Draw(neck)
    nd.rounded_rectangle([cx - 65, cy + 20, cx + 65, cy + 110], radius=25, fill=SKIN)
    canvas.alpha_composite(stroke_layer(neck, stroke_width=12, stroke_color=DARK))

    head_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    hd = ImageDraw.Draw(head_layer)
    hd.ellipse([bx - 230, by - 200, bx + 230, by + 200], fill=SKIN)
    canvas.alpha_composite(stroke_layer(head_layer, stroke_width=14, stroke_color=DARK))

    hair = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    hrd = ImageDraw.Draw(hair)
    for (hx, hy, hr) in [
        (bx - 160, by - 150, 120), (bx, by - 230, 150),
        (bx + 160, by - 150, 120), (bx - 70, by - 120, 115),
        (bx + 70, by - 120, 115)
    ]:
        hrd.ellipse([hx - hr, hy - hr, hx + hr, hy + hr], fill=HAIR_BROWN)
    canvas.alpha_composite(stroke_layer(hair, stroke_width=14, stroke_color=DARK))

    draw = ImageDraw.Draw(canvas)
    draw_glossy_eyes(draw, bx - 85, by + 25, bx + 85, by + 25, r=42)
    draw_blush(draw, bx - 150, by + 75, bx + 150, by + 75, rw=48, rh=28)
    draw.chord([bx - 70, by + 85, bx + 70, by + 195], 0, 180, fill=RED, outline=DARK, width=22)
    draw.ellipse([bx - 32, by + 140, bx + 32, by + 190], fill=PINK_BLUSH)
    return canvas.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_aim():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20
    draw.ellipse([cx-320, cy-320, cx+320, cy+320], fill=RED, outline=DARK, width=24)
    draw.ellipse([cx-240, cy-240, cx+240, cy+240], fill=WHITE, outline=DARK, width=20)
    draw.ellipse([cx-160, cy-160, cx+160, cy+160], fill=BLUE_SKY, outline=DARK, width=20)
    draw.ellipse([cx-80, cy-80, cx+80, cy+80], fill=GOLD, outline=DARK, width=20)
    draw.line([(cx+220, cy-220), (cx+20, cy-20)], fill=DARK, width=32, joint="curve")
    draw.line([(cx+220, cy-220), (cx+20, cy-20)], fill=WOOD, width=20, joint="curve")
    draw.polygon([(cx+220, cy-220), (cx+280, cy-200), (cx+260, cy-260)], fill=RED, outline=DARK)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── GROUP 2 (+ O, B, E, U) ───────────────────────────────────────────────────

def draw_bus():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.rounded_rectangle([cx-360, cy-200, cx+360, cy+160], radius=70, fill=GOLD, outline=DARK, width=28)
    for wx in [-230, -50, 130]:
        draw.rounded_rectangle([cx+wx-65, cy-150, cx+wx+65, cy-30], radius=24, fill=BLUE_SKY, outline=DARK, width=18)
    draw.rectangle([cx-380, cy+90, cx+380, cy+150], fill=CREAM, outline=DARK, width=18)
    for wx in [-210, 210]:
        draw.ellipse([cx+wx-80, cy+100, cx+wx+80, cy+260], fill=DARK)
        draw.ellipse([cx+wx-45, cy+135, cx+wx+45, cy+225], fill=CREAM)
    draw_glossy_eyes(draw, cx - 80, cy + 40, cx + 80, cy + 40, r=22)
    draw_blush(draw, cx - 140, cy + 60, cx + 140, cy + 60, rw=28, rh=16)
    draw.arc([cx - 50, cy + 45, cx + 50, cy + 85], 10, 170, fill=DARK, width=14)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_sub():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.ellipse([cx-320, cy-170, cx+280, cy+190], fill=GOLD, outline=DARK, width=28)
    draw.rectangle([cx-50, cy-290, cx+30, cy-150], fill=ORANGE, outline=DARK, width=20)
    draw.rectangle([cx-50, cy-310, cx+100, cy-250], fill=ORANGE, outline=DARK, width=20)
    for px in [-150, -10, 130]:
        draw.ellipse([cx+px-50, cy-50, cx+px+50, cy+50], fill=BLUE_SKY, outline=DARK, width=18)
    draw.polygon([(cx-310, cy), (cx-400, cy-90), (cx-370, cy), (cx-400, cy+90)], fill=ORANGE, outline=DARK)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_mom():
    canvas = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    cx, cy = W/2, H/2 + 70

    dress_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    dd = ImageDraw.Draw(dress_layer)
    dd.polygon([(cx - 150, cy + 90), (cx + 150, cy + 90), (cx + 260, cy + 420), (cx - 260, cy + 420)], fill=PINK_BLUSH)
    dd.ellipse([cx - 230, cy + 80, cx - 120, cy + 200], fill=PINK_BLUSH)
    dd.ellipse([cx + 120, cy + 80, cx + 230, cy + 200], fill=PINK_BLUSH)
    canvas.alpha_composite(stroke_layer(dress_layer, stroke_width=14, stroke_color=DARK))

    draw = ImageDraw.Draw(canvas)
    draw.chord([cx - 110, cy + 70, cx + 110, cy + 170], 0, 180, fill=WHITE, outline=DARK, width=18)
    for px in [-60, -30, 0, 30, 60]:
        draw.ellipse([cx + px - 12, cy + 120, cx + px + 12, cy + 144], fill=CREAM)

    neck = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    nd = ImageDraw.Draw(neck)
    nd.rounded_rectangle([cx - 65, cy + 20, cx + 65, cy + 110], radius=25, fill=SKIN)
    canvas.alpha_composite(stroke_layer(neck, stroke_width=12, stroke_color=DARK))

    bun_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    bnd = ImageDraw.Draw(bun_layer)
    bx, by = cx, cy - 140
    bnd.ellipse([bx - 140, by - 360, bx + 140, by - 120], fill=HAIR_BROWN)
    bnd.ellipse([bx + 110, by - 240, bx + 210, by - 140], fill=PINK_BLUSH)
    canvas.alpha_composite(stroke_layer(bun_layer, stroke_width=14, stroke_color=DARK))

    head_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    hd = ImageDraw.Draw(head_layer)
    hd.ellipse([bx - 230, by - 200, bx + 230, by + 200], fill=SKIN)
    hd.ellipse([bx - 250, by + 20, bx - 210, by + 60], fill=CREAM)
    hd.ellipse([bx + 210, by + 20, bx + 250, by + 60], fill=CREAM)
    canvas.alpha_composite(stroke_layer(head_layer, stroke_width=14, stroke_color=DARK))

    hair = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    hrd = ImageDraw.Draw(hair)
    for (hx, hy, hr) in [
        (bx - 170, by - 140, 120), (bx, by - 220, 140),
        (bx + 170, by - 140, 120), (bx - 90, by - 110, 110), (bx + 90, by - 110, 110),
        (bx - 210, by + 20, 70), (bx + 210, by + 20, 70)
    ]:
        hrd.ellipse([hx - hr, hy - hr, hx + hr, hy + hr], fill=HAIR_BROWN)
    canvas.alpha_composite(stroke_layer(hair, stroke_width=14, stroke_color=DARK))

    draw = ImageDraw.Draw(canvas)
    draw_glossy_eyes(draw, bx - 85, by + 20, bx + 85, by + 20, r=42)
    draw_blush(draw, bx - 150, by + 75, bx + 150, by + 75, rw=48, rh=28)
    draw.chord([bx - 65, by + 85, bx + 65, by + 180], 0, 180, fill=RED, outline=DARK, width=22)
    draw.ellipse([bx - 30, by + 130, bx + 30, by + 175], fill=PINK_BLUSH)
    return canvas.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_bee():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20
    draw.ellipse([cx - 240, cy - 260, cx - 40, cy - 60], fill=(200, 240, 255, 220), outline=DARK, width=22)
    draw.ellipse([cx + 40, cy - 260, cx + 240, cy - 60], fill=(200, 240, 255, 220), outline=DARK, width=22)
    draw.ellipse([cx - 260, cy - 140, cx + 260, cy + 180], fill=GOLD, outline=DARK, width=28)
    draw.rectangle([cx - 80, cy - 130, cx - 10, cy + 170], fill=DARK)
    draw.rectangle([cx + 80, cy - 130, cx + 150, cy + 170], fill=DARK)
    draw.line([(cx - 120, cy - 130), (cx - 180, cy - 240)], fill=DARK, width=20, joint="curve")
    draw.ellipse([cx - 210, cy - 270, cx - 150, cy - 210], fill=DARK)
    draw.line([(cx - 40, cy - 130), (cx - 70, cy - 240)], fill=DARK, width=20, joint="curve")
    draw.ellipse([cx - 100, cy - 270, cx - 40, cy - 210], fill=DARK)
    draw_glossy_eyes(draw, cx - 180, cy + 10, cx - 110, cy + 10, r=26)
    draw_blush(draw, cx - 210, cy + 50, cx - 80, cy + 50, rw=26, rh=16)
    draw.arc([cx - 170, cy + 30, cx - 120, cy + 75], 10, 170, fill=DARK, width=14)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_bib():
    canvas = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    cx, cy = W/2, H/2 + 20
    bib_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    bd = ImageDraw.Draw(bib_layer)
    bd.ellipse([cx - 200, cy - 260, cx + 200, cy + 20], fill=BLUE_SKY)
    bd.rounded_rectangle([cx - 240, cy - 80, cx + 240, cy + 280], radius=85, fill=BLUE_SKY)
    bd.ellipse([cx - 120, cy - 260, cx + 120, cy - 60], fill=(0, 0, 0, 0))
    canvas.alpha_composite(stroke_layer(bib_layer, stroke_width=14, stroke_color=DARK))

    draw = ImageDraw.Draw(canvas)
    draw.rounded_rectangle([cx - 170, cy + 40, cx + 170, cy + 220], radius=55, fill=WHITE, outline=DARK, width=20)
    draw.ellipse([cx - 55, cy + 85, cx + 10, cy + 150], fill=RED)
    draw.ellipse([cx - 10, cy + 85, cx + 55, cy + 150], fill=RED)
    draw.polygon([(cx - 55, cy + 120), (cx + 55, cy + 120), (cx, cy + 180)], fill=RED)
    return canvas.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── GROUP 3 (+ T, K, L, Y) ───────────────────────────────────────────────────

def draw_bat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20
    l_wing = [(cx-60, cy+80), (cx-450, cy-160), (cx-350, cy+40), (cx-250, cy+160), (cx-140, cy+180), (cx-60, cy+120)]
    draw.polygon(l_wing, fill=PURPLE, outline=DARK)
    for i in range(len(l_wing)-1):
        draw.line([l_wing[i], l_wing[i+1]], fill=DARK, width=28, joint="curve")
    r_wing = [(cx+60, cy+80), (cx+450, cy-160), (cx+350, cy+40), (cx+250, cy+160), (cx+140, cy+180), (cx+60, cy+120)]
    draw.polygon(r_wing, fill=PURPLE, outline=DARK)
    for i in range(len(r_wing)-1):
        draw.line([r_wing[i], r_wing[i+1]], fill=DARK, width=28, joint="curve")
    draw.polygon([(cx-160, cy-70), (cx-110, cy-310), (cx-20, cy-140)], fill=PURPLE, outline=DARK)
    draw.polygon([(cx-140, cy-80), (cx-110, cy-250), (cx-50, cy-130)], fill=PINK_BLUSH)
    draw.polygon([(cx+160, cy-70), (cx+110, cy-310), (cx+20, cy-140)], fill=PURPLE, outline=DARK)
    draw.polygon([(cx+140, cy-80), (cx+110, cy-250), (cx+50, cy-130)], fill=PINK_BLUSH)
    draw.ellipse([cx-190, cy-150, cx+190, cy+230], fill=PURPLE, outline=DARK, width=30)
    draw.ellipse([cx-120, cy-20, cx+120, cy+210], fill=CREAM)
    draw_glossy_eyes(draw, cx-80, cy-10, cx+80, cy-10, r=38)
    draw_blush(draw, cx-145, cy+45, cx+145, cy+45, rw=48, rh=28)
    draw.arc([cx-55, cy+25, cx+55, cy+95], 10, 170, fill=DARK, width=18)
    draw.polygon([(cx-26, cy+65), (cx-12, cy+65), (cx-19, cy+90)], fill=WHITE, outline=DARK)
    draw.polygon([(cx+12, cy+65), (cx+26, cy+65), (cx+19, cy+90)], fill=WHITE, outline=DARK)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_mat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.ellipse([cx - 420, cy - 240, cx + 420, cy + 240], fill=GOLD, outline=DARK, width=32)
    draw.ellipse([cx - 360, cy - 180, cx + 360, cy + 180], fill=CREAM, outline=DARK, width=22)
    for x in range(-260, 320, 90):
        draw.line([(cx + x, cy - 170), (cx + x, cy + 170)], fill=ORANGE, width=16)
    lx, ly = cx - 120, cy
    draw.rounded_rectangle([lx - 75, ly - 115, lx + 75, ly + 105], radius=50, fill=RED, outline=DARK, width=24)
    draw.ellipse([lx - 50, ly - 85, lx + 50, ly - 5], fill=WHITE, outline=DARK, width=14)
    rx, ry = cx + 120, cy
    draw.rounded_rectangle([rx - 75, ry - 115, rx + 75, ry + 105], radius=50, fill=RED, outline=DARK, width=24)
    draw.ellipse([rx - 50, ry - 85, rx + 50, ry - 5], fill=WHITE, outline=DARK, width=14)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_kit():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 40
    draw.arc([cx - 120, cy - 320, cx + 120, cy - 120], 180, 360, fill=DARK, width=32)
    draw.rounded_rectangle([cx - 300, cy - 180, cx + 300, cy + 220], radius=60, fill=RED, outline=DARK, width=28)
    draw.rectangle([cx - 35, cy - 90, cx + 35, cy + 110], fill=WHITE)
    draw.rectangle([cx - 100, cy - 25, cx + 100, cy + 45], fill=WHITE)
    draw_glossy_eyes(draw, cx - 80, cy + 130, cx + 80, cy + 130, r=20)
    draw.arc([cx - 40, cy + 140, cx + 40, cy + 180], 10, 170, fill=DARK, width=14)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_toy():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.polygon([(cx, cy - 260), (cx + 260, cy - 40), (cx, cy + 280), (cx - 260, cy - 40)], fill=RED, outline=DARK)
    draw.polygon([(cx, cy - 150), (cx + 170, cy - 40), (cx, cy + 170), (cx - 170, cy - 40)], fill=GOLD)
    draw.ellipse([cx - 60, cy - 310, cx + 60, cy - 210], fill=BLUE_SKY, outline=DARK, width=20)
    draw_glossy_eyes(draw, cx - 55, cy - 40, cx + 55, cy - 40, r=22)
    draw_blush(draw, cx - 110, cy - 15, cx + 110, cy - 15, rw=28, rh=16)
    draw.arc([cx - 40, cy - 25, cx + 40, cy + 20], 10, 170, fill=DARK, width=14)
    draw.arc([cx - 320, cy - 200, cx + 320, cy + 200], 30, 150, fill=BLUE_SKY, width=22)
    draw.arc([cx - 320, cy - 200, cx + 320, cy + 200], 210, 330, fill=BLUE_SKY, width=22)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_boy():
    return draw_sam()

# ── GROUP 4 (+ N, G, NG, P) ──────────────────────────────────────────────────

def draw_pig():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.polygon([(cx - 260, cy - 120), (cx - 190, cy - 310), (cx - 70, cy - 200)], fill=PINK_BLUSH, outline=DARK)
    draw.polygon([(cx + 260, cy - 120), (cx + 190, cy - 310), (cx + 70, cy - 200)], fill=PINK_BLUSH, outline=DARK)
    draw.ellipse([cx - 290, cy - 240, cx + 290, cy + 260], fill=PINK_BLUSH, outline=DARK, width=28)
    draw.ellipse([cx - 120, cy + 10, cx + 120, cy + 150], fill=(255, 175, 190, 255), outline=DARK, width=20)
    draw.ellipse([cx - 60, cy + 50, cx - 25, cy + 100], fill=DARK)
    draw.ellipse([cx + 25, cy + 50, cx + 60, cy + 100], fill=DARK)
    draw_glossy_eyes(draw, cx - 110, cy - 70, cx + 110, cy - 70, r=32)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_pan():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2 - 50, H/2
    draw.ellipse([cx - 240, cy - 240, cx + 240, cy + 240], fill=DARK)
    draw.ellipse([cx - 210, cy - 210, cx + 210, cy + 210], fill=(80, 95, 105, 255))
    draw.line([(cx + 210, cy), (cx + 410, cy)], fill=DARK, width=58, joint="curve")
    draw.line([(cx + 210, cy), (cx + 410, cy)], fill=WOOD, width=40, joint="curve")
    draw.ellipse([cx - 130, cy - 120, cx + 130, cy + 130], fill=WHITE, outline=DARK, width=16)
    draw.ellipse([cx - 65, cy - 65, cx + 65, cy + 65], fill=GOLD, outline=DARK, width=14)
    draw_glossy_eyes(draw, cx - 25, cy - 10, cx + 25, cy - 10, r=10)
    draw.arc([cx - 20, cy + 5, cx + 20, cy + 30], 10, 170, fill=DARK, width=8)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_bug():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20
    draw.ellipse([cx - 140, cy - 240, cx + 140, cy - 20], fill=DARK)
    draw.line([(cx - 70, cy - 210), (cx - 150, cy - 320)], fill=DARK, width=20, joint="curve")
    draw.ellipse([cx - 180, cy - 350, cx - 120, cy - 290], fill=DARK)
    draw.line([(cx + 70, cy - 210), (cx + 150, cy - 320)], fill=DARK, width=20, joint="curve")
    draw.ellipse([cx + 120, cy - 350, cx + 180, cy - 290], fill=DARK)
    draw.ellipse([cx - 270, cy - 120, cx + 270, cy + 280], fill=RED, outline=DARK, width=28)
    draw.line([(cx, cy - 120), (cx, cy + 280)], fill=DARK, width=22)
    for (sx, sy) in [(-140, 0), (-160, 140), (-70, 190), (140, 0), (160, 140), (70, 190)]:
        draw.ellipse([cx + sx - 35, cy + sy - 35, cx + sx + 35, cy + sy + 35], fill=DARK)
    draw_glossy_eyes(draw, cx - 60, cy - 120, cx + 60, cy - 120, r=24)
    draw_blush(draw, cx - 100, cy - 80, cx + 100, cy - 80, rw=24, rh=14)
    draw.arc([cx - 40, cy - 90, cx + 40, cy - 50], 10, 170, fill=WHITE, width=12)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_pin():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.rounded_rectangle([cx - 100, cy - 280, cx + 100, cy - 120], radius=50, fill=WHITE, outline=DARK, width=26)
    draw.ellipse([cx - 180, cy - 100, cx + 180, cy + 280], fill=WHITE, outline=DARK, width=26)
    draw.rectangle([cx - 95, cy - 170, cx + 95, cy - 130], fill=RED)
    draw.rectangle([cx - 105, cy - 100, cx + 105, cy - 60], fill=RED)
    draw_glossy_eyes(draw, cx - 45, cy - 210, cx + 45, cy - 210, r=18)
    draw_blush(draw, cx - 80, cy - 180, cx + 80, cy - 180, rw=20, rh=12)
    draw.arc([cx - 30, cy - 190, cx + 30, cy - 155], 10, 170, fill=DARK, width=10)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_nap():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 30
    draw.rounded_rectangle([cx - 420, cy - 220, cx + 420, cy + 300], radius=55, fill=WOOD, outline=DARK, width=32)
    draw.rounded_rectangle([cx - 340, cy - 250, cx + 340, cy], radius=65, fill=WHITE, outline=DARK, width=28)
    hx, hy = cx - 30, cy - 130
    draw.ellipse([hx - 160, hy - 140, hx + 160, hy + 140], fill=SKIN, outline=DARK, width=28)
    draw.arc([hx - 160, hy - 150, hx + 160, hy + 10], 180, 360, fill=HAIR_BROWN, width=70)
    draw.arc([hx - 95, hy - 10, hx - 25, hy + 50], 10, 170, fill=DARK, width=18)
    draw.arc([hx + 25, hy - 10, hx + 95, hy + 50], 10, 170, fill=DARK, width=18)
    draw_blush(draw, hx - 85, hy + 60, hx + 85, hy + 60, rw=42, rh=24)
    draw.arc([hx - 35, hy + 45, hx + 35, hy + 95], 10, 170, fill=DARK, width=14)
    draw.rounded_rectangle([cx - 420, cy - 40, cx + 420, cy + 300], radius=50, fill=BLUE_SKY, outline=DARK, width=32)
    draw.rounded_rectangle([cx - 420, cy - 40, cx + 420, cy + 60], radius=25, fill=GOLD, outline=DARK, width=20)
    for (zx, zy, sz, col) in [(cx + 240, cy - 240, 60, PURPLE), (cx + 330, cy - 330, 85, ORANGE)]:
        draw.text((zx, zy), "Z", fill=col)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── GROUP 5 (+ R, D, H, W) ───────────────────────────────────────────────────

def draw_dog():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.ellipse([cx - 290, cy - 140, cx - 140, cy + 120], fill=HAIR_BROWN, outline=DARK, width=26)
    draw.ellipse([cx + 140, cy - 140, cx + 290, cy + 120], fill=HAIR_BROWN, outline=DARK, width=26)
    draw.ellipse([cx - 240, cy - 210, cx + 240, cy + 220], fill=GOLD, outline=DARK, width=28)
    draw.ellipse([cx - 140, cy - 10, cx + 140, cy + 190], fill=CREAM, outline=DARK, width=18)
    draw.ellipse([cx - 45, cy + 10, cx + 45, cy + 70], fill=DARK)
    draw.arc([cx - 65, cy + 60, cx + 65, cy + 140], 10, 170, fill=DARK, width=16)
    draw_glossy_eyes(draw, cx - 90, cy - 50, cx + 90, cy - 50, r=36)
    draw_blush(draw, cx - 160, cy + 30, cx + 160, cy + 30, rw=42, rh=24)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_hat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 30
    draw.polygon([(cx, cy - 320), (cx + 220, cy + 180), (cx - 220, cy + 180)], fill=PURPLE, outline=DARK)
    draw.line([(cx, cy - 320), (cx + 220, cy + 180)], fill=DARK, width=28)
    draw.line([(cx + 220, cy + 180), (cx - 220, cy + 180)], fill=DARK, width=28)
    draw.line([(cx - 220, cy + 180), (cx, cy - 320)], fill=DARK, width=28)
    draw.polygon([(cx - 70, cy - 160), (cx + 70, cy - 160), (cx + 120, cy - 60), (cx - 120, cy - 60)], fill=GOLD)
    draw.polygon([(cx - 160, cy + 40), (cx + 160, cy + 40), (cx + 210, cy + 160), (cx - 210, cy + 160)], fill=RED)
    draw.ellipse([cx - 65, cy - 380, cx + 65, cy - 250], fill=GOLD, outline=DARK, width=22)
    draw_glossy_eyes(draw, cx - 50, cy + 30, cx + 50, cy + 30, r=22)
    draw.arc([cx - 40, cy + 50, cx + 40, cy + 100], 10, 170, fill=DARK, width=14)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_hen():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 20
    draw.ellipse([cx - 260, cy - 170, cx + 220, cy + 210], fill=CREAM, outline=DARK, width=28)
    draw.ellipse([cx - 160, cy - 280, cx - 60, cy - 150], fill=RED, outline=DARK, width=18)
    draw.ellipse([cx - 90, cy - 310, cx + 10, cy - 170], fill=RED, outline=DARK, width=18)
    draw.polygon([(cx - 240, cy - 80), (cx - 350, cy - 20), (cx - 230, cy + 20)], fill=GOLD, outline=DARK)
    draw.ellipse([cx - 230, cy + 10, cx - 170, cy + 90], fill=RED, outline=DARK, width=14)
    draw.ellipse([cx - 40, cy - 30, cx + 170, cy + 140], fill=ORANGE, outline=DARK, width=22)
    draw_glossy_eyes(draw, cx - 140, cy - 70, cx - 140, cy - 70, r=24)
    draw.ellipse([cx - 180, cy - 30, cx - 130, cy + 5], fill=PINK_BLUSH)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_bed():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 30
    draw.rounded_rectangle([cx - 360, cy - 240, cx + 360, cy + 260], radius=50, fill=WOOD, outline=DARK, width=32)
    draw.rounded_rectangle([cx - 280, cy - 200, cx + 280, cy - 20], radius=50, fill=WHITE, outline=DARK, width=24)
    draw.rounded_rectangle([cx - 360, cy - 40, cx + 360, cy + 260], radius=45, fill=BLUE_SKY, outline=DARK, width=32)
    draw.rounded_rectangle([cx - 360, cy - 40, cx + 360, cy + 40], radius=25, fill=GOLD, outline=DARK, width=20)
    for (qx, qy) in [(-200, 120), (-50, 170), (100, 110), (240, 160)]:
        draw.ellipse([cx + qx - 22, cy + qy - 22, cx + qx + 22, cy + qy + 22], fill=WHITE)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_hand():
    canvas = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    hand_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    hd = ImageDraw.Draw(hand_layer)
    cx, cy = W/2, H/2 + 70
    hd.rounded_rectangle([cx - 130, cy + 180, cx + 130, cy + 420], radius=50, fill=SKIN)
    hd.ellipse([cx - 200, cy - 80, cx + 200, cy + 240], fill=SKIN)
    fingers = [(-125, -170, 72, 230), (-45, -270, 78, 310), (45, -300, 80, 340), (130, -240, 78, 280)]
    for (fx, fy, fw, fh) in fingers:
        hd.rounded_rectangle([cx + fx - fw/2, cy + fy, cx + fx + fw/2, cy + fy + fh], radius=int(fw/2), fill=SKIN)
    hd.rounded_rectangle([cx + 120, cy - 10, cx + 320, cy + 90], radius=45, fill=SKIN)
    canvas.alpha_composite(stroke_layer(hand_layer, stroke_width=14, stroke_color=DARK))
    draw = ImageDraw.Draw(canvas)
    for fx in [-85, 0, 88]:
        draw.line([(cx + fx, cy - 80), (cx + fx, cy - 30)], fill=DARK, width=12, joint="curve")
    draw.arc([cx - 90, cy + 40, cx + 40, cy + 150], 20, 140, fill=PINK_BLUSH, width=18)
    draw.arc([cx - 10, cy + 50, cx + 110, cy + 160], 30, 150, fill=PINK_BLUSH, width=18)
    draw.arc([cx - 360, cy - 320, cx - 250, cy - 140], 120, 240, fill=BLUE_SKY, width=24)
    draw.arc([cx + 270, cy - 320, cx + 380, cy - 140], 300, 60, fill=BLUE_SKY, width=24)
    return canvas.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── GROUP 6 (+ C, F, J, Ñ) ───────────────────────────────────────────────────

def draw_cat():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.polygon([(cx - 240, cy - 80), (cx - 170, cy - 300), (cx - 50, cy - 180)], fill=ORANGE, outline=DARK)
    draw.polygon([(cx - 210, cy - 90), (cx - 170, cy - 250), (cx - 80, cy - 170)], fill=PINK_BLUSH)
    draw.polygon([(cx + 240, cy - 80), (cx + 170, cy - 300), (cx + 50, cy - 180)], fill=ORANGE, outline=DARK)
    draw.polygon([(cx + 210, cy - 90), (cx + 170, cy - 250), (cx + 80, cy - 170)], fill=PINK_BLUSH)
    draw.ellipse([cx - 270, cy - 230, cx + 270, cy + 240], fill=ORANGE, outline=DARK, width=28)
    draw.ellipse([cx - 180, cy - 80, cx + 180, cy + 220], fill=CREAM)
    draw_glossy_eyes(draw, cx - 90, cy - 30, cx + 90, cy - 30, r=36)
    draw_blush(draw, cx - 160, cy + 40, cx + 160, cy + 40, rw=48, rh=28)
    draw.polygon([(cx - 18, cy + 30), (cx + 18, cy + 30), (cx, cy + 50)], fill=DARK)
    draw.arc([cx - 70, cy + 35, cx + 70, cy + 115], 10, 170, fill=DARK, width=16)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_fan():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 - 40
    draw.ellipse([cx - 260, cy - 260, cx + 260, cy + 260], fill=WHITE, outline=DARK, width=26)
    for a in [0, 90, 180, 270]:
        rad = math.radians(a)
        bx = cx + 120 * math.cos(rad)
        by = cy + 120 * math.sin(rad)
        draw.ellipse([bx - 60, by - 60, bx + 60, by + 60], fill=BLUE_SKY, outline=DARK, width=16)
    draw.ellipse([cx - 80, cy - 80, cx + 80, cy + 80], fill=GOLD, outline=DARK, width=20)
    draw_glossy_eyes(draw, cx - 28, cy - 10, cx + 28, cy - 10, r=10)
    draw.arc([cx - 25, cy + 5, cx + 25, cy + 28], 10, 170, fill=DARK, width=8)
    draw.rectangle([cx - 30, cy + 260, cx + 30, cy + 400], fill=DARK)
    draw.rounded_rectangle([cx - 150, cy + 380, cx + 150, cy + 450], radius=30, fill=BLUE_SKY, outline=DARK, width=22)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_cap():
    canvas = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    cx, cy = W/2, H/2 + 30

    cap_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    cd = ImageDraw.Draw(cap_layer)
    cd.ellipse([cx - 240, cy - 240, cx + 240, cy + 100], fill=BLUE_SKY)
    cd.rounded_rectangle([cx - 60, cy - 10, cx + 340, cy + 110], radius=50, fill=RED)
    cd.ellipse([cx - 40, cy - 270, cx + 40, cy - 190], fill=GOLD)
    canvas.alpha_composite(stroke_layer(cap_layer, stroke_width=14, stroke_color=DARK))

    draw = ImageDraw.Draw(canvas)
    draw_glossy_eyes(draw, cx - 110, cy - 60, cx + 10, cy - 60, r=26)
    draw_blush(draw, cx - 160, cy - 20, cx + 60, cy - 20, rw=26, rh=16)
    draw.arc([cx - 70, cy - 30, cx - 30, cy + 10], 10, 170, fill=DARK, width=14)
    return canvas.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_cup():
    canvas = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    cx, cy = W/2 - 40, H/2 + 20

    cup_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    cd = ImageDraw.Draw(cup_layer)
    cd.rounded_rectangle([cx + 120, cy - 80, cx + 320, cy + 160], radius=60, fill=GOLD)
    cd.rounded_rectangle([cx + 140, cy - 40, cx + 270, cy + 120], radius=40, fill=(0, 0, 0, 0))
    cd.rounded_rectangle([cx - 220, cy - 140, cx + 180, cy + 240], radius=60, fill=GOLD)
    canvas.alpha_composite(stroke_layer(cup_layer, stroke_width=14, stroke_color=DARK))

    draw = ImageDraw.Draw(canvas)
    draw.arc([cx - 100, cy - 300, cx - 30, cy - 160], 30, 210, fill=BLUE_SKY, width=20)
    draw.arc([cx + 10, cy - 320, cx + 80, cy - 170], 30, 210, fill=BLUE_SKY, width=20)
    draw_glossy_eyes(draw, cx - 80, cy + 10, cx + 40, cy + 10, r=28)
    draw_blush(draw, cx - 140, cy + 60, cx + 100, cy + 60, rw=35, rh=20)
    draw.arc([cx - 45, cy + 45, cx + 15, cy + 115], 10, 170, fill=DARK, width=16)
    return canvas.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_jam():
    canvas = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    cx, cy = W/2, H/2 + 30

    jar_layer = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    jd = ImageDraw.Draw(jar_layer)
    jd.rounded_rectangle([cx - 210, cy - 130, cx + 210, cy + 270], radius=65, fill=RED)
    jd.rounded_rectangle([cx - 190, cy - 240, cx + 190, cy - 110], radius=35, fill=GOLD)
    jd.rectangle([cx - 200, cy - 140, cx + 200, cy - 110], fill=ORANGE)
    canvas.alpha_composite(stroke_layer(jar_layer, stroke_width=14, stroke_color=DARK))

    draw = ImageDraw.Draw(canvas)
    draw.rounded_rectangle([cx - 140, cy - 20, cx + 140, cy + 180], radius=35, fill=WHITE, outline=DARK, width=18)
    draw.ellipse([cx - 45, cy + 30, cx + 45, cy + 120], fill=RED)
    draw.polygon([(cx - 40, cy + 35), (cx, cy - 5), (cx + 40, cy + 35)], fill=GREEN)
    draw_glossy_eyes(draw, cx - 20, cy + 65, cx + 20, cy + 65, r=8)
    draw.arc([cx - 15, cy + 78, cx + 15, cy + 100], 10, 170, fill=DARK, width=6)
    return canvas.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── GROUP 7 (+ Q, V, X, Z) ───────────────────────────────────────────────────

def draw_van():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.rounded_rectangle([cx - 340, cy - 180, cx + 330, cy + 150], radius=60, fill=BLUE_SKY, outline=DARK, width=28)
    draw.rounded_rectangle([cx + 90, cy - 140, cx + 280, cy - 20], radius=24, fill=WHITE, outline=DARK, width=18)
    draw.rounded_rectangle([cx - 90, cy - 140, cx + 60, cy - 20], radius=24, fill=WHITE, outline=DARK, width=18)
    for wx in [-190, 180]:
        draw.ellipse([cx + wx - 75, cy + 90, cx + wx + 75, cy + 240], fill=DARK)
        draw.ellipse([cx + wx - 40, cy + 125, cx + wx + 40, cy + 205], fill=CREAM)
    draw_glossy_eyes(draw, cx + 130, cy + 45, cx + 240, cy + 45, r=20)
    draw.arc([cx + 160, cy + 60, cx + 210, cy + 100], 10, 170, fill=DARK, width=12)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_box():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2 + 30
    draw.rectangle([cx - 260, cy - 150, cx + 260, cy + 240], fill=GOLD, outline=DARK, width=28)
    draw.rectangle([cx - 280, cy - 220, cx + 280, cy - 140], fill=ORANGE, outline=DARK, width=28)
    draw.rectangle([cx - 40, cy - 150, cx + 40, cy + 240], fill=RED)
    draw.rectangle([cx - 40, cy - 220, cx + 40, cy - 140], fill=RED)
    draw.ellipse([cx - 100, cy - 300, cx - 10, cy - 200], fill=RED, outline=DARK, width=20)
    draw.ellipse([cx + 10, cy - 300, cx + 100, cy - 200], fill=RED, outline=DARK, width=20)
    draw_glossy_eyes(draw, cx - 80, cy + 50, cx + 80, cy + 50, r=26)
    draw_blush(draw, cx - 140, cy + 85, cx + 140, cy + 85, rw=35, rh=20)
    draw.arc([cx - 50, cy + 75, cx + 50, cy + 135], 10, 170, fill=DARK, width=16)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_fox():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.polygon([(cx - 250, cy - 80), (cx - 180, cy - 320), (cx - 50, cy - 170)], fill=ORANGE, outline=DARK)
    draw.polygon([(cx - 210, cy - 90), (cx - 170, cy - 270), (cx - 80, cy - 160)], fill=DARK)
    draw.polygon([(cx + 250, cy - 80), (cx + 180, cy - 320), (cx + 50, cy - 180)], fill=ORANGE, outline=DARK)
    draw.polygon([(cx + 210, cy - 90), (cx + 170, cy - 270), (cx + 80, cy - 160)], fill=DARK)
    draw.ellipse([cx - 270, cy - 190, cx + 270, cy + 230], fill=ORANGE, outline=DARK, width=28)
    draw.polygon([(cx, cy + 190), (cx - 240, cy + 20), (cx - 110, cy - 60), (cx, cy), (cx + 110, cy - 60), (cx + 240, cy + 20)], fill=WHITE, outline=DARK)
    draw_glossy_eyes(draw, cx - 95, cy - 30, cx + 95, cy - 30, r=32)
    draw_blush(draw, cx - 165, cy + 40, cx + 165, cy + 40, rw=42, rh=24)
    draw.ellipse([cx - 25, cy + 145, cx + 25, cy + 185], fill=DARK)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_zoo():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    draw.rounded_rectangle([cx - 290, cy - 200, cx + 290, cy + 250], radius=50, fill=WOOD, outline=DARK, width=28)
    draw.rounded_rectangle([cx - 180, cy - 60, cx + 180, cy + 250], radius=50, fill=WHITE, outline=DARK, width=22)
    draw.rounded_rectangle([cx - 240, cy - 290, cx + 240, cy - 130], radius=35, fill=GOLD, outline=DARK, width=22)
    draw.ellipse([cx - 90, cy + 20, cx + 90, cy + 200], fill=ORANGE, outline=DARK, width=18)
    draw_glossy_eyes(draw, cx - 35, cy + 90, cx + 35, cy + 90, r=14)
    draw.arc([cx - 30, cy + 110, cx + 30, cy + 150], 10, 170, fill=DARK, width=10)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

def draw_web():
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = W/2, H/2
    for a in range(0, 360, 45):
        rad = math.radians(a)
        draw.line([(cx, cy), (cx + 340 * math.cos(rad), cy + 340 * math.sin(rad))], fill=BLUE_SKY, width=14)
    for r in [100, 190, 280]:
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], outline=BLUE_SKY, width=12)
    spx, spy = cx + 140, cy + 120
    draw.ellipse([spx - 55, spy - 55, spx + 55, spy + 55], fill=DARK)
    for sa in [-60, -20, 20, 60]:
        srad = math.radians(sa)
        draw.line([(spx, spy), (spx - 90 * math.cos(srad), spy + 90 * math.sin(srad))], fill=DARK, width=10)
        draw.line([(spx, spy), (spx + 90 * math.cos(srad), spy + 90 * math.sin(srad))], fill=DARK, width=10)
    draw_glossy_eyes(draw, spx - 18, spy - 10, spx + 18, spy - 10, r=10)
    draw.arc([spx - 15, spy + 5, spx + 15, spy + 25], 10, 170, fill=WHITE, width=6)
    return img.resize((SIZE, SIZE), Image.Resampling.LANCZOS)

# ── Master Map ────────────────────────────────────────────────────────────────

ALL_GENERATORS = {
    # Group 1
    "blendword_sam": draw_sam,
    "blendword_sis": draw_sis,
    "blendword_aim": draw_aim,
    # Group 2
    "blendword_bus": draw_bus,
    "blendword_sub": draw_sub,
    "blendword_mom": draw_mom,
    "blendword_bee": draw_bee,
    "blendword_bib": draw_bib,
    # Group 3
    "blendword_bat": draw_bat,
    "blendword_mat": draw_mat,
    "blendword_kit": draw_kit,
    "blendword_toy": draw_toy,
    "blendword_boy": draw_boy,
    # Group 4
    "blendword_pig": draw_pig,
    "blendword_pan": draw_pan,
    "blendword_bug": draw_bug,
    "blendword_pin": draw_pin,
    "blendword_nap": draw_nap,
    # Group 5
    "blendword_dog": draw_dog,
    "blendword_hat": draw_hat,
    "blendword_hen": draw_hen,
    "blendword_bed": draw_bed,
    "blendword_hand": draw_hand,
    # Group 6
    "blendword_cat": draw_cat,
    "blendword_fan": draw_fan,
    "blendword_cap": draw_cap,
    "blendword_cup": draw_cup,
    "blendword_jam": draw_jam,
    # Group 7
    "blendword_van": draw_van,
    "blendword_box": draw_box,
    "blendword_fox": draw_fox,
    "blendword_zoo": draw_zoo,
    "blendword_web": draw_web,
}

def main():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    target_dir = os.path.join(base_dir, "app", "src", "main", "assets", "images", "pictures")
    os.makedirs(target_dir, exist_ok=True)

    print("=" * 80)
    print(f"[*] Generating Complete 33-Word Blend It Suite (4-Benchmark Vector Art)...")
    print("=" * 80)

    for name, func in ALL_GENERATORS.items():
        img = func()
        out_path = os.path.join(target_dir, f"{name}.png")
        img.save(out_path, "PNG", optimize=True)
        print(f"  [+] Generated: {name}.png ({os.path.getsize(out_path)} bytes)")

    print("=" * 80)
    print("[*] All 33 Blend It illustrations successfully generated!")

if __name__ == "__main__":
    main()
