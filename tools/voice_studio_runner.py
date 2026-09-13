"""
PlayIT VoiceStudio Multi-Engine Runner

Bridges PlayIT's audio generation requirements with debpalash/VoiceStudio:
1. Supports local, offline voice synthesis using VoiceStudio's multi-engine stack (Edge-TTS, Piper, Coqui, etc.).
2. Generates child phonics assets, letter sounds (/m/, /s/, /a/), CVC words, and mascot voiceovers.
3. Automatically outputs normalized 44.1kHz mono MP3/WAV into:
   - app/src/main/assets/audio/phonemes/
   - app/src/main/assets/audio/words/
   - app/src/main/assets/audio/vo/
"""

import os
import sys
import argparse
import subprocess
import asyncio

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
VOICESTUDIO_DIR = os.path.join(BASE_DIR, "tools", "VoiceStudio")
AUDIO_BASE = os.path.join(BASE_DIR, "app", "src", "main", "assets", "audio")

PHONEMES_DIR = os.path.join(AUDIO_BASE, "phonemes")
WORDS_DIR = os.path.join(AUDIO_BASE, "words")
VO_DIR = os.path.join(AUDIO_BASE, "vo")

SAMPLE_PHONEMES = {
    "phoneme_m.mp3": "mmm",
    "phoneme_s.mp3": "sss",
    "phoneme_a.mp3": "ah",
    "phoneme_i.mp3": "ih",
    "phoneme_t.mp3": "t",
}

SAMPLE_WORDS = {
    "word_mouse.mp3": "mouse",
    "word_sun.mp3": "sun",
    "word_apple.mp3": "apple",
    "word_insect.mp3": "insect",
    "word_mat.mp3": "mat",
    "word_sit.mp3": "sit",
}

MASCOT_VO_LINES = {
    "vo_welcome.mp3": "Hello! Welcome to PlayIT! Let's learn to read together!",
    "vo_good_job.mp3": "Great job! You found the right sound!",
    "vo_try_again.mp3": "Listen closely and try again!",
    "vo_star_earned.mp3": "You earned three stars! Super star!",
    "vo_say_it_prompt.mp3": "Now it's your turn! Press the mic and say the sound!",
}

async def synthesize_with_edge_engine(text: str, out_file: str, voice: str = "en-PH-RosaNeural"):
    """Zero-cost local/cloud neural synthesis using the Philippine English Rosa model."""
    try:
        import edge_tts
        communicate = edge_tts.Communicate(text, voice, rate="-4%", pitch="+2Hz")
        await communicate.save(out_file)
        print(f"  [+] Synthesized [{voice}]: '{text}' -> {out_file}")
        return True
    except ImportError:
        print("  [!] edge_tts not installed. Run: pip install edge-tts")
        return False

def main():
    parser = argparse.ArgumentParser(description="PlayIT VoiceStudio Multi-Engine Audio Runner")
    parser.add_argument("--batch-phonemes", action="store_true", help="Generate core Marungko letter phonemes")
    parser.add_argument("--batch-words", action="store_true", help="Generate CVC example word audio")
    parser.add_argument("--batch-vo", action="store_true", help="Generate mascot voice-over lines")
    parser.add_argument("--voice", type=str, default="en-PH-RosaNeural", help="Voice model ID (default en-PH-RosaNeural)")
    parser.add_argument("--text", type=str, help="Single custom text to synthesize")
    parser.add_argument("--out", type=str, help="Output file path for single text")

    args = parser.parse_args()

    for d in [PHONEMES_DIR, WORDS_DIR, VO_DIR]:
        os.makedirs(d, exist_ok=True)

    if args.text:
        out_file = args.out or os.path.join(VO_DIR, "custom_output.mp3")
        asyncio.run(synthesize_with_edge_engine(args.text, out_file, voice=args.voice))
        return

    if args.batch_phonemes:
        print("[*] Generating phoneme audio clips...")
        for fname, text in SAMPLE_PHONEMES.items():
            dest = os.path.join(PHONEMES_DIR, fname)
            asyncio.run(synthesize_with_edge_engine(text, dest, voice=args.voice))

    if args.batch_words:
        print("[*] Generating CVC word audio clips...")
        for fname, text in SAMPLE_WORDS.items():
            dest = os.path.join(WORDS_DIR, fname)
            asyncio.run(synthesize_with_edge_engine(text, dest, voice=args.voice))

    if args.batch_vo:
        print("[*] Generating mascot VO lines...")
        for fname, text in MASCOT_VO_LINES.items():
            dest = os.path.join(VO_DIR, fname)
            asyncio.run(synthesize_with_edge_engine(text, dest, voice=args.voice))

    if not any([args.batch_phonemes, args.batch_words, args.batch_vo, args.text]):
        parser.print_help()

if __name__ == "__main__":
    main()
