# 20 — Tools & External Resources Catalog

This document registers external developer catalogs, free developer tiers, public APIs, AI production tools, and Model Context Protocol (MCP) integrations referenced in the root `tools/` folder. It provides direct guidance on how to leverage each resource throughout PlayIT's thesis development lifecycle, asset generation pipeline, and MVP validation.

---

## 1. Catalog Index

| Category | Reference File | GitHub / Primary URL | Primary Role in PlayIT |
|---|---|---|---|
| **Public APIs** | `tools/api layer.png` | [public-apis/public-apis](https://github.com/public-apis/public-apis) | Programmatic validation of kindergarten phonics vocabulary, word definitions, and phonetic IPA dictionaries via Datamuse & Free Dictionary APIs. |
| **Awesome MCP Servers** | `tools/mcp servers.png` | [punkpeye/awesome-mcp-servers](https://github.com/punkpeye/awesome-mcp-servers) | Equipping Antigravity AI agents with autonomous tools (Pollinations MCP, Context7 documentation search, Hugging Face models, Chrome DevTools testing). |
| **Free for Developers** | `tools/free-for-dev.png` | [ripienaar/free-for-dev](https://github.com/ripienaar/free-for-dev) | Zero-cost infrastructure for hosting Capstone MVP artifacts, Supabase telemetry databases, GitHub Actions CI/CD builds, and Cloudflare Pages downloads. |
| **AI Generation Tools** | `tools/tool 1.png` | Various (ElevenLabs, Clipdrop, Magnific, Runway, Suno, etc.) | High-fidelity asset production suite for transparent mascot poses, neural voice acting, loopable nursery music, and talking tutorial animations. |

---

## 2. PlayIT System Integration Matrix

### A. Asset & Media Generation (`tools/tool 1.png`)
* **Background Removal & Transparency (`Clipdrop` / Local `rembg`)**: Ensures all mascot cutouts, reward stars, and letter cards have 100% clean alpha channels for Android Compose rendering.
* **Semantic Upscaling (`Magnific AI`)**: Upscales raster textures to 4K without losing continuous `#2D373E` outline sharpness.
* **Speech Synthesis (`ElevenLabs` / Microsoft `edge-tts`)**: Provides warm, child-friendly voice talent (`en-US-AnaNeural`) for 24+ in-game voice-over lines.
* **Music & Jingle Generation (`Suno AI`)**: Generates loopable, non-intrusive background melodies themed around Bohol landscapes.

### B. Free Curriculum & Linguistic APIs (`tools/api layer.png`)
* **Datamuse API**: Queries valid 3-letter (CVC) and 4-letter English words constructible from cumulative Marungko letter groups.
* **Free Dictionary API**: Automates validation of word complexity, ensuring words selected for early learners are concrete, visualizable, and age-appropriate.
* **Executable Utility**: See [`tools/dictionary_validator.py`](../../tools/dictionary_validator.py).

### C. Developer Infrastructure & Telemetry (`tools/free-for-dev.png`)
* **Telemetry & Research DB**: Supabase PostgreSQL for storing anonymous ISO/IEC 25010 and TAM survey responses during Week 1–2 MVP field evaluations.
* **CI/CD Build Pipeline**: GitHub Actions for compiling unit tests and assembling release APKs upon push.
* **Public Artifact Hosting**: Cloudflare Pages / R2 for hosting downloadable validation packages and research report PDFs.

---

## 3. Maintenance & Reproducibility

1. When adding new external APIs or tools, update [`tools/README.md`](../../tools/README.md) and mirror changes here.
2. Ensure no hardcoded API keys are checked into source control; use environment variables or local properties for authenticated services.
