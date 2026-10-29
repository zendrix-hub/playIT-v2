# Phoneme Clip Duration Audit (FR-02, Table 4)

**Date:** 2026-09-28 · **Branch:** `refactor/hear-say-it` · **Scope:** audit only. No audio or code was changed.

## 1. Purpose

FR-02 (`docs/specs/hear-say-refactor.md` §5.1) says that phoneme clips shall meet Table 4 (§2.2). This report measures every file in `app/src/main/assets/audio/phonemes/` and compares its length with the Table 4 target. It covers only the duration part of Gate 1 (Table 6). It does not replace the Praat spectrogram check, the blind listening screen, or the teacher audit.

## 2. Method

ffprobe/ffmpeg could not be installed in the audit environment (no apt access). Durations were therefore measured with a Python 3 script that uses only the standard library and parses the MP3 bitstream directly. The script is kept outside the repo.

1. **File duration.** The script skips the ID3v2 tag and walks every MPEG audio frame header, summing samples per frame. It also reads the Xing/Info header and its LAME-style encoder delay and padding fields. For every file, the frame count it finds matches the frame count stored in the Info header.
   - *Raw* = frames × 576 samples ÷ 24 000 Hz.
   - *Playback* = raw − (encoder delay 47 + decoder delay 529 + end padding 576 samples) = raw − 48 ms. This is the length a gapless decoder outputs, and it is used as the file duration below.
2. **Sound length (Table 4 basis).** As clarified by the project owner, Table 4 lengths refer to **the sound itself, not counting the 50 ms pads** from §2.3. Two estimates are given:
   - **File − pads** = playback − 100 ms. This assumes the clip follows the §2.3 editing standard (silence trimmed, exactly 50 ms pad at each end).
   - **Active span (heuristic).** This is the time from the first to the last MP3 frame whose Layer III `part2_3_length` is non-zero, i.e. the encoder spent bits on audio. Silent frames encode as 0 bits. Resolution is one frame (24 ms), so treat values as ±48 ms. Low-level fade tails count as active. This is a bitstream proxy, not an acoustic measurement.
3. **Pass rules.**
   - Short sounds: sound length ≤ the Table 4 limit. These limits are marked **[proposed]** in the spec.
   - Continuous sounds: Table 4 says "about 800 ms" and gives no tolerance. **Audit assumption:** a 700–900 ms band counts as PASS. This band is not a spec value.

## 3. Format

All 29 clips have the same format: MPEG-2 Layer III, 24 kHz, mono, 48 kbps CBR. The encoder tag is `Lavf63.6.100` / `Lavc63.11` (written by ffmpeg). §2.3 specifies **mono WAV** exports. The clips are mono but are MP3, not WAV. This is recorded as an observation only.

## 4. Results

Durations are in ms. "Δ" is measured minus target (800 ms for continuous sounds, the upper limit for short sounds). "Lead / trail" is the silence before and after the active span.

| File | Letter | Sound | Type | Target | Playback | Raw | File − pads | Δ | Verdict (file − pads) | Lead / trail silence | Active span | Δ | Verdict (active span) |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| `phoneme_a.mp3` | a | /æ/ | Continuous | ≈800 ms (band 700–900) | 1440 | 1488 | 1340 | +540 | **OVER** | 72 / 720 | 696 | -104 | **UNDER** |
| `phoneme_b.mp3` | b | /b/ | Short (stop) | ≤250 ms | 1512 | 1560 | 1412 | +1162 | **OVER** | 48 / 744 | 768 | +518 | **OVER** |
| `phoneme_c.mp3` | c | /k/ | Short (stop) | ≤250 ms | 1512 | 1560 | 1412 | +1162 | **OVER** | 48 / 744 | 768 | +518 | **OVER** |
| `phoneme_d.mp3` | d | /d/ | Short (stop) | ≤250 ms | 1512 | 1560 | 1412 | +1162 | **OVER** | 48 / 768 | 744 | +494 | **OVER** |
| `phoneme_e.mp3` | e | /ɛ/ | Continuous | ≈800 ms (band 700–900) | 1392 | 1440 | 1292 | +492 | **OVER** | 72 / 720 | 648 | -152 | **UNDER** |
| `phoneme_enye.mp3` | enye | n/a | n/a | n/a | 1728 | 1776 | 1628 | — | **No Table 4 target** | 48 / 744 | 984 | — | **No Table 4 target** |
| `phoneme_f.mp3` | f | /f/ | Continuous | ≈800 ms (band 700–900) | 2016 | 2064 | 1916 | +1116 | **OVER** | 72 / 240 | 1752 | +952 | **OVER** |
| `phoneme_g.mp3` | g | /g/ | Short (stop) | ≤250 ms | 1968 | 2016 | 1868 | +1618 | **OVER** | 48 / 168 | 1800 | +1550 | **OVER** |
| `phoneme_h.mp3` | h | /h/ | Short (breath) | ≤300 ms | 1488 | 1536 | 1388 | +1088 | **OVER** | 48 / 744 | 744 | +444 | **OVER** |
| `phoneme_i.mp3` | i | /ɪ/ | Continuous | ≈800 ms (band 700–900) | 1752 | 1800 | 1652 | +852 | **OVER** | 72 / 744 | 984 | +184 | **OVER** |
| `phoneme_j.mp3` | j | /dʒ/ | Short | ≤250 ms | 1536 | 1584 | 1436 | +1186 | **OVER** | 48 / 720 | 816 | +566 | **OVER** |
| `phoneme_k.mp3` | k | /k/ | Short (stop) | ≤250 ms | 1512 | 1560 | 1412 | +1162 | **OVER** | 48 / 720 | 792 | +542 | **OVER** |
| `phoneme_l.mp3` | l | /l/ | Continuous | ≈800 ms (band 700–900) | 1896 | 1944 | 1796 | +996 | **OVER** | 96 / 120 | 1728 | +928 | **OVER** |
| `phoneme_m.mp3` | m | /m/ | Continuous | ≈800 ms (band 700–900) | 1824 | 1872 | 1724 | +924 | **OVER** | 72 / 24 | 1776 | +976 | **OVER** |
| `phoneme_n.mp3` | n | /n/ | Continuous | ≈800 ms (band 700–900) | 1896 | 1944 | 1796 | +996 | **OVER** | 96 / 96 | 1752 | +952 | **OVER** |
| `phoneme_ng.mp3` | ng | n/a | n/a | n/a | 1704 | 1752 | 1604 | — | **No Table 4 target** | 48 / 720 | 984 | — | **No Table 4 target** |
| `phoneme_o.mp3` | o | /ɑ/ | Continuous | ≈800 ms (band 700–900) | 1416 | 1464 | 1316 | +516 | **OVER** | 72 / 720 | 672 | -128 | **UNDER** |
| `phoneme_p.mp3` | p | /p/ | Short (stop) | ≤250 ms | 1512 | 1560 | 1412 | +1162 | **OVER** | 48 / 744 | 768 | +518 | **OVER** |
| `phoneme_q.mp3` | q | /kw/ | Short | ≤300 ms | 1560 | 1608 | 1460 | +1160 | **OVER** | 48 / 720 | 840 | +540 | **OVER** |
| `phoneme_r.mp3` | r | /ɹ/ | Continuous | ≈800 ms (band 700–900) | 1704 | 1752 | 1604 | +804 | **OVER** | 48 / 744 | 960 | +160 | **OVER** |
| `phoneme_s.mp3` | s | /s/ | Continuous | ≈800 ms (band 700–900) | 2064 | 2112 | 1964 | +1164 | **OVER** | 48 / 264 | 1800 | +1000 | **OVER** |
| `phoneme_t.mp3` | t | /t/ | Short (stop) | ≤250 ms | 1464 | 1512 | 1364 | +1114 | **OVER** | 48 / 744 | 720 | +470 | **OVER** |
| `phoneme_u.mp3` | u | /ʌ/ | Continuous | ≈800 ms (band 700–900) | 1440 | 1488 | 1340 | +540 | **OVER** | 72 / 744 | 672 | -128 | **UNDER** |
| `phoneme_v.mp3` | v | /v/ | Continuous | ≈800 ms (band 700–900) | 1992 | 2040 | 1892 | +1092 | **OVER** | 48 / 192 | 1800 | +1000 | **OVER** |
| `phoneme_w.mp3` | w | /w/ | Short (glide) | ≤300 ms | 1488 | 1536 | 1388 | +1088 | **OVER** | 48 / 744 | 744 | +444 | **OVER** |
| `phoneme_x.mp3` | x | /ks/ | Short | ≤350 ms | 1800 | 1848 | 1700 | +1350 | **OVER** | 96 / 0 | 1752 | +1402 | **OVER** |
| `phoneme_y.mp3` | y | /j/ | Short (glide) | ≤300 ms | 1488 | 1536 | 1388 | +1088 | **OVER** | 72 / 768 | 696 | +396 | **OVER** |
| `phoneme_z.mp3` | z | /z/ | Continuous | ≈800 ms (band 700–900) | 2040 | 2088 | 1940 | +1140 | **OVER** | 48 / 240 | 1800 | +1000 | **OVER** |
| `phoneme_ñ.mp3` | ñ | n/a | n/a | n/a | 1728 | 1776 | 1628 | — | **No Table 4 target** | 48 / 744 | 984 | — | **No Table 4 target** |

## 5. Summary

| Letters in Table 4 | 26 |
|---|---|
| PASS, file − pads basis | **0** |
| OVER, file − pads basis | 26 |
| PASS, active-span basis | **0** |
| OVER, active-span basis | 22 |
| UNDER, active-span basis | 4 (a, e, o, u) |
| Not in Table 4 | 3 (`ng`, `enye`, `ñ`) |

**No clip meets Table 4 on either basis.**

### Findings

1. **Untrimmed silence.** Most clips end with 700–770 ms of encoded silence, and all of them start with 48–96 ms. The §2.3 standard allows 50 ms at each end. For 17 of 26 letters this trailing silence is the biggest reason the file is too long. Clips with little trailing silence (f, g, l, m, n, s, v, x, z at 0–264 ms) are long because the sound itself is long.
2. **Short sounds are far too long even without the silence.** Every short sound has an active span of 696–1800 ms, against limits of 250–350 ms. A stop consonant (/b/, /d/, /k/, /p/, /t/) that stays active for ~750 ms very likely carries an added vowel ("buh", "kuh") or is a letter name. That is exactly the error §2.3 warns about for TTS. g (1800 ms) and x (1752 ms) are the longest. This needs to be confirmed by listening or with Praat (Gate 1 spectrogram check, Gate 2).
3. **Continuous sounds are split.**
   - The short vowels a, e, o, u (648–696 ms active) fall just below the 700 ms audit band. i (984 ms) is just above it.
   - The continuants f, l, m, n, s, v, z (1728–1800 ms active) are about twice the ~800 ms target.
   - r (960 ms) is just over the band.
   - Once the trailing silence is trimmed, the vowels are closest to passing on length alone.
4. **Duplicates and out-of-scope clips.** `phoneme_enye.mp3` and `phoneme_ñ.mp3` are byte-identical (same MD5). `ng` and `ñ`/`enye` have no Table 4 entry, because the curriculum was reduced to 26 letters in 12922d2. They are listed here for completeness and are not judged.
5. **Similar sizes.** b, c, d, k and p share a file size (9596 bytes, 65 frames), but they are not identical files. The equal size comes from the fixed bitrate.

## 6. Limits of this audit

- The script measures the bitstream, not the audio signal. The active span can over-count low-level noise or fade tails and cannot tell a vowel from a consonant.
- The results were not checked against ffprobe, because ffprobe was unavailable. The frame counts agree with each file's own Info header, and the playback figures use the standard MP3 encoder and decoder delays.
- Nothing here decides pedagogical correctness (Gate 2 and Gate 3). Nothing has been re-generated or edited. Per project rules, audio changes go back to the ElevenLabs production pipeline.
