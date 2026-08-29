"""
4-Benchmark Unified Map Prop Generator for PlayIT
Styles: Duolingo ABC + Khan Academy Kids + Drops (#2D373E outline) + Headspace
Output: app/src/main/assets/images/backgrounds/*.png
"""

import os
from PIL import Image, ImageDraw, ImageFilter

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUTPUT_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images", "backgrounds")
os.makedirs(OUTPUT_DIR, exist_ok=True)

OUTLINE = (45, 55, 62, 255)       # #2D373E
OUTLINE_WIDTH = 12

# PlayIT Unified Palette
MANGO = (255, 179, 71, 255)
MANGO_DARK = (245, 158, 11, 255)
UBE = (139, 92, 246, 255)
UBE_DARK = (109, 40, 217, 255)
LEAF = (34, 197, 94, 255)
LEAF_DARK = (21, 128, 61, 255)
LEAF_LIGHT = (134, 239, 172, 255)
GUAVA = (244, 63, 94, 255)
GUAVA_LIGHT = (251, 113, 133, 255)
SKY = (56, 189, 248, 255)
SKY_DARK = (2, 132, 199, 255)
TAN = (217, 119, 6, 255)
TAN_LIGHT = (245, 158, 11, 255)
BROWN = (146, 64, 14, 255)
BROWN_LIGHT = (180, 83, 9, 255)
BAMBOO = (251, 191, 36, 255)
THATCH = (217, 119, 6, 255)
CLOUD = (255, 255, 255, 255)
ROSE = (254, 205, 211, 255)

def apply_unified_stroke(fill_img, stroke_w=OUTLINE_WIDTH, stroke_color=OUTLINE):
    alpha = fill_img.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_w * 2 + 1))
    stroke_base = Image.new("RGBA", fill_img.size, stroke_color)
    stroke_layer = Image.new("RGBA", fill_img.size, (0, 0, 0, 0))
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, fill_img)

def create_nipa_hut():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # Stilts & Bamboo Floor
    d.rounded_rectangle([130, 270, 382, 420], radius=16, fill=BAMBOO)
    d.rounded_rectangle([110, 390, 140, 470], radius=8, fill=BROWN)
    d.rounded_rectangle([372, 390, 402, 470], radius=8, fill=BROWN)
    d.rounded_rectangle([240, 390, 270, 470], radius=8, fill=BROWN)
    # Thatch Roof
    d.polygon([(256, 70), (70, 290), (442, 290)], fill=THATCH)
    d.rounded_rectangle([60, 275, 452, 305], radius=12, fill=BROWN_LIGHT)
    # Cute Window & Door
    d.rounded_rectangle([210, 310, 302, 420], radius=14, fill=BROWN)
    d.rounded_rectangle([145, 310, 185, 360], radius=10, fill=SKY)
    d.rounded_rectangle([327, 310, 367, 360], radius=10, fill=SKY)
    res = apply_unified_stroke(img)
    # Window panes & details
    d_res = ImageDraw.Draw(res)
    d_res.line([165, 310, 165, 360], fill=OUTLINE, width=4)
    d_res.line([145, 335, 185, 335], fill=OUTLINE, width=4)
    d_res.line([347, 310, 347, 360], fill=OUTLINE, width=4)
    d_res.line([327, 335, 367, 335], fill=OUTLINE, width=4)
    d_res.ellipse([284, 365, 296, 377], fill=MANGO)
    return res

def create_palm_tree():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # Curved Trunk
    d.rounded_rectangle([220, 200, 280, 470], radius=18, fill=BROWN)
    # Coconuts
    d.ellipse([210, 170, 260, 220], fill=BROWN_LIGHT)
    d.ellipse([252, 170, 302, 220], fill=BROWN_LIGHT)
    d.ellipse([230, 195, 280, 245], fill=BROWN_LIGHT)
    # Lush Fronds
    d.pieslice([40, 40, 290, 290], 180, 340, fill=LEAF)
    d.pieslice([220, 40, 470, 290], 200, 360, fill=LEAF)
    d.pieslice([100, 10, 412, 310], 210, 330, fill=LEAF_LIGHT)
    res = apply_unified_stroke(img)
    d_res = ImageDraw.Draw(res)
    for y in range(250, 450, 40):
        d_res.arc([220, y, 280, y + 25], 0, 180, fill=OUTLINE, width=5)
    return res

def create_flower():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # Stem & Leaf
    d.rounded_rectangle([240, 250, 272, 470], radius=12, fill=LEAF)
    d.ellipse([140, 330, 248, 390], fill=LEAF_LIGHT)
    d.ellipse([264, 290, 372, 350], fill=LEAF_LIGHT)
    # 5 Petals (Gumamela / Tropical Flower)
    centers = [(256, 120), (360, 180), (320, 290), (192, 290), (152, 180)]
    for cx, cy in centers:
        d.ellipse([cx - 65, cy - 65, cx + 65, cy + 65], fill=GUAVA)
    # Center
    d.ellipse([256 - 55, 210 - 55, 256 + 55, 210 + 55], fill=MANGO)
    res = apply_unified_stroke(img)
    # Kawaii Face in Flower Center
    d_res = ImageDraw.Draw(res)
    d_res.ellipse([230, 195, 246, 215], fill=OUTLINE)
    d_res.ellipse([266, 195, 282, 215], fill=OUTLINE)
    d_res.ellipse([234, 198, 240, 204], fill=CLOUD)
    d_res.ellipse([270, 198, 276, 204], fill=CLOUD)
    d_res.arc([244, 210, 268, 226], 0, 180, fill=OUTLINE, width=4)
    d_res.ellipse([220, 212, 232, 222], fill=ROSE)
    d_res.ellipse([280, 212, 292, 222], fill=ROSE)
    return res

def create_bush():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.ellipse([80, 210, 280, 420], fill=LEAF)
    d.ellipse([230, 210, 430, 420], fill=LEAF)
    d.ellipse([140, 130, 370, 360], fill=LEAF_LIGHT)
    # Little berry blossoms
    d.ellipse([180, 210, 210, 240], fill=GUAVA)
    d.ellipse([300, 230, 330, 260], fill=GUAVA)
    d.ellipse([240, 170, 270, 200], fill=MANGO)
    return apply_unified_stroke(img)

def create_rock():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.ellipse([70, 220, 330, 430], fill=(168, 178, 185, 255))
    d.ellipse([210, 250, 440, 440], fill=(203, 213, 225, 255))
    # Moss top
    d.ellipse([110, 220, 240, 300], fill=LEAF_LIGHT)
    return apply_unified_stroke(img)

def create_tree_small():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # Trunk
    d.rounded_rectangle([226, 260, 286, 460], radius=16, fill=BROWN)
    # Round Foliage
    d.ellipse([90, 80, 422, 380], fill=LEAF)
    d.ellipse([130, 100, 382, 320], fill=LEAF_LIGHT)
    # Fruit highlights
    d.ellipse([170, 180, 206, 216], fill=MANGO)
    d.ellipse([306, 190, 342, 226], fill=MANGO)
    d.ellipse([240, 130, 276, 166], fill=GUAVA)
    return apply_unified_stroke(img)

def create_paper_airplane():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.polygon([(100, 256), (420, 120), (280, 380)], fill=CLOUD)
    d.polygon([(280, 380), (420, 120), (260, 270)], fill=(224, 242, 254, 255))
    d.polygon([(100, 256), (420, 120), (260, 270)], fill=(186, 230, 253, 255))
    return apply_unified_stroke(img)

def create_book_stack():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # Book 1 (Bottom - Ube)
    d.rounded_rectangle([90, 330, 422, 420], radius=16, fill=UBE)
    d.rounded_rectangle([130, 345, 410, 405], radius=8, fill=CLOUD)
    # Book 2 (Middle - Mango)
    d.rounded_rectangle([110, 230, 402, 320], radius=16, fill=MANGO)
    d.rounded_rectangle([145, 245, 390, 305], radius=8, fill=CLOUD)
    # Book 3 (Top - Guava)
    d.rounded_rectangle([140, 130, 372, 220], radius=16, fill=GUAVA)
    d.rounded_rectangle([170, 145, 360, 205], radius=8, fill=CLOUD)
    return apply_unified_stroke(img)

def create_crayon_bridge():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # Arched crayons
    colors = [GUAVA, MANGO, LEAF, SKY, UBE]
    for i, c in enumerate(colors):
        y = 120 + i * 55
        d.rounded_rectangle([90, y, 390, y + 42], radius=12, fill=c)
        d.polygon([(390, y + 6), (440, y + 21), (390, y + 36)], fill=c)
    return apply_unified_stroke(img)

def create_globe():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # Base
    d.rounded_rectangle([190, 420, 322, 465], radius=12, fill=MANGO)
    d.rounded_rectangle([240, 340, 272, 430], radius=8, fill=MANGO_DARK)
    # Arc Holder
    d.arc([110, 100, 402, 392], 280, 170, fill=MANGO, width=32)
    # Sphere
    d.ellipse([140, 110, 372, 342], fill=SKY)
    # Continents
    d.ellipse([180, 140, 250, 220], fill=LEAF)
    d.ellipse([270, 190, 340, 280], fill=LEAF)
    d.ellipse([190, 250, 260, 310], fill=LEAF)
    return apply_unified_stroke(img)

def create_magnifying_glass():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # Handle
    d.rounded_rectangle([280, 280, 440, 440], radius=24, fill=MANGO)
    # Rim
    d.ellipse([90, 90, 340, 340], fill=MANGO_DARK)
    # Glass Lens
    d.ellipse([120, 120, 310, 310], fill=(224, 242, 254, 255))
    # Sparkle / Reflection
    d.ellipse([150, 140, 200, 190], fill=CLOUD)
    return apply_unified_stroke(img)

def create_pencil_tower():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    # Pencil Body
    d.rounded_rectangle([206, 170, 306, 410], radius=12, fill=MANGO)
    # Eraser on bottom/top
    d.rounded_rectangle([206, 400, 306, 450], radius=12, fill=GUAVA_LIGHT)
    d.rounded_rectangle([206, 385, 306, 405], radius=4, fill=(203, 213, 225, 255))
    # Tip
    d.polygon([(206, 175), (256, 70), (306, 175)], fill=BAMBOO)
    d.polygon([(240, 105), (256, 70), (272, 105)], fill=OUTLINE)
    return apply_unified_stroke(img)

def create_backpack():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.rounded_rectangle([130, 130, 382, 440], radius=40, fill=MANGO)
    d.rounded_rectangle([170, 250, 342, 400], radius=20, fill=MANGO_DARK)
    d.rounded_rectangle([210, 100, 302, 140], radius=10, fill=UBE)
    # Straps & Buckles
    d.rounded_rectangle([236, 280, 276, 320], radius=8, fill=SKY)
    return apply_unified_stroke(img)

def create_paint_palette():
    img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.ellipse([100, 120, 412, 400], fill=BAMBOO)
    d.ellipse([310, 300, 365, 355], fill=(0, 0, 0, 0)) # Thumb hole
    # Paint Blobs
    d.ellipse([140, 180, 190, 230], fill=GUAVA)
    d.ellipse([210, 140, 260, 190], fill=MANGO)
    d.ellipse([285, 150, 335, 200], fill=LEAF)
    d.ellipse([340, 200, 390, 250], fill=SKY)
    d.ellipse([170, 280, 220, 330], fill=UBE)
    return apply_unified_stroke(img)

def main():
    generators = {
        "mapprop_nipa_hut.png": create_nipa_hut,
        "mapprop_palm_tree.png": create_palm_tree,
        "mapprop_flower.png": create_flower,
        "map_prop_bush.png": create_bush,
        "map_prop_rock.png": create_rock,
        "map_prop_tree_small.png": create_tree_small,
        "mapprop_paper_airplane.png": create_paper_airplane,
        "mapprop_book_stack.png": create_book_stack,
        "mapprop_crayon_bridge.png": create_crayon_bridge,
        "mapprop_globe.png": create_globe,
        "mapprop_magnifying_glass.png": create_magnifying_glass,
        "mapprop_pencil_tower.png": create_pencil_tower,
        "mapprop_backpack.png": create_backpack,
        "mapprop_paint_palette.png": create_paint_palette,
        "mapprop_eraser_shrub.png": create_bush,
        "mapprop_ruler_ramp.png": create_crayon_bridge
    }

    print("=" * 80)
    print("[*] Generating Unified 4-Benchmark Map Props (100% Cohesive Art Style)...")
    print("=" * 80)

    for fname, func in generators.items():
        img = func()
        out_path = os.path.join(OUTPUT_DIR, fname)
        img.save(out_path, format="PNG")
        print(f"  [+] Generated: {fname:<30} [{os.path.getsize(out_path):>7} bytes]")

    print("=" * 80)
    print("[*] All Map Props successfully unified and saved!")

if __name__ == "__main__":
    main()
