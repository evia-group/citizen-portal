#!/usr/bin/env bash
# Shared e2e network-forwarding helper.
#
# Why this exists:
#   In the devcontainer (Mode A, full Docker) the FE bundle and Keycloak are pinned to
#   http://localhost:8888 (issuer + OIDC redirect URIs), but inside the container `localhost`
#   is the container itself, not the host. So the browser/curl can't reach the host stack.
#   This forwards localhost:{8888,9080,8180} -> host.docker.internal:{8888,9080,8180} with
#   socat, so the localhost-pinned bundle + Keycloak work unchanged.
#
#   On the host (Mode B, infra-only Docker, native FE) `localhost` already reaches the
#   stack, so this is a no-op.
#
# Environment detection (override -> marker -> default host):
#   - E2E_ENV=devcontainer|host          explicit override (escape hatch)
#   - E2E_IN_DEVCONTAINER=1              injected marker (devcontainer.json / junie-sandbox.sh)
#   - otherwise                          assume host
#
# Usage:
#   - source it to get `e2e_env_kind` and `ensure_forwards`
#   - or execute it directly to just bring the forwards up
#
# Notes:
#   - All forwarded ports (8888, 9080, 8180) are unprivileged, so socat runs without sudo.
#     Forwards are started detached (setsid) and left running for the container lifetime —
#     there is no teardown.
#   - ensure_forwards is idempotent: a port that is already forwarded is skipped, so it is
#     safe to call once from run.sh and again from each scenario's commons.sh.

# e2e_env_kind — prints "devcontainer" or "host".
e2e_env_kind() {
  if [ -n "${E2E_ENV:-}" ]; then printf '%s\n' "$E2E_ENV"; return 0; fi
  if [ "${E2E_IN_DEVCONTAINER:-}" = "1" ]; then printf 'devcontainer\n'; else printf 'host\n'; fi
}

# _forward_up <port> — true if something already accepts TCP on 127.0.0.1:<port>.
# In the devcontainer nothing binds these ports until socat does, so an open port
# means the forward is already running.
_forward_up() {
  (exec 3<>"/dev/tcp/127.0.0.1/$1") 2>/dev/null && { exec 3>&- 3<&-; return 0; }
  return 1
}

# ensure_forwards — bring up the localhost->host.docker.internal forwards (devcontainer
# only; no-op on the host). Idempotent. Returns non-zero if a forward can't be brought up.
ensure_forwards() {
  [ "$(e2e_env_kind)" = "devcontainer" ] || return 0

  if ! command -v socat >/dev/null 2>&1; then
    echo "[forwards] socat not installed — cannot forward host ports" >&2
    return 1
  fi

  local port
  for port in 8888 9080 8180; do
    if _forward_up "$port"; then continue; fi
    echo "[forwards] localhost:$port -> host.docker.internal:$port"
    setsid socat "TCP-LISTEN:$port,fork,reuseaddr" "TCP:host.docker.internal:$port" \
      >/dev/null 2>&1 &
    disown 2>/dev/null || true
  done

  # Wait briefly for the binds to come up and verify each one.
  local i
  for port in 8888 9080 8180; do
    for i in $(seq 1 20); do
      _forward_up "$port" && break
      sleep 0.3
    done
    if ! _forward_up "$port"; then
      echo "[forwards] failed to bring up forward for port $port" >&2
      return 1
    fi
  done
  return 0
}

# When executed directly (not sourced), just bring the forwards up.
if [ "${BASH_SOURCE[0]}" = "${0}" ]; then
  ensure_forwards
fi
