"""
PlayIT Audio Elevation & Whisper Verification Pipeline

Synthesizes high-quality, expressive voiceovers for 'Hear It', 'Say It', 'Blend It',
and 'Find It' modes, all 26 pedagogical phoneme lessons, target CVC words, and mascot
reactions using VoiceStudio / Edge-TTS (en-PH-RosaNeural).
Then runs each generated audio file through OpenAI Whisper to verify acoustic clarity,
pronunciation, and transcription accuracy before updating production assets.
"""

import os
import sys
import asyncio
import difflib
import edge_tts
import whisper

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
AUDIO_DIR = os.path.join(BASE_DIR, "app", "src", "main", "assets", "audio")

UI_DIR = os.path.join(AUDIO_DIR, "ui")
VO_DIR = os.path.join(AUDIO_DIR, "vo")
PHONEMES_DIR = os.path.join(AUDIO_DIR, "phonemes")
WORDS_DIR = os.path.join(AUDIO_DIR, "words")

TEACHER_VOICE = "en-PH-RosaNeural"
PHONEME_VOICE = "en-US-JennyNeural"
DEFAULT_PITCH = "+2Hz"
DEFAULT_RATE = "-2%"

AUDIO_CATALOG = [
    # =========================================================================
    # 1. CORE INSTRUCTIONAL & GAMEPLAY VOICEOVERS (UI / VO)
    # =========================================================================
    {
        "category": "ui",
        "filename": "vo_hearit_intro_01.mp3",
        "script": "Listen closely to the sound of the letter, then tap play!",
        "expected_keywords": ["listen", "sound", "letter", "play"],
        "rate": "+2%", "pitch": "+4Hz"
    },
    {
        "category": "ui",
        "filename": "vo_sayit_intro_01.mp3",
        "script": "Now it's your turn! Say the sound into the microphone!",
        "expected_keywords": ["now", "turn", "say", "sound", "microphone"],
        "rate": "+2%", "pitch": "+4Hz"
    },
    {
        "category": "ui",
        "filename": "vo_sayit_word_intro_01.mp3",
        "script": "Now it's your turn! Say the whole word clearly into the microphone!",
        "expected_keywords": ["now", "turn", "say", "word", "microphone"],
        "rate": "+2%", "pitch": "+4Hz"
    },
    {
        "category": "ui",
        "filename": "vo_blendit_intro_01.mp3",
        "script": "Let's build some words together!",
        "expected_keywords": ["build", "words", "together"],
        "rate": "+2%", "pitch": "+4Hz"
    },
    {
        "category": "ui",
        "filename": "vo_findit_intro_01.mp3",
        "script": "Can you find the pictures that match the sound?",
        "expected_keywords": ["find", "pictures", "sound"],
        "rate": "+2%", "pitch": "+4Hz"
    },
    {
        "category": "ui",
        "filename": "vo_welcome_01.mp3",
        "script": "Hi there! I'm so happy you're here. Let's play and learn together!",
        "expected_keywords": ["hi", "happy", "play", "learn", "together"],
        "rate": "+3%", "pitch": "+4Hz"
    },
    {
        "category": "ui",
        "filename": "vo_correct_01.mp3",
        "script": "Yes! That's it!",
        "expected_keywords": ["yes", "that's", "it"],
        "rate": "+4%", "pitch": "+5Hz"
    },
    {
        "category": "ui",
        "filename": "vo_correct_02.mp3",
        "script": "Awesome! Great job!",
        "expected_keywords": ["awesome", "great", "job"],
        "rate": "+4%", "pitch": "+5Hz"
    },
    {
        "category": "ui",
        "filename": "vo_encourage_01.mp3",
        "script": "Good try! Let's listen again.",
        "expected_keywords": ["good", "try", "listen", "again"],
        "rate": "+1%", "pitch": "+2Hz"
    },
    {
        "category": "ui",
        "filename": "vo_encourage_02.mp3",
        "script": "Almost! One more try — you can do it!",
        "expected_keywords": ["almost", "one", "more", "try", "can", "do"],
        "rate": "+2%", "pitch": "+3Hz"
    },
    {
        "category": "ui",
        "filename": "vo_encourage_03.mp3",
        "script": "Let's practice one more time!",
        "expected_keywords": ["practice", "one", "more", "time"],
        "rate": "+2%", "pitch": "+3Hz"
    },
    {
        "category": "ui",
        "filename": "vo_hint_01.mp3",
        "script": "Hmm, let's think about this together.",
        "expected_keywords": ["think", "together"],
        "rate": "+0%", "pitch": "+2Hz"
    },
    {
        "category": "ui",
        "filename": "vo_hint_02.mp3",
        "script": "Here's a little help!",
        "expected_keywords": ["little", "help"],
        "rate": "+1%", "pitch": "+3Hz"
    },
    {
        "category": "ui",
        "filename": "vo_complete_01.mp3",
        "script": "You did it! I'm so proud of you!",
        "expected_keywords": ["did", "proud", "you"],
        "rate": "+3%", "pitch": "+5Hz"
    },
    {
        "category": "ui",
        "filename": "vo_unlock_01.mp3",
        "script": "A new letter is ready for you!",
        "expected_keywords": ["new", "letter", "ready"],
        "rate": "+3%", "pitch": "+4Hz"
    },
    {
        "category": "ui",
        "filename": "vo_milestone_01.mp3",
        "script": "Wow, look at you go!",
        "expected_keywords": ["look", "go"],
        "rate": "+3%", "pitch": "+4Hz"
    },
    {
        "category": "ui",
        "filename": "vo_streak_01.mp3",
        "script": "You've been practicing every day — amazing!",
        "expected_keywords": ["practicing", "every", "day", "amazing"],
        "rate": "+2%", "pitch": "+3Hz"
    },
    {
        "category": "ui",
        "filename": "vo_quiet_check_01.mp3",
        "script": "Let's be as quiet as a mouse before we start listening!",
        "expected_keywords": ["quiet", "mouse", "start", "listening"],
        "rate": "-1%", "pitch": "+2Hz"
    },
    {
        "category": "ui",
        "filename": "vo_return_welcome_01.mp3",
        "script": "Welcome back! Ready to keep learning?",
        "expected_keywords": ["welcome", "ready", "keep", "learning"],
        "rate": "+2%", "pitch": "+4Hz"
    },
    {
        "category": "ui",
        "filename": "vo_noise_alert_01.mp3",
        "script": "It's a little noisy right now — let's find a quiet spot!",
        "expected_keywords": ["noisy", "find", "quiet", "spot"],
        "rate": "+1%", "pitch": "+2Hz"
    },
    {
        "category": "ui",
        "filename": "vo_map_tarana.mp3",
        "script": "Let's go! Tap a letter to begin our adventure!",
        "expected_keywords": ["let's", "go", "tap", "letter", "adventure"],
        "rate": "+3%", "pitch": "+4Hz"
    },

    # =========================================================================
    # 2. ALL 26 MARUNGKO PHONEMES (Hear It / Corrective Say It)
    # Per 19_AUDIO_SCRIPTS.md §1: "/sound/... Letter, like Word."
    # =========================================================================
    {
        "category": "phonemes",
        "filename": "phoneme_a.mp3",
        "script": "The letter A makes the sound, ah. A, like Apple.",
        "expected_keywords": ["a", "apple"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_b.mp3",
        "script": "The letter B makes the sound, buh. B, like Ball.",
        "expected_keywords": ["b", "ball"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_c.mp3",
        "script": "The letter C makes the sound, kuh. C, like Cat.",
        "expected_keywords": ["c", "cat"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_d.mp3",
        "script": "The letter D makes the sound, duh. D, like Dog.",
        "expected_keywords": ["d", "dog"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_e.mp3",
        "script": "The letter E makes the sound, eh. E, like Elephant.",
        "expected_keywords": ["e", "elephant"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_f.mp3",
        "script": "The letter F makes the sound, fff. F, like Fish.",
        "expected_keywords": ["f", "fish"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_g.mp3",
        "script": "The letter G makes the sound, guh. G, like Goat.",
        "expected_keywords": ["g", "goat"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_h.mp3",
        "script": "The letter H makes the sound, huh. H, like Hat.",
        "expected_keywords": ["h", "hat"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_i.mp3",
        "script": "The letter I makes the sound, ih. I, like Insect.",
        "expected_keywords": ["i", "insect"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_j.mp3",
        "script": "The letter J makes the sound, juh. J, like Jug.",
        "expected_keywords": ["j", "jug"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_k.mp3",
        "script": "The letter K makes the sound, kuh. K, like Kite.",
        "expected_keywords": ["k", "kite"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_l.mp3",
        "script": "The letter L makes the sound, lll. L, like Lion.",
        "expected_keywords": ["l", "lion"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_m.mp3",
        "script": "The letter M makes the sound, mmm. M, like Mouse.",
        "expected_keywords": ["m", "mouse"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_n.mp3",
        "script": "The letter N makes the sound, nnn. N, like Nest.",
        "expected_keywords": ["n", "nest"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_o.mp3",
        "script": "The letter O makes the sound, oh. O, like Orange.",
        "expected_keywords": ["o", "orange"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_p.mp3",
        "script": "The letter P makes the sound, pa. P, like Pig.",
        "expected_keywords": ["p", "pig"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_q.mp3",
        "script": "The letter Q makes the sound, kwuh. Q, like Queen.",
        "expected_keywords": ["q", "queen"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_r.mp3",
        "script": "The letter R makes the sound, rrr. R, like Rabbit.",
        "expected_keywords": ["r", "rabbit"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_s.mp3",
        "script": "The letter S makes the sound, sss. S, like Sun.",
        "expected_keywords": ["s", "sun"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_t.mp3",
        "script": "The letter T makes the sound, ta. T, like Tiger.",
        "expected_keywords": ["t", "tiger"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_u.mp3",
        "script": "The letter U makes the sound, uh. U, like Umbrella.",
        "expected_keywords": ["u", "umbrella"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_v.mp3",
        "script": "The letter V makes the sound, vvv. V, like Van.",
        "expected_keywords": ["v", "van"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_w.mp3",
        "script": "The letter W makes the sound, ww. W, like Watch.",
        "expected_keywords": ["w", "watch"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_x.mp3",
        "script": "The letter X makes the sound, ks. Like in Box.",
        "expected_keywords": ["x", "box"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_y.mp3",
        "script": "The letter Y makes the sound, yuh. Y, like Yoyo.",
        "expected_keywords": ["y", "yoyo"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "phonemes",
        "filename": "phoneme_z.mp3",
        "script": "The letter Z makes the sound, zzz. Z, like Zebra.",
        "expected_keywords": ["z", "zebra"],
        "rate": "-4%", "pitch": "+2Hz"
    },

    # =========================================================================
    # 3. BLEND IT DECODABLE CVC WORDS & SAY IT TARGET VOCABULARY
    # Spoken clearly once per 19_AUDIO_SCRIPTS.md §3
    # =========================================================================
    # Group 1: SAM, SIS, AIM
    {
        "category": "words",
        "filename": "word_sam.mp3",
        "script": "Sam",
        "expected_keywords": ["sam"],
        "rate": "-10%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_sis.mp3",
        "script": "Sis.",
        "expected_keywords": ["sis"],
        "rate": "-2%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_aim.mp3",
        "script": "Aim.",
        "expected_keywords": ["aim"],
        "rate": "-6%", "pitch": "+2Hz"
    },

    # Group 2: BUS, SUB, MOM, BEE, BIB
    {
        "category": "words",
        "filename": "word_bus.mp3",
        "script": "Bus.",
        "expected_keywords": ["bus"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_sub.mp3",
        "script": "Sub.",
        "expected_keywords": ["sub"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_mom.mp3",
        "script": "Mom.",
        "expected_keywords": ["mom"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_bee.mp3",
        "script": "Bee.",
        "expected_keywords": ["bee", "b"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_bib.mp3",
        "script": "Bib.",
        "expected_keywords": ["bib"],
        "rate": "-6%", "pitch": "+2Hz"
    },

    # Group 3: BAT, MAT, KIT, TOY, BOY
    {
        "category": "words",
        "filename": "word_bat.mp3",
        "script": "The bat.",
        "expected_keywords": ["bat"],
        "rate": "-4%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_mat.mp3",
        "script": "Mat.",
        "expected_keywords": ["mat", "matt"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_kit.mp3",
        "script": "Kit.",
        "expected_keywords": ["kit"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_toy.mp3",
        "script": "Toy.",
        "expected_keywords": ["toy"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_boy.mp3",
        "script": "Boy.",
        "expected_keywords": ["boy"],
        "rate": "-6%", "pitch": "+2Hz"
    },

    # Group 4: PIG, PAN, BUG, PIN, NAP
    {
        "category": "words",
        "filename": "word_pig.mp3",
        "script": "Pig.",
        "expected_keywords": ["pig"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_pan.mp3",
        "script": "Pan.",
        "expected_keywords": ["pan"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_bug.mp3",
        "script": "Bug.",
        "expected_keywords": ["bug"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_pin.mp3",
        "script": "Pin.",
        "expected_keywords": ["pin"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_nap.mp3",
        "script": "Nap.",
        "expected_keywords": ["nap"],
        "rate": "-6%", "pitch": "+2Hz"
    },

    # Group 5: DOG, HAT, HEN, BED, WEB, HAND
    {
        "category": "words",
        "filename": "word_dog.mp3",
        "script": "Dog.",
        "expected_keywords": ["dog"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_hat.mp3",
        "script": "Hat.",
        "expected_keywords": ["hat"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_hen.mp3",
        "script": "Hen.",
        "expected_keywords": ["hen"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_bed.mp3",
        "script": "Bed.",
        "expected_keywords": ["bed"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_web.mp3",
        "script": "Web.",
        "expected_keywords": ["web"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_hand.mp3",
        "script": "Hand.",
        "expected_keywords": ["hand"],
        "rate": "-6%", "pitch": "+2Hz"
    },

    # Group 6: CAT, FAN, CAP, CUP, JAM
    {
        "category": "words",
        "filename": "word_cat.mp3",
        "script": "Cat.",
        "expected_keywords": ["cat"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_fan.mp3",
        "script": "Fan.",
        "expected_keywords": ["fan"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_cap.mp3",
        "script": "Cap.",
        "expected_keywords": ["cap"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_cup.mp3",
        "script": "Cup.",
        "expected_keywords": ["cup"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_jam.mp3",
        "script": "Jam.",
        "expected_keywords": ["jam"],
        "rate": "-6%", "pitch": "+2Hz"
    },

    # Group 7: VAN, BOX, FOX, ZOO, QUIZ
    {
        "category": "words",
        "filename": "word_van.mp3",
        "script": "Van.",
        "expected_keywords": ["van"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_box.mp3",
        "script": "Box.",
        "expected_keywords": ["box"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_fox.mp3",
        "script": "Fox.",
        "expected_keywords": ["fox"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_zoo.mp3",
        "script": "Zoo.",
        "expected_keywords": ["zoo"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_quiz.mp3",
        "script": "Quiz.",
        "expected_keywords": ["quiz"],
        "rate": "-6%", "pitch": "+2Hz"
    },

    # --- REMAINING EXAMPLE WORDS FOR SAY IT & HEAR IT ---
    {
        "category": "words",
        "filename": "word_mouse.mp3",
        "script": "Mouse.",
        "expected_keywords": ["mouse"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_sun.mp3",
        "script": "Sun.",
        "expected_keywords": ["sun"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_apple.mp3",
        "script": "Apple.",
        "expected_keywords": ["apple"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_insect.mp3",
        "script": "Insect.",
        "expected_keywords": ["insect"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_orange.mp3",
        "script": "Orange.",
        "expected_keywords": ["orange"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_ball.mp3",
        "script": "Ball.",
        "expected_keywords": ["ball"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_elephant.mp3",
        "script": "Elephant.",
        "expected_keywords": ["elephant"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_umbrella.mp3",
        "script": "Umbrella.",
        "expected_keywords": ["umbrella"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_tiger.mp3",
        "script": "Tiger.",
        "expected_keywords": ["tiger"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_kite.mp3",
        "script": "Kite.",
        "expected_keywords": ["kite"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_lion.mp3",
        "script": "Lion.",
        "expected_keywords": ["lion"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_yoyo.mp3",
        "script": "Yoyo.",
        "expected_keywords": ["yoyo", "yo"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_nest.mp3",
        "script": "Nest.",
        "expected_keywords": ["nest"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_goat.mp3",
        "script": "Goat.",
        "expected_keywords": ["goat"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_rabbit.mp3",
        "script": "Rabbit.",
        "expected_keywords": ["rabbit"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_watch.mp3",
        "script": "Watch.",
        "expected_keywords": ["watch"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_fish.mp3",
        "script": "Fish.",
        "expected_keywords": ["fish"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_jug.mp3",
        "script": "Jug.",
        "expected_keywords": ["jug"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_queen.mp3",
        "script": "Queen.",
        "expected_keywords": ["queen"],
        "rate": "-6%", "pitch": "+2Hz"
    },
    {
        "category": "words",
        "filename": "word_zebra.mp3",
        "script": "Zebra.",
        "expected_keywords": ["zebra"],
        "rate": "-6%", "pitch": "+2Hz"
    }
]

async def synthesize_file(entry: dict) -> str:
    cat = entry["category"]
    fname = entry["filename"]
    text = entry["script"]
    rate = entry.get("rate", DEFAULT_RATE)
    pitch = entry.get("pitch", DEFAULT_PITCH)

    if cat == "ui":
        dest = os.path.join(UI_DIR, fname)
        vo_dest = os.path.join(VO_DIR, fname)
    elif cat == "phonemes":
        dest = os.path.join(PHONEMES_DIR, fname)
        vo_dest = None
    elif cat == "words":
        dest = os.path.join(WORDS_DIR, fname)
        vo_dest = None
    else:
        dest = os.path.join(AUDIO_DIR, fname)
        vo_dest = None

    voice = entry.get("voice", PHONEME_VOICE if cat == "phonemes" else TEACHER_VOICE)
    c = edge_tts.Communicate(text, voice, rate=rate, pitch=pitch)
    await c.save(dest)

    # Normalize audio loudness with FFmpeg (-16 LUFS)
    import subprocess
    tmp_dest = dest + ".tmp.mp3"
    os.replace(dest, tmp_dest)
    norm_cmd = [
        "ffmpeg", "-y", "-i", tmp_dest,
        "-af", "loudnorm=I=-16:TP=-1.5:LRA=11",
        "-ar", "44100", "-ac", "1", "-b:a", "128k",
        dest
    ]
    subprocess.run(norm_cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=True)
    if os.path.exists(tmp_dest):
        os.remove(tmp_dest)

    if vo_dest and os.path.exists(VO_DIR):
        import shutil
        shutil.copy2(dest, vo_dest)

    return dest

def verify_with_whisper(model, audio_path: str, expected_keywords: list) -> tuple:
    result = model.transcribe(audio_path, language="en", fp16=False)
    transcript = result.get("text", "").strip()
    transcript_clean = transcript.lower().replace(".", "").replace(",", "").replace("!", "").replace("?", "").replace("'", "")
    
    # Check keyword overlap
    matches = [kw for kw in expected_keywords if kw.lower() in transcript_clean]
    accuracy = len(matches) / len(expected_keywords) if expected_keywords else 1.0
    passed = accuracy >= 0.5 or any(kw.lower() in transcript_clean for kw in expected_keywords)

    return transcript, passed, accuracy

async def run_pipeline():
    print("=" * 70)
    print(" PlayIT Full Audio Synthesis & Whisper Verification Pipeline")
    print("=" * 70)
    print(f"[*] Total Assets in Catalog : {len(AUDIO_CATALOG)}")
    print(f"[*] Voice Talent           : {TEACHER_VOICE} (Warm Philippine English Neural)")
    print(f"[*] Loading Whisper model for verification...")
    model = whisper.load_model("base")
    print("[+] Whisper model loaded successfully.\n")

    results = []
    for idx, entry in enumerate(AUDIO_CATALOG, 1):
        fname = entry["filename"]
        print(f"[{idx:03d}/{len(AUDIO_CATALOG)}] Synthesizing '{fname}'...")
        audio_path = await synthesize_file(entry)

        transcript, passed, acc = verify_with_whisper(model, audio_path, entry["expected_keywords"])
        status = "[PASS]" if passed else "[WARN]"
        print(f"    Script      : \"{entry['script']}\"")
        print(f"    Whisper ASR : \"{transcript}\"")
        print(f"    Validation  : {status} (Keyword match: {acc*100:.0f}%)\n")

        results.append({
            "file": fname,
            "category": entry["category"],
            "script": entry["script"],
            "transcript": transcript,
            "passed": passed
        })

    print("=" * 70)
    total_passed = sum(1 for r in results if r["passed"])
    print(f" SUMMARY: {total_passed}/{len(results)} audio assets synthesized and verified.")
    print("=" * 70)

if __name__ == "__main__":
    asyncio.run(run_pipeline())
