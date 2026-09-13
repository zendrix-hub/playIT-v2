import os
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

def set_cell_background(cell, fill_hex):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
    tcPr.append(shd)

def set_cell_margins(cell, top=80, bottom=80, left=120, right=120):
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
        section.top_margin = Inches(0.7)
        section.bottom_margin = Inches(0.7)
        section.left_margin = Inches(0.7)
        section.right_margin = Inches(0.7)

    teal = RGBColor(43, 122, 120)     # #2B7A78
    dark_teal = RGBColor(31, 58, 61)  # #1F3A3D
    purple = RGBColor(107, 78, 113)   # #6B4E71
    charcoal = RGBColor(45, 55, 72)   # #2D3748

    # Title & Subtitle
    p_title = doc.add_paragraph()
    r_title = p_title.add_run("PlayIT: Offline-First Gamified Early Literacy Mobile Application")
    r_title.font.name = 'Calibri'
    r_title.font.size = Pt(18)
    r_title.font.bold = True
    r_title.font.color.rgb = dark_teal
    p_title.paragraph_format.space_after = Pt(2)

    p_sub = doc.add_paragraph()
    r_sub = p_sub.add_run("MVP Validation Framework & Instrument Specification (UPA Model — Weeks 1–2 Deliverable)")
    r_sub.font.name = 'Calibri'
    r_sub.font.size = Pt(12)
    r_sub.font.bold = True
    r_sub.font.color.rgb = purple
    p_sub.paragraph_format.space_after = Pt(6)

    # Metadata Box
    tbl_meta = doc.add_table(rows=1, cols=1)
    tbl_meta.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_meta.autofit = False
    tbl_meta.columns[0].width = Inches(7.1)
    c_meta = tbl_meta.cell(0, 0)
    set_cell_background(c_meta, "F0F7F6")
    set_cell_margins(c_meta, top=100, bottom=100, left=150, right=150)
    
    p_m = c_meta.paragraphs[0]
    p_m.paragraph_format.space_after = Pt(2)
    p_m.paragraph_format.line_spacing = 1.15
    r_m = p_m.add_run(
        "Course: IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027\n"
        "Institution: College of Computer Studies, Cebu Institute of Technology – University\n"
        "Primary Framework: UPA (Usability, Pedagogy, Accessibility) | Standard 10-Item SUS + Child Smileometer + Pre/Post Phonics Test"
    )
    r_m.font.name = 'Calibri'
    r_m.font.size = Pt(8.5)
    r_m.font.color.rgb = charcoal

    doc.add_paragraph().paragraph_format.space_after = Pt(6)

    # 1. Executive Summary & Research Rationale
    h1 = doc.add_paragraph()
    rh1 = h1.add_run("1. Executive Summary & Research Rationale")
    rh1.font.name = 'Calibri'
    rh1.font.size = Pt(13)
    rh1.font.bold = True
    rh1.font.color.rgb = dark_teal
    h1.paragraph_format.space_before = Pt(6)
    h1.paragraph_format.space_after = Pt(3)

    p1 = doc.add_paragraph()
    r1 = p1.add_run(
        "Early childhood literacy education in Philippine public schools faces persistent challenges, including high pupil-to-teacher ratios, "
        "inadequate learning materials, and the digital divide characterized by limited or unstable home internet connectivity. "
        "While the Department of Education (DepEd) emphasizes the Marungko Approach—a phono-syllabic reading technique that introduces "
        "letters based on phonemic frequency rather than alphabetical order—many learners lack access to interactive, individualized phonetic practice at home. "
        "PlayIT addresses this gap as a 100% offline-first Android mobile application. It pairs the pedagogical structure of the Marungko Approach "
        "with modern offline speech recognition (Vosk), engaging gamification, and diagnostic parental telemetry.\n\n"
        "The primary objective of the Weeks 1–2 MVP Validation is formative and diagnostic: (1) evaluate pediatric usability and child task flow; "
        "(2) quantify empirical reading gains through structured pre/post assessments; (3) secure expert validation from DepEd reading specialists "
        "regarding the 26-letter English Marungko adaptation; and (4) verify offline software stability across target mobile devices before feature lock."
    )
    r1.font.name = 'Calibri'
    r1.font.size = Pt(9.5)
    r1.font.color.rgb = charcoal
    p1.paragraph_format.space_after = Pt(6)

    # 2. Theoretical Evaluation Framework: The UPA Model
    h2 = doc.add_paragraph()
    rh2 = h2.add_run("2. Theoretical Evaluation Framework: The UPA Model")
    rh2.font.name = 'Calibri'
    rh2.font.size = Pt(13)
    rh2.font.bold = True
    rh2.font.color.rgb = dark_teal
    h2.paragraph_format.space_before = Pt(6)
    h2.paragraph_format.space_after = Pt(3)

    p2 = doc.add_paragraph()
    r2 = p2.add_run(
        "In educational technology for young learners, generic UI heuristics alone are insufficient. PlayIT adopts the UPA Framework "
        "(Usability, Pedagogy, Accessibility) as its primary overarching evaluation architecture across four distinct stakeholder cohorts:"
    )
    r2.font.name = 'Calibri'
    r2.font.size = Pt(9.5)
    r2.font.color.rgb = charcoal
    p2.paragraph_format.space_after = Pt(4)

    upa_points = [
        ("Usability (U): ", "Evaluates navigation ease, interaction efficiency, and user satisfaction. Operationalized via the Child Smileometer (3-point visual scale — Read & MacFarlane, 2006) for young learners, the standardized 10-Item System Usability Scale (SUS — Brooke, 1996) for parents and teachers, and observational task timings (time-on-task, vocal latency)."),
        ("Pedagogy (P): ", "Evaluates curriculum alignment, phonetic accuracy, and reading efficacy. Operationalized via the DepEd Teacher Pedagogy Checklist (7 Likert items validating Marungko sequence, phoneme modeling, and CVC decodability) and the Child Pre/Post Phonics Assessment measuring empirical letter sound and word-blending score gains."),
        ("Accessibility (A): ", "Evaluates physical ergonomics and socioeconomic equity. Operationalized via Pediatric Touch-Target Standards (≥64dp buttons for motor control), high-contrast visual cues (WCAG 2.1 AA), audio-visual dual-coding, and 100% Offline Zero-Data Operability with zero ads or tracking.")
    ]
    for bold_txt, norm_txt in upa_points:
        p_b = doc.add_paragraph(style='List Bullet')
        rb = p_b.add_run(bold_txt)
        rb.font.bold = True
        rb.font.color.rgb = teal
        rb.font.size = Pt(9)
        rn = p_b.add_run(norm_txt)
        rn.font.size = Pt(9)
        rn.font.color.rgb = charcoal
        p_b.paragraph_format.space_after = Pt(2)

    doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # 3. Curriculum Scope
    h3 = doc.add_paragraph()
    rh3 = h3.add_run("3. Curriculum Scope: 26-Letter Marungko English Adaptation & 33 CVC Matrix")
    rh3.font.name = 'Calibri'
    rh3.font.size = Pt(13)
    rh3.font.bold = True
    rh3.font.color.rgb = dark_teal
    h3.paragraph_format.space_before = Pt(6)
    h3.paragraph_format.space_after = Pt(3)

    p3 = doc.add_paragraph()
    r3 = p3.add_run(
        "PlayIT adapts the 28-letter Filipino Marungko sequence to 26 letters (A–Z) for Grade 1 English phonics. "
        "The Spanish-derived grapheme Ñ does not exist in English. The digraph NG (/ŋ/) is a phonogram rather than a single letter; "
        "introducing it as an isolated grapheme creates phonological confusion for beginner readers. "
        "The 26 letters are organized into 7 progressive chapters with 33 decodable CVC Blend It words:"
    )
    r3.font.name = 'Calibri'
    r3.font.size = Pt(9.5)
    r3.font.color.rgb = charcoal
    p3.paragraph_format.space_after = Pt(5)

    headers_c = ["Chapter", "Target Letters", "Count", "Thematic Biome", "Canonical Blend It Words (CVC Validated & Seeded in DB)"]
    c_data = [
        ["Chapter 1", "m, s, a, i", "4", "Guava Greenery", "SAM, SIS, AIM (3 words — Group 1 accepted exception)"],
        ["Chapter 2", "o, b, e, u", "4", "Mango Orchard", "BUS, SUB, MOM, BEE, BIB"],
        ["Chapter 3", "t, k, l, y", "4", "Chocolate Hills", "BAT, MAT, KIT, TOY, BOY"],
        ["Chapter 4", "n, g, p", "3", "Palm Valley", "PIG, PAN, BUG, PIN, NAP"],
        ["Chapter 5", "r, d, h, w", "4", "Rice Terraces", "DOG, HAT, HEN, BED, WEB"],
        ["Chapter 6", "c, f, j", "3", "Coral Reef", "CAT, FAN, CAP, CUP, JAM"],
        ["Chapter 7", "q, v, x, z", "4", "Mount Pulag Summit", "VAN, BOX, FOX, ZOO, QUIZ"],
        ["Total", "26 Letters", "26", "7 Biomes", "33 Decodable CVC Target Words (100% Seeded in Room DB v3)"]
    ]
    t_curr = doc.add_table(rows=len(c_data)+1, cols=5)
    t_curr.alignment = WD_TABLE_ALIGNMENT.CENTER
    col_widths = [Inches(1.0), Inches(1.1), Inches(0.6), Inches(1.4), Inches(3.0)]

    for j, h in enumerate(headers_c):
        cell = t_curr.cell(0, j)
        cell.width = col_widths[j]
        set_cell_background(cell, "2B7A78")
        set_cell_margins(cell, top=60, bottom=60, left=80, right=80)
        p = cell.paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        r = p.add_run(h)
        r.font.name = 'Calibri'
        r.font.size = Pt(8.5)
        r.font.bold = True
        r.font.color.rgb = RGBColor(255, 255, 255)

    for i, row in enumerate(c_data):
        bg = "F7FAFC" if i % 2 == 0 else "FFFFFF"
        if i == len(c_data) - 1:
            bg = "EDF2F7"
        for j, val in enumerate(row):
            cell = t_curr.cell(i+1, j)
            cell.width = col_widths[j]
            set_cell_background(cell, bg)
            set_cell_margins(cell, top=50, bottom=50, left=80, right=80)
            p = cell.paragraphs[0]
            if j in (0, 2):
                p.alignment = WD_ALIGN_PARAGRAPH.CENTER
            else:
                p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            r = p.add_run(val)
            r.font.name = 'Calibri'
            r.font.size = Pt(8)
            if i == len(c_data) - 1 or j == 0:
                r.font.bold = True
                r.font.color.rgb = dark_teal
            else:
                r.font.color.rgb = charcoal

    doc.add_page_break()

    # 4. SMART Evaluation Objectives
    h4 = doc.add_paragraph()
    rh4 = h4.add_run("4. SMART Evaluation Objectives & Measurable Metrics")
    rh4.font.name = 'Calibri'
    rh4.font.size = Pt(13)
    rh4.font.bold = True
    rh4.font.color.rgb = dark_teal
    h4.paragraph_format.space_before = Pt(6)
    h4.paragraph_format.space_after = Pt(3)

    smart_rows = [
        ["Dimension", "Project Objective", "Evaluation Metric & Instrument", "Target Threshold", "Cohort"],
        ["Specific (S)", "Verify child learning & task completion across core sublevels.", "Task completion rate; 1st-pass ASR vocal accuracy; time-on-task.", "≥ 85% task completion without help; ≥ 75% ASR first-pass accuracy.", "Early Learners (N=12)"],
        ["Measurable (M)", "Quantify usability & caregiver acceptance with standard scales.", "10-Item SUS score; Child Smileometer; TAM constructs (PU, BI).", "Mean SUS ≥ 75.0 (Grade B+); Smileometer ≥ 2.6/3.0; TAM PU ≥ 4.2/5.0.", "Parents & Teachers (N=15)"],
        ["Achievable (A)", "Validate curriculum alignment & measure empirical learning gain.", "Teacher Pedagogy Checklist (7 items); Child Pre/Post Phonics Assessment.", "100% agreement (≥ 4.0/5.0); Statistically significant gain (p < 0.05).", "DepEd Teachers (N=5) + Learners"],
        ["Relevant (R)", "Ensure 100% offline persistence, data privacy, and diagnostic speed.", "Room DB persistence audit; PDF export time; Noise gate test.", "0% data loss on force-close; PDF export ≤ 3.0s; Noise alert at >40dB.", "Technical Evaluators (N=3)"],
        ["Time-Bound (T)", "Execute validation trials, analyze data, and refactor SRS/SDD.", "Milestone schedule mapped to IT411 Capstone 2 calendar.", "Protocol: Sept 13; Field Trials: Sept 18; Refactored SRS/SDD: Sept 19.", "Research Team (Total N=30)"]
    ]
    t_sm = doc.add_table(rows=len(smart_rows), cols=5)
    t_sm.alignment = WD_TABLE_ALIGNMENT.CENTER
    sm_widths = [Inches(0.9), Inches(1.6), Inches(1.8), Inches(1.8), Inches(1.0)]
    for i, row in enumerate(smart_rows):
        is_hdr = (i == 0)
        bg = "6B4E71" if is_hdr else ("F7FAFC" if i % 2 == 1 else "FFFFFF")
        for j, val in enumerate(row):
            cell = t_sm.cell(i, j)
            cell.width = sm_widths[j]
            set_cell_background(cell, bg)
            set_cell_margins(cell, top=50, bottom=50, left=80, right=80)
            p = cell.paragraphs[0]
            r = p.add_run(val)
            r.font.name = 'Calibri'
            r.font.size = Pt(8)
            if is_hdr:
                r.font.bold = True
                r.font.color.rgb = RGBColor(255, 255, 255)
            else:
                if j == 0:
                    r.font.bold = True
                    r.font.color.rgb = dark_teal
                else:
                    r.font.color.rgb = charcoal

    doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # 5. Stakeholder Mapping & Questionnaire Item Cross-Walk
    h5 = doc.add_paragraph()
    rh5 = h5.add_run("5. Stakeholder Mapping & Questionnaire Item Cross-Walk (UPA Model)")
    rh5.font.name = 'Calibri'
    rh5.font.size = Pt(13)
    rh5.font.bold = True
    rh5.font.color.rgb = dark_teal
    h5.paragraph_format.space_before = Pt(6)
    h5.paragraph_format.space_after = Pt(3)

    items_data = [
        ["Code", "Evaluation Criterion / Question Item", "UPA Construct", "Target Respondent", "Scale"],
        ["PED-01", "Letter progression strictly follows DepEd Marungko frequency approach.", "Pedagogy: Sequence", "Teachers / SMEs", "1–5 Likert"],
        ["PED-02", "Hear It delivers clear, natural phoneme sound modeling.", "Pedagogy: Audio Model", "Teachers / SMEs", "1–5 Likert"],
        ["PED-03", "Say It uses age-appropriate example words (Mouse, Sun).", "Pedagogy: Vocalization", "Teachers / SMEs", "1–5 Likert"],
        ["PED-04", "Find It uses culturally familiar, unambiguous illustrations.", "Pedagogy: Visual Discrim.", "Teachers / SMEs", "1–5 Likert"],
        ["PED-05", "Blend It uses decodable CVC words respecting cumulative letters.", "Pedagogy: Blending", "Teachers / SMEs", "1–5 Likert"],
        ["PED-06", "Excluding NG and Ñ for 26-letter English phonics is pedagogically sound.", "Pedagogy: Scope Validity", "Teachers / SMEs", "1–5 Likert"],
        ["PED-07", "Non-punitive gamification (hearts/stars) reinforces mastery resilience.", "Pedagogy: Motivation", "Teachers / SMEs", "1–5 Likert"],
        ["PRE-POST", "Child phoneme identification and CVC blending diagnostic assessment.", "Pedagogy: Learning Gain", "Early Learners (Tested)", "0–10 Score Delta"],
        ["SUS 1–10", "Standard 10-Item System Usability Scale (Brooke, 1996).", "Usability: Standard SUS", "Parents & Teachers", "1–5 Likert (0–100)"],
        ["SMILEY", "Child Smileometer affective rating (Sad=1, Neutral=2, Happy=3).", "Usability: Affective", "Early Learners", "3-pt Visual Scale"],
        ["OBS 1–5", "Observational task completion & time-on-task (Map, Hear, Say, Find, Blend).", "Usability: Efficiency", "Child (Observed)", "3-pt + Seconds"],
        ["ACC-01", "Pediatric touch targets (≥64dp) prevent miss-taps for small hands.", "Accessibility: Ergonomics", "Parents / Teachers", "1–5 Likert"],
        ["ACC-02", "Audio-visual dual-coding enables pre-readers to navigate independently.", "Accessibility: Dual-Coding", "Parents / Teachers", "1–5 Likert"],
        ["ACC-03", "High-contrast UI colors and large text meet WCAG 2.1 AA standards.", "Accessibility: Contrast", "Parents / Teachers", "1–5 Likert"],
        ["ACC-04", "100% offline access ensures equal opportunity with zero mobile data cost.", "Accessibility: Equity", "Parents / Guardians", "1–5 Likert"],
        ["ACC-05", "Ad-free, zero in-app purchase environment guarantees child safety & privacy.", "Accessibility: Safety", "Parents / Guardians", "1–5 Likert"],
        ["SYS 1–3", "Technical verification: ASR latency ≤0.5s, 60 FPS UI, Room DB integrity.", "Technical: ISO 25010", "Technical Evaluators", "Pass / Fail / ms"],
    ]
    t_items = doc.add_table(rows=len(items_data), cols=5)
    t_items.alignment = WD_TABLE_ALIGNMENT.CENTER
    item_widths = [Inches(0.8), Inches(2.6), Inches(1.4), Inches(1.3), Inches(1.0)]
    for i, row in enumerate(items_data):
        is_hdr = (i == 0)
        bg = "2B7A78" if is_hdr else ("F7FAFC" if i % 2 == 1 else "FFFFFF")
        for j, val in enumerate(row):
            cell = t_items.cell(i, j)
            cell.width = item_widths[j]
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
                if j == 0:
                    r.font.bold = True
                    r.font.color.rgb = dark_teal
                else:
                    r.font.color.rgb = charcoal

    doc.add_page_break()

    # 6. Detailed Operationalized Instruments (UPA Model)
    h6 = doc.add_paragraph()
    rh6 = h6.add_run("6. Detailed Operationalized Instruments (UPA Model)")
    rh6.font.name = 'Calibri'
    rh6.font.size = Pt(13)
    rh6.font.bold = True
    rh6.font.color.rgb = dark_teal
    h6.paragraph_format.space_before = Pt(6)
    h6.paragraph_format.space_after = Pt(3)

    # Part A: Pedagogy
    h6a = doc.add_paragraph()
    rh6a = h6a.add_run("Part A: Pedagogy (P) — DepEd Teacher Checklist & Child Pre/Post Test")
    rh6a.font.name = 'Calibri'
    rh6a.font.size = Pt(11)
    rh6a.font.bold = True
    rh6a.font.color.rgb = purple
    h6a.paragraph_format.space_before = Pt(4)
    h6a.paragraph_format.space_after = Pt(2)

    doc.add_paragraph("1. DepEd Teacher & SME Phonics Checklist (5-Point Likert: 1=Strongly Disagree to 5=Strongly Agree):", style='Heading 3')
    ped_full = [
        ("PED-01: Sequential Phonics Progression", "The letter progression strictly follows the sequential DepEd Marungko frequency-based approach."),
        ("PED-02: Phonemic Audio Quality", "Hear It delivers accurate, natural, and clear phoneme sound modeling suitable for beginner Filipino readers."),
        ("PED-03: Vocal Production Practice", "Say It uses age-appropriate example words (e.g., Mouse, Sun) that encourage verbal phonics production without frustration."),
        ("PED-04: Visual Picture Discrimination", "Find It uses illustrations that are culturally familiar, unambiguous, and easily recognizable to Filipino children."),
        ("PED-05: Word Blending Feasibility", "Blend It uses decodable CVC words (e.g., MOM, KIT, PIG, HEN, CAP, WEB) respecting cumulative letter availability."),
        ("PED-06: Curriculum Scope Validity", "Excluding NG (digraph) and Ñ (Spanish loan) to focus on 26 English letters is pedagogically sound for Grade 1."),
        ("PED-07: Non-Punitive Gamification", "Hearts, stars, and gentle retry states support positive learning resilience without cognitive overload.")
    ]
    for title, desc in ped_full:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run(f"[{title[:6]}] ").bold = True
        p.add_run(f"{title[8:]}: ").bold = True
        p.add_run(desc)
        p.paragraph_format.space_after = Pt(2)

    doc.add_paragraph("2. Child Empirical Pre/Post Phonics Diagnostic Assessment Protocol:", style='Heading 3')
    p_prepost = doc.add_paragraph()
    p_prepost.add_run("Administered before the child interacts with PlayIT (Pre-Test) and immediately following gameplay (Post-Test):")
    p_prepost.paragraph_format.space_after = Pt(3)

    test_data = [
        ["Sub-Test", "Diagnostic Stimulus Items", "Instruction & Administration Protocol", "Scoring Rubric"],
        ["Phoneme Sound Recognition", "Letter flashcards: M, S, A, I, B", 'Show card: "What sound does this letter make?" (Prompt /m/, /s/)', "1 pt per correct sound (Max: 5 pts)"],
        ["Phonetic Picture Match", "Picture cards: Mouse, Sun, Apple", 'Show 3 images: "Which picture starts with the /m/ sound?"', "1 pt per correct match (Max: 3 pts)"],
        ["CVC Word Blending", "Word cards: SAM, BUS", 'Show word: "Can you sound out and read this word out loud?"', "1 pt per blended word (Max: 2 pts)"],
        ["Total Diagnostic Score", "10 Total Phonics Items", "Calculate empirical score gain: Delta = Post-Test Score minus Pre-Test Score", "Total Score: 0 to 10 Points"],
    ]
    t_test = doc.add_table(rows=len(test_data), cols=4)
    t_test.alignment = WD_TABLE_ALIGNMENT.CENTER
    test_widths = [Inches(1.5), Inches(1.6), Inches(2.5), Inches(1.5)]
    for i, row in enumerate(test_data):
        is_hdr = (i == 0)
        is_tot = (i == len(test_data) - 1)
        bg = "6B4E71" if is_hdr else ("EDF2F7" if is_tot else ("F7FAFC" if i % 2 == 1 else "FFFFFF"))
        for j, val in enumerate(row):
            cell = t_test.cell(i, j)
            cell.width = test_widths[j]
            set_cell_background(cell, bg)
            set_cell_margins(cell, top=50, bottom=50, left=70, right=70)
            p = cell.paragraphs[0]
            r = p.add_run(val)
            r.font.name = 'Calibri'
            r.font.size = Pt(8)
            if is_hdr:
                r.font.bold = True
                r.font.color.rgb = RGBColor(255, 255, 255)
            elif is_tot:
                r.font.bold = True
                r.font.color.rgb = dark_teal
            else:
                r.font.color.rgb = charcoal

    doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # Part B: Usability
    h6b = doc.add_paragraph()
    rh6b = h6b.add_run("Part B: Usability (U) — Standard 10-Item SUS & Child Smileometer")
    rh6b.font.name = 'Calibri'
    rh6b.font.size = Pt(11)
    rh6b.font.bold = True
    rh6b.font.color.rgb = purple
    h6b.paragraph_format.space_before = Pt(4)
    h6b.paragraph_format.space_after = Pt(2)

    doc.add_paragraph("1. Child Smileometer (Read & MacFarlane, 2006):", style='Heading 3')
    p_sm = doc.add_paragraph()
    p_sm.add_run('Show the 3 mascot emotion cards immediately post-session: "How did you feel playing with Lily today?" [1 = Sad/Difficult, 2 = Neutral/Okay, 3 = Happy/Fun]. Target: Mean Score ≥ 2.6 / 3.0.')
    p_sm.paragraph_format.space_after = Pt(3)

    doc.add_paragraph("2. Parent & Teacher System Usability Scale (Brooke, 1996):", style='Heading 3')
    sus_full = [
        ("SUS-01", "I think that I would like to use PlayIT frequently for my child's/students' reading practice."),
        ("SUS-02", "I found the application unnecessarily complex to understand or operate. [Reverse-Scored]"),
        ("SUS-03", "I thought the application was easy to use and navigate."),
        ("SUS-04", "I think that I would need the support of a technical person to be able to use this app. [Reverse-Scored]"),
        ("SUS-05", "I found the various functions in this system (Hear, Say, Find, Blend, Dashboard) were well integrated."),
        ("SUS-06", "I thought there was too much inconsistency in the interface, audio, or navigation. [Reverse-Scored]"),
        ("SUS-07", "I would imagine that most children and parents would learn to use this system very quickly."),
        ("SUS-08", "I found the application very cumbersome or awkward to use. [Reverse-Scored]"),
        ("SUS-09", "I felt very confident using the application and guiding my child through lessons."),
        ("SUS-10", "I needed to learn a lot of things before I could get going with this application. [Reverse-Scored]")
    ]
    for code, text in sus_full:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run(f"[{code}] ").bold = True
        p.add_run(text)
        p.paragraph_format.space_after = Pt(1.5)

    p_sus_formula = doc.add_paragraph()
    r_form = p_sus_formula.add_run(
        "*Scoring Formula: For positive items (1,3,5,7,9), Score = Scale - 1. For reverse items (2,4,6,8,10), Score = 5 - Scale. "
        "Composite SUS = (Sum of Scores) x 2.5 (Normalized 0–100 scale; Target: Mean ≥ 75.0 / Grade B+)."
    )
    r_form.font.italic = True
    r_form.font.size = Pt(8)
    r_form.font.color.rgb = RGBColor(113, 128, 150)

    doc.add_page_break()

    # Part C: Accessibility
    h6c = doc.add_paragraph()
    rh6c = h6c.add_run("Part C: Accessibility (A) — Pediatric Ergonomics & Offline Equity")
    rh6c.font.name = 'Calibri'
    rh6c.font.size = Pt(11)
    rh6c.font.bold = True
    rh6c.font.color.rgb = purple
    h6c.paragraph_format.space_before = Pt(4)
    h6c.paragraph_format.space_after = Pt(2)

    acc_full = [
        ("ACC-01: Pediatric Touch Targets", "The interactive buttons, letter cards, and microphone icon are large enough (≥64dp) for small child fingers to tap comfortably without accidental miss-taps."),
        ("ACC-02: Non-Verbal Audio Dual-Coding", "Spoken voice instructions and visual animations allow young, non-reading children to understand tasks without needing to read written on-screen text."),
        ("ACC-03: High Contrast & Readability", "High-contrast UI colors and large typography satisfy WCAG 2.1 AA standards, ensuring readability under indoor lighting and outdoor glare."),
        ("ACC-04: 100% Offline Access & Equity", "Operating 100% offline without requiring internet, WiFi, or mobile data credits provides equitable, cost-free learning opportunity for low-resource households."),
        ("ACC-05: Child Safety & Privacy", "The total absence of third-party advertisements, external web links, and in-app purchases ensures a secure, distraction-free environment for young learners.")
    ]
    for title, desc in acc_full:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run(f"[{title[:6]}] ").bold = True
        p.add_run(f"{title[8:]}: ").bold = True
        p.add_run(desc)
        p.paragraph_format.space_after = Pt(2)

    # Part D: Observational Rubric
    h6d = doc.add_paragraph()
    rh6d = h6d.add_run("Part D: Child Observational Task Rubric & Time-on-Task (Observer Rubric)")
    rh6d.font.name = 'Calibri'
    rh6d.font.size = Pt(11)
    rh6d.font.bold = True
    rh6d.font.color.rgb = purple
    h6d.paragraph_format.space_before = Pt(4)
    h6d.paragraph_format.space_after = Pt(2)

    obs_full = [
        ("OBS-01: Level Map Entry", "Child independently taps active glowing node. Stopwatch: Time from app launch to node tap (seconds)."),
        ("OBS-02: Hear It Audio Engagement", "Child listens to phoneme sound modeling (/m/ for Mouse) and repeats sound. Rated: High, Neutral, Distracted."),
        ("OBS-03: Say It Microphone Vocalization", "Child presses mic button and produces verbal phoneme response. Stopwatch: Mic tap to ASR result (seconds)."),
        ("OBS-04: Find It Picture Discrimination", "Child discriminates target pictures from distractors. Rated: 3/3 on first try, 1-2 mistakes, or needed help."),
        ("OBS-05: Blend It CVC Word Assembly", "Child arranges letter tiles into correct CVC word (SAM). Stopwatch: Display to submission (seconds).")
    ]
    for title, desc in obs_full:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run(f"[{title[:6]}] ").bold = True
        p.add_run(f"{title[8:]}: ").bold = True
        p.add_run(desc)
        p.paragraph_format.space_after = Pt(2)

    doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # 7. Validation Protocol
    h7 = doc.add_paragraph()
    rh7 = h7.add_run("7. Validation Testing Protocol & Field Administration Guidelines")
    rh7.font.name = 'Calibri'
    rh7.font.size = Pt(13)
    rh7.font.bold = True
    rh7.font.color.rgb = dark_teal
    h7.paragraph_format.space_before = Pt(6)
    h7.paragraph_format.space_after = Pt(3)

    protocol_steps = [
        ("Step 1: Test Environment & Device Provisioning", "5 Android test devices (tablets and smartphones running Android 8.0+) are pre-loaded with playit-debug.apk. The testing space is verified to have ambient noise ≤ 40dB using the app's built-in noise threshold indicator."),
        ("Step 2: Informed Consent & Demographic Intake", "Parents and teachers complete the digital consent form (Q1-Q2) and demographic profile (Q3-Q5) before any testing begins. Child verbal assent is confirmed."),
        ("Step 3: Diagnostic Pre-Test (2 Minutes)", "The observer administers the 10-item baseline assessment (Phoneme Flashcards M, S, A, I, B and Word Cards SAM, BUS) to record pre-intervention reading knowledge."),
        ("Step 4: Observational Gameplay Session (15 Minutes)", "The child completes Letter Node 1 (Hear It → Say It → Find It) and the Chapter 1 Blend It challenge. The observer records OBS-01 to OBS-05 timings with a stopwatch."),
        ("Step 5: Diagnostic Post-Test & Affective Feedback", "Immediately post-play, the observer re-tests the phonics items to compute the learning gain delta, followed by the 3-point Smileometer face rating."),
        ("Step 6: Caregiver SUS & Teacher Survey", "Parents and teachers complete the 10-item SUS questionnaire and accessibility checklist via Google Forms synced to the master Google Sheet.")
    ]
    for title, desc in protocol_steps:
        p = doc.add_paragraph(style='List Bullet')
        p.add_run(f"{title}: ").bold = True
        p.add_run(desc)
        p.paragraph_format.space_after = Pt(2)

    doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # 8. Thesis Chapter 4 Integration
    h8 = doc.add_paragraph()
    rh8 = h8.add_run("8. Thesis Chapter 4 Statistical Integration & Refactoring Roadmap")
    rh8.font.name = 'Calibri'
    rh8.font.size = Pt(13)
    rh8.font.bold = True
    rh8.font.color.rgb = dark_teal
    h8.paragraph_format.space_before = Pt(6)
    h8.paragraph_format.space_after = Pt(3)

    p8 = doc.add_paragraph()
    p8.add_run(
        "The empirical data gathered from the 30-respondent validation cohort will directly substantiate Thesis Chapter 4 "
        "(Results and Discussion) and drive the Week 3 Requirements Refactoring:\n\n"
        "• Inferential Phonics Gain Analysis: Pre-test and post-test scores will be evaluated via a Paired Samples t-test "
        "(or Wilcoxon signed-rank test if normality is violated) to determine whether PlayIT yields statistically significant reading gains (alpha = 0.05).\n"
        "• Caregiver Usability Benchmarking: SUS survey responses will be scored using Brooke's normalized algorithm to generate composite usability "
        "ratings, benchmarked against the standard Bangor et al. (2008) curved grading scale (Target: ≥ 75.0 / Grade B+).\n"
        "• Software Quality Verification: Offline Vosk latency (SYS-01 ≤ 0.5s), 60 FPS animation stability (SYS-02), and SQLite/Room transactional "
        "integrity (SYS-03, 0% data loss) will be verified across test device logs.\n"
        "• Week 3 Document Refactoring: Findings will be synthesized into the SRS v3.0 Requirements Traceability Matrix (RTM) "
        "(officially incorporating FR-13 Blend It and FR-14 Multi-Profile) and the SDD v2.0 Architecture Specification due on September 19, 2026."
    )
    p8.paragraph_format.space_after = Pt(6)

    return doc

if __name__ == '__main__':
    doc = create_document()
    out_root = 'playIT_MVP_Validation_Framework_Editable.docx'
    doc.save(out_root)
    print(f"Editable DOCX generated at: {out_root}")

    out_pkg = 'docs/validation-package/playIT_MVP_Validation_Framework_Editable.docx'
    try:
        doc.save(out_pkg)
        print(f"Editable DOCX also updated at: {out_pkg}")
    except PermissionError:
        print(f"Notice: '{out_pkg}' is currently open in Microsoft Word. Saved updated version at '{out_root}'.")

