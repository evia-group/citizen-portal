## Why

The `gateway-listen-on-8888` change moved the openresty gateway's **host** port from `80` to `8888` and repinned the whole stack to `http://localhost:8888`. The devcontainer (Mode A) e2e harness still targets the old port `80` — it forwards `localhost:80 → host.docker.internal:80` and sets `APP=http://localhost`. After the gateway change nothing listens on host `80`, so every Mode A e2e scenario now fails to reach the app, Keycloak login, and the service-portal-fe.

## What Changes

- Forwards target port `8888` instead of `80` (Keycloak `9080` and BE `8180` unchanged).
- `APP` / `SERVICE_APP` defaults for the devcontainer point at `http://localhost:8888` (`/service-portal-fe` suffix preserved).
- Drop the `sudo` requirement on the socat forwards: `8888`, `9080`, `8180` are all unprivileged, so the privileged-port-80 workaround is no longer needed.
- Update comments in `commons.sh`, `forwards.sh`, `run.sh`, and `user-service-request-happy-path/run.sh` that reference port `80` / "privileged port".
- No change to Mode B (host) targeting — native FE on `:8081` is unaffected.

## Capabilities

### New Capabilities
- `e2e-environment-targeting`: How the e2e harness resolves target URLs and host-port forwards per environment (devcontainer/Mode A vs host/Mode B), so scenarios reach the stack through the openresty gateway on its published port.

### Modified Capabilities
<!-- No existing specs in openspec/specs/; nothing to modify. -->

## Impact

- `e2e/forwards.sh` — forward port list `80 9080 8180` → `8888 9080 8180`; remove `sudo` from the socat invocation; update header comments.
- `e2e/commons.sh` — devcontainer `APP` default `http://localhost` → `http://localhost:8888`; update comment.
- `e2e/run.sh` — comment referencing privileged `:80`.
- `e2e/user-service-request-happy-path/run.sh` — `SERVICE_APP` default `http://localhost/service-portal-fe` → `http://localhost:8888/service-portal-fe`; update comments.
- Depends on the `gateway-listen-on-8888` change being applied (gateway published on host `8888`).
- Host/Mode B runs and the `E2E_ENV` / `KC`/`API`/`APP` override escape hatches are unchanged.
