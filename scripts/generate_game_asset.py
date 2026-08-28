"""
PlayIT Asset Generation Pipeline (Free & Unlimited via Pollinations + rembg)

Usage:
  python scripts/generate_game_asset.py --type picture --name cat --prompt "cute cartoon orange cat sitting happily"
  python scripts/generate_game_asset.py --type mascot --name lily_happy --prompt "cute tarsier character cheering with hands up"
  python scripts/generate_game_asset.py --type letter --name letter_card_m --prompt "wooden block letter M with small mouse"
"""

import os
import sys
import argparse
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
ASSETS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "images")

STYLE_SUFFIX = (
    "children educational book illustration style, Duolingo ABC aesthetic, "
    "bold clean outlines, warm pastel palette, soft rounded shapes, flat vector 2D art, "
    "pure solid white background, high contrast, pediatric friendly, no text, no watermark"
)

def generate_asset(category: str, name: str, custom_prompt: str, remove_bg: bool = True, size: int = 512):
    full_prompt = f"{custom_prompt}, {STYLE_SUFFIX}"
    encoded_prompt = urllib.parse.quote(full_prompt)
    
    # Pollinations AI free image endpoint with Flux model
    url = f"https://image.pollinations.ai/prompt/{encoded_prompt}?width=1024&height=1024&model=flux&nologo=true&seed=42"
    
    print(f"[*] Generating asset: {name} (Category: {category})")
    print(f"[*] Prompt: {full_prompt}")
    print("[*] Requesting image from Pollinations...")
    
    response = requests.get(url, timeout=90)
    if response.status_code != 200:
        print(f"[!] Error: Pollinations API returned status code {response.status_code}")
        return False
        
    image_data = response.content
    img = Image.open(BytesIO(image_data)).convert("RGBA")
    
    if remove_bg:
        if REMBG_AVAILABLE:
            print("[*] Removing background with rembg...")
            img = remove(img)
        else:
            print("[!] rembg not available, skipping background removal.")
            
    # Resize and normalize
    img.thumbnail((size, size), Image.Resampling.LANCZOS)
    
    # Create final transparent canvas
    final_img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    offset = ((size - img.width) // 2, (size - img.height) // 2)
    final_img.paste(img, offset, mask=img)
    
    target_dir = os.path.join(ASSETS_DIR, category)
    os.makedirs(target_dir, exist_ok=True)
    
    output_path = os.path.join(target_dir, f"{name}.png")
    final_img.save(output_path, "PNG", optimize=True)
    print(f"[+] Successfully saved optimized asset to: {output_path}")
    return True

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="PlayIT Asset Generator")
    parser.add_argument("--type", choices=["pictures", "letters", "mascot", "rewards", "backgrounds"], default="pictures", help="Target category folder")
    parser.add_argument("--name", required=True, help="Filename without extension (e.g. picture_cat)")
    parser.add_argument("--prompt", required=True, help="Visual subject description")
    parser.add_argument("--no-rembg", action="store_true", help="Do not remove background")
    parser.add_argument("--size", type=int, default=512, help="Output dimensions (square)")
    
    args = parser.parse_args()
    generate_asset(args.type, args.name, args.prompt, remove_bg=not args.no_rembg, size=args.size)
