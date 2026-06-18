FROM maven:3.9.6-sapmachine-21 AS user-portal-be-builder

COPY pom.xml .

ARG HTTP_PROXY=http://webproxy.kvnbw.de:3128
ARG HTTPS_PROXY=http://webproxy.kvnbw.de:3128
ARG NO_PROXY=localhost,127.0.0.1,.dzbw.de,.kvnbw.de,10.*
ARG http_proxy=http://webproxy.kvnbw.de:3128
ARG https_proxy=http://webproxy.kvnbw.de:3128
ARG no_proxy=localhost,127.0.0.1,.dzbw.de,.kvnbw.de,10.*

ENV HTTP_PROXY=${HTTP_PROXY}
ENV HTTPS_PROXY=${HTTPS_PROXY}
ENV NO_PROXY=${NO_PROXY}
ENV http_proxy=${http_proxy}
ENV https_proxy=${https_proxy}
ENV no_proxy=${no_proxy}

COPY apps/service-portal-be/settings.xml /root/.m2/settings.xml

RUN mkdir -p ./infrastructure/db
COPY infrastructure/db ./infrastructure/db

WORKDIR /apps/user-portal-be

COPY ./apps/user-portal-be .

RUN mvn package -DskipTests


FROM azul/zulu-openjdk:21 AS user-portal-be-runner

COPY --from=user-portal-be-builder /apps/user-portal-be/target/*.jar user-portal.jar

CMD [ "java", "-jar", "user-portal.jar", "--spring.profiles.active=prod"]
