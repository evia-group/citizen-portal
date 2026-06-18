---
name: local-dev
description: Start the local development environment for the Bürgerportal monorepo. Use when the user wants to run, boot, start, or set up the app locally — covers full-Docker (Mode A) and infra-only/hot-reload (Mode B) workflows, including ports, default credentials, and which command to run for each app.
---

# Local Development

There are two supported local-dev modes. They share a single Keycloak issuer URL (`http://localhost/keycloak/realms/portal`), so a token issued in one mode validates in the other.

## Mode B — Infra-only Docker (recommended for development)

Runs postgres, keycloak, and openresty in Docker; FE and BE run natively with hot-reload. Use this for day-to-day work. OpenResty is included so the shared Keycloak issuer URL (`http://localhost/keycloak`) works without any config change.

```bash
npm install
docker compose up -d
npm run dev:local
```

`npm run dev:local` starts `user-portal-be` (Maven on port 8180) and `user-portal-fe` (Expo on port 8081) concurrently with colour-prefixed logs (`be` / `fe`).

- User Portal FE: http://localhost:8081 (login: `user` / `user`)
- User Portal BE: http://localhost:8180
- Service Portal FE: http://localhost:8281 — run separately via `npm run dev:service-portal`
- Keycloak Admin: http://localhost:9080 (login: `admin` / `admin`)
- Postgres: localhost:5432

The native FE reads `apps/user-portal-fe/.env` for `EXPO_PUBLIC_API_URL` and `EXPO_PUBLIC_KEYCLOAK_URL`. Both point to `http://localhost/keycloak`, which routes through the OpenResty container. OpenResty starts as part of the default `docker compose up` set (no `--profile full` needed), so the proxied Keycloak URL works in Mode B as well as Mode A.

Note: per-app `.env` files are excluded from the Docker build context (`.dockerignore`) so they cannot interfere with Mode A image builds.

## Mode A — Full Docker (zero-setup demo / e2e)

Runs everything in containers. Use this when you want the gateway path (openresty) exercised, when you don't need hot-reload, or when running e2e tests against the full stack.

```bash
docker compose --profile full up
```

Mode A activates the `local-docker` Spring profile in all three BE services via `STAGE=local-docker` in compose. The `local-docker` profile hardcodes container-network hostnames (`postgres`, `keycloak`) so the BEs can reach each other inside the Docker network. The JWT `jwk-set-uri` points direct to `keycloak:8080` (skipping the reverse proxy), while `issuer-uri` stays at `http://localhost/keycloak/...` to match the `iss` claim in tokens.

- App: http://localhost/ (login: `user` / `user`)
- Keycloak Admin: http://localhost:9080 (login: `admin` / `admin`)
- Service Portal FE: http://localhost/service-portal-fe
- Direct ports (bypassing the gateway, for debugging only):
  - user-portal-fe (Caddy static): http://localhost:8181
  - user-portal-be: http://localhost:8180
  - postgres: localhost:5432

In Mode A the **user-portal-fe is a static Expo web export served by Caddy** — *not* the Expo dev server. Hot-reload is not available; rebuild with `docker compose --profile full up --build user-portal-fe` after editing the FE.

`docker compose up` (without `--profile full`) starts **postgres, keycloak, and openresty** — this is Mode B's infra step. OpenResty is included in the default set so the proxied Keycloak URL (`http://localhost/keycloak`) works for natively-run FE and BEs without any config change.

## Six-must-move-together

The following six settings encode the Keycloak issuer URL and must always change in lock-step. If any one drifts, tokens issued by Keycloak will fail `iss` validation in the backends:

1. `infrastructure/keycloak/import/portal-realm.json` — `frontendUrl`
2. `docker-compose.yml` — Keycloak `--hostname-url`
3. `EXPO_PUBLIC_KEYCLOAK_URL` — in `docker-compose.yml` build args **and** `apps/user-portal-fe/.env`
4. `NEXT_PUBLIC_KEYCLOAK_URL` — in `docker-compose.yml` **and** `apps/service-portal-fe/.env`
5. `issuer-uri` in both `apps/user-portal-be/src/main/resources/application-local.yml` and `apps/service-portal-be/src/main/resources/application-local.yml` (Mode B)
6. `issuer-uri` in all three `apps/*/src/main/resources/application-local-docker.yml` files (Mode A) — `jwk-set-uri` in these files points direct to `keycloak:8080` and does not need to change with the issuer URL, but the `issuer-uri` must always match the `iss` claim

Additionally, whenever the issuer URL uses a `/keycloak` path prefix, the OpenResty `location /keycloak/` block must exist in `infrastructure/openresty/nginx.conf` to proxy the path to `keycloak:8080`.

**Migration note**: after any change to `frontendUrl`, developers must wipe the postgres volume so Keycloak re-imports the realm from the JSON file — `--import-realm` is a no-op if the realm row already exists:

```bash
docker compose down -v
```

## Choosing a mode

- Day-to-day FE or BE development with hot-reload → **Mode B** (the recommended default).
- Demoing the full stack end-to-end, or testing the gateway / openresty path → **Mode A** (`--profile full`).
