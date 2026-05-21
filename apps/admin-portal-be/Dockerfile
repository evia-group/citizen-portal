FROM maven:3.9.6-sapmachine-21 AS admin-portal-be-builder

COPY pom.xml .

RUN mkdir -p ./infrastructure/db
COPY infrastructure/db ./infrastructure/db

WORKDIR /apps/admin-portal-be

COPY ./apps/admin-portal-be .

RUN mvn package -DskipTests


FROM azul/zulu-openjdk:21 AS admin-portal-be-runner

COPY --from=admin-portal-be-builder /apps/admin-portal-be/target/*.jar admin-portal.jar

CMD [ "java", "-jar", "admin-portal.jar", "--spring.profiles.active=prod"]
