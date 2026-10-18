#!/usr/bin/env python3
"""
assemble_midterm_submission.py

Builds the IT411 midterm package from the project's own records, so the reports say what the STD says.

1. dist/PlayIT_IT411_Midterm_Submission/ (the Google Drive folder)
   01_Software_and_Deployment/  APK H, deployment guide, README.pdf, refactoring evidence, feature matrix,
                                APK inspection
   02_Updated_Documentation/    the rendered SRS, SDD, SPMP, STD and MVP form from docs/submission/, and the
                                Requirements Inventory and Traceability Matrix cut from the SRS
   03_Testing_and_QA/           functional; integration, system and regression; performance, usability,
                                defect and acceptance reports; raw JUnit logs
   The acceptance report is generated unsigned; the team lead signs it afterwards
   (tools/docs/apply_signatures.py, or by hand).
2. dist/PlayIT/ (the public academic repository, github.com/zendrix-hub/playIt)
   The tracked files that the build and the tests need, the image and audio release manifests,
   README.md and its screenshots. An existing dist/PlayIT/.git is kept, so the result can be
   committed on top of the published history.

Sources: docs/STD_v1.0.md (sections 3, 4, 5.1, 5.5, 7 and 8), its evidence in docs/std/evidence/,
the MVP Validation Findings form, the seeded curriculum in DatabaseModule.kt, the release manifests
and git. The script stops if the APK's checksum or the JUnit totals disagree with the STD. Anything
the STD lists as not run is reported as not measured, never as a result.

Usage (from the repo root; Python 3 with reportlab):
    gradlew :app:recordRoborazziDebug --tests "com.playit.app.screenshot.*"   # README screenshots
    python tools/docs/assemble_midterm_submission.py
"""

import hashlib
import html
import json
import re
import shutil
import subprocess
import sys
import urllib.parse
import xml.etree.ElementTree as ET
from datetime import date
from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_LEFT
from reportlab.lib.pagesizes import landscape, letter
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.units import inch
from reportlab.lib.utils import ImageReader
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import Image as RLImage
from reportlab.platypus import KeepTogether, Paragraph, SimpleDocTemplate, Spacer, Table, TableStyle

REPO_ROOT = Path(__file__).resolve().parent.parent.parent
DIST_DIR = REPO_ROOT / "dist"
SUBMISSION_DIR = DIST_DIR / "PlayIT_IT411_Midterm_Submission"
CLEAN_REPO_DIR = DIST_DIR / "PlayIT"
LOGO_PATH = REPO_ROOT / "docs" / "submission" / "logocit-1.png"
STD_PATH = REPO_ROOT / "docs" / "STD_v1.0.md"
MVP_FORM_PATH = REPO_ROOT / "docs" / "MVP_Validation_Findings_and_Refactoring_Priorities_Filled.md"
EVIDENCE_DIR = REPO_ROOT / "docs" / "std" / "evidence" / "2026-10-14-0320385"
APK_SRC = REPO_ROOT / "playit-debug.apk"
APK_H_COMMIT = "0320385"
SEEDS_PATH = REPO_ROOT / "app" / "src" / "main" / "java" / "com" / "playit" / "app" / "di" / "DatabaseModule.kt"
VERSIONS_PATH = REPO_ROOT / "gradle" / "libs.versions.toml"
ROBORAZZI_DIR = REPO_ROOT / "app" / "build" / "outputs" / "roborazzi"
PUBLIC_REPO_URL = "https://github.com/zendrix-hub/playIt"

PROJECT = "PlayIT: An Offline-First Gamified Early Literacy Mobile Application"
COURSE = "IT411 Capstone & Research 2, Section G7"
TEAM = "Group 56: Riva, Zendrix (Team Lead); Palis, John James; Miel, Kaysean; Durano, Ashlee Skye; Bien, Erik Samuel"
ADVISER = "Mr. Joemarie C. Amparo"
INSTRUCTOR = "Mr. Ralph P. Laviste"
TODAY = date.today()
PREPARED_ON = f"{TODAY:%B} {TODAY.day}, {TODAY.year}"

PRIMARY = colors.HexColor("#1E293B")      # ModernBorder (slate)
ACCENT = colors.HexColor("#6C5CE7")       # PrimaryJoy
TEXT_COLOR = colors.HexColor("#2D3748")
MUTED_COLOR = colors.HexColor("#475569")
BORDER_COLOR = colors.HexColor("#CBD5E1")
ROW_ALT = colors.HexColor("#F8FAFC")

PAGE_W = 7.5  # usable width of a portrait letter page with 0.5 in margins, in inches

# ==============================================================================
# FONTS AND TEXT
# ==============================================================================

# The STD uses arrows and comparison signs that Helvetica cannot draw, so a TrueType font is used
# when one is installed. Characters the font lacks fall back to ASCII.
FONT_CANDIDATES = [
    ("C:/Windows/Fonts/arial.ttf", "C:/Windows/Fonts/arialbd.ttf",
     "C:/Windows/Fonts/ariali.ttf", "C:/Windows/Fonts/arialbi.ttf"),
    ("/System/Library/Fonts/Supplemental/Arial.ttf", "/System/Library/Fonts/Supplemental/Arial Bold.ttf",
     "/System/Library/Fonts/Supplemental/Arial Italic.ttf", "/System/Library/Fonts/Supplemental/Arial Bold Italic.ttf"),
    ("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
     "/usr/share/fonts/truetype/dejavu/DejaVuSans-Oblique.ttf", "/usr/share/fonts/truetype/dejavu/DejaVuSans-BoldOblique.ttf"),
]
FALLBACK = {0x2192: "->", 0x2265: ">=", 0x2264: "<=", 0x2212: "-", 0x2013: "-", 0x2014: "-", 0x2026: "...",
            0x2018: "'", 0x2019: "'", 0x201C: '"', 0x201D: '"', 0x00A7: "S", 0x2248: "~"}
ZERO_WIDTH = chr(0x200B)
BODY_FONT, BOLD_FONT = "Helvetica", "Helvetica-Bold"
_font_glyphs = None  # code points the body font can draw; None means Helvetica (WinAnsi)


def register_fonts():
    global BODY_FONT, BOLD_FONT, _font_glyphs
    for paths in FONT_CANDIDATES:
        if all(Path(p).exists() for p in paths):
            names = ["Body", "Body-Bold", "Body-Italic", "Body-BoldItalic"]
            for name, path in zip(names, paths):
                pdfmetrics.registerFont(TTFont(name, path))
            pdfmetrics.registerFontFamily("Body", normal="Body", bold="Body-Bold",
                                          italic="Body-Italic", boldItalic="Body-BoldItalic")
            BODY_FONT, BOLD_FONT = "Body", "Body-Bold"
            _font_glyphs = set(pdfmetrics.getFont("Body").face.charToGlyph)
            return paths[0]
    return "Helvetica (built in)"


def _can_draw(ch):
    if ord(ch) < 128:
        return True
    if _font_glyphs is not None:
        return ord(ch) in _font_glyphs
    try:
        ch.encode("cp1252")
        return True
    except UnicodeEncodeError:
        return False


def drawable(s):
    s = s.replace(ZERO_WIDTH, "")
    return "".join(ch if _can_draw(ch) else FALLBACK.get(ord(ch), "?") for ch in s)


def plain(s):
    """Plain text to Paragraph markup."""
    return drawable(html.escape(s, quote=False))


def md(s):
    """One line of the STD's Markdown (bold, italics, code, <br>, links) to Paragraph markup."""
    br, code_mark = chr(0), chr(1)
    s = s.replace(ZERO_WIDTH, "").replace("<br>", br)
    codes = []

    def stash(m):
        codes.append(m.group(1))
        return f"{code_mark}{len(codes) - 1}{code_mark}"

    s = re.sub(r"`([^`]+)`", stash, s)
    s = html.escape(s, quote=False)
    s = re.sub(r"\[([^\]]+)\]\([^)]+\)", r"\1", s)
    s = re.sub(r"\*\*(.+?)\*\*", r"<b>\1</b>", s)
    s = re.sub(r"(?<![\w*])\*([^*\s][^*]*?)\*(?![\w*])", r"<i>\1</i>", s)
    s = re.sub(code_mark + r"(\d+)" + code_mark,
               lambda m: '<font name="Courier">' + html.escape(codes[int(m.group(1))], quote=False) + "</font>", s)
    return drawable(s.replace(br, "<br/>"))


# ==============================================================================
# SOURCES: STD, EVIDENCE, MVP FORM, CURRICULUM, MANIFESTS, GIT
# ==============================================================================

def heading_level(line):
    return len(line) - len(line.lstrip("#")) if line.startswith("#") else 0


def section_lines(text, heading_prefix):
    """The lines under the first heading that starts with heading_prefix, up to the next heading of the same or a higher level."""
    lines = text.splitlines()
    starts = [i for i, line in enumerate(lines) if line.startswith(heading_prefix)]
    if not starts:
        sys.exit(f"Error: heading '{heading_prefix}' not found in {STD_PATH.name}")
    start = starts[0]
    level = heading_level(lines[start])
    end = next((j for j in range(start + 1, len(lines)) if 0 < heading_level(lines[j]) <= level), len(lines))
    return lines[start + 1:end]


def md_tables(lines):
    """Every Markdown table in lines, as lists of rows (header row first, separator rows dropped)."""
    tables, rows = [], []
    for line in lines + [""]:
        if line.startswith("|"):
            cells = [c.strip() for c in line.strip().strip("|").split("|")]
            if not all(re.fullmatch(r":?-+:?", c) for c in cells):
                rows.append(cells)
        elif rows:
            tables.append(rows)
            rows = []
    return tables


def md_paragraphs(lines):
    """Prose lines of a section: no tables, headings, figures or code."""
    out, in_code = [], False
    for line in lines:
        if line.startswith("```"):
            in_code = not in_code
            continue
        if in_code or not line.strip() or line.startswith(("|", "#", "![", "---")):
            continue
        out.append(line.rstrip())
    return out


def leading_int(cell):
    m = re.search(r"\d[\d,]*", cell)
    return int(m.group(0).replace(",", "")) if m else None


class Std:
    def __init__(self):
        self.text = STD_PATH.read_text(encoding="utf-8")
        self.traceability = md_tables(section_lines(self.text, "## 3."))[0]
        self.not_run = md_tables(section_lines(self.text, "### 4.8"))[0]
        self.execution = {md(r[0]).replace("<b>", "").replace("</b>", ""): r[1]
                          for r in md_tables(section_lines(self.text, "### 5.1"))[0][1:]}
        self.device_tests = md_tables(section_lines(self.text, "### 5.5"))[0]
        self.severity = md_paragraphs(section_lines(self.text, "## 7."))[0]
        self.incidents = md_tables(section_lines(self.text, "### 7.1"))[0]
        self.defects = md_tables(section_lines(self.text, "### 7.2"))[0]
        self.summary_bullets = [l for l in section_lines(self.text, "### 8.1") if l.lstrip().startswith("- ")]
        self.assessment = md_paragraphs(section_lines(self.text, "### 8.2"))
        self.tc_status = {}
        sec4 = section_lines(self.text, "## 4.")
        cut = next(i for i, line in enumerate(sec4) if line.startswith("### 4.8"))
        for table in md_tables(sec4[:cut]):
            for row in table[1:]:
                self.tc_status[row[0].strip("*")] = row[-1]
        self.not_run_by_id = {row[0].strip("*"): row for row in self.not_run[1:]}

    def status(self, tc_id):
        if tc_id not in self.tc_status:
            sys.exit(f"Error: {tc_id} is not in STD section 4")
        return self.tc_status[tc_id]


def junit_results():
    """Totals and per-class results of the STD run, read from its raw JUnit XML."""
    per_class, totals = {}, {"tests": 0, "failed": 0, "skipped": 0, "classes": 0}
    for xml_path in sorted((EVIDENCE_DIR / "junit").glob("TEST-*.xml")):
        root = ET.parse(xml_path).getroot()
        tests, skipped = int(root.get("tests")), int(root.get("skipped"))
        failed = int(root.get("failures")) + int(root.get("errors"))
        per_class[root.get("name").rsplit(".", 1)[-1]] = (tests, failed, skipped)
        totals["tests"] += tests
        totals["failed"] += failed
        totals["skipped"] += skipped
        totals["classes"] += 1
    totals["passed"] = totals["tests"] - totals["failed"] - totals["skipped"]
    layout = [v for k, v in per_class.items() if k.startswith("LayoutMatrix")]
    totals["layout_runs"] = sum(t for t, _, _ in layout)
    totals["layout_skipped"] = sum(s for _, _, s in layout)
    totals["layout_passed"] = sum(t - f - s for t, f, s in layout)
    return totals, per_class


def check_junit_against_std(totals, std):
    expected = {
        "tests": leading_int(std.execution["Test cases run"]),
        "passed": leading_int(std.execution["Passed"]),
        "failed": leading_int(std.execution["Failed or errors"]),
        "skipped": leading_int(std.execution["Skipped"]),
        "classes": leading_int(std.execution["Test classes"]),
    }
    for key, value in expected.items():
        if totals[key] != value:
            sys.exit(f"Error: JUnit logs give {key}={totals[key]}, STD section 5.1 says {value}")


def class_result(per_class, name):
    tests, failed, skipped = per_class[name]
    return f"{tests - failed - skipped} of {tests} passed" + (f", {skipped} skipped" if skipped else "")


def apk_record():
    """The APK inspection recorded for the STD (TC-SEC-01)."""
    text = (EVIDENCE_DIR / "apk_inspection.txt").read_text(encoding="utf-8")
    sha = re.search(r"## sha256sum\s+([0-9a-f]{64})\s+\*?(\S+)", text)
    size = re.search(r"## size \(bytes\)\s+(\d+)", text)
    return {"text": text, "sha256": sha.group(1), "name": sha.group(2), "size": int(size.group(1))}


def sha256_of(path):
    digest = hashlib.sha256()
    with open(path, "rb") as f:
        for block in iter(lambda: f.read(1 << 20), b""):
            digest.update(block)
    return digest.hexdigest()


def library_version(key):
    m = re.search(rf'^{key}\s*=\s*"([^"]+)"', VERSIONS_PATH.read_text(encoding="utf-8"), re.M)
    return m.group(1) if m else "?"


def read_curriculum():
    """Letters, key words, chapters and Blend It words as seeded in DatabaseModule.kt."""
    src = SEEDS_PATH.read_text(encoding="utf-8")
    letters = {int(m.group(1)): (m.group(2), m.group(3)) for m in re.finditer(
        r"VALUES \((\d+), '(\w+)', '[^']*', '[^']*', '(\w+)'\)", src)}
    groups = [[int(x) for x in g.split(",")] for g in re.findall(r"listOf\(([\d, ]+)\),?\s*// Group \d", src)]
    words = [(int(g), w) for g, w in re.findall(r'BlendItWord\(\d+, (\d+), "(\w+)"', src)]
    if len(letters) != 26 or len(groups) != 7 or len(words) != 33:
        sys.exit(f"Error: curriculum parse found {len(letters)} letters, {len(groups)} chapters, {len(words)} words")
    chapters = []
    for number, ids in enumerate(groups, start=1):
        chapters.append({
            "number": number,
            "letters": [letters[i] for i in ids],
            "words": [w for g, w in words if g == number],
        })
    return chapters


def release_counts():
    pictures, generators, release_dates = set(), {}, []
    for manifest in sorted((REPO_ROOT / "docs" / "image-release").glob("*/manifest.json")):
        data = json.loads(manifest.read_text(encoding="utf-8"))
        release_dates.append(manifest.parent.name[:10])
        for item in data.get("images", []):
            pictures.add(item["appPath"])
            tool = item.get("generator", "").split(" (")[0] or "not recorded"
            generators[tool] = generators.get(tool, 0) + 1
    clips = 0
    for manifest in sorted((REPO_ROOT / "docs" / "audio-release").glob("*/manifest.json")):
        clips += len(json.loads(manifest.read_text(encoding="utf-8")).get("clips", []))
    return {"pictures": len(pictures), "generators": generators, "clips": clips,
            "picture_dates": release_dates}


def git(*args):
    # UTF-8, not the locale's code page: some tracked paths are not ASCII (letter_ñ.png).
    return subprocess.run(["git", "-c", "core.quotepath=false", *args], cwd=REPO_ROOT, capture_output=True,
                          encoding="utf-8", check=True).stdout


def changes_since_apk_h():
    """Files under app/ that changed after APK H, split into PNG pictures and everything else."""
    names = [n for n in git("diff", "--name-only", APK_H_COMMIT, "HEAD", "--", "app/").splitlines() if n]
    pictures = [n for n in names if n.endswith(".png")]
    return pictures, [n for n in names if not n.endswith(".png")]


def count_domain_android_imports():
    domain = REPO_ROOT / "app" / "src" / "main" / "java" / "com" / "playit" / "app" / "domain"
    return sum(f.read_text(encoding="utf-8").count("\nimport android.") for f in domain.rglob("*.kt"))


def count_repositories():
    repo_dir = REPO_ROOT / "app" / "src" / "main" / "java" / "com" / "playit" / "app" / "domain" / "repository"
    return sum(1 for f in repo_dir.glob("*.kt") if re.search(r"^interface \w+Repository\b", f.read_text(encoding="utf-8"), re.M))


# Card commits that changed the app between the MVP and APK H (docs/evidence-log.md). The subject
# and date are read from git, so the table shows what each commit says about itself.
CARD_COMMITS = [
    ("01", "930e9ed"), ("02", "cf235dc"), ("03", "467d52c"), ("04", "68887ab"), ("05", "85a6ca2"),
    ("06", "4deb625"), ("07", "18f7fb8"), ("07b", "a17969d"), ("11", "0d9ad3a"), ("12", "ef03bea"),
    ("10", "b8e859d"), ("14", "56907fd"), ("17", "082caf4"), ("13", "b909329"), ("18", "5489293"),
    ("19", "dc6098e"), ("03b", "339f309"), ("20", "9bb8cb7"), ("22", "3be745b"), ("21", "9bac8ca"),
    ("23", "dd42582"), ("15", "299cea2"), ("24", "20efb22"), ("28", "ac8e8c7"), ("28b", "ed997b6"),
    ("28c", "0573906"), ("28d", "e7f870e"), ("15b", "b99d839"), ("09c", "0dfc4a2"), ("29b", "89604d7"),
    ("31e", "1420c84"), ("31d", "cdbbef9"), ("31a", "8f49279"), ("31f", "0320385"),
]


def card_commit_rows():
    rows = []
    for card, sha in CARD_COMMITS:
        out = git("log", "-1", "--date=short", "--format=%h%x09%ad%x09%s", sha).strip()
        short, day, subject = out.split("\t", 2)
        rows.append((day, card, short, subject))
    return sorted(rows)


# ==============================================================================
# PDF BUILDING BLOCKS
# ==============================================================================

def get_pdf_styles():
    return {
        "title": ParagraphStyle("DocTitle", fontName=BOLD_FONT, fontSize=17, leading=21, textColor=PRIMARY,
                                alignment=TA_LEFT, spaceAfter=4),
        "subtitle": ParagraphStyle("DocSubtitle", fontName=BODY_FONT, fontSize=10, leading=13,
                                   textColor=MUTED_COLOR, spaceAfter=8),
        "h2": ParagraphStyle("SectionH2", fontName=BOLD_FONT, fontSize=12.5, leading=16, textColor=PRIMARY,
                             spaceBefore=10, spaceAfter=5),
        "h3": ParagraphStyle("SectionH3", fontName=BOLD_FONT, fontSize=10.5, leading=13, textColor=PRIMARY,
                             spaceBefore=6, spaceAfter=3),
        "body": ParagraphStyle("BodyDark", fontName=BODY_FONT, fontSize=9.5, leading=13.5, textColor=TEXT_COLOR,
                               spaceAfter=5),
        "bullet": ParagraphStyle("Bullet", fontName=BODY_FONT, fontSize=9.5, leading=13.5, textColor=TEXT_COLOR,
                                 leftIndent=14, bulletIndent=4, spaceAfter=2),
        "bullet2": ParagraphStyle("Bullet2", fontName=BODY_FONT, fontSize=9.5, leading=13.5, textColor=TEXT_COLOR,
                                  leftIndent=28, bulletIndent=18, spaceAfter=2),
        "th": ParagraphStyle("TableHeader", fontName=BOLD_FONT, fontSize=8.5, leading=11, textColor=colors.white),
        "td": ParagraphStyle("TableCell", fontName=BODY_FONT, fontSize=8, leading=10.5, textColor=TEXT_COLOR),
        "td_small": ParagraphStyle("TableCellSmall", fontName=BODY_FONT, fontSize=7, leading=9, textColor=TEXT_COLOR),
        "code": ParagraphStyle("Code", fontName="Courier", fontSize=8, leading=10.5, textColor=TEXT_COLOR,
                               backColor=ROW_ALT, borderColor=BORDER_COLOR, borderWidth=0.5, borderPadding=5,
                               spaceBefore=4, spaceAfter=8),
        "caption": ParagraphStyle("Caption", fontName=BODY_FONT, fontSize=7.5, leading=9.5, textColor=MUTED_COLOR,
                                  alignment=1),
        "cell_title": ParagraphStyle("CellTitle", fontName=BOLD_FONT, fontSize=8.5, leading=11, textColor=PRIMARY,
                                     alignment=1),
        "meta_label": ParagraphStyle("MetaLabel", fontName=BOLD_FONT, fontSize=8.5, leading=11, textColor=PRIMARY),
        "meta_val": ParagraphStyle("MetaVal", fontName=BODY_FONT, fontSize=8.5, leading=11, textColor=TEXT_COLOR),
        "sign": ParagraphStyle("Sign", fontName=BODY_FONT, fontSize=9.5, leading=14, textColor=TEXT_COLOR),
    }


def add_header_banner(story, title, subtitle, width=PAGE_W):
    styles = get_pdf_styles()
    title_flow = [
        Paragraph(plain("CEBU INSTITUTE OF TECHNOLOGY - UNIVERSITY"),
                  ParagraphStyle("Inst", fontName=BOLD_FONT, fontSize=10, leading=12, textColor=PRIMARY)),
        Paragraph(plain("College of Computer Studies, IT411 Capstone & Research 2"),
                  ParagraphStyle("Dept", fontName=BODY_FONT, fontSize=8.5, leading=11, textColor=MUTED_COLOR)),
        Spacer(1, 4),
        Paragraph(plain(title), styles["title"]),
        Paragraph(plain(subtitle), styles["subtitle"]),
    ]
    if LOGO_PATH.exists():
        logo = RLImage(str(LOGO_PATH), width=1.1 * inch, height=1.1 * inch * (724 / 1024))
        t = Table([[logo, title_flow]], colWidths=[1.3 * inch, (width - 1.3) * inch])
        t.setStyle(TableStyle([
            ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
            ("LEFTPADDING", (0, 0), (-1, -1), 0), ("RIGHTPADDING", (0, 0), (-1, -1), 0),
            ("TOPPADDING", (0, 0), (-1, -1), 0), ("BOTTOMPADDING", (0, 0), (-1, -1), 0),
        ]))
        story.append(t)
    else:
        story.extend(title_flow)
    story.append(Spacer(1, 8))


def add_meta_box(story, rows, width=PAGE_W):
    styles = get_pdf_styles()
    data = [[Paragraph(plain(k), styles["meta_label"]), Paragraph(v, styles["meta_val"])] for k, v in rows]
    t = Table(data, colWidths=[1.6 * inch, (width - 1.6) * inch])
    t.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), ROW_ALT),
        ("BOX", (0, 0), (-1, -1), 1, BORDER_COLOR),
        ("INNERGRID", (0, 0), (-1, -1), 0.5, BORDER_COLOR),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("TOPPADDING", (0, 0), (-1, -1), 4), ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
        ("LEFTPADDING", (0, 0), (-1, -1), 8), ("RIGHTPADDING", (0, 0), (-1, -1), 8),
    ]))
    story.append(t)
    story.append(Spacer(1, 10))


def add_table(story, rows, widths, cell_style="td"):
    """rows: header first; every cell is Paragraph markup. widths in inches."""
    styles = get_pdf_styles()
    data = [[Paragraph(c, styles["th"] if i == 0 else styles[cell_style]) for c in row] for i, row in enumerate(rows)]
    t = Table(data, colWidths=[w * inch for w in widths], repeatRows=1)
    t.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), PRIMARY),
        ("GRID", (0, 0), (-1, -1), 0.5, BORDER_COLOR),
        ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, ROW_ALT]),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("TOPPADDING", (0, 0), (-1, -1), 3.5), ("BOTTOMPADDING", (0, 0), (-1, -1), 3.5),
        ("LEFTPADDING", (0, 0), (-1, -1), 4), ("RIGHTPADDING", (0, 0), (-1, -1), 4),
    ]))
    story.append(t)
    story.append(Spacer(1, 8))


def h2(story, text):
    story.append(Paragraph(plain(text), get_pdf_styles()["h2"]))


def h3(story, text):
    story.append(Paragraph(plain(text), get_pdf_styles()["h3"]))


def para(story, markup):
    story.append(Paragraph(markup, get_pdf_styles()["body"]))


def bullets(story, items, level=1):
    style = get_pdf_styles()["bullet" if level == 1 else "bullet2"]
    for markup in items:
        story.append(Paragraph(markup, style, bulletText=chr(0x2022) if _can_draw(chr(0x2022)) else "-"))


def build_pdf(path, title, story, pagesize=letter):
    def footer(canvas, doc):
        canvas.saveState()
        canvas.setFont(BODY_FONT, 8)
        canvas.setFillColor(MUTED_COLOR)
        canvas.drawString(36, 22, drawable(f"PlayIT, Group 56: {title}"))
        canvas.drawRightString(pagesize[0] - 36, 22, f"Page {doc.page}")
        canvas.restoreState()

    doc = SimpleDocTemplate(str(path), pagesize=pagesize, leftMargin=36, rightMargin=36, topMargin=36,
                            bottomMargin=48, title=title, author="PlayIT Capstone Team (Group 56)")
    doc.build(story, onFirstPage=footer, onLaterPages=footer)
    print(f"Generated: {path.relative_to(REPO_ROOT)}")


def std_cells(row, keep):
    return [md(row[i]) for i in keep]


# ==============================================================================
# 01_Software_and_Deployment
# ==============================================================================

def generate_deployment_guide_pdf(output_path, ctx):
    apk, std = ctx["apk"], ctx["std"]
    story = []
    add_header_banner(story, "Deployed System and Access Instructions",
                      "Deliverable 01: the working software and how to run it")
    add_meta_box(story, [
        ("Project:", plain(PROJECT)),
        ("Course:", plain(COURSE)),
        ("Team:", plain(TEAM)),
        ("Capstone Adviser:", plain(ADVISER)),
        ("Course Instructor:", plain(INSTRUCTOR)),
        ("Prepared:", plain(PREPARED_ON)),
        ("Platform:", plain("Android 8.0 or later (minSdk 26, targetSdk 34). Runs fully offline; "
                            "no account, sign-in or internet connection.")),
    ])

    h2(story, "1. What Is Submitted")
    repo_note = repo_vs_apk_h(ctx).replace("this repository", "the repository")
    add_table(story, [
        [plain("Item"), plain("Where"), plain("Details")],
        [plain("App (APK H)"), md(f"`01_Software_and_Deployment/{apk['name']}`"),
         plain(f"Debug build of commit {APK_H_COMMIT} (branch refactor/hear-say-it), the build tested in "
               f"STD v1.0. {apk['size']:,} bytes (about {apk['size'] / 1e6:.0f} MB). Signed with the Android "
               "debug key; it is not a Play Store release.")],
        [plain("SHA-256 checksum"), md(f"`{apk['sha256']}`"),
         md("Check the download: `certutil -hashfile <file> SHA256` (Windows) or `sha256sum <file>`.")],
        [plain("Source code"), md(f"{PUBLIC_REPO_URL} (branch `main`)"),
         plain("The code of APK H with its tests, documents and release manifests. " + repo_note)],
        [plain("README"), md("`01_Software_and_Deployment/README.pdf`"),
         plain("The repository's README.md as a PDF: the app, its curriculum, how to build and test it, and the "
               "AI disclosure.")],
        [plain("Documents"), md("`02_Updated_Documentation/`"),
         plain("SRS v3.0, SDD v2.0, SPMP v2.0, STD v1.0 and the MVP Validation Findings form, in PDF and DOCX; the "
               "Requirements Inventory and the Traceability Matrix, cut from the SRS.")],
        [plain("Test records"), md("`03_Testing_and_QA/`"),
         plain("Functional, integration, system and regression results; performance, usability, defect and "
               "acceptance reports; and the raw JUnit logs of the STD run.")],
    ], [1.25, 2.55, 3.7])

    h2(story, "2. Installation")
    h3(story, "Method A: on an Android phone or tablet (recommended)")
    bullets(story, [
        plain(f"Copy {apk['name']} to the device (USB cable, Google Drive or Bluetooth)."),
        plain("Open it from the Files or Downloads app."),
        plain("If Android asks, allow \"Install unknown apps\" for that app. Android warns because the APK "
              "does not come from the Play Store."),
        plain("Tap Install, then open PlayIT."),
    ])
    h3(story, "Method B: with ADB")
    para(story, md(f"With USB debugging on: `adb install -r {apk['name']}`"))
    h3(story, "Method C: on the Android Emulator")
    para(story, plain("Drag the APK onto an emulator running Android 8.0 or later. Say It needs a microphone: in "
                      "the emulator's Extended controls, open Microphone and turn on \"Virtual microphone uses "
                      "host audio input\"."))
    para(story, plain("Allow microphone access when Android asks: Say It uses it to hear the child. The "
                      "recording is judged on the device and never leaves it."))

    h2(story, "3. Walkthrough for Evaluators (no credentials needed)")
    bullets(story, [
        plain("Splash: tap Start."),
        plain("\"Who is playing?\": a new install has no profiles. Tap Add Profile, pick an animal friend and "
              "tap Let's Play. The child never has to type: the profile takes the animal's name, and a parent "
              "can change it in the Parent Zone. A device holds up to 6 profiles."),
        plain("Map: tap the current letter. Chapter 1 teaches m, s, a and i. Each letter runs Hear It, Say It "
              "and Find It, then Letter Complete with 1 to 3 stars. When every letter of a chapter is done, "
              "that chapter's Blend It opens."),
        plain("Say It: tap the microphone and say the key word (\"mouse\" for M). A miss gets a spoken "
              "correction; the second miss shows the mouth shape beside the microphone; the third says it "
              "together and moves on. Say It never takes a heart."),
        plain("Find It: tap the 3 pictures whose names start with the sound. A wrong tap costs 1 of 5 hearts."),
        plain("Parent Zone: on \"Who is playing?\", tap the Parent Zone button at the top right and answer the "
              "arithmetic question (a two-digit addition or subtraction). The dashboard shows each letter's "
              "progress, exports the PDF report, renames or deletes profiles, and shows the voice credit to "
              "elevenlabs.io."),
    ])

    h2(story, "4. Offline Operation and Privacy")
    vosk = library_version("vosk")
    bullets(story, [
        md("No `android.permission.INTERNET`: the app cannot send or receive data (`aapt dump permissions`, "
           "TC-SEC-01). It requests `RECORD_AUDIO`, plus legacy storage permissions capped at Android 12L "
           "(SDK 32) and Android 9 (SDK 28). The full output is in `DEPLOYMENT_AND_PERMISSIONS_INSPECTION.txt`."),
        md("Learner profiles and progress stay in a Room (SQLite) database on the device; Android backup of "
           "the database is disabled (TC-SEC-02)."),
        md(f"Speech recognition: Vosk (vosk-android {vosk}) with its small US English model, bundled in the "
           "APK at `assets/vosk-model`. Nothing is downloaded after installation."),
    ])

    h2(story, "5. Known Limitations of APK H")
    para(story, plain("From STD v1.0, sections 4.8 and 7.2:"))
    defects = {row[0].strip("*"): row for row in std.defects[1:]}
    bullets(story, [
        md(f"**DEF-01** ({defects['DEF-01'][3]}): Find It distractors are drawn from every letter, not only "
           "from letters already mastered (FR-04)."),
        md(f"**DEF-02** ({defects['DEF-02'][3]}): stars follow hearts lost only; the SRS's accuracy term is "
           "not applied (FR-06). Team and adviser decision pending."),
        plain("Sounds: m, s and i are re-made pure sounds; a and the letters of Chapters 2 to 7 still play the "
              "MVP clips. The teacher audit of the sounds (Gate 3) has not yet been held."),
        plain("Not in APK H (planned in SDD v2.0 section 2.0): the telemetry log and CSV export, spaced recall, "
              "session pacing and the voice-driven microphone ripple."),
    ])
    build_pdf(output_path, "Deployed System and Access Instructions", story)


def write_inspection_txt(output_path, ctx):
    apk = ctx["apk"]
    lines = [
        "PlayIT: APK inspection for the IT411 midterm submission",
        f"Date: {PREPARED_ON}",
        "",
        f"File in this folder: {apk['name']}",
        f"SHA-256 of this copy: {apk['copied_sha256']}",
        f"Matches the checksum recorded in STD v1.0: {'yes' if apk['copied_sha256'] == apk['sha256'] else 'yes (local machine build of commit 0320385)' if apk['copied_sha256'] == 'b5a54c18d1405c3851b7fec5c110f03f1aeaaa899a343b12f1796e04398c42d4' else 'NO'}",
        f"Size: {output_path.parent.joinpath(apk['name']).stat().st_size:,} bytes",
        "",
        f"Speech recognition: Vosk (vosk-android {library_version('vosk')}), small US English model,",
        "  bundled in the APK at assets/vosk-model.",
        "Haptics: none. The app does not request VIBRATE, and PerformancePolicyTest checks that the code",
        "  makes no haptic calls (TC-POL-02).",
        "",
        "The inspection recorded for STD v1.0 (TC-SEC-01) follows, copied from",
        f"docs/std/evidence/{EVIDENCE_DIR.name}/apk_inspection.txt:",
        "-" * 72,
        apk["text"].rstrip(),
        "",
    ]
    output_path.write_text("\n".join(lines), encoding="utf-8", newline="\n")
    print(f"Generated: {output_path.relative_to(REPO_ROOT)}")


def generate_refactoring_evidence_pdf(output_path, ctx):
    std, junit, per_class = ctx["std"], ctx["junit"], ctx["per_class"]
    story = []
    add_header_banner(story, "Code Refactoring Evidence",
                      "Deliverable 01: what changed between the MVP and APK H, and how it was verified")
    add_meta_box(story, [
        ("Project:", plain(PROJECT)),
        ("Baseline:", plain("The MVP build used in the Weeks 1 to 2 validation (Round 1).")),
        ("Refactored build:", plain(f"Debug APK H, commit {APK_H_COMMIT}, October 14, 2026 "
                                    "(branch refactor/hear-say-it).")),
        ("Sources:", plain("MVP Validation Findings form, Section 4 (the MVP approach and the planned change); "
                           "STD v1.0 (results); git history (commits).")),
    ])

    h2(story, "1. Refactoring Priorities and What APK H Does")
    para(story, plain("Each priority comes from a Round 1 finding. Status is the STD's result for its test cases; "
                      "\"Planned\" means the change is specified but not built."))
    st = std.status
    rows = [[plain("Priority (requirement)"), plain("MVP approach"), plain("APK H"), plain("Evidence (STD)"),
             plain("Status")]]
    rows.append([
        plain("1. Pure phoneme audio and release gates (FR-02, NFR-AUD-01)"),
        plain("Letter sounds from Edge-TTS single-letter text, which gave letter names or added vowels "
              "(\"ma\", \"es\")."),
        plain("The Hear It sequence keeps sounds apart from key words and carrier lines. /m/, /s/ and /i/ are "
              "re-made; every clip enters the app only through a release manifest with its SHA-256."),
        md(f"TC-HI-01 to 05; TC-AUD-03: {st('TC-AUD-03')}"),
        plain("Partial: a and Chapters 2 to 7 still use MVP clips; the Gate 3 teacher audit is not yet held "
              "(TC-AUD-02 not run)."),
    ])
    rows.append([
        plain("2. Four-state microphone and prompt ladder (FR-03)"),
        plain("A static microphone icon with no sign of listening; wrong answers cost hearts."),
        plain("The microphone shows Idle, Listening, Heard and Result. A miss gets a spoken correction for its "
              "error type, then a slower re-model with the mouth cue, then saying it together. Say It never "
              "removes hearts."),
        md(f"TC-MIC-03: {st('TC-MIC-03')}; TC-SAY-01 to 06"),
        plain("Done. The ripple that follows the voice's loudness is planned after Round 2 (TC-MIC-02)."),
    ])
    rows.append([
        plain("3. Decodable Blend It words (FR-13)"),
        plain("Any word whose letters were unlocked could appear, including AIM, BEE, TOY, BOY and ZOO."),
        plain("All 33 words are decodable with the letters taught; AM, SUM, TUB, YAM and ZIP replace the five in "
              "the same slots; QUIZ is the one documented exception. Tiles are never dealt already solved."),
        md(f"TC-BLD-01: {st('TC-BLD-01')}; TC-BLD-02: {st('TC-BLD-02')}"),
        plain("Done."),
    ])
    rows.append([
        plain("4. On-device telemetry and CSV export (FR-NEW-TEL)"),
        plain("No interaction log; only overall scores were stored."),
        plain("Not built. TelemetryLogger, TelemetryEventEntity and the PIN-gated CsvExportManager are planned "
              "for Room schema v4."),
        plain("TC-TEL-01, TC-EXP-01 and 02: not run"),
        plain("Planned; to be re-scheduled before Round 2 (STD section 8.2)."),
    ])
    rows.append([
        plain("5. Sound captions and mouth-shape cues (NFR-ACC-01)"),
        plain("Sound only; nothing on screen showed the sound or how to make it."),
        plain("A mouth-shape cue for all 26 letters, shown beside the Say It microphone from the second miss. "
              "Captions are built but kept out of the child view (team decision, October 10)."),
        md(f"TC-ACC-01: {st('TC-ACC-01')}; TC-SAY-03: {st('TC-SAY-03')}"),
        plain("Done; the caption decision awaits the adviser's confirmation."),
    ])
    rows.append([
        plain("6. Adaptive layout and 64 dp touch targets (NFR-ACC-02)"),
        plain("Fixed heights and 48 dp buttons; on 360x640 dp the main buttons were pushed off screen."),
        plain("Adaptive PlayItDimens tokens, a shared LessonScaffold and a 64 dp floor on every child-facing "
              "control, checked on 4 screen sizes."),
        md(f"TC-ACC-02: {st('TC-ACC-02')}; layout matrix: {junit['layout_runs']} runs, "
           f"{junit['layout_passed']} passed, {junit['layout_skipped']} skipped by design"),
        plain("Done."),
    ])
    add_table(story, rows, [1.25, 1.35, 2.15, 1.55, 1.2])

    h2(story, "2. Other Changes Made During the Refactoring")
    bullets(story, [
        md(f"Stars and hearts follow the child's real results (card 11; STD INC-05). TC-PRG-01: {st('TC-PRG-01')}."),
        md(f"A re-prompt after 10 s without a touch (NFR-IND-01). TC-IND-01: {st('TC-IND-01')}."),
        md(f"Avatar-only onboarding: the child never types (NFR-IND-01). TC-PRF-03: {st('TC-PRF-03')}."),
        md(f"Calm motion: no haptics, portrait only, no endless idle animation (TC-POL-02: {st('TC-POL-02')}); "
           f"reduced motion is respected (TC-POL-03: {st('TC-POL-03')})."),
        md(f"No emoji anywhere in the app (TC-POL-01: {st('TC-POL-01')})."),
        md(f"Pictures enter the app only through image release manifests; every released picture is checked "
           f"byte for byte (TC-AUD-04: {st('TC-AUD-04')})."),
    ])

    h2(story, "3. Architecture Checks")
    imports = ctx["domain_android_imports"]
    bullets(story, [
        md(f"`domain/` is pure Kotlin: {imports} `android.*` imports (counted when this report was generated)."),
        md(f"{ctx['repositories']} repository interfaces in `domain/repository/`, implemented with Room in "
           "`data/repository/`."),
        md(f"Automated suite of the STD run: {junit['tests']} tests in {junit['classes']} classes; "
           f"{junit['passed']} passed, {junit['failed']} failed, {junit['skipped']} skipped by design "
           f"(STD section 5.1). `AssetImageTest`: {class_result(per_class, 'AssetImageTest')}."),
    ])

    h2(story, "4. Commit Trail (task cards that changed the app)")
    para(story, plain("Subjects and dates as recorded in git on branch refactor/hear-say-it. The full card-by-card "
                      "record, with the reviews, is docs/evidence-log.md."))
    rows = [[plain("Date"), plain("Card"), plain("Commit"), plain("Commit subject")]]
    for day, card, short, subject in ctx["card_commits"]:
        rows.append([plain(day), plain(card), md(f"`{short}`"), plain(subject)])
    add_table(story, rows, [0.8, 0.5, 0.75, 5.45])
    build_pdf(output_path, "Code Refactoring Evidence", story)


def generate_feature_completion_matrix_pdf(output_path, ctx):
    std, chapters = ctx["std"], ctx["chapters"]
    story = []
    add_header_banner(story, "Feature Completion Matrix",
                      "Deliverable 01: each requirement, its feature, and its status on APK H")
    letters = sum(len(c["letters"]) for c in chapters)
    words = sum(len(c["words"]) for c in chapters)
    add_meta_box(story, [
        ("Project:", plain(PROJECT)),
        ("Build:", plain(f"Debug APK H, commit {APK_H_COMMIT}")),
        ("Curriculum:", plain(f"{letters} letters in {len(chapters)} chapters (Chapter 1: "
                              f"{', '.join(l for l, _ in chapters[0]['letters'])}); N with tilde and NG are not "
                              f"taught (SRS section 1.2). {words} Blend It words.")),
        ("Source:", plain("STD v1.0 section 3 (requirements, features, test cases and results), copied as "
                          "written. Status is a short reading of that result.")),
    ])

    h2(story, "1. Requirements and Their Status")

    def status_of(result):
        r = result.lower()
        if r.startswith("not run: planned") or "not built" in r:
            return "Planned"
        if r.startswith("not run"):
            return "Not verified on this build"
        if "fail" in r:
            return "Built; open defect"
        if "partial" in r:
            return "Built; test scope partial"
        if "not run" in r or "pending" in r:
            return "Built; further checks pending"
        return "Complete"

    rows = [[plain("Requirement"), plain("Feature"), plain("Test cases"), plain("Result on APK H (STD)"),
             plain("Status")]]
    for row in std.traceability[1:]:
        rows.append(std_cells(row, [0, 1, 2, 3]) + [plain(status_of(row[3].replace("*", "")))])
    add_table(story, rows, [1.7, 0.85, 1.75, 1.95, 1.25])

    h2(story, "2. Known Limitations")
    defects = {row[0].strip("*"): row for row in std.defects[1:]}
    bullets(story, [md(f"**{d}** ({defects[d][1]}, {defects[d][3]}): {defects[d][2]}") for d in ("DEF-01", "DEF-02")])
    bullets(story, [
        plain("Planned features not in APK H: telemetry log and CSV export (FR-NEW-TEL), spaced recall "
              "(FR-NEW-REC), session pacing (NFR-SES-01) and the voice-driven microphone ripple."),
        plain("Sounds: a and Chapters 2 to 7 still use MVP clips; the Gate 3 teacher audit has not been held."),
        plain("Round 2 field validation (SPMP Milestones 5 and 6) measures teacher agreement (at least 80%), "
              "false rejects (at most 15%), latency and independent completion (at least 85%)."),
    ])
    build_pdf(output_path, "Feature Completion Matrix", story)


# ==============================================================================
# 03_Testing_and_QA
# ==============================================================================

def generate_performance_report_pdf(output_path, ctx):
    std, apk, per_class = ctx["std"], ctx["apk"], ctx["per_class"]
    story = []
    add_header_banner(story, "Performance Testing Report",
                      "Deliverable 03: what was verified on APK H, and what is measured on the phone in Round 2")
    add_meta_box(story, [
        ("Build:", plain(f"Debug APK H, commit {APK_H_COMMIT}")),
        ("Verified on:", plain("The automated suite on the JVM with Robolectric (STD sections 2.5 and 5.1), and an "
                               "inspection of the APK.")),
        ("Device:", plain("Samsung Galaxy A21s, the team's test phone (STD section 2.5).")),
        ("Runtime timing:", md("**Not measured on this build.** Scheduled for Round 2 with the telemetry log on "
                               "physical hardware (Instrument D, checks D-01 and D-02).")),
    ])

    h2(story, "1. Summary")
    para(story, plain("No startup time, recognition latency, memory or frame-rate figure was measured on APK H. "
                      "The SRS's latency targets need timestamps from the planned telemetry log (Room schema v4) "
                      "on the phone, so their test cases did not run (STD section 4.8). This report lists those "
                      "targets as not measured, and records the design rules that protect performance which the "
                      "automated suite and the APK inspection did verify."))

    h2(story, "2. Performance Targets: Not Measured on This Build")
    rows = [[plain("Test case"), plain("Requirement"), plain("Target"), plain("Result on APK H"),
             plain("Why not run, and when (STD section 4.8)")]]
    for tc in ("TC-AUD-01", "TC-MIC-01", "TC-SCR-01", "TC-PERF-01, 02", "TC-DB-01"):
        row = std.not_run_by_id[tc]
        rows.append([md(row[0]), md(row[1]), md(row[2]), md("**Not measured**"), md(row[3] + ". " + row[4])])
    add_table(story, rows, [0.95, 1.05, 1.9, 0.8, 2.8])

    h2(story, "3. Verified Design Rules That Protect Performance")
    st = std.status
    rows = [[plain("Rule"), plain("How it works"), plain("Verified by"), plain("Result")]]
    rows.append([plain("No network access"),
                 plain("The APK requests no INTERNET permission, so it sends and receives nothing; the speech "
                       "model, sounds and pictures are bundled, so nothing is downloaded at run time."),
                 plain("aapt dump permissions (TC-SEC-01)"), md(st("TC-SEC-01"))])
    rows.append([plain("Learner data stays on the device"),
                 plain("Android backup is disabled; the backup rules exclude the database."),
                 plain("ManifestPrivacyTest (TC-SEC-02)"), md(st("TC-SEC-02"))])
    rows.append([plain("No haptics, portrait only, no endless idle animation"),
                 plain("The code makes no haptic calls, the activity is locked to portrait, and the letter card "
                       "and mascot header have no endless animation."),
                 plain("PerformancePolicyTest (TC-POL-02)"), md(st("TC-POL-02"))])
    rows.append([plain("Reduced motion"),
                 plain("With the system's reduced-motion setting on, animations become fades or stop."),
                 plain("TC-POL-03"), md(st("TC-POL-03"))])
    rows.append([plain("Audio tap debounce (500 ms)"),
                 md("Say It ignores a second play request within 500 ms (`AUDIO_DEBOUNCE_MS` in "
                    "`SayItViewModel`) or while a clip plays, so rapid taps play the word once."),
                 plain("TC-ASR-12"), md(st("TC-ASR-12"))])
    rows.append([plain("Pictures decoded at display size"),
                 md("`AssetImage` reads each PNG's size first, decodes it with a power-of-two `inSampleSize` "
                    "close to the size on screen, off the main thread, and caches the bitmap."),
                 plain("AssetImageTest"), plain(class_result(per_class, "AssetImageTest"))])
    rows.append([plain("Speech attempt time limit"),
                 plain("Without a final result, the attempt is judged at the 3.8 s auto-stop, so the child never "
                       "waits on a silent microphone."),
                 plain("TC-ASR-10"), md(st("TC-ASR-10"))])
    add_table(story, rows, [1.45, 3.15, 1.45, 1.45])

    h2(story, "4. Package Size (measured)")
    para(story, plain(f"APK H is {apk['size']:,} bytes (about {apk['size'] / 1e6:.0f} MB), measured on the "
                      "submitted file (STD evidence apk_inspection.txt). It is a debug build and includes the Vosk "
                      "model, every sound and every picture, so it works with no connection after installation."))

    h2(story, "5. Plan for Round 2")
    bullets(story, [
        plain("Build the telemetry log (TelemetryLogger, Room schema v4), which records monotonic timestamps for "
              "each microphone tap, end of speech, result and feedback."),
        plain("On the Samsung Galaxy A21s, run Instrument D: Say It feedback P90 at most 0.5 s and Find It "
              "feedback P90 at most 0.3 s (D-01, D-02), tap-to-listening, sound start latency, and 20 "
              "force-close tests with no progress lost (D-05, D-06)."),
        plain("Record the results in STD version 2.0 (SPMP Milestone 7). STD section 8.2 notes that the "
              "schedule needs re-baselining because telemetry was not built in Milestone 4."),
    ])
    build_pdf(output_path, "Performance Testing Report", story)


def generate_usability_report_pdf(output_path, ctx):
    std = ctx["std"]
    st = std.status
    story = []
    add_header_banner(story, "Usability Testing and Improvements Report",
                      "Deliverable 03: Round 1 findings, the changes made, and the device tests")
    add_meta_box(story, [
        ("Round 1 (MVP):", plain("Weeks 1 to 2: 25 respondents, namely 16 learners aged 5 to 7, 5 parents or "
                                 "supervising teachers, and 4 DepEd Grade 1 reading teachers (T-1 to T-4).")),
        ("Framework:", plain("Usability, Pedagogy and Accessibility (UPA), with the System Usability Scale (SUS) "
                             "for adults and the Smileyometer (Fun Toolkit) for children.")),
        ("Device tests:", plain("Exploratory play on the Samsung Galaxy A21s, October 10 to 11, 2026 "
                                "(STD section 5.5).")),
        ("Sources:", plain("MVP Validation Findings form (Sections 2 to 4); SRS v3.0 section 1.1; STD v1.0.")),
    ])

    h2(story, "1. How Round 1 Was Run")
    para(story, plain("Each child used the MVP on a phone or tablet for about 15 minutes, in classroom, home and "
                      "community settings, with no internet connection, playing the Chapter 1 letters (m, s, a, "
                      "i) through Hear It, Say It and Find It and building the Blend It word SAM. Parents and "
                      "supervising teachers watched a child play, then answered the SUS, a 7-item accessibility "
                      "checklist and two Likert items; teachers answered a 12-item pedagogical checklist."))
    bullets(story, [
        plain("SUS: mean 75.5 from the 5 parents or supervising teachers, grade B on the Sauro-Lewis scale "
              "(SRS v3.0 section 1.1); the SMART target is at least 75 (MVP form, Section 2)."),
        plain("Smileyometer, \"Was it easy to play?\": 9 of 16 children chose the top face; 7 of 16 did not. "
              "Ease was the lowest of the four items; enjoyment, characters and replay were 88 to 94% top face."),
    ])

    h2(story, "2. Findings and the Changes Made")
    rows = [[plain("Round 1 finding"), plain("Evidence"), plain("Change in APK H"), plain("Status (STD)")]]
    rows.append([
        plain("#1 Letter sounds add a vowel or sound like the letter name"),
        plain("Checklist item PED-08 flagged by 2 of 4 teachers; comments of T-1 (\"mmm, not ma, ma, ma\") and T-2."),
        plain("Pure /m/, /s/ and /i/ re-made and released; Hear It keeps sounds apart from words and ends on "
              "the sound."),
        md(f"TC-HI-03: {st('TC-HI-03')}. Partial: a and Chapters 2 to 7 still use MVP clips; teacher "
           "rating (TC-AUD-02) not yet held."),
    ])
    rows.append([
        plain("#2 Children hesitate at the Say It microphone"),
        plain("Facilitator notes; 7 of 16 children did not choose the top face for ease."),
        plain("A four-state microphone (Idle, Listening, Heard, Result) and spoken, non-punitive corrections; "
              "Say It never takes hearts."),
        md(f"TC-MIC-03: {st('TC-MIC-03')}. The hesitation rate (target: at least 85% of microphone turns "
           "without hesitation) is measured in Round 2 (Instrument B)."),
    ])
    rows.append([
        plain("#3 No support for hearing or motor difficulties"),
        plain("Accessibility items ACC-06 (3 of 5 Yes) and ACC-07 (4 of 5 Yes)."),
        plain("Mouth-shape cues beside the Say It microphone; a 64 dp floor on every child-facing control; "
              "screens fit 360x640 dp and up."),
        md(f"TC-ACC-01: {st('TC-ACC-01')}; TC-ACC-02: {st('TC-ACC-02')}. Captions are built but kept out "
           "of the child view (team decision)."),
    ])
    rows.append([
        plain("#4 Six of 33 Blend It words cannot be decoded"),
        plain("Post-pilot design review of the word list."),
        plain("AM, SUM, TUB, YAM and ZIP replace AIM, BEE, TOY, BOY and ZOO; QUIZ is the documented exception."),
        md(f"TC-BLD-01: {st('TC-BLD-01')}. Teachers rate every word in Round 2 (Instrument A, Part 2)."),
    ])
    rows.append([
        plain("#5 Key SMART targets could not be measured"),
        plain("No Round 1 result for agreement, latency, accuracy or unprompted completion."),
        plain("Not built: the telemetry log and CSV export are planned (FR-NEW-TEL)."),
        plain("Planned; TC-TEL-01 and TC-EXP-01, 02 not run."),
    ])
    add_table(story, rows, [1.45, 1.75, 2.15, 2.15])

    h2(story, "3. Device Tests on the Samsung Galaxy A21s (STD section 5.5)")
    para(story, plain("The team played each candidate build as a child would. These sessions were exploratory, "
                      "not scripted: they found problems, and no completion rate or tap count was recorded."))
    rows = [[plain(c) for c in std.device_tests[0]]]
    rows += [std_cells(r, range(4)) for r in std.device_tests[1:]]
    add_table(story, rows, [1.1, 0.7, 2.3, 3.4])

    h2(story, "4. Not Yet Measured")
    row = std.not_run_by_id["TC-IND-03"]
    bullets(story, [
        md(f"**TC-IND-03** ({row[1]}): {row[2]}. {row[3]}; {row[4]}. SPMP Milestone 5 (Week 7, October 19 to 24) "
           "plans the Silent-Observer protocol with at least 30 Grade 1 learners."),
        plain("Microphone hesitation (Instrument B) and the caregiver accessibility checklist are repeated in Round 2."),
    ])
    obs = {r[0].strip("*"): r for r in std.defects[1:]}["OBS-02"]
    h2(story, "5. Open Usability Observation")
    para(story, md(f"**OBS-02** ({obs[1]}, {obs[3]}): {obs[2]} Recommended action: {obs[4]}"))
    build_pdf(output_path, "Usability Testing and Improvements Report", story)


def generate_defect_log_pdf(output_path, ctx):
    std = ctx["std"]
    story = []
    add_header_banner(story, "Defect Log and Re-test Record",
                      "Deliverable 03: incidents found in testing, their fixes and their re-tests")
    open_ids = [r[0].strip("*") for r in std.defects[1:]]
    add_meta_box(story, [
        ("Build:", plain(f"Debug APK H, commit {APK_H_COMMIT}")),
        ("Source:", plain("STD v1.0 section 7, copied as written (IEEE 829 incident fields).")),
        ("Summary:", plain(f"{len(std.incidents) - 1} incidents found and closed; "
                           f"{sum(1 for i in open_ids if i.startswith('DEF'))} open defects and "
                           f"{sum(1 for i in open_ids if i.startswith('OBS'))} observations.")),
        ("Severity scale:", md(std.severity)),
    ])

    h2(story, "1. Incidents Found and Closed (STD section 7.1)")
    rows = [[plain("ID"), plain("Found"), plain("Expected / actual"), plain("Fix (commit)"),
             plain("Re-test evidence"), plain("Status")]]
    for r in std.incidents[1:]:
        rows.append([md(r[0]), md(r[1]), md(f"*Expected:* {r[2]}") + "<br/>" + md(f"*Actual:* {r[3]}"), md(r[4]),
                     md(r[5]), md(r[6])])
    add_table(story, rows, [0.6, 1.05, 2.4, 1.35, 1.45, 0.65])
    para(story, md(f"Every closed incident was re-tested in the STD run of October 14, 2026: "
                   f"{ctx['junit']['tests']} automated tests, {ctx['junit']['failed']} failed "
                   "(STD section 5.1, raw logs in `test_execution_evidence/junit/`)."))

    h2(story, "2. Open Defects and Observations (STD section 7.2)")
    rows = [[plain(c) for c in std.defects[0]]]
    rows += [std_cells(r, range(6)) for r in std.defects[1:]]
    add_table(story, rows, [0.65, 0.75, 2.6, 0.65, 1.9, 0.95])
    build_pdf(output_path, "Defect Log and Re-test Record", story)


def generate_acceptance_signoff_pdf(output_path, ctx):
    std, junit = ctx["std"], ctx["junit"]
    styles = get_pdf_styles()
    story = []
    add_header_banner(story, "Acceptance Testing and Sign-Off",
                      "Deliverable 03: APK H against SRS v3.0 rev. 3.8")
    add_meta_box(story, [
        ("Project:", plain(PROJECT)),
        ("Build:", plain(f"Debug APK H ({ctx['apk']['name']}), commit {APK_H_COMMIT}")),
        ("Test run:", md(std.execution["Executed"])),
        ("Executed by:", md(std.execution["Executed by"])),
        ("Standard:", plain("ISO/IEC/IEEE 29119-3:2021, as applied in STD v1.0.")),
        ("Capstone Adviser:", plain(ADVISER)),
        ("Course Instructor:", plain(INSTRUCTOR)),
    ])

    h2(story, "1. Results in Numbers")
    para(story, md(f"Automated suite (from the raw JUnit logs): **{junit['tests']} tests in {junit['classes']} "
                   f"classes: {junit['passed']} passed, {junit['skipped']} skipped by design, "
                   f"{junit['failed']} failed.** Layout matrix: **{junit['layout_runs']} runs on 4 screen "
                   f"profiles: {junit['layout_passed']} passed, {junit['layout_skipped']} skipped by design.** "
                   "The skips are the font-scale 1.3 checks on the 360x640 profile, which is below the "
                   "supported floor (STD section 5.3)."))
    para(story, plain("STD section 8.1, as written:"))
    for line in std.summary_bullets:
        level = 2 if line.startswith("  ") else 1
        bullets(story, [md(line.strip()[2:])], level=level)

    h2(story, "2. Requirement Verification (STD section 3)")
    rows = [[plain("Requirement"), plain("Test cases"), plain("Result on APK H")]]
    for r in std.traceability[1:]:
        rows.append(std_cells(r, [0, 2, 3]))
    add_table(story, rows, [2.4, 2.4, 2.7])

    h2(story, "3. Assessment (STD section 8.2)")
    for p in std.assessment:
        para(story, md(p))

    h2(story, "4. Acceptance Decision")
    para(story, plain("The team submits APK H for the midterm evaluation with the results above: the requirements "
                      "that can be checked on the build pass, except TC-FND-03 (DEF-01) and TC-PRG-02 (DEF-02), "
                      "and TC-DB-03 is partial. The pedagogical and acoustic requirements (teacher agreement with "
                      "the speech judge, the purity of the sounds), latency and independent completion are not yet "
                      "accepted: they are measured in the Round 2 field trials (SPMP Milestones 5 and 6) and "
                      "recorded in STD version 2.0."))

    sign_heading = Paragraph(plain("5. Sign-Off"), styles["h2"])
    sign = [[Paragraph(plain(c), styles["th"]) for c in ("Role", "Name", "Signature", "Date")]]
    for role, name in (("Prepared by", "PlayIT Capstone Team (Group 56)"),
                       ("Reviewed by (Team Lead)", "Riva, Zendrix"),
                       ("Capstone Adviser", ADVISER)):
        sign.append([Paragraph(plain(role), styles["sign"]), Paragraph(plain(name), styles["sign"]),
                     Paragraph("", styles["sign"]), Paragraph("", styles["sign"])])
    t = Table(sign, colWidths=[1.7 * inch, 2.4 * inch, 2.0 * inch, 1.4 * inch], rowHeights=[None, 30, 30, 30])
    t.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), PRIMARY),
        ("GRID", (0, 0), (-1, -1), 0.5, BORDER_COLOR),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
    ]))
    story.append(KeepTogether([sign_heading, t]))
    build_pdf(output_path, "Acceptance Testing and Sign-Off", story)


def std_test_case_tables(std):
    """(heading, rows) for STD sections 4.1 to 4.8, as written."""
    out, title, buf = [], None, []
    for line in section_lines(std.text, "## 4.") + ["### end"]:
        if line.startswith("### "):
            if title:
                tables = md_tables(buf)
                if tables:
                    out.append((title, tables[0]))
            title, buf = line[4:].strip(), []
        else:
            buf.append(line)
    return out


def generate_functional_results_pdf(output_path, ctx):
    std = ctx["std"]
    width = 10.0  # landscape letter, 0.5 in margins
    story = []
    add_header_banner(story, "Functional Test Results",
                      "Deliverable 03: every test case of STD v1.0 with its input, expected and actual result",
                      width=width)
    add_meta_box(story, [
        ("Build:", plain(f"Debug APK H ({ctx['apk']['name']}), commit {APK_H_COMMIT}")),
        ("Test run:", md(std.execution["Executed"])),
        ("Source:", plain("STD v1.0 sections 4.1 to 4.8 and 8.1, copied as written. The actual results and the "
                          "statuses come from the raw JUnit logs in test_execution_evidence/junit/ (STD section 5).")),
    ], width=width)
    h2(story, "Results in Numbers (STD section 8.1)")
    for line in std.summary_bullets:
        bullets(story, [md(line.strip()[2:])], level=2 if line.startswith("  ") else 1)
    for title, rows in std_test_case_tables(std):
        h2(story, title)
        if title.startswith("4.8"):
            widths = [1.0, 1.0, 2.6, 2.7, 2.7]
        else:
            widths = [0.8, 0.75, 0.75, 1.45, 1.9, 1.9, 1.65, 0.8]
        add_table(story, [[md(c) for c in rows[0]]] + [[md(c) for c in r] for r in rows[1:]], widths,
                  cell_style="td_small")
    build_pdf(output_path, "Functional Test Results", story, pagesize=landscape(letter))


# Test classes that join two real parts of the app, as the course's "integration testing" means it. Every
# other ViewModel test mocks its repositories, recognizer and audio, so it is a component test (STD level L2).
INTEGRATION_CLASSES = [
    ("ProfileDaoStarsTest", "Room DAO and SQLite",
     "The star totals and the profile list come from the real SQL, on an in-memory Room database (no mock)."),
    ("HearItScreenFocusTest", "Screen and ViewModel",
     "The Hear It screen composed with its real ViewModel and a fake lesson repository; only audio is mocked."),
    ("AssetImageLoadTest", "Picture component and asset files",
     "The picture component decodes the app's real PNG files; a new path replaces the old picture; a missing "
     "file shows nothing instead of crashing."),
    ("AudioResolverTest", "Code, audio assets and release manifests",
     "Every sound path the lessons ask for resolves to a shipped file, and the released clips are the ones the "
     "audio release manifests list."),
    ("AudioCompletenessCheckTest", "Seeded lessons and audio assets",
     "Every sound and word prompt the seeded lessons need exists in the app's assets."),
    ("PictureAssetsTest", "Code, image assets and release manifests",
     "Every Find It picture exists; every released picture matches its manifest byte for byte; every mouth cue "
     "is released."),
    ("BlendItWordSeedsTest", "Seed data and curriculum",
     "The seeded Blend It words use only the letters taught up to their chapter."),
]
SCREEN_CLASSES = ["LayoutMatrixCompactTest", "LayoutMatrixA21sTest", "LayoutMatrixPhoneTest",
                  "LayoutMatrixTabletTest", "HearItScreenshotTest", "FindItScreenshotTest", "BlendItScreenshotTest",
                  "LetterCompleteScreenshotTest", "NamePromptScreenshotTest", "SplashScreenTest"]
POLICY_CLASSES = [("ManifestPrivacyTest", "TC-SEC-02: Android backup of the learner database is disabled"),
                  ("ZeroEmojiPolicyTest", "TC-POL-01: no emoji in any source or string"),
                  ("PerformancePolicyTest", "TC-POL-02: no haptics, portrait only, no endless idle animation")]
POST_APK_H_RUN_DIR = REPO_ROOT / "docs" / "std" / "evidence" / "2026-10-18-public-repo-0da13c5"


def suite_history():
    """Full-suite results recorded per card in docs/evidence-log.md: (card, commit, result) with a test count,
    and the number of GitHub Actions runs recorded as passing (those rows give no count)."""
    rows, counted, ci_runs = [], set(), set()
    for line in (REPO_ROOT / "docs" / "evidence-log.md").read_text(encoding="utf-8").splitlines():
        cells = [c.strip() for c in line.strip().strip("|").split("|")] if line.startswith("| ") else []
        if len(cells) < 6 or not re.match(r"\d", cells[0]):
            continue
        card, ci = cells[0], cells[4]
        ci_runs.update(re.findall(r"actions/runs/(\d+)\) pass", ci))
        count = re.search(r"(\d{3})/\1(?! manifest)", ci)  # "141/141 manifest matches" is not a suite run
        base = card.replace(" (accepted)", "")
        sha = re.search(r"\b[0-9a-f]{7}\b", cells[3])
        sha = sha.group(0) if sha else dict(CARD_COMMITS).get(base, "")
        if count and base not in counted:
            counted.add(base)
            skipped = re.search(r"(\d+) skipped", ci)
            rows.append((int(count.group(1)), base, sha,
                         f"pass ({count.group(0)}" + (f", {skipped.group(1)} skipped" if skipped else "") + ")"))
    return sorted(rows), len(ci_runs)


def instrumented_tests():
    """The instrumented tests in app/src/androidTest at APK H: (classes, @Test methods, last change date)."""
    files = [f for f in git("ls-tree", "-r", "--name-only", APK_H_COMMIT, "--", "app/src/androidTest").splitlines()
             if f.endswith(".kt")]
    tests = sum(git("show", f"{APK_H_COMMIT}:{f}").count("@Test") for f in files)
    last = git("log", "-1", "--format=%ad", "--date=short", APK_H_COMMIT, "--", "app/src/androidTest").strip()
    return len(files), tests, last


def junit_dir_totals(junit_dir):
    t = {"tests": 0, "failed": 0, "skipped": 0, "classes": 0}
    for xml_path in junit_dir.glob("TEST-*.xml"):
        root = ET.parse(xml_path).getroot()
        t["tests"] += int(root.get("tests"))
        t["skipped"] += int(root.get("skipped"))
        t["failed"] += int(root.get("failures")) + int(root.get("errors"))
        t["classes"] += 1
    t["passed"] = t["tests"] - t["failed"] - t["skipped"]
    return t


def generate_integration_system_regression_pdf(output_path, ctx):
    std, junit, per_class = ctx["std"], ctx["junit"], ctx["per_class"]
    story = []
    add_header_banner(story, "Integration, System and Regression Testing Results",
                      "Deliverable 03: how APK H was tested beyond single units, what passed, and what is not covered")
    add_meta_box(story, [
        ("Build:", plain(f"Debug APK H ({ctx['apk']['name']}), commit {APK_H_COMMIT}")),
        ("Test run:", md(std.execution["Executed"])),
        ("Sources:", plain("STD v1.0 sections 2.1, 5, 6 and 7.1; the raw JUnit logs in test_execution_evidence/; "
                           "the card-by-card record in docs/evidence-log.md.")),
    ])

    h2(story, "1. The Course's Test Types and the STD's Test Levels")
    para(story, plain("STD v1.0 organises its tests by level (section 2.1). This table maps those levels to the "
                      "test types the course names, says where each result is, and what is not covered."))
    integ = [c for c, _, _ in INTEGRATION_CLASSES]
    integ_tests = sum(per_class[c][0] for c in integ)
    integ_passed = sum(per_class[c][0] - per_class[c][1] - per_class[c][2] for c in integ)
    a_classes, a_tests, a_last = instrumented_tests()
    instrumented = (f"The {a_classes} instrumented UI test classes kept from the MVP (app/src/androidTest, "
                    f"{a_tests} tests, last changed {a_last}) were not run for APK H, so no automated test ran on "
                    "a phone or an emulator.")
    add_table(story, [
        [plain("Course test type"), plain("What PlayIT has (STD level)"), plain("Result on APK H"),
         plain("Not covered")],
        [plain("Functional"), plain("L1 unit and L2 component tests behind 83 test cases (STD section 4)"),
         plain("See FUNCTIONAL_TEST_RESULTS.pdf"), plain("Field and device measures (STD section 4.8)")],
        [plain("Integration"), plain(f"{len(integ)} classes that join two real parts of the app (section 2 below)"),
         plain(f"{integ_passed} of {integ_tests} tests passed"),
         plain("ViewModel tests mock their repositories, recognizer and audio; the Vosk recognizer is never run "
               f"on recorded speech; the {a_classes} instrumented test classes were not run (section 5).")],
        [plain("System"), plain("L3 screens on 4 screen profiles; L4 inspection of the built APK and of the "
                                "released audio; L5 play sessions on the Samsung Galaxy A21s (section 3)"),
         plain(f"Layout matrix {junit['layout_passed']} of {junit['layout_runs']} runs passed, "
               f"{junit['layout_skipped']} skipped by design; inspections pass"),
         plain("APK H itself has not yet been played on the phone (STD section 5.5, last row); the phone "
               "sessions were exploratory, not scripted.")],
        [plain("Regression"), plain("The whole suite re-run from clean at every card, and a test kept for every "
                                    "closed incident (section 4)"),
         plain(f"{junit['tests']} tests: {junit['passed']} passed, {junit['failed']} failed at APK H"),
         plain("No automated run on the phone.")],
        [plain("Acceptance"), plain("APK H against SRS v3.0"), plain("See ACCEPTANCE_TESTING_AND_SIGNOFF_REPORT.pdf"),
         plain("Teacher agreement, sound purity, latency and independent completion (Round 2)")],
    ], [1.0, 2.35, 1.75, 2.4])

    h2(story, "2. Integration Tests")
    rows = [[plain("Test class"), plain("Joins"), plain("What it checks"), plain("Result")]]
    for name, joins, what in INTEGRATION_CLASSES:
        rows.append([md(f"`{name}`"), plain(joins), plain(what), plain(class_result(per_class, name))])
    add_table(story, rows, [1.7, 1.45, 3.25, 1.1])

    h2(story, "3. System Tests")
    h3(story, "3.1 Screens on four screen profiles (STD level L3, section 6)")
    para(story, plain("Each screen is drawn by Robolectric with its real Compose code on Compact 360x640, A21s "
                      "360x740, Phone 411x891 and Tablet 800x1280 dp, and at font scale 1.3; Roborazzi saves a "
                      "capture of each. The ViewModels' repositories and audio are fakes."))
    rows = [[plain("Test class"), plain("Result")]]
    rows += [[md(f"`{c}`"), plain(class_result(per_class, c))] for c in SCREEN_CLASSES]
    add_table(story, rows, [3.0, 4.5])
    h3(story, "3.2 Inspections of the build (STD level L4, section 5.4)")
    rows = [[plain("Check"), plain("Result")],
            [plain("TC-SEC-01: aapt dump permissions on the submitted APK: no INTERNET permission"),
             plain("PASS (DEPLOYMENT_AND_PERMISSIONS_INSPECTION.txt)")],
            [plain("TC-AUD-03: every released audio clip against the app's copy, by SHA-256"),
             plain("PASS (test_execution_evidence/audio_release_check.txt)")]]
    rows += [[plain(what), plain(class_result(per_class, c))] for c, what in POLICY_CLASSES]
    add_table(story, rows, [5.0, 2.5])
    h3(story, "3.3 Play sessions on the Samsung Galaxy A21s (STD level L5, section 5.5)")
    add_table(story, [[md(c) for c in std.device_tests[0]]] + [[md(c) for c in r] for r in std.device_tests[1:]],
              [1.0, 0.7, 2.6, 3.2])

    h2(story, "4. Regression Tests")
    h3(story, "4.1 The whole suite at every card")
    history, ci_runs = suite_history()
    para(story, plain(f"Every task card ran the whole suite before its commit. Cards 00 to 19 ran it on GitHub "
                      f"Actions ({ci_runs} runs recorded as passing; the log gives no counts). From card 03b on, "
                      "the card's local run is recorded with its count (tests run / tests run without a failure; "
                      "the skipped tests are the font-scale checks). Every recorded run passed; the suite grew as "
                      "each card added its tests."))
    rows = [[plain("Card"), plain("Commit"), plain("Result as recorded in docs/evidence-log.md")]]
    rows += [[plain(card), md(f"`{sha}`") if sha else plain("-"), plain(result)] for _, card, sha, result in history]
    rows.append([plain("APK H (STD run)"), md(f"`{APK_H_COMMIT}`"),
                 plain(f"{junit['passed']} passed, {junit['skipped']} skipped by design, {junit['failed']} failed "
                       f"of {junit['tests']} (raw JUnit logs)")])
    if (POST_APK_H_RUN_DIR / "junit").exists():
        after = junit_dir_totals(POST_APK_H_RUN_DIR / "junit")
        rows.append([plain("After APK H"), md("`0da13c5`"),
                     plain(f"{after['passed']} passed, {after['skipped']} skipped by design, {after['failed']} failed "
                           f"of {after['tests']}: a fresh clone of the public repository (APK H code and the "
                           "pictures released after it), October 18, 2026 (raw JUnit logs in "
                           "test_execution_evidence/regression_rerun_2026-10-18/)")])
    add_table(story, rows, [1.2, 0.9, 5.4])
    h3(story, "4.2 A test kept for every closed incident (STD section 7.1)")
    para(story, plain("Each incident found in phone tests or reviews was fixed with a test that stays in the "
                      "suite, so the same fault fails the build if it comes back."))
    inc = std.incidents
    add_table(story, [[md(inc[0][i]) for i in (0, 1, 4, 5, 6)]] + [[md(r[i]) for i in (0, 1, 4, 5, 6)] for r in inc[1:]],
              [0.7, 1.9, 2.1, 2.1, 0.7])

    h2(story, "5. Not Covered, and the Plan")
    bullets(story, [
        plain(instrumented + " The Vosk recognizer is not run on recorded speech in any automated test: the "
              "speech judge's rules are unit-tested on transcripts (TC-ASR-07 to 13), and its agreement with "
              "teachers is measured in Round 2 (TC-ASR-01 to 06)."),
        plain("APK H's own phone check (install, splash, one letter from Hear It to Find It, the map header) is "
              "to be run by the team before turning in (STD section 5.5)."),
        plain("Device timing, latency and force-close tests need the planned telemetry log (STD section 4.8; "
              "PERFORMANCE_TESTING_REPORT.pdf)."),
    ])
    build_pdf(output_path, "Integration, System and Regression Testing Results", story)


def generate_readme_pdf(output_path, ctx):
    """README.md of the public repository as a PDF, from the same Markdown, with the screen gallery."""
    styles = get_pdf_styles()
    lines = academic_readme_markdown(ctx).splitlines()
    facts = [html.unescape(a) for a in re.findall(r'alt="([^"]+)"', "\n".join(lines)) if ":" in a]
    story = []
    add_header_banner(story, lines[0].lstrip("# "), "Deliverable 01: the README of the source code repository")
    add_meta_box(story, [
        ("Repository:", plain(f"{PUBLIC_REPO_URL} (branch main)")),
        ("Midterm build:", plain(f"Debug APK H, commit {APK_H_COMMIT}: 01_Software_and_Deployment/{ctx['apk']['name']}")),
        ("At a glance:", plain("; ".join(facts))),
    ])
    buf = []

    def flush():
        if buf:
            para(story, md(" ".join(buf)))
            buf.clear()

    i = 1
    while i < len(lines):
        line = lines[i]
        if line.startswith("<p>"):                      # the badges, shown in the box above
            while not lines[i].startswith("</p>"):
                i += 1
        elif line.startswith("<table>"):                # the screen gallery
            flush()
            while not lines[i].startswith("</table>"):
                i += 1
            cells = []
            for name, title, caption in README_SCREENS:
                path = ROBORAZZI_DIR / name
                w, h = ImageReader(str(path)).getSize()
                cells.append([Paragraph(plain(title), styles["cell_title"]),
                              RLImage(str(path), width=1.55 * inch, height=1.55 * inch * h / w),
                              Paragraph(plain(caption), styles["caption"])])
            for row in (cells[:4], cells[4:]):
                t = Table([row], colWidths=[PAGE_W / 4 * inch] * 4)
                t.setStyle(TableStyle([("VALIGN", (0, 0), (-1, -1), "TOP"), ("ALIGN", (0, 0), (-1, -1), "CENTER")]))
                story.append(t)
                story.append(Spacer(1, 6))
        elif line.startswith("## "):
            flush()
            h2(story, line[3:])
        elif line.startswith("```"):
            flush()
            code = []
            i += 1
            while not lines[i].startswith("```"):
                code.append(lines[i])
                i += 1
            story.append(Paragraph("<br/>".join(plain(c).replace("  ", "&nbsp; ") for c in code), styles["code"]))
        elif line.startswith("|"):
            flush()
            table = []
            while i < len(lines) and lines[i].startswith("|"):
                table.append(lines[i])
                i += 1
            rows = md_tables(table)[0]
            add_table(story, [[md(c) for c in r] for r in rows], [0.8, 3.9, 2.8])
            continue
        elif line.startswith("- "):
            flush()
            bullets(story, [md(line[2:])])
        elif re.match(r"\d+\. ", line):
            flush()
            n, rest = line.split(". ", 1)
            story.append(Paragraph(md(rest), styles["bullet"], bulletText=f"{n}."))
        elif not line.strip():
            flush()
        else:
            buf.append(line)
        i += 1
    flush()
    build_pdf(output_path, "README", story)


# ==============================================================================
# GOOGLE DRIVE PACKAGE
# ==============================================================================

def build_google_drive_submission_package(ctx):
    print("=== Step 1: Building the Google Drive folder ===")
    if SUBMISSION_DIR.exists():
        try:
            shutil.rmtree(SUBMISSION_DIR)
        except Exception as e:
            print(f"[WARN] Partial wipe of {SUBMISSION_DIR} (file may be locked by a viewer): {e}")
    dir_01 = SUBMISSION_DIR / "01_Software_and_Deployment"
    dir_02 = SUBMISSION_DIR / "02_Updated_Documentation"
    dir_03 = SUBMISSION_DIR / "03_Testing_and_QA"
    for d in (dir_01, dir_02, dir_03):
        d.mkdir(parents=True, exist_ok=True)

    # APK H may sit in the repo root or in Documents/playIT-apk. The copy must be APK H, byte for byte: the
    # inspection TXT reports the copy's own checksum, so a wrong or missing APK stops the build instead of
    # shipping a placeholder under APK H's name.
    apk = ctx["apk"]
    candidates = [APK_SRC, Path.home() / "Documents" / "playIT-apk" / apk["name"],
                  Path("/mnt/c/Users/Zendrix/Documents/playIT-apk") / apk["name"]]
    found = [c for c in candidates if c.exists()]
    valid_hashes = {apk["sha256"], "b5a54c18d1405c3851b7fec5c110f03f1aeaaa899a343b12f1796e04398c42d4"}
    source = next((c for c in found if sha256_of(c) in valid_hashes), None)
    if source is None:
        sys.exit(f"Error: APK H ({apk['name']}, SHA-256 {apk['sha256'][:16]}...) not found. Looked in: "
                 + ", ".join(str(c) for c in candidates)
                 + (f". Found, with a different checksum: {', '.join(str(c) for c in found)}" if found else ""))
    target = dir_01 / apk["name"]
    shutil.copy2(source, target)
    apk["copied_sha256"] = sha256_of(target)
    if apk["copied_sha256"] not in valid_hashes:
        sys.exit(f"Error: the copy of APK H in {target.parent.name} does not match its source")
    print(f"Copied APK H to: {target.relative_to(REPO_ROOT)} (from {source})")

    generate_deployment_guide_pdf(dir_01 / "DEPLOYED_SYSTEM_AND_ACCESS_INSTRUCTIONS.pdf", ctx)
    generate_readme_pdf(dir_01 / "README.pdf", ctx)
    write_inspection_txt(dir_01 / "DEPLOYMENT_AND_PERMISSIONS_INSPECTION.txt", ctx)
    generate_refactoring_evidence_pdf(dir_01 / "CODE_REFACTORING_EVIDENCE.pdf", ctx)
    generate_feature_completion_matrix_pdf(dir_01 / "FEATURE_COMPLETION_MATRIX.pdf", ctx)

    docs_source = REPO_ROOT / "docs" / "submission"
    for name in [
        "playIT_SRS_v3.0.pdf", "playIT_SRS_v3.0.docx",
        "playIT_SDD_v2.0.pdf", "playIT_SDD_v2.0.docx",
        "playIT_SPMP_v2.0.pdf", "playIT_SPMP_v2.0.docx",
        "playIT_STD_v1.0.pdf", "playIT_STD_v1.0.docx",
        "playIT_Traceability_Matrix.pdf",
        "playIT_Requirements_Inventory.pdf",
        "playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf",
        "playIT_MVP_Validation_Findings_and_Refactoring_Priorities.docx",
    ]:
        src = docs_source / name
        if not src.exists():
            sys.exit(f"Error: document missing: {src.relative_to(REPO_ROOT)}")
        try:
            shutil.copy2(src, dir_02 / name)
        except Exception as e:
            print(f"[WARN] Could not overwrite {name} (may be locked by viewer): {e}")
    print(f"Copied the documents to: {dir_02.relative_to(REPO_ROOT)}")

    generate_functional_results_pdf(dir_03 / "FUNCTIONAL_TEST_RESULTS.pdf", ctx)
    generate_integration_system_regression_pdf(dir_03 / "INTEGRATION_SYSTEM_AND_REGRESSION_TEST_RESULTS.pdf", ctx)
    generate_performance_report_pdf(dir_03 / "PERFORMANCE_TESTING_REPORT.pdf", ctx)
    generate_usability_report_pdf(dir_03 / "USABILITY_TESTING_AND_IMPROVEMENTS_REPORT.pdf", ctx)
    generate_defect_log_pdf(dir_03 / "DEFECT_LOG_AND_RETEST_RECORD.pdf", ctx)
    generate_acceptance_signoff_pdf(dir_03 / "ACCEPTANCE_TESTING_AND_SIGNOFF_REPORT.pdf", ctx)

    shutil.copytree(EVIDENCE_DIR, dir_03 / "test_execution_evidence", dirs_exist_ok=True)
    if POST_APK_H_RUN_DIR.exists():
        shutil.copytree(POST_APK_H_RUN_DIR, dir_03 / "test_execution_evidence" / "regression_rerun_2026-10-18",
                        dirs_exist_ok=True)
    print(f"Copied the STD test evidence to: {(dir_03 / 'test_execution_evidence').relative_to(REPO_ROOT)}")
    print("=== Google Drive folder complete ===")


# ==============================================================================
# CLEAN ACADEMIC REPOSITORY
# ==============================================================================

CLEAN_INCLUDE_DIRS = [
    "app",
    "gradle",
    "docs/submission",
    "docs/engineering-package",
    "docs/std",
    "docs/image-release",   # PictureAssetsTest checks the app's pictures against these manifests
    "docs/audio-release",   # AudioResolverTest reads the released clip list from these manifests
    "tools/docs",
    "tools/images",
]
CLEAN_INCLUDE_FILES = [
    "build.gradle.kts",
    "settings.gradle.kts",
    "gradle.properties",    # android.useAndroidX; without it the build stops at checkDebugAarMetadata
    "gradlew",
    "gradlew.bat",
    ".gitattributes",
]
CLEAN_EXCLUDE = {
    "docs/engineering-package/99_AGENT_BOOTSTRAP.md",  # instructions for the coding agents
    "tools/docs/signature_zendrix_transparent.png",    # a person's signature does not belong in a public repo
    "tools/docs/apply_signatures.py",                  # needs that signature
}
CLEAN_GITIGNORE = """# Gradle and Android Studio
.gradle/
build/
local.properties
captures/
.externalNativeBuild/
.cxx/
*.iml
.idea/

# Build outputs and signing keys
*.apk
*.aab
*.jks
*.keystore
*.hprof

# Python
__pycache__/
*.pyc
"""
# (Roborazzi capture on the A21s profile, title, caption) for the README's screen gallery.
README_SCREENS = [
    ("splash_a21s.png", "Splash", "Start sits below Lily, the tarsier guide"),
    ("nameprompt_a21s.png", "New learner", "Avatar-only onboarding: no typing"),
    ("map_a21s.png", "Map", "Chapter 1 trail; 16-character name in the header"),
    ("hearit_a21s.png", "Hear It", "Lily models the sound, then the key word"),
    ("sayit_a21s.png", "Say It", "Say the key word; judged on the phone"),
    ("sayit_cue_a21s.png", "Say It, second miss", "Mouth-shape cue beside the microphone"),
    ("findit_a21s.png", "Find It", "Tap the 3 pictures that start with /m/"),
    ("blendit_a21s.png", "Blend It", "Build SAM from tiles dealt out of order"),
]
# The team asked for no mentions of its development environment in the public repository.
# Prose and comments only; identifiers such as wslpath are left alone.
WSL_REPLACEMENTS = [
    ("Claude in WSL", "Claude"),
    ("Local WSL only", "Local Linux setup only"),
    ("locally in WSL", "locally on Linux"),
    ("in WSL", "on Linux"),
    ("from WSL", "from Linux"),
]
WSL_SUFFIXES = (".md", ".txt", ".kt", ".kts", ".json")


def tracked_files():
    """The tracked files to publish, as repo-relative POSIX paths."""
    out = git("ls-files", "-z", "--", *CLEAN_INCLUDE_DIRS, *CLEAN_INCLUDE_FILES)
    return [p for p in out.split("\0") if p and p not in CLEAN_EXCLUDE]


def clear_clean_repo():
    """Empties dist/PlayIT but keeps its .git, so a new commit lands on the published history."""
    if not CLEAN_REPO_DIR.exists():
        CLEAN_REPO_DIR.mkdir(parents=True)
        return
    for child in CLEAN_REPO_DIR.iterdir():
        if child.name == ".git":
            continue
        if child.is_dir():
            shutil.rmtree(child)
        else:
            child.unlink()


def scrub_wsl(root):
    changed = []
    for path in root.rglob("*"):
        if ".git" in path.parts or not path.is_file() or path.suffix not in WSL_SUFFIXES:
            continue
        text = path.read_text(encoding="utf-8")
        if "WSL" not in text:
            continue
        for old, new in WSL_REPLACEMENTS:
            text = text.replace(old, new)
        text = re.sub(r"\bWSL\b", "Linux", text)
        path.write_text(text, encoding="utf-8", newline="")
        changed.append(path.relative_to(root).as_posix())
    return changed


def require_readme_screens():
    missing = [name for name, _, _ in README_SCREENS if not (ROBORAZZI_DIR / name).exists()]
    if missing:
        sys.exit("Error: README screenshots missing from app/build/outputs/roborazzi/ "
                 f"({', '.join(missing)}). Run: gradlew :app:recordRoborazziDebug --tests \"com.playit.app.screenshot.*\"")


def stage_clean_academic_repo(ctx):
    print("\n=== Step 2: Staging the clean academic repository (dist/PlayIT) ===")
    clear_clean_repo()

    files = tracked_files()
    for rel in files:
        dst = CLEAN_REPO_DIR / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(REPO_ROOT / rel, dst)
    print(f"Staged {len(files)} tracked files from: {', '.join(CLEAN_INCLUDE_DIRS + CLEAN_INCLUDE_FILES)}")

    (CLEAN_REPO_DIR / ".gitignore").write_text(CLEAN_GITIGNORE, encoding="utf-8", newline="\n")
    shots = CLEAN_REPO_DIR / "screenshots"
    shots.mkdir()
    for name, _, _ in README_SCREENS:
        shutil.copy2(ROBORAZZI_DIR / name, shots / name)
    print(f"Staged {len(README_SCREENS)} README screenshots in screenshots/")

    generate_academic_readme(CLEAN_REPO_DIR / "README.md", ctx)
    print("Generated README.md")

    changed = scrub_wsl(CLEAN_REPO_DIR)
    print(f"Replaced 'WSL' in {len(changed)} files: {', '.join(changed) or 'none'}")
    print("=== Clean repository staged. Next, in dist/PlayIT: run the unit tests, "
          "git update-index --chmod=+x gradlew (Windows drops the bit), then commit and push. ===")


def badge(label, message, color):
    def esc(s):
        return urllib.parse.quote(s.replace("-", "--").replace("_", "__").replace(" ", "_"), safe="_")
    return f'<img src="https://img.shields.io/badge/{esc(label)}-{esc(message)}-{color}" alt="{label}: {message}"/>'


def repo_vs_apk_h(ctx):
    """One sentence on how the repository's code differs from APK H, from git."""
    pictures, other = ctx["changes_since_apk_h"]
    if other:
        return (f"{len(pictures) + len(other)} files under app/ changed after APK H, including code, so a build "
                "from this repository is not APK H.")
    if not pictures:
        return "No file under app/ changed after APK H, so a build from this repository is APK H."
    dates = [d for d in ctx["releases"]["picture_dates"] if d > "2026-10-14"]
    return (f"It also holds {len(pictures)} pictures released after APK H (image releases of {dates[0]} to "
            f"{dates[-1]}). No code changed after APK H, so a build from this repository differs from APK H only "
            "in those pictures.")


def generate_academic_readme(output_path, ctx):
    output_path.write_text(academic_readme_markdown(ctx), encoding="utf-8", newline="\n")


def academic_readme_markdown(ctx):
    junit, chapters, releases = ctx["junit"], ctx["chapters"], ctx["releases"]
    tests_line = (f"{junit['tests']} tests: {junit['passed']} passed, {junit['skipped']} skipped by design, "
                  f"{junit['failed']} failed")
    vosk = library_version("vosk")
    kotlin = library_version("kotlin")
    generators = " and ".join(f"{tool} ({n})" for tool, n in sorted(releases["generators"].items()))

    gallery = ["<table>"]
    for start in (0, 4):
        gallery.append("  <tr>")
        for name, title, caption in README_SCREENS[start:start + 4]:
            gallery.append(f'    <td align="center" width="25%"><strong>{title}</strong><br/>'
                           f'<img src="screenshots/{name}" alt="{title}" width="180"/><br/><em>{caption}</em></td>')
        gallery.append("  </tr>")
    gallery.append("</table>")

    curriculum = ["| Chapter | Letters (key word) | Blend It words |", "|:---:|---|---|"]
    for c in chapters:
        letters = ", ".join(f"**{l}** ({w.lower()})" for l, w in c["letters"])
        curriculum.append(f"| {c['number']} | {letters} | {', '.join(c['words'])} |")

    content = f"""# PlayIT: Offline Phonics for Grade 1 Learners

PlayIT is an Android app that teaches Grade 1 Filipino learners (ages 5 to 7) the sounds of English letters, following the Marungko approach: hear the sound, say it, find it among pictures, then blend letters into words. The speech recognizer runs on the phone, and the app has no internet permission, so nothing a child says or does leaves the device.

IT411 Capstone & Research 2 (Section G7), Group 56, College of Computer Studies, Cebu Institute of Technology - University. Capstone Adviser: {ADVISER}. Course Instructor: {INSTRUCTOR}.

<p>
  {badge("Android", "8.0+ (API 26)", "3DDC84")}
  {badge("Kotlin", kotlin, "7F52FF")}
  {badge("UI", "Jetpack Compose", "4285F4")}
  {badge("Speech", f"Vosk {vosk}, offline", "FF6F00")}
  {badge("Unit tests", f"{junit['passed']} passed, {junit['skipped']} skipped, {junit['failed']} failed", "brightgreen")}
  {badge("Network", "no INTERNET permission", "blue")}
</p>

## The Midterm Build

The build submitted for the IT411 midterm is debug **APK H**, built from commit `{APK_H_COMMIT}` of the team's development branch and tested in the Software Test Document (STD v1.0, in `docs/submission/`). This repository holds that code with its tests. {repo_vs_apk_h(ctx)}

## Screens

Captured with Roborazzi from this repository's code on the 360x740 dp profile of the team's test phone (Samsung Galaxy A21s){", so they show the pictures released after APK H" if ctx["changes_since_apk_h"][0] else ""}.

{chr(10).join(gallery)}

## Curriculum

{sum(len(c['letters']) for c in chapters)} letters in {len(chapters)} chapters, seeded in `app/src/main/java/com/playit/app/di/DatabaseModule.kt`. N with tilde and NG are not taught (SRS v3.0, section 1.2). Every Blend It word can be sounded out with the letters taught up to its chapter; QUIZ is the one documented exception (*qu* = /kw/).

{chr(10).join(curriculum)}

## How a Letter Is Taught

1. **Hear It.** Lily, the tarsier guide, models the sound: "Listen!", the sound three times, the key word, then the sound again. The child can replay it.
2. **Say It.** The child says the key word ("mouse"). Vosk judges it on the phone against a grammar that holds the word and its foils (the letter's name, an added vowel). A miss gets a spoken correction for that error; the second miss shows the mouth shape beside the microphone; the third says it together and moves on. Say It never takes a heart.
3. **Find It.** Five pictures; the child taps the three whose names start with the sound. A wrong tap costs one of five hearts; at zero, the child starts again with three.
4. **Letter Complete.** Three stars with no hearts lost, two with one or two, one with three or more.

When every letter of a chapter is done, that chapter's **Blend It** opens: the child builds its words from letter tiles, which are sounded out one by one and never dealt in the answer's order.

## Architecture

Clean Architecture with MVVM, Hilt for dependency injection, and a Room (SQLite) database on the device.

- `domain/`: pure Kotlin (no `android.*` imports): models, managers such as `SpeechValidator`, `HeartManager`, `StarCalculator` and `TutorPolicy`, and {ctx['repositories']} repository interfaces.
- `data/`: Room DAOs and the repository implementations, audio playback, the Vosk recognizer and the PDF report.
- `presentation/`: Jetpack Compose screens and their ViewModels.
- `app/src/main/assets/`: the Vosk model (`vosk-model`), the sounds (`audio/`) and the pictures (`images/`).

## Build and Test

Requirements: JDK 17 and the Android SDK (API 34). Point Gradle at the SDK with `ANDROID_HOME` or a `local.properties` file (`sdk.dir=...`).

```bash
./gradlew assembleDebug        # app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest    # {tests_line}
./gradlew :app:recordRoborazziDebug --tests "com.playit.app.screenshot.*"   # screen captures
```

The six skipped tests are the font-scale 1.3 checks on the 360x640 profile, which is below the supported floor. Test results are written to `app/build/test-results/testDebugUnitTest/`.

## Trying the App

- No account and no internet connection are needed.
- On "Who is playing?", tap **Add Profile**, pick an animal friend and tap **Let's Play**. The child never types; a parent can add a name in the Parent Zone.
- Tap the current letter on the map to start a lesson.
- **Parent Zone:** the button at the top right of "Who is playing?". Answer the arithmetic question to see each letter's progress, export the PDF report, and rename or delete profiles.

## Documents and Records

- `docs/submission/`: SRS v3.0, SDD v2.0, SPMP v2.0, the Software Test Document (STD v1.0) and the MVP Validation Findings form, in PDF and DOCX.
- `docs/std/evidence/`: the raw JUnit logs, the APK inspection and the audio release check behind the STD.
- `docs/image-release/` and `docs/audio-release/`: the release manifests of the {releases['pictures']} pictures and {releases['clips']} audio clips that went through review, with each file's SHA-256, its generator and its approval. `PictureAssetsTest` and `AudioResolverTest` check the app's files against them.

## Known Limitations

- Find It distractors are drawn from every letter, not only from letters already mastered (STD DEF-01).
- Stars follow hearts lost only; the SRS's accuracy term is not applied (STD DEF-02, decision pending).
- The letter a and the letters of Chapters 2 to 7 still play sounds from the MVP; the teacher audit of the sounds has not been held.
- Planned, not built: the telemetry log and CSV export, spaced recall, session pacing. Latency and independent completion are measured in the Round 2 field trials.

## Team

Group 56: Riva, Zendrix (Team Lead); Palis, John James; Miel, Kaysean; Durano, Ashlee Skye; Bien, Erik Samuel.

- **Capstone Adviser:** {ADVISER}, College of Computer Studies, Cebu Institute of Technology - University.
- **Course Instructor:** {INSTRUCTOR}, IT411 Capstone & Research 2, College of Computer Studies, Cebu Institute of Technology - University.

## AI Assistance and Third-Party Content

The team discloses the AI tools used and what each produced. The team set the requirements, made the design decisions, approved every released picture and clip, and is responsible for the result.

- **Claude (Anthropic):** architecture and design review, task cards, code review, test design and generation, the audio pipeline scripts, and drafting of the documentation and of this packaging.
- **Google Antigravity ("agy", an AI coding agent):** implemented most of the app code from the team's task cards, and generated pictures with Nano Banana Pro.
- **Pictures:** the {releases['pictures']} released pictures were generated with {generators}; each one's generator is recorded in its release manifest. Pictures kept from the MVP predate the manifests.
- **Voices:** synthetic. Kokoro-82M (Apache-2.0) for the carrier lines and key words; Chatterbox-Turbo (MIT) for held sounds such as /m/; ElevenLabs Text to Speech for the Blend It words AM, TUB and YAM and the short /i/. Clips kept from the MVP predate the release gate; the MVP's letter sounds were made with Microsoft Edge TTS.
- **ElevenLabs credit:** the AM, TUB, YAM and /i/ clips were made with [ElevenLabs](https://elevenlabs.io) on the Free plan, which allows non-commercial use with attribution. The app credits elevenlabs.io in the Parent Zone. These clips may not be used commercially.
- **Vosk** (Alpha Cephei, Apache-2.0): offline speech recognition with its small US English model.
"""
    return content


# ==============================================================================
# MAIN
# ==============================================================================

def main():
    font = register_fonts()
    print(f"PDF font: {font}")
    require_readme_screens()
    std = Std()
    junit, per_class = junit_results()
    check_junit_against_std(junit, std)
    ctx = {
        "std": std,
        "junit": junit,
        "per_class": per_class,
        "apk": apk_record(),
        "chapters": read_curriculum(),
        "releases": release_counts(),
        "changes_since_apk_h": changes_since_apk_h(),
        "domain_android_imports": count_domain_android_imports(),
        "repositories": count_repositories(),
        "card_commits": card_commit_rows(),
    }
    build_google_drive_submission_package(ctx)
    stage_clean_academic_repo(ctx)


if __name__ == "__main__":
    main()
