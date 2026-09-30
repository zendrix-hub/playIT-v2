# Proposal: tutor script and audio composition (NFR-AUD-01, FR-02, FR-03)

**Status:** draft for the user's listening review. Not a task card; agy does not run anything here.
**Date:** 2026-09-30 · **Voice:** Kokoro-82M mix `af_heart*0.7+af_bella*0.3` (user's pick, round 2 review)
**Source of truth for the lines:** `tools/audio/tutor_script.py` · **Review batch:** `Documents/playIT-audio-batches/2026-09-30-tutor-script-draft/index.html`

## 1. What this settles

| Open question (Sep 29–30) | Resolution | Basis |
|---|---|---|
| Key words: spec Table 4 (egg, fan, leaf, octopus, jam, top, web) or the app's seeded words (elephant, fish, lion, orange, jug, tiger, watch)? | **Keep the app's seeded words**, pending teacher approval. Flag **orange**: it starts with /ɔr/, not the short o /ɑ/; the spec suggests octopus. | Spec §2.2: "Keep your current key words where teachers approve them." |
| How corrections are built: fragments around the sound (notebook) or one line followed by the model clip (card 03 code)? | **Fragments around the sound.** | Spec §2.3 and Table 5: lines that contain a pure sound are generated as fragments and joined with the phoneme clip at runtime. The spec wins over card 03 (AGENTS.md precedence). |
| Word mode heard "Its sound is… mouse" | Word-mode compositions play the **sound, then the key word**: "Its sound is /m/ … mouse. Your turn!" The child hears the sound and the word they must say. | AGENTS.md Decisions: the echo step stays in word mode. Spec Table 3 step 5 ("/m/ … mouse"). |

## 2. Writing rules for every line
1. No letter names, and no sound inside TTS text (§1.4, §2.3). Sounds come only from `ph_<L>` clips.
2. Short and concrete: one idea per line, at most eight words, second person, no idioms. The listener is a Filipino Grade 1 child who cannot read.
3. Corrections name the error and the fix (§3.2; Hattie & Timperley, 2007). Praise names the success, not only "Good job."
4. The same words for the same move every time ("Your turn!", "Listen:"), so the child learns the routine.
5. Kind and neutral on a miss: no "wrong" and no "no!". The third miss ends on "Nice try!" and a promise to come back.

## 3. Lines (fragments)
See `FRAGMENTS` in `tools/audio/tutor_script.py` for the exact text and speed. Changes from the Sep 28 pack:

| Line | Sep 28 text | Draft | Why |
|---|---|---|---|
| fb_letter_name | "That's the letter's name. Its sound is" (one clip) | split: "That's the letter's name." + "Its sound is" | Lets word mode and sound mode share the pieces |
| fb_added_vowel | "Almost! Just the sound, no ah. Listen:" | "Almost! Just" + /m/ + "no 'ah.'" | Spec §3.2 wording; the sound sits inside the line, as a teacher says it |
| fb_listen_again | "Listen again:" | "Listen:" | Spec Table 2 wording; one word for one move |
| fb_try_later | "Good trying! We'll practice this one again soon." | "Nice try! We'll practice this one again later." | "Good trying" is ungrammatical; "later" matches the recall recheck |
| praise_* | "Yes!", "Great job!", "You got it!" | "Yes!" + sound + cue; word mode "Yes! Mouse starts with /m/." | Specific praise (§3.2) that links the word to the sound |

## 4. Compositions (what the app plays)
`COMPOSE` in `tools/audio/tutor_script.py`. For m / mouse:

| Moment | Word mode (echo) | Sound mode (recall) |
|---|---|---|
| Hear It (I do) | Listen! · This letter says… · /m/ · /m/ · /m/ · mouse · /m/ · Say it with me! | same |
| Correct | Yes! · mouse · starts with · /m/ | Yes! · /m/ · lips together. |
| Silent or unknown (attempt 1) | Listen: · /m/ · mouse · Your turn! | Listen: · /m/ · Your turn! |
| Letter name (attempt 1) | That's the letter's name. · Its sound is · /m/ · mouse · Your turn! | without "mouse" |
| Added vowel (attempt 1) | Almost! Just · /m/ · no 'ah.' · mouse · Your turn! | without "mouse" |
| Substitution, f (attempt 1) | Listen: · /f/ · Top teeth on your lip. · fish · Your turn! | without "fish" |
| Attempt 2 | Watch my lips. · /m/ · mouse (slow) · Your turn! | without "mouse" |
| Attempt 3 | Let's say it together. · /m/ · mouse · Nice try! We'll practice this one again later. | without "mouse" |

## 5. For teachers ([proposed], not generated yet)
The spec writes cues only for m (praise: "lips together") and f (substitution: "Top teeth on your lip."). Drafts for the other held sounds, to be confirmed or rewritten by teachers:

| Letter | Praise cue (draft) | Substitution cue (draft) |
|---|---|---|
| a | "Mouth open wide." | |
| e | "Mouth open a little." | |
| i | "A small smile." | |
| o | "Mouth open, round." | |
| u | "Mouth relaxed." | |
| f | "Top teeth on your lip." | "Top teeth on your lip." (spec) |
| l | "Tongue up behind your teeth." | |
| n | "Tongue up, hum through your nose." | |
| r | "Lips round, tongue pulled back." | |
| s | "Teeth together, like a snake." | |
| v | "Top teeth on your lip, and buzz." | "Top teeth on your lip, and buzz." |
| z | "Buzz like a bee." | "Make it buzz, like a bee." |

Until a cue is approved, sound-mode praise uses "Yes! /x/. You did it!"

## 6. Impact on the app (future cards, after approval and the go signal)
- Card 03's code plays one fragment and then the model clip (`fb_letter_name`, `fb_added_vowel`, `fb_listen_again`). A fix card (03b) switches `SayItViewModel` to these compositions and fragment ids, adds `fb_starts_with`, `cue_*` and `subcue_*`, and adds the key word in word mode.
- Hear It (card 04) already matches `hearit_sequence`.
- Card 05 copies only the clips marked OK in this review and in the held-sound lab, plus the teacher's short-sound recordings.

## 7. Open
- The held-sound method for all 13 continuous sounds: `Documents/playIT-audio-batches/2026-09-30-heldsound-lab-m/` (the sequences in the draft use method B1 as a placeholder).
- Teacher review of key words (orange), cues (section 5), and the Philippine English vowel qualities (spec §2.2).
