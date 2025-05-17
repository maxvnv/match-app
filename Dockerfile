# Stage 1: Build the application
FROM gradle:jdk21-alpine AS builder
WORKDIR /usr/src/app

# Copy gradle wrapper and build files first to leverage Docker layer caching
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Download dependencies next to leverage Docker layer caching
RUN ./gradlew dependencies --no-daemon

# Copy the rest of the source code
COPY src ./src

# Build the application (the bootJar task creates the executable JAR)
# The application.properties specifies 'match-app.jar' as the archiveFileName
RUN ./gradlew build -x test --no-daemon

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install curl
RUN apk add --no-cache curl

ARG JAR_FILE_PATH=build/libs/match-app-0.0.1-SNAPSHOT.jar
COPY --from=builder /usr/src/app/${JAR_FILE_PATH} /app/app.jar

# Application properties (like H2 console, logging, actuator) are bundled in the JAR.
# The app logs to STDOUT in JSON format, which Promtail will pick up.

EXPOSE 8080
# Optional: Expose debug port
EXPOSE 5005

# agent lis is for remote debugging
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar","/app/app.jar"]