# Autonomous Asset Pipeline & Motion Directive (v2)

> **DEV-TIME ONLY.** Everything in Sections 1–3 runs once, during development,
> executed by the `agy` agent in this workspace. **None of it may be called by
> PlayIT at runtime.** PlayIT is offline-first with zero network calls after
> install — every asset produced here must be bundled locally (`assets/images/` or
> `res/drawable/`) before it ships. If a step below would need a remote URL
> at app runtime, it is wrong; fix it before continuing.

## System Role

You are an execution module responsible for generating, processing, and
animating PlayIT's UI graphics — centered on its tarsier mascot appearing
consistently across the map, lesson screens, Blend It, and the parent
dashboard. Your job is to generate character-consistent art, strip
backgrounds locally, export correct Android density variants, and attach
Jetpack Compose declarative motion (`GummyMotionAsset`) — logging every asset to a manifest for human
review before it's committed.

---

## 1. Character-Consistent Asset Generation

**Primary path: native Nano Banana / Nano Banana Pro (Gemini 3 image
models), not a third-party API.** Antigravity's agent harness has built-in
image generation using the same Google AI Pro account already authenticated
in this session — use it directly rather than shelling out to external
generation APIs. This matters specifically because it holds a consistent
character across many generations when given a reference image, which is
what a recurring mascot needs and what generic diffusion APIs don't do well.

### 1a. One-time: canonical mascot reference

Before generating any per-screen asset, produce (or reuse, if one already
exists in `./assets/reference/`) a single canonical reference image of the
tarsier mascot — neutral pose, clean lines, transparent-ready flat-color
style, front-facing. Save it as `./assets/reference/tarsier_canonical.png`.
Everything else in this pipeline references this file. Do not regenerate it
casually — character drift here propagates into every asset downstream.

### 1b. Per-asset generation (scripted, for batch/manifest logging)

```python
# generate_asset.py
# Usage: python3 generate_asset.py "<scene prompt>" <output_name>
import sys, base64, json, datetime
from google import genai

REFERENCE = "./assets/reference/tarsier_canonical.png"
MODEL = "gemini-3.1-flash-image"  # go-to model; use "gemini-3-pro-image"
                                   # for hero/high-fidelity assets that need
                                   # 4K output or extra reasoning over layout

def generate(prompt: str, output_name: str):
    client = genai.Client()  # reads GEMINI_API_KEY from env
    with open(REFERENCE, "rb") as f:
        ref_bytes = f.read()

    interaction = client.interactions.create(
        model=MODEL,
        input=[
            {"type": "text", "text": (
                "Using the attached reference image of the mascot, keep the "
                "character's proportions, colors, and face completely "
                f"consistent. {prompt}. Flat vector illustration style, "
                "isolated on a plain white background, no shadows."
            )},
            {"type": "image", "data": base64.b64encode(ref_bytes).decode(),
             "mime_type": "image/png"},
        ],
        response_format={"type": "image", "aspect_ratio": "1:1", "image_size": "2K"},
    )

    out_path = f"./assets/raw/{output_name}.png"
    with open(out_path, "wb") as f:
        f.write(base64.b64decode(interaction.output_image.data))

    # Append to manifest — see Section 4
    entry = {
        "asset_id": output_name, "prompt": prompt, "model": MODEL,
        "reference_image": REFERENCE, "generated_at": datetime.datetime.utcnow().isoformat(),
        "reviewed": False,
    }
    with open("./assets/manifest.jsonl", "a") as f:
        f.write(json.dumps(entry) + "\n")
    print(f"Generated {out_path} — flagged for review in manifest.jsonl")

if __name__ == "__main__":
    generate(sys.argv[1], sys.argv[2])
```

Requires `pip install google-genai --break-system-packages` and a
`GEMINI_API_KEY` in the environment (same Google AI Pro account already used
for this Antigravity session — get one at aistudio.google.com/apikey if not
already set).

### 1c. Fallback (non-character assets only)

For disposable, non-mascot art where consistency doesn't matter — background
textures, generic decorative shapes — Pollinations' key-less endpoint is a
legitimate free fallback:

```bash
curl -s "https://image.pollinations.ai/prompt/soft%20watercolor%20forest%20background%20texture?width=1024&height=1024&nologo=true&model=flux" \
  -o ./assets/raw/bg_texture.png
```

Do **not** use this path for the mascot or any recurring character — it has
no character-consistency mechanism and will drift between calls. The
previous HuggingFace SDXL route has been dropped entirely: it depends on
HuggingFace's legacy Serverless Inference API, which is increasingly
unreliable (rate limits, cold starts, models being migrated to paid
Inference Providers) — not worth building around.

---

## 2. Background Removal & Optimization

```bash
# Model pinned to isnet-anime — tuned for flat cartoon/illustration edges,
# not the default u2net (photo-oriented).
rembg i -m isnet-anime ./assets/raw/<asset_name>.png ./assets/transparent/<asset_name>.png

# Verify the cutout actually has transparency before continuing.
python3 - <<PY
from PIL import Image
img = Image.open("./assets/transparent/<asset_name>.png").convert("RGBA")
alpha = img.getchannel("A")
if alpha.getextrema()[0] == 255:
    raise SystemExit("No transparency detected — flag for manual review, do not proceed.")
PY

# Optimize
magick ./assets/transparent/<asset_name>.png -strip ./assets/optimized/<asset_name>.png
```

### Android density export

Ship real density buckets, not one fixed-size PNG:

```bash
declare -A DENSITIES=( [mdpi]=1 [hdpi]=1.5 [xhdpi]=2 [xxhdpi]=3 [xxxhdpi]=4 )
BASE=192  # base dp size for this asset
for density in "${!DENSITIES[@]}"; do
  scale=${DENSITIES[$density]}
  size=$(python3 -c "print(int($BASE * $scale))")
  mkdir -p ./app/src/main/res/drawable-$density
  magick ./assets/optimized/<asset_name>.png -resize ${size}x${size} \
    ./app/src/main/res/drawable-$density/<asset_name>.png
done
```

---

## 3. Motion Integration

**Default to Reanimated container transforms first** (Section 4 below) —
they cover idle float, celebration scale/wiggle, and press compression
without adding a second animation runtime. Only reach for Lottie or Rive if
Reanimated genuinely can't express the motion.

**Structural rule, unchanged from v1:** never re-trace or vectorize the
generated raster art into inline SVG. Render via `<Image source={require(...)} />`
and animate the container.

### If you do need Lottie or Rive

- **Lottie:** there is no generic "celebrating mascot" asset to fetch —
  LottieFiles URLs are per-animation hashes tied to someone else's specific
  artwork (`assetsN.lottiefiles.com/packages/lf20_<hash>.json`), not a
  reusable template. If a suitable *non-character* effect exists (confetti,
  sparkle burst), download it by hand, save it locally, and load it with
  `require()` — never fetch it at runtime.
- **Rive:** load exclusively from a bundled local `.riv` file
  (`useRiveFile(require('./assets/mascot_motion.riv'))`). Every official Rive
  code sample loads its demo file from `cdn.rive.app` by remote URL — do not
  copy that pattern here; it would violate the offline-first requirement.
  There's no free pre-rigged tarsier state machine to fetch regardless, so
  this path realistically means commissioning or hand-rigging one, not
  scripting a curl call.

---

## 4. Non-Destructive Motion & Animation Directives

*(unchanged from v1 — these are solid)*

Apply animations purely via container transforms (`translateY`, `scale`,
`rotate`):

- **Idle Floating State:** soft, looping vertical float (y ± 8px, 2000ms
  duration, cosine/sine easing).
- **Success / Celebration State:** spring scale-up (1.12×) paired with a
  rapid alternating wiggle rotation (± 6°).
- **Interactive Press Compression:** 4px downward translation on touch
  active state.

---

## 5. Manifest & Review Checkpoint

Every generated asset gets one line in `./assets/manifest.jsonl` (written
automatically by `generate_asset.py`). Before any asset is committed to the
app repo:

1. Open the raw generation, the transparent cutout, and the final density
   exports side by side.
2. Check for AI-art artifacts (extra digits, asymmetric features, style
   drift from the reference) and ragged cutout edges.
3. Flip `"reviewed": true` in the manifest entry only after a human has
   looked at it.

Do not let unreviewed assets reach `./app/src/main/res/`.
