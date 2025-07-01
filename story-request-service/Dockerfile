# ───────── Build stage ─────────
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests \
        package spring-boot:repackage

RUN cp target/*-SNAPSHOT.jar app.jar

# ─────── Runtime stage ────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 1) Copy your fat-jar
COPY --from=build /workspace/app.jar app.jar

# 2) Download the OpenTelemetry Java agent into /otel
#    pin to a version if you like, or use "latest" for bleeding edge
RUN mkdir /otel \
 && wget -qO /otel/opentelemetry-javaagent.jar \
      https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar

# 3) Copy in the entrypoint helper and make it executable
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# 4) Expose your port and default OTEL_ENABLED
EXPOSE 8080
ENV OTEL_ENABLED=false

# 5) Use the script as your entrypoint
ENTRYPOINT ["sh", "/app/entrypoint.sh"]
