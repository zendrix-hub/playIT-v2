@AGENTS.md

## Shortcut Command: "run and review"

When the user enters `"run and review"` (or `"review"`), Claude:

1. **Pulls and reads the newest state.** `git pull`. Read the newest entries at the end of `docs/tasks/SESSION_HANDOFF.md`: the last "Claude review" entry and every agy entry after it.
2. **Finds the agy commits to review:** every commit since Claude's last review whose body has `Card: NN`. List them with `git log --grep='^Card: ' --format='%h %s'`.
3. **Runs the mechanical checks** for each card: `python3 tools/dev/review_card.py NN`. Every line should be `PASS`. A `refs` FAIL means a deleted asset is still named in app code, and needs a fix card if the card didn't list that file. A `hash` WARN means the evidence-log row still needs its hash, which is filled in at acceptance.
4. **Runs the tests and checks CI.** Run `ROBOLECTRIC_DEPS_DIR=~/.playit-env/robolectric-deps tools/dev/gradlew_wsl.sh --offline testDebugUnitTest`; drop `--offline` only if a dependency is missing from the cache. Then run `tools/dev/ci_status.sh`. For UI cards, also check the 4 sizes in the `playIT-screenshots` CI artifact.
5. **Reads each diff against its card.** Check the Files list, the exact code given in the card, and stale references outside the card.
6. **Accepts or writes a fix card.** Accepting means the evidence-log row gets `(accepted)`, the hash and the CI run. A fix card is `card-NNb`. Then a "Claude review" entry goes in the handoff and the queue in `AGY_RUNBOOK.md` is updated.
7. **Checks the user's review pages.** Look for new `*_review.csv` files in `Documents/playIT-audio-batches/` and `Documents/playIT-image-batches/`. Turn approved picks into releases and set the cards that wait on them to `ready`.
8. **Does the next Claude-owned step** from the newest "Claude" handoff entry.
