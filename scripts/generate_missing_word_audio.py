import asyncio
import os
import edge_tts

OUTPUT_DIR = "app/src/main/assets/audio/words"

# 26 Say It phoneme example words
SAYIT_WORDS = [
    "mouse", "sun", "apple", "insect", "orange", "ball", "elephant", "umbrella",
    "tiger", "kite", "lion", "yoyo", "nest", "goat", "pig", "rabbit",
    "dog", "hat", "watch", "cat", "fish", "jug", "queen", "van", "box", "zebra"
]

# Other existing placeholder words in audio/words/
OTHER_PLACEHOLDERS = [
    "bam", "iguana", "lit", "octopus", "pencil", "sum", "turtle", "xylophone"
]

# Find It vocabulary words not currently present in audio/words/
FINDIT_WORDS = [
    "ant", "axe", "duck", "egg", "envelope", "gift", "igloo", "ink",
    "jet", "key", "king", "leaf", "map", "net", "nut", "owl", "ox",
    "quilt", "ring", "rocket", "six", "snake", "star", "top", "tree",
    "uncle", "up", "vase", "vest", "wing", "worm", "yak", "yarn", "zip"
]

SPECIAL_FILIPINO_WORDS = {
    "bano": ("baño", "fil-PH-BlessicaNeural"),
    "nino": ("niño", "fil-PH-BlessicaNeural"),
    "pina": ("piña", "fil-PH-BlessicaNeural"),
}

async def generate_word(word: str, text: str, voice: str):
    target_path = os.path.join(OUTPUT_DIR, f"word_{word}.mp3")
    print(f"Generating: {word} -> {target_path} (voice={voice}, text='{text}')")
    
    # Slight rate adjustment if needed, default is natural clear rate
    communicate = edge_tts.Communicate(text, voice=voice, rate="-4%")
    await communicate.save(target_path)
    
    size = os.path.getsize(target_path)
    print(f"Saved: {target_path} ({size} bytes)")

async def main():
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    
    # Generate all 26 Say It words
    print("=== Generating 26 Say It words with en-US-AnaNeural ===")
    for word in SAYIT_WORDS:
        await generate_word(word, word.capitalize(), "en-US-AnaNeural")
    
    # Generate other placeholder words
    print("\n=== Generating other placeholder words ===")
    for word in OTHER_PLACEHOLDERS:
        await generate_word(word, word.capitalize(), "en-US-AnaNeural")
        
    # Generate Find It words
    print("\n=== Generating Find It words ===")
    for word in FINDIT_WORDS:
        await generate_word(word, word.capitalize(), "en-US-AnaNeural")
        
    # Generate Filipino words
    for word, (text, voice) in SPECIAL_FILIPINO_WORDS.items():
        await generate_word(word, text, voice)

if __name__ == "__main__":
    asyncio.run(main())
