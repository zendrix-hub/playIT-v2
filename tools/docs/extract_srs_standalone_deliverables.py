#!/usr/bin/env python3
"""Generate standalone PDF deliverables for CIT-U IT411 submission portal.

Deliverables:
1. docs/submission/playIT_Requirements_Inventory.pdf
   - CIT-U Cover page + Section 3 (Specific Requirements FR-01 to FR-14, NFR-AUD to NFR-SEC)
   - Extracted from docs/submission/playIT_SRS_v3.0.pdf (pages 9-15)
2. docs/submission/playIT_Traceability_Matrix.pdf
   - CIT-U Cover page + Section 4 (Requirements Traceability Matrix v3.0, §4.1 Test Coverage Table, §5 Sign-Off & Verification)
   - Extracted from docs/submission/playIT_SRS_v3.0.pdf (pages 16-20)
"""

from __future__ import annotations

import io
from pathlib import Path
from pypdf import PdfReader, PdfWriter
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_CENTER, TA_LEFT

REPO_DIR = Path(__file__).resolve().parents[2]
SUBMISSION_DIR = REPO_DIR / "docs" / "submission"
SRS_PDF_PATH = SUBMISSION_DIR / "playIT_SRS_v3.0.pdf"

TEAL = colors.HexColor("#1F3A3D")
PLUM = colors.HexColor("#6B4E71")
TEXT = colors.HexColor("#2D3748")
MUTED = colors.HexColor("#718096")
BORDER = colors.HexColor("#B8C2CC")
BG_HEADER = colors.HexColor("#F3F6F8")


def generate_cover_pdf(
    title: str,
    subtitle: str,
    document_role: str,
    extracted_sections: str,
) -> io.BytesIO:
    """Generate an institutional CIT-U CCS cover page matching SRS v3.0 format."""
    buffer = io.BytesIO()
    doc = SimpleDocTemplate(
        buffer,
        pagesize=letter,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54,
    )

    styles = getSampleStyleSheet()

    inst_style = ParagraphStyle(
        "InstHeader",
        parent=styles["Normal"],
        fontName="Helvetica-Bold",
        fontSize=12,
        leading=16,
        textColor=TEAL,
        alignment=TA_CENTER,
        spaceAfter=2,
    )
    college_style = ParagraphStyle(
        "CollegeHeader",
        parent=styles["Normal"],
        fontName="Helvetica",
        fontSize=10.5,
        leading=14,
        textColor=TEXT,
        alignment=TA_CENTER,
        spaceAfter=2,
    )
    degree_style = ParagraphStyle(
        "DegreeHeader",
        parent=styles["Normal"],
        fontName="Helvetica",
        fontSize=9.5,
        leading=13,
        textColor=MUTED,
        alignment=TA_CENTER,
        spaceAfter=15,
    )

    doc_badge_style = ParagraphStyle(
        "DocBadge",
        parent=styles["Normal"],
        fontName="Helvetica-Bold",
        fontSize=9,
        leading=12,
        textColor=PLUM,
        alignment=TA_CENTER,
        spaceAfter=15,
    )

    title_style = ParagraphStyle(
        "CoverTitle",
        parent=styles["Heading1"],
        fontName="Helvetica-Bold",
        fontSize=20,
        leading=26,
        textColor=TEAL,
        alignment=TA_CENTER,
        spaceAfter=8,
    )
    subtitle_style = ParagraphStyle(
        "CoverSubtitle",
        parent=styles["Normal"],
        fontName="Helvetica",
        fontSize=11,
        leading=15,
        textColor=PLUM,
        alignment=TA_CENTER,
        spaceAfter=20,
    )

    cell_label_style = ParagraphStyle(
        "CellLabel",
        parent=styles["Normal"],
        fontName="Helvetica-Bold",
        fontSize=8.5,
        leading=11.5,
        textColor=TEAL,
        alignment=TA_LEFT,
    )
    cell_value_style = ParagraphStyle(
        "CellValue",
        parent=styles["Normal"],
        fontName="Helvetica",
        fontSize=8.5,
        leading=11.5,
        textColor=TEXT,
        alignment=TA_LEFT,
    )

    story = [
        Paragraph("CEBU INSTITUTE OF TECHNOLOGY – UNIVERSITY", inst_style),
        Paragraph("College of Computer Studies", college_style),
        Paragraph("Bachelor of Science in Information Technology", degree_style),
        Spacer(1, 20),
        Paragraph("IT411 CAPSTONE 2 DELIVERABLE — STANDALONE EXTRACTION", doc_badge_style),
        Spacer(1, 15),
        Paragraph(title, title_style),
        Paragraph(subtitle, subtitle_style),
        Spacer(1, 35),
    ]

    meta_rows = [
        (
            Paragraph("Course", cell_label_style),
            Paragraph("IT411 (Section G7) — Capstone & Research 2 | Semester 1, AY 2026–2027", cell_value_style),
        ),
        (
            Paragraph("Document Type", cell_label_style),
            Paragraph(document_role, cell_value_style),
        ),
        (
            Paragraph("Master Source", cell_label_style),
            Paragraph("PlayIT Software Requirements Specification (SRS v3.0, revision 3.8)", cell_value_style),
        ),
        (
            Paragraph("Extracted Scope", cell_label_style),
            Paragraph(extracted_sections, cell_value_style),
        ),
        (
            Paragraph("Publication Date", cell_label_style),
            Paragraph("September 26, 2026 (Revised October 14, 2026)", cell_value_style),
        ),
        (
            Paragraph("Document Status", cell_label_style),
            Paragraph("Submitted for Adviser Review and Evaluation", cell_value_style),
        ),
        (
            Paragraph("Prepared by", cell_label_style),
            Paragraph(
                "Group 56 — PlayIT Capstone Team: Riva, Zendrix (Team Lead); Palis, John James; Miel, Kaysean; Durano, Ashlee Skye; Bien, Erik Samuel",
                cell_value_style,
            ),
        ),
        (
            Paragraph("Adviser", cell_label_style),
            Paragraph("Mr. Joemarie C. Amparo", cell_value_style),
        ),
    ]

    table = Table(meta_rows, colWidths=[120, 384])
    table.setStyle(
        TableStyle(
            [
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("TOPPADDING", (0, 0), (-1, -1), 4),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
                ("LEFTPADDING", (0, 0), (-1, -1), 6),
                ("RIGHTPADDING", (0, 0), (-1, -1), 6),
                ("LINEBELOW", (0, 0), (-1, -1), 0.5, colors.HexColor("#E2E8F0")),
                ("BACKGROUND", (0, 0), (0, -1), colors.HexColor("#F8FAFC")),
            ]
        )
    )

    story.append(table)
    doc.build(story)
    buffer.seek(0)
    return buffer


def build_requirements_inventory():
    """Extract Section 3 (Specific Requirements) from SRS v3.0 and prepend cover page."""
    out_path = SUBMISSION_DIR / "playIT_Requirements_Inventory.pdf"
    print(f"Generating {out_path.name}...")

    cover_buf = generate_cover_pdf(
        title="PlayIT — Software Requirements Inventory",
        subtitle="Complete Inventory of Specific Requirements (FR-01 to FR-14 & NFR-AUD-01 to NFR-SEC-01)",
        document_role="Standalone Deliverable: Requirements Inventory",
        extracted_sections="Section 3: Specific Requirements (Pages 9–15 of SRS v3.0)",
    )

    cover_reader = PdfReader(cover_buf)
    srs_reader = PdfReader(str(SRS_PDF_PATH))

    writer = PdfWriter()
    # Add cover page
    writer.add_page(cover_reader.pages[0])

    # Add Pages 9 to 15 (0-indexed 8 to 14 inclusive)
    for p_idx in range(8, 15):
        writer.add_page(srs_reader.pages[p_idx])

    with open(out_path, "wb") as f:
        writer.write(f)

    print(f"  [OK] Saved {out_path.name} ({len(writer.pages)} pages: 1 cover + 7 content)")


def build_traceability_matrix():
    """Extract Section 4 & 5 from SRS v3.0 and prepend cover page."""
    out_path = SUBMISSION_DIR / "playIT_Traceability_Matrix.pdf"
    print(f"Generating {out_path.name}...")

    cover_buf = generate_cover_pdf(
        title="PlayIT — Requirements Traceability Matrix (RTM v3.0)",
        subtitle="Bidirectional Traceability Matrix (Findings F-01 to F-16), Test Coverage Table (§4.1), and Verification Protocol (§5)",
        document_role="Standalone Deliverable: Traceability Matrix",
        extracted_sections="Section 4: Requirements Traceability Matrix & Section 5: Verification Protocol (Pages 16–20 of SRS v3.0)",
    )

    cover_reader = PdfReader(cover_buf)
    srs_reader = PdfReader(str(SRS_PDF_PATH))

    writer = PdfWriter()
    # Add cover page
    writer.add_page(cover_reader.pages[0])

    # Add Pages 16 to 20 (0-indexed 15 to 19 inclusive)
    for p_idx in range(15, 20):
        writer.add_page(srs_reader.pages[p_idx])

    with open(out_path, "wb") as f:
        writer.write(f)

    print(f"  [OK] Saved {out_path.name} ({len(writer.pages)} pages: 1 cover + 5 content)")


def main():
    if not SRS_PDF_PATH.exists():
        raise FileNotFoundError(f"SRS master PDF not found: {SRS_PDF_PATH}")

    build_requirements_inventory()
    build_traceability_matrix()
    print("\nAll standalone deliverables successfully generated in docs/submission/!")


if __name__ == "__main__":
    main()
