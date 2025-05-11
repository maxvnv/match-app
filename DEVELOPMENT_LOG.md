# Development Log

## Business Requirements
- Provide a marketplace for recreational tennis players in a city to find playing partners.
- Users can register and manage their profile (email, password, name, city, tennis level).
- Authenticated users can create play proposals with date/time range, location details, and optional notes.
- Users can browse and filter play proposals by date, tennis level, and location.
- Users can express interest in proposals by sending match requests.
- Proposal creators can accept or decline match requests.
- Upon acceptance, provide contact details or in-app messaging to matched users.
- Support proposal lifecycle: OPEN, MATCHED, CANCELLED.
- Ensure data privacy, integrity, and security throughout all interactions.

## Phase 1: Project Setup & Configuration (Completed)
- Initialized Spring Boot project using Gradle, Java 21, Spring Boot 3.3.x (latest stable).
  - Reason: Rapid development with Spring Initializr, modern Java features, and a robust build tool.
- Selected packaging as Jar and H2 in-memory database for initial development (plan to migrate to PostgreSQL).
  - Reason: Embedded server simplifies deployment; H2 allows quick prototyping without external DB setup.
- Added core dependencies: Spring Web, Spring Data JPA, H2 Database, Spring Security, Lombok, Spring Boot DevTools.
  - Reason: Foundation for web, persistence, security, and developer productivity.

## Phase 2: Data Model Creation (Completed)
- **User** entity:
  - Fields: id, username, email, password, firstName, lastName, city, tennisLevel, createdAt, updatedAt.
  - Reason: Captures user credentials, profile info, and supports authentication and authorization.
- **PlayProposal** entity:
  - Fields: id, proposingUser (User), proposedStartTime, proposedEndTime, locationDetails, notes, status, createdAt, updatedAt.
  - Reason: Models a user's availability offer, including scheduling and location context.
- **Match** entity:
  - Fields: id, playProposal (PlayProposal), requestingUser (User), status, createdAt, updatedAt.
  - Reason: Represents a match request and confirmation workflow between users and proposals.

## Phase 3: Repository Layer (Completed)
- **UserRepository**: Extends `JpaRepository<User, Long>`, methods: `findByUsername`, `findByEmail`.
  - Reason: Provides CRUD and lookup operations for authentication and profile management.
- **PlayProposalRepository**: Extends `JpaRepository<PlayProposal, Long>`, method: `findByProposingUserId`.
  - Reason: Enables management of play proposals and querying by user.
- **MatchRepository**: Extends `JpaRepository<Match, Long>`, method: `findByPlayProposalId`.
  - Reason: Facilitates match tracking and querying matches for a proposal.

## Phase 4: Service Layer (Completed)
- **Added Validation Dependency**: 
  - Added `spring-boot-starter-validation` to `build.gradle` to enable validation annotations like `@NotBlank` and `@Email` in the `UserRegistrationDto` class.
  - Reason: To enforce input validation rules for user registration data.
- **Created UserService Interface**: 
  - Defined methods for user registration and lookup in `UserService.java`.
  - Reason: To encapsulate user-related business logic and provide a clear contract for user operations.
- **Implemented UserServiceImpl**: 
  - Implemented the `UserService` interface in `UserServiceImpl.java`, handling user registration and validation.
  - Reason: To manage user registration logic, including checking for existing usernames and emails, and encoding passwords.

## Phase 5: Controller Layer (In Progress)
- **Created UserController**: 
  - Will define endpoints for user registration and authentication.
  - Reason: To expose user-related operations via RESTful APIs.

## Next Steps
1. Implement **UserController** to expose registration and authentication endpoints.
   - Reason: Define RESTful endpoints for client interaction.
2. Implement **PlayProposalController** and **MatchController**.
3. Configure Spring Security (password hashing, authentication, authorization).
4. Proceed with core business logic and additional security topics (input validation, CSRF, role-based access).

## Document Maintenance
- This log should be updated whenever code is added, major features are implemented, or architectural/security decisions are made. 