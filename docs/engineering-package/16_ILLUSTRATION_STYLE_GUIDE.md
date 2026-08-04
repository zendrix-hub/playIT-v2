# 16 — Illustration Style Guide

## 1. Visual Language
Flat-to-semi-flat 2D vector illustration. Soft, rounded geometric construction throughout — no sharp corners, no photorealism, no complex gradients or dramatic lighting. Every illustration should read clearly as a simple, friendly shape from across a room, since it needs to work at both large (letter card, 200dp+) and small (Find It grid item, ~64–96dp) render sizes.

## 2. Shape Language
- **Rounded everything:** circles and soft-cornered rectangles as the base geometric vocabulary, echoing the Design System's 24–32dp card radii and Material 3 Expressive's expanded shape library (`03 §4`, `04 §5`).
- **Thick, consistent outline weight** on every asset — this is what makes the "silhouette test" (`15 §6`) pass at small sizes, and is consistent with the single-story-letterform philosophy driving the app's typography choice (`03 §5.2`): clear, unambiguous shapes over decorative detail.
- **Minimal internal detail.** A cat is a rounded head + two triangular ears + simple facial features — not fur texture, whiskers rendered individually, or complex shading. Detail competes with recognizability at grid-item scale.
- **Asymmetric decorative elements are fine for background/map props** (per Material 3 Expressive's visual-tension principle, `04 §5`) but never on functionally-meaningful shapes (letters, target pictures, buttons) where literal, stable recognizability matters more than visual interest (`04 §5`'s point about literalism for essential actions vs. abstraction for decoration).

## 3. Character Style
See `17_CHARACTER_DESIGN_GUIDE.md` for the mascot specifically. General character rules (mascot + any incidental characters in Find It/Blend It illustrations):
- Simple, exaggerated but never scary or uncanny facial expressions — emotions must be legible from the enumerated states in `03 §6` (Happy/Excited/Thinking/Encouraging/Celebrating).
- No implied violence, weapons, or anything remotely unsettling — this is a baseline safety rule as much as a style rule.
- Culturally neutral by default (animals, everyday objects) rather than depicting specific real people.

## 4. Child-Friendliness Checklist (apply to every asset before approval)
- [ ] Passes the silhouette/15%-opacity recognizability test (`15 §6`).
- [ ] No color used as the sole distinguishing feature between two similar assets (dual-coding — `03 §6`).
- [ ] No harsh red as a dominant color (`03 §5.1`).
- [ ] Reads clearly at both largest (letter card) and smallest (grid item) intended render size.
- [ ] Consistent outline weight and proportion style with the rest of the asset library — deviation reads as "off-brand," which is itself a small trust/consistency cost for a young repeat user.

## 5. Consistency Rules
- One approved style-reference sheet, used as a generation/review anchor for every batch (`15 §6`).
- Consistent light source direction (implied top-left soft highlight) across all assets, even though shading is minimal.
- Consistent outline color/weight — do not let outline weight drift between asset batches produced at different times.

## 6. Material 3 Compatibility
- Illustrations are designed to sit inside M3 `Card` containers with the Design System's radii/elevation (`03 §4`) — leave adequate internal padding in the source art so it doesn't visually collide with a card's rounded corners.
- Transparent backgrounds throughout (except full-screen background art) so illustrations composite correctly against the app's tonal surface colors in both the shipped palette and any future dark-mode/contrast-adjusted palette (`03 §5.1`).

## 7. Accessibility Considerations
- Sufficient internal contrast within each illustration (e.g., a light-colored animal needs a dark enough outline to stay legible against a light card background) — don't rely solely on the app-level contrast rules; the illustration itself must self-contrast.
- Never encode meaning through color alone within an illustration (e.g., a "correct" vs. "incorrect" state must differ in shape/icon, not just hue) — consistent with `03 §6`'s app-wide rule.
- Illustrations must remain legible under a reduced-saturation/grayscale simulation (a direct check against Color Vision Deficiency, per the `Color_Palette.md` research folded into `03 §5.1`) — shape and value contrast should carry meaning even if hue information is lost.
