# Business Requirements: Tennis Matchmaker App

This document outlines the core business requirements for the Tennis Matchmaker application. The application aims to provide a platform for recreational tennis players to find and connect with playing partners in their city.

## Core User Stories & Functionalities

1.  **User Registration & Profile Management:**
    * Users must be able to register for a new account using their email address, a chosen username, and a password.
    * User profiles should include:
        * First Name
        * Last Name
        * City
        * Self-assessed tennis level (e.g., beginner, intermediate, advanced).
    * Users must be able to view and update their profile information after registration.
    * The system must ensure that usernames and email addresses are unique.

2.  **Play Proposal Creation:**
    * Authenticated users must be able to create "Play Proposals" to indicate their availability and desire to play.
    * A Play Proposal must specify:
        * Date and time range of availability (start time, end time).
        * Preferred tennis court(s) or a general area/location detail.
    * A Play Proposal can optionally include:
        * Notes (e.g., preferred match type like singles/doubles, friendly hit, competitive match).
    * Play Proposals should have a lifecycle status (e.g., OPEN, MATCHED, CANCELLED).

3.  **Browse and Filter Play Proposals:**
    * Authenticated users must be able to browse active (OPEN) Play Proposals created by other users.
    * Users should be able to filter Play Proposals based on:
        * Date
        * Tennis level of the proposer
        * Location/City

4.  **Express Interest & Request Match:**
    * Authenticated users must be able to express interest in a Play Proposal by sending a "Match Request" to its creator.

5.  **Manage Match Requests & Confirm Match:**
    * Users who created Play Proposals must be able to view incoming Match Requests.
    * Proposal creators must be able to accept or decline these Match Requests.
    * When a Match Request is accepted:
        * The status of the Play Proposal should change (e.g., to MATCHED).
        * A "Match" is confirmed between the two users.
        * The system should facilitate sharing necessary details for the match (e.g., contact information, or enable an in-app communication channel – TBD).

6.  **Data Privacy and Security:**
    * User data, especially personal information and credentials, must be handled securely.
    * The system must protect against common web vulnerabilities.
    * Ensure data privacy, integrity, and security throughout all user interactions.

## Future Considerations (Out of Scope for Initial MVP but good to keep in mind)

* In-app messaging system.
* Tennis court booking integration/information.
* User rating/feedback system.
* Support for doubles partner search.
* Notifications for new proposals, match requests, and confirmations.