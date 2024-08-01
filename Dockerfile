FROM maven:3.5-jdk-17-alpine as builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

CMD ["java", "-jar", "app/target/yupao_back-0.0.1-SNAPSHOT.jar","--server.port=2024","--spring.profiles.active=prod"]
LABEL authors="路文斌"
