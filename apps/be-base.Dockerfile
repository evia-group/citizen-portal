# Shared multi-stage build for all *-be Spring Boot services.
# Parameterized by APP_NAME (e.g. service-portal-be).
# Corporate proxy is single-sourced from root .env (HTTP_PROXY/NO_PROXY), passed
# in as build-args by docker compose. The build derives Maven's settings.xml
# from these (see below); empty proxy -> no proxy config.
ARG APP_NAME

FROM maven:3.9.6-sapmachine-21 AS builder
ARG APP_NAME

COPY pom.xml .

RUN mkdir -p ./infrastructure/db
COPY infrastructure/db ./infrastructure/db

WORKDIR /apps/${APP_NAME}

COPY ./apps/${APP_NAME} .

ARG HTTP_PROXY=
ARG NO_PROXY=

# Generate ~/.m2/settings.xml
RUN mkdir -p /root/.m2; \
    if [ -n "$HTTP_PROXY" ]; then \
      PROXY_HOST="$(printf '%s' "$HTTP_PROXY" | sed -E 's#^[a-z]+://##; s#/.*$##; s#:.*$##')"; \
      PROXY_PORT="$(printf '%s' "$HTTP_PROXY" | sed -E 's#^[a-z]+://##; s#/.*$##; s#.*:##')"; \
      NON_PROXY_HOSTS="$(printf '%s' "$NO_PROXY" | sed -E 's#,#|#g')"; \
      { \
        echo '<settings><proxies>'; \
        for proto in http https; do \
          echo "<proxy><id>corporate-$proto</id><active>true</active><protocol>$proto</protocol><host>$PROXY_HOST</host><port>$PROXY_PORT</port><nonProxyHosts>$NON_PROXY_HOSTS</nonProxyHosts></proxy>"; \
        done; \
        echo '</proxies></settings>'; \
      } > /root/.m2/settings.xml; \
      export HTTP_PROXY="$HTTP_PROXY" HTTPS_PROXY="$HTTP_PROXY" NO_PROXY="$NO_PROXY"; \
      export http_proxy="$HTTP_PROXY" https_proxy="$HTTP_PROXY" no_proxy="$NO_PROXY"; \
    else \
      echo '<settings/>' > /root/.m2/settings.xml; \
    fi; \
    mvn package -DskipTests


FROM azul/zulu-openjdk:21 AS runner
ARG APP_NAME

COPY --from=builder /apps/${APP_NAME}/target/*.jar app.jar

CMD [ "java", "-jar", "app.jar", "--spring.profiles.active=prod"]
