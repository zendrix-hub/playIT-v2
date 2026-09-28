# playIT handoff: Sep 28, 2026

Two folders:
- **audio_review/**: for listening at home. It does not go into the repo yet.
- **repo_drop/**: copy into the repo tomorrow. It holds agy's task cards, the spike report, and the audio pipeline script.

## Tonight at home (about 30 minutes, laptop or phone speakers are fine)

1. Unzip. Open `audio_review/listening_checklist.csv` in Excel or Google Sheets.
2. Play each file and write **OK** or **FIX** in the `OK_or_FIX` column. Add a short note for any FIX, such as "ends in uh" or "too fast".
   - `phonemes_continuous/`: the 13 held sounds. This is the most important folder. Listen for a steady sound with nothing added at the end.
   - `fragments/`: the tutor lines ("Your turn!", "That's the letter's name. Its sound is…").
   - `keywords/`: the 26 example words. Note: `kw_orange` starts with an r-colored vowel, not short o. Ask the teachers whether "octopus" is better.
   - `phonemes_continuous/alternates/`: backups. Only listen to these for sounds you marked FIX.
3. Send the teachers:
   - the 3 files in `voice_candidates/`, asking which voice they prefer for Grade 1
   - `TEACHER_RECORDING_GUIDE.md`, the 13 short sounds only a person can record
4. Bring the filled checklist back to this chat. I'll fix every FIX, and regenerate everything if the teachers pick a different voice (it takes a few minutes).

Everything was generated in the af_heart voice. The machine check (Gate 1: length plus a check by the app's own Vosk model) passed 13 of 13 sounds, but a machine check doesn't replace your ears.

## Tomorrow with agy

1. Copy the contents of `repo_drop/` into the repo root (it merges into `docs/` and `tools/`), then commit:
   ```bash
   cd /mnt/c/Users/riva.zn/Documents/playIT-v2
   git add docs/tasks docs/spikes tools/audio/kokoro_local.py
   git commit -m "docs: task cards 00-04, Vosk spike, local audio pipeline"
   ```
2. Run the cards **one per agy session, in order**. Start each new session and paste:
   > Implement docs/tasks/card-00-agent-rules.md exactly. Change only the files it lists. Make every test in its Tests section pass, run ./gradlew testDebugUnitTest, then commit with the message in its Commit section. Do not push.

   Then do the same for card-01, card-02, and card-03. Card 03 needs the tutor fragments, so copy only the ones you marked OK into `app/src/main/assets/audio/vo/tutor/`. Leave card 04 until 01–03 are reviewed.
3. After each card: `git log --oneline -1`, test the app on a phone if the card touched Say It, then `git push`.
4. Come back and say **"review card NN"**. I'll pull the branch and check the diff against the card.

## If agy gets stuck
- A test fails twice: stop, and paste the error here.
- It wants to change files outside the card: say no, and ask it to explain why.
- It asks a [confirm] question: the answer is the spec default (already written in AGENTS.md after card 00).

## What the spike found (details in docs/spikes/vosk-foil-spike.md)
Your Vosk model catches added vowels ("ma" at 0.85 confidence) but never recognizes a held "mmm". So Say It keeps word mode as the scored check. The pure-sound check only works for continuous sounds, by measuring how long the sound was held. Card 01 builds that in.
