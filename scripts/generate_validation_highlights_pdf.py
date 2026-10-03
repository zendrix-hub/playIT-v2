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
            self.drawString(40, 755, 'PlayIT — MVP Validation Highlights Report (Weeks 1–2 Deliverable) | IT411')
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

    title_style = ParagraphStyle('DocTitle', parent=styles['Heading1'], fontName='Helvetica-Bold', fontSize=14, leading=17, textColor=primary_color, spaceAfter=2)
    subtitle_style = ParagraphStyle('DocSubtitle', parent=styles['Normal'], fontName='Helvetica-Bold', fontSize=9.5, leading=12.5, textColor=accent_color, spaceAfter=3)
    meta_style = ParagraphStyle('DocMeta', parent=styles['Normal'], fontName='Helvetica', fontSize=7.2, leading=9.8, textColor=colors.HexColor('#4A5568'))
    h1_style = ParagraphStyle('Heading1_Custom', parent=styles['Heading2'], fontName='Helvetica-Bold', fontSize=9.5, leading=12.5, textColor=primary_color, spaceBefore=5, spaceAfter=2, keepWithNext=True)
    h2_style = ParagraphStyle('Heading2_Custom', parent=styles['Heading3'], fontName='Helvetica-Bold', fontSize=8.2, leading=10.5, textColor=accent_color, spaceBefore=4, spaceAfter=1.5, keepWithNext=True)
    body_style = ParagraphStyle('Body_Custom', parent=styles['Normal'], fontName='Helvetica', fontSize=7.2, leading=9.8, textColor=body_color, spaceAfter=2.5)
    bullet_style = ParagraphStyle('Bullet_Custom', parent=styles['Normal'], fontName='Helvetica', fontSize=7.0, leading=9.2, textColor=body_color, leftIndent=8, spaceAfter=1.5)

    tbl_header_style = ParagraphStyle('TblHeader', parent=styles['Normal'], fontName='Helvetica-Bold', fontSize=6.8, leading=8.8, textColor=colors.white, alignment=1)
    tbl_cell_style = ParagraphStyle('TblCell', parent=styles['Normal'], fontName='Helvetica', fontSize=6.2, leading=8.0, textColor=body_color)
    tbl_cell_bold = ParagraphStyle('TblCellBold', parent=styles['Normal'], fontName='Helvetica-Bold', fontSize=6.2, leading=8.0, textColor=primary_color)
    tbl_cell_center = ParagraphStyle('TblCellCenter', parent=styles['Normal'], fontName='Helvetica', fontSize=6.2, leading=8.0, textColor=body_color, alignment=1)

    story = []

    # ==================== PAGE 1: Executive Summary & Empirical Overview ====================
    story.append(Paragraph('PlayIT: Offline-First Gamified Early Literacy Mobile Application', title_style))
    story.append(Paragraph('MVP Validation Highlights & Field Evaluation Report (Weeks 1–2 Deliverable)', subtitle_style))
    story.append(HRFlowable(width='100%', thickness=1.5, color=teal_accent, spaceBefore=0, spaceAfter=3))

    meta_text = (
        '<b>Course:</b> IT411 — Capstone & Research 2 | Semester 1, AY 2026–2027<br/>'
        '<b>Institution:</b> College of Computer Studies, Cebu Institute of Technology – University<br/>'
        '<b>Evaluation Period:</b> September 16–23, 2026 | <b>Dataset:</b> Master (responses).xlsx (Google Forms Synced)<br/>'
        '<b>Evaluated Cohorts:</b> N=16 Early Learners, N=5 Parents/Guardians, N=4 DepEd Certified Reading Educators (Total N=25)'
    )
    story.append(Paragraph(meta_text, meta_style))
    story.append(Spacer(1, 3))

    story.append(Paragraph('1. Executive Summary & Validation Purpose', h1_style))
    p1 = (
        'The MVP Validation activity was conducted during Weeks 1–2 of Capstone 2 to systematically gather diagnostic feedback from '
        'primary beneficiaries and domain experts. The objective is not merely statistical verification, but identifying ergonomic barriers, '
        'phonetic pedagogical discrepancies, and user interaction hurdles to directly inform the <b>Week 3 Requirements Refactoring (SRS v3.0 / SDD v2.0)</b> '
        'prior to full-scale development lock.<br/>'
        'Field trials were administered across physical Android test devices running <i>playit-debug.apk</i> in classroom, home, and community settings. '
        'A total of <b>25 multi-stakeholder evaluations</b> were recorded across three linked Google Forms. The empirical findings reveal <b>exceptionally high '
        'usability and learner engagement (Mean SUS = 75.50; 93.8% child joy)</b>, 100% educator agreement on Marungko curriculum alignment, and a '
        '<b>critical pedagogical insight</b>: the urgent need to isolate pure phonetic phonemes from letter-name pronunciation artifacts.'
    )
    story.append(Paragraph(p1, body_style))

    story.append(Paragraph('2. Stakeholder Cohort & Empirical Data Overview', h1_style))
    cohort_data = [
        [Paragraph('Stakeholder Cohort', tbl_header_style), Paragraph('Sample (N)', tbl_header_style), Paragraph('Evaluation Instrument & Link', tbl_header_style), Paragraph('Primary Measurement Dimension', tbl_header_style), Paragraph('Testing Modality', tbl_header_style)],
        [Paragraph('<b>Early Learners (Ages 5–7)</b>', tbl_cell_style), Paragraph('<b>N = 16</b>', tbl_cell_center), Paragraph('Child Smileyometer Form (4 items)<br/><i>forms.gle/qmVvSp6ATpizXZqn6</i>', tbl_cell_style), Paragraph('Affective joy, perceived ease, character affinity, replay intention.', tbl_cell_style), Paragraph('Physical gameplay + Facilitated post-session interview', tbl_cell_style)],
        [Paragraph('<b>Parents & Supervising Teachers</b>', tbl_cell_style), Paragraph('<b>N = 5</b>', tbl_cell_center), Paragraph('Parent/Teacher Evaluation Form (19 items)<br/><i>forms.gle/wce86JFVvUA5qfeJ7</i>', tbl_cell_style), Paragraph('10-Item Standard SUS, 7-Item Accessibility Checklist, Grade 1 fit & advocacy.', tbl_cell_style), Paragraph('Post-observation self-administered digital survey', tbl_cell_style)],
        [Paragraph('<b>DepEd Grade 1 Teachers & SMEs</b>', tbl_cell_style), Paragraph('<b>N = 4</b>', tbl_cell_center), Paragraph('Teacher Pedagogical Checklist (13 items)<br/><i>forms.gle/Jzj4hggVzuacye2Y7</i>', tbl_cell_style), Paragraph('Curriculum alignment, phonics sequence, phonetic audio quality, qualitative notes.', tbl_cell_style), Paragraph('Expert rubric review + In-depth open-ended commentary', tbl_cell_style)],
        [Paragraph('<b>Total Validation Instance</b>', tbl_cell_bold), Paragraph('<b>N = 25</b>', tbl_cell_center), Paragraph('3 Synchronized Google Forms', tbl_cell_bold), Paragraph('UPA Tripartite Model (Usability, Pedagogy, Accessibility)', tbl_cell_bold), Paragraph('Controlled field environment (Ambient noise ≤40dB)', tbl_cell_bold)],
    ]
    t_cohort = Table(cohort_data, colWidths=[105, 45, 140, 137, 105])
    t_cohort.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), teal_accent),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E0')),
        ('ROWBACKGROUNDS', (0, 1), (-1, -2), [colors.HexColor('#F7FAFC'), colors.white]),
        ('BACKGROUND', (0, -1), (-1, -1), colors.HexColor('#EDF2F7')),
        ('TOPPADDING', (0, 0), (-1, -1), 2),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
    ]))
    story.append(t_cohort)
    story.append(Spacer(1, 3))

    story.append(Paragraph('3. Quantitative Validation Findings & Empirical Benchmarks', h1_style))
    story.append(Paragraph(
        '<b>A. Adult System Usability Scale (SUS — Brooke, 1996; N=5):</b><br/>'
        '• <b>Composite Mean SUS Score: 75.50 / 100</b> (Standard Deviation: 14.70). Benchmark: Exceeds the standard industry benchmark of 68.0 and '
        'surpasses the SMART threshold of ≥ 75.0, achieving a <b>Grade B+ ("Good" to "Excellent")</b> rating on the Bangor et al. (2008) scale.<br/>'
        '• <i>Individual Respondent Scores:</i> Resp 1 = 77.5 | Resp 2 = 50.0 | Resp 3 = 80.0 | Resp 4 = 85.0 | Resp 5 = 85.0.<br/>'
        '• <i>Methodological Observation:</i> Respondent 2 exhibited classic survey acquiescence bias by selecting "Strongly Agree" (5.0) uniformly across all '
        '10 items, including reverse-scored statements (e.g., scoring 5 on "unnecessarily complex" and "cumbersome"). Excluding this acquiescent outlier yields an '
        'adjusted peer Mean SUS of <b>81.88 / 100 (Grade A- / "Excellent Usability")</b>.',
        body_style
    ))
    story.append(Paragraph(
        '<b>B. Pediatric Accessibility & Inclusivity Checklist (Dichotomous Yes/No; N=5):</b><br/>'
        '• <i>Large Text for Young Learners:</i> <b>100% Yes (5/5)</b> — Validates 24sp typography and clean sans-serif font choices.<br/>'
        '• <i>Audio Narration for Non-Readers:</i> <b>100% Yes (5/5)</b> — Confirms non-reading dual-coding eliminates adult reading dependencies.<br/>'
        '• <i>Simple Touch Mechanics (Tap, Not Drag):</i> <b>100% Yes (5/5)</b> — Confirms pediatric motor decisions prevent gesture frustration.<br/>'
        '• <i>Sufficient Color Contrast:</i> <b>100% Yes (5/5)</b> — Validates WCAG 2.1 AA visual compliance in natural ambient lighting.<br/>'
        '• <i>Appropriate Developmental Language:</i> <b>100% Yes (5/5)</b> — Confirms vocabulary fits Grade 1 Filipino cognitive milestones.<br/>'
        '• <i>Hearing Difficulty Accommodations:</i> <b>60% Yes (3/5)</b> — Identifies lack of visual sound waves/captions as an area for improvement.<br/>'
        '• <i>Motor Difficulty Accommodations:</i> <b>80% Yes (4/5)</b> — Identifies need for larger padding around corner menu toggles.',
        body_style
    ))

    # ==================== PAGE 2: Child Affect, Teacher Rubric & Qualitative Voices ====================
    story.append(PageBreak())

    story.append(Paragraph('Quantitative Validation Findings (Continued)', h1_style))
    story.append(Paragraph(
        '<b>C. Caregiver System Acceptance & Advocacy (5-Point Likert; N=5):</b><br/>'
        '• <i>Appropriate for Grade 1 Level (FIT-01):</i> <b>Mean = 4.60 / 5.0 (92.0% Endorsement)</b>.<br/>'
        '• <i>Recommend App to Other Parents & Teachers (FIT-02):</i> <b>Mean = 4.40 / 5.0 (88.0% Endorsement)</b>.',
        body_style
    ))
    story.append(Spacer(1, 2))

    story.append(Paragraph(
        '<b>D. Child Affective Smileyometer Results (5-Point Visual Scale; N=16 Early Learners):</b>',
        body_style
    ))
    child_summary_data = [
        [Paragraph('Evaluation Dimension & Question', tbl_header_style), Paragraph('😄 Love it / Fun', tbl_header_style), Paragraph('🙂 Good / Okay', tbl_header_style), Paragraph('😐 Neutral / Meh', tbl_header_style), Paragraph('🙁 Sad / Hard', tbl_header_style), Paragraph('Positive Affect %', tbl_header_style)],
        [Paragraph('<b>Game Enjoyment:</b> "How fun was the game?"', tbl_cell_style), Paragraph('15 (93.8%)', tbl_cell_center), Paragraph('1 (6.2%)', tbl_cell_center), Paragraph('0 (0.0%)', tbl_cell_center), Paragraph('0 (0.0%)', tbl_cell_center), Paragraph('<b>100.0%</b>', tbl_cell_center)],
        [Paragraph('<b>Perceived Ease:</b> "Was it easy to play?"', tbl_cell_style), Paragraph('9 (56.3%)', tbl_cell_center), Paragraph('6 (37.5%)', tbl_cell_center), Paragraph('1 (6.2%)', tbl_cell_center), Paragraph('0 (0.0%)', tbl_cell_center), Paragraph('<b>93.8%</b>', tbl_cell_center)],
        [Paragraph('<b>Character Affinity:</b> "Did you like the characters?"', tbl_cell_style), Paragraph('15 (93.8%)', tbl_cell_center), Paragraph('1 (6.2%)', tbl_cell_center), Paragraph('0 (0.0%)', tbl_cell_center), Paragraph('0 (0.0%)', tbl_cell_center), Paragraph('<b>100.0%</b>', tbl_cell_center)],
        [Paragraph('<b>Replay Intention:</b> "Would you play it again?"', tbl_cell_style), Paragraph('14 (87.5%)', tbl_cell_center), Paragraph('2 (12.5%)', tbl_cell_center), Paragraph('0 (0.0%)', tbl_cell_center), Paragraph('0 (0.0%)', tbl_cell_center), Paragraph('<b>100.0%</b>', tbl_cell_center)],
    ]
    t_child = Table(child_summary_data, colWidths=[162, 70, 75, 75, 75, 75])
    t_child.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), accent_color),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E0')),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.HexColor('#F7FAFC'), colors.white]),
        ('TOPPADDING', (0, 0), (-1, -1), 2),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 2),
    ]))
    story.append(t_child)
    story.append(Spacer(1, 3))

    story.append(Paragraph('4. DepEd Teacher Pedagogical Checklist & Curricular Verification (N=4)', h1_style))
    story.append(Paragraph(
        'Certified DepEd Grade 1 reading teachers and curriculum coordinators evaluated 12 pedagogical criteria across 3 instructional domains:',
        body_style
    ))
    ped_summary_data = [
        [Paragraph('Instructional Domain / Checklist Criterion', tbl_header_style), Paragraph('DepEd Teacher Consensus', tbl_header_style), Paragraph('Percentage', tbl_header_style), Paragraph('Pedagogical Implication & Verification', tbl_header_style)],
        [Paragraph('PED-01: DepEd Grade 1 literacy competencies alignment', tbl_cell_style), Paragraph('4 Yes / 0 No', tbl_cell_center), Paragraph('100%', tbl_cell_center), Paragraph('Verified compliant with DepEd K-12 English Phonics standards.', tbl_cell_style)],
        [Paragraph('PED-02: Phonics progression sequence logic (Marungko)', tbl_cell_style), Paragraph('4 Yes / 0 No', tbl_cell_center), Paragraph('100%', tbl_cell_center), Paragraph('Sequence m-s-a-i-o-b-e-u validates early CVC word construction.', tbl_cell_style)],
        [Paragraph('PED-03: Beginning reader difficulty level appropriateness', tbl_cell_style), Paragraph('4 Yes / 0 No', tbl_cell_center), Paragraph('100%', tbl_cell_center), Paragraph('Task pacing prevents early cognitive overload.', tbl_cell_style)],
        [Paragraph('PED-04: Clear and consistent learning objectives', tbl_cell_style), Paragraph('4 Yes / 0 No', tbl_cell_center), Paragraph('100%', tbl_cell_center), Paragraph('Hear, Say, Find, Blend cycle provides predictable mental scaffolding.', tbl_cell_style)],
        [Paragraph('PED-05: Immediate and corrective feedback delivery', tbl_cell_style), Paragraph('4 Yes / 0 No', tbl_cell_center), Paragraph('100%', tbl_cell_center), Paragraph('Gentle audio chime and mascot reactions support resilience.', tbl_cell_style)],
        [Paragraph('PED-06: Scaffolding (hints, repetition) to support learning', tbl_cell_style), Paragraph('4 Yes / 0 No', tbl_cell_center), Paragraph('100%', tbl_cell_center), Paragraph('Three-heart retry buffer prevents discouragement.', tbl_cell_style)],
        [Paragraph('PED-07: Active child engagement (non-passive viewing)', tbl_cell_style), Paragraph('4 Yes / 0 No', tbl_cell_center), Paragraph('100%', tbl_cell_center), Paragraph('Mandatory vocalization and tactile sorting maintain agency.', tbl_cell_style)],
        [Paragraph('PED-08: Letter sounds pronounced correctly', tbl_cell_bold), Paragraph('<b>2 Yes / 2 Flagged</b>', tbl_cell_center), Paragraph('<b>50% Flagged</b>', tbl_cell_center), Paragraph('<b>CRITICAL GAP:</b> Sound of M pronounced as "ma" instead of pure /m/.', tbl_cell_bold)],
        [Paragraph('PED-09: Examples and images familiar to Filipino children', tbl_cell_style), Paragraph('4 Yes / 0 No', tbl_cell_center), Paragraph('100%', tbl_cell_center), Paragraph('Sam, Bus, Dog, Cat connect directly to local environment.', tbl_cell_style)],
        [Paragraph('PED-10: Language appropriate for Grade 1 Philippines', tbl_cell_style), Paragraph('4 Yes / 0 No', tbl_cell_center), Paragraph('100%', tbl_cell_center), Paragraph('Accent-neutral, clear English phonemes tailored for ESL beginners.', tbl_cell_style)],
        [Paragraph('PED-11: App supports early literacy development', tbl_cell_style), Paragraph('4 Yes / 0 No', tbl_cell_center), Paragraph('100%', tbl_cell_center), Paragraph('Unanimous endorsement of mobile game as phonics accelerator.', tbl_cell_style)],
        [Paragraph('PED-12: Supplementary tool readiness for Grade 1 classes', tbl_cell_style), Paragraph('4 Yes / 0 No', tbl_cell_center), Paragraph('100%', tbl_cell_center), Paragraph('Teachers express desire to deploy PlayIT in learning stations.', tbl_cell_style)],
    ]
    t_ped = Table(ped_summary_data, colWidths=[152, 85, 55, 240])
    t_ped.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), teal_accent),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'MIDDLE'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E0')),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.HexColor('#F7FAFC'), colors.white]),
        ('BACKGROUND', (0, 8), (-1, 8), colors.HexColor('#FFF5F5')),  # Highlight PED-08 gap
        ('TOPPADDING', (0, 0), (-1, -1), 1.2),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 1.2),
    ]))
    story.append(t_ped)
    story.append(Spacer(1, 3))

    story.append(Paragraph('5. Qualitative Feedback & Stakeholder Voices (Direct Form Responses)', h1_style))
    story.append(Paragraph(
        '• <b>Teacher Leony Layaguin (Grade 1 DepEd Educator — <i>leony.layaguin01@deped.gov.ph</i>):</b><br/>'
        '<i>"The app is very useful and engaging for Grade 1 learners. It helps them learn the letter names, letter sounds, and words that begin with '
        'each letter in an interactive and child-friendly way. It can be a helpful tool in developing learners’ early literacy and word recognition skills. '
        '<b>One area that needs improvement is the pronunciation of the letter sounds. The app sometimes sounds like it is saying the letter name rather '
        'than producing the correct sound. For example, the sound of M should be pronounced as /m/ (mmm, mmm, mmm) rather than \'ma, ma, ma.\' '
        'Using accurate phonetic sounds would make the app more effective for beginning readers.</b>"</i>',
        body_style
    ))
    story.append(Paragraph(
        '• <b>Teacher Joy Flores (Primary Reading Facilitator — <i>flores.joy1824@gmail.com</i>):</b><br/>'
        '<i>"Good job on doing the app. It’s a helpful tool for those beginning readers. Children enjoys it as it is digitized and interactive. '
        '<b>My only concern is the sounding of letters better to have it sounds correctly so that children will not get confused with it.</b> '
        'Overall, great job! Hope you also develop reading app for advanced readers."</i>',
        body_style
    ))
    story.append(Paragraph(
        '• <b>Teacher Elena L. Bien (DepEd Public School Educator — <i>elena.bien@deped.gov.ph</i>):</b><br/>'
        '<i>"Application is appropriate for Grade 1 learners."</i><br/>'
        '• <b>Teacher Luz Bajar Rapsing (Educator — <i>luzbajarrapsing@gmail.com</i>):</b><br/>'
        '<i>"The app is appropriate for grade 1 learners and is easy to navigate."</i>',
        body_style
    ))

    # ==================== PAGE 3: Problems Encountered, Retained Strengths & Improvements ====================
    story.append(PageBreak())

    story.append(Paragraph('6. Problems & Issues Encountered During Field Validation', h1_style))
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
        story.append(Paragraph(f'• <b>{title}:</b> {desc}', bullet_style))

    story.append(Spacer(1, 3))

    story.append(Paragraph('7. Positive Aspects Validated for Permanent Retention', h1_style))
    positives = [
        ("100% Zero-Data Offline Persistence", "Supervising parents and teachers unanimously praised the complete absence of internet requirements. The app functioned flawlessly in zero-connectivity areas, guaranteeing socioeconomic equity for low-income households without data expenses."),
        ("Pediatric Ergonomics (≥64dp Tap-Only Targets)", "100% of respondents endorsed the large touch targets and elimination of drag-and-drop mechanics. Young children successfully tapped letter cards without accidental mis-taps or motor fatigue."),
        ("Mascot Affinity & Non-Punitive Feedback", "93.8% of children loved Lily the Tarsier. The three-heart resilience mechanism and gentle audio chime encouraged children to retry mistakes without emotional distress or abandonment."),
        ("26-Letter Marungko Sequence with 33 CVC Blend It Words", "100% of certified teachers verified the pedagogical progression and validated the intentional exclusion of NG and Ñ for early English reading.")
    ]
    for title, desc in positives:
        story.append(Paragraph(f'• <b>{title}:</b> {desc}', bullet_style))

    story.append(Spacer(1, 3))

    story.append(Paragraph('8. Features Requiring Improvement & Missing Requirements', h1_style))
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
        story.append(Paragraph(f'• <b>{title}:</b> {desc}', bullet_style))

    # ==================== PAGE 4: Actionable Recommendations & Refactoring Roadmap ====================
    story.append(PageBreak())

    story.append(Paragraph('9. Actionable Recommendations & Week 3 Engineering Refactoring Roadmap', h1_style))
    story.append(Paragraph(
        'In strict alignment with the CIT-U Capstone 2 schedule, the empirical findings from this 25-respondent MVP validation directly '
        'govern the <b>Week 3 Requirements & Design Refactoring</b> deliverables due on September 26, 2026:',
        body_style
    ))

    roadmap_data = [
        [Paragraph('Capstone 2 Target Artifact', tbl_header_style), Paragraph('Empirical Validation Finding', tbl_header_style), Paragraph('Engineering Action & Document Refactoring Specification', tbl_header_style), Paragraph('Priority / Status', tbl_header_style)],
        [Paragraph('<b>SRS v3.0</b><br/>Functional Requirements', tbl_cell_style),
         Paragraph('Teachers flagged letter-name vocal artifacts in phoneme modeling (PED-08).', tbl_cell_style),
         Paragraph('Update <b>FR-02 (Hear It Module)</b>: Specify that phoneme audio models must deliver isolated unvoiced/voiced pure phonemes (e.g., /m/ = [m:], duration 800ms) with zero syllabic or letter-name concatenation.', tbl_cell_style),
         Paragraph('<b>P0 (Immediate)</b><br/>Refactor in Week 3', tbl_cell_center)],
        [Paragraph('<b>SRS v3.0</b><br/>UI & Accessibility', tbl_cell_style),
         Paragraph('Children hesitated during mic recording; hearing accommodation scored 60%.', tbl_cell_style),
         Paragraph('Update <b>FR-03 (Say It Module)</b>: Add requirement for an active audio-visual ripple state on mic tap. Add <b>NFR-Accessibility</b> specifying visual mouth articulation icons.', tbl_cell_style),
         Paragraph('<b>P1 (High)</b><br/>Refactor in Week 3', tbl_cell_center)],
        [Paragraph('<b>SDD v2.0</b><br/>Audio Architecture', tbl_cell_style),
         Paragraph('Phoneme pronunciation accuracy requires pristine acoustic delivery.', tbl_cell_style),
         Paragraph('Refactor <b>AudioPlaybackManager</b> architecture: Ensure pre-cached SoundPool zero-latency playback for short phoneme bursts; isolate pure phoneme .wav files in raw/ directory.', tbl_cell_style),
         Paragraph('<b>P0 (Immediate)</b><br/>Refactor in Week 3', tbl_cell_center)],
        [Paragraph('<b>SDD v2.0</b><br/>Database & Telemetry', tbl_cell_style),
         Paragraph('100% offline persistence confirmed; teachers requested bulk progress tracking.', tbl_cell_style),
         Paragraph('Refactor <b>Room Database Schema v3</b>: Formalize multi-profile telemetry queries for diagnostic summary generation without compromising offline latency.', tbl_cell_style),
         Paragraph('<b>P1 (High)</b><br/>Refactor in Week 3', tbl_cell_center)],
        [Paragraph('<b>Traceability Matrix</b><br/>(RTM v3.0)', tbl_cell_style),
         Paragraph('Teacher validation confirmed 33 CVC Blend It words and 7-biome sequence.', tbl_cell_style),
         Paragraph('Map verified validation items (PED-01..12, SUS-01..10, ACC-01..07) directly to <b>FR-13 (Blend It CVC Synthesis)</b> and <b>FR-14 (Multi-Profile Support)</b>.', tbl_cell_style),
         Paragraph('<b>P0 (Immediate)</b><br/>Refactor in Week 3', tbl_cell_center)],
        [Paragraph('<b>Software Test Doc</b><br/>(STD Test Cases)', tbl_cell_style),
         Paragraph('Vosk speech recognition must reliably score child phoneme vocalizations.', tbl_cell_style),
         Paragraph('Formulate <b>TC-ASR-01 to 05</b>: Acoustic automated regression tests evaluating Vosk phoneme acceptance across varied child voice pitches and ambient noise levels up to 40dB.', tbl_cell_style),
         Paragraph('<b>P1 (High)</b><br/>Weeks 4–7 Plan', tbl_cell_center)],
    ]
    t_road = Table(roadmap_data, colWidths=[105, 125, 222, 80])
    t_road.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), accent_color),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('VALIGN', (0, 0), (-1, -1), 'TOP'),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor('#CBD5E0')),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.HexColor('#F7FAFC'), colors.white]),
        ('TOPPADDING', (0, 0), (-1, -1), 2.5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 2.5),
    ]))
    story.append(t_road)
    story.append(Spacer(1, 4))

    story.append(Paragraph('10. Conclusion & Midterm Readiness Statement', h1_style))
    p_concl = (
        'The Weeks 1–2 MVP Validation successfully achieved its research objectives. With an overall System Usability Scale score of <b>75.50 (Grade B+)</b>, '
        '<b>100% agreement on DepEd Grade 1 curriculum alignment</b>, and <b>93.8% child affective joy</b>, the core software foundation of PlayIT is '
        'empirically validated as sound, engaging, and developmentally appropriate. The specific qualitative feedback obtained from certified educators '
        'provides precise, high-value engineering targets for Week 3—chiefly the re-mastering of pure phonetic sound models and enhanced microphone visual feedback. '
        'The project is fully on schedule and positioned with high rigor for full system implementation in Weeks 4–7.'
    )
    story.append(Paragraph(p_concl, body_style))

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f'PDF successfully generated: {filename}')

if __name__ == '__main__':
    out_dir = 'docs/validation-package'
    os.makedirs(out_dir, exist_ok=True)

    root_pdf = 'playIT_MVP_Validation_Highlights.pdf'
    build_pdf(root_pdf)

    docs_pdf = os.path.join(out_dir, 'playIT_MVP_Validation_Highlights.pdf')
    try:
        build_pdf(docs_pdf)
        print(f"Docs PDF updated: '{docs_pdf}'")
    except PermissionError:
        pass
