package com.tennismatch.matchapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennismatch.matchapp.dto.RegisterRequest;
import com.tennismatch.matchapp.model.NtrpLevel;
import com.tennismatch.matchapp.repository.UserRepository;
import com.tennismatch.matchapp.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser_Success() throws Exception {
        RegisterRequest registrationDto = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123Valid")
                .firstName("Test")
                .lastName("User")
                .ntrpLevel(NtrpLevel.INTERMEDIATE_3_0)
                .homeTown("Testville")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully!"));
    }

    @Test
    void registerUser_DuplicateEmail() throws Exception {
        RegisterRequest registrationDto1 = RegisterRequest.builder()
                .email("duplicate@example.com")
                .password("password123Valid")
                .firstName("Test")
                .lastName("User1")
                .ntrpLevel(NtrpLevel.INTERMEDIATE_3_0)
                .homeTown("City")
                .build();
        userService.registerUser(registrationDto1);

        RegisterRequest registrationDto2 = RegisterRequest.builder()
                .email("duplicate@example.com")
                .password("anotherPasswordValid")
                .firstName("Another")
                .lastName("User2")
                .ntrpLevel(NtrpLevel.ADVANCED_4_0)
                .homeTown("Advanced City")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto2)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Email is already in use!"));
    }

    @Test
    void registerUser_InvalidInput_InvalidEmail() throws Exception {
        RegisterRequest registrationDto = RegisterRequest.builder()
                .email("invalid-email")
                .password("password123Valid")
                .firstName("Test")
                .lastName("User")
                .ntrpLevel(NtrpLevel.BEGINNER_2_0)
                .homeTown("ValidTown")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_InvalidInput_ShortPassword() throws Exception {
        RegisterRequest registrationDto = RegisterRequest.builder()
                .email("shortpass@example.com")
                .password("short")
                .firstName("Test")
                .lastName("User")
                .ntrpLevel(NtrpLevel.BEGINNER_2_0)
                .homeTown("ValidTown")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_InvalidInput_MissingFirstName() throws Exception {
        RegisterRequest registrationDto = RegisterRequest.builder()
                .email("missingfirst@example.com")
                .password("password123Valid")
                .firstName("")
                .lastName("User")
                .ntrpLevel(NtrpLevel.BEGINNER_2_0)
                .homeTown("ValidTown")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest());
    }
} 