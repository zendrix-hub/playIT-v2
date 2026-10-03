import os
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
        footer_text = f'Page {self._pageNumber} of {page_count}'
        self.drawRightString(572, 25, footer_text)
        self.drawString(40, 25, 'Cebu Institute of Technology – University | College of Computer Studies (IT411)')
        self.setStrokeColor(colors.HexColor('#CBD5E0'))
        self.setLineWidth(0.5)
        self.line(40, 35, 572, 35)
        self.restoreState()

def build_pdf(filename):
    doc = SimpleDocTemplate(
        filename, pagesize=letter,
        leftMargin=40, rightMargin=40, topMargin=45, bottomMargin=42
    )
    styles = getSampleStyleSheet()
    maroon = colors.HexColor('#800000')
    dark_teal = colors.HexColor('#1F3A3D')
    teal_accent = colors.HexColor('#2B7A78')
    body_color = colors.HexColor('#2D3748')

    inst_style = ParagraphStyle('InstHeader', fontName='Helvetica-Bold', fontSize=12, leading=15, textColor=maroon, alignment=1)
    col_style = ParagraphStyle('ColHeader', fontName='Helvetica-Bold', fontSize=10, leading=13, textColor=dark_teal, alignment=1)
    dept_style = ParagraphStyle('DeptHeader', fontName='Helvetica', fontSize=8.5, leading=11, textColor=body_color, alignment=1)
    
    title_style = ParagraphStyle('DocTitle', fontName='Helvetica-Bold', fontSize=12, leading=15, textColor=dark_teal, spaceAfter=4)
    subtitle_style = ParagraphStyle('DocSub', fontName='Helvetica-Bold', fontSize=9, leading=12, textColor=teal_accent, spaceAfter=8)
    body_style = ParagraphStyle('DocBody', fontName='Helvetica', fontSize=8.5, leading=11.5, textColor=body_color, spaceAfter=6)
    bullet_style = ParagraphStyle('DocBullet', fontName='Helvetica', fontSize=8.5, leading=11.5, textColor=body_color, leftIndent=12, spaceAfter=4)
    bold_body = ParagraphStyle('DocBoldBody', fontName='Helvetica-Bold', fontSize=8.5, leading=11.5, textColor=dark_teal, spaceAfter=4)
    sig_name = ParagraphStyle('SigName', fontName='Helvetica-Bold', fontSize=8.5, leading=11, textColor=dark_teal)
    sig_sub = ParagraphStyle('SigSub', fontName='Helvetica', fontSize=7.5, leading=9.5, textColor=body_color)

    def letterhead():
        return [
            Paragraph('CEBU INSTITUTE OF TECHNOLOGY – UNIVERSITY', inst_style),
            Paragraph('COLLEGE OF COMPUTER STUDIES', col_style),
            Paragraph('Department of Information Technology | N. Bacalso Ave., Cebu City, Philippines 6000', dept_style),
            Spacer(1, 4),
            HRFlowable(width='100%', thickness=1, color=teal_accent, spaceBefore=2, spaceAfter=8)
        ]

    story = []

    # ==================== PAGE 1: COVER & RESEARCH GUIDANCE ====================
    story.extend(letterhead())
    story.append(Paragraph('IT411 Capstone Research — Transmittal & Endorsement Package', title_style))
    story.append(Paragraph('Official Documentation for MVP User Validation Testing | Project PlayIT', subtitle_style))

    guide_box = [
        [Paragraph('<b>RESEARCH GUIDANCE: SPECIFIC VS. GENERAL TRANSMITTAL LETTERS</b>', bold_body)],
        [Paragraph(
            'Based on Philippine educational protocol (DepEd Order No. 16, s. 2017) and CIT-U CCS capstone standards:<br/><br/>'
            '<b>1. When to use SPECIFIC (Template 1 on Page 2):</b><br/>'
            '• <b>Target:</b> Formal academic schools (CIT-U Elementary Department, DepEd Public Elementary Schools, and Private Basic Ed Institutions).<br/>'
            '• <b>Why:</b> Public school principals will strictly reject generic "To Whom It May Concern" letters because school heads must log formal research requests under the DepEd Child Protection Policy (DO 40, s. 2012) and Document Tracking System.<br/>'
            '• <b>Recommendation:</b> Keep the body identical, swap the addressee block for each prospective school, and have Sir Amparo sign them in one batch.<br/><br/>'
            '<b>2. When to use GENERAL / OPEN (Template 2 on Page 3):</b><br/>'
            '• <b>Target:</b> Community learning circles, tutorial centers, church literacy ministries, or direct neighborhood testing where there is no single school principal.<br/>'
            '• <b>Why:</b> As Sir Amparo advised, getting 30 respondents purely from formal DepEd schools may cause delays due to busy teacher schedules. A general endorsement letter gives the team administrative agility to test in alternative learning environments.<br/><br/>'
            '<b>3. Parent Informed Consent Form (Template 3 on Page 4):</b><br/>'
            '• Ethically and legally mandated under RA 10173 (Data Privacy Act) whenever testing Grade 1 minors, regardless of venue.',
            body_style
        )]
    ]
    t_guide = Table(guide_box, colWidths=[532])
    t_guide.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#F0F7F6')),
        ('BOX', (0,0), (-1,-1), 1, teal_accent),
        ('TOPPADDING', (0,0), (-1,-1), 8),
        ('BOTTOMPADDING', (0,0), (-1,-1), 8),
        ('LEFTPADDING', (0,0), (-1,-1), 12),
        ('RIGHTPADDING', (0,0), (-1,-1), 12),
    ]))
    story.append(t_guide)
    story.append(PageBreak())

    # ==================== PAGE 2: TEMPLATE 1 (SPECIFIC) ====================
    story.extend(letterhead())
    story.append(Paragraph('<b>[TEMPLATE 1: FOR SPECIFIC SCHOOLS / INSTITUTIONS]</b>', subtitle_style))
    story.append(Paragraph('<b>Date:</b> September 14, 2026', body_style))
    story.append(Paragraph(
        '<b>THE SCHOOL PRINCIPAL / BASIC EDUCATION HEAD</b><br/>'
        '[Name of Target School, e.g., CIT-University Elementary Department]<br/>'
        '[School Address, e.g., N. Bacalso Avenue, Cebu City]<br/><br/>'
        '<b>THROUGH:</b> Grade 1 Curriculum Coordinator / Early Childhood Reading Coordinator',
        body_style
    ))
    story.append(Paragraph('<b>SUBJECT: REQUEST TO CONDUCT IT411 CAPSTONE MVP USER VALIDATION TESTING (PROJECT: PLAYIT)</b>', bold_body))
    story.append(Paragraph('Dear Ma\'am / Sir,', body_style))
    story.append(Paragraph('Greetings in the name of academic excellence and quality education!', body_style))
    story.append(Paragraph(
        'We, the undersigned 4th-year Bachelor of Science in Information Technology (BSIT) students of the College of Computer Studies '
        'at Cebu Institute of Technology – University (CIT-U), are currently undertaking our Capstone Research project entitled: '
        '<b>"PlayIT: An Offline-First Gamified Early Literacy Mobile Application Utilizing the Marungko Approach and Speech Recognition for Grade 1 Learners"</b>.',
        body_style
    ))
    story.append(Paragraph(
        'In partial fulfillment of the requirements for IT411 (Capstone Project & Research 2), our team is conducting an empirical Minimum Viable Product (MVP) '
        'User Validation to evaluate pediatric usability, caregiver adoption, and pedagogical fidelity of the 26-letter Marungko phonics sequence prior to final system lock. '
        'We respectfully request permission from your good office to conduct user testing and survey administration among selected Grade 1 pupils, parents, and reading teachers.',
        body_style
    ))
    story.append(Paragraph('<b>Testing Logistics & Child Protection Safeguards:</b>', bold_body))
    story.append(Paragraph('• <b>Target Participants:</b> Selected Grade 1 learners (ages 6–7), parents/guardians, and Grade 1 reading teachers/SMEs.', bullet_style))
    story.append(Paragraph('• <b>Estimated Duration:</b> Approximately 15 to 20 minutes per child session; testing will be scheduled strictly outside regular instructional hours to prevent academic disruption.', bullet_style))
    story.append(Paragraph('• <b>Testing Mechanics:</b> Children will engage in interactive, offline letter exploration (Hear It, Say It, Find It, Blend It) on provisioned Android tablets, followed by an intuitive 3-point visual Smileometer rating. Caregivers and teachers complete a brief 10-Item System Usability Scale (SUS) survey.', bullet_style))
    story.append(Paragraph('• <b>Data Privacy & Safety:</b> Fully compliant with Republic Act No. 10173 (Data Privacy Act of 2012) and the DepEd Child Protection Policy. Participation is voluntary, requiring child assent and signed parental consent. No personally identifiable information or raw audio is uploaded or stored externally.', bullet_style))
    story.append(Spacer(1, 4))
    story.append(Paragraph('Respectfully yours,', body_style))

    sig_data = [
        [Paragraph('<b>ZENDRIX [SURNAME]</b><br/>Lead Researcher / Developer, Project PlayIT', sig_name),
         Paragraph('<b>SKYE [SURNAME]</b><br/>Co-Researcher / Systems Analyst, Project PlayIT', sig_name)],
        [Paragraph('<b>[NAME OF RESEARCHER 3]</b><br/>Co-Researcher / Quality Assurance, Project PlayIT', sig_name),
         Paragraph('<b>[NAME OF RESEARCHER 4]</b><br/>Co-Researcher / UI-UX Lead, Project PlayIT', sig_name)]
    ]
    t_sig = Table(sig_data, colWidths=[266, 266])
    t_sig.setStyle(TableStyle([
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4)
    ]))
    story.append(t_sig)
    story.append(Spacer(1, 4))
    story.append(Paragraph('<b>Noted and Endorsed by:</b>', bold_body))

    end_data = [
        [Paragraph('<b>MR. JOEMARIE C. AMPARO</b><br/>Capstone Project Adviser<br/>College of Computer Studies, CIT-University', sig_name),
         Paragraph('<b>CHERRY LYNN S. STA. ROMANA, DIT</b><br/>Dean, College of Computer Studies<br/>Cebu Institute of Technology – University', sig_name)]
    ]
    t_end = Table(end_data, colWidths=[266, 266])
    t_end.setStyle(TableStyle([('VALIGN', (0,0), (-1,-1), 'TOP')]))
    story.append(t_end)
    story.append(Spacer(1, 4))

    act_box = [
        [Paragraph('<b>ACTION TAKEN BY THE INSTITUTION:</b>', bold_body)],
        [Paragraph(
            '[   ] APPROVED for testing under agreed school guidelines.<br/>'
            '[   ] DISAPPROVED / RESCHEDULE due to: _____________________________________<br/><br/>'
            '____________________________________________________          _______________________<br/>'
            'Principal / Authorized School Representative Signature              Date',
            body_style
        )]
    ]
    t_act = Table(act_box, colWidths=[532])
    t_act.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#F9FAFB')),
        ('BOX', (0,0), (-1,-1), 0.5, colors.HexColor('#CBD5E0')),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
        ('LEFTPADDING', (0,0), (-1,-1), 8),
        ('RIGHTPADDING', (0,0), (-1,-1), 8)
    ]))
    story.append(t_act)
    story.append(PageBreak())

    # ==================== PAGE 3: TEMPLATE 2 (GENERAL) ====================
    story.extend(letterhead())
    story.append(Paragraph('<b>[TEMPLATE 2: GENERAL / OPEN ENDORSEMENT FOR TUTORIAL CENTERS & COMMUNITY COHORTS]</b>', subtitle_style))
    story.append(Paragraph('<b>Date:</b> September 14, 2026', body_style))
    story.append(Paragraph(
        '<b>TO WHOM IT MAY CONCERN / COMMUNITY LEARNING FACILITATORS</b><br/>'
        '<b>Subject:</b> Academic Endorsement for PlayIT MVP User Validation Testing',
        body_style
    ))
    story.append(Paragraph('Dear Educator / Learning Partner / Respected Guardian,', body_style))
    story.append(Paragraph(
        'This letter serves as an official endorsement from the College of Computer Studies at Cebu Institute of Technology – University (CIT-U) '
        'for our 4th-year BSIT Capstone Research team conducting field testing for the educational project: '
        '<b>"PlayIT: An Offline-First Gamified Early Literacy Mobile Application Utilizing the Marungko Approach and Speech Recognition for Grade 1 Learners"</b>.',
        body_style
    ))
    story.append(Paragraph(
        'As advised by our faculty and capstone committee, this validation seeks to gather feedback across diverse early learning settings '
        '(including private tutorials, community learning programs, and home-based practice) to reach our required cohort of 30 respondents.',
        body_style
    ))
    story.append(Paragraph(
        'We cordially invite your participation in a brief, 15-minute evaluation session. The session consists of allowing the child to play 1–2 offline phonics levels '
        'on an Android device, followed by a simple satisfaction rating from the child and a standard usability survey (System Usability Scale) from the educator or parent.',
        body_style
    ))
    story.append(Paragraph('• <b>Safety & Privacy:</b> No internet connection is required, no advertisements exist, and no personal data is collected or shared.', bullet_style))
    story.append(Paragraph('• <b>Voluntary Participation:</b> Learners and caregivers may pause or conclude their participation at any moment without penalty.', bullet_style))
    story.append(Paragraph(
        'Your assistance is instrumental in refining technology that promotes equitable, mother-tongue and phonics-based early reading literacy. '
        'Any courtesies extended to our student researchers are deeply appreciated.',
        body_style
    ))
    story.append(Spacer(1, 10))
    story.append(Paragraph('Sincerely yours in academic service,', body_style))
    story.append(Spacer(1, 4))

    end_data2 = [
        [Paragraph('<b>MR. JOEMARIE C. AMPARO</b><br/>Capstone Project Adviser<br/>College of Computer Studies, CIT-University', sig_name),
         Paragraph('<b>THE PLAYIT RESEARCH TEAM</b><br/>BS Information Technology, Batch 2027<br/>College of Computer Studies, CIT-University', sig_name)]
    ]
    t_end2 = Table(end_data2, colWidths=[266, 266])
    t_end2.setStyle(TableStyle([('VALIGN', (0,0), (-1,-1), 'TOP')]))
    story.append(t_end2)
    story.append(PageBreak())

    # ==================== PAGE 4: TEMPLATE 3 (PARENT CONSENT) ====================
    story.extend(letterhead())
    story.append(Paragraph('<b>[TEMPLATE 3: PARENT / GUARDIAN INFORMED CONSENT & CHILD ASSENT FORM]</b>', subtitle_style))
    story.append(Paragraph('<b>INFORMED CONSENT FOR PARTICIPATION IN RESEARCH TESTING</b>', bold_body))
    story.append(Paragraph(
        'Magandang araw po / Maayong adlaw! We are 4th-year BSIT students from CIT-University researching early reading tools. '
        'We would like to invite your child to try <b>PlayIT</b>, a free, offline educational mobile game that teaches letter sounds and reading through fun interactive activities.',
        body_style
    ))
    story.append(Paragraph('• <b>What will happen:</b> Your child will play the letter sound activity on an Android smartphone or tablet for approximately 10 to 15 minutes. Afterwards, we will ask them which smiley face shows how much they enjoyed playing.', bullet_style))
    story.append(Paragraph('• <b>Parent survey:</b> You will be asked to answer a short 10-item questionnaire (SUS) regarding whether the app was easy to navigate and useful for home practice.', bullet_style))
    story.append(Paragraph('• <b>Safety & Confidentiality:</b> PlayIT is 100% offline. No photos, videos, or voice recordings are uploaded to the internet or shared publicly. All information is kept completely anonymous and confidential under the Data Privacy Act (RA 10173).', bullet_style))
    story.append(Paragraph('• <b>Voluntary participation:</b> Participation is completely voluntary. You or your child can stop at any time.', bullet_style))
    story.append(Spacer(1, 6))

    con_box = [
        [Paragraph('<b>PARENT / GUARDIAN WRITTEN CONSENT:</b>', bold_body)],
        [Paragraph(
            'I have read and understood the information above. I voluntarily agree to allow my child to participate in this mobile application evaluation study.<br/><br/>'
            '<b>Child\'s Name / Nickname:</b> ____________________________________     <b>Age / Grade:</b> ________________<br/><br/>'
            '<b>Parent / Guardian Name:</b>  ____________________________________     <b>Contact No:</b>  ________________<br/><br/>'
            '<b>Parent / Guardian Signature:</b> _________________________________     <b>Date:</b>        ________________',
            body_style
        )]
    ]
    t_con = Table(con_box, colWidths=[532])
    t_con.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor('#F9FAFB')),
        ('BOX', (0,0), (-1,-1), 0.5, colors.HexColor('#CBD5E0')),
        ('TOPPADDING', (0,0), (-1,-1), 6),
        ('BOTTOMPADDING', (0,0), (-1,-1), 6),
        ('LEFTPADDING', (0,0), (-1,-1), 10),
        ('RIGHTPADDING', (0,0), (-1,-1), 10)
    ]))
    story.append(t_con)

    doc.build(story, canvasmaker=NumberedCanvas)

if __name__ == "__main__":
    out_root = "playIT_Transmittal_Letter_Draft.pdf"
    build_pdf(out_root)
    print(f"Generated: {out_root}")

    out_docs = os.path.join("docs", "validation-package", "playIT_Transmittal_Letter_Draft.pdf")
    try:
        build_pdf(out_docs)
        print(f"Generated: {out_docs}")
    except Exception as e:
        print(f"Notice: Could not write to {out_docs} ({e})")
