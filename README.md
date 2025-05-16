# Tennis Match App

## Project Overview

The Tennis Match App aims to connect recreational tennis players within a city (initially Kraków) to find and schedule matches. Many players currently rely on inconvenient methods like Facebook groups. This application will provide a dedicated platform for proposing, finding, and recording tennis matches.

This document provides a high-level overview. For detailed business requirements, please see `REQ.md`. For a comprehensive development plan, refer to `PLAN.md`.

## Core MVP Features (Backend First)

The initial development focuses on delivering a Minimum Viable Product (MVP) for the backend system.

1.  **User Management:**
    *   Register via email/password and Gmail (OAuth).
    *   Login with credentials or Gmail.
    *   Manage user profiles: name, tennis NTRP level, home town, age, sex.

2.  **Match Operations:**
    *   Propose a match with details: date, time, optionally court and preferred opponent level.
    *   Search for available match proposals with filters (date, time, NTRP, location).
    *   Accept a match proposal.

3.  **Post-Match:**
    *   Enter match scores.
    *   View personal match history (opponent, date, score).

## Technical Stack (Backend MVP)

*   **Language/Framework:** Java 21, Spring Boot 3.4.5
*   **Build Tool:** Gradle
*   **Database:** H2 (development), PostgreSQL (production)
*   **API:** RESTful APIs with Spring WebMVC
*   **Security:** Spring Security (JWT for token-based authentication, OAuth2 for Gmail login)
*   **Data Access:** Spring Data JPA (Hibernate)
*   **Validation:** Spring Boot Starter Validation
*   **Utilities:** Lombok

## Observability

*   **Metrics:** Micrometer with Prometheus registry.
*   **Visualization:** Grafana (connected to Prometheus).
*   **Logging:** SLF4j/Logback, aiming for structured JSON logs for OpenSearch.
*   **Log Aggregation:** OpenSearch (planned).
*   **Distributed Tracing:** (Considered for future learning, e.g., Micrometer Tracing with Jaeger/Zipkin).

## Deployment (Planned)

*   **Infrastructure as Code:** Terraform
*   **Cloud Provider:** AWS
    *   Compute: ECS (Fargate) or Elastic Beanstalk
    *   Database: AWS RDS for PostgreSQL
    *   Networking: VPC, ALB
    *   Secrets: AWS Secrets Manager
    *   Logging: AWS CloudWatch Logs, AWS OpenSearch Service
    *   Monitoring: Amazon Managed Service for Prometheus, Amazon Managed Grafana (or self-hosted alternatives)
*   **CI/CD:** GitLab CI
    *   Automated build, test, and deployment pipelines.
    *   Containerization with Docker.

## Development Approach Highlights

*   **Iterative Development:** Features will be built in phases, focusing on core functionality first.
*   **Backend Focus:** The initial MVP is backend-only.
*   **Learning Oriented:** The development plan includes specific learning objectives, especially around security, observability, and AWS deployment with Terraform.
*   **Testing:** Emphasis on unit and integration testing throughout the development lifecycle.
*   **Early CI/CD & Observability:** Basic CI/CD and observability tools will be set up early in the project.
*   **Security by Design:** Security considerations are integrated into each development phase.

## Future Considerations (Post-MVP)

*   Frontend applications (iOS and React).
*   Advanced search filters.
*   Court booking integration.
*   User rating system.
*   In-app notifications and chat.

## How to Use This Repository

1.  **Business Requirements:** See `REQ.md` for a detailed breakdown of what the application aims to achieve from a user perspective.
2.  **Development Plan:** `PLAN.md` contains a phased checklist of technical tasks to build the backend MVP. This plan will be updated as development progresses.
3.  **Source Code:** The backend application code is primarily located in `src/main/java/com/tennismatch/matchapp/`.

This project is an opportunity to learn and build a useful application. The plan emphasizes a structured approach to development, incorporating best practices for testing, security, and deployment.