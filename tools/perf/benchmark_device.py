#!/usr/bin/env python3
"""Measure the installed PlayIT build on a phone over USB (adb), for the performance report.

What it measures on the phone that is plugged in:
  - start time: cold (process stopped) and from the background, with `am start -W`, N launches each;
  - memory: `dumpsys meminfo` (total PSS, Java and native heap) after the starts and after the workload;
  - a workload: a monkey run with a fixed seed (taps and drags inside the app only), timed on this PC;
  - frame times during the workload: `dumpsys gfxinfo` (50th to 99th percentile, janky and frozen frames);
  - errors during the workload: crashes and ANRs (from the monkey and from the log), and the error lines
    the app wrote to the log.

It does not measure NFR-PERF-01 (Say It and Find It feedback, tap to Listening): those start inside the app
(the end of speech, a tap on a card), so they need the app's own timestamps, from the planned telemetry log.

Before running: install the submitted APK, unlock the phone and keep the screen on, plug it in with USB
debugging on. The script first checks that the installed APK is the submitted file (SHA-256). It grants the
microphone permission so the permission dialog does not stop the monkey, and the monkey taps at random inside
the app, so it adds learners, stars and settings to the app's data on the phone.

Output: docs/std/evidence/<date>-device-performance/ with every raw output, summary.json (read by
tools/docs/assemble_midterm_submission.py for PERFORMANCE_TESTING_REPORT.pdf) and summary.md.

Usage: python tools/perf/benchmark_device.py [--serial ID] [--launches 10] [--events 5000] [--throttle 150]
                                             [--seed 411] [--out DIR] [--any-build]
"""
from __future__ import annotations

import argparse
import hashlib
import json
import math
import os
import re
import shutil
import statistics
import subprocess
import tempfile
import time
from datetime import datetime, timedelta, timezone
from pathlib import Path

PACKAGE = "com.playit.app"
# The submitted APK (STD revision 1.0.1; docs/std/evidence/2026-10-18-submitted-apk/apk_inspection.txt)
SUBMITTED_SHA256 = "b5a54c18d1405c3851b7fec5c110f03f1aeaaa899a343b12f1796e04398c42d4"
REPO = Path(__file__).resolve().parents[2]
PHT = timezone(timedelta(hours=8))
# A child taps and drags; system keys, app switches and trackball events are left out
MONKEY_MIX = ["--pct-touch", "80", "--pct-motion", "20"]
MONKEY_MIX_TEXT = "80% taps, 20% drags"
# The buffers a shell user can read on every phone (some refuse -b all)
LOG_BUFFERS = ["-b", "main", "-b", "system", "-b", "crash"]
LOG_LINE = re.compile(r"^\s*(\d+\.\d+)\s+(\d+)\s+(\d+)\s+([VDIWEFA])\s")


def find_adb():
    sdk = os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT") \
        or os.path.join(os.environ.get("LOCALAPPDATA", ""), "Android", "Sdk")
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


class Phone:
    def __init__(self, adb, serial):
        self.base = [adb, "-s", serial]

    def run(self, *args, check=True, timeout=120):
        out = subprocess.run(self.base + [str(a) for a in args], capture_output=True, timeout=timeout,
                             stdin=subprocess.DEVNULL)
        text = out.stdout.decode("utf-8", "replace").replace("\r\n", "\n")
        if check and out.returncode != 0:
            raise SystemExit(f"adb {' '.join(map(str, args))} failed:\n{text}{out.stderr.decode('utf-8', 'replace')}")
        return text

    def shell(self, command, check=True, timeout=120):
        return self.run("shell", command, check=check, timeout=timeout)


def pick_serial(adb, wanted):
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


def sha256_of(path):
    digest = hashlib.sha256()
    with open(path, "rb") as f:
        for block in iter(lambda: f.read(1 << 20), b""):
            digest.update(block)
    return digest.hexdigest()


def percentile(sorted_values, p):
    """Nearest-rank percentile."""
    return sorted_values[max(0, math.ceil(p / 100 * len(sorted_values)) - 1)]


def stats(values):
    v = sorted(x for x in values if x is not None)
    if not v:
        return None
    return {"n": len(v), "median": statistics.median(v), "p90": percentile(v, 90), "min": v[0], "max": v[-1],
            "mean": round(statistics.fmean(v), 1)}


def parse_launch(text):
    def number(key):
        m = re.search(rf"^{key}: (\d+)", text, re.M)
        return int(m.group(1)) if m else None
    state = re.search(r"^LaunchState: (\w+)", text, re.M)
    return {"total_ms": number("TotalTime"), "wait_ms": number("WaitTime"),
            "state": state.group(1) if state else None, "ok": "Status: ok" in text}


def parse_meminfo(text):
    def kb(pattern):
        m = re.search(pattern, text, re.M)
        return int(m.group(1)) if m else None
    return {"total_pss_kb": kb(r"TOTAL PSS:\s+(\d+)") or kb(r"^\s*TOTAL\s+(\d+)"),
            "java_heap_kb": kb(r"Java Heap:\s+(\d+)"), "native_heap_kb": kb(r"Native Heap:\s+(\d+)")}


def parse_gfxinfo(text):
    def number(pattern):
        m = re.search(pattern, text)
        return int(m.group(1)) if m else None
    janky = re.search(r"Janky frames: (\d+) \(([\d.]+)%\)", text)
    out = {"frames": number(r"Total frames rendered: (\d+)"),
           "janky": int(janky.group(1)) if janky else None, "janky_pct": float(janky.group(2)) if janky else None}
    for p in (50, 90, 95, 99):
        out[f"p{p}_ms"] = number(rf"{p}th percentile: (\d+)ms")
    # The 16 ms and 700 ms buckets start at their label. Android vitals calls frames over 16 ms slow and frames
    # over 700 ms frozen.
    histogram = re.search(r"^HISTOGRAM: (.*)$", text, re.M)
    buckets = [(int(ms), int(n)) for ms, n in re.findall(r"(\d+)ms=(\d+)", histogram.group(1))] if histogram else []
    total = sum(n for _, n in buckets)
    out["slow_16ms"] = sum(n for ms, n in buckets if ms >= 16) if buckets else None
    out["slow_16ms_pct"] = round(out["slow_16ms"] / total * 100, 1) if total else None
    out["frozen_700ms"] = sum(n for ms, n in buckets if ms >= 700) if buckets else None
    return out


def parse_battery(text):
    level = re.search(r"^\s*level: (\d+)", text, re.M)
    temp = re.search(r"^\s*temperature: (\d+)", text, re.M)
    return {"level_pct": int(level.group(1)) if level else None,
            "temperature_c": int(temp.group(1)) / 10 if temp else None}


def app_log(lines, pids):
    """The log lines of the app's processes, and the system's lines that name the app."""
    pids = set(pids)
    for line in lines:
        m = re.search(rf"Start proc (\d+):{re.escape(PACKAGE)}", line)
        if m:
            pids.add(m.group(1))
    keep = []
    for line in lines:
        m = LOG_LINE.match(line)
        if m and (m.group(2) in pids or PACKAGE in line):
            keep.append(line)
    return keep, pids


def launches(phone, component, count, from_background, raw, name):
    results = []
    for i in range(count):
        if from_background:
            phone.shell("input keyevent KEYCODE_HOME")
        else:
            phone.shell(f"am force-stop {PACKAGE}")
        time.sleep(2)
        out = phone.shell(f"am start -W -n {component}")
        raw.append(f"## launch {i + 1}\n{out}")
        results.append(parse_launch(out))
        print(f"  {name} {i + 1}/{count}: {results[-1]['total_ms']} ms ({results[-1]['state']})")
        time.sleep(5)
    return results


def main():
    parser = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    parser.add_argument("--serial", help="the phone's adb serial, when more than one device is connected")
    parser.add_argument("--launches", type=int, default=10, help="cold starts, and starts from the background")
    parser.add_argument("--events", type=int, default=5000, help="monkey events in the workload")
    parser.add_argument("--throttle", type=int, default=150,
                        help="ms the monkey waits after each tap or drag (each is several events)")
    parser.add_argument("--seed", type=int, default=411, help="monkey seed, so the run can be repeated")
    parser.add_argument("--out", help="output folder (default: docs/std/evidence/<date>-device-performance)")
    parser.add_argument("--any-build", action="store_true", help="measure even if the installed APK is not the "
                                                                  "submitted file")
    args = parser.parse_args()

    started = datetime.now(PHT)
    out_dir = Path(args.out) if args.out else \
        REPO / "docs" / "std" / "evidence" / f"{started:%Y-%m-%d}-device-performance"
    if out_dir.exists() and any(out_dir.iterdir()):
        raise SystemExit(f"{out_dir} already holds a run; pass --out to write a new one")
    adb = find_adb()
    phone = Phone(adb, pick_serial(adb, args.serial))
    raw = {}

    print("Phone and build")
    prop = lambda key: phone.shell(f"getprop {key}").strip()
    device = {"manufacturer": prop("ro.product.manufacturer"), "model": prop("ro.product.model"),
              "android": prop("ro.build.version.release"), "sdk": prop("ro.build.version.sdk"),
              "fingerprint": prop("ro.build.fingerprint"),
              "screen": phone.shell("wm size").strip().split(": ")[-1],
              "density": phone.shell("wm density").strip().split(": ")[-1]}
    apk_path = phone.shell(f"pm path {PACKAGE}", check=False).strip().removeprefix("package:").splitlines()
    if not apk_path:
        raise SystemExit(f"{PACKAGE} is not installed on the phone")
    with tempfile.TemporaryDirectory() as tmp:
        local = Path(tmp) / "installed.apk"
        phone.run("pull", apk_path[0].strip(), local, timeout=600)
        build = {"package": PACKAGE, "sha256": sha256_of(local), "size": local.stat().st_size}
    version = re.search(r"versionName=(\S+)", phone.shell(f"dumpsys package {PACKAGE}"))
    build["version_name"] = version.group(1) if version else None
    build["matches_submitted"] = build["sha256"] == SUBMITTED_SHA256
    print(f"  {device['manufacturer']} {device['model']}, Android {device['android']}; installed APK "
          f"{build['sha256'][:8]} ({'the submitted file' if build['matches_submitted'] else 'NOT the submitted file'})")
    if not build["matches_submitted"] and not args.any_build:
        raise SystemExit(f"The installed APK is not the submitted file ({SUBMITTED_SHA256[:8]}...); install it, "
                         "or pass --any-build to measure this one")

    phone.shell(f"pm grant {PACKAGE} android.permission.RECORD_AUDIO", check=False)
    phone.shell("svc power stayon usb", check=False)
    phone.shell("input keyevent KEYCODE_WAKEUP", check=False)
    raw["battery_before.txt"] = phone.shell("dumpsys battery")
    resolved = [line for line in phone.shell(f"cmd package resolve-activity --brief {PACKAGE}").splitlines()
                if "/" in line]
    component = resolved[-1].strip() if resolved else f"{PACKAGE}/.MainActivity"

    try:
        print(f"Cold starts ({args.launches})")
        log = []
        cold = launches(phone, component, args.launches, False, log, "cold start")
        raw["start_cold.txt"] = "\n".join(log)
        raw["meminfo_after_starts.txt"] = phone.shell(f"dumpsys meminfo {PACKAGE}")

        print(f"Starts from the background ({args.launches})")
        log = []
        warm = launches(phone, component, args.launches, True, log, "start from background")
        raw["start_from_background.txt"] = "\n".join(log)

        print(f"Workload: {args.events} monkey events, {args.throttle} ms apart, seed {args.seed}")
        phone.run("logcat", *LOG_BUFFERS, "-c", check=False)
        pids = phone.shell(f"pidof {PACKAGE}", check=False).split()
        phone.shell(f"dumpsys gfxinfo {PACKAGE} reset")
        monkey = (f"monkey -p {PACKAGE} -s {args.seed} --throttle {args.throttle} {' '.join(MONKEY_MIX)} "
                  f"--ignore-crashes --ignore-timeouts --ignore-security-exceptions -v {args.events}")
        t0 = time.monotonic()
        raw["monkey.txt"] = f"$ {monkey}\n" + phone.shell(monkey, check=False,
                                                            timeout=args.events * args.throttle / 1000 * 3 + 900)
        seconds = round(time.monotonic() - t0, 1)
        raw["gfxinfo_workload.txt"] = phone.shell(f"dumpsys gfxinfo {PACKAGE}")
        raw["meminfo_after_workload.txt"] = phone.shell(f"dumpsys meminfo {PACKAGE}")
        lines = phone.run("logcat", "-d", *LOG_BUFFERS, "-v", "epoch", check=False, timeout=300).splitlines()
        kept, pids = app_log(lines, pids)
        raw["logcat_app.txt"] = "\n".join(kept)
    finally:
        phone.shell("svc power stayon false", check=False)
    raw["battery_after.txt"] = phone.shell("dumpsys battery")
    finished = datetime.now(PHT)

    monkey_text = raw["monkey.txt"]
    injected = re.search(r"Events injected: (\d+)", monkey_text)
    injected = int(injected.group(1)) if injected else None
    app_lines = [line for line in kept if LOG_LINE.match(line) and LOG_LINE.match(line).group(2) in pids]
    errors = {
        "crashes_monkey": monkey_text.count("// CRASH"),
        "anrs_monkey": monkey_text.count("// NOT RESPONDING"),
        "crashes_log": sum(1 for line in kept if f"Process: {PACKAGE}, PID" in line),
        "anrs_log": sum(1 for line in kept if f"ANR in {PACKAGE}" in line),
        "app_error_lines": sum(1 for line in app_lines if LOG_LINE.match(line).group(4) in "EF"),
    }
    failures = max(errors["crashes_monkey"], errors["crashes_log"]) + max(errors["anrs_monkey"], errors["anrs_log"])
    errors["failures_per_1000_events"] = round(failures / injected * 1000, 2) if injected else None
    errors["app_error_lines_per_1000_events"] = round(errors["app_error_lines"] / injected * 1000, 2) \
        if injected else None
    frames = parse_gfxinfo(raw["gfxinfo_workload.txt"])
    frames["frames_per_s"] = round(frames["frames"] / seconds, 1) if frames["frames"] and seconds else None
    summary = {
        "run": {"started": started.isoformat(timespec="seconds"), "finished": finished.isoformat(timespec="seconds"),
                "tool": "tools/perf/benchmark_device.py", "component": component,
                "settings": {"launches": args.launches, "events": args.events, "throttle_ms": args.throttle,
                             "seed": args.seed, "mix": MONKEY_MIX_TEXT}},
        "device": device,
        "build": build,
        "battery": {"before": parse_battery(raw["battery_before.txt"]),
                    "after": parse_battery(raw["battery_after.txt"])},
        "cold_start": {"runs": cold, "total_ms": stats(r["total_ms"] for r in cold),
                       "states": sorted({r["state"] for r in cold if r["state"]})},
        "background_start": {"runs": warm, "total_ms": stats(r["total_ms"] for r in warm),
                             "states": sorted({r["state"] for r in warm if r["state"]})},
        "memory": {"after_starts": parse_meminfo(raw["meminfo_after_starts.txt"]),
                   "after_workload": parse_meminfo(raw["meminfo_after_workload.txt"])},
        "workload": {"events_requested": args.events, "events_injected": injected, "seconds": seconds,
                     "events_per_s": round(injected / seconds, 1) if injected and seconds else None,
                     "finished": "// Monkey finished" in monkey_text},
        "frames": frames,
        "errors": errors,
    }

    out_dir.mkdir(parents=True, exist_ok=True)
    for name, text in raw.items():
        (out_dir / name).write_text(text.rstrip("\n") + "\n", encoding="utf-8", newline="\n")
    (out_dir / "summary.json").write_text(json.dumps(summary, indent=2) + "\n", encoding="utf-8", newline="\n")
    (out_dir / "summary.md").write_text(summary_markdown(summary), encoding="utf-8", newline="\n")
    print(f"Wrote {out_dir}")


def summary_markdown(s):
    def ms(st, key):
        return f"{st[key]:,.0f} ms" if st else "not recorded"

    def mb(kb):
        return f"{kb / 1024:.0f} MB" if kb else "not recorded"

    def value(x, unit=""):
        return "not recorded" if x is None else f"{x:g}{unit}"
    d, b, w, f, e = s["device"], s["build"], s["workload"], s["frames"], s["errors"]
    cold, warm = s["cold_start"]["total_ms"], s["background_start"]["total_ms"]
    mem_a, mem_b = s["memory"]["after_starts"], s["memory"]["after_workload"]
    lines = [
        "# PlayIT: performance run on the phone",
        "",
        f"- **Run:** {s['run']['started']} to {s['run']['finished']}, with `{s['run']['tool']}`",
        f"- **Phone:** {d['manufacturer']} {d['model']}, Android {d['android']} (SDK {d['sdk']}), "
        f"screen {d['screen']}, density {d['density']}",
        f"- **Build:** `{b['package']}` {b['version_name']}, SHA-256 `{b['sha256']}`, {b['size']:,} bytes "
        f"({'the submitted file' if b['matches_submitted'] else 'not the submitted file'})",
        f"- **Battery:** {value(s['battery']['before']['level_pct'], '%')} and "
        f"{value(s['battery']['before']['temperature_c'], ' C')} before; "
        f"{value(s['battery']['after']['level_pct'], '%')} and {value(s['battery']['after']['temperature_c'], ' C')} after",
        "",
        "## Response times",
        "",
        "| Measure | Runs | Median | 90th percentile | Min | Max | Launch state |",
        "|---|---|---|---|---|---|---|",
    ]
    for name, st, states in (("Cold start (`am start -W`, TotalTime)", cold, s["cold_start"]["states"]),
                             ("Start from the background", warm, s["background_start"]["states"])):
        lines.append(f"| {name} | {st['n'] if st else 0} | {ms(st, 'median')} | {ms(st, 'p90')} | {ms(st, 'min')} | "
                     f"{ms(st, 'max')} | {', '.join(states) or 'not reported'} |")
    lines += [
        "",
        "## Workload and throughput",
        "",
        f"- **Workload:** {w['events_injected']} of {w['events_requested']} monkey events "
        f"({s['run']['settings']['mix']}, a {s['run']['settings']['throttle_ms']} ms pause after each, seed "
        f"{s['run']['settings']['seed']}) in {w['seconds']} s; finished: {'yes' if w['finished'] else 'no'}",
        f"- **Throughput:** {value(w['events_per_s'])} events per second; {value(f['frames_per_s'])} frames "
        "rendered per second",
        "",
        "## Frames during the workload (`dumpsys gfxinfo`)",
        "",
        f"- **Frames rendered:** {value(f['frames'])}; janky (missed their deadline): {value(f['janky'])} "
        f"({value(f['janky_pct'], '%')}); 16 ms or longer: {value(f['slow_16ms'])} ({value(f['slow_16ms_pct'], '%')}); "
        f"700 ms or longer (frozen): {value(f['frozen_700ms'])}",
        f"- **Frame time:** 50th percentile {value(f['p50_ms'], ' ms')}, 90th {value(f['p90_ms'], ' ms')}, "
        f"95th {value(f['p95_ms'], ' ms')}, 99th {value(f['p99_ms'], ' ms')}",
        "",
        "## Memory (`dumpsys meminfo`, total PSS)",
        "",
        f"- **After the starts:** {mb(mem_a['total_pss_kb'])} (Java heap {mb(mem_a['java_heap_kb'])}, native heap "
        f"{mb(mem_a['native_heap_kb'])})",
        f"- **After the workload:** {mb(mem_b['total_pss_kb'])} (Java heap {mb(mem_b['java_heap_kb'])}, native heap "
        f"{mb(mem_b['native_heap_kb'])})",
        "",
        "## Errors during the workload",
        "",
        f"- **Crashes:** {e['crashes_monkey']} reported by the monkey, {e['crashes_log']} in the log",
        f"- **ANRs (app not responding):** {e['anrs_monkey']} reported by the monkey, {e['anrs_log']} in the log",
        f"- **Failure rate:** {value(e['failures_per_1000_events'])} crashes and ANRs per 1,000 events",
        f"- **Error lines the app logged:** {e['app_error_lines']} ({value(e['app_error_lines_per_1000_events'])} "
        "per 1,000 events; `logcat_app.txt`)",
        "",
        "Not measured here: NFR-PERF-01 (Say It and Find It feedback, tap to Listening) needs the app's own "
        "timestamps (the planned telemetry log).",
    ]
    return "\n".join(lines) + "\n"


if __name__ == "__main__":
    main()
