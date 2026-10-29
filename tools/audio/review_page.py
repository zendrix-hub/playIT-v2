"""playIT listening review page (shared by voice_candidates.py and later full-pack batches).

write_review_page() writes index.html next to the clips. The listener plays each clip, answers
in the page, and clicks Export. Answers auto-save in the browser (localStorage, keyed by batch
id), so closing the tab loses nothing. Export downloads <batch>_review.csv to the browser's
Downloads folder, where Claude reads it.

Modes:
  "score"  1-5 per clip plus a note (voice candidates)
  "okfix"  OK or FIX per clip plus a note (release review; blank means not approved)
"""
import html, json, pathlib

def write_review_page(out, batch_id, title, intro, rows, mode="score"):
    """rows: dicts with file, group, says, listen_for, auto_check (all strings)."""
    assert mode in ("score", "okfix")
    data = json.dumps(rows, ensure_ascii=False).replace("</", "<\\/")
    page = f"""<!doctype html>
<meta charset="utf-8">
<title>{html.escape(title)}</title>
<style>
body {{ font-family: system-ui, sans-serif; margin: 24px; color: #1d2b36; }}
table {{ border-collapse: collapse; width: 100%; }}
th, td {{ padding: 6px 8px; border-bottom: 1px solid #dde3e8; text-align: left; vertical-align: top; }}
tr.group th {{ background: #eef3f7; font-size: 1.05em; }}
td.says {{ max-width: 280px; }}
td.auto {{ color: #8a5a00; font-size: .9em; }}
td.auto.clean {{ color: #2d6a4f; }}
textarea {{ width: 220px; height: 34px; }}
.bar {{ position: sticky; top: 0; background: #fff; padding: 8px 0; border-bottom: 2px solid #1d2b36; margin-bottom: 8px; }}
button {{ font-size: 1em; padding: 6px 14px; margin-right: 8px; }}
#status {{ color: #555; }}
</style>
<h1>{html.escape(title)}</h1>
<p>{html.escape(intro)}</p>
<div class="bar">
  <button id="export">Export CSV</button>
  <button id="clear">Clear my answers</button>
  <span id="status"></span>
</div>
<table id="t"></table>
<script>
const BATCH = {json.dumps(batch_id)};
const MODE = {json.dumps(mode)};
const ROWS = {data};
const KEY = "playit-review-" + BATCH;
let answers = JSON.parse(localStorage.getItem(KEY) || "{{}}");

function save() {{
  localStorage.setItem(KEY, JSON.stringify(answers));
  const done = ROWS.filter(r => (answers[r.file] || {{}}).mark).length;
  document.getElementById("status").textContent = done + " of " + ROWS.length + " answered, saved in this browser";
}}

function control(r) {{
  const a = answers[r.file] || {{}};
  const opts = MODE === "score" ? ["", "1", "2", "3", "4", "5"] : ["", "OK", "FIX"];
  const sel = document.createElement("select");
  opts.forEach(o => {{ const e = document.createElement("option"); e.value = o; e.textContent = o || "-"; sel.appendChild(e); }});
  sel.value = a.mark || "";
  sel.onchange = () => {{ answers[r.file] = Object.assign(answers[r.file] || {{}}, {{mark: sel.value}}); save(); }};
  const note = document.createElement("textarea");
  note.value = a.note || "";
  note.placeholder = "note";
  note.oninput = () => {{ answers[r.file] = Object.assign(answers[r.file] || {{}}, {{note: note.value}}); save(); }};
  return [sel, note];
}}

function build() {{
  const t = document.getElementById("t");
  const head = MODE === "score" ? "Score 1-5" : "OK or FIX";
  let group = null;
  ROWS.forEach(r => {{
    if (r.group !== group) {{
      group = r.group;
      const g = t.insertRow(); g.className = "group";
      const th = document.createElement("th"); th.colSpan = 6; th.textContent = group; g.appendChild(th);
      const h = t.insertRow();
      ["Clip", "Says", "Listen for", "Auto check", head, "Note"].forEach(x => {{ const c = document.createElement("th"); c.textContent = x; h.appendChild(c); }});
    }}
    const tr = t.insertRow();
    const au = document.createElement("audio"); au.controls = true; au.preload = "none"; au.src = r.file;
    tr.insertCell().appendChild(au);
    const s = tr.insertCell(); s.className = "says"; s.textContent = r.says;
    tr.insertCell().textContent = r.listen_for;
    const ac = tr.insertCell(); ac.className = "auto" + (r.auto_check.startsWith("clean") ? " clean" : ""); ac.textContent = r.auto_check;
    const [sel, note] = control(r);
    tr.insertCell().appendChild(sel);
    tr.insertCell().appendChild(note);
  }});
  save();
}}

function csvCell(v) {{ return '"' + String(v || "").replace(/"/g, '""') + '"'; }}

document.getElementById("export").onclick = () => {{
  const cols = ["file", "group", "says", "listen_for", "auto_check", MODE === "score" ? "score_1_to_5" : "OK_or_FIX", "note"];
  const lines = [cols.join(",")];
  ROWS.forEach(r => {{
    const a = answers[r.file] || {{}};
    lines.push([r.file, r.group, r.says, r.listen_for, r.auto_check, a.mark, a.note].map(csvCell).join(","));
  }});
  const blob = new Blob(["\\ufeff" + lines.join("\\r\\n")], {{type: "text/csv"}});
  const link = document.createElement("a");
  link.href = URL.createObjectURL(blob);
  link.download = BATCH + "_review.csv";
  link.click();
}};

document.getElementById("clear").onclick = () => {{
  if (confirm("Clear all answers on this page?")) {{ answers = {{}}; localStorage.removeItem(KEY); location.reload(); }}
}};

build();
</script>
"""
    (pathlib.Path(out) / "index.html").write_text(page, encoding="utf-8")
