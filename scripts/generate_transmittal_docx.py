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

def set_cell_margins(cell, top=100, bottom=100, left=140, right=140):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = OxmlElement('w:tcMar')
    for m, val in [('top', top), ('bottom', bottom), ('left', left), ('right', right)]:
        node = OxmlElement(f'w:{m}')
        node.set(qn('w:w'), str(val))
        node.set(qn('w:type'), 'dxa')
        tcMar.append(node)
    tcPr.append(tcMar)

def add_callout(doc, title, text, bg_hex="F0F4F8", border_hex="2B7A78"):
    tbl = doc.add_table(rows=1, cols=1)
    tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl.autofit = False
    tbl.columns[0].width = Inches(7.1)
    cell = tbl.cell(0, 0)
    set_cell_background(cell, bg_hex)
    set_cell_margins(cell, top=120, bottom=120, left=180, right=180)
    
    p = cell.paragraphs[0]
    p.paragraph_format.space_after = Pt(2)
    p.paragraph_format.line_spacing = 1.15
    r_t = p.add_run(title + "\n")
    r_t.font.name = 'Calibri'
    r_t.font.size = Pt(9.5)
    r_t.font.bold = True
    r_t.font.color.rgb = RGBColor(31, 58, 61)
    
    r_body = p.add_run(text)
    r_body.font.name = 'Calibri'
    r_body.font.size = Pt(8.5)
    r_body.font.color.rgb = RGBColor(45, 55, 72)
    doc.add_paragraph().paragraph_format.space_after = Pt(4)

def build_transmittal_docx(filename):
    doc = Document()
    for section in doc.sections:
        section.top_margin = Inches(0.7)
        section.bottom_margin = Inches(0.7)
        section.left_margin = Inches(0.7)
        section.right_margin = Inches(0.7)

    maroon = RGBColor(128, 0, 0)      # CIT Maroon
    dark_teal = RGBColor(31, 58, 61)  # #1F3A3D
    charcoal = RGBColor(45, 55, 72)   # #2D3748
    teal_accent = RGBColor(43, 122, 120)

    # Header Function
    def add_letterhead(doc):
        p_hdr = doc.add_paragraph()
        p_hdr.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_hdr.paragraph_format.space_after = Pt(1)
        r_inst = p_hdr.add_run("CEBU INSTITUTE OF TECHNOLOGY – UNIVERSITY\n")
        r_inst.font.name = 'Calibri'
        r_inst.font.size = Pt(13)
        r_inst.font.bold = True
        r_inst.font.color.rgb = maroon

        r_col = p_hdr.add_run("COLLEGE OF COMPUTER STUDIES\n")
        r_col.font.name = 'Calibri'
        r_col.font.size = Pt(11)
        r_col.font.bold = True
        r_col.font.color.rgb = dark_teal

        r_dept = p_hdr.add_run("Department of Information Technology\nN. Bacalso Avenue, Cebu City, Philippines 6000\n")
        r_dept.font.name = 'Calibri'
        r_dept.font.size = Pt(9)
        r_dept.font.color.rgb = charcoal

        # Divider
        p_div = doc.add_paragraph()
        p_div.paragraph_format.space_before = Pt(0)
        p_div.paragraph_format.space_after = Pt(12)
        r_div = p_div.add_run("—" * 60)
        r_div.font.name = 'Calibri'
        r_div.font.size = Pt(10)
        r_div.font.color.rgb = teal_accent
        p_div.alignment = WD_ALIGN_PARAGRAPH.CENTER

    # ==================== COVER / GUIDELINES ====================
    p_title = doc.add_paragraph()
    r = p_title.add_run("IT411 Capstone Research — Transmittal & Endorsement Package")
    r.font.name = 'Calibri'
    r.font.size = Pt(16)
    r.font.bold = True
    r.font.color.rgb = dark_teal
    p_title.paragraph_format.space_after = Pt(2)

    p_sub = doc.add_paragraph()
    r_sub = p_sub.add_run("Official Documentation for MVP User Validation Testing | Project PlayIT")
    r_sub.font.name = 'Calibri'
    r_sub.font.size = Pt(11)
    r_sub.font.bold = True
    r_sub.font.color.rgb = teal_accent
    p_sub.paragraph_format.space_after = Pt(8)

    add_callout(
        doc,
        "RESEARCH GUIDANCE: SPECIFIC VS. GENERAL TRANSMITTAL LETTERS",
        "Based on Philippine educational protocol (DepEd Order No. 16, s. 2017) and CIT-U CCS capstone standards:\n\n"
        "1. WHEN TO USE SPECIFIC (Template 1 below):\n"
        "   - Use for formal schools (CIT-U Elementary Department, DepEd Public Elementary Schools, and Private Basic Ed Schools).\n"
        "   - Public school principals will strictly reject generic 'To Whom It May Concern' letters because they must log official research approvals under the DepEd Child Protection Policy (DO 40, s. 2012).\n"
        "   - Recommendation: Print or export a specific letter addressed to each target school principal. Sir Amparo can sign them all in one batch.\n\n"
        "2. WHEN TO USE GENERAL / OPEN (Template 2 below):\n"
        "   - Use for community reading groups, tutorial centers, church literacy ministries, or direct neighborhood testing where there is no single school principal.\n"
        "   - This allows the team to gather respondents flexibly without needing a newly addressed letter every time.\n\n"
        "3. PARENT CONSENT (Template 3 below):\n"
        "   - Legally and ethically required whenever a Grade 1 child interacts with the app, regardless of location."
    )

    doc.add_page_break()

    # ==================== TEMPLATE 1: SPECIFIC TRANSMITTAL LETTER ====================
    add_letterhead(doc)

    p_lbl = doc.add_paragraph()
    r = p_lbl.add_run("[TEMPLATE 1: FOR SPECIFIC SCHOOLS / INSTITUTIONS]")
    r.font.name = 'Calibri'
    r.font.size = Pt(9)
    r.font.bold = True
    r.font.color.rgb = teal_accent
    p_lbl.paragraph_format.space_after = Pt(6)

    p_date = doc.add_paragraph()
    r = p_date.add_run("Date: September 14, 2026")
    r.font.name = 'Calibri'
    r.font.size = Pt(10)
    p_date.paragraph_format.space_after = Pt(10)

    p_addr = doc.add_paragraph()
    p_addr.paragraph_format.space_after = Pt(10)
    p_addr.paragraph_format.line_spacing = 1.15
    r = p_addr.add_run(
        "THE SCHOOL PRINCIPAL / BASIC EDUCATION HEAD\n"
        "[Name of Target School, e.g., CIT-University Elementary Department]\n"
        "[School Address, e.g., N. Bacalso Avenue, Cebu City]\n\n"
        "THROUGH: Grade 1 Curriculum Coordinator / Early Childhood Reading Coordinator"
    )
    r.font.name = 'Calibri'
    r.font.size = Pt(10)
    r.font.bold = True

    p_subj = doc.add_paragraph()
    p_subj.paragraph_format.space_after = Pt(10)
    r = p_subj.add_run("SUBJECT: REQUEST TO CONDUCT IT411 CAPSTONE MVP USER VALIDATION TESTING (PROJECT: PLAYIT)")
    r.font.name = 'Calibri'
    r.font.size = Pt(10.5)
    r.font.bold = True
    r.font.color.rgb = dark_teal

    p_sal = doc.add_paragraph()
    r = p_sal.add_run("Dear Ma'am / Sir,")
    r.font.name = 'Calibri'
    r.font.size = Pt(10)
    p_sal.paragraph_format.space_after = Pt(6)

    def add_p(text, bold_prefix=None, space_after=6):
        p = doc.add_paragraph()
        p.paragraph_format.space_after = Pt(space_after)
        p.paragraph_format.line_spacing = 1.15
        if bold_prefix:
            r_pre = p.add_run(bold_prefix)
            r_pre.font.name = 'Calibri'
            r_pre.font.size = Pt(10)
            r_pre.font.bold = True
            r_pre.font.color.rgb = dark_teal
        r = p.add_run(text)
        r.font.name = 'Calibri'
        r.font.size = Pt(10)
        r.font.color.rgb = charcoal
        return p

    add_p("Greetings in the name of academic excellence and quality education!")
    add_p(
        "We, the undersigned 4th-year Bachelor of Science in Information Technology (BSIT) students of the College of Computer Studies at Cebu Institute of Technology – University (CIT-U), are currently undertaking our Capstone Research project entitled:"
    )

    p_thesis = doc.add_paragraph()
    p_thesis.paragraph_format.space_before = Pt(2)
    p_thesis.paragraph_format.space_after = Pt(6)
    p_thesis.paragraph_format.left_indent = Inches(0.4)
    r_th = p_thesis.add_run('"PlayIT: An Offline-First Gamified Early Literacy Mobile Application Utilizing the Marungko Approach and Speech Recognition for Grade 1 Learners"')
    r_th.font.name = 'Calibri'
    r_th.font.size = Pt(10.5)
    r_th.font.bold = True
    r_th.font.italic = True
    r_th.font.color.rgb = dark_teal

    add_p(
        "In partial fulfillment of the requirements for IT411 (Capstone Project & Research 2), our team is conducting an empirical Minimum Viable Product (MVP) User Validation. The primary objective is to evaluate pediatric usability, caregiver adoption, and pedagogical fidelity of the 26-letter Marungko phonics sequence prior to final system lock."
    )
    add_p(
        "In this regard, we respectfully request permission from your good office to conduct user testing and survey administration among selected Grade 1 pupils, their parents or guardians, and early-grade reading teachers in your institution."
    )

    add_p("The validation activity encompasses the following operational parameters:", bold_prefix="Testing Logistics & Safeguards: ")

    def add_bullet(lead, body):
        p = doc.add_paragraph(style='List Bullet')
        p.paragraph_format.space_after = Pt(3)
        p.paragraph_format.line_spacing = 1.15
        r_lead = p.add_run(lead + ": ")
        r_lead.font.name = 'Calibri'
        r_lead.font.size = Pt(9.5)
        r_lead.font.bold = True
        r_lead.font.color.rgb = dark_teal
        r_body = p.add_run(body)
        r_body.font.name = 'Calibri'
        r_body.font.size = Pt(9.5)
        r_body.font.color.rgb = charcoal

    add_bullet("Target Participants", "Selected Grade 1 learners (ages 6–7), parents/guardians, and Grade 1 reading teachers/SMEs.")
    add_bullet("Estimated Duration", "Approximately 15 to 20 minutes per child session; testing will be scheduled strictly outside regular instructional hours or during designated activity periods to prevent any academic disruption.")
    add_bullet("Testing Mechanics", "Children will engage in interactive, offline letter exploration (Hear It, Say It, Find It, Blend It) on provisioned Android tablets, followed by an intuitive 3-point visual Smileometer rating. Parents and teachers will complete a brief 10-Item System Usability Scale (SUS) and pedagogical alignment survey.")
    add_bullet("Data Privacy & Ethical Adherence", "The study fully complies with Republic Act No. 10173 (Data Privacy Act of 2012) and the DepEd Child Protection Policy. All participation is strictly voluntary, requiring child verbal assent and signed parental consent. No personally identifiable information (PII) or raw audio recordings will be stored or shared; data will be analyzed strictly in aggregate.")

    add_p(
        "Attached with this letter are our Research Validation Framework & Instrument Specification (UPA Model) and sample questionnaires for your review."
    )
    add_p(
        "We earnestly hope for your favorable consideration and approval of this academic request. Your partnership will significantly contribute to improving early reading remediation tools for Filipino learners. Thank you very much!"
    )

    p_resp = doc.add_paragraph()
    p_resp.paragraph_format.space_after = Pt(16)
    r = p_resp.add_run("Respectfully yours,")
    r.font.name = 'Calibri'
    r.font.size = Pt(10)

    # Signatories Table
    tbl_sig = doc.add_table(rows=2, cols=2)
    tbl_sig.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_sig.autofit = False
    tbl_sig.columns[0].width = Inches(3.5)
    tbl_sig.columns[1].width = Inches(3.5)

    c00 = tbl_sig.cell(0, 0)
    p = c00.paragraphs[0]
    p.paragraph_format.line_spacing = 1.15
    p.paragraph_format.space_after = Pt(12)
    r = p.add_run("ZENDRIX [SURNAME]\n")
    r.font.bold = True
    r.font.size = Pt(9.5)
    r2 = p.add_run("Lead Researcher / Developer, Project PlayIT")
    r2.font.size = Pt(8.5)

    c01 = tbl_sig.cell(0, 1)
    p = c01.paragraphs[0]
    p.paragraph_format.line_spacing = 1.15
    p.paragraph_format.space_after = Pt(12)
    r = p.add_run("SKYE [SURNAME]\n")
    r.font.bold = True
    r.font.size = Pt(9.5)
    r2 = p.add_run("Co-Researcher / Systems Analyst, Project PlayIT")
    r2.font.size = Pt(8.5)

    c10 = tbl_sig.cell(1, 0)
    p = c10.paragraphs[0]
    p.paragraph_format.line_spacing = 1.15
    r = p.add_run("[NAME OF RESEARCHER 3]\n")
    r.font.bold = True
    r.font.size = Pt(9.5)
    r2 = p.add_run("Co-Researcher / Quality Assurance, Project PlayIT")
    r2.font.size = Pt(8.5)

    c11 = tbl_sig.cell(1, 1)
    p = c11.paragraphs[0]
    p.paragraph_format.line_spacing = 1.15
    r = p.add_run("[NAME OF RESEARCHER 4]\n")
    r.font.bold = True
    r.font.size = Pt(9.5)
    r2 = p.add_run("Co-Researcher / UI-UX Lead, Project PlayIT")
    r2.font.size = Pt(8.5)

    # Endorsement Block
    p_endorse = doc.add_paragraph()
    p_endorse.paragraph_format.space_before = Pt(14)
    p_endorse.paragraph_format.space_after = Pt(16)
    r = p_endorse.add_run("Noted and Endorsed by:")
    r.font.name = 'Calibri'
    r.font.size = Pt(10)
    r.font.bold = True

    tbl_end = doc.add_table(rows=1, cols=2)
    tbl_end.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_end.autofit = False
    tbl_end.columns[0].width = Inches(3.5)
    tbl_end.columns[1].width = Inches(3.5)

    ce0 = tbl_end.cell(0, 0)
    p = ce0.paragraphs[0]
    p.paragraph_format.line_spacing = 1.15
    r = p.add_run("MR. JOEMARIE C. AMPARO\n")
    r.font.bold = True
    r.font.size = Pt(10)
    r2 = p.add_run("Capstone Project Adviser\nCollege of Computer Studies\nCebu Institute of Technology – University")
    r2.font.size = Pt(8.5)

    ce1 = tbl_end.cell(0, 1)
    p = ce1.paragraphs[0]
    p.paragraph_format.line_spacing = 1.15
    r = p.add_run("CHERRY LYNN S. STA. ROMANA, DIT\n")
    r.font.bold = True
    r.font.size = Pt(10)
    r2 = p.add_run("Dean, College of Computer Studies\nCebu Institute of Technology – University")
    r2.font.size = Pt(8.5)

    # Approval Box for the Principal
    p_app = doc.add_paragraph()
    p_app.paragraph_format.space_before = Pt(14)
    p_app.paragraph_format.space_after = Pt(4)
    r = p_app.add_run("ACTION TAKEN BY THE INSTITUTION:")
    r.font.name = 'Calibri'
    r.font.size = Pt(9.5)
    r.font.bold = True

    tbl_act = doc.add_table(rows=1, cols=1)
    tbl_act.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_act.columns[0].width = Inches(7.1)
    c_act = tbl_act.cell(0, 0)
    set_cell_background(c_act, "FAFAFA")
    set_cell_margins(c_act, top=100, bottom=100, left=150, right=150)
    p = c_act.paragraphs[0]
    p.paragraph_format.line_spacing = 1.2
    r = p.add_run(
        "[   ] APPROVED for testing under agreed school guidelines.\n"
        "[   ] DISAPPROVED / RESCHEDULE due to: _____________________________________\n\n\n"
        "____________________________________________________          _______________________\n"
        "Principal / Authorized School Representative Signature              Date"
    )
    r.font.name = 'Calibri'
    r.font.size = Pt(8.5)

    doc.add_page_break()

    # ==================== TEMPLATE 2: GENERAL / MULTI-PARTNER TRANSMITTAL LETTER ====================
    add_letterhead(doc)

    p_lbl = doc.add_paragraph()
    r = p_lbl.add_run("[TEMPLATE 2: GENERAL / OPEN ENDORSEMENT FOR TUTORIAL CENTERS & COMMUNITY COHORTS]")
    r.font.name = 'Calibri'
    r.font.size = Pt(9)
    r.font.bold = True
    r.font.color.rgb = teal_accent
    p_lbl.paragraph_format.space_after = Pt(6)

    p_date = doc.add_paragraph()
    r = p_date.add_run("Date: September 14, 2026")
    r.font.name = 'Calibri'
    r.font.size = Pt(10)
    p_date.paragraph_format.space_after = Pt(10)

    p_addr = doc.add_paragraph()
    p_addr.paragraph_format.space_after = Pt(10)
    p_addr.paragraph_format.line_spacing = 1.15
    r = p_addr.add_run(
        "TO WHOM IT MAY CONCERN / COMMUNITY LEARNING FACILITATORS\n"
        "Subject: Academic Endorsement for PlayIT MVP User Validation Testing"
    )
    r.font.name = 'Calibri'
    r.font.size = Pt(10.5)
    r.font.bold = True
    r.font.color.rgb = dark_teal

    p_sal = doc.add_paragraph()
    r = p_sal.add_run("Dear Educator / Learning Partner / Respected Guardian,")
    r.font.name = 'Calibri'
    r.font.size = Pt(10)
    p_sal.paragraph_format.space_after = Pt(6)

    add_p(
        "This letter serves as an official endorsement from the College of Computer Studies at Cebu Institute of Technology – University (CIT-U) for our 4th-year BSIT Capstone Research team conducting field testing for the educational project:"
    )

    p_thesis2 = doc.add_paragraph()
    p_thesis2.paragraph_format.space_before = Pt(2)
    p_thesis2.paragraph_format.space_after = Pt(6)
    p_thesis2.paragraph_format.left_indent = Inches(0.4)
    r_th = p_thesis2.add_run('"PlayIT: An Offline-First Gamified Early Literacy Mobile Application Utilizing the Marungko Approach and Speech Recognition for Grade 1 Learners"')
    r_th.font.name = 'Calibri'
    r_th.font.size = Pt(10.5)
    r_th.font.bold = True
    r_th.font.italic = True
    r_th.font.color.rgb = dark_teal

    add_p(
        "As advised by our faculty and capstone committee, this validation seeks to gather feedback across diverse early learning settings (including private tutorials, community learning programs, and home-based practice) to reach our required cohort of 30 respondents."
    )
    add_p(
        "We cordially invite your participation in a brief, 15-minute evaluation session. The session consists of allowing the child to play 1–2 offline phonics levels on an Android device, followed by a simple satisfaction rating from the child and a standard usability survey (System Usability Scale) from the educator or parent."
    )

    add_bullet("Safety & Privacy", "No internet connection is required, no advertisements exist, and no personal data is collected or shared.")
    add_bullet("Voluntary Participation", "Learners and caregivers may pause or conclude their participation at any moment without penalty.")

    add_p(
        "Your assistance is instrumental in refining technology that promotes equitable, mother-tongue and phonics-based early reading literacy. Any courtesies extended to our student researchers are deeply appreciated."
    )

    p_resp = doc.add_paragraph()
    p_resp.paragraph_format.space_after = Pt(16)
    r = p_resp.add_run("Sincerely yours in academic service,")
    r.font.name = 'Calibri'
    r.font.size = Pt(10)

    # Re-use Endorsement Signatures
    tbl_end2 = doc.add_table(rows=1, cols=2)
    tbl_end2.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_end2.autofit = False
    tbl_end2.columns[0].width = Inches(3.5)
    tbl_end2.columns[1].width = Inches(3.5)

    ce0 = tbl_end2.cell(0, 0)
    p = ce0.paragraphs[0]
    p.paragraph_format.line_spacing = 1.15
    r = p.add_run("MR. JOEMARIE C. AMPARO\n")
    r.font.bold = True
    r.font.size = Pt(10)
    r2 = p.add_run("Capstone Project Adviser\nCollege of Computer Studies\nCebu Institute of Technology – University")
    r2.font.size = Pt(8.5)

    ce1 = tbl_end2.cell(0, 1)
    p = ce1.paragraphs[0]
    p.paragraph_format.line_spacing = 1.15
    r = p.add_run("THE PLAYIT RESEARCH TEAM\n")
    r.font.bold = True
    r.font.size = Pt(10)
    r2 = p.add_run("BS Information Technology, Batch 2027\nCollege of Computer Studies\nCebu Institute of Technology – University")
    r2.font.size = Pt(8.5)

    doc.add_page_break()

    # ==================== TEMPLATE 3: PARENT INFORMED CONSENT & ASSENT ====================
    add_letterhead(doc)

    p_lbl = doc.add_paragraph()
    r = p_lbl.add_run("[TEMPLATE 3: PARENT / GUARDIAN INFORMED CONSENT & CHILD ASSENT FORM]")
    r.font.name = 'Calibri'
    r.font.size = Pt(9)
    r.font.bold = True
    r.font.color.rgb = teal_accent
    p_lbl.paragraph_format.space_after = Pt(6)

    p_hdr3 = doc.add_paragraph()
    p_hdr3.paragraph_format.space_after = Pt(8)
    r = p_hdr3.add_run("INFORMED CONSENT FOR PARTICIPATION IN RESEARCH TESTING")
    r.font.name = 'Calibri'
    r.font.size = Pt(11)
    r.font.bold = True
    r.font.color.rgb = dark_teal

    add_p(
        "Magandang araw po / Maayong adlaw! We are 4th-year BSIT students from CIT-University researching early reading tools. We would like to invite your child to try PlayIT, a free, offline educational mobile game that teaches letter sounds and reading through fun interactive activities."
    )

    add_bullet("What will happen", "Your child will play the letter sound activity on an Android smartphone or tablet for approximately 10 to 15 minutes. Afterwards, we will ask them which smiley face shows how much they enjoyed playing.")
    add_bullet("Parent survey", "You will be asked to answer a short 10-item questionnaire (SUS) regarding whether the app was easy to navigate and useful for home practice.")
    add_bullet("Safety & Confidentiality", "PlayIT is 100% offline. No photos, videos, or voice recordings are uploaded to the internet or shared publicly. All information is kept completely anonymous and confidential under the Data Privacy Act.")
    add_bullet("Voluntary participation", "Participation is completely voluntary. You or your child can stop at any time.")

    p_cert = doc.add_paragraph()
    p_cert.paragraph_format.space_before = Pt(10)
    p_cert.paragraph_format.space_after = Pt(4)
    r = p_cert.add_run("PARENT / GUARDIAN WRITTEN CONSENT:")
    r.font.name = 'Calibri'
    r.font.size = Pt(9.5)
    r.font.bold = True

    tbl_pcon = doc.add_table(rows=1, cols=1)
    tbl_pcon.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_pcon.columns[0].width = Inches(7.1)
    c_pcon = tbl_pcon.cell(0, 0)
    set_cell_background(c_pcon, "FAFAFA")
    set_cell_margins(c_pcon, top=100, bottom=100, left=150, right=150)
    p = c_pcon.paragraphs[0]
    p.paragraph_format.line_spacing = 1.2
    r = p.add_run(
        "I have read and understood the information above. I voluntarily agree to allow my child to participate in this mobile application evaluation study.\n\n"
        "Child's Name / Nickname: ____________________________________     Age / Grade: ________________\n\n"
        "Parent / Guardian Name:  ____________________________________     Contact No:  ________________\n\n"
        "Parent / Guardian Signature: _________________________________     Date:        ________________"
    )
    r.font.name = 'Calibri'
    r.font.size = Pt(8.5)

    doc.save(filename)

if __name__ == "__main__":
    out_root = "playIT_Transmittal_Letter_Draft.docx"
    build_transmittal_docx(out_root)
    print(f"Generated: {out_root}")

    out_docs = os.path.join("docs", "validation-package", "playIT_Transmittal_Letter_Draft.docx")
    try:
        build_transmittal_docx(out_docs)
        print(f"Generated: {out_docs}")
    except Exception as e:
        print(f"Notice: Could not write to {out_docs} ({e})")
