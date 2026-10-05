#!/usr/bin/env bash
# Render a Markdown document to .docx (pandoc) and .pdf (Microsoft Word on the Windows side), so the .md
# stays the single source and the shared copies can't drift from it.
#
# Usage (WSL): tools/docs/render_doc.sh <doc.md> <out_basename>
#   e.g. tools/docs/render_doc.sh docs/validation-package/04_MVP_VALIDATION_HIGHLIGHTS.md \
#            docs/validation-package/playIT_MVP_Validation_Highlights
# Writes <out_basename>.docx and <out_basename>.pdf.
# Needs pandoc (~/.playit-env/tools/pandoc-3.6.4, standalone release) and Word (WINWORD.EXE) for the PDF.
# The .md must sit on the Windows drive (/mnt/c/...) so Word can open the .docx.
set -euo pipefail
SRC="$1"; OUT="$2"
PANDOC="${PANDOC:-$HOME/.playit-env/tools/pandoc-3.6.4/bin/pandoc}"

"$PANDOC" "$SRC" --from gfm+tex_math_dollars --to docx --resource-path "$(dirname "$SRC")" -o "$OUT.docx"
echo "wrote $OUT.docx"

DOCX_WIN=$(wslpath -w "$(realpath "$OUT.docx")")
PDF_WIN=$(wslpath -w "$(realpath -m "$OUT.pdf")")
powershell.exe -NoProfile -Command "
\$w = New-Object -ComObject Word.Application
\$w.Visible = \$false
try {
  \$d = \$w.Documents.Open('$DOCX_WIN', \$false, \$true)
  \$d.SaveAs2('$PDF_WIN', 17)   # 17 = wdFormatPDF
  \$d.Close(\$false)
} finally { \$w.Quit() }
"
echo "wrote $OUT.pdf"
