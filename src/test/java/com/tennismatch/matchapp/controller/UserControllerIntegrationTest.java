package com.tennismatch.matchapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennismatch.matchapp.dto.UserRegistrationDto;
import com.tennismatch.matchapp.repository.UserRepository;
import com.tennismatch.matchapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void registerUser_Success() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                "testuser",
                "test@example.com",
                "password",
                "Test",
                "User",
                "City",
                "Intermediate"
        );

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void registerUser_DuplicateUsername() throws Exception {
        // Register a user first
        UserRegistrationDto registrationDto1 = new UserRegistrationDto(
                "duplicateuser",
                "test1@example.com",
                "password",
                "Test",
                "User1",
                "City",
                "Intermediate"
        );
        userService.registerUser(registrationDto1); // Use service to pre-register

        // Attempt to register another user with the same username
        UserRegistrationDto registrationDto2 = new UserRegistrationDto(
                "duplicateuser",
                "test2@example.com",
                "anotherpassword",
                "Another",
                "User2",
                "City",
                "Advanced"
        );

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Username already in use"));
    }

    @Test
    void registerUser_DuplicateEmail() throws Exception {
        // Register a user first
        UserRegistrationDto registrationDto1 = new UserRegistrationDto(
                "user1",
                "duplicate@example.com",
                "password",
                "Test",
                "User1",
                "City",
                "Intermediate"
        );
        userService.registerUser(registrationDto1); // Use service to pre-register

        // Attempt to register another user with the same email
        UserRegistrationDto registrationDto2 = new UserRegistrationDto(
                "user2",
                "duplicate@example.com",
                "anotherpassword",
                "Another",
                "User2",
                "City",
                "Advanced"
        );

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Email already in use"));
    }

    @Test
    void registerUser_InvalidInput_MissingUsername() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                null, // Missing username
                "test@example.com",
                "password",
                "Test",
                "User",
                "City",
                "Intermediate"
        );

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest()); // Expecting validation errors
                // More specific assertions on error message body could be added if needed
    }

    @Test
    void registerUser_InvalidInput_InvalidEmail() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto(
                "testuser",
                "invalid-email", // Invalid email format
                "password",
                "Test",
                "User",
                "City",
                "Intermediate"
        );

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest()); // Expecting validation errors
                // More specific assertions on error message body could be added if needed
    }

    // Add more tests for other validation constraints in UserRegistrationDto

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }
} 