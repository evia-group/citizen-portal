## Context

openresty is the edge gateway for both local-dev modes. It currently has `listen 80;` in `nginx.conf` and is published via `ports: "8888:80"` in `docker-compose.yml`. Host:8888 → container:80. Because nginx's `$server_port` resolves to the listen port (`80`), `proxy_set_header X-Forwarded-Port $server_port;` forwards `80` to every upstream, which contradicts the real public port `8888` baked into issuer/redirect URLs.

## Goals / Non-Goals

**Goals:**
- Make the container listen port equal the public port (`8888`) so `$server_port` is correct.
- Keep the public entrypoint unchanged at `http://localhost:8888`.

**Non-Goals:**
- Changing the host port (stays `8888`).
- Touching app/issuer/redirect URLs (already `8888`).
- TLS or production gateway concerns.

## Decisions

- **Listen on 8888, map `8888:8888`.** Aligns internal and external ports so `$server_port` == `8888` with no hardcoding. Alternative — hardcode `X-Forwarded-Port 8888` while keeping `listen 80` — rejected: leaves the misleading `8888:80` mapping and duplicates the port literal across N `location` blocks.
- **Single listen directive.** There is one `server` block; only `listen 80;` changes to `listen 8888;`. The `$server_port` references in the existing `X-Forwarded-Port` headers then resolve correctly with no per-location edits.

## Risks / Trade-offs

- [Stale container/image keeps old `listen 80`] → Recreate openresty (`docker compose up -d --build openresty`); verify with a request to `http://localhost:8888`.
- [Other tooling assumes openresty internal port 80] → grep confirms only `nginx.conf` and `docker-compose.yml` reference it; no Dockerfile EXPOSE or healthcheck depends on 80.
- Rollback: revert both lines (`listen 8888;`→`80`, `"8888:8888"`→`"8888:80"`) and recreate the container.

## Spec ownership (for archiving — explore note)

> Captured during `/opsx-explore`. Pairs with the granularity note in `fix-e2e-gateway-port/design.md`.

When promoted to living specs, **`gateway-routing` is the single source of truth for the gateway's published origin/port (`8888`)**. Other specs that need to reach the gateway — notably `e2e-environment-targeting` — SHOULD reference "the port published by `gateway-routing`" relationally rather than restating the literal `8888`, so a future port move is a one-spec edit and the two specs cannot silently drift. The coupling is by convention only; OpenSpec tooling does not enforce the cross-reference.
