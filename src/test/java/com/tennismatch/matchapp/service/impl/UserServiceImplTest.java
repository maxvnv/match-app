package com.tennismatch.matchapp.service.impl;

import com.tennismatch.matchapp.dto.RegisterRequest;
import com.tennismatch.matchapp.model.NtrpLevel;
import com.tennismatch.matchapp.model.Sex;
import com.tennismatch.matchapp.model.User;
import com.tennismatch.matchapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private User expectedUser;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .ntrpLevel(NtrpLevel.INTERMEDIATE_3_0)
                .homeTown("Testville")
                .age(30)
                .sex(Sex.MALE)
                .build();

        expectedUser = User.builder() // Used for comparison in some tests
                .id(1L) // ID will be set by DB, but useful for some mock returns
                .email(registerRequest.getEmail())
                .password("encodedpassword") // Assume this is the encoded form
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .ntrpLevel(registerRequest.getNtrpLevel())
                .homeTown(registerRequest.getHomeTown())
                .age(registerRequest.getAge())
                .sex(registerRequest.getSex())
                .roles(Set.of("USER"))
                .build();
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedpassword");
        // Use ArgumentCaptor to capture the User object passed to save
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        // When save is called, return the captured user with an ID set (simulating DB save)
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(1L); // Simulate ID generation
            return userToSave;
        });

        User result = userService.registerUser(registerRequest);

        assertNotNull(result);
        assertEquals(registerRequest.getEmail(), result.getEmail());
        assertEquals("encodedpassword", result.getPassword());
        assertEquals(registerRequest.getFirstName(), result.getFirstName());
        assertEquals(registerRequest.getNtrpLevel(), result.getNtrpLevel());
        assertTrue(result.getRoles().contains("USER"));
        assertEquals(1, result.getRoles().size());
        assertNotNull(result.getId()); // Ensure ID is set

        verify(userRepository, times(1)).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
        verify(userRepository, times(1)).save(userCaptor.getValue());
        
        User savedUser = userCaptor.getValue();
        assertEquals(registerRequest.getEmail(), savedUser.getEmail());
        assertEquals(registerRequest.getAge(), savedUser.getAge());
        assertEquals(registerRequest.getSex(), savedUser.getSex());
    }

    @Test
    void registerUser_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registerRequest));

        assertEquals("Error: Email is already in use!", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void findByEmail_UserFound_ReturnsOptionalUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(expectedUser.getEmail(), result.get().getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void findByEmail_UserNotFound_ReturnsEmptyOptional() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }
} 