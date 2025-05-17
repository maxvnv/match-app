package com.tennismatch.matchapp.service.impl;

import com.tennismatch.matchapp.dto.RegisterRequest;
import com.tennismatch.matchapp.model.Role;
import com.tennismatch.matchapp.model.User;
import com.tennismatch.matchapp.repository.UserRepository;
import com.tennismatch.matchapp.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.HashSet;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(RegisterRequest request) {
        // Check for existing email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .ntrpLevel(request.getNtrpLevel())
                .homeTown(request.getHomeTown())
                .age(request.getAge())
                .sex(request.getSex())
                .roles(new HashSet<>())
                .build();
        
        // Add a default role, e.g., "ROLE_USER" as per Spring Security conventions if roles are simple strings
        user.getRoles().add(Role.ROLE_USER);

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
} 