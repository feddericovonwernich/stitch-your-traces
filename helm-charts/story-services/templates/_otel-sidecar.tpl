{{- define "otel-sidecar" -}}
{{- if .Values.otelCollector.enabled }}
- name: opentelemetry-collector
  image: {{ .Values.otelCollector.image.repository }}:{{ .Values.otelCollector.image.tag }}
  imagePullPolicy: {{ .Values.otelCollector.image.pullPolicy }}
  args:
    - "--config=/conf/otel-config.yaml"
  volumeMounts:
    - name: otel-config
      mountPath: /conf
  ports:
    - containerPort: 4317
      name: otlp-grpc
    - containerPort: 4318
      name: otlp-http
{{- end }}
{{- end }}
