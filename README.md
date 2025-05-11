# Tennis Matchmaker: match-app

## Project Overview

This is a Spring Boot application designed to help recreational tennis players find partners to play with in their city. The primary goal of this project is to serve as a learning platform for implementing and practicing various security concepts in a Java Spring-backed application.

**Project Details:**
*   Build Tool: Gradle
*   Java Version: 21
*   Spring Boot Version: Latest Stable (e.g., 3.3.x)
*   Group ID: `com.tennis-match`
*   Artifact ID: `match-app`
*   Packaging: Jar
*   Initial Database: H2 (In-Memory)

## Core Functionalities

The application will allow users to:

1.  **User Registration & Profile Management:**
    *   Register with necessary details (e.g., email, password, name, city, self-assessed tennis level).
    *   Manage their profile information.

2.  **Create Play Proposal:**
    *   Authenticated users can create "Play Proposals" specifying:
        *   Date and time range for availability.
        *   Preferred tennis court(s) or general area.
        *   Optional notes (e.g., match type preferences).

3.  **Browse Play Proposals:**
    *   Authenticated users can browse active Play Proposals from other users, with filtering options (e.g., by date, tennis level, location).

4.  **Express Interest / Request Match:**
    *   Users can express interest or send a match request to the creator of a Play Proposal.

5.  **Manage Match Requests & Confirm Match:**
    *   Proposal creators can view, accept, or decline incoming match requests.
    *   Upon acceptance, a match is confirmed, and necessary details may be shared (or an in-app communication channel enabled) between the matched players.

## Development Plan

*   **Phase 1: Project Setup & Basic Structure (Current)**
    *   [x] Initialize Spring Boot project with Gradle, Java 21.
    *   [x] Define core application idea and functionalities.
    *   [x] Set up initial `README.md` with project goals and plan.
    *   [ ] Define initial data models (User, PlayProposal, Match).
    *   [ ] Implement basic User registration and login (without full security yet).
    *   [ ] Set up H2 in-memory database for initial development.

*   **Phase 2: Core Feature Implementation**
    *   [ ] Implement CRUD operations for Play Proposals.
    *   [ ] Implement browsing and filtering of Play Proposals.
    *   [ ] Implement match request and confirmation workflow.

*   **Phase 3: Security Implementation - Iteration 1 (Authentication & Basic Authorization)**
    *   [ ] Integrate Spring Security.
    *   [ ] Implement robust password hashing (e.g., BCrypt).
    *   [ ] Secure endpoints: Ensure only authenticated users can create proposals, request matches, etc.
    *   [ ] Basic authorization: Ensure users can only manage their own proposals/matches.

*   **Phase 4: Security Implementation - Iteration 2 (Advanced Topics)**
    *   [ ] Input validation and sanitization to prevent common vulnerabilities (e.g., XSS, (basic) SQLi although JPA helps).
    *   [ ] CSRF protection for relevant endpoints.
    *   [ ] Session management best practices.
    *   [ ] Explore role-based access control (e.g., if an admin role is introduced).
    *   [ ] HTTPS configuration (conceptual, or actual if deploying).

*   **Phase 5: Enhancements & Refinements**
    *   [ ] Migrate database from H2 to PostgreSQL.
    *   [ ] API documentation (e.g., Swagger/OpenAPI).
    *   [ ] Unit and integration testing for security features.
    *   [ ] Logging and monitoring considerations for security events.
    *   [ ] Further security hardening based on OWASP Top 10 or other guidelines.

## Security Learning Objectives

Throughout the development of this application, we will focus on implementing and understanding:

*   Authentication Mechanisms (e.g., Form-based, JWT)
*   Authorization Rules (Resource ownership, Role-Based Access Control)
*   Secure Password Storage & Management
*   Input Validation & Output Encoding
*   Cross-Site Request Forgery (CSRF) Protection
*   Secure Session Management
*   Transport Layer Security (HTTPS)
*   Secure API Design Principles
*   Data Privacy & Protection
*   Common Web Vulnerabilities (e.g., OWASP Top 10 relevant items)

---
*This README will be updated as the project progresses.* 