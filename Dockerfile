FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY /src src
COPY pom.xml ./
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/reminderApp-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "reminderApp-0.0.1-SNAPSHOT.jar"]