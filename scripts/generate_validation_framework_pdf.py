import os
import sys
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, HRFlowable
)
from reportlab.pdfgen import canvas

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super(NumberedCanvas, self).__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super(NumberedCanvas, self).showPage()
        super(NumberedCanvas, self).save()

    def draw_page_decorations(self, page_count):
        self.saveState()
        self.setFont('Helvetica', 8)
        self.setFillColor(colors.HexColor('#506B6E'))
        if self._pageNumber > 1:
            self.drawString(40, 755, 'PlayIT — MVP Validation Framework & Research Instrument Package (UPA Model) | IT411')
            self.setStrokeColor(colors.HexColor('#CBD5E0'))
            self.setLineWidth(0.5)
            self.line(40, 748, 572, 748)
        footer_text = f'Page {self._pageNumber} of {page_count}'
        self.drawRightString(572, 25, footer_text)
        self.drawString(40, 25, 'Cebu Institute of Technology – University | College of Computer Studies')
        self.setStrokeColor(colors.HexColor('#CBD5E0'))
        self.setLineWidth(0.5)
        self.line(40, 35, 572, 35)
        self.restoreState()

def build_pdf(filename):
    doc = SimpleDocTemplate(
        filename, pagesize=letter,
        leftMargin=40, rightMargin=40, topMargin=50, bottomMargin=42
    )
    styles = getSampleStyleSheet()
    primary_color = colors.HexColor('#1F3A3D')
    accent_color = colors.HexColor('#6B4E71')
    teal_accent = colors.HexColor('#2B7A78')
    body_color = colors.HexColor('#2D3748')

    title_style = ParagraphStyle('DocTitle', parent=styles['Heading1'], fontName='Helvetica-Bold', fontSize=15, leading=19, textColor=primary_color, spaceAfter=2)
    subtitle_style = ParagraphStyle('DocSubtitle', parent=styles['Normal'], fontName='Helvetica-Bold', fontSize=10, leading=13, textColor=accent_color, spaceAfter=4)
    meta_style = ParagraphStyle('DocMeta', parent=styles['Normal'], fontName='Helvetica', fontSize=7.5, leading=10.5, textColor=colors.HexColor('#4A5568'))
    h1_style = ParagraphStyle('Heading1_Custom', parent=styles['Heading2'], fontName='Helvetica-Bold', fontSize=10.5, leading=13.5, textColor=primary_color, spaceBefore=8, spaceAfter=3, keepWithNext=True)
    h2_style = ParagraphStyle('Heading2_Custom', parent=styles['Heading3'], fontName='Helvetica-Bold', fontSize=8.5, leading=11.5, textColor=accent_color, spaceBefore=5, spaceAfter=2, keepWithNext=True)
    body_style = ParagraphStyle('Body_Custom', parent=styles['Normal'], fontName='Helvetica', fontSize=7.5, leading=10.5, textColor=body_color, spaceAfter=3)
    bullet_style = ParagraphStyle('Bullet_Custom', parent=styles['Normal'], fontName='Helvetica', fontSize=7.2, leading=9.8, textColor=body_color, leftIndent=8, spaceAfter=1.5)

    tbl_header_style = ParagraphStyle('TblHeader', parent=styles['Normal'], fontName='Helvetica-Bold', fontSize=7, leading=9, textColor=colors.white, alignment=1)
    tbl_cell_style = ParagraphStyle('TblCell', parent=styles['Normal'], fontName='Helvetica', fontSize=6.5, leading=8.5, textColor=body_color)
    tbl_cell_bold = ParagraphStyle('TblCellBold', parent=styles['Normal'], fontName='Helvetica-Bold', fontSize=6.5, leading=8.5, textColor=primary_color)
    tbl_cell_center = ParagraphStyle('TblCellCenter', parent=styles['Normal'], fontName='Helvetica', fontSize=6.5, leading=8.5, textColor=body_color, alignment=1)

    story = []

    # ==================== PAGE 1: Executive Summary & Curriculum ====================
    story.append(Paragraph('PlayIT: Offline-First Gamified Early Literacy Mobile Application', title_style))
    story.append(Paragraph('MVP Validation Framework & Instrument Specification (UPA Model — Weeks 1–2 Deliverable)', subtitle_style))
    story.append(HRFlowable(width='100%', thickness=1.5, color=teal_accent, spaceBefore=0, spaceAfter=4))

    meta_text = (
        '<b>Course:</b> IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027<br/>'
        '<b>Institution:</b> College of Computer Studies, Cebu Institute of Technology – University<br/>'
        '<b>Primary Framework:</b> UPA (Usability, Pedagogy, Accessibility) | Standard 10-Item SUS + Child Smileometer + Pre/Post Phonics Test'
    )
    story.append(Paragraph(meta_text, meta_style))
    story.append(Spacer(1, 4))

    story.append(Paragraph('1. Executive Summary & Research Rationale', h1_style))
    p1 = (
        'Early childhood literacy education in Philippine public schools faces persistent challenges, including high pupil-to-teacher ratios, '
        'inadequate learning materials, and the digital divide characterized by limited or unstable home internet connectivity. '
        'While the Department of Education (DepEd) emphasizes the <b>Marungko Approach</b>—a phono-syllabic reading technique that introduces '
        'letters based on phonemic frequency rather than alphabetical order—many learners lack access to interactive, individualized phonetic practice at home. '
        '<b>PlayIT</b> addresses this gap as a 100% offline-first Android mobile application. It pairs the pedagogical structure of the Marungko Approach '
        'with modern offline speech recognition (Vosk), engaging gamification, and diagnostic parental telemetry.'
    )
    story.append(Paragraph(p1, body_style))
    p2 = (
        'The primary objective of the <b>Weeks 1–2 MVP Validation</b> is <b>formative and diagnostic</b>: (1) evaluate pediatric usability and child task flow; '
        '(2) quantify empirical reading gains through structured pre/post assessments; (3) secure expert validation from DepEd reading specialists '
        'regarding the 26-letter English Marungko adaptation; and (4) verify offline software stability across target mobile devices before feature lock.'
    )
    story.append(Paragraph(p2, body_style))

    story.append(Paragraph('2. Theoretical Evaluation Framework: The UPA Model', h1_style))
    story.append(Paragraph(
        'In educational technology for young learners, generic UI heuristics alone are insufficient. PlayIT adopts the <b>UPA Framework '
        '(Usability, Pedagogy, Accessibility)</b> as its primary overarching evaluation architecture across four distinct stakeholder cohorts:',
        body_style
    ))
    story.append(Paragraph(
        '• <b>Usability (U):</b> Evaluates navigation ease, interaction efficiency, and user satisfaction. Operationalized via the '
        '<b>Child Smileometer</b> (3-point visual scale — Read & MacFarlane, 2006) for young learners, the standardized <b>10-Item '
        'System Usability Scale (SUS — Brooke, 1996)</b> for parents and teachers, and observational task timings (time-on-task, vocal latency).',
        bullet_style
    ))
    story.append(Paragraph(
        '• <b>Pedagogy (P):</b> Evaluates curriculum alignment, phonetic accuracy, and reading efficacy. Operationalized via the '
        '<b>DepEd Teacher Pedagogy Checklist</b> (7 Likert items validating Marungko sequence, phoneme modeling, and CVC decodability) '
        'and the <b>Child Pre/Post Phonics Assessment</b> measuring empirical letter sound and word-blending score gains.',
        bullet_style
    ))
    story.append(Paragraph(
        '• <b>Accessibility (A):</b> Evaluates physical ergonomics and socioeconomic equity. Operationalized via <b>Pediatric Touch-Target '
        'Standards</b> (>=64dp buttons for motor control), high-contrast visual cues (WCAG 2.1 AA), audio-visual dual-coding, and '
        '<b>100% Offline Zero-Data Operability</b> with zero ads or tracking.',
        bullet_style
    ))

    story.append(Paragraph('3. Curriculum Scope: 26-Letter Marungko English Adaptation & 33 CVC Matrix', h1_style))
    story.append(Paragraph(
        'PlayIT adapts the 28-letter Filipino Marungko sequence to <b>26 letters (A–Z)</b> for Grade 1 English phonics. '
        'The Spanish-derived grapheme <b>Ñ</b> does not exist in English. The digraph <b>NG</b> (/ŋ/) is a phonogram rather than a single letter; '
        'introducing it as an isolated grapheme creates phonological confusion for beginner readers. '
        'The 26 letters are organized into <b>7 progressive chapters</b> with <b>33 decodable CVC Blend It words</b>:',
        body_style
    ))

    group_data = [
        [Paragraph('Chapter', tbl_header_style), Paragraph('Target Letters', tbl_header_style), Paragraph('Count', tbl_header_style), Paragraph('Biome Theme', tbl_header_style), Paragraph('Canonical Blend It Words (CVC Validated & Seeded in DB)', tbl_header_style)],
        [Paragraph('Chapter 1', tbl_cell_bold), Paragraph('m, s, a, i', tbl_cell_style), Paragraph('4', tbl_cell_center), Paragraph('Guava Greenery', tbl_cell_style), Paragraph('SAM, SIS, AIM  (3 words — Group 1 accepted exception)', tbl_cell_style)],
        [Paragraph('Chapter 2', tbl_cell_bold), Paragraph('o, b, e, u', tbl_cell_style), Paragraph('4', tbl_cell_center), Paragraph('Mango Orchard', tbl_cell_style), Paragraph('BUS, SUB, MOM, BEE, BIB', tbl_cell_style)],
        [Paragraph('Chapter 3', tbl_cell_bold), Paragraph('t, k, l, y', tbl_cell_style), Paragraph('4', tbl_cell_center), Paragraph('Chocolate Hills', tbl_cell_style), Paragraph('BAT, MAT, KIT, TOY, BOY', tbl_cell_style)],
        [Paragraph('Chapter 4', tbl_cell_bold), Paragraph('n, g, p', tbl_cell_style), Paragraph('3', tbl_cell_center), Paragraph('Palm Valley', tbl_cell_style), Paragraph('PIG, PAN, BUG, PIN, NAP', tbl_cell_style)],
        [Paragraph('Chapter 5', tbl_cell_bold), Paragraph('r, d, h, w', tbl_cell_style), Paragraph('4', tbl_cell_center), Paragraph('Rice Terraces', tbl_cell_style), Paragraph('DOG, HAT, HEN, BED, WEB', tbl_cell_style)],
        [Paragraph('Chapter 6', tbl_cell_bold), Paragraph('c, f, j', tbl_cell_style), Paragraph('3', tbl_cell_center), Paragraph('Coral Reef', tbl_cell_style), Paragraph('CAT, FAN, CAP, CUP, JAM', tbl_cell_style)],
        [Paragraph('Chapter 7', tbl_cell_bold), Paragraph('q, v, x, z', tbl_cell_style), Paragraph('4', tbl_cell_center), Paragraph('Mount Pulag Summit', tbl_cell_style), Paragraph('VAN, BOX, FOX, ZOO, QUIZ', tbl_cell_style)],
        [Paragraph('Total', tbl_cell_bold), Paragraph('26 Letters', tbl_cell_bold), Paragraph('26', tbl_cell_center), Paragraph('7 Biomes', tbl_cell_bold), Paragraph('33 Decodable CVC Target Words (100% Seeded in Room DB v3)', tbl_cell_bold)],
    ]
    t_groups = Table(group_data, colWidths=[60, 75, 30, 110, 257])
    t_groups.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), teal_accent),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E0')),
        ('ROWBACKGROUNDS', (0, 1), (-1, -2), [colors.HexColor('#F7FAFC'), colors.white]),
        ('BACKGROUND', (0, -1), (-1, -1), colors.HexColor('#EDF2F7')),
        ('TOPPADDING', (0, 0), (-1, -1), 2),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
    ]))
    story.append(t_groups)

    # ==================== PAGE 2: SMART Targets & Item Cross-Walk ====================
    story.append(PageBreak())

    story.append(Paragraph('4. SMART Evaluation Objectives & Measurable Metrics', h1_style))
    smart_data = [
        [Paragraph('Dimension', tbl_header_style), Paragraph('Project Objective', tbl_header_style), Paragraph('Evaluation Metric & Instrument', tbl_header_style), Paragraph('Target Threshold', tbl_header_style), Paragraph('Cohort', tbl_header_style)],
        [Paragraph('<b>Specific (S)</b>', tbl_cell_style), Paragraph('Verify child learning & task completion across core sublevels.', tbl_cell_style), Paragraph('Task completion rate; 1st-pass ASR vocal accuracy; time-on-task.', tbl_cell_style), Paragraph('>=85% task completion without help; >=75% ASR first-pass accuracy.', tbl_cell_style), Paragraph('Early Learners (N=12)', tbl_cell_style)],
        [Paragraph('<b>Measurable (M)</b>', tbl_cell_style), Paragraph('Quantify usability & caregiver acceptance with standard scales.', tbl_cell_style), Paragraph('10-Item SUS score; Child Smileometer; TAM constructs (PU, BI).', tbl_cell_style), Paragraph('Mean SUS >=75.0 (Grade B+); Smileometer >=2.6/3.0; TAM PU >=4.2/5.0.', tbl_cell_style), Paragraph('Parents & Teachers (N=15)', tbl_cell_style)],
        [Paragraph('<b>Achievable (A)</b>', tbl_cell_style), Paragraph('Validate curriculum alignment & measure empirical learning gain.', tbl_cell_style), Paragraph('Teacher Pedagogy Checklist (7 items); Child Pre/Post Phonics Assessment.', tbl_cell_style), Paragraph('100% agreement (>=4.0/5.0); Statistically significant gain (p < 0.05).', tbl_cell_style), Paragraph('DepEd Teachers (N=5) + Learners', tbl_cell_style)],
        [Paragraph('<b>Relevant (R)</b>', tbl_cell_style), Paragraph('Ensure 100% offline persistence, data privacy, and diagnostic speed.', tbl_cell_style), Paragraph('Room DB persistence audit; PDF export time; Noise gate test.', tbl_cell_style), Paragraph('0% data loss on force-close; PDF export <=3.0s; Noise alert at >40dB.', tbl_cell_style), Paragraph('Technical Evaluators (N=3)', tbl_cell_style)],
        [Paragraph('<b>Time-Bound (T)</b>', tbl_cell_style), Paragraph('Execute validation trials, analyze data, and refactor SRS/SDD.', tbl_cell_style), Paragraph('Milestone schedule mapped to IT411 Capstone 2 calendar.', tbl_cell_style), Paragraph('Protocol: Sept 13; Field Trials: Sept 18; Refactored SRS/SDD: Sept 19.', tbl_cell_style), Paragraph('Research Team (Total N=30)', tbl_cell_style)]
    ]
    t_smart = Table(smart_data, colWidths=[65, 115, 125, 137, 90])
    t_smart.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), accent_color),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'TOP'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E0')),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.HexColor('#F7FAFC'), colors.white]),
        ('TOPPADDING', (0, 0), (-1, -1), 2.5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 2.5),
    ]))
    story.append(t_smart)
    story.append(Spacer(1, 4))

    story.append(Paragraph('5. Stakeholder Mapping & Questionnaire Item Cross-Walk (UPA Model)', h1_style))
    items_data = [
        [Paragraph('Code', tbl_header_style), Paragraph('Evaluation Criterion / Question Item', tbl_header_style), Paragraph('UPA Construct', tbl_header_style), Paragraph('Target Respondent', tbl_header_style), Paragraph('Scale', tbl_header_style)],
        [Paragraph('PED-01', tbl_cell_bold), Paragraph('Letter progression strictly follows DepEd Marungko frequency approach.', tbl_cell_style), Paragraph('Pedagogy: Sequence', tbl_cell_style), Paragraph('Teachers / SMEs', tbl_cell_style), Paragraph('1–5 Likert', tbl_cell_style)],
        [Paragraph('PED-02', tbl_cell_bold), Paragraph('Hear It delivers clear, natural phoneme sound modeling.', tbl_cell_style), Paragraph('Pedagogy: Audio Model', tbl_cell_style), Paragraph('Teachers / SMEs', tbl_cell_style), Paragraph('1–5 Likert', tbl_cell_style)],
        [Paragraph('PED-03', tbl_cell_bold), Paragraph('Say It uses age-appropriate example words (Mouse, Sun).', tbl_cell_style), Paragraph('Pedagogy: Vocalization', tbl_cell_style), Paragraph('Teachers / SMEs', tbl_cell_style), Paragraph('1–5 Likert', tbl_cell_style)],
        [Paragraph('PED-04', tbl_cell_bold), Paragraph('Find It uses culturally familiar, unambiguous illustrations.', tbl_cell_style), Paragraph('Pedagogy: Visual Discrim.', tbl_cell_style), Paragraph('Teachers / SMEs', tbl_cell_style), Paragraph('1–5 Likert', tbl_cell_style)],
        [Paragraph('PED-05', tbl_cell_bold), Paragraph('Blend It uses decodable CVC words respecting cumulative letters.', tbl_cell_style), Paragraph('Pedagogy: Blending', tbl_cell_style), Paragraph('Teachers / SMEs', tbl_cell_style), Paragraph('1–5 Likert', tbl_cell_style)],
        [Paragraph('PED-06', tbl_cell_bold), Paragraph('Excluding NG and Ñ for 26-letter English phonics is pedagogically sound.', tbl_cell_style), Paragraph('Pedagogy: Scope Validity', tbl_cell_style), Paragraph('Teachers / SMEs', tbl_cell_style), Paragraph('1–5 Likert', tbl_cell_style)],
        [Paragraph('PED-07', tbl_cell_bold), Paragraph('Non-punitive gamification (hearts/stars) reinforces mastery resilience.', tbl_cell_style), Paragraph('Pedagogy: Motivation', tbl_cell_style), Paragraph('Teachers / SMEs', tbl_cell_style), Paragraph('1–5 Likert', tbl_cell_style)],
        [Paragraph('PRE-POST', tbl_cell_bold), Paragraph('Child phoneme identification and CVC blending diagnostic assessment.', tbl_cell_style), Paragraph('Pedagogy: Learning Gain', tbl_cell_style), Paragraph('Early Learners (Tested)', tbl_cell_style), Paragraph('0–10 Score Delta', tbl_cell_style)],
        [Paragraph('SUS 1–10', tbl_cell_bold), Paragraph('Standard 10-Item System Usability Scale (Brooke, 1996).', tbl_cell_style), Paragraph('Usability: Standard SUS', tbl_cell_style), Paragraph('Parents & Teachers', tbl_cell_style), Paragraph('1–5 Likert (0–100)', tbl_cell_style)],
        [Paragraph('SMILEY', tbl_cell_bold), Paragraph('Child Smileometer affective rating (Sad=1, Neutral=2, Happy=3).', tbl_cell_style), Paragraph('Usability: Affective', tbl_cell_style), Paragraph('Early Learners', tbl_cell_style), Paragraph('3-pt Visual Scale', tbl_cell_style)],
        [Paragraph('OBS 1–5', tbl_cell_bold), Paragraph('Observational task completion & time-on-task (Map, Hear, Say, Find, Blend).', tbl_cell_style), Paragraph('Usability: Efficiency', tbl_cell_style), Paragraph('Child (Observed)', tbl_cell_style), Paragraph('3-pt + Seconds', tbl_cell_style)],
        [Paragraph('ACC-01', tbl_cell_bold), Paragraph('Pediatric touch targets (>=64dp) prevent miss-taps for small hands.', tbl_cell_style), Paragraph('Accessibility: Ergonomics', tbl_cell_style), Paragraph('Parents / Teachers', tbl_cell_style), Paragraph('1–5 Likert', tbl_cell_style)],
        [Paragraph('ACC-02', tbl_cell_bold), Paragraph('Audio-visual dual-coding enables pre-readers to navigate independently.', tbl_cell_style), Paragraph('Accessibility: Dual-Coding', tbl_cell_style), Paragraph('Parents / Teachers', tbl_cell_style), Paragraph('1–5 Likert', tbl_cell_style)],
        [Paragraph('ACC-03', tbl_cell_bold), Paragraph('High-contrast UI colors and large text meet WCAG 2.1 AA standards.', tbl_cell_style), Paragraph('Accessibility: Contrast', tbl_cell_style), Paragraph('Parents / Teachers', tbl_cell_style), Paragraph('1–5 Likert', tbl_cell_style)],
        [Paragraph('ACC-04', tbl_cell_bold), Paragraph('100% offline access ensures equal opportunity with zero mobile data cost.', tbl_cell_style), Paragraph('Accessibility: Equity', tbl_cell_style), Paragraph('Parents / Guardians', tbl_cell_style), Paragraph('1–5 Likert', tbl_cell_style)],
        [Paragraph('ACC-05', tbl_cell_bold), Paragraph('Ad-free, zero in-app purchase environment guarantees child safety & privacy.', tbl_cell_style), Paragraph('Accessibility: Safety', tbl_cell_style), Paragraph('Parents / Guardians', tbl_cell_style), Paragraph('1–5 Likert', tbl_cell_style)],
        [Paragraph('SYS 1–3', tbl_cell_bold), Paragraph('Technical verification: ASR latency <=0.5s, 60 FPS UI, Room DB integrity.', tbl_cell_style), Paragraph('Technical: ISO 25010', tbl_cell_style), Paragraph('Technical Evaluators', tbl_cell_style), Paragraph('Pass / Fail / ms', tbl_cell_style)],
    ]
    t_items = Table(items_data, colWidths=[48, 195, 100, 100, 89])
    t_items.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), teal_accent),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E0')),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.HexColor('#F7FAFC'), colors.white]),
        ('TOPPADDING', (0, 0), (-1, -1), 1.5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 1.5),
    ]))
    story.append(t_items)

    # ==================== PAGE 3: Detailed Instruments Part A & B ====================
    story.append(PageBreak())

    story.append(Paragraph('6. Detailed Operationalized Instruments (UPA Model)', h1_style))

    # Part A: Pedagogy
    story.append(Paragraph('Part A: Pedagogy (P) — DepEd Teacher Checklist & Child Pre/Post Test', h2_style))
    story.append(Paragraph(
        '1. <b>DepEd Teacher & SME Phonics Checklist (5-Point Likert: 1=Strongly Disagree to 5=Strongly Agree):</b>',
        body_style
    ))
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
        story.append(Paragraph(f'• <b>[{title[:6]}]</b> <b>{title[8:]}:</b> {desc}', bullet_style))

    story.append(Spacer(1, 3))
    story.append(Paragraph(
        '2. <b>Child Empirical Pre/Post Phonics Diagnostic Assessment Protocol:</b><br/>'
        'Administered before the child interacts with PlayIT (Pre-Test) and immediately following gameplay (Post-Test):',
        body_style
    ))

    test_data = [
        [Paragraph('Sub-Test', tbl_header_style), Paragraph('Diagnostic Stimulus Items', tbl_header_style), Paragraph('Instruction & Administration Protocol', tbl_header_style), Paragraph('Scoring Rubric', tbl_header_style)],
        [Paragraph('<b>Phoneme Sound Recognition</b>', tbl_cell_style), Paragraph('Letter flashcards: M, S, A, I, B', tbl_cell_style), Paragraph('Show card: "What sound does this letter make?" (Prompt /m/, /s/)', tbl_cell_style), Paragraph('1 pt per correct sound (Max: 5 pts)', tbl_cell_style)],
        [Paragraph('<b>Phonetic Picture Match</b>', tbl_cell_style), Paragraph('Picture cards: Mouse, Sun, Apple', tbl_cell_style), Paragraph('Show 3 images: "Which picture starts with the /m/ sound?"', tbl_cell_style), Paragraph('1 pt per correct match (Max: 3 pts)', tbl_cell_style)],
        [Paragraph('<b>CVC Word Blending</b>', tbl_cell_style), Paragraph('Word cards: SAM, BUS', tbl_cell_style), Paragraph('Show word: "Can you sound out and read this word out loud?"', tbl_cell_style), Paragraph('1 pt per blended word (Max: 2 pts)', tbl_cell_style)],
        [Paragraph('<b>Total Diagnostic Score</b>', tbl_cell_bold), Paragraph('10 Total Phonics Items', tbl_cell_bold), Paragraph('Calculate empirical score gain: Delta = Post-Test Score minus Pre-Test Score', tbl_cell_bold), Paragraph('<b>Total Score: 0 to 10 Points</b>', tbl_cell_bold)],
    ]
    t_test = Table(test_data, colWidths=[110, 115, 187, 120])
    t_test.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), accent_color),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E0')),
        ('ROWBACKGROUNDS', (0, 1), (-1, -2), [colors.HexColor('#F7FAFC'), colors.white]),
        ('BACKGROUND', (0, -1), (-1, -1), colors.HexColor('#EDF2F7')),
        ('TOPPADDING', (0, 0), (-1, -1), 2),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
    ]))
    story.append(t_test)
    story.append(Spacer(1, 4))

    # Part B: Usability
    story.append(Paragraph('Part B: Usability (U) — Standard 10-Item SUS & Child Smileometer', h2_style))
    story.append(Paragraph(
        '1. <b>Child Smileometer (Read & MacFarlane, 2006):</b> Show the 3 mascot emotion cards immediately post-session: '
        '<i>"How did you feel playing with Lily today?"</i> [1 = Sad/Difficult, 2 = Neutral/Okay, 3 = Happy/Fun]. '
        'Target: Mean Score >= 2.6 / 3.0 (Positive Affective Response).',
        body_style
    ))
    story.append(Paragraph(
        '2. <b>Parent & Teacher System Usability Scale (Brooke, 1996):</b> Standard 10 items (1=Strongly Disagree to 5=Strongly Agree):',
        body_style
    ))
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
        story.append(Paragraph(f'• <b>[{code}]</b> {text}', bullet_style))

    story.append(Paragraph(
        '<i>*Scoring Formula: For positive items (1,3,5,7,9), Score = Scale - 1. For reverse items (2,4,6,8,10), Score = 5 - Scale. '
        'Composite SUS = (Sum of Scores) x 2.5 (Normalized 0–100 scale; Target: Mean >= 75.0 / Grade B+).</i>',
        ParagraphStyle('NoteSUS', parent=styles['Normal'], fontName='Helvetica-Oblique', fontSize=6.8, leading=8.8, textColor=colors.HexColor('#718096'), spaceBefore=2, spaceAfter=2)
    ))

    # ==================== PAGE 4: Part C Accessibility, Testing Protocol & Chapter 4 ====================
    story.append(PageBreak())

    story.append(Paragraph('Part C: Accessibility (A) — Pediatric Ergonomics & Offline Equity', h2_style))
    story.append(Paragraph(
        'Evaluated by parents and educators on a 1–5 scale to verify inclusive design and socioeconomic equity:',
        body_style
    ))
    acc_full = [
        ("ACC-01: Pediatric Touch Targets", "The interactive buttons, letter cards, and microphone icon are large enough (>=64dp) for small child fingers to tap comfortably without accidental miss-taps."),
        ("ACC-02: Non-Verbal Audio Dual-Coding", "Spoken voice instructions and visual animations allow young, non-reading children to understand tasks without needing to read written on-screen text."),
        ("ACC-03: High Contrast & Readability", "High-contrast UI colors and large typography satisfy WCAG 2.1 AA standards, ensuring readability under indoor lighting and outdoor glare."),
        ("ACC-04: 100% Offline Access & Equity", "Operating 100% offline without requiring internet, WiFi, or mobile data credits provides equitable, cost-free learning opportunity for low-resource households."),
        ("ACC-05: Child Safety & Privacy", "The total absence of third-party advertisements, external web links, and in-app purchases ensures a secure, distraction-free environment for young learners.")
    ]
    for title, desc in acc_full:
        story.append(Paragraph(f'• <b>[{title[:6]}]</b> <b>{title[8:]}:</b> {desc}', bullet_style))

    story.append(Spacer(1, 4))
    story.append(Paragraph('Part D: Child Observational Task Rubric & Time-on-Task (Observer Rubric)', h2_style))
    story.append(Paragraph(
        'The researcher or parent observer records objective behavioral performance across Letter Node 1 (M) and Chapter 1 Blend It:',
        body_style
    ))
    obs_full = [
        ("OBS-01: Level Map Entry", "Child independently taps active glowing node. Stopwatch: Time from app launch to node tap (seconds)."),
        ("OBS-02: Hear It Audio Engagement", "Child listens to phoneme sound modeling (/m/ for Mouse) and repeats sound. Rated: High, Neutral, Distracted."),
        ("OBS-03: Say It Microphone Vocalization", "Child presses mic button and produces verbal phoneme response. Stopwatch: Mic tap to ASR result (seconds)."),
        ("OBS-04: Find It Picture Discrimination", "Child discriminates target pictures from distractors. Rated: 3/3 on first try, 1-2 mistakes, or needed help."),
        ("OBS-05: Blend It CVC Word Assembly", "Child arranges letter tiles into correct CVC word (SAM). Stopwatch: Display to submission (seconds).")
    ]
    for title, desc in obs_full:
        story.append(Paragraph(f'• <b>[{title[:6]}]</b> <b>{title[8:]}:</b> {desc}', bullet_style))

    story.append(Spacer(1, 4))
    story.append(Paragraph('7. Validation Testing Protocol & Field Administration Guidelines', h1_style))
    protocol_steps = [
        ("Step 1: Test Environment & Device Provisioning", "5 Android test devices (tablets and smartphones running Android 8.0+) are pre-loaded with playit-debug.apk. The testing space is verified to have ambient noise <= 40dB using the app's built-in noise threshold indicator."),
        ("Step 2: Informed Consent & Demographic Intake", "Parents and teachers complete the digital consent form (Q1-Q2) and demographic profile (Q3-Q5) before any testing begins. Child verbal assent is confirmed."),
        ("Step 3: Diagnostic Pre-Test (2 Minutes)", "The observer administers the 10-item baseline assessment (Phoneme Flashcards M, S, A, I, B and Word Cards SAM, BUS) to record pre-intervention reading knowledge."),
        ("Step 4: Observational Gameplay Session (15 Minutes)", "The child completes Letter Node 1 (Hear It -> Say It -> Find It) and the Chapter 1 Blend It challenge. The observer records OBS-01 to OBS-05 timings with a stopwatch."),
        ("Step 5: Diagnostic Post-Test & Affective Feedback", "Immediately post-play, the observer re-tests the phonics items to compute the learning gain delta, followed by the 3-point Smileometer face rating."),
        ("Step 6: Caregiver SUS & Teacher Survey", "Parents and teachers complete the 10-item SUS questionnaire and accessibility checklist via Google Forms synced to the master Google Sheet.")
    ]
    for title, desc in protocol_steps:
        story.append(Paragraph(f'• <b>{title}:</b> {desc}', bullet_style))

    story.append(Spacer(1, 4))
    story.append(Paragraph('8. Thesis Chapter 4 Statistical Integration & Refactoring Roadmap', h1_style))
    story.append(Paragraph(
        'The empirical data gathered from the 30-respondent validation cohort will directly substantiate <b>Thesis Chapter 4 '
        '(Results and Discussion)</b> and drive the <b>Week 3 Requirements Refactoring</b>:',
        body_style
    ))
    story.append(Paragraph(
        '• <b>Inferential Phonics Gain Analysis:</b> Pre-test and post-test scores will be evaluated via a <b>Paired Samples t-test</b> '
        '(or Wilcoxon signed-rank test if normality is violated) to determine whether PlayIT yields statistically significant reading gains (alpha = 0.05).<br/>'
        '• <b>Caregiver Usability Benchmarking:</b> SUS survey responses will be scored using Brooke\'s normalized algorithm to generate composite usability '
        'ratings, benchmarked against the standard Bangor et al. (2008) curved grading scale (Target: >= 75.0 / Grade B+).<br/>'
        '• <b>Software Quality Verification:</b> Offline Vosk latency (SYS-01 <= 0.5s), 60 FPS animation stability (SYS-02), and SQLite/Room transactional '
        'integrity (SYS-03, 0% data loss) will be verified across test device logs.<br/>'
        '• <b>Week 3 Document Refactoring:</b> Findings will be synthesized into the <b>SRS v3.0 Requirements Traceability Matrix (RTM)</b> '
        '(officially incorporating FR-13 Blend It and FR-14 Multi-Profile) and the <b>SDD v2.0 Architecture Specification</b> due on September 19, 2026.',
        body_style
    ))

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f'PDF successfully generated: {filename}')

if __name__ == '__main__':
    out_dir = 'docs/validation-package'
    os.makedirs(out_dir, exist_ok=True)
    
    # 1. Always save to workspace root (unlocked master copies)
    root_pdf = 'playIT_MVP_Validation_Framework.pdf'
    build_pdf(root_pdf)
    root_final = 'playIT_MVP_Validation_Framework_FINAL.pdf'
    build_pdf(root_final)
    print(f"Master PDFs updated at root: '{root_pdf}' and '{root_final}'")

    # 2. Update docs package if not locked by Windows viewer
    docs_final = os.path.join(out_dir, 'playIT_MVP_Validation_Framework_FINAL.pdf')
    try:
        build_pdf(docs_final)
        print(f"Docs PDF updated: '{docs_final}'")
    except PermissionError:
        pass

    out_pdf = os.path.join(out_dir, 'playIT_MVP_Validation_Framework.pdf')
    try:
        build_pdf(out_pdf)
        print(f"Docs PDF updated: '{out_pdf}'")
    except PermissionError:
        print(f"Notice: '{out_pdf}' is currently open in Windows PDF viewer.")

