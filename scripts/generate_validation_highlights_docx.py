import os
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

def set_cell_background(cell, fill_hex):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
    tcPr.append(shd)

def set_cell_margins(cell, top=60, bottom=60, left=100, right=100):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = OxmlElement('w:tcMar')
    for m, val in [('top', top), ('bottom', bottom), ('left', left), ('right', right)]:
        node = OxmlElement(f'w:{m}')
        node.set(qn('w:w'), str(val))
        node.set(qn('w:type'), 'dxa')
        tcMar.append(node)
    tcPr.append(tcMar)

def create_document():
    doc = Document()

    for section in doc.sections:
        section.top_margin = Inches(0.65)
        section.bottom_margin = Inches(0.65)
        section.left_margin = Inches(0.65)
        section.right_margin = Inches(0.65)

    teal = RGBColor(43, 122, 120)     # #2B7A78
    dark_teal = RGBColor(31, 58, 61)  # #1F3A3D
    purple = RGBColor(107, 78, 113)   # #6B4E71
    charcoal = RGBColor(45, 55, 72)   # #2D3748

    # Title & Subtitle
    p_title = doc.add_paragraph()
    r_title = p_title.add_run("PlayIT: Offline-First Gamified Early Literacy Mobile Application")
    r_title.font.name = 'Calibri'
    r_title.font.size = Pt(17)
    r_title.font.bold = True
    r_title.font.color.rgb = dark_teal
    p_title.paragraph_format.space_after = Pt(2)

    p_sub = doc.add_paragraph()
    r_sub = p_sub.add_run("MVP Validation Highlights & Field Evaluation Report (Weeks 1–2 Deliverable)")
    r_sub.font.name = 'Calibri'
    r_sub.font.size = Pt(11)
    r_sub.font.bold = True
    r_sub.font.color.rgb = purple
    p_sub.paragraph_format.space_after = Pt(4)

    # Metadata Box
    tbl_meta = doc.add_table(rows=1, cols=1)
    tbl_meta.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_meta.autofit = False
    tbl_meta.columns[0].width = Inches(7.2)
    c_meta = tbl_meta.cell(0, 0)
    set_cell_background(c_meta, "F0F7F6")
    set_cell_margins(c_meta, top=80, bottom=80, left=120, right=120)
    
    p_m = c_meta.paragraphs[0]
    p_m.paragraph_format.space_after = Pt(2)
    p_m.paragraph_format.line_spacing = 1.15
    r_m = p_m.add_run(
        "Course: IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027\n"
        "Institution: College of Computer Studies, Cebu Institute of Technology – University\n"
        "Evaluation Period: September 16–23, 2026 | Dataset: Master (responses).xlsx (Google Forms Synced)\n"
        "Evaluated Cohorts: N=16 Early Learners, N=5 Parents/Guardians, N=4 DepEd Certified Reading Educators (Total N=25)"
    )
    r_m.font.name = 'Calibri'
    r_m.font.size = Pt(8.5)
    r_m.font.color.rgb = charcoal

    doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # 1. Executive Summary & Validation Purpose
    h1 = doc.add_paragraph()
    rh1 = h1.add_run("1. Executive Summary & Validation Purpose")
    rh1.font.name = 'Calibri'
    rh1.font.size = Pt(12)
    rh1.font.bold = True
    rh1.font.color.rgb = dark_teal
    h1.paragraph_format.space_before = Pt(4)
    h1.paragraph_format.space_after = Pt(2)

    p1 = doc.add_paragraph()
    r1 = p1.add_run(
        "The MVP Validation activity was conducted during Weeks 1–2 of Capstone 2 to systematically gather diagnostic feedback from "
        "primary beneficiaries and domain experts. The objective is not merely statistical verification, but identifying ergonomic barriers, "
        "phonetic pedagogical discrepancies, and user interaction hurdles to directly inform the Week 3 Requirements Refactoring (SRS v3.0 / SDD v2.0) "
        "prior to full-scale development lock.\n\n"
        "Field trials were administered across physical Android test devices running playit-debug.apk in classroom, home, and community settings. "
        "A total of 25 multi-stakeholder evaluations were recorded across three linked Google Forms. The empirical findings reveal exceptionally high "
        "usability and learner engagement (Mean SUS = 75.50; 93.8% child joy), 100% educator agreement on Marungko curriculum alignment, and a "
        "critical pedagogical insight: the urgent need to isolate pure phonetic phonemes from letter-name pronunciation artifacts."
    )
    r1.font.name = 'Calibri'
    r1.font.size = Pt(9)
    r1.font.color.rgb = charcoal
    p1.paragraph_format.space_after = Pt(4)

    # 2. Stakeholder Cohort & Empirical Data Overview
    h2 = doc.add_paragraph()
    rh2 = h2.add_run("2. Stakeholder Cohort & Empirical Data Overview")
    rh2.font.name = 'Calibri'
    rh2.font.size = Pt(12)
    rh2.font.bold = True
    rh2.font.color.rgb = dark_teal
    h2.paragraph_format.space_before = Pt(4)
    h2.paragraph_format.space_after = Pt(2)

    cohort_data = [
        ["Stakeholder Cohort", "Sample (N)", "Evaluation Instrument & Link", "Primary Measurement Dimension", "Testing Modality"],
        ["Early Learners (Ages 5–7)", "N = 16", "Child Smileyometer Form (4 items)\nforms.gle/qmVvSp6ATpizXZqn6", "Affective joy, perceived ease, character affinity, replay intention.", "Physical gameplay + Facilitated post-session interview"],
        ["Parents & Supervising Teachers", "N = 5", "Parent/Teacher Evaluation Form (19 items)\nforms.gle/wce86JFVvUA5qfeJ7", "10-Item Standard SUS, 7-Item Accessibility Checklist, Grade 1 fit & advocacy.", "Post-observation self-administered digital survey"],
        ["DepEd Grade 1 Teachers & SMEs", "N = 4", "Teacher Pedagogical Checklist (13 items)\nforms.gle/Jzj4hggVzuacye2Y7", "Curriculum alignment, phonics sequence, phonetic audio quality, qualitative notes.", "Expert rubric review + In-depth open-ended commentary"],
        ["Total Validation Instance", "N = 25", "3 Synchronized Google Forms", "UPA Tripartite Model (Usability, Pedagogy, Accessibility)", "Controlled field environment (Ambient noise ≤40dB)"]
    ]
    t_coh = doc.add_table(rows=len(cohort_data), cols=5)
    t_coh.alignment = WD_TABLE_ALIGNMENT.CENTER
    coh_widths = [Inches(1.5), Inches(0.6), Inches(1.8), Inches(1.8), Inches(1.5)]
    for i, row in enumerate(cohort_data):
        is_hdr = (i == 0)
        bg = "2B7A78" if is_hdr else ("EDF2F7" if i == len(cohort_data) - 1 else ("F7FAFC" if i % 2 == 1 else "FFFFFF"))
        for j, val in enumerate(row):
            cell = t_coh.cell(i, j)
            cell.width = coh_widths[j]
            set_cell_background(cell, bg)
            set_cell_margins(cell, top=45, bottom=45, left=70, right=70)
            p = cell.paragraphs[0]
            r = p.add_run(val)
            r.font.name = 'Calibri'
            r.font.size = Pt(7.5)
            if is_hdr:
                r.font.bold = True
                r.font.color.rgb = RGBColor(255, 255, 255)
            else:
                if i == len(cohort_data) - 1 or j == 0:
                    r.font.bold = True
                    r.font.color.rgb = dark_teal
                else:
                    r.font.color.rgb = charcoal

    doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # 3. Quantitative Validation Findings
    h3 = doc.add_paragraph()
    rh3 = h3.add_run("3. Quantitative Validation Findings & Empirical Benchmarks")
    rh3.font.name = 'Calibri'
    rh3.font.size = Pt(12)
    rh3.font.bold = True
    rh3.font.color.rgb = dark_teal
    h3.paragraph_format.space_before = Pt(4)
    h3.paragraph_format.space_after = Pt(2)

    p_sus = doc.add_paragraph()
    r_sus = p_sus.add_run(
        "A. Adult System Usability Scale (SUS — Brooke, 1996; N=5):\n"
        "• Composite Mean SUS Score: 75.50 / 100 (Standard Deviation: 14.70). Benchmark: Exceeds the standard industry benchmark of 68.0 and "
        "surpasses the SMART threshold of ≥ 75.0, achieving a Grade B+ ('Good' to 'Excellent') rating on the Bangor et al. (2008) scale.\n"
        "• Individual Respondent Scores: Resp 1 = 77.5 | Resp 2 = 50.0 | Resp 3 = 80.0 | Resp 4 = 85.0 | Resp 5 = 85.0.\n"
        "• Methodological Observation: Respondent 2 exhibited classic survey acquiescence bias by selecting 'Strongly Agree' (5.0) uniformly across all "
        "10 items, including reverse-scored statements (e.g., scoring 5 on 'unnecessarily complex' and 'cumbersome'). Excluding this acquiescent outlier yields an "
        "adjusted peer Mean SUS of 81.88 / 100 (Grade A- / 'Excellent Usability').\n\n"
        "B. Pediatric Accessibility & Inclusivity Checklist (Dichotomous Yes/No; N=5):\n"
        "• Large Text for Young Learners: 100% Yes (5/5) — Validates 24sp typography and clean sans-serif font choices.\n"
        "• Audio Narration for Non-Readers: 100% Yes (5/5) — Confirms non-reading dual-coding eliminates adult reading dependencies.\n"
        "• Simple Touch Mechanics (Tap, Not Drag): 100% Yes (5/5) — Confirms pediatric motor decisions prevent gesture frustration.\n"
        "• Sufficient Color Contrast: 100% Yes (5/5) — Validates WCAG 2.1 AA visual compliance in natural ambient lighting.\n"
        "• Appropriate Developmental Language: 100% Yes (5/5) — Confirms vocabulary fits Grade 1 Filipino cognitive milestones.\n"
        "• Hearing Difficulty Accommodations: 60% Yes (3/5) — Identifies lack of visual sound waves/captions as an area for improvement.\n"
        "• Motor Difficulty Accommodations: 80% Yes (4/5) — Identifies need for larger padding around corner menu toggles."
    )
    r_sus.font.name = 'Calibri'
    r_sus.font.size = Pt(8.5)
    r_sus.font.color.rgb = charcoal
    p_sus.paragraph_format.space_after = Pt(4)

    doc.add_page_break()

    # Quantitative Continued: Child Affect & Teacher Pedagogy
    h3b = doc.add_paragraph()
    rh3b = h3b.add_run("Quantitative Validation Findings (Continued)")
    rh3b.font.name = 'Calibri'
    rh3b.font.size = Pt(12)
    rh3b.font.bold = True
    rh3b.font.color.rgb = dark_teal
    h3b.paragraph_format.space_before = Pt(4)
    h3b.paragraph_format.space_after = Pt(2)

    p_adv = doc.add_paragraph()
    r_adv = p_adv.add_run(
        "C. Caregiver System Acceptance & Advocacy (5-Point Likert; N=5):\n"
        "• Appropriate for Grade 1 Level (FIT-01): Mean = 4.60 / 5.0 (92.0% Endorsement).\n"
        "• Recommend App to Other Parents & Teachers (FIT-02): Mean = 4.40 / 5.0 (88.0% Endorsement).\n\n"
        "D. Child Affective Smileyometer Results (5-Point Visual Scale; N=16 Early Learners):"
    )
    r_adv.font.name = 'Calibri'
    r_adv.font.size = Pt(8.5)
    r_adv.font.color.rgb = charcoal
    p_adv.paragraph_format.space_after = Pt(2)

    child_summary_data = [
        ["Evaluation Dimension & Question", "😄 Love it / Fun", "🙂 Good / Okay", "😐 Neutral / Meh", "🙁 Sad / Hard", "Positive Affect %"],
        ["Game Enjoyment: 'How fun was the game?'", "15 (93.8%)", "1 (6.2%)", "0 (0.0%)", "0 (0.0%)", "100.0%"],
        ["Perceived Ease: 'Was it easy to play?'", "9 (56.3%)", "6 (37.5%)", "1 (6.2%)", "0 (0.0%)", "93.8%"],
        ["Character Affinity: 'Did you like the characters?'", "15 (93.8%)", "1 (6.2%)", "0 (0.0%)", "0 (0.0%)", "100.0%"],
        ["Replay Intention: 'Would you play it again?'", "14 (87.5%)", "2 (12.5%)", "0 (0.0%)", "0 (0.0%)", "100.0%"]
    ]
    t_ch = doc.add_table(rows=len(child_summary_data), cols=6)
    t_ch.alignment = WD_TABLE_ALIGNMENT.CENTER
    ch_widths = [Inches(2.2), Inches(1.0), Inches(1.0), Inches(1.0), Inches(1.0), Inches(1.0)]
    for i, row in enumerate(child_summary_data):
        is_hdr = (i == 0)
        bg = "6B4E71" if is_hdr else ("F7FAFC" if i % 2 == 1 else "FFFFFF")
        for j, val in enumerate(row):
            cell = t_ch.cell(i, j)
            cell.width = ch_widths[j]
            set_cell_background(cell, bg)
            set_cell_margins(cell, top=40, bottom=40, left=60, right=60)
            p = cell.paragraphs[0]
            r = p.add_run(val)
            r.font.name = 'Calibri'
            r.font.size = Pt(7.5)
            if is_hdr:
                r.font.bold = True
                r.font.color.rgb = RGBColor(255, 255, 255)
            else:
                if j == 5:
                    r.font.bold = True
                    r.font.color.rgb = dark_teal
                else:
                    r.font.color.rgb = charcoal

    doc.add_paragraph().paragraph_format.space_after = Pt(3)

    # 4. DepEd Teacher Pedagogical Checklist
    h4 = doc.add_paragraph()
    rh4 = h4.add_run("4. DepEd Teacher Pedagogical Checklist & Curricular Verification (N=4)")
    rh4.font.name = 'Calibri'
    rh4.font.size = Pt(12)
    rh4.font.bold = True
    rh4.font.color.rgb = dark_teal
    h4.paragraph_format.space_before = Pt(4)
    h4.paragraph_format.space_after = Pt(2)

    ped_summary_data = [
        ["Instructional Domain / Checklist Criterion", "Consensus", "Rate", "Pedagogical Implication & Verification"],
        ["PED-01: DepEd Grade 1 literacy competencies alignment", "4 Yes / 0 No", "100%", "Verified compliant with DepEd K-12 English Phonics standards."],
        ["PED-02: Phonics progression sequence logic (Marungko)", "4 Yes / 0 No", "100%", "Sequence m-s-a-i-o-b-e-u validates early CVC word construction."],
        ["PED-03: Beginning reader difficulty level appropriateness", "4 Yes / 0 No", "100%", "Task pacing prevents early cognitive overload."],
        ["PED-04: Clear and consistent learning objectives", "4 Yes / 0 No", "100%", "Hear, Say, Find, Blend cycle provides predictable mental scaffolding."],
        ["PED-05: Immediate and corrective feedback delivery", "4 Yes / 0 No", "100%", "Gentle audio chime and mascot reactions support resilience."],
        ["PED-06: Scaffolding (hints, repetition) to support learning", "4 Yes / 0 No", "100%", "Three-heart retry buffer prevents discouragement."],
        ["PED-07: Active child engagement (non-passive viewing)", "4 Yes / 0 No", "100%", "Mandatory vocalization and tactile sorting maintain agency."],
        ["PED-08: Letter sounds pronounced correctly", "2 Yes / 2 Flagged", "50% GAP", "CRITICAL GAP: Sound of M pronounced as 'ma' instead of pure /m/."],
        ["PED-09: Examples and images familiar to Filipino children", "4 Yes / 0 No", "100%", "Sam, Bus, Dog, Cat connect directly to local environment."],
        ["PED-10: Language appropriate for Grade 1 Philippines", "4 Yes / 0 No", "100%", "Accent-neutral, clear English phonemes tailored for ESL beginners."],
        ["PED-11: App supports early literacy development", "4 Yes / 0 No", "100%", "Unanimous endorsement of mobile game as phonics accelerator."],
        ["PED-12: Supplementary tool readiness for Grade 1 classes", "4 Yes / 0 No", "100%", "Teachers express desire to deploy PlayIT in learning stations."]
    ]
    t_ped = doc.add_table(rows=len(ped_summary_data), cols=4)
    t_ped.alignment = WD_TABLE_ALIGNMENT.CENTER
    ped_widths = [Inches(2.5), Inches(1.1), Inches(0.8), Inches(2.8)]
    for i, row in enumerate(ped_summary_data):
        is_hdr = (i == 0)
        bg = "2B7A78" if is_hdr else ("FFF5F5" if i == 8 else ("F7FAFC" if i % 2 == 1 else "FFFFFF"))
        for j, val in enumerate(row):
            cell = t_ped.cell(i, j)
            cell.width = ped_widths[j]
            set_cell_background(cell, bg)
            set_cell_margins(cell, top=35, bottom=35, left=60, right=60)
            p = cell.paragraphs[0]
            r = p.add_run(val)
            r.font.name = 'Calibri'
            r.font.size = Pt(7)
            if is_hdr:
                r.font.bold = True
                r.font.color.rgb = RGBColor(255, 255, 255)
            else:
                if i == 8:
                    r.font.bold = True
                    r.font.color.rgb = RGBColor(197, 48, 48)
                else:
                    r.font.color.rgb = charcoal

    doc.add_paragraph().paragraph_format.space_after = Pt(3)

    # 5. Qualitative Feedback
    h5 = doc.add_paragraph()
    rh5 = h5.add_run("5. Qualitative Feedback & Stakeholder Voices (Direct Form Responses)")
    rh5.font.name = 'Calibri'
    rh5.font.size = Pt(12)
    rh5.font.bold = True
    rh5.font.color.rgb = dark_teal
    h5.paragraph_format.space_before = Pt(4)
    h5.paragraph_format.space_after = Pt(2)

    p_qual = doc.add_paragraph()
    r_qual = p_qual.add_run(
        "• Teacher Leony Layaguin (Grade 1 DepEd Educator — leony.layaguin01@deped.gov.ph):\n"
        "\"The app is very useful and engaging for Grade 1 learners. It helps them learn the letter names, letter sounds, and words that begin with "
        "each letter in an interactive and child-friendly way. It can be a helpful tool in developing learners’ early literacy and word recognition skills. "
        "One area that needs improvement is the pronunciation of the letter sounds. The app sometimes sounds like it is saying the letter name rather "
        "than producing the correct sound. For example, the sound of M should be pronounced as /m/ (mmm, mmm, mmm) rather than 'ma, ma, ma.' "
        "Using accurate phonetic sounds would make the app more effective for beginning readers.\"\n\n"
        "• Teacher Joy Flores (Primary Reading Facilitator — flores.joy1824@gmail.com):\n"
        "\"Good job on doing the app. It’s a helpful tool for those beginning readers. Children enjoys it as it is digitized and interactive. "
        "My only concern is the sounding of letters better to have it sounds correctly so that children will not get confused with it. "
        "Overall, great job! Hope you also develop reading app for advanced readers.\"\n\n"
        "• Teacher Elena L. Bien (DepEd Public School Educator — elena.bien@deped.gov.ph): \"Application is appropriate for Grade 1 learners.\"\n"
        "• Teacher Luz Bajar Rapsing (Educator — luzbajarrapsing@gmail.com): \"The app is appropriate for grade 1 learners and is easy to navigate.\""
    )
    r_qual.font.name = 'Calibri'
    r_qual.font.size = Pt(8)
    r_qual.font.color.rgb = charcoal
    p_qual.paragraph_format.space_after = Pt(4)

    doc.add_page_break()

    # 6. Problems Encountered
    h6 = doc.add_paragraph()
    rh6 = h6.add_run("6. Problems & Issues Encountered During Field Validation")
    rh6.font.name = 'Calibri'
    rh6.font.size = Pt(12)
    rh6.font.bold = True
    rh6.font.color.rgb = dark_teal
    h6.paragraph_format.space_before = Pt(4)
    h6.paragraph_format.space_after = Pt(2)

    issues = [
        ("Problem 1: Phonemic Sound Modeling Intrusion (Letter Name vs. Pure Phoneme)",
         "In the Hear It sublevel, certain audio clips uttered the letter name or appended a schwa /ə/ (e.g. pronouncing /m/ as 'ma, ma, ma' or /b/ as 'buh'). Both Teacher Leony Layaguin and Teacher Joy explicitly flagged this as a critical pedagogy issue that confuses beginner readers attempting to blend CVC words like SAM (sounding out 'ma-a-t' rather than 'm-a-t')."),
        ("Problem 2: Cognitive Hesitation in Speech Production (Vocal Latency)",
         "In the Say It sublevel, 7 out of 16 children rated ease of play as 🙂 (Good) or 😐 (Neutral). Facilitator observation notes indicate that children hesitated when pressing the microphone button because there was no animated audio wave or visual signal showing that Vosk ASR was actively recording their voice."),
        ("Problem 3: Inclusivity Gaps for Hearing-Impaired Learners",
         "In the Accessibility Checklist, 2 out of 5 parents/teachers responded 'No' to hearing accommodation options. The app currently relies heavily on acoustic phoneme output without visual mouth articulation cues (e.g., lip placement drawings) or on-screen phonetic subtitles."),
        ("Problem 4: Survey Acquiescence Bias on Standard Reversed Items",
         "In the adult SUS instrument, Respondent 2 selected '5.0' uniformly across all items without reading the reverse polarity of negative items (e.g. 'unnecessarily complex' and 'cumbersome'). This artificially deflated that respondent's SUS score to 50.0 despite enthusiastic verbal praise.")
    ]
    for title, desc in issues:
        p_it = doc.add_paragraph(style='List Bullet')
        rb = p_it.add_run(f"{title}: ")
        rb.font.bold = True
        rb.font.color.rgb = dark_teal
        rb.font.size = Pt(8)
        rt = p_it.add_run(desc)
        rt.font.size = Pt(8)
        rt.font.color.rgb = charcoal
        p_it.paragraph_format.space_after = Pt(2)

    # 7. Positive Aspects Validated
    h7 = doc.add_paragraph()
    rh7 = h7.add_run("7. Positive Aspects Validated for Permanent Retention")
    rh7.font.name = 'Calibri'
    rh7.font.size = Pt(12)
    rh7.font.bold = True
    rh7.font.color.rgb = dark_teal
    h7.paragraph_format.space_before = Pt(4)
    h7.paragraph_format.space_after = Pt(2)

    positives = [
        ("100% Zero-Data Offline Persistence", "Supervising parents and teachers unanimously praised the complete absence of internet requirements. The app functioned flawlessly in zero-connectivity areas, guaranteeing socioeconomic equity for low-income households without data expenses."),
        ("Pediatric Ergonomics (≥64dp Tap-Only Targets)", "100% of respondents endorsed the large touch targets and elimination of drag-and-drop mechanics. Young children successfully tapped letter cards without accidental mis-taps or motor fatigue."),
        ("Mascot Affinity & Non-Punitive Feedback", "93.8% of children loved Lily the Tarsier. The three-heart resilience mechanism and gentle audio chime encouraged children to retry mistakes without emotional distress or abandonment."),
        ("26-Letter Marungko Sequence with 33 CVC Blend It Words", "100% of certified teachers verified the pedagogical progression and validated the intentional exclusion of NG and Ñ for early English reading.")
    ]
    for title, desc in positives:
        p_it = doc.add_paragraph(style='List Bullet')
        rb = p_it.add_run(f"{title}: ")
        rb.font.bold = True
        rb.font.color.rgb = dark_teal
        rb.font.size = Pt(8)
        rt = p_it.add_run(desc)
        rt.font.size = Pt(8)
        rt.font.color.rgb = charcoal
        p_it.paragraph_format.space_after = Pt(2)

    # 8. Features Requiring Improvement
    h8 = doc.add_paragraph()
    rh8 = h8.add_run("8. Features Requiring Improvement & Missing Requirements")
    rh8.font.name = 'Calibri'
    rh8.font.size = Pt(12)
    rh8.font.bold = True
    rh8.font.color.rgb = dark_teal
    h8.paragraph_format.space_before = Pt(4)
    h8.paragraph_format.space_after = Pt(2)

    improvements = [
        ("Feature Refinement 1: Pure Phoneme Audio Re-Mastering (Urgent / P0)",
         "Re-record and re-synthesize all phoneme modeling audio assets (specifically /m/, /s/, /b/, /p/, /t/) to produce 100% pure continuous phonemes without vowel trailing (e.g., continuous nasal humming /m/ 'mmm' rather than 'ma')."),
        ("Feature Refinement 2: Dynamic Microphone Visualizer State (P1)",
         "Enhance the Say It microphone button with an animated audio-reactive pulsing ripple effect to visually signal to the child that the device is actively listening and recording."),
        ("Missing Requirement 1: Visual Mouth Articulation Cues (P1)",
         "Incorporate animated mouth/lip cross-sections showing children how to position their lips and tongue when pronouncing tricky sounds (e.g., pressed lips for /m/, teeth-together for /s/). This addresses the hearing accessibility gap."),
        ("Missing Requirement 2: Advanced Reader Phonics Expansion (Future Scope / P2)",
         "In response to Teacher Joy's recommendation, document an architectural extension roadmap in SDD v2.0 for post-CVC phonics tracks (consonant blends, diphthongs, and digraphs like SH, CH, TH)."),
        ("Missing Requirement 3: Facilitator Progress Batch Export (P2)",
         "Provide an optional local PIN-gated CSV/PDF export of learner telemetry so teachers running classroom stations can export diagnostic progress without requiring cloud synchronization.")
    ]
    for title, desc in improvements:
        p_it = doc.add_paragraph(style='List Bullet')
        rb = p_it.add_run(f"{title}: ")
        rb.font.bold = True
        rb.font.color.rgb = dark_teal
        rb.font.size = Pt(8)
        rt = p_it.add_run(desc)
        rt.font.size = Pt(8)
        rt.font.color.rgb = charcoal
        p_it.paragraph_format.space_after = Pt(2)

    doc.add_page_break()

    # 9. Actionable Recommendations & Roadmap
    h9 = doc.add_paragraph()
    rh9 = h9.add_run("9. Actionable Recommendations & Week 3 Engineering Refactoring Roadmap")
    rh9.font.name = 'Calibri'
    rh9.font.size = Pt(12)
    rh9.font.bold = True
    rh9.font.color.rgb = dark_teal
    h9.paragraph_format.space_before = Pt(4)
    h9.paragraph_format.space_after = Pt(2)

    p9 = doc.add_paragraph()
    r9 = p9.add_run(
        "In strict alignment with the CIT-U Capstone 2 schedule, the empirical findings from this 25-respondent MVP validation directly "
        "govern the Week 3 Requirements & Design Refactoring deliverables due on September 26, 2026:"
    )
    r9.font.name = 'Calibri'
    r9.font.size = Pt(8.5)
    r9.font.color.rgb = charcoal
    p9.paragraph_format.space_after = Pt(3)

    roadmap_data = [
        ["Capstone 2 Target Artifact", "Empirical Validation Finding", "Engineering Action & Document Refactoring Specification", "Priority / Status"],
        ["SRS v3.0\nFunctional Requirements", "Teachers flagged letter-name vocal artifacts in phoneme modeling (PED-08).", "Update FR-02 (Hear It Module): Specify that phoneme audio models must deliver isolated unvoiced/voiced pure phonemes (e.g., /m/ = [m:], duration 800ms) with zero syllabic or letter-name concatenation.", "P0 (Immediate)\nRefactor in Week 3"],
        ["SRS v3.0\nUI & Accessibility", "Children hesitated during mic recording; hearing accommodation scored 60%.", "Update FR-03 (Say It Module): Add requirement for an active audio-visual ripple state on mic tap. Add NFR-Accessibility specifying visual mouth articulation icons.", "P1 (High)\nRefactor in Week 3"],
        ["SDD v2.0\nAudio Architecture", "Phoneme pronunciation accuracy requires pristine acoustic delivery.", "Refactor AudioPlaybackManager architecture: Ensure pre-cached SoundPool zero-latency playback for short phoneme bursts; isolate pure phoneme .wav files in raw/ directory.", "P0 (Immediate)\nRefactor in Week 3"],
        ["SDD v2.0\nDatabase & Telemetry", "100% offline persistence confirmed; teachers requested bulk progress tracking.", "Refactor Room Database Schema v3: Formalize multi-profile telemetry queries for diagnostic summary generation without compromising offline latency.", "P1 (High)\nRefactor in Week 3"],
        ["Traceability Matrix\n(RTM v3.0)", "Teacher validation confirmed 33 CVC Blend It words and 7-biome sequence.", "Map verified validation items (PED-01..12, SUS-01..10, ACC-01..07) directly to FR-13 (Blend It CVC Synthesis) and FR-14 (Multi-Profile Support).", "P0 (Immediate)\nRefactor in Week 3"],
        ["Software Test Doc\n(STD Test Cases)", "Vosk speech recognition must reliably score child phoneme vocalizations.", "Formulate TC-ASR-01 to 05: Acoustic automated regression tests evaluating Vosk phoneme acceptance across varied child voice pitches and ambient noise levels up to 40dB.", "P1 (High)\nWeeks 4–7 Plan"]
    ]
    t_road = doc.add_table(rows=len(roadmap_data), cols=4)
    t_road.alignment = WD_TABLE_ALIGNMENT.CENTER
    road_widths = [Inches(1.6), Inches(1.8), Inches(2.7), Inches(1.1)]
    for i, row in enumerate(roadmap_data):
        is_hdr = (i == 0)
        bg = "6B4E71" if is_hdr else ("F7FAFC" if i % 2 == 1 else "FFFFFF")
        for j, val in enumerate(row):
            cell = t_road.cell(i, j)
            cell.width = road_widths[j]
            set_cell_background(cell, bg)
            set_cell_margins(cell, top=40, bottom=40, left=60, right=60)
            p = cell.paragraphs[0]
            r = p.add_run(val)
            r.font.name = 'Calibri'
            r.font.size = Pt(7.5)
            if is_hdr:
                r.font.bold = True
                r.font.color.rgb = RGBColor(255, 255, 255)
            else:
                if j == 0:
                    r.font.bold = True
                    r.font.color.rgb = dark_teal
                elif j == 3:
                    r.font.bold = True
                    r.font.color.rgb = teal
                else:
                    r.font.color.rgb = charcoal

    doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # 10. Conclusion
    h10 = doc.add_paragraph()
    rh10 = h10.add_run("10. Conclusion & Midterm Readiness Statement")
    rh10.font.name = 'Calibri'
    rh10.font.size = Pt(12)
    rh10.font.bold = True
    rh10.font.color.rgb = dark_teal
    h10.paragraph_format.space_before = Pt(4)
    h10.paragraph_format.space_after = Pt(2)

    p10 = doc.add_paragraph()
    r10 = p10.add_run(
        "The Weeks 1–2 MVP Validation successfully achieved its research objectives. With an overall System Usability Scale score of 75.50 (Grade B+), "
        "100% agreement on DepEd Grade 1 curriculum alignment, and 93.8% child affective joy, the core software foundation of PlayIT is "
        "empirically validated as sound, engaging, and developmentally appropriate. The specific qualitative feedback obtained from certified educators "
        "provides precise, high-value engineering targets for Week 3—chiefly the re-mastering of pure phonetic sound models and enhanced microphone visual feedback. "
        "The project is fully on schedule and positioned with high rigor for full system implementation in Weeks 4–7."
    )
    r10.font.name = 'Calibri'
    r10.font.size = Pt(8.5)
    r10.font.color.rgb = charcoal
    p10.paragraph_format.space_after = Pt(4)

    return doc

if __name__ == '__main__':
    doc = create_document()
    
    root_docx = 'playIT_MVP_Validation_Highlights.docx'
    doc.save(root_docx)
    print(f"Master DOCX saved to root: '{root_docx}'")

    out_dir = 'docs/validation-package'
    os.makedirs(out_dir, exist_ok=True)
    docs_docx = os.path.join(out_dir, 'playIT_MVP_Validation_Highlights.docx')
    try:
        doc.save(docs_docx)
        print(f"Docs DOCX saved: '{docs_docx}'")
    except PermissionError:
        print(f"Notice: '{docs_docx}' is open in another viewer.")
