#!/bin/sh
set -e

if [ "${OTEL_AGENT_ENABLED:-false}" = "true" ]; then
  echo "🔶 OpenTelemetry agent enabled, launching with -javaagent"
  exec java \
    -javaagent:/otel/opentelemetry-javaagent.jar \
    -jar app.jar
else
  echo "⚪️ OpenTelemetry agent disabled, launching normally"
  exec java -jar app.jar
fi