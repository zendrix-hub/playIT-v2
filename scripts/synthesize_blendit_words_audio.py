"""
PlayIT Word Audio Synthesis Script (Microsoft Neural TTS)
Synthesizes clean, high-definition audio clips for all 33 Blend It words into app/src/main/assets/audio/words/
"""

import os
import asyncio
import edge_tts

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TARGET_WORDS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "audio", "words")
os.makedirs(TARGET_WORDS_DIR, exist_ok=True)

VOICE_NAME = "en-US-AnaNeural"  # Friendly, clear pediatric voice
RATE = "+0%"
PITCH = "+0Hz"

ALL_BLEND_WORDS = {
    "word_sam": "Sam",
    "word_sis": "Sis",
    "word_aim": "Aim",
    "word_bus": "Bus",
    "word_sub": "Sub",
    "word_mom": "Mom",
    "word_bee": "Bee",
    "word_bib": "Bib",
    "word_bat": "Bat",
    "word_mat": "Mat",
    "word_kit": "Kit",
    "word_toy": "Toy",
    "word_boy": "Boy",
    "word_pig": "Pig",
    "word_pan": "Pan",
    "word_bug": "Bug",
    "word_pin": "Pin",
    "word_nap": "Nap",
    "word_dog": "Dog",
    "word_hat": "Hat",
    "word_hen": "Hen",
    "word_bed": "Bed",
    "word_hand": "Hand",
    "word_cat": "Cat",
    "word_fan": "Fan",
    "word_cap": "Cap",
    "word_cup": "Cup",
    "word_jam": "Jam",
    "word_van": "Van",
    "word_box": "Box",
    "word_fox": "Fox",
    "word_zoo": "Zoo",
    "word_web": "Web",
}

async def generate_word(filename: str, word: str):
    output_path = os.path.join(TARGET_WORDS_DIR, f"{filename}.mp3")
    print(f"[*] Synthesizing: {filename}.mp3 -> '{word}'")
    communicate = edge_tts.Communicate(word, VOICE_NAME, rate=RATE, pitch=PITCH)
    await communicate.save(output_path)
    print(f"  [+] Saved: {output_path} ({os.path.getsize(output_path)} bytes)")

async def main():
    print("=" * 80)
    print(f"[*] Synthesizing Word Audio Clips for all 33 Blend It Words...")
    print("=" * 80)
    for filename, word in ALL_BLEND_WORDS.items():
        await generate_word(filename, word)
    print("=" * 80)
    print("[*] All 33 Blend It word audios successfully synthesized!")

if __name__ == "__main__":
    asyncio.run(main())
