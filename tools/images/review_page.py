"""playIT image review rounds: the page the user picks from, and the state agy reads between rounds.

Standard library only, so agy can run it on Windows (python) and Claude in WSL (python3).

Batch folder layout (outside the repo, e.g. C:\\Users\\riva.zn\\Documents\\playIT-image-batches\\<batch>\\):
  items.json                      copied from docs/assets/briefs/<batch>/items.json
  round-01\\<item>__v1.png ...    agy's generations for round 1 (only items still open)
  round-01\\generation_log.jsonl  one line per image: round, item, variant, prompt, references, model, time
  round-01\\index.html            written by this tool
  <batch>_round-01_review.csv     exported by the user from the page (Downloads or the batch folder)
  picks.json                      written by --status once every item has a pick

Commands:
  python tools/images/review_page.py <batch_dir> --round N [--app <repo>/app/src/main/assets/images/pictures]
      Writes round-NN/index.html for the items still open. --app shows the image the app uses today.
  python tools/images/review_page.py <batch_dir> --status
      Prints JSON {"picked": {...}, "open": [{"id", "notes": [...], "closest": file or null}], "next_round": N}
      and writes picks.json when nothing is open. agy uses this to build the next round's prompts.

The CSV has one row per item: item, verdict (PICK or FIX), pick (file, for PICK), closest (file the user
liked best, for FIX), note.
"""
import argparse
import csv
import html
import json
import os
import pathlib
import shutil
import sys


def load_items(batch):
    return json.loads((batch / "items.json").read_text(encoding="utf-8"))


def round_dir(batch, n):
    return batch / f"round-{n:02d}"


def find_csv(batch, n):
    name = f"{batch.name}_round-{n:02d}_review.csv"
    places = [round_dir(batch, n), batch, pathlib.Path.home() / "Downloads"]
    if os.environ.get("USERPROFILE"):
        places.append(pathlib.Path(os.environ["USERPROFILE"]) / "Downloads")
    for p in places:
        f = p / name
        if f.exists():
            return f
    return None


def read_rounds(batch):
    """Every round with a review CSV: list of (round, {item: row})."""
    out, n = [], 1
    while round_dir(batch, n).exists():
        f = find_csv(batch, n)
        if f:
            with open(f, encoding="utf-8-sig", newline="") as fh:
                out.append((n, {r["item"]: r for r in csv.DictReader(fh)}))
        n += 1
    return out


def status(batch):
    items = load_items(batch)["items"]
    rounds = read_rounds(batch)
    picked, open_items = {}, []
    for it in items:
        notes, closest = [], None
        for n, rows in rounds:
            r = rows.get(it["id"])
            if not r:
                continue
            if r.get("verdict") == "PICK" and r.get("pick"):
                picked[it["id"]] = f"round-{n:02d}/{r['pick']}"
            elif r.get("verdict") == "FIX":
                picked.pop(it["id"], None)
                if r.get("note"):
                    notes.append(f"round {n}: {r['note']}")
                if r.get("closest"):
                    closest = f"round-{n:02d}/{r['closest']}"
        if it["id"] not in picked:
            open_items.append({"id": it["id"], "word": it["word"], "notes": notes, "closest": closest})
    last = max([n for n, _ in rounds], default=0)
    # The next round is the first round folder without a review CSV, or a new one.
    n = 1
    while round_dir(batch, n).exists() and find_csv(batch, n):
        n += 1
    result = {"picked": picked, "open": open_items, "next_round": n, "last_reviewed_round": last}
    if not open_items:
        (batch / "picks.json").write_text(json.dumps(picked, indent=1) + "\n", encoding="utf-8")
        result["picks_json"] = str(batch / "picks.json")
    return result


def write_page(batch, n, app_folder=None):
    spec = load_items(batch)
    st = status(batch)
    open_ids = {o["id"]: o for o in st["open"]}
    rd = round_dir(batch, n)
    if not rd.exists():
        sys.exit(f"{rd} does not exist; generate the round first")
    cur = batch / "_current"
    cards = []
    for it in spec["items"]:
        if it["id"] not in open_ids:
            continue
        variants = sorted(p.name for p in rd.glob(f"{it['id']}__v*.png"))
        if not variants:
            continue
        now = ""
        if app_folder:
            src = pathlib.Path(app_folder) / f"{it['id']}.png"
            if src.exists():
                cur.mkdir(exist_ok=True)
                if not (cur / src.name).exists():
                    shutil.copyfile(src, cur / src.name)
                now = f'<figure class="now"><img src="../_current/{html.escape(src.name)}"><figcaption>now in the app</figcaption></figure>'
        notes = "".join(f"<li>{html.escape(x)}</li>" for x in open_ids[it["id"]]["notes"])
        vs = "".join(
            f'''<figure class="v"><img class="big" src="{html.escape(v)}">
                <div class="small"><img src="{html.escape(v)}"></div>
                <label><input type="radio" name="{it['id']}" value="PICK:{html.escape(v)}"> Pick this</label>
                <label class="cl"><input type="checkbox" data-closest="{it['id']}" value="{html.escape(v)}"> closest</label>
                <figcaption>{html.escape(v)}</figcaption></figure>''' for v in variants)
        cards.append(f'''<section class="item" data-item="{it['id']}">
          <h2>{html.escape(it['word'])} <span>letter {html.escape(it['letter'])} &middot; {html.escape(it['id'])}</span></h2>
          <p class="why">{html.escape(it['why'])}</p><p class="subj">Should show: {html.escape(it['subject'])}</p>
          {f'<ul class="notes">{notes}</ul>' if notes else ''}
          <div class="row">{now}{vs}</div>
          <label><input type="radio" name="{it['id']}" value="FIX"> None of these: make new ones</label>
          <textarea data-note="{it['id']}" placeholder="What to change (needed if you choose None of these)"></textarea>
        </section>''')
    batch_id = f"{batch.name}_round-{n:02d}"
    page = f"""<!doctype html><meta charset="utf-8"><title>{html.escape(batch_id)}</title>
<style>
body {{ font-family: system-ui, sans-serif; margin: 24px; color: #1d2b36; background: #f6f8fa; }}
.bar {{ position: sticky; top: 0; background: #fff; padding: 10px; border-bottom: 2px solid #1d2b36; z-index: 2; }}
button {{ font-size: 1em; padding: 6px 14px; margin-right: 8px; }}
.item {{ background: #fff; border-radius: 12px; padding: 12px 16px; margin: 14px 0; box-shadow: 0 1px 3px #0002; }}
.item.done {{ outline: 3px solid #2d6a4f; }}
h2 span {{ font-size: .6em; color: #667; font-weight: normal; }}
.why {{ color: #8a5a00; margin: 2px 0; }} .subj {{ margin: 2px 0 8px; }}
.notes {{ color: #5a3d99; margin: 4px 0; }}
.row {{ display: flex; gap: 18px; flex-wrap: wrap; align-items: flex-start; }}
figure {{ margin: 0; text-align: center; }}
.big {{ width: 256px; height: 256px; background: #fff; border: 1px solid #dde3e8; border-radius: 8px; object-fit: contain; }}
.small {{ width: 120px; height: 120px; margin: 6px auto; background: #dff1ff; border-radius: 16px; display: flex; align-items: center; justify-content: center; }}
.small img {{ width: 96px; height: 96px; object-fit: contain; background: #fff; border-radius: 12px; }}
.now img {{ width: 128px; height: 128px; object-fit: contain; opacity: .85; border: 1px dashed #aab; border-radius: 8px; }}
figcaption {{ font-size: .8em; color: #667; }}
textarea {{ width: 100%; max-width: 640px; height: 40px; margin-top: 6px; }}
label {{ display: block; margin-top: 4px; }} label.cl {{ font-size: .85em; color: #667; }}
</style>
<div class="bar"><b>{html.escape(spec['batch'])}, round {n}</b>. For each picture: pick one, or choose
"None of these" and say what to change (tick "closest" on the one that came nearest). The small preview is
the size a child sees in Find It. Answers save in this browser. <br>
<button id="export">Export CSV</button><button id="clear">Clear my answers</button><span id="status"></span></div>
{''.join(cards) or '<p>Nothing open in this round.</p>'}
<script>
const BATCH = {json.dumps(batch_id)};
const KEY = "playit-images-" + BATCH;
let A = JSON.parse(localStorage.getItem(KEY) || "{{}}");
function save() {{
  localStorage.setItem(KEY, JSON.stringify(A));
  const items = [...document.querySelectorAll(".item")];
  let done = 0;
  items.forEach(s => {{ const a = A[s.dataset.item] || {{}}; const ok = a.v && (a.v !== "FIX" || a.note);
    s.classList.toggle("done", !!ok); if (ok) done++; }});
  document.getElementById("status").textContent = done + " of " + items.length + " answered";
}}
document.querySelectorAll("input[type=radio]").forEach(r => {{
  const a = A[r.name] || {{}}; if (a.v === r.value) r.checked = true;
  r.addEventListener("change", () => {{ A[r.name] = Object.assign(A[r.name] || {{}}, {{v: r.value}}); save(); }});
}});
document.querySelectorAll("input[data-closest]").forEach(c => {{
  const id = c.dataset.closest; const a = A[id] || {{}}; if (a.closest === c.value) c.checked = true;
  c.addEventListener("change", () => {{
    document.querySelectorAll(`input[data-closest="${{id}}"]`).forEach(o => {{ if (o !== c) o.checked = false; }});
    A[id] = Object.assign(A[id] || {{}}, {{closest: c.checked ? c.value : ""}}); save(); }});
}});
document.querySelectorAll("textarea[data-note]").forEach(t => {{
  const id = t.dataset.note; t.value = (A[id] || {{}}).note || "";
  t.addEventListener("input", () => {{ A[id] = Object.assign(A[id] || {{}}, {{note: t.value}}); save(); }});
}});
function q(s) {{ return '"' + String(s || "").replace(/"/g, '""') + '"'; }}
document.getElementById("export").onclick = () => {{
  const lines = ["item,verdict,pick,closest,note"];
  document.querySelectorAll(".item").forEach(s => {{
    const a = A[s.dataset.item] || {{}}; if (!a.v) return;
    const pick = a.v.startsWith("PICK:") ? a.v.slice(5) : "";
    lines.push([s.dataset.item, pick ? "PICK" : "FIX", pick, a.closest || "", a.note || ""].map(q).join(","));
  }});
  const blob = new Blob(["\\ufeff" + lines.join("\\r\\n") + "\\r\\n"], {{type: "text/csv"}});
  const el = document.createElement("a"); el.href = URL.createObjectURL(blob);
  el.download = BATCH + "_review.csv"; el.click();
}};
document.getElementById("clear").onclick = () => {{ if (confirm("Clear all answers?")) {{ A = {{}}; save(); location.reload(); }} }};
save();
</script>"""
    (rd / "index.html").write_text(page, encoding="utf-8")
    return rd / "index.html", len(cards)


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("batch")
    ap.add_argument("--round", type=int)
    ap.add_argument("--app", help="folder with the images the app uses today")
    ap.add_argument("--status", action="store_true")
    a = ap.parse_args(argv)
    batch = pathlib.Path(a.batch)
    if a.status:
        print(json.dumps(status(batch), indent=1))
        return 0
    if not a.round:
        ap.error("--round or --status is required")
    page, n = write_page(batch, a.round, a.app)
    print(f"Wrote {page} with {n} open items. Open it in a browser.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
