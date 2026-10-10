# Questions from agy for the user (one entry per stop; see AGY_RUNBOOK.md "Stop and ask")

## Card 25: final OK on the 9 mouth pictures (Claude, 2026-10-10; answered)
- **Answer (user, in chat, 2026-10-10):** "Ship all 9 now": ship the set as it is. The 3 odd faces can be redrawn later and replaced by a new release. Claude recorded the verdicts in the batch's final review CSV and built the release; copy card 24b brings the pictures into the app.
- **Question:** Are the 9 cut-out mouth pictures OK to ship? And is the mix of two face designs acceptable?
- **What I found:** All 9 cut cleanly: no halo on any of the 4 app backgrounds, outline within 15 of #4A2E18, 77 px padding, no harsh red (`audit.py`: 0 FAIL, 0 WARN). But the set has two face designs:
  - 6 pictures share one face (curl nose, pink blush, white highlight): `mouth_back`, `mouth_teeth_close`, `mouth_teeth_on_lip`, `mouth_tongue_up`, `mouth_round`, `mouth_open_breath`.
  - 3 have a different face (long nose line, brown cheek shading, no blush): `mouth_lips_together`, `mouth_smile`, `mouth_wide_open`.
  - Chapter 1 (m, s, a, i) would show the second face three times and the first once. The card asked for "only the mouth shape changes".
- **Options:** (a) ship all 9 now; (b) redo the 3 in an agy round, with the 6 as references; (c) ship now and redo the 3 later.
- **How to answer (no longer needed):** open `Documents\playIT-image-batches\2026-10-07-mouth-shapes\final\index.html`, mark OK or FIX per picture, and click Export CSV. Save the CSV in that batch folder. Claude then runs `make_release.py` and writes the release.

## Card 28: captions leave the child view, but NFR-ACC-01 asks for them (Claude, 2026-10-10)
- **Question:** Do you confirm dropping on-screen sound captions (SRS NFR-ACC-01 item 2), and will you tell the adviser?
- **What I found:** Your decision (2026-10-10) removes captions because pre-readers cannot read the carrier sentences. The IT411 package lists captions as implemented (MVP form Priority #5, SDD §2.0 and §3.6.2) and as the answer to the hearing finding (ACC-06: 3 of 5).
- **What card 28 does meanwhile:** it removes the caption from Hear It and marks the SRS item as amended (adviser confirmation pending). The mouth-shape cue stays as the visual support for hearing difficulties; it shows pictures once card 25 is released. The caption logic stays in the code, unused by the child view, so it can come back.
- **Options:** (a) keep it this way; (b) show only the sound caption (for example "mmm") and drop the sentences; (c) a "Show captions" switch in the Parent Zone, off by default.

## Card 28: which 11 sp text did you see on the map? (Claude, 2026-10-10)
- **Question:** The "companion speech bubble" at 11 sp: which text was it?
- **What I found:** The card named `MascotMapDialogueBubble`, but nothing calls it, in APK B or now (its last call was removed in `c851f4d`), so it cannot be what you saw. The small texts on the map in APK B:
  - "SECTION 1, UNIT n" above each group: 11 sp;
  - the pop-up's small labels: 11 sp;
  - "x of 26 letters" and the star and streak counts in the top bar: 12-13 sp.
- **What card 28 does meanwhile:** it deletes the unused bubble and leaves these texts alone, because raising the banner or the top bar changes layouts that card 22 just fitted to small phones.
- **Options:** name the text, and Claude writes a small card to raise it to 16 sp, with the 4-size screenshot check.
