FROM node:22-slim AS user-portal-fe-build

# Bake the FE's API + Keycloak URLs into the static bundle at build time.
# Defaults target the prod/dev cluster; docker-compose overrides them for
# local Mode A (full-Docker) so the bundle calls the openresty gateway
# (`/api/v1`) and Keycloak on its host port.
ARG EXPO_PUBLIC_API_URL="https://bp-dev-dnstest.germanywestcentral.cloudapp.azure.com/api/v1"
ARG EXPO_PUBLIC_KEYCLOAK_URL="https://keycloak.eviaconsulting.com"
ENV EXPO_PUBLIC_API_URL=${EXPO_PUBLIC_API_URL}
ENV EXPO_PUBLIC_KEYCLOAK_URL=${EXPO_PUBLIC_KEYCLOAK_URL}

WORKDIR /app

COPY . .

RUN npm install

RUN npm run build:web --workspace user-portal-fe

# Belt-and-braces: verify that Expo exported at least one non-empty CSS bundle.
# Expo/Metro does not guarantee that the emitted CSS file is named `global-*.css`,
# so checking only that glob can fail even when a valid CSS bundle exists.
RUN sh -c 'set -e; \
  css_dir="./apps/user-portal-fe/dist/_expo/static/css"; \
  echo "Checking exported CSS bundles in: $css_dir"; \
  find "$css_dir" -maxdepth 1 -type f -name "*.css" -print 2>/dev/null || true; \
  f=$(find "$css_dir" -maxdepth 1 -type f -name "*.css" -size +0c -print 2>/dev/null | head -n1); \
  if [ -z "$f" ]; then \
    echo "FATAL: NativeWind/Expo CSS bundle is empty or missing after expo export"; \
    echo "Contents of dist/_expo/static, if present:"; \
    find ./apps/user-portal-fe/dist/_expo/static -maxdepth 3 -type f -print 2>/dev/null || true; \
    exit 1; \
  fi; \
  echo "Found non-empty CSS bundle: $f"'

# Belt-and-braces: verify the NativeWind CSS bundle is non-empty before the
# image is considered buildable. We use an explicit sh -c snippet rather than
# `test -s <glob>` because `test -s` takes a single operand — if expo export
# ever emits more than one matching file, busybox test returns non-zero for
# "too many args" and bash reports a usage error. The snippet picks the first
# matching file and fails loudly if it is absent or empty.
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
