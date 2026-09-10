"""
PlayIT Neural Audio Upgrade Pipeline
Synthesizes and normalizes natural, child-friendly audio assets using edge-tts and ffmpeg loudnorm.
Adheres strictly to the rule: Exclude all newly created files from recent sessions.
"""

import asyncio
import os
import shutil
import subprocess
import sys
import edge_tts

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "audio")
WORDS_DIR = os.path.join(ASSETS_DIR, "words")
PHONEMES_DIR = os.path.join(ASSETS_DIR, "phonemes")
VO_DIR = os.path.join(ASSETS_DIR, "vo")
UI_DIR = os.path.join(ASSETS_DIR, "ui")
TMP_RAW_DIR = "/tmp/playit_audio_raw"

FFMPEG_BIN = os.path.expanduser("~/.local/bin/ffmpeg")
if not os.path.exists(FFMPEG_BIN):
    FFMPEG_BIN = shutil.which("ffmpeg")

# 71 newly created words from recent sessions - STRICTLY PRESERVED / NEVER OVERWRITTEN
EXCLUDED_RECENT_WORDS = {
    "word_apple.mp3", "word_ball.mp3", "word_box.mp3", "word_cat.mp3", "word_dog.mp3",
    "word_elephant.mp3", "word_fish.mp3", "word_goat.mp3", "word_hat.mp3", "word_insect.mp3",
    "word_jug.mp3", "word_kite.mp3", "word_lion.mp3", "word_mouse.mp3", "word_nest.mp3",
    "word_orange.mp3", "word_pig.mp3", "word_queen.mp3", "word_rabbit.mp3", "word_sun.mp3",
    "word_tiger.mp3", "word_umbrella.mp3", "word_van.mp3", "word_watch.mp3", "word_yoyo.mp3",
    "word_zebra.mp3", "word_bam.mp3", "word_iguana.mp3", "word_lit.mp3", "word_octopus.mp3",
    "word_pencil.mp3", "word_sum.mp3", "word_turtle.mp3", "word_xylophone.mp3",
    "word_ant.mp3", "word_axe.mp3", "word_bano.mp3", "word_duck.mp3", "word_egg.mp3",
    "word_envelope.mp3", "word_gift.mp3", "word_igloo.mp3", "word_ink.mp3", "word_jet.mp3",
    "word_key.mp3", "word_king.mp3", "word_leaf.mp3", "word_map.mp3", "word_net.mp3",
    "word_nino.mp3", "word_nut.mp3", "word_owl.mp3", "word_ox.mp3", "word_pina.mp3",
    "word_quilt.mp3", "word_ring.mp3", "word_rocket.mp3", "word_six.mp3", "word_snake.mp3",
    "word_star.mp3", "word_top.mp3", "word_tree.mp3", "word_uncle.mp3", "word_up.mp3",
    "word_vase.mp3", "word_vest.mp3", "word_wing.mp3", "word_worm.mp3", "word_yak.mp3",
    "word_yarn.mp3", "word_zip.mp3"
}

# Newly created VO line - PRESERVED
EXCLUDED_RECENT_VO = {
    "vo_sayit_word_intro_01.mp3"
}

# 44 Words to Upgrade (Conversational pacing, child-friendly AnaNeural)
WORDS_TO_UPGRADE = {
    "word_aim.mp3": "Aim",
    "word_base.mp3": "Base",
    "word_bat.mp3": "Bat",
    "word_bed.mp3": "Bed",
    "word_bee.mp3": "Bee",
    "word_bib.mp3": "Bib",
    "word_bird.mp3": "Bird",
    "word_boat.mp3": "Boat",
    "word_boy.mp3": "Boy",
    "word_bug.mp3": "Bug",
    "word_bus.mp3": "Bus",
    "word_cake.mp3": "Cake",
    "word_cap.mp3": "Cap",
    "word_cup.mp3": "Cup",
    "word_draw.mp3": "Draw",
    "word_face.mp3": "Face",
    "word_fan.mp3": "Fan",
    "word_fox.mp3": "Fox",
    "word_gap.mp3": "Gap",
    "word_hand.mp3": "Hand",
    "word_hen.mp3": "Hen",
    "word_jam.mp3": "Jam",
    "word_kit.mp3": "Kit",
    "word_lake.mp3": "Lake",
    "word_mat.mp3": "Mat",
    "word_mob.mp3": "Mob",
    "word_mom.mp3": "Mom",
    "word_nap.mp3": "Nap",
    "word_pan.mp3": "Pan",
    "word_pin.mp3": "Pin",
    "word_quiz.mp3": "Quiz",
    "word_road.mp3": "Road",
    "word_sam.mp3": "Sam",
    "word_same.mp3": "Same",
    "word_sea.mp3": "Sea",
    "word_seat.mp3": "Seat",
    "word_sis.mp3": "Sis",
    "word_spin.mp3": "Spin",
    "word_sub.mp3": "Sub",
    "word_tale.mp3": "Tale",
    "word_toy.mp3": "Toy",
    "word_warm.mp3": "Warm",
    "word_web.mp3": "Web",
    "word_zoo.mp3": "Zoo"
}

# 30 Crisp Articulation Phonemes (rate = -10%)
PHONEMES_TO_UPGRADE = {
    "phoneme_m.mp3": ("mmm", "en-US-AnaNeural"),
    "phoneme_s.mp3": ("sss", "en-US-AnaNeural"),
    "phoneme_a.mp3": ("ah", "en-US-AnaNeural"),
    "phoneme_i.mp3": ("ih", "en-US-AnaNeural"),
    "phoneme_o.mp3": ("aw", "en-US-AnaNeural"),
    "phoneme_b.mp3": ("buh", "en-US-AnaNeural"),
    "phoneme_e.mp3": ("eh", "en-US-AnaNeural"),
    "phoneme_u.mp3": ("uh", "en-US-AnaNeural"),
    "phoneme_t.mp3": ("tuh", "en-US-AnaNeural"),
    "phoneme_k.mp3": ("kuh", "en-US-AnaNeural"),
    "phoneme_l.mp3": ("lll", "en-US-AnaNeural"),
    "phoneme_y.mp3": ("yuh", "en-US-AnaNeural"),
    "phoneme_n.mp3": ("nnn", "en-US-AnaNeural"),
    "phoneme_g.mp3": ("guh", "en-US-AnaNeural"),
    "phoneme_ng.mp3": ("ng", "fil-PH-BlessicaNeural"),
    "phoneme_p.mp3": ("puh", "en-US-AnaNeural"),
    "phoneme_r.mp3": ("rrr", "en-US-AnaNeural"),
    "phoneme_d.mp3": ("duh", "en-US-AnaNeural"),
    "phoneme_h.mp3": ("huh", "en-US-AnaNeural"),
    "phoneme_w.mp3": ("wuh", "en-US-AnaNeural"),
    "phoneme_c.mp3": ("kuh", "en-US-AnaNeural"),
    "phoneme_f.mp3": ("fff", "en-US-AnaNeural"),
    "phoneme_j.mp3": ("juh", "en-US-AnaNeural"),
    "phoneme_ñ.mp3": ("nyuh", "fil-PH-BlessicaNeural"),
    "phoneme_enye.mp3": ("nyuh", "fil-PH-BlessicaNeural"),
    "phoneme_q.mp3": ("kwuh", "en-US-AnaNeural"),
    "phoneme_v.mp3": ("vvv", "en-US-AnaNeural"),
    "phoneme_x.mp3": ("ks", "en-US-AnaNeural"),
    "phoneme_z.mp3": ("zzz", "en-US-AnaNeural")
}

# 25 Voice-Over Lines to Upgrade (Warm, natural, child-friendly delivery)
VO_TO_UPGRADE = {
    "vo_hearit_intro_01.mp3": ("Listen closely to the sound of the letter, then tap play.", "en-US-AnaNeural", "+0%"),
    "vo_sayit_intro_01.mp3": ("Now it's your turn! Say the sound into the microphone!", "en-US-AnaNeural", "+0%"),
    "vo_findit_intro_01.mp3": ("Can you find the pictures that match the sound?", "en-US-AnaNeural", "+0%"),
    "vo_blendit_intro_01.mp3": ("Let's build some words together!", "en-US-AnaNeural", "+0%"),
    "vo_welcome_01.mp3": ("Hi there! I'm Lily! Let's play and learn together! Tap your name to start!", "en-US-AnaNeural", "+0%"),
    "vo_return_welcome_01.mp3": ("Welcome back, friend! Ready for our fun reading adventure?", "en-US-AnaNeural", "+0%"),
    "vo_correct_01.mp3": ("Yes! That's it! High five!", "en-US-AnaNeural", "+0%"),
    "vo_correct_02.mp3": ("Woohoo! Perfect! Great job!", "en-US-AnaNeural", "+0%"),
    "vo_encourage_01.mp3": ("Good try! Let's listen again together!", "en-US-AnaNeural", "+0%"),
    "vo_encourage_02.mp3": ("Almost! You can do it, give it one more try!", "en-US-AnaNeural", "+0%"),
    "vo_encourage_03.mp3": ("Let's practice one more time! You've got this!", "en-US-AnaNeural", "+0%"),
    "vo_hint_01.mp3": ("Listen to the beginning sound of the word.", "en-US-AnaNeural", "+0%"),
    "vo_hint_02.mp3": ("Here is a little clue to help you!", "en-US-AnaNeural", "+0%"),
    "vo_milestone_01.mp3": ("Wow! Look at you go! That was awesome!", "en-US-AnaNeural", "+0%"),
    "vo_streak_01.mp3": ("You've been practicing every single day! Amazing superstar!", "en-US-AnaNeural", "+0%"),
    "vo_complete_01.mp3": ("Yay! You did it! I am so proud of you!", "en-US-AnaNeural", "+0%"),
    "vo_unlock_01.mp3": ("A new letter is ready for you!", "en-US-AnaNeural", "+0%"),
    "vo_quiet_check_01.mp3": ("Please find a quiet spot so we can hear your voice clearly.", "en-US-AnaNeural", "+0%"),
    "vo_noise_alert_01.mp3": ("It's a little noisy right now. Let's find a quiet spot to practice!", "en-US-AnaNeural", "+0%"),
    "vo_splash_tagline.mp3": ("Play I T. Learn letter sounds and read with joy!", "en-US-AnaNeural", "+0%"),
    "vo_nameprompt_intro.mp3": ("What is your name? Let's choose your friendly animal buddy!", "en-US-AnaNeural", "+0%"),
    "vo_map_tarana.mp3": ("Tara na! Let's go! Tap a letter to begin our journey!", "en-US-AnaNeural", "+0%"),
    "vo_blendit_complete.mp3": ("Hooray! You mastered the word blending challenge! You can read words now!", "en-US-AnaNeural", "+0%"),
    "vo_star_celebration.mp3": ("Look at all your shiny stars! Wonderful work!", "en-US-AnaNeural", "+0%"),
    "vo_parent_gate.mp3": ("Grown-ups only. Solve the math problem to continue.", "en-US-JennyNeural", "+0%")
}

def normalize_audio(raw_path: str, output_path: str):
    """Normalize audio loudness to -15 LUFS, 24kHz, 48kbps MP3."""
    cmd = [
        FFMPEG_BIN, "-y", "-i", raw_path,
        "-filter:a", "loudnorm=I=-15:LRA=7:TP=-1.5",
        "-c:a", "libmp3lame", "-b:a", "48k", "-ar", "24000",
        output_path
    ]
    res = subprocess.run(cmd, capture_output=True)
    if res.returncode != 0:
        raise RuntimeError(f"FFmpeg normalization failed for {output_path}: {res.stderr.decode()[:300]}")

async def process_item(text: str, voice: str, rate: str, dest_paths: list[str]):
    os.makedirs(TMP_RAW_DIR, exist_ok=True)
    temp_raw = os.path.join(TMP_RAW_DIR, "temp_raw.mp3")
    
    communicate = edge_tts.Communicate(text, voice, rate=rate)
    await communicate.save(temp_raw)
    
    # Normalize to first destination
    first_dest = dest_paths[0]
    normalize_audio(temp_raw, first_dest)
    
    # Copy to any mirror destinations
    for mirror_dest in dest_paths[1:]:
        shutil.copy2(first_dest, mirror_dest)

async def main():
    print("=" * 80)
    print("PlayIT Neural Audio Upgrade Pipeline (Edge-TTS + Loudness Normalization)")
    print(f"FFmpeg binary: {FFMPEG_BIN}")
    print("=" * 80)

    # 1. Phonemes (30 files, rate=-10%)
    print(f"\n[1/3] Generating {len(PHONEMES_TO_UPGRADE)} Crisp Phoneme Sounds (rate=-10%)...")
    for fname, (text, voice) in PHONEMES_TO_UPGRADE.items():
        dest = os.path.join(PHONEMES_DIR, fname)
        await process_item(text, voice, rate="-10%", dest_paths=[dest])
        sz = os.path.getsize(dest)
        print(f"  [+] Phoneme: {fname:<22} [{sz:>6}b] text=\"{text}\" ({voice})")

    # 2. Words (44 files, conversational pacing rate=+0%)
    print(f"\n[2/3] Generating {len(WORDS_TO_UPGRADE)} Natural Vocabulary Words (rate=+0%)...")
    for fname, word in WORDS_TO_UPGRADE.items():
        if fname in EXCLUDED_RECENT_WORDS:
            print(f"  [!] SKIPPED (Recently Created): {fname}")
            continue
        dest = os.path.join(WORDS_DIR, fname)
        await process_item(word, "en-US-AnaNeural", rate="+0%", dest_paths=[dest])
        sz = os.path.getsize(dest)
        print(f"  [+] Word:    {fname:<22} [{sz:>6}b] text=\"{word}\" (en-US-AnaNeural)")

    # 3. Voice-Over Lines (25 files, mirrored to both audio/vo/ and audio/ui/)
    print(f"\n[3/3] Generating {len(VO_TO_UPGRADE)} Natural Mascot & Instruction Voiceovers...")
    for fname, (script, voice, rate) in VO_TO_UPGRADE.items():
        if fname in EXCLUDED_RECENT_VO:
            print(f"  [!] SKIPPED (Recently Created): {fname}")
            continue
        dest_vo = os.path.join(VO_DIR, fname)
        dest_ui = os.path.join(UI_DIR, fname)
        await process_item(script, voice, rate=rate, dest_paths=[dest_vo, dest_ui])
        sz = os.path.getsize(dest_vo)
        print(f"  [+] VO:      {fname:<26} [{sz:>6}b] ({voice}) -> \"{script[:40]}...\"")

    # Clean up temp
    if os.path.exists(TMP_RAW_DIR):
        shutil.rmtree(TMP_RAW_DIR, ignore_errors=True)

    print("\n" + "=" * 80)
    print("ALL TARGETED AUDIO ASSETS SUCCESSFULLY UPGRADED AND NORMALIZED!")
    print("=" * 80)

if __name__ == "__main__":
    asyncio.run(main())
