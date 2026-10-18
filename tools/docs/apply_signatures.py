#!/usr/bin/env python3
"""
tools/docs/apply_signatures.py

Applies the verified transparent signature of Team Lead Zendrix Riva to:
1. playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf (Page 23, Final Declaration)
2. playIT_STD_v1.0.pdf (Page 36, Section 8.4 Sign-off)
3. ACCEPTANCE_TESTING_AND_SIGNOFF_REPORT.pdf (Page 3, Section 5 Sign-off)

Updates both docs/submission/ and dist/PlayIT_IT411_Midterm_Submission/.
"""

import sys
from pathlib import Path
import fitz  # PyMuPDF

REPO = Path(__file__).resolve().parent.parent.parent


def already_signed(page, rect):
    """True if an image already overlaps rect: running the script twice must not stamp a second signature."""
    return any(fitz.Rect(info["bbox"]).intersects(rect) for info in page.get_image_info())


def has_transparent_signature(page, rect):
    """True if an image with an alpha mask (the transparent signature) already overlaps rect."""
    masked = {img[0] for img in page.get_images(full=True) if img[1]}
    return any(info["xref"] in masked and fitz.Rect(info["bbox"]).intersects(rect)
               for info in page.get_image_info(xrefs=True))
SIG_PNG = REPO / "tools" / "docs" / "signature_zendrix_transparent.png"
SUBMISSION_DIR = REPO / "dist" / "PlayIT_IT411_Midterm_Submission"
DOCS_SUBMISSION = REPO / "docs" / "submission"


def stamp_mvp_form(pdf_path: Path):
    if not pdf_path.exists():
        print(f"Skipping {pdf_path} (does not exist)")
        return
    doc = fitz.open(pdf_path)
    page = doc[-1]  # Page 23
    sig_rect = fitz.Rect(205, 432, 265, 490)
    # Each re-stamp left a 1x1 placeholder and a new copy of the signature (the file grew 4.66 -> 4.98 MB)
    if has_transparent_signature(page, sig_rect):
        print(f"[SKIP] Already signed: {pdf_path}")
        doc.close()
        return

    # Clean existing images on the last page if any (e.g. previous opaque jpeg)
    img_list = page.get_images()
    if img_list:
        for img in img_list:
            xref = img[0]
            page.delete_image(xref)

    # Insert transparent signature centered on the right half of the underline
    # Text 'Team Lead: Zendrix Riva' and 'Date: October 14, 2026' are on the left (< x=198).
    # Underline spans x=130..300.
    page.insert_image(sig_rect, filename=str(SIG_PNG))

    temp_path = pdf_path.with_suffix(".tmp.pdf")
    doc.save(temp_path, deflate=True)
    doc.close()
    temp_path.replace(pdf_path)
    print(f"[OK] Stamped MVP form: {pdf_path}")


def stamp_std(pdf_path: Path):
    if not pdf_path.exists():
        print(f"Skipping {pdf_path} (does not exist)")
        return
    doc = fitz.open(pdf_path)
    # Find Section 8.4 Sign-off page (around page 36)
    target_page = None
    for page in doc:
        if "8.4 Sign-Off" in page.get_text() and "Reviewed by (Team Lead)" in page.get_text():
            target_page = page
            break
    if target_page is None:
        print(f"Error: Could not find Section 8.4 Sign-Off in {pdf_path}")
        return

    # Underline is around x=350..444, y=568..578; keep y within 563..577 to avoid row above (y <= 560.4)
    sig_rect = fitz.Rect(380, 563, 408, 577)
    if already_signed(target_page, sig_rect):
        print(f"[SKIP] Already signed: {pdf_path}")
        doc.close()
        return
    target_page.insert_image(sig_rect, filename=str(SIG_PNG))

    temp_path = pdf_path.with_suffix(".tmp.pdf")
    doc.save(temp_path, deflate=True)
    doc.close()
    temp_path.replace(pdf_path)
    print(f"[OK] Stamped STD: {pdf_path}")


def stamp_acceptance_report(pdf_path: Path):
    if not pdf_path.exists():
        print(f"Skipping {pdf_path} (does not exist)")
        return
    doc = fitz.open(pdf_path)
    target_page = None
    for page in doc:
        txt = page.get_text()
        if "5. Sign-Off" in txt and "Riva, Zendrix" in txt:
            target_page = page
            break
    if target_page is None:
        print(f"Error: Could not find Section 5 Sign-Off in {pdf_path}")
        return

    # Table cell for Team Lead: x in 331.2..475.2, y in 110..140
    sig_rect = fitz.Rect(365, 112, 395, 138)
    if already_signed(target_page, sig_rect):
        print(f"[SKIP] Already signed: {pdf_path}")
        doc.close()
        return
    target_page.insert_image(sig_rect, filename=str(SIG_PNG))

    # Insert date in the Date column
    target_page.insert_text(fitz.Point(495, 126), "October 18, 2026", fontsize=9, fontname="helv")

    temp_path = pdf_path.with_suffix(".tmp.pdf")
    doc.save(temp_path, deflate=True)
    doc.close()
    temp_path.replace(pdf_path)
    print(f"[OK] Stamped Acceptance Report: {pdf_path}")


def main():
    if not SIG_PNG.exists():
        sys.exit(f"Signature PNG not found at {SIG_PNG}")

    # 1. MVP Validation Form
    stamp_mvp_form(DOCS_SUBMISSION / "playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf")
    stamp_mvp_form(SUBMISSION_DIR / "02_Updated_Documentation" / "playIT_MVP_Validation_Findings_and_Refactoring_Priorities.pdf")

    # 2. STD v1.0
    stamp_std(DOCS_SUBMISSION / "playIT_STD_v1.0.pdf")
    stamp_std(SUBMISSION_DIR / "02_Updated_Documentation" / "playIT_STD_v1.0.pdf")

    # 3. Acceptance Sign-off Report
    stamp_acceptance_report(SUBMISSION_DIR / "03_Testing_and_QA" / "ACCEPTANCE_TESTING_AND_SIGNOFF_REPORT.pdf")


if __name__ == "__main__":
    main()
