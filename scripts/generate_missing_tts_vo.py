"""
PlayIT Text-To-Speech (TTS) Voice Synthesis Pipeline
Generates warm, crystal-clear neural voice-over audio for screens and prompts that lack audio.
Uses edge-tts (Microsoft Neural Voices) for natural, high-definition pediatric audio.
"""

import os
import asyncio
import edge_tts

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TARGET_VO_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "audio", "vo")
os.makedirs(TARGET_VO_DIR, exist_ok=True)

# Voice configuration: Warm, cheerful, pediatric-friendly neural voice
VOICE_NAME = "en-US-AnaNeural"      # Child/Friendly tone (or en-US-JennyNeural)
RATE = "+0%"
PITCH = "+2Hz"

CLIPS_TO_GENERATE = {
    "vo_splash_tagline": "Learn to read with Lily!",
    "vo_nameprompt_intro": "What's your name? Type your name and pick an animal friend!",
    "vo_map_tarana": "Tara na! Let's go!",
    "vo_blendit_complete": "Awesome word builder! You blended the sounds!",
    "vo_star_celebration": "Great job! You mastered this letter sound!",
    "vo_parent_gate": "Parent gate: Please solve the math question to continue."
}

async def generate_clip(filename: str, text: str):
    output_path = os.path.join(TARGET_VO_DIR, f"{filename}.mp3")
    print(f"[*] Synthesizing: {filename}.mp3 -> \"{text}\"")
    communicate = edge_tts.Communicate(text, VOICE_NAME, rate=RATE, pitch=PITCH)
    await communicate.save(output_path)
    print(f"  [+] Saved: {output_path} ({os.path.getsize(output_path)} bytes)")

async def main():
    print(f"[*] Starting TTS Voice Synthesis Pipeline using voice: {VOICE_NAME}")
    for filename, text in CLIPS_TO_GENERATE.items():
        await generate_clip(filename, text)
    print("[*] All voice-over audio clips successfully synthesized!")

if __name__ == "__main__":
    asyncio.run(main())
