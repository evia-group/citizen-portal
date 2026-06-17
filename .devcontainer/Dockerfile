FROM mcr.microsoft.com/devcontainers/java:1-21-bookworm

USER root
ARG NODE_VERSION=22.11.0
RUN ARCH=$(dpkg --print-architecture) \
    && case "$ARCH" in \
         amd64) NODEARCH=x64 ;; \
         arm64) NODEARCH=arm64 ;; \
         *) echo "unsupported arch: $ARCH" >&2; exit 1 ;; \
       esac \
    && curl -fsSL "https://nodejs.org/dist/v${NODE_VERSION}/node-v${NODE_VERSION}-linux-${NODEARCH}.tar.xz" -o /tmp/node.tar.xz \
    && tar -xJf /tmp/node.tar.xz -C /usr/local --strip-components=1 \
    && rm /tmp/node.tar.xz \
    && node --version && npm --version

USER vscode
RUN bash -o pipefail -c 'curl -fsSL https://junie.jetbrains.com/install.sh | bash' \
    && test -x /home/vscode/.local/bin/junie

USER root
# `playwright` provides `install-deps` (system libs for Chromium); `@playwright/cli`
# provides the `playwright-cli` binary the e2e harness (e2e/commons.sh) drives.
RUN rm -f /etc/apt/sources.list.d/yarn.list \
    && npm install -g playwright @playwright/cli \
    && playwright install-deps chromium \
    && apt-get update \
    && apt-get install -y --no-install-recommends socat \
    && rm -rf /var/lib/apt/lists/*

# Install the browser used by playwright-cli. Its default "chrome" channel has no
# Linux/arm64 build, so we install the bundled Chromium ("chrome-for-testing");
# e2e/commons.sh pins playwright-cli to it via browserName=chromium.
USER vscode
RUN playwright-cli install-browser chrome-for-testing \
    && test -d /home/vscode/.cache/ms-playwright
