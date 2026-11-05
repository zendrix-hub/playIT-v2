# Adviser enhancements (November 2026)

Branch `enhance/adviser-feedback`, cut from `design/visual-refresh` at `86408a9`. The adviser's advice of 2026-11-05 asked for six changes. The user chose how each one works (plan of 2026-11-05) and asked to be consulted before anything changed.

## What changed

| What | Commit | How |
|---|---|---|
| **Progress record in DepEd form** | `40fb5b0` | <ul><li>**Levels:** the Parent Zone and the PDF label progress with the DepEd report-card words: Outstanding, Very Satisfactory, Satisfactory, Fairly Satisfactory and Did Not Meet Expectations (DepEd Order 8, s. 2015). There are also two plain levels, Still learning and Not started yet.</li><li>**Grade:** a letter is graded once finished. Its percentage score (right answers in Say It and Find It) is transmuted with the Order's table, so 60% is a grade of 75, as on a class record.</li><li>**Parent Zone:** a class record of every child on the phone, then the selected child's record chapter by chapter, "Practise at home", and a legend. No bare percentages.</li><li>**PDF (landscape A4):** a class page with the DepEd header fields, then each child's letters with Say It, Find It, stars, score, grade, level and remarks, the legend, and signature lines.</li><li>**Fixes:** a letter never played was At-Risk; a replay lowered the stars; the PDF dropped rows that did not fit.</li></ul> |
| **The mouth shape shows big** | `3953e7b` | <ul><li>On Say It's second miss, the mouth grows into the prompt card's place while Lily models the sound. It shrinks back beside the mic when it is the child's turn.</li><li>The small one has a magnifier: a tap shows it big again with the sound.</li><li>One grow and shrink each time. With reduced motion it only fades.</li></ul> |
| **Find It hides the words on harder chapters** | `9987477` | <ul><li>Chapter 1 shows every word. Chapter 2 keeps the word on 2 of the 5 cards. From Chapter 3, and on every replay, only pictures show.</li><li>16 pictures that a child would likely misname keep their word until the user decides on the review page `2026-11-05-findit-clarity`.</li></ul> |
| **Words for a 5 to 7 year old** | `eb24dd5` | <ul><li>"CHAPTER n" instead of "SECTION 1, UNIT n". Short place names. "Start", "Play again", "Word game", "Build words", "New player", "You learned M! Great job!", "Done".</li><li>The Guidebook now teaches the app's 7 real chapters and words.</li><li>Blend It Complete's "practicing every day" line plays only on a real streak.</li><li>`ChildWordsTest` guards the child's screens.</li></ul> |
| **Lily welcomes a child back** | `b3e4217` | <ul><li>On the first map of an app launch, a child who has played before sees Lily's card: "Welcome back, Frog! I missed you."</li><li>It offers the next letter, with its sound and picture, or the word game. Buttons: "Let's go!" and "Not now".</li><li>The voice has no name, because the app has no text-to-speech; the name is on screen.</li><li>The clips wait for the user's OK (batch `2026-11-05-return-greeting`, `3d8f395`).</li></ul> |

## Waiting for the user

1. **Voice batch `2026-11-05-return-greeting`** (`Documents/playIT-audio-batches/`):
   - Mark one take OK per line.
   - Then the clips go in through a release manifest, and the greeting speaks.
   - With the same release:
     - Profile Select's long hello plays only on the first use, and "Who is playing? Tap your picture." after that.
     - The Say It word and Find It intros switch to the shorter lines.
2. **Picture review `2026-11-05-findit-clarity`** (`Documents/playIT-image-batches/`):
   - Choose per picture: fine without the word, swap to a picture already in the app (Mat to Mom, Draw to Drum, Kit to King, Web to Wing), keep the word, or ask agy for new art.
   - The swaps and the word list follow that choice.

## For the documents (Zendrix)

The SRS, SDD and STD describe the earlier behaviour. These parts change:

- **FR-10, FR-12 (Parent Zone, PDF report):**
  - Mastered/Developing/At-Risk and percentages become the DepEd descriptors with transmuted grades.
  - The report covers every child on the phone.
  - A replay keeps the best stars.
- **FR-04 (Find It):** words under the pictures depend on the chapter and on replays.
- **NFR-ACC-01 (mouth-shape cues):** the cue shows big while Lily models the sound, then beside the mic.
- **NFR-IND-01 (independent use):** the return greeting, the child-language wording and the Guidebook content.
- **STD:** the new tests are `ProgressLevelTest`, `ProgressRecordCalculatorTest`, `ReportGeneratorTest`, `ReportLayoutTest`, `ReportPageRendererTest`, `BestResultTest`, `WordLabelPolicyTest`, `ReturnGreetingPolicyTest`, `ChildWordsTest`, `ParentDashboardScreenshotTest` and `WelcomeBackScreenshotTest`, plus the updated view-model and layout tests.

No database schema changed. A teacher audit of the new clips is still required before merging to main.
