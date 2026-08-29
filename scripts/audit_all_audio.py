"""
PlayIT Full Audio Asset Audit Tool (ASCII compatible)
"""

import os
import glob
import re

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
AUDIO_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "audio")
DB_MODULE = os.path.join(BASE_DIR, "app", "src", "main", "java", "com", "playit", "app", "di", "DatabaseModule.kt")

def audit_audio():
    print("=" * 80)
    print("PLAYIT AUDIO ASSET AUDIT REPORT")
    print("=" * 80)

    categories = ["phonemes", "words", "ui", "vo"]
    total_files = 0
    total_bytes = 0

    on_disk = {}

    for cat in categories:
        cat_dir = os.path.join(AUDIO_DIR, cat)
        if not os.path.exists(cat_dir):
            print(f"\n[!] MISSING CATEGORY DIRECTORY: {cat}")
            continue
        
        mp3s = sorted(glob.glob(os.path.join(cat_dir, "*.mp3")) + glob.glob(os.path.join(cat_dir, "*.wav")))
        on_disk[cat] = [os.path.basename(f) for f in mp3s]
        print(f"\n[CATEGORY: {cat.upper()}] Path: {cat} (Count: {len(mp3s)})")
        
        empty_files = []
        for f in mp3s:
            sz = os.path.getsize(f)
            total_files += 1
            total_bytes += sz
            fname = os.path.basename(f)
            if sz == 0:
                empty_files.append(fname)
                print(f"  [X] EMPTY (0 bytes): {fname}")
            else:
                print(f"  [+] {fname:<30} [{sz:>7} bytes]")
        
        if empty_files:
            print(f"  [!] WARNING: {len(empty_files)} empty audio files in {cat}!")

    # Check DatabaseModule references
    print("\n" + "=" * 80)
    print("CHECKING DATABASE REFERENCES (DatabaseModule.kt)")
    print("=" * 80)
    if os.path.exists(DB_MODULE):
        with open(DB_MODULE, "r", encoding="utf-8") as f:
            content = f.read()

        # Find all audio/*.mp3 strings
        audio_refs = re.findall(r'"(audio/[^"]+\.mp3)"', content)
        print(f"Total audio references in DatabaseModule.kt: {len(audio_refs)}")
        
        missing_db_refs = []
        for ref in audio_refs:
            full_path = os.path.join(BASE_DIR, "app", "src", "main", "assets", ref.replace("/", os.sep))
            if not os.path.exists(full_path):
                missing_db_refs.append(ref)
                print(f"  [X] MISSING DB AUDIO: {ref}")
            elif os.path.getsize(full_path) == 0:
                missing_db_refs.append(ref)
                print(f"  [!] ZERO BYTES DB AUDIO: {ref}")

        if not missing_db_refs:
            print("  [+] ALL database audio references EXIST and are NON-EMPTY! (100% Verified)")
        else:
            print(f"  [!] {len(missing_db_refs)} audio files needed by DatabaseModule are missing/empty!")

    print("\n" + "=" * 80)
    print(f"TOTAL AUDIO FILES ON DISK: {total_files} ({total_bytes / (1024*1024):.2f} MB)")
    print("=" * 80)

if __name__ == "__main__":
    audit_audio()
