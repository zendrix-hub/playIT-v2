# Proposal: learning-UX principles for PlayIT, gaps, and card roadmap

**Status:** proposal (the cards it names are written separately in `docs/tasks/`). **Date:** 2026-10-01
**Basis:** literature review (sources below) and a read-only map of the current UI (`app/src/main/java/com/playit/app/presentation/`).

## 1. What helps a pre-reader actually learn (ranked by expected impact)

| # | Principle | Evidence |
|---|---|---|
| 1 | After an error, **re-model the right answer**, then retry. Never just "wrong". | Computer-based feedback meta-analysis: elaborated ES 0.49, correct answer only 0.32, right/wrong only 0.05 ([Van der Kleij et al. 2015](https://www.researchgate.net/publication/272923307)). Dutch Grade 1 ASR trial: explicit re-modelling beat "try again" ([Bai et al. 2025](https://link.springer.com/article/10.1007/s10758-025-09860-8)). |
| 2 | **Spaced review** of earlier letters inside new lessons. | Spaced 20.8% vs massed 7.5% retained after 5 weeks ([Goossens et al.](https://www.researchgate.net/publication/303833330)). |
| 3 | **Adapt difficulty**; keep early errors rare, then fade support (e.g. Find It starts with 2 choices). | Errorless methods matched or beat trial-and-error for letter discrimination ([ERIC ED039955](https://eric.ed.gov/?id=ED039955); [PubMed 39467041](https://pubmed.ncbi.nlm.nih.gov/39467041/)). Personalised levelling was shared by the two best Global Learning XPRIZE apps ([Huntington et al. 2023](https://www.researchgate.net/publication/369686280)). |
| 4 | **Show the letter** whenever its sound is practised; picture mnemonics with the letter drawn inside the picture. | Phonemic awareness taught with letters: 0.67 vs 0.38 without ([Ehri et al., NRP](https://ila.onlinelibrary.wiley.com/doi/10.1598/RRQ.36.3.2)). Embedded mnemonics: faster learning, less confusion ([Shmidman & Ehri 2010](https://www.semanticscholar.org/paper/706d4e0b8c221a67194101dee633351e638acc28)). |
| 5 | **Lenient ASR**: when unsure, re-model or accept, and never block progress on speech. | Child ASR is weak on single syllables and consonants ([Learning Agency](https://the-learning-agency.com/guides-resources/closing-the-child-speech-recognition-gap-evidence-limitations-and-paths-forward/)). False rejects damage trust ([Bai et al.](https://arxiv.org/pdf/2306.04190)). Project LISTEN: feedback should never be wrongly certain ([Mostow & Aist 1999](https://www.cs.cmu.edu/~listen/pdfs/CALICO_article_final_manuscript.pdf)). |
| 6 | **No reading required**: every instruction spoken and demonstrated; same layout every time; tap targets of about 2 cm. | XPRIZE best apps shared reduced language demands, a consistent task structure and support for autonomous learning ([Huntington et al. 2023](https://www.researchgate.net/publication/369686280)); touch guidance ([Soni et al. IDC'19](https://init.cise.ufl.edu/wp-content/uploads/sites/378/2019/04/TIDRC-Framework-soni-et-al-IDC19-final.pdf)). |
| 7 | **Sparse learning screens**; celebrations only after the answer. | Decorated rooms meant more off-task time and less learning ([Fisher et al. 2014](https://www.sciencedaily.com/releases/2014/05/140527100646.htm)). Seductive details hurt retention ([Rey 2012](https://www.researchgate.net/publication/257690772)). E-book hotspots and games hurt comprehension ([Bus et al. 2015](https://www.semanticscholar.org/paper/412bb4c923311a46a5024e37ddefc57b24747872)). |
| 8 | **No penalties**; stars show mastery; praise effort and strategy, not intelligence. | Expected tangible rewards undermine intrinsic motivation, d about -0.28 to -0.40 ([Deci, Koestner & Ryan 1999](https://depts.washington.edu/techdocs/papers/deciExtrinsicRewardsAndIntrinsicMotivation99.pdf); [Lepper et al. 1973](https://www.scirp.org/reference/referencespapers?referenceid=465321)). Intelligence praise reduces persistence ([Mueller & Dweck 1998](https://www.columbia.edu/cu/psychology/courses/3615/Readings/Mueller_Dweck.pdf)). Penalties in gamified learning ([Springer 2023](https://link.springer.com/article/10.1007/s11423-023-10337-7)). No study tests hearts on 6-year-olds directly, so this is an inference. |
| 9 | **About 10-15 min sessions**, ending on a success with a predictable "all done". | A visible plan let children stop on their own 93% of the time ([Hiniker et al. IDC'17](https://dl.acm.org/doi/10.1145/3078072.3079752)). Effective app RCTs used short daily sessions ([onebillion](https://onebillion.org/impact/evidence/)). Attention-span figures are not peer-reviewed. |
| 10 | **No streaks, guilt or countdowns.** | 80% of preschool apps used manipulative design ([Radesky et al. 2022](https://www.ovid.com/journals/janop/fulltext/10.1001/jamanetworkopen.2022.17641)). |

Context: offline adaptive phonics games show the largest gains for the lowest-achieving quartile ([GraphoLearn RCT, India](https://onlinelibrary.wiley.com/doi/full/10.1111/jcal.12592)). A Filipino reading tutor reached a 5% false-alarm rate in a small pilot ([academia.edu](https://www.academia.edu/35655518)).

## 2. Gaps in PlayIT today

| Area | Today (code) | Principle | Spec |
|---|---|---|---|
| Error feedback | Say It re-models (card 03). Find It and Blend It: buzz plus a heart lost | 1 | FR-03 (Say It only) |
| Spaced review and recall | None (`SayItViewModel` TODO FR-NEW-REC) | 2 | FR-NEW-REC |
| Adaptive difficulty | None; fixed Find It grid | 3 | — |
| Reading needed | Text-only buttons ("Next: Find It", "Continue to Map", "Complete Lesson"); silent banners and status lines; unvoiced map pop-up (`NodeActionPopupDialog.kt`); typed name (`NamePromptScreen.kt`, `isNameValid`); no idle re-prompt; no first-use demo | 6 | NFR-IND-01 |
| Penalties | Hearts in Find It and Blend It (`HeartManager`) | 8 | §3.4 keeps hearts in Find It **[confirm]** |
| Streaks | Badge, voice line, parent "Streak: Nd" (`StreakTracker`) | 10 | — |
| Session length | No timer | 9 | NFR-SES-01 [proposed] |
| Layout | Decorated, varies by screen | 7 | — |
| Bug | `BlendItCompleteViewModel.kt:45` hardcodes `totalHeartsLost = 0`, so stars ignore mistakes | — | — |

## 3. Card roadmap
Decisions of 2026-10-01: no-reading pass first; hearts and streaks go to the adviser (`2026-10-01-adviser-memo-hearts-streaks.md`); idle re-prompt 10 s; avatar-only onboarding; agy may run two cards a night with disjoint files. Card numbers follow the order cards are written; the numbers in `2026-09-29-next-cards-and-story-hook.md` were provisional.

| Card | What | Principles | Status |
|---|---|---|---|
| 06 | Say It feedback text bound to the error type; debug transcript log and overlay | 1, 5 | written 2026-10-01 |
| 07 | No-reading pass 1: 10 s idle re-prompt, voiced primary buttons, replay on every screen | 6 | written 2026-10-01 |
| 07b | Avatar-only onboarding; parent rename in the Parent Zone | 6 | next |
| 08 | Find It re-models after a wrong tap and starts with 2 choices, growing with success; fix the Blend It stars bug | 1, 3 | later |
| 09 | Sparse, consistent learning layout; animations only after the answer | 7 | later |
| 10 | Spaced review and recall; the letter is always visible | 2, 4 | needs the held sounds |
| — | Hearts and streaks | 8, 10 | after the adviser |
| — | Session end on success | 9 | after the adviser |
