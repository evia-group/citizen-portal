[Original Ticket]

Switch port 80 to 8888

[Plan]

Switch the OpenResty gateway (the only service published on host port 80) to 8888. Because the
Keycloak issuer URL `http://localhost/keycloak` relies on the implicit port 80, every place that
encodes that gateway URL must move in lock-step to `http://localhost:8888` (the documented
"six-must-move-together" set), otherwise tokens fail `iss` validation.

1. Gateway:
   - `docker-compose.yml` openresty `ports`: `"80:80"` -> `"8888:8888"`.
   - `infrastructure/openresty/nginx.conf`: `listen 80;` -> `listen 8888;` and the two
     `post_logout_redirect_uri = "http://localhost"` -> `"http://localhost:8888"`.
2. Keycloak hostname: `docker-compose.yml` keycloak `--hostname-url=http://localhost/keycloak`
   -> `http://localhost:8888/keycloak`.
3. Issuer / gateway URLs to `http://localhost:8888`:
   - `infrastructure/keycloak/import/portal-realm.json` `frontendUrl`.
   - `docker-compose.yml`: `EXPO_PUBLIC_KEYCLOAK_URL`, `KEYCLOAK_ISSUER_URL`,
     `NEXT_PUBLIC_KEYCLOAK_URL`, `NEXT_PUBLIC_API_URL` (gateway-relative).
   - `apps/user-portal-fe/.env` `EXPO_PUBLIC_KEYCLOAK_URL`; `apps/service-portal-fe/.env`
     `NEXT_PUBLIC_KEYCLOAK_URL`.
   - `apps/user-portal-be` + `apps/service-portal-be` `application-local.yml` and
     `apps/service-portal-be` `application-prod.yml` `issuer-uri`/`jwk-set-uri`.
   - `libs/shared-fe/api/api.util.ts` default `http://localhost/api/v1`.
4. Docs: refresh gateway URLs in `README.md` (app URL, issuer, service-portal path).
5. Not changed (gateway-independent): keycloak admin `:9080`, direct BE/FE ports (`:8180`,
   `:8181`, `:8080`, `:8081`, `:8281`), `DOWNLOAD_URL`, test-base config, `user-portal-be`
   prod (env-driven via `KEYCLOAK_ISSUER_URL`).
6. Verify with a grep that no stale port-80 gateway URL (`http://localhost/`) remains.
