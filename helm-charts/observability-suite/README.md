# Observability Suite Helm Chart

This chart installs the whole 

## Installing Observability Suite

```bash
helm upgrade --install observability ./ -n observability --create-namespace
```

To uninstall the suite, you can use:

```bash
helm uninstall observability --namespace observability
```

## OTel collector configuration

The definition for this is in the `otel-collector-values.yaml` file. It contains the configuration for the OpenTelemetry
Collector, including receivers, processors, exporters, and service settings.

```bash
helm upgrade --install otel-collector open-telemetry/opentelemetry-collector \
  --namespace observability --create-namespace -f ./otel-collector-values.yaml
```