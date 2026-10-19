# Memo to the adviser: hearts and streaks in PlayIT

**From:** the PlayIT team · **Date:** 2026-10-01 · **Decision needed:** spec §3.4 [confirm] ("Hearts stay in Find It"), and streaks

## Current design
- A wrong tap in Find It or Blend It costs a heart; there are 5 hearts, and one comes back after 3 correct answers in a row. At 0 hearts Lily shows a "thinking" overlay.
- Say It no longer costs hearts (card 03), because a misrecognition is the app's error.
- Streaks: a badge with a sound and voice line, and "Streak: N days" for parents.

## What the evidence says
1. **Penalties.** Negative feedback and penalties in gamified learning reduce perceived competence and motivation ([Springer 2023 meta-analysis](https://link.springer.com/article/10.1007/s11423-023-10337-7); ["dark side of gamification" review](https://www.researchgate.net/publication/326876949)). No study tests hearts on 6-year-olds directly, so this is an inference from the general findings.
2. **Feedback that teaches.** A wrong tap is a learning moment. Elaborated feedback (ES 0.49) far outperforms right/wrong (0.05) ([Van der Kleij et al. 2015](https://www.researchgate.net/publication/272923307)). A lost heart is right/wrong feedback with a cost added.
3. **Expected rewards.** Expected tangible rewards lower later interest in young children ([Lepper et al. 1973](https://www.scirp.org/reference/referencespapers?referenceid=465321); d about -0.28 to -0.40 in [Deci, Koestner & Ryan 1999](https://depts.washington.edu/techdocs/papers/deciExtrinsicRewardsAndIntrinsicMotivation99.pdf)). Verbal praise for effort and strategy does not ([Mueller & Dweck 1998](https://www.columbia.edu/cu/psychology/courses/3615/Readings/Mueller_Dweck.pdf)).
4. **Streaks.** Streak pressure is one of the manipulative designs found in 80% of preschool apps ([Radesky et al. 2022, JAMA Netw Open](https://www.ovid.com/journals/janop/fulltext/10.1001/jamanetworkopen.2022.17641)). For a 6-year-old using the app alone, a broken streak is a loss the child cannot control (it depends on the adult's schedule).

## Proposal
- **Remove hearts from Find It and Blend It.** A wrong tap names that picture's first sound and replays the target (spec Table 1, step 6), then the child taps again.
- **Stars mean mastery** ("You know /m/ now!"), earned by recall later in the session, not paid per tap.
- **No streak badge for the child.** Parents can still see days practised as neutral information.
- **Praise names the effort or strategy:** "You listened carefully!"

## Questions for the adviser
1. Do you approve removing hearts from Find It and Blend It (§3.4 [confirm])?
2. Do you approve removing the child-facing streak, keeping a neutral "days practised" for parents?
3. Should mastery stars come only from recall (FR-NEW-REC), or also from first-try success in Find It?
