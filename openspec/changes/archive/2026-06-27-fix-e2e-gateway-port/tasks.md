## 1. Forwards (`e2e/forwards.sh`)

- [x] 1.1 Change both `for port in 80 9080 8180` loops to `for port in 8888 9080 8180`
- [x] 1.2 Remove `sudo` from the `socat TCP-LISTEN` invocation
- [x] 1.3 Update header comments: `localhost:{80,9080,8180}` → `{8888,9080,8180}`; drop the "Port 80 is privileged, so socat runs under sudo" note

## 2. Target URLs (`e2e/commons.sh`)

- [x] 2.1 Set the devcontainer `APP` default to `http://localhost:8888` (was `http://localhost`)
- [x] 2.2 Update the comment referencing the gateway on the host's `:80`

## 3. Service-portal scenario (`e2e/user-service-request-happy-path/run.sh`)

- [x] 3.1 Set `SERVICE_APP` devcontainer default to `http://localhost:8888/service-portal-fe`
- [x] 3.2 Update comments referencing `:80` / forwarded `localhost:80`

## 4. Remaining references (`e2e/run.sh`)

- [x] 4.1 Update the comment about scenarios racing on the privileged port `:80`

## 5. Verify

- [x] 5.1 Confirm gateway responds on host `8888`: `curl http://localhost:8888` → HTTP 200; port `80` refused
- [x] 5.2 `grep -rn ':80\b' e2e/` returns no functional (non-comment) port-80 references
- [x] 5.3 Run the devcontainer e2e suite (`e2e/run.sh`) and confirm the user-fe and service-request scenarios reach login and pass — requires the devcontainer runtime (Mode A); verified from host: all scripts pass `bash -n`, and forcing `E2E_ENV=devcontainer` resolves `APP`/`SERVICE_APP` to `http://localhost:8888`. Closed at archive time with host-only verification accepted (no live Mode A runtime available); a live run is the open follow-up.
