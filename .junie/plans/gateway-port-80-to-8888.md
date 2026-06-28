---
sessionId: session-260625-122620-1nas
---

# Requirements

### Overview & Goals
Move the OpenResty gateway off the privileged host port **80** to the unprivileged host port **8888** so the gateway and the e2e harness run **rootless** (no more `sudo` socat forwards).

### Scope
**In scope**
- Publish gateway on host `8888` (`8888:80`), keep nginx internal `listen 80`.
- Update every shared bare `http://localhost` (implicit `:80`) reference to `http://localhost:8888` so the OIDC issuer string stays byte-identical across all consumers.
- Repoint the e2e harness to `:8888` and remove the privileged-port / `sudo` socat handling.

**Out of scope (leave untouched)**
- Both `application-prod.yml` `issuer-uri` (pre-existing smell → separate HTTPS/real-hostname work).
- `--hostname-admin-url=http://localhost:9080` (Keycloak admin, own publish).
- `DOWNLOAD_URL: http://localhost:8180` (own publish).
- Realm `user-portal` client `redirectUris`/`webOrigins` — already wildcards (`*`), no edit needed.
- Changing nginx internal `listen 80`.

### User Stories
- As a dev, I want the gateway on an unprivileged port so I can run the stack and e2e without root.
- As a dev, I want OIDC login + token validation to keep working after the switch.

### Functional Requirements
- Gateway reachable at `http://localhost:8888`.
- Full OIDC login flow + JWT validation works end-to-end (issuer `iss` claim matches everywhere).
- e2e happy-path runs rootless.

### Non-Functional Requirements
- Issuer string MUST be identical across FE build args, BE config, nginx OIDC, and realm `frontendUrl` — a mismatch breaks token validation.

# Technical Design

### Current Implementation
Only the `openresty` gateway binds host `:80` (`docker-compose.yml` `80:80`; `nginx.conf listen 80`). The bare `http://localhost` (implicit `:80`) issuer/API base is hardcoded across compose, nginx, realm, both FE, both BE local yml, README, and the e2e scripts (which use `sudo` socat because 80 is privileged).

### Key Decisions
- **Target host port = 8888** — high, unprivileged, no clash with published ports (8180/8181/8280/8281/9080) or the native FE `8081`.
- **Keep nginx `listen 80`**, only remap publish to `8888:80` — container-internal port needs no root; minimal change.
- **Hardcode the URL everywhere** (find/replace) — matches current style; no env-var refactor (FE bakes URLs at build time anyway).
- **Prod yml left untouched** — bare-localhost prod URL is already a placeholder; `:8888` wouldn't make it more correct.
- **e2e: repoint + drop sudo** — directly realizes the rootless driver.

### Proposed Changes — edit list (`http://localhost` → `http://localhost:8888`)
- `docker-compose.yml`
  - `openresty` ports `"80:80"` → `"8888:80"`
  - `keycloak` `--hostname-url=http://localhost/keycloak` → `:8888/keycloak`
  - `user-portal-fe` build arg `EXPO_PUBLIC_KEYCLOAK_URL`
  - `user-portal-be` env `KEYCLOAK_ISSUER_URL`
  - `service-portal-fe` env `NEXT_PUBLIC_API_URL` + `NEXT_PUBLIC_KEYCLOAK_URL`
- `infrastructure/openresty/nginx.conf` — `post_logout_redirect_uri "http://localhost"` (×2: /login, /logout) → `:8888`
- `infrastructure/keycloak/import/portal-realm.json` — `frontendUrl` → `:8888/keycloak`
- `apps/user-portal-be/.../application-local.yml` — `issuer-uri` + `jwk-set-uri`
- `apps/service-portal-be/.../application-local.yml` — `issuer-uri` + `jwk-set-uri`
- `README.md` — issuer/URL mentions
- e2e: `forwards.sh`, `run.sh`, `commons.sh`, `user-service-request-happy-path/run.sh` + `*-test-scenario.md` docs → `:8888`, **and remove sudo/privileged-port handling** in `forwards.sh`

### Risks
- Issuer string must stay byte-identical across Mode A and Mode B or tokens fail (`iss` mismatch) — every spot above changes together.
- Latent (pre-existing, also true at :80): in Mode A, containerized BEs/FE + nginx OIDC treat `localhost` as the host; inside a container `localhost:8888` won't reach openresty. May need `host.docker.internal`/`extra_hosts` — out of scope; hence Mode B chosen for verification.

# Testing

### Validation Approach
Mode B (infra-only) login E2E, rootless.

### Key Scenarios
- Bring up infra on `8888`.
- Run the `user-fe` happy-path e2e rootless (no `sudo`).
- Confirm full OIDC login + token validation succeeds end-to-end (exercises the issuer string across FE/BE/nginx).

### Edge Cases
- Grep to confirm no stray bare `http://localhost/` (implicit :80) gateway reference remains.
- Confirm admin `:9080` and download `:8180` URLs unchanged and still functional.

# Delivery Steps

###   Step 1: Switch gateway publish to 8888 and propagate issuer/API URLs
Gateway publishes on host 8888 and all shared bare-localhost URLs become http://localhost:8888 byte-identically.

- `docker-compose.yml`: `openresty` ports `"80:80"` → `"8888:80"`; keycloak `--hostname-url` → `:8888/keycloak`.
- `docker-compose.yml`: `user-portal-fe` build arg `EXPO_PUBLIC_KEYCLOAK_URL`, `user-portal-be` `KEYCLOAK_ISSUER_URL`, `service-portal-fe` `NEXT_PUBLIC_API_URL` + `NEXT_PUBLIC_KEYCLOAK_URL` → `:8888`.
- `infrastructure/openresty/nginx.conf`: both `post_logout_redirect_uri "http://localhost"` → `:8888` (keep `listen 80`).
- `infrastructure/keycloak/import/portal-realm.json`: `frontendUrl` → `:8888/keycloak`.
- `apps/user-portal-be` + `apps/service-portal-be` `application-local.yml`: `issuer-uri` + `jwk-set-uri` → `:8888`.
- `README.md`: issuer/URL mentions → `:8888`.
- Leave prod yml, `:9080` admin, `:8180` download, and realm client wildcard URIs untouched.

###   Step 2: Repoint e2e harness to 8888 and drop privileged-port handling
e2e targets host 8888 and runs rootless with no sudo/socat workaround.

- Update `:80` → `:8888` in `e2e` `forwards.sh`, `run.sh`, `commons.sh`, `user-service-request-happy-path/run.sh`.
- Update `*-test-scenario.md` docs to reference `:8888`.
- Remove the `sudo`/privileged-port socat forwarding logic from `forwards.sh`.
- Verify Mode B: bring up infra on 8888, run user-fe happy-path e2e rootless, confirm full OIDC login + token validation; grep for any stray bare `http://localhost/` gateway refs.