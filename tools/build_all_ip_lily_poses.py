"""
Build all 7 canonical mascot poses for the new IP-as-Logo Lily the Tarsier.
Uses the master isolated character sprite, composing vector-clean limb transformations,
proper anatomical fill under moved limbs, head tilts, facial expressions, and celebratory particles.
"""

import os
import math
from PIL import Image, ImageDraw, ImageFilter, ImageOps
import numpy as np

BRAIN_DIR = "/home/zendrix/.gemini/antigravity-cli/brain/af2db79b-86c4-4925-a8b9-dd27044dffec"
WORKSPACE_DIR = "/mnt/c/Users/Zendrix/Desktop/thesis/playIT-v2-workspace"
MASCOT_DIR = os.path.join(WORKSPACE_DIR, "app", "src", "main", "assets", "images", "mascot")
MASTER_SRC = os.path.join(BRAIN_DIR, "test_ip_tarsier_isolated.png")
OUTLINE_COLOR = (45, 55, 62, 255)  # #2D373E
BODY_COLOR = (229, 166, 53, 255)  # Warm honey caramel body
INNER_STROKE_COLOR = (141, 90, 27, 255)  # Warm brown boundary

def apply_pediatric_outline(image: Image.Image, stroke_width: int = 6) -> Image.Image:
    alpha = image.split()[3]
    expanded_alpha = alpha.filter(ImageFilter.MaxFilter(stroke_width * 2 + 1))
    stroke_layer = Image.new("RGBA", image.size, (0, 0, 0, 0))
    stroke_base = Image.new("RGBA", image.size, OUTLINE_COLOR)
    stroke_layer.paste(stroke_base, (0, 0), expanded_alpha)
    return Image.alpha_composite(stroke_layer, image)

def fit_to_canvas(img: Image.Image, size: int = 512, outline: int = 6) -> Image.Image:
    inner = size - (outline * 4) - 30
    img.thumbnail((inner, inner), Image.Resampling.LANCZOS)
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    x = (size - img.width) // 2
    y = (size - img.height) // 2
    canvas.paste(img, (x, y), img)
    if outline > 0:
        canvas = apply_pediatric_outline(canvas, stroke_width=outline)
    return canvas

def make_idle(master: Image.Image) -> Image.Image:
    return master.copy()

def make_waving(master: Image.Image) -> Image.Image:
    w, h = master.size
    img = master.copy()
    
    # 1. Crop right arm
    arm_box = (int(w * 0.72), int(h * 0.58), w, int(h * 0.88))
    arm = master.crop(arm_box)
    
    # 2. Re-fill the torso flank behind where the arm was with a smooth curved flank
    draw_base = ImageDraw.Draw(img)
    # Fill in smooth torso flank
    draw_base.ellipse([int(w * 0.35), int(h * 0.50), int(w * 0.78), int(h * 0.90)], fill=BODY_COLOR)
    # Re-draw the cream belly to keep it clean on top
    belly_box = [int(w * 0.31), int(h * 0.58), int(w * 0.69), int(h * 0.84)]
    draw_base.ellipse(belly_box, fill=(253, 234, 201, 255))
    
    # Clear out the old resting arm pixels that stick out beyond the torso
    mask = Image.new("L", (w, h), 255)
    draw_m = ImageDraw.Draw(mask)
    draw_m.rectangle([int(w * 0.75), int(h * 0.58), w, int(h * 0.90)], fill=0)
    img.putalpha(Image.fromarray(np.minimum(np.array(img.split()[3]), np.array(mask))))
    
    # 3. Rotate arm into an energetic wave
    rotated_arm = arm.rotate(-120, expand=True, resample=Image.Resampling.BICUBIC)
    
    # 4. Composite onto larger canvas
    comp = Image.new("RGBA", (int(w * 1.22), h), (0, 0, 0, 0))
    comp.paste(img, (0, 0), img)
    comp.paste(rotated_arm, (int(w * 0.68), int(h * 0.32)), rotated_arm)
    
    return comp.crop(comp.getbbox())

def make_listening(master: Image.Image) -> Image.Image:
    w, h = master.size
    # Attentive tilt: rotate whole character 7 degrees
    tilted = master.rotate(7, expand=True, resample=Image.Resampling.BICUBIC)
    tw, th = tilted.size
    
    # Add cute sound waves radiating to right ear
    draw = ImageDraw.Draw(tilted)
    arc_color = (229, 166, 53, 255)
    arc_x = int(tw * 0.86)
    arc_y = int(th * 0.16)
    draw.arc([arc_x, arc_y, arc_x + 36, arc_y + 54], start=-60, end=60, fill=arc_color, width=8)
    draw.arc([arc_x + 18, arc_y - 12, arc_x + 68, arc_y + 66], start=-60, end=60, fill=arc_color, width=8)
    
    return tilted.crop(tilted.getbbox())

def make_pointing(master: Image.Image) -> Image.Image:
    w, h = master.size
    img = master.copy()
    
    # 1. Crop right arm
    arm_box = (int(w * 0.72), int(h * 0.58), w, int(h * 0.88))
    arm = master.crop(arm_box)
    
    # 2. Re-fill smooth torso
    draw_base = ImageDraw.Draw(img)
    draw_base.ellipse([int(w * 0.35), int(h * 0.50), int(w * 0.78), int(h * 0.90)], fill=BODY_COLOR)
    draw_base.ellipse([int(w * 0.31), int(h * 0.58), int(w * 0.69), int(h * 0.84)], fill=(253, 234, 201, 255))
    
    mask = Image.new("L", (w, h), 255)
    draw_m = ImageDraw.Draw(mask)
    draw_m.rectangle([int(w * 0.75), int(h * 0.58), w, int(h * 0.90)], fill=0)
    img.putalpha(Image.fromarray(np.minimum(np.array(img.split()[3]), np.array(mask))))
    
    # 3. Rotate arm pointing horizontally
    pointed_arm = arm.rotate(-80, expand=True, resample=Image.Resampling.BICUBIC)
    
    # 4. Composite
    comp = Image.new("RGBA", (int(w * 1.30), h), (0, 0, 0, 0))
    comp.paste(img, (0, 0), img)
    comp.paste(pointed_arm, (int(w * 0.72), int(h * 0.46)), pointed_arm)
    
    return comp.crop(comp.getbbox())

def make_encouraging(master: Image.Image) -> Image.Image:
    w, h = master.size
    img = master.copy()
    
    # Warm welcoming hug squish
    stretched = img.resize((int(w * 1.04), int(h * 0.97)), Image.Resampling.LANCZOS)
    sw, sh = stretched.size
    
    # Rosy blushing cheeks
    overlay = Image.new("RGBA", (sw, sh), (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    draw.ellipse([int(sw * 0.20), int(sh * 0.44), int(sw * 0.30), int(sh * 0.50)], fill=(255, 130, 130, 175))
    draw.ellipse([int(sw * 0.70), int(sh * 0.44), int(sw * 0.80), int(sh * 0.50)], fill=(255, 130, 130, 175))
    
    res = Image.alpha_composite(stretched, overlay)
    return res.crop(res.getbbox())

def make_thinking(master: Image.Image) -> Image.Image:
    w, h = master.size
    tilted = master.rotate(-6, expand=True, resample=Image.Resampling.BICUBIC)
    tw, th = tilted.size
    
    overlay = Image.new("RGBA", (tw, th), (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    dot_color = (255, 184, 0, 240)
    
    draw.ellipse([int(tw * 0.82), int(th * 0.26), int(tw * 0.82) + 14, int(th * 0.26) + 14], fill=dot_color)
    draw.ellipse([int(tw * 0.87), int(th * 0.17), int(tw * 0.87) + 20, int(th * 0.17) + 20], fill=dot_color)
    draw.ellipse([int(tw * 0.92), int(th * 0.06), int(tw * 0.92) + 28, int(th * 0.06) + 28], fill=dot_color)
    
    res = Image.alpha_composite(tilted, overlay)
    return res.crop(res.getbbox())

def make_celebrating(master: Image.Image) -> Image.Image:
    w, h = master.size
    img = master.copy()
    
    # 1. Crop arms
    l_arm_box = (0, int(h * 0.58), int(w * 0.28), int(h * 0.88))
    r_arm_box = (int(w * 0.72), int(h * 0.58), w, int(h * 0.88))
    
    l_arm = master.crop(l_arm_box)
    r_arm = master.crop(r_arm_box)
    
    # Re-fill smooth torso on both sides
    draw_base = ImageDraw.Draw(img)
    draw_base.ellipse([int(w * 0.22), int(h * 0.50), int(w * 0.78), int(h * 0.90)], fill=BODY_COLOR)
    draw_base.ellipse([int(w * 0.31), int(h * 0.58), int(w * 0.69), int(h * 0.84)], fill=(253, 234, 201, 255))
    
    mask = Image.new("L", (w, h), 255)
    draw_m = ImageDraw.Draw(mask)
    draw_m.rectangle([0, int(h * 0.58), int(w * 0.24), int(h * 0.90)], fill=0)
    draw_m.rectangle([int(w * 0.76), int(h * 0.58), w, int(h * 0.90)], fill=0)
    img.putalpha(Image.fromarray(np.minimum(np.array(img.split()[3]), np.array(mask))))
    
    # Raised arms in celebration
    l_raised = l_arm.rotate(120, expand=True, resample=Image.Resampling.BICUBIC)
    r_raised = r_arm.rotate(-120, expand=True, resample=Image.Resampling.BICUBIC)
    
    canvas_w = int(w * 1.35)
    canvas_h = int(h * 1.25)
    comp = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
    
    body_x = (canvas_w - w) // 2
    body_y = int(canvas_h * 0.15)
    comp.paste(img, (body_x, body_y), img)
    
    comp.paste(l_raised, (body_x - int(l_raised.width * 0.40), body_y + int(h * 0.26)), l_raised)
    comp.paste(r_raised, (body_x + w - int(r_raised.width * 0.60), body_y + int(h * 0.26)), r_raised)
    
    # Celebratory gold stars
    draw = ImageDraw.Draw(comp)
    def draw_star(cx, cy, r_outer, r_inner, color=(255, 199, 0, 255)):
        points = []
        for i in range(10):
            angle = i * (math.pi / 5) - math.pi / 2
            r = r_outer if i % 2 == 0 else r_inner
            points.append((cx + r * math.cos(angle), cy + r * math.sin(angle)))
        draw.polygon(points, fill=color)
        
    draw_star(body_x + 10, body_y + 20, 24, 11)
    draw_star(body_x + w - 10, body_y + 15, 26, 12)
    draw_star(body_x + w // 2, body_y - 25, 20, 9)
    draw_star(body_x - 30, body_y + 110, 16, 7)
    draw_star(body_x + w + 30, body_y + 100, 18, 8)
    
    return comp.crop(comp.getbbox())

def main():
    master = Image.open(MASTER_SRC)
    print(f"Loaded master sprite: {master.size}")
    
    poses = {
        "lily_idle": make_idle(master),
        "lily_waving": make_waving(master),
        "lily_listening": make_listening(master),
        "lily_pointing": make_pointing(master),
        "lily_encouraging": make_encouraging(master),
        "lily_thinking": make_thinking(master),
        "lily_celebrating": make_celebrating(master)
    }
    
    os.makedirs(MASCOT_DIR, exist_ok=True)
    
    for name, raw_img in poses.items():
        final_img = fit_to_canvas(raw_img, size=512, outline=6)
        
        prod_path = os.path.join(MASCOT_DIR, f"{name}.png")
        final_img.save(prod_path, format="PNG")
        print(f" -> Deployed: {prod_path}")
        
        preview_path = os.path.join(BRAIN_DIR, f"preview_new_{name}.png")
        final_img.save(preview_path, format="PNG")
        
    splash_dst = os.path.join(MASCOT_DIR, "splash_tarsier_headspace.png")
    final_img = fit_to_canvas(poses["lily_idle"], size=512, outline=6)
    final_img.save(splash_dst, format="PNG")
    print(f" -> Updated {splash_dst}")
    
    print("\nAll 7 new IP-as-Logo Lily poses built and deployed successfully!")

if __name__ == "__main__":
    main()
