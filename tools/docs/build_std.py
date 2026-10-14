#!/usr/bin/env python3
"""Build the Software Test Document (STD) source, docs/STD_v1.0.md, from its template and the test evidence.

The template (docs/std/STD_template.md) holds the hand-written text. This script fills in what must come
from the recorded run, so the document cannot claim more than the logs show:

  {{tc: Class.method, Class.*, LayoutMatrix*.method}}
        Two table cells: the cited tests, and their status from the JUnit XML. Every cited test must exist
        in the run and pass (a skip counts only for the compact font-scale checks); otherwise the build
        stops. "Class.*" means every test of the class; "LayoutMatrix*" means the check on all 4 sizes.
  {{RUN_SUMMARY}}    the execution record (time window, totals, environment)
  {{CLASS_TABLE}}    results per test class, mapped to features and requirements
  {{LAYOUT_TABLE}}   the 21 layout checks on the 4 screen profiles
  {{TEST_LOG}}       every test case of the run with its status and time (Appendix A)
  {{COUNT:key}}      single numbers (tests, passed, skipped, failed, classes, layout_runs, ...)

With --figures it also builds the layout comparison figures (docs/std/figures/) from the Roborazzi
captures in app/build/outputs/roborazzi/ (run `gradlew recordRoborazziDebug --tests
"com.playit.app.screenshot.*"` first).

Usage: python3 tools/docs/build_std.py [--figures]
"""
from __future__ import annotations

import re
import sys
import xml.etree.ElementTree as ET
from collections import OrderedDict
from datetime import datetime, timedelta, timezone
from pathlib import Path

REPO = Path(__file__).resolve().parents[2]
STD_DIR = REPO / "docs" / "std"
EVIDENCE = STD_DIR / "evidence" / "2026-10-14-0320385"
JUNIT = EVIDENCE / "junit"
TEMPLATE = STD_DIR / "STD_template.md"
OUT = REPO / "docs" / "STD_v1.0.md"
FIGURES = STD_DIR / "figures"
ROBORAZZI = REPO / "app" / "build" / "outputs" / "roborazzi"
PHT = timezone(timedelta(hours=8))

SIZES = OrderedDict([("Compact", "360x640"), ("A21s", "360x740"), ("Phone", "411x891"), ("Tablet", "800x1280")])

# Test class -> (feature, requirement IDs). Every class in the run must be listed here.
CLASS_MAP = {
    "HearItSequenceBuilderTest": ("Hear It: modeling sequence", "FR-02"),
    "HearItViewModelTest": ("Hear It: playback, replay, next cue", "FR-01, FR-02, NFR-IND-01"),
    "HearItScreenFocusTest": ("Hear It: one message at a time", "FR-02"),
    "HearItScreenshotTest": ("Hear It: screen capture", "NFR-ACC-02"),
    "AudioResolverTest": ("Audio: released clips and paths", "FR-02, FR-13, NFR-AUD-01"),
    "AudioCompletenessCheckTest": ("Audio: every required clip on disk", "NFR-AUD-01, NFR-IND-01"),
    "CaptionTextTest": ("Audio: caption text (kept in code)", "NFR-ACC-01"),
    "SayItViewModelTest": ("Say It: tutoring loop, judge, idle", "FR-03, NFR-ASR-01, NFR-IND-01"),
    "SayItFeedbackCopyTest": ("Say It: feedback wording", "FR-03"),
    "MicStatusTest": ("Say It: four mic states", "FR-03"),
    "TutorPolicyTest": ("Say It: prompt ladder", "FR-03"),
    "SpeechValidatorTest": ("Say It: word judge and foils", "NFR-ASR-01, FR-03"),
    "ArticulationGroupTest": ("Say It: letter-to-mouth pairs", "NFR-ACC-01"),
    "ArticulationCueTest": ("Say It: mouth-shape picture", "NFR-ACC-01"),
    "FindItViewModelTest": ("Find It: taps, hearts, restart", "FR-04, FR-05, FR-07"),
    "GridGeneratorTest": ("Find It: grid and distractors", "FR-04"),
    "HeartManagerTest": ("Hearts: deduct, restart, recover", "FR-05, FR-07"),
    "FindItScreenshotTest": ("Find It: screen capture", "NFR-ACC-02"),
    "PictureAssetsTest": ("Pictures: released images", "FR-04, NFR-ACC-01"),
    "BlendItViewModelTest": ("Blend It: tiles, hints, hearts", "FR-13, FR-05"),
    "BlendItTileDealTest": ("Blend It: tile deal", "FR-13"),
    "BlendItWordSeedsTest": ("Blend It: decodable word bank", "FR-13"),
    "BlendItWordSelectorTest": ("Blend It: words per group", "FR-13"),
    "BlendItCompleteViewModelTest": ("Blend It: group stars", "FR-06, FR-13"),
    "BlendItStarThresholdsTest": ("Blend It: star thresholds", "FR-06"),
    "BlendItCardLayoutTest": ("Blend It: card layout", "NFR-ACC-02"),
    "BlendItScreenshotTest": ("Blend It: screen capture", "NFR-ACC-02"),
    "StarCalculatorTest": ("Progress: letter stars", "FR-06"),
    "LetterCompleteViewModelTest": ("Progress: letter complete", "FR-06"),
    "LetterCompleteMessageTest": ("Progress: completion message", "FR-06"),
    "LetterCompleteScreenshotTest": ("Progress: screen capture", "NFR-ACC-02"),
    "RoutesTest": ("Progress: results carried by navigation", "FR-06"),
    "UnlockManagerTest": ("Progress: linear unlock", "FR-08"),
    "GroupUnlockManagerTest": ("Progress: group and Blend It unlock", "FR-08"),
    "StreakTrackerTest": ("Progress: daily streak", "FR-09"),
    "LetterStatusCalculatorTest": ("Parent Zone: letter status flags", "FR-10"),
    "RetentionCalculatorTest": ("Parent Zone: retention score", "FR-10"),
    "ParentDashboardViewModelTest": ("Parent Zone: dashboard, report, profiles", "FR-10, FR-12, FR-14"),
    "ArithmeticGateManagerTest": ("Parent Zone: arithmetic gate", "FR-10"),
    "VoiceCreditTest": ("Parent Zone: ElevenLabs credit", "NFR-AUD-01"),
    "ProfileViewModelTest": ("Profiles: create, select, limit", "FR-14, NFR-IND-01"),
    "ProfileDaoStarsTest": ("Profiles: per-profile star totals", "FR-14, FR-06"),
    "NamePromptScreenshotTest": ("Profiles: screen capture", "NFR-ACC-02"),
    "MapViewModelTest": ("Map: nodes, unlock moment, cues", "FR-08, FR-09"),
    "MapTapRulesTest": ("Map: tap rules", "FR-08"),
    "MapLayoutTest": ("Map: trail layout", "NFR-ACC-02"),
    "MapPathGeometryTest": ("Map: trail geometry", "NFR-ACC-02"),
    "MapTextFloorTest": ("Map: 16 sp text floor", "NFR-ACC-02"),
    "TopStatsBarLayoutTest": ("Map: header card, 64 dp closes", "NFR-ACC-02"),
    "SplashScreenTest": ("Splash: start, reduced motion", "NFR-ACC-02, NFR-IND-01"),
    "DimensTest": ("Design system: 64 dp, 16 sp floors", "NFR-ACC-02"),
    "GummyContainerLayoutTest": ("Design system: container sizing", "NFR-ACC-02"),
    "PlayItMotionTest": ("Design system: motion, reduced motion", "NFR-ACC-02"),
    "FeedbackEffectsTest": ("Design system: feedback effects", "NFR-ACC-02"),
    "IdleTimerTest": ("Idle re-prompt timer (10 s)", "NFR-IND-01"),
    "IdleWiggleTest": ("Idle wiggle on the main button", "NFR-IND-01"),
    "AssetImageTest": ("Pictures: loading", "NFR-REL-01"),
    "AssetImageLoadTest": ("Pictures: no stale picture", "NFR-ACC-01"),
    "ManifestPrivacyTest": ("Privacy: backup disabled", "NFR-SEC-01"),
    "ZeroEmojiPolicyTest": ("Policy: zero emoji", "Team policy"),
    "PerformancePolicyTest": ("Policy: no haptics, portrait, no endless idle motion", "Team policy"),
    "ViewModelInitOrderTest": ("Code health: init order", "NFR-REL-01"),
    "LayoutMatrixCompactTest": ("Layout matrix, 360x640", "NFR-ACC-02"),
    "LayoutMatrixA21sTest": ("Layout matrix, 360x740 (A21s)", "NFR-ACC-02"),
    "LayoutMatrixPhoneTest": ("Layout matrix, 411x891", "NFR-ACC-02"),
    "LayoutMatrixTabletTest": ("Layout matrix, 800x1280", "NFR-ACC-02"),
}

# The 21 layout checks, in the order of LayoutMatrixTest, with what each one asserts.
LAYOUT_CHECKS = OrderedDict([
    ("lessonScaffold_bottomBarAndBodyVisible", "Lesson frame: body and bottom bar end inside the window"),
    ("fontScale13_mainActionVisible", "Lesson frame at font scale 1.3: same"),
    ("tablet_contentWidthCapped", "Lesson content is at most 560 dp wide"),
    ("hearIt_playVisible", "Hear It: play button inside the window"),
    ("hearIt_stepsAboveNext", "Hear It: step dots end above Next"),
    ("sayIt_micVisible", "Say It: mic inside the window"),
    ("sayIt_micVisible_fontScale13", "Say It at font scale 1.3: mic inside the window"),
    ("sayIt_mouthCueBesideMic", "Say It after two misses: mouth picture beside the mic, mic on screen"),
    ("findIt_wholeGridVisible", "Find It: whole picture grid inside the window"),
    ("findIt_fontScale13", "Find It at font scale 1.3: same"),
    ("blendIt_tilesVisible", "Blend It: letter tiles inside the window"),
    ("map_currentNodeVisible", "Map (16-letter name): current letter node inside the window"),
    ("map_buttonsAre64dp", "Map: back and guidebook buttons at least 64 x 64 dp"),
    ("map_unitBannerTitleFits", "Map: unit title inside the banner face"),
    ("complete_continueVisible", "Letter complete: Continue inside the window"),
    ("complete_continueVisible_fontScale13", "Letter complete at font scale 1.3: same"),
    ("splash_startVisible", "Splash: Start inside the window"),
    ("splash_startVisible_fontScale13", "Splash at font scale 1.3: Start inside, below Lily's face"),
    ("splash_lilyFaceClearOfStart", "Splash: panel, paws and Start below Lily's face"),
    ("nameprompt_letsPlayVisible", "New learner: Let's play inside the window"),
    ("nameprompt_letsPlayVisible_fontScale13", "New learner at font scale 1.3: same"),
])

# Layout comparison figures: (file, caption, screen captures, one row each).
FIGURE_SETS = [
    ("layout_splash_map.png", "Splash and map", ["splash", "map"]),
    ("layout_hearit_sayit.png", "Hear It and Say It", ["hearit", "sayit"]),
    ("layout_sayit_cue_findit.png", "Say It after two misses, and Find It", ["sayit_cue", "findit"]),
    ("layout_blendit_complete.png", "Blend It and letter complete", ["blendit", "complete"]),
]
CAPTURE_SUFFIX = OrderedDict([("Compact", "compact"), ("A21s", "a21s"), ("Phone", "phone"), ("Tablet", "tablet")])


class Run:
    """The recorded JUnit run: one entry per test case."""

    def __init__(self, folder: Path):
        self.cases: dict[tuple[str, str], dict] = {}
        self.suites: list[dict] = []
        for f in sorted(folder.glob("TEST-*.xml")):
            root = ET.parse(f).getroot()
            cls = root.get("name").rsplit(".", 1)[1]
            if cls not in CLASS_MAP:
                sys.exit(f"Error: test class {cls} is not in CLASS_MAP; add its feature and requirement.")
            start = datetime.fromisoformat(root.get("timestamp")).replace(tzinfo=timezone.utc)
            suite = {"cls": cls, "pkg": root.get("name"), "start": start, "time": float(root.get("time")),
                     "tests": int(root.get("tests")), "skipped": int(root.get("skipped")),
                     "failures": int(root.get("failures")), "errors": int(root.get("errors"))}
            self.suites.append(suite)
            for tc in root.iter("testcase"):
                status = "PASS"
                if tc.find("failure") is not None or tc.find("error") is not None:
                    status = "FAIL"
                elif tc.find("skipped") is not None:
                    status = "SKIP"
                self.cases[(cls, tc.get("name"))] = {"status": status, "time": float(tc.get("time"))}

    def count(self, status=None):
        return sum(1 for c in self.cases.values() if status is None or c["status"] == status)

    def methods(self, cls):
        return [m for (c, m) in self.cases if c == cls]


def zw(text: str) -> str:
    """Let long test names wrap in narrow table cells (zero-width space after _ and .)."""
    return text.replace("_", "_​").replace(".", ".​")


def tc_cells(run: Run, refs: str, partial: bool = False) -> str:
    shown, results = [], []
    for ref in (r.strip() for r in refs.split(",") if r.strip()):
        cls, meth = ref.split(".", 1)
        if cls == "LayoutMatrix*":
            for size in ("Compact", "A21s", "Phone", "Tablet"):
                key = (f"LayoutMatrix{size}Test", meth)
                if key not in run.cases:
                    sys.exit(f"Error: {ref} has no run on {size}")
                status = run.cases[key]["status"]
                if status == "SKIP" and not (size == "Compact" and "fontScale13" in meth):
                    sys.exit(f"Error: {ref} skipped on {size}")
                results.append(status)
            shown.append(f"`{zw('LayoutMatrixTest.' + meth)}` (4 sizes)")
            continue
        meths = run.methods(cls) if meth == "*" else [meth]
        if not meths:
            sys.exit(f"Error: no tests found for {ref}")
        for m in meths:
            if (cls, m) not in run.cases:
                sys.exit(f"Error: {cls}.{m} is cited in the STD but is not in the recorded run")
            results.append(run.cases[(cls, m)]["status"])
        shown.append(f"`{zw(cls)}` (all {len(meths)})" if meth == "*" else f"`{zw(cls + '.' + meth)}`")
    if "FAIL" in results:
        sys.exit(f"Error: a cited test failed: {refs}")
    passed, skipped = results.count("PASS"), results.count("SKIP")
    status = f"**PASS** ({passed} of {passed} runs)" if not skipped else \
        f"**PASS** ({passed} runs; {skipped} skipped by design on 360x640)"
    if partial:
        status = f"**PARTIAL** (the {passed} runs pass; scope incomplete)"
    return "<br>".join(shown) + " | " + status


def expand_ids(cell: str) -> list[str]:
    """'TC-ASR-01 to 05' -> 5 IDs; 'TC-PERF-01, 02' -> 2 IDs; 'TC-FND-03' -> 1 ID."""
    m = re.match(r"(TC-[A-Z]+)-(\d+) to (\d+)$", cell)
    if m:
        return [f"{m.group(1)}-{i:02d}" for i in range(int(m.group(2)), int(m.group(3)) + 1)]
    m = re.match(r"(TC-[A-Z]+)-(\d+), (\d+)$", cell)
    if m:
        return [f"{m.group(1)}-{m.group(2)}", f"{m.group(1)}-{m.group(3)}"]
    return [cell]


def tc_counts(text: str) -> dict:
    """Count the test cases of section 4 by status, from the filled-in tables."""
    sec = text[text.index("## 4. "):text.index("## 5. ")]
    not_run_at = sec.index("### 4.8")
    ids, out = set(), {"tc_total": 0, "tc_pass": 0, "tc_pass_inspection": 0, "tc_partial": 0,
                       "tc_fail": 0, "tc_notrun": 0}
    for m in re.finditer(r"^\| \*\*(TC-[^*]+)\*\* \|(.*)$", sec, re.M):
        cells = [c.strip() for c in m.group(2).split(" | ")]
        status = cells[-1].rstrip(" |")
        for tc in expand_ids(m.group(1)):
            if tc in ids:
                sys.exit(f"Error: test case {tc} is specified twice")
            ids.add(tc)
            out["tc_total"] += 1
            if m.start() > not_run_at:
                out["tc_notrun"] += 1
            elif "**FAIL" in status:
                out["tc_fail"] += 1
            elif "**PARTIAL" in status:
                out["tc_partial"] += 1
            elif "**PASS**" in status:
                out["tc_pass"] += 1
                out["tc_pass_inspection"] += "of 69" in status or "evidence/" in cells[-2]
            else:
                sys.exit(f"Error: no status for {tc}: {status[:60]}")
    return out


def run_summary(run: Run) -> str:
    start = min(s["start"] for s in run.suites).astimezone(PHT)
    last = max(run.suites, key=lambda s: s["start"])
    end = (last["start"] + timedelta(seconds=last["time"])).astimezone(PHT)
    busy = sum(s["time"] for s in run.suites)
    rows = [
        ("Test item", "PlayIT debug APK H: branch `refactor/hear-say-it`, app code at commit `0320385` "
                      "(the run used checkout `e2b6d4b`; no file under `app/` changed after `0320385`)"),
        ("Command", "`gradlew cleanTestDebugUnitTest testDebugUnitTest` (all results regenerated, nothing reused)"),
        ("Executed", f"{start:%B %d, %Y}, {start:%H:%M:%S} to {end:%H:%M:%S} (UTC+8); "
                     f"{busy:.1f} s inside test classes"),
        ("Executed by", "Claude (AI assistant of the team) on the team's Windows 11 development laptop"),
        ("Environment", "OpenJDK 17.0.16 (Eclipse Temurin); Gradle 8.9; Android Gradle Plugin 8.4.0; Kotlin 1.9.23; "
                        "JUnit 4.13.2; Robolectric 4.13 (Android SDK 34); Roborazzi 1.26.0; MockK 1.13.10; "
                        "kotlinx-coroutines-test 1.8.0"),
        ("Test classes", str(len(run.suites))),
        ("Test cases run", str(run.count())),
        ("Passed", str(run.count("PASS"))),
        ("Failed or errors", str(run.count("FAIL"))),
        ("Skipped", f"{run.count('SKIP')} (font-scale 1.3 checks on the 360x640 profile, by design: "
                    "that profile at 1.3 is below the supported floor)"),
        ("Raw logs", "`docs/std/evidence/2026-10-14-0320385/junit/` (66 JUnit XML files, as written by Gradle)"),
    ]
    return "| Item | Record |\n|---|---|\n" + "\n".join(f"| **{k}** | {v} |" for k, v in rows)


def class_table(run: Run) -> str:
    order = list(CLASS_MAP)
    lines = ["| Test class | Feature | Requirement | Tests | Result | Time (s) |", "|---|---|:---:|:---:|---|:---:|"]
    for s in sorted(run.suites, key=lambda s: order.index(s["cls"])):
        feature, req = CLASS_MAP[s["cls"]]
        bad = s["failures"] + s["errors"]
        passed = s["tests"] - s["skipped"] - bad
        result = f"{passed} passed" + (f", {s['skipped']} skipped" if s["skipped"] else "") + \
                 (f", **{bad} failed**" if bad else "")
        lines.append(f"| `{zw(s['cls'])}` | {feature} | {req} | {s['tests']} | {result} | {s['time']:.2f} |")
    t = run.count()
    lines.append(f"| **Total ({len(run.suites)} classes)** | | | **{t}** | **{run.count('PASS')} passed, "
                 f"{run.count('SKIP')} skipped, {run.count('FAIL')} failed** | "
                 f"{sum(s['time'] for s in run.suites):.2f} |")
    return "\n".join(lines)


def layout_table(run: Run) -> str:
    head = "| Layout check | What it asserts | " + " | ".join(SIZES.values()) + " |"
    lines = [head, "|---|---|:---:|:---:|:---:|:---:|"]
    for meth, what in LAYOUT_CHECKS.items():
        cells = []
        for size in SIZES:
            key = (f"LayoutMatrix{size}Test", meth)
            if key not in run.cases:
                sys.exit(f"Error: layout check {meth} has no run on {size}")
            st = run.cases[key]["status"]
            cells.append({"PASS": "Pass", "SKIP": "Skip*", "FAIL": "**FAIL**"}[st])
        lines.append(f"| `{zw(meth)}` | {what} | " + " | ".join(cells) + " |")
    return "\n".join(lines)


def test_log(run: Run) -> str:
    order = list(CLASS_MAP)
    lines = ["| # | Test class | Test case | Result | Time (s) |", "|:---:|---|---|:---:|:---:|"]
    n = 0
    for (cls, meth), c in sorted(run.cases.items(), key=lambda kv: (order.index(kv[0][0]), kv[0][1])):
        n += 1
        lines.append(f"| {n} | `{zw(cls)}` | `{zw(meth)}` | {c['status'].capitalize()} | {c['time']:.3f} |")
    return "\n".join(lines)


def counts(run: Run) -> dict:
    layout = [k for k in run.cases if k[0].startswith("LayoutMatrix")]
    return {
        "tests": run.count(), "passed": run.count("PASS"), "skipped": run.count("SKIP"),
        "failed": run.count("FAIL"), "classes": len(run.suites), "layout_runs": len(layout),
        "layout_passed": sum(1 for k in layout if run.cases[k]["status"] == "PASS"),
        "layout_checks": len(LAYOUT_CHECKS),
    }


def build_figures():
    from PIL import Image, ImageDraw, ImageFont
    if not ROBORAZZI.exists():
        sys.exit(f"Error: no captures in {ROBORAZZI}; run recordRoborazziDebug first.")
    FIGURES.mkdir(parents=True, exist_ok=True)
    scale = 0.55          # pixels per dp, so the four sizes keep their true proportions
    gap, label_h, pad = 28, 44, 24
    try:
        font = ImageFont.truetype("arial.ttf", 22)
        bold = ImageFont.truetype("arialbd.ttf", 24)
    except OSError:
        font = bold = ImageFont.load_default()
    for fname, _caption, screens in FIGURE_SETS:
        tiles = []
        for screen in screens:
            row = []
            for size, suffix in CAPTURE_SUFFIX.items():
                src = ROBORAZZI / f"{screen}_{suffix}.png"
                if not src.exists():
                    sys.exit(f"Error: missing capture {src.name}")
                w_dp, h_dp = (int(v) for v in SIZES[size].split("x"))
                img = Image.open(src).convert("RGB").resize((round(w_dp * scale), round(h_dp * scale)),
                                                            Image.LANCZOS)
                row.append((size, img))
            tiles.append((screen, row))
        row_w = sum(img.width for _, img in tiles[0][1]) + gap * 3
        width = row_w + pad * 2
        heights = [label_h + max(img.height for _, img in row) for _, row in tiles]
        canvas = Image.new("RGB", (width, sum(heights) + gap * (len(tiles) - 1) + pad * 2), "white")
        draw = ImageDraw.Draw(canvas)
        y = pad
        for (screen, row), h in zip(tiles, heights):
            x = pad
            for size, img in row:
                draw.text((x, y + 8), f"{size} {SIZES[size]}", fill=(45, 55, 72), font=bold if size == "A21s" else font)
                top = y + label_h
                canvas.paste(img, (x, top))
                draw.rectangle([x - 1, top - 1, x + img.width, top + img.height], outline=(200, 208, 216), width=2)
                x += img.width + gap
            y += h + gap
        canvas.save(FIGURES / fname, optimize=True)
        print(f"[OK] figure {fname} ({canvas.width}x{canvas.height})")


def main():
    if "--figures" in sys.argv:
        build_figures()
    run = Run(JUNIT)
    numbers = counts(run)
    text = TEMPLATE.read_text(encoding="utf-8")
    text = re.sub(r"\{\{tc(\(PARTIAL\))?:(.+?)\}\}", lambda m: tc_cells(run, m.group(2), bool(m.group(1))), text)
    blocks = {"RUN_SUMMARY": run_summary(run), "CLASS_TABLE": class_table(run),
              "LAYOUT_TABLE": layout_table(run), "TEST_LOG": test_log(run)}
    for key, value in blocks.items():
        text = text.replace("{{" + key + "}}", value)
    numbers.update(tc_counts(text))
    text = re.sub(r"\{\{COUNT:(\w+)\}\}", lambda m: str(numbers[m.group(1)]), text)
    left = re.findall(r"\{\{[^}]*\}\}", text)
    if left:
        sys.exit(f"Error: unfilled placeholders: {left[:5]}")
    OUT.write_text(text, encoding="utf-8", newline="\n")
    print(f"[OK] {OUT.relative_to(REPO)}: {numbers['tests']} tests, {numbers['passed']} passed, "
          f"{numbers['skipped']} skipped, {numbers['failed']} failed")


if __name__ == "__main__":
    main()
