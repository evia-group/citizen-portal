#!/usr/bin/env bash
# Runs all scenario run.sh files in parallel with live in-place status.
# Exit 0 if all pass, 1 if any fail.
set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
TMPDIR_BASE=$(mktemp -d)
trap 'rm -rf "$TMPDIR_BASE"' EXIT

# Bring host-port forwards up once (devcontainer/Mode A only; idempotent no-op on the
# host). Doing it here — before launching scenarios in parallel — avoids multiple
# scenarios racing to bind the same privileged port (:8888).
# shellcheck disable=SC1091
source "$SCRIPT_DIR/forwards.sh"
ensure_forwards || { echo "Failed to set up host-port forwards (see [forwards] above)." >&2; exit 1; }

pids=()
scenarios=()
start_time=$(date +%s)

for script in "$SCRIPT_DIR"/*/run.sh; do
  scenario=$(basename "$(dirname "$script")")
  out="$TMPDIR_BASE/$scenario.out"
  date +%s > "$TMPDIR_BASE/$scenario.start"
  bash "$script" >"$out" 2>&1 &
  pids+=($!)
  scenarios+=("$scenario")
done

if [ ${#pids[@]} -eq 0 ]; then
  echo "No scenario run.sh files found." >&2
  exit 1
fi

N=${#scenarios[@]}

# Print initial status block (N lines overwritten in place)
for scenario in "${scenarios[@]}"; do
  printf "  %-26s  starting...\n" "$scenario"
done

# Background in-place refresh loop: redraws the N-line block every 0.5s
(
  while true; do
    sleep 0.5
    printf '\033[%dA' "$N"
    for scenario in "${scenarios[@]}"; do
      out="$TMPDIR_BASE/$scenario.out"
      sc_start=$(cat "$TMPDIR_BASE/$scenario.start" 2>/dev/null || echo "$start_time")
      elapsed=$(( $(date +%s) - sc_start ))
      if tail -1 "$out" 2>/dev/null | grep -q '^PASS '; then
        duration=$(tail -1 "$out" | grep -oE '[0-9]+s$' | head -1 || echo "${elapsed}s")
        printf "  %-26s  \033[32m[PASS]\033[0m %s\033[K\n" "$scenario" "$duration"
      elif grep -q '^FAILED_AT=' "$out" 2>/dev/null; then
        point=$(grep '^FAILED_AT=' "$out" | tail -1 | cut -d= -f2-)
        printf "  %-26s  \033[31m[FAIL]\033[0m at %s (%ds)\033[K\n" "$scenario" "$point" "$elapsed"
      else
        step=$(grep '^\[run\.sh\]' "$out" 2>/dev/null | tail -1 | sed 's/^\[run\.sh\] //' || true)
        if [ -n "$step" ]; then
          [ "${#step}" -gt 44 ] && step="${step:0:44}..."
          printf "  %-26s  %s (%ds)\033[K\n" "$scenario" "$step" "$elapsed"
        else
          printf "  %-26s  starting... (%ds)\033[K\n" "$scenario" "$elapsed"
        fi
      fi
    done
  done
) &
display_pid=$!

# Collect results; sequential wait is fine — display loop reads output files directly
results=()
for i in "${!pids[@]}"; do
  wait "${pids[$i]}" && results+=("PASS") || results+=("FAIL")
done

# One final display pass, then stop
sleep 0.6
kill "$display_pid" 2>/dev/null
wait "$display_pid" 2>/dev/null

echo ""
echo ""

overall=0
for i in "${!scenarios[@]}"; do
  scenario="${scenarios[$i]}"
  result="${results[$i]}"
  out="$TMPDIR_BASE/$scenario.out"

  echo "=== $scenario: $result ==="
  cat "$out"
  echo ""

  [ "$result" = "PASS" ] || overall=1
done

echo "========================================"
for i in "${!scenarios[@]}"; do
  scenario="${scenarios[$i]}"
  result="${results[$i]}"
  out="$TMPDIR_BASE/$scenario.out"
  duration=$(tail -1 "$out" 2>/dev/null | grep -oE '[0-9]+s$' | head -1 || echo "?")
  printf "%-30s %-6s %s\n" "$scenario" "$result" "$duration"
done
echo "========================================"

exit $overall
