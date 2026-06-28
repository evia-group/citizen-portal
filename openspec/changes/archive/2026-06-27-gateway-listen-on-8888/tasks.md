## 1. Update gateway config

- [x] 1.1 In `infrastructure/openresty/nginx.conf`, change `listen 80;` to `listen 8888;`
- [x] 1.2 In `docker-compose.yml`, change openresty `ports: "8888:80"` to `"8888:8888"`

## 2. Verify

- [x] 2.1 Recreate the gateway: `docker compose up -d --build openresty`
- [x] 2.2 Confirm `http://localhost:8888` reaches the gateway (login page / route loads) in Mode B
- [x] 2.3 Confirm an upstream receives `X-Forwarded-Port: 8888` (e.g. log/inspect a proxied request)
- [x] 2.4 Confirm no server block still listens on `80` (`grep -n "listen" infrastructure/openresty/nginx.conf`)
