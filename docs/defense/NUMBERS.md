# PlayIT: verified numbers for the defense

Checked against the code and data on 2026-10-05, at commit `c5e6957` on `refactor/hear-say-it`. Before the defense, re-check any number marked (re-check), because later cards change it. Say a number only if it is on this sheet.

## Product
| Item | Value | Source |
|---|---|---|
| Letters taught | **26** (a-z); NG and Ñ excluded, pending SME review | `di/DatabaseModule.kt:103-111` |
| Chapters (groups) | **7**, of 4, 4, 4, 3, 4, 3, 4 letters | same |
| Group order | 1: m s a i · 2: o b e u · 3: t k l y · 4: n g p · 5: r d h w · 6: c f j · 7: q v x z | same |
| Activities per letter | Hear It, Say It, Find It; Blend It after each group | navigation |
| Blend It words seeded | 33 (re-check after card 15; 5 are not decodable today) | `DatabaseModule.kt` |
| Child profiles per device | up to **6** | `GameplayConstants.MAX_PROFILES` |
| Hearts | **5** at start; **3** after a restart; **+1** per 3 correct in a row, capped at the current pool | `GameplayConstants`, `HeartManager` |
| Say It hearts | **0** lost, ever | `SayItViewModel` never calls `deductHeart` |
| Stars (letter) | 3 = no heart lost; 2 = at most 2 lost; 1 = otherwise. Say It results don't count | `StarCalculator.kt` |
| Stars (Blend It) | 3 = 0 lost and all words first try; 2 = at most 2 lost and ≥80% first try; 1 = otherwise | `BlendItStarThresholds.kt` |
| Say It listening window | **3.8 s** maximum | `SayItViewModel.kt:280` |
| Idle re-prompt | after **10 s**, at most 3 times | `IdleRePrompt.kt` |
| Parent gate | random 2-digit addition or subtraction | `ArithmeticGateManager.kt:24-41` |

## Technology
| Item | Value |
|---|---|
| Language / UI | Kotlin 1.9.23, Jetpack Compose (BOM 2024.05.00), Material 3 |
| DI / DB / Nav | Hilt 2.51.1, Room 2.6.1, Navigation Compose 2.7.7 |
| Speech recognition | Vosk Android 0.3.47, model `vosk-model-small-en-us` (adult US English, **68 MB**; publisher's WER 10.38% TED-LIUM, 9.85% LibriSpeech, adult speech) |
| Android | minSdk **26** (Android 8.0), target/compile 34 |
| ABIs | arm64-v8a, armeabi-v7a, x86_64 (no splits) |
| Debug APK | about **98 MB** (build of 2026-09-30) (re-check) |
| Network permission | **none** (only `RECORD_AUDIO`) | 
| Database | Room schema version **3**, **11** tables, destructive fallback on schema change |

## Quality
| Item | Value |
|---|---|
| JVM unit tests | **226**, all passing (local, 2026-10-05) (re-check), in 30 test classes |
| Instrumented UI tests | 4 classes; **not run in CI** |
| CI | GitHub Actions: unit tests + debug APK on every push to the PR |
| Screenshot tests | Roborazzi, 5 screens rendered in CI (card 10, accepted 2026-10-06); 4-size layout tests from card 18 |

## Audio and images
| Item | Value |
|---|---|
| Voice | Kokoro-82M (Apache-2.0), voice mix af_heart 0.7 + af_bella 0.3 |
| Held sounds | Chatterbox-Turbo (MIT), voice-cloned from a Kokoro clip, chosen per clip by ear (/m/ pending) |
| Released Kokoro clips in the app | **39** (26 key words, 7 tutor lines, 6 UI lines) |
| Older Edge-TTS clips still in the app | **204** MP3 (29 phonemes, 115 words, the rest UI/VO), to be replaced |
| Teacher audit (Gate 3) | **pending** for every shipped clip |
| Pictures | 29 regenerated (AI, Nano Banana Pro), cut out and audited; waiting for the user's OK (card 13) |

## Round 1 validation (Sept 16-23, 2026)
| Item | Value |
|---|---|
| Participants | **25**: 16 children (ages 5-7), 5 parents/supervising teachers, 4 DepEd teachers (target was 30) |
| Sampling | convenience; classroom, home and community sites |
| SUS (caregivers, n = 5) | mean **75.5**, SD **14.62**, 95% CI **57.3-93.7**, grade **B** (Sauro-Lewis); vs 68: t(4) = 1.15, **p = .32** (not significant) |
| SUS scores | 77.5, 50.0, 80.0, 85.0, 85.0 (P-2 agreed with every item; kept) |
| Child enjoyment | 15 of 16 top face (93.8%; Wilson 95% CI 72-99%) |
| Child "easy to play" | 9 of 16 top face; 7 of 16 (43.8%) chose a lower face |
| Teacher checklist | 4 of 4 Yes on 11 of 12 items; PED-08 (letter sounds) 2 of 4 flagged |
| Learning outcome measured | **none** (no pre/post test) |
| ASR accuracy on children measured | **none yet** (Round 2) |
| Round 2 targets | judge ≥80% agreement with teachers; ≤15% false rejects (proposed, spec defaults) |

## Calendar
| Week | Dates | Milestone |
|---|---|---|
| 6 | Oct 12-17 | Gate 3 teacher audit, dry run |
| 7 | Oct 19-24 | Round 2 field validation |
| 8 | Oct 26-31 | Analysis, feature freeze |
| 9 | Nov 2-7 | Midterm submission and oral defense, live demo |
