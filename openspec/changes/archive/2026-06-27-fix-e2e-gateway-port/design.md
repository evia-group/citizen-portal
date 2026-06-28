## Context

The e2e harness has two environment paths, resolved in `forwards.sh` (`e2e_env_kind`) and consumed in `commons.sh`:
- **host (Mode B):** native FE on `:8081`, infra in Docker; `localhost` already reaches everything — forwards are a no-op.
- **devcontainer (Mode A):** the FE bundle, Keycloak issuer, and OIDC redirect URIs are pinned to a single gateway origin. Inside the container `localhost` is the container itself, so `ensure_forwards` uses `socat` to forward `localhost:<port> → host.docker.internal:<port>` for the gateway plus Keycloak (`9080`) and BE (`8180`).

The `gateway-listen-on-8888` change repinned that gateway origin from `http://localhost` (host port `80`, mapping `80:80`) to `http://localhost:8888` (mapping `8888:8888`). The devcontainer e2e scripts were written against the old origin and still forward/target port `80`, which now reaches nothing.

## Goals / Non-Goals

**Goals:**
- Make the devcontainer e2e path target the gateway on its current published port `8888`.
- Keep the forward set otherwise identical (`9080`, `8180`) and behaviour-preserving.
- Remove the now-unnecessary `sudo` on the forwards.

**Non-Goals:**
- Changing the host/Mode B path or the `E2E_ENV` / `KC`/`API`/`APP` override escape hatches.
- Any change to scenario logic, screenshots, or assertions.
- Re-litigating the gateway port decision (owned by `gateway-listen-on-8888`).

## Decisions

- **Forward and target `8888`, not `80`.** The origin the bundle/issuer/redirects use is the single source of truth; the harness must mirror it. Alternative — keep forwarding `80` and add a host-side `8888→80` shim — rejected: nothing publishes `80` anymore and it reintroduces the port mismatch the gateway change removed.
- **Drop `sudo` from the socat forwards.** `sudo` existed solely because `80` is a privileged port. `8888`/`9080`/`8180` are all ≥1024, so unprivileged socat works and we avoid depending on passwordless-sudo being granted in the container. Alternative — keep `sudo` defensively — rejected: it's dead weight and an extra failure mode.
- **No central port constant.** The port literal already lives inline in both `forwards.sh` (the `for port in …` loops) and the per-env `APP`/`SERVICE_APP` defaults; matching the existing style keeps the diff minimal and reviewable. The handful of sites are enumerated in tasks.

## Risks / Trade-offs

- [Stale gateway: change applied to scripts before `gateway-listen-on-8888` is actually running on `8888`] → This change depends on that one; verify the gateway responds on host `8888` before running e2e (`curl http://localhost:8888`).
- [A forwarded port already bound by something else in the container] → `_forward_up` already makes `ensure_forwards` idempotent and skips ports that are up; behaviour unchanged.
- [Missed port-80 reference in a comment or scenario doc] → grep `e2e/` for `:80` / `localhost\b` (no port) after editing; comments are non-functional but should not mislead.
- Rollback: revert the script edits (port `8888`→`80`, restore `sudo`, `APP`/`SERVICE_APP` back to `http://localhost`).

## Spec granularity (for archiving — explore note)

> Captured during `/opsx-explore`. This is about the form the delta should take **when promoted into a living `specs/e2e-environment-targeting/spec.md`**, not about the shipped change (whose delta intentionally mirrors the code line-for-line).

The shipped delta's scenarios map ~1:1 onto specific bash lines (`commons.sh` `APP=…:8888`, `forwards.sh` `for port in 8888 9080 8180`, `setsid socat` without `sudo`). That literal form is fine for a one-shot change record, but as a **living** spec it has two smells:

- **Duplicates `gateway-routing`.** Both specs hardcode `8888`; a future port move forces edits in two living specs that can silently drift. The durable e2e requirement is *relational*: e2e MUST target **whatever origin/port `gateway-routing` publishes**, not a literal.
- **Encodes HOW, not WHAT.** `socat`/`setsid`/`host.docker.internal`/`E2E_IN_DEVCONTAINER` and the exact port list are mutable mechanism. The durable intents are: (a) origin-parity with the FE bundle/issuer/redirects, (b) per-env resolution (Mode A gateway vs Mode B native), (c) host no-op, (d) unprivileged forwards (no `sudo`, no <1024 ports), (e) per-scenario `KC/API/APP` override wins — the override was only prose before and should be a first-class requirement.

Reframed shape to use **at archive time** (literals removed; cross-refs `gateway-routing`):

```markdown
### Requirement: e2e targets the active environment's reachable origin
The harness SHALL resolve APP/SERVICE_APP/KC/API to the origin where each service
is actually reachable in the detected environment, matching the origin the FE
bundle / Keycloak issuer / OIDC redirects are pinned to. Literal ports are NOT
fixed here; the gateway's published port is owned by `gateway-routing`.
  - Scenario: devcontainer (Mode A) → gateway origin from `gateway-routing`
  - Scenario: host (Mode B) → native dev origins, not the gateway
  - Scenario: per-scenario KC/API/APP/SERVICE_APP override always wins

### Requirement: Host-port forwards are environment-scoped and unprivileged
In `devcontainer` forward each required localhost port to the host using only
unprivileged ports; on the `host` be a no-op.
  - Scenario: forwards established only in the devcontainer
  - Scenario: forwards require no elevated privileges (no sudo, no port <1024)
  - Scenario: no-op on the host
```

Trade-off: softer wording trades catch-the-port-regression precision for survives-the-next-port-move durability — the right trade for a living spec (the test code still pins the literal), and it adds the missing override-precedence requirement. Open coupling: the cross-ref to `gateway-routing` is not tool-enforced.
