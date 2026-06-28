## ADDED Requirements

### Requirement: Devcontainer scenarios reach the stack through the gateway's published port

In the devcontainer (Mode A) environment, the e2e harness SHALL target the openresty gateway on its published host port `8888`, matching the port baked into the FE bundle, Keycloak issuer, and OIDC redirect URIs. It MUST NOT target the obsolete port `80`.

#### Scenario: App target URL uses port 8888

- **WHEN** `commons.sh` resolves `APP` and the environment is `devcontainer` and no override is set
- **THEN** `APP` is `http://localhost:8888`

#### Scenario: Service-portal target URL uses port 8888

- **WHEN** `user-service-request-happy-path/run.sh` resolves `SERVICE_APP` and the environment is `devcontainer` and no override is set
- **THEN** `SERVICE_APP` is `http://localhost:8888/service-portal-fe`

#### Scenario: Host (Mode B) targeting is unchanged

- **WHEN** the environment is `host`
- **THEN** `APP` defaults to `http://localhost:8081` and `SERVICE_APP` defaults to `http://localhost:3001`, unchanged by this change

### Requirement: Devcontainer port forwards follow the gateway port and run unprivileged

The `ensure_forwards` helper SHALL forward `localhost:8888 → host.docker.internal:8888` (alongside the unchanged `9080` and `8180`) so the localhost-pinned bundle and Keycloak reach the host stack. Because `8888`, `9080`, and `8180` are all unprivileged, the forwards MUST NOT require `sudo`.

#### Scenario: Forwards bring up port 8888 instead of 80

- **WHEN** `ensure_forwards` runs in the devcontainer
- **THEN** it brings up forwards for ports `8888`, `9080`, and `8180` and brings up no forward for port `80`

#### Scenario: Forwards no longer require sudo

- **WHEN** `ensure_forwards` starts a socat forward
- **THEN** the socat process is launched without `sudo`

#### Scenario: No-op on the host

- **WHEN** `ensure_forwards` runs and the environment is `host`
- **THEN** it returns success without starting any forward
