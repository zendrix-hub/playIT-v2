# ElevenLabs Audio Generation Draft — playIT

Source specs: `18_AUDIO_PRODUCTION_GUIDE.md`, `19_AUDIO_SCRIPTS.md`
Status: DRAFT — for test generation, not yet production-approved audio.

---

## 0. Excluded — do NOT generate

These are structurally excluded from this batch. Do not include them in any batch job, script loop, or export.

| Filename | Reason |
|---|---|
| `phoneme_ng.mp3` | No script exists — pending SME review per `01_REQUIREMENTS_SUMMARY.md §5` |
| `phoneme_ñ.mp3` | No script exists — pending SME review per `01_REQUIREMENTS_SUMMARY.md §5` |
| `phoneme_x.mp3` | Draft script exists but is explicitly flagged "pending SME confirmation" — breaks the word-initial pattern used by every other letter |

If you're scripting this later, treat these three as a hard skip-list, not something to remember manually.

---

## 1. Voice Setup (do once, reuse across all files)

- **Voice B — Mascot VO: Lily** (soft, cute, sweet). Fits the mascot register the doc calls for — warmer, more personality than the phoneme/word voice. Lock once, reuse across all 18 lines.
- **Voice A — Phonemes + Words: TBD, recommend a separate, plainer voice from Lily.** The production guide requires this voice to be neutral/reference-grade, "never sing-song or exaggerated — clarity over performance," with clear unhurried articulation for a Grade-1 ELL audience. Lily's cute/soft profile risks softening consonant onsets (`/t/`, `/k/`, `/p/`) and drifting sing-song — exactly what's ruled out for this role. Test Lily against a flatter/calmer voice on `phoneme_m.mp3` before locking Voice A; do not default to Lily here without a listening check.
- Lock stability medium-high for consistency across all files in a given voice's batch.
- Speed: slightly slower than natural conversational pace, for both voices.
- Do **not** let the tool auto-select or vary voice per call — same voice ID for every file in a category.

Post-processing (not done by ElevenLabs, do after generation):
- Loudness normalize every file to **-16 LUFS** (e.g. `ffmpeg -i in.wav -af loudnorm=I=-16:TP=-1.5:LRA=11 out.mp3`)
- Confirm output format matches `18`: MP3, 128–192kbps CBR, 44.1kHz, mono

---

## 2. Phoneme Scripts (25 files) — REVISED

Target duration 2.0–3.5s each. Emotion: warm/neutral/clear.

**Two corrections applied vs. the original `19_AUDIO_SCRIPTS.md` table:**
1. **Vowels** — tripled-letter notation (`/aaa/`) reads as a long, pure vowel, which is also the vowel quality Filipino speakers default to when producing English. Replaced with a short/clipped cue plus explicit direction, so the target short-vowel sound (matching the example word) isn't lost to spelling or L1 transfer.
2. **Stop consonants + glides** — dropped the added "-uh" schwa (`buh`→`b`) per standard phonics practice, since a schwa'd consonant doesn't blend cleanly back into words later in the curriculum. Continuous sounds (`mmm, sss, lll, nnn, rrr, fff, vvv, zzz`) were already correct and are unchanged.

Voice direction note (give this to the voice talent / include as a system instruction for TTS): *"For consonants, produce a quick, clipped sound with no trailing 'uh.' For vowels marked short, keep the sound brief and closed — do not stretch or round it into a long vowel."*

| Filename | Script | Direction |
|---|---|---|
| `phoneme_m.mp3` | /mmm/... M, like Mouse. | continuous — unchanged |
| `phoneme_s.mp3` | /sss/... S, like Sun. | continuous — unchanged |
| `phoneme_a.mp3` | /ă/ (short, clipped)... A, like Apple. | short vowel — do not elongate |
| `phoneme_i.mp3` | /ĭ/ (short, clipped)... I, like Insect. | short vowel — do not elongate |
| `phoneme_o.mp3` | /ŏ/ (short, clipped)... O, like Orange. | short vowel — do not elongate |
| `phoneme_b.mp3` | /b/ (quick, no "uh")... B, like Ball. | stop — clip, no schwa |
| `phoneme_e.mp3` | /ĕ/ (short, clipped)... E, like Elephant. | short vowel — do not elongate |
| `phoneme_u.mp3` | /ŭ/ (short, clipped)... U, like Umbrella. | short vowel — do not elongate |
| `phoneme_t.mp3` | /t/ (quick, no "uh")... T, like Tiger. | stop — clip, no schwa |
| `phoneme_k.mp3` | /k/ (quick, no "uh")... K, like Kite. | stop — clip, no schwa |
| `phoneme_l.mp3` | /lll/... L, like Lion. | continuous — unchanged |
| `phoneme_y.mp3` | /y/ (quick, no "uh")... Y, like Yoyo. | glide — clip, no schwa |
| `phoneme_n.mp3` | /nnn/... N, like Nest. | continuous — unchanged |
| `phoneme_g.mp3` | /g/ (quick, no "uh")... G, like Goat. | stop — clip, no schwa |
| `phoneme_p.mp3` | /p/ (quick, no "uh")... P, like Pig. | stop — clip, no schwa |
| `phoneme_r.mp3` | /rrr/... R, like Rabbit. | continuous — unchanged |
| `phoneme_d.mp3` | /d/ (quick, no "uh")... D, like Dog. | stop — clip, no schwa |
| `phoneme_h.mp3` | /h/ (light, no "uh")... H, like Hat. | breathy — clip, no schwa |
| `phoneme_w.mp3` | /w/ (quick, no "uh")... W, like Watch. | glide — clip, no schwa |
| `phoneme_c.mp3` | /k/ (quick, no "uh")... C, like Cat. | stop — clip, no schwa |
| `phoneme_f.mp3` | /fff/... F, like Fish. | continuous — unchanged |
| `phoneme_j.mp3` | /j/ (quick, no "uh")... J, like Jug. | stop — clip, no schwa |
| `phoneme_q.mp3` | /kw/ (quick, no "uh")... Q, like Queen. | blend — clip, no trailing schwa |
| `phoneme_v.mp3` | /vvv/... V, like Van. | continuous — unchanged |
| `phoneme_z.mp3` | /zzz/... Z, like Zebra. | continuous — unchanged |

⚠️ TTS risk: these are direction *notes*, not guaranteed pronunciation — TTS engines don't reliably interpret "(quick, no uh)" as a phonetic instruction the way a trained voice actor would. **Every phoneme file needs the QC listening pass** (already required per `18` §5) with specific attention to: did the schwa creep back in? Did the vowel come out short as intended, or long? If ElevenLabs doesn't produce clean results after a few attempts, budget for manual trimming (clip the trailing "-uh" in post) or a real voice talent pass for this table specifically — it's the highest-stakes content in the app.

---

## 3. Word Scripts (35 files)

Target duration 1.0–1.5s each. Single word, no carrier phrase. Same voice as phonemes.

| Filename | Word |
|---|---|
| `word_sam.mp3` | Sam |
| `word_sis.mp3` | Sis |
| `word_aim.mp3` | Aim |
| `word_bus.mp3` | Bus |
| `word_sea.mp3` | Sea |
| `word_mob.mp3` | Mob |
| `word_base.mp3` | Base |
| `word_same.mp3` | Same |
| `word_kite.mp3` | Kite |
| `word_lake.mp3` | Lake |
| `word_seat.mp3` | Seat |
| `word_boat.mp3` | Boat |
| `word_tale.mp3` | Tale |
| `word_pig.mp3` | Pig |
| `word_pan.mp3` | Pan |
| `word_gap.mp3` | Gap |
| `word_spin.mp3` | Spin |
| `word_nap.mp3` | Nap |
| `word_bird.mp3` | Bird |
| `word_hand.mp3` | Hand |
| `word_warm.mp3` | Warm |
| `word_road.mp3` | Road |
| `word_draw.mp3` | Draw |
| `word_face.mp3` | Face |
| `word_cake.mp3` | Cake |
| `word_fish.mp3` | Fish |
| `word_cat.mp3` | Cat |
| `word_fan.mp3` | Fan |
| `word_zoo.mp3` | Zoo |
| `word_van.mp3` | Van |
| `word_box.mp3` | Box |
| `word_quiz.mp3` | Quiz |
| `word_fox.mp3` | Fox |

*(Note: source table lists 34 named words for groups 1–7's "5 each except group 1" pattern, actually totaling 33 draft words — confirm final count against `19_AUDIO_SCRIPTS.md` §3 if you need an exact 35.)*

---

## 4. Mascot VO Scripts (18 files)

Target duration 1.5–3.0s each. Natural, upbeat, unhurried pace. Voice B.

| Filename | Script | Emotion |
|---|---|---|
| `vo_welcome_01.mp3` | Hi there! I'm so happy you're here. Let's play and learn together! | Excited |
| `vo_encourage_01.mp3` | Good try! Let's listen again. | Encouraging |
| `vo_encourage_02.mp3` | Almost! One more try — you can do it. | Encouraging |
| `vo_encourage_03.mp3` | Let's practice one more time. | Encouraging |
| `vo_correct_01.mp3` | Yes! That's it! | Happy |
| `vo_correct_02.mp3` | Perfect! Great job! | Happy |
| `vo_hint_01.mp3` | Hmm, let's think about this together. | Thinking |
| `vo_hint_02.mp3` | Here's a little help! | Thinking |
| `vo_milestone_01.mp3` | Wow, look at you go! | Excited |
| `vo_streak_01.mp3` | You've been practicing every day — amazing! | Excited |
| `vo_complete_01.mp3` | You did it! I'm so proud of you! | Celebrating |
| `vo_unlock_01.mp3` | A new letter is ready for you! | Excited |
| `vo_blendit_intro_01.mp3` | Let's build some words together! | Encouraging |
| `vo_findit_intro_01.mp3` | Can you find the pictures that match the sound? | Encouraging |
| `vo_sayit_intro_01.mp3` | Now it's your turn — say the sound into the microphone! | Encouraging |
| `vo_quiet_check_01.mp3` | Let's be as quiet as a mouse before we start listening! | Encouraging |
| `vo_return_welcome_01.mp3` | Welcome back! Ready to keep learning? | Happy |
| `vo_noise_alert_01.mp3` | It's a little noisy right now — let's find a quiet spot! | Encouraging |

⚠️ `vo_return_welcome_01`: must never reference a broken streak or lost progress, even if one occurred. Do not generate alternate variants that mention streak resets.

---

## 5. Not covered here — separate track

- **SFX** (`sfx_correct_chime.mp3`, `sfx_incorrect_pop.mp3`, etc.) — non-verbal sound design, not TTS. Use ElevenLabs sound-effects generation or a stock library separately.
- **QC pass** — every generated file needs a second-listener review for mispronunciation/clipping/noise before it's approved into the pipeline (per `18` §5). TTS output does not skip this step.
- **Pitch accuracy check** on phonemes (±10 cents) — verify with a tuner/spectrum pass before final approval.
