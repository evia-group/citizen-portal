---
sessionId: session-260618-120113-3v59
---

# Review Findings

### Overview & Goals
Persist the code-review findings from the unstaged Docker/proxy changes as an actionable plan. Goal: resolve each finding so the proxy/OOM-fix changeset is clean, consistent, and free of unintended side effects.

### Findings

 # | File:Line | Severity | Issue |
---|-----------|----------|-------|
 1 | `docker-compose.yml:204` | High | `openresty` port mapping changed `80:80` → `8081:80`. Breaks Keycloak `--hostname-url=http://localhost/keycloak` (expects host port 80, line 20) and the baked FE URLs (`http://localhost/keycloak`, lines 66/97). Likely unintended. |
 2 | `apps/user-portal-fe/Dockerfile:47` | Low | Committed comment contains `# FIX PROVIDED FROM JUNIE:` marker. Keep only the technical explanation. |
 3 | `apps/admin-portal-be/Dockerfile:3-43` (+ `service-portal-be`, `user-portal-be`) | Medium | Proxy/`settings.xml` block duplicated verbatim across all three BE Dockerfiles. Triplicated maintenance burden. |
 4 | `apps/admin-portal-be/Dockerfile:38-39` | Medium | `settings.xml` derives both http and https proxy entries from `HTTP_PROXY` only; `HTTPS_PROXY` arg is ignored. Confirm intended or use `HTTPS_PROXY` for the https entry. |

### Scope
**In scope:** addressing findings 1, 2, 4 (concrete fixes) and finding 3 (de-duplication, if desired).
**Out of scope:** unrelated refactors, functional changes to proxy logic beyond the noted fixes.

# Resolution Plan

### Finding 1 — openresty port
- Revert `docker-compose.yml:204` from `"8081:80"` back to `"80:80"`, unless host port 80 conflict is the real motivation.
- If 8081 is intentional, the issuer/hostname URLs must be updated consistently: `keycloak` `--hostname-url` (line 20) and baked FE args (lines 66, 97) would all need `:8081`. Otherwise OIDC issuer mismatch will break login.
- Recommended: revert to `80:80` to keep the changeset focused on proxy/OOM fixes.

### Finding 2 — comment marker
- In `apps/user-portal-fe/Dockerfile`, remove line 47 `# FIX PROVIDED FROM JUNIE:` and keep the surrounding technical explanation (lines 48-51) intact.

### Finding 3 — duplicated proxy block (optional)
- The `ARG`/`ENV`/`settings.xml` generation block (lines ~3-43) is identical in `admin-portal-be`, `service-portal-be`, `user-portal-be` Dockerfiles.
- Option A (recommended): extract into a shared script (e.g. `infrastructure/proxy/gen-maven-settings.sh`) copied into each build context and invoked via a single `RUN`.
- Option B: leave as-is and accept triplication (lowest risk, no behavior change).

### Finding 4 — HTTPS_PROXY ignored
- In each BE Dockerfile, the `env-https` proxy entry (line 39) uses host/port parsed from `HTTP_PROXY`.
- Either: (a) confirm intentional (single proxy serves both schemes) and add a one-line comment, or (b) parse `HTTPS_PROXY` separately and use it for the `env-https` entry.
- Apply the same decision across all three BE Dockerfiles for consistency.

# Delivery Steps

### x Step 1: Fix openresty port mapping (Finding 1)
openresty host port is consistent with Keycloak issuer/FE URLs.

- User Edit: skip this fix, its fine like that 
- Revert `docker-compose.yml:204` `"8081:80"` → `"80:80"`.
- If 8081 is intentional instead, update `--hostname-url` (line 20) and baked FE args `EXPO_PUBLIC_KEYCLOAK_URL` (line 66) and `KEYCLOAK_ISSUER_URL` (line 97) to match, to avoid OIDC issuer mismatch.

### ✓ Step 2: Clean up user-portal-fe comment marker (Finding 2)
Committed comment contains only the technical rationale.

- Remove `# FIX PROVIDED FROM JUNIE:` line (`apps/user-portal-fe/Dockerfile:47`).
- Preserve the Expo SDK 53 / NativeWind CSS explanation (lines 48-51).

### ✓ Step 3: Resolve HTTPS_PROXY handling in BE Dockerfiles (Finding 4)
The https proxy entry in `settings.xml` reflects an explicit, documented decision.

- In `admin-portal-be`, `service-portal-be`, `user-portal-be` Dockerfiles, either parse `HTTPS_PROXY` separately for the `env-https` entry (line ~39) or add a comment stating the single-proxy assumption.
- Apply the same approach across all three for consistency.

### ✓ Step 4: De-duplicate the proxy/settings.xml block (Finding 3, optional)
The Maven proxy setup is maintained in one place instead of three.

- Extract the `settings.xml` generation logic into a shared script (e.g. `infrastructure/proxy/gen-maven-settings.sh`).
- Copy and invoke it from each BE Dockerfile via a single `RUN`.
- Skip this stage if the team prefers to keep the triplication to minimize risk.
