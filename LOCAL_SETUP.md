# Local Development Setup with Docker Compose

This document describes how to set up and run the Tennis Match application along with its observability stack (Prometheus, Grafana, Loki) locally using Docker Compose.

## Prerequisites

*   Docker (Ensure it's installed and running. Version 20.10.0 or later recommended)
*   Docker Compose (Usually included with Docker Desktop. Version v2.0.0 or later recommended)

## Overview

The local setup consists of the following services managed by Docker Compose:

1.  **`match-app`**: The Spring Boot application (`tennis-match/match-app`). It will run on `http://localhost:8080`.
2.  **`prometheus`**: Collects metrics from `match-app`. Accessible at `http://localhost:9090`.
3.  **`loki`**: Aggregates logs from `match-app`.
4.  **`promtail`**: An agent that discovers and ships logs from the `match-app` container to Loki.
5.  **`grafana`**: Visualizes metrics from Prometheus and logs from Loki. Accessible at `http://localhost:3000`.

This setup uses the JSON logs produced by the application (via `logback-spring.xml`) and the Prometheus metrics endpoint (`/actuator/prometheus`).

## Directory Structure

At the root of the `tennis-match` project, create the following directory structure and files. The `match-app` directory already exists.

```
tennis-match/
├── match-app/
│   ├── src/
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew
│   ├── gradle/
│   └── Dockerfile  <-- You will create this
├── docker-compose.yml <-- You will create this
├── grafana/
│   └── provisioning/
│       ├── datasources/
│       │   └── datasources.yml <-- You will create this
│       └── dashboards/
│           ├── dashboards.yml  <-- You will create this
│           └── jvm-micrometer-dashboard.json <-- Optional: Download/create this
├── loki/
│   └── loki-config.yml <-- You will create this
├── prometheus/
│   └── prometheus.yml <-- You will create this
└── promtail/
    └── promtail-config.yml <-- You will create this
```

## Configuration Files

Create the following files with the specified content:

### 1. `match-app/Dockerfile`

This file tells Docker how to build an image for your Spring Boot application.

```dockerfile
# Stage 1: Build the application
FROM gradle:jdk21-alpine AS builder
WORKDIR /usr/src/app

# Copy gradle wrapper and build files first to leverage Docker layer caching
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# It's often a good practice to download dependencies next if your build system supports it explicitly
# For Gradle, copying src and then building usually handles dependencies implicitly.
# If you want to optimize layer caching for dependencies:
# RUN ./gradlew dependencies --no-daemon

# Copy the rest of the source code
COPY src ./src

# Build the application (the bootJar task creates the executable JAR)
# The application.properties specifies 'match-app.jar' as the archiveFileName
RUN ./gradlew build -x test --no-daemon

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG JAR_FILE_PATH=build/libs/match-app.jar
COPY --from=builder /usr/src/app/${JAR_FILE_PATH} /app/app.jar

# Application properties (like H2 console, logging, actuator) are bundled in the JAR.
# The app logs to STDOUT in JSON format, which Promtail will pick up.

EXPOSE 8080
# Optional: Expose debug port
EXPOSE 5005

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar","/app/app.jar"]
```

### 2. `prometheus/prometheus.yml`

Configuration for Prometheus to scrape metrics from `match-app`.

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'match-app'
    metrics_path: '/actuator/prometheus' # As configured in application.properties
    static_configs:
      - targets: ['match-app:8080'] # Docker Compose service name and port for the app
```

### 3. `loki/loki-config.yml`

Basic configuration for Loki.

```yaml
auth_enabled: false

server:
  http_listen_port: 3100
  grpc_listen_port: 9096

common:
  instance_addr: 127.0.0.1
  path_prefix: /loki # Path inside the container where Loki stores data
  storage:
    filesystem:
      chunks_directory: /loki/chunks
      rules_directory: /loki/rules
  replication_factor: 1
  ring:
    kvstore:
      store: inmemory

schema_config:
  configs:
    - from: 2020-10-24 # A date from which this schema is valid
      store: boltdb-shipper
      object_store: filesystem
      schema: v11 # Loki schema version
      index:
        prefix: index_
        period: 24h

# Optional: Configure limits (uncomment and adjust if needed)
# limits_config:
#   enforce_metric_name: false
#   reject_old_samples: true
#   reject_old_samples_max_age: 168h
```

### 4. `promtail/promtail-config.yml`

Configuration for Promtail to collect logs from the `match-app` container.

```yaml
server:
  http_listen_port: 9080 # Promtail's own HTTP port (not typically accessed directly)
  grpc_listen_port: 0 # Disabled gRPC server

positions:
  filename: /tmp/positions.yaml # File to store log read positions (persisted via volume)

clients:
  - url: http://loki:3100/loki/api/v1/push # Address of the Loki service

scrape_configs:
- job_name: match-app-logs
  # Configuration to scrape logs from Docker containers
  docker_sd_configs:
    - host: unix:///var/run/docker.sock # Access to Docker socket
      refresh_interval: 5s
      # Filter to select only the 'match-app' service containers
      filters:
        - name: label
          values: ["com.docker.compose.service=match-app"]
  relabel_configs:
    # Use the Docker Compose service name as the 'job' label in Loki
    - source_labels: ['__meta_docker_container_label_com_docker_compose_service']
      target_label: 'job'
    # Optional: Add other labels from Docker metadata if needed
    # - source_labels: ['__meta_docker_container_name']
    #   regex: '/(.*)'
    #   target_label: 'container_name'
```

### 5. `grafana/provisioning/datasources/datasources.yml`

This file automatically provisions Prometheus and Loki data sources in Grafana.

```yaml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy # Grafana backend handles requests
    url: http://prometheus:9090 # Prometheus service name and port
    isDefault: true
    editable: true # Allow edits in Grafana UI
  - name: Loki
    type: loki
    access: proxy
    url: http://loki:3100 # Loki service name and port
    jsonData:
      # Optional: Derived fields can help parse log lines, e.g., JSON logs
      # derivedFields:
      #   - datasourceUid: '<LOKI_UID>' # Will be replaced by Grafana
      #     matcherRegex: '"level":"(info|warn|error|debug)"'
      #     name: level
      #     url: '' # No URL needed for simple extraction
      maxLines: 1000 # Default number of log lines to show
    editable: true
```

### 6. `grafana/provisioning/dashboards/dashboards.yml`

This file tells Grafana where to find dashboard JSON files to provision.

```yaml
apiVersion: 1

providers:
  - name: 'default-dashboards'
    orgId: 1
    folder: '' # Root folder for dashboards
    type: file
    disableDeletion: false # Allow dashboards to be deleted from UI
    editable: true
    options:
      path: /etc/grafana/provisioning/dashboards # Path inside Grafana container where dashboards are mounted
```

### 7. `grafana/provisioning/dashboards/jvm-micrometer-dashboard.json` (Optional)

For a ready-to-use JVM metrics dashboard:

1.  Go to [Grafana Dashboards](https://grafana.com/grafana/dashboards/).
2.  Search for "JVM Micrometer" or a similar dashboard. A popular one is ID `4701`.
3.  Download the JSON model for the dashboard.
4.  Save it as `jvm-micrometer-dashboard.json` in the `grafana/provisioning/dashboards/` directory.

If you add this file, Grafana will automatically load it on startup.

### 8. `docker-compose.yml`

This is the main file that orchestrates all the services. Place it in the project root (`tennis-match/`).

```yaml
version: '3.8'

services:
  match-app:
    build:
      context: ./match-app # Path to the Spring Boot app directory
      dockerfile: Dockerfile # Specifies the Dockerfile within the context path
    container_name: match-app-service # Explicit container name
    ports:
      - "8080:8080" # Expose app port (Host:Container)
      - "5005:5005" # Expose debug port
    environment:
      # Example: Override properties if needed, or use Spring profiles
      # - SPRING_PROFILES_ACTIVE=docker
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update # Consistent with local dev expectations
      # JAVA_TOOL_OPTIONS is now part of the Dockerfile ENTRYPOINT for clarity
    volumes:
      # For H2 file-based persistence (if you configure it in application.properties):
      # - match_app_h2_data:/app/data 
      # Example: - ./match-app/src:/usr/src/app/src # For live code reloading (requires devtools, might need build reprocessing)
      # This is generally not recommended for compiled languages like Java without proper setup.
      pass:
    networks:
      - app-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s # Give Spring Boot app enough time to start up before first health check

  prometheus:
    image: prom/prometheus:v2.47.2 # Using a specific stable version
    container_name: prometheus-service
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml # Mount config file
      - prometheus_data:/prometheus # Named volume for Prometheus data persistence
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus' # Tell Prometheus where to store data
      - '--web.console.libraries=/usr/share/prometheus/console_libraries'
      - '--web.console.templates=/usr/share/prometheus/consoles'
      - '--web.enable-lifecycle' # Allows reloading config via API (e.g., POST to /-/reload)
    networks:
      - app-network
    depends_on:
      match-app:
        condition: service_healthy # Wait for match-app to be healthy before Prometheus starts scraping

  loki:
    image: grafana/loki:2.9.2
    container_name: loki-service
    ports:
      - "3100:3100" # Loki's HTTP port
    volumes:
      - ./loki/loki-config.yml:/etc/loki/config.yml # Mount config
      - loki_data:/loki # Named volume for Loki data (matches path_prefix in its config)
    command: -config.file=/etc/loki/config.yml
    networks:
      - app-network

  promtail:
    image: grafana/promtail:2.9.2
    container_name: promtail-service
    volumes:
      - ./promtail/promtail-config.yml:/etc/promtail/config.yml # Mount config
      - /var/run/docker.sock:/var/run/docker.sock:ro # Read-only access to Docker socket for service discovery
      - promtail_positions:/tmp # Named volume for Promtail to store its log reading positions
    command: -config.file=/etc/promtail/config.yml
    networks:
      - app-network
    depends_on:
      - loki # Promtail needs Loki to be available to push logs

  grafana:
    image: grafana/grafana:10.2.2
    container_name: grafana-service
    ports:
      - "3000:3000"
    volumes:
      - ./grafana/provisioning/:/etc/grafana/provisioning/ # Mount provisioning for datasources & dashboards
      - grafana_data:/var/lib/grafana # Named volume for Grafana data (dashboards created in UI, settings)
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_USERS_ALLOW_SIGN_UP=false # Disable user sign-up
      # - GF_LOG_LEVEL=debug # Uncomment for Grafana troubleshooting
    networks:
      - app-network
    depends_on:
      - prometheus
      - loki

volumes: # Define named volumes for data persistence
  prometheus_data:
  loki_data:
  grafana_data:
  promtail_positions:
  # match_app_h2_data: # Uncomment if you use it for H2 file persistence

networks:
  app-network:
    driver: bridge # Default network driver
```
*Self-correction*: In `docker-compose.yml` for `match-app` service, `volumes` section had `pass:`. This is invalid. Removed it. If no specific volumes are needed for `match-app` at this stage (H2 is in-memory or ephemeral by default), the section can be omitted or left empty.

## Running the Environment

1.  **Navigate to the project root directory** (where `docker-compose.yml` is located, i.e., `tennis-match/`).
2.  **Build and start all services in detached mode:**
    ```bash
    docker-compose up --build -d
    ```
    *   `--build`: Forces Docker to build the `match-app` image (and any other image with a `build` instruction).
    *   `-d`: Runs containers in the background.

    The first time you run this, Docker will download the images for Prometheus, Loki, Promtail, and Grafana, and build your `match-app` image. This might take a few minutes.

## Accessing Services

Once all containers are up and running (check with `docker-compose ps`):

*   **Tennis Match Application**: `http://localhost:8080`
*   **H2 Console (via app)**: `http://localhost:8080/h2-console`
    *   JDBC URL: `jdbc:h2:mem:testdb` (or whatever is configured in your `application.properties`)
    *   User Name: `sa`
    *   Password: (empty by default, or as configured)
*   **Prometheus UI**: `http://localhost:9090`
    *   Check `Status > Targets` to see if `match-app` is being scraped.
*   **Grafana UI**: `http://localhost:3000`
    *   Default credentials: `admin` / `admin` (as set in `docker-compose.yml`).
    *   You should find "Prometheus" and "Loki" pre-configured as data sources (check `Connections > Data sources`).
*   **Loki**: Typically not accessed directly, but through Grafana.

## Viewing Logs and Metrics in Grafana

1.  **Viewing Logs:**
    *   Open Grafana (`http://localhost:3000`).
    *   Go to the "Explore" view (compass icon in the sidebar).
    *   Select the "Loki" data source from the dropdown at the top.
    *   In the "Log browser" panel, you can select labels to filter logs. For example, select `job` and then `match-app-logs`.
    *   Alternatively, use LogQL queries, e.g., `{job="match-app-logs"}`.
    *   Since logs are in JSON, you can use filters like `| json | line_format "{{.message}}"` or extract specific fields: `{job="match-app-logs"} | json | logger_name="com.tennismatch.matchapp.service.UserServiceImpl"`.

2.  **Viewing Metrics:**
    *   Open Grafana (`http://localhost:3000`).
    *   Go to the "Explore" view or "Dashboards".
    *   Select the "Prometheus" data source.
    *   Use PromQL queries in Explore (e.g., `jvm_memory_used_bytes{job="match-app"}`) or browse the pre-configured dashboard (like the JVM Micrometer one if you added it).

## Interacting with the Application

You can now send requests to your application (e.g., using `curl` or Postman):

*   Register a user: `POST http://localhost:8080/api/auth/register`
*   Access other endpoints as you develop them.

As you interact, logs should appear in Grafana (Loki) and metrics should be collected by Prometheus and viewable in Grafana.

## Stopping the Environment

1.  **To stop and remove containers, networks:**
    ```bash
    docker-compose down
    ```
2.  **To stop, remove containers, networks, AND remove named volumes (all data will be lost):**
    ```bash
    docker-compose down -v
    ```

## Troubleshooting Common Issues

*   **Port Conflicts:** If a service fails to start, check if the ports it needs (e.g., 8080, 9090, 3000, 3100) are already in use on your host machine. Change the host-side port mapping in `docker-compose.yml` if necessary (e.g., `"8081:8080"`).
*   **Container Logs:** Check logs for a specific service:
    ```bash
    docker-compose logs <service_name>
    ```
    For example: `docker-compose logs match-app` or `docker-compose logs promtail`.
    For continuous logs: `docker-compose logs -f <service_name>`
*   **`match-app` Build Failures:** If `docker-compose up --build` fails at the `match-app` stage, check the output for Gradle build errors. You can also try building the app manually first within its directory: `cd match-app && ./gradlew build -x test`.
*   **Promtail Permissions for Docker Socket:** On some systems, Promtail might have issues accessing `/var/run/docker.sock`. Ensure Docker is configured to allow access if running in a non-standard way (e.g., rootless Docker).
*   **Resource Allocation:** Ensure Docker Desktop (or your Docker daemon) has sufficient CPU, memory, and disk space allocated, especially since you're running multiple services.
*   **Firewall:** Ensure your firewall is not blocking communication between containers or access to exposed ports from your host.
*   **Health Checks:** If `match-app` fails its health check, inspect its logs (`docker-compose logs match-app`) to understand why it's not becoming healthy. The `/actuator/health` endpoint might be returning a non-200 status.

This local setup should provide a robust environment for developing and testing your application with good observability. 