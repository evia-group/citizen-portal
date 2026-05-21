FROM maven:3.9.6-sapmachine-21 AS service-portal-be-builder

COPY pom.xml .

RUN mkdir -p ./infrastructure/db
COPY infrastructure/db ./infrastructure/db

WORKDIR /apps/service-portal-be

COPY ./apps/service-portal-be .

RUN mvn package -DskipTests


FROM azul/zulu-openjdk:21 AS service-portal-be-runner

COPY --from=service-portal-be-builder /apps/service-portal-be/target/*.jar service-portal.jar

CMD [ "java", "-jar", "service-portal.jar", "--spring.profiles.active=prod"]
