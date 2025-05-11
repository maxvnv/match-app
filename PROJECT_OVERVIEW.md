# Project Overview: Tennis Matchmaker (match-app)

## 1. Introduction

This is a Spring Boot application designed to help recreational tennis players find partners to play with in their city. The primary goal of this project is to build a functional backend service that can later serve a React frontend and a mobile iOS application. A key aspect of this project is to serve as a learning platform for implementing and practicing various technologies and concepts, particularly focusing on security, cloud deployment (AWS with Terraform), and observability (Prometheus, Grafana, OpenSearch).

**Project Details (Initial):**
* Build Tool: Gradle
* Java Version: 21
* Spring Boot Version: Latest Stable (e.g., 3.3.x)
* Group ID: `com.tennis-match` (Note: current code uses `com.tennismatch`)
* Artifact ID: `match-app`
* Packaging: Jar
* Initial Database: H2 (In-Memory), with a plan to migrate to PostgreSQL for production.

## 2. Core Functionalities

The application will allow users to:

1.  **User Registration & Profile Management:**
    * Register with details like email, password, name, city, and self-assessed tennis level.
    * Manage their profile information.
2.  **Create Play Proposal:**
    * Authenticated users can create "Play Proposals" specifying date/time availability, preferred location(s), and optional notes.
3.  **Browse Play Proposals:**
    * Authenticated users can browse active Play Proposals, with filtering options (e.g., by date, tennis level, location).
4.  **Express Interest / Request Match:**
    * Users can send a match request to the creator of a Play Proposal.
5.  **Manage Match Requests & Confirm Match:**
    * Proposal creators can view, accept, or decline incoming match requests.
    * Upon acceptance, a match is confirmed.

## 3. Development Phases (High Level)

The project development is planned in iterative phases. For a detailed breakdown, checklist, and technical reasoning, please refer to `TECHNICAL_DOCUMENTATION.md`.

* **Phase 1: Project Setup & Basic Structure (Largely Completed)**
    * Initialization, core model definition, repository setup, basic user service.
* **Phase 2: Core Feature Implementation**
    * User login, Play Proposal CRUD, Browse/filtering, match request workflow.
* **Phase 3: Security Implementation - Iteration 1**
    * Robust authentication (JWT), basic authorization.
* **Phase 4: Initial AWS Deployment with Terraform & PostgreSQL Migration**
    * Infrastructure setup on AWS (VPC, RDS, EC2/ECS), application deployment.
* **Phase 5: Observability Setup**
    * Logging with OpenSearch/Kibana, metrics with Prometheus/Grafana.
* **Phase 6: Advanced Security & Refinements**
    * OWASP Top 10 review, additional security measures, API documentation, comprehensive testing.
* **Phase 7: Further Enhancements & Long-term Maintenance**

## 4. Security Learning Objectives & Focus Areas

Throughout the development, there will be a strong focus on:

* **Authentication Mechanisms:** Username/Password, JWT.
* **Authorization Rules:** Resource ownership, potentially Role-Based Access Control (RBAC).
* **Secure Password Storage & Management:** BCrypt.
* **Input Validation & Output Encoding:** Preventing injection, XSS.
* **Cross-Site Request Forgery (CSRF) Protection.**
* **Secure Session Management (if applicable, aiming for stateless JWT).**
* **Transport Layer Security (HTTPS).**
* **Secure API Design Principles.**
* **Data Privacy & Protection.**
* **Common Web Vulnerabilities (OWASP Top 10).**
* **Infrastructure Security on AWS.**
* **Dependency Vulnerability Management.**

---
*This document provides a general overview. For detailed technical plans, checklists, and evolving decisions, please consult `TECHNICAL_DOCUMENTATION.md` and `AI_AGENT_GUIDE.md`.*