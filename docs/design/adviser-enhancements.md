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

### Second round (user, 2026-11-05, after trying the first)

| What | Commit | How |
|---|---|---|
| **The mouth says the sound** | `d19a3c3` | <ul><li>User: "the mouth needs to move when saying the letter".</li><li>While the model plays, the mouth goes into its shape and pulses with the voice, so a held mmm moves too. It relaxes in the gaps.</li><li>Lily models the sound twice, half a second apart.</li></ul> |
| **Plain words for parents** | `cc6fe22` | <ul><li>User: the parent screen must be easy for parents, not only teachers.</li><li>The chips say "Knows it very well", "Knows it well", "Knows it", "Almost there", "Needs more practice", "Still learning" and "Not started yet".</li><li>The DepEd word and grade stay small for teachers ("For teachers: Outstanding, average grade 91"), in the legend, and in the PDF.</li></ul> |
| **The child types a name** | `a2ffb46` | <ul><li>User: "the child has to type their name ... welcome back &lt;name&gt; not animals".</li><li>New player asks for the name under the animals, and Let's Play waits for it.</li><li>**This replaces the avatar-only onboarding of 2026-10-01 (AGENTS.md).**</li><li>Lily's voice still says no name, because the app has no text-to-speech.</li></ul> |
| **Welcome-back voice** | `77d419b` | <ul><li>Release `2026-11-05-return`: "Welcome back! I missed you.", "Do you want to keep going with this sound?", "Do you want to build some words?" and "You learned all the sounds! ...".</li><li>The Say It word intro and Find It intro keep today's clips: the user kept the Find It one, and left the Say It one unmarked.</li></ul> |
| **Picture choices** | `549d9d8` | <ul><li>Swapped: Mat to Mom, Kit to King, Draw to Drum, Web to Wing.</li><li>11 pictures keep their word.</li><li>12 keep their word until the new Canva art is chosen.</li></ul> |

| **Who is playing? and the name step** | `d2eaa33` | <ul><li>Release `2026-11-05-name-lines`: "Who is playing? Tap your animal!" and "Now type your name!".</li><li>The long hello plays only before any child has a profile.</li></ul> |

## Waiting for the user

1. **Final check of the new Canva pictures** (`Documents/playIT-image-batches/2026-11-05-findit-newart/final/index.html`):
   - The user picked 11 on 2026-11-05: ink, octopus, up, uncle, lamp, yak, guitar, horse, jeepney, question mark and quilt. Insect keeps today's picture.
   - They were exported full size from the Canva design "PlayIT Canva pictures", page 14, saved with the user's OK. They were then cut out (the gap between the octopus's tentacles is cut out too), and the jeepney's bright red was turned to tomato. They pass every automatic check.
   - After the OK, `tools/images/make_release.py` releases them:
     - `GridGenerator.pictureBank` takes Octopus, Lamp, Guitar, Horse, Jeep and Question in place of Ox, Lit, Gap, Hen, Jet and Quiz, and new art for Ink, Up, Uncle, Yak and Quilt;
     - `WordLabelPolicy.keepsWord` drops the six replaced words.

## For the documents (Zendrix)

The SRS, SDD and STD describe the earlier behaviour. These parts change:

- **FR-10, FR-12 (Parent Zone, PDF report):**
  - Mastered/Developing/At-Risk and percentages become plain words for parents, with the DepEd descriptors and transmuted grades for teachers.
  - The report covers every child on the phone.
  - A replay keeps the best stars.
- **FR-04 (Find It):** words under the pictures depend on the chapter and on replays.
- **NFR-ACC-01 (mouth-shape cues):** the cue shows big while Lily models the sound twice, and the mouth pulses while the sound plays; then it moves beside the mic.
- **NFR-IND-01 (independent use):** the return greeting, the child-language wording, the Guidebook content, and the name a new player types (replacing avatar-only onboarding).
- **STD:** the new tests are `ProgressLevelTest`, `ProgressRecordCalculatorTest`, `ReportGeneratorTest`, `ReportLayoutTest`, `ReportPageRendererTest`, `BestResultTest`, `WordLabelPolicyTest`, `ReturnGreetingPolicyTest`, `ChildWordsTest`, `ParentDashboardScreenshotTest` and `WelcomeBackScreenshotTest`, plus the updated view-model and layout tests.

No database schema changed. A teacher audit of the new clips is still required before merging to main.
