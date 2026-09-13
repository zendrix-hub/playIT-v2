# PlayIT Tools & External Resources Guide

This document catalogs and provides instructions for the integrated developer tools, public APIs, AI generation services, and voice synthesis utilities in the PlayIT workspace.

---

## 1. Directory Structure (`tools/`)

```
tools/
├── api layer.png             # Reference: Public APIs Directory (456K stars)
├── free-for-dev.png          # Reference: Free for Developers Catalog (132K stars)
├── mcp servers.png           # Reference: Awesome MCP Servers (92K stars)
├── tool 1.png                # Reference: AI Media, Audio & Video Generation Suite
├── fooocus.png               # Reference: lllyasviel/Fooocus SDXL Image Suite
├── README.md                 # This guide
├── elevenlabs_voice_studio.py# Filipina Voice Synthesizer (ElevenLabs API + Zero-Cost Edge Neural)
├── voice_studio_runner.py    # VoiceStudio multi-engine batch phoneme & audio synthesizer
├── dictionary_validator.py   # Phonics Curriculum & CVC Discovery via Public APIs
├── asset_pipeline_optimizer.py # Alpha Background Removal & 4-Benchmark Outline Styler
├── fooocus_asset_bridge.py   # Fooocus SDXL Generation to Android Asset Bridge
├── whisper_evaluator.py      # OpenAI Whisper Speech & Phoneme Accuracy Benchmark Evaluator
├── VoiceStudio/              # Cloned debpalash/VoiceStudio suite (16 TTS / 11 ASR engines)
├── whisper/                  # Cloned openai/whisper speech recognition repository
└── Fooocus/                  # Cloned lllyasviel/Fooocus SDXL image generation suite
```

---

## 2. 🎙️ Filipina Voice Synthesis in ElevenLabs & Neural TTS

### Is there a Filipina Voice in ElevenLabs?
**Yes!** ElevenLabs supports high-quality Filipina voices in two ways:

1. **ElevenLabs Voice Library (Pre-made & Community Voices)**:
   - In the ElevenLabs Voice Library, filter by:
     - **Category**: `Narrative & Story` or `Conversational`
     - **Accent**: `Filipino` or `Southeast Asian`
     - **Gender**: `Female`
   - **Recommended Community Voices**:
     - **"Maria - Warm Filipina Teacher"** / **"Aimee"** / **"Bea"** / **"Kath"**
     - Optimized for `eleven_multilingual_v2` and `eleven_turbo_v2_5` with natural Philippine English cadence and clear phoneme articulation.

2. **ElevenLabs Voice Design & Instant Voice Cloning**:
   - You can upload a 1–2 minute clean recording of a Filipina educator/speaker, or use Voice Design with prompt:
     > *"A young, cheerful Filipina kindergarten teacher with warm maternal energy, speaking clear Philippine English at a gentle pace."*

3. **Zero-Cost Built-In Alternative: `en-PH-RosaNeural`**:
   - Microsoft Neural TTS provides **Rosa (`en-PH-RosaNeural`)**, a female Philippine English voice specifically tuned for educational pronunciation, requiring **zero API keys and zero cost**.
   - Tagalog bilingual option: **Blessica (`fil-PH-BlessicaNeural`)**.

---

## 3. How to Use the Integrated Tools

### A. Voice Studio (`elevenlabs_voice_studio.py`)

```bash
# 1. Synthesize using the natural Filipina English Neural Voice (Free, no API key required)
python tools/elevenlabs_voice_studio.py --preset rosa --text "Welcome to PlayIT! Let's learn phonics together."

# 2. Synthesize using ElevenLabs with your API Key & custom Filipina Voice ID
python tools/elevenlabs_voice_studio.py --preset elevenlabs_custom --voice-id <YOUR_VOICE_ID> --api-key <YOUR_KEY> --text "Great job! You found the letter M!"

# 3. Batch synthesize all 24 mascot voice-over lines directly into the app
python tools/elevenlabs_voice_studio.py --preset rosa --batch-all
```

---

### B. Phonics Curriculum & Dictionary Validator (`dictionary_validator.py`)

Utilizes the **Datamuse API** and **Free Dictionary API** referenced in `api layer.png` to automatically check valid CVC words constructible from cumulative Marungko letter groups:

```bash
# Validate candidate CVC words formable with Group 1 (m, s, a, i)
python tools/dictionary_validator.py --group 1

# Look up IPA phonetic transcription & child definition for a word
python tools/dictionary_validator.py --word bat
```

---

### C. Asset Pipeline Optimizer & Fooocus Bridge (`asset_pipeline_optimizer.py` & `fooocus_asset_bridge.py`)

Applies background cleanup, transparent alpha channels, and the `#2D373E` continuous 4-Benchmark outline to raw or Fooocus SDXL generations:

```bash
# 1. Single asset processing:
python tools/asset_pipeline_optimizer.py --input raw_character.png --out app/src/main/assets/images/characters/kuting_wave.png --size 512 --outline 8

# 2. Bridge images generated from Fooocus directly into PlayIT Android assets:
python tools/fooocus_asset_bridge.py --input tools/Fooocus/outputs/001.png --type picture --name word_mouse --size 512

# 3. Batch bridge an entire folder of Fooocus generations:
python tools/fooocus_asset_bridge.py --input raw_cvc_generations/ --type picture --size 512
```

Fooocus PlayIT style presets are available in `tools/Fooocus/sdxl_styles/sdxl_styles_playit.json`:
- `PlayIT Pediatric Vector`: Clean 2D Duolingo ABC vector illustration style
- `PlayIT Mascot Kuting`: Philippine tarsier mascot poses with warm golden eyes
- `PlayIT Phonics Object`: Simple high-contrast phonics picture card icons

---

### D. OpenAI Whisper Speech & Phoneme Evaluator (`whisper_evaluator.py`)

Benchmarks Say It child speech attempts, validates phoneme accuracy, and grades audio against expected curriculum targets:

```bash
# Evaluate a recorded audio sample against target phoneme 'm'
python tools/whisper_evaluator.py --audio test_child_m.wav --target m --model base

# Evaluate against a target CVC word
python tools/whisper_evaluator.py --audio test_child_mouse.wav --target mouse --threshold 0.8
```

---

### E. VoiceStudio Multi-Engine Audio Runner (`voice_studio_runner.py`)

Local, offline batch audio synthesis for 28 Marungko letter phonemes, CVC words, and mascot voice lines:

```bash
# Batch generate all core phoneme audio clips (/m/, /s/, /a/, etc.)
python tools/voice_studio_runner.py --batch-phonemes

# Batch generate CVC word audio clips ('mouse', 'sun', 'apple', etc.)
python tools/voice_studio_runner.py --batch-words

# Batch generate mascot celebration and instructional VO lines
python tools/voice_studio_runner.py --batch-vo
```

---

### F. Integrated Engineering Agent Skills & Ponytail Rules

The repository is equipped with the following AI agent capabilities in `.agents/`:
1. **Matt Pocock Skills (`mattpocock/skills`)**:
   - `implement`, `tdd`, `code-review`, `diagnosing-bugs`, `domain-modeling`, `codebase-design`, `improve-codebase-architecture`, `to-spec`, `to-tickets`, `triage`, `wizard`, `grill-me`.
2. **Ponytail (`DietrichGebert/ponytail`)**:
   - `.agents/rules/ponytail.md`: Lazy senior dev principles (YAGNI, stdlib first, native platform features, zero bloat, shortest clean working diff).
   - Skills: `ponytail`, `ponytail-review`, `ponytail-audit`, `ponytail-debt`, `ponytail-gain`.

---

### G. Automated CI/CD (GitHub Actions)

Located in [`.github/workflows/android_ci.yml`](../.github/workflows/android_ci.yml), this workflow automatically runs unit tests and compiles the fresh debug APK on every commit to `main`.
