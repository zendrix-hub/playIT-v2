#!/usr/bin/env python3
"""
tools/optimize_app_assets.py
----------------------------
Automated asset pruning and WebP modernization script for PlayIT.

1. Archives unused prerendered letter cards from app/src/main/assets/images/letters/
   into archive/assets_prerendered_letters/ (saving 5.89 MB).
2. Removes unreferenced design reference sheets (_style-reference-sheet/) and stray audio files.
3. Removes redundant avatar duplicates (companion_avatar_* and splash_tarsier_headspace).
4. Converts all active PNG assets (pictures/, mascot/, characters/, rewards/)
   to Google WebP format at Quality 95 with lossless alpha transparency.
5. Verifies every converted image and removes the superseded PNG files.
"""

import os
import shutil
import sys
from PIL import Image

PROJECT_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
ASSETS_DIR = os.path.join(PROJECT_ROOT, "app", "src", "main", "assets")
IMAGES_DIR = os.path.join(ASSETS_DIR, "images")
ARCHIVE_DIR = os.path.join(PROJECT_ROOT, "archive", "assets_prerendered_letters")

def step_archive_unused_letters():
    letters_dir = os.path.join(IMAGES_DIR, "letters")
    if not os.path.exists(letters_dir):
        print("Letters directory does not exist or already archived.")
        return 0

    os.makedirs(ARCHIVE_DIR, exist_ok=True)
    saved_bytes = 0
    count = 0

    for f in os.listdir(letters_dir):
        src = os.path.join(letters_dir, f)
        if os.path.isfile(src):
            saved_bytes += os.path.getsize(src)
            shutil.copy2(src, os.path.join(ARCHIVE_DIR, f))
            os.remove(src)
            count += 1

    # Remove empty directory
    try:
        os.rmdir(letters_dir)
    except Exception:
        pass

    print(f"[Phase 1] Archived {count} unused letter cards ({saved_bytes / (1024*1024):.2f} MB) to archive/assets_prerendered_letters/")
    return saved_bytes

def step_remove_dead_files():
    dead_paths = [
        os.path.join(IMAGES_DIR, "_style-reference-sheet", "anchor_letter-card.png"),
        os.path.join(IMAGES_DIR, "_style-reference-sheet"),
        os.path.join(ASSETS_DIR, "audio", "tts_[exci_20260816_104503.mp3"),
        os.path.join(IMAGES_DIR, "mascot", "splash_tarsier_headspace.png"),
    ]
    # Redundant companion_avatar_* in mascot (exact duplicates of characters/)
    for i in range(1, 7):
        for animal in ["cat", "monkey", "bunny", "bear", "frog", "owl"]:
            p = os.path.join(IMAGES_DIR, "mascot", f"companion_avatar_0{i}_{animal}.png")
            if os.path.exists(p):
                dead_paths.append(p)

    saved_bytes = 0
    removed_count = 0
    for p in dead_paths:
        if os.path.isfile(p):
            saved_bytes += os.path.getsize(p)
            os.remove(p)
            removed_count += 1
        elif os.path.isdir(p):
            try:
                os.rmdir(p)
            except Exception:
                pass

    print(f"[Phase 2] Removed {removed_count} unreferenced files ({saved_bytes / 1024:.1f} KB)")
    return saved_bytes

def step_convert_png_to_webp():
    converted_count = 0
    orig_total_bytes = 0
    webp_total_bytes = 0

    for root, _, files in os.walk(IMAGES_DIR):
        for f in files:
            if not f.lower().endswith(".png"):
                continue

            png_path = os.path.join(root, f)
            base_name = os.path.splitext(f)[0]
            webp_path = os.path.join(root, f"{base_name}.webp")

            png_size = os.path.getsize(png_path)
            orig_total_bytes += png_size

            # Convert to WebP Q95
            img = Image.open(png_path)
            img.save(webp_path, format="WEBP", quality=95, method=6)

            # Verify converted file
            verify_img = Image.open(webp_path)
            verify_img.verify()
            webp_size = os.path.getsize(webp_path)
            webp_total_bytes += webp_size

            # Delete original PNG
            os.remove(png_path)
            converted_count += 1

    saved = orig_total_bytes - webp_total_bytes
    print(f"[Phase 3] Converted {converted_count} PNGs to WebP Q95.")
    print(f"          Original size:  {orig_total_bytes / (1024*1024):.2f} MB")
    print(f"          WebP size:      {webp_total_bytes / (1024*1024):.2f} MB")
    print(f"          Saved:          {saved / (1024*1024):.2f} MB ({(saved / orig_total_bytes)*100:.1f}%)")
    return saved

def main():
    print("=== PlayIT Asset Optimizer & Size Reducer ===")
    s1 = step_archive_unused_letters()
    s2 = step_remove_dead_files()
    s3 = step_convert_png_to_webp()

    total_saved = s1 + s2 + s3
    print("=============================================")
    print(f"Total Disk Savings in Assets: {total_saved / (1024*1024):.2f} MB")
    print("=============================================")

if __name__ == "__main__":
    main()
