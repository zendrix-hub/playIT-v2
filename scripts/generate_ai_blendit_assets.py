"""
Batch AI Generator for 9 Complex Blend It Words
Generates human, relatable, kid-friendly vector illustrations using Pollinations (Flux) + rembg.
Targets:
1. SAM: "A cheerful cute young cartoon boy named Sam, wearing a blue striped t-shirt and waving his hand happily, friendly smile, Khan Academy Kids character style, Duolingo ABC aesthetic"
2. SIS: "A cheerful cute young cartoon girl with pigtails and pink bows, wearing a cute yellow dress and waving her hand happily, friendly smile, Khan Academy Kids character style"
3. FACE: "A happy cute cartoon child's face showing big sparkling eyes, rosy cheeks, button nose, and joyful open smile, Khan Academy Kids character style"
4. HAND: "A friendly open cartoon toddler hand waving hello, 5 clearly defined fingers, palm creases, Duolingo ABC and Khan Academy Kids style"
5. DRAW: "A cute cartoon child's hand holding a bright red crayon drawing a colorful rainbow on paper, vibrant preschool learning illustration"
6. GAP: "A cute little green frog leaping across a clear gap between two stone riverbanks over blue water, clear stepping gap, educational cartoon"
7. MAT: "A cozy colorful woven floor mat with a cute pair of red slippers resting on it, welcoming preschool storybook illustration"
8. NAP: "A sweet cute toddler sleeping peacefully in a cozy bed tucked under a starry blanket with a fluffy pillow and Zzz bubbles, soothing preschool cartoon"
9. BAT: "An adorable friendly cartoon purple bat with cute open wings, big glossy eyes and rosy cheeks, cheerful storybook animal"
"""

import os
import urllib.parse
import requests
from io import BytesIO
from PIL import Image

try:
    from rembg import remove
    REMBG_AVAILABLE = True
except ImportError:
    REMBG_AVAILABLE = False

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TARGET_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images", "pictures")

STYLE = (
    "children educational book illustration, Duolingo ABC and Khan Academy Kids style, "
    "bold clean outlines, warm vibrant pediatric colors, friendly, pure solid white background, "
    "no text, no watermark, isolated central object, 2D vector flat art"
)

TARGETS = {
    "blendword_sam": ("cute cheerful little cartoon boy character named Sam waving enthusiastically, smiling with rosy cheeks, wearing a striped t-shirt", 101),
    "blendword_sis": ("cute cheerful little cartoon girl sister with pigtails and pink ribbon bows waving enthusiastically, wearing a yellow dress", 102),
    "blendword_face": ("cute happy cartoon child's face with big sparkling eyes, rosy cheeks, button nose, friendly smile, soft brown hair", 103),
    "blendword_hand": ("cute cartoon child's hand waving hello, open palm with 5 clear fingers, friendly gesture, preschool icon", 104),
    "blendword_draw": ("cute cartoon child's hand holding a chunky red crayon and drawing a colorful rainbow doodle, preschool art", 105),
    "blendword_gap": ("cute friendly green frog jumping across a clear gap between two green riverbanks over blue water, clear stepping gap", 106),
    "blendword_mat": ("a cozy colorful woven welcome floor mat with a pair of red slippers sitting on it, preschool storybook illustration", 107),
    "blendword_nap": ("a sweet cute toddler child sleeping peacefully in a cozy bed under a blanket with fluffy pillow and floating Zzz bubbles", 108),
    "blendword_bat": ("an adorable friendly purple cartoon fruit bat with open wings, big glossy eyes and rosy cheeks, cute storybook animal", 109),
}

def generate_one(name, prompt, seed):
    full_prompt = f"{prompt}, {STYLE}"
    encoded = urllib.parse.quote(full_prompt)
    url = f"https://image.pollinations.ai/prompt/{encoded}?width=1024&height=1024&model=flux&nologo=true&seed={seed}"
    print(f"[*] Requesting: {name} (seed {seed})...")
    
    try:
        resp = requests.get(url, timeout=90)
        if resp.status_code != 200:
            print(f"[!] Failed to fetch {name}: status {resp.status_code}")
            return False
        
        img = Image.open(BytesIO(resp.content)).convert("RGBA")
        
        if REMBG_AVAILABLE:
            print(f"[*] Removing background for {name}...")
            img = remove(img)
        
        # Crop transparent bounding box and pad to square 512x512
        bbox = img.getbbox()
        if bbox:
            cropped = img.crop(bbox)
            # Find scale to fit inside 460x460 with padding
            max_dim = max(cropped.size)
            scale = 440.0 / max_dim
            new_w = int(cropped.size[0] * scale)
            new_h = int(cropped.size[1] * scale)
            resized = cropped.resize((new_w, new_h), Image.Resampling.LANCZOS)
            
            final_img = Image.new("RGBA", (512, 512), (0, 0, 0, 0))
            offset_x = (512 - new_w) // 2
            offset_y = (512 - new_h) // 2
            final_img.paste(resized, (offset_x, offset_y), resized)
        else:
            final_img = img.resize((512, 512), Image.Resampling.LANCZOS)
            
        out_file = os.path.join(TARGET_DIR, f"{name}.png")
        final_img.save(out_file, "PNG", optimize=True)
        print(f"[+] Successfully saved {name}.png ({os.path.getsize(out_file)} bytes)")
        return True
    except Exception as e:
        print(f"[!] Error processing {name}: {e}")
        return False

def main():
    os.makedirs(TARGET_DIR, exist_ok=True)
    print("=" * 80)
    print("[*] Generating 9 Kid-Friendly Blend It Words via Pollinations + rembg...")
    print("=" * 80)
    
    for name, (prompt, seed) in TARGETS.items():
        generate_one(name, prompt, seed)

    print("=" * 80)
    print("[*] Completed batch generation!")

if __name__ == "__main__":
    main()
