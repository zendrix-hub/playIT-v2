# 18 — Audio Production Guide

## 1. Folder Structure (mirrors `07_FOLDER_STRUCTURE.md`)

```
app/src/main/assets/audio/
├── phonemes/       # 28 letter-sound models ("Hear It" + reused for Say It corrective playback)
├── words/           # 35 Blend It word recordings
├── ui/              # SFX + mascot voice-over lines
└── vosk-model-small-en-us/   # bundled offline recognition model
```

## 2. Naming Convention (full rules in `22_FILE_NAMING_CONVENTION.md`)
- Phonemes: `phoneme_<letter>.mp3` (e.g., `phoneme_m.mp3`, `phoneme_ng.mp3`).
- Words: `word_<lowercase_word>.mp3` (e.g., `word_sam.mp3`).
- UI/SFX: `sfx_<event>.mp3` (e.g., `sfx_correct_chime.mp3`).
- Mascot lines: `vo_<context>_<index>.mp3` (e.g., `vo_encourage_01.mp3`).

## 3. Format & Technical Standards

| Property | Spec |
|---|---|
| Format | MP3, 128–192kbps CBR (balance of quality vs. the APK size budget flagged in `01_REQUIREMENTS_SUMMARY.md §2`) |
| Sample rate | 44.1kHz for playback assets (phonemes/words/UI/VO); **16kHz mono** specifically for the *live microphone capture stream* feeding Vosk (`02_ARCHITECTURE_SUMMARY.md §3`) — these are two different pipelines, do not confuse recording-for-playback specs with the capture-for-recognition spec |
| Bit depth | 16-bit source masters, downsampled to the shipped MP3 bitrate above |
| Channels | Mono for all phoneme/word/VO assets (matches how they'll be heard on small device speakers; stereo adds no value and only increases file size); UI SFX may be stereo if it meaningfully improves the sound (short chimes/pops — mono is fine here too, prefer mono unless a specific SFX genuinely benefits) |
| Loudness normalization | Normalize all assets to a consistent integrated loudness target (recommend -16 LUFS for playback assets) so a child never has to reach for the volume dial between a phoneme and a word recording |
| Pitch accuracy | Phoneme reference tones: ≤±10 cents frequency deviation (`01 §2`, SRS 3.1.1) — verify with a tuner/spectrum analysis pass, not just by ear |

## 4. Pronunciation Standards

- **Native English speaker**, standard/neutral accent, recorded specifically with Filipino Grade-1 English-language-learner audiences in mind — clear, unhurried articulation, no regional vowel reduction that could read ambiguously to an early learner.
- Phoneme recordings isolate the sound itself first (e.g., "/mmm/") before the word-in-context framing ("...for M like Mouse") — matches the exact SRS example (`01 §1 Module 1`).
- Consistency: the same voice talent across all 28 phonemes and all 35 Blend It words, so a child isn't learning to recognize a new voice's timbre every letter.

## 5. Recording Environment & Quality Standards

- Treated/quiet room, target noise floor comparable to the app's own ≤40dB ambient threshold for the *child's* environment (`01 §2`) — the reference recordings should be materially cleaner than what we're asking the child's home environment to achieve.
- No background music, reverb, or processing effects on phoneme/word masters — the entire point of these assets is to be an unambiguous reference target for both human listening and (indirectly, as design inspiration for calibration) the Vosk engine.
- QC pass: every phoneme/word file gets a second-listener review purely for mispronunciation/clipping/noise-floor issues before it's approved into the asset pipeline.

## 6. Fallback Behavior

- If a referenced audio asset is missing at build time, this is a **build-breaking error**, not a runtime fallback (`01_REQUIREMENTS_SUMMARY.md §4`) — there is no network path to fetch a missing file post-install, so silent runtime failure would mean a permanently broken lesson for whoever installed that build.
- The `ng`/`ñ` (and draft `x`) letters remain unrecorded until SME sign-off (`01 §5`) — the seed data pipeline should make it obvious these are placeholders (e.g., failing a content-completeness build check) rather than allowing a build to ship with silent/empty audio behind those letters.

## 7. Playback & Caching Recommendations

- Load and decode audio just-in-time per screen (not all 28+35 assets preloaded at app start) — `AudioPlayer` should be a thin, reusable `MediaPlayer` wrapper per `05_ENGINEERING_BLUEPRINT.md §3`, reset/reused across rapid replay taps rather than recreated.
- Assets ship bundled in the APK (`assets/`), not downloaded — consistent with the fully-offline-after-install constraint (`00_PROJECT_SUMMARY.md §6`). No caching-from-network logic is needed or wanted.
- Corrective Say It audio reuses the Hear It phoneme recording for the same letter — do not commission a duplicate "wrong answer" recording per letter (`14_ASSET_MANIFEST.md §4`).
