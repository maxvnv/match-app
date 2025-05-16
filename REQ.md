# Business Requirements for Tennis Match App (MVP)

## 1. User Management

### 1.1. Registration
    - 1.1.1. Users must be able to register using their email address and a password.
    - 1.1.2. Users must be able to register using their Gmail account (OAuth).
    - 1.1.3. Password requirements:
        - Minimum 8 characters
        - At least one uppercase letter
        - At least one lowercase letter
        - At least one digit
        - At least one special character
    - 1.1.4. After registration, a confirmation email should be sent (optional for MVP, but highly recommended for future).

### 1.2. Login
    - 1.2.1. Users must be able to log in using their registered email and password.
    - 1.2.2. Users must be able to log in using their Gmail account.
    - 1.2.3. Implement "Forgot Password" functionality (optional for MVP).

### 1.3. User Profile
    - 1.3.1. After registration/login, users must be able to fill in and update their profile information:
        - First Name (Mandatory)
        - Last Name (Mandatory)
        - Current Tennis NTRP (National Tennis Rating Program) level (Mandatory, provide a predefined list/dropdown)
        - Home Town/City (Mandatory)
        - Age (Optional, but recommended for filtering)
        - Sex (Optional, options: Male, Female, Other, Prefer not to say)
    - 1.3.2. Users should be able to view their own profile.

## 2. Match Proposal and Search

### 2.1. Propose a Match
    - 2.1.1. Logged-in users must be able to propose a tennis match.
    - 2.1.2. Proposal details must include:
        - Date of the match (Mandatory)
        - Time of the match (Mandatory)
        - Preferred NTRP level range for the opponent (Optional)
        - Location/Tennis Court (Optional, could be free text or a selection if a court database is implemented later)
        - Notes (Optional, e.g., "Looking for a friendly match", "Willing to split court fees")
    - 2.1.3. Users should be able to see their own active proposals.
    - 2.1.4. Users should be able to cancel their own proposals if not yet accepted.

### 2.2. Search for Matches
    - 2.2.1. Logged-in users must be able to search for match proposals made by other users.
    - 2.2.2. Search filters should include:
        - Date range
        - Time range (e.g., morning, afternoon, evening)
        - NTRP level
        - Location (if available)
    - 2.2.3. Search results should display key proposal details and the proposing user's basic info (Name, NTRP level).

### 2.3. Accept a Match
    - 2.3.1. Logged-in users must be able to accept a suitable match proposal from the search results.
    - 2.3.2. Once a proposal is accepted, it should no longer be available for others to accept.
    - 2.3.3. The proposing user should be notified when their proposal is accepted.
    - 2.3.4. The accepting user should also receive a confirmation.

## 3. Post-Match Actions

### 3.1. Enter Match Score
    - 3.1.1. After a match is played, either the proposing user or the accepting user must be able to enter the match score.
    - 3.1.2. The score should include sets and games (e.g., 6-3, 6-4).
    - 3.1.3. The other player involved in the match should be notified when a score is entered and have the option to confirm or dispute it (dispute resolution is out of MVP scope, confirmation is a good to have). For MVP, we can assume the first entry is correct.

### 3.2. Match History
    - 3.2.1. Users must be able to view their own match history.
    - 3.2.2. Match history should include:
        - Opponent's name
        - Date of the match
        - Score
        - Location (if available)
    - 3.2.3. Users should only see matches they participated in.

## 4. Non-Functional Requirements (MVP Focus)

### 4.1. Security
    - 4.1.1. Secure password storage (hashing with salt).
    - 4.1.2. Protection against common web vulnerabilities (XSS, CSRF, SQL Injection).
    - 4.1.3. Secure API endpoints (authentication and authorization).
    - 4.1.4. HTTPS for all communication.

### 4.2. Usability
    - 4.2.1. The application should be intuitive and easy to use for the target audience (recreational tennis players).
    - 4.2.2. Clear navigation and user flows.

### 4.3. Reliability
    - 4.3.1. The application should be available and function correctly under normal usage.

### 4.4. Data Integrity
    - 4.4.1. Ensure that user data and match data are stored accurately and consistently.

## 5. Future Considerations (Out of MVP Scope but good to keep in mind)

    - 5.1. Advanced search filters (e.g., user age range, preferred playing times).
    - 5.2. Court booking integration.
    - 5.3. User rating system based on match results or peer reviews.
    - 5.4. Notifications (in-app, push, email) for match reminders, new proposals in preferred area, etc.
    - 5.5. Chat functionality between matched players.
    - 5.6. Admin panel for managing users and content.
    - 5.7. Dispute resolution for scores.
    - 5.8. "Forgot Password" email confirmation.
    - 5.9. Email confirmation on registration. 