#!/usr/bin/env bash
# Common setup, config, and utility functions for e2e run.sh scripts.
#
# Each run.sh must, in this order:
#   1. set -uo pipefail
#   2. cd "$(dirname "$0")"                — so screenshots/ and .playwright-cli/
#                                            land in the scenario directory
#   3. set S=<session-name>                 — required; used by `playwright-cli -s`
#   4. source ../commons.sh
#
# Optional scenario overrides (set BEFORE sourcing):
#   KC, API, APP
#
# Provides after sourcing:
#   - from e2e/.env: USER_NAME, USER_PASSWORD (and anything else defined there)
#   - vars: KC, API, APP
#   - dirs: screenshots/, .playwright-cli/ (created via mkdir -p)
#   - fns:  fail, log, pass, pw, latest_snap, ref_for, click_by, fill_by, wait_for, token
#
# Important: `playwright-cli` only accepts snapshot-derived refs (e<N>), not CSS
# selectors, and returns exit 0 even on error. The `pw` wrapper therefore
# captures stdout and treats a `### Error` line as failure; the ref_for helper
# looks up refs from the latest snapshot by role + accessible name before each
# interaction.

# shellcheck disable=SC1091
source "$(dirname "${BASH_SOURCE[0]}")/.env"   # provides USER_NAME, USER_PASSWORD

: "${S:?commons.sh: set S (session name) before sourcing}"

# Environment-aware target URLs. A scenario may still override KC/API/APP before
# sourcing; otherwise they are derived from the detected environment (see forwards.sh):
#   - devcontainer (Mode A): everything is served through the openresty gateway on the
#     host's :80, reached via the socat localhost forwards brought up below.
#   - host (Mode B): the FE runs natively on :8081; infra (Keycloak/BE) is in Docker.
# shellcheck disable=SC1091
source "$(dirname "${BASH_SOURCE[0]}")/forwards.sh"

if [ "$(e2e_env_kind)" = "devcontainer" ]; then
  APP="${APP:-http://localhost:8888}"
  KC="${KC:-http://localhost:9080}"
  API="${API:-http://localhost:8180}"
else
  APP="${APP:-http://localhost:8081}"
  KC="${KC:-http://localhost:9080}"
  API="${API:-http://localhost:8180}"
fi

rm -rf .playwright-cli
mkdir -p screenshots .playwright-cli

# In the devcontainer the agent CLI (`@playwright/cli` -> `playwright-cli`) defaults to the
# Google "chrome" channel, which has no Linux/arm64 build, so we pin it to the bundled
# Chromium ("chrome-for-testing", installed via `playwright-cli install-browser`). Chromium's
# setuid sandbox does not work inside the unprivileged container, hence `chromiumSandbox:false`.
# playwright-cli auto-loads `.playwright/cli.config.json` from the cwd (each scenario dir).
if [ "$(e2e_env_kind)" = "devcontainer" ]; then
  mkdir -p .playwright
  cat > .playwright/cli.config.json <<'JSON'
{
  "browser": {
    "browserName": "chromium",
    "launchOptions": { "chromiumSandbox": false }
  }
}
JSON
fi

START=$SECONDS

fail() { echo "FAILED_AT=$1" >&2; playwright-cli -s="$S" close >/dev/null 2>&1 || true; exit 1; }
log()  { echo "[run.sh] $*"; }
pass() { echo "PASS duration=$((SECONDS - START))s"; }

# Bring up the host-port forwards if needed (devcontainer/Mode A only; idempotent no-op
# on the host). Done here so fail() is defined. The top-level run.sh also does this once
# before launching scenarios in parallel; this call covers standalone scenario runs.
ensure_forwards || fail "forwards:setup"

# pw <subcommand> <args...> — run playwright-cli with strict error detection.
# playwright-cli prints "### Error\nError: ..." on failure but still exits 0,
# so we grep stdout and translate that into a shell failure.
#
# The bare `snapshot` command in @playwright/cli prints the accessibility tree
# inline to stdout and does NOT persist it to disk, but the ref_for helper below
# resolves refs by grepping the latest snapshot FILE. We therefore inject
# `--filename` so every `pw snapshot` writes a fresh page-<ts>.yml that
# latest_snap/ref_for can read. (Action commands like click/fill already persist
# their post-action snapshot to .playwright-cli/ automatically.)
pw() {
  local out rc
  if [ "${1:-}" = "snapshot" ] && [[ "$*" != *--filename* ]]; then
    set -- "$@" "--filename=.playwright-cli/page-$(date +%s%N).yml"
  fi
  out=$(playwright-cli -s="$S" "$@" 2>&1)
  rc=$?
  if printf '%s\n' "$out" | grep -q '^### Error'; then
    printf '%s\n' "$out" >&2
    return 1
  fi
  return $rc
}

# latest_snap — path to the freshest snapshot yaml in .playwright-cli/
latest_snap() { ls -t .playwright-cli/page-*.yml 2>/dev/null | head -1; }

# ref_for <role> <name-substring> — returns the ref (e.g. "e24") of the first
# element in the latest snapshot whose line matches `<role> "<...name-substring...>"`.
# Names with special chars are matched via fixed-string grep (no regex headaches).
ref_for() {
  local role="$1" name="$2" snap line
  snap=$(latest_snap)
  [ -n "$snap" ] || { echo "no snapshot yet" >&2; return 1; }
  # Fixed-string search for `<role> "...name...` — the full name is quoted in the YAML
  line=$(grep -F -- "$role \"" "$snap" | grep -F -- "$name" | head -1 || true)
  [ -n "$line" ] || return 1
  printf '%s\n' "$line" | grep -oE 'ref=e[0-9]+' | head -1 | cut -d= -f2
}

# click_by <role> <name>
click_by() {
  local role="$1" name="$2" ref
  ref=$(ref_for "$role" "$name") || return 1
  pw click "$ref"
}

# fill_by <role> <name> <value>
fill_by() {
  local role="$1" name="$2" value="$3" ref
  ref=$(ref_for "$role" "$name") || return 1
  pw fill "$ref" "$value"
}

# wait_for <role> <name> [timeout-seconds=15]
# Polls snapshot until element is present or timeout hits.
wait_for() {
  local role="$1" name="$2" t="${3:-15}"
  for _ in $(seq 1 "$t"); do
    if ref_for "$role" "$name" >/dev/null 2>&1; then return 0; fi
    sleep 1
    pw snapshot >/dev/null || true
  done
  return 1
}

# token <user> <password> — fetch a Keycloak access token. Echoes token on stdout;
# empty on auth failure (lets callers assert a deleted user can no longer log in).
# Retries up to 3 times on empty-token responses (Keycloak concurrent-request
# throttling / brute-force detection when multiple scenarios hit the same account
# simultaneously). Curl transport errors still exit 1 immediately so callers can
# distinguish "Keycloak unreachable" from "bad credentials / deleted user".
token() {
  local raw tok attempt
  for attempt in 1 2 3; do
    raw=$(curl -sS -X POST "$KC/realms/portal/protocol/openid-connect/token" \
      -d "username=$1&password=$2&grant_type=password&client_id=user-portal") || return 1
    tok=$(printf '%s\n' "$raw" | jq -r '.access_token // empty')
    [ -n "$tok" ] && { printf '%s\n' "$tok"; return 0; }
    [ "$attempt" -lt 3 ] && sleep $((attempt * 2))
  done
  # All retries exhausted — return empty (exit 0) so callers can distinguish
  # this from a transport error and assert the user cannot authenticate.
}
