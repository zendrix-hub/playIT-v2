#!/usr/bin/env bash
# Prints the latest GitHub Actions runs for a branch (default refactor/hear-say-it), one per line:
#   <run id> <status> <conclusion> <head sha> <url>
# Uses the git credential already stored for github.com (the token is never printed) and
# retries, because the network here is slow and flaky.
set -uo pipefail
BRANCH="${1:-refactor/hear-say-it}"
REPO="zendrix-hub/playIT-v2"
TOKEN=$(printf 'protocol=https\nhost=github.com\n\n' | git credential fill 2>/dev/null | sed -n 's/^password=//p')
for attempt in 1 2 3 4 5; do
  body=$(curl -sS -m 45 -H "Authorization: Bearer $TOKEN" -H "Accept: application/vnd.github+json" \
    "https://api.github.com/repos/$REPO/actions/runs?branch=$BRANCH&per_page=3" 2>/dev/null) && [ -n "$body" ] && break
  sleep 10
done
printf '%s' "${body:-}" | python3 -c '
import json, sys
try:
    d = json.load(sys.stdin)
except Exception:
    print("unreachable"); sys.exit(0)
for r in d.get("workflow_runs", []):
    print(r["id"], r["status"], r["conclusion"], r["head_sha"][:7], r["html_url"])
'
