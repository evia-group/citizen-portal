# e2e-environment-targeting Specification

## Purpose

Defines how the e2e harness resolves where each service is reachable across the two dev environments (Mode A devcontainer / Mode B host) and how it bridges devcontainer-local ports to the host stack. Captures the durable intent — origin-parity, per-environment resolution, host no-op, unprivileged forwards, and per-scenario override precedence — without pinning literal ports or tooling. The gateway's published origin/port is owned by `gateway-routing` and referenced relationally here.

## Requirements

### Requirement: e2e targets the active environment's reachable origin

The e2e harness SHALL resolve each target URL (`APP`, `SERVICE_APP`, `KC`, `API`) to the origin where that service is actually reachable in the detected environment, matching the origin the FE bundle, Keycloak issuer, and OIDC redirect URIs are pinned to. Literal ports are NOT fixed by this spec; the gateway's published port is owned by `gateway-routing`.

#### Scenario: Devcontainer (Mode A) routes through the gateway

- **WHEN** the environment is `devcontainer` and no per-scenario override is set
- **THEN** `APP` and `SERVICE_APP` resolve to the gateway origin published by `gateway-routing`, the same host:port baked into the FE bundle and Keycloak issuer

#### Scenario: Host (Mode B) targets the native services directly

- **WHEN** the environment is `host` and no per-scenario override is set
- **THEN** `APP` and `SERVICE_APP` resolve to the native dev origins, not the gateway

#### Scenario: Per-scenario override always wins

- **WHEN** a scenario sets `KC`, `API`, `APP`, or `SERVICE_APP` before sourcing `commons.sh`
- **THEN** the harness uses those values unchanged in any environment

### Requirement: Host-port forwards are environment-scoped and unprivileged

In the `devcontainer` the harness SHALL forward each required `localhost` port to the host so localhost-pinned artifacts reach the host stack, using only unprivileged ports so no elevated privileges are required. On the `host` it SHALL be a no-op.

#### Scenario: Forwards established only in the devcontainer

- **WHEN** `ensure_forwards` runs in the `devcontainer`
- **THEN** a forward is established for every required host-stack port

#### Scenario: Forwards require no elevated privileges

- **WHEN** a forward is established
- **THEN** it starts without `sudo` and without depending on any privileged port (`<1024`)

#### Scenario: No-op on the host

- **WHEN** `ensure_forwards` runs and the environment is `host`
- **THEN** it returns success without establishing any forward
