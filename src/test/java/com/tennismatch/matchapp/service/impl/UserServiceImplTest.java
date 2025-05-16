package com.tennismatch.matchapp.service.impl;

import com.tennismatch.matchapp.dto.UserRegistrationDto;
import com.tennismatch.matchapp.model.User;
import com.tennismatch.matchapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationDto registrationDto;
    private User user;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto(
                "testuser",
                "password",
                "test@example.com",
                "Test",
                "User",
                "City",
                "Intermediate"
        );

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedpassword"); // Mock encoded password
        user.setFirstName("Test");
        user.setLastName("User");
        user.setCity("City");
        user.setTennisLevel("Intermediate");
    }

    @Test
    void registerUser_Success() {
        when(userRepository.findByUsername(registrationDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registrationDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(registrationDto);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getPassword(), result.getPassword());
        verify(userRepository, times(1)).findByUsername(registrationDto.getUsername());
        verify(userRepository, times(1)).findByEmail(registrationDto.getEmail());
        verify(passwordEncoder, times(1)).encode(registrationDto.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_DuplicateUsername_ThrowsException() {
        when(userRepository.findByUsername(registrationDto.getUsername())).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registrationDto));

        assertEquals("Username already in use", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(registrationDto.getUsername());
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void registerUser_DuplicateEmail_ThrowsException() {
        when(userRepository.findByUsername(registrationDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(registrationDto.getEmail())).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registrationDto));

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(registrationDto.getUsername());
        verify(userRepository, times(1)).findByEmail(registrationDto.getEmail());
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void findByUsername_UserFound_ReturnsOptionalUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals(user.getUsername(), result.get().getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void findByUsername_UserNotFound_ReturnsEmptyOptional() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonexistent");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void findByEmail_UserFound_ReturnsOptionalUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(user.getEmail(), result.get().getEmail());
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