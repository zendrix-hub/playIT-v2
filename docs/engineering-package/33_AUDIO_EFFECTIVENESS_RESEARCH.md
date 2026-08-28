# 33 — Audio Effectiveness Research

> Companion to the rewritten `ELEVENLABS_AUDIO_GENERATION_DRAFT.md`. This
> doc explains *why* the current generated audio likely isn't landing with
> kids and what the research actually supports — the operational fixes
> live in the rewritten draft, not here.

## 1. The most likely concrete bug — check this first

The existing `ELEVENLABS_AUDIO_GENERATION_DRAFT.md` phoneme table's
**Script** column — the thing that would get pasted directly into a TTS
generation call — reads like this: `/b/ (quick, no "uh")... B, like Ball.`
That parenthetical direction note is sitting *inside the literal text to
be spoken*, not in a separate instructions field a model or a human
operator would treat as metadata. A TTS engine has no way to know
`(quick, no "uh")` is a production note rather than words to voice — it
either tries to read those words aloud, or handles the parenthetical
unpredictably. If whoever ran the batch pasted the Script column
verbatim (the natural reading of a column called "Script"), that alone
could account for garbled or nonsensical output, independent of anything
else in this doc. **Before doing anything else, check a few of the
existing generated files for this exact symptom** — literal spoken
artifacts of the direction notes. The rewritten draft separates these
into a clean TTS-input column and a clearly-labeled "never paste this
part" note column specifically to prevent it happening again.

## 2. The mechanism gap — direction notes were never going to work anyway

Even with clean input text, the original approach of writing
`(quick, no "uh")` as a hope-the-model-listens instruction was never
reliable. <cite>ElevenLabs' own documentation is direct about this: to
force a specific pronunciation you use SSML phoneme tags or a
pronunciation dictionary — IPA or CMU Arpabet — not free-text
instructions, and CMU Arpabet tends to be more predictable than IPA with
the current implementation.</cite> There's also a model-compatibility trap
worth checking: <cite>pronunciation dictionary phoneme tags only work
with the eleven_flash_v2 and eleven_v3 models — other models silently
fall back to default pronunciation and simply ignore the dictionary
entry.</cite> If the batch ran on an incompatible model, every phoneme
override would have been silently dropped with no error. The rewritten
draft specifies CMU Arpabet dictionary entries and names the required
model explicitly for exactly this reason.

## 3. The "no schwa" target itself needed refining, not just better execution

This is the more interesting finding. The instinct to eliminate the schwa
was reasonable — <cite>phonics educators do teach "pure" sound production
and flag added schwas as something to watch for, since they can bleed
into spelling errors later.</cite> But the stronger research signal points
somewhere more specific: <cite>standard phoneme-segmentation scoring
practice explicitly does not count a schwa added to a consonant as an
error, because some phonemes genuinely cannot be produced in isolation
without some vowel-like release.</cite> More strikingly, <cite>a classroom
study found that first graders taught to produce the 26 consonant sounds
completely "pure" (no schwa) all struggled with it, while the same
children taught the same sounds with a schwa attached learned to say them
without difficulty within two short sessions.</cite>

Practical takeaway: a **zero-release target for stop consonants (b, t, k,
d, g, p, c) was probably unrealistic** — both linguistically (a stop
consonant produced in true isolation still needs some release burst) and
pedagogically (kids parse a slightly-released stop more easily than a
clipped one). The rewritten draft's target is "light, quick release —
never a prolonged, sung syllable," not "zero release." That's both more
achievable for the TTS and closer to what the research actually
recommends.

## 4. The mascot VO problem is probably a different mechanism

The current mascot VO table pairs each line with an "Emotion" label
(Excited, Encouraging, Thinking...) as a metadata column — but nothing in
a plain-text TTS prompt makes a model perform that emotion; a label next
to the script isn't an instruction the model reads. <cite>ElevenLabs'
newer v3 model replaces this entirely with audio tags — bracketed cues
like [excited] or [warmly] placed inline in the text itself — which the
model interprets as performance direction rather than words to
speak.</cite> <cite>Community consensus going into mid-2026 is to use v3
specifically when emotional performance matters and v2 when consistent,
predictable neutral narration is the priority</cite> — which maps cleanly
onto PlayIT's existing two-voice split: Voice A (phonemes) wants
predictability, Voice B (Lily) wants genuine warmth. The rewritten draft
puts audio tags inline in Voice B's scripts and keeps Voice A on the more
predictable model.

## 5. Generation is non-deterministic — budget for it

<cite>Even identical phoneme input can produce different output across
generations; ElevenLabs' own guidance is to generate more than one take
and select the best result when consistency matters.</cite> Treat this as
a generation-time step, not just the existing post-hoc QC listening pass —
generate 2–3 takes per file before the QC pass even starts, especially
for the 12 stop/glide/affricate/blend sounds flagged in the rewritten
draft.

## Sources consulted
- ElevenLabs documentation — best practices, pronunciation dictionaries,
  forcing pronunciation, v3 audio tags
- Phonics Hero, Reading Rockets, AIM Nexus — phonics instruction and
  schwa-in-isolation practice
- USPTO patent filings (rapid decoding/encoding method; phoneme
  segmentation scoring) — classroom schwa findings and assessment
  standards
