#!/usr/bin/env python3
"""STD TC-AUD-03: check every clip in docs/audio-release/*/manifest.json against the app's copy.

For each released clip, the release copy (docs/audio-release/<release>/<file>) and the app copy
(app/src/main/assets/audio/<file>, or audio/vo/<file> for lesson and tutor lines) must exist with the same
SHA-256, and must match the manifest's sha256 when it records one. When two releases list the same file,
the later release counts (the rule PictureAssetsTest uses for images). Then it counts the shipped audio
files that no release lists (the MVP clips that predate the release gate).

Usage: python3 tools/docs/check_audio_release.py [out.txt]   (prints the report; writes it too if a path is given)
Exit code 1 if a clip that the app ships differs from its release.
"""
from __future__ import annotations

import hashlib
import json
import sys
from collections import Counter
from pathlib import Path

REPO = Path(__file__).resolve().parents[2]
RELEASES = REPO / "docs" / "audio-release"
ASSETS = REPO / "app" / "src" / "main" / "assets" / "audio"
NOT_CLIPS = ("vosk-model-small-en-us", "_dev_placeholder")


def sha256(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def app_copy(file: str) -> Path | None:
    return next((p for p in (ASSETS / file, ASSETS / "vo" / file) if p.exists()), None)


def main() -> int:
    latest: dict[str, tuple[str, dict]] = {}
    for manifest in sorted(RELEASES.glob("*/manifest.json")):
        for clip in json.loads(manifest.read_text(encoding="utf-8"))["clips"]:
            latest[clip["file"]] = (manifest.parent.name, clip)

    rows, counts = [], Counter()
    for file, (release, clip) in sorted(latest.items()):
        src, app = RELEASES / release / file, app_copy(file)
        if not src.exists():
            status, note = "FAIL", "release copy missing"
        elif app is None:
            status, note = "NOT IN APP", "released, not used by APK H (no reference in app code)"
        elif sha256(src) != sha256(app):
            status, note = "FAIL", "app bytes differ from the release"
        elif clip.get("sha256") and clip["sha256"].lower() != sha256(app):
            status, note = "FAIL", "manifest sha256 differs"
        else:
            status, note = "PASS", sha256(app)[:16]
        counts[status] += 1
        rows.append(f"{status:<10}  {release:<18}  {file:<42}  {note}")

    released = {f for f in latest} | {f"vo/{f}" for f in latest}
    shipped = [p.relative_to(ASSETS).as_posix() for p in ASSETS.rglob("*")
               if p.is_file() and p.name != ".gitkeep" and not any(n in p.parts for n in NOT_CLIPS)]
    legacy = [f for f in shipped if f not in released]
    by_folder = Counter(f.split("/")[0] for f in legacy)

    report = "\n".join([
        "# TC-AUD-03: released audio clips against the app's assets",
        f"# Releases: {len(list(RELEASES.glob('*/manifest.json')))} manifests, {len(latest)} clips",
        "",
        f"{'STATUS':<10}  {'RELEASE':<18}  {'FILE':<42}  NOTE (sha256 prefix when PASS)",
        *rows,
        "",
        f"Released clips: {len(latest)}; PASS {counts['PASS']}, NOT IN APP {counts['NOT IN APP']}, FAIL {counts['FAIL']}",
        f"Shipped audio files: {len(shipped)}; from a release: {len(shipped) - len(legacy)}; "
        f"not in any release (MVP clips, before the gate): {len(legacy)} "
        f"({', '.join(f'{k} {v}' for k, v in sorted(by_folder.items()))})",
    ])
    sys.stdout.buffer.write(report.encode("utf-8") + b"\n")
    if len(sys.argv) > 1:
        Path(sys.argv[1]).write_text(report + "\n", encoding="utf-8", newline="\n")
    return 1 if counts["FAIL"] else 0


if __name__ == "__main__":
    sys.exit(main())
