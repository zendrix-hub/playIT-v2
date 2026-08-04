# 14 — Asset Manifest

Complete inventory. Quantities are exact counts derived from the 28-letter/7-group structure in `01_REQUIREMENTS_SUMMARY.md`. Naming for every row follows `22_FILE_NAMING_CONVENTION.md`.

## 1. Images — Letter & Phoneme Assets

| Asset type | Count | Notes |
|---|---|---|
| Letter cards (uppercase glyph, styled per `16_ILLUSTRATION_STYLE_GUIDE.md`) | 28 | One per Marungko letter; `ng`/`ñ` included but content-flagged (see `01 §5`) |
| Example-word illustrations (Hear It / letter card companion image) | 28 | 1:1 per letter, e.g. Mouse for `m` |
| Find It picture-grid illustrations | ~56–80 | 3 target images per letter × 28 = 84 target slots, but many target words are reused as *distractors* for later letters — build a shared pool, not 84 unique commissions. Budget for **~60 unique illustrations** covering all target words across all 28 letters (some overlap is expected and desirable, since distractors are drawn from already-taught letters) |
| Blend It word illustrations | 35 | One per word across the 7-group draft word bank (`19_AUDIO_SCRIPTS.md §3`) |

## 2. Images — UI, Character, Reward Assets

| Asset | Count | Notes |
|---|---|---|
| Mascot pose/expression set | 8 poses | Happy, Excited, Thinking, Encouraging, Celebrating, Neutral/Idle, Listening (Say It), Pointing (onboarding) — per `17_CHARACTER_DESIGN_GUIDE.md` |
| Avatar picker options | 8–12 | Curated animal avatars for profile creation (`10 §5`) — not the mascot itself, separate asset set |
| Map background/terrain elements | ~10 | Winding path segments, decorative props (pencil tower, crayon bridge, book piles — per SDD Map mockup) |
| Map node frames | 3 states × 2 node types | Letter node (locked/unlocked/completed) × Blend It node (locked/unlocked/completed) = 6 total frame variants |
| Star icons | 2 states | Filled, empty (for 1–3 star display) |
| Heart icons | 2 states | Filled, empty (for 5-heart display) |
| Streak flame icon | 1 (+ 4 milestone badge variants: 5/10/15/20 days) | |
| Reward/celebration burst (confetti, glow) | 1 SVG/Lottie asset, reusable | See `21_ANIMATION_GUIDE.md` |
| Splash screen illustration | 1 | Logo lockup + mascot |
| Profile screen backgrounds | 2 | Soft Sky background variants |

## 3. Icons (see `20_ICON_GUIDE.md` for full spec)

| Icon | Count |
|---|---|
| Navigation (back, close, home) | 3 |
| Play/replay audio | 2 (play, replay-with-count) |
| Microphone (idle/listening states) | 2 |
| Correct/incorrect indicators | 2 |
| Lock | 1 |
| Export/download (PDF) | 1 |
| Profile/parent | 2 |
| Noise indicator (quiet/loud states) | 2 |

## 4. Audio — Speech & Phoneme Models

| Asset | Count | Notes |
|---|---|---|
| Phoneme model recordings ("Hear It") | 28 | Native-speaker recordings, one per letter; `ng`/`ñ` flagged pending SME script approval |
| Corrective phoneme playback ("Say It" wrong-answer audio) | 28 | Reuses the Hear It recordings — no separate asset needed, confirm in `18_AUDIO_PRODUCTION_GUIDE.md` |
| Word audio (Blend It) | 35 | One per word in the draft 7-group bank |
| Vosk offline speech model | 1 (bundled) | `vosk-model-small-en-us-0.15` or newer compatible small English model — confirm exact bundled model matches the pinned Vosk version (`02 §8`) |

## 5. Audio — UI/SFX (see `18`/`19` for scripts and specs)

| Asset | Count |
|---|---|
| Correct-answer chime | 1 |
| Incorrect-answer soft pop | 1 |
| Heart-loss whoosh | 1 |
| Heart-recovery sparkle | 1 |
| Node-unlock magical chime | 1 |
| Level-complete celebration fanfare | 1 |
| Blend It success chime | 1 (may reuse correct-answer chime) |
| Blend It buzz (incorrect) | 1 |
| Streak-badge unlock sound | 1 |
| Mascot voice-over lines (encouragement, instructions) | ~15–20 | See `19_AUDIO_SCRIPTS.md §2` |

## 6. Animations (Lottie/Rive or native Compose — see `21_ANIMATION_GUIDE.md`)

| Animation | Type |
|---|---|
| Tap feedback (scale bounce) | Native Compose spring |
| Map node breathing pulse (active node) | Native Compose spring, infinite repeat |
| Star reward drop/bounce/glow | Lottie (complex multi-stage) |
| Confetti burst | Lottie |
| Incorrect-answer shake | Native Compose spring |
| Screen entry fade+slide | Native Compose |
| Mascot idle loop | Lottie or sprite sheet |
| Loading/morphing shape (Splash, any wait >1s) | Lottie or native M3 Expressive loading indicator |
| Streak badge unlock celebration | Lottie |

## 7. Fonts

| Font | Weight range needed |
|---|---|
| Lexend (variable) | Regular–ExtraBold, plus hyper-expansion axis if using the variable file (`03 §5.2`) |
| Andika (static fallback) | Regular, Bold |
| Nunito | Regular, Medium, SemiBold, Bold, ExtraBold — retained for adult-facing surfaces only (`10 §2`) |
| Poppins | Regular — Nunito fallback, adult-facing only |

## 8. Content Gaps to Resolve Before Full Production Lock

- **`ng` and `ñ`** — no letter-card art, example-word illustration, Find It picture, or audio script can be finalized until a reading-curriculum SME supplies approved English-phonics content (`01 §5`). Do not have an image or voice generation pass silently invent example words for these two letters.
- **Group 1 Blend It (`m,s,a,i`)** — only 3 solid words are constructible (`SAM`, `SIS`, `AIM`); confirm with stakeholders whether 3 illustrations/audio files is acceptable for this one group or whether the word-bank constraint needs a different resolution (`13_MASTER_TASKS.md`).
