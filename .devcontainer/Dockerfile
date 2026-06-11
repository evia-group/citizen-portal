# Devcontainer image for running JetBrains' Junie CLI against this monorepo in a
# host-isolated sandbox. Ships the full static+unit toolchain: JDK 21 + Maven (base
# image), Node 22 (devcontainer feature, see devcontainer.json), and Junie CLI.
#
# Base image provides the Microsoft Build of OpenJDK 21 at /usr/lib/jvm/msopenjdk-current
# (JAVA_HOME pinned to it in devcontainer.json), Maven, git, and a ready-made non-root
# `vscode` user. Non-root matters: edits land in the bind-mounted workspace as the host
# user, not root.
FROM mcr.microsoft.com/devcontainers/java:1-21-bookworm

# Install Junie CLI as the non-root `vscode` user so the binary lands on that user's
# PATH (~/.local/bin) and auth/config is written to /home/vscode/.junie (persisted as a
# named volume). pipefail is mandatory: without it a failed curl (e.g. proxy not passed
# as build arg) feeds bash empty input, the RUN exits 0, and the image silently ships
# without Junie ("junie: command not found"). The trailing check catches an installer
# that ran but didn't produce the binary.
USER vscode
RUN bash -o pipefail -c 'curl -fsSL https://junie.jetbrains.com/install.sh | bash' \
    && test -x /home/vscode/.local/bin/junie
