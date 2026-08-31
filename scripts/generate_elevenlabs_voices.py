"""
PlayIT 2-Voice Filipino Voice-Over Synthesis Pipeline
Supports ElevenLabs API + Philippine Neural Voices (en-PH-RosaNeural & fil-PH-BlessicaNeural/AngeloNeural).

Voice Architecture:
1. Voice 1 (Child / Mascot Lily): Upbeat, cheerful Filipino child accent.
   - Used for peer encouragement, celebrations, streak rewards, mascot tap lines.
2. Voice 2 (Female Teacher / Narrator): Warm, articulate DepEd-standard Filipino English.
   - Used for lesson instructions, pure phonic sound modeling (28 Marungko phonemes), 33 blend words, and parent gate.

Usage:
  python scripts/generate_elevenlabs_voices.py [--api-key ELEVENLABS_API_KEY]
"""

import os
import sys
import argparse
import asyncio
import json
import shutil
import urllib.request
import edge_tts

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "audio")
UI_DIR = os.path.join(ASSETS_DIR, "ui")
VO_DIR = os.path.join(ASSETS_DIR, "vo")
PHONEMES_DIR = os.path.join(ASSETS_DIR, "phonemes")
WORDS_DIR = os.path.join(ASSETS_DIR, "words")

for d in [UI_DIR, VO_DIR, PHONEMES_DIR, WORDS_DIR]:
    os.makedirs(d, exist_ok=True)

# ═══════════════════════════════════════════════════════════════════════════
# ═══════════════════════════════════════════════════════════════════════════
# VOICE 1: LILY THE TARSIER (Child Peer Voice - High Energy, Joyful, Warm)
# ═══════════════════════════════════════════════════════════════════════════
CHILD_VO_SCRIPTS = {
    "vo_welcome_01.mp3": "Hi there! I'm Lily! Let's play and learn together! Tap your name to start!",
    "vo_return_welcome_01.mp3": "Welcome back, friend! Ready for our fun reading adventure?",
    "vo_correct_01.mp3": "YES! That's it! High five!",
    "vo_correct_02.mp3": "Woohoo! Perfect! You are so smart!",
    "vo_encourage_01.mp3": "Ooh, good try! Let's listen again together!",
    "vo_encourage_02.mp3": "Almost! You can do it, give it one more try!",
    "vo_encourage_03.mp3": "Let's practice one more time! You've got this!",
    "vo_complete_01.mp3": "YAAAY! You did it! I am so super proud of you!",
    "vo_milestone_01.mp3": "WOAH! Look at you go! That was awesome!",
    "vo_streak_01.mp3": "You've been practicing every single day! Amazing superstar!",
    "vo_star_celebration.mp3": "Look at all your shiny stars! Woohoo! Wonderful work!",
    "vo_blendit_complete.mp3": "Hooray! You mastered the word blending challenge! You can read words now!",
    "vo_map_tarana.mp3": "Let's go, adventurer! Tap a letter to begin our journey!",
    "vo_splash_tagline.mp3": "Play I T. Learn letter sounds and read with joy!",
    "vo_nameprompt_intro.mp3": "What is your name? Let's pick your cute animal buddy!"
}

# ═══════════════════════════════════════════════════════════════════════════
# VOICE 2: TEACHER / EDUCATIONAL GUIDE (Enthusiastic, Warm Early Grade Educator)
# ═══════════════════════════════════════════════════════════════════════════
TEACHER_VO_SCRIPTS = {
    "vo_hearit_intro_01.mp3": "Listen closely to the sound of the letter, then tap play to hear it again!",
    "vo_sayit_intro_01.mp3": "Now it's your turn! Say the sound clearly into the microphone!",
    "vo_findit_intro_01.mp3": "Can you find all three pictures that start with this sound?",
    "vo_blendit_intro_01.mp3": "Let's blend letter sounds together to build words!",
    "vo_hint_01.mp3": "Listen to the beginning sound of the word.",
    "vo_hint_02.mp3": "Here is a little clue to help you!",
    "vo_quiet_check_01.mp3": "Please find a quiet spot so we can hear your voice clearly.",
    "vo_noise_alert_01.mp3": "It's a little noisy right now. Let's find a quiet spot to practice!",
    "vo_parent_gate.mp3": "Grown-ups only. Solve the math problem to continue.",
    "vo_unlock_01.mp3": "A new letter lesson is unlocked for you!"
}

# 28 Crystal-Clear Phonics Sounds with Multi-Sensory Action & Anchor Context (Grade 1 Jolly/Marungko Pedagogy)
PURE_PHONEMES = {
    "phoneme_m.mp3": "Mmm! Yummy! /m/... Mouse!",
    "phoneme_s.mp3": "Sssss! Like a snake! /s/... Sun!",
    "phoneme_a.mp3": "Ah! Open wide! /ah/... Apple!",
    "phoneme_i.mp3": "Ih! Wiggle like an insect! /i/... Insect!",
    "phoneme_o.mp3": "Oh! Round like an orange! /o/... Orange!",
    "phoneme_b.mp3": "Buh! Bounce the ball! /b/... Ball!",
    "phoneme_e.mp3": "Eh! Big like an elephant! /eh/... Elephant!",
    "phoneme_u.mp3": "Uh! Up goes the umbrella! /uh/... Umbrella!",
    "phoneme_t.mp3": "Tuh-tuh! Tap your toes! /t/... Tiger!",
    "phoneme_k.mp3": "Kuh! Fly the kite! /k/... Kite!",
    "phoneme_l.mp3": "Lll! Lick a lollipop! /l/... Lion!",
    "phoneme_y.mp3": "Yuh-yuh! Spin the yoyo! /y/... Yoyo!",
    "phoneme_n.mp3": "Nnn! High in the nest! /n/... Nest!",
    "phoneme_g.mp3": "Guh! Gulp like a goat! /g/... Goat!",
    "phoneme_ng.mp3": "Ng! Ring the bell! /ng/... Ring!",
    "phoneme_p.mp3": "Puh! Puff the popcorn! /p/... Pig!",
    "phoneme_r.mp3": "Rrr! Roar like a rabbit! /r/... Rabbit!",
    "phoneme_d.mp3": "Duh! Beat the drum! /d/... Dog!",
    "phoneme_h.mp3": "Huh! Hop in a hat! /h/... Hat!",
    "phoneme_w.mp3": "Wuh! Wind the watch! /w/... Watch!",
    "phoneme_c.mp3": "Kuh! Quick like a cat! /k/... Cat!",
    "phoneme_f.mp3": "Fff! Swim like a fish! /f/... Fish!",
    "phoneme_j.mp3": "Juh! Jump for the jug! /j/... Jug!",
    "phoneme_ñ.mp3": "Nyuh! Play with the niño! /ny/... Niño!",
    "phoneme_enye.mp3": "Nyuh! Play with the niño! /ny/... Niño!",
    "phoneme_q.mp3": "Kwuh! Bow to the queen! /kw/... Queen!",
    "phoneme_v.mp3": "Vvv! Drive the van! /v/... Van!",
    "phoneme_x.mp3": "Ks! Pack in the box! /ks/... Box!",
    "phoneme_z.mp3": "Zzz! Zoom like a zebra! /z/... Zebra!"
}

# 33 Blend-It Target Words (Voice 2: Teacher)
ALL_BLEND_WORDS = {
    "word_sam.mp3": "Sam",
    "word_sis.mp3": "Sis",
    "word_aim.mp3": "Aim",
    "word_bus.mp3": "Bus",
    "word_sub.mp3": "Sub",
    "word_mom.mp3": "Mom",
    "word_bee.mp3": "Bee",
    "word_bib.mp3": "Bib",
    "word_bat.mp3": "Bat",
    "word_mat.mp3": "Mat",
    "word_kit.mp3": "Kit",
    "word_toy.mp3": "Toy",
    "word_boy.mp3": "Boy",
    "word_pig.mp3": "Pig",
    "word_pan.mp3": "Pan",
    "word_bug.mp3": "Bug",
    "word_pin.mp3": "Pin",
    "word_nap.mp3": "Nap",
    "word_dog.mp3": "Dog",
    "word_hat.mp3": "Hat",
    "word_hen.mp3": "Hen",
    "word_bed.mp3": "Bed",
    "word_hand.mp3": "Hand",
    "word_cat.mp3": "Cat",
    "word_fan.mp3": "Fan",
    "word_cap.mp3": "Cap",
    "word_cup.mp3": "Cup",
    "word_jam.mp3": "Jam",
    "word_van.mp3": "Van",
    "word_box.mp3": "Box",
    "word_fox.mp3": "Fox",
    "word_zoo.mp3": "Zoo",
    "word_web.mp3": "Web"
}

async def synthesize_edge_tts_expressive(text: str, voice_name: str, output_path: str, style: str = "cheerful", rate: str = "+0%", pitch: str = "+0Hz", style_degree: str = "2"):
    """
    Synthesizes speech with Microsoft Neural SSML express-as emotional styles.
    """
    ssml = f"""<speak version="1.0" xmlns="http://www.w3.org/2001/10/synthesis" xmlns:mstts="https://www.w3.org/2001/mstts" xml:lang="en-US">
  <voice name="{voice_name}">
    <mstts:express-as style="{style}" styledegree="{style_degree}">
      <prosody pitch="{pitch}" rate="{rate}">
        {text}
      </prosody>
    </mstts:express-as>
  </voice>
</speak>"""
    try:
        communicate = edge_tts.Communicate(ssml, voice_name)
        await communicate.save(output_path)
    except Exception:
        communicate = edge_tts.Communicate(text, voice_name, rate=rate, pitch=pitch)
        await communicate.save(output_path)

def synthesize_elevenlabs_voice(text: str, voice_id: str, api_key: str, output_path: str, stability: float = 0.65, similarity: float = 0.75):
    url = f"https://api.elevenlabs.io/v1/text-to-speech/{voice_id}"
    headers = {
        "Accept": "audio/mpeg",
        "Content-Type": "application/json",
        "xi-api-key": api_key
    }
    data = {
        "text": text,
        "model_id": "eleven_multilingual_v2",
        "voice_settings": {
            "stability": stability,
            "similarity_boost": similarity,
            "style": 0.35,
            "use_speaker_boost": True
        }
    }
    req = urllib.request.Request(url, data=json.dumps(data).encode("utf-8"), headers=headers)
    with urllib.request.urlopen(req) as response:
        with open(output_path, "wb") as f:
            f.write(response.read())

async def run_pipeline(elevenlabs_api_key: str = None, elevenlabs_child_id: str = None, elevenlabs_teacher_id: str = None):
    print("=" * 80)
    print("[*] PlayIT 2-Voice Expressive Pedagogical Voice-Over Generator")
    if elevenlabs_api_key and elevenlabs_child_id and elevenlabs_teacher_id:
        print("[*] Mode: ElevenLabs API (Eleven Multilingual v2 Expressive)")
        print(f"[*] Voice 1 (Child Mascot Lily): {elevenlabs_child_id}")
        print(f"[*] Voice 2 (Adult Teacher):     {elevenlabs_teacher_id}")
    else:
        print("[*] Mode: Microsoft Neural SSML Expressive Pedagogical Engine")
        print("[*] Voice 1 (Child Mascot Lily): en-US-AnaNeural (Youthful Child Prosody +10% Pitch)")
        print("[*] Voice 2 (Adult Teacher):     en-US-JennyNeural (Expressive Cheerful Teacher Style)")
    print("=" * 80)

    # 1. Synthesize Voice 1 (Child Mascot Lily - High Energy & Playful)
    print("\n--- [1/4] Synthesizing Voice 1: Lily the Child Mascot (Cheers & Greetings) ---")
    for filename, script in CHILD_VO_SCRIPTS.items():
        out_ui = os.path.join(UI_DIR, filename)
        out_vo = os.path.join(VO_DIR, filename)
        
        if elevenlabs_api_key and elevenlabs_child_id:
            try:
                synthesize_elevenlabs_voice(script, elevenlabs_child_id, elevenlabs_api_key, out_ui, stability=0.45, similarity=0.75)
            except Exception as e:
                print(f"  [!] ElevenLabs error for {filename}: {e}, falling back to Expressive Child Neural Voice...")
                await synthesize_edge_tts_expressive(script, "en-US-AnaNeural", out_ui, style="cheerful", rate="+5%", pitch="+10%")
        else:
            await synthesize_edge_tts_expressive(script, "en-US-AnaNeural", out_ui, style="cheerful", rate="+5%", pitch="+10%")
        
        shutil.copy2(out_ui, out_vo)
        print(f"  [+] Voice 1 (Child Mascot Lily): {filename:<26} -> \"{script}\"")

    # 2. Synthesize Voice 2 (Adult Teacher / Educational Guide - Lesson Intros & Prompts)
    print("\n--- [2/4] Synthesizing Voice 2: Teacher / Phonics Guide (Lesson Intros & Prompts) ---")
    for filename, script in TEACHER_VO_SCRIPTS.items():
        out_ui = os.path.join(UI_DIR, filename)
        out_vo = os.path.join(VO_DIR, filename)
        
        if elevenlabs_api_key and elevenlabs_teacher_id:
            try:
                synthesize_elevenlabs_voice(script, elevenlabs_teacher_id, elevenlabs_api_key, out_ui, stability=0.65, similarity=0.75)
            except Exception as e:
                print(f"  [!] ElevenLabs error for {filename}: {e}, falling back to Expressive Teacher Neural Voice...")
                await synthesize_edge_tts_expressive(script, "en-US-JennyNeural", out_ui, style="cheerful", rate="-2%", pitch="+4%")
        else:
            await synthesize_edge_tts_expressive(script, "en-US-JennyNeural", out_ui, style="cheerful", rate="-2%", pitch="+4%")
        
        shutil.copy2(out_ui, out_vo)
        print(f"  [+] Voice 2 (Adult Teacher):    {filename:<26} -> \"{script}\"")

    # 3. Synthesize Voice 2: 28 Crystal-Clear Phonics Sounds (High-Clarity Multi-Sensory Action & Context)
    print("\n--- [3/4] Synthesizing Voice 2: 28 Pure Phonics Sounds (Adult Teacher - Multi-Sensory) ---")
    for filename, sound in PURE_PHONEMES.items():
        out_path = os.path.join(PHONEMES_DIR, filename)
        if elevenlabs_api_key and elevenlabs_teacher_id:
            try:
                synthesize_elevenlabs_voice(sound, elevenlabs_teacher_id, elevenlabs_api_key, out_path, stability=0.75, similarity=0.80)
            except Exception as e:
                await synthesize_edge_tts_expressive(sound, "en-US-JennyNeural", out_path, style="cheerful", rate="-3%", pitch="+3%")
        else:
            await synthesize_edge_tts_expressive(sound, "en-US-JennyNeural", out_path, style="cheerful", rate="-3%", pitch="+3%")
        print(f"  [+] Pure Phonic (Teacher):      {filename:<26} -> \"{sound}\"")

    # 4. Synthesize Voice 2: 33 Blend-It Words (Crystal-Clear Articulation)
    print("\n--- [4/4] Synthesizing Voice 2: 33 Blend-It Target Words (Adult Teacher) ---")
    for filename, word in ALL_BLEND_WORDS.items():
        out_path = os.path.join(WORDS_DIR, filename)
        if elevenlabs_api_key and elevenlabs_teacher_id:
            try:
                synthesize_elevenlabs_voice(word, elevenlabs_teacher_id, elevenlabs_api_key, out_path, stability=0.70, similarity=0.75)
            except Exception as e:
                await synthesize_edge_tts_expressive(word, "en-US-JennyNeural", out_path, style="friendly", rate="-2%", pitch="+2%")
        else:
            await synthesize_edge_tts_expressive(word, "en-US-JennyNeural", out_path, style="friendly", rate="-2%", pitch="+2%")
        print(f"  [+] Blend Word (Teacher):       {filename:<26} -> \"{word}\"")

    print("\n" + "=" * 80)
    print(f"[*] SUCCESS: Synthesized 2-Voice Suite ({len(CHILD_VO_SCRIPTS) + len(TEACHER_VO_SCRIPTS) + len(PURE_PHONEMES) + len(ALL_BLEND_WORDS)} total audio assets)!")
    print("=" * 80)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="PlayIT 2-Voice Filipino Voice-Over Generator")
    parser.add_argument("--api-key", type=str, default=os.getenv("ELEVENLABS_API_KEY"), help="ElevenLabs API Key")
    parser.add_argument("--child-id", type=str, default=os.getenv("ELEVENLABS_CHILD_ID"), help="ElevenLabs Voice ID for Child Mascot")
    parser.add_argument("--teacher-id", type=str, default=os.getenv("ELEVENLABS_TEACHER_ID"), help="ElevenLabs Voice ID for Female Teacher")
    args = parser.parse_args()

    asyncio.run(run_pipeline(args.api_key, args.child_id, args.teacher_id))
