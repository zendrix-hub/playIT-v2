"""Check cut-out pictures and write the final review page (each picture on the 4 test backgrounds).

Claude runs this in WSL with the Kokoro venv (numpy, scipy, Pillow):
  ~/.playit-env/kvenv/bin/python tools/images/audit.py <final_dir> [--app app/src/main/assets/images/pictures]
      [--items <batch>/items.json]

<final_dir> is the output of cutout.py. Writes <final_dir>/audit.json and <final_dir>/index.html.
The page shows each picture on the 16 §4.3 backgrounds, at Find It size (96 px) and large, next to the
image the app uses today, with the checks below. The user marks OK or FIX per picture and exports
<batch>_final_review.csv into the batch folder.

Checks (FAIL blocks the release; WARN asks the viewer to look closely):
  size      512 x 512 RGBA                                                   FAIL
  corners   the four 16 x 16 corners are fully transparent                   FAIL
  halo      share of the outer edge ring that is light (min channel > 200)   WARN > 2 %
  outline   median colour of the outer edge ring vs #4A2E18 (RGB distance)   WARN > 60
  padding   smallest margin to the canvas edge, in px of 512                 FAIL < 32, WARN < 60
  colours   distinct colours (5 bits per channel) of opaque pixels           WARN < 12 (placeholder-like)
  red       share of opaque pixels that are harsh red (R>200, G<70, B<70)    WARN > 5 % (16: no harsh red)
"""
import argparse
import html
import json
import os
import pathlib

import numpy as np
from PIL import Image
from scipy import ndimage

OUTLINE = np.array([0x4A, 0x2E, 0x18])
BACKGROUNDS = {"Soft Sky": "#EAF6FF", "Cream White": "#FFFDF8", "Achievement Gold": "#FFC107",
               "Near-Black": "#1A1A2E"}


def check(path):
    im = Image.open(path)
    res = {"size": "PASS" if im.size == (512, 512) and im.mode == "RGBA" else f"FAIL {im.size} {im.mode}"}
    a = np.array(im.convert("RGBA"))
    alpha, rgb = a[..., 3], a[..., :3].astype(int)

    corners = [alpha[:16, :16], alpha[:16, -16:], alpha[-16:, :16], alpha[-16:, -16:]]
    res["corners"] = "PASS" if all(c.max() == 0 for c in corners) else "FAIL not transparent"

    solid = alpha > 128
    ring = solid & ~ndimage.binary_erosion(solid, iterations=3)
    light = (rgb[ring].min(axis=1) > 200).mean() if ring.any() else 0
    res["halo"] = f"{'WARN' if light > 0.02 else 'PASS'} {light:.1%}"

    med = np.median(rgb[ring], axis=0) if ring.any() else np.zeros(3)
    dist = float(np.linalg.norm(med - OUTLINE))
    res["outline"] = f"{'WARN' if dist > 60 else 'PASS'} #{int(med[0]):02X}{int(med[1]):02X}{int(med[2]):02X} (d={dist:.0f})"

    ys, xs = np.where(alpha > 8)
    margin = int(min(xs.min(), ys.min(), 511 - xs.max(), 511 - ys.max()))
    res["padding"] = f"{'FAIL' if margin < 32 else 'WARN' if margin < 60 else 'PASS'} {margin} px"

    op = rgb[alpha > 250] >> 3
    n = len(np.unique(op[:, 0] * 1024 + op[:, 1] * 32 + op[:, 2]))
    res["colours"] = f"{'WARN' if n < 12 else 'PASS'} {n}"

    o = rgb[alpha > 250]
    red = ((o[:, 0] > 200) & (o[:, 1] < 70) & (o[:, 2] < 70)).mean()
    res["red"] = f"{'WARN' if red > 0.05 else 'PASS'} {red:.1%}"
    return res


def write_page(final, results, app_dir, items, batch_name):
    words = {i["id"]: i.get("word", i["id"]) for i in items}
    app_files = {i["id"]: i["app_file"] for i in items if "app_file" in i}  # relative to --app (card 29)
    app = pathlib.Path(app_dir).resolve() if app_dir else None
    rows = []
    for name, res in results.items():
        item = name[:-4]
        cells = "".join(
            f'<div class="bg" style="background:{c}" title="{html.escape(t)}"><img src="{html.escape(name)}" width="96"></div>'
            for t, c in BACKGROUNDS.items())
        cur = ""
        today = app / app_files.get(item, name) if app else None
        if today and today.exists():
            src = pathlib.Path(os.path.relpath(today, final.resolve())).as_posix()  # works in a Windows browser
            cur = f'<div class="bg" style="background:#FFFDF8"><img src="{html.escape(src)}" width="96"><br><small>today</small></div>'
        else:
            cur = '<div class="bg"><small>new</small></div>'
        checks = "<br>".join(
            f'<span class="{v.split()[0].lower()}">{k}: {html.escape(v)}</span>' for k, v in res.items())
        rows.append(f"""<tr data-item="{item}"><td><b>{html.escape(words.get(item, item))}</b><br><small>{item}</small></td>
<td><img src="{html.escape(name)}" width="256" style="background:#FFFDF8"></td><td class="bgs">{cells}{cur}</td>
<td><small>{checks}</small></td>
<td><label><input type="radio" name="{item}" value="OK"> OK</label><br>
<label><input type="radio" name="{item}" value="FIX"> FIX</label><br>
<input type="text" class="note" placeholder="what to change"></td></tr>""")
    page = f"""<!doctype html><meta charset="utf-8"><title>{batch_name}: final pictures</title>
<style>body{{font-family:sans-serif;margin:16px}} td{{vertical-align:top;padding:8px;border-bottom:1px solid #ddd}}
.bgs{{display:flex;gap:6px;flex-wrap:wrap;max-width:560px}} .bg{{padding:8px;border-radius:12px;text-align:center}}
.fail{{color:#b00;font-weight:bold}} .warn{{color:#b60}} .pass{{color:#070}} .note{{width:180px}}
#bar{{position:sticky;top:0;background:#fff;padding:8px 0;border-bottom:2px solid #333}}</style>
<div id="bar"><b>{batch_name}: final check.</b> Each picture after the background cut, on the 4 app backgrounds at
Find It size, next to today's picture. Mark OK, or FIX with a note. Then
<button onclick="exportCsv()">Export CSV</button> <span id="count"></span></div>
<table>{''.join(rows)}</table>
<script>
function exportCsv(){{
  const lines=[["item","verdict","note"]];
  let open=0;
  document.querySelectorAll("tr[data-item]").forEach(tr=>{{
    const it=tr.dataset.item, v=tr.querySelector("input[type=radio]:checked");
    if(!v) open++;
    lines.push([it, v?v.value:"", tr.querySelector(".note").value]);
  }});
  if(open && !confirm(open+" pictures have no mark. Export anyway?")) return;
  const csv=lines.map(r=>r.map(c=>'"'+String(c).replace(/"/g,'""')+'"').join(",")).join("\\n");
  const a=document.createElement("a");
  a.href=URL.createObjectURL(new Blob([csv],{{type:"text/csv"}}));
  a.download="{batch_name}_final_review.csv"; a.click();
}}
</script>"""
    (final / "index.html").write_text(page, encoding="utf-8")


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("final_dir")
    ap.add_argument("--app", help="folder with the images the app uses today")
    ap.add_argument("--items", help="items.json of the batch (for the words)")
    a = ap.parse_args(argv)
    final = pathlib.Path(a.final_dir)
    results = {f.name: check(f) for f in sorted(final.glob("*.png"))}
    (final / "audit.json").write_text(json.dumps(results, indent=1), encoding="utf-8")
    items = json.loads(pathlib.Path(a.items).read_text(encoding="utf-8"))["items"] if a.items else []
    write_page(final, results, a.app, items, final.resolve().parent.name)
    fails = {n: r for n, r in results.items() if any(v.startswith("FAIL") for v in r.values())}
    warns = {n: [k for k, v in r.items() if v.startswith("WARN")] for n, r in results.items()}
    for n, r in results.items():
        print(n, " | ".join(f"{k} {v}" for k, v in r.items()))
    print(f"\n{len(results)} pictures, {len(fails)} FAIL, "
          f"{sum(1 for w in warns.values() if w)} with WARN; page: {final / 'index.html'}")


if __name__ == "__main__":
    main()
