# Audio task: the short vowel sounds for PlayIT, made with ElevenLabs (for a team member)

**What you make:** the short vowel sounds that PlayIT teaches, in a voice that matches Lily, the app's guide. That means "a" as in **a**pple, "i" as in **i**nsect, and so on.
**Tool:** ElevenLabs (elevenlabs.io).
**Time:** about 1 hour.
**Deadline:** Sunday, Oct 11, so there is time to check the sounds and build them into the app before the Round 2 test on Oct 19. The owner may change this date.

**Why it's done this way:**
- Three rounds of computer-made vowels failed. The owner heard them as robotic, choppy or the wrong vowel.
- The main trap is that a text-to-speech voice reads "a" as the letter's **name**, "ay". That is exactly what a child must not learn here.
- So **you say each sound** (a person gets the mouth shape right), and **ElevenLabs' Voice Changer turns your voice into a Lily-like voice**.
- Text to Speech is used only for the "a, a, apple" phrases, which it reads well.

---

## 0. Before you start (the owner decides)

**The owner picks the ElevenLabs plan:**

| Plan | Cost | What it means for PlayIT |
|---|---|---|
| **Starter (recommended)** | about USD 5-6 for one month | Commercial use is allowed and no credit line is needed. It also includes **Instant Voice Cloning**, so the voice can be cloned from Lily's own sample and the sounds really match her. One month is enough; cancel after |
| Free | 0 | Non-commercial only, and anything published must credit "elevenlabs.io". There is no voice cloning, so you pick the closest library voice and it won't fully match Lily. Free-plan audio can never become commercial later, even after upgrading |

- **Use the project's account,** not a personal one, so the owner keeps the history and the license.
- **Write down** the plan, the date and the voice used. The owner puts them in the release manifest.

**Lily's voice sample:** `docs/audio-reference/lily_reference.wav` (62 s) in the project's GitHub repo, branch `refactor/hear-say-it`. The owner can also send it to you.

---

## 1. What to make

**Required (Chapter 1, tested on Oct 19):**

| File | The sound | Say it like the start of | Never this |
|---|---|---|---|
| `a.wav` | short a | **a**pple | "ay" (the letter's name), "ah", "eh" |
| `i.wav` | short i | **i**nsect | "eye" (the letter's name), "ee" |

**If you have 15 more minutes (later chapters):**

| File | The sound | Say it like the start of | Never this |
|---|---|---|---|
| `e.wav` | short e | **e**gg | "ee" (the letter's name), "ay" |
| `o.wav` | short o | **o**ctopus ("ah", mouth open as at the doctor's) | "oh" (the letter's name) |
| `u.wav` | short u | **u**mbrella ("uh", short and relaxed) | "you" (the letter's name) |

**Each finished file holds 13 takes of one sound, in this order, with about 2 seconds of silence before every take:**

| Takes | Style | How |
|---|---|---|
| 1-5 | **Short** | Just the sound, as short as it is at the start of the word, about a quarter of a second |
| 6-10 | **A bit longer** | The same sound held steady for about half a second, then let it fade. Flat pitch: don't sing it, and don't let it rise like a question |
| 11-13 | **Phrase** | "a, a, apple" in a calm teaching voice, with short pauses (under half a second) |

---

## 2. Step 1: make the voice (once)

**On Starter:**
1. Go to Voices > Add a new voice > **Instant Voice Clone**.
2. Upload `lily_reference.wav` and name the voice **"PlayIT Lily"**.
3. If ElevenLabs asks you to confirm you have the rights to the sample: the sample is the app's own voice, made with Kokoro-82M (Apache-2.0 license), so you may clone it.

**On Free:**
1. Open the Voice Library and filter for a female voice with a young, warm, friendly tone and an American accent.
2. Play `lily_reference.wav` next to a few candidates and pick the closest one.
3. Write down its name.

---

## 3. Step 2: say the sounds yourself (Voice Changer source)

**Where:** a quiet, soft room, such as a bedroom with curtains or facing an open closet full of clothes. Turn off fans and the aircon. Late evening or early morning is quietest.

**How:**
- Record on a phone or laptop, about a hand-span (15-20 cm) from your mouth, a little to the side.
- Make **one recording per sound**, for example `a_me.wav`, holding takes 1-10 (short and a bit longer) with 2-second gaps.
- Don't say anything else in the recording. **Made a mistake?** Stay silent for 2 seconds and redo that take.
- Your voice doesn't need to sound like Lily. It only needs the **right mouth sound**:
  - **a (apple):** jaw dropped, mouth open wide, lips spread a little.
  - **i (insect):** lips relaxed, mouth only a little open, short and loose. Not the tight smile of "ee".
  - **e (egg):** mouth half open, relaxed. **o (octopus):** jaw dropped, round, "ah". **u (umbrella):** everything relaxed, a short "uh".
- Start each sound softly (no click in the throat, no "h") and end cleanly (no "uh" after it, no closing lips).
- **If a teacher is around,** ask them to say each sound once and copy them. That beats any description here.

---

## 4. Step 3: Voice Changer (your takes become Lily-like)

1. Open **Voice Changer** (it used to be called Speech to Speech).
2. Choose the voice "PlayIT Lily", or your library voice on Free.
3. Upload `a_me.wav`. Turn **Remove background noise** on.
4. Settings: Stability about **50%**, Similarity about **75%**, Style exaggeration **0**.
5. Generate and listen.
   - The sound must still be the start of "apple", not "ay", and must not have an "uh" after it.
   - If a setting change makes it worse, go back to these values.
6. Download as **WAV** if your plan offers it; otherwise MP3 is fine (see step 5).
7. Repeat for `i_me.wav` (and e, o, u).

---

## 5. Step 4: the phrases with Text to Speech (takes 11-13)

1. Open **Text to Speech** and choose the same voice.
2. Use the default (newest) English model.
3. Paste the line for the sound, **exactly**:

| Sound | Text to paste |
|---|---|
| a | `a... a... apple.` |
| i | `i... i... insect.` |
| e | `e... e... egg.` |
| o | `o... o... octopus.` |
| u | `u... u... umbrella.` |

4. Generate about **6 times** and keep the **3 best**, where the two short sounds match the start of the word.
   - If the model reads the letter name ("ay... ay... apple"), try the variant: `ah... ah... apple.` for a, `ih... ih... insect.` for i, `eh... eh... egg.` for e, `ah... ah... octopus.` for o, `uh... uh... umbrella.` for u.
   - If none sound right, skip the phrases. Takes 1-10 matter most.
5. Download each kept phrase.

---

## 6. Step 5: put each sound into one WAV file (Audacity, free)

1. Install **Audacity** (audacityteam.org).
2. Open the Voice Changer result for "a" (File > Open). MP3 is fine.
3. Add the 3 phrase takes at the end: File > Import > Audio, then cut and paste each one after the others.
   - Leave **about 2 seconds of silence** before every take. Use Generate > Silence (2 seconds) where a gap is too short.
4. Use Tracks > Mix > Mix Stereo Down to Mono if the track is stereo. Set the project rate (bottom left) to 44100 or 48000 Hz.
5. Use File > Export > Export as WAV, Encoding **"Signed 16-bit PCM"** (not 32-bit float). Save as `a.wav`.
6. Do the same for `i.wav` (and e, o, u).

---

## 7. Step 6: check your files with the script

1. **Install Python 3** from python.org. On Windows, tick **"Add python.exe to PATH"**.
2. **Download two files** from the GitHub repo (branch `refactor/hear-say-it`, folder `tools/audio/`) into one folder, for example `Documents\playit-audio\`:
   - `check_recordings.py`
   - `review_page.py`
3. **Put** `a.wav`, `i.wav` (and e, o, u) in a subfolder `raw`.
4. **Open a Command Prompt** in `Documents\playit-audio\` and run:
   ```
   python check_recordings.py raw --out checked
   ```
5. **Read the report** (it's also in `checked\report.txt`):
   - **OK:** fine.
   - **FIX:** redo that file; the line says why (too loud, too noisy, wrong format).
   - **CHECK:** a different number of takes than 13, usually a gap under 2 seconds. Fine if you know why.
6. **Open `checked\index.html`** in a browser and listen to every take. If most takes of a sound are wrong, go back to step 3 for that sound.

---

## 8. Send it

**Zip three things and send them to the owner through Google Drive or a USB stick:**
- the `raw` folder;
- the `checked` folder;
- a small text file `notes.txt` with:
  - the ElevenLabs plan and date;
  - the voice used ("PlayIT Lily" clone, or the library voice's name);
  - your Voice Changer settings;
  - which phrase text worked (`a... a... apple.` or `ah... ah... apple.`).

**Don't put your name** in any file or folder name. The project's code is public on GitHub. Your own source recordings (`a_me.wav` and so on) stay with you; they are not needed.

**By sending the zip, you agree** that these sounds may be used, without your name, in the PlayIT app, its public code repository and its school presentations. Tell the owner if you don't agree.

---

## What happens next (for your information)
1. The owner scores every take on the `checked\index.html` page.
2. Claude trims and evens out the loudness of the picked takes. Then Claude builds the release (with your `notes.txt` facts and the plan's license terms in the manifest) and the app card.
3. A teacher checks every sound before it ships.
4. In the app, Hear It plays them in the "a, a, apple" rhythm: "Listen! This letter says... a... a... apple."
