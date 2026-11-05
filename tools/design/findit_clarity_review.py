"""Builds review page 2026-11-05-findit-clarity: every Find It picture, rated for whether a 5 to 7 year old in the
Philippines would name it with the right first sound once the word under it is gone (adviser, 2026-11-05: "no labels
on pictures ... make sure ... the child can easily understand what the images says").

For each picture the user picks one choice; the page exports a CSV. Nothing here changes the app.

Usage: python -I findit_clarity_review.py <out dir>
"""
import html
import json
import pathlib
import re
import shutil
import sys

REPO = pathlib.Path(r"C:\Users\Erik\Desktop\playIT-v2")
OUT = pathlib.Path(sys.argv[1])
(OUT / "img").mkdir(parents=True, exist_ok=True)

src = (REPO / "app/src/main/java/com/playit/app/domain/manager/GridGenerator.kt").read_text(encoding="utf-8")
banks = {m.group(1): re.findall(r'"([^"]+)" to "([^"]+)"', m.group(2))
         for m in re.finditer(r'"(\w+|ñ)" to listOf\((.*?)\)\s*,?\n', src)}
ORDER = ["m", "s", "a", "i", "o", "b", "e", "u", "t", "k", "l", "y", "n", "g", "p", "r", "d", "h", "w", "c", "f", "j",
         "q", "v", "x", "z"]

# word: (rating, what a child would likely call it, swap to an already-released picture or None, new-art idea or None)
RATINGS = {
    "Insect": ("unclear", "ladybug or bug", None, None),
    "Ink": ("unclear", "bottle", None, None),
    "Ox": ("unclear", "cow or carabao", None, "octopus"),
    "Up": ("unclear", "balloon", None, None),
    "Uncle": ("unclear", "boy, man or kuya", None, None),
    "Kit": ("unclear", "medicine box or bag", ("King", "picture_king"), None),
    "Lit": ("unclear", "candle (and lit is not a thing)", None, "lamp"),
    "Yak": ("unclear", "cow or bull", None, None),
    "Gap": ("unclear", "fence", None, "guitar"),
    "Draw": ("unclear", "book or crayon (and draw is an action)", ("Drum", "picture_drum"), None),
    "Jet": ("unclear", "airplane", None, "jeep"),
    "Quiz": ("unclear", "paper or checklist", None, None),
    "Quilt": ("unclear", "blanket", None, None),
    "Hen": ("unclear", "chicken or manok", None, None),
    "Mat": ("unsure", "rug, towel or banig", ("Mom", "blendword_mom"), None),
    "Web": ("unsure", "spider web (a /s/ word)", ("Wing", "picture_wing"), None),
    "Axe": ("unsure", "hammer or palakol", None, None),
    "Igloo": ("unsure", "few children here have seen one", None, None),
    "Envelope": ("unsure", "letter or mail", None, None),
    "Top": ("unsure", "trumpo", None, None),
    "Yarn": ("unsure", "thread or ball", None, None),
    "Pin": ("unsure", "safety pin may be unfamiliar", None, None),
    "Jug": ("unsure", "pitsel", None, None),
    "Queen": ("unsure", "princess", None, None),
    "Vase": ("unsure", "flowers", None, None),
    "Nut": ("unsure", "acorn, not a Philippine nut", None, None),
    "Fox": ("unsure", "dog", None, None),
}

rows = []
for letter in ORDER:
    for word, path in banks[letter]:
        stem = pathlib.Path(path).stem
        shutil.copyfile(REPO / "app/src/main/assets" / path, OUT / "img" / f"{stem}.png")
        rating, likely, swap, art = RATINGS.get(word, ("clear", "", None, None))
        if swap:
            shutil.copyfile(REPO / "app/src/main/assets/images/pictures" / f"{swap[1]}.png", OUT / "img" / f"{swap[1]}.png")
        rows.append({"letter": letter, "word": word, "img": f"img/{stem}.png", "rating": rating, "likely": likely,
                     "swap": swap, "art": art, "x": letter == "x"})

data = json.dumps(rows, ensure_ascii=False).replace("</", "<\\/")
page = """<!doctype html>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Find It pictures without words</title>
<style>
:root { --ink:#3b2416; --muted:#6e5643; --line:#e2d3bc; --card:#fffdf8; --bg:#fff7ea;
        --clear:#d8f0dc; --clearInk:#1b5e28; --unsure:#ffefd1; --unsureInk:#7a4f00; --unclear:#fbe3df; --unclearInk:#9a3b2b; }
body { margin:0; background:var(--bg); color:var(--ink); font:15px/1.45 system-ui, sans-serif; }
main { max-width:1100px; margin:0 auto; padding:16px; }
h1 { font-size:24px; margin:8px 0 4px; } p { margin:6px 0; color:var(--muted); }
.bar { position:sticky; top:0; background:var(--bg); padding:10px 0; border-bottom:2px solid var(--ink); z-index:2; display:flex; gap:10px; flex-wrap:wrap; align-items:center; }
button { font:inherit; padding:8px 14px; border-radius:10px; border:2px solid var(--ink); background:#0b7f7a; color:#fff; cursor:pointer; }
button.alt { background:var(--card); color:var(--ink); }
#status { color:var(--muted); }
.letter { margin:18px 0 6px; font-size:20px; font-weight:800; }
.grid { display:grid; grid-template-columns:repeat(auto-fill, minmax(230px, 1fr)); gap:12px; }
.card { background:var(--card); border:2px solid var(--line); border-radius:14px; padding:10px; }
.card.unclear { border-color:#d98a7a; } .card.unsure { border-color:#e0b25a; }
.pic { display:flex; gap:8px; align-items:center; }
.pic img { width:96px; height:96px; object-fit:contain; background:#fff; border-radius:10px; border:1px solid var(--line); }
.word { font-weight:800; font-size:17px; }
.tag { display:inline-block; padding:1px 8px; border-radius:99px; font-size:12px; font-weight:700; }
.tag.clear { background:var(--clear); color:var(--clearInk); } .tag.unsure { background:var(--unsure); color:var(--unsureInk); }
.tag.unclear { background:var(--unclear); color:var(--unclearInk); }
.likely { font-size:13px; color:var(--muted); }
label { display:block; font-size:13.5px; margin:3px 0; cursor:pointer; }
textarea { width:100%; box-sizing:border-box; height:34px; font:inherit; font-size:13px; margin-top:4px; }
.swap img { width:40px; height:40px; vertical-align:middle; object-fit:contain; }
.filters label { display:inline; margin-right:12px; }
</style>
<main>
<h1>Find It pictures without words</h1>
<p>From Chapter 3, and on every replay, Find It shows only pictures (Chapter 2 keeps the word on 2 of the 5). A
child then has to name each picture with its first sound. I rated every picture for whether a 5 to 7 year old here
would call it something with the right first sound.</p>
<p><b>Unclear</b>: a child would likely call it something else (a ladybug for "insect"). <b>Unsure</b>: some
children might. <b>Clear</b>: most children name it right. Until a picture is replaced, the app keeps the word under
every picture you mark <i>Keep the word</i>, even in later chapters.</p>
<p>My suggestion is already picked on every picture. Change any you disagree with, add a note if you like, then press Export CSV and send the file to Claude.</p>
<div class="bar">
  <button id="export">Export CSV</button>
  <button class="alt" id="clear">Clear my answers</button>
  <span class="filters"><label><input type="checkbox" id="onlyFlagged"> Show only unclear and unsure</label></span>
  <span id="status"></span>
</div>
<div id="list"></div>
</main>
<script>
const ROWS = __DATA__;
const KEY = "findit-clarity-2026-11-05";
let answers = {};
try { answers = JSON.parse(localStorage.getItem(KEY) || "{}"); } catch (e) { answers = {}; }
function save() { try { localStorage.setItem(KEY, JSON.stringify(answers)); } catch (e) {} count(); }
function count() {
  const changed = ROWS.filter(r => { const a = answers[r.word + "|" + r.letter]; return a && a.choice && a.choice !== defaultChoice(r); }).length;
  document.getElementById("status").textContent = "My suggestion is picked on every picture; you changed " + changed + ".";
}
function defaultChoice(r) {
  if (r.rating === "clear") return "hide";
  if (r.swap) return "swap";
  return "keepword";
}
function choices(r) {
  const c = [["hide", "Fine without the word"]];
  if (r.swap) c.push(["swap", "Swap to " + r.swap[0] + " (already in the app)"]);
  c.push(["keepword", "Keep the word under it"]);
  c.push(["newart", r.art ? "Ask agy for new art (" + r.art + "?)" : "Ask agy for new art"]);
  return c;
}
function render() {
  const only = document.getElementById("onlyFlagged").checked;
  const list = document.getElementById("list"); list.innerHTML = "";
  let current = null, grid = null;
  ROWS.forEach(r => {
    if (only && r.rating === "clear") return;
    if (r.letter !== current) {
      current = r.letter;
      const h = document.createElement("div"); h.className = "letter";
      h.textContent = r.letter.toUpperCase() + r.letter + (r.x ? "  (words that end in x; never shown without the word's sound first)" : "");
      list.appendChild(h);
      grid = document.createElement("div"); grid.className = "grid"; list.appendChild(grid);
    }
    const id = r.word + "|" + r.letter;
    const a = answers[id] || {};
    const choice = a.choice || defaultChoice(r);
    const card = document.createElement("div"); card.className = "card " + r.rating;
    let swapImg = r.swap ? `<span class="swap"> <img src="img/${r.swap[1]}.png" alt="${r.swap[0]}"></span>` : "";
    card.innerHTML = `<div class="pic"><img src="${r.img}" alt="${r.word}"><div><div class="word">${r.word}</div>
      <span class="tag ${r.rating}">${r.rating}</span>${r.likely ? `<div class="likely">Likely called: ${r.likely}</div>` : ""}
      ${swapImg}</div></div>`;
    choices(r).forEach(([v, t]) => {
      const l = document.createElement("label");
      l.innerHTML = `<input type="radio" name="${id}" value="${v}" ${choice === v ? "checked" : ""}> ${t}`;
      l.querySelector("input").addEventListener("change", () => { answers[id] = Object.assign(answers[id] || {}, { choice: v }); save(); });
      card.appendChild(l);
    });
    const note = document.createElement("textarea"); note.placeholder = "Note (optional)"; note.value = a.note || "";
    note.addEventListener("input", () => { answers[id] = Object.assign(answers[id] || {}, { note: note.value }); save(); });
    card.appendChild(note);
    grid.appendChild(card);
  });
  count();
}
document.getElementById("onlyFlagged").addEventListener("change", render);
document.getElementById("clear").addEventListener("click", () => { if (confirm("Clear every answer?")) { answers = {}; save(); render(); } });
document.getElementById("export").addEventListener("click", () => {
  const esc = s => '"' + String(s || "").replace(/"/g, '""') + '"';
  const lines = ["letter,word,rating,choice,swap_to,note"];
  ROWS.forEach(r => {
    const a = answers[r.word + "|" + r.letter] || {};
    const choice = a.choice || defaultChoice(r);
    lines.push([r.letter, r.word, r.rating, choice, choice === "swap" && r.swap ? r.swap[0] : "", a.note].map(esc).join(","));
  });
  const blob = new Blob([lines.join("\\n")], { type: "text/csv" });
  const link = document.createElement("a"); link.href = URL.createObjectURL(blob);
  link.download = "2026-11-05-findit-clarity_review.csv"; link.click();
});
render();
</script>
""".replace("__DATA__", data)
(OUT / "index.html").write_text(page, encoding="utf-8")
print(f"{len(rows)} pictures, {sum(r['rating'] == 'unclear' for r in rows)} unclear, "
      f"{sum(r['rating'] == 'unsure' for r in rows)} unsure -> {OUT / 'index.html'}")
