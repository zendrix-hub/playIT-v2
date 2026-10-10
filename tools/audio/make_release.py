"""Build an audio release: copy user-approved clips from review batches and write manifest.json.

A release is the only way a clip reaches the app (AGENTS.md, AGY_RUNBOOK.md "Audio gate"). This tool
copies each clip unchanged, records its SHA-256, and refuses any clip whose review row in the batch's
`<batch>_review.csv` is not "OK" or a score of 4 or 5.

Usage:
  python3 tools/audio/make_release.py SPEC.json --batches <dir with review batches> --out docs/audio-release/<date>

SPEC.json:
  {"release": "2026-10-01", "approvedBy": "...", "clips": [
     {"clipId": "ui_hearit_next", "dest": "ui", "sourceBatch": "2026-10-01-ui-lines",
      "sourceFile": "ui_hearit_next.wav", "text": "...", "voice": "...", "speed": 0.95,
      "license": "Apache-2.0 (Kokoro-82M)"}, ...]}
Optional per clip: "tool" (generator and method, for clips not made by plain Kokoro), "note".

Stdlib only, so it runs on Windows and in WSL.
"""
import argparse
import csv
import hashlib
import json
import shutil
import sys
from pathlib import Path


def sha256(path):
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1 << 16), b""):
            h.update(chunk)
    return h.hexdigest()


def review_row(batches, batch, source_file):
    """The review CSV row for source_file, or None. The CSV sits next to the batch folder or inside it."""
    name = f"{batch}_review.csv"
    for csv_path in (Path(batches) / name, Path(batches) / batch / name):
        if csv_path.exists():
            with open(csv_path, encoding="utf-8-sig", newline="") as f:
                for row in csv.DictReader(f):
                    if row.get("file") == source_file:
                        return csv_path.name, row
    return None, None


def verdict_of(row):
    """('OK' | 'score N/5', approved?)"""
    okfix = (row.get("OK_or_FIX") or "").strip().upper()
    if okfix:
        return okfix, okfix == "OK"
    score = (row.get("score_1_to_5") or "").strip()
    if score.isdigit():
        return f"score {score}/5", int(score) >= 4
    return "unscored", False


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("spec")
    ap.add_argument("--batches", required=True, help="folder that holds the review batches")
    ap.add_argument("--out", required=True, help="release folder, e.g. docs/audio-release/2026-10-01")
    args = ap.parse_args(argv)

    spec = json.loads(Path(args.spec).read_text(encoding="utf-8"))
    out = Path(args.out)
    entries, problems = [], []
    for c in spec["clips"]:
        src = Path(args.batches) / c["sourceBatch"] / c["sourceFile"]
        csv_name, row = review_row(args.batches, c["sourceBatch"], c["sourceFile"])
        if not src.exists():
            problems.append(f"{c['clipId']}: missing source {src}")
            continue
        if row is None:
            problems.append(f"{c['clipId']}: no review row for {c['sourceFile']} in {c['sourceBatch']}")
            continue
        verdict, approved = verdict_of(row)
        if not approved:
            problems.append(f"{c['clipId']}: not approved ({verdict})")
            continue
        dest = out / c["dest"] / f"{c['clipId']}.wav"
        entries.append((c, src, dest, csv_name, verdict, (row.get("note") or "").strip()))

    if problems:
        print("Release refused:\n  " + "\n  ".join(problems), file=sys.stderr)
        return 1

    manifest = {
        "release": spec["release"],
        "approvedBy": spec["approvedBy"],
        "teacherAudit": "pending, required before merge to main",
        "clips": [],
    }
    for c, src, dest, csv_name, verdict, note in entries:
        dest.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(src, dest)
        digest = sha256(dest)
        if digest != sha256(src):
            print(f"copy mismatch for {c['clipId']}", file=sys.stderr)
            return 1
        entry = {
            "clipId": c["clipId"],
            "file": f"{c['dest']}/{c['clipId']}.wav",
            "text": c["text"],
            "voice": c["voice"],
            "speed": c.get("speed"),
            "sourceBatch": c["sourceBatch"],
            "sourceFile": c["sourceFile"],
            "reviewCsv": csv_name,
            "userVerdict": verdict,
            "userNote": note,
            "teacherAudit": "pending",
            "license": c["license"],
            "sha256": digest,
        }
        for key in ("tool", "note"):
            if key in c:
                entry[key] = c[key]
        manifest["clips"].append(entry)

    out.mkdir(parents=True, exist_ok=True)
    (out / "manifest.json").write_text(json.dumps(manifest, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"Wrote {out / 'manifest.json'} with {len(manifest['clips'])} clips")
    return 0


if __name__ == "__main__":
    sys.exit(main())
