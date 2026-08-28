# ElevenLabs Audio Generation Draft — playIT

Source specs: `18_AUDIO_PRODUCTION_GUIDE.md`, `19_AUDIO_SCRIPTS.md`,
`33_AUDIO_EFFECTIVENESS_RESEARCH.md` (read that one first — it explains
*why* this version differs from the original draft).

Status: DRAFT — for test generation, not yet production-approved audio.

---

## 0. Excluded — do NOT generate

Unchanged from the original draft. Structurally excluded from this batch.

| Filename | Reason |
|---|---|
| `phoneme_ng.mp3` | No script exists — pending SME review per `01_REQUIREMENTS_SUMMARY.md §5` |
| `phoneme_ñ.mp3` | No script exists — pending SME review per `01_REQUIREMENTS_SUMMARY.md §5` |
| `phoneme_x.mp3` | Draft script exists but is explicitly flagged "pending SME confirmation" — breaks the word-initial pattern used by every other letter |

---

## 1. Voice & Model Setup (do once, reuse across all files)

**Voice B — Mascot VO: Lily** (soft, cute, sweet).
**Model: `eleven_v3`.** Needed specifically for inline audio tags (§4) —
v3 is the model that treats bracketed cues as performance direction. Pick
Lily's voice from ElevenLabs' curated "best voices for v3" list rather
than an arbitrary voice — v3's tag responsiveness varies significantly by
voice, and an untested voice may not respond to tags the way the script
below assumes.

**Voice A — Phonemes + Words.** A separate, plainer voice from Lily —
neutral/reference-grade, "never sing-song or exaggerated — clarity over
performance," clear unhurried articulation for a Grade-1 ELL audience.
**Model: `eleven_flash_v2`.** This is not optional — pronunciation
dictionary phoneme tags (§3) only work on `eleven_flash_v2` and
`eleven_v3`; any other model silently ignores the dictionary and falls
back to default pronunciation with no error. v2 is also the more
predictable/consistent choice for neutral narration, which is what this
voice needs more than expressiveness. Test Lily against a flatter/calmer
voice on `phoneme_m.mp3` before locking Voice A — do not default to Lily
here without a listening check.

- Lock stability medium-high for consistency across all files in a given
  voice's batch.
- Speed: slightly slower than natural conversational pace, both voices.
- Do **not** let the tool auto-select or vary voice per call — same voice
  ID for every file in a category.

Post-processing (not done by ElevenLabs, do after generation):
- Loudness normalize every file to **-16 LUFS** (e.g. `ffmpeg -i in.wav -af loudnorm=I=-16:TP=-1.5:LRA=11 out.mp3`)
- Confirm output format matches `18`: MP3, 128–192kbps CBR, 44.1kHz, mono

---

## 2. Critical rule for every table below

**The "TTS Input" column is the only text that gets pasted into the
generation call.** The "Note" column is for you and the QC listener —
never paste it into the prompt. The original draft mixed these together
(`/b/ (quick, no "uh")... B, like Ball.` as one string), which risks the
model trying to voice the parenthetical itself. Keep them in separate
columns, always.

---

## 3. Phoneme Scripts (25 files)

Target duration 2.0–3.5s each. Emotion: warm/neutral/clear — no audio
tags on this voice, they'd fight the neutral/reference-grade requirement.

**Revised target vs. the original draft:** continuants stay pure/sustained
(unchanged, they were already correct). Stops, glides, the affricate, and
the blend now target a **light, quick release — not zero release**. A
fully "pure" isolated stop consonant is both acoustically unusual (a stop
needs some release burst) and, per `33 §3`'s classroom research, harder
for beginning readers to reproduce than a lightly-released one. The
failure mode to avoid is a *prolonged, sung* "uh" that turns the sound
into its own syllable — not the release itself.

Every row below needs a pronunciation dictionary entry (CMU Arpabet — more
predictable than IPA per ElevenLabs' own guidance) mapping the grapheme in
the TTS Input column to the listed phoneme code. Set these up once in an
ElevenLabs pronunciation dictionary attached to Voice A, not per-call.

| Filename | TTS Input (paste this only) | Dictionary grapheme → CMU phoneme | Type | Note (do not paste) |
|---|---|---|---|---|
| `phoneme_m.mp3` | "Mmm... M, like Mouse." | mmm → M | continuant | Sustain cleanly, already correct pattern |
| `phoneme_s.mp3` | "Sss... S, like Sun." | sss → S | continuant | Sustain cleanly |
| `phoneme_a.mp3` | "A... A, like Apple." | a → AE | short vowel | Keep brief and closed, don't elongate |
| `phoneme_i.mp3` | "I... I, like Insect." | i → IH | short vowel | Keep brief and closed |
| `phoneme_o.mp3` | "O... O, like Orange." | o → AA | short vowel | AA vs AO is dialect-dependent for this vowel — verify against the chosen voice's default accent before locking |
| `phoneme_b.mp3` | "B... B, like Ball." | b → B | stop | Light quick release only, never a held "buh" |
| `phoneme_e.mp3` | "E... E, like Elephant." | e → EH | short vowel | Keep brief and closed |
| `phoneme_u.mp3` | "U... U, like Umbrella." | u → AH | short vowel | Keep brief and closed |
| `phoneme_t.mp3` | "T... T, like Tiger." | t → T | stop | Light quick release |
| `phoneme_k.mp3` | "K... K, like Kite." | k → K | stop | Light quick release |
| `phoneme_l.mp3` | "Lll... L, like Lion." | lll → L | continuant | Sustain cleanly |
| `phoneme_y.mp3` | "Y... Y, like Yoyo." | y → Y | glide | Light quick release |
| `phoneme_n.mp3` | "Nnn... N, like Nest." | nnn → N | continuant | Sustain cleanly |
| `phoneme_g.mp3` | "G... G, like Goat." | g → G | stop | Light quick release, hard G |
| `phoneme_p.mp3` | "P... P, like Pig." | p → P | stop | Light quick release |
| `phoneme_r.mp3` | "Rrr... R, like Rabbit." | rrr → R | continuant | Sustain cleanly |
| `phoneme_d.mp3` | "D... D, like Dog." | d → D | stop | Light quick release |
| `phoneme_h.mp3` | "H... H, like Hat." | h → HH | aspirate | Breathy, light |
| `phoneme_w.mp3` | "W... W, like Watch." | w → W | glide | Light quick release |
| `phoneme_c.mp3` | "K... C, like Cat." | (reuse the `k → K` entry above) | stop | Same phoneme as K — hard C only |
| `phoneme_f.mp3` | "Fff... F, like Fish." | fff → F | continuant | Sustain cleanly |
| `phoneme_j.mp3` | "J... J, like Jug." | j → JH | affricate | Light quick release |
| `phoneme_q.mp3` | "Kw... Q, like Queen." | kw → K W | blend | Light quick release, no trailing schwa on either sound |
| `phoneme_v.mp3` | "Vvv... V, like Van." | vvv → V | continuant | Sustain cleanly |
| `phoneme_z.mp3` | "Zzz... Z, like Zebra." | zzz → Z | continuant | Sustain cleanly |

Generate **2–3 takes per file**, not one — ElevenLabs' own guidance is
that identical input can still produce variable output, so pick the
cleanest take rather than accepting the first one by default. Then run
the existing QC listening pass (`18 §5`) on the selected take, checking
specifically: did a schwa creep back in beyond a light release? Did the
short vowel come out short, or long?

---

## 4. Word Scripts (35 files)

Unchanged in substance — no schwa risk on whole words, TTS handles these
reliably. Target duration 1.0–1.5s each. Same Voice A / `eleven_flash_v2`
setup, single word, no carrier phrase, no audio tags. Filenames and word
list as in the original draft (`word_sam.mp3`, `word_bus.mp3`, etc.) —
confirm the exact 35-word final count against `19_AUDIO_SCRIPTS.md §3`
before locking (the original draft flagged a 33-vs-35 discrepancy that's
still unresolved).

---

## 5. Mascot VO Scripts (18 files)

Target duration 1.5–3.0s each. Voice B, `eleven_v3`. **Add an inline audio
tag to every line** — this is the actual mechanism for emotional
performance; the Emotion column alone was never read by the model as an
instruction. Tags go inside the TTS input text itself, in brackets.

| Filename | TTS Input (with audio tag) |
|---|---|
| `vo_welcome_01.mp3` | "[excitedly] Hi there! I'm so happy you're here. Let's play and learn together!" |
| `vo_encourage_01.mp3` | "[warmly] Good try! Let's listen again." |
| `vo_encourage_02.mp3` | "[gently encouraging] Almost! One more try — you can do it." |
| `vo_encourage_03.mp3` | "[warmly] Let's practice one more time." |
| `vo_correct_01.mp3` | "[happily] Yes! That's it!" |
| `vo_correct_02.mp3` | "[happily] Perfect! Great job!" |
| `vo_hint_01.mp3` | "[thoughtfully] Hmm, let's think about this together." |
| `vo_hint_02.mp3` | "[cheerfully] Here's a little help!" |
| `vo_milestone_01.mp3` | "[excitedly] Wow, look at you go!" |
| `vo_streak_01.mp3` | "[excitedly] You've been practicing every day — amazing!" |
| `vo_complete_01.mp3` | "[joyfully] You did it! I'm so proud of you!" |
| `vo_unlock_01.mp3` | "[excitedly] A new letter is ready for you!" |
| `vo_blendit_intro_01.mp3` | "[warmly] Let's build some words together!" |
| `vo_findit_intro_01.mp3` | "[cheerfully] Can you find the pictures that match the sound?" |
| `vo_sayit_intro_01.mp3` | "[encouragingly] Now it's your turn — say the sound into the microphone!" |
| `vo_quiet_check_01.mp3` | "[playfully, softly] Let's be as quiet as a mouse before we start listening!" |
| `vo_return_welcome_01.mp3` | "[happily] Welcome back! Ready to keep learning?" |
| `vo_noise_alert_01.mp3` | "[gently] It's a little noisy right now — let's find a quiet spot!" |

⚠️ `vo_return_welcome_01`: must never reference a broken streak or lost
progress, even if one occurred — unchanged from the original draft.

Test tags on 2–3 lines first before running the full batch — tag
responsiveness varies by voice (`33 §4`), so confirm Lily's chosen voice
actually performs these before committing to all 18.

---

## 6. Not covered here — separate track

- **SFX** (`sfx_correct_chime.mp3`, etc.) — non-verbal sound design, not
  TTS. Use ElevenLabs sound-effects generation or a stock library
  separately.
- **QC pass** — every generated file needs a second-listener review for
  mispronunciation/clipping/noise before it's approved into the pipeline
  (`18 §5`). TTS output does not skip this step.
- **Pitch accuracy check** on phonemes (±10 cents) — verify with a
  tuner/spectrum pass before final approval.
