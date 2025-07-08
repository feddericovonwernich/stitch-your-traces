{{/*
Return the chart name (or .Values.nameOverride if set)
*/}}
{{- define "story-services.name" -}}
{{- default .Chart.Name .Values.nameOverride }}
{{- end }}

{{/*
Generate the full name for resources:
  <release-name>-<chart-name>
Truncated to 63 chars, with a .Values.fullnameOverride override option.
*/}}
{{- define "story-services.fullname" -}}
{{- if .Values.fullnameOverride }}
  {{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
  {{- printf "%s-%s" .Release.Name (include "story-services.name" .) | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}

{{/*
Return the release name
*/}}
{{- define "story-services.releaseName" -}}
{{- .Release.Name }}
{{- end }}