# Technical Documentation: Tennis Matchmaker App

This document provides a technical overview, a detailed development plan, and reasoning for key technical decisions for the Tennis Matchmaker application. It serves as a reference for development and a guide for the AI agent assisting with the project.

## 1. High-Level Technical Overview

### 1.1. Project Goal
The primary goal is to develop a backend service for a tennis matchmaking application. This backend will eventually serve a React frontend and a mobile iOS application. A significant secondary goal is to serve as a learning platform for various technologies and best practices, including security, cloud deployment, and observability.

### 1.2. Technology Stack
* **Programming Language:** Java 21
* **Framework:** Spring Boot (currently 3.3.x or latest stable)
* **Build Tool:** Gradle
* **Database:**
    * **Initial Development:** H2 (In-Memory)
    * **Production (Planned):** PostgreSQL
* **Persistence:** Spring Data JPA (with Hibernate as the provider)
* **Security:** Spring Security
* **API:** RESTful APIs
* **Deployment (Planned):** AWS (using EC2/ECS/Fargate)
* **Infrastructure as Code (Planned):** Terraform
* **Observability (Planned):**
    * **Metrics:** Prometheus & Grafana
    * **Logging:** OpenSearch/Kibana (or ELK stack)
* **Packaging:** Jar

### 1.3. Architecture
The application follows a standard layered architecture:
* **Controller Layer:** Handles incoming HTTP requests, delegates to the service layer. (Partially implemented)
* **Service Layer:** Contains business logic, orchestrates calls to repositories and other services. (User service partially implemented)
* **Repository Layer:** Responsible for data access using Spring Data JPA. (Implemented for core entities)
* **Model Layer:** Defines JPA entities representing the application's data. (Implemented for core entities)
* **DTO Layer:** Data Transfer Objects for API communication, including validation. (UserRegistrationDto implemented)
* **Configuration Layer:** Spring configuration classes (e.g., `SecurityConfig`). (Basic `SecurityConfig` for `PasswordEncoder` implemented)

### 1.4. Current Code Status (Brief)
* **Project Setup:** Spring Boot project initialized with Gradle and Java 21.
* **Data Models:** `User`, `PlayProposal`, `Match` entities created.
* **Repositories:** `UserRepository`, `PlayProposalRepository`, `MatchRepository` created.
* **User Service:** Basic `UserService` for registration (with username/email checks and password hashing) implemented.
* **DTOs:** `UserRegistrationDto` with input validation.
* **Security:** `PasswordEncoder` bean (`BCryptPasswordEncoder`) is configured. Full security setup (filters, auth manager) is pending.
* **Controllers:** `UserController` is planned, other controllers pending.

## 2. Detailed Development Plan & Checklist

This plan integrates existing tasks with new requirements for AWS deployment, observability, enhanced security, and learning.

**(Legend: [x] - Completed, [ ] - To Do, [L] - Learning Focus: AI to provide detailed explanation)**

### Phase 1: Project Setup & Basic Structure (Mostly Completed)
* [x] Initialize Spring Boot project with Gradle, Java 21.
* [x] Define core application idea and functionalities.
* [x] Set up initial `README.md` and `DEVELOPMENT_LOG.md`.
* [x] Define initial data models (`User`, `PlayProposal`, `Match`).
    * Reasoning: Entities map directly to core business concepts. JPA annotations provide ORM capabilities.
* [x] Set up H2 in-memory database for initial development.
    * Reasoning: Quick to set up for local development, no external dependencies. Good for prototyping.
* [x] Add core dependencies: Spring Web, Spring Data JPA, H2, Spring Security, Lombok, Spring Boot DevTools, Validation.
    * Reasoning: Standard Spring Boot starters for common functionalities. Lombok reduces boilerplate. DevTools improve developer experience. Validation for data integrity.
* [x] Implement `UserRepository`, `PlayProposalRepository`, `MatchRepository`.
    * Reasoning: Spring Data JPA simplifies DAO layer implementation significantly.
* [x] Implement `UserRegistrationDto`.
    * Reasoning: Decouples API request structure from entity model, allows for specific validation.
* [x] Implement basic `UserService` (`registerUser`, `findByUsername`, `findByEmail`) including password encoding with `BCryptPasswordEncoder`.
    * Reasoning: Encapsulates user registration logic. BCrypt is a strong, widely accepted hashing algorithm.
* [x] Implement `UserController` to expose registration endpoint.
    * [L] Explain REST controller basics, request mappings, request bodies, response entities.

### Phase 2: Core Feature Implementation
* [ ] **User Login & Authentication Foundation:**
    * [ ] Implement `UserDetailsService` in Spring Security.
        * Reasoning: Core interface in Spring Security for loading user-specific data.
    * [ ] Configure `SecurityFilterChain` for basic username/password authentication (e.g., form login or basic auth for now).
        * [L] Explain `SecurityFilterChain`, `AuthenticationManager`, and how Spring Security processes authentication requests.
    * [ ] Implement login endpoint in `UserController`.
    * [ ] Secure endpoints: Ensure only authenticated users can access protected resources initially.
* [ ] **Play Proposal Management:**
    * [ ] Implement `PlayProposalService` for CRUD operations.
    * [ ] Implement `PlayProposalController` for:
        * [ ] Creating Play Proposals (authenticated users only).
        * [ ] Viewing own Play Proposals.
        * [ ] Updating own Play Proposals.
        * [ ] Deleting/Cancelling own Play Proposals.
    * [L] Discuss input validation for proposal fields (dates, times, locations).
* [ ] **Browse and Filter Play Proposals:**
    * [ ] Implement functionality in `PlayProposalService` to retrieve active proposals.
    * [ ] Add filtering capabilities (by date, tennis level, location) to the service.
        * [L] Explain JPA query methods, `Specification` API, or Querydsl for dynamic queries.
    * [ ] Expose Browse and filtering endpoints in `PlayProposalController` (authenticated users only).
* [ ] **Match Request & Confirmation Workflow:**
    * [ ] Implement `MatchService` for:
        * Creating a match request (linking `User` to `PlayProposal`).
        * Viewing match requests for a user's proposal.
        * Accepting a match request (updating `PlayProposal` status, `Match` status).
        * Declining a match request.
    * [ ] Implement `MatchController` for:
        * Sending a match request (authenticated users only).
        * Viewing/Managing incoming match requests (for proposal owner).
    * [L] Discuss transaction management (`@Transactional`) for workflows involving multiple entity updates.

### Phase 3: Security Implementation - Iteration 1 (Robust Authentication & Basic Authorization)
* [x] Integrate Spring Security (initial setup done).
* [x] Implement robust password hashing (e.g., BCrypt - done).
* [ ] **Token-Based Authentication (JWT):**
    * [ ] Add JWT library dependency (e.g., `jjwt`).
    * [ ] Implement JWT generation upon successful login.
        * [L] Explain JWT structure (header, payload, signature), claims, and best practices for choosing an algorithm (e.g., HMAC SHA256/512 or RSA).
    * [ ] Implement a JWT authentication filter to validate tokens from incoming requests.
        * [L] Explain Spring Security filter chain customization and how to integrate custom filters.
    * [ ] Configure Spring Security to use JWT for stateless authentication.
        * Reasoning for JWT: Stateless, good for APIs serving multiple clients (web, mobile), avoids session management on the server for scalability. Alternatives: Session-based (stateful, more server load), PASETO (more secure alternative to JWT, less widespread).
    * [ ] Implement token refresh mechanism (optional but good practice).
* [ ] **Basic Authorization:**
    * [ ] Secure service methods or controller endpoints using `@PreAuthorize` or `SecurityFilterChain` rules.
    * [ ] Ensure users can only manage their own proposals/matches/profile.
        * [L] Explain method-level security and expression-based access control in Spring Security.
* [ ] **Input Validation and Sanitization (Continuous effort):**
    * [ ] Rigorously apply validation annotations (`jakarta.validation.constraints`) on all DTOs and inputs.
        * Reasoning: Prevents common vulnerabilities like Injection (by ensuring type safety, format), and ensures data integrity. JPA helps against SQLi but validation is defense in depth.
    * [ ] [L] Discuss output encoding if directly rendering HTML from backend (less common for APIs, but good to know for XSS prevention).

### Phase 4: Initial AWS Deployment with Terraform & PostgreSQL Migration
* [L] **Introduction to AWS & Terraform:**
    * AI Agent: Explain core AWS services relevant to this app (EC2, RDS, S3, IAM, VPC).
    * AI Agent: Introduce Infrastructure as Code (IaC) principles and Terraform basics.
* [ ] **Database Migration to PostgreSQL:**
    * [ ] Set up a local PostgreSQL instance for development/testing.
        * Reasoning: Align local dev with planned production DB.
    * [ ] Update `application.properties` (or profiles) for PostgreSQL connection.
    * [ ] Add PostgreSQL JDBC driver dependency.
    * [ ] Test application with PostgreSQL.
* [ ] **Terraform - AWS Infrastructure Setup:**
    * [ ] Install Terraform.
    * [ ] Configure AWS provider in Terraform.
    * [ ] **VPC Setup:**
        * [ ] Create a VPC, subnets (public/private), internet gateway, NAT gateway, route tables.
            * [L] Explain VPC fundamentals and secure network design.
            * Reasoning: Provides a secure, isolated network environment in AWS.
    * [ ] **RDS for PostgreSQL:**
        * [ ] Define an RDS instance for PostgreSQL in Terraform.
        * [ ] Configure security groups for RDS (allow access from application, restrict public access).
        * [ ] Manage database credentials securely (e.g., AWS Secrets Manager, integrated with Terraform).
            * [L] Importance of not hardcoding secrets.
            * Reasoning for RDS: Managed database service, handles patching, backups, scaling. Alternative: Self-hosting PostgreSQL on EC2 (more operational overhead).
    * [ ] **IAM Roles & Policies:**
        * [ ] Create IAM roles for EC2 instances/ECS tasks to securely access other AWS services (e.g., S3, Secrets Manager) without hardcoding credentials.
            * [L] Principle of least privilege.
* [ ] **Application Deployment Strategy (Choose one or evolve):**
    * **Option 1: EC2 Instance + Systemd/Docker**
        * [ ] Create an EC2 instance configuration in Terraform (Amazon Linux 2 or similar).
        * [ ] Configure security groups for EC2 (allow HTTP/S, SSH from trusted IPs).
        * [ ] User data script for bootstrapping (install Java, Docker if used).
        * [ ] Manually deploy the application JAR (or Docker image) initially.
        * [L] Discuss pros/cons: Simpler start, more manual.
    * **Option 2: Dockerize the Application & ECS/Fargate (Recommended for scalability)**
        * [ ] Create a `Dockerfile` for the Spring Boot application.
            * [L] Best practices for Dockerizing Spring Boot apps (multi-stage builds, non-root user).
        * [ ] Push Docker image to Amazon ECR (Elastic Container Registry).
            * [ ] Define ECR repository in Terraform.
        * [ ] Set up ECS Cluster, Task Definition, and Service (using Fargate launch type) in Terraform.
            * [L] Explain ECS concepts and Fargate (serverless compute for containers).
            * Reasoning for Fargate: No EC2 instance management, scales well. Alternative: ECS on EC2 (more control, more management).
* [ ] **CI/CD Pipeline (Basic - Future Enhancement, but plan for it):**
    * [ ] Think about how a CI/CD pipeline (e.g., GitHub Actions, AWS CodePipeline) would integrate with Terraform and application deployment. (No implementation yet, just conceptual).
* [ ] **Configure Application for AWS:**
    * [ ] Use Spring Profiles for different environments (local, dev, prod).
    * [ ] Externalize configuration (DB URL, credentials) using AWS Systems Manager Parameter Store or Secrets Manager, accessed by the application.
* [ ] **Initial Deployment & Testing:**
    * [ ] Run `terraform apply`.
    * [ ] Verify infrastructure is created.
    * [ ] Deploy the application.
    * [ ] Test API endpoints on AWS.

### Phase 5: Observability Setup
* [L] **Introduction to Observability (Metrics, Logging, Tracing):**
    * AI Agent: Explain the importance of observability for monitoring application health, performance, and troubleshooting.
* [ ] **Logging with OpenSearch/Kibana (or ELK):**
    * [ ] Add logging dependencies (`logstash-logback-encoder` or similar for structured logging).
    * [ ] Configure Logback to output JSON formatted logs.
        * Reasoning: Structured logs are easier to parse and search.
    * [ ] Set up AWS OpenSearch Service (managed OpenSearch/Elasticsearch) using Terraform.
        * Reasoning: Managed service, integrates well with AWS. Alternative: Self-host ELK stack (complex).
    * [ ] Configure the application (or a log forwarder like FluentBit/Filebeat if running on EC2/ECS) to send logs to OpenSearch.
        * [L] Discuss different log shipping strategies.
    * [ ] Access logs via OpenSearch Dashboards (Kibana).
* [ ] **Metrics with Prometheus & Grafana:**
    * [ ] Add Spring Boot Actuator and Micrometer Prometheus registry dependencies.
        * Reasoning: Actuator exposes metrics, Micrometer is the facade, Prometheus registry formats for Prometheus.
    * [ ] Configure Actuator to expose Prometheus metrics endpoint (`/actuator/prometheus`).
    * [ ] Set up Prometheus server (can be run in Docker, on EC2, or using Amazon Managed Service for Prometheus - AMP).
        * [ ] Configure Prometheus to scrape metrics from your application endpoint(s).
            * [L] Service discovery for Prometheus in a dynamic environment (e.g., ECS).
    * [ ] Set up Grafana (can be run in Docker, on EC2, or using Amazon Managed Grafana).
        * [ ] Add Prometheus as a data source in Grafana.
        * [ ] Create basic dashboards to visualize key application metrics (JVM health, HTTP request rates/latency, error rates).
            * [L] What are key metrics to monitor for a Spring Boot application.

### Phase 6: Advanced Security & Refinements
* [ ] **OWASP Top 10 Review & Mitigation:**
    * [L] Go through each relevant OWASP Top 10 vulnerability and discuss how it's being addressed or needs to be addressed.
        * A01: Broken Access Control (Covered by authorization, ongoing review)
        * A02: Cryptographic Failures (HTTPS, password hashing - BCrypt)
        * A03: Injection (JPA helps with SQLi, but validate all inputs)
        * A04: Insecure Design (Consider threat modeling)
        * A05: Security Misconfiguration (Secure defaults, regular checks)
        * A06: Vulnerable and Outdated Components (Dependabot/Snyk, regular updates)
        * A07: Identification and Authentication Failures (JWT, MFA if needed later)
        * A08: Software and Data Integrity Failures (CI/CD checks, secure dependencies)
    * [ ] Implement CSRF protection if any part of the API might be called from traditional web forms or if session cookies are used (less critical for pure stateless JWT APIs if headers are checked).
        * Reasoning: Protects against unwanted actions by authenticated users. Spring Security provides built-in CSRF.
    * [ ] Implement Security Headers (e.g., Content Security Policy, X-Content-Type-Options, Strict-Transport-Security).
        * Reasoning: Browser-level security enhancements.
    * [ ] Rate Limiting (e.g., using Spring Cloud Gateway if introduced, or custom filter with Guava/Resilience4j).
        * Reasoning: Protects against DoS and brute-force attacks.
* [ ] **HTTPS Configuration:**
    * [ ] Ensure deployment on AWS uses HTTPS (e.g., via Application Load Balancer with ACM certificate).
        * Reasoning: Encrypts data in transit. Essential.
* [ ] **API Documentation:**
    * [ ] Integrate Springdoc OpenAPI (Swagger) for API documentation.
        * Reasoning: Generates interactive API docs automatically from code. Makes API easier to consume.
* [ ] **Testing:**
    * [ ] Write unit tests for services and controllers (JUnit, Mockito).
    * [ ] Write integration tests for API endpoints (`@SpringBootTest`, `TestRestTemplate`/`MockMvc`).
        * [L] Explain different types of tests and their importance.
    * [ ] Write security-focused tests (e.g., testing access control rules).
* [ ] **Session Management (if applicable):**
    * [ ] Review session management best practices if sessions are used (though JWT aims for stateless).
* [ ] **Data Privacy & Protection Review:**
    * [ ] Ensure sensitive data is handled appropriately (e.g., no logging of raw passwords, consider encryption at rest for sensitive fields if necessary).
* [ ] **Regular Dependency Updates:**
    * [ ] Set up a tool like Dependabot (GitHub) or Snyk to monitor for vulnerable dependencies.

### Phase 7: Further Enhancements & Long-term Maintenance
* [ ] Explore advanced Role-Based Access Control (RBAC) if an admin role or other roles are introduced.
* [ ] Consider distributed tracing (e.g., OpenTelemetry, AWS X-Ray) if microservices evolve.
* [ ] Advanced CI/CD pipeline (automated testing, blue/green or canary deployments).
* [ ] Performance testing and optimization.
* [ ] User feedback and iterative improvements.

## 3. Reasoning for Key Technical Decisions

| Decision                      | Chosen Technology/Approach                                  | Reasoning                                                                                                                                                              | Alternatives Considered                                                                                                                               |
| :---------------------------- | :---------------------------------------------------------- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :---------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Backend Framework** | Spring Boot                                                 | Rapid development, large ecosystem & community, robust, mature, convention over configuration, strong support for dependencies like Spring Data, Spring Security.   | Quarkus, Micronaut (excellent for cloud-native, but Spring Boot has a wider talent pool and more extensive existing libraries for this project's scope). Dropwizard (more lightweight). |
| **Build Tool** | Gradle                                                      | Powerful and flexible build automation, Groovy/Kotlin DSL, good dependency management, incremental builds.                                                             | Maven (XML-based, can be verbose, very established).                                                                                                |
| **Programming Language** | Java 21                                                     | LTS version, modern language features, large talent pool, strong performance, extensive libraries.                                                                     | Kotlin (runs on JVM, more concise, good interop with Java), Python/Node.js (different ecosystems, might be chosen for other reasons).                   |
| **Initial Database** | H2 (In-Memory)                                              | Easy for local development & testing, fast startup, no external installation needed for initial phases.                                                              | Derby (similar to H2), SQLite (file-based).                                                                                                          |
| **Production Database** | PostgreSQL                                                  | Feature-rich, robust, open-source, strong community support, good for relational data, widely used.                                                                    | MySQL (also a strong RDBMS, very popular), MariaDB. NoSQL (MongoDB, Cassandra - considered if data was highly non-relational, but current model fits RDBMS). |
| **Persistence Framework** | Spring Data JPA (Hibernate)                                 | Simplifies data access layer, reduces boilerplate code, integrates well with Spring, object-relational mapping.                                                        | MyBatis (more control over SQL, good for complex queries), Raw JDBC (too verbose, error-prone for complex apps).                                      |
| **Security Framework** | Spring Security                                             | Comprehensive security solution, highly customizable, integrates seamlessly with Spring, handles authentication and authorization effectively.                       | Apache Shiro (less integrated with Spring ecosystem), Manual Implementation (highly complex and error-prone).                                     |
| **Password Hashing** | BCryptPasswordEncoder                                       | Strong, adaptive hashing algorithm, resistant to rainbow table attacks due to salting. Industry standard.                                                              | SCrypt, Argon2 (potentially stronger, but BCrypt is well-supported and a good baseline). PBKDF2.                                                    |
| **API Authentication** | JWT (JSON Web Tokens)                                       | Stateless authentication suitable for APIs serving multiple clients (web/mobile), scalable, widely adopted.                                                          | Session-based authentication (stateful, can be problematic for scaling APIs), PASETO (Platform-Agnostic Security Tokens - more secure alternative to JWT but less common). |
| **Infrastructure as Code** | Terraform                                                   | Cloud-agnostic (though targeting AWS), declarative, enables repeatable and version-controlled infrastructure, large community.                                         | AWS CloudFormation (AWS native, tightly integrated but vendor-locked), Pulumi (uses general-purpose programming languages), Ansible (more for configuration management but can do provisioning). |
| **Metrics Monitoring** | Prometheus & Grafana                                        | Powerful open-source combination, industry standard for metrics collection and visualization, highly flexible.                                                         | Datadog, Dynatrace (SaaS, can be expensive, more batteries-included), InfluxDB stack.                                                               |
| **Log Aggregation** | OpenSearch/Kibana (or ELK Stack)                            | Robust solution for centralized logging, powerful search and visualization capabilities. OpenSearch is an AWS-friendly fork of Elasticsearch.                      | Splunk (commercial, powerful but can be costly), Grafana Loki (simpler, good if already using Grafana for metrics), CloudWatch Logs (AWS native, simpler but less powerful querying than OpenSearch). |
| **Containerization (App)** | Docker                                                      | Standard for packaging applications and dependencies, ensures consistency across environments, enables microservices and easier scaling.                         | Podman, containerd (lower-level runtimes, Docker is a higher-level platform).                                                                       |
| **Container Orchestration** | AWS ECS (Fargate)                                           | Managed container orchestration on AWS, Fargate provides serverless compute for containers, reducing operational overhead.                                         | Kubernetes (EKS on AWS - more powerful but significantly more complex to manage), Docker Swarm (simpler but less feature-rich), EC2 with custom orchestration. |

This document should be updated regularly as development progresses, decisions change, or new components are added.