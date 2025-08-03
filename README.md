# Stitch your traces

Demo project to demonstrate how to use OpenTelemetry with a Kubernetes cluster and Helm charts.

I'm using this project to show how auto-instrumentation is not enough to get the full picture of your application. 
You need to add manual instrumentation to get the most out of it.
___

Article: https://python.plainenglish.io/stitch-your-traces-lets-talk-about-auto-instrumentation-distributed-tracing-019b40ca618e
___

### Repository Content

This repository consists of two services, that are used to present the example of the article.

[Story Request Service](story-request-service/README.md): A Spring Boot Java application that allows users to request a 
story from a creation service and check the status of the request later.

[AI Story Creation Service](ai-story-creation-service/README.md): A Python application that reads creation requests from
a Kafka queue, creates them using generative AI, and notifies the Story Request Service about the result.

### Deployment

These services are containerized, and for ease of deployment, I've created Helm charts for both services.

[Story Request Service Helm chart](helm-charts/story-request-service/README.md): A Helm chart for deploying the Story Request Service.

[AI Story Creation Service Helm chart](helm-charts/ai-story-creation-service/README.md): A Helm chart for deploying the AI Story Creation Service.

There's also a Helm chart for deploying both of these services along with their dependencies (MySQL and Kafka).

[Story Services Suite Helm chart](helm-charts/story-services-suite/README.md): A Helm chart for deploying the Story Services Suite.

### Observability

In order to see telemetry data from these services, I've also set up an observability suite using Grafana labs tools.

[Observability Suite Helm chart](helm-charts/observability-suite/README.md): A Helm chart for deploying the observability suite, which includes Grafana, Tempo, Prometheus, and Loki.

___