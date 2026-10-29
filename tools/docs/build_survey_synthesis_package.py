#!/usr/bin/env python3
"""
build_survey_synthesis_package.py

Builds the Section E (Testing and Quality Assurance — Survey Responses and Synthesis)
and F2F Adviser Consultation Briefing artifacts from:
  - docs/final-validation/SURVEY_RESPONSES_AND_SYNTHESIS_REPORT.md
  - docs/final-validation/F2F_ADVISER_CONSULTATION_BRIEFING.md
and generates the de-identified N=25 survey tabulation CSV:
  - ROUND1_SURVEY_RESPONSES_TABULATION_N25.csv

Outputs into:
  1. docs/final-validation/rendered/
  2. dist/PlayIT_IT411_Midterm_Submission/03_Testing_and_QA/
  3. dist/PlayIT_IT411_Midterm_Submission/04_Final-User_Validation/
"""

from __future__ import annotations

import csv
import shutil
import sys
from pathlib import Path

SCRIPT_DIR = Path(__file__).resolve().parent
sys.path.insert(0, str(SCRIPT_DIR))

from build_final_validation_package import (  # noqa: E402
    DIST_VAL_DIR,
    FINAL_VAL_DIR,
    RENDERED_DIR,
    REPO_ROOT,
    build_docx_from_lines,
    build_pdf_from_lines,
    register_pdf_fonts,
)

DIST_QA_DIR = REPO_ROOT / "dist" / "PlayIT_IT411_Midterm_Submission" / "03_Testing_and_QA"
DIST_ROOT_DIR = REPO_ROOT / "dist" / "PlayIT_IT411_Midterm_Submission"


def write_survey_csv(csv_path: Path) -> None:
    """Write the complete de-identified N=25 Round 1 survey dataset across all 3 cohorts."""
    rows = [
        # Header
        [
            "Cohort",
            "Respondent_ID",
            "Raw_Sheet_ID",
            "Facilitator_or_Role",
            "Instrument",
            "Item_1",
            "Item_2",
            "Item_3",
            "Item_4",
            "Item_5",
            "Item_6",
            "Item_7",
            "Item_8",
            "Item_9",
            "Item_10",
            "Item_11",
            "Item_12",
            "Summary_Metric_or_Score",
            "Qualitative_Comment_or_Notes",
        ],
        # Cohort 1: Children (n=16, Smileyometer 1-5 scale: 5=Top-Box, 4=Second-Box, 3=Neutral)
        ["Cohort 1 (Child)", "C-01", "C-1", "Karen Mel Lerit", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=5", "Q3_Char=5", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=5.00 (All Top-Box)", ""],
        ["Cohort 1 (Child)", "C-02", "C-1 (2nd)", "Leony Layaguin", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=5", "Q3_Char=5", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=5.00 (All Top-Box)", ""],
        ["Cohort 1 (Child)", "C-03", "C-2", "Joy Flores", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=5", "Q3_Char=5", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=5.00 (All Top-Box)", ""],
        ["Cohort 1 (Child)", "C-04", "C-3", "Joy Flores", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=4", "Q3_Char=5", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=4.75", "Ease rated Second-Box (4)"],
        ["Cohort 1 (Child)", "C-05", "C-4", "Joy Flores", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=5", "Q3_Char=5", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=5.00 (All Top-Box)", ""],
        ["Cohort 1 (Child)", "C-06", "C-5", "Joy Flores", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=4", "Q3_Char=4", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=4.50", "Ease & Characters rated Second-Box (4)"],
        ["Cohort 1 (Child)", "C-07", "C-6", "Joy Flores", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=4", "Q3_Char=5", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=4.75", "Ease rated Second-Box (4)"],
        ["Cohort 1 (Child)", "C-08", "C-7", "Joy Flores", "Smileyometer (Q1-Q4)", "Q1_Fun=4", "Q2_Ease=4", "Q3_Char=5", "Q4_Replay=4", "", "", "", "", "", "", "", "", "Mean=4.25", "Fun, Ease, Replay rated Second-Box (4)"],
        ["Cohort 1 (Child)", "C-09", "C-8", "Joy Flores", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=5", "Q3_Char=5", "Q4_Replay=4", "", "", "", "", "", "", "", "", "Mean=4.75", "Replay rated Second-Box (4)"],
        ["Cohort 1 (Child)", "C-10", "C-9", "Joy Flores", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=5", "Q3_Char=5", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=5.00 (All Top-Box)", ""],
        ["Cohort 1 (Child)", "C-11", "C-1 (3rd)", "Ashlee Skye Durano", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=5", "Q3_Char=5", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=5.00 (All Top-Box)", ""],
        ["Cohort 1 (Child)", "C-12", "C-10", "Joy Flores", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=3", "Q3_Char=5", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=4.50", "Ease rated Neutral (3) during Say It mic interaction"],
        ["Cohort 1 (Child)", "C-13", "C-11", "Joy Flores", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=4", "Q3_Char=5", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=4.75", "Ease rated Second-Box (4)"],
        ["Cohort 1 (Child)", "C-14", "C-12", "Joy Flores", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=5", "Q3_Char=5", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=5.00 (All Top-Box)", ""],
        ["Cohort 1 (Child)", "C-15", "C-13", "Joy Flores", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=5", "Q3_Char=5", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=5.00 (All Top-Box)", ""],
        ["Cohort 1 (Child)", "C-16", "C-14", "Joy Flores", "Smileyometer (Q1-Q4)", "Q1_Fun=5", "Q2_Ease=4", "Q3_Char=5", "Q4_Replay=5", "", "", "", "", "", "", "", "", "Mean=4.75", "Ease rated Second-Box (4)"],
        # Cohort 2: Parents / Supervising Teachers (n=5, SUS-01..10 + ACC-01..07 + FIT-01..02)
        ["Cohort 2 (Parent/Caregiver)", "P-1", "Row 1", "Parent/Guardian", "SUS + Accessibility + Fit", "SUS01=4", "SUS02=1", "SUS03=5", "SUS04=2", "SUS05=3", "SUS06=3", "SUS07=4", "SUS08=1", "SUS09=4", "SUS10=2", "ACC: 6/7 Yes (ACC07_Motor=No)", "FIT01=4, FIT02=4", "SUS=77.50", "Flagged motor navigation ease (ACC-07=No)"],
        ["Cohort 2 (Parent/Caregiver)", "P-2", "Row 2", "Parent/Guardian", "SUS + Accessibility + Fit", "SUS01=5", "SUS02=5", "SUS03=4", "SUS04=4", "SUS05=4", "SUS06=5", "SUS07=5", "SUS08=5", "SUS09=5", "SUS10=4", "ACC: 7/7 Yes", "FIT01=5, FIT02=4", "SUS=50.00", "Acquiescence bias on negative SUS items; rated Grade-Fit 5/5"],
        ["Cohort 2 (Parent/Caregiver)", "P-3", "Row 3", "Parent/Guardian", "SUS + Accessibility + Fit", "SUS01=4", "SUS02=1", "SUS03=3", "SUS04=1", "SUS05=4", "SUS06=2", "SUS07=5", "SUS08=2", "SUS09=4", "SUS10=2", "ACC: 6/7 Yes (ACC06_Hearing=No)", "FIT01=5, FIT02=5", "SUS=80.00", "Flagged hearing-difficulty options (ACC-06=No)"],
        ["Cohort 2 (Parent/Caregiver)", "P-4", "Row 4", "Parent/Guardian", "SUS + Accessibility + Fit", "SUS01=4", "SUS02=1", "SUS03=5", "SUS04=1", "SUS05=4", "SUS06=2", "SUS07=5", "SUS08=2", "SUS09=4", "SUS10=2", "ACC: 6/7 Yes (ACC06_Hearing=No)", "FIT01=5, FIT02=4", "SUS=85.00", "Flagged hearing-difficulty options (ACC-06=No)"],
        ["Cohort 2 (Parent/Caregiver)", "P-5", "Row 5", "Parent/Guardian", "SUS + Accessibility + Fit", "SUS01=5", "SUS02=3", "SUS03=5", "SUS04=1", "SUS05=5", "SUS06=1", "SUS07=5", "SUS08=3", "SUS09=4", "SUS10=2", "ACC: 7/7 Yes", "FIT01=4, FIT02=5", "SUS=85.00", "All 7 accessibility items Yes"],
        # Cohort 3: DepEd Grade 1 Reading Teachers (n=4, PED-01..12)
        ["Cohort 3 (DepEd Teacher)", "T-1", "Row 1", "DepEd Grade 1 Teacher (L. Layaguin)", "Pedagogical Checklist (PED-01..12)", "PED01=Yes", "PED02=Yes", "PED03=Yes", "PED04=Yes", "PED05=Yes", "PED06=Yes", "PED07=Yes", "PED08_Sounds=No", "PED09=Yes", "PED10=Yes", "PED11=Yes", "PED12=Yes", "11/12 Yes (91.7%)", "One area that needs improvement is the pronunciation of the letter sounds. The app sometimes sounds like it is saying the letter name rather than producing the correct sound. For example, the sound of M should be pronounced as /m/ (mmm, mmm, mmm) rather than 'ma, ma, ma.'"],
        ["Cohort 3 (DepEd Teacher)", "T-2", "Row 2", "DepEd Grade 1 Teacher (E. Bien)", "Pedagogical Checklist (PED-01..12)", "PED01=Yes", "PED02=Yes", "PED03=Yes", "PED04=Yes", "PED05=Yes", "PED06=Yes", "PED07=Yes", "PED08_Sounds=Yes", "PED09=Yes", "PED10=Yes", "PED11=Yes", "PED12=Yes", "12/12 Yes (100.0%)", "Application is appropriate for Grade 1 learners"],
        ["Cohort 3 (DepEd Teacher)", "T-3", "Row 3", "DepEd Grade 1 Teacher (Teacher Joy)", "Pedagogical Checklist (PED-01..12)", "PED01=Yes", "PED02=Yes", "PED03=Yes", "PED04=Yes", "PED05=Yes", "PED06=Yes", "PED07=Yes", "PED08_Sounds=There are not properly pronounced.", "PED09=Yes", "PED10=Yes", "PED11=Yes", "PED12=Yes", "11/12 Yes (91.7%)", "Good job on doing the app. It's a helpful tool for those beginning readers. Children enjoys it as it is digitized and interactive. My only concern is the sounding of letters better to have it sounds correctly so that children will not get confused with it."],
        ["Cohort 3 (DepEd Teacher)", "T-4", "Row 4", "DepEd Grade 1 Teacher (L. Rapsing)", "Pedagogical Checklist (PED-01..12)", "PED01=Yes", "PED02=Yes", "PED03=Yes", "PED04=Yes", "PED05=Yes", "PED06=Yes", "PED07=Yes", "PED08_Sounds=Yes", "PED09=Yes", "PED10=Yes", "PED11=Yes", "PED12=Yes", "12/12 Yes (100.0%)", "The app is appropriate for grade 1 learners and is easy to navigate."],
    ]
    csv_path.parent.mkdir(parents=True, exist_ok=True)
    with csv_path.open("w", encoding="utf-8", newline="") as f:
        writer = csv.writer(f)
        writer.writerows(rows)
    print(f"Wrote {csv_path.relative_to(REPO_ROOT)} ({csv_path.stat().st_size:,} bytes)")


def main() -> None:
    font_used = register_pdf_fonts()
    print(f"PDF font family: {font_used}")
    RENDERED_DIR.mkdir(parents=True, exist_ok=True)
    DIST_QA_DIR.mkdir(parents=True, exist_ok=True)
    DIST_VAL_DIR.mkdir(parents=True, exist_ok=True)

    # 1. Generate ROUND1_SURVEY_RESPONSES_TABULATION_N25.csv
    csv_rendered = RENDERED_DIR / "ROUND1_SURVEY_RESPONSES_TABULATION_N25.csv"
    write_survey_csv(csv_rendered)
    shutil.copyfile(csv_rendered, DIST_QA_DIR / csv_rendered.name)

    # 2. Copy Cleaned Master .xlsx (if downloaded in /tmp) and corrected Highlights.pdf/.docx into 03_Testing_and_QA
    tmp_xlsx = Path("/tmp/1SIsyr4YbdtqyEy_MYqIkOKnjnB6qVGFdwyMXCM9PO3s.xlsx")
    if tmp_xlsx.exists():
        shutil.copyfile(tmp_xlsx, DIST_QA_DIR / "Cleaned_Master_Responses_N25.xlsx")
        print(f"Copied Cleaned_Master_Responses_N25.xlsx into {DIST_QA_DIR.relative_to(REPO_ROOT)}")

    for ext in (".pdf", ".docx"):
        hl_src = REPO_ROOT / "docs" / "validation-package" / f"playIT_MVP_Validation_Highlights{ext}"
        if hl_src.exists():
            shutil.copyfile(hl_src, DIST_QA_DIR / hl_src.name)

    # 3. Render SURVEY_RESPONSES_AND_SYNTHESIS_REPORT (.docx & .pdf)
    survey_md = FINAL_VAL_DIR / "SURVEY_RESPONSES_AND_SYNTHESIS_REPORT.md"
    survey_lines = survey_md.read_text(encoding="utf-8").splitlines()
    survey_docx = RENDERED_DIR / "SURVEY_RESPONSES_AND_SYNTHESIS_REPORT.docx"
    survey_pdf = RENDERED_DIR / "SURVEY_RESPONSES_AND_SYNTHESIS_REPORT.pdf"
    build_docx_from_lines(
        survey_lines,
        survey_docx,
        "Survey Responses and Synthesis Report (Round 1 -> Refactoring -> Round 2)",
        "Survey Responses & Synthesis",
        FINAL_VAL_DIR,
    )
    build_pdf_from_lines(
        survey_lines,
        survey_pdf,
        "Survey Responses and Synthesis Report (N = 25)",
        "Section E — Testing & QA: Itemized Round 1 Survey Responses, Refactoring Traceability & Round 2 Bridge",
        use_landscape=True,
    )
    shutil.copyfile(survey_docx, DIST_QA_DIR / survey_docx.name)
    shutil.copyfile(survey_pdf, DIST_QA_DIR / survey_pdf.name)

    # 4. Render F2F_ADVISER_CONSULTATION_BRIEFING (.docx & .pdf)
    brief_md = FINAL_VAL_DIR / "F2F_ADVISER_CONSULTATION_BRIEFING.md"
    brief_lines = brief_md.read_text(encoding="utf-8").splitlines()
    brief_docx = RENDERED_DIR / "F2F_ADVISER_CONSULTATION_BRIEFING.docx"
    brief_pdf = RENDERED_DIR / "F2F_ADVISER_CONSULTATION_BRIEFING.pdf"
    build_docx_from_lines(
        brief_lines,
        brief_docx,
        "Face-to-Face Adviser Consultation Master Briefing & Live Demo Guide",
        "F2F Adviser Briefing",
        FINAL_VAL_DIR,
    )
    build_pdf_from_lines(
        brief_lines,
        brief_pdf,
        "Face-to-Face Adviser Consultation Master Briefing & Live Demo Guide",
        "Consultation Agenda: (A) Software Functionality, (B) Deployment & Repo, (E) Testing, QA & Survey Synthesis",
        use_landscape=False,
    )
    shutil.copyfile(brief_docx, DIST_VAL_DIR / brief_docx.name)
    shutil.copyfile(brief_pdf, DIST_VAL_DIR / brief_pdf.name)
    shutil.copyfile(brief_pdf, DIST_ROOT_DIR / brief_pdf.name)
    print("=== Survey Synthesis Package & F2F Briefing complete ===")


if __name__ == "__main__":
    main()
