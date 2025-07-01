# syntax=docker/dockerfile:1

FROM python:3.13.3-slim AS base

# Set working directory
WORKDIR /app

# Install system dependencies (if needed)
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       build-essential \
    && rm -rf /var/lib/apt/lists/*

# Copy and install Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application source
COPY src/ ./src/

# Ensure that Python modules in src are importable
ENV PYTHONPATH=/app/src

# Expose application port
EXPOSE 8000

# Default command
CMD ["uvicorn", "src.story_worker.main:app", "--host", "0.0.0.0", "--port", "8000"]
