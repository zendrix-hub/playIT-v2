"""Mechanical part of Claude's review of an agy card commit (docs/tasks/AGY_RUNBOOK.md "Self-check").

Checks one commit against its card and prints PASS / WARN / FAIL lines:
  files      every changed file is in the card's Files list or is a bookkeeping file
  tests      every test name in the card's Tests section exists in the test tree at that commit
  status     the card says `Status: done` at that commit; 13_MASTER_TASKS.md ticks it; evidence-log has a row
  body       the commit body has Card / Requirement / Tests run / Decisions used
  assets     every added or changed file under app/src/main/assets/ matches a release manifest SHA-256
  refs       no deleted asset's file name is still written in app/src/main code at that commit
             (catches lists the card forgot, like the stale AudioCompletenessCheck after card 26)
  hash       the evidence-log row (in the working tree) has the commit hash; agy can't write its own
             hash, so this is a reminder for the reviewer to fill it in at acceptance
  emoji      no emoji in added lines (Zero-Emoji Policy)
The judgement part (does the code do what the card says, is it correct) stays with the reviewer.

Usage (repo root):  python3 tools/dev/review_card.py 07 [--commit <hash>]
Without --commit it takes the newest commit whose body says "Card: 07".
Exit code 1 if any FAIL.
"""
import argparse
import fnmatch
import hashlib
import json
import pathlib
import re
import subprocess
import sys

REPO = pathlib.Path(__file__).resolve().parents[2]
MAIN = "app/src/main/java/com/playit/app/"
TEST = "app/src/test/java/com/playit/app/"
BOOKKEEPING = {"docs/engineering-package/13_MASTER_TASKS.md", "docs/evidence-log.md", "docs/tasks/SESSION_HANDOFF.md"}
EMOJI = re.compile("[\U0001F000-\U0001FAFF\u2600-\u27BF\u2300-\u23FF\u2B00-\u2BFF\uFE0F]")


def git(*args):
    return subprocess.run(["git", *args], cwd=REPO, capture_output=True, text=True, encoding="utf-8",
                          errors="replace", check=True).stdout


def card_file(nn):
    hits = sorted((REPO / "docs/tasks").glob(f"card-{nn}-*.md"))
    if not hits:
        sys.exit(f"no docs/tasks/card-{nn}-*.md")
    return hits[0]


def section(text, title):
    m = re.search(rf"^## {title}.*?$(.*?)(?=^## |\Z)", text, re.M | re.S)
    return m.group(1) if m else ""


def card_paths(text):
    """Repo-relative paths (or glob patterns, where the card writes <id>) from the Files section."""
    paths = set()
    body = section(text, "Files")
    list_folder = None  # "- New tests, all under `app/.../screenshot/`:" then indented "  - `ATest.kt`" lines
    for line in body.splitlines():
        if line.startswith("- "):
            list_folder = None
        folder = None  # "Add: `app/.../a.wav`, `b.wav`": a bare file name shares the folder of the path before it
        for tok in re.findall(r"`([^`]+)`", line):
            tok = re.sub(r"<[^>]+>", "*", tok.strip())
            if " " in tok:
                continue
            if tok.endswith("/") and tok.startswith(("app/", "docs/", "tools/")):
                list_folder = tok.rstrip("/")
            elif tok.startswith(("app/", "docs/", "tools/", ".github/", "gradle/")) or tok in ("build.gradle.kts", "settings.gradle.kts"):
                paths.add(tok)
                folder = tok.rsplit("/", 1)[0] if "/" in tok else None
            elif "/" not in tok and (folder or list_folder) and "." in tok:
                paths.add(f"{folder or list_folder}/{tok}")
            elif tok.endswith(".kt"):
                paths.add((TEST if tok.endswith("Test.kt") else MAIN) + tok)
    return paths


def is_allowed(path, allowed):
    return any(fnmatch.fnmatchcase(path, pat) for pat in allowed)


def card_tests(text):
    """Test names from the Tests section: the backticked names in each table's "Test" column."""
    names, col = set(), None
    for line in section(text, "Tests").splitlines():
        if not line.startswith("|"):
            col = None
            continue
        cells = [c.strip() for c in line.strip().strip("|").split("|")]
        if col is None:
            col = next((i for i, c in enumerate(cells) if c.lower() == "test"), None)
            continue
        if set(line.replace("|", "").strip()) <= set("-: ") or col >= len(cells):
            continue
        names.update(t for t in re.findall(r"`([A-Za-z][A-Za-z0-9_]*)`", cells[col]))
    return names


def find_commit(nn):
    out = git("log", "--format=%H", "-E", f"--grep=^Card: {nn}([^0-9a-z]|$)", "-n", "1")
    return out.strip() or None


def sha256_bytes(data):
    return hashlib.sha256(data).hexdigest()


def release_hashes():
    hashes = {}
    for kind in ("audio-release", "image-release"):
        for man in (REPO / "docs" / kind).glob("*/manifest.json"):
            m = json.loads(man.read_text(encoding="utf-8"))
            for entry in m.get("clips", []) + m.get("images", []):
                hashes[entry["sha256"]] = f"{man.parent.name}/{entry['file']}"
    return hashes


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("card", help="card number, e.g. 07 or 07b")
    ap.add_argument("--commit")
    a = ap.parse_args(argv)
    nn = a.card
    cf = card_file(nn)
    rel_card = cf.relative_to(REPO).as_posix()
    commit = a.commit or find_commit(nn)
    if not commit:
        sys.exit(f"no commit with 'Card: {nn}' in its body; pass --commit")
    results = []

    def say(level, what, msg):
        results.append(level)
        print(f"{level:4s}  {what:7s} {msg}")

    print(f"Card {nn}: {rel_card}\nCommit: {git('log', '-1', '--format=%h %s', commit).strip()}\n")
    text_at = git("show", f"{commit}:{rel_card}")

    # files
    allowed = card_paths(text_at) | BOOKKEEPING | {rel_card}
    changed = [l.split("\t") for l in git("show", "--name-status", "--format=", commit).splitlines() if l.strip()]
    extra = [p for st, *ps in changed for p in ps[-1:] if not is_allowed(p, allowed)]
    if extra:
        say("FAIL", "files", "not in the card's Files list: " + ", ".join(extra))
    else:
        say("PASS", "files", f"{len(changed)} files, all listed or bookkeeping")
    missing = sorted(p for p in card_paths(text_at) if p.endswith(".kt") and "*" not in p and p not in {c[-1] for c in changed})
    if missing:
        say("WARN", "files", "listed but not changed: " + ", ".join(missing))

    # tests
    names = card_tests(text_at)
    tree = git("grep", "-h", "-E", r"fun [`]?[A-Za-z0-9_]+", commit, "--", "app/src/test")
    have = set(re.findall(r"fun `?([A-Za-z0-9_]+)", tree))
    absent = sorted(n for n in names if n not in have)
    if absent:
        say("FAIL", "tests", "missing: " + ", ".join(absent))
    else:
        say("PASS", "tests", f"all {len(names)} card tests exist")

    # status and bookkeeping
    m = re.search(r"^Status:\s*(\S+)", text_at, re.M)
    say("PASS" if m and m.group(1) == "done" else "FAIL", "status", f"card says Status: {m.group(1) if m else '?'}")
    tasks = git("show", f"{commit}:docs/engineering-package/13_MASTER_TASKS.md")
    tick = re.search(rf"^- \[(x| )\] Card {re.escape(nn)}:", tasks, re.M)
    say("PASS" if tick and tick.group(1) == "x" else "FAIL", "status", "13_MASTER_TASKS ticked" if tick and tick.group(1) == "x" else "13_MASTER_TASKS not ticked")
    log = git("show", f"{commit}:docs/evidence-log.md")
    say("PASS" if re.search(rf"^\| {re.escape(nn)}\b", log, re.M) else "FAIL", "status", "evidence-log row")

    # body
    body = git("log", "-1", "--format=%B", commit)
    lacking = [k for k in ("Card:", "Requirement:", "Tests run:", "Decisions used:") if k not in body]
    say("FAIL" if lacking else "PASS", "body", ("missing " + ", ".join(lacking)) if lacking else "Card / Requirement / Tests run / Decisions used present")
    if "Tests run: CI only" in body or "not run locally" in body:
        say("WARN", "body", "tests not run locally; CI decides")

    # assets
    hashes = release_hashes()
    for st, *ps in changed:
        p = ps[-1]
        if p.startswith("app/src/main/assets/") and st[0] in "AM":
            data = subprocess.run(["git", "show", f"{commit}:{p}"], cwd=REPO, capture_output=True, check=True).stdout
            src = hashes.get(sha256_bytes(data))
            say("PASS" if src else "FAIL", "assets", f"{p} = {src}" if src else f"{p} matches no release manifest")

    # refs: deleted assets still named in app code. Match the path under assets/; match the bare file
    # name only when no asset of that name is left (code often lists bare names and adds the folder).
    gone = sorted({ps[0] for st, *ps in changed if st[0] in "DR" and ps[0].startswith("app/src/main/assets/")})
    if gone:
        left = {p.rsplit("/", 1)[-1] for p in git("ls-tree", "-r", "--name-only", commit, "app/src/main/assets").splitlines()}
        stale = set()
        for path in gone:
            rel, name = path[len("app/src/main/assets/"):], path.rsplit("/", 1)[-1]
            for needle in [rel] + ([name] if name not in left else []):
                hits = subprocess.run(["git", "grep", "-n", "-F", needle, commit, "--", "app/src/main/java", "app/src/main/res"],
                                      cwd=REPO, capture_output=True, text=True).stdout
                stale.update(h.split(":", 1)[1] for h in hits.splitlines())
        if stale:
            files = sorted({h.split(":", 1)[0] for h in stale})
            say("FAIL", "refs", f"{len(stale)} lines still name a deleted asset, in: " + ", ".join(files)
                + " (if the card didn't list the file, write a fix card)")
        else:
            say("PASS", "refs", f"none of the {len(gone)} deleted assets is named in app code")

    # hash: the reviewer fills the commit hash into the evidence-log row at acceptance
    short = git("rev-parse", "--short=7", commit).strip()
    row = next((l for l in (REPO / "docs/evidence-log.md").read_text(encoding="utf-8").splitlines()
                if re.match(rf"\| {re.escape(nn)}\b", l)), None)
    if row and short not in row:
        say("WARN", "hash", f"evidence-log row has no hash yet: at acceptance write {short} and the CI run")

    # emoji in added lines
    diff = git("show", "--format=", "-U0", commit, "--", "app/")
    bad = [l for l in diff.splitlines() if l.startswith("+") and not l.startswith("+++") and EMOJI.search(l)]
    say("FAIL" if bad else "PASS", "emoji", (f"{len(bad)} added lines with emoji: " + bad[0][:80]) if bad else "no emoji added")

    print(f"\nNext: tools/dev/gradlew_wsl.sh testDebugUnitTest, tools/dev/ci_status.sh, then read the diff against the card.")
    return 1 if "FAIL" in results else 0


if __name__ == "__main__":
    sys.exit(main())
