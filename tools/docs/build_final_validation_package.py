#!/usr/bin/env python3
"""
build_final_validation_package.py

Renders the post-midterm IT411 Adviser Presentation & Week 12 Final User/Stakeholder
Validation deliverables from docs/final-validation/*.md into both .docx (python-docx +
md_to_docx.py) and .pdf (ReportLab, matching assemble_midterm_submission.py styling).

Outputs:
  1. docs/final-validation/rendered/
     - SMART_Goal_Matrix.docx & .pdf
     - Approved_Proposal_and_SMART_Goals.docx & .pdf
     - Final_Validation_Plan_and_Framework.docx & .pdf
     - Final_Validation_Instrument.docx & .pdf
     - Final_Validation_Results_and_Recommendations_Templates.docx & .pdf
     - Testing_Session_Log_and_Cohort_Registry_Template.docx & .pdf
     - Final_Validation_Results_and_Analysis_Report_Template.docx & .pdf
     - Recommendations_and_Post_Validation_Improvements_Template.docx & .pdf
  2. dist/PlayIT_IT411_Midterm_Submission/04_Final-User_Validation/
     - Root pre-adviser presentation files (.docx, .pdf, .md)
     - 01_Validation_Plan_and_Framework/
     - 02_Instruments_and_Data_Collection/
     - 03_Testing_Evidence/
     - 04_Results_and_Analysis/
     - 05_Recommendations_and_Improvements/

Usage:
    python3 tools/docs/build_final_validation_package.py
"""

from __future__ import annotations

import html
import re
import shutil
import sys
from pathlib import Path

import docx
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Inches, Pt
from reportlab.lib import colors
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import landscape, letter
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.units import inch
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import Image as RLImage
from reportlab.platypus import Paragraph, SimpleDocTemplate, Spacer, Table, TableStyle

SCRIPT_DIR = Path(__file__).resolve().parent
sys.path.insert(0, str(SCRIPT_DIR))

from md_to_docx import (  # noqa: E402
    BODY_FONT as DOCX_BODY_FONT,
    HEADING_SIZES,
    MUTED,
    PLUM,
    TEAL,
    TEXT,
    Block,
    Theme,
    add_field,
    add_inline,
    latex_to_text,
    page_break,
    parse_blocks,
    parse_inline,
    plain_text,
    render_blocks,
    set_run_font,
)

REPO_ROOT = SCRIPT_DIR.parents[1]
FINAL_VAL_DIR = REPO_ROOT / "docs" / "final-validation"
RENDERED_DIR = FINAL_VAL_DIR / "rendered"
DIST_VAL_DIR = REPO_ROOT / "dist" / "PlayIT_IT411_Midterm_Submission" / "04_Final-User_Validation"
LOGO_PATH = REPO_ROOT / "docs" / "submission" / "logocit-1.png"

FOOTER_TEXT = "IT411 Capstone & Research 2  ·  CIT-U College of Computer Studies"

PRIMARY = colors.HexColor("#1E293B")
ACCENT = colors.HexColor("#6C5CE7")
TEXT_COLOR = colors.HexColor("#2D3748")
MUTED_COLOR = colors.HexColor("#475569")
BORDER_COLOR = colors.HexColor("#CBD5E1")
ROW_ALT = colors.HexColor("#F8FAFC")

FONT_CANDIDATES = [
    ("C:/Windows/Fonts/arial.ttf", "C:/Windows/Fonts/arialbd.ttf",
     "C:/Windows/Fonts/ariali.ttf", "C:/Windows/Fonts/arialbi.ttf"),
    ("/mnt/c/Windows/Fonts/arial.ttf", "/mnt/c/Windows/Fonts/arialbd.ttf",
     "/mnt/c/Windows/Fonts/ariali.ttf", "/mnt/c/Windows/Fonts/arialbi.ttf"),
    ("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
     "/usr/share/fonts/truetype/dejavu/DejaVuSans-Oblique.ttf", "/usr/share/fonts/truetype/dejavu/DejaVuSans-BoldOblique.ttf"),
]
FALLBACK = {
    0x2192: "->", 0x2265: ">=", 0x2264: "<=", 0x2212: "-", 0x2013: "-", 0x2014: "-",
    0x2026: "...", 0x2018: "'", 0x2019: "'", 0x201C: '"', 0x201D: '"', 0x00A7: "S",
    0x2248: "~", 0x00D7: "x", 0x00B1: "+/-", 0x2260: "!=", 0x21D2: "=>",
    0x2610: "[ ]", 0x2611: "[x]", 0x2612: "[x]", 0x2713: "[v]",
}
ZERO_WIDTH = chr(0x200B)
PDF_BODY_FONT, PDF_BOLD_FONT = "Helvetica", "Helvetica-Bold"
_font_glyphs = None


def register_pdf_fonts():
    global PDF_BODY_FONT, PDF_BOLD_FONT, _font_glyphs
    for paths in FONT_CANDIDATES:
        if all(Path(p).exists() for p in paths):
            names = ["Body", "Body-Bold", "Body-Italic", "Body-BoldItalic"]
            for name, path in zip(names, paths):
                pdfmetrics.registerFont(TTFont(name, path))
            pdfmetrics.registerFontFamily("Body", normal="Body", bold="Body-Bold",
                                          italic="Body-Italic", boldItalic="Body-BoldItalic")
            PDF_BODY_FONT, PDF_BOLD_FONT = "Body", "Body-Bold"
            _font_glyphs = set(pdfmetrics.getFont("Body").face.charToGlyph)
            return paths[0]
    return "Helvetica (built in)"


def _can_draw(ch: str) -> bool:
    if ord(ch) < 128:
        return True
    if _font_glyphs is not None:
        return ord(ch) in _font_glyphs
    try:
        ch.encode("cp1252")
        return True
    except UnicodeEncodeError:
        return False


def drawable(s: str) -> str:
    s = s.replace(ZERO_WIDTH, "")
    return "".join(ch if _can_draw(ch) else FALLBACK.get(ord(ch), " ") for ch in s)


def plain_pdf(s: str) -> str:
    return drawable(html.escape(s, quote=False))


def md_pdf(s: str) -> str:
    """Convert inline Markdown + inline $...$ LaTeX to ReportLab Paragraph XML markup using parse_inline."""
    out = []
    for seg in parse_inline(s.replace(ZERO_WIDTH, "")):
        if seg.br:
            out.append("<br/>")
            continue
        if not seg.text:
            continue
        piece = drawable(html.escape(seg.text, quote=False))
        if seg.code:
            piece = f'<font name="Courier">{piece}</font>'
        if seg.italic:
            piece = f"<i>{piece}</i>"
        if seg.bold:
            piece = f"<b>{piece}</b>"
        out.append(piece)
    return "".join(out)


# ---------------------------------------------------------------------------
# Front-matter splitting & DOCX generation (matches render_submission_package.py)
# ---------------------------------------------------------------------------

def split_front_matter(lines: list[str]) -> tuple[list[str], list[str]]:
    for idx, line in enumerate(lines):
        if re.match(r"^\s*-{3,}\s*$", line):
            return lines[:idx], lines[idx + 1:]
    return [], lines


def parse_front(front: list[str]) -> dict:
    meta = {"doc_type": "", "project": "", "fields": []}
    for line in front:
        s = line.strip()
        if s.startswith("# "):
            meta["doc_type"] = s[2:].strip()
        elif s.startswith("## "):
            meta["project"] = re.sub(r"^Project:\s*", "", s[3:].strip())
        else:
            m = re.match(r"^\*\*(.+?):\*\*\s*(.*?)\s*$", s)
            if m:
                meta["fields"].append((m.group(1), m.group(2)))
    return meta


def _style_font(style, size=None, color=None, bold=None, italic=None):
    style.font.name = DOCX_BODY_FONT
    rpr = style.element.get_or_add_rPr()
    rfonts = rpr.find(qn("w:rFonts"))
    if rfonts is None:
        rfonts = OxmlElement("w:rFonts")
        rpr.insert(0, rfonts)
    for attr in list(rfonts.attrib):
        if attr.endswith("Theme") or attr.endswith("theme"):
            del rfonts.attrib[attr]
    for attr in ("w:ascii", "w:hAnsi", "w:cs", "w:eastAsia"):
        rfonts.set(qn(attr), DOCX_BODY_FONT)
    if size is not None:
        style.font.size = Pt(size)
    if color is not None:
        style.font.color.rgb = color
    if bold is not None:
        style.font.bold = bold
    if italic is not None:
        style.font.italic = italic


def setup_docx_styles(doc):
    normal = doc.styles["Normal"]
    _style_font(normal, 10, TEXT)
    normal.paragraph_format.space_before = Pt(0)
    normal.paragraph_format.space_after = Pt(4)
    normal.paragraph_format.line_spacing = 1.12
    spacing = {1: (18, 6), 2: (12, 4), 3: (10, 3), 4: (8, 2)}
    h_colors = {1: TEAL, 2: PLUM, 3: TEAL, 4: TEXT}
    for level in range(1, 5):
        st = doc.styles[f"Heading {level}"]
        _style_font(st, HEADING_SIZES[level], h_colors[level], bold=True, italic=False)
        st.paragraph_format.space_before = Pt(spacing[level][0])
        st.paragraph_format.space_after = Pt(spacing[level][1])
        st.paragraph_format.keep_with_next = True
        st.paragraph_format.line_spacing = 1.0


def setup_docx_page(section, short_title: str):
    section.page_width, section.page_height = Inches(8.5), Inches(11)
    for side in ("top_margin", "bottom_margin", "left_margin", "right_margin"):
        setattr(section, side, Inches(0.85))
    section.header_distance = Inches(0.4)
    section.footer_distance = Inches(0.4)
    section.different_first_page_header_footer = True

    hp = section.header.paragraphs[0]
    hp.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    run = hp.add_run(f"PlayIT  |  {short_title}")
    set_run_font(run, DOCX_BODY_FONT, 8, MUTED)
    p_pr = hp._p.get_or_add_pPr()
    border = OxmlElement("w:pBdr")
    bottom = OxmlElement("w:bottom")
    for k, v in (("w:val", "single"), ("w:sz", "4"), ("w:space", "4"), ("w:color", "C8D0D8")):
        bottom.set(qn(k), v)
    border.append(bottom)
    p_pr.append(border)

    fp = section.footer.paragraphs[0]
    fp.alignment = WD_ALIGN_PARAGRAPH.CENTER
    for text, field in ((FOOTER_TEXT + "  ·  Page ", "PAGE"), (" of ", "NUMPAGES")):
        set_run_font(fp.add_run(text), DOCX_BODY_FONT, 8, MUTED)
        add_field(fp, field, "1", font=DOCX_BODY_FONT, size=8, color=MUTED)


def build_docx_cover(doc, meta: dict, title: str):
    fields = dict(meta["fields"])
    place = fields.pop("Department", None) or fields.pop("Institution", None) or (
        "College of Computer Studies, Cebu Institute of Technology – University"
    )
    parts = [p.strip() for p in place.split(",")]
    college, institution = (parts[0], ", ".join(parts[1:])) if len(parts) > 1 else ("", place)

    def centered(text, size, color=TEXT, bold=False, before=0, after=4, caps=False):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.space_before = Pt(before)
        p.paragraph_format.space_after = Pt(after)
        add_inline(p, text.upper() if caps else text, size=size, color=color, bold=bold)
        return p

    if LOGO_PATH.exists():
        p_logo = doc.add_paragraph()
        p_logo.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_logo.paragraph_format.space_before = Pt(10)
        p_logo.paragraph_format.space_after = Pt(8)
        p_logo.add_run().add_picture(str(LOGO_PATH), width=Inches(1.6))
        centered(institution, 13, TEAL, bold=True, before=4, caps=True)
    else:
        centered(institution, 13, TEAL, bold=True, before=36, caps=True)

    if college:
        centered(college, 11, TEXT, after=2)
    if "Degree Program" in fields:
        centered(fields.pop("Degree Program"), 10, MUTED, after=0)

    centered(title, 20, TEAL, bold=True, before=36 if LOGO_PATH.exists() else 72, after=10)
    project = meta["project"] or "PlayIT — An Offline-First Gamified Early Literacy Mobile Application"
    if "—" in project:
        name, subtitle = (s.strip() for s in project.split("—", 1))
        centered(name, 16, PLUM, bold=True, before=6, after=2)
        centered(subtitle, 11.5, PLUM, after=0)
    else:
        centered(project, 13, PLUM, bold=True)

    spacer = doc.add_paragraph()
    spacer.paragraph_format.space_before = Pt(28 if LOGO_PATH.exists() else 56)
    table = doc.add_table(rows=0, cols=2)
    table.autofit = False
    for label, value in fields.items():
        row = table.add_row()
        row.cells[0].width, row.cells[1].width = Inches(1.85), Inches(4.65)
        lp, vp = row.cells[0].paragraphs[0], row.cells[1].paragraphs[0]
        for p in (lp, vp):
            p.paragraph_format.space_before = Pt(2)
            p.paragraph_format.space_after = Pt(2)
        add_inline(lp, label, size=9.5, color=TEAL, bold=True)
        add_inline(vp, value, size=9.5, color=TEXT)
    page_break(doc)


def build_docx_from_lines(lines: list[str], out_docx: Path, title: str, short_title: str, base_dir: Path):
    front, body = split_front_matter(lines)
    doc = docx.Document()
    setup_docx_styles(doc)
    setup_docx_page(doc.sections[0], short_title)
    build_docx_cover(doc, parse_front(front), title)
    render_blocks(doc, parse_blocks(body), Theme(base_dir=base_dir))
    out_docx.parent.mkdir(parents=True, exist_ok=True)
    doc.save(out_docx)
    print(f"[OK] DOCX  {out_docx.relative_to(REPO_ROOT)}")


# ---------------------------------------------------------------------------
# ReportLab PDF generation (matches assemble_midterm_submission.py)
# ---------------------------------------------------------------------------

def get_pdf_styles():
    return {
        "title": ParagraphStyle("DocTitle", fontName=PDF_BOLD_FONT, fontSize=15.5, leading=19, textColor=PRIMARY,
                                alignment=TA_LEFT, spaceAfter=4),
        "subtitle": ParagraphStyle("DocSubtitle", fontName=PDF_BODY_FONT, fontSize=9.5, leading=12.5,
                                   textColor=MUTED_COLOR, spaceAfter=8),
        "h1": ParagraphStyle("SectionH1", fontName=PDF_BOLD_FONT, fontSize=13, leading=16.5, textColor=PRIMARY,
                             spaceBefore=11, spaceAfter=5),
        "h2": ParagraphStyle("SectionH2", fontName=PDF_BOLD_FONT, fontSize=11.5, leading=14.5, textColor=PRIMARY,
                             spaceBefore=9, spaceAfter=4),
        "h3": ParagraphStyle("SectionH3", fontName=PDF_BOLD_FONT, fontSize=10, leading=13, textColor=PRIMARY,
                             spaceBefore=6, spaceAfter=3),
        "h4": ParagraphStyle("SectionH4", fontName=PDF_BOLD_FONT, fontSize=9.5, leading=12, textColor=TEXT_COLOR,
                             spaceBefore=5, spaceAfter=2),
        "body": ParagraphStyle("BodyDark", fontName=PDF_BODY_FONT, fontSize=9, leading=13, textColor=TEXT_COLOR,
                               spaceAfter=4.5),
        "quote": ParagraphStyle("Quote", fontName=PDF_BODY_FONT, fontSize=8.5, leading=12, textColor=MUTED_COLOR,
                                leftIndent=18, rightIndent=12, spaceAfter=5),
        "math": ParagraphStyle("MathBlock", fontName=PDF_BOLD_FONT, fontSize=9.5, leading=13, textColor=PRIMARY,
                               alignment=1, spaceBefore=4, spaceAfter=6),
        "bullet": ParagraphStyle("Bullet", fontName=PDF_BODY_FONT, fontSize=9, leading=12.5, textColor=TEXT_COLOR,
                                 leftIndent=14, bulletIndent=4, spaceAfter=2),
        "bullet2": ParagraphStyle("Bullet2", fontName=PDF_BODY_FONT, fontSize=9, leading=12.5, textColor=TEXT_COLOR,
                                  leftIndent=28, bulletIndent=18, spaceAfter=2),
        "th": ParagraphStyle("TableHeader", fontName=PDF_BOLD_FONT, fontSize=7.8, leading=10, textColor=colors.white),
        "td": ParagraphStyle("TableCell", fontName=PDF_BODY_FONT, fontSize=7.5, leading=10, textColor=TEXT_COLOR),
        "td_small": ParagraphStyle("TableCellSmall", fontName=PDF_BODY_FONT, fontSize=6.8, leading=8.8,
                                   textColor=TEXT_COLOR),
        "code": ParagraphStyle("Code", fontName="Courier", fontSize=7.5, leading=9.8, textColor=TEXT_COLOR,
                               backColor=ROW_ALT, borderColor=BORDER_COLOR, borderWidth=0.5, borderPadding=5,
                               spaceBefore=3, spaceAfter=6),
        "meta_label": ParagraphStyle("MetaLabel", fontName=PDF_BOLD_FONT, fontSize=8.2, leading=10.5,
                                     textColor=PRIMARY),
        "meta_val": ParagraphStyle("MetaVal", fontName=PDF_BODY_FONT, fontSize=8.2, leading=10.5,
                                   textColor=TEXT_COLOR),
    }


def _pdf_col_widths(rows: list[list[str]], page_w: float) -> list[float]:
    cols = max(len(r) for r in rows)
    weights = []
    for c in range(cols):
        cells = [plain_text(r[c]) if c < len(r) else "" for r in rows]
        max_len = max((len(x) for x in cells), default=4)
        avg_len = sum(len(x) for x in cells) / max(len(cells), 1)
        w = max(0.55, min(max_len * 0.4 + avg_len * 0.6, 95.0) ** 0.58)
        weights.append(w)
    total = sum(weights) or 1.0
    widths = [page_w * w / total for w in weights]
    min_w = min(0.52, page_w / max(cols * 2, 1))
    adjusted = [max(min_w, w) for w in widths]
    scale = page_w / sum(adjusted)
    return [w * scale for w in adjusted]


def build_pdf_from_lines(lines: list[str], out_pdf: Path, title: str, subtitle: str, use_landscape: bool = False):
    front, body = split_front_matter(lines)
    meta = parse_front(front)
    pagesize = landscape(letter) if use_landscape else letter
    page_w = (pagesize[0] - 72) / inch  # 0.5 in left/right margins
    styles = get_pdf_styles()
    story = []

    title_flow = [
        Paragraph(plain_pdf("CEBU INSTITUTE OF TECHNOLOGY - UNIVERSITY"),
                  ParagraphStyle("Inst", fontName=PDF_BOLD_FONT, fontSize=10, leading=12, textColor=PRIMARY)),
        Paragraph(plain_pdf("College of Computer Studies, IT411 Capstone & Research 2"),
                  ParagraphStyle("Dept", fontName=PDF_BODY_FONT, fontSize=8.5, leading=11, textColor=MUTED_COLOR)),
        Spacer(1, 4),
        Paragraph(plain_pdf(title), styles["title"]),
        Paragraph(plain_pdf(subtitle), styles["subtitle"]),
    ]
    if LOGO_PATH.exists():
        logo = RLImage(str(LOGO_PATH), width=1.05 * inch, height=1.05 * inch * (724 / 1024))
        t = Table([[logo, title_flow]], colWidths=[1.25 * inch, (page_w - 1.25) * inch])
        t.setStyle(TableStyle([
            ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
            ("LEFTPADDING", (0, 0), (-1, -1), 0), ("RIGHTPADDING", (0, 0), (-1, -1), 0),
            ("TOPPADDING", (0, 0), (-1, -1), 0), ("BOTTOMPADDING", (0, 0), (-1, -1), 0),
        ]))
        story.append(t)
    else:
        story.extend(title_flow)
    story.append(Spacer(1, 6))

    if meta["fields"]:
        meta_rows = [
            [Paragraph(plain_pdf(f"{k}:"), styles["meta_label"]), Paragraph(md_pdf(v), styles["meta_val"])]
            for k, v in meta["fields"]
        ]
        mt = Table(meta_rows, colWidths=[1.75 * inch, (page_w - 1.75) * inch])
        mt.setStyle(TableStyle([
            ("BACKGROUND", (0, 0), (-1, -1), ROW_ALT),
            ("BOX", (0, 0), (-1, -1), 1, BORDER_COLOR),
            ("INNERGRID", (0, 0), (-1, -1), 0.5, BORDER_COLOR),
            ("VALIGN", (0, 0), (-1, -1), "TOP"),
            ("TOPPADDING", (0, 0), (-1, -1), 3.5), ("BOTTOMPADDING", (0, 0), (-1, -1), 3.5),
            ("LEFTPADDING", (0, 0), (-1, -1), 6), ("RIGHTPADDING", (0, 0), (-1, -1), 6),
        ]))
        story.append(mt)
        story.append(Spacer(1, 8))

    blocks = parse_blocks(body)
    for b in blocks:
        if b.kind == "heading":
            st_key = {1: "h1", 2: "h1", 3: "h2", 4: "h3"}.get(b.level, "h4")
            story.append(Paragraph(md_pdf(b.text), styles[st_key]))
        elif b.kind == "para":
            story.append(Paragraph(md_pdf(b.text), styles["body"]))
        elif b.kind == "quote":
            story.append(Paragraph(md_pdf(b.text), styles["quote"]))
        elif b.kind == "math":
            story.append(Paragraph(plain_pdf(latex_to_text(b.text)), styles["math"]))
        elif b.kind == "code":
            code_text = "<br/>".join(plain_pdf(ln) for ln in b.lines)
            story.append(Paragraph(code_text, styles["code"]))
        elif b.kind == "list":
            for level, marker, item_text in b.items:
                st = styles["bullet" if level == 0 else "bullet2"]
                check = re.match(r"^\[( |x|X)\]\s+(.*)$", item_text)
                if check:
                    box = "[x] " if check.group(1).lower() == "x" else "[ ] "
                    story.append(Paragraph(plain_pdf(box) + md_pdf(check.group(2)), st))
                elif marker[0].isdigit():
                    story.append(Paragraph(plain_pdf(marker + " ") + md_pdf(item_text), st))
                else:
                    bullet_ch = chr(0x2022) if _can_draw(chr(0x2022)) else "-"
                    story.append(Paragraph(md_pdf(item_text), st, bulletText=bullet_ch))
        elif b.kind == "table":
            cols = max(len(r) for r in b.rows)
            td_style = styles["td_small"] if cols >= 6 else styles["td"]
            widths = _pdf_col_widths(b.rows, page_w)
            t_data = []
            for r_idx, row in enumerate(b.rows):
                padded = [row[c] if c < len(row) else "" for c in range(cols)]
                t_data.append([
                    Paragraph(md_pdf(cell), styles["th"] if r_idx == 0 else td_style)
                    for cell in padded
                ])
            tbl = Table(t_data, colWidths=[w * inch for w in widths], repeatRows=1)
            tbl.setStyle(TableStyle([
                ("BACKGROUND", (0, 0), (-1, 0), PRIMARY),
                ("GRID", (0, 0), (-1, -1), 0.5, BORDER_COLOR),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, ROW_ALT]),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("TOPPADDING", (0, 0), (-1, -1), 3), ("BOTTOMPADDING", (0, 0), (-1, -1), 3),
                ("LEFTPADDING", (0, 0), (-1, -1), 4), ("RIGHTPADDING", (0, 0), (-1, -1), 4),
            ]))
            story.append(tbl)
            story.append(Spacer(1, 7))

    def footer(canvas, doc_obj):
        canvas.saveState()
        canvas.setFont(PDF_BODY_FONT, 8)
        canvas.setFillColor(MUTED_COLOR)
        canvas.drawString(36, 20, drawable(f"PlayIT, Group 56: {title}"))
        canvas.drawRightString(pagesize[0] - 36, 20, f"Page {doc_obj.page}")
        canvas.restoreState()

    out_pdf.parent.mkdir(parents=True, exist_ok=True)
    pdf_doc = SimpleDocTemplate(
        str(out_pdf), pagesize=pagesize,
        leftMargin=36, rightMargin=36, topMargin=36, bottomMargin=44,
        title=title, author="PlayIT Capstone Team (Group 56)",
    )
    pdf_doc.build(story, onFirstPage=footer, onLaterPages=footer)
    print(f"[OK] PDF   {out_pdf.relative_to(REPO_ROOT)}")


# ---------------------------------------------------------------------------
# Split Week 12 Templates into Standalone Subfolder Documents
# ---------------------------------------------------------------------------

def extract_section_markdown(full_lines: list[str], section_heading_prefix: str, custom_title: str) -> list[str]:
    front, body = split_front_matter(full_lines)
    updated_front = []
    for line in front:
        if line.startswith("# "):
            updated_front.append(f"# {custom_title}")
        else:
            updated_front.append(line)
    start_idx = None
    end_idx = len(body)
    for idx, line in enumerate(body):
        if line.startswith(section_heading_prefix):
            start_idx = idx
            break
    if start_idx is None:
        raise SystemExit(f"Section heading '{section_heading_prefix}' not found")
    for idx in range(start_idx + 1, len(body)):
        if line_is_h2 := (body[idx].startswith("## ") and not body[idx].startswith("### ")):
            end_idx = idx
            break
    return updated_front + ["---", ""] + body[start_idx:end_idx]


def main():
    font_used = register_pdf_fonts()
    print(f"=== Building 04_Final-User_Validation Package (PDF font: {font_used}) ===")
    RENDERED_DIR.mkdir(parents=True, exist_ok=True)
    DIST_VAL_DIR.mkdir(parents=True, exist_ok=True)

    # 1. Core 5 Markdown sources in docs/final-validation/
    core_specs = [
        {
            "stem": "SMART_Goal_Matrix",
            "title": "SMART Goal Traceability & Verification Matrix",
            "subtitle": "Deliverable 04: mapping approved SMART goals to indicators, instruments, and criteria",
            "short": "SMART Goal Matrix",
            "landscape_pdf": True,
        },
        {
            "stem": "Approved_Proposal_and_SMART_Goals",
            "title": "Originally Approved Proposal & SMART Goals Baseline",
            "subtitle": "Deliverable 04: verbatim approved SMART goals and v2.0-to-v3.0 alignment",
            "short": "Approved Proposal & SMART Goals",
            "landscape_pdf": False,
        },
        {
            "stem": "Final_Validation_Plan_and_Framework",
            "title": "Final User & Stakeholder Validation Plan and Framework",
            "subtitle": "Deliverable 04: research questions, cohorts, evaluation models, and statistical methods",
            "short": "Final Validation Plan",
            "landscape_pdf": False,
        },
        {
            "stem": "Final_Validation_Instrument",
            "title": "Final User & Stakeholder Validation Instrument Package",
            "subtitle": "Deliverable 04: Instruments A (Child), B (Teacher), C (Telemetry), D (Scenarios), E (Parent SUS)",
            "short": "Final Validation Instrument",
            "landscape_pdf": False,
        },
        {
            "stem": "Final_Validation_Results_and_Recommendations_Templates",
            "title": "Final Validation Evidence, Results & Recommendations Templates",
            "subtitle": "Week 12 Deliverables 03, 04 & 05: testing session logs, results report, and improvements",
            "short": "Validation Results & Templates",
            "landscape_pdf": False,
        },
    ]

    for spec in core_specs:
        md_path = FINAL_VAL_DIR / f"{spec['stem']}.md"
        lines = md_path.read_text(encoding="utf-8").splitlines()
        docx_out = RENDERED_DIR / f"{spec['stem']}.docx"
        pdf_out = RENDERED_DIR / f"{spec['stem']}.pdf"
        build_docx_from_lines(lines, docx_out, spec["title"], spec["short"], FINAL_VAL_DIR)
        build_pdf_from_lines(lines, pdf_out, spec["title"], spec["subtitle"], use_landscape=spec["landscape_pdf"])

        # Copy to dist/PlayIT_IT411_Midterm_Submission/04_Final-User_Validation/
        shutil.copyfile(md_path, DIST_VAL_DIR / md_path.name)
        shutil.copyfile(docx_out, DIST_VAL_DIR / docx_out.name)
        shutil.copyfile(pdf_out, DIST_VAL_DIR / pdf_out.name)

    # 2. Split Week 12 Subfolder Templates from Final_Validation_Results_and_Recommendations_Templates.md
    tpl_lines = (FINAL_VAL_DIR / "Final_Validation_Results_and_Recommendations_Templates.md").read_text(
        encoding="utf-8"
    ).splitlines()
    sub_templates = [
        {
            "prefix": "## Folder 03 Template",
            "stem": "TESTING_EVIDENCE_INDEX",
            "title": "Actual User & Stakeholder Testing Evidence Index (`03_Testing_Evidence`)",
            "subtitle": "Week 12 Deliverable 03: participant registry, session audit checklist, and build verification",
            "short": "03 Testing Evidence Index",
            "subfolder": "03_Testing_Evidence",
        },
        {
            "prefix": "## Folder 04 Template",
            "stem": "FINAL_VALIDATION_RESULTS_AND_ANALYSIS",
            "title": "Final Validation Results and Statistical Analysis Report (`04_Results_and_Analysis`)",
            "subtitle": "Week 12 Deliverable 04: findings per research question (RQ1-RQ5) and SMART goal",
            "short": "04 Results & Analysis",
            "subfolder": "04_Results_and_Analysis",
        },
        {
            "prefix": "## Folder 05 Template",
            "stem": "RECOMMENDATIONS_AND_POST_VALIDATION_IMPROVEMENTS",
            "title": "Recommendations and Post-Validation Improvements (`05_Recommendations_and_Improvements`)",
            "subtitle": "Week 12 Deliverable 05: stakeholder concerns, prioritized actions, and roadmap",
            "short": "05 Recommendations",
            "subfolder": "05_Recommendations_and_Improvements",
        },
    ]

    for sub in sub_templates:
        sec_lines = extract_section_markdown(tpl_lines, sub["prefix"], sub["title"])
        md_out = RENDERED_DIR / f"{sub['stem']}.md"
        docx_out = RENDERED_DIR / f"{sub['stem']}.docx"
        pdf_out = RENDERED_DIR / f"{sub['stem']}.pdf"
        md_out.write_text("\n".join(sec_lines) + "\n", encoding="utf-8", newline="\n")
        build_docx_from_lines(sec_lines, docx_out, sub["title"], sub["short"], FINAL_VAL_DIR)
        build_pdf_from_lines(sec_lines, pdf_out, sub["title"], sub["subtitle"], use_landscape=False)

        sub_dir = DIST_VAL_DIR / sub["subfolder"]
        sub_dir.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(md_out, sub_dir / md_out.name)
        shutil.copyfile(docx_out, sub_dir / docx_out.name)
        shutil.copyfile(pdf_out, sub_dir / pdf_out.name)

    # 3. Populate 01_Validation_Plan_and_Framework/ and 02_Instruments_and_Data_Collection/
    dir_01 = DIST_VAL_DIR / "01_Validation_Plan_and_Framework"
    dir_02 = DIST_VAL_DIR / "02_Instruments_and_Data_Collection"
    dir_01.mkdir(parents=True, exist_ok=True)
    dir_02.mkdir(parents=True, exist_ok=True)

    for stem in ("SMART_Goal_Matrix", "Approved_Proposal_and_SMART_Goals", "Final_Validation_Plan_and_Framework"):
        for ext in (".pdf", ".docx", ".md"):
            src = RENDERED_DIR / f"{stem}{ext}" if ext != ".md" else FINAL_VAL_DIR / f"{stem}.md"
            shutil.copyfile(src, dir_01 / src.name)

    for ext in (".pdf", ".docx", ".md"):
        src = (
            RENDERED_DIR / f"Final_Validation_Instrument{ext}"
            if ext != ".md"
            else FINAL_VAL_DIR / "Final_Validation_Instrument.md"
        )
        shutil.copyfile(src, dir_02 / src.name)

    data_guide = dir_02 / "README_Data_Collection_Guide.md"
    data_guide.write_text(
        """# 02_Instruments_and_Data_Collection — Evidence Organization Guide

After the Capstone Adviser (Mr. Joemarie C. Amparo) reviews and approves `Final_Validation_Instrument.pdf` during the verification presentation, store the completed Week 12 data collection artifacts in this subfolder:

1. **`Final_Validation_Instrument.pdf` / `.docx`** — The adviser-approved 5-instrument validation package (Instruments A, B, C, D, and E).
2. **`completed_teacher_audits/`** — Completed **Instrument B** forms (Part B1 Phoneme/Audio Audit, Part B2 ASR Confusion Matrix, Part B3 Content Validity Index & TAM Rubric) from Cohort 2 ($n = 3$–$5$ teachers).
3. **`completed_child_observation_logs/`** — Completed **Instrument A** Silent-Observer Task Logs and Facial Affect Scales from Cohort 1 ($n = 15$–$20$ Grade 1 learners).
4. **`completed_parent_sus_forms/`** — Completed **Instrument E** System Usability Scale (SUS) and Diagnostic Clarity Questionnaires from Cohort 3 ($n = 10$–$12$ parents/guardians).
5. **`exported_device_csv_logs/`** — Anonymized CSV exports (`say_it_attempts.csv`, `find_it_attempts.csv`, `blend_it_attempts.csv`, `lesson_progress.csv`, `phoneme_summary.csv`) extracted via `python3 tools/perf/export_validation_telemetry.py` (**Instrument C**).
""",
        encoding="utf-8",
        newline="\n",
    )
    print(f"=== 04_Final-User_Validation package complete in {DIST_VAL_DIR.relative_to(REPO_ROOT)} ===")


if __name__ == "__main__":
    main()
