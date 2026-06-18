FROM maven:3.9.6-sapmachine-21 AS admin-portal-be-builder

# Corporate-proxy support. The proxy args below are exported so the shared
# infrastructure/proxy/gen-maven-settings.sh script can turn them into a Maven
# settings.xml (see that script for details). On a proxy-less host this is a no-op.
ARG HTTP_PROXY
ARG HTTPS_PROXY
ARG NO_PROXY
ARG JAVA_TOOL_OPTIONS
ENV HTTP_PROXY=${HTTP_PROXY} \
    HTTPS_PROXY=${HTTPS_PROXY} \
    NO_PROXY=${NO_PROXY} \
    http_proxy=${HTTP_PROXY} \
    https_proxy=${HTTPS_PROXY} \
    no_proxy=${NO_PROXY} \
    JAVA_TOOL_OPTIONS=${JAVA_TOOL_OPTIONS}

# The [v] glob makes this COPY a no-op when devcontainer.env is absent.
COPY .devcontainer/devcontainer.en[v] /tmp/proxy/
COPY infrastructure/proxy/gen-maven-settings.sh /tmp/proxy/gen-maven-settings.sh

RUN sh /tmp/proxy/gen-maven-settings.sh

COPY pom.xml .

RUN mkdir -p ./infrastructure/db
COPY infrastructure/db ./infrastructure/db

WORKDIR /apps/admin-portal-be

COPY ./apps/admin-portal-be .

RUN mvn package -DskipTests


FROM azul/zulu-openjdk:21 AS admin-portal-be-runner

COPY --from=admin-portal-be-builder /apps/admin-portal-be/target/*.jar admin-portal.jar

CMD [ "java", "-jar", "admin-portal.jar", "--spring.profiles.active=prod"]
