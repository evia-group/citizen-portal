FROM openresty/openresty:1.25.3.1-2-alpine-fat

# Install curl for downloading
RUN apk add --no-cache curl

# Install lua-resty-openidc and dependencies directly from source
RUN mkdir -p /usr/local/openresty/lualib/resty && \
    # Install lua-resty-openidc
    curl -L https://raw.githubusercontent.com/zmartzone/lua-resty-openidc/v1.7.6/lib/resty/openidc.lua \
         -o /usr/local/openresty/lualib/resty/openidc.lua && \
    # Install lua-resty-http
    curl -L https://raw.githubusercontent.com/ledgetech/lua-resty-http/v0.16.1/lib/resty/http.lua \
         -o /usr/local/openresty/lualib/resty/http.lua && \
    curl -L https://raw.githubusercontent.com/ledgetech/lua-resty-http/v0.16.1/lib/resty/http_headers.lua \
         -o /usr/local/openresty/lualib/resty/http_headers.lua && \
    # Install lua-resty-session
    curl -L https://raw.githubusercontent.com/bungle/lua-resty-session/v4.0.5/lib/resty/session.lua \
         -o /usr/local/openresty/lualib/resty/session.lua && \
    # Install lua-resty-jwt
    curl -L https://raw.githubusercontent.com/SkyLothar/lua-resty-jwt/v0.2.3/lib/resty/jwt.lua \
         -o /usr/local/openresty/lualib/resty/jwt.lua

### Update curl / libcurl to fix CVE-2023-27536
RUN apk del libcurl
RUN apk update && apk upgrade

# Clean up
RUN rm -rf /var/cache/apk/*
