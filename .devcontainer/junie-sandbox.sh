#!/usr/bin/env bash
# Start the Junie CLI sandbox container without devcontainer tooling — plain docker
# replica of .devcontainer/devcontainer.json (image, Node 22 feature, named volumes,
# JAVA_HOME, auth-callback port 62345, postCreateCommand).
#
# Usage: .devcontainer/junie-sandbox.sh [--rebuild]
#   --rebuild  remove the existing container and rebuild the image first
#
# First run inside the container: `junie` → JetBrains Account login (callback must
# print http://localhost:62345 — the port is published 1:1 for that reason).
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
IMAGE=citizen-portal-junie-sandbox
CONTAINER=citizen-portal-junie-sandbox
WORKDIR=/workspaces/citizen-portal

if [[ "${1:-}" == "--rebuild" ]]; then
  docker rm -f "$CONTAINER" 2>/dev/null || true
  docker rmi "$IMAGE" 2>/dev/null || true
fi

# Reattach if the container already exists (volumes keep Junie auth + caches anyway,
# but reusing the container also keeps the warmed workspace state).
if docker container inspect "$CONTAINER" >/dev/null 2>&1; then
  echo ">> Reattaching to existing container $CONTAINER"
  docker start "$CONTAINER" >/dev/null
  exec docker exec -it -w "$WORKDIR" "$CONTAINER" bash
fi

if ! docker image inspect "$IMAGE" >/dev/null 2>&1; then
  echo ">> Building $IMAGE (devcontainer Dockerfile + Node 22)"
  # The devcontainer gets Node 22 from the ghcr.io node feature; plain docker has no
  # feature mechanism, so install 22 via the base image's nvm and make it the default
  # (the base ships Node 18 as nvm default, which would shadow a system install).
  base_image="$IMAGE:base"
  docker build -t "$base_image" "$REPO_ROOT/.devcontainer"
  docker build -t "$IMAGE" - <<EOF
FROM $base_image
USER vscode
RUN bash -lc '. /usr/local/share/nvm/nvm.sh && nvm install 22 && nvm alias default 22'
EOF
fi

echo ">> Starting $CONTAINER (workspace bind-mounted at $WORKDIR)"
# Mirrors devcontainer.json: named volumes for ~/.junie (auth), ~/.m2, ~/.npm;
# JAVA_HOME pinned; 62345 published 1:1 for the Junie OAuth loopback redirect.
docker run -dit --name "$CONTAINER" \
  --user vscode \
  -v "$REPO_ROOT":"$WORKDIR" \
  -v citizen-portal-junie:/home/vscode/.junie \
  -v citizen-portal-m2:/home/vscode/.m2 \
  -v citizen-portal-npm:/home/vscode/.npm \
  -p 62345:62345 \
  -e JAVA_HOME=/usr/lib/jvm/msopenjdk-current \
  -w "$WORKDIR" \
  "$IMAGE" bash >/dev/null

# postCreateCommand equivalent: named volumes mount root-owned → chown once, then
# warm dependency caches (failure-tolerant, same as devcontainer.json).
echo ">> Running one-time setup (chown volumes, npm install, maven go-offline)"
docker exec -it -w "$WORKDIR" "$CONTAINER" bash -c '
  sudo chown -R vscode:vscode /home/vscode/.junie /home/vscode/.m2 /home/vscode/.npm
  npm install || true
  ./mvnw -q -DskipTests dependency:go-offline || true
'

exec docker exec -it -w "$WORKDIR" "$CONTAINER" bash
