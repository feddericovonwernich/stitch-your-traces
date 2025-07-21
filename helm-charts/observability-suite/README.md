# Observability Suite Helm Chart

This chart installs the whole 

## Installing Observability Suite

```bash
helm dependency update ./
```

```bash
helm upgrade --install observability ./ -n observability --create-namespace
```

To uninstall the suite, you can use:

```bash
helm uninstall observability --namespace observability
```

Get suite pods:
```bash
kubectl get pods -n observability
```

## OTel collector configuration

The definition for this is in the `otel-collector-values.yaml` file. It contains the configuration for the OpenTelemetry
Collector, including receivers, processors, exporters, and service settings.

```bash
helm upgrade --install otel-collector open-telemetry/opentelemetry-collector \
  --namespace observability --create-namespace -f ./otel-collector-values.yaml
```

## Accessing the tools in a local environment

I'm using Minikube to deploy these tools. To access them we need to take note of the cluster EXTERNAL-IP and add it to our `/etc/hosts` file.

```bash
kubectl get svc ingress-nginx-controller -n ingress-nginx -o wide
```

It will print something like this:
```
NAME                       TYPE           CLUSTER-IP       EXTERNAL-IP      PORT(S)                      AGE   SELECTOR
ingress-nginx-controller   LoadBalancer   10.111.195.188   10.111.195.188   80:31212/TCP,443:31859/TCP   16h   app.kubernetes.io/component=controller,app.kubernetes.io/instance=ingress-nginx,app.kubernetes.io/name=ingress-nginx
```

In this case the EXTERNAL-IP is: `10.111.195.188`

Add the following lines to your `/etc/hosts` file:
```
10.111.195.188       story.feddericovonwernich.com
10.111.195.188       grafana.feddericovonwernich.com
10.111.195.188       prometheus.feddericovonwernich.com
10.111.195.188       jaeger.feddericovonwernich.com
10.111.195.188       loki.feddericovonwernich.com
```