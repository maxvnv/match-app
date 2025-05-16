# Development Issues Log

This document tracks significant issues encountered during the development of the Tennis Match App and their resolutions.

## 1. Hibernate Initialization Error during Tests and Context Loading

*   **Symptom:** Consistent `java.lang.IllegalArgumentException: org.hibernate.action.internal.ActionQueue.addResolvedEntityInsertAction(...)V` (or similar `Action.java:305` error) during Spring context loading, especially in tests (`MatchAppApplicationTests.contextLoads()`, `UserRepositoryTest`, `UserControllerIntegrationTest`). This prevented tests from passing and indicated a fundamental problem with Hibernate/JPA setup.
*   **Investigation Steps:**
    *   Checked `@ElementCollection` annotations in `User` entity.
    *   Verified H2 database configuration and `application-test.properties`.
    *   Attempted running tests with `gradlew --no-daemon`.
    *   Downgraded Spring Boot version from a potential SNAPSHOT/milestone (`3.4.5`) to a stable GA release (`3.3.3`), then reverted to `3.4.5` after primary issue identified.
    *   Temporarily simplified the `User` entity by commenting out complex fields.
    *   Reviewed test configurations, especially `@DataJpaTest` and `@SpringBootTest` with custom security.
    *   Explicitly enabled `spring.main.allow-bean-definition-overriding` (later removed as default is true and wasn't the root cause for the main error).
*   **Root Cause:** An incorrectly placed comment in `match-app/src/main/resources/application.properties`. The line `spring.jpa.hibernate.ddl-auto=update # some comment here` was causing Spring to interpret `"update # some comment here"` as the value for `ddl-auto`, which is invalid. Hibernate failed to initialize correctly with this malformed `ddl-auto` instruction.
*   **Resolution:** Corrected the `application.properties` file to ensure comments are on their own lines or that there are no trailing comments on property value lines. The correct line is `spring.jpa.hibernate.ddl-auto=update`.

## 2. Initial Package Structure and Group ID Mismatch

*   **Symptom:** The `build.gradle` initially had `group = 'com.tennis-match'`, while the desired package structure was `com.tennismatch.matchapp`. An unused directory structure `com/tennis_match/match_app` also existed.
*   **Resolution:**
    *   Updated `group` in `build.gradle` to `com.tennismatch`.
    *   Deleted the unused package directory `match-app/src/main/java/com/tennis_match/`.
    *   Confirmed main package structure as `com.tennismatch.matchapp`.

## 3. Evolution of User Entity and DTOs leading to Test/Code Refactoring

*   **Symptom:** As the `User` entity was defined with more specific fields (e.g., `NtrpLevel`, `Sex`, separate name fields, removal of `username`) and `RegisterRequest` DTO was introduced, existing service layers, controllers, and tests (which were based on a simpler or different `User` structure or older DTOs) started failing compilation or logic.
*   **Resolution:** Systematically refactored:
    *   `UserService` and `UserServiceImpl` to use `RegisterRequest` and the updated `User` entity fields.
    *   `DefaultUserDetailsService` to use email for user lookup and map roles correctly.
    *   `UserController` to use `RegisterRequest` and adjust response.
    *   All affected unit and integration tests (`DefaultUserDetailsServiceTest`, `UserControllerIntegrationTest`, `UserServiceImplTest`, `UserRepositoryTest`) to align with the new DTOs, entity structure, and service method signatures.

## 4. Transient Test Failures and Build Instability (Post Hibernate Init Fix)

*   **Symptom:** After fixing the Hibernate initialization error (Issue #1), `UserControllerIntegrationTest` still failed. Some tests showed `BeanDefinitionOverrideException` and others `IllegalStateException` during context loading.
*   **Investigation Steps:**
    *   Identified that `UserControllerIntegrationTest` had a nested `@TestConfiguration` class (`TestSecurityConfig`) defining a `SecurityFilterChain` bean.
    *   The main `SecurityConfig.java` also defines a `SecurityFilterChain` bean.
    *   Attempted to use `@Primary` on the test's bean, which did not fully resolve the context loading issues for all tests in the class, though it might have changed the nature of some failures.
*   **Root Cause:** The test-specific `SecurityFilterChain` bean in `UserControllerIntegrationTest` was conflicting with the main application's `SecurityFilterChain` bean loaded by `@SpringBootTest`. This was causing bean definition override issues or context instability.
*   **Resolution:** Removed the entire nested `TestSecurityConfig` class (and its `@Primary`-annotated `SecurityFilterChain` bean) from `UserControllerIntegrationTest.java`. This was possible because the main `SecurityConfig.java` already configured `/api/auth/**` (the paths tested in `UserControllerIntegrationTest`) to `permitAll()`. This simplified the test setup and resolved the bean conflict, allowing tests to pass with the main security configuration.

## 5. Build Failures (Original Hibernate Issue - Superseded by Issue #1 detailed investigation)

*   **Symptom:** Consistent test failures (e.g., `MatchAppApplicationTests > contextLoads() FAILED`, `UserRepositoryTest > ... FAILED`) often with `java.lang.IllegalArgumentException at Action.java:305` or similar low-level Hibernate errors during context initialization or entity persistence.
*   **Resolution:** This was a manifestation of Issue #1. The root cause was the malformed `spring.jpa.hibernate.ddl-auto` property due to an incorrectly placed comment. 