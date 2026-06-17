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
docker compose --profile full up -d
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

## Dependency pinning & hoisting

The FE dependency tree is deliberately constrained so two incompatible stacks can coexist in one
npm-workspaces install: the **Next.js** apps (`admin-portal-fe`, `service-portal-fe`) and the
**Expo / React Native** app (`user-portal-fe`). Read this before bumping FE deps or running
`npm update`.

- **Root `devDependencies` are hoist anchors, not direct deps.** `react`/`react-dom` `19.0.0`,
  `react-native` `0.79.6`, `react-native-reanimated` `~3.17.4`, and `react-native-svg` `15.2.0`
  exist at the root to control what npm hoists to the top-level `node_modules`, so the Next apps
  resolve compatible versions. `user-portal-fe` keeps newer app-local copies where needed (e.g.
  `react-native-svg` `15.11.2`). Removing/changing these or running a blanket `npm update` can
  silently break the Next apps.
- **The `overrides` block pins versions tree-wide** (`nativewind` 4.2.4,
  `react-native-css-interop` 0.2.4, `@types/react` / `@types/react-dom`, `react-native`,
  `react-native-reanimated`). A dep bump elsewhere won't take effect until the matching override is
  updated; stale `package-lock.json` entries may need deleting when an override changes.
- **`.npmrc` sets `legacy-peer-deps=true`** — added during the FE version update to get past the
  React 19 / React Native peer-dependency conflicts that otherwise abort `npm install`. Trade-off:
  it masks future peer conflicts, so run `npm ls` after dependency changes to catch breakage.
- **`turbo` is pinned to `^1.13.3`** on purpose; the Turbo 2 migration is deferred.
- **`user-portal-fe/metro.config.js` forces single copies of `react`/`react-dom`/`scheduler`** to
  the app-local React 19 (root hoists for the Next apps), preventing the "two React copies" runtime
  crash in the Expo bundle.
- `ajv` / `ajv-keywords` were previously pinned in root devDeps but **removed** — they were
  redundant (ajv@6 is still resolved transitively via `eslint` → `@eslint/eslintrc`, and lint +
  Next builds pass without them).

## Contributors

- Florian Matz - UI / UX
- Matthias Zaunseder - FE
- Mark Iarovih - BE
- Ghislain Kouete - BE
- Jimdjio Dessalli - BE
- Gennadii Switlich - BE
