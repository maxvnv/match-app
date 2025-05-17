package com.tennismatch.matchapp.security;

import com.tennismatch.matchapp.model.Role;
import com.tennismatch.matchapp.model.User;
import com.tennismatch.matchapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private DefaultUserDetailsService userDetailsService;

    private User appUser;

    @BeforeEach
    void setUp() {
        appUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedpassword")
                .roles(Set.of(Role.ROLE_USER))
                .build();
    }

    @Test
    void loadUserByUsername_UserFound_ReturnsUserDetails() {
        String testEmail = "test@example.com";
        when(userService.findByEmail(testEmail)).thenReturn(Optional.of(appUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);

        assertNotNull(userDetails);
        assertEquals(testEmail, userDetails.getUsername()); // Spring Security UserDetails.getUsername() will be our email
        assertEquals("encodedpassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertEquals(1, userDetails.getAuthorities().size());
        verify(userService, times(1)).findByEmail(testEmail);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        String nonExistentEmail = "nonexistent@example.com";
        when(userService.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(nonExistentEmail));

        assertEquals("User not found with email: " + nonExistentEmail, thrown.getMessage());
        verify(userService, times(1)).findByEmail(nonExistentEmail);
    }
} 