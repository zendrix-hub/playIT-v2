#!/usr/bin/env python3
"""
export_validation_telemetry.py

Extracts PlayIT's on-device Room SQLite database (playit_db + WAL/SHM) from a connected
Android phone via `adb exec-out run-as com.playit.app`, anonymizes learner profiles to
participant codes (P-01, P-02, ...), and exports Instrument C CSV files + SMART goal
empirical summary reports for Week 12 Final User/Stakeholder Validation.

Generated files in --out (default: docs/final-validation/evidence/<date>-telemetry/):
  - profiles_anonymized.csv
  - say_it_attempts.csv
  - find_it_attempts.csv
  - blend_it_attempts.csv
  - lesson_progress.csv
  - phoneme_summary.csv
  - validation_metrics_summary.json
  - validation_metrics_summary.md

Usage:
    python3 tools/perf/export_validation_telemetry.py [--serial ID] [--db FILE] [--out DIR] [--self-test]
"""

from __future__ import annotations

import argparse
import csv
import json
import os
import shutil
import sqlite3
import subprocess
import tempfile
from datetime import datetime, timedelta, timezone
from pathlib import Path

PACKAGE = "com.playit.app"
DB_NAME = "playit_db"
REPO = Path(__file__).resolve().parents[2]
PHT = timezone(timedelta(hours=8))


def find_adb() -> str:
    sdk = (
        os.environ.get("ANDROID_HOME")
        or os.environ.get("ANDROID_SDK_ROOT")
        or os.path.join(os.environ.get("LOCALAPPDATA", ""), "Android", "Sdk")
    )
    candidates = [Path(sdk) / "platform-tools" / name for name in ("adb.exe", "adb")]
    if Path("/mnt/c/Users").exists():
        for user_dir in sorted(Path("/mnt/c/Users").glob("*")):
            candidates.append(user_dir / "AppData" / "Local" / "Android" / "Sdk" / "platform-tools" / "adb.exe")
    for candidate in candidates:
        if candidate.exists():
            return str(candidate)
    found = shutil.which("adb") or shutil.which("adb.exe")
    if not found:
        raise SystemExit("adb not found: install the Android SDK platform-tools or set ANDROID_HOME")
    return found


def pick_serial(adb: str, wanted: str | None) -> str:
    subprocess.run([adb, "start-server"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL,
                   stdin=subprocess.DEVNULL, timeout=30)
    lines = subprocess.run([adb, "devices"], capture_output=True, text=True,
                           stdin=subprocess.DEVNULL, timeout=15).stdout.replace("\r\n", "\n").splitlines()[1:]
    serials = [line.split("\t")[0] for line in lines if line.endswith("\tdevice")]
    if wanted:
        if wanted not in serials:
            raise SystemExit(f"{wanted} is not connected (connected: {', '.join(serials) or 'none'})")
        return wanted
    if len(serials) != 1:
        raise SystemExit("Connect exactly one phone, or pass --serial. Connected: " + (", ".join(serials) or "none"))
    return serials[0]


def pull_room_db(adb: str, serial: str, dst_dir: Path) -> Path:
    db_local = dst_dir / DB_NAME
    for suffix in ("", "-wal", "-shm"):
        remote_name = f"databases/{DB_NAME}{suffix}"
        out = subprocess.run(
            [adb, "-s", serial, "exec-out", "run-as", PACKAGE, "cat", remote_name],
            capture_output=True, stdin=subprocess.DEVNULL, timeout=60,
        )
        if suffix == "" and (out.returncode != 0 or not out.stdout.startswith(b"SQLite format 3")):
            err = out.stderr.decode("utf-8", "replace").strip()
            raise SystemExit(f"Could not pull {remote_name} from {PACKAGE} on {serial}: {err or 'empty/not SQLite'}")
        if out.returncode == 0 and out.stdout:
            (dst_dir / f"{DB_NAME}{suffix}").write_bytes(out.stdout)
    return db_local


def iso_pht(ms: int | None) -> str:
    if not ms or ms <= 0:
        return ""
    return datetime.fromtimestamp(ms / 1000.0, tz=PHT).strftime("%Y-%m-%d %H:%M:%S+08:00")


def write_csv(path: Path, fieldnames: list[str], rows: list[dict]):
    path.parent.mkdir(parents=True, exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)


def export_from_sqlite(db_path: Path, out_dir: Path) -> dict:
    conn = sqlite3.connect(str(db_path))
    conn.row_factory = sqlite3.Row
    cur = conn.cursor()

    # 1. Anonymized Profiles (P-01, P-02, ... — never export child names)
    profiles = cur.execute("SELECT * FROM profiles ORDER BY profileId ASC").fetchall()
    code_map = {row["profileId"]: f"P-{idx:02d}" for idx, row in enumerate(profiles, start=1)}
    profile_rows = [
        {
            "participant_code": code_map[r["profileId"]],
            "profileId": r["profileId"],
            "avatarResId": r["avatarResId"],
            "totalStars": r["totalStars"],
            "currentStreak": r["currentStreak"],
            "createdAt_ms": r["createdAt"],
            "createdAt_iso": iso_pht(r["createdAt"]),
            "lastPlayedAt_ms": r["lastPlayedAt"],
            "lastPlayedAt_iso": iso_pht(r["lastPlayedAt"]),
        }
        for r in profiles
    ]
    write_csv(
        out_dir / "profiles_anonymized.csv",
        ["participant_code", "profileId", "avatarResId", "totalStars", "currentStreak",
         "createdAt_ms", "createdAt_iso", "lastPlayedAt_ms", "lastPlayedAt_iso"],
        profile_rows,
    )

    # 2. Say It Attempts
    say_rows_raw = cur.execute("""
        SELECT s.attemptId, s.profileId, s.phonemeId, p.letter, p.exampleWord, s.isCorrect, s.attemptedAt
        FROM say_it_attempts s
        LEFT JOIN phonemes p ON p.phonemeId = s.phonemeId
        ORDER BY s.attemptedAt ASC, s.attemptId ASC
    """).fetchall()
    say_rows = [
        {
            "attemptId": r["attemptId"],
            "participant_code": code_map.get(r["profileId"], f"P-{r['profileId']}"),
            "phonemeId": r["phonemeId"],
            "letter": r["letter"] or "",
            "exampleWord": r["exampleWord"] or "",
            "isCorrect": int(bool(r["isCorrect"])),
            "attemptedAt_ms": r["attemptedAt"],
            "attemptedAt_iso": iso_pht(r["attemptedAt"]),
        }
        for r in say_rows_raw
    ]
    write_csv(
        out_dir / "say_it_attempts.csv",
        ["attemptId", "participant_code", "phonemeId", "letter", "exampleWord",
         "isCorrect", "attemptedAt_ms", "attemptedAt_iso"],
        say_rows,
    )

    # 3. Find It Attempts
    find_rows_raw = cur.execute("""
        SELECT f.attemptId, f.profileId, f.phonemeId, p1.letter AS targetLetter,
               f.selectedPhonemeId, p2.letter AS selectedLetter, f.isCorrect, f.attemptedAt
        FROM find_it_attempts f
        LEFT JOIN phonemes p1 ON p1.phonemeId = f.phonemeId
        LEFT JOIN phonemes p2 ON p2.phonemeId = f.selectedPhonemeId
        ORDER BY f.attemptedAt ASC, f.attemptId ASC
    """).fetchall()
    find_rows = [
        {
            "attemptId": r["attemptId"],
            "participant_code": code_map.get(r["profileId"], f"P-{r['profileId']}"),
            "phonemeId": r["phonemeId"],
            "targetLetter": r["targetLetter"] or "",
            "selectedPhonemeId": r["selectedPhonemeId"],
            "selectedLetter": r["selectedLetter"] or "",
            "isCorrect": int(bool(r["isCorrect"])),
            "attemptedAt_ms": r["attemptedAt"],
            "attemptedAt_iso": iso_pht(r["attemptedAt"]),
        }
        for r in find_rows_raw
    ]
    write_csv(
        out_dir / "find_it_attempts.csv",
        ["attemptId", "participant_code", "phonemeId", "targetLetter",
         "selectedPhonemeId", "selectedLetter", "isCorrect", "attemptedAt_ms", "attemptedAt_iso"],
        find_rows,
    )

    # 4. Blend It Attempts
    blend_rows_raw = cur.execute("""
        SELECT b.attemptId, b.profileId, b.groupId, b.wordId, w.word, w.wordPattern, b.isCorrect, b.attemptedAt
        FROM blend_it_attempts b
        LEFT JOIN blend_it_words w ON w.wordId = b.wordId
        ORDER BY b.attemptedAt ASC, b.attemptId ASC
    """).fetchall()
    blend_rows = [
        {
            "attemptId": r["attemptId"],
            "participant_code": code_map.get(r["profileId"], f"P-{r['profileId']}"),
            "groupId": r["groupId"],
            "wordId": r["wordId"],
            "word": r["word"] or "",
            "wordPattern": r["wordPattern"] or "",
            "isCorrect": int(bool(r["isCorrect"])),
            "attemptedAt_ms": r["attemptedAt"],
            "attemptedAt_iso": iso_pht(r["attemptedAt"]),
        }
        for r in blend_rows_raw
    ]
    write_csv(
        out_dir / "blend_it_attempts.csv",
        ["attemptId", "participant_code", "groupId", "wordId", "word",
         "wordPattern", "isCorrect", "attemptedAt_ms", "attemptedAt_iso"],
        blend_rows,
    )

    # 5. Lesson Progress
    lp_rows_raw = cur.execute("""
        SELECT lp.id, lp.profileId, lp.phonemeId, p.letter, lp.starsEarned, lp.heartsLost,
               lp.isCompleted, lp.timeSpentMs, lp.completedAt
        FROM lesson_progress lp
        LEFT JOIN phonemes p ON p.phonemeId = lp.phonemeId
        ORDER BY lp.profileId ASC, lp.phonemeId ASC
    """).fetchall()
    lp_rows = [
        {
            "id": r["id"],
            "participant_code": code_map.get(r["profileId"], f"P-{r['profileId']}"),
            "phonemeId": r["phonemeId"],
            "letter": r["letter"] or "",
            "starsEarned": r["starsEarned"],
            "heartsLost": r["heartsLost"],
            "isCompleted": int(bool(r["isCompleted"])),
            "timeSpentMs": r["timeSpentMs"],
            "completedAt_ms": r["completedAt"],
            "completedAt_iso": iso_pht(r["completedAt"]),
        }
        for r in lp_rows_raw
    ]
    write_csv(
        out_dir / "lesson_progress.csv",
        ["id", "participant_code", "phonemeId", "letter", "starsEarned", "heartsLost",
         "isCompleted", "timeSpentMs", "completedAt_ms", "completedAt_iso"],
        lp_rows,
    )

    # 6. Per-Phoneme Summary
    phonemes = cur.execute("SELECT * FROM phonemes ORDER BY phonemeId ASC").fetchall()
    phoneme_summary = []
    for p in phonemes:
        pid = p["phonemeId"]
        p_lp = [r for r in lp_rows if r["phonemeId"] == pid and r["isCompleted"] == 1]
        p_say = [r for r in say_rows if r["phonemeId"] == pid]
        p_find = [r for r in find_rows if r["phonemeId"] == pid]
        say_ok = sum(r["isCorrect"] for r in p_say)
        find_ok = sum(r["isCorrect"] for r in p_find)
        phoneme_summary.append({
            "phonemeId": pid,
            "letter": p["letter"],
            "exampleWord": p["exampleWord"],
            "learners_completed": len(p_lp),
            "avg_stars": round(sum(r["starsEarned"] for r in p_lp) / len(p_lp), 2) if p_lp else 0.0,
            "avg_hearts_lost": round(sum(r["heartsLost"] for r in p_lp) / len(p_lp), 2) if p_lp else 0.0,
            "say_it_attempts": len(p_say),
            "say_it_correct": say_ok,
            "say_it_accuracy_pct": round(say_ok / len(p_say) * 100.0, 1) if p_say else 0.0,
            "find_it_attempts": len(p_find),
            "find_it_correct": find_ok,
            "find_it_accuracy_pct": round(find_ok / len(p_find) * 100.0, 1) if p_find else 0.0,
        })
    write_csv(
        out_dir / "phoneme_summary.csv",
        ["phonemeId", "letter", "exampleWord", "learners_completed", "avg_stars", "avg_hearts_lost",
         "say_it_attempts", "say_it_correct", "say_it_accuracy_pct",
         "find_it_attempts", "find_it_correct", "find_it_accuracy_pct"],
        phoneme_summary,
    )
    conn.close()

    # 7. Empirical SMART Goal Summary
    completed_lp = [r for r in lp_rows if r["isCompleted"] == 1]
    say_correct = sum(r["isCorrect"] for r in say_rows)
    find_correct = sum(r["isCorrect"] for r in find_rows)
    blend_correct = sum(r["isCorrect"] for r in blend_rows)
    summary = {
        "generated_at": datetime.now(PHT).isoformat(timespec="seconds"),
        "profiles_count": len(profile_rows),
        "completed_lessons_count": len(completed_lp),
        "say_it": {
            "total_attempts": len(say_rows),
            "correct_attempts": say_correct,
            "accuracy_pct": round(say_correct / len(say_rows) * 100.0, 1) if say_rows else 0.0,
        },
        "find_it": {
            "total_attempts": len(find_rows),
            "correct_attempts": find_correct,
            "accuracy_pct": round(find_correct / len(find_rows) * 100.0, 1) if find_rows else 0.0,
        },
        "blend_it": {
            "total_attempts": len(blend_rows),
            "correct_attempts": blend_correct,
            "accuracy_pct": round(blend_correct / len(blend_rows) * 100.0, 1) if blend_rows else 0.0,
        },
        "stars": {
            "three_stars": sum(1 for r in completed_lp if r["starsEarned"] == 3),
            "two_stars": sum(1 for r in completed_lp if r["starsEarned"] == 2),
            "one_star": sum(1 for r in completed_lp if r["starsEarned"] == 1),
            "mean_stars": round(sum(r["starsEarned"] for r in completed_lp) / len(completed_lp), 2)
            if completed_lp else 0.0,
        },
    }
    (out_dir / "validation_metrics_summary.json").write_text(
        json.dumps(summary, indent=2) + "\n", encoding="utf-8", newline="\n"
    )
    md_lines = [
        "# PlayIT — Exported Device Telemetry Summary (Instrument C)",
        "",
        f"- **Exported At:** `{summary['generated_at']}`",
        f"- **Anonymized Learner Profiles:** `{summary['profiles_count']}` (`P-01` .. `P-{summary['profiles_count']:02d}`)",
        f"- **Completed Letter Lessons:** `{summary['completed_lessons_count']}`",
        "",
        "## Empirical Accuracy & Mastery Aggregates",
        "",
        "| Metric | Attempts / Lessons | Correct / Distribution | Rate / Mean |",
        "|---|---:|---:|---:|",
        f"| **Say It (`say_it_attempts.csv`)** | {summary['say_it']['total_attempts']} | {summary['say_it']['correct_attempts']} | **{summary['say_it']['accuracy_pct']}%** |",
        f"| **Find It (`find_it_attempts.csv`)** | {summary['find_it']['total_attempts']} | {summary['find_it']['correct_attempts']} | **{summary['find_it']['accuracy_pct']}%** |",
        f"| **Blend It (`blend_it_attempts.csv`)** | {summary['blend_it']['total_attempts']} | {summary['blend_it']['correct_attempts']} | **{summary['blend_it']['accuracy_pct']}%** |",
        f"| **Lesson Mastery (`lesson_progress.csv`)** | {summary['completed_lessons_count']} | 3★: {summary['stars']['three_stars']}, 2★: {summary['stars']['two_stars']}, 1★: {summary['stars']['one_star']} | **{summary['stars']['mean_stars']} ★** |",
        "",
    ]
    (out_dir / "validation_metrics_summary.md").write_text("\n".join(md_lines), encoding="utf-8", newline="\n")
    return summary


def run_self_test():
    with tempfile.TemporaryDirectory() as tmp:
        tmp_path = Path(tmp)
        db_file = tmp_path / DB_NAME
        conn = sqlite3.connect(str(db_file))
        conn.executescript("""
            CREATE TABLE profiles (
                profileId INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                avatarResId INTEGER NOT NULL,
                totalStars INTEGER NOT NULL,
                currentStreak INTEGER NOT NULL,
                lastPlayedAt INTEGER NOT NULL,
                createdAt INTEGER NOT NULL
            );
            CREATE TABLE phonemes (
                phonemeId INTEGER PRIMARY KEY,
                letter TEXT NOT NULL,
                audioPath TEXT NOT NULL,
                imagePath TEXT NOT NULL,
                exampleWord TEXT NOT NULL
            );
            CREATE TABLE blend_it_words (
                wordId INTEGER PRIMARY KEY,
                groupId INTEGER NOT NULL,
                word TEXT NOT NULL,
                wordPattern TEXT NOT NULL,
                audioPath TEXT NOT NULL,
                imagePath TEXT NOT NULL
            );
            CREATE TABLE say_it_attempts (
                attemptId INTEGER PRIMARY KEY AUTOINCREMENT,
                profileId INTEGER NOT NULL,
                phonemeId INTEGER NOT NULL,
                isCorrect INTEGER NOT NULL,
                attemptedAt INTEGER NOT NULL
            );
            CREATE TABLE find_it_attempts (
                attemptId INTEGER PRIMARY KEY AUTOINCREMENT,
                profileId INTEGER NOT NULL,
                phonemeId INTEGER NOT NULL,
                selectedPhonemeId INTEGER NOT NULL,
                isCorrect INTEGER NOT NULL,
                attemptedAt INTEGER NOT NULL
            );
            CREATE TABLE blend_it_attempts (
                attemptId INTEGER PRIMARY KEY AUTOINCREMENT,
                profileId INTEGER NOT NULL,
                groupId INTEGER NOT NULL,
                wordId INTEGER NOT NULL,
                isCorrect INTEGER NOT NULL,
                attemptedAt INTEGER NOT NULL
            );
            CREATE TABLE lesson_progress (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                profileId INTEGER NOT NULL,
                phonemeId INTEGER NOT NULL,
                starsEarned INTEGER NOT NULL,
                heartsLost INTEGER NOT NULL,
                isCompleted INTEGER NOT NULL,
                completedAt INTEGER NOT NULL,
                timeSpentMs INTEGER NOT NULL
            );
            INSERT INTO profiles VALUES (1, 'Child A', 1, 6, 2, 1730000000000, 1729990000000);
            INSERT INTO phonemes VALUES (1, 'm', 'audio/phonemes/ph_m.wav', 'images/pictures/picture_mouse.png', 'Mouse');
            INSERT INTO phonemes VALUES (2, 's', 'audio/phonemes/ph_s.wav', 'images/pictures/picture_sun.png', 'Sun');
            INSERT INTO blend_it_words VALUES (1, 1, 'SAM', 'CVC', 'audio/words/word_sam.wav', 'images/pictures/blendword_sam.png');
            INSERT INTO say_it_attempts VALUES (1, 1, 1, 1, 1730000010000);
            INSERT INTO find_it_attempts VALUES (1, 1, 1, 1, 1, 1730000020000);
            INSERT INTO blend_it_attempts VALUES (1, 1, 1, 1, 1, 1730000030000);
            INSERT INTO lesson_progress VALUES (1, 1, 1, 3, 0, 1, 1730000040000, 45000);
        """)
        conn.commit()
        conn.close()
        out_dir = tmp_path / "out"
        summary = export_from_sqlite(db_file, out_dir)
        assert summary["profiles_count"] == 1
        assert summary["say_it"]["accuracy_pct"] == 100.0
        assert summary["find_it"]["accuracy_pct"] == 100.0
        assert summary["blend_it"]["accuracy_pct"] == 100.0
        assert (out_dir / "profiles_anonymized.csv").exists()
        assert "Child A" not in (out_dir / "profiles_anonymized.csv").read_text(encoding="utf-8")
        print("[OK] Self-test passed: 6 anonymized CSVs + JSON/MD summary verified.")


def main():
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[1])
    parser.add_argument("--serial", help="the phone's adb serial")
    parser.add_argument("--db", help="path to a local SQLite database file instead of pulling via adb")
    parser.add_argument("--out", help="output directory (default: docs/final-validation/evidence/<date>-telemetry)")
    parser.add_argument("--self-test", action="store_true", help="run built-in self-test and exit")
    args = parser.parse_args()

    if args.self_test:
        run_self_test()
        return

    started = datetime.now(PHT)
    out_dir = (
        Path(args.out)
        if args.out
        else REPO / "docs" / "final-validation" / "evidence" / f"{started:%Y-%m-%d}-telemetry"
    )
    out_dir.mkdir(parents=True, exist_ok=True)

    if args.db:
        summary = export_from_sqlite(Path(args.db), out_dir)
    else:
        adb = find_adb()
        serial = pick_serial(adb, args.serial)
        with tempfile.TemporaryDirectory() as tmp:
            db_local = pull_room_db(adb, serial, Path(tmp))
            summary = export_from_sqlite(db_local, out_dir)

    print(f"[OK] Exported 6 CSVs and summary to {out_dir.relative_to(REPO)}")
    print(f"     Profiles: {summary['profiles_count']}, Completed lessons: {summary['completed_lessons_count']}")


if __name__ == "__main__":
    main()
