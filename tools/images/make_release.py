"""Build an image release: copy user-approved cutouts and write manifest.json (AGY_RUNBOOK.md "Image gate").

A release is the only way a picture reaches app/src/main/assets/images/. This tool copies each picture
unchanged from <batch>/final/, records its SHA-256, and refuses any picture whose row in the final review
CSV (<batch>_final_review.csv, exported from final/index.html by tools/images/audit.py) is not "OK", or
whose audit.json has a FAIL.

Usage:
  python3 tools/images/make_release.py <batch_dir> --out docs/image-release/<date> [--dest pictures]
  python3 tools/images/make_release.py <batch_dir> --out <batch_dir>/release-draft/<date> --draft
      Prepares the same manifest before the user's final OK (every image "pending"); never under
      docs/image-release, because review_card.py treats every manifest there as approved.

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
    ap.add_argument("--generator", default="Nano Banana Pro (agy, card 08)",
                    help="how the picks were made, recorded per image")
    ap.add_argument("--draft", action="store_true",
                    help="prepare the manifest before the user's final OK: no review CSV, every image 'pending'. "
                         "--out must be outside docs/image-release, because review_card.py treats every manifest "
                         "there as approved")
    ap.add_argument("--approved-by", help="how the user gave the verdicts, when not by the page's CSV export")
    a = ap.parse_args(argv)

    batch, out = pathlib.Path(a.batch), pathlib.Path(a.out)
    if a.draft and "image-release" in out.resolve().parts:
        sys.exit("a draft never goes into docs/image-release: a manifest there counts as the user's approval")
    final = batch / "final"
    review = None if a.draft else find_csv(batch)
    rows = {} if a.draft else {r["item"]: r for r in csv.DictReader(review.open(encoding="utf-8"))}
    audit = json.loads((final / "audit.json").read_text(encoding="utf-8"))
    items = {i["id"]: i for i in json.loads((batch / "items.json").read_text(encoding="utf-8"))["items"]}
    picks = json.loads((batch / "picks.json").read_text(encoding="utf-8"))

    refused, entries = [], []
    for name in sorted(audit):
        item = name[:-4]
        verdict = "pending" if a.draft else rows.get(item, {}).get("verdict", "")
        fails = [k for k, v in audit[name].items() if v.startswith("FAIL")]
        if fails or (verdict != "OK" and not a.draft):
            refused.append(f"{item}: verdict {verdict or 'none'}{', audit FAIL ' + ','.join(fails) if fails else ''}")
            continue
        # An item's "app_file" (e.g. "characters/avatar_01_cat.png") picks its own folder; else --dest.
        rel = items.get(item, {}).get("app_file", f"{a.dest}/{name}")
        (out / rel).parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(final / name, out / rel)
        entries.append({
            "itemId": item, "file": rel, "appPath": f"images/{rel}",
            "word": items.get(item, {}).get("word"), "letter": items.get(item, {}).get("letter"),
            "sourceBatch": batch.name, "pick": picks.get(item), "tool": "tools/images/cutout.py",
            "generator": a.generator, "audit": audit[name],
            "reviewCsv": review.name if review else None, "userVerdict": verdict,
            "userNote": rows.get(item, {}).get("note", ""),
            "sha256": sha256(out / rel),
        })
    if not entries:
        sys.exit("nothing approved; no release written\n" + "\n".join(refused))
    if a.draft:
        approved = "PENDING: the user's final OK on final/index.html; then run make_release.py without --draft"
    else:
        approved = a.approved_by or f"user (final review page {batch.name}, {review.name})"
    manifest = {"release": out.name, "approvedBy": approved, "images": entries}
    if a.draft:
        manifest["draft"] = True
    (out / "manifest.json").write_text(json.dumps(manifest, indent=1, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"{'prepared a DRAFT of' if a.draft else 'released'} {len(entries)} pictures into {out}")
    if refused:
        print("not released:\n  " + "\n  ".join(refused))


if __name__ == "__main__":
    main()
