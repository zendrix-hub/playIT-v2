"""Build an image release: copy user-approved cutouts and write manifest.json (AGY_RUNBOOK.md "Image gate").

A release is the only way a picture reaches app/src/main/assets/images/. This tool copies each picture
unchanged from <batch>/final/, records its SHA-256, and refuses any picture whose row in the final review
CSV (<batch>_final_review.csv, exported from final/index.html by tools/images/audit.py) is not "OK", or
whose audit.json has a FAIL.

Usage:
  python3 tools/images/make_release.py <batch_dir> --out docs/image-release/<date> [--dest pictures]

Stdlib only, so it runs on Windows and in WSL.
"""
import argparse
import csv
import hashlib
import json
import pathlib
import shutil
import sys


def sha256(path):
    return hashlib.sha256(pathlib.Path(path).read_bytes()).hexdigest()


def find_csv(batch):
    name = f"{batch.name}_final_review.csv"
    for p in (batch / name, batch / "final" / name, pathlib.Path.home() / "Downloads" / name):
        if p.exists():
            return p
    sys.exit(f"no {name} in {batch} or Downloads; export it from final/index.html first")


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("batch")
    ap.add_argument("--out", required=True)
    ap.add_argument("--dest", default="pictures", help="folder under app/src/main/assets/images/")
    a = ap.parse_args(argv)

    batch, out = pathlib.Path(a.batch), pathlib.Path(a.out)
    final = batch / "final"
    review = find_csv(batch)
    rows = {r["item"]: r for r in csv.DictReader(review.open(encoding="utf-8"))}
    audit = json.loads((final / "audit.json").read_text(encoding="utf-8"))
    items = {i["id"]: i for i in json.loads((batch / "items.json").read_text(encoding="utf-8"))["items"]}
    picks = json.loads((batch / "picks.json").read_text(encoding="utf-8"))

    refused, entries = [], []
    for name in sorted(audit):
        item = name[:-4]
        verdict = rows.get(item, {}).get("verdict", "")
        fails = [k for k, v in audit[name].items() if v.startswith("FAIL")]
        if verdict != "OK" or fails:
            refused.append(f"{item}: verdict {verdict or 'none'}{', audit FAIL ' + ','.join(fails) if fails else ''}")
            continue
        (out / a.dest).mkdir(parents=True, exist_ok=True)
        shutil.copyfile(final / name, out / a.dest / name)
        entries.append({
            "itemId": item, "file": f"{a.dest}/{name}", "appPath": f"images/{a.dest}/{name}",
            "word": items.get(item, {}).get("word"), "letter": items.get(item, {}).get("letter"),
            "sourceBatch": batch.name, "pick": picks.get(item), "tool": "tools/images/cutout.py",
            "generator": "Nano Banana Pro (agy, card 08)", "audit": audit[name],
            "reviewCsv": review.name, "userVerdict": verdict, "userNote": rows[item].get("note", ""),
            "sha256": sha256(out / a.dest / name),
        })
    if not entries:
        sys.exit("nothing approved; no release written\n" + "\n".join(refused))
    manifest = {"release": out.name, "approvedBy": f"user (final review page {batch.name}, {review.name})",
                "images": entries}
    (out / "manifest.json").write_text(json.dumps(manifest, indent=1, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"released {len(entries)} pictures into {out}")
    if refused:
        print("not released:\n  " + "\n  ".join(refused))


if __name__ == "__main__":
    main()
