FROM maven:3.9.6-sapmachine-21 AS user-portal-be-builder

COPY pom.xml .

RUN mkdir -p ./infrastructure/db
COPY infrastructure/db ./infrastructure/db

WORKDIR /apps/user-portal-be

COPY ./apps/user-portal-be .

RUN mvn package -DskipTests


FROM azul/zulu-openjdk:21 AS user-portal-be-runner

COPY --from=user-portal-be-builder /apps/user-portal-be/target/*.jar user-portal.jar

CMD [ "java", "-jar", "user-portal.jar", "--spring.profiles.active=prod"]
