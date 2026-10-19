#!/usr/bin/env python3
"""Inspect an APK for the STD (TC-SEC-01) and, optionally, compare it with another build of the same commit.

Writes the same sections as the inspection of October 14 (docs/std/evidence/2026-10-14-0320385/
apk_inspection.txt): checksum, size, `aapt dump permissions`, the package and SDK lines of `aapt dump
badging`, and whether the INTERNET permission is present. It adds the signer certificate (keytool).

With --compare, it also writes what differs between the two APKs, entry by entry:
  - signature files (META-INF/),
  - text files that are equal once CRLF line endings are made LF,
  - dex files: D8 records a checksum for every class it writes, so the classes are compared one by one,
    and classes the compiler generated (names with $$ExternalSynthetic) are counted apart,
  - anything else, listed in full.

Usage: python tools/docs/inspect_apk.py APK --out FILE [--name NAME] [--source TEXT] [--compare OTHER_APK]
"""
from __future__ import annotations

import argparse
import hashlib
import json
import os
import re
import shutil
import subprocess
import tempfile
import zipfile
from datetime import datetime, timedelta, timezone
from pathlib import Path

PHT = timezone(timedelta(hours=8))


def find_aapt():
    sdk = os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT") \
        or os.path.join(os.environ.get("LOCALAPPDATA", ""), "Android", "Sdk")
    tools = sorted((Path(sdk) / "build-tools").glob("*"), key=lambda p: [int(x) for x in re.findall(r"\d+", p.name)])
    for d in reversed(tools):
        for name in ("aapt.exe", "aapt"):
            if (d / name).exists():
                return d / name, d.name
    raise SystemExit(f"aapt not found under {sdk}/build-tools")


def find_keytool():
    java_home = os.environ.get("JAVA_HOME")
    for name in ("keytool.exe", "keytool"):
        if java_home and (Path(java_home) / "bin" / name).exists():
            return Path(java_home) / "bin" / name
    found = shutil.which("keytool")
    if not found:
        raise SystemExit("keytool not found: set JAVA_HOME")
    return Path(found)


def run(cmd):
    return subprocess.run([str(c) for c in cmd], capture_output=True, text=True, check=True).stdout


def sha256_of(path):
    digest = hashlib.sha256()
    with open(path, "rb") as f:
        for block in iter(lambda: f.read(1 << 20), b""):
            digest.update(block)
    return digest.hexdigest()


def signer(apk):
    # keytool -jarfile needs a .jar or .apk name
    with tempfile.TemporaryDirectory() as tmp:
        copy = Path(tmp) / "check.apk"
        shutil.copyfile(apk, copy)
        out = run([find_keytool(), "-printcert", "-jarfile", copy])
    owner = re.search(r"Owner: (.+)", out)
    digest = re.search(r"SHA256: ([0-9A-F:]+)", out)
    return f"{owner.group(1).strip()}; SHA-256 {digest.group(1)}"


def inspection(apk, name, source):
    aapt, version = find_aapt()
    perms = run([aapt, "dump", "permissions", apk]).strip()
    badging = [line for line in run([aapt, "dump", "badging", apk]).splitlines()
               if re.match(r"(package|sdkVersion|targetSdkVersion)\b", line)]
    internet = "android.permission.INTERNET'" in perms
    lines = [
        "# TC-SEC-01: inspection of the submitted debug APK",
        f"# Run on {datetime.now(PHT):%Y-%m-%d %H:%M} +0800 with {version}/aapt dump; file: {name}",
    ]
    if source:
        lines.append(f"# Source: {source}")
    lines += [
        "", "## sha256sum", f"{sha256_of(apk)} *{name}",
        "", "## size (bytes)", str(Path(apk).stat().st_size),
        "", "## aapt dump permissions", perms,
        "", "## aapt dump badging (package and SDK lines)", *badging,
        "", "## Signer certificate (keytool -printcert -jarfile)", signer(apk),
        "", "## INTERNET permission present?", f"{'YES' if internet else 'NO'} (expected: NO)",
    ]
    return lines


def d8_checksums(data):
    sums = {}
    for marker in re.findall(rb'~~~(\{"L[^\n]*?\})', data):
        try:
            sums.update(json.loads(marker))
        except ValueError:
            pass
    return sums


def comparison(apk, other, name, other_name):
    a, b = zipfile.ZipFile(apk), zipfile.ZipFile(other)
    ea = {i.filename: a.read(i) for i in a.infolist()}
    eb = {i.filename: b.read(i) for i in b.infolist()}
    lines = ["", f"## Comparison with {other_name} (SHA-256 {sha256_of(other)}, {Path(other).stat().st_size} bytes)",
             f"Entries: {len(ea)} in {name}, {len(eb)} in {other_name}."]
    only = sorted(set(ea) ^ set(eb))
    lines.append(f"Entries in only one of the two: {len(only)}" + (f": {', '.join(only)}" if only else ""))
    differ = sorted(k for k in set(ea) & set(eb) if ea[k] != eb[k])
    signature = [k for k in differ if k.startswith("META-INF/")]
    eol = [k for k in differ if k not in signature
           and ea[k].replace(b"\r\n", b"\n") == eb[k].replace(b"\r\n", b"\n")]
    dex = [k for k in differ if k.endswith(".dex")]
    other_files = [k for k in differ if k not in signature + eol + dex]
    lines.append(f"Entries with different bytes: {len(differ)}")
    lines.append(f"  Signature files: {len(signature)} ({', '.join(signature)})")
    lines.append(f"  Text files that differ only in line endings (CRLF and LF): {len(eol)} ({', '.join(eol)})")
    sums_a = {}
    sums_b = {}
    for k in ea:
        if k.endswith(".dex"):
            sums_a.update(d8_checksums(ea[k]))
    for k in eb:
        if k.endswith(".dex"):
            sums_b.update(d8_checksums(eb[k]))
    changed = sorted(c for c in set(sums_a) | set(sums_b) if sums_a.get(c) != sums_b.get(c))
    synthetic = [c for c in changed if "$$ExternalSynthetic" in c]
    rest = [c for c in changed if c not in synthetic]
    lines.append(f"  Dex files: {len(dex)}. D8 class checksums: {len(sums_a)} classes in {name}, {len(sums_b)} in "
                 f"{other_name}; {len(changed)} differ, of which {len(synthetic)} are compiler-generated "
                 f"($$ExternalSynthetic) and {len(rest)} are not" + (":" if rest else "."))
    lines += [f"    {c}" for c in rest]
    lines.append(f"  Other entries: {len(other_files)}" + (f": {', '.join(other_files)}" if other_files else ""))
    lines.append(f"Signer certificate of {other_name}: {signer(other)}")
    return lines


def main():
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("apk")
    parser.add_argument("--out", required=True)
    parser.add_argument("--name", help="file name to record (default: the APK's own name)")
    parser.add_argument("--source", help="where the inspected file came from")
    parser.add_argument("--compare", help="another APK to compare with")
    parser.add_argument("--compare-name", help="name to record for the other APK")
    args = parser.parse_args()
    name = args.name or Path(args.apk).name
    lines = inspection(args.apk, name, args.source)
    if args.compare:
        lines += comparison(args.apk, args.compare, name, args.compare_name or Path(args.compare).name)
    Path(args.out).parent.mkdir(parents=True, exist_ok=True)
    Path(args.out).write_text("\n".join(lines) + "\n", encoding="utf-8", newline="\n")
    print(f"Wrote {args.out}")


if __name__ == "__main__":
    main()
