#!/usr/bin/env python3
"""Populates the official MVP Validation Findings & Refactoring Priorities.docx form
with complete data from the Weeks 1-2 MVP validation and Week 3 refactoring specs.
Outputs: docs/MVP_Validation_Findings_and_Refactoring_Priorities_Filled.docx
"""

import docx
from docx.shared import Pt, RGBColor
import pathlib

REPO = pathlib.Path(__file__).resolve().parents[2]
TEMPLATE_PATH = REPO / "docs" / "MVP Validation Findings & Refactoring Priorities.docx"
OUTPUT_PATH = REPO / "docs" / "MVP_Validation_Findings_and_Refactoring_Priorities_Filled.docx"

def set_run_font(run, size=10, bold=False, italic=False, color=(30, 41, 59)):
    run.font.name = "Arial"
    run.font.size = Pt(size)
    run.bold = bold
    run.italic = italic
    run.font.color.rgb = RGBColor(*color)

def add_response(p, text, italic=False):
    p.paragraph_format.space_after = Pt(4)
    run = p.add_run(f"\n{text}")
    set_run_font(run, size=9.5, italic=italic, color=(15, 23, 42))

def fill_form():
    doc = docx.Document(TEMPLATE_PATH)

    # --- Section 1: Project Information ---
    doc.paragraphs[2].text = "Team/Group Name: Group 56 — PlayIT Capstone Team"
    doc.paragraphs[3].text = "Project/System Title: PlayIT: An Offline-First Gamified Early Literacy Mobile Application Using the DepEd Marungko Approach"
    doc.paragraphs[4].text = "Program / Section: Bachelor of Science in Information Technology / IT411 G1–G8"
    doc.paragraphs[5].text = "Team Members: Zendrix Riva (Lead), J. J. Palis, K. Miel, A. S. Durano, E. S. Bien"
    doc.paragraphs[6].text = "Adviser: Prof. [Adviser Name]"
    doc.paragraphs[7].text = "System URL / MVP Link: Offline Android Application (playit-debug.apk, Package: com.playit.app, Min SDK 26)"
    doc.paragraphs[8].text = "Date of MVP Validation: September 16–23, 2026"
    doc.paragraphs[9].text = "Number of respondents/participants: Total N = 25 (16 Early Learners, 5 Caregivers/Supervising Teachers, 4 DepEd Teachers)"
    doc.paragraphs[10].text = "Types of respondents involved:"
    doc.paragraphs[11].text = "[✓] Customer: Parents / Caregivers & Supervising Teachers (N = 5)"
    doc.paragraphs[12].text = "[✓] End User: Grade 1 & Early Learners (Ages 5–7) (N = 16)"
    doc.paragraphs[13].text = "[✓] Subject Matter Expert: Certified DepEd Grade 1 Reading Teachers (N = 4)"
    doc.paragraphs[14].text = "[✓] Decision Maker: School Reading Coordinators"
    doc.paragraphs[15].text = "Other: Technical Evaluators (N = 3) for offline performance and accessibility audits"

    for i in range(2, 16):
        for run in doc.paragraphs[i].runs:
            set_run_font(run, size=9.5, bold=(i in [2,3,4,5,6,7,8,9,10]))

    # --- Section 2: MVP Validation Overview ---
    doc.paragraphs[19].text = (
        "The primary purpose of the Weeks 1–2 MVP validation was formative and diagnostic. "
        "The team evaluated whether the MVP's foundational learning loop—Hear It (phoneme modeling), "
        "Say It (speech production with on-device ASR), Find It (sound discrimination), and Blend It (CVC blending)—"
        "could achieve its intended outcomes in low-resource Philippine settings. "
        "The validation investigated pedagogical fidelity (especially phonemic accuracy), learner autonomy, "
        "speech recognition friction points, and accessibility floors to identify necessary requirements refactoring "
        "before full-scale deployment."
    )
    set_run_font(doc.paragraphs[19].runs[0], size=9.5)

    doc.paragraphs[22].text = "Framework/Model: Usability, Pedagogy, and Accessibility (UPA) Framework integrated with Brooke's (1996) SUS and Read & MacFarlane's (2006) Smileyometer."
    doc.paragraphs[23].text = "Key constructs/criteria evaluated: DepEd Grade 1 alignment, phonemic sound purity, Marungko sequence logic, child ease of use, mic hesitation, feedback latency (P90 ≤ 0.5s), caregiver usability (target SUS ≥ 75), and pediatric accessibility (≥64dp touch targets, hearing cues)."
    doc.paragraphs[24].text = "Why was this framework/model appropriate: Educational software for early literacy requires both pedagogical validity and child-appropriate interaction design. The UPA framework directly captures curriculum alignment, child developmental usability, and offline accessibility under low-resource constraints."
    for idx in [22, 23, 24]:
        set_run_font(doc.paragraphs[idx].runs[0], size=9.5)

    doc.paragraphs[27].text = "Who participated: 16 Grade 1 learners (ages 5–7), 5 caregivers/supervising teachers, 4 certified DepEd Grade 1 reading teachers, and 3 technical evaluators."
    doc.paragraphs[28].text = "How they interacted: Learners used physical Android tablets/phones running playit-debug.apk under airplane mode (zero internet connection) in quiet settings (≤40 dB ambient noise)."
    doc.paragraphs[29].text = "What activities they performed: Children completed Chapter 1 letter nodes (m, s, a, i) across Hear It, Say It, Find It, and Blend It (SAM). Teachers completed an independent 12-item curriculum and phoneme audit. Caregivers completed the 10-item SUS and 7-item accessibility checklist."
    doc.paragraphs[30].text = "How data was collected: Three synchronized Google Forms (Master responses.xlsx), structured facilitator observation logs, post-session child Smileyometer interviews, and qualitative teacher debriefs."
    for idx in [27, 28, 29, 30]:
        set_run_font(doc.paragraphs[idx].runs[0], size=9.5)

    # --- Section 3: MVP Validation Findings ---
    # Finding 1
    doc.paragraphs[35].text = (
        "Finding #1: Phonemic Audio Impurity and Added Schwas.\n"
        "Stakeholder feedback revealed that synthetic phoneme audio clips in Hear It and Say It suffered from phonetic impurity: "
        "several clips added an intrusive trailing schwa (/ə/) or sounded like letter names. Specifically, /m/ sounded like 'ma, ma, ma' or 'em' "
        "rather than a pure continuous hum [m:]."
    )
    doc.paragraphs[36].text = (
        "Evidence supporting Finding #1:\n"
        "• Pedagogical Checklist Item PED-08 ('Letter sounds pronounced correctly'): 2 of 4 DepEd teachers flagged this item as a critical defect.\n"
        "• Teacher T-1 (DepEd Reading Specialist): 'The sound of M should be pronounced as /m/ (mmm, mmm, mmm) rather than ma, ma, ma. Using accurate phonetic sounds would make the app more effective for beginning readers.'\n"
        "• Teacher T-2: 'My only concern is the sounding of letters better to have it sounds correctly so that children will not get confused with it.'"
    )
    doc.paragraphs[44].text = "Stakeholder: [✓] Subject Matter Expert (Certified DepEd Grade 1 Reading Teachers)"
    doc.paragraphs[50].text = "SMART Objective Affected: Objective O1 (Hear It) — 100% of modeled phoneme clips are pure (no intrusive vowel); playback per letter ≤ 5 s."
    doc.paragraphs[52].text = "Measurable Outcome Impacted: Accuracy and Pedagogical Effectiveness"
    doc.paragraphs[62].text = (
        "Explanation: In the Marungko synthetic phonics approach, children blend isolated letter sounds to form words. If /m/ is modeled as 'ma' "
        "and /s/ as 'sa', the child decodes SAM as 'sa-a-ma' instead of /s/-/a/-/m/. Trailing vowels directly break word blending, impairing literacy acquisition."
    )
    for idx in [35, 36, 44, 50, 52, 62]:
        set_run_font(doc.paragraphs[idx].runs[0], size=9.5)

    # Finding 2 (Paragraph 65)
    doc.paragraphs[65].text = (
        "Finding #2: Say It Microphone Hesitation and State Ambiguity.\n"
        "Learners hesitated at the microphone CTA because the screen lacked immediate, real-time visual feedback indicating when the app was listening, "
        "when speech was detected, and when processing occurred."
    )
    doc.paragraphs[66].text = (
        "Evidence supporting Finding #2:\n"
        "• Child Smileyometer Perceived Ease: 7 of 16 children (43.8%) did not choose the top smiling face. Ease was the lowest rated item.\n"
        "• Facilitator observation sheets noted repeated hesitation at the mic; children tapped the button but delayed speaking due to uncertainty.\n"
        "Stakeholder: [✓] End User (Grade 1 Learners) & Facilitators.\n"
        "SMART Objective Affected: Objective O2c (≥85% unhesitating mic turns) and Objective O2a (ASR agreement ≥80%).\n"
        "Measurable Outcome Impacted: Usability, Task Completion Rate, and Learner Autonomy.\n"
        "Explanation: Hesitation leads to clipped audio, false timeouts, and ASR rejection, creating frustration and requiring adult prompting."
    )
    set_run_font(doc.paragraphs[65].runs[0], size=9.5, bold=True)
    set_run_font(doc.paragraphs[66].runs[0], size=9.5)

    # Finding 3 (Paragraph 67)
    doc.paragraphs[67].text = (
        "Finding #3: Structural Accessibility Deficits (Hearing Accommodations & Motor Navigation).\n"
        "Caregiver ratings identified a lack of accommodations for learners with hearing difficulties, and observations noted tight touch targets on corner buttons.\n"
        "Evidence supporting Finding #3:\n"
        "• Accessibility Checklist Item ACC-06 (Hearing): Rated 3 of 5 Yes (40% negative). No visual articulation cues or sound captions.\n"
        "• Accessibility Checklist Item ACC-07 (Motor): Rated 4 of 5 Yes. Tight padding around top-bar and navigation controls.\n"
        "Stakeholder: [✓] Customer (Caregivers & Supervising Teachers).\n"
        "SMART Objective Affected: Objective O6 (Pediatric Accessibility & Architecture Compliance).\n"
        "Measurable Outcome Impacted: Usability and Inclusivity.\n"
        "Explanation: Purely auditory feedback excludes hearing-impaired learners and fails noisy classrooms. Sub-64dp touch targets create accidental misses."
    )
    set_run_font(doc.paragraphs[67].runs[0], size=9.5, bold=True)
    set_run_font(doc.paragraphs[67].runs[0], size=9.5)

    # Finding 4 (Paragraph 68)
    doc.paragraphs[68].text = (
        "Finding #4: Non-Decodable Blend It Word Bank Entries.\n"
        "Curriculum review revealed that 6 of 33 seeded Blend It words violated short-vowel rules taught in early chapters.\n"
        "Evidence: Expert linguistic audit of DatabaseModule.kt identified AIM, BEE, TOY, BOY, ZOO, and QUIZ as unlocked prematurely.\n"
        "Stakeholder: [✓] Subject Matter Expert (DepEd Teachers & Reading Curriculum Review).\n"
        "SMART Objective Affected: Objective O4 (Blend It Word Decodability — 100% decodable with taught letter-sounds).\n"
        "Measurable Outcome Impacted: Pedagogical Effectiveness and Task Success Rate.\n"
        "Explanation: Teaching irregular vowel teams in early CVC lessons contradicts systematic phonics and leads to blending failure."
    )
    set_run_font(doc.paragraphs[68].runs[0], size=9.5, bold=True)
    set_run_font(doc.paragraphs[68].runs[0], size=9.5)

    # --- Section 4: Potential Refactoring Priorities ---
    # Priority 1 (p 74..111)
    doc.paragraphs[74].text = "Refactoring Priority #1: Pure Phoneme Acoustic Modeling (FR-02)"
    doc.paragraphs[75].text = "1. Validation Finding: Finding #1 (Phonemic Audio Impurity; PED-08)"
    doc.paragraphs[77].text = "2. What needs to be improved: Re-master all 26 letter-sound clips as acoustically pure phonemes: held continuous sounds (≈800ms) and clipped short stops without trailing vowels."
    doc.paragraphs[79].text = "3. Type of refactoring: [✓] Functional requirement (FR-02), [✓] Data/Audio assets, [✓] Multi-stage audio pipeline"
    doc.paragraphs[90].text = "4. Current MVP approach: Single-letter Edge-TTS synthesis producing letter names ('ma', 'es', 'bee')."
    doc.paragraphs[92].text = "5. Proposed refactored approach: Multi-stage pipeline combining Kokoro-82M neural TTS and Chatterbox-Turbo voice cloning, governed by strict SHA-256 release manifests and teacher audit."
    doc.paragraphs[94].text = "6. Why necessary: Eliminates schwa intrusion so children can blend sounds into words without extra syllables."
    doc.paragraphs[96].text = "7. Expected improvement: 100% of clips rated 'Pure' by at least 3 of 4 DepEd teachers; 0 trailing schwas."
    doc.paragraphs[98].text = "8. How measured: Teacher Phoneme and Content Audit (Instrument A, Part 1)."
    doc.paragraphs[108].text = "9. Priority: 🔴 High (P0 Urgent) — Essential to achieving SMART Objective O1."

    for idx in [74, 75, 77, 79, 90, 92, 94, 96, 98, 108]:
        set_run_font(doc.paragraphs[idx].runs[0], size=9.5, bold=(idx in [74, 108]))

    # --- Section 5: Matrix (already filled in table 0) ---
    # --- Section 6: Non-priorities (already filled in table 1) ---
    doc.paragraphs[121].text = "Explanation of Scope Discipline: The team focused refactoring strictly on issues directly impacting the project's SMART objectives and pedagogical validity. Routine bugs, visual polish, and advanced phonics features were appropriately separated from core architectural refactoring."
    set_run_font(doc.paragraphs[121].runs[0], size=9.5, italic=True)

    # --- Section 7: Final Team Reflection ---
    doc.paragraphs[125].text = "1. What is the single most important thing your team learned from the MVP validation?"
    doc.paragraphs[126].text = (
        "Pedagogical accuracy must strictly govern technology implementation in early literacy applications. "
        "In adult software, slight acoustic variations in synthetic speech are inconsequential; for Grade 1 readers, "
        "even a fraction of a second of trailing schwa (/m/ as 'ma') breaks the mechanics of synthetic phonics blending. "
        "An engaging and stable application is educationally ineffective if it reinforces phonetic errors."
    )
    doc.paragraphs[127].text = "2. What is the most important change your team should make based on this learning?"
    doc.paragraphs[128].text = (
        "The complete restructuring of our acoustic modeling and tutoring architecture: re-mastering all 26 letter-sounds into pure phonemes, "
        "separating pure sounds from key-word carrier phrases, implementing an encouraging 4-state mic visualizer without penalty hearts, "
        "and validating every asset through certified DepEd teacher release gates."
    )
    doc.paragraphs[129].text = "3. How will this change help your project achieve its SMART objectives more effectively?"
    doc.paragraphs[130].text = (
        "By modeling acoustically pure phonemes and eliminating mic hesitation, learners internalize accurate phonological representations. "
        "This directly satisfies Objective O1 (100% pure phonemes), Objective O2 (unprompted speech production), and Objective O4 (successful blending), "
        "ensuring measurable reading gains in the Capstone 2 evaluation."
    )
    doc.paragraphs[131].text = "4. What requirements and/or design documents will need to be updated as a result?"
    doc.paragraphs[132].text = "[✓] SRS v3.0: Updated FR-02 (pure phonemes), FR-03 (mic states/ladder), FR-13 (word bank), FR-14 (profiles), FR-NEW-TEL (telemetry), NFR-ACC-01/02."
    doc.paragraphs[133].text = "[✓] SDD v2.0: Audio Subsystem (SoundPool + AudioResolver), Tutoring FSM (TutorPolicy), Room schema v3/v4, and adaptive LessonScaffold."
    doc.paragraphs[134].text = "[✓] SPMP v2.0: 9-WBS master schedule, Kokoro/Chatterbox compute allocations, tablet hardware protocols, and 8-point Risk Register (R1–R8)."
    doc.paragraphs[135].text = "[✓] RTM v3.0: Full 16-row bidirectional traceability linking empirical findings F-01..F-16 to SRS, SDD, and STD test cases."

    doc.paragraphs[139].text = "Team Lead: Zendrix Riva                                                    Date: October 10, 2026"

    for idx in [126, 128, 130, 132, 133, 134, 135, 139]:
        set_run_font(doc.paragraphs[idx].runs[0], size=9.5)

    doc.save(OUTPUT_PATH)
    print(f"Complete document successfully populated and saved at: {OUTPUT_PATH}")

if __name__ == "__main__":
    fill_form()
