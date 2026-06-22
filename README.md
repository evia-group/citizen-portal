# Bürgerportal / Citizen Portal

> Monorepo for the Bürgerportal / Citizen Portal project — frontend and backend apps under `apps/` and shared libraries under `libs/`.

## Getting started

_Prerequisites_: Docker, Node.js ≥ 18, Java 21, Maven.

There are two supported local-dev modes. Both use the same Keycloak issuer (`http://localhost/keycloak/realms/portal`) — tokens are
interchangeable.

### Mode A — Full Docker

Runs everything in containers, including a static Expo web export of `user-portal-fe` served by Caddy through openresty. Use this to test
the gateway path or when you don't need hot-reload.

```sh
set -a; . ./.env; set +a
docker compose --profile full up -d --build
```

- App: http://localhost — login `user` / `user`
- Keycloak admin: http://localhost:9080 — login `admin` / `admin`

After editing FE source, rebuild the bundle: `docker compose --profile full up --build user-portal-fe`.

#### Known issues:

- After all containers started, restart of service-portal-fe might be needed in case of http://localhost/service-portal-fe gives 502

### Mode B — Infra-only Docker (recommended for development)

Runs postgres, keycloak, and openresty in Docker; FE and BE run natively with hot-reload. OpenResty proxies `/keycloak` so the shared issuer
URL (`http://localhost/keycloak`) works without any config change.

```sh
npm install
docker compose up -d
npm run dev:local
```

>**Note:** for local Spring setup, for user-portal-be add server.port=8180 env variable

- User Portal FE: http://localhost:8081 — login `user` / `user`
- User Portal BE: http://localhost:8180
- Keycloak admin: http://localhost:9080 — login `admin` / `admin`

`npm run dev:local` starts `user-portal-be` (Maven) and `user-portal-fe` (Expo) concurrently with prefixed logs.

> **Note:** `npm install` is required once after cloning and again after running `npm run clean` (which deletes `node_modules`).

Native FE config lives in `apps/user-portal-fe/.env`. Note: `.env` files are excluded from the Docker build context (see `.dockerignore`) so
they do not interfere with Mode A builds.

### Other URLs (Mode A)

- [User Portal FE (direct, debug only)](http://localhost:8181)
- [Service Portal FE](http://localhost:8281)
- [Admin Portal FE](http://localhost:8381)

### Export keycloak realms with users

Modify `docker-compose.yml`:

```
command: start-dev -Dkeycloak.migration.action=export -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=/opt/keycloak/data/import/export-realms.json -Dkeycloak.migration.strategy=OVERWRITE_EXISTING
```

The export file is written to `infrastructure/keycloak/import/export-realms.json` containing all realms and users.

## Running Junie CLI in a devcontainer

You can run JetBrains' **Junie CLI** against this monorepo inside a **sandbox** via devcontainer. 

**Setup:**

1. Start devcontainer via IntelliJ or a command line: `.devcontainer/devcontainer.json`
2. In the container terminal, run `junie` → **JetBrains Account**. Complete the
   login in the browser window your client forwards back to the host.

   > **Port config:** the `/account` login uses a localhost OAuth callback that must be forwarded
   > 1:1 from host to container, otherwise the redirect URI mismatches and the token exchange fails
   > (`invalid_grant / Redirect URI mismatch`). The `.devcontainer/devcontainer.json` pins this with
   > `forwardPorts: [62345]` and a matching `portsAttributes` entry. 
   > Change the port if needed and recreate the devcontainer

3. If http://localhost:62345 is unreachable run the following in the container with a link from your callback in browser:

```
curl -s "http://localhost:62345/?code=...."
```

4. Connect to a dev container terminal:

```sh
docker exec -it -w /IdeaProjects/citizen-portal citizen-portal-devcontainer bash
```

## Corporate proxy

Set the proxy **once** in the repo-root `.env`:

```
HTTP_PROXY=http://proxy.example.com:8080
HTTPS_PROXY=http://proxy.example.com:8080
NO_PROXY=localhost,127.0.0.1
```

This single file feeds every build path:

- **devcontainer shells** — `.devcontainer/setup-proxy.sh` derives `~/.proxy.env` (HTTP(S)_PROXY, lowercase variants, NO_PROXY, `JAVA_TOOL_OPTIONS`).
- **native + container Maven** — the same script generates `~/.m2/settings.xml` (Maven's resolver ignores proxy env vars and JVM props, so `settings.xml` is required).
- **openresty image build** — `docker-compose.yml` interpolates `${HTTP_PROXY}` into the build args.
- **runtime containers** — services read `.env` via `env_file`; runtime-install services (e.g. `service-portal-fe`) also get `HTTPS_PROXY`.

Leave `HTTP_PROXY` empty for a proxy-less setup everywhere.

## Contributors

- Florian Matz - UI / UX
- Matthias Zaunseder - FE
- Mark Iarovih - BE
- Ghislain Kouete - BE
- Jimdjio Dessalli - BE
- Gennadii Switlich - BE
