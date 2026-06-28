# gateway-routing Specification

## Purpose

Defines the edge contract for the openresty gateway: the port it listens on, how that port is published to the host, and the forwarded-port header it sends to upstreams. This is the single source of truth for the gateway's published origin/port; other capabilities (e.g. `e2e-environment-targeting`) reference it relationally rather than restating the literal.

## Requirements

### Requirement: Gateway listen port

The openresty gateway SHALL listen on port `8888` inside the container, matching the host-published port.

#### Scenario: Gateway accepts requests on 8888

- **WHEN** a request reaches the openresty container on port `8888`
- **THEN** the gateway server block handles it and routes to the matching upstream

#### Scenario: Gateway no longer listens on 80

- **WHEN** the openresty config is loaded
- **THEN** no server block listens on port `80`

### Requirement: Symmetric host port mapping

The docker-compose openresty service SHALL publish the gateway with a symmetric mapping `"8888:8888"`.

#### Scenario: Gateway reachable from host

- **WHEN** the stack is started with `docker compose up`
- **THEN** `http://localhost:8888` reaches the gateway in both Mode A and Mode B

### Requirement: Correct forwarded port header

The gateway SHALL forward the public port `8888` to upstreams via the `X-Forwarded-Port` header.

#### Scenario: Upstream receives public port

- **WHEN** the gateway proxies a request to any upstream
- **THEN** the `X-Forwarded-Port` header equals `8888`
