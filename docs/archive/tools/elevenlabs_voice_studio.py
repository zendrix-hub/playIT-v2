"""
PlayIT Voice Studio — ElevenLabs & Filipina Neural Voice Synthesizer

Features:
1. ElevenLabs API Integration with Filipina Voice Models (e.g. Aimee, Maria, Kath)
2. Microsoft Neural Voice: en-PH-RosaNeural (Natural Filipina English, Zero-Cost)
3. Batch synthesis directly into PlayIT assets (audio/vo and audio/ui)
"""

import os
import sys
import json
import argparse
import asyncio
import urllib.request
import urllib.parse
import edge_tts

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
VO_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "audio", "vo")
UI_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "audio", "ui")

# Pre-curated Filipina Voices in ElevenLabs & Neural TTS
FILIPINA_VOICE_PRESETS = {
    "rosa": {
        "engine": "edge-tts",
        "voice_id": "en-PH-RosaNeural",
        "name": "Rosa (Philippine English Neural)",
        "description": "Warm, clear, and natural Philippine English teacher tone. Zero API cost."
    },
    "blessica": {
        "engine": "edge-tts",
        "voice_id": "fil-PH-BlessicaNeural",
        "name": "Blessica (Tagalog/Filipino Neural)",
        "description": "Friendly Tagalog female voice for bilingual announcements."
    },
    "elevenlabs_aimee": {
        "engine": "elevenlabs",
        "voice_id": "EXAVITQu4vr4xnSDxMaL", # Example standard friendly voice ID or custom library ID
        "name": "Aimee (ElevenLabs Warm Storybook)",
        "description": "Soft, expressive, playful storybook persona."
    },
    "elevenlabs_custom": {
        "engine": "elevenlabs",
        "voice_id": "CUSTOM_VOICE_ID",
        "name": "Custom ElevenLabs Filipina Voice",
        "description": "Custom cloned or voice-library Filipina voice ID."
    }
}

VO_SAMPLE_LINES = {
    "vo_welcome_01.mp3": "Hi there! I'm so happy you're here. Let's play and learn together!",
    "vo_encourage_01.mp3": "Good try! Let's listen again.",
    "vo_complete_01.mp3": "You did it! I'm so proud of you!",
    "vo_map_tarana.mp3": "Let's go! Tap a letter to begin our adventure!",
    "vo_blendit_complete.mp3": "Awesome! You mastered the word blending challenge!"
}

async def synthesize_edge_tts(text: str, voice: str, output_path: str):
    """Synthesizes speech using Microsoft Neural TTS (e.g. en-PH-RosaNeural)."""
    communicate = edge_tts.Communicate(text, voice, rate="-3%", pitch="+2Hz")
    await communicate.save(output_path)
    print(f"  [+] Synthesized via Edge-TTS ({voice}) -> {output_path}")

def synthesize_elevenlabs(text: str, voice_id: str, api_key: str, output_path: str):
    """Synthesizes speech using the ElevenLabs Text-to-Speech API."""
    if not api_key:
        print("[!] Error: ElevenLabs API Key is required. Set ELEVENLABS_API_KEY env var or pass --api-key.")
        return False

    url = f"https://api.elevenlabs.io/v1/text-to-speech/{voice_id}"
    headers = {
        "Accept": "audio/mpeg",
        "Content-Type": "application/json",
        "xi-api-key": api_key
    }
    payload = {
        "text": text,
        "model_id": "eleven_multilingual_v2",
        "voice_settings": {
            "stability": 0.55,
            "similarity_boost": 0.75,
            "style": 0.20,
            "use_speaker_boost": True
        }
    }
    
    req = urllib.request.Request(url, data=json.dumps(payload).encode('utf-8'), headers=headers, method="POST")
    try:
        with urllib.request.urlopen(req, timeout=30) as response:
            with open(output_path, "wb") as f:
                f.write(response.read())
        print(f"  [+] Synthesized via ElevenLabs ({voice_id}) -> {output_path}")
        return True
    except urllib.error.HTTPError as e:
        print(f"[!] ElevenLabs API HTTP Error: {e.code} - {e.read().decode('utf-8')}")
        return False
    except Exception as e:
        print(f"[!] ElevenLabs API request failed: {e}")
        return False

def main():
    parser = argparse.ArgumentParser(description="PlayIT Voice Studio — ElevenLabs & Filipina Neural Voice Synthesizer")
    parser.add_argument("--preset", type=str, default="rosa", choices=list(FILIPINA_VOICE_PRESETS.keys()), help="Voice preset")
    parser.add_argument("--text", type=str, default=None, help="Custom text string to synthesize")
    parser.add_argument("--out", type=str, default=None, help="Output MP3 file path")
    parser.add_argument("--voice-id", type=str, default=None, help="Override ElevenLabs Voice ID")
    parser.add_argument("--api-key", type=str, default=os.getenv("ELEVENLABS_API_KEY", ""), help="ElevenLabs API Key")
    parser.add_argument("--batch-all", action="store_true", help="Batch synthesize all sample voice-over lines")
    args = parser.parse_args()

    print("=" * 80)
    print("[*] PlayIT Voice Studio — Filipina Neural & ElevenLabs Voice Pipeline")
    print("=" * 80)

    preset_info = FILIPINA_VOICE_PRESETS[args.preset]
    print(f"[*] Selected Preset: {preset_info['name']}")
    print(f"[*] Engine:          {preset_info['engine'].upper()}")
    print(f"[*] Description:     {preset_info['description']}")
    print("=" * 80)

    if args.batch_all:
        print("[*] Batch synthesizing mascot voice-overs...")
        for filename, script in VO_SAMPLE_LINES.items():
            vo_out = os.path.join(VO_DIR, filename)
            ui_out = os.path.join(UI_DIR, filename)
            if preset_info["engine"] == "edge-tts":
                asyncio.run(synthesize_edge_tts(script, preset_info["voice_id"], vo_out))
            else:
                vid = args.voice_id or preset_info["voice_id"]
                synthesize_elevenlabs(script, vid, args.api_key, vo_out)
            
            # Copy to UI audio folder
            if os.path.exists(vo_out):
                import shutil
                shutil.copy2(vo_out, ui_out)
        print("[*] Batch synthesis complete!")
        return

    text = args.text or "Hello! I am Lily the Tarsier. Let's learn to read together!"
    output_path = args.out or os.path.join(BASE_DIR, "tools", "preview_voice.mp3")

    if preset_info["engine"] == "edge-tts":
        asyncio.run(synthesize_edge_tts(text, preset_info["voice_id"], output_path))
    else:
        vid = args.voice_id or preset_info["voice_id"]
        synthesize_elevenlabs(text, vid, args.api_key, output_path)

    if os.path.exists(output_path):
        size_kb = os.path.getsize(output_path) / 1024
        print(f"[+] Output ready: {output_path} ({size_kb:.1f} KB)")
    print("=" * 80)

if __name__ == "__main__":
    main()
