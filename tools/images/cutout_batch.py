"""Cut out and audit one picked batch in a single command: cutout.py, then audit.py.

  python3 tools/images/cutout_batch.py <batch> [--root <batches folder>] [--fill 0.70] [--app <folder>]

<batch> is a brief name such as 2026-10-07-mouth-shapes. The picks come from the repo hand-off folder
docs/assets/briefs/<batch>/ (picks/*.png, items.json, picks.json), which agy commits at the end of an
asset card. The work goes to <root>/<batch>/ (default: ~/Documents/playIT-image-batches), outside the repo:
  picks/      copies of the picked PNGs
  final/      cutout.py output (512 px app images, master/ at 1024 px), then audit.json and index.html
The user then marks OK or FIX on final/index.html, and make_release.py builds the release.

--fill and --app pass through to cutout.py and audit.py. Stdlib only here; cutout.py and audit.py need
numpy, scipy and Pillow.
"""
import argparse
import pathlib
import shutil
import sys

HERE = pathlib.Path(__file__).resolve().parent
REPO = HERE.parents[1]
sys.path.insert(0, str(HERE))

import audit  # noqa: E402
import cutout  # noqa: E402


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("batch")
    ap.add_argument("--root", default=str(pathlib.Path.home() / "Documents" / "playIT-image-batches"))
    ap.add_argument("--fill", help="passed to cutout.py (share of the canvas the picture fills)")
    ap.add_argument("--app", help="passed to audit.py (folder with the images the app uses today)")
    a = ap.parse_args(argv)

    brief = REPO / "docs" / "assets" / "briefs" / a.batch
    picks = sorted((brief / "picks").glob("*.png"))
    if not picks:
        sys.exit(f"no picked PNGs in {brief / 'picks'}; agy commits them at the end of the asset card")
    work = pathlib.Path(a.root) / a.batch
    (work / "picks").mkdir(parents=True, exist_ok=True)
    for p in picks:
        shutil.copyfile(p, work / "picks" / p.name)
    for name in ("items.json", "picks.json"):
        src = brief / name if (brief / name).exists() else brief / "picks" / name
        if src.exists():
            shutil.copyfile(src, work / name)

    cutout.main([str(work / "picks"), str(work / "final")] + (["--fill", a.fill] if a.fill else []))
    audit_args = [str(work / "final")]
    if (work / "items.json").exists():
        audit_args += ["--items", str(work / "items.json")]
    if a.app:
        audit_args += ["--app", a.app]
    audit.main(audit_args)
    print(f"cut out and audited {len(picks)} pictures in {work / 'final'}; review page: {work / 'final' / 'index.html'}")


if __name__ == "__main__":
    main()
