# ---------- STAGE 1: build ----------
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY webapp/pom.xml webapp/
COPY agent/pom.xml agent/
COPY cleaner/pom.xml cleaner/
COPY .mvn .mvn
COPY mvnw .

RUN chmod +x mvnw

# Copiar apenas o código do webapp para o build
COPY webapp/src webapp/src

# Build apenas do módulo webapp
RUN ./mvnw clean verify -pl webapp -am

# ---------- STAGE 2: runtime ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/webapp/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=prod,h2", "-jar", "app.jar"]