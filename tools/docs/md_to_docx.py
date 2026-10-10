"""Markdown to .docx rendering for the IT411 submission package (python-docx only).

Covers the Markdown the academic documents use: headings, paragraphs with hard
breaks, bullet and numbered lists (nested), GFM pipe tables (inline formatting
and <br> inside cells), fenced code blocks and ASCII diagrams, block quotes,
and $...$ / $$...$$ LaTeX, which becomes plain Unicode text (>= becomes the
"greater than or equal" sign, \\frac{a}{b} becomes a/b, and so on).

render_submission_package.py builds whole documents from these pieces;
fill_mvp_validation_form.py reuses add_inline() and latex_to_text().
"""

from __future__ import annotations

import re
from dataclasses import dataclass, field

from docx.enum.section import WD_ORIENT, WD_SECTION
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Emu, Inches, Pt, RGBColor

TEAL = RGBColor(0x1F, 0x3A, 0x3D)
PLUM = RGBColor(0x6B, 0x4E, 0x71)
TEXT = RGBColor(0x2D, 0x37, 0x48)
MUTED = RGBColor(0x71, 0x80, 0x96)
CODE = RGBColor(0x1F, 0x3A, 0x3D)

BODY_FONT = "Arial"
CODE_FONT = "Consolas"
MATH_FONT = "Cambria Math"
SYMBOL_FONT = "Segoe UI Symbol"

TABLE_HEADER_FILL = "1F3A3D"
TABLE_BAND_FILL = "F3F6F8"
TABLE_BORDER = "B8C2CC"
CODE_FILL = "F4F6F8"

# Tables with at least this many columns go on a landscape page.
LANDSCAPE_MIN_COLUMNS = 7


# ---------------------------------------------------------------------------
# LaTeX to Unicode
# ---------------------------------------------------------------------------

_SYMBOLS = {
    "ge": "≥", "geq": "≥", "le": "≤", "leq": "≤", "neq": "≠", "times": "×", "cdot": "·",
    "pm": "±", "approx": "≈", "rightarrow": "→", "longrightarrow": "→", "to": "→",
    "Rightarrow": "⇒", "sum": " Σ", "log": "log", "infty": "∞", "dots": "…", "ldots": "…",
}
_SPACES = {",": "\u00a0", ";": " ", ":": " ", "!": "", " ": " ", "quad": "\u2003", "qquad": "\u2003\u2003"}
_ESCAPES = {"%": "%", "&": "&", "_": "_", "{": "{", "}": "}", "#": "#", "$": "$"}
_TEXT_COMMANDS = {"text", "mathrm", "textrm", "textbf", "mathbf", "textit", "mathit", "operatorname"}
_SUP = str.maketrans("0123456789+-−=()nNi", "⁰¹²³⁴⁵⁶⁷⁸⁹⁺⁻⁻⁼⁽⁾ⁿᴺⁱ")
_SUB = str.maketrans("0123456789+-−=()aeoxijn", "₀₁₂₃₄₅₆₇₈₉₊₋₋₌₍₎ₐₑₒₓᵢⱼₙ")
_OPS = "≥≤≠×·±≈→⇒=<>+−"


def _needs_parens(expr: str) -> bool:
    depth = 0
    for ch in expr:
        if ch == "(":
            depth += 1
        elif ch == ")":
            depth -= 1
        elif depth == 0 and ch in _OPS:
            return True
    return False


def _script(arg: str, sup: bool) -> str:
    table = _SUP if sup else _SUB
    converted = arg.translate(table)
    if all(not c.isascii() or c == " " for c in converted) and " " not in arg:
        return converted
    return ("^" if sup else "_") + (f"({arg})" if len(arg) > 1 else arg)


class _Latex:
    def __init__(self, src: str):
        self.s = src
        self.i = 0

    def parse(self, stop: str | None = None) -> str:
        out = []
        while self.i < len(self.s):
            c = self.s[self.i]
            if stop is not None and c == stop:
                self.i += 1
                return "".join(out)
            if c == "\\":
                out.append(self.command())
            elif c == "{":
                self.i += 1
                out.append(self.parse("}"))
            elif c in "^_":
                self.i += 1
                out.append(_script(self.argument(), sup=(c == "^")))
            elif c == "-":
                self.i += 1
                out.append("−")
            else:
                self.i += 1
                out.append(c)
        return "".join(out)

    def _skip_spaces(self):
        while self.i < len(self.s) and self.s[self.i] == " ":
            self.i += 1

    def argument(self) -> str:
        self._skip_spaces()
        if self.i >= len(self.s):
            return ""
        c = self.s[self.i]
        if c == "{":
            self.i += 1
            return self.parse("}")
        if c == "\\":
            return self.command()
        self.i += 1
        return "−" if c == "-" else c

    def text_group(self) -> str:
        self._skip_spaces()
        if self.i >= len(self.s) or self.s[self.i] != "{":
            return self.argument()
        self.i += 1
        depth, buf = 1, []
        while self.i < len(self.s):
            ch = self.s[self.i]
            if ch == "\\" and self.i + 1 < len(self.s):
                nxt = self.s[self.i + 1]
                self.i += 2
                if nxt in _ESCAPES:
                    buf.append(_ESCAPES[nxt])
                elif nxt == ",":
                    buf.append("\u00a0")
                continue
            if ch == "{":
                depth += 1
            elif ch == "}":
                depth -= 1
                if depth == 0:
                    self.i += 1
                    break
            buf.append(ch)
            self.i += 1
        return "".join(buf).replace("---", "—").replace("--", "–")

    def command(self) -> str:
        self.i += 1
        if self.i >= len(self.s):
            return ""
        c = self.s[self.i]
        if not c.isalpha():
            self.i += 1
            if c in _ESCAPES:
                return _ESCAPES[c]
            return _SPACES.get(c, c)
        j = self.i
        while j < len(self.s) and self.s[j].isalpha():
            j += 1
        name, self.i = self.s[self.i:j], j
        if name in _TEXT_COMMANDS:
            return self.text_group()
        if name == "frac":
            num, den = self.argument().strip(), self.argument().strip()
            num = f"({num})" if _needs_parens(num) else num
            den = f"({den})" if _needs_parens(den) else den
            sep = " / " if (" " in num or " " in den) else "/"
            return f"{num}{sep}{den}"
        if name == "sqrt":
            arg = self.argument().strip()
            return f"√({arg})" if len(arg) > 1 else f"√{arg}"
        if name in ("left", "right", "big", "Big", "bigl", "bigr", "Bigl", "Bigr"):
            if self.i < len(self.s):
                delim = self.s[self.i]
                self.i += 1
                return "" if delim == "." else delim
            return ""
        if name in _SPACES:
            return _SPACES[name]
        return _SYMBOLS.get(name, name)


def latex_to_text(src: str) -> str:
    """Convert a LaTeX math expression (without the $ signs) to readable Unicode text."""
    s = _Latex(src.strip()).parse()
    s = re.sub(rf"[ ]*([{_OPS}])[ ]*", r" \1 ", s)
    s = re.sub(r"^\s*([+−])\s+", r"\1", s)  # leading sign stays attached: +1
    s = re.sub(r"([(\[,])\s*([+−])\s+", r"\1\2", s)
    s = re.sub(r"\(\s+", "(", s)
    s = re.sub(r"\s+\)", ")", s)
    s = re.sub(r"\s+,", ",", s)
    s = re.sub(r"[ ]{2,}", " ", s)
    return s.strip()


# ---------------------------------------------------------------------------
# Inline Markdown
# ---------------------------------------------------------------------------

_INLINE = re.compile(
    r"(?P<code_tick>`+)(?P<code>.+?)(?P=code_tick)"
    r"|(?<![\\$\w])\$(?![\s$])(?P<math>[^$\n]+?)(?<![\\\s])\$(?![$\w])"
    r"|\*\*\*(?P<bold_italic>.+?)\*\*\*"
    r"|\*\*(?P<bold>.+?)\*\*"
    r"|(?<![\w*])\*(?![\s*])(?P<italic>[^*]+?)(?<![\s\\])\*(?![\w*])"
    r"|\[(?P<link_text>[^\]]+)\]\((?P<link_url>[^)\s]+)\)"
    r"|(?P<br><br\s*/?>)"
    r"|\\(?P<escape>[\\`*_{}\[\]()#+\-.!|$<>])"
)


@dataclass
class Segment:
    text: str
    bold: bool = False
    italic: bool = False
    code: bool = False
    math: bool = False
    br: bool = False


def parse_inline(text: str, bold: bool = False, italic: bool = False) -> list[Segment]:
    """Split inline Markdown into formatted segments."""
    out: list[Segment] = []
    pos = 0
    for m in _INLINE.finditer(text):
        if m.start() > pos:
            out.append(Segment(text[pos:m.start()], bold, italic))
        if m.group("code") is not None:
            out.append(Segment(m.group("code"), bold, italic, code=True))
        elif m.group("math") is not None:
            out.append(Segment(latex_to_text(m.group("math")), bold, italic, math=True))
        elif m.group("bold_italic") is not None:
            out.extend(parse_inline(m.group("bold_italic"), True, True))
        elif m.group("bold") is not None:
            out.extend(parse_inline(m.group("bold"), True, italic))
        elif m.group("italic") is not None:
            out.extend(parse_inline(m.group("italic"), bold, True))
        elif m.group("link_text") is not None:
            out.extend(parse_inline(m.group("link_text"), bold, italic))
        elif m.group("br") is not None:
            out.append(Segment("", br=True))
        elif m.group("escape") is not None:
            out.append(Segment(m.group("escape"), bold, italic))
        pos = m.end()
    if pos < len(text):
        out.append(Segment(text[pos:], bold, italic))
    return out


def plain_text(text: str) -> str:
    """Inline Markdown without its markup (used to size table columns)."""
    return "".join(" " if s.br else s.text for s in parse_inline(text))


def set_run_font(run, name=None, size=None, color=None, bold=None, italic=None):
    if name:
        run.font.name = name
        rpr = run._r.get_or_add_rPr()
        rfonts = rpr.find(qn("w:rFonts"))
        if rfonts is None:
            rfonts = OxmlElement("w:rFonts")
            rpr.insert(0, rfonts)
        for attr in ("w:ascii", "w:hAnsi", "w:cs", "w:eastAsia"):
            rfonts.set(qn(attr), name)
    if size is not None:
        run.font.size = Pt(size)
    if color is not None:
        run.font.color.rgb = color
    if bold is not None:
        run.bold = bold
    if italic is not None:
        run.italic = italic


def add_inline(paragraph, text: str, size: float | None = None, color=None,
               bold: bool = False, italic: bool = False, font: str | None = None, break_code: bool = False):
    """Append inline Markdown to a python-docx paragraph as formatted runs.

    break_code lets long code spans wrap after '/', '.' and '_' (used in narrow table cells).
    """
    for seg in parse_inline(text, bold, italic):
        if seg.br:
            paragraph.add_run().add_break()
            continue
        if not seg.text:
            continue
        if seg.code and break_code:
            seg.text = re.sub(r"([/._])(?=\w)", "\\1\u200b", seg.text)
        for part in re.split(r"([☐☒☑✓])", seg.text):
            if not part:
                continue
            run = paragraph.add_run(part)
            if part in "☐☒☑✓":
                set_run_font(run, SYMBOL_FONT, size, color)
            elif seg.code:
                set_run_font(run, CODE_FONT, (size or 10) - 0.5, CODE, bold=seg.bold, italic=seg.italic)
            else:
                set_run_font(run, font, size, color, bold=seg.bold or None, italic=seg.italic or None)
    return paragraph


# ---------------------------------------------------------------------------
# Block parsing
# ---------------------------------------------------------------------------

@dataclass
class Block:
    kind: str  # heading, para, list, code, math, table, quote, hr
    text: str = ""
    level: int = 0
    items: list = field(default_factory=list)  # list: (level, marker, text)
    rows: list = field(default_factory=list)  # table rows (list of cell strings)
    aligns: list = field(default_factory=list)
    lines: list = field(default_factory=list)  # code lines


_HEADING = re.compile(r"^(#{1,6})\s+(.*?)\s*#*\s*$")
_LIST = re.compile(r"^(\s*)([-*+]|\d+[.)])\s+(.*)$")
_TABLE_SEP = re.compile(r"^\s*\|?\s*:?-{2,}:?\s*(\|\s*:?-{2,}:?\s*)*\|?\s*$")
_HR = re.compile(r"^\s*([-*_])(\s*\1){2,}\s*$")


def split_row(line: str) -> list[str]:
    """Split a pipe-table row, keeping | inside code spans and math."""
    s = line.strip()
    if s.startswith("|"):
        s = s[1:]
    if s.endswith("|") and not s.endswith("\\|"):
        s = s[:-1]
    cells, buf, in_code, in_math = [], [], False, False
    i = 0
    while i < len(s):
        ch = s[i]
        if ch == "\\" and i + 1 < len(s) and s[i + 1] == "|":
            buf.append("|")
            i += 2
            continue
        if ch == "`":
            in_code = not in_code
        elif ch == "$" and not in_code:
            in_math = not in_math
        if ch == "|" and not in_code and not in_math:
            cells.append("".join(buf).strip())
            buf = []
        else:
            buf.append(ch)
        i += 1
    cells.append("".join(buf).strip())
    return cells


def _list_level(indent: int) -> int:
    if indent < 2:
        return 0
    if indent < 5:
        return 1
    if indent < 8:
        return 2
    return 3


def parse_blocks(lines: list[str]) -> list[Block]:
    blocks: list[Block] = []
    i, n = 0, len(lines)
    para: list[str] = []

    def flush_para():
        if para:
            text = ""
            for k, ln in enumerate(para):
                hard = ln.endswith("  ") or ln.endswith("\\")
                piece = ln.rstrip("\\").strip()
                text += piece
                if k < len(para) - 1:
                    text += "<br>" if hard else " "
            blocks.append(Block("para", text=text))
            para.clear()

    while i < n:
        line = lines[i]
        stripped = line.strip()
        if not stripped:
            flush_para()
            i += 1
            continue
        if stripped.startswith("```"):
            flush_para()
            fence = stripped[:3]
            code, i = [], i + 1
            while i < n and not lines[i].strip().startswith(fence):
                code.append(lines[i].rstrip("\n"))
                i += 1
            blocks.append(Block("code", lines=code))
            i += 1
            continue
        if stripped.startswith("$$"):
            flush_para()
            body = stripped[2:]
            while not body.rstrip().endswith("$$") and i + 1 < n:
                i += 1
                body += " " + lines[i].strip()
            blocks.append(Block("math", text=body.rstrip()[:-2].strip()))
            i += 1
            continue
        m = _HEADING.match(stripped)
        if m:
            flush_para()
            blocks.append(Block("heading", text=m.group(2), level=len(m.group(1))))
            i += 1
            continue
        if _HR.match(stripped):
            flush_para()
            blocks.append(Block("hr"))
            i += 1
            continue
        if stripped.startswith("|") and i + 1 < n and _TABLE_SEP.match(lines[i + 1]):
            flush_para()
            header = split_row(stripped)
            seps = split_row(lines[i + 1])
            aligns = []
            for sep in seps:
                sep = sep.strip()
                if sep.startswith(":") and sep.endswith(":"):
                    aligns.append("center")
                elif sep.endswith(":"):
                    aligns.append("right")
                else:
                    aligns.append("left")
            rows = [header]
            i += 2
            while i < n and lines[i].strip().startswith("|"):
                rows.append(split_row(lines[i]))
                i += 1
            blocks.append(Block("table", rows=rows, aligns=aligns))
            continue
        if stripped.startswith(">"):
            flush_para()
            quote = []
            while i < n and lines[i].strip().startswith(">"):
                quote.append(lines[i].strip().lstrip(">").strip())
                i += 1
            blocks.append(Block("quote", text=" ".join(q for q in quote if q)))
            continue
        lm = _LIST.match(line)
        if lm:
            flush_para()
            items = []
            while i < n:
                cur = lines[i]
                lm = _LIST.match(cur)
                if lm:
                    indent = len(lm.group(1).expandtabs(4))
                    items.append([_list_level(indent), lm.group(2), lm.group(3).rstrip()])
                    i += 1
                    continue
                if cur.strip().startswith("$$"):
                    break  # display math gets its own centred block
                if cur.strip() and cur.startswith((" ", "\t")) and items:
                    prev = items[-1][2]
                    joiner = "<br>" if lines[i - 1].endswith("  ") else " "
                    items[-1][2] = prev.rstrip() + joiner + cur.strip()
                    i += 1
                    continue
                if not cur.strip() and i + 1 < n and _LIST.match(lines[i + 1]):
                    i += 1
                    continue
                break
            blocks.append(Block("list", items=[tuple(it) for it in items]))
            continue
        para.append(line.rstrip("\n"))
        i += 1
    flush_para()
    return blocks


# ---------------------------------------------------------------------------
# Low-level docx helpers
# ---------------------------------------------------------------------------

def shade(cell, fill_hex: str):
    tc_pr = cell._tc.get_or_add_tcPr()
    for old in tc_pr.findall(qn("w:shd")):
        tc_pr.remove(old)
    shd = OxmlElement("w:shd")
    shd.set(qn("w:val"), "clear")
    shd.set(qn("w:color"), "auto")
    shd.set(qn("w:fill"), fill_hex)
    tc_pr.append(shd)


def set_table_borders(table, color=TABLE_BORDER, size=4):
    tbl_pr = table._tbl.tblPr
    borders = OxmlElement("w:tblBorders")
    for edge in ("top", "left", "bottom", "right", "insideH", "insideV"):
        el = OxmlElement(f"w:{edge}")
        el.set(qn("w:val"), "single")
        el.set(qn("w:sz"), str(size))
        el.set(qn("w:space"), "0")
        el.set(qn("w:color"), color)
        borders.append(el)
    for old in tbl_pr.findall(qn("w:tblBorders")):
        tbl_pr.remove(old)
    tbl_pr.append(borders)


def set_cell_margins(table, top=50, bottom=50, left=80, right=80):
    tbl_pr = table._tbl.tblPr
    mar = OxmlElement("w:tblCellMar")
    for edge, val in (("top", top), ("left", left), ("bottom", bottom), ("right", right)):
        el = OxmlElement(f"w:{edge}")
        el.set(qn("w:w"), str(val))
        el.set(qn("w:type"), "dxa")
        mar.append(el)
    tbl_pr.append(mar)


def set_fixed_layout(table):
    tbl_pr = table._tbl.tblPr
    layout = OxmlElement("w:tblLayout")
    layout.set(qn("w:type"), "fixed")
    tbl_pr.append(layout)


def mark_header_row(row):
    tr_pr = row._tr.get_or_add_trPr()
    el = OxmlElement("w:tblHeader")
    el.set(qn("w:val"), "true")
    tr_pr.append(el)


def keep_row_together(row):
    tr_pr = row._tr.get_or_add_trPr()
    el = OxmlElement("w:cantSplit")
    el.set(qn("w:val"), "true")
    tr_pr.append(el)


def add_field(paragraph, instr: str, placeholder: str = "", font=None, size=None, color=None):
    """Insert a complex field (PAGE, NUMPAGES, TOC ...) into a paragraph.

    Every run of the field carries the same formatting, so Word keeps it when it updates the result.
    """
    runs = []

    def fld(kind):
        run = paragraph.add_run()
        el = OxmlElement("w:fldChar")
        el.set(qn("w:fldCharType"), kind)
        if kind == "begin":
            el.set(qn("w:dirty"), "true")
        run._r.append(el)
        runs.append(run)

    fld("begin")
    instr_run = paragraph.add_run()
    it = OxmlElement("w:instrText")
    it.set(qn("xml:space"), "preserve")
    it.text = f" {instr} " if instr.startswith("TOC") else f" {instr} \\* MERGEFORMAT "
    instr_run._r.append(it)
    runs.append(instr_run)
    fld("separate")
    runs.append(paragraph.add_run(placeholder))
    fld("end")
    if font or size or color:
        for run in runs:
            set_run_font(run, font, size, color)
    return runs[-2]


def text_width(section) -> Emu:
    return Emu(section.page_width - section.left_margin - section.right_margin)


def start_section(doc, landscape: bool):
    """Begin a new section on a new page, in landscape or portrait."""
    sec = doc.add_section(WD_SECTION.NEW_PAGE)
    sec.different_first_page_header_footer = False
    w, h = sec.page_width, sec.page_height
    if landscape != (w > h):
        sec.page_width, sec.page_height = h, w
    sec.orientation = WD_ORIENT.LANDSCAPE if landscape else WD_ORIENT.PORTRAIT
    return sec


# ---------------------------------------------------------------------------
# Block rendering
# ---------------------------------------------------------------------------

@dataclass
class Theme:
    body_size: float = 10
    table_size: float = 8.5
    code_size: float = 8
    heading_map: dict = field(default_factory=lambda: {2: 1, 3: 2, 4: 3, 5: 4, 6: 4, 1: 1})


_BULLETS = ["•", "◦", "▪", "▫"]


def _column_widths(rows: list[list[str]], available: int) -> list[int]:
    """Every column gets room for its longest word (capped), and the rest is shared by content length."""
    cols = max(len(r) for r in rows)
    char, pad = Pt(4.7), Pt(10)
    mins, prefs = [], []
    for c in range(cols):
        texts = [plain_text(r[c]) if c < len(r) else "" for r in rows]
        header_words = texts[0].split()
        body_words = [w for t in texts[1:] for w in re.split(r"[\s/._]+", t) if w]
        longest = max([len(w) for w in body_words] + [len(w) * 1.15 for w in header_words] + [1])
        mins.append(min(longest * char + pad, available * 0.24))
        lengths = [len(t) for t in texts[1:]] or [len(texts[0])]
        avg = sum(lengths) / len(lengths)
        prefs.append(max(0.5 * min(max(lengths), 120) + 0.5 * avg, 3))
    total_min = sum(mins)
    if total_min >= available:
        return [int(available * m / total_min) for m in mins]
    spare = available - total_min
    total_pref = sum(prefs)
    return [int(m + spare * p / total_pref) for m, p in zip(mins, prefs)]


def render_table(doc, block: Block, theme: Theme, available: int):
    rows = block.rows
    cols = max(len(r) for r in rows)
    table = doc.add_table(rows=len(rows), cols=cols)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    set_fixed_layout(table)
    set_table_borders(table)
    set_cell_margins(table)
    widths = _column_widths(rows, available)
    for r_idx, row in enumerate(rows):
        tr = table.rows[r_idx]
        if r_idx == 0:
            mark_header_row(tr)
        elif sum(len(c) for c in row) < 700:
            keep_row_together(tr)
        for c_idx in range(cols):
            cell = tr.cells[c_idx]
            cell.width = widths[c_idx]
            text = row[c_idx] if c_idx < len(row) else ""
            p = cell.paragraphs[0]
            p.paragraph_format.space_before = Pt(1)
            p.paragraph_format.space_after = Pt(1)
            p.paragraph_format.line_spacing = 1.05
            align = block.aligns[c_idx] if c_idx < len(block.aligns) else "left"
            if r_idx == 0:
                shade(cell, TABLE_HEADER_FILL)
                add_inline(p, text, size=theme.table_size, color=RGBColor(0xFF, 0xFF, 0xFF), bold=True,
                           break_code=True)
                p.alignment = WD_ALIGN_PARAGRAPH.CENTER if align == "center" else WD_ALIGN_PARAGRAPH.LEFT
            else:
                if r_idx % 2 == 0:
                    shade(cell, TABLE_BAND_FILL)
                add_inline(p, text, size=theme.table_size, color=TEXT, break_code=True)
                if align == "center":
                    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
                elif align == "right":
                    p.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    # Column widths must also be set on the grid for Word to honour them.
    grid = table._tbl.tblGrid
    for c_idx, gc in enumerate(grid.findall(qn("w:gridCol"))):
        gc.set(qn("w:w"), str(int(widths[c_idx] / 635)))
    spacer = doc.add_paragraph()
    spacer.paragraph_format.space_after = Pt(2)
    _shrink(spacer)
    return table


def _shrink(paragraph, size=4):
    paragraph.paragraph_format.space_before = Pt(0)
    paragraph.paragraph_format.line_spacing = 1.0
    run = paragraph.add_run()
    run.font.size = Pt(size)


def render_code(doc, block: Block, theme: Theme, available: int):
    table = doc.add_table(rows=1, cols=1)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    set_fixed_layout(table)
    set_table_borders(table, color="D5DCE3")
    set_cell_margins(table, top=90, bottom=90, left=140, right=140)
    cell = table.rows[0].cells[0]
    cell.width = available
    table._tbl.tblGrid.findall(qn("w:gridCol"))[0].set(qn("w:w"), str(int(available / 635)))
    shade(cell, CODE_FILL)
    lines = [line.expandtabs(4).rstrip() for line in block.lines]
    while lines and not lines[-1].strip():
        lines = lines[:-1]
    if len(lines) <= 45:
        keep_row_together(table.rows[0])
    # Shrink the font for long lines so diagrams never wrap (Consolas is about 0.55 em wide).
    usable_pt = (available - 2 * 140 * 635) / 12700
    longest = max((len(line) for line in lines), default=1)
    size = min(theme.code_size, max(6.5, usable_pt / (0.56 * longest)))
    p = cell.paragraphs[0]
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(0)
    p.paragraph_format.line_spacing = 1.0
    for k, line in enumerate(lines):
        run = p.add_run(line)
        for t in run._r.findall(qn("w:t")):
            t.set(qn("xml:space"), "preserve")
        set_run_font(run, CODE_FONT, round(size * 2) / 2, TEXT)
        if k < len(lines) - 1:
            run.add_break()
    spacer = doc.add_paragraph()
    _shrink(spacer)


def render_list(doc, block: Block, theme: Theme):
    for level, marker, text in block.items:
        p = doc.add_paragraph()
        pf = p.paragraph_format
        pf.left_indent = Inches(0.3 + 0.28 * level)
        pf.first_line_indent = Inches(-0.22)
        pf.space_before = Pt(1)
        pf.space_after = Pt(2)
        pf.tab_stops.add_tab_stop(pf.left_indent)
        check = re.match(r"^\[( |x|X)\]\s+(.*)$", text)
        if check:
            glyph = "☒" if check.group(1).lower() == "x" else "☐"
            text = check.group(2)
            run = p.add_run(glyph + "\t")
            set_run_font(run, SYMBOL_FONT, theme.body_size, TEXT)
        elif marker[0].isdigit():
            run = p.add_run(marker.replace(")", ".") + "\t")
            set_run_font(run, BODY_FONT, theme.body_size, TEXT)
        else:
            run = p.add_run(_BULLETS[min(level, 3)] + "\t")
            set_run_font(run, BODY_FONT, theme.body_size, TEXT)
        add_inline(p, text, size=theme.body_size, color=TEXT)


HEADING_SIZES = {1: 15, 2: 12.5, 3: 11, 4: 10.5}


def render_heading(doc, block: Block, theme: Theme):
    level = theme.heading_map.get(block.level, 4)
    h = doc.add_heading(level=level)
    add_inline(h, block.text, size=HEADING_SIZES[level])
    return h


def render_blocks(doc, blocks: list[Block], theme: Theme):
    """Render parsed blocks. Wide tables (and the heading that introduces them) get landscape pages."""
    landscape_starts, landscape_ends = set(), set()
    for idx, b in enumerate(blocks):
        if b.kind == "table" and max(len(r) for r in b.rows) >= LANDSCAPE_MIN_COLUMNS:
            start = idx
            for back in range(idx - 1, max(idx - 4, -1), -1):
                if blocks[back].kind == "heading":
                    start = back
                    break
                if blocks[back].kind not in ("para", "quote"):
                    break
            landscape_starts.add(start)
            landscape_ends.add(idx)

    section = doc.sections[-1]
    for idx, b in enumerate(blocks):
        if idx in landscape_starts:
            section = start_section(doc, landscape=True)
        available = text_width(section)
        if b.kind == "heading":
            render_heading(doc, b, theme)
        elif b.kind == "para":
            p = doc.add_paragraph()
            add_inline(p, b.text, size=theme.body_size, color=TEXT)
            nxt = blocks[idx + 1] if idx + 1 < len(blocks) else None
            if nxt is not None and nxt.kind in ("code", "table", "math", "list") and idx + 1 not in landscape_starts:
                p.paragraph_format.keep_with_next = True
        elif b.kind == "list":
            render_list(doc, b, theme)
        elif b.kind == "table":
            render_table(doc, b, theme, available)
        elif b.kind == "code":
            render_code(doc, b, theme, available)
        elif b.kind == "math":
            p = doc.add_paragraph()
            p.alignment = WD_ALIGN_PARAGRAPH.CENTER
            p.paragraph_format.space_before = Pt(4)
            p.paragraph_format.space_after = Pt(6)
            run = p.add_run(latex_to_text(b.text))
            # A chain of \text{...} labels joined by arrows reads better in the body font.
            textual = re.fullmatch(r"(\s*(\\text\{[^}]*\}|\\(long)?rightarrow|\\to)\s*)+", b.text)
            if textual:
                set_run_font(run, BODY_FONT, theme.body_size + 0.5, TEAL, bold=True)
            else:
                set_run_font(run, MATH_FONT, theme.body_size + 0.5, TEXT)
        elif b.kind == "quote":
            p = doc.add_paragraph()
            p.paragraph_format.left_indent = Inches(0.35)
            add_inline(p, b.text, size=theme.body_size - 0.5, color=MUTED, italic=True)
        if idx in landscape_ends and any(later.kind != "hr" for later in blocks[idx + 1:]):
            section = start_section(doc, landscape=False)


def page_break(doc):
    doc.add_paragraph().add_run().add_break(WD_BREAK.PAGE)
