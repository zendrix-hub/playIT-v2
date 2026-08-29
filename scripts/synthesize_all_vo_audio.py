"""
PlayIT Clean English VO Voice-Over Audio Synthesizer
Uses edge-tts with Microsoft Neural Voice: en-US-AnaNeural
Outputs directly to:
  app/src/main/assets/audio/ui/
  app/src/main/assets/audio/vo/
"""

import asyncio
import os
import shutil
import edge_tts

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
UI_AUDIO_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "audio", "ui")
VO_AUDIO_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "audio", "vo")

os.makedirs(UI_AUDIO_DIR, exist_ok=True)
os.makedirs(VO_AUDIO_DIR, exist_ok=True)

VOICE = "en-US-AnaNeural" # Upbeat, warm child-friendly neural voice

VO_SCRIPTS = {
    "vo_welcome_01.mp3": "Hi there! I'm so happy you're here. Let's play and learn together!",
    "vo_encourage_01.mp3": "Good try! Let's listen again.",
    "vo_encourage_02.mp3": "Almost! One more try, you can do it!",
    "vo_encourage_03.mp3": "Let's practice one more time.",
    "vo_correct_01.mp3": "Yes! That's it!",
    "vo_correct_02.mp3": "Perfect! Great job!",
    "vo_hint_01.mp3": "Hmm, let's think about this together.",
    "vo_hint_02.mp3": "Here's a little help!",
    "vo_milestone_01.mp3": "Wow, look at you go!",
    "vo_streak_01.mp3": "You've been practicing every day, amazing!",
    "vo_complete_01.mp3": "You did it! I'm so proud of you!",
    "vo_unlock_01.mp3": "A new letter is ready for you!",
    "vo_blendit_intro_01.mp3": "Let's build some words together!",
    "vo_findit_intro_01.mp3": "Can you find the picture that matches the sound?",
    "vo_sayit_intro_01.mp3": "Now it's your turn. Say the sound into the microphone!",
    "vo_quiet_check_01.mp3": "Let's be as quiet as a mouse before we start listening!",
    "vo_return_welcome_01.mp3": "Welcome back! Ready to keep learning?",
    "vo_noise_alert_01.mp3": "It's a little noisy right now. Let's find a quiet spot!",
    "vo_splash_tagline.mp3": "Play I T. Learn phonics and read with joy!",
    "vo_nameprompt_intro.mp3": "What is your name? Let's choose your friendly animal avatar!",
    "vo_map_tarana.mp3": "Let's go! Tap a letter to begin our adventure!",
    "vo_blendit_complete.mp3": "Awesome! You mastered the word blending challenge!",
    "vo_star_celebration.mp3": "Look at all your stars! Wonderful work!",
    "vo_parent_gate.mp3": "Grown-ups only. Solve the math problem to continue."
}

async def synthesize_all():
    print("=" * 80)
    print("[*] Synthesizing Clean English Mascot VO Voice-Over Suite...")
    print(f"[*] Voice: {VOICE} (Microsoft Neural Audio)")
    print("=" * 80)

    for filename, script in VO_SCRIPTS.items():
        ui_path = os.path.join(UI_AUDIO_DIR, filename)
        vo_path = os.path.join(VO_AUDIO_DIR, filename)

        communicate = edge_tts.Communicate(script, VOICE, rate="-4%", pitch="+2Hz")
        await communicate.save(ui_path)

        # Mirror copy to vo_path
        shutil.copy2(ui_path, vo_path)

        size_kb = os.path.getsize(ui_path) / 1024
        print(f"  [+] Generated: {filename:<28} [{size_kb:>5.1f} KB] -> \"{script}\"")

    print("=" * 80)
    print(f"[*] Successfully synthesized all {len(VO_SCRIPTS)} VO audio clips in 100% clean English!")

if __name__ == "__main__":
    asyncio.run(synthesize_all())
