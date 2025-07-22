## Story Services Suite Helm Chart

Create namespace if it's not created already:

```bash
kubectl create namespace story-space
```

## MySQL Configuration

Command to create secret it. This command does not apply it directly to the cluster. It just generates the file that 
we'll encode for storage in Git.

```bash
kubectl create secret generic mysql-auth \
--from-literal=mysql-root-password='rootpass' \
--from-literal=mysql-password='secretpass' \
--from-literal=mysql-replication-password=dummy \
--dry-run=client -o yaml > mysql-secret.yaml
```

Command to apply the secret, so it is in the cluster.

```bash
kubectl apply -f mysql-secret.yaml -n story-space
```

## AI Story Creation Service Configuration

If we want to use OpenAI, we need to create a secret with the API key.

```bash
kubectl create secret generic openai-api-key \
--from-literal=api-key='sk-dummykey-1234567890abcdef1234567890abcdef' \
--dry-run=client -o yaml > openai-secret.yaml
```

Command to apply the secret, so it is in the cluster.

```bash
kubectl apply -f openai-secret.yaml -n story-space
```

It is applied the same way as above secret.

## Instrumentation configuration

### Using the OpenTelemetry Kubernetes Operator

#### Install a cert-manager if there's not one already
```bash
# Add Jetstack repository
helm repo add jetstack https://charts.jetstack.io && helm repo update

# Install cert-manager
helm install cert-manager jetstack/cert-manager --namespace cert-manager --create-namespace --set crds.enabled=true
```

To check if it's running already:

```bash
kubectl get pods -n cert-manager
```

Install the operator's chart:

```bash
# Add OTel repository
helm repo add open-telemetry https://open-telemetry.github.io/opentelemetry-helm-charts && helm repo update

# Install
helm install otel-operator open-telemetry/opentelemetry-operator --namespace opentelemetry-operator-system --create-namespace
```

To check if it's running already:
```bash
kubectl --namespace opentelemetry-operator-system get pods -l "app.kubernetes.io/instance=otel-operator"
```

## Installing the Story-Services-Suite

Pull dependencies:

```bash
helm dependency update ./
```

Command to install:

```bash
helm upgrade --install story-services ./ -n story-space --create-namespace
```

Command to check pods:
```bash
kubectl get pods -n story-space
```

Command to un-install:

```bash
helm uninstall story-services -n story-space
```

Generate template for debugging:
```bash
helm template my-story ./ --namespace story-space --values ./values.yaml > template.yml
```

### Local development: Minikube setup

If running locally, your minikube cluster needs to have the images, since they're not uploaded anywhere.

Make sure Minikube is running.

```bash
minikube start
```

Point your shell to the Minikube docker daemon:
```bash
eval $(minikube docker-env)
```

Build story-request-service image:
```bash
docker build -t story-request-service:latest ../../story-request-service
```

Build ai-story-creation-service image:
```bash
docker build -t ai-story-creation-service:latest ../../ai-story-creation-service
```

### Apply OpenTelemetry instrumentation

To put the instrumentation in place, we need to apply the `instrumentation-cr.yaml` file.

```bash
kubectl apply -f ./instrumentation-cr.yaml -n story-space
```

To remove the instrumentation custom resource:
```bash
kubectl delete -f ./instrumentation-cr.yaml -n story-space
```
