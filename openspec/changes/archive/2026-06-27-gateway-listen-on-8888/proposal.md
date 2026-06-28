## Why

The openresty gateway listens on port `80` inside the container but is published to the host as `8888` (`"8888:80"`). The internal/external port mismatch means `$server_port` is `80`, so `X-Forwarded-Port` sent to upstreams is `80` while the real public port is `8888`. This breaks any upstream that builds absolute URLs from forwarded headers and makes the config confusing to reason about.

## What Changes

- openresty listens on `8888` internally instead of `80`.
- Docker port mapping becomes symmetric: `"8888:8888"` instead of `"8888:80"`.
- `X-Forwarded-Port` now correctly forwards `8888` to all upstreams.
- No public-facing URL changes — the gateway is still reached at `http://localhost:8888`.

## Capabilities

### New Capabilities
- `gateway-routing`: The openresty edge gateway — the port it listens on, the host port mapping, and the forwarded-port header it passes to upstreams.

### Modified Capabilities
<!-- No existing specs in openspec/specs/; nothing to modify. -->

## Impact

- `infrastructure/openresty/nginx.conf` — `listen 80;` → `listen 8888;`.
- `docker-compose.yml` — openresty `ports: "8888:80"` → `"8888:8888"`.
- No change to issuer URL, post-logout redirect URIs, or any app config (all already use `http://localhost:8888`).
- Affects both Mode A (full Docker) and Mode B (infra-only) since openresty runs in both.
