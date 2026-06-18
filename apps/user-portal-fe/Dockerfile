FROM node:22-slim AS user-portal-fe-base

# Prune the monorepo down to just the user-portal-fe workspace and its
# dependencies. Installing the *entire* monorepo here (all FEs/libs plus the
# heavy Expo/React-Native graph) blew up memory during `docker compose build`
# and got OOM-killed (`npm install` exit code 146), especially while sibling
# images build in parallel. `turbo prune` mirrors the admin-portal-fe pattern
# and keeps the install footprint to this workspace's subgraph.
FROM user-portal-fe-base AS user-portal-fe-builder
WORKDIR /app
COPY . .
RUN npx --yes turbo prune user-portal-fe --docker

# Install only the pruned subworkspace and build the static Expo web bundle.
FROM user-portal-fe-base AS user-portal-fe-build

# Bake the FE's API + Keycloak URLs into the static bundle at build time.
# Defaults target the prod/dev cluster; docker-compose overrides them for
# local Mode A (full-Docker) so the bundle calls the openresty gateway
# (`/api/v1`) and Keycloak on its host port.
ARG EXPO_PUBLIC_API_URL="https://bp-dev-dnstest.germanywestcentral.cloudapp.azure.com/api/v1"
ARG EXPO_PUBLIC_KEYCLOAK_URL="https://keycloak.eviaconsulting.com"
ENV EXPO_PUBLIC_API_URL=${EXPO_PUBLIC_API_URL}
ENV EXPO_PUBLIC_KEYCLOAK_URL=${EXPO_PUBLIC_KEYCLOAK_URL}

WORKDIR /app

# First install the dependencies (as they change less often)
COPY .gitignore .gitignore
COPY --from=user-portal-fe-builder /app/out/json/ .
COPY --from=user-portal-fe-builder /app/out/package-lock.json ./package-lock.json
RUN npm install

# Then bring in the full pruned sources and build
COPY --from=user-portal-fe-builder /app/out/full/ .
COPY turbo.json turbo.json

RUN npm run build:web --workspace user-portal-fe

# Belt-and-braces: verify the NativeWind CSS bundle is non-empty before the
# image is considered buildable. We use an explicit sh -c snippet rather than
# `test -s <glob>` because `test -s` takes a single operand — if expo export
# ever emits more than one matching file, busybox test returns non-zero for
# "too many args" and bash reports a usage error. The snippet picks the first
# matching file and fails loudly if it is absent or empty.

# Expo SDK 53 emits the merged web CSS bundle (which includes the NativeWind
# global.css output) as `web-<hash>.css`, not `global-<hash>.css`. Match the
# actual filename so the guard validates the real artefact instead of always
# tripping on a glob that never resolves.
RUN sh -c 'set -e; \
  f=$(ls ./apps/user-portal-fe/dist/_expo/static/css/web-*.css 2>/dev/null | head -n1); \
  if [ -z "$f" ] || [ ! -s "$f" ]; then \
    echo "FATAL: NativeWind CSS bundle is empty or missing after expo export"; exit 1; \
  fi'

# Caddy server
FROM caddy:2-alpine

RUN addgroup -g 101 caddy \
  && adduser -u 101 -G caddy -s /bin/sh -D caddy \
  && chown caddy:caddy /usr/bin/caddy \
  && chown caddy:caddy /config/caddy \
  && chown caddy:caddy /data/caddy

USER caddy

COPY --from=user-portal-fe-build /app/apps/user-portal-fe/dist /app/dist

# Copy Caddyfile configuration
COPY apps/user-portal-fe/Caddyfile /etc/caddy/Caddyfile

EXPOSE 8181
