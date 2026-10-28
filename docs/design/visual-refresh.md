# PlayIT design refresh (branch `design/visual-refresh`)

The user asked for a new design on its own branch: a new look, new AI-generated art, and animation such as Lily's mouth moving when she talks (2026-10-27). Only the look and the motion change. Every screen, step, word, sound and rule of the app stays as it is on `refactor/hear-say-it`.

The design board, with the talking demo, the palette on Hear It and the four approved art samples: https://claude.ai/artifact/9JjfcPy4g4Tr8rGcxCdU4X (private to the user).

## Decisions (user, 2026-10-27)
- **Scope:** a new look and new art.
- **Lily:** kept as she is and animated. Her released pictures are not redrawn; only her mouth is painted out for the talking frames.
- **Who builds it:** on this branch Claude writes the app code, and agy generates the pictures (card D1). `main` and `refactor/hear-say-it` are untouched.
- **Art style:** a refined flat cartoon, after four samples the user approved: the umbrella, mouse, fish and sun in `docs/assets/briefs/2026-10-27-refresh-anchors/`.
- **Nothing paid:** pictures come from Nano Banana Pro in Antigravity on its free quota. Canva is the fallback only if there is no other way. No Figma AI.

## Kept on purpose
- **Fonts:** Lexend and Andika. The stakeholder chose them because their single-story a and g match how children learn to write (`Type.kt`).
- **No idle loops:** effects happen only on meaningful moments (team decision 2026-10-06), and `PerformancePolicyTest` (TC-POL-02) checks it. Lily's mouth moves only while a clip plays.
- **Sizes:** every size stays as it is, including 64 dp touch targets and 16 sp text (TC-ACC-02).

## Built
| What | Commit | How |
|---|---|---|
| **Lily talks** | `ae93752` | `AudioPlayer.voice` reports the speech clip playing. `TalkingLily` swaps to her mouthless frame (`images/mascot/talk/`) and draws the mouth in her colours. For a letter's sound it takes that letter's `ArticulationGroup` shape (m, b, p press the lips together); other speech follows the clip's loudness (`assets/lipsync/voice_envelopes.json`, from `tools/design/build_voice_envelopes.py`). This covers the lesson header, the bubbles, the map header and the splash. |
| **Lily blinks and hops; pictures arrive** | `da70b44` | <ul><li>Lily blinks once as each line begins: a lid in her face colour drawn over her eyes, whose places come from her pictures (`LilyEyes.kt`); celebrating already has her eyes shut.</li><li>She hops once when she starts celebrating, with a small squash as she lands.</li><li>The letter card and the Find It pictures settle in like stickers when a screen opens, the pictures one by one (`stickerIn`). It draws in a layer, so layout and touch targets don't change.</li><li>All three happen once per moment. With reduced motion there is no blink or hop, and the stickers only fade in.</li></ul> |
| **Find It pictures redrawn** | release `2026-10-29-refresh-findit` | All 63 Find It and Hear It pictures (`pictures/picture_*.png`) in the refined flat cartoon. <ul><li>Nano Banana Pro in Antigravity drew 25 of the picks. When its free quota locked until about 2026-11-03, the user chose Canva for the rest (2026-10-28): 38 picks, from Canva's image generator on the user's Business trial, which is set to end and not charge. Each picture's generator is in the manifest.</li><li>Cut out with `cutout.py --fill 0.70`. The white inside a ring, the key's heart, the yo-yo's loop, the jug's handle and the kite's string loop is cleared with `--holes` (`docs/assets/briefs/2026-10-27-refresh-findit/holes.json`).</li><li>Audit: 0 FAIL, 0 WARN. The user marked all 63 OK on the final review page.</li></ul> |
| **Lily's island palette** | `2a39faa` | New values on the old token names in `Color.kt`. Each colour and its contrast (normal text needs 4.5:1, large text 3:1): <ul><li>lagoon teal `#0B7F7A` for actions, white text 4.8:1</li><li>sunflower `#FFC53D` for listening and stars, brown text 7.9:1</li><li>leaf `#2E9A3E`, white text 3.6:1, so large text only</li><li>hibiscus `#F2545B`</li><li>apricot `#F7922F`</li><li>Lily brown `#4A2E18` for outlines, the pictures' own line</li><li>text `#3B2416`, 14:1</li><li>ground: morning sky to sand (`GroundColors`)</li><li>locked things: warm stone (`Locked*`)</li></ul> |

## Next
1. **New art:** agy runs card D1, three batches (126 pictures). After each batch Claude cuts out, audits and releases it. The Find It batch is released (above); Blend It and the world batch are next, when agy's free quota is back.
2. **Check on the A21s:** build an APK from this branch and play section 1, to see the mouth and the colours on the phone.

## Rules on this branch
- Run `./gradlew testDebugUnitTest` before every commit. Behaviour tests must keep passing unchanged.
- **A picture reaches `app/src/main/assets/images/` only from a release manifest** in `docs/image-release/<date>/`, with the user's OK on a review page (AGY_RUNBOOK.md "Image gate").
- **Lily's talking frames** (`images/mascot/talk/`) are derived from her released poses by `tools/design/make_lily_talk.py`. They go into a release manifest once the user approves how she talks on the phone.
