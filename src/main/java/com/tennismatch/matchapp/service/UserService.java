package com.tennismatch.matchapp.service;

import com.tennismatch.matchapp.dto.UserRegistrationDto;
import com.tennismatch.matchapp.model.User;

import java.util.Optional;

public interface UserService {

    /**
     * Registers a new user based on the provided registration data.
     * @param registrationDto the user registration data
     * @return the newly created User entity
     */
    User registerUser(UserRegistrationDto registrationDto);

    /**
     * Finds a user by their username.
     * @param username the username to search for
     * @return an Optional containing the User if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email.
     * @param email the email to search for
     * @return an Optional containing the User if found
     */
    Optional<User> findByEmail(String email);
} 