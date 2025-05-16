# Tennis Match App - Development Plan (MVP Backend)

This plan outlines the steps to develop the MVP backend for the Tennis Match App. It emphasizes iterative development, testing, security, observability, and deployment preparation, keeping in mind the learning objectives.

## Phase 0: Project Setup & Foundation (Review and Enhance)

*   [x] 0.1. Analyze existing project structure and dependencies (`build.gradle`).
*   [x] 0.2. Review and refine package structure if necessary (e.g., ensure consistency between `group` in `build.gradle` and actual package names - current: `com.tennis-match` vs `com.tennismatch.matchapp`). For now, we will proceed with `com.tennismatch.matchapp` as the primary code package.
*   [x] 0.3. Initial H2 Database Configuration:
    *   [x] 0.3.1. Verify H2 console is accessible for development (`spring.h2.console.enabled=true`).
    *   [x] 0.3.2. Define basic JPA properties for schema generation (e.g., `spring.jpa.hibernate.ddl-auto=update` or `create-drop` for dev).
*   [x] 0.4. Basic Logging Setup:
    *   [x] 0.4.1. Configure Logback/SLF4j for structured logging (e.g., JSON format for easier parsing by OpenSearch later).
    *   [x] 0.4.2. Ensure application logs basic startup and error information.
*   [x] 0.5. Setup Initial CI/CD with GitLab:
    *   [x] 0.5.1. Create a `.gitlab-ci.yml` file.
    *   [x] 0.5.2. Define a simple pipeline: build -> test.
    *   [x] 0.5.3. Ensure the pipeline runs on every push to the main branch.
    *   [ ] 0.5.4. (Learning) Research GitLab CI/CD basics: stages, jobs, artifacts.
*   [x] 0.6. Introduce Basic Observability (Micrometer):
    *   [x] 0.6.1. Add Micrometer core dependency if not already present.
    *   [x] 0.6.2. Add Micrometer Prometheus registry (`micrometer-registry-prometheus`).
    *   [x] 0.6.3. Expose a `/actuator/prometheus` endpoint.
    *   [ ] 0.6.4. (Learning) Run Prometheus locally (e.g., via Docker) and configure it to scrape the app's metrics endpoint.
    *   [ ] 0.6.5. (Learning) Run Grafana locally (e.g., via Docker) and connect it to Prometheus. Create a very simple dashboard to visualize a basic metric (e.g., JVM memory usage).
*   [x] 0.7. Initial Security Configuration (Spring Security):
    *   [x] 0.7.1. Review existing `SecurityConfig` (if any).
    *   [x] 0.7.2. For now, configure it to allow all requests to simplify early development, or secure actuator endpoints.
        *   Example: Permit access to `/actuator/**` and H2 console.
    *   [ ] 0.7.3. (Learning) Understand Spring Security filter chain basics.
*   [x] 0.8. Developer Experience - API Documentation & Interaction (NEW)
    *   [x] 0.8.1. Integrate Swagger UI using `springdoc-openapi-starter-webmvc-ui`.
    *   [x] 0.8.2. Verify Swagger UI is accessible (e.g., at `/swagger-ui.html`).

## Phase 1: User Management (Core)

*   [ ] 1.1. User Entity & Repository:
    *   [ ] 1.1.1. Define `User` entity (`@Entity`) with fields: `id`, `email` (unique), `password`, `firstName`, `lastName`, `ntrpLevel`, `homeTown`, `age`, `sex`, `roles` (e.g., `USER`, `ADMIN`).
        *   NTRP Level: Consider using an Enum.
        *   Sex: Consider using an Enum.
        *   Password: Will be hashed.
    *   [ ] 1.1.2. Create `UserRepository` extending `JpaRepository`.
    *   [ ] 1.1.3. Write unit tests for `UserRepository` (e.g., findByEmail).
*   [ ] 1.2. Registration (Email/Password):
    *   [ ] 1.2.1. Create `RegisterRequest` DTO (email, password, firstName, lastName, ntrpLevel, homeTown, age, sex).
    *   [ ] 1.2.2. Implement `UserService` with a `registerUser` method:
        *   Validate input (using `@Valid` and validation annotations on DTO).
        *   Check if email already exists.
        *   Encode password using `PasswordEncoder` (BCrypt).
        *   Save new user.
    *   [ ] 1.2.3. Create `AuthController` with a `/api/auth/register` endpoint.
    *   [ ] 1.2.4. Implement password policies (programmatically or using Spring Security features if applicable, though complexity might be deferred).
    *   [ ] 1.2.5. Write unit tests for `UserService.registerUser`.
    *   [ ] 1.2.6. Write integration tests for the registration endpoint.
*   [ ] 1.3. Login (Email/Password) & JWT:
    *   [ ] 1.3.1. Create `LoginRequest` DTO (email, password).
    *   [ ] 1.3.2. Create `JwtResponse` DTO (accessToken, tokenType, userDetails).
    *   [ ] 1.3.3. Add JWT dependencies (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`).
    *   [ ] 1.3.4. Create `JwtTokenProvider` service to generate, validate, and extract claims from JWTs.
        *   Store JWT secret key securely (e.g., in application properties, later in environment variables/AWS Secrets Manager).
        *   Define token expiration.
    *   [ ] 1.3.5. Configure Spring Security:
        *   Implement `UserDetailsService` to load user by email from `UserRepository`.
        *   Configure `AuthenticationManager` and `PasswordEncoder` beans.
        *   Create a JWT authentication filter (`OncePerRequestFilter`) to validate tokens and set `SecurityContextHolder`.
        *   Update `SecurityConfig` to use JWT authentication, define public (`/api/auth/**`) and protected endpoints.
    *   [ ] 1.3.6. Implement login endpoint in `AuthController` (`/api/auth/login`):
        *   Authenticate user using `AuthenticationManager`.
        *   Generate JWT upon successful authentication.
        *   Return `JwtResponse`.
    *   [ ] 1.3.7. (Learning) Understand JWT flow, Spring Security `AuthenticationManager`, `UserDetailsService`, and custom filter integration.
    *   [ ] 1.3.8. Write unit tests for `JwtTokenProvider`.
    *   [ ] 1.3.9. Write integration tests for the login endpoint and accessing a secured endpoint with/without a token.
*   [ ] 1.4. User Profile Management:
    *   [ ] 1.4.1. Create `UserProfileDto` (firstName, lastName, ntrpLevel, homeTown, age, sex).
    *   [ ] 1.4.2. Implement `UserService` methods:
        *   `getCurrentUserProfile()`: Get details of the currently logged-in user.
        *   `updateCurrentUserProfile(UserProfileDto)`: Update profile details for the logged-in user.
    *   [ ] 1.4.3. Create `UserController` with endpoints:
        *   `GET /api/users/me` (secured)
        *   `PUT /api/users/me` (secured)
    *   [ ] 1.4.4. Write unit and integration tests for profile management.
*   [ ] 1.5. (Stretch Goal for Phase 1 / Early Phase 2) Gmail Login (OAuth2/OIDC):
    *   [ ] 1.5.1. Add Spring Boot OAuth2 client dependency.
    *   [ ] 1.5.2. Configure Google as an OAuth2 provider in `application.properties` (client-id, client-secret).
        *   (Learning) Create OAuth2 credentials in Google Cloud Console.
    *   [ ] 1.5.3. Extend Spring Security configuration to support OAuth2 login.
    *   [ ] 1.5.4. Implement a custom `OAuth2UserService` to handle user creation/linking in your database after successful Google authentication.
    *   [ ] 1.5.5. (Learning) Understand OAuth2 Authorization Code Grant flow.
    *   [ ] 1.5.6. Test Gmail login flow.

## Phase 2: Match Proposal & Search

*   [ ] 2.1. Match Proposal Entity & Repository:
    *   [ ] 2.1.1. Define `MatchProposal` entity: `id`, `proposingUser (User)`, `proposedDateTime`, `preferredOpponentNtrpMin`, `preferredOpponentNtrpMax`, `location`, `notes`, `status (Enum: OPEN, ACCEPTED, CANCELLED, COMPLETED)`, `createdTimestamp`, `updatedTimestamp`.
    *   [ ] 2.1.2. Create `MatchProposalRepository` extending `JpaRepository`.
    *   [ ] 2.1.3. Unit tests for the repository.
*   [ ] 2.2. Propose a Match:
    *   [ ] 2.2.1. Create `CreateMatchProposalRequest` DTO.
    *   [ ] 2.2.2. Create `MatchProposalResponse` DTO.
    *   [ ] 2.2.3. Implement `MatchProposalService` method `createProposal(CreateMatchProposalRequest, User principal)`:
        *   Validate input.
        *   Set `proposingUser` from the authenticated principal.
        *   Set initial `status` to `OPEN`.
        *   Save proposal.
    *   [ ] 2.2.4. Create `MatchProposalController` with `POST /api/match-proposals` endpoint (secured).
    *   [ ] 2.2.5. Unit and integration tests.
*   [ ] 2.3. View Own Proposals:
    *   [ ] 2.3.1. `MatchProposalService` method `getMyActiveProposals(User principal)`.
    *   [ ] 2.3.2. `MatchProposalController` endpoint `GET /api/match-proposals/my-active` (secured).
    *   [ ] 2.3.3. Unit and integration tests.
*   [ ] 2.4. Cancel Own Proposal:
    *   [ ] 2.4.1. `MatchProposalService` method `cancelMyProposal(Long proposalId, User principal)`:
        *   Ensure proposal exists, belongs to the user, and is in `OPEN` state.
        *   Change status to `CANCELLED`.
    *   [ ] 2.4.2. `MatchProposalController` endpoint `PUT /api/match-proposals/{proposalId}/cancel` (secured).
    *   [ ] 2.4.3. Unit and integration tests.
*   [ ] 2.5. Search for Match Proposals:
    *   [ ] 2.5.1. `MatchProposalService` method `searchProposals(LocalDate date, LocalTime time, NtrpLevel ntrp, String location)`:
        *   Implement filtering logic (JPA Specifications or Querydsl recommended for complex queries).
        *   Filter by `status = OPEN`.
        *   Exclude proposals from the current user.
    *   [ ] 2.5.2. `MatchProposalController` endpoint `GET /api/match-proposals/search` (secured) with query parameters.
    *   [ ] 2.5.3. Unit and integration tests for various search criteria.
*   [ ] 2.6. Accept a Match Proposal:
    *   [ ] 2.6.1. Define `AcceptedMatch` entity: `id`, `matchProposal (OneToOne)`, `acceptingUser (User)`, `acceptedTimestamp`. (Or add `acceptingUser` and `acceptedTimestamp` directly to `MatchProposal` and update its status). Let's opt for adding to `MatchProposal` for simplicity in MVP.
        *   Update `MatchProposal` entity: add `acceptingUser (User, nullable)`, `acceptedTimestamp (nullable)`.
    *   [ ] 2.6.2. `MatchProposalService` method `acceptProposal(Long proposalId, User acceptingUser)`:
        *   Ensure proposal exists, is `OPEN`.
        *   Ensure accepting user is not the proposing user.
        *   Set `acceptingUser`, `acceptedTimestamp`, and update `status` to `ACCEPTED`.
        *   (Future: Notification to proposer).
    *   [ ] 2.6.3. `MatchProposalController` endpoint `PUT /api/match-proposals/{proposalId}/accept` (secured).
    *   [ ] 2.6.4. Unit and integration tests.

## Phase 3: Post-Match Actions

*   [ ] 3.1. Match Entity & Repository (if not part of `MatchProposal`):
    *   For MVP, we'll consider a `MatchProposal` with status `COMPLETED` and score details as the "Match Record".
    *   Update `MatchProposal` entity: add `proposerScore`, `acceptorScore` (String or structured type).
*   [ ] 3.2. Enter Match Score:
    *   [ ] 3.2.1. Create `EnterScoreRequest` DTO (`proposalId`, `scorePlayer1`, `scorePlayer2`).
    *   [ ] 3.2.2. `MatchProposalService` method `enterScore(Long proposalId, String score, User principal)`:
        *   Ensure proposal exists and its status is `ACCEPTED`.
        *   Ensure the principal is either the proposer or acceptor.
        *   Update the score fields (e.g., `proposerScore`, `acceptorScore` - decide how to map DTO fields to entity based on who submitted).
        *   Change status to `COMPLETED`.
        *   (Future: Notification to the other player, confirmation).
    *   [ ] 3.2.3. `MatchProposalController` endpoint `POST /api/match-proposals/{proposalId}/score` (secured).
    *   [ ] 3.2.4. Unit and integration tests.
*   [ ] 3.3. View Match History:
    *   [ ] 3.3.1. `MatchProposalService` method `getMyMatchHistory(User principal)`:
        *   Retrieve `MatchProposal` entities where `status = COMPLETED` and the principal is either `proposingUser` or `acceptingUser`.
    *   [ ] 3.3.2. `MatchProposalController` endpoint `GET /api/matches/history` (secured).
    *   [ ] 3.3.3. Response DTO should clearly show opponent, date, score, location.
    *   [ ] 3.3.4. Unit and integration tests.

## Phase 4: Observability Enhancement

*   [ ] 4.1. Structured Logging for OpenSearch:
    *   [ ] 4.1.1. Ensure Logback (or chosen logging framework) is configured for JSON output.
    *   [ ] 4.1.2. Include important context in logs (e.g., user ID, trace ID).
    *   [ ] 4.1.3. (Learning) Set up a local OpenSearch and OpenSearch Dashboards instance (e.g., via Docker).
    *   [ ] 4.1.4. (Learning) Configure a log shipping agent (e.g., Filebeat, or app directly sends logs if feasible for local setup) to send logs to OpenSearch.
    *   [ ] 4.1.5. Create basic dashboards in OpenSearch Dashboards to view and search logs.
*   [ ] 4.2. Custom Metrics with Micrometer:
    *   [ ] 4.2.1. Identify key business metrics to track (e.g., user registrations, proposals created, matches played).
    *   [ ] 4.2.2. Implement custom counters and timers using Micrometer in relevant services.
    *   [ ] 4.2.3. Update Grafana dashboards to visualize these custom metrics.
    *   [ ] 4.2.4. (Learning) Explore different types of Micrometer meters.
*   [ ] 4.3. Distributed Tracing (Optional for MVP, but good to learn):
    *   [ ] 4.3.1. Add Spring Boot Actuator and Micrometer Tracing (with Brave or OpenTelemetry) dependencies.
    *   [ ] 4.3.2. Configure a tracing system (e.g., Jaeger or Zipkin locally via Docker).
    *   [ ] 4.3.3. Configure the application to export traces.
    *   [ ] 4.3.4. Observe traces for a multi-service interaction (even if simulated locally).

## Phase 5: Security Hardening & Review

*   [ ] 5.1. Review all endpoints for correct authorization (e.g., ensuring users can only modify their own data).
*   [ ] 5.2. Implement HTTPS for all communication (will be handled by AWS Load Balancer in production, but good to be aware of for local dev if needed via self-signed certs).
*   [ ] 5.3. Protection against common vulnerabilities:
    *   [ ] 5.3.1. XSS: Ensure proper output encoding (Spring Boot helps, but be mindful with custom JS if any).
    *   [ ] 5.3.2. CSRF: Spring Security provides protection, verify it's enabled for stateful parts (if any, JWT is stateless but login form might need it).
    *   [ ] 5.3.3. SQL Injection: JPA/Hibernate helps prevent this, but review any custom queries.
*   [ ] 5.4. Secure sensitive configuration (JWT secret, database credentials):
    *   [ ] 5.4.1. Use environment variables locally.
    *   [ ] 5.4.2. Plan for AWS Secrets Manager for production.
*   [ ] 5.5. Dependency Vulnerability Scanning:
    *   [ ] 5.5.1. Integrate a tool like OWASP Dependency-Check (Gradle plugin) into the CI pipeline.
*   [ ] 5.6. Implement Rate Limiting (Optional for MVP, consider for API Gateway level).
*   [ ] 5.7. Review password policies implementation.
*   [ ] 5.8. (Learning) Research OWASP Top 10 and how they apply to the application.

## Phase 6: Deployment Preparation (Terraform & AWS)

*   [ ] 6.1. Database Choice:
    *   [ ] 6.1.1. Switch from H2 to PostgreSQL for production-like environment.
    *   [ ] 6.1.2. Update `application.properties` (or profiles) for PostgreSQL connection.
    *   [ ] 6.1.3. (Learning) Run PostgreSQL locally using Docker.
*   [ ] 6.2. Containerize the Application:
    *   [ ] 6.2.1. Create a `Dockerfile` to build a Docker image for the Spring Boot application.
    *   [ ] 6.2.2. Test building and running the Docker image locally.
*   [ ] 6.3. Introduction to Terraform:
    *   [ ] 6.3.1. (Learning) Install Terraform.
    *   [ ] 6.3.2. (Learning) Understand basic Terraform concepts: providers, resources, variables, outputs.
    *   [ ] 6.3.3. Set up AWS CLI and configure credentials.
*   [ ] 6.4. Terraform for AWS Infrastructure (Simplified MVP):
    *   [ ] 6.4.1. VPC, Subnets (public/private).
    *   [ ] 6.4.2. RDS for PostgreSQL.
        *   (Learning) How to manage database credentials securely (e.g., Terraform variable passed to RDS, then app reads from AWS Secrets Manager).
    *   [ ] 6.4.3. ECS (Fargate) or Elastic Beanstalk for running the application container. Start with Elastic Beanstalk for simplicity if preferred.
    *   [ ] 6.4.4. Application Load Balancer (ALB) with HTTPS.
    *   [ ] 6.4.5. Security Groups for RDS, ALB, App.
    *   [ ] 6.4.6. (Optional) S3 for storing build artifacts if not using GitLab registry directly with AWS.
*   [ ] 6.5. CI/CD Pipeline Enhancement for AWS Deployment:
    *   [ ] 6.5.1. Add a deployment stage to `.gitlab-ci.yml`.
    *   [ ] 6.5.2. Build Docker image and push to a container registry (e.g., GitLab Container Registry, AWS ECR).
    *   [ ] 6.5.3. Script to trigger deployment to AWS (e.g., update ECS service, deploy to Beanstalk).
*   [ ] 6.6. Logging & Monitoring in AWS:
    *   [ ] 6.6.1. Configure application logs to be sent to AWS CloudWatch Logs from ECS/Beanstalk.
    *   [ ] 6.6.2. Explore options for running Prometheus/Grafana in AWS or using AWS managed services (e.g., Amazon Managed Service for Prometheus, Amazon Managed Grafana). For MVP, continue local/self-hosted if simpler, or basic CloudWatch metrics.
    *   [ ] 6.6.3. If using OpenSearch, plan for AWS OpenSearch Service.

## Phase 7: Documentation & Review

*   [ ] 7.1. Update/Create API Documentation (e.g., using SpringDoc OpenAPI).
    *   [ ] 7.1.1. Add `springdoc-openapi-starter-webmvc-ui` dependency.
    *   [ ] 7.1.2. Access Swagger UI at `/swagger-ui.html`. Annotate DTOs and controllers for better docs.
*   [ ] 7.2. Review and update `README.md`.
*   [ ] 7.3. Review all learning points and ensure understanding.
*   [ ] 7.4. Final code cleanup and refactoring.

## General Considerations Throughout:

*   **Testing:** Write unit and integration tests for all new functionalities. Aim for good test coverage.
*   **Code Quality:** Follow Java best practices, keep code clean and maintainable. Use static analysis tools if possible (e.g., SonarLint IDE plugin).
*   **Incremental Commits:** Commit changes frequently with clear messages.
*   **Error Handling:** Implement consistent and user-friendly error handling. Use global exception handlers (`@ControllerAdvice`).
*   **Configuration Management:** Use Spring profiles for different environments (dev, test, prod).
*   **Stream API:** Prefer Stream API for collections processing where appropriate.
*   **Background Builds:** After every significant code change, manually trigger or ensure CI triggers a build.
*   **Documentation Updates:** Keep this `PLAN.md` and `REQ.md` updated as development evolves or decisions change. Update `README.md` as major milestones are hit. 